package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerPageInfoResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinSchedulerHttpUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectSearchOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public class DolphinSchedulerProjectSearchOperation
        extends AbstractStructureOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl, ProjectResponseRef>
        implements ProjectSearchOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl> {

    private String listPagingUrl;

    @Override
    protected String getAppConnName() {
        return DolphinSchedulerAppConn.DOLPHINSCHEDULER_APPCONN_NAME;
    }

    @Override
    public void init() {
        super.init();
        this.listPagingUrl = getBaseUrl().endsWith("/") ?
                getBaseUrl() + "projects/list-paging" : getBaseUrl() + "/projects/list-paging";
    }

    @Override
    public ProjectResponseRef searchProject(RefProjectContentRequestRef.RefProjectContentRequestRefImpl requestRef) throws ExternalOperationFailedException {
        String url = this.listPagingUrl + "?pageNo=1&pageSize=40&searchVal=" + requestRef.getProjectName();
        DolphinSchedulerPageInfoResponseRef responseRef = DolphinSchedulerHttpUtils.getHttpGetResult(ssoRequestOperation, url, requestRef.getUserName());
        return responseRef.getTotalList().stream().filter(project -> requestRef.getProjectName().equals(project.get("name")))
                .map(project -> {
                    Long id = Long.parseLong(project.get("id").toString());
                    return ProjectResponseRef.newExternalBuilder().setRefProjectId(id).success();
                }).findAny().orElse(ProjectResponseRef.newExternalBuilder().success());
    }

}
