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
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.exception.DSSOrchestratorErrorException;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorCopyRequestRef;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorCopyResponseRef;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorCreateRequestRef;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorDeleteRequestRef;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorOpenRequestRef;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorUpdateRef;
import com.webank.wedatasphere.dss.orchestrator.core.utils.OrchestratorUtils;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.loader.OrchestratorManager;
import com.webank.wedatasphere.dss.orchestrator.loader.utils.OrchestratorLoaderUtils;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.standard.app.development.ref.CommonResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.UrlResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.service.RefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.service.RefQueryService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AppConnRefFactoryUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class OrchestratorServiceImpl implements OrchestratorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorServiceImpl.class);
    @Autowired
    private OrchestratorManager orchestratorManager;
    @Autowired
    private OrchestratorMapper orchestratorMapper;
    @Autowired
    private ContextService contextService;

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
        Pair<AppInstance, DevelopmentIntegrationStandard> standMap = OrchestratorLoaderUtils.getOrcDevelopStandard(userName, workspaceName, dssOrchestratorInfo, dssLabels);
        DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(userName,
                workspaceName, dssOrchestratorInfo.getType(), dssOrchestratorInfo.getAppConnName(), dssLabels);
        AppConn orchestratorAppConn = dssOrchestrator.getAppConn();
        if (null != standMap && null != orchestratorAppConn) {
            //访问工作流微模块创建工作流
            OrchestratorCreateRequestRef ref = AppConnRefFactoryUtils.newAppConnRef(OrchestratorCreateRequestRef.class,
                    orchestratorAppConn.getClass().getClassLoader(), dssOrchestratorInfo.getType());
            if (null != ref) {
                ref.setUserName(userName);
                ref.setWorkspaceName(workspaceName);
                ref.setProjectName(projectName);
                ref.setProjectId(projectId);
                ref.setDssOrchestratorInfo(dssOrchestratorInfo);
                String version = OrchestratorUtils.generateNewVersion();
                String contextId = contextService.createContextID(workspaceName, projectName, dssOrchestratorInfo.getName(), version, userName);
                ref.setContextID(contextId);
                LOGGER.info("Create a new ContextId: {} ", contextId);
                RefCRUDService crudService =standMap.getValue().getRefCRUDService(standMap.getKey());
                if (crudService != null) {
                    CommonResponseRef appRef = (CommonResponseRef) crudService
                            .getRefCreationOperation().createRef(ref);
                    if (null != appRef) {
                        DSSOrchestratorVersion dssOrchestratorVersion = new DSSOrchestratorVersion();
                        dssOrchestratorVersion.setOrchestratorId(dssOrchestratorInfo.getId());
                        dssOrchestratorVersion.setAppId(appRef.getOrcId());
                        dssOrchestratorVersion.setContent(appRef.getContent());
                        dssOrchestratorVersion.setComment(description);
                        dssOrchestratorVersion.setProjectId(projectId);
                        dssOrchestratorVersion.setSource("Orchestrator create");
                        dssOrchestratorVersion.setUpdater(userName);
                        dssOrchestratorVersion.setVersion(version);
                        dssOrchestratorVersion.setUpdateTime(new Date());
                        dssOrchestratorVersion.setFormatContextId(contextId);
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

        //todo 目前写死为工作流，这里应该是要和dssOrchestratorInfo.type作为参数传入
        AppConn appConn = AppConnManager.getAppConnManager().getAppConn("workflow");
        if(appConn==null){
            LOGGER.error("appConn is null,not update workflow");
            return;
        }
        OrchestratorUpdateRef orchestratorUpdateRef = AppConnRefFactoryUtils.newAppConnRef(OrchestratorUpdateRef.class,
                appConn.getClass().getClassLoader(), dssOrchestratorInfo.getType());
        if (orchestratorUpdateRef != null) {
            orchestratorUpdateRef.setOrcId(dssOrchestratorVersion.getAppId());
            orchestratorUpdateRef.setUserName(userName);
            orchestratorUpdateRef.setDescription(dssOrchestratorInfo.getComment());
            orchestratorUpdateRef.setOrcName(dssOrchestratorInfo.getName());
            orchestratorUpdateRef.setUses(dssOrchestratorInfo.getUses());
            //update ref orchestrator  info
            Pair<AppInstance,DevelopmentIntegrationStandard> standMap = OrchestratorLoaderUtils.getOrcDevelopStandard(userName, workspaceName, dssOrchestratorInfo, dssLabels);
            if (null != standMap ) {
                RefCRUDService crudService = standMap.getValue().getRefCRUDService(standMap.getKey());
                if (null != crudService) {
                    crudService.getRefUpdateOperation().updateRef(orchestratorUpdateRef);
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
        if(null== dssOrchestratorInfo){
            LOGGER.error("dssOrchestratorInfo is null,no need to  delete");
            return;
        }
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionById(orchestratorInfoId);

        //todo 是否需要删除版本信息

        //todo 删除版本信息对应的工作流信息

        OrchestratorDeleteRequestRef orchestratorDeleteRequestRef = null;
        try {
            //todo 目前写死为工作流，这里应该是要和dssOrchestratorInfo.type作为参数传入
            AppConn appConn = AppConnManager.getAppConnManager().getAppConn(dssOrchestratorInfo.getType());
            if(appConn == null){
                LOGGER.error("appConn is null, not delete {}." + dssOrchestratorInfo.getType());
                return;
            }
            orchestratorDeleteRequestRef = AppConnRefFactoryUtils.newAppConnRefByPackageName(OrchestratorDeleteRequestRef.class,
                    appConn.getClass().getClassLoader(), appConn.getClass().getPackage().getName());
        } catch (Exception e) {
            LOGGER.error("Failed to create a new ref for {}.", OrchestratorDeleteRequestRef.class, e);
        }
        assert orchestratorDeleteRequestRef != null;
        Pair<AppInstance,DevelopmentIntegrationStandard> standMap = OrchestratorLoaderUtils.getOrcDevelopStandard(userName, workspaceName, dssOrchestratorInfo, dssLabels);

        RefCRUDService refCRUDService = standMap.getValue().getRefCRUDService (standMap.getKey());

        //删除只需要
        orchestratorDeleteRequestRef.setAppId(dssOrchestratorVersion.getAppId());
        orchestratorDeleteRequestRef.setOrcId(orchestratorInfoId);
        orchestratorDeleteRequestRef.setUserName(userName);
        orchestratorDeleteRequestRef.setDSSLabels(dssLabels);
        if (null != refCRUDService) {
            refCRUDService.getRefDeletionOperation().deleteRef(orchestratorDeleteRequestRef);
        }

        orchestratorMapper.deleteOrchestrator(orchestratorInfoId);
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
        Pair<AppInstance,DevelopmentIntegrationStandard> standMap = OrchestratorLoaderUtils.getOrcDevelopStandard(userName, workspaceName, dssOrchestratorInfo, dssLabels);

        DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(userName,
                workspaceName, dssOrchestratorInfo.getType(), dssOrchestratorInfo.getAppConnName(), dssLabels);
        AppConn orchestratorAppConn = dssOrchestrator.getAppConn();

        try {
            orchestratorOpenRequestRef = AppConnRefFactoryUtils.newAppConnRefByPackageName(OrchestratorOpenRequestRef.class,
                    orchestratorAppConn.getClass().getClassLoader(), orchestratorAppConn.getClass().getPackage().getName());
        } catch (final Exception e) {
            LOGGER.error("Failed to open a new ref for {}", OrchestratorOpenRequestRef.class, e);
        }
        assert orchestratorOpenRequestRef != null;
        RefQueryService refQueryService =standMap.getValue().getRefQueryService (standMap.getKey());

        orchestratorOpenRequestRef.setRefAppId(dssOrchestratorVersion.getAppId());
        orchestratorOpenRequestRef.setOrchestratorId(orchestratorId);
        orchestratorOpenRequestRef.setUserName(userName);
        orchestratorOpenRequestRef.setSecondaryType(dssOrchestratorInfo.getSecondaryType());
        orchestratorOpenRequestRef.setDSSLabels(dssLabels);
        if (null != refQueryService) {
            UrlResponseRef urlResponseRef=(UrlResponseRef) refQueryService.getRefQueryOperation().query(orchestratorOpenRequestRef);
            return  urlResponseRef.getUrl();
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

    @Override
    public List<DSSOrchestratorVersion> getVersionByOrchestratorId(Long orchestratorId) {
        return orchestratorMapper.getVersionByOrchestratorId(orchestratorId);
    }

//    @Override
//    public OrchestratorInfo getOrchestratorInfo(String username, Long workflowId) {
//        LOGGER.info("{} ask the orcInfo for workflowId {}", username, workflowId);
//        OrchestratorInfo orchestratorInfo = orchestratorMapper.getOrcInfoByAppId(workflowId);
//        LOGGER.info("workflowId is {} , orcId is {}, orcVersionId is {}", workflowId, orchestratorInfo.getOrchestratorId(), orchestratorInfo.getOrchestratorVersionId());
//        return orchestratorInfo;
//    }

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
        String latestVersion = orchestratorMapper.getLatestVersion(orchestratorId);
        List<DSSLabel> labels = new ArrayList<>();
        labels.add(dssLabel);
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orchestratorId);
        String newVersion = OrchestratorUtils.increaseVersion(latestVersion);
        DSSOrchestratorVersion dssOrchestratorVersion = new DSSOrchestratorVersion();
        dssOrchestratorVersion.setId(orchestratorId);
        dssOrchestratorVersion.setVersion(newVersion);
        dssOrchestratorVersion.setUpdateTime(new Date());
        dssOrchestratorVersion.setProjectId(projectId);
        dssOrchestratorVersion.setUpdater(userName);
        dssOrchestratorVersion.setComment("回滚工作流到版本:" + version);
        dssOrchestratorVersion.setSource("rollback from version :" + version);
        Long appId = orchestratorMapper.getAppIdByVersion(orchestratorId, version);

        Pair<AppInstance,DevelopmentIntegrationStandard> standMap = OrchestratorLoaderUtils.getOrcDevelopStandard(userName, workspace.getWorkspaceName(), dssOrchestratorInfo, labels);

        if(standMap == null){
            LOGGER.error("dev stand Service is null");
            throw new DSSErrorException(61105, "dev stand Service is null");
        }
        RefCRUDService refcrudservice = standMap.getValue().getRefCRUDService(standMap.getKey());
        if (null != refcrudservice) {
            try {
                OrchestratorCopyRequestRef orchestratorCopyRequestRef =
                        AppConnRefFactoryUtils.newAppConnRef(OrchestratorCopyRequestRef.class,
                                refcrudservice.getClass().getClassLoader(), dssOrchestratorInfo.getType());
                orchestratorCopyRequestRef.setCopyOrcAppId(appId);
                orchestratorCopyRequestRef.setCopyOrcVersionId(dssOrchestratorVersion.getOrchestratorId());
                orchestratorCopyRequestRef.setUserName(userName);
                Field field = orchestratorCopyRequestRef.getClass().getDeclaredField("projectName");
                field.setAccessible(true);
                field.set(orchestratorCopyRequestRef, projectName);

                //5、生成上下文ContextId
                String contextId = contextService.createContextID(workspace.getWorkspaceName(), projectName, dssOrchestratorInfo.getName(), dssOrchestratorVersion.getVersion(), userName);
                dssOrchestratorVersion.setContextId(contextId);
                LOGGER.info("Create a new ContextId for import: {} ", contextId);

                orchestratorCopyRequestRef.setContextID(contextId);
                OrchestratorCopyResponseRef orchestratorCopyResponseRef =
                        (OrchestratorCopyResponseRef) refcrudservice.getRefCopyOperation().copyRef(orchestratorCopyRequestRef);
                dssOrchestratorVersion.setAppId(orchestratorCopyResponseRef.getCopyTargetAppId());
                dssOrchestratorVersion.setContent(orchestratorCopyResponseRef.getCopyTargetContent());
                dssOrchestratorVersion.setFormatContextId(contextId);
                //update appConn node contextId
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
