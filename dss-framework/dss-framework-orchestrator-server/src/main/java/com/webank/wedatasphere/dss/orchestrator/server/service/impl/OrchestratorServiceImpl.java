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

package com.webank.wedatasphere.dss.orchestrator.server.service.impl;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.common.entity.*;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.orchestrator.common.entity.*;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.core.exception.DSSOrchestratorErrorException;
import com.webank.wedatasphere.dss.orchestrator.core.ref.*;
import com.webank.wedatasphere.dss.orchestrator.core.utils.OrchestratorUtils;
import com.webank.wedatasphere.dss.orchestrator.loader.OrchestratorManager;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.process.DevProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.ProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.ProdProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.TestProcessService;
import com.webank.wedatasphere.dss.standard.app.development.query.RefVisibleService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.desc.DSSLabelUtils;
import com.webank.wedatasphere.dss.standard.common.entity.ref.DefaultRefFactory;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RefFactory;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;


/**
 * @author allenlliu
 * @date 2020/11/13 15:17
 */
@Service
public class OrchestratorServiceImpl implements OrchestratorService {


    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorServiceImpl.class);


    @Autowired
    private OrchestratorManager orchestratorManager;

    @Autowired
    private OrchestratorMapper orchestratorMapper;

    @Autowired
    private ContextService contextService;

    private RefFactory<RequestRef> refFactory = new DefaultRefFactory<>();


    private final static Logger logger = LoggerFactory.getLogger(OrchestratorServiceImpl.class);


    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrchestratorVo createOrchestrator(String userName,
                                             String workspaceName,
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
        orchestratorMapper.addOrchestrator(dssOrchestratorInfo);
        ProcessService processService = getOrcProcessService(userName, workspaceName, dssOrchestratorInfo, dssLabels);
        DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(userName,
                workspaceName, dssOrchestratorInfo.getType(), dssOrchestratorInfo.getAppConnName(), dssLabels);
        AppConn orchestratorAppConn = dssOrchestrator.getAppConn();
        if (null != processService && null != orchestratorAppConn) {
            //访问工作流微模块创建工作流
            OrchestratorCreateRequestRef ref = (OrchestratorCreateRequestRef) refFactory.newRef(OrchestratorCreateRequestRef.class,
                    orchestratorAppConn.getClass().getClassLoader(), "com.webank.wedatasphere.dss.workflow.appconn.ref");
            if (null != ref) {
                ref.setUserName(userName);
                ref.setWorkspaceName(workspaceName);
                ref.setProjectName(projectName);
                ref.setProjectId(projectId);
                ref.setDssOrchestratorInfo(dssOrchestratorInfo);
                String version = OrchestratorUtils.generateNewVersion();
                String contextId = contextService.createContextID(workspaceName, projectName, dssOrchestratorInfo.getName(), version, userName);
                ref.setContextIDStr(contextId);
                LOGGER.info("Create a new ContextId: {} ", contextId);
                RefCRUDService crudService = (RefCRUDService) processService.getRefOperationService().stream()
                        .filter(refOperationService -> refOperationService instanceof RefCRUDService).findAny().orElse(null);
                if (crudService != null) {
                    OrchestratorCreateResponseRef appRef = (OrchestratorCreateResponseRef) crudService
                            .createTaskCreationOperation().createRef(ref);
                    if (null != appRef) {
                        DSSOrchestratorVersion dssOrchestratorVersion = new DSSOrchestratorVersion();
                        dssOrchestratorVersion.setOrchestratorId(dssOrchestratorInfo.getId());
                        dssOrchestratorVersion.setAppId(appRef.getOrchestratorId());
                        dssOrchestratorVersion.setContent(appRef.getContent());
                        dssOrchestratorVersion.setComment(description);
                        dssOrchestratorVersion.setProjectId(projectId);
                        dssOrchestratorVersion.setSource("Orchestrator create");
                        dssOrchestratorVersion.setUpdater(userName);
                        dssOrchestratorVersion.setVersion(version);
                        dssOrchestratorVersion.setUpdateTime(new Date());
                        orchestratorMapper.addOrchestratorVersion(dssOrchestratorVersion);
                        orchestratorVo.setDssOrchestratorInfo(dssOrchestratorInfo);
                        orchestratorVo.setDssOrchestratorVersion(dssOrchestratorVersion);
                    } else {
                        LOGGER.error("create ref return appRef is null");
                        throw new DSSErrorException(100068, "Use appInstance create workflow failed!");
                    }
                } else {
                    LOGGER.error("crudService is null can not continue");
                    throw new DSSErrorException(60092, "create crudService is null");
                }
            } else {
                LOGGER.error("ref is null can not continue");
                throw new DSSErrorException(60093, "ref is null can not continue");
            }
        } else {
            throw new DSSErrorException(100069, "Can not find correct devCRUDService");
        }
        return orchestratorVo;
    }

    @Override
    public void updateOrchestrator(String userName,
                                   String workspaceName,
                                   DSSOrchestratorInfo dssOrchestratorInfo,
                                   List<DSSLabel> dssLabels) throws Exception {
        orchestratorMapper.updateOrchestrator(dssOrchestratorInfo);
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionById(dssOrchestratorInfo.getId());
        OrchestratorUpdateRef orchestratorUpdateRef = (OrchestratorUpdateRef) refFactory.newRef(OrchestratorUpdateRef.class,
                this.getClass().getClassLoader(), "com.webank.wedatasphere.dss.workflow.appconn.ref");
        if (orchestratorUpdateRef != null) {
            orchestratorUpdateRef.setOrcID(dssOrchestratorVersion.getAppId());
            orchestratorUpdateRef.setUserName(userName);
            orchestratorUpdateRef.setDescription(dssOrchestratorInfo.getComment());
            orchestratorUpdateRef.setOrchestratorName(dssOrchestratorInfo.getName());
            orchestratorUpdateRef.setUses(dssOrchestratorInfo.getUses());
            //update ref orchestrator  info
            ProcessService processService = getOrcProcessService(userName, workspaceName, dssOrchestratorInfo, dssLabels);
            if (processService != null) {
                RefCRUDService crudService = (RefCRUDService) processService.getRefOperationService().stream()
                        .filter(refOperationService -> refOperationService instanceof RefCRUDService).findAny().orElse(null);
                if (null != crudService) {
                    crudService.createRefUpdateOperation().updateRef(orchestratorUpdateRef);
                }
            } else {
                LOGGER.error("processService is null,can not do crud service");
                DSSExceptionUtils.dealErrorException(60056, "process service is null, can not do crud", DSSErrorException.class);
            }

        } else {
            LOGGER.error("update ref is null, can not continue doing");
            DSSExceptionUtils.dealErrorException(61123, "update ref is null, can not continue doing", DSSErrorException.class);
        }

    }

    @Override
    public void deleteOrchestrator(String userName,
                                   String workspaceName,
                                   String projectName,
                                   Long orchestratorInfoId,
                                   List<DSSLabel> dssLabels) throws Exception {

        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorInfoId);
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionById(orchestratorInfoId);

        orchestratorMapper.deleteOrchestrator(orchestratorInfoId);
        //todo 是否需要删除版本信息

        //todo 删除版本信息对应的工作流信息

        OrchestratorDeleteRequestRef orchestratorDeleteRequestRef = null;
        try {
            orchestratorDeleteRequestRef = (OrchestratorDeleteRequestRef) refFactory.newRef(OrchestratorDeleteRequestRef.class,
                    this.getClass().getClassLoader(), "com.webank.wedatasphere.dss.workflow.appconn.ref");
        } catch (Exception e) {
            LOGGER.error("Failed to create a new ref for {}", OrchestratorDeleteRequestRef.class, e);
        }
        assert orchestratorDeleteRequestRef != null;

        ProcessService processService = getOrcProcessService(userName, workspaceName, dssOrchestratorInfo, dssLabels);
        RefCRUDService refCRUDService = (RefCRUDService) processService.getRefOperationService().stream().
                filter(refOperationService -> refOperationService instanceof RefCRUDService).findAny().orElse(null);

        //删除只需要
        orchestratorDeleteRequestRef.setAppId(dssOrchestratorVersion.getAppId());
        orchestratorDeleteRequestRef.setOrchestratorId(orchestratorInfoId);
        orchestratorDeleteRequestRef.setUserName(userName);
        if (null != refCRUDService) {
            refCRUDService.createRefDeletionOperation().deleteRef(orchestratorDeleteRequestRef);
        }
    }

    @Override
    public List<OrchestratorVo> getOrchestratorVoList(List<Long> orchestratorIds) {
        List<OrchestratorVo> orchestratorVoList = new ArrayList<>();
        orchestratorIds.stream().forEach(orchestratorId -> {
            OrchestratorVo orchestratorVo = getOrchestratorVoById(orchestratorId);
            orchestratorVoList.add(orchestratorVo);
        });
        return orchestratorVoList;
    }

    @Override
    public String openOrchestrator(String userName, String workspaceName, Long orchestratorId, List<DSSLabel> dssLabels) throws Exception {
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorId);
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionById(orchestratorId);
        if (null == dssOrchestratorInfo || null == dssOrchestratorVersion) {
            throw new DSSOrchestratorErrorException(1000856, "can not find orc from db for orcId: " + orchestratorId);
        }
        OrchestratorOpenRequestRef orchestratorOpenRequestRef = null;
        ProcessService processService = getOrcProcessService(userName, workspaceName, dssOrchestratorInfo, dssLabels);
        DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(userName,
                workspaceName, dssOrchestratorInfo.getType(), dssOrchestratorInfo.getAppConnName(), dssLabels);
        AppConn orchestratorAppConn = dssOrchestrator.getAppConn();
        try {
            orchestratorOpenRequestRef = (OrchestratorOpenRequestRef) refFactory.newRef(OrchestratorOpenRequestRef.class,
                    orchestratorAppConn.getClass().getClassLoader(), "com.webank.wedatasphere.dss.workflow.appconn.ref");
        } catch (final Exception e) {
            LOGGER.error("Failed to open a new ref for {}", OrchestratorOpenRequestRef.class, e);
        }
        assert orchestratorOpenRequestRef != null;
        RefVisibleService refVisibleService = (RefVisibleService) processService.getRefOperationService().stream().
                filter(refOperationService -> refOperationService instanceof RefVisibleService).findAny().orElse(null);

        orchestratorOpenRequestRef.setRefAppId(dssOrchestratorVersion.getAppId());
        orchestratorOpenRequestRef.setOrchestratorId(orchestratorId);
        orchestratorOpenRequestRef.setUserName(userName);
        orchestratorOpenRequestRef.setSecondaryType(dssOrchestratorInfo.getSecondaryType());
        orchestratorOpenRequestRef.setDSSLabels(dssLabels);
        if (null != refVisibleService) {
            return refVisibleService.getRefVisibleOperation().getRefIFrame(orchestratorOpenRequestRef).getUrl();
        }
        return null;
    }

    @Override
    public OrchestratorVo getOrchestratorVoById(Long orchestratorId) {

        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorId);
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionById(orchestratorId);

        OrchestratorVo orchestratorVo = new OrchestratorVo();
        orchestratorVo.setDssOrchestratorInfo(dssOrchestratorInfo);
        orchestratorVo.setDssOrchestratorVersion(dssOrchestratorVersion);
        return orchestratorVo;
    }


    private ProcessService getOrcProcessService(String userName,
                                                String workspaceName,
                                                DSSOrchestratorInfo dssOrchestratorInfo,
                                                List<DSSLabel> dssLabels) throws Exception {
        ProcessService processService = null;
        DevelopmentIntegrationStandard developmentIntegrationStandard = null;
        DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(userName,
                workspaceName, dssOrchestratorInfo.getType(), dssOrchestratorInfo.getAppConnName(), dssLabels);
        if (null != dssOrchestrator) {
            AppConn orchestratorAppConn = dssOrchestrator.getAppConn();
            for (AppStandard appStandard : orchestratorAppConn.getAppStandards()) {
                if (appStandard instanceof DevelopmentIntegrationStandard) {
                    developmentIntegrationStandard = (DevelopmentIntegrationStandard) appStandard;
                }
            }
            //todo labels判别
            List<AppInstance> appInstance = orchestratorAppConn.getAppDesc().getAppInstancesByLabels(dssLabels);
            if (appInstance.size() > 0) {
                List<ProcessService> processServices = developmentIntegrationStandard.getProcessServices();
                if (DSSLabelUtils.belongToDev(dssLabels)) {
                    processService = processServices.stream().filter(processServiceTmp -> processServiceTmp instanceof DevProcessService).findAny().orElse(null);

                } else if (DSSLabelUtils.belongToTest(dssLabels)) {
                    processService = processServices.stream().filter(processServiceTmp -> processServiceTmp instanceof TestProcessService).findAny().orElse(null);
                } else {
                    processService = processServices.stream().filter(processServiceTmp -> processServiceTmp instanceof ProdProcessService).findAny().orElse(null);
                }
            }
        } else {
            logger.error("Can not get dssOrchestrator from manager");
            return null;
        }
        return processService;
    }

    @Override
    public List<DSSOrchestratorVersion> getVersionByOrchestratorId(Long orchestratorId) {
        return orchestratorMapper.getVersionByOrchestratorId(orchestratorId);
    }

    @Override
    public OrchestratorInfo getOrchestratorInfo(String username, Long workflowId) {
        LOGGER.info("{} ask the orcInfo for workflowId {}", username, workflowId);
        OrchestratorInfo orchestratorInfo = orchestratorMapper.getOrcInfoByAppId(workflowId);
        LOGGER.info("workflowId is {} , orcId is {}, orcVersionId is {}", workflowId, orchestratorInfo.getOrchestratorId(), orchestratorInfo.getOrchestratorVersionId());
        return orchestratorInfo;
    }

    @Override
    public List<OrchestratorProdDetail> getOrchestratorDetails(String username, Long projectId, String dssLabel) {
        LOGGER.info("{} ask the orc prod detail for projectId {}", username, projectId);
        List<OrchestratorProdDetail> orchestratorProdDetails = orchestratorMapper.getOrchestratorProdDetails(projectId);
        List<OrchestratorProdDetail> realDetails = new ArrayList<>(orchestratorProdDetails);
        LOGGER.info("projectId is {}, orcDetails is {} and class is {}", projectId, realDetails, realDetails.getClass());
        return realDetails;
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
    public String rollbackOrchestrator(String username, Long projectId, String projectName,
                                       Long orchestratorId, String version, String dssLabel, Workspace workspace) throws Exception {
        //1.新建一个版本
        //2.然后将version的版本内容进行去workflow进行cp
        //3.然后把生产的内容进行update到数据库
        String latestVersion = orchestratorMapper.getLatestVersion(orchestratorId);
        DSSLabel label = new DSSLabel(dssLabel);
        List<DSSLabel> labels = new ArrayList<>();
        labels.add(label);
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorId);
        String newVersion = OrchestratorUtils.increaseVersion(latestVersion);
        DSSOrchestratorVersion dssOrchestratorVersion = new DSSOrchestratorVersion();
        dssOrchestratorVersion.setId(orchestratorId);
        dssOrchestratorVersion.setVersion(newVersion);
        dssOrchestratorVersion.setUpdateTime(new Date());
        dssOrchestratorVersion.setProjectId(projectId);
        dssOrchestratorVersion.setUpdater(username);
        dssOrchestratorVersion.setComment("回滚工作流到版本:" + version);
        dssOrchestratorVersion.setSource("rollback from version :" + version);
        Long appId = orchestratorMapper.getAppIdByVersion(orchestratorId, version);
        ProcessService processService = getOrcProcessService(username, workspace.getWorkspaceName(), dssOrchestratorInfo, labels);
        if(processService == null){
            LOGGER.error("dev Process Service is null");
            throw new DSSErrorException(61105, "dev Process Service is null");
        }
        RefCRUDService refcrudservice = (RefCRUDService) processService.getRefOperationService().
                stream().
                filter(refOperationService -> refOperationService instanceof RefCRUDService).
                findAny().
                orElse(null);
        if (null != refcrudservice) {
            try {
                OrchestratorCopyRequestRef orchestratorCopyRequestRef =
                        (OrchestratorCopyRequestRef) refFactory.newRef(OrchestratorCopyRequestRef.class,
                                refcrudservice.getClass().getClassLoader(), "com.webank.wedatasphere.dss.workflow.appconn.ref");
                orchestratorCopyRequestRef.setCopyOrcAppId(appId);
                orchestratorCopyRequestRef.setCopyOrcVersionId(dssOrchestratorVersion.getOrchestratorId());
                orchestratorCopyRequestRef.setUserName(username);
                Field field = orchestratorCopyRequestRef.getClass().getDeclaredField("projectName");
                field.setAccessible(true);
                field.set(orchestratorCopyRequestRef, projectName);
                OrchestratorCopyResponseRef orchestratorCopyResponseRef =
                        (OrchestratorCopyResponseRef) refcrudservice.createRefCopyOperation().copyRef(orchestratorCopyRequestRef);
                dssOrchestratorVersion.setAppId(orchestratorCopyResponseRef.getCopyTargetAppId());
                dssOrchestratorVersion.setContent(orchestratorCopyResponseRef.getCopyTargetContent());
                //update appjoint node contextId
                orchestratorMapper.addOrchestratorVersion(dssOrchestratorVersion);
            } catch (final Throwable t) {
                LOGGER.error("Faild to copy app in orchestrator server", t);
                DSSExceptionUtils.dealErrorException(60099, "Faild to copy app in orchestrator server", t, DSSOrchestratorErrorException.class);
            }
            return dssOrchestratorVersion.getVersion();
        } else {
            throw new DSSOrchestratorErrorException(10023, "获取第三方应用的Ref为空，不能完成拷贝操作！");
        }
    }
}
