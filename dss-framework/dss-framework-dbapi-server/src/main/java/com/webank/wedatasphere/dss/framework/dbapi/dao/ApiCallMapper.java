package com.webank.wedatasphere.dss.framework.dbapi.dao;

import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiCall;
import com.webank.wedatasphere.dss.framework.dbapi.entity.request.CallMonitorResquest;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiCallInfoByCnt;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiCallInfoByFailRate;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Classname ApiCallMapper
 * @Description TODO
 * @Date 2021/7/20 13:49
 * @Created by suyc
 */
public interface ApiCallMapper {
    @Insert("INSERT INTO dss_dataapi_call(api_id, params_value, status, time_start, time_end, time_length, caller) " +
            "VALUES(#{apiCall.apiId}, #{apiCall.paramsValue}, #{apiCall.status}, #{apiCall.timeStart}, #{apiCall.timeEnd}, #{apiCall.timeLength}, #{apiCall.caller})")
    @Options(useGeneratedKeys = true, keyProperty = "apiCall.id", keyColumn = "id")
    int addApiCall(@Param("apiCall") ApiCall apiCall);

    @Select("SELECT COUNT(b.id) FROM dss_dataapi_config a\n" +
            "LEFT JOIN dss_dataapi_call b ON a.id = b.api_id\n" +
            "WHERE a.workspace_id = #{callMonitorResquest.workspaceId}\n" +
            "   AND (b.time_start BETWEEN #{callMonitorResquest.beginTime} AND #{callMonitorResquest.endTime})")
    long getCallTotalCnt(@Param("callMonitorResquest") CallMonitorResquest callMonitorResquest);   //总调用次数

    @Select("SELECT SUM(b.time_length) FROM dss_dataapi_config a\n" +
            "LEFT JOIN dss_dataapi_call b ON a.id = b.api_id\n" +
            "WHERE a.workspace_id = #{callMonitorResquest.workspaceId}\n" +
            "   AND (b.time_start BETWEEN #{callMonitorResquest.beginTime} AND #{callMonitorResquest.endTime})")
    long getCallTotalTime(@Param("callMonitorResquest") CallMonitorResquest callMonitorResquest);  //总调用时长


    /**
     * 调用量排行TOP10
     */
    @Select("SELECT a.id, a.api_name, COUNT(b.id) total_cnt, SUM(b.time_length) total_time, ROUND(AVG(b.time_length),0) avg_time\n" +
            "FROM dss_dataapi_config a\n" +
            "LEFT JOIN dss_dataapi_call b ON a.id = b.api_id\n" +
            "WHERE a.workspace_id = #{callMonitorResquest.workspaceId}\n" +
            "   AND (b.time_start BETWEEN #{callMonitorResquest.beginTime} AND #{callMonitorResquest.endTime})\n" +
            "GROUP BY a.id\n" +
            "ORDER BY COUNT(b.id) DESC\n" +
            "LIMIT 10")
    List<ApiCallInfoByCnt> getCallListByCnt(@Param("callMonitorResquest") CallMonitorResquest callMonitorResquest);

    /**
     * 出错排行TOP10
     */
    @Select("SELECT a.id, a.api_name, COUNT(b.id) total_cnt, SUM(CASE WHEN b.status =2 THEN 1 ELSE 0 END) fail_cnt, SUM(CASE WHEN b.status =2 THEN 1 ELSE 0 END) / COUNT(b.id) fail_rate\n" +
            "FROM dss_dataapi_config a\n" +
            "LEFT JOIN dss_dataapi_call b ON a.id = b.api_id\n" +
            "WHERE a.workspace_id = #{callMonitorResquest.workspaceId}\n" +
            "   AND (b.time_start BETWEEN #{callMonitorResquest.beginTime} AND #{callMonitorResquest.endTime})\n" +
            "GROUP BY a.id\n" +
            "ORDER BY fail_rate DESC\n" +
            "LIMIT 10")
    List<ApiCallInfoByFailRate> getCallListByFailRate(@Param("callMonitorResquest") CallMonitorResquest callMonitorResquest);


}
