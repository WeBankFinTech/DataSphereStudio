package com.webank.wedatasphere.dss.data.governance.atlas;

import com.google.common.collect.Lists;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.webank.wedatasphere.dss.data.governance.conf.GovernanceConf;
import com.webank.wedatasphere.dss.data.governance.entity.GlossaryConstant;
import com.webank.wedatasphere.dss.data.governance.entity.RelatedObjectId;
import com.webank.wedatasphere.dss.data.governance.exception.DataGovernanceException;
import org.apache.atlas.ApplicationProperties;
import org.apache.atlas.AtlasClientV2;
import org.apache.atlas.AtlasException;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.AtlasBaseModelObject;
import org.apache.atlas.model.discovery.AtlasSearchResult;
import org.apache.atlas.model.glossary.AtlasGlossary;
import org.apache.atlas.model.glossary.AtlasGlossaryTerm;
import org.apache.atlas.model.instance.*;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.apache.atlas.model.typedef.AtlasClassificationDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;


@Service("atlasService")
public class AtlasService {
    private final AtlasClient atlasClient;
    private final Gson gson;

    public AtlasService() throws AtlasException {
        Configuration configuration = ApplicationProperties.get("linkis.properties");
        String[] urls = new String[]{GovernanceConf.ATLAS_REST_ADDRESS.getValue()};
        String[] basicAuthUsernamePassword = new String[]{GovernanceConf.ATLAS_USERNAME.getValue(), GovernanceConf.ATLAS_PASSWORD.getValue()};

        atlasClient = new AtlasClient(configuration, urls, basicAuthUsernamePassword);


        GsonBuilder builder = new GsonBuilder();
        // Register an adapter to manage the date types as long values
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });

        gson = builder.create();
    }

    /**
     * hive db数量
     */
    public Long getHiveDbCnt() throws AtlasServiceException {
        String jsonStr = atlasClient.getHiveDbs();
        if (jsonStr != null && jsonStr.trim() != "") {
            JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);
            if (jsonObject != null) {
                return jsonObject.get("count").getAsLong();
            } else {
                return 0l;
            }
        } else {
            return 0l;
        }
    }

    /**
     * hive table数量
     */
    public long getHiveTableCnt() throws AtlasServiceException {
        String jsonStr = atlasClient.getHiveTables();
        if (jsonStr != null && jsonStr.trim() != "") {
            JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);
            if (jsonObject != null) {
                return jsonObject.get("count").getAsLong();
            } else {
                return 0l;
            }
        } else {
            return 0l;
        }
    }


    /**
     * 根据关键字搜索hive table
     */
    public List<AtlasEntityHeader> searchHiveTable(String classification, String query,
                                                   boolean excludeDeletedEntities, int limit, int offset) throws AtlasServiceException {
        String jsonStr = atlasClient.basicSearchForString("hive_table", classification, query, excludeDeletedEntities, limit, offset);
        AtlasSearchResult atlasSearchResult = gson.fromJson(jsonStr, AtlasSearchResult.class);

        return atlasSearchResult.getEntities();
    }

    /**
     * 根据关键字搜索hive table
     */
    public List<AtlasEntityHeader> searchHiveTable0(String classification, String query, String termName,
                                                    boolean excludeDeletedEntities, int limit, int offset) throws AtlasServiceException {

        String jsonStr = atlasClient.basicSearchPostForString("hive_table", classification, query, termName , excludeDeletedEntities, limit, offset);
        AtlasSearchResult atlasSearchResult = gson.fromJson(jsonStr, AtlasSearchResult.class);//atlasClient.facetedSearch(searchParameters);

        //实体绑定类型
        //atlasClient.addClassification();
        //创建子类型
        //atlasClient.createAtlasTypeDefs()
        return atlasSearchResult.getEntities();
    }
    /**
     * 创建子类型
     *
     * @param name
     * @param superType
     * @return
     * @throws AtlasServiceException
     */
    public AtlasClassificationDef createSubClassification(String name, String superType) throws AtlasServiceException {
        String jsonStr = atlasClient.createSubClassification(name, superType);
        AtlasTypesDef atlasTypesDef = gson.fromJson(jsonStr, AtlasTypesDef.class);
        return atlasTypesDef.getClassificationDefs().get(0);
    }

    /**
     * 删除指定模型类型
     *
     * @param name
     * @throws AtlasServiceException
     */
    public void deleteClassification(String name) throws AtlasServiceException {
        atlasClient.deleteTypeByName(name);
    }


    /**
     * 绑定类型
     *
     * @param typeName
     * @param guid
     * @throws AtlasServiceException
     */
    public void addClassification(String typeName, String guid, boolean propagate) throws AtlasServiceException {
        AtlasClassification atlasClassification = new AtlasClassification();
        atlasClassification.setTypeName(typeName);
        atlasClassification.setPropagate(propagate);
        atlasClassification.setRemovePropagationsOnEntityDelete(false);

        ClassificationAssociateRequest request = new ClassificationAssociateRequest(Lists.newArrayList(guid), atlasClassification);
        atlasClient.callAPI(AtlasClientV2.API_V2.ADD_CLASSIFICATION, (Class<?>) null, gson.toJson(request), new MultivaluedMapImpl());
    }

    /**
     * 解绑类型
     *
     * @param guid
     * @param typeName
     * @throws AtlasServiceException
     */
    public void deleteClassification(String guid, String typeName) throws AtlasServiceException {
        atlasClient.deleteClassification(guid, typeName);
    }

    /**
     * 获取hive table对象
     */
    public AtlasEntity getHiveTbl(String guid) throws AtlasServiceException {
        String jsonStr = atlasClient.getEntityByGuidForString(guid, false, false);

        AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo = gson.fromJson(jsonStr, AtlasEntity.AtlasEntityWithExtInfo.class);

        return atlasEntityWithExtInfo.getEntity();
    }

    /**
     * 获取hive column对象
     */
    public AtlasEntity getHiveColumn(String guid) throws AtlasServiceException {
        String jsonStr = atlasClient.getEntityByGuidForString(guid, true, true);

        AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo = gson.fromJson(jsonStr, AtlasEntity.AtlasEntityWithExtInfo.class);

        return atlasEntityWithExtInfo.getEntity();
    }

    /**
     * 获取多个hive column对象
     */
    public List<AtlasEntity> getHiveColumnsByGuids(List<String> guids) throws AtlasServiceException {
        String jsonStr = atlasClient.getEntitiesByGuidsForString(guids, true, true);
        AtlasEntity.AtlasEntitiesWithExtInfo atlasEntitiesWithExtInfo = gson.fromJson(jsonStr, AtlasEntity.AtlasEntitiesWithExtInfo.class);

        return atlasEntitiesWithExtInfo.getEntities();
    }


    /**
     * 修改实体的注释
     */
    public void modifyComment(String guid, String commentStr) throws AtlasServiceException {
        atlasClient.modifyComment(guid, commentStr);
    }

    /**
     * 设置实体的标签
     */
    public void setLabels(String guid, Set<String> labels) throws AtlasServiceException {
        atlasClient.setLabels(guid, labels);
    }

    /**
     * 获取根glossary guid
     *
     * @return
     * @throws AtlasServiceException
     */
    public Optional<String> getRootGlossaryGuid(GlossaryConstant glossaryConstant) throws AtlasServiceException {
        String result = atlasClient.getAllGlossaries();
        List<AtlasGlossary> glossaries = gson.fromJson(result, new TypeToken<List<AtlasGlossary>>() {
        }.getType());
        Optional<AtlasGlossary> root = glossaries.stream().filter(atlasGlossary -> StringUtils.equals(atlasGlossary.getName(), glossaryConstant.getRoot())).findFirst();
        return root.map(AtlasBaseModelObject::getGuid);
    }

    /**
     * 新建标签
     *
     * @param labelName
     * @return
     * @throws AtlasServiceException
     */
    public AtlasGlossaryTerm createLabel(String labelName) throws AtlasServiceException {
        String result = atlasClient.createGlossaryTerm(labelName, getRootGlossaryGuid(GlossaryConstant.LABEL).get());
        return gson.fromJson(result, AtlasGlossaryTerm.class);
    }

    /**
     * 绑定标签
     * @param termGuid
     * @param entityGuids
     * @throws AtlasServiceException
     */
    public void assignTermToEntities(String termGuid,List<String> entityGuids) throws AtlasServiceException{
        atlasClient.assignTermToEntities0(termGuid,entityGuids);
    }

    /**
     * 解绑实体
     * @param termGuid
     * @param
     * @throws AtlasServiceException
     */
    public void disassociateTermFromEntities(String termGuid, List<RelatedObjectId> relatedObjectIds) throws AtlasServiceException {
        atlasClient.disassociateTermFromEntities0(termGuid,relatedObjectIds.stream().map(
                relatedObjectId -> {
                    AtlasRelatedObjectId atlasRelatedObjectId = new AtlasRelatedObjectId();
                    atlasRelatedObjectId.setGuid(relatedObjectId.getGuid());
                    atlasRelatedObjectId.setRelationshipGuid(relatedObjectId.getRelationshipGuid());
                    return atlasRelatedObjectId;
                }
        ).collect(Collectors.toList()));
    }



    /**
     * 删除标签
     * @param labelName
     * @throws AtlasServiceException
     */
    public void deleteLabel(String labelName) throws Exception{
        Optional<String> optional = getTermGuid(GlossaryConstant.LABEL,labelName);
        if (!optional.isPresent()){
            throw new DataGovernanceException(23000, labelName + "标签不存在");
        }
        atlasClient.deleteGlossaryTermByGuid(optional.get());
    }

    /**
     * 获取分词guid
     * @param glossaryConstant
     * @param name
     * @return
     * @throws AtlasServiceException
     */
    public Optional<String> getTermGuid(GlossaryConstant glossaryConstant, String name) throws AtlasServiceException {
        String result = atlasClient.attributeSearchByName(glossaryConstant, name);
        AtlasSearchResult atlasSearchResult = gson.fromJson(result, AtlasSearchResult.class);
        return !CollectionUtils.isEmpty(atlasSearchResult.getEntities()) ?
                Optional.ofNullable(atlasSearchResult.getEntities().get(0).getGuid()) : Optional.empty();
    }

    public List<AtlasEntityHeader> listLabels(String query,Integer limit,Integer offset) throws AtlasServiceException{
        String result = atlasClient.attributeSearch0(GlossaryConstant.LABEL.getAtlasType(),query,GlossaryConstant.ARR,limit,offset);
        AtlasSearchResult atlasSearchResult = gson.fromJson(result, AtlasSearchResult.class);
        return !CollectionUtils.isEmpty(atlasSearchResult.getEntities())?atlasSearchResult.getEntities():Lists.newArrayList();
    }


    /**
     * 血缘信息
     */
    public AtlasLineageInfo getLineageInfo(final String guid, final AtlasLineageInfo.LineageDirection direction, final int depth) throws AtlasServiceException {
        String jsonStr = atlasClient.getLineageInfoForString(guid, direction, depth);
        AtlasLineageInfo atlasLineageInfo = gson.fromJson(jsonStr, AtlasLineageInfo.class);
        return atlasLineageInfo;
    }

    /**
     * 根据guid来获取hive tbl名称： db.table
     */
    public String getHiveTblNameById(String guid) throws AtlasServiceException {
        String jsonStr = atlasClient.getHeaderByIdForString(guid);
        AtlasEntityHeader atlasEntityHeader = gson.fromJson(jsonStr, AtlasEntityHeader.class);

        return atlasEntityHeader.getAttribute("qualifiedName").toString().split("@")[0];
    }

    /**
     * 根据guid来获取hive tbl名称 和 是否分区表
     */
    public Map<String, Object> getHiveTblNameAndIsPartById(String guid) throws AtlasServiceException {
        Map<String, Object> result = new HashMap<>(2);

        String jsonStr = atlasClient.getEntityByGuidForString(guid, true, false);
        AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo = gson.fromJson(jsonStr, AtlasEntity.AtlasEntityWithExtInfo.class);

        result.put("tblName", atlasEntityWithExtInfo.getEntity().getAttribute("qualifiedName").toString().split("@")[0]);
        result.put("isPartition", ((List) atlasEntityWithExtInfo.getEntity().getAttribute("partitionKeys")).size() > 0);

        return result;
    }

    /**
     * 获取hive db对象
     */
    public List<AtlasEntityHeader> searchHiveDb(String classification, String query,
                                                boolean excludeDeletedEntities, int limit, int offset) throws AtlasServiceException {
        String jsonStr = atlasClient.basicSearchPostForString("hive_db", classification, query,null, excludeDeletedEntities, limit, offset);
        AtlasSearchResult atlasSearchResult = gson.fromJson(jsonStr, AtlasSearchResult.class);

        return atlasSearchResult.getEntities();
    }

    public AtlasGlossaryTerm getGlossaryTermDetail(String termGuid) throws AtlasServiceException {
        String detail =  atlasClient.getGlossaryTerm0(termGuid);
        return gson.fromJson(detail,AtlasGlossaryTerm.class);
    }
}
