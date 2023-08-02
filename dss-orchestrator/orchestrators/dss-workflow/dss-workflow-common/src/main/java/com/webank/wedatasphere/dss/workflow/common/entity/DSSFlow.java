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

package com.webank.wedatasphere.dss.workflow.common.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DSSFlow implements Flow {
    private Long id;
    private String name;
    private Boolean state; //0,1代表发布过和未发布过
    private String source;
    private String description;
    private Date createTime;
    private String creator;
    private Boolean isRootFlow;
    private Integer rank;
    private Long projectId;
    private String linkedAppConnNames;
    private String dssLabels;
    private String flowEditLock;//工作流编辑锁
    /**
     * 0disable 1 enable  0表示工作流从来没存过，发布的时候忽略
     */

    private Boolean hasSaved;
    private String uses;
    private List<DSSFlow> children;
    private String flowType;
    private String resourceId;

    private String bmlVersion;

    private String metrics;
    /**
     * 工作流内（包括子工作流）的Tuple(flowId,templateId) 二元组。templateId是参数模板的id
     */
    private List<String[]> flowIdParamConfTemplateIdTuples;

    public List<String[]> getFlowIdParamConfTemplateIdTuples() {
        return flowIdParamConfTemplateIdTuples;
    }

    public void setFlowIdParamConfTemplateIdTuples(List<String[]> flowIdParamConfTemplateIdTuples) {
        this.flowIdParamConfTemplateIdTuples = flowIdParamConfTemplateIdTuples;
    }

    public String getMetrics() {
        return metrics;
    }

    public void setMetrics(String metrics) {
        this.metrics = metrics;
    }

    public String getFlowJson() {
        return flowJson;
    }

    public void setFlowJson(String flowJson) {
        this.flowJson = flowJson;
    }

    private String flowJson;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getBmlVersion() {
        return bmlVersion;
    }

    public void setBmlVersion(String bmlVersion) {
        this.bmlVersion = bmlVersion;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }


    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public List<? extends DSSFlow> getChildren() {
        return children;
    }

    public void addChildren(DSSFlow children) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(children);
    }

    @Override
    public void setChildren(List<? extends Flow> children) {
        this.children = children.stream().map(f -> (DSSFlow) f).collect(Collectors.toList());
    }

    @Override
    public String getFlowType() {
        return flowType;
    }

    @Override
    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    @Override
    public Boolean getRootFlow() {
        return isRootFlow;
    }

    public void setRootFlow(Boolean rootFlow) {
        isRootFlow = rootFlow;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Boolean getHasSaved() {
        return hasSaved;
    }

    public void setHasSaved(Boolean hasSaved) {
        this.hasSaved = hasSaved;
    }

    public String getUses() {
        return uses;
    }

    public void setUses(String uses) {
        this.uses = uses;
    }

    public String getLinkedAppConnNames() {
        return linkedAppConnNames;
    }

    public void setLinkedAppConnNames(String linkedAppConnNames) {
        this.linkedAppConnNames = linkedAppConnNames;
    }

    public String getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(String dssLabels) {
        this.dssLabels = dssLabels;
    }

    public String getFlowEditLock() {
        return flowEditLock;
    }

    public void setFlowEditLock(String flowEditLock) {
        this.flowEditLock = flowEditLock;
    }

    @Override
    public String toString() {
        return "DSSFlow{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", state=" + state +
                ", source='" + source + '\'' +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", creator='" + creator + '\'' +
                ", isRootFlow=" + isRootFlow +
                ", rank=" + rank +
                ", projectId=" + projectId +
                ", linkedAppConnNames='" + linkedAppConnNames + '\'' +
                ", dssLabels='" + dssLabels + '\'' +
                ", flowEditLock='" + flowEditLock + '\'' +
                ", hasSaved=" + hasSaved +
                ", uses='" + uses + '\'' +
                ", children=" + children +
                ", flowType='" + flowType + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", bmlVersion='" + bmlVersion + '\'' +
                ", flowJson='" + flowJson + '\'' +
                '}';
    }
}
