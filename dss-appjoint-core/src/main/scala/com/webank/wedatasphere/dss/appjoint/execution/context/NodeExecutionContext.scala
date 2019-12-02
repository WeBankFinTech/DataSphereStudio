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

package com.webank.wedatasphere.dss.appjoint.execution.context

import com.webank.wedatasphere.linkis.common.utils.Logging

/**
  * created by enjoyyin on 2019/9/27
  * Description:
  */
object NodeExecutionContext extends Logging{

//  private lazy val querySender = Sender.getSender(NodeExecutionConfiguration.QUERY_APPLICATION_NAME.getValue)
//
//  val SUCCESS_FLAG:Int = 0
//
//  val TASK_MAP_KEY:String = "task"
//
//  /**
//    * 通过的taskId将所有的node都拿到
//    * @param taskIds
//    * @return
//    */
//
//  def getNodes(taskIds:Array[Long]):Array[AppJointNode] = {
//    val nodes = new ArrayBuffer[AppJointNode]()
//    val requestPersistTasks = new ArrayBuffer[RequestPersistTask]()
//    taskIds foreach {
//      taskId => val requestQueryTask = new RequestQueryTask()
//        requestQueryTask.setTaskID(taskId)
//        Utils.tryCatch{
//          val taskResponse = querySender.ask(requestQueryTask)
//          taskResponse match {
//            case responsePersist:ResponsePersist => val status = responsePersist.getStatus
//              if (status != SUCCESS_FLAG){
//                logger.error(s"query from jobHistory status failed, status is $status")
//                throw new AppJointErrorException(75533, "query from jobHistory status failed")
//              }else{
//                val data = responsePersist.getData
//                data.get(TASK_MAP_KEY) match {
//                  case tasks:util.List[RequestPersistTask] => tasks.get(0) match {
//                    case requestPersistTask:RequestPersistTask => requestPersistTasks += requestPersistTask
//                    case _ => logger.error(s"query from jobhistory not a correct RequestPersistTask type taskId is $taskId")
//                      throw new AppJointErrorException(75533, s"query from jobhistory not a correct RequestPersistTask type taskId is $taskId")
//                  }
//                  case _ => logger.error(s"query from jobhistory not a correct List type taskId is $taskId")
//                    throw new AppJointErrorException(75533, s"query from jobhistory not a correct List type taskId is $taskId")
//                }
//              }
//            case _ => logger.error("get query response incorrectly")
//              throw new AppJointErrorException(75533, "get query response incorrectly")
//          }
//        }{
//          case errorException:ErrorException => logger.error(s"query taskId $taskId error", errorException)
//            throw errorException
//          case e:Exception => logger.error(s"query taskId $taskId error", e)
//            throw new AppJointErrorException(75533, s"query taskId $taskId error")
//        }
//    }
//    requestPersistTasks foreach {
//      requestPersistTask => requestPersistTask.getSource
//    }
//    nodes.toArray
//  }

}
