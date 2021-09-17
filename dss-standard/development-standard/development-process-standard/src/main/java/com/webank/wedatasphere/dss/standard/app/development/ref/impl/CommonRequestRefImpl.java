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

package com.webank.wedatasphere.dss.standard.app.development.ref.impl;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.ref.CommonRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractRequestRef;

import java.util.List;
import java.util.Map;


public  class CommonRequestRefImpl extends AbstractRequestRef implements CommonRequestRef {

    protected String userName;
    protected Long projectId;
    protected String projectName;
    protected String orcName;
    protected Long orcId;
    protected Workspace workspace;
    protected String workspaceName;
    protected List<DSSLabel> dssLabelList;
    protected String contextID;

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String username) {
        this.userName = username;
    }

    @Override
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public Long getProjectId() {
        return projectId;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String getOrcName() {
        return orcName;
    }

    @Override
    public void setOrcName(String orcName) {
        this.orcName = orcName;
    }

    @Override
    public Long getOrcId() {
        return orcId;
    }

    @Override
    public void setOrcId(Long orcId) {
        this.orcId = orcId;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    @Override
    public String getWorkspaceName() {
        return this.workspaceName;
    }

    @Override
    public void setDSSLabels(List<DSSLabel> dssLabels) {
        this.dssLabelList = dssLabels;
    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return dssLabelList;
    }

    @Override
    public String getContextID() {
        return contextID;
    }

    @Override
    public void setContextID(String contextID) {
        this.contextID = contextID;
    }

}
