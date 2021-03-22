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

package com.webank.wedatasphere.dss.appconn.eventchecker.service;

import com.webank.wedatasphere.dss.appconn.eventchecker.execution.EventCheckerRefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.execution.RefExecutionOperation;
import com.webank.wedatasphere.dss.standard.app.development.execution.RefExecutionService;

/**
 * @author allenlliu
 * @date 2021/1/20 16:30
 */
public class EventCheckerExecuteService  implements RefExecutionService {

    private DevelopmentService developmentService;
    @Override
    public RefExecutionOperation createRefExecutionOperation() {
        EventCheckerRefExecutionOperation eventCheckerRefExecutionOperation = new EventCheckerRefExecutionOperation();
        eventCheckerRefExecutionOperation.setDevelopmentService(developmentService);
        return eventCheckerRefExecutionOperation;
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
