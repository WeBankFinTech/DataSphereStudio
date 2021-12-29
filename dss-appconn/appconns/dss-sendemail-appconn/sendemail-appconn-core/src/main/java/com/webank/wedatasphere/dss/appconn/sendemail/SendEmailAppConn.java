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

package com.webank.wedatasphere.dss.appconn.sendemail;

import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;
import com.webank.wedatasphere.dss.appconn.core.impl.AbstractAppConn;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefExecutionService;
import com.webank.wedatasphere.dss.standard.app.development.service.RefExecutionService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.standard.OnlyExecutionDevelopmentStandard;

public class SendEmailAppConn extends AbstractAppConn implements OnlyDevelopmentAppConn {

    private DevelopmentIntegrationStandard standard;

    @Override
    protected void initialize() {
        standard = new OnlyExecutionDevelopmentStandard() {
            @Override
            public void close() {
            }

            @Override
            protected RefExecutionService createRefExecutionService() {
                return new AbstractRefExecutionService() {
                    private RefExecutionOperation refExecutionOperation = new SendEmailRefExecutionOperation();

                    @Override
                    public RefExecutionOperation createRefExecutionOperation() {
                        return refExecutionOperation;
                    }
                };
            }

            @Override
            public void init() {

            }
        };
    }

    @Override
    public DevelopmentIntegrationStandard getOrCreateDevelopmentStandard() {
        return standard;
    }
}
