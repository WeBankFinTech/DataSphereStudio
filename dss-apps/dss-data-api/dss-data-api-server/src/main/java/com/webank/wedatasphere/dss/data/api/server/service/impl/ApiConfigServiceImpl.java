package com.webank.wedatasphere.dss.data.api.server.service.impl;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.sql.PagerUtils;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.data.api.server.dao.ApiAuthMapper;
import com.webank.wedatasphere.dss.data.api.server.dao.ApiCallMapper;
import com.webank.wedatasphere.dss.data.api.server.dao.ApiConfigMapper;
import com.webank.wedatasphere.dss.data.api.server.entity.ApiCall;
import com.webank.wedatasphere.dss.data.api.server.entity.ApiConfig;
import com.webank.wedatasphere.dss.data.api.server.entity.ApiGroup;
import com.webank.wedatasphere.dss.data.api.server.entity.DataSource;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiExecuteInfo;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiResDataInfo;
import com.webank.wedatasphere.dss.data.api.server.exception.DataApiException;
import com.webank.wedatasphere.dss.data.api.server.service.ApiConfigService;
import com.webank.wedatasphere.dss.data.api.server.service.ApiDataSourceService;
import com.webank.wedatasphere.dss.data.api.server.util.*;
import com.webank.wedatasphere.dss.orange.SqlMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired
    ApiDataSourceService apiDataSourceService;

    /**
     * 保存API配置信息
     * @param apiConfig
     * @throws JSONException
     */
    public void saveApi(ApiConfig apiConfig) throws JSONException, DataApiException {

        List<ApiConfig> apiList = this.list(new QueryWrapper<ApiConfig>().eq("api_path", apiConfig.getApiPath()));
        Integer id = apiConfig.getId();
        if(apiList.size() > 0 && id == null){
            throw new DataApiException("路径已经存在");
        }
        String apiType = apiConfig.getApiType();
        if ("GUIDE".equals(apiType)) {
            String table = apiConfig.getTblName();
            String resFields = apiConfig.getResFields();
            String reqFields = apiConfig.getReqFields();
            String orderFields = apiConfig.getOrderFields();
            String whereCause = StringUtils.isBlank(reqFields) ? "" : CommUtil.getWhereCause(reqFields);
            String orderCause = StringUtils.isBlank(orderFields) ? "" : CommUtil.getOrderCause(orderFields);
            String sql = String.format("%s%s%s%s%s%s", "select ", resFields, " from ", table, whereCause, orderCause);
            apiConfig.setSql(sql);
        }
//        UpdateWrapper<ApiConfig> apiConfigUpdateWrapper = new UpdateWrapper<ApiConfig>()
//                .eq("id", id);
        if (id != null) {
            if(apiConfig.getStatus() == 1){
                throw new DataApiException("请先下线,测试通过后, 重新发布");
            }
            apiConfig.setIsTest(0);
            this.updateById(apiConfig);
        } else {
            this.save(apiConfig);
        }
    }

    /**
     * API 测试
     * @param path
     * @param request
     * @param map
     * @return
     * @throws Exception
     */

    @Override
    public ApiExecuteInfo apiTest(String path, HttpServletRequest request,Map<String,Object> map,boolean isTest) throws JSONException, SQLException, DataApiException {
        ApiExecuteInfo apiExecuteInfo = new ApiExecuteInfo();
        ApiConfig apiConfig = this.getOne(new QueryWrapper<ApiConfig>().eq("api_path", path));
        List<Object > jdbcParamValues = new ArrayList<>();
        int pageSize = apiConfig.getPageSize();
        String limitSent = "";
        String sqlText = "";
        int pageNum = 0;

        //组装分页limit
        if(pageSize > 0 ){
            Object pageNumObject = map.get("pageNum");
            if(pageNumObject == null){
                throw new DataApiException("请设置pageNum参数");
            }
            pageNum = Integer.valueOf(pageNumObject.toString());
            if(pageNum < 1){
                throw new DataApiException("pageNum参数错误");
            }
            limitSent = " limit "+ ((pageNum-1) * pageSize)+","+pageSize;
        }
        if (apiConfig != null) {
            Map<String, Object> sqlParam = this.getSqlParam(request, apiConfig,map);
            String sqlFiled = apiConfig.getSql();
            if(!sqlParam.isEmpty()){
                SqlMeta sqlMeta = SqlEngineUtil.getEngine().parse(sqlFiled, sqlParam);
                sqlText = sqlMeta.getSql();
                jdbcParamValues = sqlMeta.getJdbcParamValues();
            }else {
                sqlText = sqlFiled;
            }
            //不分页,最多返回500条数据
            if(pageSize <= 0) {
                sqlText = String.format("%s %s",sqlText," limit 500");
            }

            Integer datasourceId = apiConfig.getDatasourceId();
            DataSource dataSource = apiDataSourceService.getById(datasourceId);

            String dataSourceType = dataSource.getType();
            if("MYSQL".equals(dataSourceType)){
                dataSource.setClassName("com.mysql.jdbc.Driver");
            }
            if(sqlText.toLowerCase().contains("select")){
                apiExecuteInfo = this.executeSql(1, dataSource, sqlText,limitSent, jdbcParamValues,pageSize,pageNum);
            }else {
                apiExecuteInfo = this.executeSql(2, dataSource, sqlText,limitSent, jdbcParamValues,pageSize,pageNum);
            }
            if(isTest){
                apiConfigMapper.updateApiTestStatus(apiConfig.getId(),1);
            }

        }else {
            apiExecuteInfo.setLog("该服务不存在,请检查服务url是否正确");
        }
        return apiExecuteInfo;
    }


    /**
     * 第三方调用APi
     * @param path
     * @param request
     * @return
     * @throws Exception
     */
    @Override
    public ApiExecuteInfo apiExecute(String path, HttpServletRequest request,Map<String,Object> map) throws DataApiException, JSONException, SQLException {
        ApiCall apiCall = new ApiCall();
        //校验token
        String appKey = request.getHeader("appKey");
        String appSecret = request.getHeader("appSecret");
        if(StringUtils.isAnyBlank(appKey,appSecret)){
            throw new DataApiException("请求header需添加appKey,appSecret");
        }
        ApiConfig apiConfig = this.getOne(new QueryWrapper<ApiConfig>().eq("api_path", path).eq("is_delete",0));
        if(apiConfig != null){
            int status = apiConfig.getStatus();
            if(status == 0){
                throw new DataApiException("该服务已下线");
            }
            long startTime = System.currentTimeMillis();
            apiCall.setApiId(apiConfig.getId().longValue());
            apiCall.setTimeStart(new Date(startTime));
            apiCall.setCaller(appKey);
            int groupId = apiConfig.getGroupId();
            Long expireTime = apiAuthMapper.getToken(appKey,groupId,appSecret);
            if(expireTime != null && (expireTime * 1000) > startTime){
                ApiExecuteInfo apiExecuteInfo = apiTest(path,request,map,false);
                long endTime = System.currentTimeMillis();
                apiCall.setTimeEnd(new Date(endTime));
                apiCall.setTimeLength(endTime-startTime);
                apiCall.setStatus(0);
                apiCallMapper.addApiCall(apiCall);
                return apiExecuteInfo;
            }else {
                throw new DataApiException("token已失效");
            }
        }else {
            throw new DataApiException("该服务不存在,请检查服务url是否正确");
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
        return   apiConfigMapper.release(status,apiId);
    }


    private Map<String, Object> getSqlParam(HttpServletRequest request, ApiConfig config,Map<String,Object> paraMap) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        String reqFields = config.getReqFields();
        if(StringUtils.isNotBlank(reqFields)){
            JSONArray requestParams = new JSONArray(config.getReqFields());
            for (int i = 0; i < requestParams.length(); i++) {
                JSONObject jo = requestParams.getJSONObject(i);
                String name = jo.getString("name");
                String type = jo.getString("type");
                if(type.indexOf("(") > 0){
                    type = type.substring(0,type.indexOf("(")).toLowerCase();
                }
                if (type.startsWith("Array")) {
                    String[] values = CommUtil.objectToArray(paraMap.get(name));
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
//                                case "Array<string>":
//                                case "Array<date>":
                                default:
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

                    String value = paraMap == null || paraMap.get(name) == null ? null : String.valueOf(paraMap.get(name));
                    if (StringUtils.isNotBlank(value)) {

                        switch (type) {
                            case "double":
                                Double v = Double.valueOf(value);
                                map.put(name, v);
                                break;
                            case "int":
                            case "bigint":
                                Long longV = Long.valueOf(value);
                                map.put(name, longV);
                                break;
//                            case "string":
//                            case "varchar":
//                            case "datetime":
//                            case "timestamp":
//                            case "date":
                            default:
                                map.put(name, value);
                                break;
                        }
                    } else {
                        map.put(name, value);
                    }
                }
            }

        }

        return map;
    }


    public ApiExecuteInfo executeSql(int isSelect, DataSource datasource, String sql,String pageSent, List<Object> jdbcParamValues,int pageSize,int  pageNum) throws DataApiException, SQLException {
        DruidPooledConnection connection = null;
        StringBuilder logBuilder = new StringBuilder();
        ApiExecuteInfo apiExecuteInfo = new ApiExecuteInfo();
        ApiResDataInfo apiResDataInfo = new ApiResDataInfo();
        ArrayList<HashMap<String,Object>> list = new ArrayList<>();
        try {
            connection = PoolManager.getPooledConnection(datasource);
            PreparedStatement statement = connection.prepareStatement(sql+pageSent);
            //参数注入
            for (int i = 1; i <= jdbcParamValues.size(); i++) {
                statement.setObject(i, jdbcParamValues.get(i - 1));
            }
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
                        jo.put(columnName,value == null ? null :value.toString() );
                    }
                    list.add(jo);
                }

                //分页,计算总page
                if(pageSize > 0){
                    String countSql = PagerUtils.count(sql, DbType.MYSQL.getDb());
                    PreparedStatement countStatement = connection.prepareStatement(countSql);
                    //参数注入
                    for (int i = 1; i <= jdbcParamValues.size(); i++) {
                        countStatement.setObject(i, jdbcParamValues.get(i-1));
                    }
                    ResultSet  countRs = countStatement.executeQuery();
                    if ((countRs.next())){
                        int totalRecord = countRs.getInt(1);
                        int totalPage = (totalRecord + pageSize-1) / pageSize;
                        apiResDataInfo.setTotalPage(totalPage);
                        apiResDataInfo.setTotal(totalRecord);
                        apiResDataInfo.setPageSize(pageSize);
                        apiResDataInfo.setCurrentPageNum(pageNum);
                    }
                }

            } else {
                statement.executeUpdate();
            }
            apiResDataInfo.setData(list);
        } catch (Exception e){
            log.error("ERROR", "Error found: ", e);
            logBuilder.append(e.getMessage());
           throw new DataApiException(logBuilder.toString());
        }finally {
            connection.close();
        }
        apiExecuteInfo.setLog(logBuilder.toString());
        apiExecuteInfo.setResList(apiResDataInfo);
        return apiExecuteInfo;
    }

}
