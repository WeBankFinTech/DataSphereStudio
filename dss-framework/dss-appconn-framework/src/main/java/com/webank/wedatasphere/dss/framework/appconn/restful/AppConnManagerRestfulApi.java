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

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.manager.conf.AppConnManagerCoreConf;
import com.webank.wedatasphere.dss.appconn.manager.entity.AppConnInfo;
import com.webank.wedatasphere.dss.appconn.manager.entity.AppInstanceInfo;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnInfoService;
import com.webank.wedatasphere.dss.appconn.manager.utils.AppConnManagerUtils;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnQualityChecker;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnResourceUploadService;
import com.webank.wedatasphere.dss.sender.service.conf.DSSSenderServiceConf;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.server.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import static com.webank.wedatasphere.dss.framework.appconn.conf.AppConnConf.APPCONN_UPLOAD_THREAD_NUM;

@RequestMapping(path = "/dss/framework/project/appconn", produces = {"application/json"})
@RestController
public class AppConnManagerRestfulApi {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppConnManagerRestfulApi.class);

    @Autowired
    private AppConnInfoService appConnInfoService;
    @Autowired
    private AppConnResourceUploadService appConnResourceUploadService;
    @Autowired
    private List<AppConnQualityChecker> appConnQualityCheckers;

    private ExecutorService uploadThreadPool = Utils.newFixedThreadPool(APPCONN_UPLOAD_THREAD_NUM.getValue(), "Upload-Appconn-Thread-", false);


    @PostConstruct
    public void init() throws InterruptedException {
        //仅dss-server-dev的其中一个服务需要作为appconn-manager节点上传appconn包，其他服务都是client端
        if (Objects.equals(AppConnManagerCoreConf.IS_APPCONN_MANAGER.getValue(), AppConnManagerCoreConf.hostname)
                && "dss-server-dev".equals(DSSSenderServiceConf.CURRENT_DSS_SERVER_NAME.getValue())) {
            LOGGER.info("First, try to load all AppConn...");
            AppConnManager.getAppConnManager().listAppConns().forEach(appConn -> {
                LOGGER.info("Try to check the quality of AppConn {}.", appConn.getAppDesc().getAppName());
                appConnQualityCheckers.forEach(DSSExceptionUtils.handling(checker -> checker.checkQuality(appConn)));
            });
            LOGGER.info("All AppConn have loaded successfully.");
            LOGGER.info("Last, try to scan AppConn plugins and upload AppConn resources...");
            List<? extends AppConnInfo> uploadList = appConnInfoService.getAppConnInfos();
            CountDownLatch cdl = new CountDownLatch(uploadList.size());
            AtomicInteger failedCnt = new AtomicInteger(0);
            uploadList.forEach(appConnInfo -> {
                uploadThreadPool.submit(() -> {
                    LOGGER.info("Try to scan AppConn {}.", appConnInfo.getAppConnName());
                    try {
                        appConnResourceUploadService.upload(appConnInfo.getAppConnName());
                    } catch (Exception e) {
                        LOGGER.error("Error happened when uploading appconn:{}, error:", appConnInfo.getAppConnName(), e);
                        failedCnt.getAndIncrement();
                    }
                    cdl.countDown();
                });
            });
            cdl.await();
            if (failedCnt.get() > 0) {
                throw new DSSRuntimeException("Error happened when uploading appconn, service startup terminated.");
            }
            LOGGER.info("All AppConn plugins has scanned.");
            uploadThreadPool.shutdown();
        } else {
            LOGGER.info("Not appConn manager, will not scan plugins.");
            AppConnManagerUtils.autoLoadAppConnManager();
        }
    }

    @RequestMapping(path = "listAppConnInfos", method = RequestMethod.GET)
    public Message listAppConnInfos() {
        List<? extends AppConnInfo> appConnInfos = appConnInfoService.getAppConnInfos();
        Message message = Message.ok("Get AppConnInfo list succeed.");
        message.data("appConnInfos", appConnInfos);
        return message;
    }

    @RequestMapping(path = "{appConnName}/get", method = RequestMethod.GET)
    public Message get(@PathVariable("appConnName") String appConnName) {
        LOGGER.info("try to get appconn info:{}.", appConnName);
        AppConnInfo appConnInfo = appConnInfoService.getAppConnInfo(appConnName);
        Message message = Message.ok("Get AppConnInfo succeed.");
        message.data("appConnInfo", appConnInfo);
        return message;
    }

    @RequestMapping(path = "{appConnName}/getAppInstances", method = RequestMethod.GET)
    public Message getAppInstancesByAppConnInfo(@PathVariable("appConnName") String appConnName) {
        LOGGER.debug("try to get instances for appconn: {}.", appConnName);
        List<? extends AppInstanceInfo> appInstanceInfos = appConnInfoService.getAppInstancesByAppConnName(appConnName);
        Message message = Message.ok("Get AppInstance list succeed.");
        message.data("appInstanceInfos", appInstanceInfos);
        return message;
    }

    @RequestMapping(path = "{appConnName}/load", method = RequestMethod.GET)
    public Message load(@PathVariable("appConnName") String appConnName) {
        if (!Objects.equals(AppConnManagerCoreConf.IS_APPCONN_MANAGER.getValue(), AppConnManagerCoreConf.hostname)){
            return Message.error("not appconn manager node,please try again");
        }
        LOGGER.info("Try to reload AppConn {}.", appConnName);
        try {
            LOGGER.info("First, reload AppConn {}.", appConnName);
            AppConnManager.getAppConnManager().reloadAppConn(appConnName);
            AppConn appConn = AppConnManager.getAppConnManager().getAppConn(appConnName);
            LOGGER.info("Second, check the quality of AppConn {}.", appConnName);
            appConnQualityCheckers.forEach(DSSExceptionUtils.handling(checker -> checker.checkQuality(appConn)));
            LOGGER.info("Last, upload AppConn {} resources.", appConnName);
            appConnResourceUploadService.upload(appConnName);
        } catch (Exception e) {
            LOGGER.error("Load AppConn " + appConnName + " failed.", e);
            return Message.error("Load AppConn " + appConnName + " failed. Reason: " + ExceptionUtils.getRootCauseMessage(e));
        }
        return Message.ok("Load AppConn " + appConnName + " succeed.");
    }

}
