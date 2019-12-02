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

import java.util
import java.util.{Properties, UUID}

import com.webank.wedatasphere.dss.appjoint.execution.common._
import com.webank.wedatasphere.dss.appjoint.execution.core._
import com.webank.wedatasphere.dss.appjoint.schedulis.jobtype.entity.DataChecker
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrl
import com.webank.wedatasphere.dss.appjoint.service.session.Session
import com.webank.wedatasphere.linkis.common.utils.Utils
import org.slf4j.LoggerFactory;

/**
 * Created by allenlliu on 2019/11/11.
 */
class  DataCheckerExecution extends  LongTermNodeExecution  with   AppJointUrl with Killable with Procedure {
  private val logger = LoggerFactory.getLogger(classOf[DataCheckerExecution])
  import scala.collection.JavaConversions.mapAsScalaMap
  var appJointParams : scala.collection.mutable.Map[String,AnyRef]= null


  /**
    * 表示任务能否提交到该AppJoint去执行
    *
    * @param node AppJointNode
    * @return true is ok while false is not
    */
  override def canExecute(node: AppJointNode, context: NodeContext, session: Session): Boolean = node.getNodeType.toLowerCase.contains("datachecker")

  protected def putErrorMsg(errorMsg: String, t: Throwable, action: DataCheckerNodeExecutionAction): DataCheckerNodeExecutionAction = t match {

    case t: Exception =>
      val response = action.response
      response.setErrorMsg(errorMsg)
      response.setException(t)
      response.setIsSucceed(false)
      action
  }

  override def init(params: util.Map[String, AnyRef]): Unit = {
    this.appJointParams = params
  }
  override def submit(node: AppJointNode, context: NodeContext, session:Session): NodeExecutionAction = {
    val nodeAction = new DataCheckerNodeExecutionAction()
    nodeAction.setId(UUID.randomUUID().toString())
    val jobName = node.getName
    val scalaParams: scala.collection.mutable.Map[String,Object] =context.getRuntimeMap
    val properties = new Properties()
    this.appJointParams.foreach{
      case (key: String, value: Object) =>
        logger.info("appjoint params key : "+key+",value : "+value)
        properties.put(key, value.toString)
    }
    scalaParams.foreach { case (key: String, value: Object) =>
      logger.info("request params key : "+key+",value : "+value)
      properties.put(key, value.toString)
    }
    val dc = new DataChecker(jobName,properties,nodeAction)
    dc.run()
    nodeAction.setDc(dc)
    nodeAction
  }

  override def state(action: NodeExecutionAction): NodeExecutionState = {
    action match {
      case action: DataCheckerNodeExecutionAction => {
        if (action.state.isCompleted) return action.state
        action.dc.begineCheck()
        action.state
      }
      case _ => NodeExecutionState.Failed
    }
  }
  private var baseUrl:String =""

  override def getBaseUrl: String = baseUrl

  override def setBaseUrl(basicUrl: String): Unit = {
    this.baseUrl = basicUrl
  }

  override def result(action: NodeExecutionAction, nodeContext: NodeContext): CompletedNodeExecutionResponse = {
    val response = new CompletedNodeExecutionResponse
    action match {
      case action: DataCheckerNodeExecutionAction => {
        if (action.state.equals(NodeExecutionState.Success)) {
          response.setIsSucceed(true)
        } else {
          response.setIsSucceed(false)
        }
        response
      }
      case _  => {
        response.setIsSucceed(false)
        response
      }


    }
  }

  override def kill(action: NodeExecutionAction): Boolean = action match {
    case longTermAction: DataCheckerNodeExecutionAction =>
      getScheduler.removeAsyncResponse(longTermAction)
      true
  }

  override def progress(action: NodeExecutionAction): Float = {
    return 0.5f
  }

  override def log(action: NodeExecutionAction): String = {
    action match {
      case action: DataCheckerNodeExecutionAction => {
        if (!action.state.isCompleted) {
          "DataChecker is waiting for tables"
        } else {
          "DataChecker successfully received info of tables"
        }
      }
      case _ => "Error for NodeExecutionAction "
    }

  }

  override  def createAsyncNodeExecutionResponse(node: AppJointNode, context: NodeContext, action: NodeExecutionAction): AsyncNodeExecutionResponse = {
    action match {
      case action: DataCheckerNodeExecutionAction => {
        val response = new AsyncNodeExecutionResponse
        response.setAction(action)
        response.setAppJointNode(node)
        response.setNodeContext(context)
        response.setMaxLoopTime(action.dc.maxWaitTime)
        response.setAskStatePeriod(action.dc.queryFrequency)
        response
      }
    }
  }
}
