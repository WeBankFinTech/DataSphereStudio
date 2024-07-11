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

package com.webank.wedatasphere.dss.workflow;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerAppConn;
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestConvertOrchestrations;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.operation.DSSToRelConversionOperation;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.DSSToRelConversionRequestRef;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.OrchestrationToRelConversionRequestRef;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.ProjectToRelConversionRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.utils.RequestRefUtils;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import com.webank.wedatasphere.dss.workflow.common.protocol.*;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.dao.LockMapper;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowEditLock;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowImportParam;
import com.webank.wedatasphere.dss.workflow.io.export.WorkFlowExportService;
import com.webank.wedatasphere.dss.workflow.io.input.MetaInputService;
import com.webank.wedatasphere.dss.workflow.io.input.WorkFlowInputService;
import com.webank.wedatasphere.dss.workflow.lock.DSSFlowEditLockManager;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.protocol.util.ImmutablePair;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.webank.wedatasphere.dss.common.utils.IoUtils.FLOW_META_DIRECTORY_NAME;
import static com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant.DEFAULT_SCHEDULER_APP_CONN;

@Component
public class DefaultWorkFlowManager implements WorkFlowManager {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DSSFlowService flowService;

    @Autowired
    private WorkFlowInputService workFlowInputService;

    @Autowired
    private WorkFlowExportService workFlowExportService;

    @Autowired
    @Qualifier("workflowBmlService")
    private BMLService bmlService;

    @Autowired
    private MetaInputService metaInputService;
    @Autowired
    private WorkFlowParser workFlowParser;
    @Autowired
    private LockMapper lockMapper;

    @Override
    public DSSFlow createWorkflow(String userName,
                                  Long projectId,
                                  String flowName,
                                  String contextIdStr,
                                  String description,
                                  Long parentFlowId,
                                  String uses,
                                  List<String> linkedAppConnNames,
                                  List<DSSLabel> dssLabels,
                                  String orcVersion,
                                  String schedulerAppConn) throws DSSErrorException, JsonProcessingException {
        DSSFlow dssFlow = new DSSFlow();
        dssFlow.setName(flowName);
        dssFlow.setProjectId(projectId);
        dssFlow.setDescription(description);
        dssFlow.setCreator(userName);
        dssFlow.setCreateTime(new Date());
        dssFlow.setState(false);
        dssFlow.setSource("create by user");
        dssFlow.setUses(uses);

        dssFlow.setLinkedAppConnNames(linkedAppConnNames == null ? "" : String.join(",", linkedAppConnNames));
        Map<String, String> dssLabelList = new HashMap<>(1);
        if (null != dssLabels) {
            dssLabels.stream().map(DSSLabel::getValue).forEach(a -> {
                dssLabelList.put(EnvDSSLabel.DSS_ENV_LABEL_KEY, a.get(EnvDSSLabel.DSS_ENV_LABEL_KEY));
            });
        } else {
            dssLabelList.put(EnvDSSLabel.DSS_ENV_LABEL_KEY, DSSCommonUtils.ENV_LABEL_VALUE_DEV);
        }
        dssFlow.setDssLabels(BDPJettyServerHelper.jacksonJson().writeValueAsString(dssLabelList));
        //-1是rpc传递消息不支持null,所有父工作流都是-1。
        if (parentFlowId == null || parentFlowId == -1) {
            dssFlow.setRootFlow(true);
            dssFlow.setRank(0);
            dssFlow.setHasSaved(true);
            dssFlow = flowService.addFlow(dssFlow, contextIdStr, orcVersion, schedulerAppConn);
        } else {
            dssFlow.setRootFlow(false);
            DSSFlow parentFlow = flowService.getFlowByID(parentFlowId);
            // TODO 并发问题考虑 for update，由于加了工作流编辑锁，暂时可忽略
            dssFlow.setRank(parentFlow.getRank() + 1);
            dssFlow.setHasSaved(true);
            dssFlow.setProjectId(parentFlow.getProjectId());
            dssFlow = flowService.addSubFlow(dssFlow, parentFlowId, contextIdStr, orcVersion, schedulerAppConn);
        }
        logger.info("create workflow success. flowId:{}",dssFlow.getId());
        return dssFlow;
    }

    @Override
    public DSSFlow copyRootFlowWithSubFlows(String userName, Long rootFlowId, Workspace workspace,
                                            String projectName, String contextIdStr, String orcVersion,
                                            String description, List<DSSLabel> dssLabels, String nodeSuffix,
                                            String newFlowName, Long newProjectId) throws DSSErrorException, IOException {
        return flowService.copyRootFlow(rootFlowId, userName, workspace, projectName,
                orcVersion, contextIdStr, description, dssLabels, nodeSuffix, newFlowName, newProjectId);
    }

