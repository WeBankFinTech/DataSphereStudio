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

package com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype;


import azkaban.jobExecutor.AbstractJob;
import azkaban.utils.Props;
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.execution.impl.LinkisNodeExecutionImpl;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.job.JobTypeEnum;
import com.webank.wedatasphere.dss.linkis.node.execution.job.LinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.listener.LinkisExecutionListener;
import com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.conf.LinkisJobTypeConf;
import com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.job.JobBuilder;
import com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.log.AzkabanJobLog;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class AzkabanDssJobType extends AbstractJob {



    private static final String SENSITIVE_JOB_PROP_NAME_SUFFIX = "_X";
    private static final String SENSITIVE_JOB_PROP_VALUE_PLACEHOLDER = "[MASKED]";
    private static final String JOB_DUMP_PROPERTIES_IN_LOG = "job.dump.properties";



    private final Logger log;

    protected volatile Props jobProps;

    protected volatile Props sysProps;

    protected volatile Map<String, String> jobPropsMap;

    private final String type;

    private Job job;

    private boolean isCanceled = false;




    public AzkabanDssJobType(String jobId, Props sysProps, Props jobProps, Logger log) {


        super(jobId, log);

        this.jobProps = jobProps;

        this.sysProps = sysProps;

        this.jobPropsMap = this.jobProps.getMapByPrefix("");

        this.log = log;
        this.type = jobProps.getString(JOB_TYPE, LinkisJobExecutionConfiguration.JOB_DEFAULT_TYPE.getValue(this.jobPropsMap));
        if(!LinkisJobExecutionConfiguration.JOB_DEFAULT_TYPE.getValue(this.jobPropsMap).equalsIgnoreCase(this.type) ){
            throw new RuntimeException("This job(" + this.type + " )is not linkis type");
        }

    }


    @Override
    public void run() throws Exception {

        info("Start to execute job");
        logJobProperties();
        String runDate = getRunDate();
        if (StringUtils.isNotBlank(runDate)) {
            this.jobPropsMap.put("run_date", runDate);
        }
        String runTodayH = getRunTodayh(false);
        if (StringUtils.isNotBlank(runTodayH)) {
            this.jobPropsMap.put("run_today_h", runTodayH);
        }
        this.job = JobBuilder.getAzkanbanBuilder().setJobProps(this.jobPropsMap).build();
        this.job.setLogObj(new AzkabanJobLog(this));
        if(JobTypeEnum.EmptyJob == ((LinkisJob)this.job).getJobType()){
            warn("This node is empty type");
            return;
        }
       // info("runtimeMap is " + job.getRuntimeParams());
        //job.getRuntimeParams().put("workspace", getWorkspace(job.getUser()));
        info("runtimeMap is " + job.getRuntimeParams());
        LinkisNodeExecutionImpl.getLinkisNodeExecution().runJob(this.job);

        try {
            LinkisNodeExecutionImpl.getLinkisNodeExecution().waitForComplete(this.job);
        } catch (Exception e) {
            warn("Failed to execute job", e);
            //String reason = LinkisNodeExecutionImpl.getLinkisNodeExecution().getLog(this.job);
            //this.log.error("Reason for failure: " + reason);
            throw e;
        }
        try {
            String endLog = LinkisNodeExecutionImpl.getLinkisNodeExecution().getLog(this.job);
            info(endLog);
        } catch (Throwable e){
            info("Failed to get log", e);
        }

        LinkisExecutionListener listener = (LinkisExecutionListener)LinkisNodeExecutionImpl.getLinkisNodeExecution();
        listener.onStatusChanged(null,  LinkisNodeExecutionImpl.getLinkisNodeExecution().getState(this.job),this.job);
        int resultSize =  0;
        try{
            resultSize = LinkisNodeExecutionImpl.getLinkisNodeExecution().getResultSize(this.job);
        }catch(final Throwable t){
            error("failed to get result size");
            resultSize = -1;
        }
        for (int i = 0; i < resultSize; i++) {
            String result = LinkisNodeExecutionImpl.getLinkisNodeExecution().getResult(this.job, i, LinkisJobExecutionConfiguration.RESULT_PRINT_SIZE.getValue(this.jobPropsMap));
            if (result.length() > LinkisJobTypeConf.LOG_MAX_RESULTSIZE.getValue()) {
                result = result.substring(0, LinkisJobTypeConf.LOG_MAX_RESULTSIZE.getValue());
            }
            info("The content of the " + (i + 1) + "th resultset is :" + result);
        }

        info("Finished to execute job");
    }

    @Override
    public void cancel() throws Exception {
        //super.cancel();
        LinkisNodeExecutionImpl.getLinkisNodeExecution().cancel(this.job);
        isCanceled = true;
        warn("This job has been canceled");
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

    @Override
    public double getProgress() throws Exception {
        return   LinkisNodeExecutionImpl.getLinkisNodeExecution().getProgress(this.job);
    }

    /**
     * prints the current Job props to the Job log.
     */
    private void logJobProperties() {
        if (this.jobProps != null &&
                this.jobProps.getBoolean(JOB_DUMP_PROPERTIES_IN_LOG, true)) {
            try {
                this.info("******   Job properties   ******");
                this.info(String.format("- Note : value is masked if property name ends with '%s'.",
                        SENSITIVE_JOB_PROP_NAME_SUFFIX));
                for (final Map.Entry<String, String> entry : this.jobPropsMap.entrySet()) {
                    final String key = entry.getKey();
                    final String value = key.endsWith(SENSITIVE_JOB_PROP_NAME_SUFFIX) ?
                            SENSITIVE_JOB_PROP_VALUE_PLACEHOLDER :
                            entry.getValue();
                    this.info(String.format("%s=%s", key, value));
                }
                this.info("****** End Job properties  ******");
            } catch (final Exception ex) {
                this.log.error("failed to log job properties ", ex);
            }
        }
    }

    private String getRunDate(){
        this.info("begin to get run date");
        if (this.jobProps != null &&
                this.jobProps.getBoolean(JOB_DUMP_PROPERTIES_IN_LOG, true)) {
            try {
                for (final Map.Entry<String, String> entry : this.jobPropsMap.entrySet()) {
                    final String key = entry.getKey();
                    final String value = key.endsWith(SENSITIVE_JOB_PROP_NAME_SUFFIX) ?
                            SENSITIVE_JOB_PROP_VALUE_PLACEHOLDER :
                            entry.getValue();
                    if ("azkaban.flow.start.timestamp".equals(key)){
                        this.info("run time is " + value);
                        String runDateNow = value.substring(0, 10).replaceAll("-", "");
                        this.info("run date now is " + runDateNow);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                        try {
                            Date date = simpleDateFormat.parse(runDateNow);
                            //因为date已经当天的00:00:00 减掉12小时 就是昨天的时间
                            String runDate = simpleDateFormat.format(new Date(date.getTime() - 24 * 60 * 60 * 1000));
                            this.info("runDate is " + runDate);
                            return runDate;
                        } catch (ParseException e) {
                            this.log.error("failed to parse run date " + runDateNow, e);
                        }
                    }
                }
            } catch (final Exception ex) {
                this.log.error("failed to get run date ", ex);
            }
        }
        return null;
    }

    private String getRunTodayh(boolean stdFormat) {
        this.info("begin to get run_today_h");
        if (this.jobProps != null &&
                this.jobProps.getBoolean(JOB_DUMP_PROPERTIES_IN_LOG, true)) {
            try {
                for (final Map.Entry<String, String> entry : this.jobPropsMap.entrySet()) {
                    final String key = entry.getKey();
                    final String value = key.endsWith(SENSITIVE_JOB_PROP_NAME_SUFFIX) ?
                            SENSITIVE_JOB_PROP_VALUE_PLACEHOLDER :
                            entry.getValue();
                    if ("azkaban.flow.start.timestamp".equals(key)) {
                        this.info("run time is " + value);
                        String runTodayh = value.substring(0, 13).replaceAll("-", "").replaceAll("T", "");
                        this.info("run today h is " + runTodayh);
                        //for std
//                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH");
                        if(!stdFormat){
                            return runTodayh;
                        }
                    }
                }
            } catch (final Exception ex) {
                this.log.error("failed to get run_today_h ", ex);
            }
        }
        return null;
    }

}
