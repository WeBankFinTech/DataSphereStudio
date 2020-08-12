package com.webank.wedatasphere.linkis.oneservice.core.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.webank.wedatasphere.linkis.bml.client.BmlClient;
import com.webank.wedatasphere.linkis.bml.client.BmlClientFactory;
import com.webank.wedatasphere.linkis.bml.protocol.BmlDownloadResponse;
import com.webank.wedatasphere.linkis.common.io.FsPath;
import com.webank.wedatasphere.linkis.oneservice.core.constant.ParamTypeEnum;
import com.webank.wedatasphere.linkis.oneservice.core.constant.RequireEnum;
import com.webank.wedatasphere.linkis.oneservice.core.constant.StatusEnum;
import com.webank.wedatasphere.linkis.oneservice.core.dao.OneServiceConfigDao;
import com.webank.wedatasphere.linkis.oneservice.core.dao.OneServiceParamDao;
import com.webank.wedatasphere.linkis.oneservice.core.jdbc.DatasourceService;
import com.webank.wedatasphere.linkis.oneservice.core.jdbc.JdbcUtil;
import com.webank.wedatasphere.linkis.oneservice.core.restful.exception.OneserviceRuntimeException;
import com.webank.wedatasphere.linkis.oneservice.core.service.OneServiceQueryService;
import com.webank.wedatasphere.linkis.oneservice.core.util.AssertUtil;
import com.webank.wedatasphere.linkis.oneservice.core.util.DateUtil;
import com.webank.wedatasphere.linkis.oneservice.core.util.ModelMapperUtil;
import com.webank.wedatasphere.linkis.oneservice.core.vo.ApiVersionVo;
import com.webank.wedatasphere.linkis.oneservice.core.vo.OneServiceVo;
import com.webank.wedatasphere.linkis.oneservice.core.vo.ParamType;
import com.webank.wedatasphere.linkis.oneservice.core.vo.ParamVo;
import com.webank.wedatasphere.linkis.oneservice.core.vo.TestParamVo;
import com.webank.wedatasphere.linkis.storage.source.FileSource;
import com.webank.wedatasphere.linkis.storage.source.FileSource$;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedCaseInsensitiveMap;
import scala.Tuple3;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * 接口调用service
 *
 * @author lidongzhang
 */
@Service
public class OneServiceQueryServiceImpl implements OneServiceQueryService {
    private static final Logger LOG = LoggerFactory.getLogger(OneServiceQueryServiceImpl.class);

    /**
     * key:resourceId+version
     * value:bml
     */
    private static Cache<String, Pair<Object, ArrayList<String[]>>> bmlCache = CacheBuilder.newBuilder()
            .expireAfterWrite(6, TimeUnit.HOURS)
            .maximumSize(10000)
            .removalListener((notification) -> {
                if (notification.getCause() == RemovalCause.SIZE) {
                    LOG.warn("bml缓存容量不足，移除key:" + notification.getKey());
                }
            })
            .build();

    /**
     * key:resourceId+version
     * value:configParam
     */
    private static Cache<String, Map<String, String>> configParamCache = CacheBuilder.newBuilder()
            .expireAfterWrite(6, TimeUnit.HOURS)
            .maximumSize(10000)
            .removalListener((notification) -> {
                if (notification.getCause() == RemovalCause.SIZE) {
                    LOG.warn("configParamCache缓存容量不足，移除key:" + notification.getKey());
                }
            })
            .build();

    /**
     * key:datasourceMap
     * value:jdbc连接信息
     */
    private static Cache<Map<String, Object>, Tuple3> datasourceCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .maximumSize(2000)
            .removalListener((notification) -> {
                if (notification.getCause() == RemovalCause.SIZE) {
                    LOG.warn("datasource缓存容量不足，移除key:" + notification.getKey());
                }
            })
            .build();

    @Autowired
    private OneServiceConfigDao oneServiceConfigDao;

    @Autowired
    private OneServiceParamDao oneServiceParamDao;

    @Autowired
    private DatasourceService datasourceService;

    /**
     * Bml client
     */
    private BmlClient client;

    @PostConstruct
    public void init() {
        LOG.info("build client start ======");
        client = BmlClientFactory.createBmlClient();
        LOG.info("build client end =======");
    }

