/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.webank.wedatasphere.dss.apiservice.core.service.impl;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.webank.wedatasphere.dss.apiservice.core.constant.ParamTypeEnum;
import com.webank.wedatasphere.dss.apiservice.core.constant.RequireEnum;
import com.webank.wedatasphere.dss.apiservice.core.constant.StatusEnum;
import com.webank.wedatasphere.dss.apiservice.core.dao.ApiServiceConfigDao;
import com.webank.wedatasphere.dss.apiservice.core.dao.ApiServiceParamDao;
import com.webank.wedatasphere.dss.apiservice.core.jdbc.DatasourceService;
import com.webank.wedatasphere.dss.apiservice.core.restful.exception.ApiServiceRuntimeException;
import com.webank.wedatasphere.dss.apiservice.core.service.ApiServiceQueryService;
import com.webank.wedatasphere.dss.apiservice.core.util.AssertUtil;
import com.webank.wedatasphere.dss.apiservice.core.util.DateUtil;
import com.webank.wedatasphere.dss.apiservice.core.util.ModelMapperUtil;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiVersionVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiServiceVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.ParamType;
import com.webank.wedatasphere.dss.apiservice.core.vo.ParamVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.TestParamVo;
import com.webank.wedatasphere.dss.apiservice.core.jdbc.JdbcUtil;
import com.webank.wedatasphere.linkis.bml.client.BmlClient;
import com.webank.wedatasphere.linkis.bml.client.BmlClientFactory;
import com.webank.wedatasphere.linkis.bml.protocol.BmlDownloadResponse;
import com.webank.wedatasphere.linkis.common.io.FsPath;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * 接口调用service
 *
 * @author lidongzhang
 */
@Service
public class ApiServiceQueryServiceImpl implements ApiServiceQueryService {
    private static final Logger LOG = LoggerFactory.getLogger(ApiServiceQueryServiceImpl.class);

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
    private ApiServiceConfigDao apiServiceConfigDao;

    @Autowired
    private ApiServiceParamDao apiServiceParamDao;

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

        ApiServiceVo apiServiceVo = apiServiceConfigDao.queryByPath(path);

        AssertUtil.notNull(apiServiceVo, "接口不存在，path=" + path);
        AssertUtil.isTrue(StringUtils.equals(httpMethod, apiServiceVo.getMethod().toUpperCase()),
                "该接口不支持" + httpMethod + "请求，请用" + apiServiceVo.getMethod() + "请求");
        AssertUtil.isTrue(1 == apiServiceVo.getStatus(), "接口已禁用");

        try {
            Pair<Object, ArrayList<String[]>> collect = queryBml(apiServiceVo.getCreator(), apiServiceVo.getResourceId(),
                    apiServiceVo.getVersion(), apiServiceVo.getScriptPath());
            String executeCode = collect.getSecond().get(0)[0];

            Map<String, Object> variable = (Map) ((Map) collect.getFirst()).get("variable");

            // 没有传入的参数，使用默认值
            if (variable != null) {
                Map<String, String> paramTypes = queryConfigParam(apiServiceVo.getId(), apiServiceVo.getVersion());
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
            throw new ApiServiceRuntimeException(e.getMessage(), e);
        }

    }

    @Override
    public List<TestParamVo> query(String scriptPath, String version) {
        ApiServiceVo apiServiceVo = apiServiceConfigDao.queryByScriptPathVersion(scriptPath, version);

        AssertUtil.notNull(apiServiceVo, "接口不存在，path=" + scriptPath);

        List<ParamVo> paramVoList = apiServiceParamDao.queryByConfigIdAndVersion(apiServiceVo.getId(), apiServiceVo.getVersion());

        List<TestParamVo> testParamVoList = new ArrayList<>();

        Map<String, ParamVo> paramMap = paramVoList.stream()
                .collect(Collectors.toMap(ParamVo::getName, k -> k, (k, v) -> k));
        Map<String, Object> variableMap = getVariable(apiServiceVo);

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
    public List<ApiVersionVo> queryApiVersion(String scriptPath) {

        ApiServiceVo apiServiceVo = apiServiceConfigDao.queryByScriptPath(scriptPath);
        
        AssertUtil.isFalse(null == apiServiceVo, "api 不存在");

        ApiVersionVo apiVersionVo = new ApiVersionVo();
        apiVersionVo.setId(apiServiceVo.getId());
        apiVersionVo.setScriptPath(apiServiceVo.getScriptPath());
        apiVersionVo.setPath(apiServiceVo.getPath());
        apiVersionVo.setVersion(apiServiceVo.getVersion());
        apiVersionVo.setStatus(apiServiceVo.getStatus());
        apiVersionVo.setStatusStr(StatusEnum.getEnum(apiServiceVo.getStatus()).getName());
        apiVersionVo.setCreator(apiServiceVo.getCreator());
        apiVersionVo.setPublishDateStr(DateUtil.format(apiServiceVo.getCreateTime(), DateUtil.FORMAT_LONG));
        apiVersionVo.setUpdateDateStr(DateUtil.format(apiServiceVo.getModifyTime(), DateUtil.FORMAT_LONG));
        return Collections.singletonList(apiVersionVo);
    }
    
    private Map<String, Object> getVariable(ApiServiceVo apiServiceVo) {
        Map<String, Object> variableMap = null;
        try {
            Pair<Object, ArrayList<String[]>> collect = queryBml(apiServiceVo.getCreator(), apiServiceVo.getResourceId(),
                    apiServiceVo.getVersion(), apiServiceVo.getScriptPath());

            variableMap = (Map) ((Map) collect.getFirst()).get("variable");
        } catch (IOException e) {
            throw new ApiServiceRuntimeException(e.getMessage(), e);
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
                    collect = apiServiceParamDao.queryByConfigIdAndVersion(configId, version)
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
