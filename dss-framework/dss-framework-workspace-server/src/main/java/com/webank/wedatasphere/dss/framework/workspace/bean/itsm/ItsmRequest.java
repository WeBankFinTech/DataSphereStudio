package com.webank.wedatasphere.dss.framework.workspace.bean.itsm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ItsmRequest {

    private static final String dataKey = "dataList";

    private String createDate;
    private String createUser;
    private String data;
    private String externalId;
    private String flowId;
    private String operateUser;
    private String requestTitle;
    private String style;
    private String taskId;


    // getters和setters
    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public String getRequestTitle() {
        return requestTitle;
    }

    public void setRequestTitle(String requestTitle) {
        this.requestTitle = requestTitle;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<Map<String,String>> getDataList(){
        String dataStr = getData();
        if(!StringUtils.isEmpty(dataStr))
        {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(dataStr, JsonObject.class);
            JsonArray dataList = jsonObject.getAsJsonArray("dataList");

            Type listType = new TypeToken<List<Map<String, String>>>(){}.getType();
            return  gson.fromJson(dataList.toString(), listType);
        }
        return Collections.emptyList();
    }

}
