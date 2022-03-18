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

package com.webank.wedatasphere.dss.orchestrator.server.service.impl;

import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationCreationOperation;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationDeletionOperation;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationService;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationUpdateOperation;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.DSSOrchestrationContentRequestRef;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.OrchestrationResponseRef;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.OrchestrationUpdateRequestRef;
import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref.RefOrchestrationContentRequestRef;
import com.webank.wedatasphere.dss.appconn.scheduler.utils.OrchestrationOperationUtils;
import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.constant.project.ProjectUserPrivEnum;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.protocol.project.*;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestration;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorRefOrchestration;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.exception.DSSOrchestratorErrorException;
import com.webank.wedatasphere.dss.orchestrator.core.type.OrchestratorKindEnum;
import com.webank.wedatasphere.dss.orchestrator.core.utils.OrchestratorUtils;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.server.constant.OrchestratorTypeEnum;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorCreateRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorDeleteRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorModifyRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.CommonOrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorFrameworkService;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.DSSJobContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.UpdateRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.RefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.utils.DevelopmentOperationUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureRequestRef;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.NoSuchAppInstanceException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.linkis.common.exception.WarnException;
import org.apache.linkis.protocol.util.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class OrchestratorFrameworkServiceImpl implements OrchestratorFrameworkService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    OrchestratorMapper orchestratorMapper;
    @Autowired
    OrchestratorService newOrchestratorService;

    /**
     * 1.拿到的dss orchestrator的appconn
     * 2.然后创建
     *
     * @param orchestratorCreateRequest 创建参数
     * @return
     */
    @Override
    @SuppressWarnings("ConstantConditions")
    public CommonOrchestratorVo createOrchestrator(String username, OrchestratorCreateRequest orchestratorCreateRequest,
                                                   Workspace workspace) throws Exception {
        //是否存在相同的编排名称
        newOrchestratorService.isExistSameNameBeforeCreate(orchestratorCreateRequest.getWorkspaceId(), orchestratorCreateRequest.getProjectId(), orchestratorCreateRequest.getOrchestratorName());
        //判断工程是否存在,并且取出工程名称和空间名称
        DSSProject dssProject = validateOperation(orchestratorCreateRequest.getProjectId(), username);

        //1.创建编排实体bean
        OrchestratorKindEnum orchestratorKindEnum = OrchestratorKindEnum.
                getType(OrchestratorTypeEnum.getTypeByKey(orchestratorCreateRequest.getOrchestratorMode()));
        DSSOrchestratorInfo dssOrchestratorInfo = new DSSOrchestratorInfo();
        dssOrchestratorInfo.setType(orchestratorKindEnum.getName());
        dssOrchestratorInfo.setDesc(orchestratorCreateRequest.getDescription());
        dssOrchestratorInfo.setCreateTime(new Date());
        dssOrchestratorInfo.setAppConnName(orchestratorKindEnum.getName());
        dssOrchestratorInfo.setName(orchestratorCreateRequest.getOrchestratorName());
        dssOrchestratorInfo.setCreator(username);
        dssOrchestratorInfo.setProjectId(orchestratorCreateRequest.getProjectId());
        dssOrchestratorInfo.setComment(orchestratorCreateRequest.getDescription());
        dssOrchestratorInfo.setSecondaryType(orchestratorCreateRequest.getOrchestratorWays().toString());
        //new field
        dssOrchestratorInfo.setWorkspaceId(workspace.getWorkspaceId());
        dssOrchestratorInfo.setOrchestratorWay(OrchestratorUtils.getModeStr(orchestratorCreateRequest.getOrchestratorWays()));
        dssOrchestratorInfo.setOrchestratoMode(orchestratorCreateRequest.getOrchestratorMode());
        //1.去orchestratorFramework创建编排模式
        LOGGER.info("{} begins to create a orchestrator {}.", username, orchestratorCreateRequest);
        List<DSSLabel> dssLabels = Collections.singletonList(new EnvDSSLabel(orchestratorCreateRequest.getLabels().getRoute()));
        //2.如果调度系统要求同步创建工作流，向调度系统发送创建工作流的请求
        OrchestrationResponseRef orchestrationResponseRef = tryOrchestrationOperation(dssLabels, dssOrchestratorInfo.getProjectId(),
                dssProject.getName(), workspace, dssOrchestratorInfo,
                OrchestrationService::getOrchestrationCreationOperation, null,
                (structureOperation, structureRequestRef) -> ((OrchestrationCreationOperation) structureOperation)
                        .createOrchestration((DSSOrchestrationContentRequestRef) structureRequestRef), "create");
        //3.新建编排模式需要将编排模式的类型等内容提交给OrchestratorFramework
//        RefJobContentResponseRef responseRef = DevelopmentOperationUtils.tryDSSJobContentRequestRefOperation(() -> getRefCRUDService(dssLabels),
//                developmentService -> ((RefCRUDService) developmentService).getRefCreationOperation(),
//                dssJobContentRequestRef -> dssJobContentRequestRef.setDSSJobContent(MapUtils.newCommonMap(OrchestratorRefConstant.DSS_ORCHESTRATOR_INFO_KEY, dssOrchestratorInfo))
//                        .setUserName(username).setWorkspace(workspace).setName(orchestratorCreateRequest.getOrchestratorName()).setDSSLabels(dssLabels), null,
//                projectRefRequestRef -> projectRefRequestRef.setProjectRefId(orchestratorCreateRequest.getProjectId()).setProjectName(orchestratorCreateRequest.getProjectName()),
//                (developmentOperation, developmentRequestRef) -> {
//                    return ((RefCreationOperation) developmentOperation).createRef((DSSJobContentRequestRef) developmentRequestRef);
//                }, "create orchestration " + orchestratorCreateRequest.getOrchestratorName());
        OrchestratorVo orchestratorVo = newOrchestratorService.createOrchestrator(username, workspace.getWorkspaceName(), dssProject.getName(),
                dssOrchestratorInfo.getProjectId(), dssOrchestratorInfo.getDesc(), dssOrchestratorInfo, dssLabels);
        Long orchestratorId = orchestratorVo.getDssOrchestratorInfo().getId();
        Long orchestratorVersionId = orchestratorVo.getDssOrchestratorVersion().getId();
//        Long orchestratorId = (Long) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATOR_ID_KEY);
//        Long orchestratorVersionId = (Long) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATOR_VERSION_ID_KEY);
        LOGGER.info("created orchestration {} with orchestratorId is {}, and versionId is {}.", orchestratorCreateRequest.getOrchestratorName(), orchestratorId, orchestratorVersionId);
        //4.将工程和orchestrator的关系存储到的数据库中
        if (orchestrationResponseRef != null) {
//            orchestratorService.saveOrchestrator(orchestratorCreateRequest, orchestratorId, orchestratorVersionId, orchestrationResponseRef.getRefOrchestrationId(), username);
            orchestratorMapper.addOrchestratorRefOrchestration(new DSSOrchestratorRefOrchestration(orchestratorId, null, orchestrationResponseRef.getRefOrchestrationId()));
        } else {
            //did not get thirdly orc_id
        }
        CommonOrchestratorVo commonOrchestratorVo = new CommonOrchestratorVo();
        commonOrchestratorVo.setOrchestratorId(orchestratorId);
        return commonOrchestratorVo;
    }

    private <K extends StructureRequestRef, V extends ResponseRef> V tryOrchestrationOperation(List<DSSLabel> dssLabels, Long dssProjectId, String projectName, Workspace workspace, DSSOrchestration dssOrchestrator,
                                                                                               Function<OrchestrationService, StructureOperation> getOrchestrationOperation,
                                                                                               Consumer<RefOrchestrationContentRequestRef> refOrchestrationContentRequestRefConsumer,
                                                                                               BiFunction<StructureOperation, K, V> responseRefConsumer, String operationName) {
        ImmutablePair<OrchestrationService, AppInstance> orchestrationPair = getOrchestrationService(dssLabels);
//        Long refProjectId = projectService.getAppConnProjectId(orchestrationPair.getValue().getId(), dssProjectId);
        ProjectRefIdResponse projectRefIdResponse = (ProjectRefIdResponse) DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender()
                .ask(new ProjectRefIdRequest(orchestrationPair.getValue().getId(), dssProjectId));
        Long refProjectId = projectRefIdResponse.getRefProjectId();
        V orchestrationResponseRef = null;
        if (refProjectId != null && orchestrationPair.getKey() != null) {
            LOGGER.info("try to {} a orchestration {} in SchedulerAppConn.", operationName, dssOrchestrator.getName());
            //这里无需进行重名判断，因为只在 SchedulerAppConn进行创建，一旦创建失败，会触发整个编排创建失败。
            orchestrationResponseRef = OrchestrationOperationUtils.tryOrchestrationOperation(() -> orchestrationPair.getKey(), getOrchestrationOperation,
                    dssOrchestrationContentRequestRef ->
                            dssOrchestrationContentRequestRef.setDSSOrchestration(dssOrchestrator).setProjectName(projectName).setRefProjectId(refProjectId),
                    refOrchestrationContentRequestRef -> {
                        refOrchestrationContentRequestRef.setRefProjectId(refProjectId).setProjectName(projectName);
                        refOrchestrationContentRequestRefConsumer.accept(refOrchestrationContentRequestRef);
                    },
                    (structureOperation, structureRequestRef) -> {
                        structureRequestRef.setDSSLabels(dssLabels).setWorkspace(workspace);
                        return responseRefConsumer.apply(structureOperation, (K) structureRequestRef);
                    }, operationName + " orchestration " + dssOrchestrator.getName() + " in SchedulerAppConn");
        }
        return orchestrationResponseRef;
    }

    @Override
    public CommonOrchestratorVo modifyOrchestrator(String username, OrchestratorModifyRequest orchestratorModifyRequest, Workspace workspace) throws Exception {
        //判断工程是否存在,并且取出工程名称和空间名称
        DSSProject dssProject = validateOperation(orchestratorModifyRequest.getProjectId(), username);
        workspace.setWorkspaceName(dssProject.getWorkspaceName());
        //是否存在相同的编排名称 //todo 返回orchestratorInfo而不是id
        Long orchestratorId = newOrchestratorService.isExistSameNameBeforeUpdate(orchestratorModifyRequest);
        LOGGER.info("{} begins to update a orchestrator {}.", username, orchestratorModifyRequest.getOrchestratorName());
        List<DSSLabel> dssLabels = Collections.singletonList(new EnvDSSLabel(orchestratorModifyRequest.getLabels().getRoute()));
        OrchestratorKindEnum orchestratorKindEnum = OrchestratorKindEnum.
                getType(OrchestratorTypeEnum.getTypeByKey(orchestratorModifyRequest.getOrchestratorMode()));
        DSSOrchestratorInfo dssOrchestratorInfo = new DSSOrchestratorInfo();
        dssOrchestratorInfo.setId(orchestratorId);
        dssOrchestratorInfo.setType(orchestratorKindEnum.getName());
        dssOrchestratorInfo.setDesc(orchestratorModifyRequest.getDescription());
        dssOrchestratorInfo.setUpdateTime(new Date(System.currentTimeMillis()));
        dssOrchestratorInfo.setAppConnName(orchestratorKindEnum.getName());
        dssOrchestratorInfo.setName(orchestratorModifyRequest.getOrchestratorName());
        dssOrchestratorInfo.setCreator(username);
        dssOrchestratorInfo.setProjectId(orchestratorModifyRequest.getProjectId());
        dssOrchestratorInfo.setComment(orchestratorModifyRequest.getDescription());
        dssOrchestratorInfo.setSecondaryType(orchestratorModifyRequest.getOrchestratorWays().toString());
        dssOrchestratorInfo.setUpdateUser(username);
        dssOrchestratorInfo.setOrchestratoMode(orchestratorModifyRequest.getOrchestratorMode());
        dssOrchestratorInfo.setOrchestratorWay(OrchestratorUtils.getModeStr(orchestratorModifyRequest.getOrchestratorWays()));
        dssOrchestratorInfo.setUses(orchestratorModifyRequest.getUses());
        //1.如果调度系统要求同步创建工作流，向调度系统发送创建工作流的请求
        List<Long> refOrchestrationId = orchestratorMapper.getRefOrchestrationId(dssOrchestratorInfo.getId());
        tryOrchestrationOperation(dssLabels, dssProject.getId(), dssProject.getName(), workspace, dssOrchestratorInfo,
                OrchestrationService::getOrchestrationUpdateOperation,
                refOrchestrationContentRequestRef ->
                        refOrchestrationContentRequestRef.setRefOrchestrationId(CollectionUtils.isEmpty(refOrchestrationId) ? null : refOrchestrationId.get(0)),
                (structureOperation, structureRequestRef) -> ((OrchestrationUpdateOperation) structureOperation)
                        .updateOrchestration((OrchestrationUpdateRequestRef) structureRequestRef), "update");
        //2.更新编排模式需要将编排模式的类型等内容提交给OrchestratorFramework
//        DevelopmentOperationUtils.tryRefJobContentRequestRefOperation(() -> getRefCRUDService(dssLabels),
//                developmentService -> ((RefCRUDService) developmentService).getRefUpdateOperation(),
//                refJobContentRequestRef -> {
//                    refJobContentRequestRef.setRefJobContent(MapUtils.newCommonMap(OrchestratorRefConstant.DSS_ORCHESTRATOR_INFO_KEY, dssOrchestratorInfo))
//                            .setUserName(username).setWorkspace(workspace).setName(orchestratorModifyRequest.getOrchestratorName()).setDSSLabels(dssLabels);
//                }, null,
//                projectRefRequestRef -> projectRefRequestRef.setProjectRefId(orchestratorModifyRequest.getProjectId()).setProjectName(dssProject.getName()),
//                (developmentOperation, developmentRequestRef) -> {
//                    return ((RefUpdateOperation) developmentOperation).updateRef((UpdateRequestRef) developmentRequestRef);
//                }, "update orchestration " + orchestratorModifyRequest.getOrchestratorName());
        newOrchestratorService.updateOrchestrator(username, workspace.getWorkspaceName(), dssOrchestratorInfo, dssLabels);
        //3.将工程和orchestrator的关系存储到的数据库中
        CommonOrchestratorVo orchestratorVo = new CommonOrchestratorVo();
//        orchestratorService.updateOrchestrator(orchestratorModifyRequest, username);
        orchestratorVo.setOrchestratorId(orchestratorId);
        return orchestratorVo;
    }

    @Override
    public CommonOrchestratorVo deleteOrchestrator(String username, OrchestratorDeleteRequest orchestratorDeleteRequest, Workspace workspace) throws Exception {
        //判断工程是否存在,并且取出工程名称和空间名称
        //todo 需要判断是否有工程编辑权限
        DSSProject dssProject = validateOperation(orchestratorDeleteRequest.getProjectId(), username);
        DSSOrchestratorInfo orchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorDeleteRequest.getId());
        LOGGER.info("{} begins to delete a orchestrator {}.", username, orchestratorInfo.getName());
        List<DSSLabel> dssLabels = Collections.singletonList(new EnvDSSLabel(orchestratorDeleteRequest.getLabels().getRoute()));
        List<Long> refOrchestrationId = orchestratorMapper.getRefOrchestrationId(orchestratorInfo.getId());
        tryOrchestrationOperation(dssLabels, dssProject.getId(), dssProject.getName(), workspace, null,
                OrchestrationService::getOrchestrationDeletionOperation,
                refOrchestrationContentRequestRef -> refOrchestrationContentRequestRef
                        .setRefOrchestrationId(CollectionUtils.isEmpty(refOrchestrationId) ? null : refOrchestrationId.get(0)).setOrchestrationName(orchestratorInfo.getName()),
                (structureOperation, structureRequestRef) -> ((OrchestrationDeletionOperation) structureOperation)
                        .deleteOrchestration((RefOrchestrationContentRequestRef) structureRequestRef), "delete");
        //删除编排模式需要将编排模式的类型等内容提交给OrchestratorFramework
        //调用orchestrator framework服务 删除编排模式、编排模式版本
