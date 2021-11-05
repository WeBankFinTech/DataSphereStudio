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

package com.webank.wedatasphere.dss.orchestrator.converter.standard.ref;

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestration;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractRequestRef;
import java.util.List;


public class ProjectToRelConversionRequestRefImpl extends AbstractRequestRef implements ProjectToRelConversionRequestRef {

    private DSSProject dssProject;
    private List<DSSOrchestration> dssOrcList;
    private Workspace workspace;
    private String userName;

    @Override
    public DSSProject getDSSProject() {
        return dssProject;
    }

    public void setDSSProject(DSSProject dssProject) {
        this.dssProject = dssProject;
    }

    @Override
    public List<DSSOrchestration> getDSSOrcList() {
        return dssOrcList;
    }

    public void setDSSOrcList(List<DSSOrchestration> dssOrcList) {
        this.dssOrcList = dssOrcList;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

}
