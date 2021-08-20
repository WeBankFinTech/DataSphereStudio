/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.core.impl;


import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.exception.AppConnErrorException;
import com.webank.wedatasphere.dss.appconn.core.exception.AppConnWarnException;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlySSOAppConn;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.core.AppIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractAppConn implements AppConn {

    private AppDesc appDesc;
    private List<AppStandard> appStandards;
    protected final List<String> appStandardMethodHeader = Arrays.asList("create", "getOrCreate", "get");
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public List<AppStandard> getAppStandards() {
        if(appStandards == null) {
            synchronized (appStandardMethodHeader) {
                if(appStandards == null) {
                    try {
                        init();
                    } catch (AppConnErrorException e) {
                        throw new AppConnWarnException(e.getErrCode(), e.getMessage(), e);
                    }
                }
            }
        }
        return appStandards;
    }

    protected abstract void initialize();

    /**
     * Specification: each appconn needs to define a method starting with create
     * and returning as standard type to initialize the specifications owned by appconn.
     * */
    @Override
    public final void init() throws AppConnErrorException {
        initialize();
        appStandards = Arrays.stream(getClass().getDeclaredMethods()).map(method -> {
            String methodName = method.getName();
            if(appStandardMethodHeader.stream().anyMatch(methodName::startsWith) &&
                AppStandard.class.isAssignableFrom(method.getReturnType())) {
                try {
                    return (AppStandard) method.invoke(this);
                } catch (ReflectiveOperationException e) {
                    logger.warn(methodName + " execute failed, ignore to set it into appStandardList of " + getClass().getSimpleName(), e);
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if(this instanceof OnlySSOAppConn) {
            SSORequestService ssoRequestService = ((OnlySSOAppConn) this).getOrCreateSSOStandard().getSSORequestService();
            for(AppStandard appStandard : appStandards) {
                if(appStandard instanceof AppIntegrationStandard) {
                    ((AppIntegrationStandard<SSORequestService>) appStandard).setSSORequestService(ssoRequestService);
                }
                try {
                    appStandard.init();
                } catch (AppStandardErrorException e) {
                    throw new AppConnErrorException(e.getErrCode(), "Init " + appStandard.getStandardName() + " failed!", e);
                }
            }
        }
    }

    @Override
    public AppDesc getAppDesc() {
        return appDesc;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

}
