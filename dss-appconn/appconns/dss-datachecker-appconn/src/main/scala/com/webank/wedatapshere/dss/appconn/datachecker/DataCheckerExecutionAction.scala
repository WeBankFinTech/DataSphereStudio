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

package com.webank.wedatapshere.dss.appconn.datachecker


import com.webank.wedatasphere.dss.standard.app.development.execution.common.{AbstractRefExecutionAction, LongTermRefExecutionAction, RefExecutionAction, RefExecutionState}

/**
  * Created by allenlliu on 2019/11/12.
  */
class DataCheckerExecutionAction extends AbstractRefExecutionAction with LongTermRefExecutionAction{
  private[this] var _state: RefExecutionState = null
  private var schedulerId: Int = _
  def state: RefExecutionState = _state

  def setState(value: RefExecutionState): Unit = {
    _state = value
  }
  val response = new DataCheckerCompletedExecutionResponseRef(200)
  private[this] var _dc: DataChecker = null

  def dc: DataChecker = _dc

  def setDc(value: DataChecker): Unit = {
    _dc = value
  }

  override def setSchedulerId(schedulerId: Int): Unit = this.schedulerId = schedulerId

  override def getSchedulerId: Int = schedulerId
}
