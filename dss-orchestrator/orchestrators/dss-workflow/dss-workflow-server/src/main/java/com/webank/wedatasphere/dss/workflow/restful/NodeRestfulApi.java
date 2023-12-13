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

package com.webank.wedatasphere.dss.workflow.restful;

import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.development.utils.DSSJobContentConstant;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import com.webank.wedatasphere.dss.workflow.cs.DSSCSHelper;
import com.webank.wedatasphere.dss.workflow.entity.*;
import com.webank.wedatasphere.dss.workflow.entity.request.AppConnNodeUrlRequest;
import com.webank.wedatasphere.dss.workflow.entity.request.BatchDeleteAppConnNodeRequest;
import com.webank.wedatasphere.dss.workflow.entity.request.CreateExternalNodeRequest;
import com.webank.wedatasphere.dss.workflow.entity.request.UpdateExternalNodeRequest;
import com.webank.wedatasphere.dss.workflow.entity.vo.NodeGroupVO;
import com.webank.wedatasphere.dss.workflow.entity.vo.NodeInfoVO;
import com.webank.wedatasphere.dss.workflow.entity.vo.NodeUiVO;
import com.webank.wedatasphere.dss.workflow.entity.vo.NodeUiValidateVO;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import com.webank.wedatasphere.dss.workflow.service.WorkflowNodeService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/dss/workflow", produces = {"application/json"})
public class NodeRestfulApi {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private DSSFlowService dssFlowService;
    @Autowired
    private WorkflowNodeService workflowNodeService;
    @Autowired
    private WorkFlowParser workFlowParser;

    @RequestMapping(value = "/listNodeType",method = RequestMethod.GET)
    public Message listNodeType(HttpServletRequest req) {
        Function<NodeGroup, String> supplier = internationalization(req, NodeGroup::getNameEn, NodeGroup::getName);
        List<NodeGroupVO> groupVos = new ArrayList<>();
        //cache
        List<NodeGroup> groups = workflowNodeService.listNodeGroups();
        for (NodeGroup group : groups) {
            NodeGroupVO nodeGroupVO = new NodeGroupVO();
            BeanUtils.copyProperties(group, nodeGroupVO);
            nodeGroupVO.setTitle(supplier.apply(group));
            nodeGroupVO.setChildren(group.getNodes().stream().map(n -> {
                try {
                    return transfer(n, req);
                } catch (IOException e) {
                    logger.error("ListNodeType get AppConn {} icons failed.", n.getAppConnName(), e);
                    throw new DSSRuntimeException(81200, e.getMessage(), e);
                }
            }).collect(Collectors.toList()));
            groupVos.add(nodeGroupVO);
        }
        groupVos = groupVos.stream().sorted(NodeGroupVO::compareTo).collect(Collectors.toList());
        return Message.ok().data("nodeTypes", groupVos);
    }

    private <P, T> Function<P, T> internationalization(HttpServletRequest req,
                                                       Function<P, T> supplier1,
                                                       Function<P, T> supplier2) {
        String language = req.getHeader("Content-language");
        if (language != null) {
            language = language.trim();
        }
        if (ContentLanguage.en.getName().equals(language)) {
            return supplier1;
        } else {
            return supplier2;
        }
    }

    private NodeUiValidateVO transfer(NodeUiValidate v, HttpServletRequest req) {
        Function<NodeUiValidate, String> supplier = internationalization(req, NodeUiValidate::getErrorMsgEn,
                NodeUiValidate::getErrorMsg);
        NodeUiValidateVO nodeUiValidateVO = new NodeUiValidateVO();
        BeanUtils.copyProperties(v, nodeUiValidateVO);
        nodeUiValidateVO.setMessage(supplier.apply(v));
        return nodeUiValidateVO;
    }

    private NodeInfoVO transfer(NodeInfo nodeInfo, HttpServletRequest req) throws IOException {
        NodeInfoVO nodeInfoVO = new NodeInfoVO();
        BeanUtils.copyProperties(nodeInfo, nodeInfoVO);
        nodeInfoVO.setTitle(nodeInfo.getName());
        nodeInfoVO.setType(nodeInfo.getNodeType());
        nodeInfoVO.setImage("");
        Function<NodeUi, String> descriptionSupplier = internationalization(req, NodeUi::getDescriptionEn, NodeUi::getDescription);
        Function<NodeUi, String> labelNameSupplier = internationalization(req, NodeUi::getLableNameEn, NodeUi::getLableName);
        ArrayList<NodeUiVO> nodeUiVOS = new ArrayList<>();
        Set<String> keySet = new HashSet<>(nodeInfo.getNodeUis().size());
        for (NodeUi nodeUi : nodeInfo.getNodeUis()) {
            //避免重复的ui key，因为第三方组件可能会重复配置。
            if(keySet.contains(nodeUi.getKey())){
                continue;
            }
            NodeUiVO nodeUiVO = new NodeUiVO();
            BeanUtils.copyProperties(nodeUi, nodeUiVO);
            nodeUiVO.setDesc(descriptionSupplier.apply(nodeUi));
            nodeUiVO.setLableName(labelNameSupplier.apply(nodeUi));
            nodeUiVO.setNodeUiValidateVOS(nodeUi.getNodeUiValidates().stream().map(v -> transfer(v, req)).sorted(NodeUiValidateVO::compareTo).collect(Collectors.toList()));
            nodeUiVOS.add(nodeUiVO);
            keySet.add(nodeUi.getKey());
        }
        nodeUiVOS.sort(NodeUiVO::compareTo);
        nodeInfoVO.setNodeUiVOS(nodeUiVOS);
        return nodeInfoVO;
    }


