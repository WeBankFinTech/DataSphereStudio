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

package com.webank.wedatasphere.dss.appconn.manager.conf;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;

public class AppConnManagerClientConfiguration {

    public final static CommonVars<String> DSS_APPCONN_CLIENT_TOKEN = CommonVars.apply("wds.dss.appconn.client.user.token","WS-AUTH");
    public final static CommonVars<String> LINKIS_ADMIN_USER = CommonVars.apply("wds.dss.appconn.client.user","ws");



}
