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

package com.webank.wedatasphere.dss.appconn.sendemail;

import com.webank.wedatasphere.dss.appconn.core.ext.DevAppConn;
import com.webank.wedatasphere.dss.appconn.sendemail.conf.SendEmailAppConnInstanceConfiguration;
import com.webank.wedatasphere.dss.standard.app.development.AbstractLabelDevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.app.development.execution.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.execution.RefExecutionService;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: jinyangrao on 2020/11/24
 */
public class SendEmailAppConn implements DevAppConn {

    private AppDesc appDesc;
    private List<AppStandard> appStandardList = new ArrayList<>();

    //防止类加载器错误提前
    private SendEmailAppConnInstanceConfiguration sendEmailAppConnInstanceConfiguration;


    public SendEmailAppConn() {
        init();
    }

    void init() {
        if(appStandardList.isEmpty()) {
            DevelopmentIntegrationStandard standard = new AbstractLabelDevelopmentIntegrationStandard() {
                private List<RefOperationService> refOperationServices = new ArrayList<>();
                @Override
                public void close(){}

                @Override
                public AppDesc getAppDesc() {
                    return appDesc;
                }

                @Override
                public void setAppDesc(AppDesc appDesc) {}

                @Override
                public void init(){
                    refOperationServices.add(new RefExecutionService() {
                        private DevelopmentService developmentService;
                        private RefExecutionOperation refExecutionOperation = new SendEmailRefExecutionOperation();
                        @Override
                        public DevelopmentService getDevelopmentService() {
                            return developmentService;
                        }
                        @Override
                        public void setDevelopmentService(DevelopmentService developmentService) {
                            this.developmentService = developmentService;
                        }

                        @Override
                        public RefExecutionOperation createRefExecutionOperation() {
                            return refExecutionOperation;
                        }
                    });
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
            };
            appStandardList.add(standard);
        }
    }


    @Override
    public DevelopmentIntegrationStandard getDevelopmentIntegrationStandard() {
        if(null != appStandardList && appStandardList.size() > 0) {
            return (DevelopmentIntegrationStandard) appStandardList.get(0);
        }
        return null;
    }



    @Override
    public List<AppStandard> getAppStandards() {
        return appStandardList;
    }

    @Override
    public AppDesc getAppDesc() {
        return appDesc;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }
}