    @Override
    public DSSFlow queryWorkflow(String userName, Long rootFlowId) {
        return flowService.getFlowWithJsonAndSubFlowsByID(rootFlowId);
    }

    @Override
    public void updateWorkflow(String userName, Long flowId, String flowName, String description, String uses) throws DSSErrorException {
        DSSFlow dssFlow = new DSSFlow();
        dssFlow.setId(flowId);
        dssFlow.setName(flowName);
        dssFlow.setDescription(description);
        dssFlow.setUses(uses);
        flowService.updateFlowBaseInfo(dssFlow);
        logger.info("update workflow success. flowId:{}",flowId);
    }

    @Override
    public void deleteWorkflow(String userName, Long flowId) throws DSSErrorException {
        DSSFlow dssFlow = flowService.getFlowByID(flowId);
        if (dssFlow.getCreator().equals(userName)) {
            flowService.batchDeleteFlow(Collections.singletonList(flowId));
        } else {
            throw new DSSErrorException(100088, "Workflow can not be deleted unless the owner.(工作流不允许被其拥有者之外的用户删除！)");        }
        logger.info("delete workflow success. flowId:{}",flowId);
    }

    @Override
    public ResponseUnlockWorkflow unlockWorkflow(String userName, Long flowId, Boolean confirmDelete, Workspace workspace) throws DSSErrorException {
        DSSFlowEditLock editLock = lockMapper.getFlowEditLockByID(flowId);
        if (editLock == null) {
            return new ResponseUnlockWorkflow(ResponseUnlockWorkflow.NONEED_UNLOCK, null);
        } else if (!Boolean.TRUE.equals(confirmDelete)) {
            return new ResponseUnlockWorkflow(ResponseUnlockWorkflow.NEED_SECOND_CONFIRM, editLock.getUsername());
        }
        DSSFlowEditLockManager.deleteLock(editLock.getLockContent(), workspace);
        return new ResponseUnlockWorkflow(ResponseUnlockWorkflow.UNLOCK_SUCCESS, null);
    }
    @Override
    public BmlResource exportWorkflowNew(String userName, Long flowId, Long dssProjectId,
                                         String projectName, Workspace workspace,
                                         List<DSSLabel> dssLabels,boolean exportExternalNodeAppConnResource) throws Exception {
        DSSFlow dssFlow = flowService.getFlowByID(flowId);
        String projectPath = workFlowExportService.exportFlowInfoNew(dssProjectId, projectName, flowId, userName, workspace, dssLabels,exportExternalNodeAppConnResource);
        String exportPath = ZipHelper.zip(projectPath);
        InputStream inputStream = bmlService.readLocalResourceFile(userName, exportPath);
        BmlResource bmlResource = bmlService.upload(userName, inputStream, dssFlow.getName() + ".export", projectName);
        logger.info("export workflow success.  flowId:{},bmlResource:{} .",flowId,bmlResource);
        return  bmlResource;
    }
    @Override
    public String readWorkflowNew(String userName, Long flowId, Long dssProjectId,
                                         String projectName, Workspace workspace,
                                         List<DSSLabel> dssLabels,boolean exportExternalNodeAppConnResource, String filePath) throws Exception {
        DSSFlow dssFlow = flowService.getFlowByID(flowId);
        String projectPath = null;
        try {
            projectPath = workFlowExportService.exportFlowInfoNew(dssProjectId, projectName, flowId, userName, workspace, dssLabels,exportExternalNodeAppConnResource);
        } catch (Exception e) {
            logger.error("exportFlowInfoNew failed , the reason is:", e);
            throw new DSSErrorException(100098, "工作流导出失败，原因为" + e.getMessage());

        } finally {
            //删掉整个目录
            if (StringUtils.isNotEmpty(projectPath)) {
                File file = new File(projectPath);
                if (ZipHelper.deleteDir(file)){
                    logger.info("结束删除目录 {} 成功", projectPath);
                }else{
                    logger.info("删除目录 {} 失败", projectPath);
                }
            }
        }
        String fullPath = projectPath + File.separator + filePath;
        logger.info("export workflow success.  flowId:{},fullPath:{} .",flowId,fullPath);
        return new String(Files.readAllBytes(Paths.get(fullPath)));
    }
    @Override
    public BmlResource exportWorkflow(String userName, Long flowId, Long dssProjectId,
                                      String projectName, Workspace workspace,
                                      List<DSSLabel> dssLabels) throws Exception {
        DSSFlow dssFlow = flowService.getFlowByID(flowId);
        String exportPath = workFlowExportService.exportFlowInfo(dssProjectId, projectName, flowId, userName, workspace, dssLabels);
        InputStream inputStream = bmlService.readLocalResourceFile(userName, exportPath);
        BmlResource bmlResource = bmlService.upload(userName, inputStream, dssFlow.getName() + ".export", projectName);
        logger.info("export workflow success. flowId:{},bmlResource:{} .",flowId,bmlResource);
        return bmlResource;
    }
    @Override
    public List<DSSFlow> importWorkflowNew(String userName,
                                           String resourceId,
                                           String bmlVersion,
                                           DSSFlowImportParam dssFlowImportParam,
                                           List<DSSLabel> dssLabels) throws DSSErrorException, IOException {

        //todo download workflow bml file contains flowInfo and flowRelationInfo
        String projectName = dssFlowImportParam.getProjectName();
        // /appcom/tmp/dss/yyyyMMddHHmmssSSS/arionliu
        String tempPath = IoUtils.generateTempIOPath(userName);
        // /appcom/tmp/dss/yyyyMMddHHmmssSSS/arionliu/projectxxx.zip
        String inputZipPath = IoUtils.addFileSeparator(tempPath, projectName + ".zip");
        bmlService.downloadToLocalPath(userName, resourceId, bmlVersion, inputZipPath);
        try{
            String  originProjectName=readImportZipProjectName(inputZipPath);
            if(!projectName.equals(originProjectName)){
                String msg=String.format("target project name must be same with origin project name.origin project name:%s,target project name:%s(导入的目标工程名必须与导出时源工程名保持一致。源工程名：%s，目标工程名：%s)"
                        ,originProjectName,projectName,originProjectName,projectName);
                throw new DSSRuntimeException(msg);
            }
        }catch (IOException e){
            throw new DSSRuntimeException("upload file format error(导入包格式错误)");
        }
        String projectPath = ZipHelper.unzip(inputZipPath,true);
        String flowName = IoUtils.getSubdirectoriesNames(projectPath).stream().filter(name -> !name.startsWith("."))
                .findFirst().orElseThrow(() -> new DSSRuntimeException("import package has no flow（未导入任何工作流，请检查导入包格式）"));
        String flowMetaPath=IoUtils.addFileSeparator(projectPath, FLOW_META_DIRECTORY_NAME, flowName);
        ImmutablePair<List<DSSFlow>,List<DSSFlowRelation>> meta= metaInputService.inputFlowNew(flowMetaPath);
        //导入工作流数据库信息
        List<DSSFlow> dssFlows = meta.getKey();
        //导入工作流关系信息
        List<DSSFlowRelation> dwsFlowRelations = meta.getValue();

        List<DSSFlow> dwsFlowRelationList = workFlowInputService.persistenceFlow(dssFlowImportParam.getProjectID(),
                dssFlowImportParam.getUserName(),
                dssFlows,
                dwsFlowRelations);
        //这里其实只会有1个元素
        List<DSSFlow> rootFlows = dwsFlowRelationList.stream().filter(DSSFlow::getRootFlow).collect(Collectors.toList());
        for (DSSFlow rootFlow : rootFlows) {
            String flowCodePath0=IoUtils.addFileSeparator(projectPath,  flowName);
            String flowMetaPath0=IoUtils.addFileSeparator(projectPath, FLOW_META_DIRECTORY_NAME, flowName);
            workFlowInputService.inputWorkFlowNew(dssFlowImportParam.getUserName(),
                    rootFlow,
                    projectName,
                    flowCodePath0,
                    flowMetaPath0,null, dssFlowImportParam.getWorkspace(), dssFlowImportParam.getOrcVersion(),
                    dssFlowImportParam.getContextId(), dssLabels);
        }
        logger.info("import workflow success.orcVersion:{},context Id:{}", dssFlowImportParam.getOrcVersion(), dssFlowImportParam.getContextId());
        return  rootFlows;
    }

