package com.webank.wedatasphere.dss.datamodel.center.common.launcher;


import com.webank.wedatasphere.dss.datamodel.center.common.config.LinkisJobConfiguration;
import org.apache.linkis.computation.client.LinkisJobClient;
import org.apache.linkis.computation.client.interactive.SubmittableInteractiveJob;

public interface DataModelJobLauncher<E> {
    /**
     *
     * @param launchTask
     * @return
     */
    default E launch(DataModelJobTask launchTask){
        LinkisJobClient.config().setDefaultServerUrl(LinkisJobConfiguration.LINKIS_SERVER_URL.getValue());
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
