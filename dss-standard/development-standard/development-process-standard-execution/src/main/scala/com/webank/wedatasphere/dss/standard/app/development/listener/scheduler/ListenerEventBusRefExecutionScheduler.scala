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

package com.webank.wedatasphere.dss.standard.app.development.listener.scheduler

import java.util.concurrent.ArrayBlockingQueue

import com.webank.wedatasphere.dss.standard.app.development.listener.common._
import com.webank.wedatasphere.dss.standard.app.development.listener.conf.RefExecutionConfiguration._
import com.webank.wedatasphere.dss.standard.app.development.listener.exception.AppConnExecutionErrorException
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.ExecutionResponseRef.ExecutionResponseRefBuilder
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.{AsyncExecutionResponseRef, ExecutionResponseRef}
import org.apache.commons.lang.time.DateFormatUtils
import org.apache.linkis.common.listener.ListenerEventBus
import org.apache.linkis.common.utils.{ByteTimeUtils, Utils}

import scala.collection.JavaConverters._


class ListenerEventBusRefExecutionScheduler(eventQueueCapacity: Int, name: String)(listenerConsumerThreadSize: Int)
  extends LongTermRefExecutionScheduler {

  private val listenerEventBus = new ListenerEventBus[AsyncRefExecutionSchedulerListener, AsyncResponseRefEvent](eventQueueCapacity, name)(listenerConsumerThreadSize) {
    override protected val dropEvent: DropEvent = new DropEvent {
      override def onDropEvent(event: AsyncResponseRefEvent): Unit = throw new AppConnExecutionErrorException(95536, "LongTermNodeExecutionScheduler is full, please ask admin for help!")
      override def onBusStopped(event: AsyncResponseRefEvent): Unit = throw new AppConnExecutionErrorException(95536, "LongTermNodeExecutionScheduler is stopped, please ask admin for help!")
    }
    override def doPostEvent(listener: AsyncRefExecutionSchedulerListener, event: AsyncResponseRefEvent): Unit = {
      listener.onEvent(event)
    }
  }

  def this() = {
    this(ASYNC_REF_EXECUTION_SCHEDULER_QUEUE_SIZE.getValue, "Async-NodeExecution-Scheduler")(ASYNC_REF_EXECUTION_SCHEDULER_THREAD_SIZE.getValue)
    getAsyncRefExecutionSchedulerListeners.foreach(listenerEventBus.addListener)
  }

  private val eventQueue = {
    val ru = scala.reflect.runtime.universe
    val classMirror = ru.runtimeMirror(getClass.getClassLoader)
    val listenerEventBusClass = classMirror.reflect(listenerEventBus)
    val field1 = ru.typeOf[ListenerEventBus[_, _]].decl(ru.TermName("eventQueue")).asMethod
    val result = listenerEventBusClass.reflectMethod(field1)
    result() match {
      case queue: ArrayBlockingQueue[AsyncResponseRefEvent] => queue
    }
  }

  protected def getAsyncRefExecutionSchedulerListeners: Array[AsyncRefExecutionSchedulerListener] = {
    Array(new AsyncRefExecutionSchedulerListener() {
      override def onEvent(event: AsyncResponseRefEvent): Unit = if(!event.getResponse.isCompleted) {
        val response = event.getResponse
        val action = response.getAction.asInstanceOf[AbstractRefExecutionAction]
        if(action.isKilledFlag){
          val resultResponse = response.getRefExecutionOperation.result(response.getAction)
          onEventCompleted(event, resultResponse)
        }
        if(response.getMaxLoopTime > 0 && System.currentTimeMillis - response.getStartTime >= response.getMaxLoopTime) {
          onEventError(event, new AppConnExecutionErrorException(75533, s"AppConnNode Execution is overtime! StartTime is ${DateFormatUtils.format(response.getStartTime, "yyyy-MM-dd HH:mm:ss")}, maxWaitTime is " +
            ByteTimeUtils.msDurationToString(response.getMaxLoopTime)))
          return
        }
        val period = System.currentTimeMillis() - event.getLastAskTime
        if(period < response.getAskStatePeriod) {
          if(period < 10) Utils.sleepQuietly(100)
          if(!response.isCompleted) addEvent(event)
          return
        }
        val state = response.getRefExecutionOperation.state(response.getAction)
        if(state.isCompleted) {
          val resultResponse = response.getRefExecutionOperation.result(response.getAction)
          onEventCompleted(event, resultResponse)
        } else if(!response.isCompleted) {
          event.setLastAskTime()
          addEvent(event)
        }
      }

      private def onEventCompleted(event: AsyncResponseRefEvent, response: ExecutionResponseRef): Unit = {
        event.getResponse.setCompleted(response)
      }

      override def onEventError(event: AsyncResponseRefEvent, t: Throwable): Unit = t match {
        case e: Exception =>
          val responseRef = new ExecutionResponseRefBuilder().setException(e).error()
          onEventCompleted(event, responseRef)
      }
    })
  }

  override def addAsyncResponse(response: AsyncExecutionResponseRef): Unit =
    addEvent(new AsyncResponseRefEvent(response))

  protected def addEvent(event: AsyncResponseRefEvent): Unit = synchronized {
    listenerEventBus.post(event)
  }

  override def removeAsyncResponse(action: LongTermRefExecutionAction): Unit = {

  }

  override def getAsyncResponse(action: LongTermRefExecutionAction): AsyncExecutionResponseRef =
    eventQueue.iterator().asScala.find(_.getResponse.getAction == action).map(_.getResponse).orNull

  override def start(): Unit = listenerEventBus.start()

  override def stop(): Unit = listenerEventBus.stop()
}