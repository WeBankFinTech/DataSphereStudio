package com.webank.wedatasphere.dss.data.governance.atlas;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.webank.wedatasphere.dss.data.governance.conf.GovernanceConf;
import org.apache.atlas.ApplicationProperties;
import org.apache.atlas.AtlasClientV2;
import org.apache.atlas.AtlasException;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.discovery.AtlasSearchResult;
import org.apache.atlas.model.discovery.SearchParameters;
import org.apache.atlas.model.instance.AtlasClassification;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasEntityHeader;
import org.apache.atlas.model.instance.ClassificationAssociateRequest;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.apache.atlas.model.typedef.AtlasClassificationDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.commons.configuration.Configuration;
import org.springframework.stereotype.Service;

import javax.ws.rs.core.MultivaluedMap;
import java.lang.reflect.Type;
import java.util.*;


@Service("atlasService")
public class AtlasService {
    private final AtlasClient atlasClient;
    private final Gson gson;

    public AtlasService() throws AtlasException {
        Configuration configuration = ApplicationProperties.get("linkis.properties");
        String[] urls =new String[]{GovernanceConf.ATLAS_REST_ADDRESS.getValue()};
        String[] basicAuthUsernamePassword =new String[]{ GovernanceConf.ATLAS_USERNAME.getValue(),GovernanceConf.ATLAS_PASSWORD.getValue()};

        atlasClient = new AtlasClient(configuration,urls,basicAuthUsernamePassword);



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
    public int getHiveDbCnt() throws AtlasServiceException {
        String jsonStr = atlasClient.getHiveDbs();
        if(jsonStr != null && jsonStr.trim() !=""){
            JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);
            if(jsonObject !=null){
                return jsonObject.get("count").getAsInt();
            }
            else{
                return 0;
            }
        }
        else {
            return 0;
        }
    }

    /**
     * hive table数量
     */
    public int getHiveTableCnt() throws AtlasServiceException {
        String jsonStr = atlasClient.getHiveTables();
        if(jsonStr != null && jsonStr.trim() !=""){
            JsonObject jsonObject = gson.fromJson(jsonStr, JsonObject.class);
            if(jsonObject !=null){
                return jsonObject.get("count").getAsInt();
            }
            else{
                return 0;
            }
        }
        else {
            return 0;
        }
    }


    /**
     * 根据关键字搜索hive table
     */
    public List<AtlasEntityHeader> searchHiveTable(String classification, String query,
                                                   boolean excludeDeletedEntities, int limit, int offset) throws AtlasServiceException {
        String jsonStr =atlasClient.basicSearchForString("hive_table",classification,query,excludeDeletedEntities,limit,offset);
        AtlasSearchResult atlasSearchResult = gson.fromJson(jsonStr, AtlasSearchResult.class);

        return atlasSearchResult.getEntities();
    }

    /**
     * 根据关键字搜索hive table
     */
    public List<AtlasEntityHeader> searchHiveTable0(String classification, String query,
                                                   boolean excludeDeletedEntities, int limit, int offset) throws AtlasServiceException {

        String jsonStr = atlasClient.basicSearchPostForString("hive_table",classification,query,excludeDeletedEntities,limit,offset);
        AtlasSearchResult atlasSearchResult = gson.fromJson(jsonStr,AtlasSearchResult.class);//atlasClient.facetedSearch(searchParameters);

        //实体绑定类型
        //atlasClient.addClassification();
        //创建子类型
        //atlasClient.createAtlasTypeDefs()
        return atlasSearchResult.getEntities();
    }

    /**
     * 创建子类型
     * @param name
     * @param superType
     * @return
     * @throws AtlasServiceException
     */
    public AtlasClassificationDef createSubClassification(String name, String superType) throws AtlasServiceException {
        String jsonStr = atlasClient.createSubClassification(name, superType);
        AtlasTypesDef atlasTypesDef = gson.fromJson(jsonStr,AtlasTypesDef.class);
        return atlasTypesDef.getClassificationDefs().get(0);
    }

    /**
     * 删除指定模型类型
     * @param name
     * @throws AtlasServiceException
     */
    public void  deleteClassification(String name) throws AtlasServiceException{
        atlasClient.deleteTypeByName(name);
    }


