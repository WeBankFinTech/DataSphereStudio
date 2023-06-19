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

package com.webank.wedatasphere.dss.standard.app.sso.plugin

import com.webank.wedatasphere.dss.standard.app.sso.builder.DssMsgBuilderOperation
import com.webank.wedatasphere.dss.standard.app.sso.builder.DssMsgBuilderOperation.DSSMsg
import com.webank.wedatasphere.dss.standard.app.sso.builder.impl.DSSMsgImpl
import javax.servlet.http.HttpServletRequest


class DssMsgCacheOperationImpl private() extends DssMsgCacheOperation {

  override def setWorkspaceToSession(req: HttpServletRequest, workspaceName: String): Unit =
    getDSSMsgInSession(req) match {
      case dssMsg: DSSMsgImpl => dssMsg.setWorkspaceName(workspaceName)
      case _ =>
    }

  override def getWorkspaceInSession(req: HttpServletRequest): String =
    getDSSMsgInSession(req) match {
      case dssMsg: DSSMsgImpl => dssMsg.getWorkspaceName
      case _ => null
    }

  override def getDSSMsgInSession(request: HttpServletRequest): DssMsgBuilderOperation.DSSMsg =
    request.getSession.getAttribute(DssMsgCacheOperationImpl.DSS_MSG_KEY) match {
      case dssMsg: DSSMsgImpl => dssMsg
      case _ => null
    }

  override def setDSSMsgToSession(dssMsg: DssMsgBuilderOperation.DSSMsg, request: HttpServletRequest): Unit = {
    val newDSSMsg = dssMsg match {
      case dss: DSSMsgImpl =>
        dss.setRedirectUrl(null)
        dss
      case dss: DSSMsg =>
        val dssMsg1 = new DSSMsgImpl
        dssMsg1.setCookies(dss.getCookies)
        dssMsg1.setDSSUrl(dss.getDSSUrl)
        dssMsg1.setWorkspaceName(dss.getWorkspaceName)
        dssMsg1
    }
    request.getSession.setAttribute(DssMsgCacheOperationImpl.DSS_MSG_KEY, newDSSMsg)
  }

  override def setExistsProxyUser(req: HttpServletRequest): Unit = req.getSession.setAttribute("existsDSSProxyUser", true)

  override def isExistsProxyUser(req: HttpServletRequest): Boolean = req.getSession.getAttribute("existsDSSProxyUser") != null
}
object DssMsgCacheOperationImpl {
  private val DSS_MSG_KEY = "dss_msg_key"

  private val dssMsgCacheOperation = new DssMsgCacheOperationImpl

  def getDssMsgCacheOperation: DssMsgCacheOperation = dssMsgCacheOperation
}