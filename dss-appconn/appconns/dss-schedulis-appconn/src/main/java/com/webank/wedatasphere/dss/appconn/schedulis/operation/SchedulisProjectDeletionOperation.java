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
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SSORequestWTSS;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SchedulisExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulisProjectDeletionOperation implements ProjectDeletionOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulisProjectDeletionOperation.class);

    private ProjectService schedulisProjectService;
    private String managerUrl;

    public SchedulisProjectDeletionOperation(){
    }

    @Override
    public void init() {
        managerUrl = this.schedulisProjectService.getAppInstance().getBaseUrl().endsWith("/") ? this.schedulisProjectService.getAppInstance().getBaseUrl() + "manager" :
                this.schedulisProjectService.getAppInstance().getBaseUrl() + "/manager";
    }

    @Override
    public void setStructureService(StructureService service) {
        schedulisProjectService = (ProjectService) service;
        init();
    }

    @Override
    public ProjectResponseRef deleteProject(ProjectRequestRef projectRef) throws ExternalOperationFailedException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("project", projectRef.getName());
            params.put("delete", "true");
            String responseContent =SSORequestWTSS.requestWTSSWithSSOGet(this.managerUrl,params,this.schedulisProjectService.getSSORequestService(),projectRef.getWorkspace());
            LOGGER.info(" deleteWtssProject --response-->{}",responseContent);
        } catch (Exception e){
            SchedulisExceptionUtils.dealErrorException(60052, "failed to delete project in schedulis", e,
                    ExternalOperationFailedException.class);
        }
        return new SchedulisProjectResponseRef();
    }

}
