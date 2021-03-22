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

package com.webank.wedatasphere.dss.orchestrator.publish;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.common.entity.IOEnv;
import com.webank.wedatasphere.dss.common.entity.IOType;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.core.exception.DSSOrchestratorErrorException;
import com.webank.wedatasphere.dss.orchestrator.core.ref.*;
import com.webank.wedatasphere.dss.orchestrator.core.ref.impl.DefaultWorkflowOrchestratorPublishRequestRef;
import com.webank.wedatasphere.dss.orchestrator.core.service.BMLService;
import com.webank.wedatasphere.dss.orchestrator.core.utils.OrchestratorUtils;
import com.webank.wedatasphere.dss.orchestrator.loader.OrchestratorManager;
import com.webank.wedatasphere.dss.orchestrator.publish.io.export.MetaExportService;
import com.webank.wedatasphere.dss.orchestrator.publish.io.input.MetaInputService;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.process.DevProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.ProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.ProdProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.TestProcessService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefExportService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefImportService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefPublishToSchedulerService;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.ProjectPublishToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.common.desc.DSSLabelUtils;
import com.webank.wedatasphere.dss.standard.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.DefaultRefFactory;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RefFactory;
import com.webank.wedatasphere.dss.standard.common.exception.NoSuchAppInstanceException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static com.webank.wedatasphere.dss.common.utils.ZipHelper.zipExportProject;

/**
 * @author allenlliu
 * @date 2020/12/3 10:37
 */
@Service
public class OrchestratorPublishServiceImpl implements OrchestratorPublishService {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrchestratorMapper orchestratorMapper;

    @Autowired
    private MetaExportService metaExportService;

    @Autowired
    private MetaInputService metaInputService;

    @Autowired
    private OrchestratorManager orchestratorManager;

    @Autowired
    private BMLService bmlService;

    private static final String DEFAULT_ORC_NAME = "default_orc";

