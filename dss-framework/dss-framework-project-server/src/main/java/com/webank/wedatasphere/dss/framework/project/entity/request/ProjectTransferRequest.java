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

package com.webank.wedatasphere.dss.framework.project.entity.request;

import javax.validation.constraints.NotNull;

public class ProjectTransferRequest {

    @NotNull(message = "工程转移用户不能为空")
    private String transferUserName;

    @NotNull(message = "工程名称不能为空")
    private String projectName;


    public String getTransferUserName() {
        return transferUserName;
    }

    public void setTransferUserName(String transferUserName) {
        this.transferUserName = transferUserName;
    }


    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }


    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("ProjectTransferRequest{");
        sb.append("transferUserName='").append(transferUserName).append('\'');
        sb.append(", projectName='").append(projectName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}