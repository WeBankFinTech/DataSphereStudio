package com.webank.wedatasphere.dss.appconn.visualis.project;

import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisCommonUtil;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPutAction;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public class VisualisProjectUpdateOperation extends AbstractStructureOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl, ProjectResponseRef>
        implements ProjectUpdateOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl> {
    @Override
    protected String getAppConnName() {
        return VisualisAppConn.VISUALIS_APPCONN_NAME;
    }

    @Override
    public ResponseRef updateProject(RefProjectContentRequestRef.RefProjectContentRequestRefImpl projectRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + URLUtils.PROJECT_DELETE_UPDATE_URL;
        DSSPutAction deleteAction = new DSSPutAction();
        deleteAction.
        LabelRouteVO routeVO = new LabelRouteVO();
        routeVO.setRoute(((EnvDSSLabel) (projectRef.getDSSLabels().get(0))).getEnv());
        deleteAction.addRequestPayload("labels", routeVO);
        deleteAction.setUser(projectRef.getUserName());
        deleteAction.setParameter("id", projectRef.getRefProjectId());
        return VisualisCommonUtil.getExternalResponseRef(projectRef, ssoRequestOperation, url, deleteAction);
    }
}
