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

package com.webank.wedatasphere.dss.appconn.visualis.project;

import com.webank.wedatasphere.dss.appconn.visualis.VisualisAppConn;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisCommonUtil;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.server.conf.ServerConfiguration;

public class VisualisProjectCreationOperation extends AbstractStructureOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl, ProjectResponseRef>
        implements ProjectCreationOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl> {

    private final static String projectUrl = "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/projects";

    @Override
    public ProjectResponseRef createProject(DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl projectRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + projectUrl;
        DSSPostAction visualisPostAction = new DSSPostAction();
        visualisPostAction.setUser(projectRef.getDSSProject().getCreateBy());
        visualisPostAction.addRequestPayload("name", projectRef.getName());
        visualisPostAction.addRequestPayload("description", projectRef.getDSSProject().getDescription());
        visualisPostAction.addRequestPayload("pic", "6");
        visualisPostAction.addRequestPayload("visibility", true);
        visualisPostAction.addRequestPayload("labels",  ((EnvDSSLabel) (projectRef.getDSSLabels().get(0))).getEnv());
        ResponseRef responseRef = VisualisCommonUtil.getExternalResponseRef(projectRef, ssoRequestOperation, url, visualisPostAction);
        @SuppressWarnings("unchecked")
        Long projectId = DSSCommonUtils.parseToLong(responseRef.getValue("id"));
        return ProjectResponseRef.newExternalBuilder()
                .setRefProjectId(projectId).success();
    }

    @Override
    protected String getAppConnName() {
        return VisualisAppConn.VISUALIS_APPCONN_NAME;
    }
}
