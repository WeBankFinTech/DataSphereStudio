package com.webank.wedatasphere.dss.datamodel.table.listener;


import com.webank.wedatasphere.dss.data.governance.entity.ClassificationConstant;
import com.webank.wedatasphere.dss.datamodel.center.common.event.BindModelEvent;
import com.webank.wedatasphere.dss.datamodel.center.common.event.UnBindModelEvent;
import com.webank.wedatasphere.dss.datamodel.table.dto.ModelTypeDTO;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.event.BindModelByColumnsEvent;
import com.webank.wedatasphere.dss.datamodel.table.event.BindModelByTableEvent;
import com.webank.wedatasphere.dss.datamodel.table.event.UpdateBindModelByColumnsEvent;
import com.webank.wedatasphere.dss.datamodel.table.event.UpdateBindModelByTableEvent;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TableModelListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableModelListener.class);


    @Resource
    private ApplicationEventPublisher publisher;

    @EventListener
    @Async
    public void bindByTable(BindModelByTableEvent event){
        bindByTable(event.getUser(),event.getTable());
    }

    @Async
    @EventListener
    public void updateBindByTable(UpdateBindModelByTableEvent event){
        if (StringUtils.equals(event.getOrg().getName(),event.getUpdateOne().getName())){
            return;
        }
        String user = event.getUser();
        String tableName = event.getOrg().getName();

        String orgLayerNameEn = event.getOrg().getWarehouseLayerNameEn();
        String updateLayerNameEn = event.getUpdateOne().getWarehouseLayerNameEn();
        //分层名称不同 则换绑
        if (!StringUtils.equals(orgLayerNameEn,updateLayerNameEn)){
            publishBind(user,null, tableName,updateLayerNameEn,ClassificationConstant.LAYER);
            publishUnBind(user,null, tableName,orgLayerNameEn,ClassificationConstant.LAYER);
        }


        String orgThemeNameEn = event.getOrg().getWarehouseThemeNameEn();
        String updateThemeNameEn = event.getUpdateOne().getWarehouseThemeNameEn();
        //主题名称不同 则换绑
        if (!StringUtils.equals(orgThemeNameEn,updateThemeNameEn)){
            publishBind(user,null, tableName,updateThemeNameEn,ClassificationConstant.THEME);
            publishUnBind(user,null, tableName,orgThemeNameEn,ClassificationConstant.THEME);
        }
    }




    @Async
    @EventListener
    public void updateBindByColumns(UpdateBindModelByColumnsEvent event){
        Set<ModelTypeDTO> orgModels = event.getUpdateColumns().stream().map(c->new ModelTypeDTO(c.getModelType(),c.getModelNameEn(),c.getModelName())).collect(Collectors.toSet());
        Set<ModelTypeDTO> updateModels = event.getOrgColumns().stream().map(c->new ModelTypeDTO(c.getModelType(),c.getModelNameEn(),c.getModelName())).collect(Collectors.toSet());

        //去重 获取绑定列表
        Set<ModelTypeDTO> bindModels = updateModels.stream().filter(bindOne->!orgModels.contains(bindOne)).collect(Collectors.toSet());
        LOGGER.info("bindModels : {}",bindModels);
        //去重 获取解绑列表
        Set<ModelTypeDTO> unBindModels = orgModels.stream().filter(unBindOne->!updateModels.contains(unBindOne)).collect(Collectors.toSet());
        LOGGER.info("unBindModels : {}",unBindModels);

        bindByColumnsModel(event.getUser(),event.getTableName(),bindModels);
        unBindByColumnsModel(event.getUser(), event.getTableName(), unBindModels);
    }

    private void bindByTable(String user,DssDatamodelTable newOne) {
        publishBind(user,null, newOne.getName(), newOne.getWarehouseThemeNameEn(),ClassificationConstant.THEME);
        publishBind(user,null, newOne.getName(), newOne.getWarehouseLayerNameEn(),ClassificationConstant.LAYER);
    }

    private void publishBind(String user, String guid, String tableName, String modelName, ClassificationConstant modelType){
        publisher.publishEvent(new BindModelEvent(this,user,guid,tableName,modelName,modelType));
    }

    private void publishUnBind(String user, String guid, String tableName, String modelName, ClassificationConstant modelType){
        publisher.publishEvent(new UnBindModelEvent(this,user,guid,tableName,modelName,modelType));
    }

    @EventListener
    @Async
    public void bindByColumnsModel(BindModelByColumnsEvent event){
        bindByColumnsModel(event.getUser(),event.getTableName(),event.getColumns().stream().map(c->new ModelTypeDTO(c.getModelType(),c.getModelNameEn(),c.getModelName())).collect(Collectors.toSet()));
    }

    private void bindByColumnsModel(String user, String tableName, Set<ModelTypeDTO> modelTypeDTOS){
        for (ModelTypeDTO model : modelTypeDTOS){
            Optional<ClassificationConstant> optional = ClassificationConstant.getClassificationConstantByType(model.getModelType());
            if (!optional.isPresent()){
                continue;
            }
            publishBind(user,null, tableName, model.getModelNameEn(),optional.get());
        }
    }

    private void unBindByColumnsModel(String user, String tableName, Set<ModelTypeDTO> modelTypeDTOS){
        for (ModelTypeDTO column : modelTypeDTOS){
            Optional<ClassificationConstant> optional = ClassificationConstant.getClassificationConstantByType(column.getModelType());
            if (!optional.isPresent()){
                continue;
            }
            publishUnBind(user,null, tableName, column.getModelNameEn(),optional.get());
        }
    }
}
