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

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.git.common.protocol.GitTree;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitHistoryResponse;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorCopyInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorMeta;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorMetaRequest;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorSubmitJob;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.*;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.CommonOrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorCopyHistory;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorUnlockVo;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import org.apache.commons.math3.util.Pair;

import java.util.List;


public interface OrchestratorFrameworkService {

    CommonOrchestratorVo createOrchestrator(String username, OrchestratorCreateRequest orchestratorCreateRequest, Workspace workspace) throws Exception;

    CommonOrchestratorVo modifyOrchestrator(String username, OrchestratorModifyRequest orchestratorModifyRequest, Workspace workspace) throws Exception;

    CommonOrchestratorVo deleteOrchestrator(String username, OrchestratorDeleteRequest orchestratorDeleteRequest, Workspace workspace) throws Exception;

    OrchestratorUnlockVo unlockOrchestrator(String username, Workspace workspace, OrchestratorUnlockRequest request) throws DSSErrorException;

    String copyOrchestrator(String username, OrchestratorCopyRequest orchestratorCopyRequest, Workspace workspace) throws Exception;

    Pair<Long, List<OrchestratorCopyHistory>> getOrchestratorCopyHistory(String username, Workspace workspace, Long orchestratorId, Integer currentPage, Integer pageSize) throws Exception;

    Boolean getOrchestratorCopyStatus(Long sourceOrchestratorId);

    DSSOrchestratorCopyInfo getOrchestratorCopyInfoById(String copyInfoId);


    OrchestratorSubmitJob getOrchestratorStatus(Long orchestratorId);

    GitHistoryResponse getHistory(Long workspaceId, Long orchestratorId, String projectName);


    void modifyOrchestratorMeta(String username,ModifyOrchestratorMetaRequest modifyOrchestratorMetaRequest,Workspace workspace) throws Exception;

    List<OrchestratorMeta> getAllOrchestratorMeta(OrchestratorMetaRequest orchestratorMetaRequest, List<Long> total);

}
