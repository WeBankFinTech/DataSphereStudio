package com.webank.wedatasphere.dss.data.governance.service;

import com.webank.wedatasphere.dss.data.governance.entity.*;
import com.webank.wedatasphere.dss.data.governance.exception.DataGovernanceException;
import org.apache.atlas.model.lineage.AtlasLineageInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface AssetService {
    public Map<String,Integer> getHiveSummary() throws DataGovernanceException;

    public List<HiveTblSimpleInfo> searchHiveTable(String classification, String query,
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

    public List<TableInfo> getTop10Table () throws DataGovernanceException, SQLException;

}
