package com.webank.wedatasphere.dss.data.governance.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Sets;
import com.google.gson.internal.LinkedTreeMap;
import com.webank.wedatasphere.dss.data.governance.atlas.AtlasService;
import com.webank.wedatasphere.dss.data.governance.dao.*;
import com.webank.wedatasphere.dss.data.governance.dto.HiveTblStatsDTO;
import com.webank.wedatasphere.dss.data.governance.dto.SearchLabelDTO;
import com.webank.wedatasphere.dss.data.governance.entity.*;
import com.webank.wedatasphere.dss.data.governance.exception.DAOException;
import com.webank.wedatasphere.dss.data.governance.exception.DataGovernanceException;
import com.webank.wedatasphere.dss.data.governance.service.AssetService;
import com.webank.wedatasphere.dss.data.governance.utils.DateUtil;
import com.webank.wedatasphere.dss.data.governance.vo.*;
import jersey.repackaged.com.google.common.collect.Lists;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.glossary.AtlasGlossaryTerm;
import org.apache.atlas.model.glossary.relations.AtlasTermAssignmentHeader;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasEntityHeader;
import org.apache.atlas.model.instance.AtlasStruct;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.apache.atlas.model.typedef.AtlasClassificationDef;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Service
public class AssetServiceImpl implements AssetService {
    private static final Logger logger = LoggerFactory.getLogger(AssetServiceImpl.class);
    @Resource
    private AtlasService atlasService;
    private WorkspaceInfoMapper workspaceInfoMapper;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy MM-dd HH:mm:ss");

    @Resource
    private TableColumnCountQueryMapper tableColumnCountQueryMapper;

    @Resource
    private TableSizeInfoMapper tableSizeInfoMapper;

    @Resource
    private TableStorageMapper tableStorageMapper;

    @Resource
    private PartInfoMapper partInfoMapper;

    @Resource
    private TableStorageInfoMapper tableStorageInfoMapper;

    @Resource
    private TableSizePartitionInfoMapper tableSizePartitionInfoMapper;

    public AssetServiceImpl(AtlasService atlasService) {
        this.atlasService = atlasService;
    }

    @Override
    public Map<String, Long> getHiveSummary() throws DataGovernanceException {
        try {
            Map<String, Long> result = new HashMap<>();

            result.put("hiveDb", atlasService.getHiveDbCnt());
            result.put("hiveTable", atlasService.getHiveTableCnt());
            result.put("hiveStore", tableStorageMapper.getTableStorage().get(0));

            return result;
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(23000, exception.getMessage());
        }
    }


