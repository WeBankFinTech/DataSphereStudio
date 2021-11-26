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

package com.webank.wedatasphere.dss.framework.appconn.restful;

import com.webank.wedatasphere.dss.appconn.manager.entity.AppConnInfo;
import com.webank.wedatasphere.dss.appconn.manager.entity.AppInstanceInfo;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnInfoService;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnResourceUploadService;
import com.webank.wedatasphere.linkis.server.Message;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
@Path("/dss/framework/project/appconn")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AppConnManagerRestfulApi {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConnManagerRestfulApi.class);

    @Autowired
    private AppConnInfoService appConnInfoService;
    @Autowired
    private AppConnResourceUploadService appConnResourceUploadService;

    @PostConstruct
    public void init() {
        LOGGER.info("Try to scan AppConn plugins...");
        appConnInfoService.getAppConnInfos().forEach(DSSExceptionUtils.handling(appConnInfo -> {
            LOGGER.info("Try to load or update AppConn {}.", appConnInfo.getAppConnName());
            appConnResourceUploadService.upload(appConnInfo.getAppConnName());
        }));
        LOGGER.info("All AppConn plugins has scanned.");
    }

    @GET
    @Path("listAppConnInfos")
    public Response listAppConnInfos() {
        List<? extends AppConnInfo> appConnInfos = appConnInfoService.getAppConnInfos();
        Message message = Message.ok("Get AppConnInfo list succeed.");
        message.data("appConnInfos", appConnInfos);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("{appConnName}/get")
    public Response get(@PathParam("appConnName") String appConnName) {
        AppConnInfo appConnInfo = appConnInfoService.getAppConnInfo(appConnName);
        Message message = Message.ok("Get AppConnInfo succeed.");
        message.data("appConnInfo", appConnInfo);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("/{appConnName}/getAppInstances")
    public Response getAppInstancesByAppConnInfo(@PathParam("appConnName") String appConnName) {
        List<? extends AppInstanceInfo> appInstanceInfos = appConnInfoService.getAppInstancesByAppConnName(appConnName);
        Message message = Message.ok("Get AppInstance list succeed.");
        message.data("appInstanceInfos", appInstanceInfos);
        return Message.messageToResponse(message);
    }

    @GET
    @Path("/{appConnName}/load")
    public Response load(@PathParam("appConnName") String appConnName) {
        LOGGER.info("Try to load a new AppConn {}.", appConnName);
        try {
            appConnResourceUploadService.upload(appConnName);
        } catch (Exception e) {
            LOGGER.error("Load AppConn " + appConnName + " failed.", e);
            Message message = Message.error("Load AppConn " + appConnName + " failed. Reason: " + ExceptionUtils.getRootCauseMessage(e));
            return Message.messageToResponse(message);
        }
        Message message = Message.ok("Load AppConn " + appConnName + " succeed.");
        return Message.messageToResponse(message);
    }

}
