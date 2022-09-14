package com.webank.wedatasphere.dss.datamodel.table.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTable;
import com.webank.wedatasphere.dss.datamodel.table.entity.DssDatamodelTableCollcetion;
import com.webank.wedatasphere.dss.datamodel.table.vo.TableCollectVO;

public interface TableCollectService extends IService<DssDatamodelTableCollcetion> {

    /**
     * 收藏目标表
     *
     * @param user
     * @param targetTable
     * @return
     */
    Integer tableCollect(String user, DssDatamodelTable targetTable);


    /**
     * 收藏上传表信息
     * @param vo
     * @return
     */
    Integer tableCollect(TableCollectVO vo);


    /**
     * 取消收藏
     * @param user
     * @param tableName
     * @return
     */
    Integer tableCollectCancel(String user, String tableName);


}
