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
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestImportOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.plugin.DSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import java.io.IOException;
import java.util.List;


public interface ImportDSSOrchestratorPlugin extends DSSOrchestratorPlugin {

    /**
     * 导入Orchestrator
     * 注意：导入导出接口只适合于不同环境下，先从A环境导出工作流，再导入到B环境的情况。
     * 不适合用于在同一环境下的复制。同一环境的复制操作需使用copyOperation。
     * @param requestImportOrchestrator
     * @return
     * @throws DSSErrorException
     * @throws IOException
     * @throws ExternalOperationFailedException
     */
    DSSOrchestratorVersion importOrchestrator(RequestImportOrchestrator requestImportOrchestrator) throws Exception;

}
