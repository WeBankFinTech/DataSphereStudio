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

package com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype;


import azkaban.jobExecutor.AbstractJob;
import azkaban.utils.Props;
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.execution.impl.LinkisNodeExecutionImpl;
import com.webank.wedatasphere.dss.linkis.node.execution.job.LinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.job.JobTypeEnum;
import com.webank.wedatasphere.dss.linkis.node.execution.listener.LinkisExecutionListener;
import com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.job.JobBuilder;
import com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.log.AzkabanAppjointLog;
import org.apache.log4j.Logger;
import java.util.Map;



/**
 * Created by peacewong on 2019/9/19.
 */
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
        this.job = JobBuilder.getAzkanbanBuilder().setJobProps(this.jobPropsMap).build();
        this.job.setLogObj(new AzkabanAppjointLog(this.log));
        if(JobTypeEnum.EmptyJob == ((LinkisJob)this.job).getJobType()){
            this.log.warn("This node is empty type");
            return;
        }
        LinkisNodeExecutionImpl.getLinkisNodeExecution().runJob(this.job);
        LinkisNodeExecutionImpl.getLinkisNodeExecution().waitForComplete(this.job);

        LinkisExecutionListener listener = (LinkisExecutionListener)LinkisNodeExecutionImpl.getLinkisNodeExecution();
        listener.onStatusChanged(null,  LinkisNodeExecutionImpl.getLinkisNodeExecution().getState(this.job),this.job);
        int resultSize =  LinkisNodeExecutionImpl.getLinkisNodeExecution().getResultSize(this.job);
        for(int i =0; i < resultSize; i++){
            this.log.info("The content of the " + (i + 1) + "th resultset is :"
                    +  LinkisNodeExecutionImpl.getLinkisNodeExecution().getResult(this.job, i, LinkisJobExecutionConfiguration.RESULT_PRINT_SIZE.getValue(this.jobPropsMap)));
        }

        info("Finished to execute job");
    }

    @Override
    public void cancel() throws Exception {
        super.cancel();
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

}
