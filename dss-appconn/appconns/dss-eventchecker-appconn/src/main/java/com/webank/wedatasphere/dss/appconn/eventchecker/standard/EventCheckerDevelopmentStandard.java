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

package com.webank.wedatasphere.dss.appconn.eventchecker.standard;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.eventchecker.service.EventCheckerExecuteService;
import com.webank.wedatasphere.dss.standard.app.development.AbstractLabelDevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author allenlliu
 * @date 2021/1/20 16:26
 */
public class EventCheckerDevelopmentStandard extends AbstractLabelDevelopmentIntegrationStandard {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventCheckerDevelopmentStandard.class);

    private AppConn appConn;

    private  List<RefOperationService> refOperationServices = new ArrayList<>();
    public EventCheckerDevelopmentStandard(AppConn appConn){
        this.appConn = appConn;
    }
    @Override
    public AppDesc getAppDesc() {
        return appConn != null ? appConn.getAppDesc() : null;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appConn.setAppDesc(appDesc);
    }

    @Override
    public void init() {
        LOGGER.info("class EventCheckerDevelopmentStandard init");
        refOperationServices.add(new EventCheckerExecuteService());
    }


    @Override
    protected List<RefOperationService> getRefOperationService() {
        synchronized (this){
            if (refOperationServices.size() == 0){
                synchronized (this){
                    init();
                }
            }
        }

        return refOperationServices;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public String getStandardName() {
        return "EventCheckDevelopmentStandard";
    }

    @Override
    public int getGrade() {
        return 0;
    }

    @Override
    public boolean isNecessary() {
        return false;
    }
}
