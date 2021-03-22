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

import java.util.{Properties, UUID}

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService
import com.webank.wedatasphere.dss.standard.app.development.execution.ExecutionRequestRef
import com.webank.wedatasphere.dss.standard.app.development.execution.common.{AsyncExecutionRequestRef, AsyncExecutionResponseRef, CompletedExecutionResponseRef, RefExecutionAction, RefExecutionState}
import com.webank.wedatasphere.dss.standard.app.development.execution.core.{Killable, LongTermRefExecutionOperation, Procedure}
import com.webank.wedatasphere.linkis.common.log.LogUtils
import com.webank.wedatasphere.linkis.common.utils.Utils
import org.slf4j.LoggerFactory;

/**
  * Created by allenlliu on 2019/11/11.
  */
class DataCheckerRefExecutionOperation extends LongTermRefExecutionOperation with Killable with Procedure{

  private var service:DevelopmentService = _

  private val logger = LoggerFactory.getLogger(classOf[DataCheckerRefExecutionOperation])






  /**
    * 表示任务能否提交到该AppJoint去执行  *

    * @return true is ok while false is not
    */
//  override def canExecute(node: AppJointNode, context: NodeContext, session: Session): Boolean = node.getNodeType.toLowerCase.contains("datachecker")

  protected def putErrorMsg(errorMsg: String, t: Throwable, action: DataCheckerExecutionAction): DataCheckerExecutionAction = t match {

    case t: Exception =>
      val response = action.response
      response.setErrorMsg(errorMsg)
      response.setException(t)
      response.setIsSucceed(false)
      action
  }

  override def submit(requestRef: ExecutionRequestRef): RefExecutionAction = {
    val asyncExecutionRequestRef = requestRef.asInstanceOf[AsyncExecutionRequestRef]
    val nodeAction = new DataCheckerExecutionAction()
    nodeAction.setId(UUID.randomUUID().toString())
    import scala.collection.JavaConversions.mapAsScalaMap
      val InstanceConfig = this.service.getAppInstance.getConfig
      val scalaParams: scala.collection.mutable.Map[String, Object] = asyncExecutionRequestRef.getExecutionRequestRefContext().getRuntimeMap()
      val properties = new Properties()
      InstanceConfig.foreach {
        case (key: String, value: Object) =>
          //避免密码被打印
          //        logger.info("appjoint params key : "+key+",value : "+value)
          properties.put(key, value.toString)
      }
      scalaParams.foreach { record =>
        logger.info("request params key : " + record._1 + ",value : " + record._2)
        if (null == record._2) {
          properties.put(record._1, "")
        }
        else {
          properties.put(record._1, record._2.toString)
        }
      }
      Utils.tryCatch({
        val dc = new DataChecker(properties, nodeAction)
        dc.run()
        nodeAction.setDc(dc)
      })(t => {
        logger.error("DataChecker run failed for " + t.getMessage, t)
        putErrorMsg("DataChecker run failed! " + t.getMessage, t, nodeAction)
      })
      nodeAction

  }

  override def state(action: RefExecutionAction): RefExecutionState = {
    action match {
      case action: DataCheckerExecutionAction => {
        if (action.state.isCompleted) return action.state
        Utils.tryCatch(action.dc.begineCheck())(t => {
          action.setState(RefExecutionState.Failed)
          logger.error("DataChecker run failed for " + t.getMessage, t)
          putErrorMsg("DataChecker run failed! " + t.getMessage, t, action)
        })
        action.state
      }
      case _ => RefExecutionState.Failed
    }
  }

  override def result(action: RefExecutionAction): CompletedExecutionResponseRef = {
    val response:DataCheckerCompletedExecutionResponseRef = new DataCheckerCompletedExecutionResponseRef(200)
    action match {
      case action: DataCheckerExecutionAction => {
        if (action.state.equals(RefExecutionState.Success)) {
          response.setIsSucceed(true)
        } else {
          response.setErrorMsg(action.response.getErrorMsg)
          response.setIsSucceed(false)
        }
        response
      }
      case _ => {
        response.setIsSucceed(false)
        response
      }


    }
  }

  override def kill(action: RefExecutionAction): Boolean = action match {
    case longTermAction: DataCheckerExecutionAction =>
      longTermAction.setKilledFlag(true)
      longTermAction.setState(RefExecutionState.Killed)
      true
  }

  override def progress(action: RefExecutionAction): Float = {
    //todo complete progress
    0.5f
  }

  override def log(action: RefExecutionAction): String = {
    action match {
      case action: DataCheckerExecutionAction => {
        if (!action.state.isCompleted) {
          LogUtils.generateInfo("DataChecker is waiting for tables")
        } else {
          LogUtils.generateInfo("DataChecker successfully received info of tables")
        }
      }
      case _ => LogUtils.generateERROR("Error for NodeExecutionAction ")
    }

  }

  override def createAsyncResponseRef(requestRef: ExecutionRequestRef, action: RefExecutionAction): AsyncExecutionResponseRef = {
    action match {
      case action: DataCheckerExecutionAction => {
        val response = super.createAsyncResponseRef(requestRef,action)
//        response.setAppJointNode(node)
//        response.setNodeContext(context)
        response.setMaxLoopTime(action.dc.maxWaitTime)
        response.setAskStatePeriod(action.dc.queryFrequency)
        response
      }
    }
  }


  override def setDevelopmentService(service: DevelopmentService): Unit = {
    this.service = service
  }
}
