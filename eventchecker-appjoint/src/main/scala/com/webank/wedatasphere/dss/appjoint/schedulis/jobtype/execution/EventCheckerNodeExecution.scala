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

package com.webank.wedatasphere.dss.appjoint.schedulis.jobtype.execution;

import java.util
import java.util.{Properties, UUID}

import com.webank.wedatasphere.dss.appjoint.execution.common._
import com.webank.wedatasphere.dss.appjoint.execution.core._
import com.webank.wedatasphere.dss.appjoint.schedulis.jobtype.entity.EventChecker
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrl
import com.webank.wedatasphere.dss.appjoint.service.session.Session
import com.webank.wedatasphere.linkis.common.utils.Utils
import com.webank.wedatasphere.linkis.storage.LineRecord
import org.apache.commons.io.IOUtils



/**
 * Created by allenlliu on 2019/11/11.
 */
class  EventCheckerNodeExecution extends  LongTermNodeExecution  with   AppJointUrl with Procedure with Killable{
  import scala.collection.JavaConversions.mapAsScalaMap
  var appjointParams : scala.collection.mutable.Map[String,AnyRef]= null

  /**
    * 表示任务能否提交到该AppJoint去执行
    *
    * @param node AppJointNode
    * @return true is ok while false is not
    */
  override def canExecute(node: AppJointNode, context: NodeContext, session: Session): Boolean = node.getNodeType.toLowerCase.contains("eventchecker")



  override def progress(action: NodeExecutionAction): Float = {
    //temp  set
    0.5f
  }

  override def log(action: NodeExecutionAction): String = {
    action match {
      case action: EventCheckerNodeExecutionAction => {
        if (!action.state.isCompleted) {
          "EventChecker is sending or waiting for message"
        }else{
          "EventChecker successfully received or sent  message"
        }
      }
    }
  }

  override def kill(action: NodeExecutionAction): Boolean = action match {
    case longTermAction: EventCheckerNodeExecutionAction =>
      getScheduler.removeAsyncResponse(longTermAction)
      true
  }

  protected def putErrorMsg(errorMsg: String, t: Throwable, action: EventCheckerNodeExecutionAction): EventCheckerNodeExecutionAction = t match {

    case t: Exception =>
      val response = action.response
      response.setErrorMsg(errorMsg)
      response.setException(t)
      response.setIsSucceed(false)
      action
  }

  override def init(params: util.Map[String, AnyRef]): Unit = {
    this.appjointParams = params
  }

  override def submit(node: AppJointNode, context: NodeContext, session:Session): NodeExecutionAction = {
    val nodeAction = new EventCheckerNodeExecutionAction()
    nodeAction.setId(UUID.randomUUID().toString())
    val jobName = node.getName
    val scalaParams: scala.collection.mutable.Map[String,Object] =context.getRuntimeMap
    val properties = new Properties()
    this.appjointParams.foreach{
      case (key: String, value: Object) => properties.put(key, value.toString)
    }
    scalaParams.foreach { case (key: String, value: Object) =>
      properties.put(key, value.toString)
    }
   val ec = new EventChecker(jobName,properties,nodeAction)
    ec.run()
    nodeAction.setEc(ec)
    nodeAction
  }

  override def state(action: NodeExecutionAction): NodeExecutionState = {
    action match {
      case action: EventCheckerNodeExecutionAction => {
        if (action.state.isCompleted) return action.state
        if(action.eventType.equals("RECEIVE")){
          action.ec.receiveMsg()
        }
        action.state
      }
    }
  }

  override def result(action: NodeExecutionAction, nodeContext: NodeContext): CompletedNodeExecutionResponse = {
    val response = new CompletedNodeExecutionResponse
    action match {
      case action:EventCheckerNodeExecutionAction =>{
        if(action.state.equals(NodeExecutionState.Success)) {
          if(action.saveKeyAndValue != null){
            val resultSetWriter = nodeContext.createTextResultSetWriter()
            Utils.tryFinally {
              resultSetWriter.addMetaData(null)
              resultSetWriter.addRecord(new LineRecord(action.saveKeyAndValue))
            }(Utils.tryQuietly(resultSetWriter.close()))
          }
          response.setIsSucceed(true)
        }else{
          response.setIsSucceed(false)
        }
        response
      }
      case _ =>
        response.setIsSucceed(false);
        response
    }

  }


  private var baseUrl:String =""

  override def getBaseUrl: String = baseUrl

  override def setBaseUrl(basicUrl: String): Unit = {
    this.baseUrl = basicUrl
  }

  override  def createAsyncNodeExecutionResponse(node: AppJointNode, context: NodeContext, action: NodeExecutionAction): AsyncNodeExecutionResponse = {
    action match {
      case action: EventCheckerNodeExecutionAction => {
        val response = new AsyncNodeExecutionResponse
        response.setAction(action)
        response.setAppJointNode(node)
        response.setNodeContext(context)
        response.setMaxLoopTime(action.ec.maxWaitTime)
        response.setAskStatePeriod(action.ec.queryFrequency)
        response
      }
    }
  }
}
