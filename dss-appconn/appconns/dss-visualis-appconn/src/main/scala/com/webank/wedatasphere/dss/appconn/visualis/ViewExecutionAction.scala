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

import com.webank.wedatasphere.dss.standard.app.development.listener.common.{AbstractRefExecutionAction, LongTermRefExecutionAction, RefExecutionState}
import com.webank.wedatasphere.dss.standard.app.development.ref.ExecutionRequestRef

/**
 * Created by allenlliu on 2019/11/12.
 */

class ViewExecutionAction extends AbstractRefExecutionAction with LongTermRefExecutionAction {
  private[this] var _state: RefExecutionState = _
  private var schedulerId: Int = _
  private[this] var _execId: String = _
  private[this] var requestRef: ExecutionRequestRef = _

  def getRequestRef: ExecutionRequestRef = requestRef

  def setRequestRef(value: ExecutionRequestRef): Unit = {
    requestRef = value
  }

  def getExecId: String = _execId

  def setExecId(value: String): Unit = {
    _execId = value
  }


  def state: RefExecutionState = _state

  def setState(value: RefExecutionState): Unit = {
    _state = value
  }



  override def setSchedulerId(schedulerId: Int): Unit = this.schedulerId = schedulerId

  override def getSchedulerId: Int = schedulerId
}
