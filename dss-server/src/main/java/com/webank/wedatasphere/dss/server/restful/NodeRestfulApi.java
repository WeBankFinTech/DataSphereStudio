/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.server.restful;


import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.execution.core.CommonAppJointNode;
import com.webank.wedatasphere.dss.appjoint.service.NodeService;
import com.webank.wedatasphere.dss.application.entity.Application;
import com.webank.wedatasphere.dss.application.service.ApplicationService;
import com.webank.wedatasphere.dss.application.util.ApplicationUtils;
import com.webank.wedatasphere.dss.server.entity.NodeInfo;
import com.webank.wedatasphere.dss.server.function.FunctionInvoker;
import com.webank.wedatasphere.dss.server.function.FunctionPool;
import com.webank.wedatasphere.dss.server.function.NodeServiceFunction;
import com.webank.wedatasphere.dss.server.service.DWSNodeInfoService;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
@Path("/dss")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NodeRestfulApi {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DWSNodeInfoService nodeInfoService;

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private FunctionInvoker functionInvoker;

    @GET
    @Path("/listNodeType")
    public Response listNodeType(@Context HttpServletRequest req, @QueryParam("applicationName")List<String> names) {
        HashMap<String, List<NodeInfo>> nodeTypes = new HashMap<>();
        if(names == null || names.isEmpty()){
            names = applicationService.listApplicationNames();
        }
        names.forEach(f ->{
            //如果redirecturl不为ull的话，返回固定格式，并且进行url加密
            Application application = applicationService.getApplication(f);
            List<NodeInfo> nodeInfo = nodeInfoService.getNodeType(f);
            String redirectUrl = application.getRedirectUrl();
            if(redirectUrl != null){
                nodeInfo.forEach(n ->n.setJumpUrl(ApplicationUtils.redirectUrlFormat(redirectUrl,n.getJumpUrl())));
            }
            nodeTypes.put(f,nodeInfo);
        });
        return Message.messageToResponse(Message.ok().data("nodeTypes",nodeTypes));
    }

    @POST
    @Path("/createAppjointNode")
    public Response  createExternalNode(@Context HttpServletRequest req,Map<String,Object> json) throws AppJointErrorException {
        return createNodeResponse(req,json, FunctionPool.createNode);
    }

    private Response createNodeResponse(HttpServletRequest req, Map<String,Object> json, NodeServiceFunction function) throws AppJointErrorException {
        String userName = SecurityFilter.getLoginUsername(req);
        Long projectID = Long.parseLong(json.get("id").toString());
        String nodeType = json.get("nodeType").toString();
        Map<String, Object> params = (Map<String,Object>)json.get("params");
        logger.info("CreateExternalNode request params is "+ params+",nodeType:"+nodeType);
        CommonAppJointNode node = new CommonAppJointNode();
        node.setProjectId(projectID);
        node.setNodeType(nodeType);
        Map<String,Object> jobContent = functionInvoker.nodeServiceFunction(userName,params,node,function);
        return Message.messageToResponse(Message.ok().data("result",jobContent));
    }

    @POST
    @Path("/updateAppjointNode")
    public Response  updateExternalNode(@Context HttpServletRequest req,Map<String,Object> json) throws AppJointErrorException {
        return createNodeResponse(req,json, FunctionPool.updateNode);
    }

    @POST
    @Path("/deleteAppjointNode")
    public Response  deleteExternalNode(@Context HttpServletRequest req,Map<String,Object> json) throws AppJointErrorException {
        return createNodeResponse(req,json, FunctionPool.deleteNode);
    }
}

