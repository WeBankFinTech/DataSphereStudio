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
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUrlOperation;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;

public class VisualisProjectService extends ProjectService {

    @Override
    public boolean isCooperationSupported() {
        return true;
    }

    @Override
    public boolean isProjectNameUnique() {
        return false;
    }

    @Override
    protected VisualisProjectCreationOperation createProjectCreationOperation() {
        SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation = getSSORequestService().createSSORequestOperation(VisualisAppConn.VISUALIS_APPCONN_NAME);
        VisualisProjectCreationOperation visualisProjectCreationOperation = new VisualisProjectCreationOperation(this, ssoRequestOperation);
        visualisProjectCreationOperation.setStructureService(this);
        return visualisProjectCreationOperation;
    }

    @Override
    protected ProjectUpdateOperation createProjectUpdateOperation() {
        return null;
    }

    @Override
    protected ProjectDeletionOperation createProjectDeletionOperation() {
        return null;
    }

    @Override
    protected ProjectUrlOperation createProjectUrlOperation() {
        return null;
    }

}
