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

package com.webank.wedatasphere.dss.appconn.orchestrator.ref;

import com.webank.wedatasphere.dss.common.entity.IOEnv;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.CommonRequestRefImpl;


public class DefaultOrchestratorImportRequestRef extends CommonRequestRefImpl implements OrchestratorImportRequestRef {
    private String resourceId;
    private String bmlVersion;
    private IOEnv sourceEnv;
    private String orcVersion;

    @Override
    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public String getResourceId() {
        return resourceId;
    }

    @Override
    public void setBmlVersion(String bmlVersion) {
        this.bmlVersion = bmlVersion;
    }

    @Override
    public String getBmlVersion() {
        return bmlVersion;
    }
    @Override
    public void setSourceEnv(IOEnv sourceEnv) {
        this.sourceEnv = sourceEnv;
    }

    @Override
    public IOEnv getSourceEnv() {
        return sourceEnv;
    }

    @Override
    public void setOrcVersion(String orcVersion) {
        this.orcVersion = orcVersion;
    }

    @Override
    public String getOrcVersion() {
        return orcVersion;
    }


}
