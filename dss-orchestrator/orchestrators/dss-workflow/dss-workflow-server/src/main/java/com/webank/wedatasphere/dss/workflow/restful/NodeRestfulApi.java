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

package com.webank.wedatasphere.dss.workflow.restful;


import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.schedule.core.SchedulerAppConn;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.standard.app.sso.SSOIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardErrorException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.cs.DSSCSHelper;
import com.webank.wedatasphere.dss.workflow.entity.*;
import com.webank.wedatasphere.dss.workflow.entity.vo.NodeGroupVO;
import com.webank.wedatasphere.dss.workflow.entity.vo.NodeInfoVO;
import com.webank.wedatasphere.dss.workflow.entity.vo.NodeUiVO;
import com.webank.wedatasphere.dss.workflow.entity.vo.NodeUiValidateVO;
import com.webank.wedatasphere.dss.workflow.function.DomainSupplier;
import com.webank.wedatasphere.dss.workflow.function.FunctionInvoker;
import com.webank.wedatasphere.dss.workflow.function.FunctionPool;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import com.webank.wedatasphere.dss.workflow.service.WorkflowNodeService;
import com.webank.wedatasphere.linkis.cs.common.utils.CSCommonUtils;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by v_wbjftang on 2019/7/12.
 */
@Component
@Path("/dss/workflow")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NodeRestfulApi {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AppConnService appConnService;

    @Autowired
    private FunctionInvoker functionInvoker;

    @Autowired
    private DSSFlowService DSSFlowService;

    @Autowired
    private WorkflowNodeService workflowNodeService;

    @GET
    @Path("/listNodeType")
    public Response listNodeType(@Context HttpServletRequest req) {
        DomainSupplier<NodeGroup, String> supplier = internationalization(req, NodeGroup::getNameEn, NodeGroup::getName);
        List<NodeGroupVO> groupVOS = new ArrayList<>();
        //cache
        List<NodeGroup> groups = workflowNodeService.listNodeGroups();
        for (NodeGroup group : groups) {
            NodeGroupVO nodeGroupVO = new NodeGroupVO();
            BeanUtils.copyProperties(group, nodeGroupVO);
            nodeGroupVO.setTitle(supplier.get(group));
            nodeGroupVO.setChildren(group.getNodes().stream().map(n -> {
                try {
                    return transfer(n, req);
                } catch (AppStandardErrorException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList()));
            groupVOS.add(nodeGroupVO);
        }
        groupVOS = groupVOS.stream().sorted(NodeGroupVO::compareTo).collect(Collectors.toList());
        return Message.messageToResponse(Message.ok().data("nodeTypes", groupVOS));
    }

    private <P, T> DomainSupplier<P, T> internationalization(HttpServletRequest req,
                                                             DomainSupplier<P, T> supplier1,
                                                             DomainSupplier<P, T> supplier2) {
        String language = req.getHeader("Content-language");
        if (language != null) {language = language.trim();}
        if (ContentLanguage.en.getName().equals(language))
        {return supplier1;}
        else {
            return supplier2;
        }
    }

    private NodeUiValidateVO transfer(NodeUiValidate v, HttpServletRequest req) {
        DomainSupplier<NodeUiValidate, String> supplier = internationalization(req, NodeUiValidate::getErrorMsgEn,
                NodeUiValidate::getErrorMsg);
        NodeUiValidateVO nodeUiValidateVO = new NodeUiValidateVO();
        BeanUtils.copyProperties(v, nodeUiValidateVO);
        nodeUiValidateVO.setMessage(supplier.get(v));
        return nodeUiValidateVO;
    }

    private NodeInfoVO transfer(NodeInfo nodeInfo, HttpServletRequest req) throws AppStandardErrorException {
        NodeInfoVO nodeInfoVO = new NodeInfoVO();
        BeanUtils.copyProperties(nodeInfo, nodeInfoVO);
        nodeInfoVO.setTitle(nodeInfo.getName());
        nodeInfoVO.setType(nodeInfo.getNodeType());
        nodeInfoVO.setImage(nodeInfo.getIcon());
        DomainSupplier<NodeUi, String> descriptionSupplier = internationalization(req, NodeUi::getDescriptionEn, NodeUi::getDescription);
        DomainSupplier<NodeUi, String> lableNameSupplier = internationalization(req, NodeUi::getLableNameEn, NodeUi::getLableName);
        ArrayList<NodeUiVO> nodeUiVOS = new ArrayList<>();
        for (NodeUi nodeUi : nodeInfo.getNodeUis()) {
            NodeUiVO nodeUiVO = new NodeUiVO();
            BeanUtils.copyProperties(nodeUi, nodeUiVO);
            nodeUiVO.setDesc(descriptionSupplier.get(nodeUi));
            nodeUiVO.setLableName(lableNameSupplier.get(nodeUi));
            nodeUiVO.setNodeUiValidateVOS(nodeUi.getNodeUiValidates().stream().map(v -> transfer(v, req)).sorted(NodeUiValidateVO::compareTo).collect(Collectors.toList()));
            nodeUiVOS.add(nodeUiVO);
        }
        nodeUiVOS.sort(NodeUiVO::compareTo);
        nodeInfoVO.setNodeUiVOS(nodeUiVOS);
        //cache
        AppConn applicationAppConn = appConnService.getAppConnByNodeType(nodeInfo.getNodeType());
        if (applicationAppConn != null && !(applicationAppConn instanceof SchedulerAppConn)) {
            AppStandard standard = applicationAppConn.getAppStandards().stream().filter(appStandard -> appStandard instanceof SSOIntegrationStandard)
                    .findAny().orElse(null);
            if(null !=standard) {
               SSOIntegrationStandard ssoIntegrationStandard = (SSOIntegrationStandard)standard;
                String redirectUrl =ssoIntegrationStandard.getSSOBuilderService().createSSOUrlBuilderOperation().getBuiltUrl();
                if (redirectUrl != null) {
                    nodeInfoVO.setJumpUrl(redirectUrl);
                }
            }
        }
        return nodeInfoVO;
    }

    @POST
    @Path("/createAppConnNode")
    public Response createExternalNode(@Context HttpServletRequest req, Map<String, Object> json) throws DSSErrorException, IllegalAccessException, ExternalOperationFailedException, InstantiationException {
        String userName = SecurityFilter.getLoginUsername(req);
        Workspace workspace = SSOHelper.getWorkspace(req);
        Long projectID = Long.parseLong(json.get("projectID").toString());
        String nodeType = json.get("nodeType").toString();
        Long flowID = Long.parseLong(json.get("flowID").toString());
        Map<String, Object> params = (Map<String, Object>) json.get("params");
        logger.info("CreateExternalNode request params is " + params + ",nodeType:" + nodeType);
        CommonAppConnNode node = new CommonAppConnNode();
        node.setProjectId(projectID);
        node.setNodeType(nodeType);
        node.setFlowId(flowID);
        node.setProjectId(projectID);
        //update by peaceWong add nodeID to appJointNode
        Object nodeID = json.get(CSCommonUtils.NODE_ID);
        if (null != nodeID) {
            node.setId(nodeID.toString());
        }
        //update by shanhuang json中解析获取contextID,然后放到请求参数中
        DSSFlow DSSFlow = DSSFlowService.getLatestVersionFlow(flowID);
        String flowContent = DSSFlow.getFlowJson();
        String ContextIDStr = DSSCSHelper.getContextIDStrByJson(flowContent);
        params.put(CSCommonUtils.CONTEXT_ID_STR, ContextIDStr);
        //补个flowName的信息..如果这里没查询就直接让前台传了
        node.setFlowName(DSSFlow.getName());
        //补充json信息,方便appjoint去解析获取响应的值
        params.put("json", flowContent);
        String label = ((Map<String, Object>) json.get("labels")).get("route").toString();
        params.put("labels", label);
        params.put("workspace", workspace);
        functionInvoker.nodeServiceFunction(userName, params, node, FunctionPool.createNode);
        return Message.messageToResponse(Message.ok().data("result", node.getJobContent()));
    }

    @POST
    @Path("/updateAppConnNode")
    public Response updateExternalNode(@Context HttpServletRequest req, Map<String, Object> json) throws IllegalAccessException, ExternalOperationFailedException, InstantiationException {
        String userName = SecurityFilter.getLoginUsername(req);
        Workspace workspace = SSOHelper.getWorkspace(req);
        Long projectID = Long.parseLong(json.get("projectID").toString());
        String nodeType = json.get("nodeType").toString();
        Map<String, Object> params = (Map<String, Object>) json.get("params");
        logger.info("UpdateExternalNode request params is " + params + ",nodeType:" + nodeType);
        CommonAppConnNode node = new CommonAppConnNode();
        node.setProjectId(projectID);
        node.setNodeType(nodeType);
        node.setProjectId(projectID);
        String label = ((Map<String, Object>) json.get("labels")).get("route").toString();
        params.put("labels", label);
        params.put("workspace", workspace);
        functionInvoker.nodeServiceFunction(userName, params, node, FunctionPool.updateNode);
        return Message.messageToResponse(Message.ok().data("result", node.getJobContent()));
    }

    @POST
    @Path("/deleteAppConnNode")
    public Response deleteExternalNode(@Context HttpServletRequest req, Map<String, Object> json) throws IllegalAccessException, ExternalOperationFailedException, InstantiationException {
        String userName = SecurityFilter.getLoginUsername(req);
        Workspace workspace = SSOHelper.getWorkspace(req);
        Long projectID = Long.parseLong(json.get("projectID").toString());
        String nodeType = json.get("nodeType").toString();
        Map<String, Object> params = (Map<String, Object>) json.get("params");
        logger.info("DeletepwdExternalNode request params is " + params + ",nodeType:" + nodeType);
        CommonAppConnNode node = new CommonAppConnNode();
        node.setProjectId(projectID);
        node.setNodeType(nodeType);
        String label = ((Map<String, Object>) json.get("labels")).get("route").toString();
        params.put("labels", label);
        params.put("workspace", workspace);
        functionInvoker.nodeServiceFunction(userName, params, node, FunctionPool.deleteNode);
        return Message.messageToResponse(Message.ok().data("result", node.getJobContent()));
    }

    @POST
    @Path("/getAppConnNodeUrl")
    public Response getAppConnNodeUrl(@Context HttpServletRequest req, Map<String, Object> json) throws IllegalAccessException, ExternalOperationFailedException, InstantiationException {
        String userName = SecurityFilter.getLoginUsername(req);
        Workspace workspace = SSOHelper.getWorkspace(req);
        Long projectID = Long.parseLong(json.get("projectID").toString());
        String nodeType = json.get("nodeType").toString();
        Map<String, Object> params = (Map<String, Object>) json.get("params");
        logger.info("getAppConnNodeUrl request params is " + params + ",nodeType:" + nodeType);
        CommonAppConnNode node = new CommonAppConnNode();
        node.setProjectId(projectID);
        node.setNodeType(nodeType);
        String label = ((Map<String, Object>) json.get("labels")).get("route").toString();
        params.put("labels", label);
        params.put("workspace", workspace);
        String jumpUrl = workflowNodeService.getNodeJumpUrl(params, node);
        return Message.messageToResponse(Message.ok().data("jumpUrl", jumpUrl));
    }


}

