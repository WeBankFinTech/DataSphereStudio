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

import org.apache.linkis.common.conf.CommonVars;

public class DSSSenderServiceConf {
    public static final CommonVars<String> ORCHESTRATOR_SERVER_DEV_NAME =
            CommonVars.apply("wds.dss.orc.server.dev.name", "DSS-Framework-Orchestrator-Server-Dev");

    public static final CommonVars<String> DSS_WORKFLOW_APPLICATION_NAME_DEV =
            CommonVars.apply("wds.dss.workflow.name.dev", "dss-workflow-server-dev");

    public static final CommonVars<String> PROJECT_SERVER_NAME =
            CommonVars.apply("wds.dss.project.sever.name", "dss-framework-project-server");

    public static final CommonVars<String> GIT_SERVER_NAME =
            CommonVars.apply("wds.dss.git.sever.name", "dss-framework-git-server");

    public static final CommonVars<String> DSS_SERVER_NAME =
            CommonVars.apply("wds.dss.sever.name.dev", "dss-server-dev");

    public static final CommonVars<String> CURRENT_DSS_SERVER_NAME =
            CommonVars.apply("spring.spring.application.name", "dss-server-dev");

    //以服务合并后方式启动服务需要开启该参数，回退到老方式启动则需要关闭该参数
    public static final CommonVars<Boolean> USE_DSS_SENDER =
            CommonVars.apply("wds.dss.server.use.dssSender", true);

}
