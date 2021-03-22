/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.appconn.service.impl;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.PresentationAppConn;
import com.webank.wedatasphere.dss.appconn.loader.loader.AppConnLoader;
import com.webank.wedatasphere.dss.appconn.loader.loader.AppConnLoaderFactory;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.framework.appconn.dao.AppConnMapper;
import com.webank.wedatasphere.dss.framework.appconn.dao.AppInstanceMapper;
import com.webank.wedatasphere.dss.framework.appconn.entity.AppConnBean;
import com.webank.wedatasphere.dss.framework.appconn.entity.AppInstanceBean;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.framework.appconn.utils.AppConnServiceUtils;
import com.webank.wedatasphere.dss.standard.common.desc.*;
import com.webank.wedatasphere.linkis.common.conf.Configuration;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * created by cooperyang on 2020/11/18
 * Description:
 */
@Component
public class AppConnServiceImpl implements AppConnService {


    private static final Logger LOGGER = LoggerFactory.getLogger(AppConnServiceImpl.class);


    private final Map<String, AppConn> appConnMap = new ConcurrentHashMap<>();

    private final AppConnLoader appConnLoader = AppConnLoaderFactory.getAppConnLoader();

    @Autowired
    private AppConnMapper appConnMapper;

    @Autowired
    private AppInstanceMapper appInstanceMapper;

    private Map<String, AppConn> appConns ;



    @Override
    public void refreshAppConns() {
        LOGGER.info("begin to load and refresh appConn!");
        init();
    }

    // TODO！
    // 由于在反射生成AppConn的时候，就会调用AppConn的构造方法，在生成AppConn的时候，并未把对应的AppDesc给初始化好，
    // 所以需要Standard在构造时，传入AppConn的引用，这里构造AppConn的还需要考虑初始化顺序和设置Standard顺序的问题，
    // 需要以后进行优化。
    @PostConstruct
    public void init(){
        appConns = new ConcurrentHashMap<>();
        LOGGER.info("begin to init appConn Service");
        List<AppConnBean> appConnBeans = appConnMapper.getAllAppConnBeans();
        LOGGER.info("appConnBeans are {}", appConnBeans);
        appConnBeans.forEach(appConnBean -> {
            AppConn appConn = null;
            try {
                appConn = appConnLoader.getAppConn(appConnBean.getAppConnName(),
                        appConnBean.getClassName(), appConnBean.getAppConnClassPath());
            } catch (Throwable t) {
                LOGGER.error("Failed to get appConn {}", appConnBean.getAppConnName(), t);
            }
            if(appConn != null){
                List<AppInstanceBean> instanceBeans = appInstanceMapper.getAppInstancesByAppConnId(appConnBean.getId());
                LOGGER.info("instanceBeans are {}", instanceBeans);
                AppDescImpl appDesc = new AppDescImpl();
                for (AppInstanceBean instanceBean : instanceBeans) {
                    AppInstanceImpl appInstance = new AppInstanceImpl();
                    copyProperties(instanceBean, appInstance);
                    appDesc.addAppInstance(appInstance);
                }
                appDesc.setAppName(appConnBean.getAppConnName());
                appDesc.setId(appConnBean.getId());
                appConn.setAppDesc(appDesc);
                appConns.put(appConnBean.getAppConnName(), appConn);
            }
            LOGGER.info("appConns are {}", appConns);
        });
    }


    @SuppressWarnings("unchecked")
    private void copyProperties(AppInstanceBean appInstanceBean, AppInstanceImpl appInstance){
        appInstance.setBaseUrl(appInstanceBean.getUrl());
        Map<String, Object> config = new HashMap<>();
        if(StringUtils.isNotEmpty(appInstanceBean.getEnhanceJson())){
            try{
                // todo:该数据库json字段存储的具体内容
                config = AppConnServiceUtils.COMMON_GSON.fromJson(appInstanceBean.getEnhanceJson(), config.getClass());
            }catch(Exception e){
                LOGGER.warn("{} is not a correct json", appInstanceBean.getEnhanceJson(), e);
            }
        }
        appInstance.setConfig(config);
        List<DSSLabel> labels = new ArrayList<>();
        Arrays.stream(appInstanceBean.getLabel().split(",")).map(s -> new CommonDSSLabel(s){
            {
                setLabel(s);
            }
        }).forEach(labels::add);
        appInstance.setLabels(labels);
        appInstance.setId(appInstanceBean.getId());
    }


    @Override
    public AppConn getAppConn(Class<? extends AppConn> clazz) {
       return appConns.values().
               stream().
               filter(appConn -> clazz.isAssignableFrom(appConn.getClass())).
               findFirst().
               orElse(null);
    }

    @Override
    public List<AppConn> listAppConns() {
        return new ArrayList<>(appConns.values());
    }

    @Override
    public AppConn getAppConn(String appConnName) {
        return appConns.get(appConnName);
    }

    @Override
    public AppConn getAppConnByNodeType(String nodeType) {
        String appConnName = appConnMapper.getAppConnNameByNodeType(nodeType);
        if(StringUtils.isBlank(appConnName)){
            return null;
        }
        return appConns.get(appConnName);
    }

    @Override
    public String getRedirectUrl(String nodeType, AppInstance appInstance) {
        String redirectUrl = appConnMapper.getRedirectUrl(nodeType, appInstance.getId());
        if(StringUtils.isBlank(redirectUrl)){
            return Configuration.getGateWayURL();
        }
        return redirectUrl;
    }

    @Override
    public List<PresentationAppConn> getPresentationAppConns() {
        return appConnMap.values().stream().
                filter(appConn -> appConn instanceof PresentationAppConn).
                map(appConn -> (PresentationAppConn)appConn).collect(Collectors.toList());
    }
}
