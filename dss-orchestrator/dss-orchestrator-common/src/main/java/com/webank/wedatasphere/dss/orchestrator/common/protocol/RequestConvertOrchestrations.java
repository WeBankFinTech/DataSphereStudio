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

package com.webank.wedatasphere.dss.orchestrator.common.protocol;

import com.webank.wedatasphere.dss.common.entity.DSSWorkspace;
import com.webank.wedatasphere.dss.common.entity.project.Project;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import java.util.List;


public class RequestConvertOrchestrations {

    private String userName;
    private List<Long> orcAppIds;
    private Project project;
    private DSSWorkspace workspace;
    private List<DSSLabel> dssLabels;

    public void setOrcAppIds(List<Long> orcAppIds) {
        this.orcAppIds = orcAppIds;
    }

    public List<Long> getOrcAppIds() {
        return orcAppIds;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public DSSWorkspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(DSSWorkspace workspace) {
        this.workspace = workspace;
    }

    public List<DSSLabel> getDSSLabels() {
        return dssLabels;
    }

    public void setDSSLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }
}
