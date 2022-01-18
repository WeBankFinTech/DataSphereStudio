package com.webank.wedatasphere.dss.datamodel.center.common.listener;


import com.webank.wedatasphere.dss.data.governance.impl.LinkisDataAssetsRemoteClient;
import com.webank.wedatasphere.dss.data.governance.request.*;
import com.webank.wedatasphere.dss.data.governance.response.*;
import com.webank.wedatasphere.dss.datamodel.center.common.event.*;
import com.webank.wedatasphere.dss.datamodel.center.common.service.AssertsSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AssertsListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AssertsListener.class);


    @Resource
    private AssertsSyncService assertsSyncService;

    @EventListener
    @Async("taskExecutor")
    public void createModel(CreateModelEvent event) {
        try {
            CreateModelTypeResult result = assertsSyncService.syncCreateModel(event);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    @EventListener
    @Async("taskExecutor")
    public void updateModel(UpdateModelEvent event) {
        try {
            UpdateModelTypeResult result = assertsSyncService.syncUpdateModel(event);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Async("taskExecutor")
    public void deleteModel(DeleteModelEvent event) {
        LOGGER.info("deleteModel event : {}", event);
        try {
            DeleteModelTypeResult result = assertsSyncService.syncDeleteModel(event);
            LOGGER.info("deleteModel result : {}", result.getResult());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Async("taskExecutor")
    public void bindModel(BindModelEvent event) {
        LOGGER.info("bindModel event : {}", event);
        try {
            BindModelTypeResult result = assertsSyncService.syncBindModel(event);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Async("taskExecutor")
    public void unBindModel(UnBindModelEvent event) {
        LOGGER.info("unBindModel event : {}", event);
        try {
            UnBindModelTypeResult result = assertsSyncService.syncUnBindModel(event);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Async("taskExecutor")
    public void createLabel(CreateLabelEvent event) {
        LOGGER.info("createLabel event : {}", event);
        try {
            CreateLabelResult result = assertsSyncService.syncCreateLabel(event);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Async("taskExecutor")
    public void updateLabel(UpdateLabelEvent event) {
        LOGGER.info("updateLabel event : {}", event);
        try {
            UpdateLabelResult result = assertsSyncService.syncUpdateLabel(event);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Async("taskExecutor")
    public void deleteLabel(DeleteLabelEvent event) {
        LOGGER.info("deleteLabel event : {}", event);
        try {
            DeleteLabelResult result = assertsSyncService.syncDeleteLabel(event);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Async("taskExecutor")
    public void bindLabel(BindLabelEvent event) {
        LOGGER.info("bindLabel event : {}", event);
        try {
            BindLabelResult result = assertsSyncService.syncBindLabel(event);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @EventListener
    @Async("taskExecutor")
    public void unBindLabel(UnBindLabelEvent event) {
        LOGGER.info("unBindLabel event : {}", event);
        try {
            UnBindLabelResult result = assertsSyncService.syncUnBindLabel(event);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
