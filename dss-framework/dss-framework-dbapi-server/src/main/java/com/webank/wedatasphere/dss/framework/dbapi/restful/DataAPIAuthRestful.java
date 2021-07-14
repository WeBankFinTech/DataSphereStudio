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

package com.webank.wedatasphere.dss.framework.dbapi.restful;


import com.webank.wedatasphere.dss.framework.dbapi.entity.DSSDataApiAuth;
import com.webank.wedatasphere.dss.framework.dbapi.service.DSSDataApiAuthService;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.UUID;

/**
 * @Classname DataAPIAuthRestful
 * @Description 数据API授权相关REST服务
 * @Date 2021/7/14 10:44
 * @Created by suyc
 */
@Component
@Path("/dss/framework/dataapi")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DataAPIAuthRestful {
    @Autowired
    private DSSDataApiAuthService dssDataApiAuthService;

    @POST
    @Path("/createDataApiAuth")
    public Response createDataApiAuth(@Context HttpServletRequest request, @RequestBody DSSDataApiAuth dssDataApiAuth) throws ErrorException {
        //String userName = SecurityFilter.getLoginUsername(request);
        String userName ="suyc";
        dssDataApiAuth.setCreateBy(userName);
        dssDataApiAuth.setCreateTime(new Date(System.currentTimeMillis()));

        Long dataApiAuthId = dssDataApiAuthService.createDataApiAuth(dssDataApiAuth);
        return Message.messageToResponse(Message.ok().data("dataApiAuthId", dataApiAuthId));
    }

    @GET
    @Path("/generateToken")
    public Response generateToken(@Context HttpServletRequest request) {
        String token = DigestUtils.md5Hex(UUID.randomUUID().toString());
        return Message.messageToResponse(Message.ok().data("token",token));
    }

}

