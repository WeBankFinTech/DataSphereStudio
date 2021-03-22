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

package com.webank.wedatapshere.dss.appconn.datachecker.standard;

import com.webank.wedatapshere.dss.appconn.datachecker.service.DataCheckerExecuteService;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.standard.app.development.AbstractLabelDevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * created by cooperyang on 2021/1/12
 * Description:
 */
public class DataCheckerDevelopmentStandard extends AbstractLabelDevelopmentIntegrationStandard {


    private static final Logger LOGGER = LoggerFactory.getLogger(DataCheckerDevelopmentStandard.class);

    private AppConn appConn;

    private  List<RefOperationService> refOperationServices = new ArrayList<>();
    public DataCheckerDevelopmentStandard(AppConn appConn){
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
        LOGGER.info("class DataCheckerDevelopmentStandard init");
        refOperationServices.add(new DataCheckerExecuteService());
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
        return "DataCheckDevelopmentStandard";
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
