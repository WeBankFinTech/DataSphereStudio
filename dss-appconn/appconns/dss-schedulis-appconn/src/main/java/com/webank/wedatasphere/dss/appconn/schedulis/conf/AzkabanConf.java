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

package com.webank.wedatasphere.dss.appconn.schedulis.conf;


import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.common.conf.TimeType;

public class AzkabanConf {

    public static final CommonVars<String> DEFAULT_STORE_PATH = CommonVars.apply("wds.dss.appconn.scheduler.project.store.dir", "/appcom/tmp/wds/dss");
    public static final CommonVars<String> LINKIS_VERSION = CommonVars.apply("wds.dss.appconn.scheduler.linkis.version",  "1.0.0");
    public static final CommonVars<TimeType> REALESE_USER_FRESH_TIME = CommonVars.apply("wds.dss.appconn.scheduler.releaseUsers.fresh.interval",  new TimeType("2m"));

}
