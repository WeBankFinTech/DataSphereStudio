/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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
import com.webank.wedatasphere.dss.apiservice.core.bo.ApiServiceJob;
import com.webank.wedatasphere.dss.apiservice.core.bo.ApiServiceToken;
import com.webank.wedatasphere.dss.apiservice.core.bo.LinkisExecuteResult;
import com.webank.wedatasphere.dss.apiservice.core.config.ApiServiceConfiguration;
import com.webank.wedatasphere.dss.apiservice.core.constant.ParamType;
import com.webank.wedatasphere.dss.apiservice.core.constant.RequireEnum;
import com.webank.wedatasphere.dss.apiservice.core.dao.*;
import com.webank.wedatasphere.dss.apiservice.core.exception.ApiServiceQueryException;
import com.webank.wedatasphere.dss.apiservice.core.execute.ApiServiceExecuteJob;
import com.webank.wedatasphere.dss.apiservice.core.execute.DefaultApiServiceJob;
import com.webank.wedatasphere.dss.apiservice.core.execute.ExecuteCodeHelper;
import com.webank.wedatasphere.dss.apiservice.core.execute.LinkisJobSubmit;
import com.webank.wedatasphere.dss.apiservice.core.jdbc.DatasourceService;
import com.webank.wedatasphere.dss.apiservice.core.service.ApiService;
import com.webank.wedatasphere.dss.apiservice.core.util.DateUtil;
import com.webank.wedatasphere.dss.apiservice.core.util.SQLCheckUtil;
import com.webank.wedatasphere.dss.apiservice.core.vo.*;
//import com.webank.wedatasphere.dss.oneservice.core.jdbc.JdbcUtil;
import com.webank.wedatasphere.dss.apiservice.core.exception.ApiServiceRuntimeException;
import com.webank.wedatasphere.dss.apiservice.core.service.ApiServiceQueryService;
import com.webank.wedatasphere.dss.apiservice.core.util.AssertUtil;
import com.webank.wedatasphere.dss.apiservice.core.util.ModelMapperUtil;
//import com.webank.wedatasphere.dss.oneservice.core.vo.*;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiServiceVo;
import org.apache.linkis.bml.client.BmlClient;
import org.apache.linkis.bml.client.BmlClientFactory;
import org.apache.linkis.bml.protocol.BmlDownloadResponse;
import org.apache.linkis.common.io.FsPath;
import org.apache.linkis.storage.source.FileSource;
import org.apache.linkis.storage.source.FileSource$;
import org.apache.linkis.ujes.client.UJESClient;
import org.apache.linkis.ujes.client.response.JobExecuteResult;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;


@Service
public class ApiServiceQueryServiceImpl implements ApiServiceQueryService {
    private static final Logger LOG = LoggerFactory.getLogger(ApiServiceQueryServiceImpl.class);
    private static final Pattern pattern = Pattern.compile("--+");
    private static final String REPLACEMENT = "\\-";

    Map<String, ApiServiceJob> runJobs = new HashMap<>();

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
    private ApiServiceDao apiServiceDao;

    @Autowired
    private ApiServiceParamDao apiServiceParamDao;

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private ApiServiceVersionDao apiServiceVersionDao;

    @Autowired
    private ApiServiceTokenManagerDao apiServiceTokenManagerDao;


    @Autowired
    private ApiService apiService;


