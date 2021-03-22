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

package com.webank.wedatasphere.linkis.manager.engineplugin.appconn.executor

import java.util

import com.webank.wedatasphere.dss.standard.app.development.execution.core.ExecutionRequestRefContext
import com.webank.wedatasphere.dss.standard.app.development.execution.exception.AppConnExecutionErrorException
import com.webank.wedatasphere.linkis.common.io.resultset.{ResultSet, ResultSetReader, ResultSetWriter}
import com.webank.wedatasphere.linkis.common.io.{FsPath, MetaData, Record}
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}
import com.webank.wedatasphere.linkis.engineconn.computation.executor.execute.EngineExecutionContext
import com.webank.wedatasphere.linkis.manager.engineplugin.appconn.conf.AppConnEngineConnConfiguration
import com.webank.wedatasphere.linkis.protocol.UserWithCreator
import com.webank.wedatasphere.linkis.rpc.Sender
import com.webank.wedatasphere.linkis.storage.FSFactory
import com.webank.wedatasphere.linkis.storage.fs.FileSystem
import com.webank.wedatasphere.linkis.storage.resultset.{ResultSetFactory, ResultSetReader}

import scala.collection.JavaConversions._

/**
  * Created by enjoyyin on 2021/1/27.
  */
abstract class AbstractExecutionRequestRefContext(engineExecutorContext: EngineExecutionContext, userWithCreator: UserWithCreator)
  extends ExecutionRequestRefContext with Logging {

  private var storePath: String = _

  override def getRuntimeMap: util.Map[String, AnyRef] = engineExecutorContext.getProperties

  override def appendLog(log: String): Unit = engineExecutorContext.appendStdout(log)

  override def updateProgress(progress: Float): Unit = engineExecutorContext.pushProgress(progress, Array.empty)

  /**
    * 获取本节点的操作用户
    *
    * @return
    */
  override def getUser: String = userWithCreator.user

  override def setStorePath(storePath: String): Unit = {
    this.storePath = storePath
    engineExecutorContext.setStorePath(storePath)
  }

  override def getStorePath: String = storePath

  override def createTableResultSetWriter[M <: MetaData, R <: Record](): ResultSetWriter[M, R] =
    createTableResultSetWriter(null)

  override def createTableResultSetWriter[M <: MetaData, R <: Record](resultSetAlias: String): ResultSetWriter[M, R] =
    createResultSetWriter(ResultSetFactory.TABLE_TYPE, resultSetAlias)

  override def createTextResultSetWriter[M <: MetaData, R <: Record](): ResultSetWriter[M, R] = createTextResultSetWriter(null)

  override def createTextResultSetWriter[M <: MetaData, R <: Record](resultSetAlias: String): ResultSetWriter[M, R] =
    createResultSetWriter(ResultSetFactory.TEXT_TYPE, resultSetAlias)

  override def createHTMLResultSetWriter[M <: MetaData, R <: Record](): ResultSetWriter[M, R] = createHTMLResultSetWriter(null)

  override def createHTMLResultSetWriter[M <: MetaData, R <: Record](resultSetAlias: String): ResultSetWriter[M, R] =
    createResultSetWriter(ResultSetFactory.HTML_TYPE, resultSetAlias)

  override def createPictureResultSetWriter[M <: MetaData, R <: Record](): ResultSetWriter[M, R] = createPictureResultSetWriter(null)

  override def createPictureResultSetWriter[M <: MetaData, R <: Record](resultSetAlias: String): ResultSetWriter[M, R] =
    createResultSetWriter(ResultSetFactory.PICTURE_TYPE, resultSetAlias)

  override def createResultSetWriter[M <: MetaData, R <: Record](resultSet: ResultSet[_ <: MetaData, _ <: Record],
                                                                 resultSetAlias: String): ResultSetWriter[M, R] =
    engineExecutorContext.createResultSetWriter(resultSet, resultSetAlias).asInstanceOf[ResultSetWriter[M, R]]

  override def getResultSetReader[M <: MetaData, R <: Record](fsPath: FsPath): ResultSetReader[M, R] =
    ResultSetReader.getResultSetReader(fsPath.getPath).asInstanceOf[ResultSetReader[M, R]]

  private def createResultSetWriter[M <: MetaData, R <: Record](resultSetType: String, resultSetAlias: String): ResultSetWriter[M, R] =
    engineExecutorContext.createResultSetWriter(resultSetType, resultSetAlias).asInstanceOf[ResultSetWriter[M, R]]

  /**
    *
    * @return
    */
  override def getGatewayUrl: String = {
    val instances = Utils.tryThrow {
      Sender.getInstances(AppConnEngineConnConfiguration.GATEWAY_SPRING_APPLICATION.getValue)
    }{ t => new AppConnExecutionErrorException(75538, "获取gateway的url失败", t)}
    if (instances.length == 0) throw new AppConnExecutionErrorException(75538, "获取gateway的url失败")
    instances(0).getInstance
  }

  override def fetchLinkisJobResultSetPaths(jobId: Long): Array[FsPath] = {
    val task = fetchLinkisJob(jobId)
    val resultSetLocation = task.getResultLocation
    val user = task.getExecuteUser
    FSFactory.getFsByProxyUser(new FsPath(resultSetLocation), user) match {
      case fileSystem: FileSystem =>
        fileSystem.init(new util.HashMap[String, String])
        Utils.tryFinally {
          import  scala.collection.JavaConverters._
          fileSystem.listPathWithError(new FsPath(resultSetLocation)).getFsPaths.asScala.toArray[FsPath]
        }(Utils.tryQuietly(fileSystem.close()))
    }
  }
}