package com.webank.wedatasphere.dss.appconn.manager.impl;

import com.webank.wedatasphere.dss.appconn.manager.entity.AppConnInfo;
import com.webank.wedatasphere.dss.appconn.manager.entity.AppInstanceInfo;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnRefreshListener;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author enjoyyin
 * @date 2022-04-08
 * @since 1.1.0
 */
public class AppConnRefreshThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConnRefreshThread.class);

    private AbstractAppConnManager appConnManager;
    private volatile List<? extends AppConnInfo> appConnInfos;
    private List<AppConnRefreshListener> refreshListeners = new ArrayList<>();

    public AppConnRefreshThread(AbstractAppConnManager appConnManager,
                                List<? extends AppConnInfo> appConnInfos) {
        this.appConnManager = appConnManager;
        this.appConnInfos = appConnInfos;
    }

    public List<? extends AppConnInfo> getAppConnInfos() {
        return appConnInfos;
    }

    @Override
    public void run() {
        try{
            run0();
        }catch (Exception e){
            LOGGER.error("refresh appconn failed,we will try again next time.",e);
        }
    }
    private void run0() {
        LOGGER.info("try to refresh all AppConns.");
        List<? extends AppConnInfo> appConnInfos;
        try {
            appConnInfos = appConnManager.appConnInfoService.getAppConnInfos();
        } catch (Exception e) {
            LOGGER.warn("Fetch appConn infos failed, ignore to refresh all AppConns.", e);
            return;
        }
        if(appConnInfos == null || appConnInfos.isEmpty()) {
            LOGGER.warn("no appConnInfos fetched, ignore to refresh it.");
            return;
        }
        LOGGER.info("Fetched appConn infos list => {}.", appConnInfos);
        appConnInfos.forEach(appConnInfo -> {
            Optional<? extends AppConnInfo> oldOne = this.appConnInfos.stream().filter(old -> old.getAppConnName().equals(appConnInfo.getAppConnName())).findAny();
            if(!oldOne.isPresent() || isChanged(oldOne.get(), appConnInfo)) {
                LOGGER.warn("The appConnInfo of AppConn {} has changed, now try to refresh it.", appConnInfo.getAppConnName());
                LOGGER.warn("The new appConnInfo of AppConn {} is {}.", appConnInfo.getAppConnName(), appConnInfo);
                reloadAppConn(appConnInfo);
            } else {
                List<? extends AppInstanceInfo> appInstanceInfos = appConnManager.appConnInfoService.getAppInstancesByAppConnInfo(appConnInfo);
                List<AppInstance> oldAppInstanceList = appConnManager.getAppConn(appConnInfo.getAppConnName()).getAppDesc().getAppInstances();
                if(isChanged(appInstanceInfos, oldAppInstanceList)) {
                    LOGGER.warn("The appInstanceInfo of AppConn {} has changed, now try to refresh it.", appConnInfo.getAppConnName());
                    LOGGER.warn("The old appInstanceInfo of AppConn {} is {}.", appConnInfo.getAppConnName(), oldAppInstanceList);
                    LOGGER.warn("The new appInstanceInfo of AppConn {} is {}.", appConnInfo.getAppConnName(), appInstanceInfos);
                    reloadAppConn(appConnInfo);
                }
            }
        });
        // Now, try to delete not exists AppConn.
        // Since deletion is very dangerous, it is not suggested to do this operation.
        this.appConnInfos.stream().filter(appConnInfo -> appConnInfos.stream().noneMatch(newOne -> appConnInfo.getAppConnName().equals(newOne.getAppConnName())))
                .forEach(appConnInfo -> {
                    LOGGER.warn("The AppConn {} is not exists in DSS DB, it seems like that admin has deleted it, now try to delete it.", appConnInfo.getAppConnName());
                    appConnManager.deleteAppConn(appConnInfo);
                });
        this.appConnInfos = appConnInfos;
        LOGGER.info("all AppConns have refreshed.");
    }

    private boolean isChanged(AppConnInfo one, AppConnInfo other) {
        if(one.getAppConnResource() == null && other.getAppConnResource() == null) {
            return false;
        } else if(one.getAppConnResource() == null || other.getAppConnResource() == null) {
            return true;
        } else {
            return !one.getAppConnResource().equals(other.getAppConnResource());
        }
    }

    private boolean isChanged(List<? extends AppInstanceInfo> one, List<AppInstance> other) {
        if (CollectionUtils.isEmpty(one) && CollectionUtils.isEmpty(other)) {
            return false;
        } else if (CollectionUtils.isEmpty(one) || CollectionUtils.isEmpty(other) || one.size() != other.size()) {
            return true;
        } else {
            // 判断条件为：一种是找不到 ID 相同的，一种是 ID 相同但是属性有变化
            // 这里不判断标签，因为标签是不会改动的
            return one.stream().anyMatch(newOne -> other.stream().noneMatch(otherOne -> otherOne.getId().equals(newOne.getId()))
                    || other.stream().anyMatch(otherOne -> otherOne.getId().equals(newOne.getId())
                    && (!StringUtils.equals(newOne.getUrl(), otherOne.getBaseUrl())
                    || ((StringUtils.isNotEmpty(newOne.getHomepageUri()) || StringUtils.isNotEmpty(otherOne.getHomepageUri()))
                    && !StringUtils.equals(newOne.getHomepageUri(), otherOne.getHomepageUri()))
                    || ((StringUtils.isNotEmpty(newOne.getEnhanceJson()) || MapUtils.isNotEmpty(otherOne.getConfig())) &&
                    //考虑到enhanceJson中key和value之间含有空格的情况，这里需要比较map而不是map的jsonString，否则由map转换为json时不会再包含空格
                    !otherOne.getConfig().equals(DSSCommonUtils.COMMON_GSON.fromJson(newOne.getEnhanceJson(), Map.class)))))
            );
        }
    }

    private void reloadAppConn(AppConnInfo appConnInfo) {
        try {
            appConnManager.reloadAppConn(appConnInfo);
            refreshListeners.forEach(listener -> listener.afterRefresh(appConnInfo.getAppConnName()));
        } catch (Exception e) {
            // If update failed, it seems like some error happened in this AppConn.
            // this AppConn will not be refreshed any more, unless the admin changes the AppConn plugin files to optimize it.
            LOGGER.warn("Reload AppConn {} failed, ignore it", appConnInfo.getAppConnName(), e);
        }
    }

    public void registerRefreshListener(AppConnRefreshListener listener) {
        refreshListeners.add(listener);
    }
}