    private RefFactory refFactory = new DefaultRefFactory();


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> exportOrchestrator(String userName,
                                                  String workspaceName,
                                                  Long orchestratorId,
                                                  Long orcVersionId,
                                                  String projectName,
                                                  List<DSSLabel> dssLabels,
                                                  boolean addOrcVersion, Workspace workspace) throws Exception {
        //1、导出info信息
        if (orcVersionId == null || orcVersionId < 0){
            LOGGER.info("orchestratorVersionId is {}", orcVersionId);
            //最简单的就是通过orcId来找到最新的versionId
            orcVersionId = orchestratorMapper.findLatestOrcVersionId(orchestratorId);
        }
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getOrchestratorVersion(orcVersionId);
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(dssOrchestratorVersion.getOrchestratorId());
        if (dssOrchestratorInfo != null) {
            String orcExportSaveBasePath = IoUtils.generateIOPath(userName, DEFAULT_ORC_NAME, "");
            Files.createDirectories(Paths.get(orcExportSaveBasePath).toAbsolutePath().normalize());
            //标记当前导出为project导出
            IoUtils.generateIOType(IOType.ORCHESTRATOR, orcExportSaveBasePath);
            //标记当前导出环境env
            IoUtils.generateIOEnv(orcExportSaveBasePath);
            metaExportService.export(dssOrchestratorInfo, orcExportSaveBasePath);
            LOGGER.info("{} 开始导出Orchestrator: {} 版本ID为: {} ", userName, dssOrchestratorInfo.getName(), orcVersionId);

            //2、导出第三方应用信息，如工作流、Visualis、Qualities
            ProcessService processService = getOrcProcessService(userName, workspaceName, dssOrchestratorInfo, dssLabels);
            if (null != processService) {
                RefExportService refExportService = (RefExportService) processService.
                        getRefOperationService().
                        stream().
                        filter(refOperationService -> refOperationService instanceof RefExportService).
                        findAny().
                        orElse(null);
                if (refExportService == null) {
                    LOGGER.error("for {} to export orchestrator {} refExportService is null", userName, dssOrchestratorInfo.getName());
                    DSSExceptionUtils.dealErrorException(60089, "refExportService is null", DSSErrorException.class);
                }
                OrchestratorExportRequestRef orchestratorExportRequestRef =
                        (OrchestratorExportRequestRef) refFactory.newRef(OrchestratorExportRequestRef.class,
                                refExportService.getClass().getClassLoader(), "com.webank.wedatasphere.dss.workflow.appconn.ref");
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("orchestrator Export Request Ref class is {}", orchestratorExportRequestRef.getClass());
                }
                orchestratorExportRequestRef.setAppId(dssOrchestratorVersion.getAppId());
                orchestratorExportRequestRef.setProjectId(dssOrchestratorVersion.getProjectId());
                orchestratorExportRequestRef.setProjectName(projectName);
                orchestratorExportRequestRef.setUserName(userName);
                orchestratorExportRequestRef.setWorkspace(workspace);
                AbstractResponseRef responseRef = (AbstractResponseRef) refExportService.
                        createRefExportOperation().
                        exportRef(orchestratorExportRequestRef);
                String resourceId = responseRef.getValue("resourceId").toString();
                String version = responseRef.getValue("version").toString();
                bmlService.downloadToLocalPath(userName, resourceId, version, orcExportSaveBasePath + "orc_flow.zip");
            }

            //打包导出工程
            String exportPath = zipExportProject(orcExportSaveBasePath);

            //3、打包新的zip包上传BML
            InputStream inputStream = bmlService.readLocalResourceFile(userName, exportPath);
            Map<String, Object> resultMap = bmlService.upload(userName, inputStream,
                    dssOrchestratorInfo.getName() + ".OrcExport", projectName);

            //4、判断导出后是否改变Orc的版本
            if (addOrcVersion) {
                Long orcIncreaseVersionId = orchestratorVersionIncrease(dssOrchestratorInfo.getId(),
                        userName, dssOrchestratorInfo.getComment(),
                        workspaceName, dssOrchestratorInfo, projectName, dssLabels);
                resultMap.put("orcVersionId", orcIncreaseVersionId);
            } else {
                resultMap.put("orcVersionId", orcVersionId);
            }

            return resultMap;
            //4、返回BML存储信息
        } else {
            throw new DSSErrorException(90038, "该Orchestrator的版本号不存在，请检查版本号是否正确");
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long importOrchestrator(String userName,
                                   String workspaceName,
                                   String projectName,
                                   Long projectId,
                                   String resourceId,
                                   String version,
                                   List<DSSLabel> dssLabels, Workspace workspace) throws Exception {
        Long appId = null;
        //1、下载BML的Orchestrator的导入包
        String inputZipPath = IoUtils.generateIOPath(userName, projectName, DEFAULT_ORC_NAME + ".zip");
        bmlService.downloadToLocalPath(userName, resourceId, version, inputZipPath);
        String inputPath = ZipHelper.unzip(inputZipPath);

        IOEnv ioEnv = IoUtils.readIOEnv(inputPath);
        //2、导入Info信息(导入冲突处理)
        List<DSSOrchestratorInfo> dssOrchestratorInfos = metaInputService.importOrchestrator(inputPath);
        DSSOrchestratorInfo importDssOrchestratorInfo = dssOrchestratorInfos.get(0);
        //通过UUID查找是否已经导入过
        DSSOrchestratorInfo existFlag = orchestratorMapper.getOrchestratorByUUID(importDssOrchestratorInfo.getUUID());
        //add 和update都需要更新成当前环境id信息，放到新的版本记录中
        //todo 跨环境必须保证工程ID一样，或者需要更新导入包中的所有工程ID为当前最新ID,不一致的话关系到上下文、第三方工程的映射问题
        if (null != existFlag) {
            //如果Orchestrator已经导入过，目前只更新版本信息，并更新基础信息name,其它信息不修改。
            orchestratorMapper.updateOrchestrator(importDssOrchestratorInfo);
        } else {
            orchestratorMapper.addOrchestrator(importDssOrchestratorInfo);
        }
        String flowZipPath = inputPath + File.separator + "orc_flow.zip";
        //3、上传工作流zip包到bml
        InputStream inputStream = bmlService.readLocalResourceFile(userName, flowZipPath);
        Map<String, Object> resultMap = bmlService.upload(userName, inputStream, importDssOrchestratorInfo.getName() + "_orc_flow.zip", projectName);
        String orcResourceId = resultMap.get("resourceId").toString();
        String orcBmlVersion = resultMap.get("version").toString();
        //4、导入版本Version信息
        DSSOrchestratorVersion dssOrchestratorVersion = new DSSOrchestratorVersion();
        dssOrchestratorVersion.setAppId(null);
        dssOrchestratorVersion.setComment("orchestrator import");
        dssOrchestratorVersion.setOrchestratorId(importDssOrchestratorInfo.getId());
        dssOrchestratorVersion.setContent("");
        dssOrchestratorVersion.setProjectId(projectId);
        dssOrchestratorVersion.setSource("Orchestrator create");
        dssOrchestratorVersion.setUpdater(userName);
        String oldVersion = orchestratorMapper.getLatestVersion(importDssOrchestratorInfo.getId());
        if (StringUtils.isNotEmpty(oldVersion)){
            dssOrchestratorVersion.setVersion(OrchestratorUtils.increaseVersion(oldVersion));
        }else{
            dssOrchestratorVersion.setVersion(OrchestratorUtils.generateNewVersion());
        }
        dssOrchestratorVersion.setUpdateTime(new Date());
        orchestratorMapper.addOrchestratorVersion(dssOrchestratorVersion);

        //6、导出第三方应用信息，如工作流、Visualis、Qualities
        DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(userName,
                workspaceName, importDssOrchestratorInfo.getType(), importDssOrchestratorInfo.getAppConnName(), dssLabels);
        AppConn orchestratorAppConn = dssOrchestrator.getAppConn();
        ProcessService processService = getOrcProcessService(userName, workspaceName, importDssOrchestratorInfo, dssLabels);
        if (null != processService) {
            RefImportService refImportService = (RefImportService) processService.getRefOperationService().stream()
                    .filter(refOperationService -> refOperationService instanceof RefImportService).findAny().orElse(null);
            OrchestratorImportRequestRef orchestratorImportRequestRef = null;
            try {
                orchestratorImportRequestRef =
                        (OrchestratorImportRequestRef) refFactory.newRef(OrchestratorImportRequestRef.class,
                                orchestratorAppConn.getClass().getClassLoader(), "com.webank.wedatasphere.dss.workflow.appconn.ref");
            } catch (final Exception e) {
                DSSExceptionUtils.dealErrorException(61001, "failed to new ref with class " + OrchestratorImportRequestRef.class.getName(), e, DSSErrorException.class);
            }
            orchestratorImportRequestRef.setResourceId(orcResourceId);
            orchestratorImportRequestRef.setBmlVersion(orcBmlVersion);
            orchestratorImportRequestRef.setProjectId(projectId);
            orchestratorImportRequestRef.setUserName(userName);
            orchestratorImportRequestRef.setWorkspaceName(workspaceName);
            orchestratorImportRequestRef.setWorkspace(workspace);
            orchestratorImportRequestRef.setProjectName(projectName);
            //todo getSourceEnv
            orchestratorImportRequestRef.setSourceEnv(ioEnv);
            orchestratorImportRequestRef.setOrcVersion(dssOrchestratorVersion.getVersion());
            OrchestratorImportResponseRef responseRef = (OrchestratorImportResponseRef) refImportService.createRefImportOperation().importRef(orchestratorImportRequestRef);
            appId = responseRef.getRefOrcId();
            String appContent = responseRef.getRefOrcContent();

            //更新返回內容
            dssOrchestratorVersion.setAppId(appId);
            dssOrchestratorVersion.setContent(appContent);
            orchestratorMapper.updateOrchestratorVersion(dssOrchestratorVersion);

        }
        return dssOrchestratorVersion.getOrchestratorId();
    }

    @Override
    public void publishOrchestrator(String userName,
                                    DSSProject project,
                                    Workspace workspace,
                                    List<Long> orcIdList,
                                    List<DSSLabel> dssLabels) throws NoSuchAppInstanceException, ExternalOperationFailedException {
        //6、导出第三方应用信息，如工作流、Visualis、Qualities
        Long toPublishOrcId = orcIdList.get(0);
        DSSOrchestratorInfo publishDSSOrchestratorInfo = orchestratorMapper.getOrchestrator(orcIdList.get(0));
        ProcessService processService = getOrcProcessService(userName, workspace.getWorkspaceName(), publishDSSOrchestratorInfo, dssLabels);
        if (null != processService) {
            RefPublishToSchedulerService refPublishService = (RefPublishToSchedulerService) processService.getRefOperationService().
                    stream().
                    filter(refOperationService -> refOperationService instanceof RefPublishToSchedulerService).
                    findAny().
                    orElse(null);
            if (null != refPublishService) {
                ProjectPublishToSchedulerRef projectPublishToSchedulerRef = new DefaultWorkflowOrchestratorPublishRequestRef();
                //把orcId装车第三方的AppId
                List<Long> refAppIdList = new ArrayList<>();
                //这个地方应该是要获取所有的已经发布过的orchestrator
                List<Long> publishedOrcIds = orchestratorMapper.getAllOrcIdsByProjectId(project.getId());
                if (!publishedOrcIds.contains(toPublishOrcId)){
                    publishedOrcIds.add(toPublishOrcId);
                }
                for (Long orcId : publishedOrcIds) {
                    DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionById(orcId);
                    refAppIdList.add(dssOrchestratorVersion.getAppId());
                }
                projectPublishToSchedulerRef.setOrcAppIds(refAppIdList);
                projectPublishToSchedulerRef.setProject(project);
                projectPublishToSchedulerRef.setWorkspace(workspace);
                projectPublishToSchedulerRef.setLabels(dssLabels);
                projectPublishToSchedulerRef.setUserName(userName);
                refPublishService.createRefScheduleOperation().createPublishToSchedulerStage().publishToScheduler(projectPublishToSchedulerRef);
                //做一个标记表示已经发布过了
                orchestratorMapper.setPublished(orcIdList.get(0));
            }
        }
    }


    private ProcessService getOrcProcessService(String userName,
                                                String workspaceName,
                                                DSSOrchestratorInfo dssOrchestratorInfo,
                                                List<DSSLabel> dssLabels) throws NoSuchAppInstanceException {
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
            LOGGER.error("Can not get dssOrchestrator from manager");
            return null;
        }
        return processService;
    }


