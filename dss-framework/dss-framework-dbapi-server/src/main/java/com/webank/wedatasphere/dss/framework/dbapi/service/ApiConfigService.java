package com.webank.wedatasphere.dss.framework.dbapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.google.gson.JsonObject;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiConfig;
import com.webank.wedatasphere.dss.framework.dbapi.entity.ApiGroup;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiExecuteInfo;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiGroupInfo;
import com.webank.wedatasphere.dss.framework.dbapi.entity.response.ApiListInfo;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface ApiConfigService extends IService<ApiConfig> {

    public  Boolean release(Integer status,String apiId);

    void addGroup(ApiGroup apiGroup);

    List<ApiGroupInfo> getGroupList(String workspaceId);

    void saveApi(ApiConfig apiConfig) throws JSONException;

    ApiExecuteInfo apiTest(String path, HttpServletRequest httpRequest) throws  Exception;
}
