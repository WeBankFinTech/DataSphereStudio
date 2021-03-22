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

package com.webank.wedatasphere.dss.orchestrator.server.service;


import com.webank.wedatasphere.dss.common.entity.*;
import com.webank.wedatasphere.dss.orchestrator.common.entity.*;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;


public interface OrchestratorService {
    /**
     * 新建编排，实例化一个用户的Orchestrator,并创建数据库记录
     *
     * @param dssOrchestratorInfo
     * @return
     */
    OrchestratorVo createOrchestrator(String userName,
                                      String workspaceName,
                                      String projectName,
                                      Long projectId,
                                      String description,
                                      DSSOrchestratorInfo dssOrchestratorInfo,
                                      List<DSSLabel> dssLabels) throws Exception;


    /**
     * 更新编排，更新编排的基本信息
     *
     * @param dssOrchestratorInfo
     */
    void updateOrchestrator(String userName,
                            String workspaceName,
                            DSSOrchestratorInfo dssOrchestratorInfo,
                            List<DSSLabel> dssLabels) throws Exception;

    /**
     * 删除编排，根据编排ID删除一个编排
     *
     * @param orchestratorInfoId
     */
    void deleteOrchestrator(String userName,
                            String workspaceName,
                            String projectName,
                            Long orchestratorInfoId,
                            List<DSSLabel> dssLabels) throws Exception;


    /**
     * 返回一个编排，包含编排的基本信息和最新版本信息
     *
     * @param orchestratorId
     * @return
     */
    OrchestratorVo getOrchestratorVoById(Long orchestratorId);

    /**
     * 根据一个集合查找
     *
     * @param orchestratorIds
     * @return
     */

    List<OrchestratorVo> getOrchestratorVoList(List<Long> orchestratorIds);

    String openOrchestrator(String userName, String workspaceName, Long orchestratorId, List<DSSLabel> dssLabels) throws Exception;
    /**
     * 获取编排模式下的版本号
     *
     * @param orchestratorId
     * @return
     */
    List<DSSOrchestratorVersion> getVersionByOrchestratorId(Long orchestratorId);

    OrchestratorInfo getOrchestratorInfo(String username, Long workflowId);

    List<OrchestratorProdDetail> getOrchestratorDetails(String username, Long projectId, String dssLabel);

    List<DSSOrchestratorVersion> getOrchestratorVersions(String username, Long projectId, Long orchestratorId);

    String rollbackOrchestrator(String username, Long projectId, String projectName,
                                Long orchestratorId, String version, String dssLabel, Workspace workspace) throws Exception;
}
