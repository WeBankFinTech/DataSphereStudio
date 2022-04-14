package com.webank.wedatasphere.dss.appconn.manager.impl;

import com.webank.wedatasphere.dss.appconn.manager.entity.AppConnInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * @author enjoyyin
 * @date 2022-04-08
 * @since 1.1.0
 */
public class AppConnRefreshThread implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAppConnManager.class);

    private AbstractAppConnManager appConnManager;
    private List<? extends AppConnInfo> appConnInfos;

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
        appConnInfos.forEach(appConnInfo -> {
            Optional<? extends AppConnInfo> oldOne = this.appConnInfos.stream().filter(old -> old.getAppConnName().equals(appConnInfo.getAppConnName())).findAny();
            if(!oldOne.isPresent() || !oldOne.get().getAppConnResource().equals(appConnInfo.getAppConnResource())) {
                LOGGER.warn("AppConn info {} has updated, now try to refresh it.", appConnInfo.getAppConnName());
                try {
                    appConnManager.reloadAppConn(appConnInfo);
                } catch (Exception e) {
                    // If update failed, it seems like some error happened in this AppConn.
                    // this AppConn will not be refreshed any more, unless the admin changes the AppConn plugin files to optimize it.
                    LOGGER.warn("Reload AppConn {} failed, ignore it", appConnInfo.getAppConnName(), e);
                }
            }
        });
        // now, do not support to delete exists AppConn, since deletion operation is very dangerous.
        this.appConnInfos = appConnInfos;
        LOGGER.info("all AppConns have refreshed.");
    }
}
