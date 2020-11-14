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

package com.webank.wedatasphere.dss.linkis.appjoint.entrance.job

import java.util

import com.webank.wedatasphere.dss.appjoint.execution.core.{AppJointNode, CommonAppJointNode}
import com.webank.wedatasphere.dss.linkis.appjoint.entrance.execute.AppJointEntranceEngine
import com.webank.wedatasphere.linkis.common.exception.ErrorException
import com.webank.wedatasphere.linkis.common.log.LogUtils
import com.webank.wedatasphere.linkis.common.utils.Utils
import com.webank.wedatasphere.linkis.entrance.execute.StorePathExecuteRequest
import com.webank.wedatasphere.linkis.entrance.job.EntranceExecutionJob
import com.webank.wedatasphere.linkis.protocol.query.RequestPersistTask
import com.webank.wedatasphere.linkis.rpc.Sender
import com.webank.wedatasphere.linkis.scheduler.executer._
import com.webank.wedatasphere.linkis.scheduler.queue.SchedulerEventState._
import org.apache.commons.lang.StringUtils
/**
  * created by cooperyang on 2019/9/26
  * Description:
  */
class AppJointEntranceJob extends EntranceExecutionJob{
  var node:AppJointNode = _
  var runTimeMap:util.Map[String, Object] = _

  def setNode(node:AppJointNode):Unit = this.node = node
  def getNode:AppJointNode = this.node

  def setRunTimeMap(runTimeMap:util.Map[String, Object]):Unit = this.runTimeMap = runTimeMap
  def getRunTimeMap:util.Map[String, Object] = this.runTimeMap


  override def jobToExecuteRequest(): ExecuteRequest = new ExecuteRequest with StorePathExecuteRequest with AppJointExecuteRequest {


    override val code: String = ""

    val jobContent: util.Map[String, Object] = AppJointEntranceJob.this.getNode match {
      case commonAppJointNode:CommonAppJointNode => commonAppJointNode.getJobContent
      case _ => null
    }
    override val storePath: String = AppJointEntranceJob.this.getTask match{
      case requestPersistTask:RequestPersistTask => requestPersistTask.getResultLocation
      case _ => null
    }
    override val node: AppJointNode = AppJointEntranceJob.this.node
    override val runTimeParams: util.Map[String, Object] = AppJointEntranceJob.this.runTimeMap
  }


  override def run(): Unit = {
    if(!isScheduled) return
    info(s"$getId starts to run")
    getLogListener.foreach(_.onLogUpdate(this, LogUtils.generateInfo(s"$getId starts to execute.")))
    startTime = System.currentTimeMillis
    getExecutor match {
      case appjointEntranceEngine:AppJointEntranceEngine => appjointEntranceEngine.setJob(this)
        appjointEntranceEngine.setInstance(Sender.getThisInstance)
    }
    Utils.tryAndErrorMsg(transition(Running))(s"transition $getId from Scheduler to Running failed.")
    val rs = Utils.tryCatch(getExecutor.execute(jobToExecuteRequest())){
      case e:ErrorException => logger.error(s"execute job $getId failed", e)
        ErrorExecuteResponse(s"execute job $getId failed", e)
      case t:Throwable => logger.error(s"execute job $getId failed", t)
        ErrorExecuteResponse(s"execute job $getId failed", t)
    }
    rs match {
      case r: CompletedExecuteResponse =>
        setResultSize(0)
        transitionCompleted(r)
      case r: IncompleteExecuteResponse =>
        setResultSize(0)
        transitionCompleted(ErrorExecuteResponse(if(StringUtils.isNotEmpty(r.message)) r.message else "incomplete code.", null))
      case r: AsynReturnExecuteResponse =>
        setResultSize(0)
        r.notify(r1 => {
          val realRS = r1 match {
            case r: IncompleteExecuteResponse =>
              ErrorExecuteResponse(if(StringUtils.isNotEmpty(r.message)) r.message else "incomplete code.", null)
            case r: CompletedExecuteResponse => r
          }
          transitionCompleted(realRS)
        })
    }
  }
}
