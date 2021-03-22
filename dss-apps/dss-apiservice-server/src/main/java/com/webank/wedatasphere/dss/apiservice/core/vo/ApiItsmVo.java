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

package com.webank.wedatasphere.dss.apiservice.core.vo;

public class ApiItsmVo {

    private String formId;
    private String requestCreateDate;
    private String status;
    private String creator;
    private String name;
    private String description;
    private String grantedUser;
    private String grantedSystem;
    private String grantedStartTime;
    private Integer grantedDays;
    private String reason;
    private String ipAcceptList;
    private String priority;

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getRequestCreateDate() {
        return requestCreateDate;
    }

    public void setRequestCreateDate(String requestCreateDate) {
        this.requestCreateDate = requestCreateDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGrantedUser() {
        return grantedUser;
    }

    public void setGrantedUser(String grantedUser) {
        this.grantedUser = grantedUser;
    }

    public String getGrantedSystem() {
        return grantedSystem;
    }

    public void setGrantedSystem(String grantedSystem) {
        this.grantedSystem = grantedSystem;
    }

    public String getGrantedStartTime() {
        return grantedStartTime;
    }

    public void setGrantedStartTime(String grantedStartTime) {
        this.grantedStartTime = grantedStartTime;
    }

    public Integer getGrantedDays() {
        return grantedDays;
    }

    public void setGrantedDays(Integer grantedDays) {
        this.grantedDays = grantedDays;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getIpAcceptList() {
        return ipAcceptList;
    }

    public void setIpAcceptList(String ipAcceptList) {
        this.ipAcceptList = ipAcceptList;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }
}
