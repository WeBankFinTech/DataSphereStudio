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



import com.webank.wedatasphere.dss.workflow.entity.NodeUiType;

import java.util.List;

public class NodeUiVO implements Comparable<NodeUiVO> {
    private Long id;
    private String key;
    private String desc;
    private String lableName;
    private NodeUiType uiType;
    private Boolean required;
    private String value;
    private String defaultValue;
    private Boolean isHidden;
    private String condition;
    private Boolean isAdvanced;
    private Integer order;
    private Boolean isBaseInfo;
    private Integer nodeMenuType;//1为右边栏
    private String position;//存放位置  nodes /nodes.params.startup/nodes.params.runtime
    private List<NodeUiValidateVO> nodeUiValidateVOS;

    public List<NodeUiValidateVO> getNodeUiValidateVOS() {
        return nodeUiValidateVOS;
    }

    public void setNodeUiValidateVOS(List<NodeUiValidateVO> nodeUiValidateVOS) {
        this.nodeUiValidateVOS = nodeUiValidateVOS;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getLableName() {
        return lableName;
    }

    public void setLableName(String lableName) {
        this.lableName = lableName;
    }

    public NodeUiType getUiType() {
        return uiType;
    }

    public void setUiType(NodeUiType uiType) {
        this.uiType = uiType;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Boolean getAdvanced() {
        return isAdvanced;
    }

    public void setAdvanced(Boolean advanced) {
        isAdvanced = advanced;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Boolean getBaseInfo() {
        return isBaseInfo;
    }

    public void setBaseInfo(Boolean baseInfo) {
        isBaseInfo = baseInfo;
    }

    public Integer getNodeMenuType() {
        return nodeMenuType;
    }

    public void setNodeMenuType(Integer nodeMenuType) {
        this.nodeMenuType = nodeMenuType;
    }

    @Override
    public int compareTo(NodeUiVO o) {
        if (!this.isBaseInfo.equals(o.isBaseInfo)) {
            return Boolean.compare(o.isBaseInfo, this.isBaseInfo);
        }
        return this.order - o.order;
    }
}
