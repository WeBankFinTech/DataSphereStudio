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


object LinkisJobTypeConf {

  val LINKIS_TYPE = "linkistype"
  val RUN_DATE = "run_date"
  val JOB_ID = "task.instance.key.str"

  val JOB_COMMAND = "jobCommand"
  val JOB_PARAMS = "jobParams"
  val JOB_SOURCE = "jobSource"
  val JOB_RESOURCES = "jobResources"
  val JOB_LABELS = "jobLabels"

  val FLOW_RESOURCES = "flowResources"
  val FLOW_PROPERTIES = "flowProperties"


  val PROXY_USER = "proxy.user"
  val FLOW_SUBMIT_USER = "submit.user"

  val LINKIS_VERSION = "linkis.version"

}
