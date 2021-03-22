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
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCreateRequestRef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by cooperyang on 2020/11/18
 * Description:
 */
public class DefaultOrchestratorCreateRequestRef implements OrchestratorCreateRequestRef {


    private String workspaceName;

    private String projectName;

    private Long projectId;

    private Map<String, Object> parameters = new HashMap<>();

    private String name;

    private String type;

    private  String userName;

    private DSSOrchestratorInfo dssOrchestratorInfo;

    private  String contextIDStr;

    private List<DSSLabel> dssLabels;




    @Override
    public String getWorkspaceName() {
        return workspaceName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return this.userName;
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
    public Long getProjectId() {
        return projectId;
    }


    @Override
    public void setDssOrchestratorInfo(DSSOrchestratorInfo dssOrchestratorInfo) {
        this.dssOrchestratorInfo = dssOrchestratorInfo;
    }

    @Override
    public DSSOrchestratorInfo getDSSOrchestratorInfo() {
        return dssOrchestratorInfo;
    }

    @Override
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }


    @Override
    public void setContextIDStr(String contextIDStr) {
     this.contextIDStr = contextIDStr;
    }

    @Override
    public String getContextIDStr() {
        return contextIDStr;
    }

    @Override
    public void setDSSLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return dssLabels;
    }

    @Override
    public Object getParameter(String key) {
        return this.parameters.get(key);
    }

    @Override
    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    @Override
    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Override
    public String toString() {
        return "name: " + this.getName();
    }
}

