package com.webank.wedatasphere.dss.data.asset.service.impl;

import com.google.gson.internal.LinkedTreeMap;
import com.webank.wedatasphere.dss.data.asset.dao.MetaInfoMapper;
import com.webank.wedatasphere.dss.data.asset.dao.impl.MetaInfoMapperImpl;
import com.webank.wedatasphere.dss.data.asset.entity.HivePartInfo;
import com.webank.wedatasphere.dss.data.asset.entity.HiveStorageInfo;
import com.webank.wedatasphere.dss.data.asset.entity.HiveTblDetailInfo;
import com.webank.wedatasphere.dss.data.asset.entity.HiveTblSimpleInfo;
import com.webank.wedatasphere.dss.data.asset.service.AssetService;
import com.webank.wedatasphere.dss.data.common.atlas.AtlasClassificationV2;
import com.webank.wedatasphere.dss.data.common.atlas.AtlasService;
import com.webank.wedatasphere.dss.data.common.conf.AtlasConf;
import com.webank.wedatasphere.dss.data.common.exception.DAOException;
import com.webank.wedatasphere.dss.data.common.exception.DataGovernanceException;
import com.webank.wedatasphere.dss.data.common.utils.DateUtil;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.instance.AtlasClassification;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasEntityHeader;
import org.apache.atlas.model.instance.AtlasRelatedObjectId;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.apache.atlas.model.typedef.AtlasClassificationDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AssetServiceImpl implements AssetService {
    private static final Logger logger = LoggerFactory.getLogger(AssetServiceImpl.class);

    private AtlasService atlasService;
    private MetaInfoMapper metaInfoMapper;

    public AssetServiceImpl(AtlasService atlasService) {
        this.atlasService = atlasService;
        this.metaInfoMapper = new MetaInfoMapperImpl();
    }

    @Override
    public Map<String, Long> getHiveSummary() throws DataGovernanceException {
        try {
            Map<String, Long> result = new HashMap<>();

            result.put("hiveDb", atlasService.getHiveDbCnt());
            result.put("hiveTable", atlasService.getHiveTableCnt());
            result.put("hiveStore", metaInfoMapper.getTableStorage());

            return result;
        } catch (AtlasServiceException | DAOException | SQLException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }

    @Override
    public List<HiveTblSimpleInfo> searchHiveTable(String classification, String query,
                                                   int limit, int offset) throws DataGovernanceException {
        List<AtlasEntityHeader> atlasEntityHeaders = null;
        try {
            atlasEntityHeaders = atlasService.searchHiveTable(classification, "*" + query + "*", true, limit, offset);
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(ex.getMessage());
        }

        if (atlasEntityHeaders != null) {
            //columns  根据keyword来正则匹配过滤
            Pattern regex = Pattern.compile(query);
            return atlasEntityHeaders.parallelStream().filter(Objects::nonNull).map(atlasEntityHeader -> {
                HiveTblSimpleInfo hiveTblSimpleInfo = new HiveTblSimpleInfo();
                hiveTblSimpleInfo.setGuid(atlasEntityHeader.getGuid());
                hiveTblSimpleInfo.setName(stringValueOfObject(atlasEntityHeader.getAttribute("name")));
                String qualifiedName =stringValueOfObject(atlasEntityHeader.getAttribute("qualifiedName"));
                hiveTblSimpleInfo.setQualifiedName(qualifiedName);
                hiveTblSimpleInfo.setOwner(stringValueOfObject(atlasEntityHeader.getAttribute("owner")));
                Object createTime = atlasEntityHeader.getAttribute("createTime");
                if (createTime != null) {
                    hiveTblSimpleInfo.setCreateTime(DateUtil.unixToTimeStr((Double) createTime));
                }
                if(null != qualifiedName && qualifiedName.split("\\.").length >0){
                    String dbName = qualifiedName.split("\\.")[0];
                    hiveTblSimpleInfo.setDbName(dbName);
                }
                hiveTblSimpleInfo.setLabels(atlasEntityHeader.getLabels());

                try {
                    AtlasEntity atlasEntity = atlasService.getHiveTblByGuid(atlasEntityHeader.getGuid());

                    //comment
                    hiveTblSimpleInfo.setComment(stringValueOfObject(atlasEntity.getAttribute("comment")));
                    List<Map<String,Object>> atlasRelatedObjectIdListForColumns = (List<Map<String,Object>>)atlasEntity.getRelationshipAttribute("columns");
                    if(null != query && !query.trim().equalsIgnoreCase("")) {
                        hiveTblSimpleInfo.setColumns(atlasRelatedObjectIdListForColumns.stream().map(columnMap -> columnMap.getOrDefault("displayText","").toString())
                                .filter(columnName -> regex.matcher(columnName).find()).collect(Collectors.toList()));
                    }
                    //classifications
                    List<HiveTblDetailInfo.HiveClassificationInfo> classificationInfoList = getClassificationInfoList(atlasEntity);
                    hiveTblSimpleInfo.setClassifications(classificationInfoList);
                } catch (AtlasServiceException ex) {
                    logger.error(ex.getMessage());
                }

                return hiveTblSimpleInfo;
            }).collect(Collectors.toList());
        }
        return null;
    }

    private String stringValueOfObject(Object obj){
        if(null !=obj) {
            return obj.toString();
        }
        else {
            return null;
        }
    }

    @Override
    public String getHiveTblGuid(String qualifiedName) throws DataGovernanceException{
        qualifiedName = qualifiedName + "@" + AtlasConf.ATLAS_CLUSTER_NAME.getValue();
        Map<String,String> uniqAttributes =new HashMap<>();
        uniqAttributes.put("qualifiedName",qualifiedName);
        try{
            AtlasEntity atlasEntity = atlasService.getHiveTblByAttribute(uniqAttributes,true,true);
            if(atlasEntity == null){
                logger.warn(String.format("%s not exist in atlas", qualifiedName));
                return null;
            }
            else {
                return atlasEntity.getGuid();
            }
        } catch (AtlasServiceException ex) {
            logger.warn(String.format("%s not exist in atlas", qualifiedName));
            return null;
        }
    }

    @Override
    public HiveTblDetailInfo getHiveTblDetail(String guid) throws DataGovernanceException {
        try {
            AtlasEntity atlasEntity = atlasService.getHiveTblByGuid(guid);

            HiveTblDetailInfo hiveTblDetailInfo = new HiveTblDetailInfo();
            hiveTblDetailInfo.setBasic(getBasicInfo(guid, atlasEntity));
            hiveTblDetailInfo.setColumns(getBasicColumnInfoList(atlasEntity));
            hiveTblDetailInfo.setPartitionKeys(getPartitionColumnInfoList(atlasEntity));
            hiveTblDetailInfo.setClassifications(getClassificationInfoList(atlasEntity));

            return hiveTblDetailInfo;
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(ex.getMessage());
        }
    }

    private HiveTblDetailInfo.HiveTblBasicInfo getBasicInfo(String guid, AtlasEntity atlasEntity) throws AtlasServiceException {
        Map<String, Object> hiveTblAttributesMap = atlasService.getHiveTblAttributesByGuid(guid);
        Boolean isPartTable = (Boolean) hiveTblAttributesMap.get("isPartition");
        int storage = 0;
        String db_name = String.valueOf(atlasEntity.getAttributes().get("qualifiedName")).split("@")[0];
        String tableName = db_name.split("\\.")[1];
        String dbName = db_name.split("\\.")[0];
        try {
            storage = metaInfoMapper.getTableInfo(dbName, tableName, isPartTable);
        } catch (SQLException e) {
           // logger.error("invoke failed",e);
        }

        HiveTblDetailInfo.HiveTblBasicInfo basic = new HiveTblDetailInfo.HiveTblBasicInfo();
        basic.setName(tableName);
        basic.setOwner(String.valueOf(atlasEntity.getAttributes().getOrDefault("owner","NULL")));
        basic.setCreateTime(new java.text.SimpleDateFormat("yyyy MM-dd HH:mm:ss").format(atlasEntity.getCreateTime()));
        basic.setStore(String.valueOf(storage));
        basic.setComment(String.valueOf(atlasEntity.getAttributes().getOrDefault("comment","NULL")));
        Set<String> labels = atlasEntity.getLabels();
        basic.setLabels(labels);
        basic.setIsParTbl(isPartTable);
        basic.setGuid(guid);
        basic.setTableType(hiveTblAttributesMap.getOrDefault("tableType","NULL").toString());
        basic.setLocation(hiveTblAttributesMap.getOrDefault("location","NULL").toString());

        return basic;
    }

    private List<HiveTblDetailInfo.HiveColumnInfo> getBasicColumnInfoList(AtlasEntity atlasEntity) throws AtlasServiceException {
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
        return hiveColumnInfos;
    }

    private List<HiveTblDetailInfo.HiveColumnInfo> getPartitionColumnInfoList(AtlasEntity atlasEntity) throws AtlasServiceException {
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
        return partitionColumns;
    }

    private List<HiveTblDetailInfo.HiveClassificationInfo> getClassificationInfoList(AtlasEntity atlasEntity) throws AtlasServiceException {
        if(atlasEntity.getClassifications() ==null) {
            return null;
        }
        else {
            List<HiveTblDetailInfo.HiveClassificationInfo> hiveClassificationInfoList =new ArrayList<>();
            String typeName =null;
            AtlasClassificationDef atlasClassificationDef =null;

            List<AtlasClassification> atlasClassificationList = atlasEntity.getClassifications();
            for (AtlasClassification atlasClassification : atlasClassificationList) {
                typeName = atlasClassification.getTypeName();
                atlasClassificationDef = getClassificationDefByName(typeName);
                hiveClassificationInfoList.add(
                        new HiveTblDetailInfo.HiveClassificationInfo(typeName,atlasClassificationDef.getSuperTypes(),atlasClassificationDef.getSubTypes()));
            }
            return hiveClassificationInfoList;
        }
    }

    private AtlasClassificationDef getClassificationDefByName(String name) throws DataGovernanceException {
        try {
            return atlasService.getClassificationDefByName(name);
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }

    @Override
    public List<HivePartInfo> getHiveTblPartition(String guid) throws DataGovernanceException {
        try {
            AtlasEntity atlasEntity = atlasService.getHiveTblByGuid(guid);
            String db_name = String.valueOf(atlasEntity.getAttributes().get("qualifiedName")).split("@")[0];
            String tableName = db_name.split("\\.")[1];
            String dbName = db_name.split("\\.")[0];
            List<HivePartInfo> hivePartInfo = new ArrayList<>();
            try {
                hivePartInfo = metaInfoMapper.getPartInfo(dbName, tableName);
            } catch (SQLException e) {
//                logger.error("invoke failed",e);
            }
            return hivePartInfo;

        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(ex.getMessage());
        }
    }

    @Override
    public String getTbSelect(String guid) throws DataGovernanceException {
        try {
            AtlasEntity atlasEntity = atlasService.getHiveTblByGuid(guid);
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
            Map<String, Object> hiveTblAttributesMap = atlasService.getHiveTblAttributesByGuid(guid);
            Boolean isPartTable = (Boolean) hiveTblAttributesMap.get("isPartition");
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
            throw new DataGovernanceException(ex.getMessage());
        }
    }

    @Override
    public String getTbCreate(String guid) throws DataGovernanceException {
        try {
            StringBuilder sql = new StringBuilder();
            AtlasEntity atlasEntity = atlasService.getHiveTblByGuid(guid);
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
            Map<String, Object> hiveTblAttributesMap = atlasService.getHiveTblAttributesByGuid(guid);
            Boolean isPartTable = (Boolean) hiveTblAttributesMap.get("isPartition");
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
            throw new DataGovernanceException(ex.getMessage());
        }
    }

    @Override
    public void modifyComment(String guid, String commentStr) throws DataGovernanceException {
        try {
            atlasService.modifyComment(guid, commentStr);
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(ex.getMessage());
        }
    }

    @Override
    public void bulkModifyComment(Map<String, String> commentMap) throws DataGovernanceException {
        commentMap.keySet().forEach(key -> {
            try {
                atlasService.modifyComment(key, commentMap.get(key));
            } catch (AtlasServiceException ex) {
                throw new DataGovernanceException(ex.getMessage());
            }
        });

    }

    @Override
    public void setLabels(String guid, Set<String> labels) throws DataGovernanceException {
        try {
            atlasService.setLabels(guid, labels);
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(ex.getMessage());
        }
    }

    @Override
    public void removeLabels(String guid, Set<String> labels) throws DataGovernanceException {
        try {
            atlasService.removeLabels(guid, labels);
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(ex.getMessage());
        }
    }

    @Override
    public AtlasLineageInfo getHiveTblLineage(final String guid, final AtlasLineageInfo.LineageDirection direction, final int depth) throws DataGovernanceException {
        try {
            return atlasService.getLineageInfo(guid, direction, depth);
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }

    @Override
    public List<HiveStorageInfo> getTop10Table() throws DataGovernanceException, SQLException {
            return  metaInfoMapper.getTop10Table();
    }

    @Override
    public void addClassifications(String guid, List<AtlasClassification> classifications) throws DataGovernanceException {
        try {
            atlasService.addClassifications(guid,classifications);
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }

    @Override
    public void deleteClassification(String guid, String classificationName) throws DataGovernanceException{
        try {
            atlasService.deleteClassification(guid, classificationName);
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }

    @Override
    public void deleteClassifications(String guid, List<AtlasClassification> classifications) throws DataGovernanceException {
        try {
            atlasService.deleteClassifications(guid, classifications);
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }

    @Override
    public void updateClassifications(String guid, List<AtlasClassification> classifications) throws DataGovernanceException {
        try {
            atlasService.updateClassifications(guid, classifications);
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }

    @Override
    public void removeAndAddClassifications(String guid, List<AtlasClassification> newClassifications) throws DataGovernanceException {
        try {
            atlasService.removeAndAddClassifications(guid, newClassifications);
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }

    @Override
    public AtlasClassificationV2.AtlasClassificationsV2 getClassifications(String guid) throws DataGovernanceException {
        try {
            return atlasService.getClassifications(guid);
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(exception.getMessage());
        }
    }
}
