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

package com.webank.wedatasphere.dss.framework.project.service.impl;


import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.project.conf.ProjectConf;
import com.webank.wedatasphere.dss.framework.project.contant.OrchestratorTypeEnum;
import com.webank.wedatasphere.dss.framework.project.contant.ProjectServerResponse;
import com.webank.wedatasphere.dss.framework.project.dao.DSSOrchestratorMapper;
import com.webank.wedatasphere.dss.framework.project.entity.DSSOrchestrator;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorDeleteRequest;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorModifyRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.CommonOrchestratorVo;
import com.webank.wedatasphere.dss.framework.project.entity.vo.ProjectInfoVo;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.framework.project.service.DSSFrameworkOrchestratorService;
import com.webank.wedatasphere.dss.framework.project.service.DSSOrchestratorService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.ref.DefaultOrchestratorCreateRequestRef;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorCreateRequestRef;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorCreateResponseRef;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorFrameworkAppConn;
import com.webank.wedatasphere.dss.orchestrator.common.ref.impl.WorkflowOrchestratoDeleteRequestRef;
import com.webank.wedatasphere.dss.orchestrator.common.ref.impl.WorkflowOrchestratorUpdateRequestRef;
import com.webank.wedatasphere.dss.orchestrator.core.type.OrchestratorKindEnum;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CommonResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.service.RefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


