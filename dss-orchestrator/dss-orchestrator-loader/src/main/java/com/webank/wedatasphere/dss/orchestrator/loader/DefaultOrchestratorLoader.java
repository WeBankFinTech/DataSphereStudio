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

package com.webank.wedatasphere.dss.orchestrator.loader;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.exception.AppConnErrorException;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.DefaultOrchestrator;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author allenlliu
 * @date 2020/11/12 14:33
 */
@Component
public class DefaultOrchestratorLoader implements OrchestratorLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOrchestratorLoader.class);
    @Autowired
    private AppConnService appConnService;

    @Autowired
    private LinkedAppConnResolver linkedAppConnResolver;


    @Override
    public DSSOrchestrator loadOrchestrator(String userName,
                                            String workspaceName,
                                            String typeName,
                                            String appConnName,
                                            List<DSSLabel> dssLabels) throws AppConnErrorException {

        //todo load DSSOrchestatror by type name
        DSSOrchestrator dssOrchestrator = new DefaultOrchestrator();

        //向工作流添加实现了第三级规范的AppConn
        List<AppConn> appConns = linkedAppConnResolver.resolveAppConnByUser(userName, workspaceName, typeName);
        for (AppConn appConn : appConns) {
            appConn.getAppStandards().stream()
                    .filter(appStandard -> appStandard instanceof DevelopmentIntegrationStandard)
                    .findAny()
                    .ifPresent(targetStandard -> dssOrchestrator.addLinkedAppConn(appConn));
        }
        LOGGER.info("Load dss orchestrator:"+appConnName+",typeName:"+typeName);
        AppConn appConn = appConnService.getAppConn(appConnName);
        dssLabels.forEach(dssOrchestrator::addLinkedDssLabels);
        dssOrchestrator.setAppConn(appConn);
        return dssOrchestrator;
    }
}
