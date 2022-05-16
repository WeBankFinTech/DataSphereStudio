package com.webank.wedatasphere.dss.appconn.visualis.project;

import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisCommonUtil;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPutAction;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectUpdateRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public class VisualisProjectUpdateOperation extends AbstractStructureOperation<ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl, ResponseRef>
        implements ProjectUpdateOperation<ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl> {
    @Override
    protected String getAppConnName() {
        return VisualisAppConn.VISUALIS_APPCONN_NAME;
    }

    @Override
    public ResponseRef updateProject(ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl projectRef) throws ExternalOperationFailedException {
        String url = URLUtils.getUrl(getBaseUrl(), URLUtils.PROJECT_DELETE_UPDATE_URL, projectRef.getRefProjectId().toString());
        DSSPutAction updateAction = new DSSPutAction();
        updateAction.addRequestPayload("description", projectRef.getDSSProject().getDescription());
        LabelRouteVO routeVO = new LabelRouteVO();
        routeVO.setRoute(projectRef.getDSSLabels().get(0).getValue().get("DSSEnv"));
        updateAction.addRequestPayload("labels", routeVO);
        updateAction.setUser(projectRef.getUserName());
        return VisualisCommonUtil.getExternalResponseRef(projectRef, ssoRequestOperation, url, updateAction);
    }

}
