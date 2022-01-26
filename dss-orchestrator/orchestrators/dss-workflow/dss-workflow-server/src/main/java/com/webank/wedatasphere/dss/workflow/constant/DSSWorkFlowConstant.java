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

package com.webank.wedatasphere.dss.workflow.constant;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.common.conf.CommonVars$;


public class DSSWorkFlowConstant {
    public static final CommonVars<String> DSS_EXPORT_ENV = CommonVars.apply("wds.dss.server.export.env", "DEV");
    public static final CommonVars<String> DSS_IMPORT_ENV = CommonVars.apply("wds.dss.server.import.env", "PROD");
    public static final String PUBLISH_FLOW_REPORT_FORMATE = "工作流名:%s,版本号:%s，工作流内容为空,请自行修改或者删除";
    public static final Interner<Long> saveFlowLock = Interners.<Long>newWeakInterner();
    public static final CommonVars CACHE_TIMEOUT = CommonVars$.MODULE$.apply("wds.dss.server.cache.timeout", 1000 * 60 * 60);
    public static final CommonVars PUBLISH_TIMEOUT = CommonVars$.MODULE$.apply("wds.dss.server.publish.timeout", 60 * 10);
    /**
     * 工作流编辑锁超时时间
     */
    public static final CommonVars<Long> DSS_FLOW_EDIT_LOCK_TIMEOUT = CommonVars$.MODULE$.apply("wds.dss.server.flow.edit.lock.timeout", 1000 * 60 * 10L);

    public static final String SPLIT = "_";

    public static final String BDP_USER_TICKET_ID = "bdp-user-ticket-id";
    /**
     * 用户已锁定编辑错误码
     */
    public static final int EDIT_LOCK_ERROR_CODE = 60056;

    public static final String APPCONN_NAME_VISUALIS = CommonVars.apply("appconn.name.visualis", "visualis").getValue();
    public static final String APPCONN_NAME_QUALITIS = CommonVars.apply("appconn.name.qualitis", "qualitis").getValue();

}
