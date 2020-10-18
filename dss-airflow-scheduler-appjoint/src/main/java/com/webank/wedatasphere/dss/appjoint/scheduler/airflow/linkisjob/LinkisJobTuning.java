package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.linkisjob;

/**
 * Created by Xudong Zhang on 2020/8/6.
 */
public interface LinkisJobTuning {

    LinkisJob tuningJob(LinkisJob job);

    boolean ifJobCantuning(String nodeType);
}
