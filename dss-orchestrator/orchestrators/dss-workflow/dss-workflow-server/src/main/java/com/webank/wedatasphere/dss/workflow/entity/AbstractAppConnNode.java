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

package com.webank.wedatasphere.dss.workflow.entity;


import java.util.Map;


public abstract class AbstractAppConnNode {

    private String projectName;

    private Long projectId;

    private String flowName;

    private Long flowId;

    private String nodeName;

    private String nodeId;

    private String nodeType;

    private String contextId;

    private Map<String, Object> jobContent;

    public AbstractAppConnNode(String projectName, long projectId, String flowName, long flowId,
                               String nodeName, String nodeTye, Map<String, Object> jobContent){
        this.projectId = projectId;
        this.projectName = projectName;
        this.flowName = flowName;
        this.flowId = flowId;
        this.nodeName = nodeName;
        this.nodeType = nodeTye;
        this.jobContent = jobContent;
    }

    public AbstractAppConnNode(){

    }


    public String getContextId() {
        return contextId;
    }

    public void setContextId(String contextId) {
        this.contextId = contextId;  }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public void setFlowId(Long flowId) {
        this.flowId = flowId;
    }

    public String getId() {
        return this.nodeId;
    }


    public void setId(String id) {
        this.nodeId = id;
    }


    public String getNodeType() {
        return this.nodeType;
    }


    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }


    public String getName() {
        return this.nodeName;
    }


    public void setName(String name) {
        this.nodeName = name;
    }


    public Long getProjectId() {
        return this.projectId;
    }

    public String getProjectName() {
        return this.projectName;
    }


    public String getFlowName() {
        return this.flowName;
    }


    public Long getFlowId() {
        return this.flowId;
    }


    public Map<String, Object> getJobContent() {
        return this.jobContent;
    }


    public void setJobContent(Map<String, Object> jobContent) {
        this.jobContent = jobContent;
    }

}
