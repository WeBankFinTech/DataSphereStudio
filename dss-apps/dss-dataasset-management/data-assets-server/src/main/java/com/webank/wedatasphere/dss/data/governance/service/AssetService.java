package com.webank.wedatasphere.dss.data.governance.service;

import com.webank.wedatasphere.dss.data.governance.entity.CreateModelTypeInfo;
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
    public Map<String,Integer> getHiveSummary() throws DataGovernanceException;

    public List<HiveTblSimpleInfo> searchHiveTable(String classification, String query,
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
}
