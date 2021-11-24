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
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.ws.rs.core.Response;
import java.util.List;

@RequestMapping(path = "/dss/framework/project/appconn", produces = {"application/json"})
@RestController
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

    @RequestMapping(path ="listAppConnInfos", method = RequestMethod.GET)
    public Message listAppConnInfos() {
        List<? extends AppConnInfo> appConnInfos = appConnInfoService.getAppConnInfos();
        Message message = Message.ok("Get AppConnInfo list succeed.");
        message.data("appConnInfos", appConnInfos);
        return message;
    }

    @RequestMapping(path ="{appConnName}/get", method = RequestMethod.GET)
    public Message get(@PathVariable("appConnName") String appConnName) {
        AppConnInfo appConnInfo = appConnInfoService.getAppConnInfo(appConnName);
        Message message = Message.ok("Get AppConnInfo succeed.");
        message.data("appConnInfo", appConnInfo);
        return message;
    }

    @RequestMapping(path ="{appConnName}/getAppInstances", method = RequestMethod.GET)
    public Message getAppInstancesByAppConnInfo(@PathVariable("appConnName") String appConnName) {
        List<? extends AppInstanceInfo> appInstanceInfos = appConnInfoService.getAppInstancesByAppConnName(appConnName);
        Message message = Message.ok("Get AppInstance list succeed.");
        message.data("appInstanceInfos", appInstanceInfos);
        return message;
    }

    @RequestMapping(path ="{appConnName}/load", method = RequestMethod.GET)
    public Message load(@PathVariable("appConnName") String appConnName) {
        LOGGER.info("Try to load a new AppConn {}.", appConnName);
        try {
            appConnResourceUploadService.upload(appConnName);
        } catch (Exception e) {
            LOGGER.error("Load AppConn " + appConnName + " failed.", e);
            Message message = Message.error("Load AppConn " + appConnName + " failed. Reason: " + ExceptionUtils.getRootCauseMessage(e));
            return message;
        }
        Message message = Message.ok("Load AppConn " + appConnName + " succeed.");
        return message;
    }

}
