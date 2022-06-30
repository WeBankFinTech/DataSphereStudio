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

import java.io.Serializable;
import java.util.List;


public class NodeInfo implements Serializable {
    private Integer id;
    /**
     * 工作流节点图标路径，每个工作流节点的图标都存储在对应的 AppConn 之中
     */
    private String iconPath;
    /**
     * 工作流节点类型，必须以 linkis.[appconn/engineconn]开头。
     * 例如：
     *  1. 如果是 sparkSQL，则必须是：linkis.spark.sql
     *  2. 如果是 Visualis 的 widget，则必须是：linkis.appconn.visualis.widget
     */
    private String nodeType;
    /**
     * 所关联的 AppConn 的 id
     */
    private Integer appConnId;
    /**
     * 所关联的 AppConn 的 name
     */
    private String appConnName;
    /**
     * 【保留字段】表示是否可以发布给调度系统
     */
    private Boolean submitToScheduler;
    /**
     * 节点是否支持拷贝
     */
    private Boolean enableCopy;
    /**
     * 该节点在拖拽到工作流画布前，是否需要先弹窗创建
     */
    private Boolean shouldCreationBeforeNode;
    /**
     * 是否支持跳转URL，即工作流节点是否支持Iframe嵌入
     */
    private Boolean supportJump;
    /**
     * 跳转类型：1 表示是外部节点，2 表示是 Scriptis 节点。如果 supportJump 为 false，则该字段无意义。
     */
    private int jumpType;
    /**
     * 节点名
     */
    private String name;
    private List<NodeUi> nodeUis;

    public List<NodeUi> getNodeUis() {
        return nodeUis;
    }

    public void setNodeUis(List<NodeUi> nodeUis) {
        this.nodeUis = nodeUis;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String getNodeType() {
        return nodeType;
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    public Integer getAppConnId() {
        return appConnId;
    }

    public void setAppConnId(Integer appConnId) {
        this.appConnId = appConnId;
    }

    public String getAppConnName() {
        return appConnName;
    }

    public void setAppConnName(String appConnName) {
        this.appConnName = appConnName;
    }

    public Boolean getSubmitToScheduler() {
        return submitToScheduler;
    }

    public void setSubmitToScheduler(Boolean submitToScheduler) {
        this.submitToScheduler = submitToScheduler;
    }

    public Boolean getEnableCopy() {
        return enableCopy;
    }

    public void setEnableCopy(Boolean enableCopy) {
        this.enableCopy = enableCopy;
    }

    public Boolean getShouldCreationBeforeNode() {
        return shouldCreationBeforeNode;
    }

    public void setShouldCreationBeforeNode(Boolean shouldCreationBeforeNode) {
        this.shouldCreationBeforeNode = shouldCreationBeforeNode;
    }

    public Boolean getSupportJump() {
        return supportJump;
    }

    public void setSupportJump(Boolean supportJump) {
        this.supportJump = supportJump;
    }

    public int getJumpType() {
        return jumpType;
    }

    public void setJumpType(int jumpType) {
        this.jumpType = jumpType;
    }
}
