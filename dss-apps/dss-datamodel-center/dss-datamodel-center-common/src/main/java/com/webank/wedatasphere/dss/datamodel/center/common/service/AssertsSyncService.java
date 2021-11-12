package com.webank.wedatasphere.dss.datamodel.center.common.service;

import com.webank.wedatasphere.dss.data.governance.impl.LinkisDataAssetsRemoteClient;
import com.webank.wedatasphere.dss.data.governance.request.*;
import com.webank.wedatasphere.dss.data.governance.response.*;
import com.webank.wedatasphere.dss.datamodel.center.common.event.*;
import com.webank.wedatasphere.dss.datamodel.center.common.listener.AssertsListener;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AssertsSyncService {
    @Resource
    private LinkisDataAssetsRemoteClient client;

    private static final Logger LOGGER = LoggerFactory.getLogger(AssertsListener.class);

   
    public CreateModelTypeResult syncCreateModel(CreateModelEvent event){
        LOGGER.info("createModel event : {}",event);
        try {
            CreateModelTypeResult result = client.createModelType(CreateModelTypeAction.builder()
                    .setUser(event.getUser())
                    .setType(event.getType())
                    .setName(event.getName())
                    .build());
            LOGGER.info("createModel result : {}",result.getInfo());
            return result;
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw e;
        }
    }
    
    public UpdateModelTypeResult syncUpdateModel(UpdateModelEvent event){
        LOGGER.info("updateModel event : {}",event);
        if(StringUtils.equals(event.getName(),event.getOrgName())){
            LOGGER.info("name is same ignore");
            return null;
        }
        try {
            UpdateModelTypeResult result = client.updateModelType(UpdateModelTypeAction.builder()
                    .setUser(event.getUser())
                    .setName(event.getName())
                    .setType(event.getType())
                    .setOrgName(event.getOrgName())
                    .build());
            LOGGER.info("updateModel result : {}",result.getInfo());
            return result;
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw e;
        }
    }

    
    public DeleteModelTypeResult syncDeleteModel(DeleteModelEvent event){
        LOGGER.info("deleteModel event : {}",event);
        try {
            DeleteModelTypeResult result =  client.deleteModelType(DeleteModelTypeAction.builder()
                    .setUser(event.getUser())
                    .setType(event.getType())
                    .setName(event.getName())
                    .build());
            LOGGER.info("deleteModel result : {}",result.getResult());
            return result;
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw e;
        }
    }


    public BindModelTypeResult syncBindModel(BindModelEvent event){
        LOGGER.info("bindModel event : {}",event);
        try {
            BindModelTypeResult result = client.bindModelType(BindModelTypeAction.builder()
                    .setUser(event.getUser())
                    .setGuid(event.getGuid())
                    .setModelName(event.getModelName())
                    .setModelType(event.getModelType())
                    .setTableName(event.getTableName())
                    .build());
            LOGGER.info("bindModel result : {}",result.getResult());
            return result;
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw e;
        }
    }


    public UnBindModelTypeResult syncUnBindModel(UnBindModelEvent event){
        LOGGER.info("unBindModel event : {}", event);
        try {
            UnBindModelTypeResult result = client.unBindModelType(UnBindModelTypeAction.builder()
                    .setUser(event.getUser())
                    .setGuid(event.getGuid())
                    .setModelName(event.getModelName())
                    .setModelType(event.getModelType())
                    .setTableName(event.getTableName())
                    .build());
            LOGGER.info("unBindModel result : {}",result.getResult());
            return result;
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw e;
        }
    }
}
