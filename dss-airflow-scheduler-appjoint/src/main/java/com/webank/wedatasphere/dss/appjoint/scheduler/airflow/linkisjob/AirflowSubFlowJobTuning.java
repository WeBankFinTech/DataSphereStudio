package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.linkisjob;



/**
 * Created by Xudong Zhang on 2020/8/6.
 */

public class AirflowSubFlowJobTuning implements LinkisJobTuning {

    @Override
    public LinkisJob tuningJob(LinkisJob job) {
        job.setType("flow");
        job.setLinkistype(null);
        job.getConf().put("flow.name",job.getName() + "_");
        return job;
    }

    @Override
    public boolean ifJobCantuning(String nodeType) {
        return "workflow.subflow".equals(nodeType);
    }
}
