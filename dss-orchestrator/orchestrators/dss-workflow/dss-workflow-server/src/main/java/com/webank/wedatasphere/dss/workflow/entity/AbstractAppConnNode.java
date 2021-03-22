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

package com.webank.wedatasphere.dss.workflow.entity;



import java.util.Map;

/**
 * created by cooperyang on 2019/9/25
 * Description:
 */
public abstract class AbstractAppConnNode {

    String projectName;

    long projectId;


    String flowName;

    long flowId;

    String nodeName;

    String nodeId;

    String nodeType;

    Map<String, Object> jobContent;

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


    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public void setFlowId(long flowId) {
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


    public long getProjectId() {
        return this.projectId;
    }

    public String getProjectName() {
        return this.projectName;
    }


    public String getFlowName() {
        return this.flowName;
    }


    public long getFlowId() {
        return this.flowId;
    }


    public Map<String, Object> getJobContent() {
        return this.jobContent;
    }


    public void setJobContent(Map<String, Object> jobContent) {
        this.jobContent = jobContent;
    }

}
