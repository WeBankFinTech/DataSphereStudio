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
import com.webank.wedatasphere.dss.appconn.core.exception.AppConnErrorException;
import com.webank.wedatasphere.dss.appconn.core.exception.AppConnWarnException;
import com.webank.wedatasphere.dss.appconn.loader.loader.AppConnLoader;
import com.webank.wedatasphere.dss.appconn.loader.loader.AppConnLoaderFactory;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.manager.conf.AppConnManagerCoreConf;
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
import com.webank.wedatasphere.dss.sender.service.conf.DSSSenderServiceConf;
import com.webank.wedatasphere.dss.standard.common.desc.AppDescImpl;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstanceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public abstract class AbstractAppConnManager implements AppConnManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAppConnManager.class);
    private final AppConnLoader appConnLoader = AppConnLoaderFactory.getAppConnLoader();

    private final Map<String, AppConn> appConns = new HashMap<>();
    private boolean isLoaded = false;
    private List<AppConn> appConnList = null;
    AppConnInfoService appConnInfoService;
    private AppConnResourceService appConnResourceService;
    private AppConnRefreshThread appConnRefreshThread;

    private static AppConnManager appConnManager;
    private static boolean lazyLoad = false;

    public static void setLazyLoad() {
        lazyLoad = true;
    }

    public static AppConnManager getAppConnManager() {
        if (appConnManager != null) {
            return appConnManager;
        }
        synchronized (AbstractAppConnManager.class) {
            if (appConnManager == null) {
                //appconn-manager-core包无法引入manager-client包，会有maven循环依赖，这里通过反射获取client的实现类
                //ismanager=false时，获取client端的AppConnManager实现类，ismanager=true时，获取appconn-framework端的AppConnManager实现类。
                if (Objects.equals(AppConnManagerCoreConf.IS_APPCONN_MANAGER.getValue(), AppConnManagerCoreConf.hostname)
                        && "dss-server-dev".equals(DSSSenderServiceConf.CURRENT_DSS_SERVER_NAME.getValue())) {
                    //通过包名过滤
                    appConnManager = ClassUtils.getInstanceOrDefault(AppConnManager.class, c -> c.getPackage().getName().contains("com.webank.wedatasphere.dss.framework.appconn"), new AppConnManagerImpl());
                } else {
                    appConnManager = ClassUtils.getInstanceOrWarn(AppConnManagerImpl.class);
                }
//                appConnManager = !AppConnManagerCoreConf.IS_APPCONN_MANAGER.getValue() ? ClassUtils.getInstanceOrWarn(AppConnManagerImpl.class) :
//                        //通过包名过滤
//                        ClassUtils.getInstanceOrDefault(AppConnManager.class, c -> c.getPackage().getName().contains("com.webank.wedatasphere.dss.framework.appconn"), new AppConnManagerImpl());
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
        if (!lazyLoad && !isLoaded) {
            loadAppConns();
            isLoaded = true;
        }
    }

    protected abstract AppConnInfoService createAppConnInfoService();

    protected abstract AppConnResourceService createAppConnResourceService();

    protected void loadAppConns() {
        LOGGER.info("Begin to init all AppConns.");
        List<? extends AppConnInfo> appConnInfos = appConnInfoService.getAppConnInfos();
        if (appConnInfos == null || appConnInfos.isEmpty()) {
            if (appConns.isEmpty()) {
                throw new DSSRuntimeException("No AppConnInfos returned when the first time init AppConnList.");
            }
            LOGGER.warn("No AppConnInfos returned, ignore it.");
            return;
        }
        long refreshInterval = AppInstanceConstants.APP_CONN_REFRESH_INTERVAL.getValue().toLong();
        appConnRefreshThread = new AppConnRefreshThread(this, appConnInfos);
        Utils.defaultScheduler().scheduleAtFixedRate(appConnRefreshThread, refreshInterval, refreshInterval, TimeUnit.MILLISECONDS);
        Map<String, AppConn> appConns = new HashMap<>();
        Consumer<AppConnInfo> loadAndAdd = DSSExceptionUtils.handling(appConnInfo -> {
            AppConn appConn = loadAppConn(appConnInfo);
            if (appConn != null) {
                appConns.put(appConnInfo.getAppConnName().toLowerCase(), appConn);
            }
        });
        appConnInfos.forEach(DSSExceptionUtils.handling(appConnInfo -> {
            if (appConns.containsKey(appConnInfo.getAppConnName())) {
                return;
            }
            if (StringUtils.isNotBlank(appConnInfo.getReference())) {
                if (!appConns.containsKey(appConnInfo.getReference())) {
                    AppConnInfo referenceAppConnInfo = appConnInfos.stream().filter(info -> info.getAppConnName().equals(appConnInfo.getReference()))
                            .findAny().orElseThrow(() -> new DSSRuntimeException("cannot find the reference appConn " + appConnInfo.getReference() +
                                    " for appConn " + appConnInfo.getAppConnName()));
                    loadAndAdd.accept(referenceAppConnInfo);
                }
                AppConn appConn = loadAppConn(appConnInfo, appConns.get(appConnInfo.getReference()));
                appConns.put(appConnInfo.getAppConnName().toLowerCase(), appConn);
            } else {
                loadAndAdd.accept(appConnInfo);
            }
        }));
        synchronized (this.appConns) {
            this.appConns.clear();
            this.appConns.putAll(appConns);
            appConnList = Collections.unmodifiableList(new ArrayList<>(appConns.values()));
        }
        LOGGER.info("Inited all AppConns, the AppConn list are {}.", this.appConns.keySet());
    }

    protected AppConn loadAppConn(AppConnInfo appConnInfo) throws Exception {
        LOGGER.info("Ready to load AppConn {}, the appConnInfo are {}.", appConnInfo.getAppConnName(), appConnInfo);
        String appConnHome = appConnResourceService.getAppConnHome(appConnInfo);
        LOGGER.info("Try to load AppConn {} with home path {}.", appConnInfo.getAppConnName(), appConnHome);
        AppConn appConn = appConnLoader.getAppConn(appConnInfo.getAppConnName(),
                appConnInfo.getClassName(), appConnHome);
        AppDescImpl appDesc = loadAppDesc(appConnInfo);
        appConn.setAppDesc(appDesc);
        appConn.init();
        LOGGER.info("AppConn {} is loaded successfully.", appConnInfo.getAppConnName());
        return appConn;
    }

    protected AppConn loadAppConn(AppConnInfo appConnInfo, AppConn referenceAppConn) throws Exception {
        LOGGER.info("Ready to load AppConn {} with referenceAppConn {}, the appConnInfo are {}.", appConnInfo.getAppConnName(),
                referenceAppConn.getClass().getSimpleName(), appConnInfo);
        if (appConnInfo.getAppConnResource() != null) {
            String appConnHome = appConnResourceService.getAppConnHome(appConnInfo);
            LOGGER.warn("Because AppConn {} is a referenced AppConn, we only download its resources(since not null) to home path {}, but never load AppConn by it.",
                    appConnInfo.getAppConnName(), appConnHome);
        }
        AppDescImpl appDesc = loadAppDesc(appConnInfo);
        AppConn appConn = referenceAppConn.getClass().newInstance();
        appConn.setAppDesc(appDesc);
        appConn.init();
        LOGGER.info("AppConn {} is loaded successfully.", appConnInfo.getAppConnName());
        return appConn;
    }

    protected AppDescImpl loadAppDesc(AppConnInfo appConnInfo) {
        List<? extends AppInstanceInfo> instanceInfos = appConnInfoService.getAppInstancesByAppConnInfo(appConnInfo);
        LOGGER.info("The instanceInfos of AppConn {} are {}.", appConnInfo.getAppConnName(), instanceInfos);
        AppDescImpl appDesc = new AppDescImpl();
        for (AppInstanceInfo instanceBean : instanceInfos) {
            AppInstanceImpl appInstance = new AppInstanceImpl();
            copyProperties(appConnInfo.getAppConnName(), instanceBean, appInstance);
            appDesc.addAppInstance(appInstance);
        }
        if (appDesc.getAppInstances().isEmpty()) {
            LOGGER.warn("The AppConn {} has no appInstance, maybe this AppConn is a reference AppConn? If not, please check the database info.", appConnInfo.getAppConnName());
        }
        appDesc.setAppName(appConnInfo.getAppConnName());
        return appDesc;
    }

    private void copyProperties(String appConnName, AppInstanceInfo appInstanceInfo, AppInstanceImpl appInstance) {
        appInstance.setBaseUrl(appInstanceInfo.getUrl());
        Map<String, Object> config = new HashMap<>();
        if (StringUtils.isNotEmpty(appInstanceInfo.getEnhanceJson())) {
            try {
                config = DSSCommonUtils.COMMON_GSON.fromJson(appInstanceInfo.getEnhanceJson(), Map.class);
            } catch (Exception e) {
                LOGGER.error("The json of AppConn {} is not a correct json. content: {}.", appConnName, appInstanceInfo.getEnhanceJson(), e);
                throw new DSSRuntimeException("The json of AppConn " + appConnName + " is not a correct json.");
            }
        }
        appInstance.setConfig(config);
        if (StringUtils.isNotBlank(appInstanceInfo.getHomepageUri())) {
            appInstance.setHomepageUri(appInstanceInfo.getHomepageUri());
        }
        //TODO should use Linkis labelFactory to new labels.
        List<DSSLabel> labels = Arrays.stream(appInstanceInfo.getLabels().split(",")).map(EnvDSSLabel::new)
                .collect(Collectors.toList());
        appInstance.setLabels(labels);
        appInstance.setId(appInstanceInfo.getId());
    }

    private void lazyLoadAppConns() {
        if(lazyLoad){
            LOGGER.info("lazyLoad set to true,isLoaded={}",isLoaded);
        }
        if (lazyLoad && !isLoaded) {
            synchronized (this.appConns) {
                if (lazyLoad && !isLoaded) {
                    loadAppConns();
                }
            }
        }
    }

    @Override
    public List<AppConn> listAppConns() {
        lazyLoadAppConns();
        if(appConnList==null){
            throw new AppConnWarnException(25344,"appconn list has not been loaded,please try again later.");
        }
        return appConnList;
    }


    @Override
    public AppConn getAppConn(String appConnName) {
        lazyLoadAppConns();
        return appConns.get(appConnName.toLowerCase());
    }

    @Override
    public void reloadAppConn(String appConnName) {
        AppConnInfo appConnInfo = appConnInfoService.getAppConnInfo(appConnName);
        if (appConnInfo == null) {
            throw new DSSRuntimeException("Cannot get any info about AppConn " + appConnName);
        }
        reloadAppConn(appConnInfo);
    }

    @Override
    public String getAppConnHomePath(String appConnName) {
        AppConnInfo appConnInfo = appConnRefreshThread.getAppConnInfos().stream().filter(info -> info.getAppConnName().equals(appConnName))
                .findAny().orElseThrow(() -> new DSSRuntimeException("Not exists AppConn " + appConnName));
        return appConnResourceService.getAppConnHome(appConnInfo);
    }

    public void reloadAppConn(AppConnInfo appConnInfo) {
        lazyLoadAppConns();
        AppConn appConn;
        try {
            if (StringUtils.isNotBlank(appConnInfo.getReference())) {
                AppConn referenceAppConn = getAppConn(appConnInfo.getReference());
                if (referenceAppConn == null) {
                    throw new DSSRuntimeException("Load AppConn " + appConnInfo.getAppConnName() +
                            " failed! Caused by: The reference AppConn " + appConnInfo.getReference() + " is not exists.");
                }
                appConn = loadAppConn(appConnInfo, referenceAppConn);
            } else {
                appConn = loadAppConn(appConnInfo);
            }
        } catch (DSSRuntimeException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("Reload AppConn failed.", e);
            DSSRuntimeException exception = new DSSRuntimeException("Load AppConn " + appConnInfo.getAppConnName() + " failed!");
            exception.initCause(e);
            throw exception;
        }
        if (appConn == null) {
            return;
        }
        synchronized (this.appConns) {
            this.appConns.put(appConnInfo.getAppConnName().toLowerCase(), appConn);
            appConnList = Collections.unmodifiableList(new ArrayList<>(appConns.values()));
        }
        LOGGER.info("Reloaded AppConn {}.", appConnInfo.getAppConnName());
    }

    void deleteAppConn(AppConnInfo appConnInfo) {
        lazyLoadAppConns();
        if (this.appConns.containsKey(appConnInfo.getAppConnName())) {
            synchronized (this.appConns) {
                if (this.appConns.containsKey(appConnInfo.getAppConnName())) {
                    this.appConns.remove(appConnInfo.getAppConnName());
                    appConnList = Collections.unmodifiableList(new ArrayList<>(appConns.values()));
                    LOGGER.info("Deleted AppConn {}.", appConnInfo.getAppConnName());
                }
            }
        }
    }

    public AppConnRefreshThread getAppConnRefreshThread() {
        return appConnRefreshThread;
    }

}
