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


import com.webank.wedatasphere.dss.apiservice.core.action.ResultSetDownloadAction;
import com.webank.wedatasphere.dss.apiservice.core.config.ApiServiceConfiguration;
import com.webank.wedatasphere.dss.apiservice.core.exception.ApiExecuteException;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.httpclient.response.Result;
import org.apache.linkis.ujes.client.UJESClient;
import org.apache.linkis.ujes.client.request.ResultSetAction;
import org.apache.linkis.ujes.client.request.ResultSetListAction;
import org.apache.linkis.ujes.client.response.JobExecuteResult;
import org.apache.linkis.ujes.client.response.JobInfoResult;
import org.apache.linkis.ujes.client.response.JobLogResult;
import org.apache.linkis.ujes.client.response.ResultSetListResult;
import org.apache.commons.lang.StringUtils;
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


    public static  void waitForComplete(ApiServiceExecuteJob job,UJESClient client) throws Exception {
        JobInfoResult jobInfo = client.getJobInfo(job.getJobExecuteResult());
        while (!jobInfo.isCompleted()) {
            LOGGER.info("Update Progress info:" + getProgress(job,client));
            LOGGER.info("<----linkis log ---->");
            Utils.sleepQuietly(ApiServiceConfiguration.LINKIS_JOB_REQUEST_STATUS_TIME.getValue(job.getJobProps()));
            jobInfo = client.getJobInfo(job.getJobExecuteResult());
        }
        if (!jobInfo.isSucceed()) {
            throw new ApiExecuteException(90101, "Failed to execute Job: " + jobInfo.getTask().get("errDesc").toString());
        }
    }


    public static void cancel(ApiServiceExecuteJob job,UJESClient client) throws Exception {
        client.kill(job.getJobExecuteResult());
    }


    public static  double getProgress(ApiServiceExecuteJob job,UJESClient client) {
        return client.progress(job.getJobExecuteResult()).getProgress();
    }


    public static  Boolean isCompleted(ApiServiceExecuteJob job,UJESClient client) {
        return client.getJobInfo(job.getJobExecuteResult()).isCompleted();
    }

    public static  String getResult(ApiServiceExecuteJob job, int index, int maxSize,UJESClient client) {
        String resultContent = null;
        JobInfoResult jobInfo = client.getJobInfo(job.getJobExecuteResult());
        String[] resultSetList = jobInfo.getResultSetList(LinkisJobSubmit.getClient(job.getJobProps()));
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


    public static  int getResultSize(ApiServiceExecuteJob job,UJESClient client) {
        JobInfoResult jobInfo = client.getJobInfo(job.getJobExecuteResult());
        if (jobInfo.isSucceed()) {
            String[] resultSetList = jobInfo.getResultSetList(LinkisJobSubmit.getClient(job.getJobProps()));
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



    public static  String getResultContent(String user, String path,int page, int maxSize,String charset,UJESClient client) {

        String fileContent = client.resultSet(ResultSetAction.builder()
                .setPath(path)
                .setUser(user)
                .setPage(page)
                .setCharset(charset)
                .setPageSize(maxSize).build()).getResponseBody();

        return fileContent;
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

        Map<String, Object> taskInfo = (Map<String, Object>)client.getJobInfo(jobExecuteResult).getTask();

        return taskInfo;
    }













}
