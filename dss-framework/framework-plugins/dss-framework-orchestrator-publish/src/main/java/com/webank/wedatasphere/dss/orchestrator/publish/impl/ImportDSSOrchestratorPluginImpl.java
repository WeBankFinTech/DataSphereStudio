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

package com.webank.wedatasphere.dss.orchestrator.publish.impl;

import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.DSSLabelUtil;
import com.webank.wedatasphere.dss.common.utils.*;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestImportOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.plugin.AbstractDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.orchestrator.core.utils.OrchestratorUtils;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.db.hook.AddOrchestratorVersionHook;
import com.webank.wedatasphere.dss.orchestrator.loader.OrchestratorManager;
import com.webank.wedatasphere.dss.orchestrator.publish.ImportDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.publish.conf.DSSOrchestratorConf;
import com.webank.wedatasphere.dss.orchestrator.publish.io.export.MetaExportService;
import com.webank.wedatasphere.dss.orchestrator.publish.io.input.MetaInputService;
import com.webank.wedatasphere.dss.orchestrator.publish.utils.OrchestrationDevelopmentOperationUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.service.RefImportService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.webank.wedatasphere.dss.common.utils.IoUtils.FLOW_META_DIRECTORY_NAME;
import static com.webank.wedatasphere.dss.orchestrator.publish.impl.ExportDSSOrchestratorPluginImpl.DEFAULT_ORC_NAME;


@Component
public class ImportDSSOrchestratorPluginImpl extends AbstractDSSOrchestratorPlugin implements ImportDSSOrchestratorPlugin {

    @Autowired
    @Qualifier("orcMetaInputService")
    private MetaInputService metaInputService;
    @Autowired
    @Qualifier("orcMetaExportService")
    private MetaExportService metaExportService;
    @Autowired
    private OrchestratorMapper orchestratorMapper;
    @Autowired
    @Qualifier("orchestratorBmlService")
    private BMLService bmlService;
    @Autowired
    private ContextService contextService;
    @Autowired
    private OrchestratorManager orchestratorManager;

    @Autowired
    private AddOrchestratorVersionHook addOrchestratorVersionHook;

