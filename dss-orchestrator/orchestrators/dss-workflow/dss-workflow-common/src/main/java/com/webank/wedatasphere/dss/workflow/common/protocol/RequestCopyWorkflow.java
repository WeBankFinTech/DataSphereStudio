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

package com.webank.wedatasphere.dss.workflow.common.protocol;

/**
 * @author allenlliu
 * @date 2020/12/29 20:20
 */
public class RequestCopyWorkflow {
   private String  userName;
   private String  workspaceName;
   private Long  rootFlowId;
   private String  contextIdStr;
   private String projectName;
   private Long  orcVersionId;
   private String  version;
   private String  description;

    public RequestCopyWorkflow(String userName,
                               String workspaceName,
                               Long rootFlowId,
                               String contextIdStr,
                               String projectName,
                               Long orcVersionId,
                               String version,
                               String description) {

        this.userName = userName;
        this.workspaceName = workspaceName;
        this.rootFlowId = rootFlowId;
        this.contextIdStr = contextIdStr;
        this.projectName = projectName;
        this.orcVersionId = orcVersionId;
        this.version = version;
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public Long getRootFlowId() {
        return rootFlowId;
    }

    public void setRootFlowId(Long  rootFlowId) {
        this.rootFlowId = rootFlowId;
    }

    public String getContextIdStr() {
        return contextIdStr;
    }

    public void setContextIdStr(String contextIdStr) {
        this.contextIdStr = contextIdStr;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getOrcVersionId() {
        return orcVersionId;
    }

    public void setOrcVersionId(Long orcVersionId) {
        this.orcVersionId = orcVersionId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
