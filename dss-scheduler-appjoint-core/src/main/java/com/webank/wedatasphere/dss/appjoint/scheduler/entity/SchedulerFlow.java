/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appjoint.scheduler.entity;

import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.entity.flow.Flow;
import com.webank.wedatasphere.dss.common.entity.flow.FlowVersion;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by enjoyyin on 2019/9/7.
 */
public class SchedulerFlow implements Flow {
    private Long id;
    private String name;
    private String description;
    private String type;
    private List<SchedulerFlow> children;
    private Boolean isRootFlow;
    private String userProxy;
    private List<SchedulerNode> schedulerNodes;
    private List<SchedulerEdge> schedulerEdges;
    private List<Resource> flowResources;
    private List<Map<String, Object>> flowProperties;

    public String getUserProxy() {
        return userProxy;
    }

    public void setUserProxy(String userProxy) {
        this.userProxy = userProxy;
    }

    public List<SchedulerNode> getSchedulerNodes() {
        return schedulerNodes;
    }

    public void setSchedulerNodes(List<SchedulerNode> schedulerNodes) {
        this.schedulerNodes = schedulerNodes;
    }

    public List<SchedulerEdge> getSchedulerEdges() {
        return schedulerEdges;
    }

    public void setSchedulerEdges(List<SchedulerEdge> schedulerEdges) {
        this.schedulerEdges = schedulerEdges;
    }

    public List<Resource> getFlowResources() {
        return flowResources;
    }

    public void setFlowResources(List<Resource> flowResources) {
        this.flowResources = flowResources;
    }

    public List<Map<String, Object>> getFlowProperties() {
        return flowProperties;
    }

    public void setFlowProperties(List<Map<String, Object>> flowProperties) {
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
    public List<SchedulerFlow> getChildren() {
        return this.children;
    }

    @Override
    public void setChildren(List<? extends Flow> children) {
        this.children = children.stream().map(f ->(SchedulerFlow)f).collect(Collectors.toList());
    }


    @Override
    public List<FlowVersion> getFlowVersions() {
        return null;
    }

    @Override
    public void setFlowVersions(List<? extends FlowVersion> flowVersions) {

    }


    @Override
    public void addFlowVersion(FlowVersion flowVersion) {

    }
}
