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

import com.google.gson.annotations.SerializedName;


public class StaffInfo {
    @SerializedName(value = "ID")
    private String id;
    @SerializedName(value = "StaffID")
    private String staffId;
    @SerializedName(value = "OID")
    private String oId;
    @SerializedName(value = "ChineseName")
    private String chineseName;
    @SerializedName(value = "EnglishName")
    private String englishName;
    @SerializedName(value = "FullName")
    private String fullName;
    @SerializedName(value = "OrgName")
    private String orgName;
    @SerializedName(value = "OrgFullName")
    private String orgFullName;
    @SerializedName(value = "Status")
    private String status;
    @SerializedName(value = "PersonGroup")
    private String personGroup;

    public StaffInfo(){
    }

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