    @Override
    public DSSOrchestratorVersion importOrchestratorNew(RequestImportOrchestrator requestImportOrchestrator) throws Exception {
        String userName = requestImportOrchestrator.getUserName();
        String projectName = requestImportOrchestrator.getProjectName();
        Long projectId = requestImportOrchestrator.getProjectId();
        String resourceId = requestImportOrchestrator.getResourceId();
        String version = requestImportOrchestrator.getBmlVersion();
        List<DSSLabel> dssLabels = requestImportOrchestrator.getDssLabels();
        Workspace workspace = requestImportOrchestrator.getWorkspace();

        //1、下载BML的Orchestrator的导入包
        // /appcom/tmp/dss/yyyyMMddHHmmssSSS/arionliu
        String tempPath = IoUtils.generateTempIOPath(userName);
        // /appcom/tmp/dss/yyyyMMddHHmmssSSS/arionliu/projectxxx.zip
        String inputZipPath = IoUtils.addFileSeparator(tempPath, projectName + ".zip");
        bmlService.downloadToLocalPath(userName, resourceId, version, inputZipPath);
        // /appcom/tmp/dss/yyyyMMddHHmmssSSS/arionliu/projectxxx
        String projectPath = ZipHelper.unzip(inputZipPath,true);
        String flowName = IoUtils.getSubdirectoriesNames(projectPath).stream().filter(name -> !name.startsWith("."))
                .findFirst().orElseThrow(() -> new DSSRuntimeException("import package has no flow（未导入任何工作流，请检查导入包格式）"));
        String flowMetaPath = IoUtils.addFileSeparator(projectPath, FLOW_META_DIRECTORY_NAME, flowName);

        //2、导入Info信息(导入冲突处理)
        DSSOrchestratorInfo importDssOrchestratorInfo = metaInputService.importOrchestratorNew(flowMetaPath);
        importDssOrchestratorInfo.setProjectId(projectId);
        importDssOrchestratorInfo.setWorkspaceId(workspace.getWorkspaceId());
        //复制工程，直接使用新的UUID和复制后的工程ID
        if (requestImportOrchestrator.getCopyProjectId() != null
                && StringUtils.isNotBlank(requestImportOrchestrator.getCopyProjectName())) {
            projectId = requestImportOrchestrator.getCopyProjectId();
            importDssOrchestratorInfo.setProjectId(projectId);
            importDssOrchestratorInfo.setUUID(UUID.randomUUID().toString());
        }
        //根据工程ID和编排名称查询
        String uuid = orchestratorMapper.getOrcNameByParam(importDssOrchestratorInfo.getProjectId(), importDssOrchestratorInfo.getName());
        //通过UUID查找是否已经导入过
        DSSOrchestratorInfo existFlag = orchestratorMapper.getOrchestratorByUUID(importDssOrchestratorInfo.getUUID());
        //add 和update都需要更新成当前环境id信息，放到新的版本记录中
        //todo 跨环境必须保证工程ID一样，或者需要更新导入包中的所有工程ID为当前最新ID,不一致的话关系到上下文、第三方工程的映射问题
        if (null != existFlag) {
            //判断是否存在相同名称的编排
            if (StringUtils.isNotBlank(uuid) && !uuid.equals(importDssOrchestratorInfo.getUUID())) {
                DSSExceptionUtils
                        .dealErrorException(61002, "The same orchestration name already exists", DSSErrorException.class);
            }
            importDssOrchestratorInfo.setId(existFlag.getId());
        } else {
            //判断是否存在相同名称的编排
            if (StringUtils.isNotBlank(uuid)) {
                DSSExceptionUtils
                        .dealErrorException(61002, "The same orchestration name already exists", DSSErrorException.class);
            }
            //使用生产环境的id
            importDssOrchestratorInfo.setId(null);
            importDssOrchestratorInfo.setCreator(userName);
            importDssOrchestratorInfo.setCreateTime(new Date());
            //兼容
            if (importDssOrchestratorInfo.getWorkspaceId() == null) {
                importDssOrchestratorInfo.setWorkspaceId(workspace.getWorkspaceId());
            }
            if (StringUtils.isEmpty(importDssOrchestratorInfo.getOrchestratorMode())) {
                importDssOrchestratorInfo.setOrchestratorMode("pom_work_flow");
            }
            if (StringUtils.isEmpty(importDssOrchestratorInfo.getOrchestratorWay())) {
                importDssOrchestratorInfo.setOrchestratorWay(",pom_work_flow_DAG,");
            }
        }

        //4、导入版本Version信息
        DSSOrchestratorVersion dssOrchestratorVersion = new DSSOrchestratorVersion();
        dssOrchestratorVersion.setAppId(null);
        dssOrchestratorVersion.setComment("orchestrator import");
        dssOrchestratorVersion.setOrchestratorId(importDssOrchestratorInfo.getId());
        dssOrchestratorVersion.setContent("");
        dssOrchestratorVersion.setProjectId(projectId);
        dssOrchestratorVersion.setSource("Orchestrator create");
        dssOrchestratorVersion.setUpdater(userName);
        //生产导入：默认是为无效，除非开启直接有效（在生成环境独立部署的时候，导入和发布是分开的）；开发环境为有效
        int valid = DSSOrchestratorConf.DSS_IMPORT_VALID_IMMEDIATELY.getValue() || DSSLabelUtil.isDevEnv(dssLabels) ? 1 : 0;
        dssOrchestratorVersion.setValidFlag(valid);

        DSSOrchestratorVersion oldVersion  = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(importDssOrchestratorInfo.getId(), 1);
        if (oldVersion!=null) {
            dssOrchestratorVersion.setVersion(OrchestratorUtils.increaseVersion(oldVersion.getVersion()));
        } else {
            dssOrchestratorVersion.setVersion(OrchestratorUtils.generateNewVersion());
        }

        //5、生成上下文ContextId
        String contextId = contextService.createContextID(workspace.getWorkspaceName(), projectName, importDssOrchestratorInfo.getName(), dssOrchestratorVersion.getVersion(), userName);
        dssOrchestratorVersion.setFormatContextId(contextId);
        LOGGER.info("Create a new ContextId for import: {} ", contextId);

        //6、导入第三方应用信息，如工作流、Visualis、Qualities
        DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(userName,
                workspace.getWorkspaceName(), importDssOrchestratorInfo.getType(), dssLabels);
        Long finalProjectId = projectId;
        RefJobContentResponseRef responseRef = OrchestrationDevelopmentOperationUtils.tryOrchestrationOperation(importDssOrchestratorInfo,
                dssOrchestrator, userName, workspace, dssLabels,
                DevelopmentIntegrationStandard::getRefImportService,
                developmentService -> ((RefImportService) developmentService).getRefImportOperation(),
                dssContextRequestRef -> dssContextRequestRef.setContextId(contextId),
                projectRefRequestRef -> projectRefRequestRef.setRefProjectId(finalProjectId).setProjectName(projectName),
                (developmentOperation, developmentRequestRef) -> {
                    ImportRequestRef requestRef = (ImportRequestRef) developmentRequestRef;
                    requestRef.setResourceMap(MapUtils.newCommonMap(ImportRequestRef.RESOURCE_ID_KEY, resourceId, ImportRequestRef.RESOURCE_VERSION_KEY, version));
                    requestRef.setNewVersion(dssOrchestratorVersion.getVersion());
                    return ((RefImportOperation) developmentOperation).importRef(requestRef);
                }, "import");
        long orchestrationId = (Long) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_ID_KEY);
        String orchestrationContent = (String) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_CONTENT_KEY);
        List<String[]> paramConfTemplateIds =  (List<String[]>) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_FLOWID_PARAMCONF_TEMPLATEID_TUPLES_KEY);
        if(null  != existFlag){
            if(oldVersion!=null) {
                //如果生产中心的所有orc版本的valid_flag都是0（之前的所有发布都在convert期间失败了），那么oldVersion是为空的。
                addOrchestratorVersionHook.beforeAdd(oldVersion, Collections.emptyMap());
            }
            //如果Orchestrator已经导入过，目前只更新版本信息，并更新基础信息name,其它信息不修改。
            orchestratorMapper.updateOrchestrator(importDssOrchestratorInfo);
        }else{
            orchestratorMapper.addOrchestrator(importDssOrchestratorInfo);
        }

        //更新返回內容
        dssOrchestratorVersion.setUpdateTime(new Date());
        dssOrchestratorVersion.setAppId(orchestrationId);
        dssOrchestratorVersion.setContent(orchestrationContent);
        dssOrchestratorVersion.setOrchestratorId(importDssOrchestratorInfo.getId());

        orchestratorMapper.addOrchestratorVersion(dssOrchestratorVersion);
        addOrchestratorVersionHook.afterAdd(dssOrchestratorVersion, Collections.singletonMap(OrchestratorRefConstant.ORCHESTRATION_FLOWID_PARAMCONF_TEMPLATEID_TUPLES_KEY,paramConfTemplateIds));
        LOGGER.info("import orchestrator success,orcId:{},appId:{}",importDssOrchestratorInfo.getId(),orchestrationId);

        return dssOrchestratorVersion;
    }

    @Override
    public DSSOrchestratorVersion importOrchestrator(RequestImportOrchestrator requestImportOrchestrator) throws Exception {
        String userName = requestImportOrchestrator.getUserName();
        String projectName = requestImportOrchestrator.getProjectName();
        Long projectId = requestImportOrchestrator.getProjectId();
        String resourceId = requestImportOrchestrator.getResourceId();
        String version = requestImportOrchestrator.getBmlVersion();
        List<DSSLabel> dssLabels = requestImportOrchestrator.getDssLabels();
        Workspace workspace = requestImportOrchestrator.getWorkspace();

        //1、下载BML的Orchestrator的导入包
        String inputZipPath = IoUtils.generateIOPath(userName, projectName, DEFAULT_ORC_NAME + ".zip");
        bmlService.downloadToLocalPath(userName, resourceId, version, inputZipPath);
        String inputPath = ZipHelper.unzip(inputZipPath);

        //2、导入Info信息(导入冲突处理)
        List<DSSOrchestratorInfo> dssOrchestratorInfos = metaInputService.importOrchestrator(inputPath);
        DSSOrchestratorInfo importDssOrchestratorInfo = dssOrchestratorInfos.get(0);
        importDssOrchestratorInfo.setProjectId(projectId);
        importDssOrchestratorInfo.setWorkspaceId(workspace.getWorkspaceId());
        //复制工程，直接使用新的UUID和复制后的工程ID
        if (requestImportOrchestrator.getCopyProjectId() != null
                && StringUtils.isNotBlank(requestImportOrchestrator.getCopyProjectName())) {
            projectId = requestImportOrchestrator.getCopyProjectId();
            importDssOrchestratorInfo.setProjectId(projectId);
            importDssOrchestratorInfo.setUUID(UUID.randomUUID().toString());
        }
        //根据工程ID和编排名称查询
        String uuid = orchestratorMapper.getOrcNameByParam(importDssOrchestratorInfo.getProjectId(), importDssOrchestratorInfo.getName());
        //通过UUID查找是否已经导入过
        DSSOrchestratorInfo existFlag = orchestratorMapper.getOrchestratorByUUID(importDssOrchestratorInfo.getUUID());
        //add 和update都需要更新成当前环境id信息，放到新的版本记录中
        //todo 跨环境必须保证工程ID一样，或者需要更新导入包中的所有工程ID为当前最新ID,不一致的话关系到上下文、第三方工程的映射问题
        if (null != existFlag) {
            //判断是否存在相同名称的编排
            if (StringUtils.isNotBlank(uuid) && !uuid.equals(importDssOrchestratorInfo.getUUID())) {
                DSSExceptionUtils
                        .dealErrorException(61002, "The same orchestration name already exists", DSSErrorException.class);
            }
            importDssOrchestratorInfo.setId(existFlag.getId());
        } else {
            //判断是否存在相同名称的编排
            if (StringUtils.isNotBlank(uuid)) {
                DSSExceptionUtils
                        .dealErrorException(61002, "The same orchestration name already exists", DSSErrorException.class);
            }
            //使用生产环境的id
            importDssOrchestratorInfo.setId(null);
            importDssOrchestratorInfo.setCreator(userName);
            importDssOrchestratorInfo.setCreateTime(new Date());
            //0.x工作流导入兼容
            if (importDssOrchestratorInfo.getWorkspaceId() == null) {
                importDssOrchestratorInfo.setWorkspaceId(workspace.getWorkspaceId());
            }
            if (StringUtils.isEmpty(importDssOrchestratorInfo.getOrchestratorMode())) {
                importDssOrchestratorInfo.setOrchestratorMode("pom_work_flow");
            }
            if (StringUtils.isEmpty(importDssOrchestratorInfo.getOrchestratorWay())) {
                importDssOrchestratorInfo.setOrchestratorWay(",pom_work_flow_DAG,");
            }
        }
        String flowZipPath = inputPath + File.separator + "orc_flow.zip";
        //3、上传工作流zip包到bml
        InputStream inputStream = bmlService.readLocalResourceFile(userName, flowZipPath);
        BmlResource resultMap = bmlService.upload(userName, inputStream, importDssOrchestratorInfo.getName() + "_orc_flow.zip", projectName);
        String orcResourceId = resultMap.getResourceId();
        String orcBmlVersion = resultMap.getVersion();

        //4、导入版本Version信息
        DSSOrchestratorVersion dssOrchestratorVersion = new DSSOrchestratorVersion();
        dssOrchestratorVersion.setAppId(null);
        dssOrchestratorVersion.setComment("orchestrator import");
        dssOrchestratorVersion.setOrchestratorId(importDssOrchestratorInfo.getId());
        dssOrchestratorVersion.setContent("");
        dssOrchestratorVersion.setProjectId(projectId);
        dssOrchestratorVersion.setSource("Orchestrator create");
        dssOrchestratorVersion.setUpdater(userName);
        //生产导入：默认是为无效，除非开启直接有效（在生成环境独立部署的时候，导入和发布是分开的）；开发环境为有效
        int valid = DSSOrchestratorConf.DSS_IMPORT_VALID_IMMEDIATELY.getValue() || DSSLabelUtil.isDevEnv(dssLabels) ? 1 : 0;
        dssOrchestratorVersion.setValidFlag(valid);

        DSSOrchestratorVersion oldVersion  = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(importDssOrchestratorInfo.getId(), 1);
        if (oldVersion!=null) {
            dssOrchestratorVersion.setVersion(OrchestratorUtils.increaseVersion(oldVersion.getVersion()));
        } else {
            dssOrchestratorVersion.setVersion(OrchestratorUtils.generateNewVersion());
        }

        //5、生成上下文ContextId
        String contextId = contextService.createContextID(workspace.getWorkspaceName(), projectName, importDssOrchestratorInfo.getName(), dssOrchestratorVersion.getVersion(), userName);
        dssOrchestratorVersion.setFormatContextId(contextId);
        LOGGER.info("Create a new ContextId for import: {} ", contextId);

        //6、导入第三方应用信息，如工作流、Visualis、Qualities
        DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(userName,
                workspace.getWorkspaceName(), importDssOrchestratorInfo.getType(), dssLabels);
        Long finalProjectId = projectId;
        RefJobContentResponseRef responseRef = OrchestrationDevelopmentOperationUtils.tryOrchestrationOperation(importDssOrchestratorInfo,
                dssOrchestrator, userName, workspace, dssLabels,
                DevelopmentIntegrationStandard::getRefImportService,
                developmentService -> ((RefImportService) developmentService).getRefImportOperation(),
                dssContextRequestRef -> dssContextRequestRef.setContextId(contextId),
                projectRefRequestRef -> projectRefRequestRef.setRefProjectId(finalProjectId).setProjectName(projectName),
                (developmentOperation, developmentRequestRef) -> {
                    ImportRequestRef requestRef = (ImportRequestRef) developmentRequestRef;
                    requestRef.setResourceMap(MapUtils.newCommonMap(ImportRequestRef.RESOURCE_ID_KEY, orcResourceId, ImportRequestRef.RESOURCE_VERSION_KEY, orcBmlVersion));
                    requestRef.setNewVersion(dssOrchestratorVersion.getVersion());
                    return ((RefImportOperation) developmentOperation).importRef(requestRef);
                }, "import");
        long orchestrationId = (Long) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_ID_KEY);
        String orchestrationContent = (String) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_CONTENT_KEY);
        List<String[]> paramConfTemplateIds = (List<String[]>) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_FLOWID_PARAMCONF_TEMPLATEID_TUPLES_KEY);
        if(null != existFlag){
            if(oldVersion!=null) {
                //如果生产中心的所有orc版本的valid_flag都是0（之前的所有发布都在convert期间失败了），那么oldVersion是为空的。
                addOrchestratorVersionHook.beforeAdd(oldVersion, Collections.emptyMap());
            }
            //如果Orchestrator已经导入过，目前只更新版本信息，并更新基础信息name,其它信息不修改。
            orchestratorMapper.updateOrchestrator(importDssOrchestratorInfo);
        }else{
            orchestratorMapper.addOrchestrator(importDssOrchestratorInfo);
        }

        //更新返回內容
        dssOrchestratorVersion.setUpdateTime(new Date());
        dssOrchestratorVersion.setAppId(orchestrationId);
        dssOrchestratorVersion.setContent(orchestrationContent);
        dssOrchestratorVersion.setOrchestratorId(importDssOrchestratorInfo.getId());

        orchestratorMapper.addOrchestratorVersion(dssOrchestratorVersion);
        addOrchestratorVersionHook.afterAdd(dssOrchestratorVersion, Collections.singletonMap(OrchestratorRefConstant.ORCHESTRATION_FLOWID_PARAMCONF_TEMPLATEID_TUPLES_KEY,paramConfTemplateIds));
        LOGGER.info("import orchestrator success,orcId:{},appId:{}",importDssOrchestratorInfo.getId(),orchestrationId);

        return dssOrchestratorVersion;
    }
}
