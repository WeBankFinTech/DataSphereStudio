package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.service.DolphinSchedulerProjectService;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinSchedulerHttpUtils;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.ProjectUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectUpdateRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;


public class DolphinSchedulerProjectCreationOperation
        extends AbstractStructureOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl, ProjectResponseRef>
        implements ProjectCreationOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl> {

    private String projectUrl;

    @Override
    protected String getAppConnName() {
        return DolphinSchedulerAppConn.DOLPHINSCHEDULER_APPCONN_NAME;
    }

    @Override
    public void init() {
        super.init();
        String baseUrl = DolphinSchedulerHttpUtils.getDolphinSchedulerBaseUrl(getBaseUrl());
        this.projectUrl = mergeUrl(baseUrl, "projects/create");
    }

    @Override
    public ProjectResponseRef createProject(DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl requestRef) throws ExternalOperationFailedException {
        // Dolphin Scheduler项目名
        String dsProjectName = ProjectUtils.generateDolphinProjectName(requestRef.getWorkspace().getWorkspaceName(), requestRef.getDSSProject().getName());
        logger.info("begin to create project in DolphinScheduler, project name is {}, creator is {}.", dsProjectName, requestRef.getDSSProject().getUsername());
        Map<String, Object> formData = MapUtils.newCommonMap("projectName", dsProjectName, "description", requestRef.getDSSProject().getDescription());
        DolphinSchedulerHttpUtils.getHttpPostResult(ssoRequestOperation, projectUrl, DolphinSchedulerConf.DS_ADMIN_USER.getValue(), formData);
        RefProjectContentRequestRef searchRequestRef = new RefProjectContentRequestRef.RefProjectContentRequestRefImpl();
        searchRequestRef.setProjectName(dsProjectName).setWorkspace(requestRef.getWorkspace())
                .setUserName(DolphinSchedulerConf.DS_ADMIN_USER.getValue());
        Long refProjectId = ((ProjectService) service).getProjectSearchOperation().searchProject(searchRequestRef).getRefProjectId();
        logger.info("the refProjectId in dolphinScheduler of projectName:{} is:{}", requestRef.getDSSProject().getName(), refProjectId);
        // 对releaseUsers授权工程可访问权限
        if(CollectionUtils.isNotEmpty(requestRef.getDSSProjectPrivilege().getReleaseUsers())) {
            ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl updateRequestRef = new ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl();
            updateRequestRef.setAddedDSSProjectPrivilege(requestRef.getDSSProjectPrivilege()).setUserName(requestRef.getUserName())
                    .setRefProjectId(refProjectId).setDSSProject(requestRef.getDSSProject()).setWorkspace(requestRef.getWorkspace());
            ((DolphinSchedulerProjectService) service).getProjectGrantOperation().grantProject(updateRequestRef);
        }
        // 返回project id
        return ProjectResponseRef.newExternalBuilder().setRefProjectId(refProjectId).success();
    }

}
