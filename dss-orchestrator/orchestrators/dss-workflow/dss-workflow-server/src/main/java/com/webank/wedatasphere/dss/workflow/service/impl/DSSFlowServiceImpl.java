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

package com.webank.wedatasphere.dss.workflow.service.impl;


import com.google.gson.Gson;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.contextservice.service.impl.ContextServiceImpl;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import com.webank.wedatasphere.dss.workflow.dao.FlowMapper;
import com.webank.wedatasphere.dss.workflow.io.input.NodeInputService;
import com.webank.wedatasphere.dss.workflow.lock.Lock;
import com.webank.wedatasphere.dss.workflow.service.BMLService;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import com.webank.wedatasphere.linkis.cs.common.utils.CSCommonUtils;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

//import static com.webank.wedatasphere.dss.server.conf.DSSServerConf.DSS_FLOW_EDIT_LOCK_TIMEOUT;

/**
 * Created by v_wbjftang on 2019/5/15.
 */
@Service
public class DSSFlowServiceImpl implements DSSFlowService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FlowMapper flowMapper;


    @Autowired
    private NodeInputService nodeInputService;

//    @Autowired
//    private EventRelationMapper eventRelationMapper;

    @Autowired
    private WorkFlowParser workFlowParser;

    @Autowired
    private BMLService bmlService;


