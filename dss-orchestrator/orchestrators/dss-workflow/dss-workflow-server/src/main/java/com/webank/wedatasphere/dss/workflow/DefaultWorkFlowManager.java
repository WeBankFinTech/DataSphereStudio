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

package com.webank.wedatasphere.dss.workflow;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowImportParam;
import com.webank.wedatasphere.dss.workflow.io.export.WorkFlowExportService;
import com.webank.wedatasphere.dss.workflow.io.input.MetaInputService;
import com.webank.wedatasphere.dss.workflow.io.input.WorkFlowInputService;
import com.webank.wedatasphere.dss.workflow.service.BMLService;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author allenlliu
 * @date 2020/10/21 03:37 PM
 */

@Component
public class DefaultWorkFlowManager implements WorkFlowManager {


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
        List<String> dssLabelList = null;
        if (null != dssLabels) {
            dssLabelList = dssLabels.stream().map(DSSLabel::getLabel).collect(Collectors.toList());
        } else {
            dssLabelList = Collections.singletonList("DEV");
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

        return flowService.copyRootFlow(rootFlowId, userName, workspaceName, projectName, version);

    }

    @Override
    public DSSFlow queryWorkflow(String userName, Long rootFlowId) throws DSSErrorException {
        DSSFlow dssFlow = flowService.getFlowWithJsonAndSubFlowsByID(rootFlowId);
//        if (!dssFlow.getCreator().equals(userName)) {
//            throw new DSSErrorException(100089, "Workflow can not be query by others");
//        }
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
    public Map<String, Object> exportWorkflow(String userName, Long flowId, Long dssProjectId, String projectName, Workspace workspace) throws Exception {
        Map<String, Object> resultMap = null;
        DSSFlow dssFlow = flowService.getFlowByID(flowId);
//        if (!dssFlow.getCreator().equals(userName)) {
//            throw new DSSErrorException(100089, "Workflow can not be export by others");
//        }
        String exportPath = workFlowExportService.exportFlowInfo(dssProjectId, projectName, flowId, userName, workspace);
        InputStream inputStream = bmlService.readLocalResourceFile(userName, exportPath);
        resultMap = bmlService.upload(userName, inputStream, dssFlow.getName() + ".export", projectName);
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
                    inputPath, null, dssFlowImportParam.getWorkspace(), dssFlowImportParam.getOrcVersion());
        }
        return rootFlows;
    }
}
