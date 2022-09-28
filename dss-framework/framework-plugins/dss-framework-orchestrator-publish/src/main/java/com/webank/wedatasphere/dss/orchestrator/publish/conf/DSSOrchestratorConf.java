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

package com.webank.wedatasphere.dss.orchestrator.publish.conf;

import org.apache.linkis.common.conf.CommonVars;


public class DSSOrchestratorConf {
    public static final CommonVars<String> DSS_EXPORT_ENV = CommonVars.apply("wds.dss.server.export.env", "dev");
    /**
     * 导入之后的工作流直接生效，而不需要发布之后。表明工作流的导入和发布是分开的，要发布的话，还需要手动触发
     */
    public static final CommonVars<Boolean> DSS_IMPORT_VALID_IMMEDIATELY = CommonVars.apply("wds.dss.server.import.valid.immediately", false);
}
