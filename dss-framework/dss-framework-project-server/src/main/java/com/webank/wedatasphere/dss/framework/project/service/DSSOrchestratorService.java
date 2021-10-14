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

package com.webank.wedatasphere.dss.framework.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.*;

import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkErrorException;
import com.webank.wedatasphere.dss.framework.project.entity.*;
import com.webank.wedatasphere.dss.framework.project.entity.request.*;
import com.webank.wedatasphere.dss.framework.project.entity.vo.OrchestratorBaseInfo;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestProjectImportOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorCreateResponseRef;


public interface DSSOrchestratorService extends IService<DSSOrchestrator> {


    void saveOrchestrator(OrchestratorCreateRequest orchestratorCreateRequest, OrchestratorCreateResponseRef responseRef, String username) throws DSSFrameworkErrorException, DSSProjectErrorException;


    void updateOrchestrator(OrchestratorModifyRequest orchestratorModifyRequest, String username) throws DSSFrameworkErrorException, DSSProjectErrorException;


    List<OrchestratorBaseInfo> getListByPage(OrchestratorRequest orchestratorRequest, String username);


    boolean deleteOrchestrator(OrchestratorDeleteRequest orchestratorDeleteRequest, String username) throws DSSProjectErrorException;


    void isExistSameNameBeforeCreate(Long workspaceId, Long projectId, String orchestratorName) throws DSSFrameworkErrorException;


    Long isExistSameNameBeforeUpdate(OrchestratorModifyRequest orchestratorModifRequest)throws DSSFrameworkErrorException;


    DSSOrchestrator getOrchestratorById(Long id);

    Long importOrchestrator(RequestProjectImportOrchestrator orchestratorInfo) throws Exception;
}