//        DevelopmentOperationUtils.tryRefJobContentRequestRefOperation(() -> getRefCRUDService(dssLabels),
//                developmentService -> ((RefCRUDService) developmentService).getRefDeletionOperation(),
//                refJobContentRequestRef -> {
//                    refJobContentRequestRef.setRefJobContent(MapUtils.newCommonMap(OrchestratorRefConstant.ORCHESTRATOR_ID_KEY, orchestratorInfo.getId()))
//                            .setUserName(username).setWorkspace(workspace).setName(orchestratorInfo.getName()).setDSSLabels(dssLabels);
//                }, null,
//                projectRefRequestRef -> projectRefRequestRef.setProjectRefId(orchestratorInfo.getProjectId()).setProjectName(dssProject.getName()),
//                (developmentOperation, developmentRequestRef) -> {
//                    return ((RefDeletionOperation) developmentOperation).deleteRef((RefJobContentRequestRef) developmentRequestRef);
//                }, "delete orchestration " + orchestratorInfo.getName());

        newOrchestratorService.deleteOrchestrator(username, workspace.getWorkspaceName(), dssProject.getName(), orchestratorInfo.getId(), dssLabels);
        LOGGER.info("delete orchestrator {} by orchestrator framework succeed.", orchestratorInfo.getName());
        //删除编排模式
