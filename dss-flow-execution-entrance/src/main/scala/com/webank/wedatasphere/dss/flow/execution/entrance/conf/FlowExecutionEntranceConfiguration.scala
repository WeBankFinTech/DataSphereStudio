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

package com.webank.wedatasphere.dss.flow.execution.entrance.conf

import com.webank.wedatasphere.linkis.common.conf.{ByteType, CommonVars}

/**
 * created by chaogefeng on 2019/11/4 16:35
 * Description:
 */
object FlowExecutionEntranceConfiguration {

  val APPLICATION_SPRING_APPLICATION = CommonVars("wds.linkis.application.spring.name", "cloud-application")

  val PUBLIC_SERVICE_SPRING_APPLICATION = CommonVars("wds.linkis.publicservice.spring.name", "cloud-publicserivce")

  val GATEWAY_SPRING_APPLICATION = CommonVars("wds.linkis.gateway.spring.name", "dataworkcloud-gateway")

  val QUERY_APPLICATION_NAME = CommonVars("wds.linkis.flow.execution.query.name", "cloud-query")

  val SCHEDULER_APPLICATION = CommonVars("wds.linkis.scheduler.spring.name", "dss-server")


  val EVENT_CHECK_MSG_KEY = CommonVars("wds.dss.flow.datacheck.msg.key", "msg.savekey")

  val FLOW_EXECUTION_POOL_SIZE = CommonVars("wds.linkis.flow.execution.pool.size", 1000)

  val NODE_PRINT_FLAG = CommonVars("wds.linkis.flow.node.log.flag", false)

  val NODE_STATUS_POLLER_THREAD_SIZE = CommonVars("wds.dds.flow.node.status.poller.thread.size", 20)

  val NODE_STATUS_POLLER_SCHEDULER_TIME = CommonVars("wds.dds.flow.node.status.poller.scheduler.time", 2)
  val FLOW_EXECUTION_SCHEDULER_POOL_SIZE = CommonVars("wds.linkis.flow.execution.pool.size", 30)


  val COMMAND = "command"

  val JOB_ID = "job.id"

  val FLOW_NAME = "dss.flow.flowid"


  val PROJECT_NAME = "dss.flow.projectname"

  val FLOW_EXEC_ID = "dss.flow.execid"



  val PROXY_USER = "user.to.proxy"


  val FLOW_SUBMIT_USER = "flow.submituser"

  val READ_NODE_TOKEN = "read.nodes"

  val SHARED_NODE_TOKEN = "share.num"



  val SKIP_NODES =   CommonVars("wds.dss.flow.skip.nodes", "workflow.subflow")
  val EMAIL_TYPE = "linkis.appjoint.sendemail"

  val SIGNAL_NODES = CommonVars("wds.dss.flow.signal.nodes", "linkis.appjoint.eventchecker.eventreceiver")

  val PROPS_MAP = "props_map"

  val FLOW_VAR_MAP = "flow_var_map"


  val NODE_CONFIGURATION_KEY = "configuration"

  val PROJECT_RESOURCES = "project_resources"

  val FLOW_RESOURCES = "flow_resources"




}
