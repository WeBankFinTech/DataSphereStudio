package com.webank.wedatasphere.dss.appconn.dolphinscheduler.service;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerWorkflowUploadOperation;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefSchedulerService;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.ProjectUploadToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.PublishToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.UploadToScheduleOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;

import java.util.List;

public class DolphinSchedulerRefSchedulerService implements RefSchedulerService {

    private AppDesc appDesc;

    private List<DSSLabel> dssLabels;

    public DolphinSchedulerRefSchedulerService() {

    }

    /**
     * 工作流上传到Dolphin Scheduler.
     *
     * @param requestRef
     * @return
     */
    @Override
    public UploadToScheduleOperation<ProjectUploadToSchedulerRef> createRefUploadToScheduleOperation(
        PublishToSchedulerRef requestRef) {
        DolphinSchedulerWorkflowUploadOperation dolphinSchedulerProjectUploadOperation
            = new DolphinSchedulerWorkflowUploadOperation(this.appDesc);
        dolphinSchedulerProjectUploadOperation.setDssLabels(requestRef.getLabels());
        return dolphinSchedulerProjectUploadOperation;
    }

    @Override
    public AppDesc getAppDesc() {
        return appDesc;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

    @Override
    public void setDSSLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return this.dssLabels;
    }
}