public class DSSFrameworkOrchestratorServiceImpl implements DSSFrameworkOrchestratorService {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private DSSOrchestratorMapper orchestratorMapper;
    @Autowired
    private DSSOrchestratorService orchestratorService;
    @Autowired
    private DSSProjectService projectService;

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
        orchestratorService.isExistSameNameBeforeCreate(orchestratorCreateRequest.getProjectId(),orchestratorCreateRequest.getWorkspaceId(),orchestratorCreateRequest.getOrchestratorName());
        //判断工程是否存在,并且取出工程名称和空间名称
        ProjectInfoVo projectInfoVo = projectService.getProjectInfoById(orchestratorCreateRequest.getProjectId());
        if (projectInfoVo == null) {
            DSSExceptionUtils.dealErrorException(ProjectServerResponse.PROJECT_NOT_EXIST.getCode(),
                    ProjectServerResponse.PROJECT_NOT_EXIST.getMsg(), DSSProjectErrorException.class);
        }
        //1.去orchestratorFramework创建编排模式
        //2.将工程和orchestrator的关系存储到的数据库中
        LOGGER.info("{} begins to create a orchestrator {}", username, orchestratorCreateRequest);
        //新建编排模式需要将编排模式的类型等内容提交给OrchestratorFramework
        CommonOrchestratorVo orchestratorVo = new CommonOrchestratorVo();
        try {
            OrchestratorKindEnum orchestratorKindEnum = OrchestratorKindEnum.
                    getType(OrchestratorTypeEnum.getTypeByKey(orchestratorCreateRequest.getOrchestratorMode()));
            List<DSSLabel> dssLabels = Arrays.asList(new EnvDSSLabel(orchestratorCreateRequest.getLabels().getRoute()));
            OrchestratorCreateRequestRef orchestratorCreateRequestRef = null;
            String appconnName = "workflow";
            switch (orchestratorKindEnum) {
                case WORKFLOW:
                    orchestratorCreateRequestRef = new DefaultOrchestratorCreateRequestRef();
                    appconnName = "workflow";
                    break;
                case COMBINED:
                    appconnName = "combined";
                    break;
                case SINGLE_TASK:
                    appconnName = "singleTask";
                    break;
                default:
                    orchestratorCreateRequestRef = new DefaultOrchestratorCreateRequestRef();
            }
            orchestratorCreateRequestRef.setUserName(username);
            orchestratorCreateRequestRef.setDSSLabels(dssLabels);
            orchestratorCreateRequestRef.setProjectId(orchestratorCreateRequest.getProjectId());
            orchestratorCreateRequestRef.setProjectName(StringUtils.isBlank(orchestratorCreateRequest.getProjectName()) ? projectInfoVo.getProjectName() : orchestratorCreateRequest.getProjectName());
            orchestratorCreateRequestRef.setWorkspaceName(StringUtils.isBlank(orchestratorCreateRequest.getWorkspaceName()) ? projectInfoVo.getWorkspaceName() : orchestratorCreateRequest.getWorkspaceName());
            DSSOrchestratorInfo dssOrchestratorInfo = new DSSOrchestratorInfo();
            dssOrchestratorInfo.setType(orchestratorKindEnum.getName());
            dssOrchestratorInfo.setDesc(orchestratorCreateRequest.getDescription());
            dssOrchestratorInfo.setCreateTime(new Date(System.currentTimeMillis()));
            dssOrchestratorInfo.setAppConnName(appconnName);
            dssOrchestratorInfo.setName(orchestratorCreateRequest.getOrchestratorName());
            dssOrchestratorInfo.setCreator(username);
            dssOrchestratorInfo.setProjectId(orchestratorCreateRequest.getProjectId());
            dssOrchestratorInfo.setComment(orchestratorCreateRequest.getDescription());
            dssOrchestratorInfo.setSecondaryType(orchestratorCreateRequest.getOrchestratorWays().toString());
            orchestratorCreateRequestRef.setDssOrchestratorInfo(dssOrchestratorInfo);
            //根据label获取对应label的创建operation
            RefCreationOperation<OrchestratorCreateRequestRef> refCreationOperation =
                    getRefCreationOperation(dssLabels).getRefCreationOperation();
            //调用orchestrator服务 创建编排模式、编排模式版本
            OrchestratorCreateResponseRef responseRef =(OrchestratorCreateResponseRef) refCreationOperation.createRef(orchestratorCreateRequestRef);
            LOGGER.info("operation ends to create ref, content is {}", responseRef.getContent());
            Long orchestratorId = responseRef.getOrcId();
            Long versionId = responseRef.getOrchestratorVersionId();
            LOGGER.info("orchestratorId is {}, and version is {}", orchestratorId, versionId);
            orchestratorVo.setOrchestratorId(orchestratorId);
            //保存编排模式到数据库
            orchestratorService.saveOrchestrator(orchestratorCreateRequest, responseRef, username);
        } catch (final Exception e) {
            DSSExceptionUtils.dealErrorException(60033, "failed to create orchestrator", e, DSSProjectErrorException.class);
        }
        return orchestratorVo;
    }

    @Override
    public CommonOrchestratorVo modifyOrchestrator(String username, OrchestratorModifyRequest orchestratorModifyRequest, Workspace workspace) throws Exception {
        //判断工程是否存在,并且取出工程名称和空间名称
        ProjectInfoVo projectInfoVo = projectService.getProjectInfoById(orchestratorModifyRequest.getProjectId());
        if (projectInfoVo == null) {
            DSSExceptionUtils.dealErrorException(ProjectServerResponse.PROJECT_NOT_EXIST.getCode(),
                    ProjectServerResponse.PROJECT_NOT_EXIST.getMsg(), DSSProjectErrorException.class);
        }
        //是否存在相同的编排名称
        Long orchestratorId = orchestratorService.isExistSameNameBeforeUpdate(orchestratorModifyRequest);
        //1.去orchestratorFramework创建编排模式
        //2.将工程和orchestrator的关系存储到的数据库中
        LOGGER.info("{} begins to update a orchestrator {}", username, orchestratorModifyRequest);
        //新建编排模式需要将编排模式的类型等内容提交给OrchestratorFramework
        CommonOrchestratorVo orchestratorVo = new CommonOrchestratorVo();
        try {
            OrchestratorKindEnum orchestratorKindEnum = OrchestratorKindEnum.
                    getType(OrchestratorTypeEnum.getTypeByKey(orchestratorModifyRequest.getOrchestratorMode()));
            List<DSSLabel> dssLabels = Arrays.asList(new EnvDSSLabel(orchestratorModifyRequest.getLabels().getRoute()));
            String appconnName = "workflow";
            WorkflowOrchestratorUpdateRequestRef orchestratorUpdateRequestRef = new WorkflowOrchestratorUpdateRequestRef();
            switch (orchestratorKindEnum) {
                case COMBINED:
                    appconnName = "combined";
                    break;
                case SINGLE_TASK:
                    appconnName = "singleTask";
                    break;
                default:
                    appconnName = "workflow";
                    orchestratorUpdateRequestRef = new WorkflowOrchestratorUpdateRequestRef();
            }
            DSSOrchestratorInfo dssOrchestratorInfo = new DSSOrchestratorInfo();
            dssOrchestratorInfo.setId(orchestratorId);
            dssOrchestratorInfo.setType(orchestratorKindEnum.getName());
            dssOrchestratorInfo.setDesc(orchestratorModifyRequest.getDescription());
            dssOrchestratorInfo.setCreateTime(new Date(System.currentTimeMillis()));
            dssOrchestratorInfo.setAppConnName(appconnName);
            dssOrchestratorInfo.setName(orchestratorModifyRequest.getOrchestratorName());
            dssOrchestratorInfo.setCreator(username);
            dssOrchestratorInfo.setProjectId(orchestratorModifyRequest.getProjectId());
            dssOrchestratorInfo.setComment(orchestratorModifyRequest.getDescription());
            dssOrchestratorInfo.setSecondaryType(orchestratorModifyRequest.getOrchestratorWays().toString());
            String workspaceName = projectInfoVo.getWorkspaceName();

            orchestratorUpdateRequestRef.setUserName(username);
            orchestratorUpdateRequestRef.setWorkspaceName(workspaceName);
            orchestratorUpdateRequestRef.setDssOrchestratorInfo(dssOrchestratorInfo);
            orchestratorUpdateRequestRef.setDSSLabels(dssLabels);

            //根据label获取对应label的创建operation
            RefUpdateOperation<WorkflowOrchestratorUpdateRequestRef> updateOperation = getRefCreationOperation(dssLabels).getRefUpdateOperation();
            //调用orchestrator服务 更新编排模式、编排模式版本
            LOGGER.info("----orchestratorUpdateRequestRef---> {}",  orchestratorUpdateRequestRef);
            CommonResponseRef responseRef = (CommonResponseRef)updateOperation.updateRef(orchestratorUpdateRequestRef);
            LOGGER.info("operation ends to updateOrchestrator ref, UpdateResult is {},ErrorMsg is {}", responseRef.getResult(),responseRef.getErrorMsg());

            //保存编排模式
            orchestratorService.updateOrchestrator(orchestratorModifyRequest, username);
        } catch (final Exception e) {
            DSSExceptionUtils.dealErrorException(60034, "failed to create orchestrator", e, DSSProjectErrorException.class);
        }
        return orchestratorVo;
    }

    @Override
    public CommonOrchestratorVo deleteOrchestrator(String username, OrchestratorDeleteRequest orchestratorDeleteRequest, Workspace workspace) throws Exception {
        //判断工程是否存在,并且取出工程名称和空间名称
        ProjectInfoVo projectInfoVo = projectService.getProjectInfoById(orchestratorDeleteRequest.getProjectId());
        if (projectInfoVo == null) {
            DSSExceptionUtils.dealErrorException(ProjectServerResponse.PROJECT_NOT_EXIST.getCode(),
                    ProjectServerResponse.PROJECT_NOT_EXIST.getMsg(), DSSProjectErrorException.class);
        }

        DSSOrchestrator orchestrator = orchestratorService.getOrchestratorById(orchestratorDeleteRequest.getId());
        //1.去orchestratorFramework创建编排模式
        //2.将工程和orchestrator的关系存储到的数据库中
        LOGGER.info("{} begins to delete a orchestrator {}", username, orchestratorDeleteRequest);
        //新建编排模式需要将编排模式的类型等内容提交给OrchestratorFramework
        CommonOrchestratorVo orchestratorVo = new CommonOrchestratorVo();
        try {
            String uuid = orchestratorMapper.getUUID(orchestrator.getOrchestratorId());
            if(StringUtils.isBlank(uuid)){
                DSSExceptionUtils.dealErrorException(60035,"工作流不存在", DSSProjectErrorException.class);
            }
            List<DSSLabel> dssLabels = Arrays.asList(new EnvDSSLabel(orchestratorDeleteRequest.getLabels().getRoute()));
            WorkflowOrchestratoDeleteRequestRef orchestratorDeleteRequestRef = new WorkflowOrchestratoDeleteRequestRef();
            String workspaceName = projectInfoVo.getWorkspaceName();
            String projectName = projectInfoVo.getProjectName();
            orchestratorDeleteRequestRef.setUserName(username);
            orchestratorDeleteRequestRef.setWorkspaceName(workspaceName);
            orchestratorDeleteRequestRef.setProjectName(projectName);
            orchestratorDeleteRequestRef.setDSSLabels(dssLabels);
            orchestratorDeleteRequestRef.setOrcId(orchestrator.getOrchestratorId());
            RefDeletionOperation<WorkflowOrchestratoDeleteRequestRef> refDeletionOperation =
                    getRefCreationOperation(dssLabels).getRefDeletionOperation();

            deleteOperation(refDeletionOperation, orchestratorDeleteRequestRef, uuid);

            //删除编排模式
            orchestratorService.deleteOrchestrator(orchestratorDeleteRequest, username);
        } catch (final Exception e) {
            DSSExceptionUtils.dealErrorException(60035, "failed to delete orchestrator", e, DSSProjectErrorException.class);
        }
        return orchestratorVo;
    }

    protected void deleteOperation(RefDeletionOperation<WorkflowOrchestratoDeleteRequestRef> refDeletionOperation,
        WorkflowOrchestratoDeleteRequestRef orchestratorDeleteRequestRef, String uuid) throws ExternalOperationFailedException {
        //调用orchestrator服务 创建编排模式、编排模式版本
        refDeletionOperation.deleteRef(orchestratorDeleteRequestRef);
        LOGGER.info("Operation deleteOrchestrator[DEV] for uuid {} success.", uuid);
    }

    /**
     * 获取更新的operation
     *
     * @param dssLabels 根据label获取对应label的创建operation
     *                  todo 目前已经写死
     * @return
     * @throws Exception
     */
    public RefCRUDService getRefCreationOperation(List<DSSLabel> dssLabels) throws Exception {
        OrchestratorFrameworkAppConn orcAppConn = AppConnManager.getAppConnManager().getAppConn(OrchestratorFrameworkAppConn.class);

        if (orcAppConn == null) {
            LOGGER.error("orcAppConn is null, will not go on creating orchestrator");
            DSSExceptionUtils.dealErrorException(60028, "orcAppConn is null", DSSProjectErrorException.class);
        }

        DevelopmentIntegrationStandard standard = ((OnlyDevelopmentAppConn)orcAppConn).getOrCreateDevelopmentStandard();
        if (standard == null) {
            LOGGER.error("standard is null, will not go on creating orchestrator");
            DSSExceptionUtils.dealErrorException(60028, "standard is null", DSSProjectErrorException.class);
        }
        AppInstance appInstance = orcAppConn.getAppDesc().getAppInstancesByLabels(dssLabels).get(0);

        RefCRUDService service = standard.getRefCRUDService(appInstance);
        return service;
    }
}