    @RequestMapping(path ="nodeIcon/{nodeType}", method = RequestMethod.GET)
    public void getIcon(HttpServletResponse response, @PathVariable("nodeType") String nodeType) throws IOException {
        byte[] icon = workflowNodeService.getNodeIcon(nodeType);
        response.setContentType("image/svg+xml");
        response.addHeader("Cache-Control", "max-age=3600, no-transform");
        response.getOutputStream().write(icon);
    }

    @RequestMapping(value = "/createAppConnNode",method = RequestMethod.POST)
    public Message createExternalNode(HttpServletRequest req, @RequestBody CreateExternalNodeRequest createExternalNodeRequest) throws DSSErrorException, IllegalAccessException, ExternalOperationFailedException, InstantiationException {
        String userName = SecurityFilter.getLoginUsername(req);
        Workspace workspace = SSOHelper.getWorkspace(req);
        Long projectId = createExternalNodeRequest.getProjectID();
        String nodeType = createExternalNodeRequest.getNodeType();
        Long flowId = createExternalNodeRequest.getFlowID();
        Map<String, Object> params = createExternalNodeRequest.getParams();
        String nodeId = createExternalNodeRequest.getNodeID();

        logger.info("User {} try to create a {} node for workflow {}, params is {}.", userName, nodeType, flowId, params);
        CommonAppConnNode node = new CommonAppConnNode();
        node.setNodeType(nodeType);
        node.setFlowId(flowId);
        node.setProjectId(projectId);
        //update by peaceWong add nodeId to appConnNode
        node.setId(nodeId);
        //update by shanhuang json中解析获取contextID,然后放到请求参数中
        DSSFlow dssFlow = dssFlowService.getFlow(flowId);
        String flowContent = dssFlow.getFlowJson();
        String ContextIDStr = DSSCSHelper.getContextIDStrByJson(flowContent);
        node.setContextId(ContextIDStr);
        //补个flowName的信息..如果这里没查询就直接让前台传了
        node.setFlowName(dssFlow.getName());
        String label = createExternalNodeRequest.getLabels().getRoute();
        node.setDssLabels(Collections.singletonList(new EnvDSSLabel(label)));
        node.setWorkspace(workspace);
        node.setParams(params);
        node.setJobContent(params);
        //补充json信息,方便appConn去解析获取响应的值
        if(params.containsKey(DSSJobContentConstant.UP_STREAM_KEY)) {
            List<DSSNode> dssNodes = null;
            if(params.get(DSSJobContentConstant.UP_STREAM_KEY).equals("empty")) {
//                dssNodes = workFlowParser.getWorkFlowNodes(flowContent);
                params.remove(DSSJobContentConstant.UP_STREAM_KEY);
                logger.info("Create a node that is not bound to an upstream node.");
            } else {
                String[] upStreams = ((String) params.get(DSSJobContentConstant.UP_STREAM_KEY)).split(",");
                dssNodes = workFlowParser.getWorkFlowNodes(flowContent).stream()
                        .filter(dssNode -> ArrayUtils.contains(upStreams, dssNode.getId())).collect(Collectors.toList());
                if(dssNodes.isEmpty() && upStreams.length > 0) {
                    return Message.error("Create node failed! Caused by: the banding up-stream nodes are not exists(绑定的上游节点不存在).");
                }
                params.put(DSSJobContentConstant.UP_STREAM_KEY, dssNodes);
            }

        }
        Map<String, Object> jobContent = workflowNodeService.createNode(userName, node);
        return Message.ok().data("result", jobContent);
    }

    @RequestMapping(value = "/updateAppConnNode",method = RequestMethod.POST)
    public Message updateExternalNode(HttpServletRequest req, @RequestBody UpdateExternalNodeRequest updateExternalNodeRequest) throws IllegalAccessException, ExternalOperationFailedException, InstantiationException {
        String userName = SecurityFilter.getLoginUsername(req);
        Workspace workspace = SSOHelper.getWorkspace(req);
        Long projectId = updateExternalNodeRequest.getProjectID();
        Long flowId = updateExternalNodeRequest.getFlowID();
        String nodeType = updateExternalNodeRequest.getNodeType();
        Map<String, Object> params = updateExternalNodeRequest.getParams();
        logger.info("User {} try to update ExternalNode request with projectId {}, flowId {}, params is {}, nodeType: {}.",
                userName, projectId, flowId, params, nodeType);
        CommonAppConnNode node = new CommonAppConnNode();
        node.setProjectId(projectId);
        node.setNodeType(nodeType);
        node.setFlowId(flowId);
        String label = updateExternalNodeRequest.getLabels().getRoute();
        node.setDssLabels(Collections.singletonList(new EnvDSSLabel(label)));
        node.setWorkspace(workspace);
        node.setParams(params);
        node.setJobContent(params);
        workflowNodeService.updateNode(userName, node);
        return Message.ok().data("result", node.getJobContent());
    }

