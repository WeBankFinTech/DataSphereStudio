package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinSchedulerHttpUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.ProjectUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public class DolphinSchedulerProjectDeletionOperation
        extends AbstractStructureOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl, ResponseRef>
        implements ProjectDeletionOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl> {

    private String deleteProjectByIdUrl;

    public DolphinSchedulerProjectDeletionOperation() {}

    @Override
    protected String getAppConnName() {
        return DolphinSchedulerAppConn.DOLPHINSCHEDULER_APPCONN_NAME;
    }

    @Override
    public void init() {
        super.init();
        String baseUrl = DolphinSchedulerHttpUtils.getDolphinSchedulerBaseUrl(getBaseUrl());
        this.deleteProjectByIdUrl = mergeUrl(baseUrl, "projects/delete");
    }

    @Override
    public ResponseRef deleteProject(RefProjectContentRequestRef.RefProjectContentRequestRefImpl projectRef) throws ExternalOperationFailedException {
        // Dolphin Scheduler项目名
        String dsProjectName =
                ProjectUtils.generateDolphinProjectName(projectRef.getWorkspace().getWorkspaceName(), projectRef.getProjectName());
        logger.info("User {} begin to delete project in DolphinScheduler, project name is {}.", projectRef.getUserName(), dsProjectName);
        DolphinSchedulerHttpUtils.getHttpGetResult(ssoRequestOperation, this.deleteProjectByIdUrl, DolphinSchedulerConf.DS_ADMIN_USER.getValue(),
                MapUtils.newCommonMap("projectId", projectRef.getRefProjectId()));
        return ProjectResponseRef.newExternalBuilder().success();
    }
}
