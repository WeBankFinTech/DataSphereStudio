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

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.DSSLabelUtil;
import com.webank.wedatasphere.dss.common.utils.*;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestImportOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestProjectImportOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.plugin.AbstractDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.core.service.BMLService;
import com.webank.wedatasphere.dss.orchestrator.core.utils.OrchestratorUtils;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.loader.OrchestratorManager;
import com.webank.wedatasphere.dss.orchestrator.publish.ImportDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.publish.io.export.MetaExportService;
import com.webank.wedatasphere.dss.orchestrator.publish.io.input.MetaInputService;
import com.webank.wedatasphere.dss.orchestrator.publish.utils.OrchestrationDevelopmentOperationUtils;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.service.RefImportService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.utils.DSSJobContentConstant;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.cs.common.utils.CSCommonUtils;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.orchestrator.publish.impl.ExportDSSOrchestratorPluginImpl.DEFAULT_ORC_NAME;


@Component
public class ImportDSSOrchestratorPluginImpl extends AbstractDSSOrchestratorPlugin implements ImportDSSOrchestratorPlugin {

    @Autowired
    private MetaInputService metaInputService;
    @Autowired
    private MetaExportService metaExportService;
    @Autowired
    private OrchestratorMapper orchestratorMapper;
    @Autowired
    private BMLService bmlService;
    @Autowired
    private ContextService contextService;
    @Autowired
    private OrchestratorManager orchestratorManager;

    @Override
    public Long importOrchestrator(RequestImportOrchestrator requestImportOrchestrator) throws Exception {
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
        //生产导入：默认是为无效，开发环境为有效
        dssOrchestratorVersion.setValidFlag(DSSLabelUtil.isDevEnv(dssLabels) ? 1 : 0);

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

        return dssOrchestratorVersion.getOrchestratorId();
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

        //TODO 需要对zip包中json内容进行修改，每个节点都需要加上后缀，主要修改meta.txt和各flow的json，需要验证下资源文件的保存方式
        if (StringUtils.isBlank(nodeSuffix)){
            nodeSuffix = "copy";
        }
        //修改flow的json文件
        List<DSSFlow> dssFlows = metaInputService.inputFlow(inputPath);
        for (DSSFlow dssFlow: dssFlows) {
            String flowInputPath = inputPath + File.separator + dssFlow.getName();
            String flowJsonPath = flowInputPath + File.separator + dssFlow.getName() + ".json";
            // 修改原有的json内容
            String flowJson = bmlService.readLocalFlowJsonFile(userName, flowJsonPath);
            Map<String, Object> flowJsonObject = BDPJettyServerHelper.jacksonJson().readValue(flowJson, Map.class);
            List<DSSNode> workflowNodes = DSSCommonUtils.getWorkFlowNodes(flowJson);
            String finalNodeSuffix = nodeSuffix;
            List<DSSNode> targetWorkflowNodes = workflowNodes.stream().peek(s -> {
                String name = s.getName();
                s.setName(name + "_" + finalNodeSuffix);
            }).collect(Collectors.toList());
            flowJsonObject.replace("nodes", targetWorkflowNodes);
            String updatedJson = BDPJettyServerHelper.jacksonJson().writeValueAsString(flowJsonObject);
            //修改文件路径
            StringBuilder newFlowJsonPath;
            StringBuilder newFlowInputPath;
            if (dssFlow.getRootFlow()){
                //采用递归的方式对children节点进行修改
                dssFlow.setName(targetOrchestratorName);
                dssFlow.setCreateTime(new Date());
                dssFlow.setProjectID(targetProjectId);
                newFlowInputPath = new StringBuilder(inputPath + File.separator + targetOrchestratorName);
                newFlowJsonPath = new StringBuilder(newFlowInputPath + File.separator + targetOrchestratorName + ".json");
            }else {

                newFlowInputPath = new StringBuilder(inputPath + File.separator + dssFlow.getName() + "_" + nodeSuffix);
                newFlowJsonPath = new StringBuilder(newFlowInputPath + File.separator + dssFlow.getName() + "_" + nodeSuffix + ".json");
            }
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(newFlowJsonPath.toString()));
            bufferedWriter.write(updatedJson);
            bufferedWriter.flush();
            bufferedWriter.close();
        }
        //修改meta.txt
        List<DSSFlowRelation> dssFlowRelations = metaInputService.inputFlowRelation(inputPath);


//        metaExportService.exportFlowBaseInfo();


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

        String flowZipPath = inputPath + File.separator + "orc_flow.zip";

        //3、上传工作流zip包到bml
        InputStream inputStream = bmlService.readLocalResourceFile(userName, flowZipPath);
        Map<String, Object> resultMap = bmlService.upload(userName, inputStream, importDssOrchestratorInfo.getName() + "_orc_flow.zip", targetProjectName);

        String orcResourceId = resultMap.get("resourceId").toString();
        String orcBmlVersion = resultMap.get("version").toString();

        //4、导入版本Version信息
        DSSOrchestratorVersion dssOrchestratorVersion = new DSSOrchestratorVersion();
        dssOrchestratorVersion.setAppId(null);
        dssOrchestratorVersion.setComment("orchestrator copy");
        //这个Id是不是需要写入后importDssOrchestratorInfo生成的自增id
        dssOrchestratorVersion.setOrchestratorId(importDssOrchestratorInfo.getId());
        dssOrchestratorVersion.setContent("");
        dssOrchestratorVersion.setProjectId(targetProjectId);
        dssOrchestratorVersion.setSource("Orchestrator create");
        dssOrchestratorVersion.setUpdater(userName);

        //生产导入：默认是为无效，开发环境为有效
        dssOrchestratorVersion.setValidFlag(DSSLabelUtil.isDevEnv(dssLabels) ? 1 : 0);

        String oldVersion = orchestratorMapper.getLatestVersion(importDssOrchestratorInfo.getId(), 1);
        if (StringUtils.isNotEmpty(oldVersion)) {
            dssOrchestratorVersion.setVersion(oldVersion);
        } else {
            dssOrchestratorVersion.setVersion(OrchestratorUtils.generateNewVersion());
        }

        //TODO 所有信息都要转换成target信息
        //5、生成上下文ContextId
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

        dssOrchestratorVersion.setOrchestratorId(importDssOrchestratorInfo.getId());
        dssOrchestratorVersion.setUpdateTime(new Date());
        dssOrchestratorVersion.setAppId(orchestrationId);
        dssOrchestratorVersion.setContent(orchestrationContent);
        orchestratorMapper.addOrchestratorVersion(dssOrchestratorVersion);

        return dssOrchestratorVersion.getOrchestratorId();
    }

}
