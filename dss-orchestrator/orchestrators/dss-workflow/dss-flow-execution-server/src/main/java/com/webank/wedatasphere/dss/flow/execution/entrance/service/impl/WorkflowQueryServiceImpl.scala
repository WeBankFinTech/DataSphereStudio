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

package com.webank.wedatasphere.dss.flow.execution.entrance.service.impl

import java.lang.Long
import java.util
import java.util.Date

import com.google.common.collect.Iterables
import com.webank.wedatasphere.dss.flow.execution.entrance.dao.TaskMapper
import com.webank.wedatasphere.dss.flow.execution.entrance.entity.{WorkflowQueryTask, WorkflowQueryTaskVO}
import com.webank.wedatasphere.dss.flow.execution.entrance.exception.FlowQueryErrorException
import com.webank.wedatasphere.dss.flow.execution.entrance.service.WorkflowQueryService
import com.webank.wedatasphere.dss.flow.execution.entrance.status.TaskStatus
import com.webank.wedatasphere.dss.flow.execution.entrance.utils.FlowExecutionUtils
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import com.webank.wedatasphere.linkis.governance.common.entity.task._
import com.webank.wedatasphere.linkis.manager.label.utils.LabelUtils
import com.webank.wedatasphere.linkis.protocol.utils.ZuulEntranceUtils
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.BeanUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
  * Created by johnnwang on 2019/2/25.
  */
@Service
class WorkflowQueryServiceImpl extends WorkflowQueryService with Logging {
  @Autowired
  private var taskMapper: TaskMapper = _
//  @Autowired
//  private var queryCacheService: QueryCacheService = _

  override def add(requestInsertTask: RequestInsertTask): ResponsePersist = {
    info("Insert data into the database(往数据库中插入数据)：" + requestInsertTask.toString)
//    QueryUtils.storeExecutionCode(requestInsertTask)
    val persist = new ResponsePersist
    Utils.tryCatch {
      val queryTask = requestPersistTaskTask2QueryTask(requestInsertTask)
      taskMapper.insertTask(queryTask)
      val map = new util.HashMap[String, Object]()
      map.put("taskID", queryTask.getTaskID())
      persist.setStatus(0)
      persist.setData(map)
    } {
      case e: Exception =>
        error(e.getMessage)
        persist.setStatus(1)
        persist.setMsg(e.getMessage)
    }
    persist
  }

  @Transactional
  override def change(requestUpdateTask: RequestUpdateTask): ResponsePersist = {
    val executionCode = requestUpdateTask.getExecutionCode
    requestUpdateTask.setExecutionCode(null)
    info("Update data to the database(往数据库中更新数据)：" + requestUpdateTask.toString)
    /*//TODO cooperyang 去查数据内容
    if (StringUtils.isNotEmpty(requestUpdateTask.getEngineInstance)) {
      requestUpdateTask.setTaskResource(taskMapper.getResource(requestUpdateTask.getEngineInstance));
    }*/
    val persist = new ResponsePersist
    Utils.tryCatch {
      if (requestUpdateTask.getErrDesc != null) {
        if (requestUpdateTask.getErrDesc.length > 256) {
          info(s"errorDesc is too long,we will cut some message")
          requestUpdateTask.setErrDesc(requestUpdateTask.getErrDesc.substring(0, 256))
          info(s"${requestUpdateTask.getErrDesc}")
        }
      }
      if (requestUpdateTask.getStatus != null) {
        val oldStatus: String = taskMapper.selectTaskStatusForUpdate(requestUpdateTask.getTaskID)
        if (oldStatus != null && !shouldUpdate(oldStatus, requestUpdateTask.getStatus))
          throw new FlowQueryErrorException(100639,s"${requestUpdateTask.getTaskID}数据库中的task状态为：${oldStatus}更新的task状态为：${requestUpdateTask.getStatus}更新失败！")
      }
      taskMapper.updateTask(requestPersistTaskTask2QueryTask(requestUpdateTask))

      //updated by shanhuang to write cache
      //todo  add cache
      if (TaskStatus.Succeed.toString.equals(requestUpdateTask.getStatus)) {
        info("Write cache for task: " + requestUpdateTask.getTaskID)
        requestUpdateTask.setExecutionCode(executionCode)
//        queryCacheService.writeCache(requestUpdateTask)
      }

      val map = new util.HashMap[String, Object]
      map.put("taskID", requestUpdateTask.getTaskID)
      persist.setStatus(0)
      persist.setData(map)
    } {
      case e: Exception =>
        error(e.getMessage)
        persist.setStatus(1);
        persist.setMsg(e.getMessage);
    }
    persist
  }

  override def query(requestQueryTask: RequestQueryTask): ResponsePersist = {
    info("查询历史task：" + requestQueryTask.toString)
    val persist = new ResponsePersist
    Utils.tryCatch {
      val task = taskMapper.selectTask(requestPersistTaskTask2QueryTask(requestQueryTask))
      val map = new util.HashMap[String, Object]()
      map.put("task", queryTaskList2RequestPersistTaskList(task))
      persist.setStatus(0)
      persist.setData(map)
    } {
      case e: Exception =>
        error(e.getMessage)
        persist.setStatus(1);
        persist.setMsg(e.getMessage);
    }
    persist
  }

  private def queryTaskList2RequestPersistTaskList(queryTask: java.util.List[WorkflowQueryTask]): java.util.List[RequestPersistTask] = {
    import scala.collection.JavaConversions._
    val tasks = new util.ArrayList[RequestPersistTask]
    queryTask.foreach(f => tasks.add(queryTask2RequestPersistTask(f)))
    tasks
  }

