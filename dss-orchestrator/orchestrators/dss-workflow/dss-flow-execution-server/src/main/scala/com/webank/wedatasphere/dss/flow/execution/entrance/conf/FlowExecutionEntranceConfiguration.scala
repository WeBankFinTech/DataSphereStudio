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

package com.webank.wedatasphere.dss.flow.execution.entrance.conf

import org.apache.linkis.common.conf.CommonVars


object FlowExecutionEntranceConfiguration {

  val NODE_PRINT_FLAG = CommonVars("wds.linkis.flow.node.log.flag", false)

  val NODE_STATUS_POLLER_THREAD_SIZE = CommonVars("wds.dds.flow.node.status.poller.thread.size", 20)

  val NODE_STATUS_POLLER_SCHEDULER_TIME = CommonVars("wds.dds.flow.node.status.poller.scheduler.time", 5)
  val FLOW_EXECUTION_SCHEDULER_POOL_SIZE = CommonVars("wds.linkis.flow.execution.pool.size", 30)

  val NODE_STATUS_INTERVAL = CommonVars("wds.dds.flow.node.status.poller.interval.time", 3000)

  val COMMAND = "command"

  val JOB_ID = "job.id"

  val FLOW_NAME = "dss.flow.flowid"


  val PROJECT_NAME = "dss.flow.projectname"

  val FLOW_EXEC_ID = "dss.flow.execid"

  val WORKSPACE_ID = "dss.flow.workspace.id"


  val PROXY_USER = "user.to.proxy"


  val FLOW_SUBMIT_USER = "flow.submituser"


  val SKIP_NODES =   CommonVars("wds.dss.flow.skip.nodes", "workflow.subflow")

  val SIGNAL_NODES = CommonVars("wds.dss.flow.signal.nodes", "linkis.appconn.eventchecker.eventreceiver")

  val PROPS_MAP = "props_map"

  val WORKSPACE = "workspace"

  val CONTEXT_ID = "contextID"

  val NODE_NAME = "nodeName"

  val FLOW_VAR_MAP = "flow_var_map"


  val NODE_CONFIGURATION_KEY = "configuration"

  val PROJECT_RESOURCES = "project_resources"

  val FLOW_RESOURCES = "flow_resources"

}
