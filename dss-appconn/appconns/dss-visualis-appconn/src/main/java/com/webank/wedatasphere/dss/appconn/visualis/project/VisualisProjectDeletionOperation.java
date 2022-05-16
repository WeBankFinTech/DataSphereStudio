package com.webank.wedatasphere.dss.appconn.visualis.project;

import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisCommonUtil;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public class VisualisProjectDeletionOperation extends AbstractStructureOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl, ResponseRef>
        implements ProjectDeletionOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl> {

    @Override
    protected String getAppConnName() {
        return VisualisAppConn.VISUALIS_APPCONN_NAME;
    }

    @Override
    public ResponseRef deleteProject(RefProjectContentRequestRef.RefProjectContentRequestRefImpl projectRef)
            throws ExternalOperationFailedException {
        String url = URLUtils.getUrl(getBaseUrl(), URLUtils.PROJECT_DELETE_UPDATE_URL, projectRef.getRefProjectId().toString());
        DSSPostAction deleteAction = new DSSPostAction();
        LabelRouteVO routeVO = new LabelRouteVO();
        routeVO.setRoute(projectRef.getDSSLabels().get(0).getValue().get("DSSEnv"));
        deleteAction.addRequestPayload("labels", routeVO);
        deleteAction.setUser(projectRef.getUserName());
        return VisualisCommonUtil.getExternalResponseRef(projectRef, ssoRequestOperation, url, deleteAction);
    }
}
