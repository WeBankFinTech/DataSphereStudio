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

package com.webank.wedatasphere.dss.apiservice.core.execute;

import com.webank.wedatasphere.dss.apiservice.core.action.ApiServiceGetAction;
import com.webank.wedatasphere.dss.apiservice.core.action.ResultSetDownloadAction;
import com.webank.wedatasphere.dss.apiservice.core.action.ResultWorkspaceIds;
import com.webank.wedatasphere.dss.apiservice.core.config.ApiServiceConfiguration;
import com.webank.wedatasphere.dss.apiservice.core.exception.ApiExecuteException;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.ujes.client.UJESClient;
import org.apache.linkis.ujes.client.request.ResultSetAction;
import org.apache.linkis.ujes.client.request.ResultSetListAction;
import org.apache.linkis.ujes.client.response.JobExecuteResult;
import org.apache.linkis.ujes.client.response.JobInfoResult;
import org.apache.linkis.ujes.client.response.JobLogResult;
import org.apache.linkis.ujes.client.response.ResultSetListResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ExecuteCodeHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExecuteCodeHelper.class);

    private static final String RELEASE_SCALA_TEMPLATE  = "import org.apache.spark.sql.DataFrame\n" +
            "val sql = %s\n" +
            "val df = sqlContext.sql(sql)\n" +
            "show(df)\n";


    private static final String SCALA_MARK = "\"\"\"";

    private static final String EXECUTE_SCALA_TEMPLATE = "import org.apache.spark.sql.DataFrame\n" +
            "val executeCode = %s\n" +
            "val df = sqlContext.sql(executeCode)\n" +
            "show(df)\n";



    public static String packageCodeToRelease(String executeCode){
        String retStr = String.format(RELEASE_SCALA_TEMPLATE, SCALA_MARK + executeCode + SCALA_MARK);
        LOGGER.info("release scala code is {}", retStr);
        return retStr;
    }

    public static String packageCodeToExecute(String executeCode, String metaDataInfo){
        String retStr = String.format(EXECUTE_SCALA_TEMPLATE, SCALA_MARK + executeCode + SCALA_MARK);
        LOGGER.info("execute scala code is {}", retStr);
        return retStr;
    }


    public static Map<String,Object>  getMetaDataInfoByExecute(String user,
                                             String executeCode,
                                             Map<String, Object> params,
                                             String scriptPath) throws Exception {
        Map<String,Object>  resultMap = new HashMap<>();
        UJESClient client = LinkisJobSubmit.getClient();
        ApiServiceExecuteJob job = new DefaultApiServiceJob();
        //sql代码封装成scala执行
        job.setCode(ExecuteCodeHelper.packageCodeToRelease(executeCode));
        job.setEngineType("spark");
        job.setRunType("scala");
        job.setUser(user);
        job.setParams(null);
        // pattern注入
        job.setRuntimeParams((Map<String,Object>) params.get("variable"));
        job.setScriptePath(scriptPath);
        JobExecuteResult jobExecuteResult = LinkisJobSubmit.execute(job, client, "IDE");
        job.setJobExecuteResult(jobExecuteResult);
        try {
            waitForComplete(job, client);
        } catch (Exception e) {
            LOGGER.warn("Failed to execute job", e);
            String reason = getLog(job, client);
            LOGGER.error("Reason for failure: " + reason);
            throw new ApiExecuteException(800024,"获取库表信息失败，执行脚本出错！");
        }
        int resultSize = getResultSize(job, client);
        for(int i =0; i < resultSize; i++){
            String result = getResult(job, i, ApiServiceConfiguration.RESULT_PRINT_SIZE.getValue(),client);
            LOGGER.info("The content of the " + (i + 1) + "th resultset is :"
                    +  result);
            resultMap.put(Integer.toString(i),result);

        }

        LOGGER.info("Finished to execute job");
        return  resultMap;
    }


    public static  void waitForComplete(ApiServiceExecuteJob job, UJESClient client) throws Exception {
        JobInfoResult jobInfo = client.getJobInfo(job.getJobExecuteResult());
        while (!jobInfo.isCompleted()) {
            LOGGER.info("Update Progress info:" + getProgress(job, client));
            LOGGER.info("<----linkis log ---->");
            Utils.sleepQuietly(ApiServiceConfiguration.LINKIS_JOB_REQUEST_STATUS_TIME.getValue(job.getJobProps()));
            jobInfo = client.getJobInfo(job.getJobExecuteResult());
        }
        if (!jobInfo.isSucceed()) {
            throw new ApiExecuteException(90101, "Failed to execute Job: " + jobInfo.getTask().get("errDesc").toString());
        }
    }


    public static void cancel(ApiServiceExecuteJob job,UJESClient client) {
        client.kill(job.getJobExecuteResult());
    }


    public static  double getProgress(ApiServiceExecuteJob job,UJESClient client) {
        return client.progress(job.getJobExecuteResult()).getProgress();
    }


    public static  Boolean isCompleted(ApiServiceExecuteJob job,UJESClient client) {
        return client.getJobInfo(job.getJobExecuteResult()).isCompleted();
    }

    public static  String getResult(ApiServiceExecuteJob job, int index, int maxSize, UJESClient client) {
        String resultContent = null;
        JobInfoResult jobInfo = client.getJobInfo(job.getJobExecuteResult());
        String[] resultSetList = jobInfo.getResultSetList(client);
        if (resultSetList != null && resultSetList.length > 0) {
            Object fileContent = client.resultSet(ResultSetAction.builder()
                    .setPath(resultSetList[index])
                    .setUser(job.getJobExecuteResult().getUser())
                    .setPageSize(maxSize).build()).getFileContent();
            if (fileContent instanceof ArrayList) {
                ArrayList<ArrayList<String>> resultSetRow = (ArrayList<ArrayList<String>>) fileContent;
                resultContent = StringUtils.join(resultSetRow.get(0), "\n");
            } else {
                resultContent = fileContent.toString();
            }
        }
        return resultContent;
    }


    public static  int getResultSize(ApiServiceExecuteJob job, UJESClient client) {
        JobInfoResult jobInfo = client.getJobInfo(job.getJobExecuteResult());
        if (jobInfo.isSucceed()) {
            String[] resultSetList = jobInfo.getResultSetList(client);
            if (resultSetList != null && resultSetList.length > 0) {
                return resultSetList.length;
            }
        }
        return 0;
    }


    public static String getLog(ApiServiceExecuteJob job, UJESClient client) {

        JobLogResult jobLogResult = client
                .log(job.getJobExecuteResult(),
                        0,
                         50);

        ArrayList<String> logArray = jobLogResult.getLog();

        if (logArray != null && logArray.size()
                >= ApiServiceConfiguration.LOG_ARRAY_LEN.getValue()
                && StringUtils.isNotEmpty(logArray.get(3))) {
            return logArray.get(3);
        }
        return null;
    }

    public static  String getResultList(JobExecuteResult executeResult,UJESClient client, String path) {
        ResultSetListResult resultList = (ResultSetListResult) client.executeUJESJob(ResultSetListAction.builder()
                .setUser(executeResult.getUser()).setPath(path).build());
        return resultList.getResponseBody();
    }



    public static  String getResultContent(String user, String path, int maxSize, UJESClient client, boolean enableLimit) {
        return client.resultSet(ResultSetAction.builder()
                    .setPath(path)
                    .setUser(user)
                    .setEnableLimit(enableLimit)
                    .setPageSize(maxSize).build()).getResponseBody();
    }

    public static InputStream downloadResultSet(String user,
                                                String path,
                                                String charset,
                                                String outputFileType,
                                                String csvSeperator,
                                                String outputFileName,
                                                String sheetName,
                                                String nullValue,
                                                UJESClient client) {

        ResultSetDownloadAction resultSetDownloadAction = new ResultSetDownloadAction();
        resultSetDownloadAction.setUser(user);
        resultSetDownloadAction.setParameter("path",path);
        resultSetDownloadAction.setParameter("charset",charset);
        resultSetDownloadAction.setParameter("outputFileType",outputFileType);
        resultSetDownloadAction.setParameter("csvSeperator",csvSeperator);
        resultSetDownloadAction.setParameter("outputFileName",outputFileName);
        resultSetDownloadAction.setParameter("sheetName",sheetName);
        resultSetDownloadAction.setParameter("nullValue",nullValue);
        client.executeUJESJob(resultSetDownloadAction);
        return resultSetDownloadAction.getInputStream();
    }







    public static  Map<String, Object> getTaskInfoById(JobExecuteResult jobExecuteResult, UJESClient client) {
        return (Map<String, Object>) client.getJobInfo(jobExecuteResult).getTask();
    }




    public static String getUserWorkspaceIds(String userName,UJESClient client){
        ApiServiceGetAction apiServiceGetAction = new ApiServiceGetAction();
        apiServiceGetAction.setUser(userName);
        apiServiceGetAction.setParameter("userName",userName);
        ResultWorkspaceIds userWorkspaceIds = (ResultWorkspaceIds)client.executeUJESJob(apiServiceGetAction);
        return userWorkspaceIds.getUserWorkspaceIds();

    }








}
