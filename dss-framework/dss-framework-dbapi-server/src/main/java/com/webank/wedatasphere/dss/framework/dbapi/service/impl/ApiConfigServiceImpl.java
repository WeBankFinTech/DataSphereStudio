package com.webank.wedatasphere.dss.framework.dbapi.service.impl;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.framework.dbapi.dao.ApiAuthMapper;
import com.webank.wedatasphere.dss.framework.dbapi.dao.ApiCallMapper;
import com.webank.wedatasphere.dss.framework.dbapi.dao.ApiConfigMapper;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiCall;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiConfig;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiGroup;
import com.webank.wedatasphere.dss.framework.dbapi.entity.DataSource;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiExecuteInfo;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.dss.framework.dbapi.service.ApiConfigService;
import com.webank.wedatasphere.dss.framework.dbapi.util.DateJsonDeserializer;
import com.webank.wedatasphere.dss.framework.dbapi.util.DateJsonSerializer;
import com.webank.wedatasphere.dss.framework.dbapi.util.PoolManager;
import com.webank.wedatasphere.dss.framework.dbapi.util.SqlEngineUtil;
import com.webank.wedatasphere.dss.orange.SqlMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ApiConfigServiceImpl extends ServiceImpl<ApiConfigMapper, ApiConfig> implements ApiConfigService {
    @Autowired
    ApiConfigMapper dssApiConfigMapper;
    @Autowired
    ApiAuthMapper apiAuthMapper;
    @Autowired
    ApiCallMapper apiCallMapper;
    @Autowired
    ApiConfigMapper apiConfigMapper;

    public void saveApi(ApiConfig apiConfig) throws JSONException {

        String apiType = apiConfig.getApiType();
        if ("GUIDE".equals(apiType)) {
            String table = apiConfig.getTblName();
            String resFields = apiConfig.getResFields();
            String reqFields = apiConfig.getReqFields();
            String orderFields = apiConfig.getOrderFields();
            String whereCause = getWhereCause(reqFields);
            String orderCause = getOrderCause(orderFields);
            String sql = String.format("%s%s%s%s%s%s", "select ", resFields, " from ", table, whereCause, orderCause);
            apiConfig.setSql(sql);
        }

        Integer id = apiConfig.getId();
        if (id != null) {
            this.updateById(apiConfig);
        } else {
            this.save(apiConfig);
        }

//
//        UpdateWrapper<ApiConfig> apiConfigUpdateWrapper = new UpdateWrapper<ApiConfig>()
//                .eq("id", id);


    }

    @Override
    public ApiExecuteInfo apiTest(String path, HttpServletRequest request) throws Exception {
        ApiExecuteInfo apiExecuteInfo = new ApiExecuteInfo();

        ApiConfig apiConfig = this.getOne(new QueryWrapper<ApiConfig>().eq("api_path", path));
        if (apiConfig != null) {
            Map<String, Object> sqlParam = this.getSqlParam(request, apiConfig);
            String sql = apiConfig.getSql();
            SqlMeta sqlMeta = SqlEngineUtil.getEngine().parse(sql, sqlParam);
            log.info(sqlMeta.getSql());
            DataSource dataSource = new DataSource();
            dataSource.setUrl("jdbc:mysql://192.168.10.219:3306/dss_test?characterEncoding=UTF-8");
            dataSource.setClassName("com.mysql.jdbc.Driver");
            dataSource.setUsername("root");
            dataSource.setPwd("123456");
            dataSource.setDatasourceId(1);
            apiExecuteInfo = this.executeSql(1, dataSource, sqlMeta.getSql(), sqlMeta.getJdbcParamValues());

        }else {
            apiExecuteInfo.setLog("该服务不存在,请检查服务url是否正确");
        }
        return apiExecuteInfo;
    }

    @Override
    public ApiExecuteInfo apiExecute(String path, HttpServletRequest request) throws Exception {
        ApiCall apiCall = new ApiCall();
        //校验token
        String appKey = request.getHeader("appKey");
        String appSecret = request.getHeader("appSecret");
        if(StringUtils.isAnyBlank(appKey,appSecret)){
            throw new Exception("请求头需添加appkey,appSecret");
        }

        ApiConfig apiConfig = this.getOne(new QueryWrapper<ApiConfig>().eq("api_path", path));
        if(apiConfig != null){
            long startTime = System.currentTimeMillis();
            apiCall.setApiId(apiConfig.getId().longValue());
            apiCall.setTimeStart(new Date(startTime));
            apiCall.setCaller(appKey);
            int groupId = apiConfig.getGroupId();
            Long expireTime = apiAuthMapper.getToken(appKey,groupId,appSecret);
            if(expireTime != null && (expireTime * 1000) > startTime){
                ApiExecuteInfo apiExecuteInfo = apiTest(path,request);
                long endTime = System.currentTimeMillis();
                apiCall.setTimeEnd(new Date(endTime));
                apiCall.setTimeLength(endTime-startTime);
                apiCall.setStatus(0);
                apiCallMapper.addApiCall(apiCall);
                return apiExecuteInfo;
            }else {
                throw new Exception("token已失效");
            }
        }else {
            throw new Exception("该服务不存在,请检查服务url是否正确");
        }
    }



    @Override
    public void addGroup(ApiGroup apiGroup) {
        dssApiConfigMapper.addApiGroup(apiGroup);
    }

    @Override
    public List<ApiGroupInfo> getGroupList(String workspaceId) {
        List<ApiGroupInfo> apiGroupInfoList = dssApiConfigMapper.getGroupByWorkspaceId(workspaceId);
        for (ApiGroupInfo apiGroupInfo : apiGroupInfoList) {
            int groupId = apiGroupInfo.getGroupId();
            apiGroupInfo.setApis(dssApiConfigMapper.getApiListByGroup(groupId));
        }
        return apiGroupInfoList;
    }


    @Override
    public Boolean release(Integer status, String apiId) {
        return    apiConfigMapper.release(status,apiId);
    }


    private String getWhereCause(String requestFields) throws JSONException {
        JSONArray jsonArray = new JSONArray(requestFields);
        StringBuilder whereCauseBuild = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            if (i == 0) {
                whereCauseBuild.append(" where ");
            }
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String columnName = jsonObject.getString("name");
            String compareType = jsonObject.getString("compare");
            String whereCause = String.format("%s %s #{%s}", columnName, compareType, columnName);
            System.out.println(whereCause);
            if (i < jsonArray.length() - 1) {
                whereCauseBuild.append(whereCause).append(" and ");
            } else {
                whereCauseBuild.append(whereCause);
            }
        }
        return whereCauseBuild.toString();
    }

    private String getOrderCause(String orderFields) throws JSONException {
        JSONArray jsonArray = new JSONArray(orderFields);
        StringBuilder orderCauseBuild = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            if (i == 0) {
                orderCauseBuild.append(" order by ");
            }
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String columnName = jsonObject.getString("name");
            String orderType = jsonObject.getString("type");
            String orderCause = String.format("%s %s", columnName, orderType);
            System.out.println(orderCause);
            if (i < jsonArray.length() - 1) {
                orderCauseBuild.append(orderCause).append(",");
            } else {
                orderCauseBuild.append(orderCause);
            }
        }
        return orderCauseBuild.toString();
    }


    private Map<String, Object> getSqlParam(HttpServletRequest request, ApiConfig config) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        JSONArray requestParams = new JSONArray(config.getReqFields());
        for (int i = 0; i < requestParams.length(); i++) {
            JSONObject jo = requestParams.getJSONObject(i);
            String name = jo.getString("name");
            String type = jo.getString("type");
            if (type.startsWith("Array")) {
                String[] values = request.getParameterValues(name);
                if (values != null) {
                    List<String> list = Arrays.asList(values);
                    if (values.length > 0) {
                        switch (type) {
                            case "Array<double>":
                                List<Double> collect = list.stream().map(value -> Double.valueOf(value)).collect(Collectors.toList());
                                map.put(name, collect);
                                break;
                            case "Array<bigint>":
                                List<Long> longs = list.stream().map(value -> Long.valueOf(value)).collect(Collectors.toList());
                                map.put(name, longs);
                                break;
                            case "Array<string>":
                            case "Array<date>":
                                map.put(name, list);
                                break;
                        }
                    } else {
                        map.put(name, list);
                    }
                } else {
                    map.put(name, null);
                }
            } else {

                String value = request.getParameter(name);
                if (StringUtils.isNotBlank(value)) {

                    switch (type) {
                        case "double":
                            Double v = Double.valueOf(value);
                            map.put(name, v);
                            break;
                        case "bigint":
                            Long longV = Long.valueOf(value);
                            map.put(name, longV);
                            break;
                        case "string":
                        case "date":
                            map.put(name, value);
                            break;
                    }
                } else {
                    map.put(name, value);
                }
            }
        }
        return map;
    }


    public ApiExecuteInfo executeSql(int isSelect, DataSource datasource, String sql, List<Object> jdbcParamValues) throws SQLException {
        DruidPooledConnection connection = null;
        StringBuilder logBuilder = new StringBuilder();

        ApiExecuteInfo apiExecuteInfo = new ApiExecuteInfo();
        ArrayList<HashMap<String,Object>> list = new ArrayList<>();
        try {
            connection = PoolManager.getPooledConnection(datasource);
            PreparedStatement statement = connection.prepareStatement(sql);
            //参数注入
            for (int i = 1; i <= jdbcParamValues.size(); i++) {
                statement.setObject(i, jdbcParamValues.get(i - 1));
            }
            log.info("statement"+statement);
            logBuilder.append("sql:"+statement +"\n");
            if (isSelect == 1) {
                ResultSet rs = statement.executeQuery();
                int columnCount = rs.getMetaData().getColumnCount();
                List<String> columns = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rs.getMetaData().getColumnLabel(i);
                    columns.add(columnName);
                }

                while (rs.next()) {
                    HashMap<String,Object> jo = new HashMap<>();
                    for (String columnName : columns) {
                        Object value = rs.getObject(columnName);
                        jo.put(columnName,value.toString() );
                    }
                    list.add(jo);
                }
            } else {
                statement.executeUpdate();
            }
            apiExecuteInfo.setResList(list);

        } catch (Exception e){
            logBuilder.append(e.getMessage());
        }finally {
            connection.close();
        }
        apiExecuteInfo.setLog(logBuilder.toString());
        return apiExecuteInfo;

    }



}
