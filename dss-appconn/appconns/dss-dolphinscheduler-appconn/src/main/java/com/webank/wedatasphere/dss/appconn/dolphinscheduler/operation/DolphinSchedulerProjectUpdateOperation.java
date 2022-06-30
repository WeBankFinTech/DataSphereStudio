package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.DolphinSchedulerProjectService;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinSchedulerHttpUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.ProjectUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectUpdateRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

import java.util.Map;

public class DolphinSchedulerProjectUpdateOperation
        extends AbstractStructureOperation<ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl, ResponseRef>
        implements ProjectUpdateOperation<ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl> {

    private String projectUpdateUrl;

    @Override
    protected String getAppConnName() {
        return DolphinSchedulerAppConn.DOLPHINSCHEDULER_APPCONN_NAME;
    }

    @Override
    public void init() {
        super.init();
        String baseUrl = DolphinSchedulerHttpUtils.getDolphinSchedulerBaseUrl(getBaseUrl());
        this.projectUpdateUrl = mergeUrl(baseUrl, "projects/update");
    }

    @Override
    public ProjectResponseRef updateProject(ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl requestRef) throws ExternalOperationFailedException {
        // Dolphin Scheduler项目名
        String dsProjectName =
            ProjectUtils.generateDolphinProjectName(requestRef.getWorkspace().getWorkspaceName(),
                    requestRef.getProjectName());
        logger.info("user {} begin to update project in DolphinScheduler, project name is {}.", requestRef.getUserName(), dsProjectName);
        Map<String, Object> formData = MapUtils.newCommonMapBuilder().put("projectId", requestRef.getRefProjectId())
                .put("projectName", dsProjectName).put("description", requestRef.getDSSProject().getDescription()).build();
        DolphinSchedulerHttpUtils.getHttpPostResult(ssoRequestOperation, projectUpdateUrl, DolphinSchedulerConf.DS_ADMIN_USER.getValue(), formData);
        // 更新授权用户
        ((DolphinSchedulerProjectService) service).getProjectGrantOperation().grantProject(requestRef);
        return ProjectResponseRef.newExternalBuilder().success();
    }

}
