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

package com.webank.wedatasphere.dss.appconn.orchestrator.ref;

import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.entity.PublishType;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.ProjectPublishToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.entity.project.Project;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;

import java.util.List;

/**
 * @author allenlliu
 * @date 2020/12/22 16:31
 */
public class DefaultOrchestratorPublishRequestRef implements ProjectPublishToSchedulerRef {

    private  List<Long> publishOrcIds;
    private  List<Long> orcAppIds;
    private List<DSSFlow> dssFlowList;
    private  String userName;
    private Workspace workspace;
    private  PublishType publishType;
    private  Project project;
    private List<DSSLabel> dssLabels;



    @Override
    public void setOrcIds(List<Long> orcIds) {
        this.publishOrcIds = orcIds;
    }

    @Override
    public List<Long> getOrcIds() {
        return this.publishOrcIds;
    }

    @Override
    public void setOrcAppIds(List<Long> orcAppIds) {
        this.orcAppIds = orcAppIds;
    }

    @Override
    public List<Long> getOrcAppIds() {
        return this.orcAppIds;
    }

    @Override
    public Project getProject() {
        return this.project;
    }

    @Override
    public void setProject(Project project) {
         this.project = project;
    }

    @Override
    public List<DSSFlow> getDSSFlows() {
        return this.dssFlowList;
    }

    @Override
    public void setDSSFlows(List<DSSFlow> dssFlows) {
         this.dssFlowList = dssFlows;
    }

    @Override
    public PublishType getPublishType() {
        return this.publishType;
    }

    @Override
    public void setPublishType(PublishType publishType) {
        this.publishType=publishType;
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
    public void setWorkspace(Workspace workspace) {
         this.workspace = workspace;
    }

    @Override
    public Workspace getWorkspace() {
        return this.workspace;
    }

    @Override
    public void setLabels(List<DSSLabel> dssLabels) {
        this.dssLabels =dssLabels;
    }

    @Override
    public List<DSSLabel> getLabels() {
        return this.dssLabels;
    }
}
