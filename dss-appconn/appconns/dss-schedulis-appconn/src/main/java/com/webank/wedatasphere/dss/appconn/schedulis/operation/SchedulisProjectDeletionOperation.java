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

import com.webank.wedatasphere.dss.appconn.schedulis.SchedulisAppConn;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SchedulisHttpUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

import java.util.HashMap;
import java.util.Map;

public class SchedulisProjectDeletionOperation
        extends AbstractStructureOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl, ResponseRef>
        implements ProjectDeletionOperation<RefProjectContentRequestRef.RefProjectContentRequestRefImpl> {

    private String managerUrl;

    @Override
    protected String getAppConnName() {
        return SchedulisAppConn.SCHEDULIS_APPCONN_NAME;
    }

    @Override
    public void init() {
        super.init();
        managerUrl = getBaseUrl().endsWith("/") ? getBaseUrl() + "manager" :
                getBaseUrl() + "/manager";
    }

    @Override
    public ResponseRef deleteProject(RefProjectContentRequestRef.RefProjectContentRequestRefImpl projectRef) throws ExternalOperationFailedException {
        try {
            Map<String, Object> params = new HashMap<>(2);
            params.put("project", projectRef.getProjectName());
            params.put("delete", "true");
            String responseContent = SchedulisHttpUtils.getHttpGetResult(this.managerUrl, params, ssoRequestOperation, projectRef.getWorkspace());
            logger.info("delete Schedulis Project with response: {}.", responseContent);
        } catch (Exception e){
            DSSExceptionUtils.dealWarnException(60052, "failed to delete project in Schedulis.", e,
                    ExternalOperationFailedException.class);
        }
        return ProjectResponseRef.newExternalBuilder().success();
    }

}
