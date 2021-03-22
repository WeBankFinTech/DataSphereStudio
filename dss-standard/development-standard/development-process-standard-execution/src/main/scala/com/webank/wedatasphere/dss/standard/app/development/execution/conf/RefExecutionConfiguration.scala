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

package com.webank.wedatasphere.dss.standard.app.development.execution.conf

import com.webank.wedatasphere.linkis.common.conf.{CommonVars, TimeType}

/**
  * created by cooperyang on 2019/9/29
  * Description:
  */
object RefExecutionConfiguration {

  val JOB_HISTORY_APPLICATION_NAME = CommonVars("wds.dss.appconn.jobhistory.service.name", "linkis-ps-publicservice")
  val CALL_BACK_URL = CommonVars("wds.dss.appconn.ref-execution.callback.url", "/api/rest_j/v1/engineconn/callback")

  val ASYNC_REF_EXECUTION_SCHEDULER_QUEUE_SIZE = CommonVars("wds.dss.appconn.ref-execution.scheduler.queue.size", 2000)
  val ASYNC_REF_EXECUTION_SCHEDULER_THREAD_SIZE = CommonVars("wds.dss.appconn.ref-execution.scheduler.thread.max", 20)

  val CALLBACK_REF_EXECUTION_REFRESH_INTERVAL = CommonVars("wds.dss.appconn.ref-execution.callback.refresh.interval", new TimeType("2m"))

}
