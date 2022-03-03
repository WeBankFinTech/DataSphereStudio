package com.webank.wedatasphere.dss.datamodel.center.common.launcher;


import org.apache.linkis.computation.client.interactive.SubmittableInteractiveJob;

public class CommonDataModelJobLauncher implements DataModelJobLauncher<SubmittableInteractiveJob> {

    @Override
    public SubmittableInteractiveJob callBack(SubmittableInteractiveJob job) {
        return job;
    }
}
