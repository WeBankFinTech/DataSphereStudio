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

package com.webank.wedatasphere.dss.workflow.service.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.entity.node.DSSEdge;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.dss.common.entity.node.Node;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.common.utils.RpcAskUtils;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.contextservice.service.impl.ContextServiceImpl;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestQuertByAppIdOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestQueryByIdOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestUpdateOrchestratorBML;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.utils.DSSJobContentConstant;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.WorkFlowManager;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import com.webank.wedatasphere.dss.workflow.common.parser.NodeParser;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.core.WorkflowFactory;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowWithContextImpl;
import com.webank.wedatasphere.dss.workflow.core.json2flow.JsonToFlowParser;
import com.webank.wedatasphere.dss.workflow.dao.*;
import com.webank.wedatasphere.dss.workflow.dto.NodeContentDO;
import com.webank.wedatasphere.dss.workflow.dto.NodeContentUIDO;
import com.webank.wedatasphere.dss.workflow.dto.NodeMetaDO;
import com.webank.wedatasphere.dss.workflow.entity.CommonAppConnNode;
import com.webank.wedatasphere.dss.workflow.entity.NodeInfo;
import com.webank.wedatasphere.dss.workflow.entity.vo.ExtraToolBarsVO;
import com.webank.wedatasphere.dss.workflow.io.export.NodeExportService;
import com.webank.wedatasphere.dss.workflow.io.input.NodeInputService;
import com.webank.wedatasphere.dss.workflow.lock.DSSFlowEditLockManager;
import com.webank.wedatasphere.dss.workflow.lock.Lock;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import com.webank.wedatasphere.dss.workflow.service.SaveFlowHook;
import com.webank.wedatasphere.dss.workflow.service.WorkflowNodeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.cs.client.utils.SerializeHelper;
import org.apache.linkis.cs.common.utils.CSCommonUtils;
import org.apache.linkis.rpc.Sender;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant.*;

