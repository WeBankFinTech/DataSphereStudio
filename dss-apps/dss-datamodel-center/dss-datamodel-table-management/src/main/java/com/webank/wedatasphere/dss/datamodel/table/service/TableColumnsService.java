package com.webank.wedatasphere.dss.datamodel.table.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.data.governance.entity.ClassificationConstant;
import com.webank.wedatasphere.dss.datamodel.center.common.constant.ModeType;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableColumns;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

import java.util.List;

public interface TableColumnsService extends IService<DssDatamodelTableColumns> {

    /**
     * 批量新增表字段
     * @param columnsList
     * @return
     */
    int batchInsert(Long tableId,List<DssDatamodelTableColumns> columnsList) throws ErrorException;


    /**
     * 批量更新表字段
     * @param tableId
     * @param columnsList
     * @return
     * @throws ErrorException
     */
    int batchUpdate(Long tableId,List<DssDatamodelTableColumns> columnsList) throws ErrorException;


    /**
     * 表字段列表
     * @param tableId
     * @return
     * @throws ErrorException
     */
    List<DssDatamodelTableColumns> listByTableId(Long tableId);


    /**
     * 根据表id删除字段
     * @param tableId
     * @return
     * @throws ErrorException
     */
    Integer deleteByTableId(Long tableId)throws ErrorException;


    /**
     * 新增数据表字段
     * @param column
     * @return
     */
    Integer addColumn(DssDatamodelTableColumns column);



    Integer tableColumnBind(Long id, Integer modelType, String modelName,String modelNameEn) throws ErrorException;


    /**
     * 模型引用计数
     * @param modeType
     * @param name
     * @return
     */
    int modelReferenceCount(ModeType modeType, String name);

    /**
     * 模型英文引用计数
     * @param modeType
     * @param name
     * @return
     */
    int modelReferenceCountEn(ModeType modeType, String name);
}
