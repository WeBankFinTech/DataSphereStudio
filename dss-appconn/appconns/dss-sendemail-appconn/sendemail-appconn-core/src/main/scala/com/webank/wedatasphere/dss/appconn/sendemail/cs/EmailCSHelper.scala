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

package com.webank.wedatasphere.dss.appconn.sendemail.cs

import com.webank.wedatasphere.dss.appconn.sendemail.exception.EmailSendFailedException
import com.webank.wedatasphere.dss.standard.app.development.execution.core.ExecutionRequestRefContext
import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.linkis.cs.client.service.LinkisJobDataServiceImpl
import com.webank.wedatasphere.linkis.cs.client.utils.{ContextServiceUtils, SerializeHelper}
import com.webank.wedatasphere.linkis.cs.common.entity.enumeration.{ContextScope, ContextType}
import com.webank.wedatasphere.linkis.cs.common.entity.source.CommonContextKey
import com.webank.wedatasphere.linkis.cs.common.utils.CSCommonUtils
import com.webank.wedatasphere.linkis.server.JSONUtils

import scala.collection.JavaConversions._

/**
  * @author peacewong
  * @date 2020/3/23 17:56
  */
object EmailCSHelper extends Logging{

  /**
    * update by peaceWong form cs to get job ID
    * @param refContext
    * @return
    */
  def getJobIds(refContext: ExecutionRequestRefContext): Array[Long] = {
    val contextIDStr = ContextServiceUtils.getContextIDStrByMap(refContext.getRuntimeMap)
    val nodeIDs = refContext.getRuntimeMap.get("content") match {
      case string: String => JSONUtils.gson.fromJson(string, classOf[java.util.List[String]])
      case list: java.util.List[String] => list
    }
    if (null == nodeIDs || nodeIDs.length < 1){
      throw new EmailSendFailedException(80003 ,"empty result set is not allowed")
    }
    info(s"From cs to getJob ids $nodeIDs.")
    val jobIds = nodeIDs.map(ContextServiceUtils.getNodeNameByNodeID(contextIDStr, _)).map{ nodeName =>
      val contextKey = new CommonContextKey
      contextKey.setContextScope(ContextScope.PUBLIC)
      contextKey.setContextType(ContextType.DATA)
      contextKey.setKey(CSCommonUtils.NODE_PREFIX + nodeName + CSCommonUtils.JOB_ID)
      LinkisJobDataServiceImpl.getInstance().getLinkisJobData(contextIDStr, SerializeHelper.serializeContextKey(contextKey))
    }.map(_.getJobID).toArray
    if (null == jobIds || jobIds.length < 1){
      throw new EmailSendFailedException(80003 ,"empty result set is not allowed")
    }
    info(s"Job IDs is ${jobIds.toList}.")
    jobIds
  }
}
