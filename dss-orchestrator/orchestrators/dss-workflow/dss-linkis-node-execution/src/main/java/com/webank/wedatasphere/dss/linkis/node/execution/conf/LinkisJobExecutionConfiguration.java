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

package com.webank.wedatasphere.dss.linkis.node.execution.conf;

import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.common.conf.Configuration;

import java.util.Map;

public class LinkisJobExecutionConfiguration {

    public static final String LINKIS_TYPE = "linkistype";

    public static final String APPCONN = "appconn";

    public static final String DSS_LABELS_KEY = "labels";

    public static final String PROJECT_PREFIX = "project";
    public static final String JOB_PREFIX = "job";

    public static final String FLOW_VARIABLE_PREFIX = "flow.variable.";

    public static final String NODE_CONF_PREFIX = "node.conf.";

    public static final String CONF_STARTUP = "startup";
    public static final String CONF_RUNTIME = "runtime";
    public static final String CONF_SPECIAL = "special";

    public static final String RESOURCES_NAME = ".resources";

    public final static String LINKIS_VERSION_KEY = "linkis.version";

    public static final String LINKIS_SUBMIT_USER = "wds.dss.workflow.submit.user";

    public static final String LINKIS_CONTROL_EMPTY_NODE = "linkis.control.empty";

    public static final String FLOW_CONTEXTID = "wds.linkis.flow.contextID";

    public final static  CommonVars<String>  JOB_DEFAULT_TYPE = CommonVars.apply("wds.linkis.flow.default.job.type","linkis");

    public final static  CommonVars<String>  LINKIS_DEFAULT_TYPE = CommonVars.apply("wds.linkis.flow.default.linkis.type","spark.sql");

    public final static  CommonVars<Integer>  LINKIS_CONNECTION_TIMEOUT = CommonVars.apply("wds.linkis.flow.connection.timeout",300000);

    public final static  CommonVars<Integer>  LINKIS_JOB_REQUEST_STATUS_TIME = CommonVars.apply("wds.linkis.job.status.timeout",10000);

    public final static CommonVars<String> LINKIS_ADMIN_USER = CommonVars.apply("wds.linkis.client.flow.adminuser","ws");


    public final static CommonVars<String> LINKIS_AUTHOR_USER_TOKEN = CommonVars.apply("wds.linkis.client.flow.author.user.token","");

    public final static CommonVars<String> LINKIS_JOB_CREATOR = CommonVars.apply("wds.linkis.flow.job.creator","nodeexecution");

    public final static CommonVars<String> LINKIS_JOB_CREATOR_1_X = CommonVars.apply("wds.linkis.flow.job.creator.v1","Schedulis");

    public final static CommonVars<String> LINKIS_URL = CommonVars.apply("wds.linkis.gateway.url.v0", Configuration.getGateWayURL());

    public final static CommonVars<String> LINKIS_URL_1_X = CommonVars.apply("wds.linkis.gateway.url.v1", Configuration.getGateWayURL());

    public final static CommonVars<String> LINKIS_API_VERSION = CommonVars.apply("wds.linkis.server.version","v1");

    public final static CommonVars<Integer> RESULT_PRINT_SIZE = CommonVars.apply("wds.linkis.result.print.size",10);


    public final static CommonVars<Integer> LOG_SIZE = CommonVars.apply("wds.linkis.log.size",-1);
    public final static CommonVars<Integer> LOG_ARRAY_LEN = CommonVars.apply("wds.linkis.log.array.len",4);

    public final static CommonVars<Integer>  REQUEST_MAX_RETRY_TIME = CommonVars.apply("wds.linkis.log.retry.time",10);

    public final static  CommonVars<Integer>  MAX_HTTP_CONNECTION_COUNT = CommonVars.apply("wds.linkis.job.max.http.connection.count",100);

    //兼容老版本
    public static final CommonVars<String> LINKIS_DEFAULT_VERSION = CommonVars.apply("wds.dss.workflow.execution.linkis.version",  "1.0.0");

    public static final CommonVars<Boolean> LINKIS_DISCOVERY_ENABLE = CommonVars.apply("wds.dss.workflow.execution.linkis.discovery.enable",  false);
    public static final CommonVars<String> EMBEDDED_FLOW_ID = CommonVars.apply("wds.dss.workflow.execution.subflow.flag","embeddedFlowId");
    public static boolean isLinkis1_X(Map<String, String> props) {
        return props.getOrDefault(LinkisJobExecutionConfiguration.LINKIS_VERSION_KEY,"")
                .startsWith("1.");
    }
}