    @Override
    public CreateModelTypeInfo createModelType(CreateModelTypeVO vo) throws DataGovernanceException {
        if (!ClassificationConstant.isTypeScope(vo.getType())) {
            throw new DataGovernanceException(23000, "不支持此类型" + vo.getType());
        }
        try {
            AtlasClassificationDef atlasClassificationDef = atlasService.createSubClassification(ClassificationConstant.formatName(vo.getType(), vo.getName()).get(), ClassificationConstant.getRoot(vo.getType()).get());
            CreateModelTypeInfo dto = new CreateModelTypeInfo();
            dto.setGuid(atlasClassificationDef.getGuid());
            dto.setName(atlasClassificationDef.getName());
            return dto;
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000, ex.getMessage());
        }
    }


    @Override
    public void deleteModelType(DeleteModelTypeVO vo) throws Exception {
        if (!ClassificationConstant.isTypeScope(vo.getType())) {
            throw new DataGovernanceException(23000, "不支持此类型" + vo.getType());
        }
        try {
            atlasService.deleteClassification(ClassificationConstant.formatName(vo.getType(), vo.getName()).get());
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000, ex.getMessage());
        }
    }

    @Override
    public UpdateModelTypeInfo updateModelType(UpdateModelTypeVO vo) throws Exception {
        if (!ClassificationConstant.isTypeScope(vo.getType())) {
            throw new DataGovernanceException(23000, "不支持此类型" + vo.getType());
        }
        String newName = ClassificationConstant.formatName(vo.getType(), vo.getName()).get();
        String root = ClassificationConstant.getRoot(vo.getType()).get();
        String orgName = ClassificationConstant.formatName(vo.getType(), vo.getOrgName()).get();
        AtlasClassificationDef atlasClassificationDef = null;
        //首先尝试创建
        try {
            atlasClassificationDef = atlasService.createSubClassification(newName, root);
        } catch (AtlasServiceException ex) {
            //创建失败直接回退
            throw new DataGovernanceException(23000, ex.getMessage());
        }

        //尝试原类型
        try {
            atlasService.deleteClassification(orgName);
        } catch (AtlasServiceException ex) {
            //回滚删除创建的新类型
            try {
                atlasService.deleteClassification(newName);
            } catch (AtlasServiceException ex1) {
                throw new DataGovernanceException(23000, ex1.getMessage());
            }
            throw new DataGovernanceException(23000, ex.getMessage());
        }
        UpdateModelTypeInfo info = new UpdateModelTypeInfo();
        info.setGuid(atlasClassificationDef.getGuid());
        info.setName(atlasClassificationDef.getName());
        return info;
    }


    @Override
    public void bindModelType(BindModelVO vo) throws Exception {
        if (!ClassificationConstant.isTypeScope(vo.getModelType())) {
            throw new DataGovernanceException(23000, "不支持此类型" + vo.getModelType());
        }
        String tableGuid = vo.getGuid();

        if (StringUtils.isBlank(tableGuid)) {
            tableGuid = getGuid(vo.getTableName());
        }

        try {
            atlasService.addClassification(ClassificationConstant.formatName(vo.getModelType(), vo.getModelName()).get(), tableGuid, false);
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000, ex.getMessage());
        }

    }

    @Override
    public void unBindModel(UnBindModelVO vo) throws Exception {
        if (!ClassificationConstant.isTypeScope(vo.getModelType())) {
            throw new DataGovernanceException(23000, "不支持此类型" + vo.getModelType());
        }
        String tableGuid = vo.getGuid();

        if (StringUtils.isBlank(tableGuid)) {
            tableGuid = getGuid(vo.getTableName());
        }

        try {
            atlasService.deleteClassification(tableGuid, ClassificationConstant.formatName(vo.getModelType(), vo.getModelName()).get());
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000, ex.getMessage());
        }

    }

    private String getGuid(String tableName) throws DataGovernanceException {

        //首先搜索指定表,查找guid
        List<AtlasEntityHeader> atlasEntityHeaders = null;
        try {
            atlasEntityHeaders = atlasService.searchHiveTable0(null, tableName, null,true, 1, 0);
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000, ex.getMessage());
        }
        if (CollectionUtils.isEmpty(atlasEntityHeaders)) {
            throw new DataGovernanceException(23000, "table " + tableName + " not find");
        }

        if (atlasEntityHeaders.size() > 1) {
            throw new DataGovernanceException(23000, "table " + tableName + " duplicate " + atlasEntityHeaders);
        }
        return atlasEntityHeaders.get(0).getGuid();
    }

    @Override
    public List<HiveTblSimpleInfo> searchHiveTable(String classification, String query, String termName,
                                                   int limit, int offset) throws DataGovernanceException {
        List<AtlasEntityHeader> atlasEntityHeaders = null;
        try {
            atlasEntityHeaders = atlasService.searchHiveTable0(classification, query, termName,true, limit, offset);
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000, ex.getMessage());
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
                hiveTblBasic.setClassifications(atlasEntityHeader.getClassificationNames());

                Object comment = atlasEntityHeader.getAttribute("comment");
                if (comment != null) {
                    hiveTblBasic.setComment(comment.toString());
                }

                Object aliases = atlasEntityHeader.getAttribute("aliases");
                if (aliases != null) {
                    hiveTblBasic.setAliases(aliases.toString());
                }

                Object lastAccessTime = atlasEntityHeader.getAttribute("lastAccessTime");
                if (lastAccessTime != null) {
                    hiveTblBasic.setLastAccessTime(DateUtil.unixToTimeStr((Double) lastAccessTime));
                }

                Object parameters = atlasEntityHeader.getAttribute("parameters");
                if (parameters != null) {
                    Map mapParameters = (Map) parameters;
                    Object totalSize = mapParameters.get("totalSize");
                    if (totalSize != null) {
                        hiveTblBasic.setTotalSize(totalSize.toString());
                    }
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
            throw new DataGovernanceException(23000, ex.getMessage());
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
            long storage = 0;
            String db_name = String.valueOf(atlasEntity.getAttributes().get("qualifiedName")).split("@")[0];
            String tableName = db_name.split("\\.")[1];
            String dbName = db_name.split("\\.")[0];
            if (isPartTable){
                List<Long> partTableInfo = tableStorageMapper.getPartTableInfo(dbName, tableName);
                storage = partTableInfo.isEmpty()?0:partTableInfo.get(0);
            }else{
                List<Long> tableInfo = tableStorageMapper.getTableInfo(dbName, tableName);
                storage = tableInfo.isEmpty()?0:tableInfo.get(0);
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
            basic.setQualifiedName(atlasEntity.getAttribute("qualifiedName").toString());
            basic.setOwner(String.valueOf(atlasEntity.getAttributes().get("owner")));
            basic.setCreateTime(new SimpleDateFormat("yyyy MM-dd HH:mm:ss").format(atlasEntity.getCreateTime()));
            basic.setStore(String.valueOf(storage));
            //设置标签
            Set<String> labels = Sets.newHashSet();
            List<Map<String,String>> meanings =(List<Map<String,String>>) atlasEntity.getRelationshipAttributes().get("meanings");
            if (!CollectionUtils.isEmpty(meanings)) {
               labels =  meanings.stream()
                       .filter(label->isLabel(label.get("guid")))
                       .map(label->(label.get("displayText")))
                       .collect(Collectors.toSet());
            }
            basic.setLabels(labels);
            basic.setIsParTbl(isPartTable);
            basic.setGuid(guid);

//            if (!CollectionUtils.isEmpty(atlasEntity.getClassifications())) {
//                basic.setClassifications(atlasEntity.getClassifications().stream().map(AtlasStruct::getTypeName).collect(Collectors.toList()));
//            }
            if (!CollectionUtils.isEmpty(atlasEntity.getClassifications())) {
                basic.setClassifications(atlasEntity.getClassifications().stream()
                    .map(AtlasStruct::getTypeName)
                    .filter(typeName ->{
                        String[] split = typeName.split("_");
                        int modeType = ClassificationConstant.valueOf(split[0].toUpperCase(Locale.ROOT)).getType();
                        return (modeType == 3 || modeType == 4);
                    }).collect(Collectors.toList()));
            }
            Object comment = atlasEntity.getAttribute("comment");
            if (comment != null) {
                basic.setComment(comment.toString());
            }

            Object aliases = atlasEntity.getAttribute("aliases");
            if (aliases != null) {
                basic.setAliases(aliases.toString());
            }

            Object lastAccessTime = atlasEntity.getAttribute("lastAccessTime");
            if (lastAccessTime != null) {
                basic.setLastAccessTime(DateUtil.unixToTimeStr((Double) lastAccessTime));
            }

            Object parameters = atlasEntity.getAttribute("parameters");
            if (parameters != null) {
                Map mapParameters = (Map) parameters;
                Object totalSize = mapParameters.get("totalSize");
                if (totalSize != null) {
                    basic.setTotalSize(totalSize.toString());
                }

            }

            Object external =  atlasEntity.getAttribute("tableType");
            if (external != null) {
                basic.setExternal(external.toString());
            }


            hiveTblDetailInfo.setColumns(hiveColumnInfos);
            hiveTblDetailInfo.setBasic(basic);
            hiveTblDetailInfo.setPartitionKeys(partitionColumns);


            return hiveTblDetailInfo;
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000, ex.getMessage());
        }
    }

    @Override
    public List<PartInfo> getHiveTblPartition(String guid) throws DataGovernanceException {
        try {
            AtlasEntity atlasEntity = atlasService.getHiveTbl(guid);
            String db_name = String.valueOf(atlasEntity.getAttributes().get("qualifiedName")).split("@")[0];
            return getPartInfos(db_name);

        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000, ex.getMessage());
        }
    }

    private List<PartInfo> getPartInfos(String db_name) {
        String tableName = db_name.split("\\.")[1];
        String dbName = db_name.split("\\.")[0];
        List<PartInfo> partInfo = partInfoMapper.query(dbName, tableName);
        return partInfo;
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
            throw new DataGovernanceException(23000, ex.getMessage());
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
            throw new DataGovernanceException(23000, ex.getMessage());
        }
    }

    @Override
    public void modifyComment(String guid, String commentStr) throws DataGovernanceException {
        try {
            atlasService.modifyComment(guid, commentStr);
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000, ex.getMessage());
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
            throw new DataGovernanceException(23000, ex.getMessage());
        }
    }

    @Override
    public AtlasLineageInfo getHiveTblLineage(final String guid, final AtlasLineageInfo.LineageDirection direction, final int depth) throws DataGovernanceException {
        try {
            return atlasService.getLineageInfo(guid, direction, depth);
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(23000, exception.getMessage());
        }
    }

    @Override
    public List<TableInfo> getTop10Table() throws DAOException {
        List<TableInfo> tableInfoList = tableStorageInfoMapper.query();
        // 根据存储量进行排序，并获取Top10
        tableInfoList.sort((tb1,tb2)-> Long.compare(Long.parseLong(tb2.getStorage()), Long.parseLong(tb1.getStorage())));
        return tableInfoList.stream().limit(10).collect(Collectors.toList());
    }

    @Override
    public List<TableInfo> getTop10TableByLabelDay(String label,String startDate,String endDate) throws DAOException, DataGovernanceException {

        if (!StringUtils.isBlank(label)){
            label = GlossaryConstant.LABEL.formatQuery(label);
        }

        //根据标签和日期去Atlas获取tableName列表
        List<String> tableNameList = null;
        try {
            List<AtlasEntityHeader> atlasEntityHeaders = atlasService.searchHiveTable0(null,null,label,true,0,0);
            //对Atlas查询结果根据day进行过滤，并对tableName进行格式化处理

            if (atlasEntityHeaders != null) {
                tableNameList = atlasEntityHeaders.stream()
                        .filter(atlasEntityHeader -> {
                            long createTime = 0;
                            Object ct = atlasEntityHeader.getAttribute("createTime");
                            if (ct != null) {
                                try {
                                    createTime = simpleDateFormat.parse(DateUtil.unixToTimeStr((Double) ct)).getTime()/1000;
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            return Long.parseLong(startDate) < createTime && createTime < Long.parseLong(endDate);
                        }).map(atlasEntityHeader -> {
                            String qualifiedName = atlasEntityHeader.getAttribute("qualifiedName").toString();
                            return StringUtils.substringBefore(qualifiedName,"@");
                        }).collect(Collectors.toList());
            }
        } catch (AtlasServiceException ex) {
            throw new DataGovernanceException(23000, ex.getMessage());
        }

        if (!CollectionUtils.isEmpty(tableNameList)){
            //根据表名（DB.TABLE）列表去数据库获取存储量
            List<TableInfo> tableInfoList = tableStorageInfoMapper.queryByTableName(Wrappers.<TableInfo>lambdaQuery().in(TableInfo::getTableName, tableNameList));

            tableInfoList.sort((tb1,tb2)-> Long.compare(Long.parseLong(tb2.getStorage()), Long.parseLong(tb1.getStorage())));

            return tableInfoList.stream().limit(10).collect(Collectors.toList());
        }
        return null;
    }


    @Override
    public HiveTblStatsDTO hiveTblStats(String dbName, String tableName, String guid) throws Exception {
        //如果有guid则使用guid
        if (StringUtils.isNotBlank(guid)) {
            String qualifiedName = getQualifiedName(guid);
            dbName = StringUtils.substringBefore(qualifiedName, ".");
            tableName = StringUtils.substringAfter(qualifiedName, ".");
        }


        HiveTblStatsDTO dto = new HiveTblStatsDTO();

        //字段数量
        List<TableColumnCount> columnCounts = tableColumnCountQueryMapper.query(Wrappers.<TableColumnCount>lambdaQuery().eq(TableColumnCount::getDbName, dbName).eq(TableColumnCount::getTblName, tableName));
        if (!CollectionUtils.isEmpty(columnCounts)) {
            dto.setColumnCount(columnCounts.get(0).getColumnCount());
        }

        //表容量
        List<TableSizeInfo> tableSizeInfos = tableSizeInfoMapper.query(Wrappers.<TableSizeInfo>lambdaQuery().eq(TableSizeInfo::getDbName, dbName).eq(TableSizeInfo::getTblName, tableName));
        if (!CollectionUtils.isEmpty(tableSizeInfos)) {
            tableSizeInfos.forEach(tableSizeInfo -> {
                if (StringUtils.equals("totalSize", tableSizeInfo.getParamKey())) {
                    dto.setTotalSize(Long.parseLong(tableSizeInfo.getParamValue()));
                }
                if (StringUtils.equals("numFiles", tableSizeInfo.getParamKey())) {
                    dto.setNumFiles(Integer.parseInt(tableSizeInfo.getParamValue()));
                }
            });
        }
        //分区信息
        List<TablePartitionSizeInfo> tablePartitionSizeInfos = tableSizePartitionInfoMapper.query(Wrappers.<TablePartitionSizeInfo>lambdaQuery().eq(TablePartitionSizeInfo::getDbName, dbName).eq(TablePartitionSizeInfo::getTblName, tableName));
        if (!CollectionUtils.isEmpty(tablePartitionSizeInfos)) {
            Set<Long> partIds = Sets.newHashSet();
            tablePartitionSizeInfos.forEach(tablePartitionSizeInfo -> {
                partIds.add(tablePartitionSizeInfo.getParId());
                if (StringUtils.equals("totalSize", tablePartitionSizeInfo.getParamKey())) {
                    dto.setTotalSize(dto.getTotalSize() + Long.parseLong(tablePartitionSizeInfo.getParamValue()));
                }
                if (StringUtils.equals("numFiles", tablePartitionSizeInfo.getParamKey())) {
                    dto.setNumFiles(dto.getNumFiles() + Integer.parseInt(tablePartitionSizeInfo.getParamValue()));
                }
            });
            dto.setPartitionCount(partIds.size());
        }
        return dto;
    }

    private String getQualifiedName(String guid) throws AtlasServiceException {
        AtlasEntity atlasEntity = atlasService.getHiveTbl(guid);
        return StringUtils.substringBefore(String.valueOf(atlasEntity.getAttributes().get("qualifiedName")), "@");
    }

    @Override
    public Long hiveTblSize(String dbName, String tableName, String guid) throws Exception {
        //如果有guid则使用guid
        if (StringUtils.isNotBlank(guid)) {
            String qualifiedName = getQualifiedName(guid);
            dbName = StringUtils.substringBefore(qualifiedName, ".");
            tableName = StringUtils.substringAfter(qualifiedName, ".");
        }

        AtomicReference<Long> size = new AtomicReference<>(0L);
        //表容量
        List<TableSizeInfo> tableSizeInfos = tableSizeInfoMapper.query(Wrappers.<TableSizeInfo>lambdaQuery().eq(TableSizeInfo::getDbName, dbName).eq(TableSizeInfo::getTblName, tableName));
        if (!CollectionUtils.isEmpty(tableSizeInfos)) {
            tableSizeInfos.forEach(tableSizeInfo -> {
                if (StringUtils.equals("totalSize", tableSizeInfo.getParamKey())) {
                    size.set(Long.parseLong(tableSizeInfo.getParamValue()));
                }
            });
        }
        //分区信息
        List<TablePartitionSizeInfo> tablePartitionSizeInfos = tableSizePartitionInfoMapper.query(Wrappers.<TablePartitionSizeInfo>lambdaQuery().eq(TablePartitionSizeInfo::getDbName, dbName).eq(TablePartitionSizeInfo::getTblName, tableName));
        if (!CollectionUtils.isEmpty(tablePartitionSizeInfos)) {
            Set<Long> partIds = Sets.newHashSet();
            tablePartitionSizeInfos.forEach(tablePartitionSizeInfo -> {
                partIds.add(tablePartitionSizeInfo.getParId());
                if (StringUtils.equals("totalSize", tablePartitionSizeInfo.getParamKey())) {
                    size.set(size.get() + Long.parseLong(tablePartitionSizeInfo.getParamValue()));
                }

            });

        }
        return size.get();
    }


    @Override
    public CreateLabelInfo createLabel(CreateLabelVO vo) throws Exception {
        try {
            return CreateLabelInfo.from(atlasService.createLabel(vo.getName()));
        } catch (AtlasServiceException exception) {
            throw new DataGovernanceException(23000, exception.getMessage());
        }

    }


    @Override
    public void deleteLabel(DeleteLabelVO vo) throws Exception {
        try {
            atlasService.deleteLabel(vo.getName());
        } catch (Exception exception) {
            throw new DataGovernanceException(23000, exception.getMessage());
        }
    }


    @Override
    public UpdateLabelInfo updateLabel(UpdateLabelVO vo) throws Exception {

        if (StringUtils.equals(vo.getName(),vo.getOrgName())){
            throw new DataGovernanceException(23000, "修改标签名称前后相同");
        }

        Optional<String> termGuidOptional = getTermGuidOptional(vo.getOrgName());
        if (!termGuidOptional.isPresent()){
            throw new DataGovernanceException(23000, "标签"+vo.getOrgName()+ "不存在");
        }

        AtlasGlossaryTerm atlasGlossaryTerm = null;
        //首先新建标签
        try {
            atlasGlossaryTerm = atlasService.createLabel(vo.getName());
        } catch (Exception exception) {
            throw new DataGovernanceException(23000, exception.getMessage());
        }
        //尝试删除原标签
        try {
            atlasService.deleteLabel(vo.getOrgName());
        } catch (Exception exception) {
            //休眠五秒,atlas新建分词有延迟
            logger.error("wait for 5 seconds to roll back term " + vo.getName());
            TimeUnit.SECONDS.sleep(5);
            //回滚删除新建标签
            try {
                atlasService.deleteLabel(vo.getName());
            } catch (Exception exception1) {
                throw new DataGovernanceException(23000, exception1.getMessage());
            }
            throw new DataGovernanceException(23000, exception.getMessage());
        }

        return UpdateLabelInfo.from(atlasGlossaryTerm);
    }


    @Override
    public void bindLabel(BindLabelVO vo) throws Exception {

        if (StringUtils.isNotBlank(vo.getLabelGuid()) && StringUtils.isNotBlank(vo.getTableGuid())) {
            try {
                atlasService.assignTermToEntities(vo.getLabelGuid(), Lists.newArrayList(vo.getTableGuid()));
            } catch (AtlasServiceException exception1) {
                throw new DataGovernanceException(23000, exception1.getMessage());
            }
        }

        Optional<String> termGuidOptional = getTermGuidOptional(vo.getLabel());
        List<AtlasEntityHeader> entityHeaders = getAtlasEntityHeaders(vo.getTableName());

        try {
            atlasService.assignTermToEntities(termGuidOptional.get(), Lists.newArrayList(entityHeaders.get(0).getGuid()));
        } catch (AtlasServiceException exception1) {
            throw new DataGovernanceException(23000, exception1.getMessage());
        }

    }

    private Optional<String> getTermGuidOptional(String label) throws AtlasServiceException, DataGovernanceException {
        Optional<String> termGuidOptional = atlasService.getTermGuid(GlossaryConstant.LABEL, label);
        if (!termGuidOptional.isPresent()) {
            throw new DataGovernanceException(23000, label + "标签不存在");
        }
        return termGuidOptional;
    }

    @Override
    public void unBindLabel(UnBindLabelVO vo) throws Exception {
        if (StringUtils.isNotBlank(vo.getLabelGuid()) && StringUtils.isNotBlank(vo.getTableGuid()) && StringUtils.isNotBlank(vo.getRelationGuid())) {
            try {
                atlasService.disassociateTermFromEntities(vo.getLabelGuid(), Lists.newArrayList(RelatedObjectId.from(vo.getTableGuid(), vo.getRelationGuid())));
            } catch (AtlasServiceException exception1) {
                throw new DataGovernanceException(23000, exception1.getMessage());
            }
        }

        Optional<String> termGuidOptional = getTermGuidOptional(vo.getLabel());
        List<AtlasEntityHeader> entityHeaders = getAtlasEntityHeaders(vo.getTableName());

        String termGuid = termGuidOptional.get();
        AtlasEntityHeader atlasEntityHeader = entityHeaders.get(0);
        String tableGuid = atlasEntityHeader.getGuid();
        Optional<AtlasTermAssignmentHeader> termAssignmentHeaderOptional = atlasEntityHeader.getMeanings().stream().filter(atlasTermAssignmentHeader -> StringUtils.equals(termGuid, atlasTermAssignmentHeader.getTermGuid())).findFirst();
        if (!termAssignmentHeaderOptional.isPresent()){
            throw new DataGovernanceException(23000, vo.getTableName() + "表与标签" + vo.getLabel() +"未绑定");
        }
        try {
            atlasService.disassociateTermFromEntities(termGuidOptional.get(), Lists.newArrayList(RelatedObjectId.from(tableGuid, termAssignmentHeaderOptional.get().getRelationGuid())));
        } catch (AtlasServiceException exception1) {
            throw new DataGovernanceException(23000, exception1.getMessage());
        }
    }

    private List<AtlasEntityHeader> getAtlasEntityHeaders(String tableName) throws AtlasServiceException, DataGovernanceException {
        List<AtlasEntityHeader> entityHeaders = atlasService.searchHiveTable0(null, tableName, null,true, 1, 0);
        if (CollectionUtils.isEmpty(entityHeaders)) {
            throw new DataGovernanceException(23000, tableName + "表不存在");
        }
        return entityHeaders;
    }

    @Override
    public List<SearchLabelDTO> listLabels(String query, Integer limit, Integer offset) throws Exception {
        List<AtlasEntityHeader> atlasEntityHeaders = atlasService.listLabels(query, limit, offset);
        Optional<String> labelOptional = atlasService.getRootGlossaryGuid(GlossaryConstant.LABEL);
        if (!labelOptional.isPresent()) {
            throw new DataGovernanceException(23000, "需要创建 " + GlossaryConstant.LABEL.getRoot() + " glossary ");
        }
        return atlasEntityHeaders.stream()
                .filter(atlasEntityHeader ->
                        StringUtils.endsWith(atlasEntityHeader.getAttribute("qualifiedName").toString(), GlossaryConstant.LABEL.endWith()))
                .map(SearchLabelDTO::from).collect(Collectors.toList());
    }

    public List<PartInfo> getHiveTblPartitionByName(String dbName,String tableName) throws Exception {
        return partInfoMapper.query(dbName,tableName);

    }


    private boolean isLabel(String termGuid) {

        AtlasGlossaryTerm term;
        try {
            term = atlasService.getGlossaryTermDetail(termGuid);
        }catch (AtlasServiceException e){
            logger.error(e.getMessage(),e);
            return false;
        }

        if (term == null){
            return false;
        }
        return term.getQualifiedName().endsWith(GlossaryConstant.LABEL.endWith());
    }
}
