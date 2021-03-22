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

package com.webank.wedatapshere.dss.appconn.datachecker.service;

import com.webank.wedatapshere.dss.appconn.datachecker.DataCheckerRefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.execution.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.execution.RefExecutionService;

/**
 * @author allenlliu
 * @date 2021/1/20 15:41
 */
public class DataCheckerExecuteService implements RefExecutionService {

    private DevelopmentService developmentService;
    @Override
    public RefExecutionOperation createRefExecutionOperation() {
        DataCheckerRefExecutionOperation dataCheckerRefExecutionOperation = new DataCheckerRefExecutionOperation();
        dataCheckerRefExecutionOperation.setDevelopmentService(developmentService);
        return dataCheckerRefExecutionOperation;
    }

    @Override
    public DevelopmentService getDevelopmentService() {
        return this.developmentService;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
