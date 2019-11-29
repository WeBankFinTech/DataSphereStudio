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

package com.webank.wedatasphere.dss.linkis.appjoint.entrance.execute

import java.util
import java.util.UUID

import com.webank.wedatasphere.dss.appjoint.execution.NodeExecution
import com.webank.wedatasphere.dss.appjoint.execution.async.NodeExecutionResponseListener
import com.webank.wedatasphere.dss.appjoint.execution.common._
import com.webank.wedatasphere.dss.appjoint.execution.core._
import com.webank.wedatasphere.dss.linkis.appjoint.entrance.appjoint.AppJointManager
import com.webank.wedatasphere.dss.linkis.appjoint.entrance.conf.AppJointConst
import com.webank.wedatasphere.dss.linkis.appjoint.entrance.exception.OperateNotSupportedException
import com.webank.wedatasphere.dss.linkis.appjoint.entrance.job.AppJointExecuteRequest
import com.webank.wedatasphere.linkis.common.exception.ErrorException
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import com.webank.wedatasphere.linkis.entrance.execute.{EngineExecuteAsynReturn, EntranceEngine, EntranceJob}
import com.webank.wedatasphere.linkis.protocol.engine.{JobProgressInfo, RequestTask}
import com.webank.wedatasphere.linkis.protocol.query.RequestPersistTask
import com.webank.wedatasphere.linkis.scheduler.executer._
import com.webank.wedatasphere.linkis.scheduler.queue.Job
import org.apache.commons.lang.StringUtils

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._
/**
  * created by chaogefeng on 2019/9/26 20:23
  * Description:
  */
