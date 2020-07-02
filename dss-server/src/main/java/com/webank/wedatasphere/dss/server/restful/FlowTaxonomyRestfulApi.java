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


import com.webank.wedatasphere.dss.server.service.DSSFlowTaxonomyService;
import com.webank.wedatasphere.dss.server.service.DSSProjectService;
import com.webank.wedatasphere.dss.server.service.DSSUserService;
import com.webank.wedatasphere.dss.server.entity.DSSFlowTaxonomy;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.server.publish.PublishManager;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;


@Component
@Path("/dss")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FlowTaxonomyRestfulApi {

    @Autowired
    private DSSFlowTaxonomyService flowTaxonomyService;
    @Autowired
    private DSSUserService dssUserService;
    @Autowired
    private DSSProjectService projectService;
    @Autowired
    private PublishManager publishManager;

    @POST
    @Path("/addFlowTaxonomy")
    public Response addFlowTaxonomy(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException {
        String userName = SecurityFilter.getLoginUsername(req);
        String name = json.get("name").getTextValue();
        String description = json.get("description").getTextValue();
        Long projectVersionID = json.get("projectVersionID").getLongValue();
        publishManager.checkeIsPublishing(projectVersionID);
        // TODO: 2019/5/16 空值校验，重复名校验
        DSSFlowTaxonomy dssFlowTaxonomy = new DSSFlowTaxonomy();
        Date date = new Date();
        dssFlowTaxonomy.setName(name);
        dssFlowTaxonomy.setDescription(description);
        dssFlowTaxonomy.setCreatorID(dssUserService.getUserID(userName));
        dssFlowTaxonomy.setCreateTime(date);
        dssFlowTaxonomy.setUpdateTime(date);
        dssFlowTaxonomy.setProjectID(projectService.getProjectByProjectVersionID(projectVersionID).getId());
        flowTaxonomyService.addFlowTaxonomy(dssFlowTaxonomy,projectVersionID);
        return Message.messageToResponse(Message.ok());
    }
    @POST
    @Path("/updateFlowTaxonomy")
    public Response updateFlowTaxonomy(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException {
        String name = json.get("name")==null?null:json.get("name").getTextValue();
        String description = json.get("description") == null?null:json.get("description").getTextValue();
        Long id = json.get("id").getLongValue();
        Long projectVersionID = json.get("projectVersionID").getLongValue();
        publishManager.checkeIsPublishing(projectVersionID);
        // TODO: 2019/6/13  projectVersionID的更新校验
        // TODO: 2019/5/16 空值校验，重复名校验
        DSSFlowTaxonomy dssFlowTaxonomy = new DSSFlowTaxonomy();
        dssFlowTaxonomy.setId(id);
        dssFlowTaxonomy.setName(name);
        dssFlowTaxonomy.setDescription(description);
        dssFlowTaxonomy.setUpdateTime(new Date());
        flowTaxonomyService.updateFlowTaxonomy(dssFlowTaxonomy,projectVersionID);
        return Message.messageToResponse(Message.ok());
    }

    @POST
    @Path("/deleteFlowTaxonomy")
    public Response deleteFlowTaxonomy(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException {
        Long id = json.get("id").getLongValue();
        // TODO: 2019/5/16 工程分类下工程是不是空的校验
        // TODO: 2019/5/16  如果删除的是我的工程，我参与的工程
        Long projectVersionID = json.get("projectVersionID").getLongValue();
        publishManager.checkeIsPublishing(projectVersionID);
        // TODO: 2019/6/13  projectVersionID的更新校验
        if(flowTaxonomyService.hasFlows(id)){
            throw new DSSErrorException(90001,"该工作流分类下还有工作流，不能删除该分类！");
        }
        flowTaxonomyService.deleteFlowTaxonomy(id,projectVersionID);
        return Message.messageToResponse(Message.ok());
    }
}
