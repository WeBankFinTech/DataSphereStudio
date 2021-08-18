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

package com.webank.wedatasphere.dss.orchestrator.loader.utils;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.loader.OrchestratorManager;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.NoSuchAppInstanceException;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;


@Component
public class OrchestratorLoaderUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrchestratorLoaderUtils.class);
    @Autowired
    private OrchestratorManager orchestratorManager;
    private static OrchestratorLoaderUtils orchestratorLoaderUtils;

    @PostConstruct
    public void init(){
        orchestratorLoaderUtils = this;
        orchestratorLoaderUtils.orchestratorManager=this.orchestratorManager;
    }


    public static Pair<AppInstance,DevelopmentIntegrationStandard> getOrcDevelopStandard(String userName,
                                                                                         String workspaceName,
                                                                                         DSSOrchestratorInfo dssOrchestratorInfo,
                                                                                         List<DSSLabel> dssLabels) throws NoSuchAppInstanceException {
        DSSOrchestrator dssOrchestrator = orchestratorLoaderUtils.orchestratorManager.getOrCreateOrchestrator(userName,
                workspaceName, dssOrchestratorInfo.getType(), dssOrchestratorInfo.getAppConnName(), dssLabels);
        if (null != dssOrchestrator) {
            AppConn orchestratorAppConn = dssOrchestrator.getAppConn();

            if(orchestratorAppConn instanceof OnlyDevelopmentAppConn) {
                DevelopmentIntegrationStandard  developmentIntegrationStandard = ((OnlyDevelopmentAppConn) orchestratorAppConn).getOrCreateDevelopmentStandard();

                //todo labels判别
                List<AppInstance> appInstance = orchestratorAppConn.getAppDesc().getAppInstancesByLabels(dssLabels);
                if (appInstance.size() > 0 && null != developmentIntegrationStandard) {
                   return new Pair(appInstance.get(0),developmentIntegrationStandard);

                }else{
                    return null;
                }
            }
        } else {
            LOGGER.error("Can not get dssOrchestrator from manager");
            return null;
        }

        return null;
    }
}
