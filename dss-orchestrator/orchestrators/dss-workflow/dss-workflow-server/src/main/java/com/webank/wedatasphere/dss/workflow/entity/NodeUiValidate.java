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

public class NodeUiValidate implements Serializable {
    private Integer id;
    private Integer uiId;
    private NodeUiValidateType validateType;
    private String validateRange;
    private String errorMsg;
    private String errorMsgEn;
    private NodeUiValidateTrigger trigger;

    public NodeUiValidateTrigger getTrigger() {
        return trigger;
    }

    public void setTrigger(NodeUiValidateTrigger trigger) {
        this.trigger = trigger;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUiId() {
        return uiId;
    }

    public void setUiId(Integer uiId) {
        this.uiId = uiId;
    }

    public NodeUiValidateType getValidateType() {
        return validateType;
    }

    public void setValidateType(NodeUiValidateType validateType) {
        this.validateType = validateType;
    }

    public String getValidateRange() {
        return validateRange;
    }

    public void setValidateRange(String validateRange) {
        this.validateRange = validateRange;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsgEn() {
        return errorMsgEn;
    }

    public void setErrorMsgEn(String errorMsgEn) {
        this.errorMsgEn = errorMsgEn;
    }
}
