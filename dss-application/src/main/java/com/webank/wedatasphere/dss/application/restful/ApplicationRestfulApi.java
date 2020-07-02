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

package com.webank.wedatasphere.dss.application.restful;

import com.webank.wedatasphere.dss.application.entity.Application;
import com.webank.wedatasphere.dss.application.entity.DSSUser;
import com.webank.wedatasphere.dss.application.entity.DSSUserVO;
import com.webank.wedatasphere.dss.application.handler.ApplicationHandlerChain;
import com.webank.wedatasphere.dss.application.service.ApplicationService;
import com.webank.wedatasphere.dss.application.service.DSSApplicationUserService;
import com.webank.wedatasphere.dss.application.util.ApplicationUtils;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by chaogefeng on 2019/10/10.
 */
@Component
@Path("/dss")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApplicationRestfulApi {

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private DSSApplicationUserService dataworkisUserService;
    @Autowired
    private ApplicationHandlerChain applicationHandlerChain;

    @GET
    @Path("getBaseInfo")
    public Response getBaseInfo(@Context HttpServletRequest req){
        String username = SecurityFilter.getLoginUsername(req);
        applicationHandlerChain.handle(username);
        List<Application> applicationList = applicationService.listApplications();
        for (Application application : applicationList) {
            String redirectUrl = application.getRedirectUrl();
            if(redirectUrl != null) {
                application.setHomepageUrl(ApplicationUtils.redirectUrlFormat(redirectUrl,application.getHomepageUrl()));
                application.setProjectUrl(ApplicationUtils.redirectUrlFormat(redirectUrl,application.getProjectUrl()));
            }
        }
        DSSUser dssUser = dataworkisUserService.getUserByName(username);
        DSSUserVO dataworkisUserVO = new DSSUserVO();
        dataworkisUserVO.setBasic(dssUser);
        return Message.messageToResponse(Message.ok().data("applications",applicationList).data("userInfo",dataworkisUserVO));
    }
}
