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
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestration;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestConvertOrchestrations;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.operation.DSSToRelConversionOperation;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.DSSToRelConversionRequestRef;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.ref.ProjectToRelConversionRequestRefImpl;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AppConnRefFactoryUtils;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowImportParam;
import com.webank.wedatasphere.dss.workflow.io.export.WorkFlowExportService;
import com.webank.wedatasphere.dss.workflow.io.input.MetaInputService;
import com.webank.wedatasphere.dss.workflow.io.input.WorkFlowInputService;
import com.webank.wedatasphere.dss.workflow.service.BMLService;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
    private BMLService bmlService;

    @Autowired
    private MetaInputService metaInputService;

    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Override
    public DSSFlow createWorkflow(String userName,
                                  String flowName,
                                  String contextIdStr,
                                  String description,
                                  Long parentFlowId,
                                  String uses,
                                  List<String> linkedAppConnNames,
                                  List<DSSLabel> dssLabels) throws DSSErrorException, JsonProcessingException {
        DSSFlow dssFlow = new DSSFlow();
        dssFlow.setName(flowName);
        dssFlow.setDescription(description);
        dssFlow.setCreator(userName);
        dssFlow.setCreateTime(new Date());
        dssFlow.setState(false);
        dssFlow.setSource("create by user");
        dssFlow.setUses(uses);

        String appConnJson = BDPJettyServerHelper.jacksonJson().writeValueAsString(linkedAppConnNames);
        dssFlow.setLinkedAppConnNames(appConnJson);
        Map<String, String> dssLabelList = new HashMap<>();
        if (null != dssLabels) {
            dssLabels.stream().map(a->a.getValue()).forEach(a->{
                dssLabelList.put(EnvDSSLabel.DSS_ENV_LABEL_KEY,a.get(EnvDSSLabel.DSS_ENV_LABEL_KEY));
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
            dssFlow = flowService.addFlow(dssFlow, contextIdStr);
        } else {
            dssFlow.setRootFlow(false);
            Integer rank = flowService.getParentRank(parentFlowId);
            // TODO: 2019/6/3 并发问题考虑for update
            dssFlow.setRank(rank + 1);
            dssFlow.setHasSaved(true);
            dssFlow = flowService.addSubFlow(dssFlow, parentFlowId, contextIdStr);
        }
        return dssFlow;
    }

    @Override
    public DSSFlow copyRootflowWithSubflows(String userName, long rootFlowId, String workspaceName, String projectName, String contextIdStr, String version, String description) throws DSSErrorException, IOException {

        return flowService.copyRootFlow(rootFlowId, userName, workspaceName, projectName, version,contextIdStr);

    }

    @Override
    public DSSFlow queryWorkflow(String userName, Long rootFlowId) throws DSSErrorException {
        DSSFlow dssFlow = flowService.getFlowWithJsonAndSubFlowsByID(rootFlowId);
        if (!dssFlow.getCreator().equals(userName)) {
            throw new DSSErrorException(100089, "Workflow can not be query by others");
        }
        return dssFlow;
    }

    @Override
    public void updateWorkflow(String userName, Long flowId, String flowName, String description, String uses) throws DSSErrorException {
        DSSFlow dssFlow = new DSSFlow();
        dssFlow.setId(flowId);
        dssFlow.setName(flowName);
        dssFlow.setDescription(description);
        dssFlow.setUses(uses);
        flowService.updateFlowBaseInfo(dssFlow);
    }

    @Override
    public void deleteWorkflow(String userName, Long flowId) throws DSSErrorException {
        DSSFlow dssFlow = flowService.getFlowByID(flowId);
        if (dssFlow.getCreator().equals(userName)) {
            flowService.batchDeleteFlow(Collections.singletonList(flowId));
        } else {
            throw new DSSErrorException(100088, "Workflow can not be deleted by others");
        }
    }

    @Override
    public Map<String, Object> exportWorkflow(String userName, Long flowId, Long dssProjectId, String projectName, Workspace workspace,List<DSSLabel> dssLabels) throws Exception {
        DSSFlow dssFlow = flowService.getFlowByID(flowId);
        String exportPath = workFlowExportService.exportFlowInfo(dssProjectId, projectName, flowId, userName, workspace,dssLabels);
        InputStream inputStream = bmlService.readLocalResourceFile(userName, exportPath);
        Map<String, Object> resultMap = bmlService.upload(userName, inputStream, dssFlow.getName() + ".export", projectName);
        return resultMap;
    }

    @Override
    public List<DSSFlow> importWorkflow(String userName,
                               String resourceId,
                               String bmlVersion,
                               DSSFlowImportParam dssFlowImportParam) throws DSSErrorException, IOException {

        //todo download workflow bml file contains flowInfo and flowRelationInfo
        String inputZipPath = IoUtils.generateIOPath(userName, dssFlowImportParam.getProjectName(), dssFlowImportParam.getProjectName() + ".zip");
        bmlService.downloadToLocalPath(userName, resourceId, bmlVersion, inputZipPath);
        String inputPath = ZipHelper.unzip(inputZipPath);
        //导入工作流数据库信息
        List<DSSFlow> dssFlows = metaInputService.inputFlow(inputPath);
        //导入工作流关系信息
        List<DSSFlowRelation> dwsFlowRelations = metaInputService.inputFlowRelation(inputPath);

        List<DSSFlow> dwsFlowRelationList = workFlowInputService.persistenceFlow(dssFlowImportParam.getProjectID(),
                dssFlowImportParam.getUserName(),
                dssFlows,
                dwsFlowRelations,
                dssFlowImportParam.getSourceEnv());
        List<DSSFlow> rootFlows = dwsFlowRelationList.stream().filter(DSSFlow::getRootFlow).collect(Collectors.toList());
        for (DSSFlow rootFlow : rootFlows) {
            workFlowInputService.inputWorkFlow(dssFlowImportParam.getUserName(),
                    dssFlowImportParam.getWorkspaceName(),
                    rootFlow,
                    dssFlowImportParam.getVersion(),
                    dssFlowImportParam.getProjectName(),
                    inputPath, null, dssFlowImportParam.getWorkspace(), dssFlowImportParam.getOrcVersion(),
                    dssFlowImportParam.getContextId());
        }
        return rootFlows;
    }

    @Override
    public ResponseOperateOrchestrator convertWorkflow(RequestConvertOrchestrations requestConversionWorkflow) throws DSSErrorException {
        //TODO try to optimize it by select db in batch.
        List<DSSOrchestration> flows = requestConversionWorkflow.getOrcAppIds().stream().map(flowService::getFlowWithJsonAndSubFlowsByID).collect(Collectors.toList());
        SchedulerAppConn appConn = AppConnManager.getAppConnManager().getAppConn(SchedulerAppConn.class);
//        List<AppInstance> appInstances = appConn.getAppDesc().getAppInstancesByLabels(requestConversionWorkflow.getDSSLabels());
        AppInstance schedulerInstance = appConn.getAppDesc().getAppInstances().get(0);
        DSSToRelConversionOperation operation = appConn.getOrCreateWorkflowConversionStandard()
            .getDSSToRelConversionService(schedulerInstance).getDSSToRelConversionOperation();
        DSSToRelConversionRequestRef requestRef = AppConnRefFactoryUtils.newAppConnRef(DSSToRelConversionRequestRef.class, appConn.getAppDesc().getAppName());
        if(requestRef instanceof ProjectToRelConversionRequestRefImpl) {
            ProjectToRelConversionRequestRefImpl relConversionRequestRef = (ProjectToRelConversionRequestRefImpl) requestRef;
            relConversionRequestRef.setDSSProject((DSSProject) requestConversionWorkflow.getProject());
            relConversionRequestRef.setDSSOrcList(flows);
            relConversionRequestRef.setUserName(requestConversionWorkflow.getUserName());
            relConversionRequestRef.setWorkspace((Workspace) requestConversionWorkflow.getWorkspace());
        }
        try{
            ResponseRef responseRef = operation.convert(requestRef);
            if(responseRef.isFailed()) {
                return ResponseOperateOrchestrator.failed(responseRef.getErrorMsg());
            }
            return ResponseOperateOrchestrator.success();
        }catch (Exception e){
            logger.error("convertWorkflow error:",e);
            return ResponseOperateOrchestrator.failed(e.getMessage());
        }
    }
}
