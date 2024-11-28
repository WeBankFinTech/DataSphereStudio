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

package com.webank.wedatasphere.dss.datapipe.config;

import org.apache.linkis.common.conf.CommonVars;


public class DataPipeServiceConfiguration {
    public final static CommonVars<String> LINKIS_AUTHOR_USER_TOKEN = CommonVars.apply("wds.linkis.client.api.service.author.user.token","");
    public final static CommonVars<String> LINKIS_ADMIN_USER = CommonVars.apply("wds.linkis.client.api.service.adminuser","ws");

    public final static  CommonVars<Integer>  LINKIS_CONNECTION_TIMEOUT = CommonVars.apply("wds.linkis.flow.connection.timeout",30000);
    public final static CommonVars<String> LINKIS_API_VERSION = CommonVars.apply("wds.linkis.server.version","v1");


}
