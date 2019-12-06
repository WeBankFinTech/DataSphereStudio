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

package com.webank.wedatasphere.dss.appjoint.execution.conf

import com.webank.wedatasphere.linkis.common.conf.{CommonVars, TimeType}

/**
  * created by enjoyyin on 2019/9/29
  * Description:
  */
object NodeExecutionConfiguration {
  val QUERY_APPLICATION_NAME = CommonVars("wds.dss.appjoint.query.service.name", "cloud-query")
  val CALL_BACK_URL = CommonVars("wds.dss.appjoint.callback.url", "/api/rest_j/v1/entrance/callback")

  val ASYNC_NODE_EXECUTION_SCHEDULER_QUEUE_SIZE = CommonVars("wds.dss.appjoint.nodeexecution.scheduler.queue.size", 2000)
  val ASYNC_NODE_EXECUTION_SCHEDULER_THREAD_SIZE = CommonVars("wds.dss.appjoint.nodeexecution.scheduler.thread.max", 20)

  val CALLBACK_NODE_EXECUTION_REFRESH_INTERVAL = CommonVars("wds.dss.appjoint.nodeexecution.callback.refresh.interval", new TimeType("2m"))

  val WORKFLOW_SHARED_NODES_JOBIDS = "workflow.shared.nodes.jobids"
}
