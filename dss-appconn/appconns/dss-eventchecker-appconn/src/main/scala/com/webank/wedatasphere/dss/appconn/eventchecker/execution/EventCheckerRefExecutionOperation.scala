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

package com.webank.wedatasphere.dss.appconn.eventchecker.execution




import java.util.{Properties, UUID}

import com.webank.wedatasphere.dss.appconn.eventchecker.EventCheckerCompletedExecutionResponseRef
import com.webank.wedatasphere.dss.appconn.eventchecker.entity.EventChecker
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService
import com.webank.wedatasphere.dss.standard.app.development.execution.{ExecutionLogListener, ExecutionRequestRef, ExecutionResultListener}
import com.webank.wedatasphere.dss.standard.app.development.execution.common.{AsyncExecutionRequestRef, AsyncExecutionResponseRef, CompletedExecutionResponseRef, RefExecutionAction, RefExecutionState}
import com.webank.wedatasphere.dss.standard.app.development.execution.core.{Killable, LongTermRefExecutionOperation, Procedure}
import com.webank.wedatasphere.linkis.common.log.LogUtils
import com.webank.wedatasphere.linkis.common.utils.Utils
import com.webank.wedatasphere.linkis.storage.LineRecord
import org.apache.commons.io.IOUtils
import org.slf4j.LoggerFactory;


/**
  * Created by allenlliu on 2019/11/11.
  */
class EventCheckerRefExecutionOperation  extends LongTermRefExecutionOperation with Killable with Procedure{



  private var service:DevelopmentService = _
  private val logger = LoggerFactory.getLogger(classOf[EventCheckerRefExecutionOperation])




  override def progress(action: RefExecutionAction): Float = {
    //temp  set
    0.5f
  }

  override def log(action: RefExecutionAction): String = {
    action match {
      case action: EventCheckerExecutionAction => {
        if (!action.state.isCompleted) {
          LogUtils.generateInfo("EventChecker is sending or waiting for message")
        } else {
          LogUtils.generateInfo("EventChecker successfully received or send  message")
        }
      }
      case _ => LogUtils.generateERROR("Error NodeExecutionAction for log")
    }
  }

  override def kill(action: RefExecutionAction): Boolean = action match {
    case longTermAction: EventCheckerExecutionAction =>
      longTermAction.setKilledFlag(true)
      longTermAction.setState(RefExecutionState.Killed)
      true
  }

  protected def putErrorMsg(errorMsg: String, t: Throwable, action: EventCheckerExecutionAction): EventCheckerExecutionAction = t match {

    case t: Exception =>
      val response = action.response
      response.setErrorMsg(errorMsg)
      response.setException(t)
      response.setIsSucceed(false)
      action
  }

  override def submit(requestRef: ExecutionRequestRef): RefExecutionAction = {
   val asyncExecutionRequestRef = requestRef.asInstanceOf[AsyncExecutionRequestRef]
    val nodeAction = new EventCheckerExecutionAction()
    nodeAction.setId(UUID.randomUUID().toString())
    import scala.collection.JavaConversions.mapAsScalaMap
    val InstanceConfig = this.service.getAppInstance.getConfig
    val scalaParams: scala.collection.mutable.Map[String, Object] =asyncExecutionRequestRef.getExecutionRequestRefContext().getRuntimeMap()
    val properties = new Properties()
    InstanceConfig.foreach { record =>
      logger.info("request params key : " + record._1 + ",value : " + record._2)
      if(null == record._2){
        properties.put(record._1, "")}
      else {
        properties.put(record._1, record._2.toString)
      }
    }
    scalaParams.foreach { case (key, value) =>
      if (key != null && value != null) properties.put(key, value.toString)
    }
    Utils.tryCatch({
      val ec = new EventChecker(properties, nodeAction)
      ec.run()
      nodeAction.setEc(ec)
    })(t => {
      logger.error("EventChecker run failed for " + t.getMessage, t)
      putErrorMsg("EventChecker run failed!" + t.getMessage, t, nodeAction)
    })

    nodeAction
  }

  override def state(action: RefExecutionAction): RefExecutionState = {
    action match {
      case action: EventCheckerExecutionAction => {
        if (action.state.isCompleted) return action.state
        if (action.eventType.equals("RECEIVE")) {
          Utils.tryCatch(action.ec.receiveMsg())(t => {
            action.setState(RefExecutionState.Failed)
            logger.error("EventChecker run failed for " + t.getMessage, t)
            putErrorMsg("EventChecker run failed!" + t.getMessage, t, action)
            false
          })
        }
        action.state
      }
      case _ => RefExecutionState.Failed
    }
  }

  override def result(action: RefExecutionAction): CompletedExecutionResponseRef = {
    val response = new EventCheckerCompletedExecutionResponseRef(200)
    action match {
      case action: EventCheckerExecutionAction => {
        if (action.state.equals(RefExecutionState.Success)) {
          val resultSetWriter =action.getExecutionRequestRefContext.createTextResultSetWriter()
          var resultStr = "EventChecker runs successfully!"
          if (action.saveKeyAndValue != null) {
            resultStr = action.saveKeyAndValue
            logger.info("EventChecker save receive value: " + resultStr)
          }
          Utils.tryFinally {
            resultSetWriter.addMetaData(null)
            resultSetWriter.addRecord(new LineRecord(resultStr))
          }(IOUtils.closeQuietly(resultSetWriter))
          response.setIsSucceed(true)
        } else {
          response.setException(action.response.getException)
          response.setIsSucceed(false)
        }
        response
      }
      case _ =>
        response.setIsSucceed(false);
        response
    }

  }

  override def createAsyncResponseRef(requestRef: ExecutionRequestRef, action: RefExecutionAction): AsyncExecutionResponseRef = {
    action match {
      case action: EventCheckerExecutionAction => {
        val response = super.createAsyncResponseRef(requestRef,action)
        response.setAction(action)
//        response.setAppJointNode(node)
//        response.setNodeContext(context)
        response.setMaxLoopTime(action.ec.maxWaitTime)
        response.setAskStatePeriod(action.ec.queryFrequency)
        response
      }
    }
  }

  override def setDevelopmentService(service: DevelopmentService): Unit = {
    this.service = service
  }

}
