package com.webank.wedatasphere.dss.data.governance.atlas;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import com.webank.wedatasphere.dss.data.governance.entity.GlossaryConstant;
import org.apache.atlas.AtlasClientV2;
import org.apache.atlas.AtlasServiceException;
import org.apache.atlas.model.discovery.SearchParameters;
import org.apache.atlas.model.glossary.AtlasGlossaryTerm;
import org.apache.atlas.model.glossary.relations.AtlasGlossaryHeader;
import org.apache.atlas.model.instance.AtlasRelatedObjectId;
import org.apache.atlas.model.lineage.AtlasLineageInfo;
import org.apache.atlas.model.typedef.AtlasClassificationDef;
import org.apache.atlas.model.typedef.AtlasTypesDef;
import org.apache.commons.configuration.Configuration;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class AtlasClient extends AtlasClientV2 {
    // Entities APIs
    public static final String ENTITIES_API   = BASE_URI + "entities/";
    public static final String UPDATE_ENTITY_ATTR_API = ENTITY_API + "guid/";
    public static final String SET_ENTITY_LABELS_BY_GUID_TEMPLATE = ENTITY_API + "/guid/%s/labels";
    public static final String GET_ENTITY_HEADER_BY_GUID_TEMPLATE = ENTITY_API + "/guid/{guid}/header";
    private final Gson gson = new Gson();

    public AtlasClient(Configuration configuration,String[] baseUrl, String[] basicAuthUserNamePassword) {
        super(configuration,baseUrl, basicAuthUserNamePassword);
    }

    /**
     * 获取所有的hive db实体
     */
    public String getHiveDbs() throws AtlasServiceException {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("type", "hive_db");
        return callAPI(API_V3.GET_ENTITIES, String.class, queryParams);
    }

    /**
     * 获取所有的hive table实体
     */
    public String getHiveTables() throws AtlasServiceException {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();
        queryParams.add("type", "hive_table");
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

        return callAPI(API_V2.BASIC_SEARCH, String.class, queryParams);
    }


    /**
     * 创建子类型
     * @param name
     * @param superType
     * @return
     * @throws AtlasServiceException
     */
    public String createSubClassification(String name, String superType) throws AtlasServiceException {
        AtlasClassificationDef atlasClassificationDef = new AtlasClassificationDef();
        atlasClassificationDef.setName(name);
        atlasClassificationDef.setSuperTypes(Sets.newHashSet(superType));
        AtlasTypesDef atlasTypesDef = new AtlasTypesDef();
        atlasTypesDef.setClassificationDefs(Lists.newArrayList(atlasClassificationDef));
        MultivaluedMap<String, String> params =  new MultivaluedMapImpl();
        params.add("type","classification");
        return  callAPI(API_V2.CREATE_TYPE_DEFS, String.class, gson.toJson(atlasTypesDef),params);
    }

    // Glossary APIs
    public String getAllGlossaries() throws AtlasServiceException {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();

        queryParams.add(LIMIT, "99999");
        queryParams.add(OFFSET, "0");

        return callAPI(API_V2.GET_ALL_GLOSSARIES, String.class, queryParams);
    }

    /**
     * 新建分词
     * @param name
     * @param rootGuid
     * @return
     * @throws AtlasServiceException
     */
    public String createGlossaryTerm(String name,String rootGuid) throws AtlasServiceException {
        AtlasGlossaryTerm glossaryTerm = new AtlasGlossaryTerm();
        glossaryTerm.setName(name);
        AtlasGlossaryHeader anchor = new AtlasGlossaryHeader();
        anchor.setGlossaryGuid(rootGuid);
        glossaryTerm.setAnchor(anchor);
        return callAPI(API_V2.CREATE_GLOSSARY_TERM, String.class, gson.toJson(glossaryTerm));
    }

    /**
     * 根据名称 查询分词详情
     * @param glossaryConstant
     * @param name
     * @return
     * @throws AtlasServiceException
     */
    public String attributeSearchByName(GlossaryConstant glossaryConstant, String name) throws AtlasServiceException {
        return attributeSearch0(glossaryConstant.getAtlasType(),glossaryConstant.formatQuery(name), GlossaryConstant.ARR,1,0);
    }

    public String attributeSearch0(String typeName, String name, String attrName, Integer limit, Integer offset) throws AtlasServiceException {
        MultivaluedMap<String, String> queryParams = new MultivaluedMapImpl();

        queryParams.add("attrName", attrName);
        queryParams.add("attrValuePrefix",name);
        queryParams.add("typeName", typeName);
        queryParams.add(LIMIT, limit+"");
        queryParams.add(OFFSET, offset+"");

        return callAPI(API_V2.ATTRIBUTE_SEARCH, String.class, queryParams);
    }


    /**
     * 实体绑定分词
     * @param termGuid
     * @param entityGuids
     * @throws AtlasServiceException
     */
    public void assignTermToEntities0(String termGuid, List<String> entityGuids) throws AtlasServiceException {
        List<AtlasRelatedObjectId> relatedObjectIds = entityGuids.stream().map(entityGuid->{AtlasRelatedObjectId atlasRelatedObjectId = new AtlasRelatedObjectId();
        atlasRelatedObjectId.setGuid(entityGuid);
        return atlasRelatedObjectId;
        }).collect(Collectors.toList());
        callAPI(formatPathParameters(API_V2.ASSIGN_TERM_TO_ENTITIES, termGuid), (Class<?>) null, gson.toJson(relatedObjectIds));
    }

    /**
     * 解绑实体
     * @param termGuid
     * @param
     * @throws AtlasServiceException
     */
    public void disassociateTermFromEntities0(String termGuid, List<AtlasRelatedObjectId> relatedObjectIds) throws AtlasServiceException {
        callAPI(formatPathParameters(API_V2.DISASSOCIATE_TERM_FROM_ENTITIES, termGuid), (Class<?>) null, gson.toJson(relatedObjectIds));
    }

    /**
     * 根据关键字检索实体
     */
    public String basicSearchPostForString(final String typeName, final String classification, final String query,final String termName,
                                       final boolean excludeDeletedEntities, final int limit, final int offset) throws AtlasServiceException {
        SearchParameters searchParameters = new SearchParameters();
        Set<String> returnColumnsParams = Sets.newHashSet("aliases", "parameters", "lastAccessTime","comment");
        searchParameters.setTypeName(typeName);
        searchParameters.setClassification(classification);
        searchParameters.setQuery(query);
        searchParameters.setLimit(limit);
        searchParameters.setTermName(termName);
        searchParameters.setOffset(offset);
        searchParameters.setIncludeSubClassifications(true);
        searchParameters.setIncludeSubTypes(true);
        searchParameters.setIncludeClassificationAttributes(true);
        searchParameters.setExcludeDeletedEntities(excludeDeletedEntities);
        searchParameters.setAttributes(returnColumnsParams);
       return  callAPI(API_V2.FACETED_SEARCH, String.class, gson.toJson(searchParameters),new MultivaluedMapImpl());
    }

    /**
     * 根据关键字检索实体
     */
    public String basicSearchPostForLabel(final String typeName, final String termName,final boolean excludeDeletedEntities) throws AtlasServiceException {
        SearchParameters searchParameters = new SearchParameters();
        Set<String> returnColumnsParams = Sets.newHashSet( "parameters", "lastAccessTime");
        searchParameters.setTypeName(typeName);
        searchParameters.setTermName(termName);
        searchParameters.setIncludeSubClassifications(true);
        searchParameters.setIncludeSubTypes(true);
        searchParameters.setIncludeClassificationAttributes(true);
        searchParameters.setExcludeDeletedEntities(excludeDeletedEntities);
        searchParameters.setAttributes(returnColumnsParams);
        return  callAPI(API_V2.FACETED_SEARCH, String.class, gson.toJson(searchParameters),new MultivaluedMapImpl());
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
     * 获取分词详情
     * @param termGuid
     * @return
     * @throws AtlasServiceException
     */
    public String getGlossaryTerm0(String termGuid) throws AtlasServiceException {

        return callAPI(API_V2.GET_GLOSSARY_TERM, String.class, null, termGuid);
    }

    /**
     * 设置实体的标签
     */
    public void setLabels(String guid, Set<String> labels) throws AtlasServiceException {
        API    api       = new API(String.format(SET_ENTITY_LABELS_BY_GUID_TEMPLATE, guid), HttpMethod.POST, Response.Status.NO_CONTENT);
        callAPI(api,  (Class<?>)null, labels);
    }

    public static class API_V3 extends API {
        public static final API_V3 GET_ENTITIES = new API_V3(ENTITIES_API, HttpMethod.GET, Response.Status.OK);
        public static final API_V3 UPDATE_ENTITY_ATTR  = new API_V3(UPDATE_ENTITY_ATTR_API,HttpMethod.PUT,Response.Status.OK);

        private API_V3(String path, String method, Response.Status status) {
                super(path, method, status);
        }
    }
}
