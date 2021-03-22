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

package com.webank.wedatasphere.dss.orchestrator.loader;


import com.webank.wedatasphere.dss.appconn.core.exception.AppConnErrorException;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;

import java.util.List;

public interface OrchestratorLoader {

    /**
     * 用于返回一个指定类型的的Orchestrator
     * @param userName
     * @param workspaceName
     * @param typeName
     * @param appConnName    唯一标识一种类型的AppConn,比如workflowOrchestratorAppConn
     * @return
     */
    DSSOrchestrator loadOrchestrator(String userName,
                                     String workspaceName,
                                     String typeName,
                                     String appConnName,
                                     List<DSSLabel> dssLabels) throws AppConnErrorException;
}
