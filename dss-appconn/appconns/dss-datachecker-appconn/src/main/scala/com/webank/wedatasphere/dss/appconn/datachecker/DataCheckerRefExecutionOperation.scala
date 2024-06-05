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


import java.util
import java.util.{Properties, UUID}

import com.webank.wedatasphere.dss.standard.app.development.listener.common._
import com.webank.wedatasphere.dss.standard.app.development.listener.core.{Killable, LongTermRefExecutionOperation, Procedure}
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.ExecutionResponseRef.ExecutionResponseRefBuilder
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.{AsyncExecutionResponseRef, ExecutionResponseRef, RefExecutionRequestRef}
import org.apache.linkis.common.log.LogUtils
import org.apache.linkis.common.utils.{Utils, VariableUtils}

import scala.collection.mutable

class DataCheckerRefExecutionOperation
  extends LongTermRefExecutionOperation[RefExecutionRequestRef.RefExecutionRequestRefImpl] with Killable with Procedure{

  protected def putErrorMsg(errorMsg: String, t: Throwable, action: DataCheckerExecutionAction): DataCheckerExecutionAction = {
    val responseRef = new ExecutionResponseRefBuilder().setErrorMsg(errorMsg).setException(t).error()
    action.setExecutionResponseRef(responseRef)
    action
  }

  override def submit(requestRef: RefExecutionRequestRef.RefExecutionRequestRefImpl): RefExecutionAction = {
    val nodeAction = new DataCheckerExecutionAction()
    nodeAction.setId(UUID.randomUUID().toString)
    import scala.collection.JavaConversions.mapAsScalaMap
    val InstanceConfig = this.service.getAppInstance.getConfig
    val runTimeParams = requestRef.getExecutionRequestRefContext.getRuntimeMap
    val variableParams: mutable.Map[String, Object]= requestRef.getRefJobContent.get("variable"). asInstanceOf[java.util.Map[String,Object]]
    val inputParams = runTimeParams ++ variableParams
    val properties = new Properties()
    InstanceConfig.foreach {
      case (key: String, value: Object) =>
        //避免密码被打印
        properties.put(key, value.toString)
    }
    val tmpProperties = new Properties()
    runTimeParams.foreach(
      record=>
        if (null == record._2) {
          properties.put(record._1, "")
        }else {
          if (record._1.equalsIgnoreCase("job.desc")) {
            val descValue = record._2.asInstanceOf[String]
            var rows=Array.empty[String]
            if (descValue.contains("\n")) {
              rows = descValue.split("\n")
            }else{
              rows= descValue.split(";")
            }
            rows.foreach(row => if (row.contains("=")) {
              val endLocation = row.indexOf("=")
              val rowKey = row.trim.substring(0, endLocation)
              val rowEnd = row.trim.substring(endLocation + 1)
              tmpProperties.put(rowKey, rowEnd)
            })
          } else {
            tmpProperties.put(record._1, record._2)
          }
        }
    )
    tmpProperties.foreach { record =>
      logger.info("request params key : " + record._1 + ",value : " + record._2)
      if (null == record._2) {
        properties.put(record._1, "")
      }
      else {
        if (inputParams.exists(x => x._1.equalsIgnoreCase(VariableUtils.RUN_DATE))) {
          val tmp: util.HashMap[String, Any] = new util.HashMap[String, Any]()
          tmp.put(VariableUtils.RUN_DATE, inputParams.getOrElse(VariableUtils.RUN_DATE, null))
          properties.put(record._1, VariableUtils.replace(record._2.toString, tmp))
        } else {
          properties.put(record._1, VariableUtils.replace(record._2.toString))
        }
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
      case action: DataCheckerExecutionAction =>
        action.getExecutionRequestRefContext.appendLog("DataCheck is running!")
        Utils.tryCatch(action.dc.begineCheck(action))(t => {
          action.setState(RefExecutionState.Failed)
          logger.error("DataChecker run failed for " + t.getMessage, t)
          putErrorMsg("DataChecker run failed! " + t.getMessage, t, action)
        })
        action.getState
      case _ => RefExecutionState.Failed
    }
  }

  override def result(action: RefExecutionAction): ExecutionResponseRef = {
    action match {
      case action: DataCheckerExecutionAction =>
        if (action.getState.equals(RefExecutionState.Success)) {
          new ExecutionResponseRefBuilder().success()
        } else if(action.getExecutionResponseRef != null) action.getExecutionResponseRef
        else new ExecutionResponseRefBuilder().error()
      case _ =>
        new ExecutionResponseRefBuilder().error()
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
      case action: DataCheckerExecutionAction =>
        if (!action.getState.isCompleted) {
          LogUtils.generateInfo("DataChecker is waiting for tables")
        } else {
          LogUtils.generateInfo("DataChecker successfully received info of tables")
        }
      case _ => LogUtils.generateERROR("Error for NodeExecutionAction ")
    }

  }

  override def createAsyncResponseRef(requestRef: RefExecutionRequestRef.RefExecutionRequestRefImpl, action: RefExecutionAction): AsyncExecutionResponseRef = {
    action match {
      case action: DataCheckerExecutionAction =>
        val response = super.createAsyncResponseRef(requestRef, action)
        new AsyncExecutionResponseRef.Builder().setMaxLoopTime(action.dc.maxWaitTime)
        .setAskStatePeriod(action.dc.queryFrequency).setAsyncExecutionResponseRef(response).build()
    }
  }

}
