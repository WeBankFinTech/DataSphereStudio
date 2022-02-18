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

package com.webank.wedatasphere.dss.linkis.node.execution.execution.impl;

import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.exception.LinkisJobExecutionErrorException;
import com.webank.wedatasphere.dss.linkis.node.execution.execution.LinkisNodeExecution;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.listener.LinkisExecutionListener;
import com.webank.wedatasphere.dss.linkis.node.execution.parser.CodeParser;
import com.webank.wedatasphere.dss.linkis.node.execution.parser.JobParamsParser;
import com.webank.wedatasphere.dss.linkis.node.execution.parser.JobParser;
import com.webank.wedatasphere.dss.linkis.node.execution.parser.JobRuntimeParamsParser;
import com.webank.wedatasphere.dss.linkis.node.execution.service.LinkisURLService;
import com.webank.wedatasphere.dss.linkis.node.execution.service.impl.BuildJobActionImpl;
import com.webank.wedatasphere.dss.linkis.node.execution.utils.JsonUtil;
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisJobExecutionUtils;
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisUjesClientUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.exception.LinkisException;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.ujes.client.UJESClient;
import org.apache.linkis.ujes.client.request.JobExecuteAction;
import org.apache.linkis.ujes.client.request.JobSubmitAction;
import org.apache.linkis.ujes.client.request.OpenLogAction;
import org.apache.linkis.ujes.client.request.ResultSetAction;
import org.apache.linkis.ujes.client.response.JobInfoResult;
import org.apache.linkis.ujes.client.response.JobLogResult;
import org.apache.linkis.ujes.client.response.OpenLogResult;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkisNodeExecutionImpl implements LinkisNodeExecution , LinkisExecutionListener {

    private static LinkisNodeExecution linkisExecution = new LinkisNodeExecutionImpl();

    private LinkisNodeExecutionImpl() {
        registerJobParser(new CodeParser());
        registerJobParser(new JobRuntimeParamsParser());
        registerJobParser(new JobParamsParser());
    }

    public static LinkisNodeExecution getLinkisNodeExecution() {
        return linkisExecution;
    }

    private final Map<String, UJESClient> clientMap = new HashMap<>();

    private ArrayList<JobParser> jobParsers = new ArrayList<>();

    protected UJESClient getClient(Job job) {
        Map<String, String> props =job.getJobProps();
        String linkisUrl = LinkisURLService.Factory.getLinkisURLService().getLinkisURL(job);
        if(clientMap.containsKey(linkisUrl)) {
            return clientMap.get(linkisUrl);
        }
        synchronized (clientMap) {
            if(!clientMap.containsKey(linkisUrl)) {
                UJESClient client = LinkisUjesClientUtils.getUJESClient(
                    linkisUrl,
                    LinkisJobExecutionConfiguration.LINKIS_ADMIN_USER.getValue(props),
                    LinkisJobExecutionConfiguration.LINKIS_AUTHOR_USER_TOKEN.getValue(props),
                    props);
                clientMap.put(linkisUrl, client);
                job.getLogObj().info("Create a new Linkis client by " + linkisUrl);
                return client;
            }
        }
        return clientMap.get(linkisUrl);
    }

    public void registerJobParser(JobParser jobParser) {
        this.jobParsers.add(jobParser);
    }

    @Override
    public void runJob(Job job) throws Exception {
        // job parser
        for(JobParser parser : jobParsers){
            parser.parseJob(job);
        }
        Map<String, String> props =job.getJobProps();
        //用户传入的参数优先级最高 by suyc
        String userParams =props.get("params");
        if(userParams != null){
            job.getLogObj().info("************************************PARAMS FROM USER, Higher Priority************************************");
            job.getLogObj().info(userParams);
            job.getLogObj().info("************************************PARAMS FROM USER, Higher Priority************************************");
            job.getLogObj().info("************************************PARAMS FROM USER, Start to  put variable and configuration");
            job.setParams(JsonUtil.toMap(userParams));
            job.getLogObj().info("************************************PARAMS FROM USER, Finished to  put variable and configuration");
        }
        if(LinkisJobExecutionConfiguration.isLinkis1_X(props)) {
            JobSubmitAction submitAction = BuildJobActionImpl.getbuildJobAction().getSubmitAction(job);

            job.setJobExecuteResult(getClient(job).submit(submitAction));
        }else{
            //兼容0.X版本的任务提交方式
            JobExecuteAction jobAction = BuildJobActionImpl.getbuildJobAction().getJobAction(job);
            job.setJobExecuteResult(getClient(job).execute(jobAction));
        }

        job.getLogObj().info("************************************Start to execute job************************************");
        job.getLogObj().info("************************************Task id is:"+ job.getJobExecuteResult().getTaskID());
        job.getLogObj().info("************************************Exec id is:"+ job.getJobExecuteResult().getExecID());
    }

    @Override
    public String getState(Job job) {
        return getClient(job).getJobInfo(job.getJobExecuteResult()).getJobStatus();
    }

    @Override
    public String getLog(Job job) {

        JobInfoResult jobInfo =getClient(job).getJobInfo(job.getJobExecuteResult());

        List<String> logArray = null;
        //只有job是终态(成功完成or失败完成)，才打印全部日志，否则只打印linkis运行时缓存日志
        //if(jobInfo.isCompleted() && jobInfo.isFailed()){
        if(jobInfo.isCompleted()){
            job.getLogObj().info("************************************任务为已完成/已失败状态");
            try {
                logArray = Arrays.asList(queryPersistedLogAll(job).getLog());
            } catch (LinkisJobExecutionErrorException e) {
                job.getLogObj().error("************************************任务完整日志获取失败，具体异常为: " + e.getMessage());
            }
        }else if(jobInfo.isRunning()){
            job.getLogObj().info("************************************任务为运行中状态");
            JobLogResult jobLogResult = getClient(job)
                    .log(job.getJobExecuteResult(),
                            job.getLogFromLine(),
                            LinkisJobExecutionConfiguration.LOG_SIZE.getValue());

            job.setLogFromLine(jobLogResult.fromLine());
            logArray = jobLogResult.getLog();
        }else {
            job.getLogObj().info("************************************任务为等待/调度中等其它状态，暂无日志输出");
        }

        if (logArray != null && logArray.size()
                >= LinkisJobExecutionConfiguration.LOG_ARRAY_LEN.getValue()
                && StringUtils.isNotEmpty(logArray.get(3))) {
            return logArray.get(3);
        }
        return null;
    }





    public OpenLogResult queryPersistedLogAll(Job job) throws LinkisJobExecutionErrorException {
        String taskID = job.getJobExecuteResult().getTaskID();
        String user = job.getUser();
        JobInfoResult jobInfo = getClient(job).getJobInfo(job.getJobExecuteResult());
        String logPath = jobInfo.getRequestPersistTask().getLogPath();
        int retryCnt = 0;
        final int MAX_RETRY_TIMES = LinkisJobExecutionConfiguration.REQUEST_MAX_RETRY_TIME.getValue();
        OpenLogResult openLogResult = null;

        int backCnt = 0;
        final int MAX_BACK_TIMES = 3;

        while (retryCnt++ < MAX_RETRY_TIMES) {
            try {
                openLogResult = getClient(job).openLog(OpenLogAction.newBuilder().setLogPath(logPath).setProxyUser(user).build());
                job.getLogObj().info("persisted-log-result:" + LinkisJobExecutionUtils.gson.toJson(openLogResult));
                if (openLogResult == null ||
                        0 != openLogResult.getStatus() ||
                        StringUtils.isBlank(openLogResult.getLog()[LinkisJobExecutionUtils.IDX_FOR_LOG_TYPE_ALL])) {
                    String reason;
                    if (openLogResult == null) {
                        reason = "OpenLogResult is null";
                    } else if (0 != openLogResult.getStatus()) {
                        reason = "server returns non-zero status-code";
                    } else {
                        reason = "server returns empty log";
                    }
                    String msg = MessageFormat.format("Get log from openLog failed. retry time : {0}/{1}. taskID={2}. Reason: {3}", retryCnt, MAX_RETRY_TIMES, taskID, reason);
                    job.getLogObj().warn(msg);
                    if (retryCnt >= MAX_RETRY_TIMES) {
                        if (backCnt >= MAX_BACK_TIMES) {
                            msg = MessageFormat.format("Get log from openLog failed. Retry exhausted. taskID={0}, Reason: {1}", job.getJobExecuteResult().getTaskID(), reason);
                            throw new LinkisJobExecutionErrorException(100079,msg);
                        } else {
                            backCnt++;
                            retryCnt = 0;
                            Utils.sleepQuietly(LinkisJobExecutionConfiguration.LINKIS_JOB_REQUEST_STATUS_TIME.getValue(job.getJobProps()));;//wait 10s and try again
                        }
                    }
                } else {
                    break;
                }
            } catch (Exception e) {
                String msg = MessageFormat.format("Get log from openLog failed. retry time : {0}/{1}", retryCnt, MAX_RETRY_TIMES);
                if (e instanceof LinkisException) {
                    msg += " " + e.toString();
                }
                job.getLogObj().warn(msg, e);
                if (retryCnt >= MAX_RETRY_TIMES) {
                    if (backCnt >= MAX_BACK_TIMES) {
                        throw new LinkisJobExecutionErrorException(100080, "Get log from openLog failed. Retry exhausted. taskID=" + taskID);
                    } else {
                        backCnt++;
                        retryCnt = 0;
                        Utils.sleepQuietly(LinkisJobExecutionConfiguration.LINKIS_JOB_REQUEST_STATUS_TIME.getValue(job.getJobProps()));//wait 10s and try again
                    }
                }
            }
            Utils.sleepQuietly(LinkisJobExecutionConfiguration.LINKIS_JOB_REQUEST_STATUS_TIME.getValue(job.getJobProps()));;
        }
        return openLogResult;
    }




    @Override
    public void waitForComplete(Job job) throws Exception {
        JobInfoResult jobInfo = getClient(job).getJobInfo(job.getJobExecuteResult());
        int fetchLogFailCnt = 0;
        //如果第一次请求时，任务已经是成功完成状态了
        if(jobInfo.isSucceed()) {
            printJobLog(job);
        }
        while (!jobInfo.isCompleted()) {
            double progress = -1;
            try{
                progress = this.getProgress(job);
            }catch(Exception e){
                //ignore
            }
            if (progress >= 0){
                job.getLogObj().info("************************************任务运行进度更新: " + progress);
            }
            JobInfoResult oldJobInfo = jobInfo;
            try{
                jobInfo = getClient(job).getJobInfo(job.getJobExecuteResult());
            }catch(Throwable e){
                jobInfo = oldJobInfo;
                fetchLogFailCnt += 1;
                job.getLogObj().error("************************************任务信息获取失败，失败次数合计为: " + fetchLogFailCnt);
                //两分钟内获取不到,就认为不行，因为这个时候我应该是重启完成了
                if (fetchLogFailCnt == 40) {
                    job.getLogObj().info("***********************************任务信息获取失败次数已达40次，应该是linkis不能获取到正常信息，认为任务失败，抛出异常");
                    throw new LinkisJobExecutionErrorException(90101, "Failed to execute Job: " + e.getMessage());
                }
            }
            //UJESClient获取任务log间隔时间，默认3秒钟
            Utils.sleepQuietly(LinkisJobExecutionConfiguration.LINKIS_JOB_REQUEST_STATUS_TIME.getValue(job.getJobProps()));
            printJobLog(job);
        }
        //任务不是成功完成，打印log，并抛出异常
        if (!jobInfo.isSucceed()) {
            printJobLog(job);
            throw new LinkisJobExecutionErrorException(90101, "Failed to execute Job: " + jobInfo.getMessage());
        }
    }

    private void printJobLog(Job job){
        String log = null;
        try{
            log = this.getLog(job);
        }catch(Exception e){
            job.getLogObj().warn("************************************failed to get log info", e);
        }
        if (StringUtils.isEmpty(log)){
            return;
        }
        Arrays.stream(log.split("\n")).forEach(l -> {
            if (l != null) {
                if(l.contains("ERROR")) {
                    job.getLogObj().error("************************************" + l);
                }else{
                    job.getLogObj().info("************************************" + l);
                }
            }
        });

    }

    @Override
    public void cancel(Job job) throws Exception {
        getClient(job).kill(job.getJobExecuteResult());
    }

    @Override
    public double getProgress(Job job) {
        return getClient(job).progress(job.getJobExecuteResult()).getProgress();
    }

    @Override
    public Boolean isCompleted(Job job) {
        return getClient(job).getJobInfo(job.getJobExecuteResult()).isCompleted();
    }

    @Override
    public int getResultSize(Job job) {
        JobInfoResult jobInfo = getClient(job).getJobInfo(job.getJobExecuteResult());

        job.getLogObj().info("JobInfo result location is "+jobInfo.getRequestPersistTask().getResultLocation());

        job.getLogObj().info("JobInfo user location is "+jobInfo.getRequestPersistTask().getUmUser());
        //解决azkaban  执行报 user is need问题
        jobInfo.getRequestPersistTask().setUmUser(job.getUser());
        if (jobInfo.isSucceed()) {
            String[] resultSetList = jobInfo.getResultSetList(getClient(job));
            if (resultSetList != null && resultSetList.length > 0) {
                return resultSetList.length;
            }
        }
        return 0;
    }

    @Override
    public String getResult(Job job, int index, int maxSize) {
        String resultContent = null;
        JobInfoResult jobInfo = getClient(job).getJobInfo(job.getJobExecuteResult());
        String[] resultSetList = jobInfo.getResultSetList(getClient(job));
        if (resultSetList != null && resultSetList.length > 0) {
            Object fileContent = getClient(job).resultSet(ResultSetAction.builder()
                    .setPath(resultSetList[index])
                    .setUser(job.getJobExecuteResult().getUser())
                    .setPageSize(maxSize).build()).getFileContent();
            if (fileContent instanceof ArrayList) {
                ArrayList<ArrayList<String>> resultSetRow = (ArrayList<ArrayList<String>>) fileContent;
                resultContent = StringUtils.join(resultSetRow, "\n");
            } else {
                resultContent = fileContent.toString();
            }
        }
        return resultContent;
    }

    @Override
    public void onStatusChanged(String fromState, String toState, Job job) {
    }

    @Override
    public void close() {
        clientMap.values().forEach(IOUtils::closeQuietly);
    }
}