  def queryTask2RequestPersistTask(queryTask: WorkflowQueryTask): RequestPersistTask = {
//    QueryUtils.exchangeExecutionCode(queryTask)
    val task = new RequestPersistTask
    BeanUtils.copyProperties(queryTask, task)
    task.setSource(FlowExecutionUtils.gson.fromJson(queryTask.getSourceJson, classOf[java.util.HashMap[String, String]]))
    task.setParams(FlowExecutionUtils.gson.fromJson(queryTask.getParamsJson, classOf[java.util.HashMap[String, Object]]))
    task
  }

  private def requestPersistTaskTask2QueryTask(requestPersistTask: RequestPersistTask): WorkflowQueryTask = {
    val task: WorkflowQueryTask = new WorkflowQueryTask
    BeanUtils.copyProperties(requestPersistTask, task)
    if (requestPersistTask.getSource != null)
      task.setSourceJson(FlowExecutionUtils.gson.toJson(requestPersistTask.getSource))
    if (requestPersistTask.getParams != null)
      task.setParamsJson(FlowExecutionUtils.gson.toJson(requestPersistTask.getParams))
    if (requestPersistTask.getLabels != null)
      task.setLabelJson(FlowExecutionUtils.gson.toJson(LabelUtils.labelsToMap(requestPersistTask.getLabels)))
    task
  }

  override def getTaskByID(taskID: Long, userName: String): WorkflowQueryTaskVO = {
    val task = new WorkflowQueryTask
    task.setTaskID(taskID)
    task.setUmUser(userName)
    val taskR = taskMapper.selectTask(task)
    import scala.collection.JavaConversions._
    if (taskR.isEmpty) null else queryTask2QueryTaskVO(taskR(0))
  }


  def queryTask2QueryTaskVO(queryTask: WorkflowQueryTask): WorkflowQueryTaskVO = {
//    QueryUtils.exchangeExecutionCode(queryTask)
    val taskVO = new WorkflowQueryTaskVO
    BeanUtils.copyProperties(queryTask, taskVO)
    if (!StringUtils.isEmpty(taskVO.getSourceJson)) {
      Utils.tryCatch {
        val source = FlowExecutionUtils.gson.fromJson(taskVO.getSourceJson, classOf[util.Map[String, String]])
        import scala.collection.JavaConversions._
        taskVO.setSourceTailor(source.map(_._2).foldLeft("")(_ + _ + "-").stripSuffix("-"))
      } {
        case _ => warn("sourceJson deserializae failed,this task may be the old data")
      }
    }
    if (queryTask.getExecId() != null && queryTask.getExecuteApplicationName() != null && queryTask.getInstance() != null) {
      taskVO.setStrongerExecId(ZuulEntranceUtils.generateExecID(queryTask.getExecId(),
        queryTask.getExecuteApplicationName(), queryTask.getInstance(), queryTask.getRequestApplicationName))
    }
    val status = queryTask.getStatus()
    val createdTime = queryTask.getCreatedTime()
    val updatedTime = queryTask.getUpdatedTime()
    if (isJobFinished(status) && createdTime != null && updatedTime != null) {
      taskVO.setCostTime(queryTask.getUpdatedTime().getTime() - queryTask.getCreatedTime().getTime());
    } else if (createdTime != null) {
      taskVO.setCostTime(System.currentTimeMillis() - queryTask.getCreatedTime().getTime());
    }
    taskVO
  }

  def isJobFinished(status: String): Boolean = {
    TaskStatus.Succeed.toString.equals(status) ||
      TaskStatus.Failed.toString.equals(status) ||
      TaskStatus.Cancelled.toString.equals(status) ||
      TaskStatus.Timeout.toString.equals(status)
  }

  override def search(taskID: Long, username: String, status: String, sDate: Date, eDate: Date, executeApplicationName: String): util.List[WorkflowQueryTask] = {
    import scala.collection.JavaConversions._
    val split: util.List[String] = if (status != null) status.split(",").toList else null
    taskMapper.search(taskID, username, split, sDate, eDate, executeApplicationName, null, null)
  }

  def getQueryVOList(list: java.util.List[WorkflowQueryTask]): java.util.List[WorkflowQueryTaskVO] = {
    val ovs = new util.ArrayList[WorkflowQueryTaskVO]
    import scala.collection.JavaConversions._
    list.foreach(f => {
      ovs.add(queryTask2QueryTaskVO(f))
    })
    ovs
  }

  private def shouldUpdate(oldStatus: String, newStatus: String): Boolean = TaskStatus.valueOf(oldStatus).ordinal <= TaskStatus.valueOf(newStatus).ordinal

  override def searchOne(instance: String, execId: String, sDate: Date, eDate: Date, executeApplicationName: String): RequestPersistTask = {
    Iterables.getFirst(
      queryTaskList2RequestPersistTaskList(taskMapper.search(null, null, null, sDate, eDate, executeApplicationName, instance, execId)),
      {
        val requestPersistTask = new RequestPersistTask
        requestPersistTask.setInstance(instance)
        requestPersistTask.setExecId(execId)
        requestPersistTask.setStatus(TaskStatus.Inited.toString)
        requestPersistTask.setProgress(0.0f)
        requestPersistTask.setTaskID(0l)
        requestPersistTask
      })
  }
}

