package com.webank.wedatasphere.dss.appconn.visualis.project;

import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisCommonUtil;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSGetAction;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectSearchOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public class VisualisProjectSearchOperation extends AbstractStructureOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl, ProjectResponseRef>
        implements ProjectSearchOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl> {

    @Override
    public ProjectResponseRef searchProject(RefProjectContentRequestRef.RefProjectContentRequestRefImpl projectRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + URLUtils.PROJECT_SEARCH_URL;
        DSSGetAction visualisGetAction = new DSSGetAction();
        visualisGetAction.setUser(projectRef.getUserName());
        visualisGetAction.setParameter("keywords", projectRef.getProjectName());
        ResponseRef responseRef = VisualisCommonUtil.getExternalResponseRef(projectRef, ssoRequestOperation, url, visualisGetAction);
        return ProjectResponseRef.newExternalBuilder().setRefProjectId(Long.parseLong(responseRef.toMap().get("id").toString())).success();
    }

    @Override
    protected String getAppConnName() {
        return VisualisAppConn.VISUALIS_APPCONN_NAME;
    }
}
