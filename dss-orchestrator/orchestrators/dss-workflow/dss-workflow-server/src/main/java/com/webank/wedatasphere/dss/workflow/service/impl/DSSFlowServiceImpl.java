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

import cn.hutool.core.collection.CollUtil;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
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
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.common.protocol.project.*;
import com.webank.wedatasphere.dss.common.utils.*;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.contextservice.service.impl.ContextServiceImpl;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.*;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.utils.DSSJobContentConstant;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.WorkFlowManager;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import com.webank.wedatasphere.dss.workflow.common.parser.NodeParser;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import com.webank.wedatasphere.dss.workflow.constant.SignalNodeConstant;
import com.webank.wedatasphere.dss.workflow.constant.WorkflowNodeGroupEnum;
import com.webank.wedatasphere.dss.workflow.core.WorkflowFactory;
import com.webank.wedatasphere.dss.workflow.core.entity.Workflow;
import com.webank.wedatasphere.dss.workflow.core.entity.WorkflowWithContextImpl;
import com.webank.wedatasphere.dss.workflow.core.json2flow.JsonToFlowParser;
import com.webank.wedatasphere.dss.workflow.dao.*;
import com.webank.wedatasphere.dss.workflow.dto.NodeContentDO;
import com.webank.wedatasphere.dss.workflow.dto.NodeContentUIDO;
import com.webank.wedatasphere.dss.workflow.dto.NodeMetaDO;
import com.webank.wedatasphere.dss.workflow.dto.NodeTypeDO;
import com.webank.wedatasphere.dss.workflow.entity.*;
import com.webank.wedatasphere.dss.workflow.entity.request.*;
import com.webank.wedatasphere.dss.workflow.entity.response.*;
import com.webank.wedatasphere.dss.workflow.entity.request.BatchEditFlowRequest;
import com.webank.wedatasphere.dss.workflow.entity.request.DataDevelopNodeRequest;
import com.webank.wedatasphere.dss.workflow.entity.request.DataViewNodeRequest;
import com.webank.wedatasphere.dss.workflow.entity.request.EditFlowRequest;
import com.webank.wedatasphere.dss.workflow.entity.response.DataDevelopNodeResponse;
import com.webank.wedatasphere.dss.workflow.entity.response.DataViewNodeResponse;
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
import java.time.format.DateTimeFormatter;
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

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static ContextService contextService = ContextServiceImpl.getInstance();

    private static final String nodeUITitleKey = "title";

    private static final String nodeUIViewIdKey = "viewId";

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
        if (checkExistSameSubflow(parentFlowID, dssFlow.getName())) {
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
    public void getRootFlowProxy(DSSFlow dssFlow){

        try {

            if(Boolean.FALSE.equals(dssFlow.getRootFlow())){
                Long rootFlowId = getRootFlowId(dssFlow.getId());
                if(rootFlowId  == null){
                    return;
                }
                DSSFlow rootFlow = getFlowByID(rootFlowId);
                Map<String,Object> json = bmlService.query(rootFlow.getName(), rootFlow.getResourceId(), rootFlow.getBmlVersion());

                List<Map<String, Object>> props = DSSCommonUtils.getFlowAttribute(json.get("string").toString(), "props");
                String proxyUser = "";
                if (CollectionUtils.isNotEmpty(props)) {
                    for (Map<String, Object> prop : props) {
                        if (prop.containsKey("user.to.proxy") && prop.get("user.to.proxy") != null) {
                            proxyUser = prop.get("user.to.proxy").toString();
                            break;
                        }
                    }
                }

                dssFlow.setDefaultProxyUser(proxyUser);
            }

        }catch (Exception e){
            logger.error("getRootFlowProxy dssFlow id is {}, error msg is {}", dssFlow.getId(), e.getMessage());
        }
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
                           LabelRouteVO labels) throws Exception {

        // 判断工作流中是否存在命名相同的节点
        if (checkIsExistSameFlow(jsonFlow)) {
            throw new DSSErrorException(80001, "It exists same flow.(存在相同的节点)");
        }

        // 判断工作流中是否有子工作流未被保存
        List<String> unSaveNodes = checkIsSave(flowID, jsonFlow);

        if (CollectionUtils.isNotEmpty(unSaveNodes)) {
            throw new DSSErrorException(80001, "工作流中存在子工作流未被保存，请先保存子工作流：" + unSaveNodes);
        }

        //判断该工作流对应编排是否已发布，若已发布则不允许修改
        Long rootFlowId = getRootFlowId(flowID);
        OrchestratorVo orchestratorVo = null;
        try {
            orchestratorVo = RpcAskUtils.processAskException(getOrchestratorSender().ask(new RequestQuertByAppIdOrchestrator(rootFlowId)),
                    OrchestratorVo.class, RequestQuertByAppIdOrchestrator.class);
        } catch (Exception e) {
            logger.error("保存工作流时获取编排失败，原因为：", e);
        }

        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorVo != null ? orchestratorVo.getDssOrchestratorVersion() : null;

        DSSFlow dssFlow = flowMapper.selectFlowByID(flowID);
        String creator = dssFlow.getCreator();
        if (StringUtils.isNotEmpty(creator)) {
            userName = creator;
        }
        String flowJsonOld = getFlowJson(userName, projectName, dssFlow);

        // 解析并保存元数据
        Long orchestratorId = dssOrchestratorVersion != null ? dssOrchestratorVersion.getOrchestratorId() : null;
        try {
            // 当前保存的工作流为最新版本工作流时，再执行保存元数据，否则会有重复数据
            if (orchestratorId != null && dssOrchestratorVersion.getAppId().equals(rootFlowId)) {
                saveFlowMetaData(flowID, jsonFlow, orchestratorId);
                // 更新版本更新时间
                RpcAskUtils.processAskException(getOrchestratorSender().ask(new RequestOrchestratorVersionUpdateTime(orchestratorId)),
                        Boolean.class, RequestOrchestratorVersionUpdateTime.class);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        if (StringUtils.isNotEmpty(flowJsonOld) && isEqualTwoJson(flowJsonOld, jsonFlow)) {
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
        saveFlowHook.beforeSave(jsonFlow, dssFlow, parentFlowID);
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
            throw new DSSRuntimeException(e.getErrCode(), "保存ContextId失败，您可以尝试重新发布工作流！原因：" + ExceptionUtils.getRootCauseMessage(e), e);
        }
        saveFlowHook.afterSave(jsonFlow, dssFlow, parentFlowID);
        String version = bmlReturnMap.get("version").toString();
        // 对子工作流,需更新父工作流状态，以便提交
        Long updateFlowId = parentFlowID == null ? dssFlow.getId() : parentFlowID;
        updateTOSaveStatus(dssFlow.getProjectId(), updateFlowId, orchestratorId);

        return version;
    }

    @Override
    public void updateTOSaveStatus(Long projectId, Long flowID, Long orchestratorId) throws Exception {
        try {
            DSSProject projectInfo = DSSFlowEditLockManager.getProjectInfo(projectId);
            //仅对接入Git的项目 更新状态为 保存
            if (projectInfo.getAssociateGit() != null && projectInfo.getAssociateGit()) {
                Long rootFlowId = getRootFlowId(flowID);
                if (rootFlowId != null) {
                    // 工作流变更时，清空BML
                    BmlResource bmlResource = new BmlResource();
                    bmlResource.setResourceId(null);
                    bmlResource.setVersion(null);
                    RpcAskUtils.processAskException(getOrchestratorSender().ask(new RequestUpdateOrchestratorBML(rootFlowId, bmlResource)),
                            OrchestratorVo.class, RequestUpdateOrchestratorBML.class);
                    lockMapper.updateOrchestratorStatus(orchestratorId, OrchestratorRefConstant.FLOW_STATUS_SAVE);
                }
            }
        } catch (DSSErrorException e) {
            logger.error("getProjectInfo failed by:", e);
            throw new DSSRuntimeException(e.getErrCode(), "更新工作流状态失败，您可以尝试重新保存工作流！原因：" + ExceptionUtils.getRootCauseMessage(e), e);
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
    public void deleteFlowMetaData(Long orchestratorId) {
        List<NodeContentDO> nodeContentListByOrchestratorId = nodeContentMapper.getNodeContentListByOrchestratorId(orchestratorId);
        nodeContentMapper.deleteNodeContentByOrchestratorId(orchestratorId);
        if (CollectionUtils.isNotEmpty(nodeContentListByOrchestratorId)) {
            List<Long> collect = nodeContentListByOrchestratorId.stream().map(NodeContentDO::getId).collect(Collectors.toList());
            nodeContentUIMapper.deleteNodeContentUIByContentList(collect);
        }
    }

    @Override
    public void saveAllFlowMetaData(Long flowId, Long orchestratorId) {
        DSSFlow flowWithJsonAndSubFlowsByID = getFlowWithJsonAndSubFlowsByID(flowId);
        saveAllMetaData(flowWithJsonAndSubFlowsByID, orchestratorId);
    }

    private void saveAllMetaData(DSSFlow dssFlow, Long orchestratorId) {
        if (dssFlow == null) return;
        List<? extends DSSFlow> subFlows = dssFlow.getChildren();
        saveFlowMetaData(dssFlow.getId(), dssFlow.getFlowJson(), orchestratorId);
        if (subFlows != null) {
            // 递归遍历子节点保存
            for (DSSFlow subFlow : subFlows) {
                saveAllMetaData(subFlow, orchestratorId);
            }
        }
    }

    @Override
    public void saveFlowMetaData(Long flowID, String jsonFlow, Long orchestratorId) {
        // 解析 jsonflow
        // 解析 proxyUser
        try {
            List<Map<String, Object>> props = DSSCommonUtils.getFlowAttribute(jsonFlow, "props");
            String proxyUser = null;
            StringBuilder globalVar = new StringBuilder();
            if (CollectionUtils.isNotEmpty(props)) {
                for (Map<String, Object> prop : props) {
                    if (prop.containsKey("user.to.proxy") && prop.get("user.to.proxy") != null) {
                        proxyUser = prop.get("user.to.proxy").toString();
                    } else {
                        for (Map.Entry<String, Object> map : prop.entrySet()) {
                            if (map.getValue() != null) {
                                globalVar.append(map.getKey() + "=" + map.getValue());
                                globalVar.append(";");
                            }
                        }
                    }
                }
            }
            // 解析 resources
            List<Map<String, Object>> resources = DSSCommonUtils.getFlowAttribute(jsonFlow, "resources");
            StringBuilder resourceToString = new StringBuilder();
            if (CollectionUtils.isNotEmpty(resources)) {
                for (Map<String, Object> resource : resources) {
                    if (resource.containsKey("fileName")) {
                        resourceToString.append(resource.get("fileName"));
                        resourceToString.append(";");
                    }
                }
            }

            List<DSSNodeDefault> workFlowNodes = DSSCommonUtils.getWorkFlowNodes(jsonFlow);

            if (CollectionUtils.isEmpty(workFlowNodes)) {
                return;
            }

            NodeMetaDO nodeMetaByOrchestratorId = nodeMetaMapper.getNodeMetaByOrchestratorId(orchestratorId);

            if (nodeMetaByOrchestratorId == null) {
                nodeMetaMapper.insertNodeMeta(new NodeMetaDO(orchestratorId, proxyUser, resourceToString.toString(), globalVar.toString()));
            } else {
                nodeMetaMapper.updateNodeMeta(new NodeMetaDO(nodeMetaByOrchestratorId.getId(), orchestratorId, proxyUser, resourceToString.toString(), globalVar.toString()));
            }

            List<NodeContentDO> contentDOS = nodeContentMapper.getContentListByOrchestratorIdAndFlowId(orchestratorId, flowID);

            logger.info("workFlowNodes:{}", workFlowNodes);
            Set<NodeContentUIDO> nodeContentUIDOS = new HashSet<>();
            List<NodeContentDO> nodeContentDOS = new ArrayList<>();
            List<String> keyList = new ArrayList<>();
            Map<String, DSSNodeDefault> map = new HashMap<>();
            for (DSSNodeDefault nodeDefault : workFlowNodes) {
                String key = nodeDefault.getKey();
                if (StringUtils.isNotEmpty(key)) {
                    keyList.add(key);
                    map.put(key, nodeDefault);
                }
                String id = nodeDefault.getId();
                String jobType = nodeDefault.getJobType();
                Long createTime = nodeDefault.getCreateTime();
                Long modifyTime = nodeDefault.getModifyTime();
                Date createDate = null;
                Date modifyDate = null;
                if (createTime != null && modifyTime != null) {
                    createDate = new Date(createTime);
                }
                if (modifyTime != null) {
                    modifyDate = new Date(modifyTime);
                }
                String modifyUser = nodeDefault.getModifyUser();
                NodeContentDO contentDO = new NodeContentDO(key, id, jobType, orchestratorId, flowID, createDate, modifyDate, modifyUser);
                nodeContentDOS.add(contentDO);
            }

            if (CollectionUtils.isEmpty(nodeContentDOS)) {

                return;
            }

            // 获取相同的部分（交集）
            Set<NodeContentDO> intersection = new HashSet<>(contentDOS);
            intersection.retainAll(nodeContentDOS);

            // 获取删除的部分（差集）
            Set<NodeContentDO> difference1 = new HashSet<>(contentDOS);
            difference1.removeAll(nodeContentDOS);

            // 获取新增的部分（差集）
            Set<NodeContentDO> difference2 = new HashSet<>(nodeContentDOS);
            difference2.removeAll(contentDOS);

            if (CollectionUtils.isNotEmpty(difference2)) {
                nodeContentMapper.batchInsert(new ArrayList<>(difference2));
            }

            if (CollectionUtils.isNotEmpty(intersection)) {
                for (NodeContentDO nodeContentDO : intersection) {

                    nodeContentMapper.updateByKey(nodeContentDO);
                }
            }

            if (CollectionUtils.isNotEmpty(difference1)) {
                nodeContentMapper.batchDelete(new ArrayList<>(difference1), orchestratorId, flowID);
            }

            List<NodeContentDO> nodeContents = nodeContentMapper.getNodeContentByKeyList(keyList, orchestratorId, flowID);
            List<NodeTypeDO> workflowNodeNumberType = nodeInfoMapper.getWorkflowNodeNumberType();
            Map<String, String> nodeNumberMap = new HashMap<>();
            for (NodeTypeDO nodeTypeDO : workflowNodeNumberType) {
                nodeNumberMap.put(nodeTypeDO.getNodeType(), nodeTypeDO.getKey());
            }
            for (NodeContentDO nodeContentDO : nodeContents) {
                String nodeKey = nodeContentDO.getNodeKey();
                Long contentByKeyId = nodeContentDO.getId();
                DSSNodeDefault nodeDefault = map.get(nodeKey);
                String jobType = nodeDefault.getJobType();
                Map<String, Object> jobContent = nodeDefault.getJobContent();
                if (jobContent != null) {
                    for (Map.Entry<String, Object> entry : jobContent.entrySet()) {
                        String value = entry.getValue() != null ? entry.getValue().toString() : "";
                        nodeContentUIDOS.add(new NodeContentUIDO(contentByKeyId, entry.getKey(), value, jobType, "String"));
                    }
                }

                String title = nodeDefault.getTitle();
                if (StringUtils.isNotEmpty(title)) {
                    nodeContentUIDOS.add(new NodeContentUIDO(contentByKeyId, "title", title, jobType, "String"));
                }
                String desc = nodeDefault.getDesc();
                if (StringUtils.isNotEmpty(desc)) {
                    nodeContentUIDOS.add(new NodeContentUIDO(contentByKeyId, "desc", desc, jobType, "String"));
                }
                String businessTag = nodeDefault.getBusinessTag();
                if (StringUtils.isNotEmpty(businessTag)) {
                    nodeContentUIDOS.add(new NodeContentUIDO(contentByKeyId, "businessTag", businessTag, jobType, "String"));
                }
                String appTag = nodeDefault.getAppTag();
                if (StringUtils.isNotEmpty(appTag)) {
                    nodeContentUIDOS.add(new NodeContentUIDO(contentByKeyId, "appTag", appTag, jobType, "String"));
                }
                String ecConfTemplateName = nodeDefault.getEcConfTemplateName();
                if (StringUtils.isNotEmpty(ecConfTemplateName)) {
                    nodeContentUIDOS.add(new NodeContentUIDO(contentByKeyId, "ecConfTemplateName", ecConfTemplateName, jobType, "String"));
                }
                String ecConfTemplateId = nodeDefault.getEcConfTemplateId();
                if (StringUtils.isNotEmpty(ecConfTemplateId)) {
                    nodeContentUIDOS.add(new NodeContentUIDO(contentByKeyId, "ecConfTemplateId", ecConfTemplateId, jobType, "String"));
                }
                List<Resource> nodeDefaultResources = nodeDefault.getResources();
                if (CollectionUtils.isNotEmpty(nodeDefaultResources)) {
                    StringBuilder nodeResources = new StringBuilder();
                    for (Resource resource : nodeDefaultResources) {
                        nodeResources.append(resource.getFileName());
                        nodeResources.append(";");
                    }
                    nodeContentUIDOS.add(new NodeContentUIDO(contentByKeyId, "resources", new String(nodeResources), jobType, "String"));
                }


                Map<String, Object> params = nodeDefault.getParams();
                if (params != null) {
                    Map<String, Map<String, Object>> mapParams = (Map<String, Map<String, Object>>) params.get("configuration");
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
                                    String nodeContentType = null;
                                    if (nodeNumberMap.containsKey(jobType) && nodeNumberMap.get(jobType).equals(paramName)) {
                                        nodeContentType = "NumInterval";
                                    } else if (paramName.endsWith("memory")) {
                                        nodeContentType = "Memory";
                                    } else {
                                        nodeContentType = "String";
                                    }
                                    nodeContentUIDOS.add(new NodeContentUIDO(contentByKeyId, paramName, paramVal, jobType, nodeContentType));
                                }

                            }
                        }
                        logger.info(mapParams.toString());
                    }
                }
            }
            List<Long> contentIdListByOrchestratorId = contentDOS.stream().map(NodeContentDO::getId).collect(Collectors.toList());
            // 先删后增
            if (CollectionUtils.isNotEmpty(contentIdListByOrchestratorId)) {
                nodeContentUIMapper.deleteNodeContentUIByContentList(contentIdListByOrchestratorId);
            }
            if (CollectionUtils.isNotEmpty(nodeContentUIDOS)) {
                nodeContentUIMapper.batchInsertNodeContentUI(new ArrayList<>(nodeContentUIDOS));
            }
        } catch (Exception e) {
            logger.error("saveFlowMeta error, the reason is: ", e);
        }
    }


    private void deleteFlowMetaDataByFlowId(Long flowID) {
        Long rootFlowId = getRootFlowId(flowID);
        OrchestratorVo orchestratorVo = null;
        try {
            orchestratorVo = RpcAskUtils.processAskException(getOrchestratorSender().ask(new RequestQuertByAppIdOrchestrator(rootFlowId)),
                    OrchestratorVo.class, RequestQuertByAppIdOrchestrator.class);
        } catch (Exception e) {
            logger.error("保存工作流时获取编排失败，原因为：", e);
        }
        DSSOrchestratorVersion dssOrchestratorVersion = orchestratorVo != null ? orchestratorVo.getDssOrchestratorVersion() : null;
        // 解析并保存元数据
        Long orchestratorId = dssOrchestratorVersion != null ? dssOrchestratorVersion.getOrchestratorId() : null;
        if (orchestratorId != null && dssOrchestratorVersion.getAppId().equals(rootFlowId)) {
            List<NodeContentDO> contentDOList = nodeContentMapper.getContentListByOrchestratorIdAndFlowId(orchestratorId, Long.valueOf(flowID));
            if (!CollectionUtils.isEmpty(contentDOList)) {
                List<Long> contentIdList = contentDOList.stream().map(NodeContentDO::getId).collect(Collectors.toList());
                nodeContentMapper.deleteNodeContentByOrchestratorIdAndFlowId(orchestratorId, Long.valueOf(flowID));
                if (CollectionUtils.isNotEmpty(contentIdList)) {
                    nodeContentUIMapper.deleteNodeContentUIByContentList(contentIdList);
                }
                nodeMetaMapper.deleteNodeMetaByOrchestratorId(orchestratorId);
            }
        }
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

    @Override
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
        retList.add(new ExtraToolBarsVO("前往调度中心", GOTO_SCHEDULER_CENTER_URL.getValue() + "?workspaceId=" + workspaceId, "icon:null"));
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
        deleteFlowMetaDataByFlowId(flowId);
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
        Sender orcSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender(dssLabels);
        OrchestratorVo orchestratorVo = RpcAskUtils.processAskException(orcSender.ask(new RequestQuertByAppIdOrchestrator(dssFlow.getId())),
                OrchestratorVo.class, RequestQueryByIdOrchestrator.class);
        Long orchestratorId = orchestratorVo.getDssOrchestratorInfo().getId();
        deleteFlowMetaData(orchestratorId);
        DSSFlow rootFlowWithSubFlows = copyFlowAndSetSubFlowInDB(dssFlow, userName, description, nodeSuffix, newFlowName, newProjectId);
        updateFlowJson(userName, projectName, rootFlowWithSubFlows, version, null,
                contextIdStr, workspace, dssLabels, nodeSuffix, orchestratorId);
        DSSFlow copyFlow = flowMapper.selectFlowByID(rootFlowWithSubFlows.getId());
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

    @Override
    public void getAllOldFlowId(Long flowId, List<Long> flowIdList) {
        DSSFlow dssFlow = getFlow(flowId);
        List<Long> subFlowIDs = flowMapper.selectSubFlowIDByParentFlowID(flowId);
        flowIdList.add(flowId);
        for (Long subFlowID : subFlowIDs) {
            DSSFlow subDSSFlow = flowMapper.selectFlowByID(subFlowID);
            if (dssFlow.getChildren() == null) {
                dssFlow.setChildren(new ArrayList<DSSFlow>());
            }
            getAllOldFlowId(subFlowID, flowIdList);
        }
    }

    @Override
    public void deleteNodeContent(List<Long> flowIdList) {
        if (CollectionUtils.isEmpty(flowIdList)) {
            return;
        }
        List<NodeContentDO> contentDOS = nodeContentMapper.getContentListByFlowId(flowIdList);
        List<Long> contentIdListByOrchestratorId = contentDOS.stream().map(NodeContentDO::getId).collect(Collectors.toList());
        // 删除当前节点的属性信息
        if (CollectionUtils.isNotEmpty(contentIdListByOrchestratorId)) {
            nodeContentUIMapper.deleteNodeContentUIByContentList(contentIdListByOrchestratorId);
        }
        nodeContentMapper.deleteNodeContentByFlowId(flowIdList);
    }

    private void updateFlowJson(String userName, String projectName, DSSFlow rootFlow,
                                String version, Long parentFlowId, String contextIdStr,
                                Workspace workspace, List<DSSLabel> dssLabels, String nodeSuffix,
                                Long orchestratorId) throws DSSErrorException, IOException {
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
        // 更新对应节点的FlowJson
        saveFlowMetaData(rootFlow.getId(), updateFlowJson, orchestratorId);
        List<? extends DSSFlow> subFlows = rootFlow.getChildren();
        List<String[]> templateIds = new ArrayList<>();
        if (subFlows != null) {
            for (DSSFlow subflow : subFlows) {
                updateFlowJson(userName, projectName, subflow, version, rootFlow.getId(),
                        contextIdStr, workspace, dssLabels, nodeSuffix, orchestratorId);
                templateIds.addAll(subflow.getFlowIdParamConfTemplateIdTuples());
            }
        }

        DSSFlow updateDssFlow = uploadFlowJsonToBml(userName, projectName, rootFlow, updateFlowJson);
        List<String> tempIds = workFlowParser.getParamConfTemplate(updateFlowJson);
        List<String[]> templateIdsInRoot = tempIds.stream()
                .map(e -> new String[]{updateDssFlow.getId().toString(), e})
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
            } else if (nodeInfo == null) {
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


    @Override
    public DataDevelopNodeResponse queryDataDevelopNodeList(String username, Workspace workspace, DataDevelopNodeRequest request) {

        DataDevelopNodeResponse dataDevelopNodeResponse = new DataDevelopNodeResponse();

        // 获取项目
        List<DSSProject> dssProjectList = getDSSProject(workspace, username);

        if (CollectionUtils.isEmpty(dssProjectList)) {
            logger.error("queryDataDevelopNodeList find project is empty, workspaceId is {}, username is {}", workspace.getWorkspaceId(), username);
            return dataDevelopNodeResponse;
        }

        Map<Long, DSSProject> dssProjectMap = projectListToMap(dssProjectList);

        List<Long> projectIdList = new ArrayList<>(dssProjectMap.keySet());

        List<NodeInfo> nodeInfoList = getNodeInfoByGroupName(WorkflowNodeGroupEnum.DataDevelopment.getNameEn());

        if (CollectionUtils.isEmpty(nodeInfoList)) {
            logger.error("queryDataDevelopNodeList not find node type info");
            return dataDevelopNodeResponse;
        }

        List<String> nodeTypeList = nodeInfoList.stream().map(NodeInfo::getNodeType).collect(Collectors.toList());
        // 查询节点信息
        List<DSSFlowNodeInfo> flowNodeInfoList = nodeContentMapper.queryFlowNodeInfo(projectIdList, nodeTypeList);

//        Map<String, List<NodeUIInfo>> nodeInfoGroup = nodeUIInfoGroupByNodeType(nodeTypeList);

        if (CollectionUtils.isEmpty(flowNodeInfoList)) {
            logger.error("queryDataDevelopNodeList find node info is empty, example project id is {}", projectIdList.get(0));
            return dataDevelopNodeResponse;
        }

        Map<Long, DSSFlowNodeInfo> dssFlowNodeInfoMap = flowNodeToMap(flowNodeInfoList);
        List<Long> contentIdList = new ArrayList<>(dssFlowNodeInfoMap.keySet());

        // 查询节点保存的配置信息
        Map<Long, List<NodeContentUIDO>> nodeContentUIGroup = getNodeContentUIGroup(contentIdList);
        if (nodeContentUIGroup.isEmpty()) {
            logger.error("queryDataDevelopNodeList not find nodeUI info , example contextId is {}", contentIdList.get(0));
            return dataDevelopNodeResponse;
        }



        // 查询模板信息
        //  List<Long> orchestratorIdList = flowNodeInfoList.stream().map(DSSFlowNodeInfo::getOrchestratorId).collect(Collectors.toList());
        //  Map<String, String> templateMap = getTemplateMap(orchestratorIdList);

        List<DataDevelopNodeInfo> dataDevelopNodeInfoList = new ArrayList<>();

        for (Long contentId : nodeContentUIGroup.keySet()) {

            try {
                DataDevelopNodeInfo dataDevelopNodeInfo = new DataDevelopNodeInfo();
                DSSFlowNodeInfo dssFlowNodeInfo = dssFlowNodeInfoMap.get(contentId);
                List<NodeContentUIDO> nodeUIList = nodeContentUIGroup.get(contentId);
                DSSProject dssProject = dssProjectMap.get(dssFlowNodeInfo.getProjectId());

//                Map<String, String> nodeDefaultValue = getNodeDefaultValue(nodeInfoGroup, dssFlowNodeInfo.getJobType());

                List<String> nodeUIKeys = nodeUIList.stream().map(NodeContentUIDO::getNodeUIKey).collect(Collectors.toList());
                List<String> nodeUIValues = nodeUIList.stream().map(NodeContentUIDO::getNodeUIValue).collect(Collectors.toList());

                Map<String, String> nodeMap = CollUtil.zip(nodeUIKeys, nodeUIValues);
                NodeBaseInfo nodeBaseInfo = getNodeBaseInfo(dssFlowNodeInfo, dssProject, nodeMap,username);
                BeanUtils.copyProperties(nodeBaseInfo, dataDevelopNodeInfo);

                dataDevelopNodeInfo.setResource(nodeMap.get("resources"));

                if (nodeMap.containsKey("ec.conf.templateId") && StringUtils.isNotEmpty(nodeMap.get("ec.conf.templateId"))) {
                    dataDevelopNodeInfo.setRefTemplate(Boolean.TRUE);
                    String templateId = nodeMap.get("ec.conf.templateId");
                    dataDevelopNodeInfo.setTemplateId(templateId);
                    if (nodeMap.containsKey("ecConfTemplateName")) {
                        dataDevelopNodeInfo.setTemplateName(nodeMap.get("ecConfTemplateName"));
                    } else if (nodeMap.containsKey("ec.conf.templateName")) {
                        dataDevelopNodeInfo.setTemplateName(nodeMap.get("ec.conf.templateName"));
                    } else {
                        logger.error("queryDataDevelopNodeList not find template,contentId is {},template id is {}", contentId, templateId);
                    }

                } else {
                    dataDevelopNodeInfo.setRefTemplate(Boolean.FALSE);
                }

                if (nodeMap.containsKey("ReuseEngine") && StringUtils.isNotEmpty(nodeMap.get("ReuseEngine"))) {
                    dataDevelopNodeInfo.setReuseEngine(Boolean.valueOf(nodeMap.get("ReuseEngine")));
                }

                if(nodeMap.containsKey("script") && StringUtils.isNotEmpty(nodeMap.get("script"))){
                    dataDevelopNodeInfo.setScript(nodeMap.get("script"));
                }

                dataDevelopNodeInfoList.add(dataDevelopNodeInfo);
            } catch (Exception exception) {
                logger.error("queryDataDevelopNodeList error content id is {}", contentId);
                logger.error(exception.getMessage());
            }
        }

        // 条件筛选、排序
        dataDevelopNodeInfoList = dataDevelopNodeResultFilter(request, dataDevelopNodeInfoList);

        dataDevelopNodeResponse.setTotal((long) dataDevelopNodeInfoList.size());
        // 分页处理
        int page = request.getPageNow() >= 1 ? request.getPageNow() : 1;
        int pageSize = request.getPageSize() >= 1 ? request.getPageSize() : 10;
        int start = (page - 1) * pageSize;
        int end = Math.min(page * pageSize, dataDevelopNodeInfoList.size());

        for (int i = start; i < end; i++) {
            DataDevelopNodeInfo dataDevelopNodeInfo = dataDevelopNodeInfoList.get(i);
            dataDevelopNodeResponse.getDataDevelopNodeInfoList().add(dataDevelopNodeInfo);
        }

        return dataDevelopNodeResponse;

    }


    @Override
    public Map<String, Object> getDataDevelopNodeContent(String nodeId, Long contentId) throws DSSErrorException {
        NodeContentDO nodeContentDO = nodeContentMapper.getNodeContentById(contentId, nodeId);
        Map<String, Object> content = new HashMap<>();
        if (nodeContentDO == null) {
            logger.error("not find node info , id is {}, nodeId is {}", contentId, nodeId);
            throw new DSSRuntimeException(90004, "未找到" + nodeId + "节点信息，请查看节点是否被删除");
        }

        List<NodeContentUIDO> nodeContentUIDOList = nodeContentUIMapper.getNodeContentUIByContentId(contentId);

        if (CollectionUtils.isEmpty(nodeContentUIDOList)) {
            logger.error("not find node params info , content id is {}", contentId);
            throw new DSSRuntimeException(90004, "未找到" + nodeId + "节点信息，请查看节点是否被删除");
        }

        List<NodeUIInfo> nodeUIInfoList = nodeInfoMapper.getNodeUIInfoByNodeType(nodeContentDO.getJobType());

        if (CollectionUtils.isEmpty(nodeUIInfoList)) {
            logger.error("node type not params content, node type is {}, content id is {}", nodeContentDO.getJobType(), contentId);
            return content;
        }


        List<String> nodeKey = nodeContentUIDOList.stream().map(NodeContentUIDO::getNodeUIKey).collect(Collectors.toList());
        List<String> nodeValue = nodeContentUIDOList.stream().map(NodeContentUIDO::getNodeUIValue).collect(Collectors.toList());

        Map<String, String> nodeMap = CollUtil.zip(nodeKey, nodeValue);

        for (NodeUIInfo nodeUIInfo : nodeUIInfoList) {

            content.put(nodeUIInfo.getKey(), nodeMap.getOrDefault(nodeUIInfo.getKey(), ""));

        }

        return content;

    }


    @Override
    public DataViewNodeResponse queryDataViewNode(String username, Workspace workspace, DataViewNodeRequest request) {

        DataViewNodeResponse dataViewNodeResponse = new DataViewNodeResponse();

        List<DSSProject> dssProjectList = getDSSProject(workspace, username);

        if (CollectionUtils.isEmpty(dssProjectList)) {
            logger.error("queryDataViewNode workspace is {}, username is {}", workspace, username);
            return dataViewNodeResponse;
        }

        Map<Long, DSSProject> dssProjectMap = projectListToMap(dssProjectList);

        List<Long> projectIdList = new ArrayList<>(dssProjectMap.keySet());
        // 查询节点信息
        List<NodeInfo> nodeInfoList = getNodeInfoByGroupName(WorkflowNodeGroupEnum.DataVisualization.getNameEn());
        if (CollectionUtils.isEmpty(nodeInfoList)) {
            logger.error("queryDataViewNode not find node type info");
            return dataViewNodeResponse;
        }
        List<String> nodeTypeList = nodeInfoList.stream().map(NodeInfo::getNodeType).collect(Collectors.toList());
        List<DSSFlowNodeInfo> flowNodeInfoList = nodeContentMapper.queryFlowNodeInfo(projectIdList, nodeTypeList);

        if (CollectionUtils.isEmpty(flowNodeInfoList)) {
            logger.error("queryDataViewNode not find node info , example projectId is {}", projectIdList.get(0));
            return dataViewNodeResponse;
        }

        Map<Long, DSSFlowNodeInfo> dssFlowNodeInfoMap = flowNodeToMap(flowNodeInfoList);
        List<Long> contentIdList = new ArrayList<>(dssFlowNodeInfoMap.keySet());

        // 查询节点保存的配置信息
        Map<Long, List<NodeContentUIDO>> nodeContentUIGroup = getNodeContentUIGroup(contentIdList);
        if (nodeContentUIGroup.isEmpty()) {
            logger.error("queryDataViewNode not find nodeUI info , example contextId is {}", contentIdList.get(0));
            return dataViewNodeResponse;
        }

        List<DataViewNodeInfo> dataViewNodeInfoList = new ArrayList<>();

        for (Long contentId : nodeContentUIGroup.keySet()) {

            DataViewNodeInfo dataViewNodeInfo = new DataViewNodeInfo();
            DSSFlowNodeInfo dssFlowNodeInfo = dssFlowNodeInfoMap.get(contentId);
            List<NodeContentUIDO> nodeUIList = nodeContentUIGroup.get(contentId);
            DSSProject dssProject = dssProjectMap.get(dssFlowNodeInfo.getProjectId());

            List<String> nodeUIKeys = nodeUIList.stream().map(NodeContentUIDO::getNodeUIKey).collect(Collectors.toList());
            List<String> nodeUIValues = nodeUIList.stream().map(NodeContentUIDO::getNodeUIValue).collect(Collectors.toList());
            Map<String, String> nodeMap = CollUtil.zip(nodeUIKeys, nodeUIValues);

            NodeBaseInfo nodeBaseInfo = getNodeBaseInfo(dssFlowNodeInfo, dssProject, nodeMap,username);
            BeanUtils.copyProperties(nodeBaseInfo, dataViewNodeInfo);

            dataViewNodeInfo.setNodeDesc(nodeMap.get("desc"));
            if(nodeMap.containsKey("viewId")){
                dataViewNodeInfo.setViewId(nodeMap.get("viewId"));
            } else if (nodeMap.containsKey("datasourceId")) {
                dataViewNodeInfo.setViewId(nodeMap.get("datasourceId"));
            }

            dataViewNodeInfoList.add(dataViewNodeInfo);
        }

        dataViewNodeInfoList = dataViewNodeResultFilter(request, dataViewNodeInfoList);

        dataViewNodeResponse.setTotal((long) dataViewNodeInfoList.size());
        // 分页处理
        int page = request.getPageNow() >= 1 ? request.getPageNow() : 1;
        int pageSize = request.getPageSize() >= 1 ? request.getPageSize() : 10;
        int start = (page - 1) * pageSize;
        int end = Math.min(page * pageSize, dataViewNodeInfoList.size());

        for (int i = start; i < end; i++) {
            DataViewNodeInfo dataViewNodeInfo = dataViewNodeInfoList.get(i);
            dataViewNodeResponse.getDataDevelopNodeInfoList().add(dataViewNodeInfo);
        }

        return dataViewNodeResponse;

    }


    public List<DSSProject> getDSSProject(Workspace workspace, String username) {

        ProjectListQueryResponse response = RpcAskUtils.processAskException(DSSSenderServiceFactory.getOrCreateServiceInstance()
                        .getProjectServerSender().ask(new ProjectListQueryRequest(workspace.getWorkspaceId(), username)),
                ProjectListQueryResponse.class, ProjectListQueryRequest.class);


        if (CollectionUtils.isEmpty(response.getProjectList())) {
            response.setProjectList(new ArrayList<>());
        }

        return response.getProjectList().stream().filter(project -> {
            // 过滤具有编辑工作流权限的项目
            return (CollectionUtils.isNotEmpty(project.getEditUsers()) && project.getEditUsers().contains(username))
                    || (CollectionUtils.isNotEmpty(project.getReleaseUsers()) && project.getReleaseUsers().contains(username))
                    || (StringUtils.isNotEmpty(project.getCreateBy()) && project.getCreateBy().equals(username))
                    || (CollectionUtils.isNotEmpty(project.getAccessUsers()) && project.getAccessUsers().contains(username))
                    || project.isAdmin();
        }).collect(Collectors.toList());

    }


    public Map<Long, DSSProject> projectListToMap(List<DSSProject> dssProjectList) {

        Map<Long, DSSProject> dssProjectMap = new HashMap<>();

        for (DSSProject project : dssProjectList) {

            dssProjectMap.put(project.getId(), project);
        }

        return dssProjectMap;
    }


    public Map<String, String> getTemplateMap(List<Long> orchestratorIdList) {

        // 查询模板信息
        List<DSSFlowNodeTemplate> dssFlowNodeTemplateList = nodeContentMapper.queryFlowNodeTemplate(orchestratorIdList);

        Map<String, String> templateMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(dssFlowNodeTemplateList)) {
            List<String> templateIdList = dssFlowNodeTemplateList.stream().map(DSSFlowNodeTemplate::getTemplateId).collect(Collectors.toList());
            List<String> templateNameList = dssFlowNodeTemplateList.stream().map(DSSFlowNodeTemplate::getTemplateName).collect(Collectors.toList());
            templateMap = CollUtil.zip(templateIdList, templateNameList);
        }

        return templateMap;
    }


    public Map<Long, DSSFlowNodeInfo> flowNodeToMap(List<DSSFlowNodeInfo> flowNodeInfoList) {
        Map<Long, DSSFlowNodeInfo> dssFlowNodeInfoMap = new HashMap<>();

        for (DSSFlowNodeInfo dssFlowNodeInfo : flowNodeInfoList) {
            dssFlowNodeInfoMap.put(dssFlowNodeInfo.getContentId(), dssFlowNodeInfo);
        }

        return dssFlowNodeInfoMap;
    }


    public Map<Long, List<NodeContentUIDO>> getNodeContentUIGroup(List<Long> contentIdList) {
        Map<Long, List<NodeContentUIDO>> nodeContentUIGroup = new HashMap<>();
        List<NodeContentUIDO> nodeContentUIDOList = nodeContentUIMapper.queryNodeContentUIList(contentIdList);

        if (CollectionUtils.isEmpty(nodeContentUIDOList)) {
            return nodeContentUIGroup;
        }

        nodeContentUIGroup = nodeContentUIDOList.stream()
                .collect(Collectors.groupingBy(NodeContentUIDO::getContentId));
        return nodeContentUIGroup;
    }


    public List<DataDevelopNodeInfo> dataDevelopNodeResultFilter(DataDevelopNodeRequest request, List<DataDevelopNodeInfo> dataDevelopNodeInfoList) {

        dataDevelopNodeInfoList = dataDevelopNodeInfoList.stream().filter(dataDevelopNodeInfo -> {
            boolean flag = true;

            if (request.getRefTemplate() != null) {
                // 是否引用模板
                flag = request.getRefTemplate().equals(dataDevelopNodeInfo.getRefTemplate());
            }

            if (request.getReuseEngine() != null && flag) {
                // 是否引用引擎
                flag = request.getReuseEngine().equals(dataDevelopNodeInfo.getReuseEngine());
            }

            if (!CollectionUtils.isEmpty(request.getNodeTypeNameList()) && flag) {
                // 节点小类
                flag = request.getNodeTypeNameList().contains(dataDevelopNodeInfo.getNodeTypeName());
            }

            if (!CollectionUtils.isEmpty(request.getNodeNameList()) && flag) {
                // 节点名称
                flag = request.getNodeNameList().contains(dataDevelopNodeInfo.getNodeName());
            }

            if (!CollectionUtils.isEmpty(request.getTemplateNameList()) && flag) {
                // 模板名称
                flag = request.getTemplateNameList().contains(dataDevelopNodeInfo.getTemplateName());
            }


            // 工作流名称
            if (!StringUtils.isBlank(request.getOrchestratorName()) && flag) {
                flag = request.getOrchestratorName().equals(dataDevelopNodeInfo.getOrchestratorName());
            }

            // 项目名称
            if (!CollectionUtils.isEmpty(request.getProjectNameList()) && flag) {
                flag = request.getProjectNameList().contains(dataDevelopNodeInfo.getProjectName());
            }

            return flag;
        }).collect(Collectors.toList());

        // 创建时间倒序排列
        dataDevelopNodeInfoList = dataDevelopNodeInfoList.stream().sorted(Comparator.comparing(DataDevelopNodeInfo::getCreateTime,
                Comparator.nullsFirst(Comparator.naturalOrder())).reversed()).collect(Collectors.toList());

        return dataDevelopNodeInfoList;

    }


    public List<DataViewNodeInfo> dataViewNodeResultFilter(DataViewNodeRequest request, List<DataViewNodeInfo> dataViewNodeInfoList) {

        dataViewNodeInfoList = dataViewNodeInfoList.stream().filter(dataViewNodeInfo -> {
            boolean flag = true;
            // 工作流名称
            if (!StringUtils.isBlank(request.getOrchestratorName())) {
                flag = request.getOrchestratorName().equals(dataViewNodeInfo.getOrchestratorName());
            }

            // 项目名称
            if (!CollectionUtils.isEmpty(request.getProjectNameList()) && flag) {
                flag = request.getProjectNameList().contains(dataViewNodeInfo.getProjectName());
            }

            // 节点类型
            if (!CollectionUtils.isEmpty(request.getNodeTypeNameList()) && flag) {
                flag = request.getNodeTypeNameList().contains(dataViewNodeInfo.getNodeTypeName());
            }

            // 节点名称
            if (!CollectionUtils.isEmpty(request.getNodeNameList()) && flag) {
                flag = request.getNodeNameList().contains(dataViewNodeInfo.getNodeName());
            }

            if (!StringUtils.isEmpty(request.getViewId()) && flag) {
                flag = StringUtils.isNotEmpty(dataViewNodeInfo.getViewId())
                        && dataViewNodeInfo.getViewId().contains(request.getViewId());
            }

            return flag;

        }).collect(Collectors.toList());

        // 创建时间倒序排列
        dataViewNodeInfoList = dataViewNodeInfoList.stream().sorted(Comparator.comparing(DataViewNodeInfo::getCreateTime,
                Comparator.nullsFirst(Comparator.naturalOrder())).reversed()).collect(Collectors.toList());

        return dataViewNodeInfoList;


    }


    @Override
    public List<NodeInfo> getNodeInfoByGroupName(String groupNameEn) {

        return nodeInfoMapper.getNodeTypeByGroupName(groupNameEn, null);
    }


    @Override
    public DSSFlowName queryFlowNameList(String username, Workspace workspace, String groupNameEn, String nodeTypeName) {

        DSSFlowName dssFlowName = new DSSFlowName();

        // 获取项目
        List<DSSProject> dssProjectList = getDSSProject(workspace, username);

        if (CollectionUtils.isEmpty(dssProjectList)) {
            logger.error("queryFlowNameList find project is empty, workspaceId is {}, username is {}", workspace.getWorkspaceId(), username);
            return dssFlowName;
        }

        List<Long> projectIdList = dssProjectList.stream().map(DSSProject::getId).collect(Collectors.toList());

        List<NodeInfo> nodeInfoList = nodeInfoMapper.getNodeTypeByGroupName(groupNameEn, nodeTypeName);

        if (CollectionUtils.isEmpty(nodeInfoList)) {
            logger.error("queryFlowNameList groupNameEn is {},nodeTypeName is {}", groupNameEn, nodeTypeName);
            return dssFlowName;
        }

        List<String> nodeTypeList = nodeInfoList.stream().map(NodeInfo::getNodeType).collect(Collectors.toList());
        // 查询节点信息
        List<DSSFlowNodeInfo> flowNodeInfoList = nodeContentMapper.queryFlowNodeInfo(projectIdList, nodeTypeList);

        if (CollectionUtils.isEmpty(flowNodeInfoList)) {
            logger.error("queryFlowNameList find node info is empty, example project id is {}", projectIdList.get(0));
            return dssFlowName;
        }

        if (WorkflowNodeGroupEnum.DataDevelopment.getNameEn().equals(groupNameEn)) {

            List<Long> orchestratorIdList = flowNodeInfoList.stream().map(DSSFlowNodeInfo::getOrchestratorId).distinct().collect(Collectors.toList());
            List<DSSFlowNodeTemplate> dssFlowNodeTemplateList = nodeContentMapper.queryFlowNodeTemplate(orchestratorIdList);
            List<String> templateNameList = dssFlowNodeTemplateList.stream().map(DSSFlowNodeTemplate::getTemplateName).distinct().collect(Collectors.toList());
            dssFlowName.setTemplateNameList(templateNameList);
        }


        List<String> orchestratorNameList = flowNodeInfoList.stream().map(DSSFlowNodeInfo::getOrchestratorName).distinct().collect(Collectors.toList());
        dssFlowName.setOrchestratorNameList(orchestratorNameList);

        List<Long> contentIdList = flowNodeInfoList.stream().map(DSSFlowNodeInfo::getContentId).collect(Collectors.toList());
        List<NodeContentUIDO> nodeContentUIDOList = nodeContentUIMapper.getNodeContentUIByNodeUIKey(contentIdList, nodeUITitleKey);
        List<String> nodeNameList = nodeContentUIDOList.stream().map(NodeContentUIDO::getNodeUIValue).distinct().collect(Collectors.toList());
        dssFlowName.setNodeNameList(nodeNameList);

        return dssFlowName;

    }


    @Override
    public List<String> queryViewId(Workspace workspace, String username) {

        List<String> viewIdList = queryNodeUiValueByKey(workspace, username, nodeUIViewIdKey, WorkflowNodeGroupEnum.DataVisualization.getNameEn());

        List<String> dataSourceIdList = queryNodeUiValueByKey(workspace, username, "datasourceId", WorkflowNodeGroupEnum.DataVisualization.getNameEn());

        viewIdList.addAll(dataSourceIdList);

        return viewIdList.stream().distinct().collect(Collectors.toList());
    }

    @Override
    public void batchEditFlow(BatchEditFlowRequest batchEditFlowRequest, String ticketId, Workspace workspace, String userName) throws Exception {
        if (batchEditFlowRequest == null) {
            return;
        }
        List<EditFlowRequest> editFlowRequestList = batchEditFlowRequest.getEditNodeList();
        Map<Long, List<EditFlowRequest>> editFlowRequestMap = new HashMap<>();
        Set<Long> flowIdList = new HashSet<>();

        for (EditFlowRequest editFlowRequest : editFlowRequestList) {
            Long orchestratorId = editFlowRequest.getOrchestratorId();
            List<EditFlowRequest> editFlowRequests = editFlowRequestMap.containsKey(orchestratorId) ?
                    editFlowRequestMap.get(orchestratorId) : new ArrayList<>();
            editFlowRequests.add(editFlowRequest);
            editFlowRequestMap.put(orchestratorId, editFlowRequests);
        }

        Set<Long> orchestratorIdList = editFlowRequestList.stream().map(EditFlowRequest::getOrchestratorId).collect(Collectors.toSet());
        // 批量获取编辑锁
        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender();
        RequestQueryOrchestrator requestQueryOrchestrator = new RequestQueryOrchestrator(new ArrayList<>(orchestratorIdList));
        ResponseQueryOrchestrator responseQueryOrchestrator = RpcAskUtils.processAskException(sender.ask(requestQueryOrchestrator), ResponseQueryOrchestrator.class, RequestQueryOrchestrator.class);

        if (responseQueryOrchestrator == null) {
            throw new DSSErrorException(80001, "该工作流节点没有对应编排");
        }
        List<OrchestratorVo> orchestratorVoes = responseQueryOrchestrator.getOrchestratorVoes();

        Long modifyTime = System.currentTimeMillis();
        Map<Long, List<EditFlowRequest>> editFlowRequestTOFlowIDMap = new HashMap<>();

        for (OrchestratorVo orchestratorVo : orchestratorVoes) {
            DSSOrchestratorVersion dssOrchestratorVersion = orchestratorVo.getDssOrchestratorVersion();
            if (dssOrchestratorVersion != null) {
                Long orchestratorId = dssOrchestratorVersion.getOrchestratorId();
                Long flowId = dssOrchestratorVersion.getAppId();
                DSSFlow dssFlow = getFlow(flowId);
                DSSFlowEditLock flowEditLock = lockMapper.getFlowEditLockByID(flowId);
                if (flowEditLock != null && !flowEditLock.getOwner().equals(ticketId)) {
                    throw new DSSErrorException(80001, "当前工作流" + dssFlow.getName() +"被用户" + flowEditLock.getUsername() + "已锁定编辑，您编辑的内容不能再被保存。如有疑问，请与" + flowEditLock.getUsername() + "确认");
                }

                lockFlow(dssFlow, userName, ticketId);
                List<EditFlowRequest> editFlowRequests = editFlowRequestMap.get(orchestratorId);

                for (EditFlowRequest editFlowRequest : editFlowRequests) {
                    NodeContentDO nodeContentByContentId = nodeContentMapper.getNodeContentByContentId(editFlowRequest.getId());
                    Long targetFlowId = nodeContentByContentId.getFlowId();
                    List<EditFlowRequest> editFlowRequestsList = editFlowRequestTOFlowIDMap.containsKey(targetFlowId) ?
                            editFlowRequestTOFlowIDMap.get(targetFlowId) : new ArrayList<>();
                    editFlowRequestsList.add(editFlowRequest);
                    editFlowRequestTOFlowIDMap.put(targetFlowId, editFlowRequestsList);
                }
            }
        }

        for (Map.Entry<Long, List<EditFlowRequest>> entry : editFlowRequestTOFlowIDMap.entrySet()) {
            Long targetFlowId = entry.getKey();
            DSSFlow targetFlow = getFlow(targetFlowId);
            DSSProject project = getProjectByProjectId(targetFlow.getProjectId());
            StringBuilder modifyJson = new StringBuilder(targetFlow.getFlowJson());
            List<EditFlowRequest> value = entry.getValue();
            if (value != null) {
                for (EditFlowRequest editFlow : value) {
                    String json = modifyJson(String.valueOf(modifyJson), editFlow, modifyTime, userName);
                    modifyJson.setLength(0);
                    modifyJson.append(json);
                }
                // 批量修改属性
                String resultJson = modifyFlowJsonTime(String.valueOf(modifyJson), modifyTime, userName);
                saveFlow(targetFlowId, String.valueOf(resultJson)
                        , targetFlow.getDescription(), userName
                        , workspace.getWorkspaceName(), project.getName(), null);
            }
        }

        // 完成后批量解锁
        for (OrchestratorVo orchestratorVo : orchestratorVoes) {
            DSSOrchestratorVersion dssOrchestratorVersion = orchestratorVo.getDssOrchestratorVersion();
            if (dssOrchestratorVersion != null) {
                Long flowId = dssOrchestratorVersion.getAppId();
                workFlowManager.unlockWorkflow(userName, flowId, true, workspace);
            }
        }

    }

    private DSSProject getProjectByProjectId(Long projectId) throws DSSErrorException {
        ProjectInfoRequest projectInfoRequest = new ProjectInfoRequest();
        projectInfoRequest.setProjectId(projectId);
        DSSProject dssProject = RpcAskUtils.processAskException(DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender()
                .ask(projectInfoRequest), DSSProject.class, ProjectInfoRequest.class);
        if (dssProject == null) {
            DSSExceptionUtils.dealErrorException(6003, "工程不存在", DSSErrorException.class);
        }
        return dssProject;
    }

    private String modifyFlowJsonTime(String flowJson, Long modifyTime, String userName) {
        JsonObject jsonObject = JsonParser.parseString(flowJson).getAsJsonObject();

        jsonObject.addProperty("updateTime", modifyTime);
        jsonObject.addProperty("updateUser", userName);

        return jsonObject.toString();
    }

    private String modifyJson(String flowJson, EditFlowRequest editFlowRequest, Long modifyTime, String username) {
        String nodeKey = editFlowRequest.getNodeKey();
        String params = editFlowRequest.getParams();
        String title = editFlowRequest.getTitle();
        String desc = editFlowRequest.getDesc();
        String appTag = editFlowRequest.getAppTag();
        String businessTag = editFlowRequest.getBusinessTag();
        String ecConfTemplateId = editFlowRequest.getEcConfTemplateId();
        String ecConfTemplateName = editFlowRequest.getEcConfTemplateName();
        JsonObject jsonObject = JsonParser.parseString(flowJson).getAsJsonObject();

        JsonArray listArray = jsonObject.getAsJsonArray("nodes");

        for (JsonElement element : listArray) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.get("key").getAsString().equals(nodeKey)) {
                JsonElement jsonElement = JsonParser.parseString(params);
                if (StringUtils.isNotEmpty(params)) {
                    obj.add("params", jsonElement);
                }
                obj.addProperty("modifyTime", modifyTime);
                obj.addProperty("modifyUser", username);
                flowJsonAddPointProperty(obj, "title", title);
                flowJsonAddPointProperty(obj, "desc", desc);
                flowJsonAddProperty(obj, "appTag", appTag);
                flowJsonAddProperty(obj, "businessTag", businessTag);
                flowJsonAddProperty(obj, "ecConfTemplateId", ecConfTemplateId);
                flowJsonAddProperty(obj, "ecConfTemplateName", ecConfTemplateName);
                break;
            }
        }
        String modifyJson = jsonObject.toString();
        return modifyJson;
    }

    private void flowJsonAddPointProperty(JsonObject obj, String contentName, String content) {
        if (content != null) {
            obj.addProperty(contentName, content);
        }
    }

    private void flowJsonAddProperty(JsonObject obj, String contentName, String content) {
        if (StringUtils.isNotEmpty(content)) {
            obj.addProperty(contentName, content);
        } else {
            obj.remove(contentName);
        }
    }

    private void lockFlow(DSSFlow dssFlow, String username, String ticketId) throws DSSErrorException {

        // 尝试获取工作流编辑锁
        try {
            //只有父工作流才有锁，子工作流复用父工作流的锁
            if (dssFlow.getRootFlow()) {
                String flowEditLock = DSSFlowEditLockManager.tryAcquireLock(dssFlow, username, ticketId);
                dssFlow.setFlowEditLock(flowEditLock);
            }
        } catch (DSSErrorException e) {
            if (EDIT_LOCK_ERROR_CODE == e.getErrCode()) {
                DSSFlowEditLock flowEditLock = lockMapper.getFlowEditLockByID(dssFlow.getId());
                throw new DSSErrorException(60056, "用户已锁定编辑错误码，editLockInfo:" + flowEditLock);
            }
            throw e;
        }
    }


    public DataCheckerNodeResponse queryDataCheckerNode(String username, Workspace workspace, DataCheckerNodeRequest request) {

        DataCheckerNodeResponse dataCheckerNodeResponse = new DataCheckerNodeResponse();

        List<NodeInfo> nodeInfoList = nodeInfoMapper.getNodeTypeByGroupName(WorkflowNodeGroupEnum.SignalNode.getNameEn()
                , SignalNodeConstant.dataCheckerNode);

        if (CollectionUtils.isEmpty(nodeInfoList)) {
            logger.error("queryDataCheckerNode not find dataChecker node info");
            return dataCheckerNodeResponse;
        }

        List<DSSProject> dssProjectList = getDSSProject(workspace, username);

        if (CollectionUtils.isEmpty(dssProjectList)) {
            logger.error("queryDataCheckerNode workspace is {}, username is {}", workspace, username);
            return dataCheckerNodeResponse;
        }

        Map<Long, DSSProject> dssProjectMap = projectListToMap(dssProjectList);

        List<Long> projectIdList = new ArrayList<>(dssProjectMap.keySet());
        List<String> nodeTypeList = nodeInfoList.stream().map(NodeInfo::getNodeType).collect(Collectors.toList());
        List<DSSFlowNodeInfo> flowNodeInfoList = nodeContentMapper.queryFlowNodeInfo(projectIdList, nodeTypeList);

//        Map<String, List<NodeUIInfo>> nodeInfoGroup = nodeUIInfoGroupByNodeType(nodeTypeList);

        if (CollectionUtils.isEmpty(flowNodeInfoList)) {
            logger.error("queryDataCheckerNode query not find node info , example projectId is {}", projectIdList.get(0));
            return dataCheckerNodeResponse;
        }

        Map<Long, DSSFlowNodeInfo> dssFlowNodeInfoMap = flowNodeToMap(flowNodeInfoList);
        List<Long> contentIdList = new ArrayList<>(dssFlowNodeInfoMap.keySet());


        // 查询节点保存的配置信息
        Map<Long, List<NodeContentUIDO>> nodeContentUIGroup = getNodeContentUIGroup(contentIdList);
        if (nodeContentUIGroup.isEmpty()) {
            logger.error("queryDataCheckerNode query not find nodeUI info , example contextId is {}", contentIdList.get(0));
            return dataCheckerNodeResponse;
        }


        List<DataCheckerNodeInfo> dataCheckerNodeInfoList = new ArrayList<>();

        for (Long contentId : nodeContentUIGroup.keySet()) {

            DataCheckerNodeInfo dataCheckerNodeInfo = new DataCheckerNodeInfo();

            DSSFlowNodeInfo dssFlowNodeInfo = dssFlowNodeInfoMap.get(contentId);
            List<NodeContentUIDO> nodeUIList = nodeContentUIGroup.get(contentId);
            DSSProject dssProject = dssProjectMap.get(dssFlowNodeInfo.getProjectId());

//            Map<String, String> nodeDefaultValue = getNodeDefaultValue(nodeInfoGroup, dssFlowNodeInfo.getJobType());

            List<String> nodeUIKeys = nodeUIList.stream().map(NodeContentUIDO::getNodeUIKey).collect(Collectors.toList());
            List<String> nodeUIValues = nodeUIList.stream().map(NodeContentUIDO::getNodeUIValue).collect(Collectors.toList());
            Map<String, String> nodeMap = CollUtil.zip(nodeUIKeys, nodeUIValues);

            NodeBaseInfo nodeBaseInfo = getNodeBaseInfo(dssFlowNodeInfo, dssProject, nodeMap,username);
            BeanUtils.copyProperties(nodeBaseInfo, dataCheckerNodeInfo);

            dataCheckerNodeInfo.setMaxCheckHours(nodeMap.get("max.check.hours"));
            dataCheckerNodeInfo.setJobDesc(nodeMap.get("job.desc"));
            dataCheckerNodeInfo.setCheckObject(nodeMap.get("check.object"));
            dataCheckerNodeInfo.setNodeDesc(nodeMap.get("desc"));
            dataCheckerNodeInfo.setSourceType(nodeMap.get("source.type"));

            if (nodeMap.containsKey("qualitis.check") && StringUtils.isNotEmpty(nodeMap.get("qualitis.check"))) {
                dataCheckerNodeInfo.setQualitisCheck(Boolean.valueOf(nodeMap.get("qualitis.check")));
            }

            dataCheckerNodeInfoList.add(dataCheckerNodeInfo);

        }

        dataCheckerNodeInfoList = dataCheckerNodeResultFilter(dataCheckerNodeInfoList, request);


        dataCheckerNodeResponse.setTotal((long) dataCheckerNodeInfoList.size());
        // 分页处理
        int page = request.getPageNow() >= 1 ? request.getPageNow() : 1;
        int pageSize = request.getPageSize() >= 1 ? request.getPageSize() : 10;
        int start = (page - 1) * pageSize;
        int end = Math.min(page * pageSize, dataCheckerNodeInfoList.size());

        for (int i = start; i < end; i++) {
            DataCheckerNodeInfo dataCheckerNodeInfo = dataCheckerNodeInfoList.get(i);
            dataCheckerNodeResponse.getDataCheckerNodeInfoList().add(dataCheckerNodeInfo);
        }

        return dataCheckerNodeResponse;
    }


    public List<DataCheckerNodeInfo> dataCheckerNodeResultFilter(List<DataCheckerNodeInfo> dataCheckerNodeInfoList, DataCheckerNodeRequest request) {


        dataCheckerNodeInfoList = dataCheckerNodeInfoList.stream().filter(dataCheckerNodeInfo -> {

            boolean flag = true;

            if (!CollectionUtils.isEmpty(request.getProjectNameList())) {
                flag = request.getProjectNameList().contains(dataCheckerNodeInfo.getProjectName());
            }

            if (!StringUtils.isEmpty(request.getOrchestratorName()) && flag) {
                flag = request.getOrchestratorName().equals(dataCheckerNodeInfo.getOrchestratorName());
            }

            if (!CollectionUtils.isEmpty(request.getNodeNameList()) && flag) {
                flag = request.getNodeNameList().contains(dataCheckerNodeInfo.getNodeName());
            }

            if (!StringUtils.isEmpty(request.getSourceType()) && flag) {
                flag = request.getSourceType().equals(dataCheckerNodeInfo.getSourceType());
            }

            if (!StringUtils.isEmpty(request.getCheckObject()) && flag) {
                flag = StringUtils.isNotEmpty(dataCheckerNodeInfo.getCheckObject())
                        && dataCheckerNodeInfo.getCheckObject().contains(request.getCheckObject());
            }

            if (!StringUtils.isEmpty(request.getJobDesc()) && flag) {

                flag = StringUtils.isNotEmpty(dataCheckerNodeInfo.getJobDesc())
                        && dataCheckerNodeInfo.getJobDesc().contains(request.getJobDesc());
            }

            if (request.getQualitisCheck() != null && flag) {
                flag = request.getQualitisCheck().equals(dataCheckerNodeInfo.getQualitisCheck());
            }

            return flag;

        }).collect(Collectors.toList());


        dataCheckerNodeInfoList = dataCheckerNodeInfoList.stream().sorted(Comparator.comparing(DataCheckerNodeInfo::getCreateTime,
                Comparator.nullsFirst(Comparator.naturalOrder())).reversed()).collect(Collectors.toList());


        return dataCheckerNodeInfoList;

    }


    public EventSenderNodeResponse queryEventSenderNode(String username, Workspace workspace, EventSenderNodeRequest request) {

        EventSenderNodeResponse eventSenderNodeResponse = new EventSenderNodeResponse();

        List<NodeInfo> nodeInfoList = nodeInfoMapper.getNodeTypeByGroupName(WorkflowNodeGroupEnum.SignalNode.getNameEn()
                , SignalNodeConstant.eventSenderNode);

        if (CollectionUtils.isEmpty(nodeInfoList)) {
            logger.error("queryEventSenderNode not find dataChecker node info");
            return eventSenderNodeResponse;
        }

        List<DSSProject> dssProjectList = getDSSProject(workspace, username);

        if (CollectionUtils.isEmpty(dssProjectList)) {
            logger.error("queryEventSenderNode workspace is {}, username is {}", workspace, username);
            return eventSenderNodeResponse;
        }

        Map<Long, DSSProject> dssProjectMap = projectListToMap(dssProjectList);

        List<Long> projectIdList = new ArrayList<>(dssProjectMap.keySet());
        List<String> nodeTypeList = nodeInfoList.stream().map(NodeInfo::getNodeType).collect(Collectors.toList());
        List<DSSFlowNodeInfo> flowNodeInfoList = nodeContentMapper.queryFlowNodeInfo(projectIdList, nodeTypeList);

        if (CollectionUtils.isEmpty(flowNodeInfoList)) {
            logger.error("queryEventSenderNode query not find node info , example projectId is {}", projectIdList.get(0));
            return eventSenderNodeResponse;
        }

        Map<Long, DSSFlowNodeInfo> dssFlowNodeInfoMap = flowNodeToMap(flowNodeInfoList);
        List<Long> contentIdList = new ArrayList<>(dssFlowNodeInfoMap.keySet());

        // 查询节点保存的配置信息
        Map<Long, List<NodeContentUIDO>> nodeContentUIGroup = getNodeContentUIGroup(contentIdList);
        if (nodeContentUIGroup.isEmpty()) {
            logger.error("queryEventSenderNode query not find nodeUI info , example contextId is {}", contentIdList.get(0));
            return eventSenderNodeResponse;
        }


        List<EventSenderNodeInfo> eventSenderNodeInfoList = new ArrayList<>();

        for (Long contentId : nodeContentUIGroup.keySet()) {

            EventSenderNodeInfo eventSenderNodeInfo = new EventSenderNodeInfo();

            DSSFlowNodeInfo dssFlowNodeInfo = dssFlowNodeInfoMap.get(contentId);
            List<NodeContentUIDO> nodeUIList = nodeContentUIGroup.get(contentId);
            DSSProject dssProject = dssProjectMap.get(dssFlowNodeInfo.getProjectId());

            List<String> nodeUIKeys = nodeUIList.stream().map(NodeContentUIDO::getNodeUIKey).collect(Collectors.toList());
            List<String> nodeUIValues = nodeUIList.stream().map(NodeContentUIDO::getNodeUIValue).collect(Collectors.toList());
            Map<String, String> nodeMap = CollUtil.zip(nodeUIKeys, nodeUIValues);

            NodeBaseInfo nodeBaseInfo = getNodeBaseInfo(dssFlowNodeInfo, dssProject, nodeMap,username);
            BeanUtils.copyProperties(nodeBaseInfo, eventSenderNodeInfo);
            eventSenderNodeInfo.setNodeDesc(nodeMap.get("desc"));
            eventSenderNodeInfo.setMsgType(nodeMap.get("msg.type"));
            eventSenderNodeInfo.setMsgSender(nodeMap.get("msg.sender"));
            eventSenderNodeInfo.setMsgBody(nodeMap.get("msg.body"));
            eventSenderNodeInfo.setMsgTopic(nodeMap.get("msg.topic"));
            eventSenderNodeInfo.setMsgName(nodeMap.get("msg.name"));

            eventSenderNodeInfoList.add(eventSenderNodeInfo);

        }

        eventSenderNodeInfoList = eventSenderNodeResultFilter(eventSenderNodeInfoList, request);

        eventSenderNodeResponse.setTotal((long) eventSenderNodeInfoList.size());
        // 分页处理
        int page = request.getPageNow() >= 1 ? request.getPageNow() : 1;
        int pageSize = request.getPageSize() >= 1 ? request.getPageSize() : 10;
        int start = (page - 1) * pageSize;
        int end = Math.min(page * pageSize, eventSenderNodeInfoList.size());

        for (int i = start; i < end; i++) {
            EventSenderNodeInfo eventSenderNodeInfo = eventSenderNodeInfoList.get(i);
            eventSenderNodeResponse.getEventSenderNodeInfoList().add(eventSenderNodeInfo);
        }

        return eventSenderNodeResponse;
    }


    public List<EventSenderNodeInfo> eventSenderNodeResultFilter(List<EventSenderNodeInfo> eventSenderNodeInfoList, EventSenderNodeRequest request) {

        eventSenderNodeInfoList = eventSenderNodeInfoList.stream().filter(eventSenderNodeInfo -> {

            boolean flag = true;

            if (!CollectionUtils.isEmpty(request.getProjectNameList())) {
                flag = request.getProjectNameList().contains(eventSenderNodeInfo.getProjectName());
            }

            if (!StringUtils.isEmpty(request.getOrchestratorName()) && flag) {
                flag = request.getOrchestratorName().equals(eventSenderNodeInfo.getOrchestratorName());
            }

            if (!CollectionUtils.isEmpty(request.getNodeNameList()) && flag) {
                flag = request.getNodeNameList().contains(eventSenderNodeInfo.getNodeName());
            }

            if (!StringUtils.isEmpty(request.getMsgSender()) && flag) {
                flag = StringUtils.isNotEmpty(eventSenderNodeInfo.getMsgSender())
                        && eventSenderNodeInfo.getMsgSender().contains(request.getMsgSender());
            }

            if (!StringUtils.isEmpty(request.getMsgBody()) && flag) {

                flag = StringUtils.isNotEmpty(eventSenderNodeInfo.getMsgBody())
                        && eventSenderNodeInfo.getMsgBody().contains(request.getMsgBody());
            }

            if (!StringUtils.isEmpty(request.getMsgTopic()) && flag) {
                flag = StringUtils.isNotEmpty(eventSenderNodeInfo.getMsgTopic())
                        && eventSenderNodeInfo.getMsgTopic().contains(request.getMsgTopic());
            }

            if (!StringUtils.isEmpty(request.getMsgName()) && flag) {
                flag = StringUtils.isNotEmpty(eventSenderNodeInfo.getMsgName())
                        && eventSenderNodeInfo.getMsgName().contains(request.getMsgName());
            }

            return flag;

        }).collect(Collectors.toList());

        eventSenderNodeInfoList = eventSenderNodeInfoList.stream().sorted(Comparator.comparing(EventSenderNodeInfo::getCreateTime,
                Comparator.nullsFirst(Comparator.naturalOrder())).reversed()).collect(Collectors.toList());

        return eventSenderNodeInfoList;

    }


    public EventReceiveNodeResponse queryEventReceiveNode(String username, Workspace workspace, EventReceiverNodeRequest request) {

        EventReceiveNodeResponse eventReceiveNodeResponse = new EventReceiveNodeResponse();

        List<NodeInfo> nodeInfoList = nodeInfoMapper.getNodeTypeByGroupName(WorkflowNodeGroupEnum.SignalNode.getNameEn()
                , SignalNodeConstant.eventReceiverNode);

        if (CollectionUtils.isEmpty(nodeInfoList)) {
            logger.error("queryEventReceiveNode not find dataChecker node info");
            return eventReceiveNodeResponse;
        }

        List<DSSProject> dssProjectList = getDSSProject(workspace, username);

        if (CollectionUtils.isEmpty(dssProjectList)) {
            logger.error("queryEventReceiveNode workspace is {}, username is {}", workspace, username);
            return eventReceiveNodeResponse;
        }

        Map<Long, DSSProject> dssProjectMap = projectListToMap(dssProjectList);

        List<Long> projectIdList = new ArrayList<>(dssProjectMap.keySet());
        List<String> nodeTypeList = nodeInfoList.stream().map(NodeInfo::getNodeType).collect(Collectors.toList());
        List<DSSFlowNodeInfo> flowNodeInfoList = nodeContentMapper.queryFlowNodeInfo(projectIdList, nodeTypeList);

//        Map<String, List<NodeUIInfo>> nodeInfoGroup = nodeUIInfoGroupByNodeType(nodeTypeList);

        if (CollectionUtils.isEmpty(flowNodeInfoList)) {
            logger.error("queryEventReceiveNode query not find node info , example projectId is {}", projectIdList.get(0));
            return eventReceiveNodeResponse;
        }

        Map<Long, DSSFlowNodeInfo> dssFlowNodeInfoMap = flowNodeToMap(flowNodeInfoList);
        List<Long> contentIdList = new ArrayList<>(dssFlowNodeInfoMap.keySet());

        // 查询节点保存的配置信息
        Map<Long, List<NodeContentUIDO>> nodeContentUIGroup = getNodeContentUIGroup(contentIdList);
        if (nodeContentUIGroup.isEmpty()) {
            logger.error("queryEventSenderNode query not find nodeUI info , example contextId is {}", contentIdList.get(0));
            return eventReceiveNodeResponse;
        }


        List<EventReceiverNodeInfo> eventReceiverNodeInfoList = new ArrayList<>();

        for (Long contentId : nodeContentUIGroup.keySet()) {

            EventReceiverNodeInfo eventReceiverNodeInfo = new EventReceiverNodeInfo();

            DSSFlowNodeInfo dssFlowNodeInfo = dssFlowNodeInfoMap.get(contentId);
            List<NodeContentUIDO> nodeUIList = nodeContentUIGroup.get(contentId);
            DSSProject dssProject = dssProjectMap.get(dssFlowNodeInfo.getProjectId());
//            Map<String, String> nodeDefaultValue = getNodeDefaultValue(nodeInfoGroup, dssFlowNodeInfo.getJobType());

            List<String> nodeUIKeys = nodeUIList.stream().map(NodeContentUIDO::getNodeUIKey).collect(Collectors.toList());
            List<String> nodeUIValues = nodeUIList.stream().map(NodeContentUIDO::getNodeUIValue).collect(Collectors.toList());
            Map<String, String> nodeMap = CollUtil.zip(nodeUIKeys, nodeUIValues);

            NodeBaseInfo nodeBaseInfo = getNodeBaseInfo(dssFlowNodeInfo, dssProject, nodeMap,username);
            BeanUtils.copyProperties(nodeBaseInfo, eventReceiverNodeInfo);
            eventReceiverNodeInfo.setNodeDesc(nodeMap.get("desc"));
            eventReceiverNodeInfo.setMsgType(nodeMap.get("msg.type"));
            eventReceiverNodeInfo.setMsgReceiver(nodeMap.get("msg.receiver"));
            eventReceiverNodeInfo.setMsgTopic(nodeMap.get("msg.topic"));
            eventReceiverNodeInfo.setMsgName(nodeMap.get("msg.name"));
            eventReceiverNodeInfo.setQueryFrequency(nodeMap.get("query.frequency"));
            eventReceiverNodeInfo.setMaxReceiveHours(nodeMap.get("max.receive.hours"));
            eventReceiverNodeInfo.setMsgSaveKey(nodeMap.get("msg.savekey"));

            if (nodeMap.containsKey("only.receive.today") && StringUtils.isNotEmpty(nodeMap.get("only.receive.today"))) {
                eventReceiverNodeInfo.setOnlyReceiveToday(Boolean.valueOf(nodeMap.get("only.receive.today")));
            }

            if (nodeMap.containsKey("msg.receive.use.rundate") && StringUtils.isNotEmpty(nodeMap.get("msg.receive.use.rundate"))) {
                eventReceiverNodeInfo.setMsgReceiveUseRunDate(Boolean.valueOf(nodeMap.get("msg.receive.use.rundate")));
            }

            eventReceiverNodeInfoList.add(eventReceiverNodeInfo);
        }

        eventReceiverNodeInfoList = eventReceiveNodeResultFilter(eventReceiverNodeInfoList, request);

        eventReceiveNodeResponse.setTotal((long) eventReceiverNodeInfoList.size());
        // 分页处理
        int page = request.getPageNow() >= 1 ? request.getPageNow() : 1;
        int pageSize = request.getPageSize() >= 1 ? request.getPageSize() : 10;
        int start = (page - 1) * pageSize;
        int end = Math.min(page * pageSize, eventReceiverNodeInfoList.size());

        for (int i = start; i < end; i++) {
            EventReceiverNodeInfo eventReceiverNodeInfo = eventReceiverNodeInfoList.get(i);
            eventReceiveNodeResponse.getEventReceiverNodeInfoList().add(eventReceiverNodeInfo);
        }

        return eventReceiveNodeResponse;

    }


    public List<EventReceiverNodeInfo> eventReceiveNodeResultFilter(List<EventReceiverNodeInfo> eventReceiverNodeInfoList,
                                                                    EventReceiverNodeRequest request) {

        eventReceiverNodeInfoList = eventReceiverNodeInfoList.stream().filter(eventReceiverNodeInfo -> {

            boolean flag = true;

            if (!CollectionUtils.isEmpty(request.getProjectNameList())) {
                flag = request.getProjectNameList().contains(eventReceiverNodeInfo.getProjectName());
            }

            if (!StringUtils.isEmpty(request.getOrchestratorName()) && flag) {
                flag = request.getOrchestratorName().equals(eventReceiverNodeInfo.getOrchestratorName());
            }

            if (!CollectionUtils.isEmpty(request.getNodeNameList()) && flag) {
                flag = request.getNodeNameList().contains(eventReceiverNodeInfo.getNodeName());
            }

            if (!StringUtils.isEmpty(request.getMsgReceiver()) && flag) {

                flag = StringUtils.isNotEmpty(eventReceiverNodeInfo.getMsgReceiver())
                        && eventReceiverNodeInfo.getMsgReceiver().contains(request.getMsgReceiver());
            }

            if (!StringUtils.isEmpty(request.getMsgTopic()) && flag) {
                flag = StringUtils.isNotEmpty(eventReceiverNodeInfo.getMsgTopic())
                        && eventReceiverNodeInfo.getMsgTopic().contains(request.getMsgTopic());
            }

            if (!StringUtils.isEmpty(request.getMsgName()) && flag) {

                flag = StringUtils.isNotEmpty(eventReceiverNodeInfo.getMsgName())
                        && eventReceiverNodeInfo.getMsgName().contains(request.getMsgName());
            }

            if (!StringUtils.isEmpty(request.getMaxReceiveHours()) && flag) {
//                flag = request.getMaxReceiveHours().equals(eventReceiverNodeInfo.getMaxReceiveHours());
                flag = StringUtils.isNotEmpty(eventReceiverNodeInfo.getMaxReceiveHours())
                        && eventReceiverNodeInfo.getMaxReceiveHours().contains(request.getMaxReceiveHours());
            }

            if (!StringUtils.isEmpty(request.getMsgSaveKey()) && flag) {
                flag = StringUtils.isNotEmpty(eventReceiverNodeInfo.getMsgSaveKey())
                        && eventReceiverNodeInfo.getMsgSaveKey().contains(request.getMsgSaveKey());
            }

            if (request.getOnlyReceiveToday() != null && flag) {
                flag = request.getOnlyReceiveToday().equals(eventReceiverNodeInfo.getOnlyReceiveToday());
            }

            if (request.getMsgReceiveUseRunDate() != null && flag) {
                flag = request.getMsgReceiveUseRunDate().equals(eventReceiverNodeInfo.getMsgReceiveUseRunDate());
            }

            return flag;

        }).collect(Collectors.toList());

        eventReceiverNodeInfoList = eventReceiverNodeInfoList.stream().sorted(Comparator.comparing(EventReceiverNodeInfo::getCreateTime,
                Comparator.nullsFirst(Comparator.naturalOrder())).reversed()).collect(Collectors.toList());

        return eventReceiverNodeInfoList;

    }


    public List<String> queryNodeUiValueByKey(Workspace workspace, String username, String nodeUiKey, String groupNameEn) {

        logger.info("nodeUiKey is {}", nodeUiKey);
        List<String> nodeUIValue = new ArrayList<>();
        try {

            // 获取项目
            List<DSSProject> dssProjectList = getDSSProject(workspace, username);

            if (CollectionUtils.isEmpty(dssProjectList)) {
                logger.error("queryNodeUiValueByKey find project is empty, workspaceId is {}, username is {}, nodeUikey is {}", workspace.getWorkspaceId(), username, nodeUiKey);
                return nodeUIValue;
            }

            List<Long> projectIdList = dssProjectList.stream().map(DSSProject::getId).collect(Collectors.toList());

            List<NodeInfo> nodeInfoList = getNodeInfoByGroupName(groupNameEn);
            if (CollectionUtils.isEmpty(nodeInfoList)) {
                logger.error("queryNodeUiValueByKey groupNameEn is {}", groupNameEn);
                return nodeUIValue;
            }
            List<String> nodeTypeList = nodeInfoList.stream().map(NodeInfo::getNodeType).collect(Collectors.toList());
            // 查询节点信息
            List<DSSFlowNodeInfo> flowNodeInfoList = nodeContentMapper.queryFlowNodeInfo(projectIdList, nodeTypeList);

            if (CollectionUtils.isEmpty(flowNodeInfoList)) {
                logger.error("queryNodeUiValueByKey find node info is empty, example project id is {}, nodeUikey is {}", projectIdList.get(0), nodeUiKey);
                return nodeUIValue;
            }

            List<Long> contentIdList = flowNodeInfoList.stream().map(DSSFlowNodeInfo::getContentId).collect(Collectors.toList());
            List<NodeContentUIDO> nodeContentUIDOList = nodeContentUIMapper.getNodeContentUIByNodeUIKey(contentIdList, nodeUiKey);
            nodeUIValue = nodeContentUIDOList.stream().map(NodeContentUIDO::getNodeUIValue).distinct().collect(Collectors.toList());

        } catch (Exception e) {
            logger.error("queryNodeUiValueByKey error, workspaceId is {}, username is {}, nodeUikey is {}", workspace.getWorkspaceId(), username, nodeUiKey);
            logger.error(e.getMessage());
        }

        // 过滤出不为空的信息
        return nodeUIValue.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
    }


    @Override
    public List<String> querySourceType(Workspace workspace, String username) {
        String nodeUIkey = "source.type";
        return queryNodeUiValueByKey(workspace, username, nodeUIkey, WorkflowNodeGroupEnum.SignalNode.getNameEn());
    }


    @Override
    public List<String> queryJobDesc(Workspace workspace, String username) {
        String nodeUIkey = "job.desc";

        List<String> jobDescValue = queryNodeUiValueByKey(workspace, username, nodeUIkey, WorkflowNodeGroupEnum.SignalNode.getNameEn());

        List<String> descValue = new ArrayList<>();
        for (String value : jobDescValue) {
            if (StringUtils.isEmpty(value)) {
                continue;
            }

            descValue.addAll(splitJobDesc(value));
        }

        return descValue.stream().filter(value -> {
            return !StringUtils.isEmpty(value.trim());
        }).distinct().collect(Collectors.toList());

    }


    public Map<String, List<NodeUIInfo>> nodeUIInfoGroupByNodeType(List<String> nodeTypeList) {

        List<NodeUIInfo> nodeUIInfoList = nodeInfoMapper.queryNodeUIInfoList(nodeTypeList);
        return nodeUIInfoList.stream().collect(Collectors.groupingBy(NodeUIInfo::getNodeType));

    }


    public Map<String, String> getNodeDefaultValue(Map<String, List<NodeUIInfo>> nodeInfoGroup, String nodeType) {

        List<NodeUIInfo> nodeUIInfoList = nodeInfoGroup.get(nodeType);
        Map<String, String> nodeDefaultValue = new HashMap<>();
        if (CollectionUtils.isNotEmpty(nodeUIInfoList)) {
            List<String> key = nodeUIInfoList.stream().map(NodeUIInfo::getKey).collect(Collectors.toList());
            List<String> value = nodeUIInfoList.stream().map(NodeUIInfo::getDefaultValue).collect(Collectors.toList());
            nodeDefaultValue = CollUtil.zip(key, value);
        }

        return nodeDefaultValue;

    }




    public NodeBaseInfo getNodeBaseInfo(DSSFlowNodeInfo dssFlowNodeInfo, DSSProject dssProject, Map<String, String> nodeMap,String username) {

        NodeBaseInfo nodeBaseInfo = new NodeBaseInfo();

        nodeBaseInfo.setNodeName(nodeMap.get("title"));
        nodeBaseInfo.setNodeTypeName(dssFlowNodeInfo.getNodeTypeName());
        nodeBaseInfo.setNodeType(dssFlowNodeInfo.getJobType());
        nodeBaseInfo.setNodeId(dssFlowNodeInfo.getNodeId());
        nodeBaseInfo.setOrchestratorId(dssFlowNodeInfo.getOrchestratorId());
        nodeBaseInfo.setNodeKey(dssFlowNodeInfo.getNodeKey());
        nodeBaseInfo.setOrchestratorName(dssFlowNodeInfo.getOrchestratorName());
        nodeBaseInfo.setProjectName(dssProject.getName());
        nodeBaseInfo.setProjectId(dssProject.getId());
        nodeBaseInfo.setContentId(dssFlowNodeInfo.getContentId());
        nodeBaseInfo.setUpdateTime(dssFlowNodeInfo.getModifyTime());
        nodeBaseInfo.setCreateTime(dssFlowNodeInfo.getCreateTime());
        nodeBaseInfo.setNodeContent(nodeMap);
        nodeBaseInfo.setAssociateGit(dssProject.getAssociateGit());
        boolean editable = (CollectionUtils.isNotEmpty(dssProject.getEditUsers()) && dssProject.getEditUsers().contains(username))
                || (CollectionUtils.isNotEmpty(dssProject.getReleaseUsers()) && dssProject.getReleaseUsers().contains(username))
                || (StringUtils.isNotEmpty(dssProject.getCreateBy()) && dssProject.getCreateBy().equals(username));
        nodeBaseInfo.setEditable(editable);
        nodeBaseInfo.setFlowId(dssFlowNodeInfo.getFlowId());

        return nodeBaseInfo;

    }


    public List<String> splitJobDesc(String jobDesc) {

        if (jobDesc.contains(";")) {
            return Arrays.asList(jobDesc.trim().split(";"));
        } else if (jobDesc.contains("\n")) {
            return Arrays.asList(jobDesc.trim().split("\n"));
        } else {
            return Arrays.asList(jobDesc.trim().split(""));
        }

    }


    public Map<Long,DSSFlow> getFlowMap(List<DSSFlowNodeInfo> flowNodeInfoList){

        Map<Long,DSSFlow> flowMap = new HashMap<>();

        List<Long> orchestratorIdList = flowNodeInfoList.stream().map(DSSFlowNodeInfo::getOrchestratorId)
                .collect(Collectors.toList());

        if(CollectionUtils.isEmpty(orchestratorIdList)){
            logger.error("getFlowMap orchestratorIdList is empty");
            return flowMap;
        }

        List<DSSFlow> dssFlowList = flowMapper.selectFlowListByOrchestratorId(orchestratorIdList);

        if(CollectionUtils.isEmpty(dssFlowList)){
            logger.error("example orchestratorId is {}",orchestratorIdList.get(0));
            logger.error("getFlowMap dssFlow list is empty");
            return flowMap;
        }

        Map<Long,List<DSSFlow>> dssFlowMap = dssFlowList.stream().collect(Collectors.groupingBy(DSSFlow::getOrchestratorId));


        for(Long orchestratorId : dssFlowMap.keySet()){

            DSSFlow dssFlow = dssFlowMap.get(orchestratorId).stream().max(Comparator.comparing(DSSFlow::getId)).orElse(null);

            if(dssFlow == null){
                continue;
            }

            flowMap.put(orchestratorId,dssFlow);
        }

        return flowMap;

    }


}



