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

package com.webank.wedatasphere.dss.workflow.receiver


import java.util

import com.webank.wedatasphere.dss.common.exception.DSSErrorException
import com.webank.wedatasphere.dss.common.protocol._
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils
import com.webank.wedatasphere.dss.standard.app.sso.Workspace
import com.webank.wedatasphere.dss.workflow.WorkFlowManager
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow
import com.webank.wedatasphere.dss.workflow.common.protocol.{RequestCopyWorkflow, RequestCreateWorkflow, RequestImportWorkflow, ResponseCopyWorkflow, ResponseCreateWorkflow, ResponseDeleteWorkflow, ResponseImportWorkflow, ResponseQueryWorkflow, ResponseUpdateWorkflow}
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowImportParam
import com.webank.wedatasphere.linkis.rpc.{Receiver, Sender}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.concurrent.duration.Duration

/**
 * Created by allenlliu on 2020/10/21.
 */
@Component
class DSSWorkflowReceiver extends Receiver {

  @Autowired
  var workflowManager: WorkFlowManager = _


  override def receive(message: Any, sender: Sender): Unit = {}

  override def receiveAndReply(message: Any, sender: Sender): Any = message match {

    case reqCreateFlow: RequestCreateWorkflow =>
      val dssFlow: DSSFlow = workflowManager.createWorkflow(reqCreateFlow.getUserName, reqCreateFlow.getWorkflowName,
        reqCreateFlow.getContextIDStr, reqCreateFlow.getDescription,
        reqCreateFlow.getParentFlowID, reqCreateFlow.getUses,
        reqCreateFlow.getLinkedAppConnNames, reqCreateFlow.getDssLabels)
      val responseCreateWorkflow = new ResponseCreateWorkflow()
      responseCreateWorkflow.setDssFlow(dssFlow)
      responseCreateWorkflow

    case reqUpdateFlow: RequestUpdateWorkflow =>
      workflowManager.updateWorkflow(reqUpdateFlow.userName,
        reqUpdateFlow.flowID, reqUpdateFlow.flowName,
        reqUpdateFlow.description, reqUpdateFlow.uses)
      new ResponseUpdateWorkflow(JobStatus.Success)

    case reqDeleteFlow: RequestDeleteWorkflow =>
      workflowManager.deleteWorkflow(reqDeleteFlow.userName, reqDeleteFlow.flowID)
      new ResponseDeleteWorkflow(JobStatus.Success)

    case reqExportFlow: RequestExportWorkflow =>
      val dssExportFlowResource: util.Map[String, AnyRef] = workflowManager.exportWorkflow(
        reqExportFlow.userName,
        reqExportFlow.flowID,
        reqExportFlow.projectId,
        reqExportFlow.projectName,
        DSSCommonUtils.COMMON_GSON.fromJson(reqExportFlow.workspaceStr, classOf[Workspace]))
      ResponseExportWorkflow(dssExportFlowResource.get("resourceId").toString, dssExportFlowResource.get("version").toString,
        reqExportFlow.flowID)

    case requestImportWorkflow: RequestImportWorkflow =>
      val dssFlowImportParam: DSSFlowImportParam = new DSSFlowImportParam()
      dssFlowImportParam.setProjectID(requestImportWorkflow.getProjectId)
      dssFlowImportParam.setProjectName(requestImportWorkflow.getProjectName)
      dssFlowImportParam.setSourceEnv(requestImportWorkflow.getSourceEnv)
      dssFlowImportParam.setUserName(requestImportWorkflow.getUserName)
      dssFlowImportParam.setVersion(requestImportWorkflow.getBmlVersion)
      dssFlowImportParam.setOrcVersion(requestImportWorkflow.getOrcVersion)
      dssFlowImportParam.setWorkspaceName(requestImportWorkflow.getWorkspaceName)
      dssFlowImportParam.setWorkspace(DSSCommonUtils.COMMON_GSON.fromJson(requestImportWorkflow.getWorkspaceStr, classOf[Workspace]))
      val dssFlows = workflowManager.importWorkflow(requestImportWorkflow.getUserName,
        requestImportWorkflow.getResourceId,
        requestImportWorkflow.getBmlVersion,
        dssFlowImportParam)
      import scala.collection.JavaConversions._
      val dssFlowIds = dssFlows.map(dssFlow => dssFlow.getId).toList
      new ResponseImportWorkflow(JobStatus.Success, dssFlowIds)

    case requestCopyWorkflow: RequestCopyWorkflow =>
      val copyFlow: DSSFlow = workflowManager.copyRootflowWithSubflows(requestCopyWorkflow.getUserName,
        requestCopyWorkflow.getRootFlowId,
        requestCopyWorkflow.getWorkspaceName,
        requestCopyWorkflow.getProjectName,
        requestCopyWorkflow.getContextIdStr,
        requestCopyWorkflow.getVersion,
        requestCopyWorkflow.getDescription)
      new ResponseCopyWorkflow(copyFlow)

    case requestQueryWorkFlow: RequestQueryWorkFlow =>
      val dssFlow: DSSFlow = workflowManager.queryWorkflow(requestQueryWorkFlow.userName,
        requestQueryWorkFlow.rootFlowId)
      new ResponseQueryWorkflow(dssFlow)

    case _ => throw new DSSErrorException(90000, "")
  }

  override def receiveAndReply(message: Any, duration: Duration, sender: Sender): Any = {}
}
