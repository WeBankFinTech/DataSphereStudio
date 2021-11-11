package com.webank.wedatasphere.dss.datamodel.table.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.datamodel.table.dao.DssDatamodelTableColumnsMapper;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableColumns;
import com.webank.wedatasphere.dss.datamodel.table.service.TableColumnsService;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;


@Service
public class TableColumnsServiceImpl extends ServiceImpl<DssDatamodelTableColumnsMapper, DssDatamodelTableColumns> implements TableColumnsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TableColumnsServiceImpl.class);

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(Long tableId,List<DssDatamodelTableColumns> columnsList) throws ErrorException {
        for (DssDatamodelTableColumns newOne : columnsList){
            newOne.setTableId(tableId);
            newOne.setCreateTime(new Date());
            newOne.setUpdateTime(new Date());
            getBaseMapper().insert(newOne);
        }
        return 1;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchUpdate(Long tableId, List<DssDatamodelTableColumns> columnsList) throws ErrorException {
        getBaseMapper().delete(Wrappers.<DssDatamodelTableColumns>lambdaQuery().eq(DssDatamodelTableColumns::getTableId, tableId));
        return batchInsert(tableId,columnsList);
    }


    @Override
    public List<DssDatamodelTableColumns> listByTableId(Long tableId) {
        return getBaseMapper().selectList(Wrappers.<DssDatamodelTableColumns>lambdaQuery().eq(DssDatamodelTableColumns::getTableId,tableId));
    }


    @Override
    public Integer deleteByTableId(Long tableId) throws ErrorException {
        return getBaseMapper().delete(Wrappers.<DssDatamodelTableColumns>lambdaQuery().eq(DssDatamodelTableColumns::getTableId,tableId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer addColumn(DssDatamodelTableColumns column) {
        column.setCreateTime(new Date());
        column.setUpdateTime(new Date());
        return getBaseMapper().insert(column);
    }


    @Override
    public Integer tableColumnBind(Long id, Integer modelType, String modelName,String modelNameEn) throws ErrorException{
        DssDatamodelTableColumns updateOne = new DssDatamodelTableColumns();
        updateOne.setModelType(modelType);
        updateOne.setModelName(modelName);
        updateOne.setModelNameEn(modelNameEn);
        updateOne.setUpdateTime(new Date());
        return getBaseMapper().update(updateOne, Wrappers.<DssDatamodelTableColumns>lambdaUpdate().eq(DssDatamodelTableColumns::getId,id));
    }
}
