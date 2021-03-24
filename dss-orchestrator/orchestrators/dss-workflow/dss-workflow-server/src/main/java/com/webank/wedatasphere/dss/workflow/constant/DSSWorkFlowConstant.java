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

package com.webank.wedatasphere.dss.workflow.constant;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import com.webank.wedatasphere.linkis.common.conf.CommonVars$;

/**
 * Created by v_wbjftang on 2019/6/10.
 */
public class DSSWorkFlowConstant {
    public static final CommonVars<String> DSS_EXPORT_URL = CommonVars.apply("wds.dss.server.export.url", "/appcom/tmp/dss");
    public static final CommonVars<String> DSS_EXPORT_ENV = CommonVars.apply("wds.dss.server.export.env", "DEV");
    public static final String DWS_PROJECT_FIRST_VERSION = "v000001";
    public static final String DWS_PROJECT_FIRST_VERSION_COMMENT = "first version";
    public static final String DWS_PROJECT_SOURCE = "create by user";
    public static final String PROJECT_VERSION_ID = "projectVersionID";
    public static final String PUBLISH_FLOW_REPORT_FORMATE = "工作流名:%s,版本号:%s，工作流内容为空,请自行修改或者删除";
    public static final String EMVEDDEDFLOWID ="\"embeddedFlowId\":" ;
    public static final String VERSION_FORMAT = "%06d";
    public static final String VERSION_PREFIX = "v";
    public static final String WORKSPACE_ID_STR = "workspaceId";
    public static final Interner<Long> saveFlowLock = Interners.<Long>newWeakInterner();
    public static final CommonVars CACHE_TIMEOUT = CommonVars$.MODULE$.apply("wds.dss.server.cache.timeout",1000 * 60 * 60);
    public static final CommonVars PUBLISH_TIMEOUT = CommonVars$.MODULE$.apply("wds.dss.server.publish.timeout",60 * 10);
    public static final CommonVars<String> PROJECT_SERVER_NAME = CommonVars$.MODULE$.apply("wds.dss.server.project.server.name","dss-framework-project-server");
}
