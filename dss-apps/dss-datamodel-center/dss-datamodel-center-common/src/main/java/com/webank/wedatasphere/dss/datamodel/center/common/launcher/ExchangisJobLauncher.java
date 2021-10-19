package com.webank.wedatasphere.dss.datamodel.center.common.launcher;


import com.webank.wedatasphere.dss.datamodel.center.common.config.ExchangisJobConfiguration;
import com.webank.wedatasphere.linkis.computation.client.LinkisJobClient;
import com.webank.wedatasphere.linkis.computation.client.interactive.SubmittableInteractiveJob;

public interface ExchangisJobLauncher<E> {
    /**
     *
     * @param launchTask
     * @return
     */
    default E launch(ExchangisJobTask launchTask){
        LinkisJobClient.config().setDefaultServerUrl(ExchangisJobConfiguration.LINKIS_SERVER_URL.getValue());
        SubmittableInteractiveJob job =
                LinkisJobClient.interactive().builder().setEngineType(launchTask.getEngineType())
                        .setRunTypeStr(launchTask.getRunType()).setCreator(launchTask.getCreator())
                        .setCode(launchTask.getCode()).addExecuteUser(launchTask.getExecuteUser()).build();
        // 3. Submit Job to Linkis
        job.submit();
        // 4. Wait for Job completed
        job.waitForCompleted();
        return callBack(job);
    }

    E callBack(SubmittableInteractiveJob job);
}