    @RequestMapping(value = "/deleteAppConnNode",method = RequestMethod.POST)
    public Message deleteExternalNode(HttpServletRequest req, @RequestBody UpdateExternalNodeRequest updateExternalNodeRequest) throws IllegalAccessException, ExternalOperationFailedException, InstantiationException {
        String userName = SecurityFilter.getLoginUsername(req);
        Workspace workspace = SSOHelper.getWorkspace(req);
        Long projectId = updateExternalNodeRequest.getProjectID();
        Long flowId = updateExternalNodeRequest.getFlowID();
        String nodeType = updateExternalNodeRequest.getNodeType();
        Map<String, Object> params = updateExternalNodeRequest.getParams();
        logger.info("User {} with the workflow {} in project {} try to delete externalNode with params: {}, nodeType: {}.",
                userName, flowId, projectId, params, nodeType);
        CommonAppConnNode node = new CommonAppConnNode();
        String label = updateExternalNodeRequest.getLabels().getRoute();
        node.setDssLabels(Collections.singletonList(new EnvDSSLabel(label)));
        node.setWorkspace(workspace);
        node.setProjectId(projectId);
        node.setNodeType(nodeType);
        node.setJobContent(params);
        node.setFlowId(flowId);
        node.setName(updateExternalNodeRequest.getName());
        workflowNodeService.deleteNode(userName, node);
        return Message.ok().data("result", node.getJobContent());
    }

    @RequestMapping(value = "/batchDeleteAppConnNode",method = RequestMethod.POST)
    public Message batchDeleteAppConnNode(HttpServletRequest req, @RequestBody BatchDeleteAppConnNodeRequest batchDeleteAppConnNodeRequest) throws IllegalAccessException, ExternalOperationFailedException, InstantiationException {
        String userName = SecurityFilter.getLoginUsername(req);
        Workspace workspace = SSOHelper.getWorkspace(req);
        List<Map<String,Object>>  jsonList = batchDeleteAppConnNodeRequest.getNodes();
        jsonList.forEach(json ->{
            Long projectId = Long.parseLong(json.get("projectID").toString());
            String nodeType = json.get("nodeType").toString();
            Long flowId = (Long) json.get("flowID");
            Map<String, Object> params = (Map<String, Object>) json.get("params");
            logger.info("User {} try to delete ExternalNode with projectId {}, flowId {}, params is {}, nodeType: {}.",
                    userName, projectId, flowId, params, nodeType);
            CommonAppConnNode node = new CommonAppConnNode();
            String label = ((Map<String, Object>) json.get("labels")).get("route").toString();
            node.setDssLabels(Collections.singletonList(new EnvDSSLabel(label)));
            node.setWorkspace(workspace);
            node.setProjectId(projectId);
            node.setNodeType(nodeType);
            node.setJobContent(params);
            node.setFlowId(flowId);
            node.setName(json.get("name").toString());
            workflowNodeService.deleteNode(userName, node);
        });

        return Message.ok("success").data("result","success");
    }

    @RequestMapping(value = "/getAppConnNodeUrl",method = RequestMethod.POST)
    public Message getAppConnNodeUrl(HttpServletRequest req, @RequestBody AppConnNodeUrlRequest appConnNodeUrlRequest) {
        String userName = SecurityFilter.getLoginUsername(req);
        Workspace workspace = SSOHelper.getWorkspace(req);
        Long projectId = appConnNodeUrlRequest.getProjectID();
        Long flowId = appConnNodeUrlRequest.getFlowId();
        String nodeType = appConnNodeUrlRequest.getNodeType();
        Map<String, Object> params = appConnNodeUrlRequest.getParams();
        logger.info("User {} with the workflow {} in project {} try to getAppConnNodeUrl with params: {}, nodeType: {}.",
                userName, flowId, projectId, params, nodeType);
        CommonAppConnNode node = new CommonAppConnNode();
        node.setWorkspace(workspace);
        node.setProjectId(projectId);
        node.setNodeType(nodeType);
        node.setJobContent(params);
        node.setFlowId(flowId);
        String label = appConnNodeUrlRequest.getLabels().getRoute();
        node.setDssLabels(Collections.singletonList(new EnvDSSLabel(label)));
        String jumpUrl = workflowNodeService.getNodeJumpUrl(params, node, userName);
        return Message.ok().data("jumpUrl", jumpUrl);
    }
}

