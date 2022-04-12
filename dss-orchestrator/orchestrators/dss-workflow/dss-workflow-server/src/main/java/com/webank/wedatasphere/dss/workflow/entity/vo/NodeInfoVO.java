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

package com.webank.wedatasphere.dss.workflow.entity.vo;

import java.util.List;

public class NodeInfoVO {

    private Integer id;
    //icon
    private String image;
    //nodeType
    private String type;
    private Boolean enableCopy;
    private Boolean shouldCreationBeforeNode;
    private Boolean supportJump;  // 是否支持跳转URL，即工作流节点是否支持Iframe嵌入
    private int jumpType;  // 跳转类型：1 表示是外部节点，2 表示是 Scriptis 节点
    //name
    private String title;

    private List<NodeUiVO> nodeUiVOS;

    public List<NodeUiVO> getNodeUiVOS() {
        return nodeUiVOS;
    }

    public void setNodeUiVOS(List<NodeUiVO> nodeUiVOS) {
        this.nodeUiVOS = nodeUiVOS;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getJumpType() {
        return jumpType;
    }

    public void setJumpType(int jumpType) {
        this.jumpType = jumpType;
    }
}
