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

package com.webank.wedatasphere.dss.appconn.eventchecker.execution

import java.util.{Properties, UUID}

import com.webank.wedatasphere.dss.appconn.eventchecker.entity.EventChecker
import com.webank.wedatasphere.dss.standard.app.development.listener.common._
import com.webank.wedatasphere.dss.standard.app.development.listener.core.{Killable, LongTermRefExecutionOperation, Procedure}
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.ExecutionResponseRef.ExecutionResponseRefBuilder
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.{AsyncExecutionResponseRef, ExecutionResponseRef, RefExecutionRequestRef}
import org.apache.commons.io.IOUtils
import org.apache.linkis.common.log.LogUtils
import org.apache.linkis.common.utils.Utils
import org.apache.linkis.storage.LineRecord


class EventCheckerRefExecutionOperation
  extends LongTermRefExecutionOperation[RefExecutionRequestRef.RefExecutionContextRequestRef] with Killable with Procedure {


  override def progress(action: RefExecutionAction): Float = {
    //temp  set
    0.5f
  }

  override def log(action: RefExecutionAction): String = {
    action match {
      case action: EventCheckerExecutionAction =>
        if (!action.getState.isCompleted) {
          LogUtils.generateInfo("EventChecker is sending or waiting for message")
        } else {
          LogUtils.generateInfo("EventChecker successfully received or send  message")
        }
      case _ => LogUtils.generateERROR("Error NodeExecutionAction for log")
    }
  }

  override def kill(action: RefExecutionAction): Boolean = {
    action match {
      case longTermAction: EventCheckerExecutionAction =>
        longTermAction.setKilledFlag(true)
        longTermAction.setState(RefExecutionState.Killed)
        true
      case _ => {
        logger.error("EventChecker kill failed for error action")
        false
      }
    }

  }

  protected def putErrorMsg(errorMsg: String, t: Throwable, action: EventCheckerExecutionAction): EventCheckerExecutionAction = {
    action.setExecutionResponseRef(new ExecutionResponseRefBuilder().setErrorMsg(errorMsg).setException(t).error())
    action
  }

  override def submit(requestRef: RefExecutionRequestRef.RefExecutionContextRequestRef): RefExecutionAction = {
    requestRef.getExecutionRequestRefContext
    val nodeAction = new EventCheckerExecutionAction()
    nodeAction.setId(UUID.randomUUID().toString)
    import scala.collection.JavaConversions.mapAsScalaMap
    val InstanceConfig = this.service.getAppInstance.getConfig
    val scalaParams: scala.collection.mutable.Map[String, Object] = requestRef.getExecutionRequestRefContext.getRuntimeMap
    val properties = new Properties()
    InstanceConfig.foreach { record =>
      if(null == record._2) {
        properties.put(record._1, "")}
      else {
        properties.put(record._1, record._2.toString)
      }
    }
    scalaParams.foreach { case (key, value) =>
      if (key != null && value != null) properties.put(key, value.toString)
    }
    Utils.tryCatch{
      val ec = new EventChecker(properties, nodeAction)
      ec.run()
      nodeAction.setEc(ec)
    } (t => {
      logger.error("EventChecker run failed for " + t.getMessage, t)
      putErrorMsg("EventChecker run failed!" + t.getMessage, t, nodeAction)
    })
    nodeAction
  }

  override def state(action: RefExecutionAction): RefExecutionState = {
    action match {
      case action: EventCheckerExecutionAction =>{
        action.getExecutionRequestRefContext.appendLog("EventCheck is running!")
        if (action.getState.isCompleted) return action.getState
        if (action.eventType.equals("RECEIVE")) {
          Utils.tryCatch(action.ec.receiveMsg())(t => {
            action.setState(RefExecutionState.Failed)
            logger.error("EventChecker run failed for " + t.getMessage, t)
            putErrorMsg("EventChecker run failed!" + t.getMessage, t, action)
            false
          })
        }
        action.getState
      }
      case _ => {
        logger.error("EventChecker run failed for error action")
        RefExecutionState.Failed
      }
    }
  }

  override def result(action: RefExecutionAction): ExecutionResponseRef = {
    action match {
      case action: EventCheckerExecutionAction =>
        if (action.getState.equals(RefExecutionState.Success)) {
          val resultSetWriter = action.getExecutionRequestRefContext.createTextResultSetWriter()
          var resultStr = "EventChecker runs successfully!"
          if (action.saveKeyAndValue != null) {
            resultStr = action.saveKeyAndValue
            logger.info("EventChecker save receive value: " + resultStr)
          }
          Utils.tryFinally {
            resultSetWriter.addMetaData(null)
            resultSetWriter.addRecord(new LineRecord(resultStr))
          }(IOUtils.closeQuietly(resultSetWriter))
          new ExecutionResponseRefBuilder().success()
        } else if(action.getExecutionResponseRef != null) action.getExecutionResponseRef
        else new ExecutionResponseRefBuilder().error()
      case _ =>
        new ExecutionResponseRefBuilder().error()
    }

  }

  override def createAsyncResponseRef(requestRef: RefExecutionRequestRef.RefExecutionContextRequestRef, action: RefExecutionAction): AsyncExecutionResponseRef = {
    action match {
      case action: EventCheckerExecutionAction =>
        val response = super.createAsyncResponseRef(requestRef, action)
        new AsyncExecutionResponseRef.Builder().setMaxLoopTime(action.ec.maxWaitTime)
          .setAskStatePeriod(action.ec.queryFrequency).setAsyncExecutionResponseRef(response).build()
    }
  }

}
