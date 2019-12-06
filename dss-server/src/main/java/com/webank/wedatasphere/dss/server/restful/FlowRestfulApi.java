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
import com.webank.wedatasphere.dss.server.service.DWSFlowService;
import com.webank.wedatasphere.dss.server.service.DWSProjectService;
import com.webank.wedatasphere.dss.server.service.DWSUserService;
import com.webank.wedatasphere.dss.common.entity.flow.DWSFlow;
import com.webank.wedatasphere.dss.common.entity.flow.DWSFlowVersion;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.server.operate.Op;
import com.webank.wedatasphere.dss.server.publish.PublishManager;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@Component
@Path("/dss")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FlowRestfulApi {

    @Autowired
    private DWSFlowService flowService;
    @Autowired
    private DWSUserService dwsUserService;
    @Autowired
    private PublishManager publishManager;

    ObjectMapper mapper = new ObjectMapper();

    @GET
    @Path("/listAllFlowVersions")
    public Response listAllVersions(@Context HttpServletRequest req, @QueryParam("id")Long flowID,@QueryParam("projectVersionID")Long projectVersionID) {
        List<DWSFlowVersion> versions = flowService.listAllFlowVersions(flowID,projectVersionID);
        return Message.messageToResponse(Message.ok().data("versions",versions));
    }

    @POST
    @Path("/addFlow")
    public Response addFlow(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException {
        //如果是子工作流，那么分类应该是和父类一起的？
        String userName = SecurityFilter.getLoginUsername(req);
        // TODO: 2019/5/23 flowName工程名下唯一校验
        String name = json.get("name").getTextValue();
        String description = json.get("description") == null?null:json.get("description").getTextValue();
        Long parentFlowID = json.get("parentFlowID") ==null?null:json.get("parentFlowID").getLongValue();
        Long taxonomyID = json.get("taxonomyID") == null? null:json.get("taxonomyID").getLongValue();
        Long projectVersionID = json.get("projectVersionID").getLongValue();
        String uses = json.get("uses") == null?null:json.get("uses").getTextValue();
        if(taxonomyID == null && parentFlowID == null) throw new DSSErrorException(90009,"请求选择工作流分类");
        publishManager.checkeIsPublishing(projectVersionID);
        DWSFlow dwsFlow = new DWSFlow();
        dwsFlow.setProjectID(projectService.getProjectByProjectVersionID(projectVersionID).getId());
        dwsFlow.setName(name);
        dwsFlow.setDescription(description);
        dwsFlow.setCreatorID(dwsUserService.getUserID(userName));
        dwsFlow.setCreateTime(new Date());
        dwsFlow.setState(false);
        dwsFlow.setSource("create by user");
        dwsFlow.setUses(uses);
        if(parentFlowID == null){
            dwsFlow.setRootFlow(true);
            dwsFlow.setRank(0);
            dwsFlow.setHasSaved(true);
            dwsFlow = flowService.addRootFlow(dwsFlow,taxonomyID,projectVersionID);
        }else {
            dwsFlow.setRootFlow(false);
            Integer rank = flowService.getParentRank(parentFlowID);
            // TODO: 2019/6/3 并发问题考虑for update
            dwsFlow.setRank(rank+1);
            dwsFlow.setHasSaved(false);
            dwsFlow = flowService.addSubFlow(dwsFlow,parentFlowID,projectVersionID);
        }
        // TODO: 2019/5/16 空值校验，重复名校验
        return Message.messageToResponse(Message.ok().data("flow",dwsFlow));
    }

    @POST
    @Path("/updateFlowBaseInfo")
    public Response updateFlowBaseInfo(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException {
        Long flowID = json.get("id").getLongValue();
        String name = json.get("name")==null?null:json.get("name").getTextValue();
        String description = json.get("description") == null?null:json.get("description").getTextValue();
        Long taxonomyID = json.get("taxonomyID") == null?null:json.get("taxonomyID").getLongValue();
        Long projectVersionID = json.get("projectVersionID").getLongValue();
        String uses = json.get("uses") == null?null:json.get("uses").getTextValue();
        publishManager.checkeIsPublishing(projectVersionID);
        // TODO: 2019/6/13  projectVersionID的更新校验
        //这里可以不做事务
        DWSFlow dwsFlow = new DWSFlow();
        dwsFlow.setId(flowID);
        dwsFlow.setName(name);
        dwsFlow.setDescription(description);
        dwsFlow.setUses(uses);
        flowService.updateFlowBaseInfo(dwsFlow,projectVersionID,taxonomyID);
        return Message.messageToResponse(Message.ok());
    }

    @GET
    @Path("/get")
    public Response get(@Context HttpServletRequest req, @QueryParam("id")Long flowID,@QueryParam("version")String version,@QueryParam("projectVersionID")Long projectVersionID) throws DSSErrorException {
        // TODO: 2019/5/23 id空值判断
        DWSFlow dwsFlow;
        if (StringUtils.isEmpty(version)){
            dwsFlow = flowService.getLatestVersionFlow(flowID,projectVersionID);
            dwsFlow.setFlowVersions(Arrays.asList(dwsFlow.getLatestVersion()));
        }else {
            dwsFlow = flowService.getOneVersionFlow(flowID, version,projectVersionID);
        }
        return Message.messageToResponse(Message.ok().data("flow",dwsFlow));
    }

    @POST
    @Path("/deleteFlow")
    public Response deleteFlow(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException {
        Long flowID = json.get("id").getLongValue();
        Boolean sure = json.get("sure") == null?false:json.get("sure").getBooleanValue();
        Long projectVersionID = json.get("projectVersionID").getLongValue();
        // TODO: 2019/6/13  projectVersionID的更新校验
        //state为true代表曾经发布过
        if(flowService.getFlowByID(flowID).getState() && !sure) {
            return Message.messageToResponse(Message.ok().data("warmMsg","该工作流曾经发布过，删除将会将该工作流的所有版本都删除，是否继续？"));
        }
        publishManager.checkeIsPublishing(projectVersionID);
        flowService.batchDeleteFlow(Arrays.asList(flowID),projectVersionID);
        return Message.messageToResponse(Message.ok());
    }

    @POST
    @Path("/saveFlow")
    public Response saveFlow(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException, IOException, AppJointErrorException {
        Long flowID = json.get("id").getLongValue();
        String jsonFlow = json.get("json").getTextValue();
        Long projectVersionID = json.get("projectVersionID").getLongValue();
        String userName = SecurityFilter.getLoginUsername(req);
        String comment = json.get("comment") == null?"保存更新":json.get("comment").getTextValue();
        List<Op> ops = mapper.readValue(json.get("ops"), new TypeReference<List<Op>>(){});
        publishManager.checkeIsPublishing(projectVersionID);
        String version =flowService.saveFlow(flowID,jsonFlow,comment,userName,projectVersionID,ops);
        return Message.messageToResponse(Message.ok().data("flowVersion",version));
    }

    @Autowired
    private DWSProjectService projectService;

}
