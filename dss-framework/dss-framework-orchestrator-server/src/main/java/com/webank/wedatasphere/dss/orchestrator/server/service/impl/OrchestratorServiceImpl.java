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

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.common.constant.project.ProjectUserPrivEnum;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.DSSLabelUtil;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectUserAuthRequest;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectUserAuthResponse;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkErrorException;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestOrchestratorInfos;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestProjectUpdateOrcVersion;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOrchestratorInfos;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.exception.DSSOrchestratorErrorException;
import com.webank.wedatasphere.dss.orchestrator.core.utils.OrchestratorUtils;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.loader.OrchestratorManager;
import com.webank.wedatasphere.dss.orchestrator.publish.utils.OrchestrationDevelopmentOperationUtils;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorModifyRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorBaseInfo;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.*;
import com.webank.wedatasphere.dss.standard.app.development.ref.*;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.service.RefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.service.RefQueryService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationWarnException;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class OrchestratorServiceImpl implements OrchestratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorServiceImpl.class);
    @Autowired
    private OrchestratorManager orchestratorManager;
    @Autowired
    private OrchestratorMapper orchestratorMapper;
    @Autowired
    private ContextService contextService;

    private static final int VALID_FLAG = 1;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrchestratorVo createOrchestrator(String userName,
                                             Workspace workspace,
                                             String projectName,
                                             Long projectId,
                                             String description,
                                             DSSOrchestratorInfo dssOrchestratorInfo,
                                             List<DSSLabel> dssLabels) throws Exception {
        OrchestratorVo orchestratorVo = new OrchestratorVo();
        //todo 增加校验
        String uuid = UUID.randomUUID().toString();

        //作为Orchestrator的唯一标识，包括跨环境导入导出也不发生变化。
        dssOrchestratorInfo.setUUID(uuid);

        String version = OrchestratorUtils.generateNewVersion();
        String contextId = contextService.createContextID(workspace.getWorkspaceName(), projectName, dssOrchestratorInfo.getName(), version, userName);
        LOGGER.info("Create a new ContextId: {} for new orchestrator {}.", contextId, dssOrchestratorInfo.getName());
        //1. 访问DSS工作流微模块创建工作流
        RefJobContentResponseRef appRef = tryRefOperation(dssOrchestratorInfo, userName, workspace, dssLabels, null,
                developmentService -> ((RefCRUDService) developmentService).getRefCreationOperation(),
                dssContextRequestRef -> dssContextRequestRef.setContextId(contextId),
                projectRefRequestRef -> projectRefRequestRef.setProjectName(projectName).setRefProjectId(projectId),
                (developmentOperation, developmentRequestRef) -> {
                    DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(userName,
                            workspace.getWorkspaceName(), dssOrchestratorInfo.getType(), dssLabels);
                    Map<String, Object> dssJobContent = MapUtils.newCommonMapBuilder()
                            .put(OrchestratorRefConstant.DSS_ORCHESTRATOR_INFO_KEY, dssOrchestratorInfo)
                            .put(OrchestratorRefConstant.ORCHESTRATOR_VERSION_KEY, version)
                            .put(OrchestratorRefConstant.ORCHESTRATION_SCHEDULER_APP_CONN, Optional.ofNullable(dssOrchestrator)
                                    .map(DSSOrchestrator::getSchedulerAppConn).map(AppConn::getAppDesc).map(AppDesc::getAppName)
                                    .map(Object::toString).orElse("NULL")).build();
                    DSSJobContentRequestRef requestRef = (DSSJobContentRequestRef) developmentRequestRef;
                    requestRef.setDSSJobContent(dssJobContent);
                    return ((RefCreationOperation) developmentOperation).createRef(requestRef);
                }, "create");

        //2.往数据库插入一条DSS编排模式
        orchestratorMapper.addOrchestrator(dssOrchestratorInfo);

        DSSOrchestratorVersion dssOrchestratorVersion = new DSSOrchestratorVersion();
        dssOrchestratorVersion.setOrchestratorId(dssOrchestratorInfo.getId());
        dssOrchestratorVersion.setAppId((Long) appRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_ID_KEY));
        dssOrchestratorVersion.setContent((String) appRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_CONTENT_KEY));
        dssOrchestratorVersion.setComment(description);
        dssOrchestratorVersion.setProjectId(projectId);
        dssOrchestratorVersion.setSource("Orchestrator create");
        dssOrchestratorVersion.setUpdater(userName);
        dssOrchestratorVersion.setVersion(version);
        dssOrchestratorVersion.setUpdateTime(new Date());
        dssOrchestratorVersion.setValidFlag(VALID_FLAG);
        dssOrchestratorVersion.setFormatContextId(contextId);
        // 4. 持久化编排的版本信息
        orchestratorMapper.addOrchestratorVersion(dssOrchestratorVersion);
        orchestratorVo.setDssOrchestratorInfo(dssOrchestratorInfo);
        orchestratorVo.setDssOrchestratorVersion(dssOrchestratorVersion);

        return orchestratorVo;
    }


    private <K extends DevelopmentRequestRef, V extends ResponseRef> V tryRefOperation(DSSOrchestratorInfo dssOrchestratorInfo,
                                                                                       String userName,
                                                                                       Workspace workspace,
                                                                                       List<DSSLabel> dssLabels,
                                                                                       BiFunction<DevelopmentIntegrationStandard, AppInstance, DevelopmentService> getDevelopmentService,
                                                                                       Function<DevelopmentService, DevelopmentOperation> getOperation,
                                                                                       Consumer<DSSContextRequestRef> contextRequestRefConsumer,
                                                                                       Consumer<ProjectRefRequestRef> projectRefRequestRefConsumer,
                                                                                       BiFunction<DevelopmentOperation, K, V> responseRefConsumer,
                                                                                       String operationName) throws DSSErrorException {
        DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(userName,
                workspace.getWorkspaceName(), dssOrchestratorInfo.getType(), dssLabels);
        dssOrchestratorInfo.setAppConnName(dssOrchestrator.getAppConn().getAppDesc().getAppName());
        if (getDevelopmentService == null) {
            getDevelopmentService = DevelopmentIntegrationStandard::getRefCRUDService;
        }
        return OrchestrationDevelopmentOperationUtils.tryOrchestrationOperation(dssOrchestratorInfo, dssOrchestrator,
                userName, workspace, dssLabels, getDevelopmentService, getOperation, contextRequestRefConsumer, projectRefRequestRefConsumer,
                responseRefConsumer, operationName);
    }

    @Override
    public void updateOrchestrator(String userName,
                                   Workspace workspace,
                                   DSSOrchestratorInfo dssOrchestratorInfo,
                                   List<DSSLabel> dssLabels) throws Exception {
        orchestratorMapper.updateOrchestrator(dssOrchestratorInfo);
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(dssOrchestratorInfo.getId(), VALID_FLAG);

        tryRefOperation(dssOrchestratorInfo, userName, workspace, dssLabels, null,
                developmentService -> ((RefCRUDService) developmentService).getRefUpdateOperation(),
                null,
                projectRefRequestRef -> projectRefRequestRef.setRefProjectId(dssOrchestratorInfo.getProjectId()),
                (developmentOperation, developmentRequestRef) -> {
                    UpdateRequestRef requestRef = (UpdateRequestRef) developmentRequestRef;
                    Map<String, Object> dssJobContent = MapUtils.newCommonMapBuilder()
                            .put(OrchestratorRefConstant.ORCHESTRATION_NAME, dssOrchestratorInfo.getName())
                            .put(OrchestratorRefConstant.ORCHESTRATION_DESCRIPTION, dssOrchestratorInfo.getComment())
                            .put(OrchestratorRefConstant.ORCHESTRATION_USES, dssOrchestratorInfo.getUses()).build();
                    requestRef.setDSSJobContent(dssJobContent);
                    requestRef.setRefJobContent(MapUtils.newCommonMap(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, dssOrchestratorVersion.getAppId(),
                            OrchestratorRefConstant.ORCHESTRATOR_ID_KEY, dssOrchestratorInfo.getId()));
                    return ((RefUpdateOperation) developmentOperation).updateRef(requestRef);
                }, "update");
    }

    @Override
    public void deleteOrchestrator(String userName,
                                   Workspace workspace,
                                   String projectName,
                                   Long orchestratorInfoId,
                                   List<DSSLabel> dssLabels) throws Exception {
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorInfoId);
        if (null == dssOrchestratorInfo) {
            LOGGER.error("Not exists orchestration {} in project {}.", orchestratorInfoId, projectName);
            DSSExceptionUtils.dealErrorException(61123,
                    String.format("Not exists orchestration %s in project %s.", orchestratorInfoId, projectName),
                    DSSErrorException.class);
        }
        orchestratorMapper.getOrchestratorVersions(dssOrchestratorInfo.getProjectId(), orchestratorInfoId).forEach(dssOrchestratorVersion -> {
            LOGGER.info("user {} try to delete the DSS project {} orchestration(such as DSS workflow) {} of orchestrator {} in version {}.",
                    userName, projectName, dssOrchestratorVersion.getAppId(), dssOrchestratorInfo.getName(), dssOrchestratorVersion.getVersion());
            try {
                tryRefOperation(dssOrchestratorInfo, userName, workspace, dssLabels, null,
                        developmentService -> ((RefCRUDService) developmentService).getRefDeletionOperation(),
                        null, projectRefRequestRef -> projectRefRequestRef.setRefProjectId(dssOrchestratorInfo.getProjectId()),
                        (developmentOperation, developmentRequestRef) -> {
                            RefJobContentRequestRef requestRef = (RefJobContentRequestRef) developmentRequestRef;
                            Map<String, Object> refJobContent = MapUtils.newCommonMap(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, dssOrchestratorVersion.getAppId(),
                                    OrchestratorRefConstant.ORCHESTRATOR_ID_KEY, orchestratorInfoId);
                            requestRef.setRefJobContent(refJobContent);
                            return ((RefDeletionOperation) developmentOperation).deleteRef(requestRef);
                        }, "delete");
            } catch (DSSErrorException e) {
                throw new ExternalOperationWarnException(e.getErrCode(), e.getMessage(), e);
            }
            orchestratorMapper.deleteOrchestratorVersion(dssOrchestratorVersion.getId());
        });
        orchestratorMapper.deleteOrchestrator(orchestratorInfoId);
    }

    @Override
    public List<OrchestratorVo> getOrchestratorVoList(List<Long> orchestratorIds) {
        List<OrchestratorVo> orchestratorVoList = new ArrayList<>();
        orchestratorIds.forEach(orchestratorId -> {
            OrchestratorVo orchestratorVo = getOrchestratorVoById(orchestratorId);
            orchestratorVoList.add(orchestratorVo);
        });
        return orchestratorVoList;
    }

    @Override
    public String openOrchestrator(String userName, Workspace workspace, Long orchestratorId, List<DSSLabel> dssLabels) throws Exception {
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorId);
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orchestratorId, VALID_FLAG);
        if (null == dssOrchestratorInfo || null == dssOrchestratorVersion) {
            throw new DSSOrchestratorErrorException(1000856, "can not find orc from db for orcId: " + orchestratorId);
        }
        QueryJumpUrlResponseRef responseRef = (QueryJumpUrlResponseRef) tryRefOperation(dssOrchestratorInfo, userName,
                workspace, dssLabels, DevelopmentIntegrationStandard::getRefQueryService,
                developmentService -> ((RefQueryService) developmentService).getRefQueryOperation(),
                null,
                projectRefRequestRef -> projectRefRequestRef.setRefProjectId(dssOrchestratorInfo.getProjectId()),
                (developmentOperation, developmentRequestRef) -> {
                    RefJobContentRequestRef requestRef = (RefJobContentRequestRef) developmentRequestRef;
                    requestRef.setRefJobContent(MapUtils.newCommonMap(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, dssOrchestratorVersion.getAppId(),
                            OrchestratorRefConstant.ORCHESTRATOR_ID_KEY, orchestratorId));
                    return ((RefQueryOperation) developmentOperation).query(requestRef);
                }, "open");

        return responseRef.getJumpUrl();
    }

    @Override
    public OrchestratorVo getOrchestratorVoById(Long orchestratorId) {

        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorId);
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orchestratorId, VALID_FLAG);

        OrchestratorVo orchestratorVo = new OrchestratorVo();
        orchestratorVo.setDssOrchestratorInfo(dssOrchestratorInfo);
        orchestratorVo.setDssOrchestratorVersion(dssOrchestratorVersion);
        return orchestratorVo;
    }

    @Override
    public OrchestratorVo getOrchestratorVoByIdAndOrcVersionId(Long orchestratorId, Long orcVersionId) {
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorId);
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getOrcVersionByIdAndOrcVersionId(orchestratorId, orcVersionId, VALID_FLAG);

        OrchestratorVo orchestratorVo = new OrchestratorVo();
        orchestratorVo.setDssOrchestratorInfo(dssOrchestratorInfo);
        orchestratorVo.setDssOrchestratorVersion(dssOrchestratorVersion);
        return orchestratorVo;
    }

    @Override
    public List<DSSOrchestratorVersion> getVersionByOrchestratorId(Long orchestratorId) {
        return orchestratorMapper.getVersionByOrchestratorId(orchestratorId);
    }

    @Override
    public List<DSSOrchestratorVersion> getOrchestratorVersions(String username, Long projectId, Long orchestratorId) {
        LOGGER.info("user {} wants to get orc versions in projectId {} for orcId {}", username, projectId, orchestratorId);
        List<DSSOrchestratorVersion> orchestratorVersions = orchestratorMapper.getOrchestratorVersions(projectId, orchestratorId);
        LOGGER.info("projectId is {} , orcId is {}, orcVersions are {}", projectId, orchestratorId, orchestratorVersions);
        return orchestratorVersions;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String rollbackOrchestrator(String userName, Long projectId, String projectName,
                                       Long orchestratorId, String version, DSSLabel dssLabel, Workspace workspace) throws Exception {
        //1.新建一个版本
        //2.然后将version的版本内容进行去workflow进行cp
        //3.然后把生产的内容进行update到数据库
        String latestVersion = orchestratorMapper.getLatestVersion(orchestratorId, 1);
        List<DSSLabel> labels = new ArrayList<>();
        labels.add(dssLabel);
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorId);
        String newVersion = OrchestratorUtils.increaseVersion(latestVersion);
        DSSOrchestratorVersion dssOrchestratorVersion = new DSSOrchestratorVersion();
        String comment = "回滚工作流到版本:" + version;
        dssOrchestratorVersion.setOrchestratorId(orchestratorId);
        dssOrchestratorVersion.setVersion(newVersion);
        dssOrchestratorVersion.setUpdateTime(new Date());
        dssOrchestratorVersion.setProjectId(projectId);
        dssOrchestratorVersion.setUpdater(userName);
        dssOrchestratorVersion.setComment(comment);
        dssOrchestratorVersion.setSource("rollback from version :" + version);
        dssOrchestratorVersion.setValidFlag(1);
        DSSOrchestratorVersion dbOrcVersion = orchestratorMapper.getVersionByOrchestratorIdAndVersion(orchestratorId, version);
        if (dbOrcVersion == null) {
            DSSExceptionUtils.dealErrorException(60070, "Rollback version " + version + " is not exist.", DSSOrchestratorErrorException.class);
        }
        String contextId = contextService.createContextID(workspace.getWorkspaceName(), projectName, dssOrchestratorInfo.getName(), dssOrchestratorVersion.getVersion(), userName);
        dssOrchestratorVersion.setContextId(contextId);
        LOGGER.info("Create a new ContextId {} for rollback the orchestration {} to version {}.", contextId, dssOrchestratorInfo.getName(), version);
        RefJobContentResponseRef responseRef = tryRefOperation(dssOrchestratorInfo, userName, workspace, Collections.singletonList(dssLabel), null,
                developmentService -> ((RefCRUDService) developmentService).getRefCopyOperation(),
                dssContextRequestRef -> dssContextRequestRef.setContextId(contextId),
                projectRefRequestRef -> projectRefRequestRef.setRefProjectId(dssOrchestratorInfo.getProjectId()).setProjectName(projectName),
                (developmentOperation, developmentRequestRef) -> {
                    CopyRequestRef copyRequestRef = (CopyRequestRef) developmentRequestRef;
                    Map<String, Object> refJobContent = MapUtils.newCommonMap(OrchestratorRefConstant.ORCHESTRATION_ID_KEY, dbOrcVersion.getAppId(),
                            OrchestratorRefConstant.ORCHESTRATION_DESCRIPTION, comment);
                    copyRequestRef.setNewVersion(dssOrchestratorVersion.getVersion())
                            .setRefJobContent(refJobContent);
                    return ((RefCopyOperation) developmentOperation).copyRef(copyRequestRef);
                }, "copy");
        dssOrchestratorVersion.setAppId((Long) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_ID_KEY));
        dssOrchestratorVersion.setContent((String) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_CONTENT_KEY));
        dssOrchestratorVersion.setFormatContextId(contextId);
        //update appConn node contextId
        orchestratorMapper.addOrchestratorVersion(dssOrchestratorVersion);
