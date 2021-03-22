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

package com.webank.wedatasphere.dss.appconn.sendemail.hook

import com.webank.wedatasphere.dss.appconn.sendemail.email.Email
import com.webank.wedatasphere.dss.appconn.sendemail.exception.EmailSendFailedException
import com.webank.wedatasphere.dss.standard.app.development.execution.ExecutionRequestRef
import com.webank.wedatasphere.dss.standard.app.development.execution.common.AsyncExecutionRequestRef
import com.webank.wedatasphere.dss.standard.app.development.execution.core.ExecutionRequestRefContext

/**
  * Created by enjoyyin on 2021/1/26.
  */
abstract class AbstractSendEmailRefExecutionHook extends SendEmailRefExecutionHook {

  protected def getExecutionRequestRefContext(requestRef: ExecutionRequestRef): ExecutionRequestRefContext = requestRef match {
    case async: AsyncExecutionRequestRef => async.getExecutionRequestRefContext
    case _ => throw new EmailSendFailedException(80002, "ExecutionRequestRefContext is empty!")
  }

  override def preSend(requestRef: ExecutionRequestRef, email: Email): Unit = {}
}