class AppJointEntranceEngine(properties: util.Map[String, Any])
  extends EntranceEngine(id = 0) with SingleTaskOperateSupport with SingleTaskInfoSupport  {

  private val defaultProgress = 0.0f
  private val defaultLog = ""


  private var nodeExecution:NodeExecution = _

  private var appJointAction:NodeExecutionAction = _


  private var response:AppJointEntranceAsyncExecuteResponse = _

  private var job:Job = _


  def setJob(job: Job):Unit = this.job = job
  def getJob:Job = this.job

  def setResponse(response:AppJointEntranceAsyncExecuteResponse):Unit = this.response = response
  def getResponse:AppJointEntranceAsyncExecuteResponse = this.response



  /**
    * 设置一个tag是用一个唯一记号标识一个AppJointEntranceEngine，由于每一个job都会对应一个AppJointEntranceEngine
    * 所以在callback的时候带上这个
    */
  private val tag = UUID.randomUUID().toString

  override protected def callExecute(request: RequestTask): EngineExecuteAsynReturn = null

  def getTag:String = this.tag

  override def kill(): Boolean = {
    nodeExecution match {
      case killable:Killable => if (appJointAction != null) killable.kill(appJointAction) else false
      case _ => logger.warn(s"${nodeExecution.getClass.getSimpleName} cannot be killed")
        throw OperateNotSupportedException(s"${nodeExecution.getClass.getSimpleName} cannot be killed")
    }
  }

  override def pause(): Boolean = {
    throw OperateNotSupportedException(s"${nodeExecution.getClass.getSimpleName} cannot be paused")
  }

  override def resume(): Boolean = {
    throw OperateNotSupportedException(s"${nodeExecution.getClass.getSimpleName} cannot be resumed")
  }


  override def progress(): Float = {
    nodeExecution match {
      case procedure:Procedure => if (appJointAction != null) procedure.progress(appJointAction) else defaultProgress
      case _ => logger.debug(s"${nodeExecution.getClass.getSimpleName} cannot get progress")
        defaultProgress
    }
  }

  override def getProgressInfo: Array[JobProgressInfo] = Array.empty

  override def log(): String = {
    nodeExecution match {
      case procedure:Procedure => if (appJointAction != null) procedure.log(appJointAction) else defaultLog
      case _ => logger.debug(s"${nodeExecution.getClass.getSimpleName} cannot get log")
        defaultLog
    }
  }

  def generateContext(node: AppJointNode, runTimeMap:util.Map[String, Object]):NodeContext = {
    node match {
      case commonNode:CommonAppJointNode => val nodeContext = new AppJointNodeContextImpl()
        Utils.tryCatch{
          nodeContext.setAppJointNode(node)
          nodeContext.setJob(getJob)
          nodeContext.setRuntimeMap(runTimeMap)
          nodeContext.setStorePath(job.asInstanceOf[EntranceJob].getTask.asInstanceOf[RequestPersistTask].getResultLocation)
          nodeContext.setUser(runTimeMap.get("user").asInstanceOf[String])
          nodeContext.setStorePath(runTimeMap.get("storePath").asInstanceOf[String])
          if (runTimeMap.containsKey(AppJointConst.WORKFLOW_SHARED_NODES_JOBIDS)){
            nodeContext.setIsReadNode(true)
            runTimeMap.get(AppJointConst.WORKFLOW_SHARED_NODES_JOBIDS) match {
              case ids:util.Map[String, String] => val jobIds = new ArrayBuffer[Long]()
                ids foreach {
                  case (k ,v) => Utils.tryAndWarnMsg(jobIds += v.toLong)(s"$v is not a long type")
                }
                nodeContext.setJobIdsOfShareNode(jobIds.toArray)
              case _ =>
            }
          }else{
            nodeContext.setIsReadNode(false)
          }
        }{
          case e:Exception => logger.warn("创建nodeContext时出现异常,警告信息", e)
        }
        nodeContext
      case _ => logger.warn(s"$node is not a CommonAppJointNode")
        null
    }
  }


  override def close(): Unit = {}

  override def execute(executeRequest: ExecuteRequest): ExecuteResponse = executeRequest match {
    case appJointExecuteRequest:AppJointExecuteRequest =>
      val node = appJointExecuteRequest.node
      val runTimeMap = appJointExecuteRequest.runTimeParams
      val nodeContext = generateContext(node, runTimeMap)
      val nodeType = nodeContext.getAppJointNode.getNodeType
      val realAppJointType = if (nodeType.contains(".")) nodeType.substring(0, nodeType.indexOf(".")) else nodeType
      val appJoint = AppJointManager.getAppJoint(realAppJointType)
      val user = if (null != runTimeMap.get("user")) runTimeMap.get("user").toString else null
      val session = if (StringUtils.isNotEmpty(user)){
        if (appJoint.getSecurityService != null) appJoint.getSecurityService.login(user) else null
      } else null
      this.nodeExecution = appJoint.getNodeExecution
      this.nodeExecution match{
        case longTermNodeExecution:LongTermNodeExecution => longTermNodeExecution.setScheduler(AppJointManager.getScheduler)
        case _ =>
      }
      if (!nodeExecution.canExecute(node, nodeContext, session)) throw new ErrorException(70058, s"appJoint ${appJoint.getAppJointName} can not execute $appJointExecuteRequest.")
      this.nodeExecution.execute(node, nodeContext, session) match {
        case completedResponse:CompletedNodeExecutionResponse =>
          if (completedResponse.isSucceed) SuccessExecuteResponse()
          else {
            logger.error(s"Failed Reason is ${completedResponse.getErrorMsg}", completedResponse.getException)
            ErrorExecuteResponse(completedResponse.getErrorMsg, completedResponse.getException)
          }
        case asyncAppJointResponse:AsyncNodeExecutionResponse =>
          appJointAction = asyncAppJointResponse.getAction
          response = new AppJointEntranceAsyncExecuteResponse
          asyncAppJointResponse.addListener(new NodeExecutionResponseListener{
            override def onNodeExecutionCompleted(resp: CompletedNodeExecutionResponse): Unit = response.notifyStatus(resp)
          })
          response
        case _ =>
          logger.warn(s"Can not solve this type of response $response")
          ErrorExecuteResponse(s"Can not solve this type of response $response",
            new ErrorException(80056, s"Can not solve this type of response $response"))
      }
    case _ =>
      ErrorExecuteResponse(s"cannot do this executeRequest $executeRequest",
        new ErrorException(80056, s"cannot do this executeRequest $executeRequest"))
  }
}

case class AppJointEntranceExecuteException(errMsg:String) extends ErrorException(70046, errMsg)

class AppJointEntranceAsyncExecuteResponse extends AsynReturnExecuteResponse with Logging{

  var rs:ExecuteResponse => Unit = _

  def notifyStatus(response: CompletedNodeExecutionResponse):Unit = {
    val executeResponse = if(response.isSucceed) SuccessExecuteResponse()
    else ErrorExecuteResponse(response.getErrorMsg, response.getException)
    if (rs == null) this synchronized(while(rs == null) this.wait(1000))
    rs(executeResponse)
  }

  override def notify(rs: ExecuteResponse => Unit): Unit = {
    this.rs = rs
    this synchronized notifyAll()
  }
}


