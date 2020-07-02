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


import com.webank.wedatasphere.dss.server.service.DSSProjectTaxonomyService;
import com.webank.wedatasphere.dss.server.service.DSSUserService;
import com.webank.wedatasphere.dss.server.entity.DSSProjectTaxonomy;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
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
public class ProjectTaxonomyRestfulApi {

    @Autowired
    private DSSProjectTaxonomyService projectTaxonomyService;
    @Autowired
    private DSSUserService dssUserService;

    @POST
    @Path("/addProjectTaxonomy")
    public Response addProjectTaxonomy(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException {
        String userName = SecurityFilter.getLoginUsername(req);
        String name = json.get("name").getTextValue();
        String description = json.get("description").getTextValue();
        // TODO: 2019/5/16 空值校验，重复名校验
        DSSProjectTaxonomy dssProjectTaxonomy = new DSSProjectTaxonomy();
        Date date = new Date();
        dssProjectTaxonomy.setName(name);
        dssProjectTaxonomy.setDescription(description);
        dssProjectTaxonomy.setCreatorID(dssUserService.getUserID(userName));
        dssProjectTaxonomy.setCreateTime(date);
        dssProjectTaxonomy.setUpdateTime(date);
        projectTaxonomyService.addProjectTaxonomy(dssProjectTaxonomy);
        return Message.messageToResponse(Message.ok());
    }
    @POST
    @Path("/updateProjectTaxonomy")
    public Response updateProjectTaxonomy(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException {
        String name = json.get("name")==null?null:json.get("name").getTextValue();
        String description = json.get("description") == null?null:json.get("description").getTextValue();
        Long id = json.get("id").getLongValue();
        // TODO: 2019/5/16 空值校验，重复名校验
        DSSProjectTaxonomy dssProjectTaxonomy = new DSSProjectTaxonomy();
        dssProjectTaxonomy.setId(id);
        dssProjectTaxonomy.setName(name);
        dssProjectTaxonomy.setDescription(description);
        dssProjectTaxonomy.setUpdateTime(new Date());
        projectTaxonomyService.updateProjectTaxonomy(dssProjectTaxonomy);
        return Message.messageToResponse(Message.ok());
    }

    @POST
    @Path("/deleteProjectTaxonomy")
    public Response deleteProjectTaxonomy(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException {
        Long id = json.get("id").getLongValue();
        // TODO: 2019/5/16 工程分类下工程是不是空的校验
        // TODO: 2019/5/16  如果删除的是我的工程，我参与的工程
        if(projectTaxonomyService.hasProjects(id)){
            throw new DSSErrorException(60000,"该工程分类下还有工程，不能删除该分类！");
        }
        projectTaxonomyService.deleteProjectTaxonomy(id);
        return Message.messageToResponse(Message.ok());
    }
}
