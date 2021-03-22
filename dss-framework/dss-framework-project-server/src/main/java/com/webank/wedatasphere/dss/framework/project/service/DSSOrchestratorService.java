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

package com.webank.wedatasphere.dss.framework.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import java.util.*;

import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkErrorException;
import com.webank.wedatasphere.dss.framework.project.entity.*;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.OrchestratorBaseInfo;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCreateResponseRef;

/**
 * <p>
 * DSS编排模式信息表 服务类
 * </p>
 *
 * @author v_wbzwchen
 * @since 2020-12-21
 */
public interface DSSOrchestratorService extends IService<DSSOrchestrator> {

    /**
     * 保存编排模式
     * @param orchestratorCreateRequest
     * @param responseRef
     * @param username
     * @throws DSSFrameworkErrorException
     */
    public void saveOrchestrator(OrchestratorCreateRequest orchestratorCreateRequest, OrchestratorCreateResponseRef responseRef, String username) throws DSSFrameworkErrorException, DSSProjectErrorException;

    /**
     *修改编排模式
     * @param orchestratorModifyRequest
     * @param username
     * @throws DSSFrameworkErrorException
     */
    public void updateOrchestrator(OrchestratorModifyRequest orchestratorModifyRequest, String username) throws DSSFrameworkErrorException, DSSProjectErrorException;

    /**
     * 查询编排模式
     * @param orchestratorRequest
     * @param username
     * @return
     */
    public List<OrchestratorBaseInfo> getListByPage(OrchestratorRequest orchestratorRequest, String username);

    /**
     * 删除编排模式
     * @param orchestratorDeleteRequest
     * @return
     */
    public boolean deleteOrchestrator(OrchestratorDeleteRequest orchestratorDeleteRequest, String username) throws DSSProjectErrorException;


    List<DSSOrchestratorVersion> getOrchestratorVersions(String username, Long projectId, Long orchestratorId, String dssLabel);


}
