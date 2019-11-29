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
import java.util.concurrent.atomic.AtomicInteger

import com.google.gson.Gson
import com.webank.wedatasphere.dss.appjoint.execution.core.NodeContextImpl
import com.webank.wedatasphere.dss.linkis.appjoint.entrance.conf.AppJointEntranceConfiguration
import com.webank.wedatasphere.dss.linkis.appjoint.entrance.exception.{AppJointErrorException, RPCFailedException}
import com.webank.wedatasphere.linkis.DataWorkCloudApplication
import com.webank.wedatasphere.linkis.common.exception.ErrorException
import com.webank.wedatasphere.linkis.common.io.resultset.{ResultSet, ResultSetReader, ResultSetWriter}
import com.webank.wedatasphere.linkis.common.io.{FsPath, MetaData, Record}
import com.webank.wedatasphere.linkis.common.utils.{ByteTimeUtils, Logging, Utils}
import com.webank.wedatasphere.linkis.entrance.EntranceContext
import com.webank.wedatasphere.linkis.protocol.query.{RequestPersistTask, RequestQueryTask, ResponsePersist}
import com.webank.wedatasphere.linkis.rpc.Sender
import com.webank.wedatasphere.linkis.scheduler.queue.Job
import com.webank.wedatasphere.linkis.storage.resultset.table.{TableMetaData, TableRecord}
import com.webank.wedatasphere.linkis.storage.resultset.{ResultSetFactory, ResultSetReader, ResultSetWriter, StorageResultSetWriter}
import com.webank.wedatasphere.linkis.storage.{FSFactory, LineMetaData, LineRecord}
import org.apache.commons.lang.StringUtils

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer
/**
  * created by cooperyang on 2019/10/12
  * Description:
  */
class AppJointNodeContextImpl extends NodeContextImpl with Logging{


  //querySender 是用来进行访query模块
  val publicServiceSender:Sender = Sender.getSender(AppJointEntranceConfiguration.PUBLIC_SERVICE_SPRING_APPLICATION.getValue)

  val entranceContext:EntranceContext = DataWorkCloudApplication.getApplicationContext.getBean(classOf[EntranceContext])

  var job:Job = _

  var storePath:String = _

  val SUCCESS_FLAG = 0

  val TASK_MAP_KEY = "task"

  def setJob(job:Job):Unit = this.job = job

  private val resultSetFactory = ResultSetFactory.getInstance

  private val aliasNum = new AtomicInteger(0)

  private val defaultResultSetAlias = ""

  /**
    * 通过jobId拿回一个task
    * @param jobId
    * @return
    */

  override def getJobById(jobId: Long): RequestPersistTask = {

    val requestQueryTask = new RequestQueryTask()
    requestQueryTask.setTaskID(jobId)
    requestQueryTask.setSource(null)
    val task = Utils.tryCatch{
      val taskResponse = publicServiceSender.ask(requestQueryTask)
      taskResponse match {
        case responsePersist:ResponsePersist => val status = responsePersist.getStatus
          if (status != SUCCESS_FLAG){
            logger.error(s"query from jobHistory status failed, status is $status")
            throw AppJointErrorException("query from jobHistory status failed")
          }else{
            val data = responsePersist.getData
            data.get(TASK_MAP_KEY) match {
              case tasks:util.List[util.Map[String, Object]] => tasks.get(0) match {
                case map:util.Map[String, Object] => val gson = new Gson()
                  val json = gson.toJson(map)
                  val requestPersistTask = gson.fromJson(json, classOf[RequestPersistTask])
                  requestPersistTask
                case _ => throw AppJointErrorException(s"query from jobhistory not a correct RequestPersistTask type taskId is $jobId")
              }
              case _ => throw AppJointErrorException(s"query from jobhistory not a correct List type taskId is $jobId")
            }
          }
        case _ => logger.error("get query response incorrectly")
          throw AppJointErrorException("get query response incorrectly")
      }
    }{
      case errorException:ErrorException => throw errorException
      case e:Exception => val e1 = AppJointErrorException(s"query taskId $jobId error")
        e1.initCause(e)
        throw e
    }
    task
  }

  override def appendLog(log: String): Unit = {
    entranceContext.getOrCreateLogManager().onLogUpdate(job, log)
  }

  override def updateProgress(progress: Float): Unit = {
    entranceContext.getOrCreatePersistenceManager().onProgressUpdate(job, progress, Array.empty)
  }

  override def setStorePath(storePath: String): Unit = this.storePath = storePath

  override def getStorePath: String = this.storePath

  private def getMaxCache:Long = ByteTimeUtils.byteStringAsBytes("0K")

  override def createTableResultSetWriter(): ResultSetWriter[TableMetaData, TableRecord] = {
    createTableResultSetWriter(defaultResultSetAlias)
  }



  override def createTableResultSetWriter(resultSetAlias: String): ResultSetWriter[TableMetaData, TableRecord] = {
    val resultSetType = ResultSetFactory.resultSetType.getOrElse(ResultSetFactory.TABLE_TYPE, "TABLE")
    val resultSet = resultSetFactory.getResultSetByType(ResultSetFactory.TABLE_TYPE)
    val filePath = this.storePath
    val fileName = if(StringUtils.isEmpty(resultSetAlias)) "_" + aliasNum.getAndIncrement() else resultSetAlias + "_" + aliasNum.getAndIncrement()
    val resultSetPath = resultSet.getResultSetPath(new FsPath(filePath), fileName)
    val resultSetWriter = ResultSetWriter.getResultSetWriter(resultSet, getMaxCache, resultSetPath)
    resultSetWriter.asInstanceOf[StorageResultSetWriter[TableMetaData, TableRecord]].setProxyUser(this.getUser)
    resultSetWriter match {
      case r:ResultSetWriter[TableMetaData, TableRecord] => r
      case _ => null
    }
  }

