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

package com.webank.wedatasphere.dss.framework.workspace.bean;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown = true)
public class StaffInfo {
    @JsonProperty(value = "ID")
    private String id;
    @JsonProperty(value = "StaffID")
    private String staffId;
    @JsonProperty(value = "OID")
    private String oId;
    @JsonProperty(value = "ChineseName")
    private String chineseName;
    @JsonProperty(value = "EnglishName")
    private String englishName;
    @JsonProperty(value = "FullName")
    private String fullName;
    @JsonProperty(value = "OrgName")
    private String orgName;
    @JsonProperty(value = "OrgFullName")
    private String orgFullName;
    @JsonProperty(value = "Status")
    private String status;
    @JsonProperty(value = "PersonGroup")
    private String personGroup;

    public StaffInfo(String id,String englishName, String orgFullName){
        this.id = id;
        this.englishName = englishName;
        this.orgFullName = orgFullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    public String getoId() {
        return oId;
    }

    public void setoId(String oId) {
        this.oId = oId;
    }

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgFullName() {
        return orgFullName;
    }

    public void setOrgFullName(String orgFullName) {
        this.orgFullName = orgFullName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPersonGroup() {
        return personGroup;
    }

    public void setPersonGroup(String personGroup) {
        this.personGroup = personGroup;
    }

    @Override
    public String toString() {
        return "StaffInfo{" +
                "id='" + id + '\'' +
                ", staffId='" + staffId + '\'' +
                ", oId='" + oId + '\'' +
                ", chineseName='" + chineseName + '\'' +
                ", englishName='" + englishName + '\'' +
                ", fullName='" + fullName + '\'' +
                ", orgName='" + orgName + '\'' +
                ", orgFullName='" + orgFullName + '\'' +
                ", status='" + status + '\'' +
                ", personGroup='" + personGroup + '\'' +
                '}';
    }
}
