package com.webank.wedatasphere.dss.plugins.airflow.linkis.client;

import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.execution.impl.LinkisNodeExecutionImpl;
import com.webank.wedatasphere.dss.linkis.node.execution.job.LinkisJob;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.job.JobTypeEnum;
import com.webank.wedatasphere.dss.linkis.node.execution.listener.LinkisExecutionListener;
import com.webank.wedatasphere.dss.plugins.airflow.linkis.client.job.JobBuilder;
import com.webank.wedatasphere.dss.plugins.airflow.linkis.client.log.AirflowAppjointLog;
import org.apache.log4j.Logger;

import java.util.Map;

public class AirflowDssJobType {

    public static final String JOB_TYPE = "type";
    private static final String SENSITIVE_JOB_PROP_NAME_SUFFIX = "_X";
    private static final String SENSITIVE_JOB_PROP_VALUE_PLACEHOLDER = "[MASKED]";
    private static final String JOB_DUMP_PROPERTIES_IN_LOG = "job.dump.properties";

    //private static Logger log = LoggerFactory.getLogger(AirflowDssJobType.class);

    protected volatile Map<String, String> jobPropsMap;

    private final String type;

    private Logger log;

    private Job job;

    private boolean isCanceled = false;

    public AirflowDssJobType(Map<String, String> jobPropsMap, Logger log) {
        this.jobPropsMap = jobPropsMap;

        this.log = log;
        this.type = jobPropsMap.getOrDefault(JOB_TYPE, LinkisJobExecutionConfiguration.JOB_DEFAULT_TYPE.getValue(this.jobPropsMap));
        if(!LinkisJobExecutionConfiguration.JOB_DEFAULT_TYPE.getValue(this.jobPropsMap).equalsIgnoreCase(this.type) ){
            throw new RuntimeException("This job(" + this.type + " )is not linkis type");
        }
    }

    public Job getJob() {
        return job;
    }

    public void run() throws Exception {

        log.info("Start to execute job");
        logJobProperties();
        this.job = JobBuilder.getAirflowBuilder().setJobProps(this.jobPropsMap).build();
        this.job.setLogObj(new AirflowAppjointLog(log));
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

        log.info("Finished to execute job");
    }

    public void cancel() throws Exception {
        LinkisNodeExecutionImpl.getLinkisNodeExecution().cancel(this.job);
        log.warn("This job has been canceled: " + this.job.getJobName());
    }

    /**
     * prints the current Job props to the Job log.
     */
    private void logJobProperties() {
        if (this.jobPropsMap != null &&
                this.jobPropsMap.getOrDefault(JOB_DUMP_PROPERTIES_IN_LOG, "false").equalsIgnoreCase("true")) {
            try {
                log.info("******   Job properties   ******");
                log.info(String.format("- Note : value is masked if property name ends with '%s'.",
                        SENSITIVE_JOB_PROP_NAME_SUFFIX));
                for (final Map.Entry<String, String> entry : this.jobPropsMap.entrySet()) {
                    final String key = entry.getKey();
                    final String value = key.endsWith(SENSITIVE_JOB_PROP_NAME_SUFFIX) ?
                            SENSITIVE_JOB_PROP_VALUE_PLACEHOLDER :
                            entry.getValue();
                    log.info(String.format("%s=%s", key, value));
                }
                log.info("****** End Job properties  ******");
            } catch (final Exception ex) {
                this.log.error("failed to log job properties ", ex);
            }
        }
    }

}
