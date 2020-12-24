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

package com.webank.wedatasphere.dss.plugins.airflow.linkis.client.conf;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * Created by peacewong on 2019/11/3.
 */
public class LinkisJobTypeConf {

    public static final String COMMAND = "command";

    public static final String JOB_ID = "task.instance.key.str"; // "azkaban.job.id";

    public static final String FLOW_ID = "flow.id"; // "azkaban.flow.flowid";

    //public static final String FLOW_NAME = "flow.name";

    public static final String PROJECT_ID = "project.name"; // "azkaban.flow.projectid";

    public static final String PROJECT_NAME = "project.name"; // "azkaban.flow.projectname";

    public static final String FLOW_EXEC_ID = "run.id"; // "azkaban.flow.execid";

    public static final String PROXY_USER =  "user.to.proxy";


    public static final String FLOW_SUBMIT_USER = "submit.user"; // "azkaban.flow.submituser";

    public static final String READ_NODE_TOKEN = "read.nodes";

    public static final String SHARED_NODE_TOKEN = "share.num";

    public static final String MSG_SAVE_KEY = "msg.savekey";

    public final static CommonVars<String> SIGNAL_NODES = CommonVars.apply("wds.dss.flow.signal.nodes","linkis.appjoint.eventchecker.eventreceiver");

}
