package com.webank.wedatasphere.dss.appconn.visualis.project;

import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisCommonUtil;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public class VisualisProjectDeletionOperation extends AbstractStructureOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl, ProjectResponseRef>
        implements ProjectDeletionOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl> {

    @Override
    protected String getAppConnName() {
        return VisualisAppConn.VISUALIS_APPCONN_NAME;
    }

    @Override
    public ResponseRef deleteProject(RefProjectContentRequestRef.RefProjectContentRequestRefImpl projectRef)
            throws ExternalOperationFailedException {
        String url = getBaseUrl() + URLUtils.PROJECT_DELETE_UPDATE_URL;
        DSSPostAction deleteAction = new DSSPostAction();
        LabelRouteVO routeVO = new LabelRouteVO();
        routeVO.setRoute(projectRef.getDSSLabels().get(0).getValue().get("DSSEnv"));
        deleteAction.addRequestPayload("labels", routeVO);
        deleteAction.setUser(projectRef.getUserName());
        deleteAction.setParameter("id", projectRef.getRefProjectId());
        return VisualisCommonUtil.getExternalResponseRef(projectRef, ssoRequestOperation, url, deleteAction);
    }
}
