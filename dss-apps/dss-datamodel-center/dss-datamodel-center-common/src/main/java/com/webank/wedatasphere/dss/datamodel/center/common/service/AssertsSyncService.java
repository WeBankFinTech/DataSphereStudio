package com.webank.wedatasphere.dss.datamodel.center.common.service;

import com.webank.wedatasphere.dss.data.governance.impl.LinkisDataAssetsRemoteClient;
import com.webank.wedatasphere.dss.data.governance.request.*;
import com.webank.wedatasphere.dss.data.governance.response.*;
import com.webank.wedatasphere.dss.datamodel.center.common.event.*;
import com.webank.wedatasphere.dss.datamodel.center.common.listener.AssertsListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


    public CreateLabelResult syncCreateLabel(CreateLabelEvent event){
        LOGGER.info("createLabel event : {}", event);
        try {
            CreateLabelResult result = client.createLabel(CreateLabelAction.builder()
                    .setUser(event.getUser())
                    .setName(event.getName())
                    .build());
            LOGGER.info("createLabel result : {}",result.getResult());
            return result;
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw e;
        }
    }


    public UpdateLabelResult syncUpdateLabel(UpdateLabelEvent event){
        LOGGER.info("updateLabel event : {}", event);
        if(StringUtils.equals(event.getName(),event.getOriName())){
            LOGGER.info("name is same ignore");
            return null;
        }
        try {
            UpdateLabelResult result = client.updateLabel(UpdateLabelAction.builder()
                    .setUser(event.getUser())
                    .setOrgName(event.getOriName())
                    .setName(event.getName())
                    .build());
            LOGGER.info("updateLabel result : {}",result.getResult());
            return result;
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw e;
        }
    }

    public DeleteLabelResult syncDeleteLabel(DeleteLabelEvent event){
        LOGGER.info("deleteLabel event : {}", event);
        try {
            DeleteLabelResult result = client.deleteLabel(DeleteLabelAction.builder()
                    .setUser(event.getUser())
                    .setName(event.getName())
                    .build());
            LOGGER.info("deleteLabel result : {}",result.getResult());
            return result;
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw e;
        }
    }


    public BindLabelResult syncBindLabel(BindLabelEvent event){
        LOGGER.info("bindLabel event : {}", event);
        try {
            BindLabelResult result = client.bindLabel(BindLabelAction.builder()
                    .setUser(event.getUser())
                    .setLabel(event.getLabel())
                    .setTableName(event.getTableName())
                    .setLabelGuid(event.getLabelGuid())
                    .setTableGuid(event.getTableGuid())
                    .build());
            LOGGER.info("bindLabel result : {}",result.getResult());
            return result;
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw e;
        }
    }


    public UnBindLabelResult syncUnBindLabel(UnBindLabelEvent event){
        LOGGER.info("unBindLabel event : {}", event);
        try {
            UnBindLabelResult result = client.unBindLabel(UnBindLabelAction.builder()
                    .setUser(event.getUser())
                    .setLabel(event.getLabel())
                    .setTableName(event.getTableName())
                    .setLabelGuid(event.getLabelGuid())
                    .setTableGuid(event.getTableGuid())
                    .setRelationGuid(event.getRelationGuid())
                    .build());
            LOGGER.info("unBindLabel result : {}",result.getResult());
            return result;
        }catch (Exception e){
            LOGGER.error(e.getMessage(),e);
            throw e;
        }
    }



}
