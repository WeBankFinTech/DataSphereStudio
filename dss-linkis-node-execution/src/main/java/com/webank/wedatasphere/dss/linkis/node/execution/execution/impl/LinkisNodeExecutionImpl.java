/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

import com.webank.wedatasphere.dss.linkis.node.execution.WorkflowContext;
import com.webank.wedatasphere.dss.linkis.node.execution.exception.LinkisJobExecutionErrorException;
import com.webank.wedatasphere.dss.linkis.node.execution.execution.LinkisNodeExecution;
import com.webank.wedatasphere.dss.linkis.node.execution.job.SharedJob;
import com.webank.wedatasphere.dss.linkis.node.execution.job.SignalSharedJob;
import com.webank.wedatasphere.dss.linkis.node.execution.listener.LinkisExecutionListener;
import com.webank.wedatasphere.dss.linkis.node.execution.parser.JobParamsParser;
import com.webank.wedatasphere.dss.linkis.node.execution.parser.JobRuntimeParamsParser;
import com.webank.wedatasphere.dss.linkis.node.execution.service.impl.BuildJobActionImpl;
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.parser.CodeParser;
import com.webank.wedatasphere.dss.linkis.node.execution.parser.JobParser;
import com.webank.wedatasphere.dss.linkis.node.execution.utils.LinkisUjesClientUtils;
import com.webank.wedatasphere.linkis.common.utils.Utils;
import com.webank.wedatasphere.linkis.ujes.client.UJESClient;
import com.webank.wedatasphere.linkis.ujes.client.request.JobExecuteAction;
import com.webank.wedatasphere.linkis.ujes.client.request.ResultSetAction;
import com.webank.wedatasphere.linkis.ujes.client.response.JobInfoResult;
import com.webank.wedatasphere.linkis.ujes.client.response.JobLogResult;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by peacewong on 2019/11/2.
 */
public class LinkisNodeExecutionImpl implements LinkisNodeExecution , LinkisExecutionListener {

    private static LinkisNodeExecution linkisExecution = new LinkisNodeExecutionImpl();

    private LinkisNodeExecutionImpl() {
        registerJobParser(new CodeParser());
        registerJobParser(new JobParamsParser());
        registerJobParser(new JobRuntimeParamsParser());
    }


    public static LinkisNodeExecution getLinkisNodeExecution() {
        return linkisExecution;
    }

    private UJESClient client;

    private ArrayList<JobParser> jobParsers = new ArrayList<>();

    public UJESClient getClient(Map<String, String> props) {
        if (client == null) {
            synchronized (LinkisNodeExecution.class) {
                if (client == null) {
                    client = LinkisUjesClientUtils.getUJESClient(
                            LinkisJobExecutionConfiguration.LINKIS_URL.getValue(props),
                            LinkisJobExecutionConfiguration.LINKIS_ADMIN_USER.getValue(props),
                            LinkisJobExecutionConfiguration.LINKIS_AUTHOR_USER_TOKEN.getValue(props),
                            props);
                }
            }
        }
        return client;
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
        JobExecuteAction jobAction = BuildJobActionImpl.getbuildJobAction().getJobAction(job);
        job.setJobExecuteResult(getClient(job.getJobProps()).execute(jobAction));
        job.getLogObj().info("<---------------Start to execute job--------------->");
    }

    @Override
    public String getState(Job job) {
        return getClient(job.getJobProps()).getJobInfo(job.getJobExecuteResult()).getJobStatus();
    }

    @Override
    public String getLog(Job job) {

        JobLogResult jobLogResult = getClient(job.getJobProps())
                .log(job.getJobExecuteResult(),
                        job.getLogFromLine(),
                        LinkisJobExecutionConfiguration.LOG_SIZE.getValue());

        job.setLogFromLint(jobLogResult.fromLine());

        ArrayList<String> logArray = jobLogResult.getLog();

        if (logArray != null && logArray.size()
                >= LinkisJobExecutionConfiguration.LOG_ARRAY_LEN.getValue()
                && StringUtils.isNotEmpty(logArray.get(3))) {
            return logArray.get(3);
        }
        return null;
    }

    @Override
    public void waitForComplete(Job job) throws Exception {
        JobInfoResult jobInfo = getClient(job.getJobProps()).getJobInfo(job.getJobExecuteResult());
        while (!jobInfo.isCompleted()) {
            job.getLogObj().info("Update Progress info:" + this.getProgress(job));
            job.getLogObj().info("<----linkis log ---->");
            String log = this.getLog(job);
            if (log != null) {
                job.getLogObj().info(log);
            }
            job.getLogObj().info("<----linkis log ---->");
            Utils.sleepQuietly(LinkisJobExecutionConfiguration.LINKIS_JOB_REQUEST_STATUS_TIME.getValue(job.getJobProps()));
            jobInfo = getClient(job.getJobProps()).getJobInfo(job.getJobExecuteResult());
        }
        if (!jobInfo.isSucceed()) {
            throw new LinkisJobExecutionErrorException(90101, "Failed to execute Job: " + jobInfo.getMessage());
        }
    }

    @Override
    public void cancel(Job job) throws Exception {
        getClient(job.getJobProps()).kill(job.getJobExecuteResult());
    }

    @Override
    public double getProgress(Job job) {
        return getClient(job.getJobProps()).progress(job.getJobExecuteResult()).getProgress();
    }

    @Override
    public Boolean isCompleted(Job job) {
        return getClient(job.getJobProps()).getJobInfo(job.getJobExecuteResult()).isCompleted();
    }

    @Override
    public int getResultSize(Job job) {
        JobInfoResult jobInfo = getClient(job.getJobProps()).getJobInfo(job.getJobExecuteResult());
        if (jobInfo.isSucceed()) {
            String[] resultSetList = jobInfo.getResultSetList(getClient(job.getJobProps()));
            if (resultSetList != null && resultSetList.length > 0) {
                return resultSetList.length;
            }
        }
        return 0;
    }

    @Override
    public String getResult(Job job, int index, int maxSize) {
        String resultContent = null;
        JobInfoResult jobInfo = getClient(job.getJobProps()).getJobInfo(job.getJobExecuteResult());
        String[] resultSetList = jobInfo.getResultSetList(getClient(job.getJobProps()));
        if (resultSetList != null && resultSetList.length > 0) {
            Object fileContent = client.resultSet(ResultSetAction.builder()
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
        if (LinkisJobExecutionConfiguration.SUCCEED.equalsIgnoreCase(toState)){
            if (job instanceof SignalSharedJob){
                SignalSharedJob signalSharedJob = (SignalSharedJob) job;
                String result = getResult(job, 0, -1);
                String msgSaveKey = signalSharedJob.getMsgSaveKey();
                String key = SignalSharedJob.PREFIX ;
                if (StringUtils.isNotEmpty(msgSaveKey)){
                    key = key + msgSaveKey;
                }
                WorkflowContext.getAppJointContext().setValue(key, result , -1);
            } else if(job instanceof SharedJob){
                String taskId = job.getJobExecuteResult().getTaskID();
                job.getLogObj().info("Set shared info:" + taskId);
                SharedJob sharedJob = ((SharedJob)job);
                WorkflowContext.getAppJointContext().setValue(sharedJob.getSharedKey(), taskId , sharedJob.getSharedNum());
            }

        }
    }
}
