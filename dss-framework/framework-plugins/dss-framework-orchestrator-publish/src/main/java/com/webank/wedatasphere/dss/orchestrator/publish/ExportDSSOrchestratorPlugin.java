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
import com.webank.wedatasphere.dss.orchestrator.publish.entity.OrchestratorExportResult;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import java.util.List;


public interface ExportDSSOrchestratorPlugin extends DSSOrchestratorPlugin {

    /**
     * 导出Orchestrator基本信息和工作流，并放到BML中。
     * 注意：导入导出接口只适合于不同环境下，先从A环境导出工作流，再导入到B环境的情况。
     * 不适合用于在同一环境下的复制。同一环境的复制操作需使用copyOperation。
     * @param addOrcVersion 导出之后，是否要升级一个版本
     */
    OrchestratorExportResult exportOrchestrator(String userName,
                                                Long orchestratorId,
                                                Long orcVersionId,
                                                String projectName,
                                                List<DSSLabel> dssLabels,
                                                boolean addOrcVersion, Workspace workspace) throws DSSErrorException;

    /**
     * 导出Orchestrator基本信息和工作流，并放到BML中。
     * 注意：导入导出接口只适合于不同环境下，先从A环境导出工作流，再导入到B环境的情况。
     * 不适合用于在同一环境下的复制。同一环境的复制操作需使用copyOperation。
     * @param addOrcVersion 导出之后，是否要升级一个版本
     */
    OrchestratorExportResult exportOrchestratorNew(String userName,
                                                Long orchestratorId,
                                                Long orcVersionId,
                                                String projectName,
                                                List<DSSLabel> dssLabels,
                                                boolean addOrcVersion, Workspace workspace) throws DSSErrorException;
    Long orchestratorVersionIncrease(Long orcId,
                                     String userName,
                                     String comment,
                                     Workspace workspace,
                                     DSSOrchestratorInfo dssOrchestratorInfo,
                                     String projectName,
                                     List<DSSLabel> dssLabels) throws DSSErrorException;

    Long addVersionAfterPublish(String userName, Workspace workspace,
                                Long orchestratorId, Long orcVersionId, String projectName,
                                List<DSSLabel> dssLabels,String comment) throws DSSErrorException;

}
