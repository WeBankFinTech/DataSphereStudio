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

package com.webank.wedatasphere.dss.appjoint.schedulis.jobtype.execution

import com.webank.wedatasphere.dss.appjoint.execution.common.{AbstractNodeExecutionAction, CompletedNodeExecutionResponse, LongTermNodeExecutionAction, NodeExecutionState}
import com.webank.wedatasphere.dss.appjoint.schedulis.jobtype.entity.EventChecker

/**
  * Created by allenlliu on 2019/11/12.
  */
class EventCheckerNodeExecutionAction extends  AbstractNodeExecutionAction with LongTermNodeExecutionAction {
  private[this] var _state: NodeExecutionState = _
  private var schedulerId: Int = _

  def state: NodeExecutionState = _state

  def setState(value: NodeExecutionState): Unit = {
    _state = value
  }


  val response = new CompletedNodeExecutionResponse

  private[this] var _saveKeyAndValue: String = null

  def saveKeyAndValue: String = _saveKeyAndValue

  def saveKeyAndValue(value: String): Unit = {
    _saveKeyAndValue = value
  }

  private[this] var _eventType: String = "SEND"

  def eventType: String = _eventType

  def eventType(value: String): Unit = {
    _eventType = value
  }

  private[this] var _ec: EventChecker = null

  def ec: EventChecker = _ec

  def setEc(value: EventChecker): Unit = {
    _ec = value
  }

  override def setSchedulerId(schedulerId: Int): Unit = this.schedulerId = schedulerId

  override def getSchedulerId: Int = schedulerId
}
