package com.webank.wedatasphere.dss.framework.dbapi.dao;

import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiCall;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

/**
 * @Classname ApiCallMapper
 * @Description TODO
 * @Date 2021/7/20 13:49
 * @Created by suyc
 */
public interface ApiCallMapper {
    @Insert("insert into dss_dataapi_call(api_id, params_value, status, time_start, time_end, time_length, caller) " +
            "values(#{apiCall.apiId}, #{apiCall.paramsValue}, #{apiCall.status}, #{apiCall.timeStart}, #{apiCall.timeEnd}, #{apiCall.timeLength}, #{apiCall.caller})")
    @Options(useGeneratedKeys = true, keyProperty = "apiCall.id", keyColumn = "id")
    int addApiCall(@Param("apiCall") ApiCall apiCall);
}
