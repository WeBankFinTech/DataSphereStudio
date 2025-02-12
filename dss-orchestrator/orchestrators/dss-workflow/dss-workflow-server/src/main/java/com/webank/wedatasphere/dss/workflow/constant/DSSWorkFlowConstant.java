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
import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.common.conf.CommonVars$;


public class DSSWorkFlowConstant {
    public static final CommonVars<String> DSS_EXPORT_ENV = CommonVars.apply("wds.dss.server.export.env", "DEV");
    public static final String PUBLISH_FLOW_REPORT_FORMATE = "工作流名:%s,版本号:%s，工作流内容为空,请自行修改或者删除";
    public static final Interner<Long> saveFlowLock = Interners.<Long>newWeakInterner();
    public static final CommonVars CACHE_TIMEOUT = CommonVars$.MODULE$.apply("wds.dss.server.cache.timeout", 1000 * 60 * 60);
    public static final CommonVars PUBLISH_TIMEOUT = CommonVars$.MODULE$.apply("wds.dss.server.publish.timeout", 60 * 10);
    public static final CommonVars<Integer> NODE_EXPORT_IMPORT_THREAD_NUM = CommonVars.apply("wds.dss.server.workflow.exportImport.thread.num", 50);
    public static final CommonVars<Integer> NODE_EXPORT_IMPORT_TIMEOUT_MINUTES= CommonVars.apply("wds.dss.server.workflow.exportImport.timeout.minutes", 30);
    /**
     * 工作流编辑锁超时时间
     */
    public static final CommonVars<Long> DSS_FLOW_EDIT_LOCK_TIMEOUT = CommonVars$.MODULE$.apply("wds.dss.server.flow.edit.lock.timeout", 1000 * 60 * 10L);

    public static final String SPLIT = "_";

    public static final String BDP_USER_TICKET_ID = DSSCommonConf.DSS_TOKEN_TICKET_KEY.getValue();

    public static final String SCHEDULER_APP_CONN_NAME = "schedulerAppConnName";
    public static final String REF_PROJECT_ID_KEY = "refProjectId";
    public static final String TITLE_KEY = "title";
    public static final String JOBTYPE_KEY = "jobType";

    /**
     * 用户已锁定编辑错误码
     */
    public static final int EDIT_LOCK_ERROR_CODE = 60056;

    /**
     * 发布中的错误码，工程下有其他工作流在发布中
     */
    public static final String PUBLISHING_ERROR_CODE = "-999";

    public static final CommonVars<String> GOTO_SCHEDULER_CENTER_URL = CommonVars.apply("wds.dss.workflow.schedulerCenter.url", "/scheduleCenter");
    /**
     * 仅仅用于兼容老的、已经创建的工作量，用于自动路由到一个默认的调度系统。
     */
    public static final CommonVars<String> DEFAULT_SCHEDULER_APP_CONN = CommonVars.apply("wds.dss.workflow.scheduler.default", "schedulis");
    /**
     * 压缩文件大小限制，默认5GB
     */
    public static final CommonVars<String> DEFAULT_ZIP_FILE_LIMIT = CommonVars.apply("wds.dss.workflow.export.default.zip.limit", "5");

}
