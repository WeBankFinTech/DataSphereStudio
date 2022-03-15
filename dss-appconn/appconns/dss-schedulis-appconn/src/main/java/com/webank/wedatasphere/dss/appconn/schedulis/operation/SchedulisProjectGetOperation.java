package com.webank.wedatasphere.dss.appconn.schedulis.operation;

import com.webank.wedatasphere.dss.appconn.schedulis.utils.SSORequestWTSS;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectSearchOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SchedulisProjectGetOperation implements ProjectSearchOperation {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulisProjectGetOperation.class);

    private ProjectService schedulisProjectService;

    private String queryUrl;

    public SchedulisProjectGetOperation() {
    }

    @Override
    public ProjectResponseRef searchProject(ProjectRequestRef requestRef) throws ExternalOperationFailedException {
        LOGGER.info("begin to get schedulis project , projectName is {}", requestRef.getName());

        Map<String, Object> params = new HashMap<>();
        params.put("project", requestRef.getName());
        params.put("ajax", "fetchprojectflows");
        try {
            String responseBody = SSORequestWTSS.requestWTSSWithSSOGet(queryUrl, params, this.schedulisProjectService.getSSORequestService(), requestRef.getWorkspace());
            JsonNode jsonNode = new ObjectMapper().readValue(responseBody, JsonNode.class);
            JsonNode errorInfo = jsonNode.get("error");
            if (errorInfo != null && errorInfo.toString().contains("Project " + requestRef.getName() + " doesn't exist")) {
                return ProjectResponseRef.newExternalBuilder().error(errorInfo.toString());
            }
            return ProjectResponseRef.newExternalBuilder().setRefProjectId(jsonNode.get("projectId").getLongValue()).success();
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90117, "Failed to query project!", e);
        }
    }

    @Override
    public void init() {
        String baseUrl = this.schedulisProjectService.getAppInstance().getBaseUrl();
        queryUrl = baseUrl.endsWith("/") ? baseUrl + "manager" : baseUrl + "/manager";
    }

    @Override
    public void setStructureService(StructureService service) {
        this.schedulisProjectService = (ProjectService) service;
        init();
    }
}