  override def createTextResultSetWriter(): ResultSetWriter[LineMetaData, LineRecord] = {
    createTextResultSetWriter(defaultResultSetAlias)
  }

  override def createTextResultSetWriter(resultSetAlias: String): ResultSetWriter[LineMetaData, LineRecord] = {
    val resultSetType = ResultSetFactory.resultSetType.getOrElse(ResultSetFactory.TEXT_TYPE, "TEXT")
    val resultSet = resultSetFactory.getResultSetByType(ResultSetFactory.TEXT_TYPE)
    val filePath = this.storePath
    val fileName = if(StringUtils.isEmpty(resultSetAlias)) "_" + aliasNum.getAndIncrement() else resultSetAlias + "_" + aliasNum.getAndIncrement()
    val resultSetPath = resultSet.getResultSetPath(new FsPath(filePath), fileName)
    val resultSetWriter = ResultSetWriter.getResultSetWriter(resultSet, getMaxCache, resultSetPath)
    resultSetWriter.asInstanceOf[StorageResultSetWriter[LineMetaData, LineRecord]].setProxyUser(this.getUser)
    resultSetWriter match {
      case r:ResultSetWriter[LineMetaData, LineRecord] => r
      case _ => null
    }
  }

  override def createHTMLResultSetWriter(): ResultSetWriter[LineMetaData, LineRecord] = {
    createHTMLResultSetWriter(defaultResultSetAlias)
  }

  override def createHTMLResultSetWriter(resultSetAlias: String): ResultSetWriter[LineMetaData, LineRecord] = {
    val resultSetType = ResultSetFactory.resultSetType.getOrElse(ResultSetFactory.HTML_TYPE, "HTML")
    val resultSet = resultSetFactory.getResultSetByType(ResultSetFactory.HTML_TYPE)
    val filePath = this.storePath
    val fileName = if(StringUtils.isEmpty(resultSetAlias)) "_" + aliasNum.getAndIncrement() else resultSetAlias + "_" + aliasNum.getAndIncrement()
    val resultSetPath = resultSet.getResultSetPath(new FsPath(filePath), fileName)
    val resultSetWriter = ResultSetWriter.getResultSetWriter(resultSet, getMaxCache, resultSetPath)
    resultSetWriter.asInstanceOf[StorageResultSetWriter[LineMetaData, LineRecord]].setProxyUser(this.getUser)
    resultSetWriter match {
      case r:ResultSetWriter[LineMetaData, LineRecord] => r
      case _ => null
    }
  }

  override def createPictureResultSetWriter(): ResultSetWriter[LineMetaData, LineRecord] = {
    createPictureResultSetWriter(defaultResultSetAlias)
  }

  override def createPictureResultSetWriter(resultSetAlias: String): ResultSetWriter[LineMetaData, LineRecord] = {
    val resultSetType = ResultSetFactory.resultSetType.getOrElse(ResultSetFactory.PICTURE_TYPE, "PICTURE")
    val resultSet = resultSetFactory.getResultSetByType(ResultSetFactory.PICTURE_TYPE)
    val filePath = this.storePath
    val fileName = if(StringUtils.isEmpty(resultSetAlias)) "_" + aliasNum.getAndIncrement() else resultSetAlias + "_" + aliasNum.getAndIncrement()
    val resultSetPath = resultSet.getResultSetPath(new FsPath(filePath), fileName)
    val resultSetWriter = ResultSetWriter.getResultSetWriter(resultSet, getMaxCache, resultSetPath)
    resultSetWriter.asInstanceOf[StorageResultSetWriter[LineMetaData, LineRecord]].setProxyUser(this.getUser)
    resultSetWriter match {
      case r:ResultSetWriter[LineMetaData, LineRecord] => r
      case _ => null
    }
  }

  override def createResultSetWriter(resultSet: ResultSet[_ <: MetaData, _ <: Record], resultSetAlias: String): ResultSetWriter[_ <: MetaData, _ <: Record] = ???

  //todo
  override def getResultSetReader(fsPath: FsPath): ResultSetReader[_ <: MetaData, _ <: Record] = {
    ResultSetReader.getResultSetReader(fsPath.getPath)
  }

  /**
    *
    * @return
    */
  override def getGatewayUrl: String = {
    val instances = Utils.tryCatch{
      Sender.getInstances(AppJointEntranceConfiguration.GATEWAY_SPRING_APPLICATION.getValue)
    }{
      case e:Exception => val e1 = RPCFailedException("获取gateway的url失败")
        e1.initCause(e)
        throw e1
    }
    if (instances.length == 0) throw RPCFailedException("获取gateway的url失败")
    instances(0).getInstance
  }

  override def getResultSetPathsByJobId(jobId: Long): Array[FsPath] = {
    val paths = new ArrayBuffer[FsPath]()
    val task = getJobById(jobId)
    val resultSetLocation = task.getResultLocation
    val user = task.getUmUser
    val fileSystem = FSFactory.getFsByProxyUser(new FsPath(resultSetLocation), user)
    fileSystem.init(new util.HashMap[String, String]())
    Utils.tryFinally{
      fileSystem.list(new FsPath(resultSetLocation)) foreach (paths += _)
    }{
      Utils.tryQuietly(fileSystem.close())
    }
    paths.toArray
  }
}