//        synProjectOrchestratorVersionId(dssOrchestratorVersion, labels);
        return dssOrchestratorVersion.getVersion();
    }


    public void synProjectOrchestratorVersionId(DSSOrchestratorVersion dssOrchestratorVersion, List<DSSLabel> dssLabels) {
        //Is dev environment
        if (DSSLabelUtil.isDevEnv(dssLabels)) {
            RequestProjectUpdateOrcVersion updateOrchestratorVersion = new RequestProjectUpdateOrcVersion();
            updateOrchestratorVersion.setOrchestratorId(dssOrchestratorVersion.getOrchestratorId());
            updateOrchestratorVersion.setVersionId(dssOrchestratorVersion.getId());
            updateOrchestratorVersion.setDssLabels(dssLabels);
            updateOrchestratorVersion.setProjectId(dssOrchestratorVersion.getProjectId());
            //将最新的编排版本ID更新到工程编排表
            DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender()
                    .ask(updateOrchestratorVersion);
        }
    }

    //新建前是否存在相同的编排名称
    @Override
    public void isExistSameNameBeforeCreate(Long workspaceId, Long projectId, String arrangeName) throws DSSFrameworkErrorException {
        List<DSSOrchestratorInfo> retList = orchestratorMapper.getByNameAndProjectId(projectId, arrangeName);
        if (CollectionUtils.isNotEmpty(retList)) {
            DSSFrameworkErrorException.dealErrorException(60000, "编排名称已经存在");
        }
    }

    //是否存在相同的编排名称,如果不存在相同的编排名称則返回编排id
    @Override
    public Long isExistSameNameBeforeUpdate(OrchestratorModifyRequest orchestratorModifRequest) throws DSSFrameworkErrorException {
        DSSOrchestratorInfo orchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorModifRequest.getId());
        if (orchestratorInfo == null) {
            DSSFrameworkErrorException.dealErrorException(60000, "编排模式ID=" + orchestratorModifRequest.getId() + "不存在");
        }
        //若修改了编排名称，检查是否存在相同的编排名称
        if (!orchestratorModifRequest.getOrchestratorName().equals(orchestratorInfo.getName())) {
            isExistSameNameBeforeCreate(orchestratorModifRequest.getWorkspaceId(), orchestratorModifRequest.getProjectId(), orchestratorModifRequest.getOrchestratorName());
        }
        return orchestratorInfo.getId();
    }

    /**
     * 查询编排模式
     *
     * @param orchestratorRequest request of front-end
     * @param username            username
     * @return list of OrchestratorBaseInfo
     */
    @Override
    public List<OrchestratorBaseInfo> getListByPage(OrchestratorRequest orchestratorRequest, String username) {
        List<DSSOrchestratorInfo> list = orchestratorMapper.queryOrchestratorInfos(new HashMap<String, Object>() {{
            put("workspace_id", orchestratorRequest.getWorkspaceId());
            put("project_id", orchestratorRequest.getProjectId());
            put("orchestrator_mode", orchestratorRequest.getOrchestratorMode());
        }});
        List<OrchestratorBaseInfo> retList = new ArrayList<>(list.size());
        if (!CollectionUtils.isEmpty(list)) {
            //todo Is used in front-end?
            ProjectUserAuthResponse projectUserAuthResponse = (ProjectUserAuthResponse) DSSSenderServiceFactory.getOrCreateServiceInstance()
                    .getProjectServerSender().ask(new ProjectUserAuthRequest(orchestratorRequest.getProjectId(), username));
            boolean isReleasable = false, isEditable = false;
            if (!CollectionUtils.isEmpty(projectUserAuthResponse.getPrivList())) {
                isReleasable = projectUserAuthResponse.getPrivList().contains(ProjectUserPrivEnum.PRIV_RELEASE.getRank());
                isEditable = projectUserAuthResponse.getPrivList().contains(ProjectUserPrivEnum.PRIV_EDIT.getRank());
            }
            isReleasable = isReleasable || projectUserAuthResponse.getProjectOwner().equals(username);
            isEditable = isEditable || projectUserAuthResponse.getProjectOwner().equals(username);

            for (DSSOrchestratorInfo dssOrchestratorInfo : list) {
                OrchestratorBaseInfo orchestratorBaseInfo = new OrchestratorBaseInfo();
                BeanUtils.copyProperties(dssOrchestratorInfo, orchestratorBaseInfo);
                orchestratorBaseInfo.setOrchestratorWays(OrchestratorUtils.convertList(dssOrchestratorInfo.getOrchestratorWay()));
                orchestratorBaseInfo.setOrchestratorName(dssOrchestratorInfo.getName());
                orchestratorBaseInfo.setCreateUser(dssOrchestratorInfo.getCreator());
                orchestratorBaseInfo.setOrchestratorId(dssOrchestratorInfo.getId());
                orchestratorBaseInfo.setEditable(isEditable || isReleasable);
                orchestratorBaseInfo.setReleasable(isReleasable);
                retList.add(orchestratorBaseInfo);
            }
        }
        return retList;
    }

    @Override
    public ResponseOrchestratorInfos queryOrchestratorInfos(RequestOrchestratorInfos requestOrchestratorInfos) {
        List<DSSOrchestratorInfo> orchestratorInfos = orchestratorMapper.queryOrchestratorInfos(new HashMap<String, Object>() {{
            put("workspace_id", requestOrchestratorInfos.getWorkspaceId());
            put("project_id", requestOrchestratorInfos.getProjectId());
            put("name", requestOrchestratorInfos.getName());
            put("orchestrator_mode", requestOrchestratorInfos.getOrchestratorMode());
        }});
        return new ResponseOrchestratorInfos(orchestratorInfos);
    }

}