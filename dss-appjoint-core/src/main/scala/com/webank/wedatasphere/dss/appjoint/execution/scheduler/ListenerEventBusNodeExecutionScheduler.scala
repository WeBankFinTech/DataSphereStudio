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

package com.webank.wedatasphere.dss.appjoint.execution.scheduler

import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException
import com.webank.wedatasphere.dss.appjoint.execution.common.{AsyncNodeExecutionResponse, CompletedNodeExecutionResponse, LongTermNodeExecutionAction}
import com.webank.wedatasphere.dss.appjoint.execution.conf.NodeExecutionConfiguration._
import com.webank.wedatasphere.linkis.common.collection.BlockingLoopArray
import com.webank.wedatasphere.linkis.common.listener.ListenerEventBus
import com.webank.wedatasphere.linkis.common.utils.{ByteTimeUtils, Utils}
import org.apache.commons.lang.time.DateFormatUtils

import scala.collection.JavaConversions._

/**
  * Created by enjoyyin on 2019/11/13.
  */
class ListenerEventBusNodeExecutionScheduler(eventQueueCapacity: Int, name: String)(listenerConsumerThreadSize: Int)
  extends LongTermNodeExecutionScheduler {

  private val listenerEventBus = new ListenerEventBus[AsyncNodeExecutionSchedulerListener, AsyncNodeExecutionResponseEvent](eventQueueCapacity, name)(listenerConsumerThreadSize) {
    override protected val dropEvent: DropEvent = new DropEvent {
      override def onDropEvent(event: AsyncNodeExecutionResponseEvent): Unit = throw new AppJointErrorException(95536, "LongTermNodeExecutionScheduler is full, please ask admin for help!")
      override def onBusStopped(event: AsyncNodeExecutionResponseEvent): Unit = throw new AppJointErrorException(95536, "LongTermNodeExecutionScheduler is stopped, please ask admin for help!")
    }
    override def doPostEvent(listener: AsyncNodeExecutionSchedulerListener, event: AsyncNodeExecutionResponseEvent): Unit = {
      listener.onEvent(event)
    }
  }

  def this() = {
    this(ASYNC_NODE_EXECUTION_SCHEDULER_QUEUE_SIZE.getValue, "Async-NodeExecution-Scheduler")(ASYNC_NODE_EXECUTION_SCHEDULER_THREAD_SIZE.getValue)
    getAsyncNodeExecutionSchedulerListeners.foreach(listenerEventBus.addListener)
  }

  private val eventQueue = {
    val ru = scala.reflect.runtime.universe
    val classMirror = ru.runtimeMirror(getClass.getClassLoader)
    val listenerEventBusClass = classMirror.reflect(listenerEventBus)
    val field1 = ru.typeOf[ListenerEventBus[_, _]].decl(ru.TermName("eventQueue")).asMethod
    val result = listenerEventBusClass.reflectMethod(field1)
    result() match {
      case queue: BlockingLoopArray[AsyncNodeExecutionResponseEvent] => queue
    }
  }

  protected def getAsyncNodeExecutionSchedulerListeners: Array[AsyncNodeExecutionSchedulerListener] = {
    Array(new AsyncNodeExecutionSchedulerListener() {
      override def onEvent(event: AsyncNodeExecutionResponseEvent): Unit = if(!event.getResponse.isCompleted) {
        val response = event.getResponse
        if(response.getMaxLoopTime > 0 && System.currentTimeMillis - response.getStartTime >= response.getMaxLoopTime) {
          onEventError(event, new AppJointErrorException(75533, s"AppJointNode Execution is overtime! StartTime is ${DateFormatUtils.format(response.getStartTime, "yyyy-MM-dd HH:mm:ss")}, maxWaitTime is " +
            ByteTimeUtils.msDurationToString(response.getMaxLoopTime)))
          return
        }
        val period = System.currentTimeMillis() - event.getLastAskTime
        if(period < response.getAskStatePeriod) {
          if(period < 10) Utils.sleepQuietly(100)
          if(!response.isCompleted) addEvent(event)
          return
        }
        val state = response.getNodeExecution.state(response.getAction)
        if(state.isCompleted) {
          val resultResponse = response.getNodeExecution.result(response.getAction, response.getNodeContext)
          onEventCompleted(event, resultResponse)
        } else if(!response.isCompleted) {
          event.setLastAskTime()
          addEvent(event)
        }
      }

      private def onEventCompleted(event: AsyncNodeExecutionResponseEvent, response: CompletedNodeExecutionResponse): Unit = {
        event.getResponse.getListeners.foreach(_.onNodeExecutionCompleted(response))
        event.getResponse.setCompleted(true)
      }

      override def onEventError(event: AsyncNodeExecutionResponseEvent, t: Throwable): Unit = t match {
        case e: Exception =>
          val response = new CompletedNodeExecutionResponse
          response.setIsSucceed(false)
          response.setException(e)
          onEventCompleted(event, response)
      }
    })
  }

  override def addAsyncResponse(response: AsyncNodeExecutionResponse): Unit =
    addEvent(new AsyncNodeExecutionResponseEvent(response))

  protected def addEvent(event: AsyncNodeExecutionResponseEvent): Unit = synchronized {
    listenerEventBus.post(event)
    event.getResponse.getAction match {
      case longTermAction: LongTermNodeExecutionAction =>
        longTermAction.setSchedulerId(eventQueue.max)
      case _ =>
    }
  }

  override def removeAsyncResponse(action: LongTermNodeExecutionAction): Unit =
    getAsyncResponse(action).setCompleted(true)

  override def getAsyncResponse(action: LongTermNodeExecutionAction): AsyncNodeExecutionResponse =
    eventQueue.get(action.getSchedulerId).getResponse

  override def start(): Unit = listenerEventBus.start()

  override def stop(): Unit = listenerEventBus.stop()
}