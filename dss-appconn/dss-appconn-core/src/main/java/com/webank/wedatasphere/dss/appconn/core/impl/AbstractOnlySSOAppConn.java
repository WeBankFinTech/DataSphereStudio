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

import com.webank.wedatasphere.dss.appconn.core.exception.AppConnErrorException;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlySSOAppConn;
import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandardFactory;
import com.webank.wedatasphere.dss.standard.app.sso.origin.OriginSSOIntegrationStandardFactory;
import com.webank.wedatasphere.dss.standard.app.sso.user.SSOUserService;
import com.webank.wedatasphere.dss.standard.app.sso.user.impl.SSOUserServiceImpl;
import com.webank.wedatasphere.dss.standard.common.utils.AppStandardClassUtils;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.LoggerFactory;

public abstract class AbstractOnlySSOAppConn extends AbstractAppConn implements OnlySSOAppConn {

    private SSOIntegrationStandard SSO_INTEGRATION_STANDARD;

    @Override
    public final void init() throws AppConnErrorException {
        SSOIntegrationStandardFactory ssoIntegrationStandardFactory =
                AppStandardClassUtils.getInstance(getAppDesc().getAppName()).getInstanceOrDefault(SSOIntegrationStandardFactory.class, new OriginSSOIntegrationStandardFactory());
        ssoIntegrationStandardFactory.init();
        SSO_INTEGRATION_STANDARD = ssoIntegrationStandardFactory.getSSOIntegrationStandard();
        LoggerFactory.getLogger(AbstractOnlySSOAppConn.class).info("For the first SSO Standard of {} AppConn, {} has created {}.", getAppDesc().getAppName(),
                ssoIntegrationStandardFactory.getClass().getName(), SSO_INTEGRATION_STANDARD.getClass().getName());
        if(CollectionUtils.isNotEmpty(getAppDesc().getAppInstances())) {
            getAppDesc().getAppInstances().forEach(appInstance -> {
                SSOUserService ssoUserService = SSO_INTEGRATION_STANDARD.getSSOUserService(appInstance);
                if(ssoUserService instanceof SSOUserServiceImpl) {
                    ((SSOUserServiceImpl) ssoUserService).setAppConnName(getAppDesc().getAppName());
                }
            });
        }
        // considering the plugin design model in different classloader, We must set it when each AppConn is instanced.
        SSOHelper.setSSOBuilderService(SSO_INTEGRATION_STANDARD.getSSOBuilderService());
        super.init();
    }

    @Override
    public final SSOIntegrationStandard getOrCreateSSOStandard() {
        return SSO_INTEGRATION_STANDARD;
    }
}
