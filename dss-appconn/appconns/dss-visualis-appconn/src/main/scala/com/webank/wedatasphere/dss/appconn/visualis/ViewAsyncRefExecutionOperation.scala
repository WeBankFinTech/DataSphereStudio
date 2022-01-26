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

package com.webank.wedatasphere.dss.appconn.visualis

import com.webank.wedatasphere.dss.appconn.visualis.enums.ModuleEnum
import com.webank.wedatasphere.dss.appconn.visualis.operation.ModuleFactory
import com.webank.wedatasphere.dss.appconn.visualis.operation.impl.ViewOptStrategy
import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisCompletedExecutionResponseRef
import com.webank.wedatasphere.dss.standard.app.development.listener.common._
import com.webank.wedatasphere.dss.standard.app.development.listener.core.{Killable, Procedure}
import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation
import org.apache.linkis.httpclient.request.HttpAction
import org.apache.linkis.httpclient.response.HttpResult

/**
 * Created by allenlliu on 2019/11/11.
 */
class ViewAsyncRefExecutionOperation extends Killable with Procedure {
  protected def execute(requestRef: ExecutionRequestRef, baseUrl: String, ssoRequestOperation: SSORequestOperation[HttpAction, HttpResult]): RefExecutionAction = {
    val asyncExecutionRequestRef = requestRef.asInstanceOf[AsyncExecutionRequestRef]
    val viewOpt: ViewOptStrategy = ModuleFactory.getInstance.crateModule(ModuleEnum.VIEW.getName).asInstanceOf[ViewOptStrategy]
    val execId = viewOpt.submit(asyncExecutionRequestRef, baseUrl, ssoRequestOperation)
    val viewExecutionAction = new ViewExecutionAction
    viewExecutionAction.setExecId(execId)
    viewExecutionAction.setRequestRef(requestRef)
    viewExecutionAction
  }

  protected def state(action: RefExecutionAction, baseUrl: String, ssoRequestOperation: SSORequestOperation[HttpAction, HttpResult]): RefExecutionState = {
    val viewAction = action.asInstanceOf[ViewExecutionAction]
    val requestRef = viewAction.getRequestRef
    val asyncExecutionRequestRef = requestRef.asInstanceOf[AsyncExecutionRequestRef]
    val execId = viewAction.getExecId
    val viewOpt: ViewOptStrategy = ModuleFactory.getInstance.crateModule(ModuleEnum.VIEW.getName).asInstanceOf[ViewOptStrategy]
    val refExecutionState = viewOpt.state(asyncExecutionRequestRef, baseUrl, ssoRequestOperation, execId)
    refExecutionState
  }


  protected def result(action: RefExecutionAction, baseUrl: String, ssoRequestOperation: SSORequestOperation[HttpAction, HttpResult]): CompletedExecutionResponseRef = {
    val viewExecutionAction = action.asInstanceOf[ViewExecutionAction]
    val requestRef = viewExecutionAction.getRequestRef
    val execId = viewExecutionAction.getExecId
    val asyncExecutionRequestRef = requestRef.asInstanceOf[AsyncExecutionRequestRef]

    val viewOpt: ViewOptStrategy = ModuleFactory.getInstance.crateModule(ModuleEnum.VIEW.getName).asInstanceOf[ViewOptStrategy]
    viewOpt.getAsyncResult(asyncExecutionRequestRef, baseUrl, ssoRequestOperation, execId)
    val response: VisualisCompletedExecutionResponseRef = new VisualisCompletedExecutionResponseRef(200)
    response.setIsSucceed(true)
    response
  }

  override def kill(action: RefExecutionAction): Boolean = ???

  override def progress(action: RefExecutionAction): Float = ???

  override def log(action: RefExecutionAction): String = ???

}
