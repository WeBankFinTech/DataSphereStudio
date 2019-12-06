package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.linkisjob;

/**
 * Created by cooperyang on 2019/11/1.
 */
public interface LinkisJobTuning {

    LinkisJob tuningJob(LinkisJob job);

    boolean ifJobCantuning(String nodeType);
}
