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
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
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
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

        String oldVersion = orchestratorMapper.getLatestVersion(importDssOrchestratorInfo.getId(), 1);
        if (StringUtils.isNotEmpty(oldVersion)) {
            dssOrchestratorVersion.setVersion(OrchestratorUtils.increaseVersion(oldVersion));
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
        if(null != existFlag){
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
        LOGGER.info("import orchestrator success,orcId:{},appId:{}",importDssOrchestratorInfo.getId(),orchestrationId);

        return dssOrchestratorVersion;
    }

    @Override
    public Long importCopyOrchestrator(RequestImportOrchestrator requestImportOrchestrator, String nodeSuffix) throws Exception {
        String userName = requestImportOrchestrator.getUserName();
        String projectName = requestImportOrchestrator.getProjectName();
        String resourceId = requestImportOrchestrator.getResourceId();
        String version = requestImportOrchestrator.getBmlVersion();
        List<DSSLabel> dssLabels = requestImportOrchestrator.getDssLabels();
        Workspace workspace = requestImportOrchestrator.getWorkspace();

        String targetProjectName = requestImportOrchestrator.getCopyProjectName();
        Long targetProjectId = requestImportOrchestrator.getCopyProjectId();
        String targetOrchestratorName = requestImportOrchestrator.getOrchestratorName();

        //1、下载BML的Orchestrator的导入包
        String inputZipPath = IoUtils.generateIOPath(userName, targetProjectName, DEFAULT_ORC_NAME + ".zip");
        bmlService.downloadToLocalPath(userName, resourceId, version, inputZipPath);
        String inputPath = ZipHelper.unzip(inputZipPath);

        if (StringUtils.isBlank(nodeSuffix)){
            nodeSuffix = "copy";
        }
        String flowZipPath = inputPath + File.separator + "orc_flow.zip";
        ZipHelper.unzip(flowZipPath);
        String sourceProjectDir = inputPath + File.separator + projectName;
        //修改flow的json文件
        List<DSSFlow> dssFlows = metaInputService.inputFlow(sourceProjectDir);
        for (DSSFlow dssFlow: dssFlows) {
            LOGGER.info("Start to modify dssFlow {} json.", dssFlow.getName());
            String flowInputPath = sourceProjectDir + File.separator + dssFlow.getName();
            String flowJsonPath = flowInputPath + File.separator + dssFlow.getName() + ".json";
            // 修改原有的json内容
            String flowJson = bmlService.readLocalTextFile(userName, flowJsonPath);
            Map<String, Object> flowJsonObject = BDPJettyServerHelper.jacksonJson().readValue(flowJson, Map.class);
            List<DSSNodeDefault> workflowNodes = DSSCommonUtils.getWorkFlowNodes(flowJson);
            String finalNodeSuffix = nodeSuffix;
            List<DSSNodeDefault> targetWorkflowNodes = workflowNodes.stream().peek(s -> {
                String name = s.getName();
                s.setName(name + "_" + finalNodeSuffix);
            }).collect(Collectors.toList());
            flowJsonObject.replace("nodes", targetWorkflowNodes);
            String updatedJson = DSSCommonUtils.COMMON_GSON.toJson(flowJsonObject);
            //修改json文件保存路径
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(flowJsonPath))) {
                bufferedWriter.write(updatedJson);
                bufferedWriter.flush();
            }
            if (dssFlow.getRootFlow()) {
                LOGGER.info("Modify root dssFlow {} json to dssFlow {} json.", dssFlow.getName(), targetOrchestratorName);
                if (!targetOrchestratorName.equals(dssFlow.getName())){
                    FileUtils.moveFile(new File(flowJsonPath), new File(flowInputPath + File.separator + targetOrchestratorName + ".json"));
                }
                FileUtils.moveDirectory(new File(flowInputPath), new File(sourceProjectDir + File.separator + targetOrchestratorName));
            } else {
                LOGGER.info("Modify dssFlow {} json to dssFlow {} json.", dssFlow.getName(), dssFlow.getName() + "_" + nodeSuffix);
                FileUtils.moveFile(new File(flowJsonPath), new File(flowInputPath + File.separator + dssFlow.getName() + "_" + nodeSuffix + ".json"));
                FileUtils.moveDirectory(new File(flowInputPath), new File(sourceProjectDir + File.separator + dssFlow.getName() + "_" + nodeSuffix));
            }
        }
        //修改meta.txt并保存
        LOGGER.info("Modify dssFlows meta.txt");
        modifyFlowMeta(dssFlows, targetOrchestratorName, targetProjectId, nodeSuffix);

        List<DSSFlowRelation> dssFlowRelations = metaInputService.inputFlowRelation(sourceProjectDir);
        metaExportService.exportFlowBaseInfo(dssFlows, dssFlowRelations, sourceProjectDir);

        //2、导入Info信息(导入冲突处理)
        List<DSSOrchestratorInfo> dssOrchestratorInfos = metaInputService.importOrchestrator(inputPath);
        DSSOrchestratorInfo importDssOrchestratorInfo = dssOrchestratorInfos.get(0);
        //修改orchestrator信息为target信息
        importDssOrchestratorInfo.setProjectId(targetProjectId);
        importDssOrchestratorInfo.setUUID(UUID.randomUUID().toString());
        importDssOrchestratorInfo.setId(null);
        importDssOrchestratorInfo.setCreator(userName);
        importDssOrchestratorInfo.setCreateTime(new Date());
        importDssOrchestratorInfo.setName(targetOrchestratorName);

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

        // rename orc_flow.zip from source projectName to target projectName.
        FileUtils.delete(new File(flowZipPath));
        if (!projectName.equals(targetProjectName)) {
            FileUtils.moveDirectory(new File(inputPath + File.separator + projectName), new File(inputPath + File.separator + targetProjectName));
        }
        ZipHelper.zip(inputPath + File.separator + targetProjectName);
        FileUtils.moveFile(new File(inputPath + File.separator + targetProjectName + ".zip"), new File(flowZipPath));


        //3、上传工作流zip包到bml
        InputStream inputStream = bmlService.readLocalResourceFile(userName, flowZipPath);
        BmlResource resultMap = bmlService.upload(userName, inputStream, importDssOrchestratorInfo.getName() + "_orc_flow.zip", targetProjectName);

        String orcResourceId = resultMap.getResourceId();
        String orcBmlVersion = resultMap.getVersion();

        //4、导入版本Version信息
        DSSOrchestratorVersion dssOrchestratorVersion = new DSSOrchestratorVersion();
        dssOrchestratorVersion.setAppId(null);
        dssOrchestratorVersion.setComment("orchestrator copy");
        dssOrchestratorVersion.setOrchestratorId(importDssOrchestratorInfo.getId());
        dssOrchestratorVersion.setContent("");
        dssOrchestratorVersion.setProjectId(targetProjectId);
        dssOrchestratorVersion.setSource("Orchestrator create");
        dssOrchestratorVersion.setUpdater(userName);

        //生产导入：默认是为无效，开发环境为有效
        int valid = DSSOrchestratorConf.DSS_IMPORT_VALID_IMMEDIATELY.getValue() || DSSLabelUtil.isDevEnv(dssLabels) ? 1 : 0;
        dssOrchestratorVersion.setValidFlag(valid);

        String oldVersion = orchestratorMapper.getLatestVersion(importDssOrchestratorInfo.getId(), 1);
        if (StringUtils.isNotEmpty(oldVersion)) {
            dssOrchestratorVersion.setVersion(oldVersion);
        } else {
            dssOrchestratorVersion.setVersion(OrchestratorUtils.generateNewVersion());
        }

        //5、生成上下文ContextId，所有信息都要转换成target信息
        String contextId = contextService.createContextID(workspace.getWorkspaceName(), targetProjectName, importDssOrchestratorInfo.getName(), dssOrchestratorVersion.getVersion(), userName);
        dssOrchestratorVersion.setFormatContextId(contextId);
        LOGGER.info("Create a new ContextId {} for copy a new orchestrator.", contextId);

        //6、导入第三方应用信息，如工作流、Visualis、Qualities
        DSSOrchestrator dssOrchestrator = orchestratorManager.getOrCreateOrchestrator(userName,
                workspace.getWorkspaceName(), importDssOrchestratorInfo.getType(), dssLabels);
        RefJobContentResponseRef responseRef = OrchestrationDevelopmentOperationUtils.tryOrchestrationOperation(importDssOrchestratorInfo,
                dssOrchestrator, userName, workspace, dssLabels,
                DevelopmentIntegrationStandard::getRefImportService,
                developmentService -> ((RefImportService) developmentService).getRefImportOperation(),
                dssContextRequestRef -> dssContextRequestRef.setContextId(contextId),
                projectRefRequestRef -> projectRefRequestRef.setRefProjectId(targetProjectId).setProjectName(targetProjectName),
                (developmentOperation, developmentRequestRef) -> {
                    ImportRequestRef requestRef = (ImportRequestRef) developmentRequestRef;
                    requestRef.setResourceMap(MapUtils.newCommonMap(ImportRequestRef.RESOURCE_ID_KEY, orcResourceId, ImportRequestRef.RESOURCE_VERSION_KEY, orcBmlVersion));
                    requestRef.setNewVersion(dssOrchestratorVersion.getVersion());
                    return ((RefImportOperation) developmentOperation).importRef(requestRef);
                }, "import");
        long orchestrationId = (Long) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_ID_KEY);
        String orchestrationContent = (String) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATION_CONTENT_KEY);

        orchestratorMapper.addOrchestrator(importDssOrchestratorInfo);
        //此处需要获取ID后再写入
        dssOrchestratorVersion.setOrchestratorId(importDssOrchestratorInfo.getId());
        dssOrchestratorVersion.setUpdateTime(new Date());
        dssOrchestratorVersion.setAppId(orchestrationId);
        dssOrchestratorVersion.setContent(orchestrationContent);
        orchestratorMapper.addOrchestratorVersion(dssOrchestratorVersion);

        return dssOrchestratorVersion.getOrchestratorId();
    }

    private void modifyFlowMeta(List<DSSFlow> dssFlows, String targetOrchestratorName, Long targetProjectId, String flowNodeSuffix) {
        for (DSSFlow dssFlow : dssFlows) {
            dssFlow.setCreateTime(new Date());
            dssFlow.setProjectId(targetProjectId);
            if (dssFlow.getChildren() != null) {
                modifyFlowMeta((List<DSSFlow>) dssFlow.getChildren(), null, targetProjectId, flowNodeSuffix);
            }
            if (dssFlow.getRootFlow()) {
                dssFlow.setName(targetOrchestratorName);
            } else {
                dssFlow.setName(dssFlow.getName() + "_" + flowNodeSuffix);
            }
        }
    }
}
