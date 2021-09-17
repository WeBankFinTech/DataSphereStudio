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

package com.webank.wedatasphere.dss.sender.service.conf;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * @author allenlliu
 * @date 2021/6/24 17:23
 */
public class DSSSenderServiceConf {
    public static final CommonVars<String> ORCHESTRATOR_SERVER_DEV_NAME =
            CommonVars.apply("wds.dss.orc.server.dev.name", "DSS-Framework-Orchestrator-Server-Dev");

    public static final CommonVars<String> DSS_WORKFLOW_APPLICATION_NAME_DEV =
            CommonVars.apply("wds.dss.workflow.name.dev", "dss-workflow-server-dev");

    public static final CommonVars<String> PROJECT_SERVER_NAME =
            CommonVars.apply("wds.dss.project.sever.name", "dss-framework-project-server");

}