    /**
     * 绑定类型
     * @param typeName
     * @param guid
     * @throws AtlasServiceException
     */
    public void addClassification(String typeName,String guid,boolean propagate) throws AtlasServiceException {
        AtlasClassification atlasClassification = new AtlasClassification();
        atlasClassification.setTypeName(typeName);
        atlasClassification.setPropagate(propagate);
        atlasClassification.setRemovePropagationsOnEntityDelete(false);

        ClassificationAssociateRequest request = new ClassificationAssociateRequest(Lists.newArrayList(guid),atlasClassification);
        atlasClient.callAPI(AtlasClientV2.API_V2.ADD_CLASSIFICATION, (Class<?>) null, gson.toJson(request),new MultivaluedMapImpl());
    }

    /**
     * 解绑类型
     * @param guid
     * @param typeName
     * @throws AtlasServiceException
     */
    public void deleteClassification(String guid, String typeName)  throws AtlasServiceException{
        atlasClient.deleteClassification(guid,typeName);
    }
    /**
     * 获取hive table对象
     */
    public AtlasEntity getHiveTbl(String guid) throws AtlasServiceException {
        String jsonStr =atlasClient.getEntityByGuidForString(guid,false,false);

        AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo = gson.fromJson(jsonStr, AtlasEntity.AtlasEntityWithExtInfo.class);

        return atlasEntityWithExtInfo.getEntity();
    }

    /**
     * 获取hive column对象
     */
    public AtlasEntity getHiveColumn(String guid) throws AtlasServiceException {
        String jsonStr =atlasClient.getEntityByGuidForString(guid,true,true);

        AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo = gson.fromJson(jsonStr, AtlasEntity.AtlasEntityWithExtInfo.class);

        return atlasEntityWithExtInfo.getEntity();
    }

    /**
     * 获取多个hive column对象
     */
    public List<AtlasEntity> getHiveColumnsByGuids(List<String> guids) throws AtlasServiceException {
        String jsonStr =atlasClient.getEntitiesByGuidsForString(guids,true,true);
        AtlasEntity.AtlasEntitiesWithExtInfo atlasEntitiesWithExtInfo = gson.fromJson(jsonStr, AtlasEntity.AtlasEntitiesWithExtInfo.class);

        return atlasEntitiesWithExtInfo.getEntities();
    }


    /**
     * 修改实体的注释
     */
    public void modifyComment(String guid,String commentStr) throws AtlasServiceException {
        atlasClient.modifyComment(guid, commentStr);
    }

    /**
     * 设置实体的标签
     */
    public void setLabels(String guid, Set<String> labels) throws AtlasServiceException {
        atlasClient.setLabels(guid, labels);
    }

    /**
     * 血缘信息
     */
    public AtlasLineageInfo getLineageInfo(final String guid, final AtlasLineageInfo.LineageDirection direction, final int depth) throws AtlasServiceException {
        String jsonStr = atlasClient.getLineageInfoForString(guid, direction, depth);

        AtlasLineageInfo atlasLineageInfo = gson.fromJson(jsonStr,AtlasLineageInfo.class);

        return atlasLineageInfo;
    }

    /**
     * 根据guid来获取hive tbl名称： db.table
     */
    public String getHiveTblNameById(String guid) throws AtlasServiceException {
        String jsonStr = atlasClient.getHeaderByIdForString(guid);
        AtlasEntityHeader atlasEntityHeader = gson.fromJson(jsonStr,AtlasEntityHeader.class);

        return atlasEntityHeader.getAttribute("qualifiedName").toString().split("@")[0];
    }

    /**
     * 根据guid来获取hive tbl名称 和 是否分区表
     */
    public Map<String,Object> getHiveTblNameAndIsPartById(String guid) throws AtlasServiceException {
        Map<String,Object> result =new HashMap<>(2);

        String jsonStr = atlasClient.getEntityByGuidForString(guid,true,false);
        AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo = gson.fromJson(jsonStr, AtlasEntity.AtlasEntityWithExtInfo.class);

        result.put("tblName",atlasEntityWithExtInfo.getEntity().getAttribute("qualifiedName").toString().split("@")[0]);
        result.put("isPartition",((List)atlasEntityWithExtInfo.getEntity().getAttribute("partitionKeys")).size() >0);

        return result;
    }
    /**
     * 获取hive db对象
     */
    public List<AtlasEntityHeader> searchHiveDb(String classification, String query,
                                                boolean excludeDeletedEntities, int limit, int offset) throws AtlasServiceException{
        String jsonStr =atlasClient.basicSearchPostForString("hive_db",classification,query,excludeDeletedEntities,limit,offset);
        AtlasSearchResult atlasSearchResult = gson.fromJson(jsonStr, AtlasSearchResult.class);

        return atlasSearchResult.getEntities();
    }
}
