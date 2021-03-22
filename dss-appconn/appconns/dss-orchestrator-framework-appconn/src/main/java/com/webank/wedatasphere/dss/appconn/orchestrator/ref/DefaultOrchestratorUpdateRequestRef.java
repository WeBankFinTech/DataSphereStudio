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

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorUpdateRef;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;

import java.util.List;
import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/12/15 10:32
 */
public class DefaultOrchestratorUpdateRequestRef implements OrchestratorUpdateRef {

    private String userName;
    private Long orcID;
    private String orcName;
    private String description;
    private String uses;
    private  String workspaceName;
    private  String projectName;
    private List<DSSLabel> dssLabelList;

    private DSSOrchestratorInfo dssOrchestratorInfo;


    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public void setOrcID(Long orcID) {
      this.orcID = orcID;
    }

    @Override
    public Long getOrcID() {
        return orcID;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setUses(String uses) {
        this.uses = uses;
    }

    @Override
    public String getUses() {
        return uses;
    }

    @Override
    public void setOrchestratorName(String name) {
        this.orcName = name;
    }

    @Override
    public String getOrchestratorName() {
        return orcName;
    }

    @Override
    public String getWorkspaceName() {
        return workspaceName;
    }

    @Override
    public void setWorkspaceName(String workspaceName) {
    this.workspaceName = workspaceName;
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
    public DSSOrchestratorInfo getOrchestratorInfo() {
        return null;
    }

    @Override
    public void setOrchestratorInfo(DSSOrchestratorInfo dssOrchestratorInfo) {
        this.dssOrchestratorInfo = dssOrchestratorInfo;
    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return dssLabelList;
    }

    @Override
    public void setDSSLabels(List<DSSLabel> dssLabels) {
       this.dssLabelList = dssLabels;
    }

    @Override
    public Object getParameter(String key) {
        return null;
    }

    @Override
    public void setParameter(String key, Object value) {

    }

    @Override
    public Map<String, Object> getParameters() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }
}
