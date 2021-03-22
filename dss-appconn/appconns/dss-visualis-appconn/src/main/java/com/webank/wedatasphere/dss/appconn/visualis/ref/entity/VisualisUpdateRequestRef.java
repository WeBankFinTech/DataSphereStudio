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

package com.webank.wedatasphere.dss.appconn.visualis.ref.entity;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.appconn.visualis.utils.NumberUtils;
import com.webank.wedatasphere.dss.standard.app.development.crud.UpdateNodeRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;

import java.util.Map;

public class VisualisUpdateRequestRef implements UpdateNodeRequestRef {

    String nodeType;
    String username;
    String name;
    Map<String, Object> jobContent;
    Long projectId;
    String projectName;
    String flowName;
    Long flowId;
    Map<String, Object> parameters = Maps.newHashMap();
    String type;
    Workspace workspace;

    public String getId() throws Exception {
        String externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(jobContent);
        if("linkis.appjoint.visualis.display".equalsIgnoreCase(nodeType)){
            DisplayCreateResponseRef displayCreateResponseRef = new DisplayCreateResponseRef(externalContent);
            return NumberUtils.parseDoubleString(displayCreateResponseRef.getDisplayId());
        } else if("linkis.appjoint.visualis.dashboard".equalsIgnoreCase(nodeType)){
            DashboardCreateResponseRef dashboardCreateResponseRef = new DashboardCreateResponseRef(externalContent);
            return NumberUtils.parseDoubleString(dashboardCreateResponseRef.getDashboardPortalId());
        } else if ("linkis.appjoint.visualis.widget".equalsIgnoreCase(nodeType)){
            WidgetCreateResponseRef widgetCreateResponseRef = new WidgetCreateResponseRef(externalContent);
            return NumberUtils.parseDoubleString(widgetCreateResponseRef.getWidgetId());
        }
        return null;
    }

    @Override
    public Object getParameter(String key) {
        return parameters.get(key);
    }

    @Override
    public void setParameter(String key, Object value) {
        parameters.put(key, value);
    }


    public String getNodeType() {
        return nodeType;
    }

    @Override
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void setUserName(String username) {
        this.username = username;
    }

    @Override
    public String getName() {
        if(name == null){
            name = jobContent.get("title").toString();
        }
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getJobContent() {
        return jobContent;
    }

    @Override
    public void setJobContent(Map<String, Object> jobContent) {
        this.jobContent = jobContent;
    }

    @Override
    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    @Override
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public void setOrcName(String flowName) {
        this.flowName = flowName;
    }

    @Override
    public void setOrcId(long flowId) {
        this.flowId = flowId;
    }

    public String getFlowName() {
        return flowName;
    }

    public Long getFlowId() {
        return flowId;
    }

    @Override
    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