//    @Autowired(required = false)
//    private LockMapper lockMapper;

    private static ContextService contextService = ContextServiceImpl.getInstance();

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
        for (Long subFlowID : subFlowIDs) {
            if (cyFlow.getChildren() == null) {
                cyFlow.setChildren(new ArrayList<DSSFlow>());
            }
            DSSFlow SubFlow = genDSSFlowTree(subFlowID);

            cyFlow.addChildren(SubFlow);
        }
        return cyFlow;
    }


    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public DSSFlow addFlow(DSSFlow dssFlow,
                               String contextID) throws DSSErrorException {
        try {
            flowMapper.insertFlow(dssFlow);
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage());
            throw new DSSErrorException(90003, "工作流名不能重复");
        }
        Map<String, Object> contextIDMap = new HashMap<>();
        String userName = dssFlow.getCreator();
        if (StringUtils.isNotBlank(contextID)) {
            contextIDMap.put(CSCommonUtils.CONTEXT_ID_STR, contextID);
        } else {
            logger.error("Generate contextID null.");
        }
        String jsonFlow = new Gson().toJson(contextIDMap);
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
    public DSSFlow addSubFlow(DSSFlow DSSFlow, Long parentFlowID, String contextIDStr) throws DSSErrorException {
        DSSFlow parentFlow = flowMapper.selectFlowByID(parentFlowID);
        DSSFlow.setProjectID(parentFlow.getProjectID());
        DSSFlow subFlow = addFlow(DSSFlow, contextIDStr);
        //数据库中插入关联信息
        flowMapper.insertFlowRelation(subFlow.getId(), parentFlowID);
        return subFlow;
    }

    @Override
    public DSSFlow getLatestVersionFlow(Long flowID) {
        DSSFlow DSSFlow = getFlowByID(flowID);
        //todo update
        String userName = DSSFlow.getCreator();
        Map<String, Object> query = bmlService.query(userName, DSSFlow.getResourceId(), DSSFlow.getBmlVersion());
        DSSFlow.setFlowJson(query.get("string").toString());
        return DSSFlow;
    }

    @Override
    public DSSFlow getOneVersionFlow(Long flowID) {
        DSSFlow DSSFlow = getFlowByID(flowID);

        String userName = DSSFlow.getCreator();
        Map<String, Object> query = bmlService.query(userName, DSSFlow.getResourceId(), DSSFlow.getBmlVersion());
        DSSFlow.setFlowJson(query.get("string").toString());
        return DSSFlow;
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
        flowIDlist.stream().forEach(f -> {
            deleteFlow(f);
        });
    }

    /*@Lock*/
    @Transactional(rollbackFor = {DSSErrorException.class})
    @Override
    public String saveFlow(Long flowID,
                           String jsonFlow,
                           String comment,
                           String userName,
                           String workspaceName,
                           String projectName
    ) throws DSSErrorException {

        DSSFlow dssFlow = flowMapper.selectFlowByID(flowID);
        String creator = dssFlow.getCreator();
        if (StringUtils.isNotEmpty(creator)) {
            userName = creator;
        }

        String resourceId = dssFlow.getResourceId();

        Long parentFlowID = flowMapper.getParentFlowID(flowID);

        // 这里不要检查ContextID具体版本等，只要存在就不创建 2020-0423
        jsonFlow = contextService.checkAndCreateContextID(jsonFlow, dssFlow.getBmlVersion(),
                workspaceName, projectName, dssFlow.getName(), userName, false);


        Map<String, Object> bmlReturnMap = bmlService.update(userName, resourceId, jsonFlow);

        dssFlow.setId(flowID);
        dssFlow.setHasSaved(true);
        dssFlow.setDescription(comment);
        dssFlow.setResourceId(bmlReturnMap.get("resourceId").toString());
        dssFlow.setBmlVersion(bmlReturnMap.get("version").toString());
        //todo 数据库增加版本更新
        flowMapper.updateFlowInputInfo(dssFlow);

        try {
            contextService.checkAndSaveContext(jsonFlow,  String.valueOf(parentFlowID));
        }  catch (DSSErrorException e) {
            logger.error("Failed to saveContext: ", e);
        }

        String version = bmlReturnMap.get("version").toString();

        return version;
    }

    @Override
    public Integer getParentRank(Long parentFlowID) {
        return getFlowByID(parentFlowID).getRank();
    }

    @Override
    public Long getParentFlowID(Long flowID) {
        return flowMapper.getParentFlowID(flowID);
    }

    @Deprecated
    private Integer getParentFlowIDByFlowID(Long flowID, Integer rank) {
        Long parentFlowID = flowMapper.selectParentFlowIDByFlowID(flowID);
        if (parentFlowID != null) {
            rank++;
            getParentFlowIDByFlowID(parentFlowID, rank);
        }
        return rank;
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


    @Override
    public DSSFlow copyRootFlow(Long rootFlowId, String userName, String workspaceName, String projectName, String version) throws DSSErrorException, IOException {
        DSSFlow dssFlow = flowMapper.selectFlowByID(rootFlowId);
        DSSFlow rootFlowWithSubFlows = copyFlowAndSetSubFlowInDB(dssFlow, userName);
        updateFlowJson(userName, workspaceName, projectName, rootFlowWithSubFlows, version, null);
        DSSFlow copyFlow = flowMapper.selectFlowByID(rootFlowWithSubFlows.getId());
        return copyFlow;
    }


    private DSSFlow copyFlowAndSetSubFlowInDB(DSSFlow DSSFlow,
                                              String userName) {
        DSSFlow cyFlow = new DSSFlow();
        BeanUtils.copyProperties(DSSFlow, cyFlow, "children", "flowVersions");
        //封装flow信息
        cyFlow.setCreator(userName);
        cyFlow.setCreateTime(new Date());
        cyFlow.setId(null);
        flowMapper.insertFlow(cyFlow);
        List<Long> subFlowIDs = flowMapper.selectSubFlowIDByParentFlowID(DSSFlow.getId());
        for (Long subFlowID : subFlowIDs) {
            DSSFlow subDSSFlow = flowMapper.selectFlowByID(subFlowID);
            if (DSSFlow.getChildren() == null) {
                DSSFlow.setChildren(new ArrayList<DSSFlow>());
            }
            DSSFlow copySubFlow = copyFlowAndSetSubFlowInDB(subDSSFlow, userName);
            persistenceFlowRelation(copySubFlow.getId(), cyFlow.getId());
            cyFlow.addChildren(copySubFlow);
        }
        return cyFlow;
    }

    private void updateFlowJson(String userName, String workspaceName, String projectName, DSSFlow rootFlow, String version, Long parentFlowId) throws DSSErrorException, IOException {
        String flowJson = bmlService.readFlowJsonFromBML(userName, rootFlow.getResourceId(), rootFlow.getBmlVersion());
        //如果包含subflow,需要一同导入subflow内容，并更新parrentflow的json内容
        // TODO: 2020/7/31 优化update方法里面的saveContent
        String updateFlowJson = updateFlowContextId(userName, workspaceName, projectName, flowJson, rootFlow.getName(), version, parentFlowId);
        updateFlowJson = updateWorkFlowNodeJson(userName, projectName, updateFlowJson, rootFlow);
        List<? extends DSSFlow> subFlows = rootFlow.getChildren();
        if (subFlows != null) {
            for (DSSFlow subflow : subFlows) {
                updateFlowJson(userName, workspaceName, projectName, subflow, version, rootFlow.getId());
            }
        }
        DSSFlow updateDssFlow = uploadFlowJsonToBml(userName, projectName, rootFlow, updateFlowJson);
        //todo add dssflow to database
        flowMapper.updateFlowInputInfo(updateDssFlow);
        contextService.checkAndSaveContext(updateFlowJson, String.valueOf(parentFlowId));
    }


    private DSSFlow uploadFlowJsonToBml(String userName, String projectName, DSSFlow dssFlow, String flowJson) {

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


    private String updateFlowContextId(String userName, String workspace, String projectName, String flowJson, String flowName, String flowVersion, Long parentFlowId) throws DSSErrorException, IOException {
        String parentFlowIdStr = null;
        if (parentFlowId != null) {
            parentFlowIdStr = parentFlowId.toString();
        }
        String contextID = contextService.checkAndInitContext(flowJson, parentFlowIdStr, workspace, projectName, flowName, flowVersion, userName);
        String updatedFlowJson = workFlowParser.updateFlowJsonWithKey(flowJson, "contextID", contextID);
        return updatedFlowJson;
    }


    private String updateWorkFlowNodeJson(String userName, String projectName, String flowJson, DSSFlow DSSFlow) throws DSSErrorException, IOException {
        List<String> nodeJsonList = workFlowParser.getWorkFlowNodesJson(flowJson);
        if (nodeJsonList == null) {
            throw new DSSErrorException(90073, "工作流内没有工作流节点，导入失败" + DSSFlow.getName());
        }
        String updateContextId = workFlowParser.getValueWithKey(flowJson, "contextID");
        if (nodeJsonList.size() == 0) {
            return flowJson;
        }
        List<DSSFlow> subflows = (List<DSSFlow>) DSSFlow.getChildren();
        List<Map<String, Object>> nodeJsonListRes = new ArrayList<>();
        if (nodeJsonList != null && nodeJsonList.size() > 0) {
            for (String nodeJson : nodeJsonList) {
                String updateNodeJson = nodeJson;

                // TODO: 2020/3/20 暂时注视掉appjoint相关
//                String updateNodeJson = nodeInputService.uploadResourceToBml(userName, nodeJson, workFlowResourceSavePath, projectName);
//                updateNodeJson =nodeInputService.uploadAppjointResource(userName,projectName, DSSFlow,updateNodeJson,updateContextId,appjointResourceSavePath);
//
                Map<String, Object> nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(nodeJson, Map.class);
                //更新subflowID
                String nodeType = nodeJsonMap.get("jobType").toString();
                if ("workflow.subflow".equals(nodeType)) {
                    String subFlowName = nodeJsonMap.get("title").toString();
                    List<DSSFlow> DSSFlowList = subflows.stream().filter(subflow ->
                            subflow.getName().equals(subFlowName)
                    ).collect(Collectors.toList());
                    if (DSSFlowList.size() == 1) {
                        updateNodeJson = nodeInputService.updateNodeSubflowID(updateNodeJson, DSSFlowList.get(0).getId());
                        nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(updateNodeJson, Map.class);
                        nodeJsonListRes.add(nodeJsonMap);
                    } else if (DSSFlowList.size() > 1) {
                        logger.error("工程内存在重复的子工作流节点名称，导入失败" + subFlowName);
                        throw new DSSErrorException(90077, "工程内存在重复的子工作流节点名称，导入失败" + subFlowName);
                    } else {
                        logger.error("工程内存在重复的子工作流节点名称，导入失败" + subFlowName);
                        throw new DSSErrorException(90078, "工程内未能找到子工作流节点，导入失败" + subFlowName);
                    }
                } else {
                    nodeJsonListRes.add(nodeJsonMap);
                }
            }
        }

        return workFlowParser.updateFlowJsonWithKey(flowJson, "nodes", nodeJsonListRes);
    }


    private void persistenceFlowRelation(Long flowID, Long parentFlowID) {
        DSSFlowRelation relation = flowMapper.selectFlowRelation(flowID, parentFlowID);
        if (relation == null) {
            flowMapper.insertFlowRelation(flowID, parentFlowID);
        }
    }

}
