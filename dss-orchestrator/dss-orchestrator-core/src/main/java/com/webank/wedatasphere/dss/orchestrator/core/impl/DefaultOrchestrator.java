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

package com.webank.wedatasphere.dss.orchestrator.core.impl;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.scheduler.SchedulerAppConn;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestratorContext;

import java.util.ArrayList;
import java.util.List;


public class DefaultOrchestrator extends AbstractOrchestrator {

    private static volatile DSSOrchestratorContext orchestratorContext;

    private static void initDSSOrchestratorContext() {
        if(orchestratorContext == null) {
            synchronized (DefaultOrchestrator.class) {
                if(orchestratorContext == null) {
                    orchestratorContext = new DSSOrchestratorContextImpl();
                    orchestratorContext.initialize();
                }
            }
        }
    }

    private String name;

    private  List<AppConn> linkedAppConn = new ArrayList<>();

    private List<DSSLabel> labels = new ArrayList<>();

    private AppConn appConn;

    private SchedulerAppConn schedulerAppConn;

    public DefaultOrchestrator(String name) {
        this.name = name;
    }

    public void setAppConn(AppConn appConn){
        this.appConn = appConn;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public AppConn getAppConn() {
        return this.appConn;
    }

    @Override
    public SchedulerAppConn getSchedulerAppConn() {
        return schedulerAppConn;
    }

    public void setSchedulerAppConn(SchedulerAppConn schedulerAppConn) {
        this.schedulerAppConn = schedulerAppConn;
    }

    /**
     * 添加当前编排需要使用到的 AppConn
     * @param appConn 添加当前编排需要使用到的 AppConn
     */
    public void addLinkedAppConn(AppConn appConn) {
        linkedAppConn.add(appConn);
    }

    /**
     * 为编排提供标签说明，如DEV
     * @param dssLabel 标签
     */
    public void addLinkedDssLabels(DSSLabel dssLabel) {
        labels.add(dssLabel);
    }

    @Override
    public List<AppConn> getLinkedAppConn() {
        return linkedAppConn;
    }

    @Override
    protected DSSOrchestratorContext createOrchestratorContext() {
        initDSSOrchestratorContext();
        return orchestratorContext;
    }
}
