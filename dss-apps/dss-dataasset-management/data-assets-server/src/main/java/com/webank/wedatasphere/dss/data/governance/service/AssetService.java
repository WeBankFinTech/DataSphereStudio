package com.webank.wedatasphere.dss.data.governance.service;


import com.webank.wedatasphere.dss.data.governance.dto.HiveTblStatsDTO;
import com.webank.wedatasphere.dss.data.governance.dto.SearchLabelDTO;
import com.webank.wedatasphere.dss.data.governance.entity.*;
import com.webank.wedatasphere.dss.data.governance.exception.DAOException;
import com.webank.wedatasphere.dss.data.governance.exception.DataGovernanceException;
import com.webank.wedatasphere.dss.data.governance.vo.*;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.lineage.AtlasLineageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface AssetService {
    public Map<String,Long> getHiveSummary() throws DataGovernanceException;

    public List<HiveTblSimpleInfo> searchHiveTable(String classification, String query,String termName,
                                                   int limit, int offset) throws DataGovernanceException;

    public List<HiveTblSimpleInfo> searchHiveDb(String classification, String query,
                                                   int limit, int offset) throws DataGovernanceException;

    public HiveTblDetailInfo getHiveTblDetail(String guid) throws DataGovernanceException ;

    public List<PartInfo> getHiveTblPartition(String guid) throws  DataGovernanceException;

    public  String getTbSelect(String guid) throws  DataGovernanceException;

    public  String getTbCreate(String guid) throws  DataGovernanceException;

    public void modifyComment(String guid, String commentStr) throws DataGovernanceException ;

    public void bulkModifyComment(Map<String,String> commentMap) throws DataGovernanceException ;

    public void setLabels(String guid, Set<String> labels) throws DataGovernanceException;

    /**
     * 获取表实体的血缘信息
     */
    public AtlasLineageInfo getHiveTblLineage(final String guid, final AtlasLineageInfo.LineageDirection direction, final int depth) throws DataGovernanceException;

    public List<TableInfo> getTop10Table () throws DataGovernanceException, SQLException, DAOException;

    /**
     * 根据标签和日期获取存储量前10的表
     * @param label
     * @param startDate
     * @param endDate
     * @return
     * @throws DataGovernanceException
     * @throws SQLException
     * @throws DAOException
     */
    public List<TableInfo> getTop10TableByLabelDay (final String label,final String startDate,final String endDate) throws DataGovernanceException, SQLException, DAOException;

    /**
     * 创建模型
     * @param vo
     * @return
     */
    CreateModelTypeInfo createModelType(CreateModelTypeVO vo) throws DataGovernanceException, AtlasServiceException;


    /**
     * 删除模型
     * @param vo
     * @throws Exception
     */
    void deleteModelType(DeleteModelTypeVO vo) throws Exception;



    /**
     * 更新模型
     * @param vo
     * @return
     */
    UpdateModelTypeInfo updateModelType(UpdateModelTypeVO vo) throws Exception;


    /**
     * 绑定模型
     * @param vo
     * @throws Exception
     */
    void bindModelType(BindModelVO vo) throws Exception;


    /**
     * 解绑模型
     * @param vo
     * @throws Exception
     */
    void unBindModel(UnBindModelVO vo) throws Exception;



    /**
     * 表统计信息
     * @param dbName
     * @param tableName
     * @param guid
     * @return
     */
    HiveTblStatsDTO hiveTblStats(String dbName, String tableName, String guid) throws AtlasServiceException, Exception;


    /**
     * 表统计信息
     * @param dbName
     * @param tableName
     * @param guid
     * @return
     */
    Long hiveTblSize(String dbName, String tableName, String guid) throws Exception;


    /**
     * 新建标签
     * @param vo
     * @return
     * @throws Exception
     */
    CreateLabelInfo createLabel(CreateLabelVO vo) throws Exception;


    /**
     * 删除标签
     * @param vo
     * @return
     * @throws Exception
     */
    void deleteLabel(DeleteLabelVO vo) throws Exception;


    /**
     * 删除标签
     * @param vo
     * @return
     * @throws Exception
     */
    UpdateLabelInfo updateLabel(UpdateLabelVO vo) throws Exception;


    /**
     * 绑定标签
     * @param vo
     * @throws Exception
     */
    void bindLabel(BindLabelVO vo) throws Exception;


    /**
     * 解绑实体
     * @param vo
     * @throws Exception
     */
    void unBindLabel(UnBindLabelVO vo) throws Exception;

    /**
     * 标签搜索列表
     * @param query
     * @param limit
     * @param offset
     * @return
     */
    List<SearchLabelDTO> listLabels(String query, Integer limit, Integer offset) throws AtlasServiceException, Exception;
    /*
     * 根据名称查询分区
     * @param name
     * @return
     * @throws Exception
     */
    List<PartInfo> getHiveTblPartitionByName(String dbName, String tableName) throws Exception;

}
