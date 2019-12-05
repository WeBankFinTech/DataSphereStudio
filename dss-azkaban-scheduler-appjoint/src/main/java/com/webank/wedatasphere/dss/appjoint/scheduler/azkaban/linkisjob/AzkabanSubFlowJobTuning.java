package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.linkisjob;



/**
 * Created by cooperyang on 2019/11/1.
 */

public class AzkabanSubFlowJobTuning implements LinkisJobTuning {

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
