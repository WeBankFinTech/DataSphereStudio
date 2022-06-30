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

package com.webank.wedatasphere.dss.appconn.datachecker

import com.webank.wedatasphere.dss.standard.app.development.listener.common.AbstractRefExecutionAction
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.ExecutionResponseRef

class DataCheckerExecutionAction extends AbstractRefExecutionAction {

  private var response: ExecutionResponseRef = _

  def setExecutionResponseRef(response: ExecutionResponseRef): Unit =
    this.response = response

  def getExecutionResponseRef: ExecutionResponseRef = response

  private[this] var _dc: DataChecker = _

  def dc: DataChecker = _dc

  def setDc(value: DataChecker): Unit = {
    _dc = value
  }

}
