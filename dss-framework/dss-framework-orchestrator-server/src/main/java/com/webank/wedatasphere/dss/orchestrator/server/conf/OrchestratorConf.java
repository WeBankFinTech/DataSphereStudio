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

package com.webank.wedatasphere.dss.orchestrator.server.conf;

import org.apache.linkis.common.conf.CommonVars;


public class OrchestratorConf {
    public static final CommonVars<String> DSS_UPLOAD_PATH = CommonVars.apply("wds.dss.file.upload.dir", "/appcom/tmp/uploads");
    public static final CommonVars<Integer> DSS_PUBLISH_MAX_VERSION = CommonVars.apply("wds.dss.publish.max.remain.version", 30);
    public static final CommonVars<String> DSS_CS_CLEAR_ENV = CommonVars.apply("wds.dss.server.cs.clear.env", "DEV");
    public static final  CommonVars<String> DSS_CS_CLEAR_CRON = CommonVars.apply("wds.dss.server.scheduling.clear.cs.cron", "");

}
