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

package com.webank.wedatasphere.dss.common.protocol.framework

import com.webank.wedatasphere.dss.common.protocol.DSSProtocol


/**
 * created by cooperyang on 2020/11/17
 * Description:
 * DSS框架RPC所需要用到RPC
 */



trait FrameworkProtocol extends DSSProtocol


case class ReleaseOrchestratorRequest(releaseUser:String, orchestratorVersionId:Long, orchestratorId:Long,
                                      dssLabel:String, workspaceName:String, workSpaceStr: String) extends FrameworkProtocol

case class ReleaseOrchestratorResponse(jobId:Long) extends FrameworkProtocol



case class ReleaseOrchestratorStatusRequest(jobId:Long) extends FrameworkProtocol

case class ReleaseOrchestratorStatusResponse(status:String, jobId:Long, errorMsg:String) extends FrameworkProtocol






