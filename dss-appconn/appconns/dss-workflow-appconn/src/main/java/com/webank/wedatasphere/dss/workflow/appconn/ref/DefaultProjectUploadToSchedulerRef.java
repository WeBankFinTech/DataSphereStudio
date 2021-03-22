/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.workflow.appconn.ref;

import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.ProjectUploadToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;

import java.util.List;

/**
 * @author allenlliu
 * @date 2020/12/11 17:55
 */
public class DefaultProjectUploadToSchedulerRef implements ProjectUploadToSchedulerRef {

    private DSSProject dssProject;
    private List<DSSFlow> dssFlowList;
    private Workspace workspace;

    private String userName;

    @Override
    public DSSProject getDSSProject() {
        return dssProject;
    }

    @Override
    public List<DSSFlow> getDSSFlowList() {
        return dssFlowList;
    }

    @Override
    public void setDSSProject(DSSProject dssProject) {
        this.dssProject = dssProject;
    }

    @Override
    public void setDSSFlowList(List<DSSFlow> dssFlowList) {
        this.dssFlowList = dssFlowList;
    }

    @Override
    public boolean equals(Object ref) {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }
}
