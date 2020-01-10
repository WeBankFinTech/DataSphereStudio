/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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



import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * Created by peacewong on 2019/9/19.
 */
public class LinkisJobExecutionConfiguration {


    public static final String LINKIS_TYPE = "linkistype";

    public static final String APPJOINT = "appjoint";








    public static final String PROJECT_PREFIX = "project";
    public static final String FLOW_PREFIX = "flow";
    public static final String JOB_PREFIX = "job";

    public static final String FLOW_VARIABLE_PREFIX = "flow.variable.";

    public static final String NODE_CONF_PREFIX = "node.conf.";

    public static final String CONF_STARTUP = "startup";
    public static final String CONF_RUNTIME = "runtime";
    public static final String CONF_SPECIAL = "special";

    public static final String RESOURCES_NAME = ".resources";


    public static final String WORKFLOW_SHARED_NODES_JOBIDS = "workflow.shared.nodes.jobids";

    public static final String LINKIS_SUBMIT_USER = "wds.linkis.schedulis.submit.user";

    public static final String LINKIS_CONTROL_EMPTY_NODE = "linkis.control.empty";

    public static final String LINKIS_DATA_EXCHANGE_NODE = "linkis.data.exchange";

    public final static  CommonVars<String>  JOB_DEFAULT_TYPE = CommonVars.apply("wds.linkis.flow.default.job.type","linkis");

    public final static  CommonVars<String>  LINKIS_DEFAULT_TYPE = CommonVars.apply("wds.linkis.flow.default.linkis.type","spark.sql");


    public final static  CommonVars<Integer>  LINKIS_CONNECTION_TIMEOUT = CommonVars.apply("wds.linkis.flow.connection.timeout",30000);

    public final static  CommonVars<Integer>  LINKIS_JOB_REQUEST_STATUS_TIME = CommonVars.apply("wds.linkis.flow.connection.timeout",3000);

    public final static CommonVars<String> LINKIS_ADMIN_USER = CommonVars.apply("wds.linkis.client.flow.adminuser","ws");





    public final static CommonVars<String> LINKIS_AUTHOR_USER_TOKEN = CommonVars.apply("wds.linkis.client.flow.author.user.token","***REMOVED***");

    public final static CommonVars<String> LINKIS_JOB_CREATOR = CommonVars.apply("wds.linkis.flow.job.creator","nodeexecution");

    public final static CommonVars<String> LINKIS_URL = CommonVars.apply("wds.linkis.gateway.url","wtss");

    public final static CommonVars<String> LINKIS_API_VERSION = CommonVars.apply("wds.linkis.server.version","v1");

    public final static CommonVars<Integer> LINKIS_CACHE_MAX_SIZE = CommonVars.apply("wds.linkis.context.cache.max.size",100000);

    public final static CommonVars<Integer> LINKIS_CACHE_EXPIRE_TIME = CommonVars.apply("wds.linkis.context.cache.expire.time",24);

    public final static CommonVars<Integer> RESULT_PRINT_SIZE = CommonVars.apply("wds.linkis.result.print.size",10);

    public final static CommonVars<String> APPJOINT_CONTEXT_CLASS = CommonVars.apply("wds.dss.appjoint.context","");


    public final static CommonVars<Integer> LOG_SIZE = CommonVars.apply("wds.linkis.log.size",-1);
    public final static CommonVars<Integer> LOG_ARRAY_LEN = CommonVars.apply("wds.linkis.log.array.len",4);

    public final static String SUCCEED= "Succeed";

}