    public Long orchestratorVersionIncrease(Long orcId,
                                            String userName,
                                            String comment,
                                            String workspaceName,
                                            DSSOrchestratorInfo dssOrchestratorInfo,
                                            String projectName,
                                            List<DSSLabel> dssLabels) throws Exception {
        //对于导出来说,json需替换 subflowID
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionById(orcId);

        // TODO: 2020/3/25 set updator(userID 修改为userName后)
        dssOrchestratorVersion.setUpdateTime(new Date());
        Long oldOrcVersionId = dssOrchestratorVersion.getId();
        String oldOrcVersion = dssOrchestratorVersion.getVersion();
        dssOrchestratorVersion.setId(null);
        // 如果是project发布 ,version都是01,如果是工作流发布,version + 1
        dssOrchestratorVersion.setVersion(OrchestratorUtils.increaseVersion(oldOrcVersion));

        //发布的comment应该更新到上个版本,而当前最新版本的comment应该修改为release from..
        dssOrchestratorVersion.setComment(String.format("release from version %s", oldOrcVersion));
        //更新老版本的comment
        DSSOrchestratorVersion updateCommentVersion = new DSSOrchestratorVersion();
        updateCommentVersion.setId(oldOrcVersionId);
        String realComment = comment != null ? comment : "release comment";
        updateCommentVersion.setComment(realComment);
        orchestratorMapper.updateOrchestratorVersion(updateCommentVersion);

        //要求AppConn对应第三方应用拷贝一个新的app出来关联，如工作流，需要新建一个新的工作流进行关联。
        ProcessService devProcessService = getOrcProcessService(userName, workspaceName, dssOrchestratorInfo, dssLabels);
        if(devProcessService == null){
            LOGGER.error("dev Process Service is null");
            throw new DSSErrorException(61105, "dev Process Service is null");
        }
        RefCRUDService refcrudservice = (RefCRUDService) devProcessService.getRefOperationService().
                stream().
                filter(refOperationService -> refOperationService instanceof RefCRUDService).
                findAny().
                orElse(null);
        if (null != refcrudservice) {
            try {
                OrchestratorCopyRequestRef orchestratorCopyRequestRef =
                        (OrchestratorCopyRequestRef) refFactory.newRef(OrchestratorCopyRequestRef.class,
                                refcrudservice.getClass().getClassLoader(), "com.webank.wedatasphere.dss.workflow.appconn.ref");
                orchestratorCopyRequestRef.setCopyOrcAppId(dssOrchestratorVersion.getAppId());
                orchestratorCopyRequestRef.setCopyOrcVersionId(dssOrchestratorVersion.getOrchestratorId());
                orchestratorCopyRequestRef.setUserName(userName);
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
            return dssOrchestratorVersion.getId();
        } else {
            throw new DSSOrchestratorErrorException(10023, "获取第三方应用的Ref为空，不能完成拷贝操作！");
        }
    }


}
