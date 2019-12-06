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

package com.webank.wedatasphere.dss.common.entity.flow;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by enjoyyin on 2019/5/14.
 */
public class DWSFlow implements Flow {
    private Long id;
    private String name;
    private Boolean state; //0,1代表发布过和未发布过
    private String source;
    private String description;
    private Date createTime;
    private Long creatorID;
    private Boolean isRootFlow;
    private Integer rank;
    private Long projectID;
    private Boolean hasSaved;//0disable 1 enable  0表示工作流从来没存过，发布的时候忽略
    private String uses;

    private List<DWSFlowVersion> versions; //为了前台不做修改，还是使用versions 而不使用flowVersions的变量名
    private List<DWSFlow> children;
    private String flowType;

    private DWSFlowVersion latestVersion;


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
    public void addFlowVersion(FlowVersion flowVersion) {
        this.versions.add((DWSFlowVersion) flowVersion);
    }

    @Override
    public List<? extends DWSFlow> getChildren() {
        return children;
    }

    @Override
    public void setChildren(List<? extends Flow> children) {
        this.children = children.stream().map(f ->(DWSFlow)f).collect(Collectors.toList());
    }

    @Override
    public List<DWSFlowVersion> getFlowVersions() {
        return this.versions;
    }

    @Override
    public void setFlowVersions(List<? extends FlowVersion> flowVersions) {
        this.versions = flowVersions.stream().map(f ->(DWSFlowVersion)f).collect(Collectors.toList());
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

    public Long getCreatorID() {
        return creatorID;
    }

    public void setCreatorID(Long creatorID) {
        this.creatorID = creatorID;
    }

    public Long getProjectID() {
        return projectID;
    }

    public void setProjectID(Long projectID) {
        this.projectID = projectID;
    }

    public Boolean getHasSaved() {
        return hasSaved;
    }

    public void setHasSaved(Boolean hasSaved) {
        this.hasSaved = hasSaved;
    }

    public DWSFlowVersion getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(DWSFlowVersion latestVersion) {
        this.latestVersion = latestVersion;
    }

    public String getUses() {
        return uses;
    }

    public void setUses(String uses) {
        this.uses = uses;
    }
}
