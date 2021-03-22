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

package com.webank.wedatasphere.dss.workflow.entity.vo;

import java.util.List;

public class NodeInfoVO {

    private Integer id;
    //icon
    private String image;
    //nodeType
    private String type;
    private Integer applicationId;
    private Boolean submitToScheduler; //是否可以提交执行
    private Boolean enableCopy;
    private Boolean shouldCreationBeforeNode;
    private Boolean supportJump;
    private String jumpUrl;
    //name
    private String title;
    //前台插件参数 默认false
    private boolean editParam;
    //前台插件参数 默认false
    private boolean editBaseInfo;

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

    public Integer getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Integer applicationId) {
        this.applicationId = applicationId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isEditParam() {
        return editParam;
    }

    public void setEditParam(boolean editParam) {
        this.editParam = editParam;
    }

    public boolean isEditBaseInfo() {
        return editBaseInfo;
    }

    public void setEditBaseInfo(boolean editBaseInfo) {
        this.editBaseInfo = editBaseInfo;
    }
}
