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
import com.webank.wedatasphere.dss.orchestrator.core.plugin.DSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import java.io.IOException;
import java.util.List;


public interface ImportDSSOrchestratorPlugin extends DSSOrchestratorPlugin {

    /**
     * 导入Orchestrator
     * @param userName 用户名
     * @param workspaceName 工作空间名
     * @param projectName 工程名
     * @param projectId 工程id
     * @param resourceId bml resourceId
     * @param version bml version
     * @param dssLabels dss标签
     * @param workspace
     * @return
     * @throws DSSErrorException
     * @throws IOException
     * @throws ExternalOperationFailedException
     */
    Long importOrchestrator(String userName,
        String workspaceName,
        String projectName,
        Long projectId,
        String resourceId,
        String version,
        List<DSSLabel> dssLabels,
        Workspace workspace) throws Exception;

}
