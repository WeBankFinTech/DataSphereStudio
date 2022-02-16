package com.webank.wedatasphere.dss.data.common.atlas;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.apache.atlas.AtlasClientV2;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.SortOrder;
import org.apache.atlas.model.instance.AtlasClassification;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.atlas.type.AtlasType;
import org.apache.commons.collections.MapUtils;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AtlasClient extends AtlasClientV2 {
    private static final String PREFIX_ATTR          = "attr:";

    private static final String ENTITIES_API   = BASE_URI + "entities/";
    private static final String UPDATE_ENTITY_ATTR_API = ENTITY_API + "guid/";
    private static final String SET_ENTITY_LABELS_BY_GUID_TEMPLATE = ENTITY_API + "/guid/%s/labels";
    private static final String DELETE_ENTITY_LABELS_BY_GUID_TEMPLATE = ENTITY_API + "/guid/%s/labels";
    private static final String GET_ENTITY_HEADER_BY_GUID_TEMPLATE = ENTITY_API + "/guid/{guid}/header";

    private static final String TYPEDEF_BY_NAME      = TYPES_API + "typedef/name/";
    private static final String TYPEDEFS_API         = TYPES_API + "typedefs/";
    private static final String GET_CLASSIFICATION_BY_NAME_TEMPLATE = TYPES_API + "classificationdef/name/%s";

    public AtlasClient(String[] baseUrl, String[] basicAuthUserNamePassword) {
        super(baseUrl, basicAuthUserNamePassword);
    }

    /**
     * 获取所有的hive db实体
     */
    public String getHiveDbs() throws AtlasServiceException {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("type", "hive_db");
        queryParams.add("property", AtlasEntity.KEY_STATUS);
        queryParams.add("value", AtlasEntity.Status.ACTIVE.name());
        return callAPI(API_V3.GET_ENTITIES, String.class, queryParams);
    }

    /**
     * 获取所有的hive table实体
     */
    public String getHiveTables() throws AtlasServiceException {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("type", "hive_table");
        queryParams.add("property", AtlasEntity.KEY_STATUS);
        queryParams.add("value", AtlasEntity.Status.ACTIVE.name());
        return callAPI(API_V3.GET_ENTITIES, String.class, queryParams);
    }

    /**
     * 获取表实体的血缘信息
     */
    public String getLineageInfoForString(final String guid, final AtlasLineageInfo.LineageDirection direction, final int depth) throws AtlasServiceException {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("direction", direction.toString());
        queryParams.add("depth", String.valueOf(depth));

        return callAPI(API_V2.LINEAGE_INFO, String.class, queryParams, guid);
    }

    /**
     * 获取实体信息
     */
    public String getEntityByGuidForString(String guid) throws AtlasServiceException {
        return getEntityByGuidForString(guid, false, false);
    }

    /**
     * 获取实体信息
     */
    public String getEntityByGuidForString(String guid, boolean minExtInfo, boolean ignoreRelationships) throws AtlasServiceException {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();

        queryParams.add("minExtInfo", String.valueOf(minExtInfo));
        queryParams.add("ignoreRelationships", String.valueOf(ignoreRelationships));

        return callAPI(API_V2.GET_ENTITY_BY_GUID, String.class, queryParams, guid);
    }

    /**
     * 通过type 和 属性查一个entity
     */
    public String getEntityByAttributeForString(String type, Map<String, String> uniqAttributes, boolean minExtInfo, boolean ignoreRelationship) throws AtlasServiceException {
        MultivaluedMap<String, String> queryParams = attributesToQueryParams(uniqAttributes);

        queryParams.add("minExtInfo", String.valueOf(minExtInfo));
        queryParams.add("ignoreRelationships", String.valueOf(ignoreRelationship));

        return callAPI(API_V2.GET_ENTITY_BY_ATTRIBUTE, String.class, queryParams, type);
    }


    public String getEntitiesByGuidsForString(List<String> guids) throws AtlasServiceException {
        return getEntitiesByGuidsForString(guids, false, false);
    }

    public String getEntitiesByGuidsForString(List<String> guids, boolean minExtInfo, boolean ignoreRelationships) throws AtlasServiceException {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();

        queryParams.put("guid", guids);
        queryParams.add("minExtInfo", String.valueOf(minExtInfo));
        queryParams.add("ignoreRelationships", String.valueOf(ignoreRelationships));

        return callAPI(API_V2.GET_ENTITIES_BY_GUIDS, String.class, queryParams);
    }

    /**
     * 获取实体基本信息
     */
    public String getHeaderByIdForString(String guid) throws AtlasServiceException {
        API    api       = new API(String.format(GET_ENTITY_HEADER_BY_GUID_TEMPLATE, guid), HttpMethod.GET, Response.Status.OK);
        return callAPI(api, String.class,null);
    }

    /**
     * 根据关键字检索实体
     */
    public String basicSearchForString(final String typeName, final String classification, final String query,
                                         final boolean excludeDeletedEntities, final int limit, final int offset) throws AtlasServiceException {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("typeName", typeName);
        queryParams.add("classification", classification);
        queryParams.add(QUERY, query);
        queryParams.add("excludeDeletedEntities", String.valueOf(excludeDeletedEntities));
        queryParams.add(LIMIT, String.valueOf(limit));
        queryParams.add(OFFSET, String.valueOf(offset));
        queryParams.add("sortBy", "name");
        queryParams.add("sortOrder", String.valueOf(SortOrder.ASCENDING));

        return callAPI(API_V2.BASIC_SEARCH, String.class, queryParams);
    }

    /**
     * 修改实体的注释
     */
    public String modifyComment(String guid,String commentStr) throws AtlasServiceException {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("name", "comment");

        return callAPI(API_V3.UPDATE_ENTITY_ATTR,String.class,commentStr,queryParams,guid);
    }

    /**
     * 设置实体的标签
     */
    public void setLabels(String guid, Set<String> labels) throws AtlasServiceException {
        API    api       = new API(String.format(SET_ENTITY_LABELS_BY_GUID_TEMPLATE, guid), HttpMethod.POST, Response.Status.NO_CONTENT);
        callAPI(api,  (Class<?>)null, labels);
    }

    /**
     * 删除实体的标签
     */
    public void removeLabels(String guid, Set<String> labels) throws AtlasServiceException {
        API    api       = new API(String.format(DELETE_ENTITY_LABELS_BY_GUID_TEMPLATE, guid), HttpMethod.DELETE, Response.Status.NO_CONTENT);
        callAPI(api,  (Class<?>)null, labels);
    }

    /**
     * 获取所有的类别
     */
    public String getClassificationDefForString() throws AtlasServiceException {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("type", "classification");
        return callAPI(API_V2.GET_ALL_TYPE_DEFS, String.class, queryParams);
    }

    /**
     * 根据名称获取分类
     */
    public String getClassificationDefByNameForString(String name) throws AtlasServiceException {
        API    api       = new API(String.format(GET_CLASSIFICATION_BY_NAME_TEMPLATE, name), HttpMethod.GET, Response.Status.OK);
        return callAPI(api, String.class, null);
    }

    /**
     * 根据名称删除类型
     */
    public void deleteTypedefByName(String name) throws AtlasServiceException {
        callAPI(API_V3.DELETE_TYPEDEF_BY_NAME, (Class<?>)null, null, name);
    }

    /**
     * 创建类型
     */
    public String createAtlasTypeDefsForString(final AtlasTypesDef typesDef) throws AtlasServiceException {
        return callAPI(API_V2.CREATE_ALL_TYPE_DEFS, String.class, AtlasType.toJson(typesDef));
    }

    /**
     * 更新类型
     */
    public void updateAtlasTypeDefsV2(final AtlasTypesDef typesDef) throws AtlasServiceException {
        callAPI(API_V2.UPDATE_ALL_TYPE_DEFS, (Class<?>)null, AtlasType.toJson(typesDef));
    }

    /**
     * 为实体添加分类
     */
    @Override
    public void addClassifications(String guid, List<AtlasClassification> classifications) throws AtlasServiceException {
        callAPI(formatPathParameters(API_V2.ADD_CLASSIFICATIONS, guid), (Class<?>)null, AtlasType.toJson(classifications), (String[]) null);
    }

    /**
     * 为实体更新分类
     */
    @Override
    public void updateClassifications(String guid, List<AtlasClassification> classifications) throws AtlasServiceException {
        callAPI(formatPathParameters(API_V2.UPDATE_CLASSIFICATIONS, guid), (Class<?>)null, AtlasType.toJson(classifications));
    }

    /**
     * 为实体删除分类
     */
    @Override
    public void deleteClassifications(String guid, List<AtlasClassification> classifications) throws AtlasServiceException {
        if(classifications !=null && classifications.size() > 0) {
            for (AtlasClassification c : classifications) {
                this.deleteClassification(guid, c.getTypeName());
            }
        }
    }

    /**
     * 查询实体的分类
     */
    public String getClassificationsForString(String guid) throws AtlasServiceException {
        return callAPI(formatPathParameters(API_V2.GET_CLASSIFICATIONS, guid), String.class, null);
    }




    public static class API_V3 extends API {
        public static final API_V3 GET_ENTITIES = new API_V3(ENTITIES_API, HttpMethod.GET, Response.Status.OK);
        public static final API_V3 UPDATE_ENTITY_ATTR  = new API_V3(UPDATE_ENTITY_ATTR_API,HttpMethod.PUT,Response.Status.OK);
        public static final API_V3 DELETE_TYPEDEF_BY_NAME         = new API_V3(TYPEDEF_BY_NAME, HttpMethod.DELETE, Response.Status.NO_CONTENT);

        private API_V3(String path, String method, Response.Status status) {
                super(path, method, status);
        }
    }


    private MultivaluedMap<String, String> attributesToQueryParams(Map<String, String> attributes) {
        return attributesToQueryParams(attributes, null);
    }

    private MultivaluedMap<String, String> attributesToQueryParams(Map<String, String>            attributes,
                                                                   MultivaluedMap<String, String> queryParams) {
        if (queryParams == null) {
            queryParams = new MultivaluedMapImpl();
        }

        if (MapUtils.isNotEmpty(attributes)) {
            for (Map.Entry<String, String> e : attributes.entrySet()) {
                queryParams.putSingle(PREFIX_ATTR + e.getKey(), e.getValue());
            }
        }

        return queryParams;
    }
}