    @Autowired
    private  ApiServiceAccessDao apiServiceAccessDao;

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
    public LinkisExecuteResult query(String path,
                                     Map<String, Object> reqParams,
                                     String moduleName,
                                     String httpMethod,
                                     ApiServiceToken tokenDetail,
                                     String loginUser) {
        // 根据path查询resourceId和version
        // 得到metadata
        // 执行查询
        //path 必须唯一
        ApiServiceVo apiServiceVo = apiServiceDao.queryByPath(path);
        if(null == apiServiceVo){
            throw new ApiServiceRuntimeException("根据脚本路径未匹配到数据服务！");
        }
        if(!apiService.checkUserWorkspace(loginUser,apiServiceVo.getWorkspaceId().intValue())){
            throw new ApiServiceRuntimeException("用户工作空间检查不通过！");
        }
        if(!apiServiceVo.getId().equals(tokenDetail.getApiServiceId())){
            throw new ApiServiceRuntimeException("用户token中服务ID不匹配！");
        }

        ApiVersionVo maxApiVersionVo =apiService.getMaxVersion(apiServiceVo.getId());

        AssertUtil.notNull(apiServiceVo, "接口不存在，path=" + path);
        AssertUtil.isTrue(StringUtils.equals(httpMethod, apiServiceVo.getMethod().toUpperCase()),
                "该接口不支持" + httpMethod + "请求，请用" + apiServiceVo.getMethod() + "请求");
        AssertUtil.isTrue(1 == apiServiceVo.getStatus(), "接口已禁用");
        AssertUtil.notNull(maxApiVersionVo, "未找到最新的版本，path=" + path);

        try {
            Pair<Object, ArrayList<String[]>> collect = queryBml(apiServiceVo.getCreator(), maxApiVersionVo.getBmlResourceId(),
                    maxApiVersionVo.getBmlVersion(), apiServiceVo.getScriptPath());
            String executeCode = collect.getSecond().get(0)[0];

            Map<String, Object> variable = (Map) ((Map) collect.getFirst()).get("variable");

            //没有传入的参数，使用默认值
            Map<String, String> paramTypes = queryConfigParam(apiServiceVo.getId(), maxApiVersionVo.getVersion());
            if (variable != null) {
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

            // 用户请求的参数值注入检查，排除token
            for(Map.Entry<String, Object> entry: reqParams.entrySet()){
                String k = entry.getKey();
                String v = String.valueOf(entry.getValue());
                if (v.contains("--")){
                    entry.setValue(replaceSymbol(v));
                }
                if(!k.equals(ApiServiceConfiguration.API_SERVICE_TOKEN_KEY.getValue())
                   && SQLCheckUtil.doParamInjectionCheck((String) reqParams.get(k))) {
                    // 如果注入直接返回null
                    LOG.warn("用户参数存在非法的关键字{}", reqParams.get(k).toString());
                    return null;
                }
            }

            //数组类型，如果没有加单引号，自动添加
            reqParams.forEach((k,v) ->{
                if(ParamType.array.equals(paramTypes.get(k))){
                    String sourceStr = v.toString();
                    String targetStr =sourceStr;
                    sourceStr= sourceStr.replaceAll("(\n\r|\r\n|\r|\n)", ",");
                    sourceStr= sourceStr.replaceAll(",,", ",");

                    if(!sourceStr.contains("\'")){
                        targetStr= Arrays.stream(sourceStr.split(",")).map(s -> "\'" + s + "\'").collect(Collectors.joining(","));
                        reqParams.put(k, targetStr);
                    }else {
                        reqParams.put(k, sourceStr);
                    }
                }
            });

            ApiServiceExecuteJob job = new DefaultApiServiceJob();
            //sql代码封装成scala执行
            job.setCode(ExecuteCodeHelper.packageCodeToExecute(executeCode, maxApiVersionVo.getMetadataInfo()));
            job.setEngineType(apiServiceVo.getType());
            job.setRunType("scala");
            //不允许创建用户自己随意代理执行，创建用户只能用自己用户执行
            //如果需要代理执行可以在这里更改用户
            job.setUser(loginUser);

            job.setParams(null);
            job.setRuntimeParams(reqParams);
            job.setScriptePath(apiServiceVo.getScriptPath());
            UJESClient ujesClient = LinkisJobSubmit.getClient(paramTypes);

            //记录用户Api被执行信息
            ApiAccessVo apiAccessVo = new ApiAccessVo();
            apiAccessVo.setUser(loginUser);
            apiAccessVo.setApiPublisher(apiServiceVo.getCreator());
            apiAccessVo.setApiServiceName(apiServiceVo.getName());
            apiAccessVo.setApiServiceId(apiServiceVo.getId());
            apiAccessVo.setApiServiceVersionId(maxApiVersionVo.getId());
            apiAccessVo.setProxyUser(job.getUser());
            apiAccessVo.setAccessTime(DateUtil.getNow());
            apiServiceAccessDao.addAccessRecord(apiAccessVo);

            JobExecuteResult jobExecuteResult = LinkisJobSubmit.execute(job,ujesClient);

            //记录执行任务用户和代理用户关系，没有代理用户的统一设置为登录用户
            ApiServiceJob apiServiceJob = new ApiServiceJob();
            apiServiceJob.setSubmitUser(loginUser);
            apiServiceJob.setProxyUser(job.getUser());
            apiServiceJob.setJobExecuteResult(jobExecuteResult);
            runJobs.put(jobExecuteResult.getTaskID(),apiServiceJob);

            LinkisExecuteResult linkisExecuteResult = new LinkisExecuteResult(jobExecuteResult.getTaskID(), jobExecuteResult.getExecID());
            return linkisExecuteResult;
        } catch (IOException e) {
            throw new ApiServiceRuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public ApiServiceVo queryByVersionId(String userName,Long versionId) throws ApiServiceQueryException {
        ApiVersionVo apiVersionVo =   apiServiceVersionDao.queryApiVersionByVersionId(versionId);
        ApiServiceVo apiServiceVo = apiServiceDao.queryById(apiVersionVo.getApiId());
        //授权后才可以查看内容
        List<TokenManagerVo> userTokenManagerVos = apiServiceTokenManagerDao.queryByApplyUserAndVersionId(userName,versionId);
        if(userTokenManagerVos.size()>0) {
            try {
                Pair<Object, ArrayList<String[]>> collect = queryBml(apiServiceVo.getCreator(), apiVersionVo.getBmlResourceId(),
                        apiVersionVo.getBmlVersion(), apiServiceVo.getScriptPath());
                String executeCode = collect.getSecond().get(0)[0];
                apiServiceVo.setContent(executeCode);

            } catch (IOException e) {
                throw new ApiServiceQueryException(800002, "查询数据服务API内容异常");
            }
            apiServiceVo.setScriptPath(apiVersionVo.getSource());
            return apiServiceVo;
        }else {

            throw new ApiServiceQueryException(800003, "没有权限查看数据服务API内容，请先提单授权");
        }
    }

    @Override
    public List<QueryParamVo> queryParamList(String scriptPath, Long versionId) {
        ApiVersionVo targetApiVersionVo = apiServiceVersionDao.queryApiVersionByVersionId(versionId);

        ApiServiceVo apiServiceVo=apiServiceDao.queryById(targetApiVersionVo.getApiId());

        AssertUtil.notNull(apiServiceVo, "接口不存在，path=" + scriptPath);

        AssertUtil.notNull(targetApiVersionVo, "目标参数版本不存在，path=" + scriptPath+",version:"+versionId);

        List<ParamVo> paramVoList = apiServiceParamDao.queryByVersionId(targetApiVersionVo.getId());

        List<QueryParamVo> queryParamVoList = new ArrayList<>();

        Map<String, ParamVo> paramMap = paramVoList.stream()
                .collect(Collectors.toMap(ParamVo::getName, k -> k, (k, v) -> k));
        Map<String, Object> variableMap = getVariable(apiServiceVo,versionId);
        paramMap.keySet()
                .forEach(keyItem -> {
                    ParamVo paramVo = paramMap.get(keyItem);
                    QueryParamVo queryParamVo = ModelMapperUtil.strictMap(paramVo, QueryParamVo.class);
                    queryParamVo.setTestValue(variableMap.containsKey(keyItem) ? variableMap.get(keyItem).toString() : "");
                    queryParamVo.setRequireStr(RequireEnum.getEnum(paramVo.getRequired()).getName());
                    queryParamVo.setType(paramVo.getType());

                    queryParamVoList.add(queryParamVo);
                });

        return queryParamVoList;
    }

    @Override
    public List<ApiVersionVo> queryApiVersionById(Long serviceId) {
        List<ApiVersionVo> apiVersionVoList = apiServiceVersionDao.queryApiVersionByApiServiceId(serviceId);
        return apiVersionVoList;
    }

    private Map<String, Object> getVariable(ApiServiceVo apiServiceVo,Long versionId) {
        Map<String, Object> variableMap = null;
        ApiVersionVo apiVersionVo = apiServiceVersionDao.queryApiVersionByVersionId(versionId);
        if(null != apiServiceVo) {
            try {
                Pair<Object, ArrayList<String[]>> collect = queryBml(apiServiceVo.getCreator(), apiVersionVo.getBmlResourceId(),
                        apiVersionVo.getBmlVersion(), apiServiceVo.getScriptPath());

                variableMap = (Map) ((Map) collect.getFirst()).get("variable");
            } catch (IOException e) {
                throw new ApiServiceRuntimeException(e.getMessage(), e);
            }
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

                    try (FileSource fileSource = FileSource.create(new FsPath(scriptPath), inputStream)) {
                        //todo   数组取了第一个
                        Pair<Object, List<String[]>> sourcePair = fileSource.collect()[0];
                        collect = new Pair<>(sourcePair.getKey(), new ArrayList<>(sourcePair.getValue()));
                        bmlCache.put(key, collect);
                    }
                }
            }
        }

        return collect;
    }

    private Map<String, String> queryConfigParam(long apiId, String version) {
        String key = String.join("-", apiId + "", version);
        Map<String, String> collect = configParamCache.getIfPresent(key);

        if (collect == null) {
            synchronized (this) {
                collect = configParamCache.getIfPresent(key);
                if (collect == null) {
                    List<ApiVersionVo> apiVersionVoList = apiServiceVersionDao.queryApiVersionByApiServiceId(apiId);
                    ApiVersionVo apiVersionVo = apiVersionVoList.stream().filter(apiVersionVoTmp -> apiVersionVoTmp.getVersion().equals(version)).findFirst().orElse(null);

                    collect = apiServiceParamDao.queryByVersionId(apiVersionVo.getId())
                            .stream()
                            .collect(toMap(ParamVo::getName, ParamVo::getType));
                    configParamCache.put(key, collect);
                }
            }
        }

        return collect;
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

    @Override
    public ApiServiceJob getJobByTaskId(String taskId){
        return runJobs.get(taskId);
    }


    private static String getRunTypeFromScriptsPath(String scriptsPath) {

        String res = "sql";
        String fileFlag = scriptsPath.substring(scriptsPath.lastIndexOf(".") + 1);
        switch (fileFlag) {
            case "sh":
                res = "shell";
                break;
            case "py":
                res= "pyspark";
                break;
            default:
                res = fileFlag;
                break;
        }
        return res;

    }

    private static String replaceSymbol(String str) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = pattern.matcher(str);
        while (matcher.find()){
            String match = matcher.group();
            int length = match.length();
            StringBuilder replacement = new StringBuilder();
            for (int i = 0; i < length; i++) {
                replacement.append(REPLACEMENT);
            }
            //避免将replacement识别为正则，将替换字符追加到sb中
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement.toString()));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
