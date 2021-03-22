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


import com.webank.wedatasphere.dss.workflow.entity.NodeUiValidateTrigger;
import com.webank.wedatasphere.dss.workflow.entity.NodeUiValidateType;

public class NodeUiValidateVO implements Comparable<NodeUiValidateVO> {

    private NodeUiValidateType validateType; //前台的自定义校验函数名,对应validateType
    private String validateRange;
    private NodeUiValidateTrigger trigger;  //blur change
    private String message;

    public NodeUiValidateType getValidateType() {
        return validateType;
    }

    public void setValidateType(NodeUiValidateType validateType) {
        this.validateType = validateType;
    }

    public NodeUiValidateTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(NodeUiValidateTrigger trigger) {
        this.trigger = trigger;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getValidateRange() {
        return validateRange;
    }

    public void setValidateRange(String validateRange) {
        this.validateRange = validateRange;
    }


    @Override
    public int compareTo(NodeUiValidateVO o) {
        //Required校验类型要在数组最前，否则前台无法渲染
        return o.validateType.ordinal() - this.validateType.ordinal();
    }
}
