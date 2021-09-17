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

package com.webank.wedatasphere.dss.appconn.workflow.ref;

import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorCopyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.CommonRequestRefImpl;


public class WorkflowCopyRequestRef extends CommonRequestRefImpl implements OrchestratorCopyRequestRef {

    private  Long  appId;
    private  Long  orcVersionId;
    private String version;
    private String description;


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
