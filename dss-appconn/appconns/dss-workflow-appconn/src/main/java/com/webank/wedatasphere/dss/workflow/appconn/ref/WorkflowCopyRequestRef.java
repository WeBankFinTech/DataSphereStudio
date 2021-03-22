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

package com.webank.wedatasphere.dss.workflow.appconn.ref;

import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCopyRequestRef;

import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/12/9 18:10
 */
public class WorkflowCopyRequestRef implements OrchestratorCopyRequestRef {

    private  Long  appId;
    private  Long  orcVersionId;
    private String workspaceName;
    private String contextIdStr;
    private String projectName;
    private String version;
    private String description;
    private String userName;

    public String getUserName() {
        return userName;
    }

    @Override
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWorkspaceName() {
        return workspaceName;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
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

    public Long getAppId(){
        return this.appId;
    }


    @Override
    public Object getParameter(String key) {
        return null;
    }

    @Override
    public void setParameter(String key, Object value) {

    }

    @Override
    public Map<String, Object> getParameters() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public boolean equals(Object ref) {
        return false;
    }

    @Override
    public String toString() {
        return null;
    }

    @Override
    public void setCopyOrcAppId(long appId) {
        this.appId = appId;
    }

    @Override
    public void setCopyOrcVersionId(long orcVersionId) {
     this.orcVersionId = orcVersionId;
    }

    public Long getOrcVersionId(){
        return orcVersionId;
    }
}
