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

package com.webank.wedatasphere.dss.appconn.eventchecker.execution

import com.webank.wedatasphere.dss.appconn.eventchecker.entity.EventChecker
import com.webank.wedatasphere.dss.standard.app.development.listener.common.AbstractRefExecutionAction
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.ExecutionResponseRef

class EventCheckerExecutionAction extends AbstractRefExecutionAction {

  private var response: ExecutionResponseRef = _

  def setExecutionResponseRef(response: ExecutionResponseRef): Unit =
    this.response = response

  def getExecutionResponseRef: ExecutionResponseRef = response

  private[this] var _saveKeyAndValue: String = _

  def saveKeyAndValue: String = _saveKeyAndValue

  def saveKeyAndValue(value: String): Unit = {
    _saveKeyAndValue = value
  }

  private[this] var _eventType: String = "SEND"

  def eventType: String = _eventType

  def eventType(value: String): Unit = {
    _eventType = value
  }

  private[this] var _ec: EventChecker = _

  def ec: EventChecker = _ec

  def setEc(value: EventChecker): Unit = {
    _ec = value
  }
}
