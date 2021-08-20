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

package com.webank.wedatasphere.dss.framework.workspace.constant;


import com.webank.wedatasphere.linkis.common.conf.CommonVars;


public class ApplicationConf {

    public static final CommonVars<String> FAQ = CommonVars.apply("wds.linkis.application.dws.params","http://127.0.0.1:8088/wiki/scriptis/manual/feature_overview_cn.html");
    public static final CommonVars<String> SCHEDULIS_URL =
            CommonVars.apply("wds.linkis.schedulis.url", "http://127.0.0.1:8088");

    public static final CommonVars<String> HOMEPAGE_MODULE_NAME =
            CommonVars.apply("wds.linkis.special.homepage.module.name", "apiServices");

    public static final CommonVars<String> HOMEPAGE_URL =
            CommonVars.apply("wds.linkis.special.homepage.module.url", "/newHome?workspaceId=");

    public static final CommonVars<String> DSS_ENV_PROD_LABEL =
            CommonVars.apply("wds.dss.env.prod.label", "PROD");

    public static final String SCHEDULER_APP_CONN_NAME = CommonVars.apply("wds.dss.appconn.scheduler.name", "schedulis").getValue();

    public static final String ESB_APPID = CommonVars.apply("wds.dss.esb.appid", "").getValue();

}
