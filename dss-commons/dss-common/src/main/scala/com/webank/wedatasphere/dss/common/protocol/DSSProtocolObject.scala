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

package com.webank.wedatasphere.dss.common.protocol

import com.webank.wedatasphere.dss.common.label.DSSLabel

case class ResponseCreateOrchestrator(orchestratorId: Long,
                                      orchestratorVersionId: Long)

case class ResponseExportOrchestrator(resourceId: String,
                                      version: String,
                                      orcVersionId: Long)

case class ResponseImportOrchestrator(orcId: Long,version:String)

case class RequestUpdateWorkflow(userName: String,
                                 flowID: Long,
                                 flowName: String,
                                 description: String,
                                 uses: String)

case class RequestDeleteWorkflow(userName: String, flowID: Long)


case class RequestExportWorkflow(userName: String,
                                 flowID: Long,
                                 projectId: Long,
                                 projectName: String,
                                 workspaceStr: String,
                                 dssLabelList: java.util.List[DSSLabel],
                                 exportExternalNodeAppConnResource: Boolean
                                 )

case class RequestReadWorkflowNode(userName: String,
                                   flowID: Long,
                                   projectId: Long,
                                   projectName: String,
                                   workspaceStr: String,
                                   dssLabelList: java.util.List[DSSLabel],
                                   exportExternalNodeAppConnResource: Boolean,
                                   filePath: String
                                  )

case class ResponseExportWorkflow(resourceId: String, version: String, flowID: Long)

case class RequestQueryWorkFlow(userName: String, rootFlowId: Long)

case class ProxyUserCheckRequest(userName: String, proxyUser: String)

case class ResponseProxyUserCheck(canProxy: Boolean,proxyUserList:java.util.List[String])










