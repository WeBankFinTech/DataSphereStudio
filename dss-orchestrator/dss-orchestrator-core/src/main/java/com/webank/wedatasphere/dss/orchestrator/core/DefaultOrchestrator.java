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

package com.webank.wedatasphere.dss.orchestrator.core;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.core.AbstractOrchestrator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author allenlliu
 * @date 2020/09/27 04:34 PM
 */
public class DefaultOrchestrator extends AbstractOrchestrator {

    private  List<AppConn> linkedAppConn = new ArrayList<>();

    private List<DSSLabel> labels = new ArrayList<>();

    private AppConn appConn;

    public DefaultOrchestrator(){

    }

    @Override
    public void setAppConn(AppConn appConn){
        this.appConn = appConn;
    }

    @Override
    public String getName() {
        return "DefaultOrchestrator";
    }

    @Override
    public AppConn getAppConn() {
        return this.appConn;
    }

    @Override
    public void addLinkedAppConn(AppConn appconn) {
        linkedAppConn.add(appconn);
    }

    @Override
    public void addLinkedDssLabels(DSSLabel dssLabel) {
        labels.add(dssLabel);
    }

    @Override
    public List<AppConn> getLinkedAppConn() {
        return linkedAppConn;
    }

}
