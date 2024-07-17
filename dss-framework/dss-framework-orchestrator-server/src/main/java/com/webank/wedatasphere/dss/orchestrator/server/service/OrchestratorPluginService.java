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

package com.webank.wedatasphere.dss.orchestrator.server.service;

import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.GitTree;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitCommitResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitFileContentResponse;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestFrameworkConvertOrchestration;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseConvertOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorSubmitRequest;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;


public interface OrchestratorPluginService {
    /**
     * 把编排发布到调度系统，并处理一些本地db的元数据
     */

    ResponseConvertOrchestrator convertOrchestration(RequestFrameworkConvertOrchestration requestConversionOrchestration);

    ResponseConvertOrchestrator getConvertOrchestrationStatus(String id);

    List<GitTree> diffFlow(OrchestratorSubmitRequest flowRequest, String username, Workspace workspace);

    List<GitTree> diffPublish(OrchestratorSubmitRequest flowRequest, String username, Workspace workspace);

    GitFileContentResponse diffFlowContent(OrchestratorSubmitRequest flowRequest, String username, Workspace workspace);

    void submitFlow(OrchestratorSubmitRequest flowRequest, String username, Workspace workspace) throws DSSErrorException;

    void batchSubmitFlow(List<OrchestratorSubmitRequest> flowRequestList, String username, Workspace workspace) throws DSSErrorException;

    GitCommitResponse submitWorkflowToBML(OrchestratorSubmitRequest flowRequest, String username, Workspace workspace);

    BmlResource uploadWorkflowListToGit(List<Long> flowIdList, String projectName, String label, String username, Workspace workspace, Long projectId);
}
