package com.webank.wedatasphere.dss.data.governance.service.impl;

import com.google.gson.internal.LinkedTreeMap;
import com.webank.wedatasphere.dss.data.governance.atlas.AtlasService;
import com.webank.wedatasphere.dss.data.governance.dao.MetaInfoMapper;
import com.webank.wedatasphere.dss.data.governance.dao.WorkspaceInfoMapper;
import com.webank.wedatasphere.dss.data.governance.dao.impl.MetaInfoMapperImpl;
import com.webank.wedatasphere.dss.data.governance.entity.*;
import com.webank.wedatasphere.dss.data.governance.exception.DAOException;
import com.webank.wedatasphere.dss.data.governance.exception.DataGovernanceException;
import com.webank.wedatasphere.dss.data.governance.service.AssetService;
import com.webank.wedatasphere.dss.data.governance.utils.DateUtil;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasEntityHeader;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class AssetServiceImpl implements AssetService {
    private AtlasService atlasService;
    private MetaInfoMapper metaInfoMapper;
    private WorkspaceInfoMapper workspaceInfoMapper;

    public AssetServiceImpl(AtlasService atlasService) {
        this.atlasService = atlasService;
        this.metaInfoMapper = new MetaInfoMapperImpl();
    }

    @Override
    public Map<String, Integer> getHiveSummary() throws DataGovernanceException {
        try {
            Map<String, Integer> result = new HashMap<>();

            result.put("hiveDb", atlasService.getHiveDbCnt());
            result.put("hiveTable", atlasService.getHiveTableCnt());
            result.put("hiveStore", metaInfoMapper.getTableStorage());

            return result;
        } catch (AtlasServiceException | DAOException exception) {
            throw new DataGovernanceException(23000,exception.getMessage());
        }
    }

    @Override
    public List<HiveTblSimpleInfo> searchHiveTable(String classification, String query,
                                                   int limit, int offset) throws DataGovernanceException {
        List<AtlasEntityHeader> atlasEntityHeaders = null;
        try {
            atlasEntityHeaders = atlasService.searchHiveTable(classification, query, true, limit, offset);
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000,ex.getMessage());
        }

        if (atlasEntityHeaders != null) {
            return atlasEntityHeaders.parallelStream().map(atlasEntityHeader -> {
                HiveTblSimpleInfo hiveTblBasic = new HiveTblSimpleInfo();
                hiveTblBasic.setGuid(atlasEntityHeader.getGuid());
                hiveTblBasic.setName(atlasEntityHeader.getAttribute("name").toString());
                hiveTblBasic.setQualifiedName(atlasEntityHeader.getAttribute("qualifiedName").toString());
                hiveTblBasic.setOwner(atlasEntityHeader.getAttribute("owner").toString());
                Object createTime = atlasEntityHeader.getAttribute("createTime");
                if (createTime != null) {
                    hiveTblBasic.setCreateTime(DateUtil.unixToTimeStr((Double) createTime));
                }

                return hiveTblBasic;
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public List<HiveTblSimpleInfo> searchHiveDb(String classification, String query, int limit, int offset) throws DataGovernanceException {
        List<AtlasEntityHeader> atlasEntityHeaders = null;
        try {
            atlasEntityHeaders = atlasService.searchHiveDb(classification, query, true, limit, offset);
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000,ex.getMessage());
        }

        if (atlasEntityHeaders != null) {
            return atlasEntityHeaders.parallelStream().map(atlasEntityHeader -> {
                HiveTblSimpleInfo hiveTblBasic = new HiveTblSimpleInfo();
                hiveTblBasic.setGuid(atlasEntityHeader.getGuid());
                hiveTblBasic.setName(atlasEntityHeader.getAttribute("name").toString());
                hiveTblBasic.setQualifiedName(atlasEntityHeader.getAttribute("qualifiedName").toString());
                hiveTblBasic.setOwner(atlasEntityHeader.getAttribute("owner").toString());
                Object createTime = atlasEntityHeader.getAttribute("createTime");
                if (createTime != null) {
                    hiveTblBasic.setCreateTime(DateUtil.unixToTimeStr((Double) createTime));
                }

                return hiveTblBasic;
            }).collect(Collectors.toList());
        }
        return null;
    }

    @Override
    public HiveTblDetailInfo getHiveTblDetail(String guid) throws DataGovernanceException {
        try {
            HiveTblDetailInfo hiveTblDetailInfo = new HiveTblDetailInfo();

            AtlasEntity atlasEntity = atlasService.getHiveTbl(guid);
            Map<String, Object> hiveTblNameAndIsPartById = atlasService.getHiveTblNameAndIsPartById(guid);
            Boolean isPartTable = (Boolean) hiveTblNameAndIsPartById.get("isPartition");
            int storage = 0;
            String db_name = String.valueOf(atlasEntity.getAttributes().get("qualifiedName")).split("@")[0];
            String tableName = db_name.split("\\.")[1];
            String dbName = db_name.split("\\.")[0];
            try {
                storage = metaInfoMapper.getTableInfo(dbName, tableName, isPartTable);
            } catch (DAOException e) {
                e.printStackTrace();
            }
            List<String> guids = new ArrayList<>();
            List<LinkedTreeMap<String, String>> columns = (List<LinkedTreeMap<String, String>>) atlasEntity.getAttributes().get("columns");
            for (LinkedTreeMap<String, String> column : columns) {
                guids.add(column.get("guid"));
            }
            List<HiveTblDetailInfo.HiveColumnInfo> hiveColumnInfos = new ArrayList<>();
            if (guids.size() > 0) {
                List<AtlasEntity> hiveColumnsByGuids = atlasService.getHiveColumnsByGuids(guids);
                for (AtlasEntity hiveColumnsByGuid : hiveColumnsByGuids) {
                    HiveTblDetailInfo.HiveColumnInfo hiveColumnInfo = new HiveTblDetailInfo.HiveColumnInfo();
                    hiveColumnInfo.setName(String.valueOf(hiveColumnsByGuid.getAttributes().get("name")));
                    hiveColumnInfo.setType(String.valueOf(hiveColumnsByGuid.getAttributes().get("type")));
                    hiveColumnInfo.setComment(String.valueOf(hiveColumnsByGuid.getAttributes().get("comment")));
                    hiveColumnInfo.setGuid(hiveColumnsByGuid.getGuid());
                    hiveColumnInfos.add(hiveColumnInfo);
                }
            }
            List<String> partguids = new ArrayList<>();
            List<LinkedTreeMap<String, String>> partitionKeys = (List<LinkedTreeMap<String, String>>) atlasEntity.getAttributes().get("partitionKeys");
            for (LinkedTreeMap<String, String> column : partitionKeys) {
                partguids.add(column.get("guid"));
            }
            List<HiveTblDetailInfo.HiveColumnInfo> partitionColumns = new ArrayList<>();
            if (partguids.size() > 0) {
                List<AtlasEntity> hivePartColumnsByGuids = atlasService.getHiveColumnsByGuids(partguids);
                for (AtlasEntity hiveColumnsByGuid : hivePartColumnsByGuids) {
                    HiveTblDetailInfo.HiveColumnInfo hiveColumnInfo = new HiveTblDetailInfo.HiveColumnInfo();
                    hiveColumnInfo.setName(String.valueOf(hiveColumnsByGuid.getAttributes().get("name")));
                    hiveColumnInfo.setType(String.valueOf(hiveColumnsByGuid.getAttributes().get("type")));
                    hiveColumnInfo.setComment(String.valueOf(hiveColumnsByGuid.getAttributes().get("comment")));
                    hiveColumnInfo.setGuid(hiveColumnsByGuid.getGuid());
                    partitionColumns.add(hiveColumnInfo);
                }
            }
            HiveTblDetailInfo.HiveTblBasicInfo basic = new HiveTblDetailInfo.HiveTblBasicInfo();
            basic.setName(tableName);
            basic.setOwner(String.valueOf(atlasEntity.getAttributes().get("owner")));
            basic.setCreateTime(new java.text.SimpleDateFormat("yyyy MM-dd HH:mm:ss").format(atlasEntity.getCreateTime()));
            basic.setStore(String.valueOf(storage));
            basic.setComment(String.valueOf(atlasEntity.getAttributes().get("comment")));
            Set<String> labels = atlasEntity.getLabels();
            basic.setLabels(labels);
            basic.setIsParTbl(isPartTable);
            basic.setGuid(guid);
            hiveTblDetailInfo.setColumns(hiveColumnInfos);
            hiveTblDetailInfo.setBasic(basic);
            hiveTblDetailInfo.setPartitionKeys(partitionColumns);


            return hiveTblDetailInfo;
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000,ex.getMessage());
        }
    }

    @Override
    public List<PartInfo> getHiveTblPartition(String guid) throws DataGovernanceException {
        try {
            AtlasEntity atlasEntity = atlasService.getHiveTbl(guid);
            String db_name = String.valueOf(atlasEntity.getAttributes().get("qualifiedName")).split("@")[0];
            String tableName = db_name.split("\\.")[1];
            String dbName = db_name.split("\\.")[0];
            List<PartInfo> partInfo = new ArrayList<>();
            try {
                partInfo = metaInfoMapper.getPartInfo(dbName, tableName);
            } catch (DAOException e) {
                e.printStackTrace();
            }
            return partInfo;

        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000,ex.getMessage());
        }
    }

    @Override
    public String getTbSelect(String guid) throws DataGovernanceException {
        try {
            AtlasEntity atlasEntity = atlasService.getHiveTbl(guid);
            String db_name = String.valueOf(atlasEntity.getAttributes().get("qualifiedName")).split("@")[0];
            String tableName = db_name.split("\\.")[1];
            List<String> guids = new ArrayList<>();
            List<LinkedTreeMap<String, String>> columns = (List<LinkedTreeMap<String, String>>) atlasEntity.getAttributes().get("columns");
            for (LinkedTreeMap<String, String> column : columns) {
                guids.add(column.get("guid"));
            }
            List<String> fields = new ArrayList<>();
            List<AtlasEntity> hiveColumnsByGuids = atlasService.getHiveColumnsByGuids(guids);
            for (AtlasEntity hiveColumnsByGuid : hiveColumnsByGuids) {
                fields.add((String) hiveColumnsByGuid.getAttributes().get("name"));
            }
            Map<String, Object> hiveTblNameAndIsPartById = atlasService.getHiveTblNameAndIsPartById(guid);
            Boolean isPartTable = (Boolean) hiveTblNameAndIsPartById.get("isPartition");
            if (isPartTable == true) {
                List<String> partguids = new ArrayList<>();
                List<LinkedTreeMap<String, String>> partitionKeys = (List<LinkedTreeMap<String, String>>) atlasEntity.getAttributes().get("partitionKeys");
                for (LinkedTreeMap<String, String> column : partitionKeys) {
                    partguids.add(column.get("guid"));
                }
                List<AtlasEntity> hiveColumnsByGuids1 = atlasService.getHiveColumnsByGuids(partguids);
                for (AtlasEntity entity : hiveColumnsByGuids1) {
                    fields.add((String) entity.getAttributes().get("name"));
                }
            }
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT @$ ");
            for (int i = 0; i < fields.size() - 1; i++) {
                sql.append(fields.get(i)).append(", @$ ");
            }
            sql.append(fields.get(fields.size() - 1)).append(" @$ from  ").append(tableName);
            return sql.toString();
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000,ex.getMessage());
        }
    }

    @Override
    public String getTbCreate(String guid) throws DataGovernanceException {
        try {
            StringBuilder sql = new StringBuilder();
            AtlasEntity atlasEntity = atlasService.getHiveTbl(guid);
            String db_name = String.valueOf(atlasEntity.getAttributes().get("qualifiedName")).split("@")[0];
            String tableName = db_name.split("\\.")[1];
            List<String> guids = new ArrayList<>();
            List<LinkedTreeMap<String, String>> columns = (List<LinkedTreeMap<String, String>>) atlasEntity.getAttributes().get("columns");
            for (LinkedTreeMap<String, String> column : columns) {
                guids.add(column.get("guid"));
            }
            sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" @$ ( @$ ");
            List<String> fields = new ArrayList<>();
            List<AtlasEntity> hiveColumnsByGuids = atlasService.getHiveColumnsByGuids(guids);
            for (int i = 0; i < hiveColumnsByGuids.size(); i++) {
                if (i < hiveColumnsByGuids.size() - 1) {
                    AtlasEntity hiveColumnsByGuid = hiveColumnsByGuids.get(i);
                    StringBuilder sb = new StringBuilder();
                    sb.append((String) hiveColumnsByGuid.getAttributes().get("name")).append(" ").append((String) hiveColumnsByGuid.getAttributes().get("type"));
                    if (hiveColumnsByGuid.getAttributes().get("comment") == null) {
                        sb.append(", ");
                    } else {
                        sb.append(" COMMENT '").append(hiveColumnsByGuid.getAttributes().get("comment")).append("',");
                    }
                    fields.add(sb.append(" @$ ").toString());
                } else {
                    AtlasEntity hiveColumnsByGuid = hiveColumnsByGuids.get(i);
                    StringBuilder sb = new StringBuilder();
                    sb.append((String) hiveColumnsByGuid.getAttributes().get("name")).append(" ").append((String) hiveColumnsByGuid.getAttributes().get("type"));
                    if (hiveColumnsByGuid.getAttributes().get("comment") == null) {
                        sb.append(" ");
                    } else {
                        sb.append(" COMMENT '").append(hiveColumnsByGuid.getAttributes().get("comment")).append("' ");
                    }
                    fields.add(sb.append(" @$ ").toString());
                }
            }
            for (String field : fields) {
                sql.append(field);
            }
            sql.append(") @$ ");
            Map<String, Object> hiveTblNameAndIsPartById = atlasService.getHiveTblNameAndIsPartById(guid);
            Boolean isPartTable = (Boolean) hiveTblNameAndIsPartById.get("isPartition");
            if (isPartTable == true) {
                sql.append("PARTITIONED BY @$  ( @$ ");
                List<String> partguids = new ArrayList<>();
                List<LinkedTreeMap<String, String>> partitionKeys = (List<LinkedTreeMap<String, String>>) atlasEntity.getAttributes().get("partitionKeys");
                for (LinkedTreeMap<String, String> column : partitionKeys) {
                    partguids.add(column.get("guid"));
                }
                List<String> keyFields = new ArrayList<>();
                List<AtlasEntity> hiveColumnsByGuids1 = atlasService.getHiveColumnsByGuids(partguids);
                for (int i = 0; i < hiveColumnsByGuids1.size(); i++) {
                    if (i < hiveColumnsByGuids1.size() - 1) {
                        AtlasEntity entity = hiveColumnsByGuids1.get(i);
                        StringBuilder sb = new StringBuilder();
                        sb.append((String) entity.getAttributes().get("name")).append(" ").append((String) entity.getAttributes().get("type"));
                        if (entity.getAttributes().get("comment") == null) {
                            sb.append(", ");
                        } else {
                            sb.append(" COMMENT '").append(entity.getAttributes().get("comment")).append("', ");
                        }
                        keyFields.add(sb.append(" @$ ").toString());
                    } else {
                        AtlasEntity entity = hiveColumnsByGuids1.get(i);
                        StringBuilder sb = new StringBuilder();
                        sb.append((String) entity.getAttributes().get("name")).append(" ").append((String) entity.getAttributes().get("type"));
                        if (entity.getAttributes().get("comment") == null) {
                            sb.append(" ");
                        } else {
                            sb.append(" COMMENT '").append(entity.getAttributes().get("comment")).append("' ");
                        }
                        keyFields.add(sb.append(" @$ ").toString());
                    }
                }
                for (int i = 0; i < keyFields.size(); i++) {
                    sql.append(keyFields.get(i));
                }
                sql.append(")");
            }
            return sql.toString();
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000,ex.getMessage());
        }
    }

    @Override
    public void modifyComment(String guid, String commentStr) throws DataGovernanceException {
        try {
            atlasService.modifyComment(guid, commentStr);
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000,ex.getMessage());
        }
    }

    @Override
    public void bulkModifyComment(Map<String, String> commentMap) throws DataGovernanceException {
        commentMap.keySet().forEach(key -> {
            try {
                atlasService.modifyComment(key, commentMap.get(key));
            } catch (AtlasServiceException ex) {

            }
        });
    }

    @Override
    public void setLabels(String guid, Set<String> labels) throws DataGovernanceException {
        try {
            atlasService.setLabels(guid, labels);
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000,ex.getMessage());
        }
    }

    @Override
    public AtlasLineageInfo getHiveTblLineage(final String guid, final AtlasLineageInfo.LineageDirection direction, final int depth) throws DataGovernanceException {
        try {
            return atlasService.getLineageInfo(guid, direction, depth);
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(23000,exception.getMessage());
        }
    }

    @Override
    public List<TableInfo> getTop10Table() throws DAOException {
            return  metaInfoMapper.getTop10Table();
    }



}
