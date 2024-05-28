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


import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkErrorException;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestOrchestratorInfos;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOrchestratorInfos;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorModifyRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorBaseInfo;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorUnlockVo;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;
import java.util.concurrent.ExecutionException;


public interface OrchestratorService {
    /**
     * 新建编排，实例化一个用户的Orchestrator,并创建数据库记录
     *
     * @param dssOrchestratorInfo
     * @return
     */
    OrchestratorVo createOrchestrator(String userName,
                                      Workspace workspace,
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
                            Workspace workspace,
                            DSSOrchestratorInfo dssOrchestratorInfo,
                            List<DSSLabel> dssLabels) throws Exception;

    /**
     * 删除编排，根据编排ID删除一个编排
     *
     * @param orchestratorInfoId
     */
    void deleteOrchestrator(String userName,
                            Workspace workspace,
                            String projectName,
                            Long orchestratorInfoId,
                            List<DSSLabel> dssLabels) throws Exception;

    /**
     * 解锁编排对应的工作流
     *
     * @param orchestratorInfoId 编排id
     * @param confirmDelete 是否确认删除编辑锁
     * @param dssLabels
     * @throws DSSErrorException
     * @return
     */
    OrchestratorUnlockVo unlockOrchestrator(String userName,
                                                   Workspace workspace,
                                                   String projectName,
                                                   Long orchestratorInfoId,
                                                   Boolean confirmDelete,
                                                   List<DSSLabel> dssLabels) throws DSSErrorException;

    /**
     * 返回一个编排，包含编排的基本信息和最新版本信息
     *
     * @param orchestratorId
     * @return
     */
    OrchestratorVo getOrchestratorVoById(Long orchestratorId);

    /**
     * 获取一个指定版本编排
     *
     * @param orchestratorId 編排id
     * @param orcVersionId   编排版本id
     * @return 编排
     */
    OrchestratorVo getOrchestratorVoByIdAndOrcVersionId(Long orchestratorId, Long orcVersionId);

    /**
     * 根据一个集合查找
     *
     * @param orchestratorIds
     * @return
     */

    List<OrchestratorVo> getOrchestratorVoList(List<Long> orchestratorIds);

    String openOrchestrator(String userName, Workspace workspace, Long orchestratorId, List<DSSLabel> dssLabels) throws Exception;

    /**
     * 获取编排模式下的版本号
     *
     * @param orchestratorId
     * @return
     */
    List<DSSOrchestratorVersion> getVersionByOrchestratorId(Long orchestratorId);

    List<DSSOrchestratorVersion> getOrchestratorVersions(String username, Long projectId, Long orchestratorId);

    String rollbackOrchestrator(String username, Long projectId, String projectName,
                                Long orchestratorId, String version, LabelRouteVO labels, Workspace workspace) throws Exception;

    //**** new method
    void isExistSameNameBeforeCreate(Long workspaceId, Long projectId, String orchestratorName) throws DSSFrameworkErrorException;

    Long isExistSameNameBeforeUpdate(OrchestratorModifyRequest orchestratorModifRequest, DSSProject dssProject, String username) throws DSSFrameworkErrorException;

    List<OrchestratorBaseInfo> getOrchestratorInfos(OrchestratorRequest orchestratorRequest, String username);

    ResponseOrchestratorInfos queryOrchestratorInfos(RequestOrchestratorInfos requestOrchestratorInfos);

    void batchClearContextId();

    String getAuthenToken(String gitUrl, String gitUsername, String gitPassword) throws ExecutionException;

    OrchestratorVo getOrchestratorByAppId(Long appId);

}