    @Override
    public List<Map<String, Object>> query(String path, Map<String, Object> reqParams, String moduleName, String httpMethod) {
        // 根据path查询resourceId和version

        // 得到metadata

        // 执行查询

        OneServiceVo oneServiceVo = oneServiceConfigDao.queryByPath(path);

        AssertUtil.notNull(oneServiceVo, "接口不存在，path=" + path);
        AssertUtil.isTrue(StringUtils.equals(httpMethod, oneServiceVo.getMethod().toUpperCase()),
                "该接口不支持" + httpMethod + "请求，请用" + oneServiceVo.getMethod() + "请求");
        AssertUtil.isTrue(1 == oneServiceVo.getStatus(), "接口已禁用");

        try {
            Pair<Object, ArrayList<String[]>> collect = queryBml(oneServiceVo.getCreator(), oneServiceVo.getResourceId(),
                    oneServiceVo.getVersion(), oneServiceVo.getScriptPath());
            String executeCode = collect.getSecond().get(0)[0];

            Map<String, Object> variable = (Map) ((Map) collect.getFirst()).get("variable");

            // 没有传入的参数，使用默认值
            if (variable != null) {
                Map<String, String> paramTypes = queryConfigParam(oneServiceVo.getId(), oneServiceVo.getVersion());
                variable.forEach((k, v) -> {
                    if (!reqParams.containsKey(k)) {
                        if (ParamType.number.equals(paramTypes.get(k))) {
                            reqParams.put(k, Integer.valueOf(v.toString()));
                        } else {
                            reqParams.put(k, v);
                        }
                    }
                });
            }

            AssertUtil.isTrue(MapUtils.isNotEmpty((Map) collect.getKey()), "数据源不能为空");

            return executeJob(executeCode, collect.getKey(), reqParams);
        } catch (IOException e) {
            throw new OneserviceRuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public List<TestParamVo> query(Long id, String version) {
        OneServiceVo oneServiceVo = oneServiceConfigDao.queryById(id);

        AssertUtil.notNull(oneServiceVo, "接口不存在，id=" + id);

        List<ParamVo> paramVoList = oneServiceParamDao.queryByConfigIdAndVersion(oneServiceVo.getId(), oneServiceVo.getVersion());

        List<TestParamVo> testParamVoList = new ArrayList<>();

        Map<String, ParamVo> paramMap = paramVoList.stream()
                .collect(Collectors.toMap(ParamVo::getName, k -> k, (k, v) -> k));
        Map<String, Object> variableMap = getVariable(oneServiceVo);

        paramMap.keySet()
                .forEach(keyItem -> {
                    ParamVo paramVo = paramMap.get(keyItem);
                    TestParamVo testParamVo = ModelMapperUtil.strictMap(paramVo, TestParamVo.class);
                    testParamVo.setTestValue(variableMap.containsKey(keyItem) ? variableMap.get(keyItem).toString() : "");
                    testParamVo.setRequireStr(RequireEnum.getEnum(paramVo.getRequired()).getName());
                    testParamVo.setType(ParamTypeEnum.getEnum(Integer.valueOf(paramVo.getType())).getName());
                    
                    testParamVoList.add(testParamVo);
                });

        return testParamVoList;
    }

    @Override
    public List<ApiVersionVo> queryApiVersion(Long id) {

        OneServiceVo oneServiceVo = oneServiceConfigDao.queryById(id);
        
        AssertUtil.isFalse(null == oneServiceVo, "api 不存在");

        ApiVersionVo apiVersionVo = new ApiVersionVo();
        apiVersionVo.setId(oneServiceVo.getId());
        apiVersionVo.setScriptPath(oneServiceVo.getScriptPath());
        apiVersionVo.setPath(oneServiceVo.getPath());
        apiVersionVo.setVersion(oneServiceVo.getVersion());
        apiVersionVo.setStatus(oneServiceVo.getStatus());
        apiVersionVo.setStatusStr(StatusEnum.getEnum(oneServiceVo.getStatus()).getName());
        apiVersionVo.setCreator(oneServiceVo.getCreator());
        apiVersionVo.setPublishDateStr(DateUtil.format(oneServiceVo.getCreateTime(), DateUtil.FORMAT_LONG));
        apiVersionVo.setUpdateDateStr(DateUtil.format(oneServiceVo.getModifyTime(), DateUtil.FORMAT_LONG));
        return Collections.singletonList(apiVersionVo);
    }
    
    private Map<String, Object> getVariable(OneServiceVo oneServiceVo) {
        Map<String, Object> variableMap = null;
        try {
            Pair<Object, ArrayList<String[]>> collect = queryBml(oneServiceVo.getCreator(), oneServiceVo.getResourceId(),
                    oneServiceVo.getVersion(), oneServiceVo.getScriptPath());

            variableMap = (Map) ((Map) collect.getFirst()).get("variable");
        } catch (IOException e) {
            throw new OneserviceRuntimeException(e.getMessage(), e);
        }
        return null == variableMap ? Collections.EMPTY_MAP : variableMap;
    }

    private Pair<Object, ArrayList<String[]>> queryBml(String userName, String resourceId, String version,
                                                       String scriptPath) throws IOException {
        String key = String.join("-", resourceId, version);
        Pair<Object, ArrayList<String[]>> collect = bmlCache.getIfPresent(key);

        if (collect == null) {
            synchronized (this) {
                collect = bmlCache.getIfPresent(key);
                if (collect == null) {
                    BmlDownloadResponse resource;
                    if (version == null) {
                        resource = client.downloadResource(userName, resourceId, null);
                    } else {
                        resource = client.downloadResource(userName, resourceId, version);
                    }

                    AssertUtil.isTrue(resource.isSuccess(), "查询bml错误");

                    InputStream inputStream = resource.inputStream();

                    try (FileSource fileSource = FileSource$.MODULE$.create(new FsPath(scriptPath), inputStream)) {
                        collect = fileSource.collect();
                        bmlCache.put(key, collect);
                    }
                }
            }
        }


        return collect;
    }

    private Map<String, String> queryConfigParam(long configId, String version) {
        String key = String.join("-", configId + "", version);
        Map<String, String> collect = configParamCache.getIfPresent(key);

        if (collect == null) {
            synchronized (this) {
                collect = configParamCache.getIfPresent(key);
                if (collect == null) {
                    collect = oneServiceParamDao.queryByConfigIdAndVersion(configId, version)
                            .stream()
                            .collect(toMap(ParamVo::getName, ParamVo::getType));
                    configParamCache.put(key, collect);
                }
            }
        }

        return collect;
    }

    private Tuple3 getDatasourceInfo(final Map<String, Object> datasourceMap) {
        Tuple3 tuple3 = datasourceCache.getIfPresent(datasourceMap);

        if (tuple3 == null) {
            synchronized (this) {
                tuple3 = datasourceCache.getIfPresent(datasourceMap);
                if (tuple3 == null) {
                    tuple3 = JdbcUtil.getDatasourceInfo(datasourceMap);
//                    tuple3 = new Tuple3<>("jdbc:postgresql://192.168.1.43:5432/ajdw", "aijia", "sa");
                    datasourceCache.put(datasourceMap, tuple3);
                }
            }
        }

        return tuple3;
    }

    private List<Map<String, Object>> executeJob(String executeCode,
                                                 Object datasourceMap, Map<String, Object> params) {

        Tuple3 tuple3 = getDatasourceInfo((Map<String, Object>) datasourceMap);
        final String jdbcUrl = tuple3._1().toString();
        final String username = tuple3._2().toString();
        final String password = tuple3._3().toString();

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = datasourceService.getNamedParameterJdbcTemplate(jdbcUrl, username, password);

        String namedSql = genNamedSql(executeCode, params);

        return namedParameterJdbcTemplate.query(namedSql, new MapSqlParameterSource(params), new ColumnAliasMapRowMapper());
    }

    private static String genNamedSql(String executeCode, Map<String, Object> params) {
        // 没有参数，无需生成namedSql
        if (MapUtils.isEmpty(params)) {
            return executeCode;
        }

        for (String paramName : params.keySet()) {
            for (String $name : new String[]{"'${" + paramName + "}'", "${" + paramName + "}", "\"${" + paramName + "}\""}) {
                if (executeCode.contains($name)) {
                    executeCode = StringUtils.replace(executeCode, $name, ":" + paramName);
                    break;
                }
            }
        }

        return executeCode;
    }


    public static class ColumnAliasMapRowMapper implements RowMapper<Map<String, Object>> {
        @Override
        public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();
            Map<String, Object> mapOfColValues = createColumnMap(columnCount);
            Map<String, Integer> mapOfColSuffix = new LinkedCaseInsensitiveMap<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd, i));
                if (mapOfColValues.containsKey(key)) {
                    if (!mapOfColSuffix.containsKey(key)) {
                        mapOfColSuffix.put(key, 1);
                    } else {
                        mapOfColSuffix.put(key, mapOfColSuffix.get(key) + 1);
                    }

                    key = key + "_" + mapOfColSuffix.get(key);
                }

                Object obj = getColumnValue(rs, i);
                mapOfColValues.put(key, obj);
            }
            return mapOfColValues;
        }

        protected Map<String, Object> createColumnMap(int columnCount) {
            return new LinkedCaseInsensitiveMap<>(columnCount);
        }

        protected String getColumnKey(String columnName) {
            return columnName;
        }

        protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
            return JdbcUtils.getResultSetValue(rs, index);
        }

    }
}