    @Override
    public List<DSSFlow> importWorkflow(String userName,
                                        String resourceId,
                                        String bmlVersion,
                                        DSSFlowImportParam dssFlowImportParam,
                                        List<DSSLabel> dssLabels) throws DSSErrorException, IOException {

        //todo download workflow bml file contains flowInfo and flowRelationInfo
        String projectName = dssFlowImportParam.getProjectName();
        String inputZipPath = IoUtils.generateIOPath(userName, projectName, projectName + ".zip");
        bmlService.downloadToLocalPath(userName, resourceId, bmlVersion, inputZipPath);
        try{
            String  originProjectName=readImportZipProjectName(inputZipPath);
            if(!projectName.equals(originProjectName)){
                String msg=String.format("target project name must be same with origin project name.origin project name:%s,target project name:%s(导入的目标工程名必须与导出时源工程名保持一致。源工程名：%s，目标工程名：%s)"
                        ,originProjectName,projectName,originProjectName,projectName);
                throw new DSSRuntimeException(msg);
            }
        }catch (IOException e){
            throw new DSSRuntimeException("upload file format error(导入包格式错误)");
        }
        String inputPath = ZipHelper.unzip(inputZipPath);
        //导入工作流数据库信息
        List<DSSFlow> dssFlows = metaInputService.inputFlow(inputPath);
        //导入工作流关系信息
        List<DSSFlowRelation> dwsFlowRelations = metaInputService.inputFlowRelation(inputPath);
        //兼容0.x导入
        if (CollectionUtils.isEmpty(dwsFlowRelations)) {
            dwsFlowRelations = metaInputService.inputFlowRelation_for_0_x(inputPath);
        }

        List<DSSFlow> dwsFlowRelationList = workFlowInputService.persistenceFlow(dssFlowImportParam.getProjectID(),
                dssFlowImportParam.getUserName(),
                dssFlows,
                dwsFlowRelations);
        List<DSSFlow> rootFlows = dwsFlowRelationList.stream().filter(DSSFlow::getRootFlow).collect(Collectors.toList());
        for (DSSFlow rootFlow : rootFlows) {
            workFlowInputService.inputWorkFlow(dssFlowImportParam.getUserName(),
                    rootFlow,
                    projectName,
                    inputPath, null, dssFlowImportParam.getWorkspace(), dssFlowImportParam.getOrcVersion(),
                    dssFlowImportParam.getContextId(), dssLabels);
        }
        logger.info("import workflow success.orcVersion:{},contextId:{}", dssFlowImportParam.getOrcVersion(), dssFlowImportParam.getContextId());
        return rootFlows;
    }

