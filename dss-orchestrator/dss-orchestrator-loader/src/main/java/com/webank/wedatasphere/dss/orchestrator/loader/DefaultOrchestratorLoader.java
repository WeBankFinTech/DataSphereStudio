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

package com.webank.wedatasphere.dss.orchestrator.loader;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerAppConn;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestratorContext;
import com.webank.wedatasphere.dss.orchestrator.core.impl.DefaultOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.type.DSSOrchestratorRelation;
import com.webank.wedatasphere.dss.orchestrator.core.type.DSSOrchestratorRelationManager;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class DefaultOrchestratorLoader implements OrchestratorLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOrchestratorLoader.class);
    @Autowired
    private DSSOrchestratorContext dssOrchestratorContext;

    @Autowired
    private LinkedAppConnResolver linkedAppConnResolver;


    @Override
    public DSSOrchestrator loadOrchestrator(String userName,
                                            String workspaceName,
                                            String typeName,
                                            List<DSSLabel> dssLabels) {

        DefaultOrchestrator dssOrchestrator = new DefaultOrchestrator(typeName) {
            @Override
            protected DSSOrchestratorContext createOrchestratorContext() {
                return dssOrchestratorContext;
            }
        };
        DSSOrchestratorRelation relation = DSSOrchestratorRelationManager.getDSSOrchestratorRelationByName(typeName);
        // 添加实现了第三级规范的AppConn
        List<AppConn> appConnList = linkedAppConnResolver.resolveAppConnByUser(userName, workspaceName, typeName);
        appConnList.stream().filter(relation.isLinkedAppConn()).forEach(dssOrchestrator::addLinkedAppConn);
        AppConn appConn = AppConnManager.getAppConnManager().getAppConn(relation.getBindingAppConnName());
        dssOrchestrator.setAppConn(appConn);
        if (appConnList.stream().anyMatch(t -> t instanceof SchedulerAppConn)) {
            List<SchedulerAppConn> schedulerAppConns = AppConnManager.getAppConnManager().listAppConns(SchedulerAppConn.class);
            SchedulerAppConn schedulerAppConn;
            if (StringUtils.isBlank(relation.getBindingSchedulerAppConnName())) {
                schedulerAppConn = schedulerAppConns.get(0);
            } else {
                schedulerAppConn = schedulerAppConns.stream().filter(appConn1 -> appConn1.getAppDesc().getAppName().equals(relation.getBindingSchedulerAppConnName()))
                        .findAny().orElseThrow(() -> new ExternalOperationFailedException(50032, "cannot find the matched SchedulerAppConn with name " + relation.getBindingSchedulerAppConnName()));
            }
            dssOrchestrator.setSchedulerAppConn(schedulerAppConn);
            LOGGER.info("Load dss orchestrator: {}, with the binding AppConn: {} and binding SchedulerAppConn {}.",
                    typeName, relation.getBindingAppConnName(), schedulerAppConn.getAppDesc().getAppName());
        }
        dssLabels.forEach(dssOrchestrator::addLinkedDssLabels);
        return dssOrchestrator;
    }
}
