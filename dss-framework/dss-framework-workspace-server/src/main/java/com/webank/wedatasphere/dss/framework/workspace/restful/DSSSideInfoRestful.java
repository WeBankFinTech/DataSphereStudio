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

package com.webank.wedatasphere.dss.framework.workspace.restful;

import com.webank.wedatasphere.dss.framework.workspace.service.DSSSideInfoService;
import com.webank.wedatasphere.dss.framework.workspace.util.RestfulUtils;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * created by cooperyang on 2020/10/26
 * Description:侧边栏的内容展示
 */
@Component
@Path("/dss/framework/workspace/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DSSSideInfoRestful {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSSideInfoRestful.class);

    @Autowired
    private DSSSideInfoService dssSideInfoService;

    @GET
    @Path("getSideInfos")
    public Response getSideInfos(@Context HttpServletRequest request, @QueryParam("workspaceId") Integer workspaceId){
        String username = SecurityFilter.getLoginUsername(request);
        try{
            boolean isEnglish = "en".equals(request.getHeader("Content-language"));
            return RestfulUtils.dealOk("获取侧边栏成功", new Pair<>("presentations", dssSideInfoService.getSidebarVOList(username, workspaceId,isEnglish)));
        }catch(Exception e){
            LOGGER.info("Fail to get sideinfos for user {} in workspace {}",username, workspaceId, e);
            return RestfulUtils.dealError("获取侧边栏失败");
        }
    }

}
