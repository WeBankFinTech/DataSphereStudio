package com.webank.wedatasphere.dss.framework.admin.service.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.webank.wedatasphere.dss.framework.admin.conf.AdminConf;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssExchangeTask;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssExchangeTaskRes;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssExchangisProject;

import com.webank.wedatasphere.dss.framework.admin.util.OkHttpHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: Han Tang
 * @Date: 2022/1/18-01-18-15:10
 */
@Service
public class DssExchangeService {

    private static final String PROJECT_TREE_PATH = "/api/v1/project/tree";
    private static final String TASK_TREE_PATH = "/api/v1/jobinfo/pageList";
    private static final Logger LOGGER = LoggerFactory.getLogger(OkHttpHelper.class);

    public List<DssExchangisProject> queryExchangeProject(String userName) throws Exception {
        String url = AdminConf.EXCHANGE_URL.getValue() + PROJECT_TREE_PATH + "/"+userName;
        Request getRequest = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", AdminConf.EXCHANGE_ADMIN_COOKIE.getValue())
                .build();
        Response response = OkHttpHelper.syncGet(getRequest);
        JsonObject returnData = new JsonParser().parse(response.body().string()).getAsJsonObject();
        LOGGER.info(returnData.toString());
        JsonArray jsonArray = returnData.getAsJsonArray("data");
        List<DssExchangisProject> projectList = new ArrayList<>();
        List<DssExchangisProject> dssExchangisProjects = getExchangeProjectList(projectList, jsonArray);
        return dssExchangisProjects;
    }

    public DssExchangeTaskRes queryExchangeTask(int projectId, String userName, int pageNum, String fullName) throws Exception {
        String url = AdminConf.EXCHANGE_URL.getValue() + TASK_TREE_PATH + "/" + userName +
                "?projectId=" + projectId + "&page=" + pageNum + "&pageSize=10&fuzzyName=&jobId=";
        Request getRequest = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .addHeader("Cookie", AdminConf.EXCHANGE_ADMIN_COOKIE.getValue())
                .get()
                .build();
        Response response = OkHttpHelper.syncGet(getRequest);
        JsonObject returnData = new JsonParser().parse(response.body().string()).getAsJsonObject();
        LOGGER.info(returnData.toString());
        DssExchangeTaskRes dssExchangeTaskRes = new DssExchangeTaskRes();
        JsonObject resJsonObject = returnData.getAsJsonObject("data");
        JsonArray resJsonArray = null;
        if (resJsonObject != null) {
            resJsonArray = resJsonObject.getAsJsonArray("data");
        }

        dssExchangeTaskRes.setPage(resJsonObject.get("page").getAsInt());
        dssExchangeTaskRes.setPageSize(resJsonObject.get("pageSize").getAsInt());
        dssExchangeTaskRes.setTotalPages(resJsonObject.get("totalPages").getAsInt());
        dssExchangeTaskRes.setTotalItems(resJsonObject.get("totalItems").getAsInt());
        List<DssExchangeTask> taskList = new ArrayList<>();
        if (resJsonArray != null) {
            for (JsonElement jsonElement : resJsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                DssExchangeTask dssExchangeTask = new DssExchangeTask();
                dssExchangeTask.setId(jsonObject.get("id").getAsInt());
                dssExchangeTask.setJobName(jsonObject.get("jobName") == null ? null : jsonObject.get("jobName").getAsString());
                dssExchangeTask.setJobCorn(jsonObject.get("jobCorn") == null ? null : jsonObject.get("jobCorn").getAsString());
                dssExchangeTask.setJobDesc(jsonObject.get("jobDesc") == null ? null : jsonObject.get("jobDesc").getAsString());
                dssExchangeTask.setCreateTime(jsonObject.get("createTime") == null ? null : jsonObject.get("createTime").getAsString());
                dssExchangeTask.setJobStatus(jsonObject.get("jobStatus") == null ? null : jsonObject.get("jobStatus").getAsString());
                taskList.add(dssExchangeTask);
            }
        }

        dssExchangeTaskRes.setDssExchangeTaskList(taskList);
        return dssExchangeTaskRes;
    }


    public String getSellScript(int taskId, int projectId) {
        String shellScript = "str=`curl -X GET --data '{\"project_id\":" + projectId + ",\"task_id\":" + taskId + "}' " +
                "--header 'Content-Type: application/json' --header 'Accept: application/json' " +
                "--header 'Cookie:" + AdminConf.EXCHANGE_ADMIN_COOKIE.getValue() + "' " + AdminConf.EXCHANGE_URL.getValue() +
                "/api/v1/jobinfo/runTask/" + taskId + "?userName=admin`;if [[ ${str} =~ 'job execution successed' ]];then exit 0;else exit 1;fi";
        return shellScript;
    }

    public List<DssExchangisProject> getExchangeProjectList(List<DssExchangisProject> projectList, JsonArray jsonArray) {
        if (jsonArray != null) {
            for (JsonElement jsonElement : jsonArray) {
                DssExchangisProject dssExchangisProject = new DssExchangisProject();
                dssExchangisProject.setProjectName(jsonElement.getAsJsonObject().get("projectName") == null ? null : jsonElement.getAsJsonObject().get("projectName").getAsString());
                dssExchangisProject.setId(jsonElement.getAsJsonObject().get("id") == null ? null : jsonElement.getAsJsonObject().get("id").getAsInt());
                projectList.add(dssExchangisProject);
                JsonArray childJsonArray = jsonElement.getAsJsonObject().getAsJsonArray("children");
                if (childJsonArray != null && childJsonArray.size() > 0) {
                    getExchangeProjectList(projectList, childJsonArray);
                }
            }
        }
        return projectList;
    }
}
