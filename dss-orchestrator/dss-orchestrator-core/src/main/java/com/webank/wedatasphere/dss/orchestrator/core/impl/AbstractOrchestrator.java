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

package com.webank.wedatasphere.dss.orchestrator.core.impl;

import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestrator;

import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestratorContext;
import java.util.Arrays;
import java.util.List;


abstract class AbstractOrchestrator implements DSSOrchestrator {

    private volatile DSSOrchestratorContext dssOrchestratorContext;

    @Override
    public List<String> getToolBars() {
        String[] toolNames = {"参数", "资源", "执行", "发布","保存"};
        return Arrays.asList(toolNames);
    }

    protected abstract DSSOrchestratorContext createOrchestratorContext();

    @Override
    public DSSOrchestratorContext getDSSOrchestratorContext() {
        if(dssOrchestratorContext == null) {
            synchronized (this) {
                if(dssOrchestratorContext == null) {
                    dssOrchestratorContext = createOrchestratorContext();
                }
            }
        }
        return dssOrchestratorContext;
    }
}