@Service
public class DSSFlowServiceImpl implements DSSFlowService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FlowMapper flowMapper;
    @Autowired
    private LockMapper lockMapper;
    @Autowired
    private NodeInfoMapper nodeInfoMapper;
    @Autowired
    private NodeInputService nodeInputService;

    @Autowired
    private WorkFlowParser workFlowParser;
    @Autowired
    private NodeParser nodeParser;
    @Autowired
    @Qualifier("workflowBmlService")
    private BMLService bmlService;
    @Autowired
    private NodeExportService nodeExportService;
    @Autowired
    private WorkflowNodeService workflowNodeService;

    @Autowired
    private SaveFlowHook saveFlowHook;

    @Autowired
    private WorkFlowManager workFlowManager;
    @Autowired
    private NodeContentMapper nodeContentMapper;
    @Autowired
    private NodeContentUIMapper nodeContentUIMapper;
    @Autowired
    private NodeMetaMapper nodeMetaMapper;

    private static ContextService contextService = ContextServiceImpl.getInstance();


    protected Sender getOrchestratorSender() {
        return DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender();
    }

    @Override
    public DSSFlow getFlowByID(Long id) {
        return flowMapper.selectFlowByID(id);
    }

    @Override
    public DSSFlow getFlowWithJsonAndSubFlowsByID(Long rootFlowId) {
        return genDSSFlowTree(rootFlowId);
    }

    private DSSFlow genDSSFlowTree(Long parentFlowId) {
        DSSFlow cyFlow = flowMapper.selectFlowByID(parentFlowId);

        String userName = cyFlow.getCreator();
        Map<String, Object> query = bmlService.query(userName, cyFlow.getResourceId(), cyFlow.getBmlVersion());
        cyFlow.setFlowJson(query.get("string").toString());

        List<Long> subFlowIDs = flowMapper.selectSubFlowIDByParentFlowID(parentFlowId);
        logger.info("find subFlow_ids:{} for parentFlow_id:{}", subFlowIDs, parentFlowId);
        for (Long subFlowID : subFlowIDs) {
            if (cyFlow.getChildren() == null) {
                cyFlow.setChildren(new ArrayList<DSSFlow>());
            }
            DSSFlow subFlow = genDSSFlowTree(subFlowID);

            cyFlow.addChildren(subFlow);
        }
        return cyFlow;
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public DSSFlow addFlow(DSSFlow dssFlow,
                           String contextID, String orcVersion,
                           String schedulerAppConn) throws DSSErrorException {
        try {
            flowMapper.insertFlow(dssFlow);
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage());
            throw new DSSErrorException(90003, "工作流名不能重复");
        }
        Map<String, Object> flowJsonMap = new HashMap<>();
        String userName = dssFlow.getCreator();
        if (StringUtils.isNotBlank(contextID)) {
            flowJsonMap.put(CSCommonUtils.CONTEXT_ID_STR, contextID);
        }
        if (StringUtils.isNotBlank(orcVersion)) {
            flowJsonMap.put(DSSJobContentConstant.ORC_VERSION_KEY, orcVersion);
        }
        if (StringUtils.isNotBlank(schedulerAppConn)) {
            flowJsonMap.put(SCHEDULER_APP_CONN_NAME, schedulerAppConn);
        }
        String jsonFlow = DSSCommonUtils.COMMON_GSON.toJson(flowJsonMap);
        Map<String, Object> bmlReturnMap = bmlService.upload(userName, jsonFlow, UUID.randomUUID().toString() + ".json",
                dssFlow.getName());

        dssFlow.setResourceId(bmlReturnMap.get("resourceId").toString());

        dssFlow.setBmlVersion(bmlReturnMap.get("version").toString());
        Long parentFlowID = null;
        if (!dssFlow.getRootFlow()) {
            parentFlowID = flowMapper.getParentFlowID(dssFlow.getId());
        }
        //todo update dssflow
        contextService.checkAndSaveContext(jsonFlow, String.valueOf(parentFlowID));
        flowMapper.updateFlowInputInfo(dssFlow);
        return dssFlow;
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public DSSFlow addSubFlow(DSSFlow dssFlow, Long parentFlowID, String contextIDStr, String orcVersion, String schedulerAppConn) throws DSSErrorException {
        if (checkExistSameSubflow(parentFlowID, dssFlow.getName())){
            throw new DSSErrorException(90003, "子工作流名不能重复");
        }
        DSSFlow parentFlow = flowMapper.selectFlowByID(parentFlowID);
        dssFlow.setProjectId(parentFlow.getProjectId());
        DSSFlow subFlow = addFlow(dssFlow, contextIDStr, orcVersion, schedulerAppConn);
        //数据库中插入关联信息
        flowMapper.insertFlowRelation(subFlow.getId(), parentFlowID);
        return subFlow;
    }

    @Override
    public DSSFlow getFlow(Long flowID) throws NullPointerException {
        DSSFlow dssFlow = getFlowByID(flowID);
        //todo update
        String userName = dssFlow.getCreator();
        Map<String, Object> query = bmlService.query(userName, dssFlow.getResourceId(), dssFlow.getBmlVersion());
        dssFlow.setFlowJson(query.get("string").toString());
        return dssFlow;
    }

    @Override
    public List<String> getSubFlowContextIdsByFlowIds(List<Long> flowIdList) throws ErrorException {
        ArrayList<String> contextIdList = new ArrayList<>();
        // 查出所有子工作流的上下文ID
        for (Long flowId : flowIdList) {
            generateSubFlowContextIdByFlowID(flowId, contextIdList);
        }
        return contextIdList;
    }

    private void generateSubFlowContextIdByFlowID(Long flowId, ArrayList<String> contextIdList) throws ErrorException {
        List<Long> subFlowIDs = flowMapper.selectSubFlowIDByParentFlowID(flowId);
        if (subFlowIDs.size() > 0) {
            // 获取子工作流 contextId
            JsonToFlowParser jsonToFlowParser = WorkflowFactory.INSTANCE.getJsonToFlowParser();
            for (Long subFlowID : subFlowIDs) {
                DSSFlow dssFlow = getFlow(subFlowID);
                Workflow workflow = jsonToFlowParser.parse(dssFlow);
                String contextIDStr = ((WorkflowWithContextImpl) workflow).getContextID();
                if (StringUtils.isNotBlank(contextIDStr)) {
                    // 获取需要清理的contextId
                    contextIdList.add(SerializeHelper.deserializeContextID(contextIDStr).getContextId());
                }
                //获取下一层的subflow的上下文Id
                generateSubFlowContextIdByFlowID(subFlowID, contextIdList);
            }
        }
    }


    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public void updateFlowBaseInfo(DSSFlow DSSFlow) throws DSSErrorException {
        try {
            flowMapper.updateFlowBaseInfo(DSSFlow);
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage());
            throw new DSSErrorException(90003, "工作流名不能重复");
        }
    }


    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public void batchDeleteFlow(List<Long> flowIDlist) {
        flowIDlist.forEach(this::deleteFlow);
    }

    @Override
    public String saveFlow(Long flowID,
                           String jsonFlow,
                           String comment,
                           String userName,
                           String workspaceName,
                           String projectName,
                           LabelRouteVO labels) throws IOException {

        DSSFlow dssFlow = flowMapper.selectFlowByID(flowID);
        String creator = dssFlow.getCreator();
        if (StringUtils.isNotEmpty(creator)) {
            userName = creator;
        }
        String flowJsonOld = getFlowJson(userName, projectName, dssFlow);

        // 解析并保存元数据
        if (dssFlow.getRootFlow()) {
            List<DSSLabel> dssLabelList = Arrays.asList(new EnvDSSLabel(labels.getRoute()));
            saveFlowMetaData(flowID, jsonFlow, dssLabelList);
        }

        if (isEqualTwoJson(flowJsonOld, jsonFlow)) {
            logger.info("saveFlow is not change");
            return dssFlow.getBmlVersion();
        } else {
            logger.info("saveFlow is change");
        }
        checkSubflowDependencies(userName, flowID, jsonFlow);

        String resourceId = dssFlow.getResourceId();
        Long parentFlowID = flowMapper.getParentFlowID(flowID);
        // 这里不要检查ContextID具体版本等，只要存在就不创建 2020-0423
        jsonFlow = contextService.checkAndCreateContextID(jsonFlow, dssFlow.getBmlVersion(),
                workspaceName, projectName, dssFlow.getName(), userName, false);
        saveFlowHook.beforeSave(jsonFlow,dssFlow,parentFlowID);
        Map<String, Object> bmlReturnMap = bmlService.update(userName, resourceId, jsonFlow);

        dssFlow.setId(flowID);
        dssFlow.setHasSaved(true);
        dssFlow.setDescription(comment);
        dssFlow.setResourceId(bmlReturnMap.get("resourceId").toString());
        dssFlow.setBmlVersion(bmlReturnMap.get("version").toString());
        updateMetrics(dssFlow, jsonFlow);
        //todo 数据库增加版本更新
        flowMapper.updateFlowInputInfo(dssFlow);

        try {
            contextService.checkAndSaveContext(jsonFlow, String.valueOf(parentFlowID));
        } catch (DSSErrorException e) {
            logger.error("Failed to saveContext: ", e);
            throw new DSSRuntimeException(e.getErrCode(),"保存ContextId失败，您可以尝试重新发布工作流！原因：" + ExceptionUtils.getRootCauseMessage(e),e);
        }
        saveFlowHook.afterSave(jsonFlow,dssFlow,parentFlowID);
        String version = bmlReturnMap.get("version").toString();
        // 对子工作流,需更新父工作流状态，以便提交
        Long updateFlowId = parentFlowID == null? dssFlow.getId():parentFlowID;
        updateTOSaveStatus(dssFlow.getProjectId(), updateFlowId);

        return version;
    }

    private void updateTOSaveStatus(Long projectId, Long flowID) {
        try {
            DSSProject projectInfo = DSSFlowEditLockManager.getProjectInfo(projectId);
            //仅对接入Git的项目 更新状态为 保存
            if (projectInfo.getAssociateGit() != null && projectInfo.getAssociateGit()) {
                Long rootFlowId = getRootFlowId(flowID);
                if (rootFlowId != null) {
                    OrchestratorVo orchestratorVo = RpcAskUtils.processAskException(getOrchestratorSender().ask(new RequestUpdateOrchestratorBML(rootFlowId, new BmlResource())),
                            OrchestratorVo.class, RequestQueryByIdOrchestrator.class);
                    lockMapper.updateOrchestratorStatus(orchestratorVo.getDssOrchestratorInfo().getId(), OrchestratorRefConstant.FLOW_STATUS_SAVE);
                }
            }
        } catch (DSSErrorException e) {
            logger.error("getProjectInfo failed by:", e);
            throw new DSSRuntimeException(e.getErrCode(),"更新工作流状态失败，您可以尝试重新保存工作流！原因：" + ExceptionUtils.getRootCauseMessage(e),e);
        }
    }

    private Long getRootFlowId(Long flowId) {
        if (flowId == null) {
            return null;
        }
        DSSFlow dssFlow = flowMapper.selectFlowByID(flowId);
        if (dssFlow == null) {
            return null;
        }
        if (dssFlow.getRootFlow()) {
            return dssFlow.getId();
        } else {
            Long parentFlowID = flowMapper.getParentFlowID(flowId);
            return getRootFlowId(parentFlowID);
        }
    }

    @Override
    public void saveFlowMetaData(Long flowID, String jsonFlow, List<DSSLabel> dssLabelList) {
        // 解析 jsonflow
        // 解析 proxyUser
        List<Map<String, Object>> props = DSSCommonUtils.getFlowAttribute(jsonFlow, "props");
        String proxyUser = null;
        StringBuilder globalVar = new StringBuilder();
        for (Map<String, Object> prop : props) {
            if (prop.containsKey("user.to.proxy")) {
                proxyUser = prop.get("user.to.proxy").toString();
            }else {
                for (Map.Entry<String, Object> map : prop.entrySet()) {
                    if (map.getValue() != null) {
                        globalVar.append(map.getKey() + "=" + map.getValue().toString());
                        globalVar.append(";");
                    }
                }
            }
        }
        // 解析 resources
        List<Map<String, Object>> resources = DSSCommonUtils.getFlowAttribute(jsonFlow, "resources");
        StringBuilder resourceToString = new StringBuilder();
        for (Map<String, Object> resource : resources) {
            resourceToString.append(resource.values());
            resourceToString.append(";");
        }

        List<DSSNodeDefault> workFlowNodes = DSSCommonUtils.getWorkFlowNodes(jsonFlow);
        Sender orcSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender(dssLabelList);
        OrchestratorVo orchestratorVo = RpcAskUtils.processAskException(orcSender.ask(new RequestQuertByAppIdOrchestrator(flowID)),
                OrchestratorVo.class, RequestQueryByIdOrchestrator.class);
        Long orchestratorId = orchestratorVo.getDssOrchestratorInfo().getId();
        NodeMetaDO nodeMetaByOrchestratorId = nodeMetaMapper.getNodeMetaByOrchestratorId(orchestratorId);

        if (nodeMetaByOrchestratorId == null) {
            nodeMetaMapper.insertNodeMeta(new NodeMetaDO(orchestratorId, proxyUser, resourceToString.toString(), globalVar.toString()));
        } else {
            nodeMetaMapper.updateNodeMeta(new NodeMetaDO(nodeMetaByOrchestratorId.getId(), orchestratorId, proxyUser, resourceToString.toString(), globalVar.toString()));
        }

        List<Long> contentIdListByOrchestratorId = nodeContentMapper.getContentIdListByOrchestratorId(orchestratorId);

        logger.info("workFlowNodes:{}", workFlowNodes);
        List<NodeContentUIDO> nodeContentUIDOS = new ArrayList<>();
        for (DSSNodeDefault nodeDefault : workFlowNodes) {
            String key = nodeDefault.getKey();
            String id = nodeDefault.getId();
            String jobType = nodeDefault.getJobType();
            Long createTime = nodeDefault.getCreateTime();
            Long modifyTime = nodeDefault.getModifyTime();
            Date createDate = new Date(createTime);
            Date modifyDate = new Date(modifyTime);
            String modifyUser = nodeDefault.getModifyUser();
            NodeContentDO contentDO = new NodeContentDO(key, id, jobType, orchestratorId, createDate, modifyDate, modifyUser);
            NodeContentDO nodeContentByKey = nodeContentMapper.getNodeContentByKey(key);

            if (nodeContentByKey == null) {
                nodeContentMapper.insert(contentDO);
                nodeContentByKey = nodeContentMapper.getNodeContentByKey(key);
            } else {
                contentDO.setId(nodeContentByKey.getId());
                nodeContentMapper.update(contentDO);
            }
            Long contentByKeyId = nodeContentByKey.getId();
            String title = nodeDefault.getTitle();
            if (title != null) {
                nodeContentUIDOS.add(new NodeContentUIDO(contentByKeyId, "title", title));
            }
            String desc = nodeDefault.getDesc();
            if (desc != null) {
                nodeContentUIDOS.add(new NodeContentUIDO(contentByKeyId, "desc", desc));
            }
            Map<String, Object> params = nodeDefault.getParams();
            if (params != null) {
                Map<String, Map<String, Object>> mapParams = (Map<String, Map<String, Object>>)params.get("configuration");
                if (mapParams != null) {
                    for (Map.Entry<String, Map<String, Object>> entry : mapParams.entrySet()) {
                        // special runtime startup
                        String paramKey = entry.getKey();
                        Map<String, Object> paramValue = entry.getValue();
                        for (Map.Entry<String, Object> paramEntry : paramValue.entrySet()) {
                            String paramName = paramEntry.getKey();
                            if (paramEntry.getValue() != null) {
                                String paramVal = paramEntry.getValue().toString();
                                logger.info("{}:{}", paramName, paramVal);
                                nodeContentUIDOS.add(new NodeContentUIDO(contentByKeyId, paramName, paramVal));
                            }

                        }
                    }
                }
                logger.info(mapParams.toString());
            }

        }
        // 先删后增
        if (CollectionUtils.isNotEmpty(contentIdListByOrchestratorId)) {
            nodeContentUIMapper.deleteNodeContentUIByContentList(contentIdListByOrchestratorId);
        }
        nodeContentUIMapper.batchInsertNodeContentUI(nodeContentUIDOS);
    }

    /**
     * 当数据库的父子工作流依赖关系和json中不一致时，该方法会删除数据库中的脏数据
     *
     * @param user
     * @param flowId
     * @param flowJson
     * @throws IOException
     */
    private void checkSubflowDependencies(String user, long flowId, String flowJson) throws IOException {
        List<String> nodeJsonList = workFlowParser.getWorkFlowNodesJson(flowJson);
        if (CollectionUtils.isEmpty(nodeJsonList)) {
            return;
        }
        List<DSSFlow> subflowInfos = flowMapper.getSubflowInfoByParentId(flowId);
        List<String> subflowTitleList = new ArrayList<>();
        for (String nodeJson : nodeJsonList) {
            Map<String, Object> nodeMap = BDPJettyServerHelper.jacksonJson().readValue(nodeJson, Map.class);
            String nodeType = nodeMap.get(JOBTYPE_KEY).toString();
            if ("workflow.subflow".equals(nodeType)) {
                String title = nodeMap.get(TITLE_KEY).toString();
                subflowTitleList.add(title);
            }
        }
        subflowInfos.forEach(subflow -> {
            if (subflowTitleList.stream().noneMatch(t -> t.equals(subflow.getName()))) {
                //查看子工作流有无内容
                String subflowJson = bmlService.query(user, subflow.getResourceId(), subflow.getBmlVersion()).get("string").toString();
                if (CollectionUtils.isEmpty(workFlowParser.getWorkFlowNodesJson(subflowJson))) {
                    flowMapper.deleteFlowRelation(subflow.getId());
                } else {
                    logger.warn("子工作流内容不为空，不予删除和父工作流的依赖关系。");
                }
            }
        });
    }

    private void updateMetrics(DSSFlow dssFlow, String flowJson) {
        Map<String, Object> metricsMap = new HashMap<>();
        List<DSSNode> nodes = workFlowParser.getWorkFlowNodes(flowJson);
        metricsMap.put("totalNodes", nodes == null ? 0 : nodes.size());
        if (nodes != null) {
            Map<String, Long> mapCnt = nodes.stream().collect(Collectors.groupingBy(DSSNode::getNodeType, Collectors.counting()));
            metricsMap.putAll(mapCnt);
        }
        dssFlow.setMetrics(new Gson().toJson(metricsMap));
    }

    public boolean isEqualTwoJson(String oldJsonNode, String newJsonNode) {
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(oldJsonNode).getAsJsonObject();
        jsonObject.remove("updateTime");
        jsonObject.remove("comment");
        String tempOldJson = gson.toJson(jsonObject);

        JsonObject jsonObject2 = parser.parse(newJsonNode).getAsJsonObject();
        jsonObject2.remove("updateTime");
        jsonObject2.remove("comment");
        String tempNewJson = gson.toJson(jsonObject2);
        return tempOldJson.equals(tempNewJson);
    }

    public String getFlowJson(String userName, String projectName, DSSFlow dssFlow) {
        String flowExportSaveBasePath = IoUtils.generateIOPath(userName, projectName, "");
        String savePath = flowExportSaveBasePath + File.separator + dssFlow.getName() + File.separator + dssFlow.getName() + ".json";
        String flowJson = bmlService.downloadAndGetText(userName, dssFlow.getResourceId(), dssFlow.getBmlVersion(), savePath);
        return flowJson;
    }

    @Override
    public Long getParentFlowID(Long flowID) {
        return flowMapper.getParentFlowID(flowID);
    }

    @Override
    public List<ExtraToolBarsVO> getExtraToolBars(long workspaceId, long projectId) {
        List<ExtraToolBarsVO> retList = new ArrayList<>();
        retList.add(new ExtraToolBarsVO("前往调度中心", DSSWorkFlowConstant.GOTO_SCHEDULER_CENTER_URL.getValue() + "?workspaceId=" + workspaceId, "icon:null"));
        return retList;
    }


    public void deleteFlow(Long flowId) {
        List<Long> subFlowIDs = flowMapper.selectSubFlowIDByParentFlowID(flowId);
        for (Long subFlowID : subFlowIDs) {
            deleteFlow(subFlowID);
        }
        for (Long subFlowID : subFlowIDs) {
            deleteDWSDB(subFlowID);
            // TODO: 2019/6/5 wtss发布过的工作流的删除？
            // TODO: 2019/6/5 json中资源的删除
            // TODO: 2019/6/5 事务的保证
        }
        deleteDWSDB(flowId);
    }

    private void deleteDWSDB(Long flowID) {
        flowMapper.deleteFlowBaseInfo(flowID);
        flowMapper.deleteFlowRelation(flowID);
        //第一期没有工作流的发布，所以不需要删除dws工作流的发布表
    }

    /**
     * 支持跨项目拷贝工作流
     *
     * @param rootFlowId   拷贝基于的工作流id
     * @param userName
     * @param workspace
     * @param projectName  目标工程名
     * @param version      新的编排版本
     * @param contextIdStr 新的contextId
     * @param description
     * @param dssLabels
     * @param nodeSuffix   节点后缀
     * @param newFlowName  新的工作流名称
     * @param newProjectId 目标工程id
     * @return
     * @throws DSSErrorException
     * @throws IOException
     */
    @Override
    public DSSFlow copyRootFlow(Long rootFlowId, String userName, Workspace workspace,
                                String projectName, String version, String contextIdStr,
                                String description, List<DSSLabel> dssLabels, String nodeSuffix,
                                String newFlowName, Long newProjectId) throws DSSErrorException, IOException {
        DSSFlow dssFlow = flowMapper.selectFlowByID(rootFlowId);
        DSSFlow rootFlowWithSubFlows = copyFlowAndSetSubFlowInDB(dssFlow, userName, description, nodeSuffix, newFlowName, newProjectId);
        updateFlowJson(userName, projectName, rootFlowWithSubFlows, version, null,
                contextIdStr, workspace, dssLabels, nodeSuffix);
        DSSFlow copyFlow= flowMapper.selectFlowByID(rootFlowWithSubFlows.getId());
        copyFlow.setFlowIdParamConfTemplateIdTuples(rootFlowWithSubFlows.getFlowIdParamConfTemplateIdTuples());
        return copyFlow;
    }


    @Override
    public boolean checkExistSameSubflow(Long parentFlowID, String name) {
        List<String> subflowName = flowMapper.getSubflowName(parentFlowID);
        return subflowName.stream().anyMatch(s -> s.equals(name));
    }

    @Override
    public boolean checkExistSameFlow(Long parentFlowID, String name, String existName) {
        List<String> subflowName = flowMapper.getSubflowName(parentFlowID);
        if (name.equals(existName)) {
            return false;
        }
        return subflowName.stream().anyMatch(s -> s.equals(name));
    }

    @Override
    public List<String> checkIsSave(Long flowID, String jsonFlow) {
        List<String> subflowName = flowMapper.getSubflowName(flowID);
        List<DSSNode> workFlowNodes = workFlowParser.getWorkFlowNodes(jsonFlow);
        return workFlowNodes.stream().filter(t -> Objects.equals("workflow.subflow", t.getNodeType()))
                .map(DSSNode::getName)
                .filter(name -> !subflowName.contains(name))
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkIsExistSameFlow(String jsonFlow) {
        List<DSSNode> workFlowNodes = workFlowParser.getWorkFlowNodes(jsonFlow);
        long distinctSize = workFlowNodes.stream().map(Node::getName).distinct().count();
        return distinctSize < workFlowNodes.size();
    }

    /**
     * @param dssFlow
     * @param userName
     * @param description
     * @param newFlowName       拷贝时新的工作流名
     * @param newProjectId      拷贝到新的工程id
     * @param subFlowNameSuffix 工作流拷贝时需要为所有子工作流节点名添加后缀
     * @return
     */
    private DSSFlow copyFlowAndSetSubFlowInDB(DSSFlow dssFlow, String userName, String description,
                                              String subFlowNameSuffix, String newFlowName, Long newProjectId) {
        DSSFlow cyFlow = new DSSFlow();
        BeanUtils.copyProperties(dssFlow, cyFlow, "children", "flowVersions");
        //封装flow信息
        cyFlow.setCreator(userName);
        cyFlow.setCreateTime(new Date());
        //工作流复制时用户输入的的目标工作流名
        if (StringUtils.isNotBlank(newFlowName)) {
            cyFlow.setName(newFlowName);
        }
        //跨项目复制时，newProjectId为目标工程id
        if (newProjectId != null) {
            cyFlow.setProjectId(newProjectId);
        }
        if (StringUtils.isNotBlank(subFlowNameSuffix) && !cyFlow.getRootFlow()) {
            cyFlow.setName(cyFlow.getName() + "_" + subFlowNameSuffix);
        }
        if (StringUtils.isNotBlank(description)) {
            cyFlow.setDescription(description);
        }
        cyFlow.setId(null);
        flowMapper.insertFlow(cyFlow);
        List<Long> subFlowIDs = flowMapper.selectSubFlowIDByParentFlowID(dssFlow.getId());
        for (Long subFlowID : subFlowIDs) {
            DSSFlow subDSSFlow = flowMapper.selectFlowByID(subFlowID);
            if (dssFlow.getChildren() == null) {
                dssFlow.setChildren(new ArrayList<DSSFlow>());
            }
            DSSFlow copySubFlow = copyFlowAndSetSubFlowInDB(subDSSFlow, userName, description, subFlowNameSuffix, null, newProjectId);
            persistenceFlowRelation(copySubFlow.getId(), cyFlow.getId());
            cyFlow.addChildren(copySubFlow);
        }
        return cyFlow;
    }

    private void updateFlowJson(String userName, String projectName, DSSFlow rootFlow,
                                String version, Long parentFlowId, String contextIdStr,
                                Workspace workspace, List<DSSLabel> dssLabels, String nodeSuffix) throws DSSErrorException, IOException {
        String flowJson = bmlService.readTextFromBML(userName, rootFlow.getResourceId(), rootFlow.getBmlVersion());
        //如果包含subflow,需要一同导入subflow内容，并更新parrentflow的json内容
        // TODO: 2020/7/31 优化update方法里面的saveContent
        //copy subflow need new contextID
        if (!rootFlow.getRootFlow()) {
            contextIdStr = contextService.checkAndInitContext(flowJson, parentFlowId.toString(), workspace.getWorkspaceName(), projectName, rootFlow.getName(), version, userName);
            logger.info("update subflow contextID :" + contextIdStr + ",flow name is " + rootFlow.getName());
        }
        String updateFlowJson = updateFlowContextIdAndVersion(flowJson, contextIdStr, version);
        if (StringUtils.isNotBlank(nodeSuffix)) {
            updateFlowJson = addFLowNodeSuffix(updateFlowJson, nodeSuffix);
        }
        //重新上传工作流资源
        updateFlowJson = uploadFlowResourceToBml(userName, updateFlowJson, projectName, rootFlow);
        //上传节点的资源或调用appconn的copyRef
        updateFlowJson = updateWorkFlowNodeJson(userName, projectName, updateFlowJson, rootFlow,
                version, workspace, dssLabels);
        List<? extends DSSFlow> subFlows = rootFlow.getChildren();
        List<String[]> templateIds = new ArrayList<>();
        if (subFlows != null) {
            for (DSSFlow subflow : subFlows) {
                updateFlowJson(userName, projectName, subflow, version, rootFlow.getId(),
                        contextIdStr, workspace, dssLabels, nodeSuffix);
                templateIds.addAll(subflow.getFlowIdParamConfTemplateIdTuples());
            }
        }

        DSSFlow updateDssFlow = uploadFlowJsonToBml(userName, projectName, rootFlow, updateFlowJson);
        List<String> tempIds = workFlowParser.getParamConfTemplate(updateFlowJson);
        List<String[]> templateIdsInRoot = tempIds.stream()
                .map(e->new String[]{updateDssFlow.getId().toString(),e})
                .collect(Collectors.toList());
        templateIds.addAll(templateIdsInRoot);
        rootFlow.setFlowIdParamConfTemplateIdTuples(templateIds);
        //todo add dssflow to database
        flowMapper.updateFlowInputInfo(updateDssFlow);
        contextService.checkAndSaveContext(updateFlowJson, String.valueOf(parentFlowId));
    }

    private String addFLowNodeSuffix(String flowJson, String nodeSuffix) throws IOException {
        List<String> nodeJsonList = workFlowParser.getWorkFlowNodesJson(flowJson);
        List<DSSEdge> edgeList = workFlowParser.getWorkFlowEdges(flowJson);
        if (CollectionUtils.isEmpty(nodeJsonList)) {
            return flowJson;
        }
        List<Map<String, Object>> nodeList = new ArrayList<>();
        for (String nodeJson : nodeJsonList) {
            Map<String, Object> nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(nodeJson, Map.class);
            nodeJsonMap.replace(TITLE_KEY, nodeJsonMap.get(TITLE_KEY) + "_" + nodeSuffix);
            List<Resource> resourceList = nodeParser.getNodeResource(nodeJson);
            if (CollectionUtils.isNotEmpty(resourceList)) {
                String oldKey = (String) nodeJsonMap.get("key");
                final String newKey = UUID.randomUUID().toString();
                //需要替换resource的fileName
                resourceList.forEach(resource -> {
                    resource.setFileName(resource.getFileName().replace(oldKey, newKey));
                });
                nodeJsonMap.put("resources", resourceList);
                Map jobContent = (Map) nodeJsonMap.get("jobContent");
                if (MapUtils.isNotEmpty(jobContent)) {
                    jobContent.forEach((k, v) -> {
                        jobContent.put(k, ((String) v).replace(oldKey, newKey));
                    });
                    nodeJsonMap.put("jobContent", jobContent);
                }
                nodeJsonMap.put("id", newKey);
                nodeJsonMap.put("key", newKey);
                if (CollectionUtils.isNotEmpty(edgeList)) {
                    edgeList.forEach(e -> {
                        if (e.getSource().equals(oldKey)) {
                            e.setSource(newKey);
                        }
                        if (e.getTarget().equals(oldKey)) {
                            e.setTarget(newKey);
                        }
                    });
                }
            }
            nodeList.add(nodeJsonMap);
        }
        flowJson = workFlowParser.updateFlowJsonWithKey(flowJson, "edges", edgeList);
        return workFlowParser.updateFlowJsonWithKey(flowJson, "nodes", nodeList);
    }

    //上传工作流资源
    public String uploadFlowResourceToBml(String userName, String flowJson, String projectName, DSSFlow dssFlow) throws IOException {
        List<Resource> resourceList = workFlowParser.getWorkFlowResources(flowJson);
        if (CollectionUtils.isEmpty(resourceList)) {
            return flowJson;
        }
        String flowExportSaveBasePath = IoUtils.generateIOPath(userName, projectName, "");
        String savePath = flowExportSaveBasePath + File.separator + dssFlow.getName() + File.separator + "resource";
        //上传文件获取resourceId和version save应该是已经有
        resourceList.forEach(resource -> {
            //从bml取出resource
            String flowResourcePath = savePath + File.separator + resource.getResourceId() + ".re";
            bmlService.downloadToLocalPath(userName, resource.getResourceId(), resource.getVersion(), flowResourcePath);
            //重新上传resource
            InputStream resourceInputStream = bmlService.readLocalResourceFile(userName, flowResourcePath);
            BmlResource bmlReturnMap = bmlService.upload(userName, resourceInputStream, UUID.randomUUID().toString() + ".json", projectName);
            resource.setResourceId(bmlReturnMap.getResourceId());
            resource.setVersion(bmlReturnMap.getVersion());
        });
        //更新flowJson的resources
        return workFlowParser.updateFlowJsonWithKey(flowJson, "resources", resourceList);
    }


    private DSSFlow uploadFlowJsonToBml(String userName, String projectName, DSSFlow dssFlow, String flowJson) throws IOException {
        //上传文件获取resourceId和version save应该是已经有
        Map<String, Object> bmlReturnMap;

        //上传文件获取resourceId和version save应该是已经有
        bmlReturnMap = bmlService.upload(userName, flowJson, UUID.randomUUID().toString() + ".json", projectName);

        dssFlow.setCreator(userName);
        dssFlow.setBmlVersion(bmlReturnMap.get("version").toString());
        dssFlow.setResourceId(bmlReturnMap.get("resourceId").toString());
        dssFlow.setDescription("copy update workflow");
        dssFlow.setSource("copy更新");
        //version表中插入数据
        return dssFlow;
    }

    private String updateFlowContextIdAndVersion(String flowJson, String contextIdStr, String orcVersion) throws IOException {
        return workFlowParser.updateFlowJsonWithMap(flowJson, MapUtils.newCommonMap(CSCommonUtils.CONTEXT_ID_STR, contextIdStr,
                DSSJobContentConstant.ORC_VERSION_KEY, orcVersion));
    }

    private String updateWorkFlowNodeJson(String userName, String projectName,
                                          String flowJson, DSSFlow dssFlow,
                                          String version, Workspace workspace, List<DSSLabel> dssLabels) throws DSSErrorException, IOException {
        if (StringUtils.isEmpty(version)) {
            logger.warn("version id is null when updateWorkFlowNodeJson");
            version = String.valueOf((new Random().nextLong()));
        }
        List<String> nodeJsonList = workFlowParser.getWorkFlowNodesJson(flowJson);
        if (nodeJsonList == null) {
            throw new DSSErrorException(90073, "工作流内没有工作流节点，导入失败." + dssFlow.getName());
        }
        String updateContextId = workFlowParser.getValueWithKey(flowJson, CSCommonUtils.CONTEXT_ID_STR);
        if (nodeJsonList.size() == 0) {
            return flowJson;
        }
        List<DSSFlow> subflows = (List<DSSFlow>) dssFlow.getChildren();
        List<Map<String, Object>> nodeJsonListRes = new ArrayList<>();

        for (String nodeJson : nodeJsonList) {
            //重新上传一份jar文件到bml
            String updateNodeJson = inputNodeFiles(userName, projectName, nodeJson);

            Map<String, Object> nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(updateNodeJson, Map.class);
            //更新subflowID
            String nodeType = nodeJsonMap.get(JOBTYPE_KEY).toString();
            NodeInfo nodeInfo = nodeInfoMapper.getWorkflowNodeByType(nodeType);
            if ("workflow.subflow".equals(nodeType)) {
                String subFlowName = nodeJsonMap.get(TITLE_KEY).toString();
                List<DSSFlow> dssFlowList = subflows.stream().filter(subflow ->
                        subflow.getName().equals(subFlowName)
                ).collect(Collectors.toList());
                if (dssFlowList.size() == 1) {
                    updateNodeJson = nodeInputService.updateNodeSubflowID(updateNodeJson, dssFlowList.get(0).getId());
                    nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(updateNodeJson, Map.class);
                    nodeJsonListRes.add(nodeJsonMap);
                } else if (dssFlowList.size() > 1) {
                    logger.error("工程内存在重复的子工作流节点名称，导入失败" + subFlowName);
                    throw new DSSErrorException(90077, "工程内存在重复的子工作流节点名称，导入失败" + subFlowName);
                } else {
                    logger.error("工程内存在重复的子工作流节点名称，导入失败" + subFlowName);
                    throw new DSSErrorException(90078, "工程内未能找到子工作流节点，导入失败" + subFlowName);
                }
//            } else if (nodeJsonMap.get("jobContent") != null && !((Map) nodeJsonMap.get("jobContent")).containsKey("script")) {
            }else if(nodeInfo==null){
                String msg = String.format("%s note type not exist,please check appconn install successfully", nodeType);
                logger.error(msg);
                throw new DSSRuntimeException(msg);
            } else if (Boolean.TRUE.equals(nodeInfo.getSupportJump()) && nodeInfo.getJumpType() == 1) {
                logger.info("nodeJsonMap.jobContent is:{}", nodeJsonMap.get("jobContent"));
                CommonAppConnNode newNode = new CommonAppConnNode();
                CommonAppConnNode oldNode = new CommonAppConnNode();
                oldNode.setJobContent((Map<String, Object>) nodeJsonMap.get("jobContent"));
                oldNode.setContextId(updateContextId);
                oldNode.setNodeType(nodeType);
                oldNode.setName((String) nodeJsonMap.get(TITLE_KEY));
                oldNode.setFlowId(dssFlow.getId());
                oldNode.setWorkspace(workspace);
                oldNode.setDssLabels(dssLabels);
                oldNode.setFlowName(dssFlow.getName());
                oldNode.setProjectId(dssFlow.getProjectId());
                oldNode.setProjectName(projectName);
                newNode.setName(oldNode.getName());
                Map<String, Object> jobContent = workflowNodeService.copyNode(userName, newNode, oldNode, version);
                nodeJsonMap.put("jobContent", jobContent);
                nodeJsonListRes.add(nodeJsonMap);
            } else {
                nodeJsonListRes.add(nodeJsonMap);
            }
        }

        return workFlowParser.updateFlowJsonWithKey(flowJson, "nodes", nodeJsonListRes);
    }

    //由于每一个节点可能含有jar文件，这个功能不能直接复制使用，因为删掉新版本节点会直接删掉旧版本的node中的jar文件
    //所以重新上传一份jar文件到bml
    private String inputNodeFiles(String userName, String projectName, String nodeJson) throws IOException {
        String flowPath = IoUtils.generateIOPath(userName, projectName, "");
        String workFlowResourceSavePath = flowPath + File.separator + "resource" + File.separator;
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(nodeJson).getAsJsonObject();
        DSSNode node = gson.fromJson(jsonObject, new TypeToken<DSSNodeDefault>() {
        }.getType());
        //先导出来
        nodeExportService.downloadNodeResourceToLocal(userName, node, workFlowResourceSavePath);
        //后导入到bml
        String updateNodeJson = nodeInputService.uploadResourceToBml(userName, nodeJson, workFlowResourceSavePath, projectName);
        return updateNodeJson;
    }

    private void persistenceFlowRelation(Long flowID, Long parentFlowID) {
        DSSFlowRelation relation = flowMapper.selectFlowRelation(flowID, parentFlowID);
        if (relation == null) {
            flowMapper.insertFlowRelation(flowID, parentFlowID);
        }
    }

}
