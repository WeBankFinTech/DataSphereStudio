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

package com.webank.wedatasphere.dss.workflow.io.export.impl;


import com.webank.wedatasphere.dss.common.entity.IOType;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.dao.FlowMapper;
import com.webank.wedatasphere.dss.workflow.dao.NodeInfoMapper;
import com.webank.wedatasphere.dss.workflow.entity.NodeInfo;
import com.webank.wedatasphere.dss.workflow.io.export.MetaExportService;
import com.webank.wedatasphere.dss.workflow.io.export.NodeExportService;
import com.webank.wedatasphere.dss.workflow.io.export.WorkFlowExportService;
import com.webank.wedatasphere.dss.workflow.service.BMLService;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.workflow.scheduler.DssJobThreadPool.nodeExportThreadPool;


@Service
public class WorkFlowExportServiceImpl implements WorkFlowExportService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BMLService bmlService;

    @Autowired
    private WorkFlowParser workFlowParser;

    @Autowired
    private NodeExportService nodeExportService;

    @Autowired
    private MetaExportService metaExportService;

    @Autowired
    private FlowMapper flowMapper;

    @Autowired
    private NodeInfoMapper nodeInfoMapper;

    @Autowired
    private DSSFlowService flowService;


    @Override
    public String exportFlowInfo(Long dssProjectId, String projectName, long rootFlowId, String userName, Workspace workspace, List<DSSLabel> dssLabels) throws Exception {
        //获取rootFlow,和所有子Flow
        DSSFlow rootFlow = flowMapper.selectFlowByID(rootFlowId);
        List<DSSFlow> dssFlowList = new ArrayList<>();
        //生成rootflow及所有子flow
        dssFlowList.add(rootFlow);
        getAllDssFlowsByRootflowId(rootFlow, dssFlowList);
        //生成rootflow及所有子flow的Relations
        List<Long> flowIds = dssFlowList.stream().map(DSSFlow::getId).collect(Collectors.toList());
        List<DSSFlowRelation> flowRelations = flowIds.isEmpty() ? new ArrayList<>() : flowMapper.listFlowRelation(flowIds);
        String flowExportSaveBasePath = IoUtils.generateIOPath(userName, projectName, "");
        //标记当前导出为project导出
        IoUtils.generateIOType(IOType.FLOW, flowExportSaveBasePath);
        //标记当前导出环境
        IoUtils.generateIOEnv(flowExportSaveBasePath);
        metaExportService.exportFlowBaseInfo(dssFlowList, flowRelations, flowExportSaveBasePath);
        logger.info(userName + "-开始导出Flow：" + rootFlow.getName());
        List<DSSFlow> dssFlows = new ArrayList<>();

        for (DSSFlow dssFlow : dssFlowList) {
            if (dssFlow.getRootFlow()) {
                String savePath = flowExportSaveBasePath + File.separator + dssFlow.getName() + File.separator + dssFlow.getName() + ".json";
                //导出工作流json文件
                String flowJson = downloadFlowJsonFromBml(userName, dssFlow.getResourceId(), dssFlow.getBmlVersion(), savePath);
                if (!dssFlow.getHasSaved()) {
                    logger.info("工作流{}从未保存过，忽略", dssFlow.getName());
                } else if (StringUtils.isNotBlank(flowJson)) {
                    exportFlowResources(userName, dssProjectId, projectName, flowExportSaveBasePath, flowJson, dssFlow.getName(), workspace, dssLabels);
                    exportAllSubFlows(userName, dssFlow, dssProjectId, projectName, flowExportSaveBasePath, workspace, dssLabels);
                    dssFlows.add(dssFlow);
                } else {
                    String warnMsg = String.format(DSSWorkFlowConstant.PUBLISH_FLOW_REPORT_FORMATE, dssFlow.getName(), dssFlow.getBmlVersion());
                    logger.info(warnMsg);
                    throw new DSSErrorException(90033, warnMsg);
                }
            }
        }
        if (dssFlows.isEmpty()) {
            throw new DSSErrorException(90037, "该工程没有可以导出的工作流,请检查工作流是否都为空");
        }
        //打包导出工程
        return ZipHelper.zipExportProject(flowExportSaveBasePath);
    }


    private void exportAllSubFlows(String userName, DSSFlow dssFlowParent, Long projectId, String projectName,
                                   String projectExportBasePath, Workspace workspace, List<DSSLabel> dssLabels) throws Exception {
        List<? extends DSSFlow> subFlows = dssFlowParent.getChildren();
        if (subFlows != null) {
            for (DSSFlow subFlow : subFlows) {
                String savePath = projectExportBasePath + File.separator + subFlow.getName() + File.separator + subFlow.getName() + ".json";
                //导出子flow的json文件
                String flowJson = downloadFlowJsonFromBml(userName, subFlow.getResourceId(), subFlow.getBmlVersion(), savePath);
                if (!subFlow.getHasSaved()) {
                    logger.info("工作流{}从未保存过，忽略", subFlow.getName());
                } else if (StringUtils.isNotBlank(flowJson)) {
                    exportFlowResources(userName, projectId, projectName, projectExportBasePath, flowJson, subFlow.getName(), workspace, dssLabels);
                    exportAllSubFlows(userName, subFlow, projectId, projectName, projectExportBasePath, workspace, dssLabels);
                } else {
                    String warnMsg = String.format(DSSWorkFlowConstant.PUBLISH_FLOW_REPORT_FORMATE, subFlow.getName(), subFlow.getBmlVersion());
                    logger.info(warnMsg);
                    throw new DSSErrorException(90014, warnMsg);
                }
            }
        }
    }


    private String genWorkFlowExportDir(String projectExportPath, String flowName) {
        return projectExportPath + File.separator + flowName;
    }

    @Override
    public void exportFlowResources(String userName, Long projectId, String projectName,
                                    String projectSavePath, String flowJson, String flowName,
                                    Workspace workspace, List<DSSLabel> dssLabels) throws Exception {
        String workFlowExportPath = genWorkFlowExportDir(projectSavePath, flowName);
        String workFlowResourceSavePath = workFlowExportPath + File.separator + "resource";
        String appConnResourceSavePath = workFlowExportPath + File.separator + "appconn-resource";
        if (StringUtils.isNotEmpty(workFlowExportPath)) {
            //导出工作流资源文件
            List<Resource> resources = workFlowParser.getWorkFlowResources(flowJson);
            if (resources != null) {
                resources.forEach(resource -> {
                    downloadFlowResourceFromBml(userName, resource, workFlowResourceSavePath);
                });
            }

            //导出工作流节点资源文件,工作流节点appconn文件
            List<DSSNode> nodes = workFlowParser.getWorkFlowNodes(flowJson);
            CountDownLatch countDownLatch;
            AtomicInteger failedImportCount = new AtomicInteger(0);
            if (nodes != null) {
                countDownLatch = new CountDownLatch(nodes.size());
                for (DSSNode node : nodes) {
                    nodeExportThreadPool.submit(() -> {
                        try {
                            nodeExportService.downloadNodeResourceToLocal(userName, node, workFlowResourceSavePath);
                            NodeInfo nodeInfo = nodeInfoMapper.getWorkflowNodeByType(node.getNodeType());
                            if (Boolean.TRUE.equals(nodeInfo.getSupportJump()) && nodeInfo.getJumpType() == 1) {
//                    if (MapUtils.isNotEmpty(node.getJobContent()) && !node.getJobContent().containsKey("script")) {
                                logger.info("node.getJobContent() is :{}", node.getJobContent());
                                nodeExportService.downloadAppConnResourceToLocal(userName, projectId, projectName, node, appConnResourceSavePath, workspace, dssLabels);
                            }
                        } catch (Exception e) {
                            //todo 失败重试
                            failedImportCount.getAndAdd(1);
                            logger.error("failed to export node:{}", node.getName(), e);
                        } finally {
                            countDownLatch.countDown();
                        }
                    });
                }
                boolean success = false;
                try {
                    success = countDownLatch.await(30, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    logger.error("failed to export node for workflow:{}", flowName, e);
                    throw new DSSErrorException(90071, "导出节点超时！");
                }
                if (failedImportCount.get() > 0) {
                    throw new DSSErrorException(90070, "有" + failedImportCount.get() + "个节点导出失败，请重试！");
                }
            }

        } else {
            throw new DSSErrorException(90067, "工作流导出生成路径为空");
        }
    }

    @Override
    public String downloadFlowJsonFromBml(String userName, String resourceId, String version, String savePath) {
        return bmlService.downloadAndGetFlowJson(userName, resourceId, version, savePath);
    }

    private String downloadFlowResourceFromBml(String userName, Resource resource, String savePath) {
        String flowResourcePath = savePath + File.separator + resource.getResourceId() + ".re";
        return bmlService.downloadToLocalPath(userName, resource.getResourceId(), resource.getVersion(), flowResourcePath);
    }

    private void getAllDssFlowsByRootflowId(DSSFlow parentFlow, List<DSSFlow> flowList) {
        List<Long> subFlowIds = flowMapper.selectSavedSubFlowIDByParentFlowID(parentFlow.getId());
        if (subFlowIds.size() > 0) {
            for (Long subFlowId : subFlowIds) {

                DSSFlow subDssFlow = flowMapper.selectFlowByID(subFlowId);
                if (!subDssFlow.getHasSaved()) {
                    logger.info("工作流{}从未保存过，忽略", subDssFlow.getName());
                } else {
                    flowList.add(subDssFlow);
                    parentFlow.addChildren(subDssFlow);
                    getAllDssFlowsByRootflowId(subDssFlow, flowList);
                }
            }
        }
    }
}
