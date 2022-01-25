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

package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client.conf

import org.apache.linkis.common.conf.CommonVars


/**
 * Created by peacewong on 2019/11/3.
 */
object LinkisJobTypeConf {

  val JOB_TYPE = "type"
  val LINKIS_TYPE = "linkistype"

  val COMMAND = "command"
  val JOB_ID = "task.instance.key.str" // "azkaban.job.id";

  val FLOW_ID = "flow.id" // "azkaban.flow.flowid";

  val PROJECT_ID = "project.name" // "azkaban.flow.projectid";

  val PROJECT_NAME = "project.name" // "azkaban.flow.projectname";

  val FLOW_EXEC_ID = "run.id" // "azkaban.flow.execid";

  val PROXY_USER = "proxy.user"
  val FLOW_SUBMIT_USER = "submit.user" // "azkaban.flow.submituser";

  val READ_NODE_TOKEN = "read.nodes"
  val SHARED_NODE_TOKEN = "share.num"
  val MSG_SAVE_KEY = "msg.savekey"
  val SIGNAL_NODES: CommonVars[String] = CommonVars.apply("wds.dss.flow.signal.nodes", "linkis.appjoint.eventchecker.eventreceiver")
}
