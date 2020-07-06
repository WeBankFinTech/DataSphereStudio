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



import com.webank.wedatasphere.dss.server.crumb.CrumbFactory;
import com.webank.wedatasphere.dss.server.crumb.QuerParamsParser;
import com.webank.wedatasphere.dss.server.entity.Crumb;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import scala.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Component
@Path("/dss")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TreeRestfulApi {
    @Autowired
    private CrumbFactory crumbFactory;

    @GET
    @Path("/tree")
    public Response Crumb(@Context HttpServletRequest req) {
        String userName = SecurityFilter.getLoginUsername(req);
        String queryString = req.getQueryString() == null ? "" : req.getQueryString();
        String[] params = new String[]{} ;
        if (!"".equals(queryString)) params = QuerParamsParser.toArray(queryString);
        Enumeration.Value crumbType = QuerParamsParser.getCrumbType(queryString);
        Crumb[] crumbs = crumbFactory.createCrumbs(crumbType, params);
        Object crumbData = crumbFactory.createCrumbData(crumbType, QuerParamsParser.toMap(params),userName);
        return Message.messageToResponse(Message.ok().data("crumbs", crumbs).data("data",crumbData));
    }
}
