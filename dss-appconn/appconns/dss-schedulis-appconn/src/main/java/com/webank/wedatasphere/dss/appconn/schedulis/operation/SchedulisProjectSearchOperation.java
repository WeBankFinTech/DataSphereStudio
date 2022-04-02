package com.webank.wedatasphere.dss.appconn.schedulis.operation;

import com.webank.wedatasphere.dss.appconn.schedulis.SchedulisAppConn;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SchedulisHttpUtils;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectSearchOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

import java.util.HashMap;
import java.util.Map;

public class SchedulisProjectSearchOperation
        extends AbstractStructureOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl, ProjectResponseRef>
        implements ProjectSearchOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl> {

    private String queryUrl;

    @Override
    public ProjectResponseRef searchProject(RefProjectContentRequestRef.RefProjectContentRequestRefImpl requestRef) throws ExternalOperationFailedException {
        logger.info("begin to search Schedulis project , projectName is {}.", requestRef.getProjectName());

        Map<String, Object> params = new HashMap<>(2);
        params.put("project", requestRef.getProjectName());
        params.put("ajax", "fetchprojectflows");
        try {
            String responseBody = SchedulisHttpUtils.getHttpGetResult(queryUrl, params, ssoRequestOperation, requestRef.getWorkspace());
            logger.info("responseBody from Schedulis is: {}.", responseBody);
            Map map = DSSCommonUtils.COMMON_GSON.fromJson(responseBody, Map.class);
            String errorInfo = (String) map.get("error");
            if (errorInfo != null && errorInfo.contains("Project " + requestRef.getProjectName() + " doesn't exist")) {
                return ProjectResponseRef.newExternalBuilder().success();
            } else if (errorInfo != null) {
                return ProjectResponseRef.newExternalBuilder().error(errorInfo);
            }
            return ProjectResponseRef.newExternalBuilder().setRefProjectId(DSSCommonUtils.parseToLong(map.get("projectId"))).success();
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90117, "Failed to search Schedulis project name!", e);
        }
    }

    @Override
    protected String getAppConnName() {
        return SchedulisAppConn.SCHEDULIS_APPCONN_NAME;
    }

    @Override
    public void init() {
        super.init();
        queryUrl = mergeBaseUrl("manager");
    }

}
