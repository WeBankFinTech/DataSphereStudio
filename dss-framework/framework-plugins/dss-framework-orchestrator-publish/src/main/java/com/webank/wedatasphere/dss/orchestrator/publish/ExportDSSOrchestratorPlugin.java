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

package com.webank.wedatasphere.dss.orchestrator.publish;

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.core.plugin.DSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import java.util.List;
import java.util.Map;


public interface ExportDSSOrchestratorPlugin extends DSSOrchestratorPlugin {


    Map<String,Object> exportOrchestrator(String userName,
        String workspaceName,
        Long orchestratorId,
        Long orcVersionId,
        String projectName,
        List<DSSLabel> dssLabels,
        boolean addOrcVersion, Workspace workspace) throws DSSErrorException;

    Long orchestratorVersionIncrease(Long orcId,
        String userName,
        String comment,
        String workspaceName,
        DSSOrchestratorInfo dssOrchestratorInfo,
        String projectName,
        List<DSSLabel> dssLabels) throws DSSErrorException;

}
