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

import com.webank.wedatasphere.dss.appconn.sendemail.service.EmailExecutionService;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.*;
import com.webank.wedatasphere.dss.standard.app.development.standard.OnlyExecutionDevelopmentStandard;

/**
 * @author allenlliu
 * @date 2021/10/27 11:34
 */
public class EmailDevelopmentIntegrationStandard extends OnlyExecutionDevelopmentStandard {

    @Override
    public void close() {
    }

    @Override
    protected RefExecutionService createRefExecutionService() {
        return new EmailExecutionService();
    }

    @Override
    public void init() {

    }

}
