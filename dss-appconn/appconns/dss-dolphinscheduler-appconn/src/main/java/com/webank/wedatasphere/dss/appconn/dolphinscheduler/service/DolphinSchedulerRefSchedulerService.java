package com.webank.wedatasphere.dss.appconn.dolphinscheduler.service;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation.DolphinSchedulerWorkflowUploadOperation;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefSchedulerService;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.ProjectPublishToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.ProjectUploadToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.PublishToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.UploadToScheduleOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;

import java.util.List;

/**
 * The type Dolphin scheduler ref scheduler service.
 *
 * @author yuxin.yuan
 * @date 2021/05/21
 */
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
        DolphinSchedulerWorkflowUploadOperation dolphinSchedulerWorkflowUploadOperation
            = new DolphinSchedulerWorkflowUploadOperation(this.appDesc);
        dolphinSchedulerWorkflowUploadOperation.setDssLabels(requestRef.getLabels());
        if (requestRef instanceof ProjectPublishToSchedulerRef) {
            ProjectPublishToSchedulerRef projectPublishToSchedulerRef = (ProjectPublishToSchedulerRef)requestRef;
            dolphinSchedulerWorkflowUploadOperation.setProcessDefinitionId(
                projectPublishToSchedulerRef.getSchedulerWorkflowId());
        }
        return dolphinSchedulerWorkflowUploadOperation;
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
