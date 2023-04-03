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

package com.webank.wedatasphere.dss.workflow.core.entity;

import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSJsonFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.Flow;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkflowImpl implements Workflow {
    private Long id;
    private String name;
    private String description;
    private String type;
    private List<Workflow> children;
    private Boolean isRootFlow;
    private String userProxy;
    private List<WorkflowNode> workflowNodes;
    private List<WorkflowNodeEdge> workflowNodeEdges;
    private List<Resource> flowResources;
    private List<Map<String, Object>> flowProperties;
    private DSSJsonFlow jsonFlow;
    private Workflow parentWorkflow;
    private Long createTime;
    private Long updateTime;
    private String updateUser;

    public DSSJsonFlow getJsonFlow() {
        return jsonFlow;
    }

    public void setJsonFlow(DSSJsonFlow jsonFlow) {
        this.jsonFlow = jsonFlow;
    }

    @Override
    public Workflow getParentWorkflow() {
        return parentWorkflow;
    }

    public void setParentWorkflow(WorkflowImpl parentWorkflow) {
        this.parentWorkflow = parentWorkflow;
    }

    public String getUserProxy() {
        return userProxy;
    }

    public void setUserProxy(String userProxy) {
        this.userProxy = userProxy;
    }

    @Override
    public List<WorkflowNode> getWorkflowNodes() {
        return workflowNodes;
    }

    public void setWorkflowNodes(List<WorkflowNode> workflowNodes) {
        this.workflowNodes = workflowNodes;
    }

    @Override
    public List<WorkflowNodeEdge> getWorkflowNodeEdges() {
        return workflowNodeEdges;
    }

    public void setWorkflowNodeEdges(List<WorkflowNodeEdge> workflowNodeEdges) {
        this.workflowNodeEdges = workflowNodeEdges;
    }

    @Override
    public List<Resource> getFlowResources() {
        return flowResources;
    }

    public void setFlowResources(List<Resource> flowResources) {
        this.flowResources = flowResources;
    }

    @Override
    public List<Map<String, Object>> getFlowProperties() {
        return flowProperties;
    }

    public void setFlowProperties(List<Map<String, Object>> flowProperties) {
        try{
            //1.1.11版本的前端修复了updateUser为空的bug，这里user.to.proxy只是为了兼容bug修复前的老工作流。
            if(this.getUpdateUser()==null&&CollectionUtils.isNotEmpty(flowProperties)){
                flowProperties.forEach(a->{
                    String key = "user.to.proxy";
                    if(a.containsKey(key)){
                        this.setUpdateUser((String)a.get(key));
                    }
                });
            }
        }catch (Exception e){
            e.fillInStackTrace();
        }
        this.flowProperties = flowProperties;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getFlowType() {
        return this.type;
    }

    @Override
    public void setFlowType(String flowType) {
        this.type = flowType;

    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Boolean getRootFlow() {
        return this.isRootFlow;
    }

    public void setRootFlow(Boolean rootFlow) {
        isRootFlow = rootFlow;
    }

    @Override
    public List<Workflow> getChildren() {
        return this.children;
    }

    @Override
    public void setChildren(List<? extends Flow> children) {
        if(null!=children) {
            this.children = children.stream().map(f -> (WorkflowImpl) f).collect(Collectors.toList());
        }
    }

    @Override
    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

}
