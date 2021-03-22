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

package com.webank.wedatasphere.dss.appconn.orchestrator.service;

import com.webank.wedatasphere.dss.appconn.orchestrator.operation.OrchestratorFrameworkQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryService;
import com.webank.wedatasphere.dss.standard.app.development.query.RefVisibleOperation;

/**
 * @author allenlliu
 * @date 2020/12/23 14:42
 */
public class OrchestratorQueryService  implements RefQueryService {

    private DevelopmentService developmentService;

    @Override
    public RefQueryOperation getRefQueryOperation() {
        OrchestratorFrameworkQueryOperation  orchestratorFrameworkQueryOperation = new OrchestratorFrameworkQueryOperation();
        orchestratorFrameworkQueryOperation.setDevelopmentService(this.developmentService);
        return orchestratorFrameworkQueryOperation ;
    }

    @Override
    public DevelopmentService getDevelopmentService() {
        return developmentService;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
