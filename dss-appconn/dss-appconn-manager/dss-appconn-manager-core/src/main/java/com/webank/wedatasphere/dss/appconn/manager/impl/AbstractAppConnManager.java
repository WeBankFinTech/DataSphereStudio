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

package com.webank.wedatasphere.dss.appconn.manager.impl;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.loader.loader.AppConnLoader;
import com.webank.wedatasphere.dss.appconn.loader.loader.AppConnLoaderFactory;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.manager.entity.AppConnInfo;
import com.webank.wedatasphere.dss.appconn.manager.entity.AppInstanceInfo;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnInfoService;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnResourceService;
import com.webank.wedatasphere.dss.appconn.manager.utils.AppInstanceConstants;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.utils.ClassUtils;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.common.desc.AppDescImpl;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstanceImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAppConnManager implements AppConnManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAppConnManager.class);
    private final AppConnLoader appConnLoader = AppConnLoaderFactory.getAppConnLoader();

    private final Map<String, AppConn> appConns = new HashMap<>();
    private boolean isLoaded = false;
    private List<AppConn> appConnList = null;
    private AppConnInfoService appConnInfoService;
    private AppConnResourceService appConnResourceService;

    private static AppConnManager appConnManager;
    private static boolean lazyLoad = false;

    public static void setLazyLoad() {
        lazyLoad = true;
    }

    public static AppConnManager getAppConnManager() {
        if(appConnManager != null) {
            return appConnManager;
        }
        synchronized (AbstractAppConnManager.class) {
            if(appConnManager == null) {
                appConnManager = ClassUtils.getInstanceOrDefault(AppConnManager.class, new AppConnManagerImpl());
                LOGGER.info("The instance of AppConnManager is {}.", appConnManager.getClass().getName());
                appConnManager.init();
            }
        }
        return appConnManager;
    }

    @Override
    public void init() {
        appConnInfoService = createAppConnInfoService();
        LOGGER.info("The instance of AppConnInfoService is {}.", appConnInfoService.getClass().getName());
        appConnResourceService = createAppConnResourceService();
        LOGGER.info("The instance of AppConnResourceService is {}.", appConnResourceService.getClass().getName());
        if(!lazyLoad && !isLoaded) {
            loadAppConns();
            isLoaded = true;
        }
    }

    protected abstract AppConnInfoService createAppConnInfoService();

    protected abstract AppConnResourceService createAppConnResourceService();

    protected void loadAppConns() {
        LOGGER.info("Begin to init all AppConns.");
        List<? extends AppConnInfo> appConnInfos = appConnInfoService.getAppConnInfos();
        if(appConnInfos == null || appConnInfos.isEmpty()) {
            if(appConns.isEmpty()) {
                throw new DSSRuntimeException("No AppConnInfos returned when the first time init AppConnList.");
            }
            LOGGER.warn("No AppConnInfos returned, ignore it.");
            return;
        }
        Map<String, AppConn> appConns = new HashMap<>();
        appConnInfos.forEach(DSSExceptionUtils.handling(appConnInfo -> {
            AppConn appConn = loadAppConn(appConnInfo);
            appConns.put(appConnInfo.getAppConnName(), appConn);
        }));
        synchronized (this.appConns) {
            this.appConns.clear();
            this.appConns.putAll(appConns);
            appConnList = Collections.unmodifiableList(new ArrayList<>(appConns.values()));
        }
        LOGGER.info("Inited all AppConns, the AppConn list are {}.", this.appConnList);
    }

    protected AppConn loadAppConn(AppConnInfo appConnInfo) throws Exception {
        LOGGER.info("Ready to load AppConn {}, the appConnInfo are {}.", appConnInfo.getAppConnName(), appConnInfo);
        String appConnHome = appConnResourceService.getAppConnHome(appConnInfo);
        LOGGER.info("Try to load AppConn {} with home path {}.", appConnInfo.getAppConnName(), appConnHome);
        AppConn appConn = appConnLoader.getAppConn(appConnInfo.getAppConnName(),
            appConnInfo.getClassName(), appConnHome);
        appConn.init();
        List<? extends AppInstanceInfo> instanceInfos = appConnInfoService.getAppInstancesByAppConnInfo(appConnInfo);
        LOGGER.info("The instanceInfos of AppConn {} are {}.", appConnInfo.getAppConnName(), instanceInfos);
        AppDescImpl appDesc = new AppDescImpl();
        for (AppInstanceInfo instanceBean : instanceInfos) {
            AppInstanceImpl appInstance = new AppInstanceImpl();
            copyProperties(appConnInfo.getAppConnName(), instanceBean, appInstance);
            appDesc.addAppInstance(appInstance);
        }
        appDesc.setAppName(appConnInfo.getAppConnName());
        appConn.setAppDesc(appDesc);
        LOGGER.info("AppConn {} is loaded successfully.", appConnInfo.getAppConnName());
        return appConn;
    }

    private void copyProperties(String appConnName, AppInstanceInfo appInstanceInfo, AppInstanceImpl appInstance){
        appInstance.setBaseUrl(appInstanceInfo.getUrl());
        Map<String, Object> config = new HashMap<>();
        if(StringUtils.isNotEmpty(appInstanceInfo.getEnhanceJson())){
            try{
                config = DSSCommonUtils.COMMON_GSON.fromJson(appInstanceInfo.getEnhanceJson(), Map.class);
            } catch (Exception e) {
                LOGGER.error("The json of AppConn {} is not a correct json. content: {}.", appConnName, appInstanceInfo.getEnhanceJson(), e);
                throw new DSSRuntimeException("The json of AppConn " + appConnName + " is not a correct json.");
            }
        }
        appInstance.setConfig(config);
        if(StringUtils.isNotBlank(appInstanceInfo.getRedirectUrl())) {
            config.put(AppInstanceConstants.REDIRECT_URL, appInstanceInfo.getRedirectUrl());
        }
        if(StringUtils.isNotBlank(appInstanceInfo.getHomepageUrl())) {
            config.put(AppInstanceConstants.HOMEPAGE_URL, appInstanceInfo.getHomepageUrl());
        }
        //TODO should use Linkis labelFactory to new labels.
        List<DSSLabel> labels = Arrays.stream(appInstanceInfo.getLabels().split(",")).map(EnvDSSLabel::new)
            .collect(Collectors.toList());
        appInstance.setLabels(labels);
        appInstance.setId(appInstanceInfo.getId());
    }

    private void lazyLoadAppConns() {
        if(lazyLoad && !isLoaded) {
            synchronized (this.appConns) {
                if(lazyLoad && !isLoaded) {
                    loadAppConns();
                }
            }
        }
    }

    @Override
    public List<AppConn> listAppConns() {
        lazyLoadAppConns();
        return appConnList;
    }


    @Override
    public AppConn getAppConn(String appConnName) {
        lazyLoadAppConns();
        return appConns.get(appConnName);
    }

    @Override
    public void reloadAppConn(String appConnName) {
        AppConnInfo appConnInfo = appConnInfoService.getAppConnInfo(appConnName);
        if(appConnInfo == null) {
            throw new DSSRuntimeException("Cannot get any info about AppConn " + appConnName);
        }
        reloadAppConn(appConnInfo);
    }

    public void reloadAppConn(AppConnInfo appConnInfo) {
        lazyLoadAppConns();
        AppConn appConn;
        try {
            appConn = loadAppConn(appConnInfo);
        } catch (Exception e) {
            LOGGER.error("Reload AppConn failed.", e);
            throw new DSSRuntimeException("Load AppConn " + appConnInfo.getAppConnName() + " failed!");
        }
        synchronized (this.appConns) {
            this.appConns.put(appConnInfo.getAppConnName(), appConn);
            appConnList = Collections.unmodifiableList(new ArrayList<>(appConns.values()));
        }
        LOGGER.info("Reloaded AppConn {}.", appConnInfo.getAppConnName());
    }

}