//        orchestratorService.deleteOrchestrator(orchestratorDeleteRequest, username);
        CommonOrchestratorVo orchestratorVo = new CommonOrchestratorVo();
        return orchestratorVo;
    }

    /**
     * 获取更新的operation
     *
     * @param dssLabels 根据label获取对应label的创建operation
     *                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                 todo 目前已经写死
     * @return
     * @throws Exception
     */
    protected RefCRUDService getRefCRUDService(List<DSSLabel> dssLabels) {
        OnlyDevelopmentAppConn orcAppConn = (OnlyDevelopmentAppConn) AppConnManager.getAppConnManager().getAppConn(DSSCommonConf.DSS_ORCHESTRATOR_FRAMEWORK_APP_CONN_NAME.getValue());
        if (orcAppConn == null) {
            LOGGER.error("OrchestratorFrameworkAppConn is not exists, please ask admin for help.");
            throw new WarnException(60028, "OrchestratorFrameworkAppConn is not exists, please ask admin for help.");
        }
        DevelopmentIntegrationStandard standard = orcAppConn.getOrCreateDevelopmentStandard();
        if (standard == null) {
            LOGGER.error("OrchestratorFrameworkAppConn is not completed, please ask admin for help.");
            throw new WarnException(60028, "OrchestratorFrameworkAppConn is not completed, please ask admin for help.");
        }
        AppInstance appInstance = null;
        try {
            appInstance = orcAppConn.getAppDesc().getAppInstancesByLabels(dssLabels).get(0);
        } catch (NoSuchAppInstanceException e) {
            throw new WarnException(60028, e.getDesc());
        }
        return standard.getRefCRUDService(appInstance);
    }

    protected ImmutablePair<OrchestrationService, AppInstance> getOrchestrationService(List<DSSLabel> dssLabels) {
        SchedulerAppConn appConn = AppConnManager.getAppConnManager().getAppConn(SchedulerAppConn.class);
        if (appConn == null) {
            return null;
        }
        AppInstance appInstance;
        try {
            appInstance = appConn.getAppDesc().getAppInstancesByLabels(dssLabels).get(0);
        } catch (NoSuchAppInstanceException e) {
            throw new WarnException(60028, e.getDesc());
        }
        return new ImmutablePair<>(appConn.getOrCreateStructureStandard().getOrchestrationService(appInstance), appInstance);
    }

    /**
     * Determine whether the project exists.
     * and validate user edit_auth for the project.
     *
     * @param projectId
     * @return
     * @throws DSSOrchestratorErrorException
     */
    private DSSProject validateOperation(long projectId, String username) throws DSSOrchestratorErrorException {
        ProjectInfoRequest projectInfoRequest = new ProjectInfoRequest();
        projectInfoRequest.setProjectId(projectId);
        DSSProject dssProject = (DSSProject) DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender()
                .ask(projectInfoRequest);
        if (dssProject == null) {
            DSSExceptionUtils.dealErrorException(6003, "工程不存在", DSSOrchestratorErrorException.class);
        }
        if (!hasProjectEditPriv(projectId, username)) {
            DSSExceptionUtils.dealErrorException(6004, "用户没有工程编辑权限", DSSOrchestratorErrorException.class);
        }
        return dssProject;
    }

    private boolean hasProjectEditPriv(Long projectId, String username) {
        ProjectUserAuthResponse projectUserAuthResponse = (ProjectUserAuthResponse) DSSSenderServiceFactory.getOrCreateServiceInstance()
                .getProjectServerSender().ask(new ProjectUserAuthRequest(projectId, username));
        return projectUserAuthResponse.getPrivList().contains(ProjectUserPrivEnum.PRIV_EDIT.getRank())
                || projectUserAuthResponse.getProjectOwner().equals(username);
    }

}
