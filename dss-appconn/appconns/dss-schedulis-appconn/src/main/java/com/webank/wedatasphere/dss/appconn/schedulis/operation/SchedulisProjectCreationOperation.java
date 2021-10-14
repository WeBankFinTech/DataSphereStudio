/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.schedulis.operation;

import com.webank.wedatasphere.dss.appconn.schedulis.ref.SchedulisProjectResponseRef;
import com.webank.wedatasphere.dss.appconn.schedulis.service.SchedulisProjectService;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.AzkabanUtils;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SSORequestWTSS;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SchedulisExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class SchedulisProjectCreationOperation implements ProjectCreationOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulisProjectCreationOperation.class);

    private ProjectService schedulisProjectService;

    private String projectUrl;

    private String managerUrl;

    private static  Long DEFAULT_PROJECT_ID = 0L;


    public SchedulisProjectCreationOperation() {
    }

    @Override
    public void init() {
        this.projectUrl = this.schedulisProjectService.getAppInstance().getBaseUrl().endsWith("/") ?
                this.schedulisProjectService.getAppInstance().getBaseUrl() + "manager" :
                this.schedulisProjectService.getAppInstance().getBaseUrl() + "/manager";
        managerUrl = this.schedulisProjectService.getAppInstance().getBaseUrl().endsWith("/") ? this.schedulisProjectService.getAppInstance().getBaseUrl() + "manager" :
                this.schedulisProjectService.getAppInstance().getBaseUrl() + "/manager";
    }


    @Override
    public ProjectResponseRef createProject(ProjectRequestRef requestRef) throws ExternalOperationFailedException {
        LOGGER.info("begin to create project in schedulis project is {}", requestRef.getName());
        SchedulisProjectResponseRef responseRef = new SchedulisProjectResponseRef();
        Map<String, String> params = new HashMap<>();
        params.put("action", "create");
        params.put("name", requestRef.getName());
        params.put("description", requestRef.getDescription());
        try {

            String entStr = SSORequestWTSS.requestWTSSWithSSOPost(projectUrl,params,this.schedulisProjectService,requestRef.getWorkspace());
            LOGGER.error("新建工程 {}, azkaban 返回的信息是 {}", requestRef.getName(), entStr);
            String message = AzkabanUtils.handleAzkabanEntity(entStr);
            if (!"success".equals(message)) {
                throw new ExternalOperationFailedException(90008, "新建工程失败, 原因:" + message);
            }

        } catch (final Exception t) {
            LOGGER.error("Failed to create project!",t);
        }
        try {
            DEFAULT_PROJECT_ID = getSchedulisProjectId(requestRef.getName(),requestRef);
        } catch (Exception e) {
            SchedulisExceptionUtils.dealErrorException(60051, "failed to get project id", e,
                    ExternalOperationFailedException.class);
        }

        responseRef.setProjectRefId(DEFAULT_PROJECT_ID);
        // There is no project ID returned in schedulis, so there is no need to set.
        // Other exceptions are thrown out, so the correct code returned as 0 is OK.
        return responseRef;
    }

    @Override
    public void setStructureService(StructureService service) {
        this.schedulisProjectService = (SchedulisProjectService) service;
    }

    /**
     * Get project ID.
     */
    public Long getSchedulisProjectId(String projectName, ProjectRequestRef requestRef) throws Exception {

        Map<String, Object> params = new HashMap<>();
        params.put("ajax", "getProjectId");
        params.put("project", projectName);

        long projectId = 0L;
        try {
            String content = SSORequestWTSS.requestWTSSWithSSOGet(this.managerUrl, params, this.schedulisProjectService.getSSORequestService(), requestRef.getWorkspace());
            LOGGER.info("Get  schedulis  project  id return str is " + content);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readValue(content, JsonNode.class);
            projectId = jsonNode.get("projectId").getLongValue();
        } catch (final Throwable t) {
            SchedulisExceptionUtils.dealErrorException(60051, "failed to create project in schedulis", t,
                    ExternalOperationFailedException.class);
        }
        return projectId;
    }


}
