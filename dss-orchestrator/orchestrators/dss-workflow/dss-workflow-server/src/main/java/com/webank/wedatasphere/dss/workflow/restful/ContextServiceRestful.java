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

import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.workflow.cs.service.CSTableService;
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

@Component
@Path("/dss/workflow")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ContextServiceRestful {

    @Autowired
    private CSTableService csTableService;

    @POST
    @Path("tables")
    public Response tables(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException {
        String userName = SecurityFilter.getLoginUsername(req);
        String contextIDStr = json.get("contextID").getTextValue();
        String nodeName = json.get("nodeName").getTextValue();
        return Message.messageToResponse(Message.ok().data("tables", csTableService.queryTables("default", contextIDStr, nodeName)));
    }


    @POST
    @Path("columns")
    public Response queryTableMeta(@Context HttpServletRequest req, JsonNode json) throws DSSErrorException {
        String userName = SecurityFilter.getLoginUsername(req);
        String contextIDStr = json.get("contextID").getTextValue();
        String contextKeyStr = json.get("contextKey").getTextValue();
        return Message.messageToResponse(Message.ok().data("columns", csTableService.queryTableMeta("default", contextIDStr, contextKeyStr)));
    }

}
