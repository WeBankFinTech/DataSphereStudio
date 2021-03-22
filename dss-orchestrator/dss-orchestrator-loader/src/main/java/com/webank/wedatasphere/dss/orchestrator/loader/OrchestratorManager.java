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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author allenlliu
 * @date 2020/11/10 17:58
 */
@Component
public class OrchestratorManager {

    private final static Logger logger = LoggerFactory.getLogger(OrchestratorManager.class);

    private Map<String, DSSOrchestrator> cacheDssOrchestrator = new ConcurrentHashMap<>();

    @Autowired
    private DefaultOrchestratorLoader defaultOrchestratorLoader;

    public DSSOrchestrator getOrCreateOrchestrator(String userName,
                                                   String workspaceName,
                                                   String typeName,
                                                   String appConnName,
                                                   List<DSSLabel> dssLabels) {
        String findKey = getCacheKey(userName, workspaceName, typeName, appConnName);
        DSSOrchestrator dssOrchestrator = cacheDssOrchestrator.get(findKey);
        if (null == dssOrchestrator) {
            try {

                dssOrchestrator = defaultOrchestratorLoader.loadOrchestrator(userName, workspaceName, typeName, appConnName, dssLabels);

                cacheDssOrchestrator.put(findKey, dssOrchestrator);
            } catch (AppConnErrorException e) {
                logger.error("OrchestratorManager get DSSOrchestrator exception!", e);
            }
        }
        return dssOrchestrator;
    }

    protected String getCacheKey(String userName, String workspaceName, String typeName, String appConnName) {
        return userName + "_" + workspaceName + "_" + typeName + "_" + appConnName;
    }
}
