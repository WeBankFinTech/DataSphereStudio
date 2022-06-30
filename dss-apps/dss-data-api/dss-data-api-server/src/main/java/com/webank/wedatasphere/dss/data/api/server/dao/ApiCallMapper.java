package com.webank.wedatasphere.dss.data.api.server.dao;


import com.webank.wedatasphere.dss.data.api.server.entity.ApiCall;
import com.webank.wedatasphere.dss.data.api.server.entity.request.CallMonitorResquest;
import com.webank.wedatasphere.dss.data.api.server.entity.request.SingleCallMonitorRequest;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiCallInfoByCnt;
import com.webank.wedatasphere.dss.data.api.server.entity.response.ApiCallInfoByFailRate;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

public interface ApiCallMapper {
    @Insert("INSERT INTO dss_dataapi_call(api_id, params_value, status, time_start, time_end, time_length, caller) " +
            "VALUES(#{apiCall.apiId}, #{apiCall.paramsValue}, #{apiCall.status}, #{apiCall.timeStart}, #{apiCall.timeEnd}, #{apiCall.timeLength}, #{apiCall.caller})")
    @Options(useGeneratedKeys = true, keyProperty = "apiCall.id", keyColumn = "id")
    int addApiCall(@Param("apiCall") ApiCall apiCall);

    @Select("SELECT COUNT(b.id) FROM dss_dataapi_config a\n" +
            "LEFT JOIN dss_dataapi_call b ON a.id = b.api_id\n" +
            "WHERE a.workspace_id = #{callMonitorResquest.workspaceId}\n" +
            "   AND (b.time_start BETWEEN #{callMonitorResquest.startTime} AND #{callMonitorResquest.endTime})")
    Long getCallTotalCnt(@Param("callMonitorResquest") CallMonitorResquest callMonitorResquest);   //总调用次数

    @Select("SELECT IFNULL(SUM(b.time_length),0) FROM dss_dataapi_config a\n" +
            "LEFT JOIN dss_dataapi_call b ON a.id = b.api_id\n" +
            "WHERE a.workspace_id = #{callMonitorResquest.workspaceId}\n" +
            "   AND (b.time_start BETWEEN #{callMonitorResquest.startTime} AND #{callMonitorResquest.endTime})")
    Long getCallTotalTime(@Param("callMonitorResquest") CallMonitorResquest callMonitorResquest);  //总调用时长


    /**
     * 调用量排行TOP10
     */
    @Select("SELECT a.id, a.api_name, COUNT(b.id) total_cnt, IFNULL(SUM(b.time_length),0) total_time, IFNULL(ROUND(AVG(b.time_length),0),0) avg_time\n" +
            "FROM dss_dataapi_config a\n" +
            "LEFT JOIN dss_dataapi_call b ON a.id = b.api_id\n" +
            "WHERE a.workspace_id = #{callMonitorResquest.workspaceId}\n" +
            "   AND (b.time_start BETWEEN #{callMonitorResquest.startTime} AND #{callMonitorResquest.endTime})\n" +
            "GROUP BY a.id\n" +
            "ORDER BY COUNT(b.id) DESC\n" +
            "LIMIT 10")
    List<ApiCallInfoByCnt> getCallListByCnt(@Param("callMonitorResquest") CallMonitorResquest callMonitorResquest);

    /**
     * 出错排行TOP10
     */
    @Select("SELECT a.id, a.api_name, COUNT(b.id) total_cnt, SUM(CASE WHEN b.status =2 THEN 1 ELSE 0 END) fail_cnt, ROUND(100 * SUM(CASE WHEN b.status =2 THEN 1 ELSE 0 END) / COUNT(b.id),2) fail_rate\n" +
            "FROM dss_dataapi_config a\n" +
            "LEFT JOIN dss_dataapi_call b ON a.id = b.api_id\n" +
            "WHERE a.workspace_id = #{callMonitorResquest.workspaceId}\n" +
            "   AND (b.time_start BETWEEN #{callMonitorResquest.startTime} AND #{callMonitorResquest.endTime})\n" +
            "GROUP BY a.id\n" +
            "ORDER BY fail_rate DESC\n" +
            "LIMIT 10")
    List<ApiCallInfoByFailRate> getCallListByFailRate(@Param("callMonitorResquest") CallMonitorResquest callMonitorResquest);


    /**
     * 过去24小时，每小时的请求数目
     * 特别注意：dss_project -- 记录数足够的任意一张表，主要是为了给前端拼凑缺失时间段数据
     */
    @Select("SELECT DATE_FORMAT(time_start,'%Y-%m-%d %H:00') AS k, COUNT(*) AS v\n" +
            "FROM dss_dataapi_config a JOIN dss_dataapi_call b ON a.id =b.api_id \n" +
            "WHERE a.workspace_id =#{workspaceId} AND time_start >= (NOW() - INTERVAL 24 HOUR)\n" +
            "GROUP BY DATE_FORMAT(time_start,'%Y-%m-%d %H:00')\n" +
            "ORDER BY k")
    List<Map<String, Object>> getCallCntForPast24H(Long workspaceId);


    /**
     * 时间范围内指定API的每小时的平均响应时间
     * 特别注意：dss_project -- 记录数足够的任意一张表，主要是为了给前端拼凑缺失时间段数据
     */
    @Select("SELECT DATE_FORMAT(time_start,'%Y-%m-%d %H:00') AS k, IFNULL(ROUND(AVG(time_length),0),0) AS v\n" +
            "FROM dss_dataapi_call\n" +
            "WHERE api_id =#{singleCallMonitorRequest.apiId} AND (time_start BETWEEN #{singleCallMonitorRequest.startTime} AND #{singleCallMonitorRequest.endTime})\n" +
            "GROUP BY DATE_FORMAT(time_start,'%Y-%m-%d %H:00')\n" +
            "ORDER BY k")
    List<Map<String, Object>> getCallTimeForSinleApi(@Param("singleCallMonitorRequest") SingleCallMonitorRequest singleCallMonitorRequest);


    /**
     * 时间范围内指定API的每小时的请求次数
     *
     */
    @Select("SELECT DATE_FORMAT(time_start,'%Y-%m-%d %H:00') AS k, COUNT(id) AS v\n" +
            "FROM dss_dataapi_call\n" +
            "WHERE api_id =#{singleCallMonitorRequest.apiId} AND (time_start >= #{singleCallMonitorRequest.startTime} AND time_start <= #{singleCallMonitorRequest.endTime})\n" +
            "GROUP BY DATE_FORMAT(time_start,'%Y-%m-%d %H:00')\n" +
            "ORDER BY k")
    List<Map<String, Object>> getCallCntForSinleApi(@Param("singleCallMonitorRequest") SingleCallMonitorRequest singleCallMonitorRequest);
}
