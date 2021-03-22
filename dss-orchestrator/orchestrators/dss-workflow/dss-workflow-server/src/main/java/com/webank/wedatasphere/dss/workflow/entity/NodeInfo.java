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

import java.io.Serializable;
import java.util.List;

/**
 * Created by v_wbjftang on 2019/10/10.
 */
public class NodeInfo implements Serializable {
    private Integer id;
    private String icon;
    private String nodeType;
    private Integer appConnId;
    private Boolean submitToScheduler; //是否可以提交执行
    private Boolean enableCopy;
    private Boolean shouldCreationBeforeNode;
    private Boolean supportJump;
    private String jumpUrl;
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public String getJumpUrl() {
        return jumpUrl;
    }

    public void setJumpUrl(String jumpUrl) {
        this.jumpUrl = jumpUrl;
    }
}
