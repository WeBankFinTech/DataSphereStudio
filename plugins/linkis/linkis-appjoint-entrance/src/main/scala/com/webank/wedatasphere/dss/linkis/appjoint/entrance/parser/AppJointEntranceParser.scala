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

package com.webank.wedatasphere.dss.linkis.appjoint.entrance.parser

import java.util

import com.webank.wedatasphere.dss.appjoint.execution.core.CommonAppJointNode
import com.webank.wedatasphere.dss.linkis.appjoint.entrance.conf.AppJointConst
import com.webank.wedatasphere.dss.linkis.appjoint.entrance.job.AppJointEntranceJob
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import com.webank.wedatasphere.linkis.entrance.parser.CommonEntranceParser
import com.webank.wedatasphere.linkis.protocol.query.RequestPersistTask
import com.webank.wedatasphere.linkis.protocol.task.Task
import com.webank.wedatasphere.linkis.protocol.utils.TaskUtils
import com.webank.wedatasphere.linkis.scheduler.queue.Job

/**
  * created by cooperyang on 2019/9/26
  * Description:
  */

class AppJointEntranceParser extends CommonEntranceParser with Logging{

  private val PROJECT_NAME_STR = "projectName"

  private val FLOW_NAME_STR = "flowName"

  private val NODE_NAME_STR = "nodeName"

  private val TYPE_NAME_STR = "type"


  override def parseToJob(task: Task): Job = {
    val job = new AppJointEntranceJob
    task match{
      case requestPersistTask:RequestPersistTask =>
        job.setTask(task)
        job.setUser(requestPersistTask.getUmUser)
        job.setCreator(requestPersistTask.getRequestApplicationName)
        job.setParams(requestPersistTask.getParams.asInstanceOf[util.Map[String, Any]])
        val projectName = Utils.tryCatch{
          requestPersistTask.getSource.get(PROJECT_NAME_STR)
        }{
          case e:Exception => ""
        }
        val flowName = Utils.tryCatch{
          requestPersistTask.getSource.get(FLOW_NAME_STR)
        }{
          case e:Exception => ""
        }
        val nodeName = Utils.tryCatch{
          requestPersistTask.getSource.get(NODE_NAME_STR)
        }{
          case e:Exception => ""
        }
        val _type = Utils.tryCatch{
          requestPersistTask.getRunType
        }{
          case e:Exception => ""
        }
        val executionCode = requestPersistTask.getExecutionCode
        val jobContent = AppJointConst.gson.fromJson(executionCode, classOf[util.Map[String,Object ]])
        val node = new CommonAppJointNode()
        node.setProjectName(projectName)
        node.setFlowName(flowName)
        node.setNodeType(_type)
        node.setJobContent(jobContent)
        job.setNode(node)
        val um_user = requestPersistTask.getUmUser
        val runTimeMap = TaskUtils.getRuntimeMap(requestPersistTask.getParams.asInstanceOf[util.Map[String, Any]])
        runTimeMap.put("user", um_user)
        runTimeMap.put("storePath", requestPersistTask.getResultLocation)
        job.setRunTimeMap(runTimeMap.asInstanceOf[java.util.Map[String, Object]])
        job.setEntranceListenerBus(getEntranceContext.getOrCreateEventListenerBus)
        job.setListenerEventBus(null)
        job.setProgress(0f)
    }
    job
  }
}