    @Override
    public ResponseOperateOrchestrator convertWorkflow(RequestConvertOrchestrations requestConversionWorkflow) {
        if (requestConversionWorkflow.getOrchestrationIdMap() == null || requestConversionWorkflow.getOrchestrationIdMap().isEmpty()) {
            logger.info("the project {} has no workflow, the conversion by user {} is ignored.", requestConversionWorkflow.getProject().getName(),
                    requestConversionWorkflow.getUserName());
            return ResponseOperateOrchestrator.failed("No workflow found, publish is ignored.");
        }
        logger.info("user {} try to convert workflowId(s) {} in project {} to SchedulerAppConn(s).", requestConversionWorkflow.getUserName(),
                requestConversionWorkflow.getOrchestrationIdMap().keySet(), requestConversionWorkflow.getProject().getName());
        //第一步：从db、bml中获取所有的所有的工作流和子工作流的元信息
        //TODO try to optimize it by select db in batch.
        List<ImmutablePair<DSSFlow, Long>> flowInfos = requestConversionWorkflow.getOrchestrationIdMap().entrySet()
                .stream().map(entry -> new ImmutablePair<>(flowService.getFlowWithJsonAndSubFlowsByID(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
        List<DSSFlow> flows = flowInfos.stream().map(ImmutablePair::getKey).collect(Collectors.toList());
        // 区分各个工作流所归属的调度系统
        List<ResponseOperateOrchestrator> responseList = new ArrayList<>();
        flows.stream().map(DSSExceptionUtils.map(flow -> {
                    String schedulerAppConnName = workFlowParser.getValueWithKey(flow.getFlowJson(), DSSWorkFlowConstant.SCHEDULER_APP_CONN_NAME);
                    if (StringUtils.isBlank(schedulerAppConnName)) {
                        // 向下兼容老版本
                        schedulerAppConnName = DEFAULT_SCHEDULER_APP_CONN.getValue();
                    }
                    return new ImmutablePair<>(schedulerAppConnName, flow);
                })).collect(Collectors.groupingBy(ImmutablePair::getKey))
                .forEach((appConnName, pairList) -> {
                    List<DSSFlow> selectedFlows = pairList.stream().map(ImmutablePair::getValue).collect(Collectors.toList());
                    //第二步：把各DSSFlow发布到调度系统（appConnName）中
                    ResponseOperateOrchestrator response = convert(requestConversionWorkflow, appConnName, selectedFlows, flowInfos);
                    responseList.add(response);
                });
        List<ResponseOperateOrchestrator> failedResponseList = responseList.stream().filter(ResponseOperateOrchestrator::isFailed).collect(Collectors.toList());
        if (!failedResponseList.isEmpty()) {
            return ResponseOperateOrchestrator.failed("发布失败，失败原因如下："
                    + failedResponseList.stream().map(ResponseOperateOrchestrator::getMessage).collect(Collectors.joining("; ")));
        } else {
            logger.info("convert(publish)  workflow success");
            return responseList.get(0);
        }
    }

    @Override
    public ResponseSubFlowContextIds getSubFlowContextIdsByFlowIds(RequestSubFlowContextIds requestSubFlowContextIds) throws ErrorException {
        List<String> contextIdList = flowService.getSubFlowContextIdsByFlowIds(requestSubFlowContextIds.getFlowIdList());
        ResponseSubFlowContextIds responseSubFlowContextIds = new ResponseSubFlowContextIds();
        responseSubFlowContextIds.setContextIdList(contextIdList);
        return responseSubFlowContextIds;
    }


    private ResponseOperateOrchestrator convert(RequestConvertOrchestrations requestConversionWorkflow,
                                                String schedulerAppConnName,
                                                List<DSSFlow> flows,
                                                List<ImmutablePair<DSSFlow, Long>> flowInfos) {
        String convertFlowStr = flows.stream().map(DSSFlow::getName).collect(Collectors.joining(", "));
        logger.info("user {} try to convert workflow(s) {} to SchedulerAppConn {}.", requestConversionWorkflow.getUserName(),
                convertFlowStr, schedulerAppConnName);
        SchedulerAppConn appConn = (SchedulerAppConn) AppConnManager.getAppConnManager().getAppConn(schedulerAppConnName);
        AppInstance schedulerInstance = appConn.getAppDesc().getAppInstances().get(0);
        DSSToRelConversionOperation operation = appConn.getOrCreateConversionStandard()
                .getDSSToRelConversionService(schedulerInstance).getDSSToRelConversionOperation();
        DSSToRelConversionRequestRef requestRef = RequestRefUtils.getRequestRef(operation);
        requestRef.setDSSProject((DSSProject) requestConversionWorkflow.getProject())
                .setUserName(requestConversionWorkflow.getUserName())
                .setWorkspace((Workspace) requestConversionWorkflow.getWorkspace())
                .setApprovalId(requestConversionWorkflow.getApprovalId());
        if (requestRef instanceof ProjectToRelConversionRequestRef) {
            //要发布整个项目
            Map<String, Long> orchestrationMap = flowInfos.stream().filter(pair -> flows.contains(pair.getKey()))
                    .map(pair -> new ImmutablePair<>(pair.getKey().getName(), pair.getValue()))
                    .collect(HashMap::new, (map, pair) -> map.put(pair.getKey(), pair.getValue()), HashMap::putAll);
            ((ProjectToRelConversionRequestRef) requestRef).setDSSOrcList(flows).setRefOrchestrationId(orchestrationMap);
        } else if (requestRef instanceof OrchestrationToRelConversionRequestRef) {
            //要发布整个工作流
            flowInfos.stream().filter(pair -> pair.getKey() == flows.get(0)).forEach(pair ->
                    ((OrchestrationToRelConversionRequestRef) requestRef).setDSSOrchestration(pair.getKey())
                            .setRefOrchestrationId(pair.getValue())
            );
        }
        try {
            //把（多个）工作流转化成第三方调度系统，即发布到第三方调度系统做调度。
            ResponseRef responseRef = operation.convert(requestRef);
            if (responseRef.isFailed()) {
                logger.error("user {} convert workflow(s) {} to {} failed, Reason: {}.", requestConversionWorkflow.getUserName(),
                        convertFlowStr, schedulerAppConnName, responseRef.getErrorMsg());
                return ResponseOperateOrchestrator.failed("workflow(s) " + convertFlowStr + " publish to " + schedulerAppConnName + "failed! Reason: "
                        + responseRef.getErrorMsg());
            }
            return ResponseOperateOrchestrator.success();
        } catch (Exception e) {
            logger.error("user {} convert workflow(s) {} to {} failed.", requestConversionWorkflow.getUserName(),
                    convertFlowStr, schedulerAppConnName, e);
            return ResponseOperateOrchestrator.failed("Workflow(s) " + convertFlowStr + " publish to " + schedulerAppConnName +
                    "failed! Reason: " + ExceptionUtils.getRootCauseMessage(e));
        }
    }

    private String readImportZipProjectName(String zipFilePath) throws IOException {
        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            if (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.endsWith("\\") || name.endsWith("/")) {
                    name = name.substring(0, name.length() - 1);
                }
                return name;
            }
        }
        throw new IOException();
    }

}



