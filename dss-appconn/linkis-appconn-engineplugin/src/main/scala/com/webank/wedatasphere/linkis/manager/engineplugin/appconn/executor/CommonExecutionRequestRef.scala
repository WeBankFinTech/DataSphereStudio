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

package com.webank.wedatasphere.linkis.manager.engineplugin.appconn.executor

import java.util

import com.webank.wedatasphere.dss.standard.app.development.listener.common.AsyncExecutionRequestRef
import com.webank.wedatasphere.dss.standard.app.development.listener.core.ExecutionRequestRefContext
import com.webank.wedatasphere.dss.standard.app.sso.Workspace

class CommonExecutionRequestRef extends AsyncExecutionRequestRef {

  private var workspace: Workspace = _
  private var projectId: Long = _
  private var projectName: String = _
  private var orchestratorName: String = _
  private var orchestratorVersion: String = _
  private var orchestratorId: Long = _
  private var jobContent: util.Map[String, AnyRef] = _
  private var executionRequestRefContext: ExecutionRequestRefContext = _

  private val parameters: util.Map[String, AnyRef] = new util.HashMap[String, AnyRef]()
  private var name: String = _
  private var refType: String = _


  override def setWorkspace(workspace: Workspace): Unit = {
    this.workspace = workspace
  }

  override def getWorkspace: Workspace = workspace

  /**
    * Gets the ID of the project to which the node belongs
    */
  override def getProjectId: Long = projectId
  def setProjectId(projectId: Long): Unit = this.projectId = projectId

  /**
    * Get the name of the project to which node belongs
    */
  override def getProjectName: String = projectName
  override def setProjectName(projectName: String): Unit = this.projectName = projectName

  override def getOrchestratorName: String = orchestratorName
  override def setOrchestratorName(orchestratorName: String): Unit = this.orchestratorName = orchestratorName

  override def getOrchestratorVersion: String = orchestratorVersion
  def setOrchestratorVersion(orchestratorVersion: String): Unit = this.orchestratorVersion = orchestratorVersion

  override def getOrchestratorId: Long = orchestratorId
  def setOrchestratorId(orchestratorId: Long): Unit = this.orchestratorId = orchestratorId

  /**
    * Get the execution content of the node. The execution content is in the form of map
    */
  override def getJobContent: util.Map[String, AnyRef] = jobContent
  override def setJobContent(jobContent: util.Map[String, AnyRef]): Unit = this.jobContent = jobContent

  override def getParameter(key: String): AnyRef = parameters.get(key)

  override def setParameter(key: String, value: AnyRef): Unit = parameters.put(key, value)

  override def getParameters: util.Map[String, AnyRef] = parameters

  override def getName: String = name
  override def setName(name: String): Unit = this.name = name

  override def getType: String = refType
  override def setType(`type`: String): Unit = this.refType = `type`

  override def getExecutionRequestRefContext: ExecutionRequestRefContext = executionRequestRefContext

  override def setExecutionRequestRefContext(executionRequestRefContext: ExecutionRequestRefContext): Unit =
    this.executionRequestRefContext = executionRequestRefContext
}
