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

package com.webank.wedatasphere.dss.workflow.receiver

import com.webank.wedatasphere.dss.common.entity.BmlResource
import com.webank.wedatasphere.dss.common.exception.DSSErrorException
import com.webank.wedatasphere.dss.common.protocol._
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestConvertOrchestrations
import com.webank.wedatasphere.dss.standard.app.sso.Workspace
import com.webank.wedatasphere.dss.workflow.WorkFlowManager
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow
import com.webank.wedatasphere.dss.workflow.common.protocol._
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowImportParam
import org.apache.linkis.rpc.{Receiver, Sender}

import scala.concurrent.duration.Duration

class DSSWorkflowReceiver(workflowManager: WorkFlowManager)  extends Receiver {
  override def receive(message: Any, sender: Sender): Unit = {}

  override def receiveAndReply(message: Any, sender: Sender): Any = message match {

    case reqCreateFlow: RequestCreateWorkflow =>
      val dssFlow = workflowManager.createWorkflow(reqCreateFlow.getUserName, reqCreateFlow.getProjectId, reqCreateFlow.getWorkflowName,
        reqCreateFlow.getContextIDStr, reqCreateFlow.getDescription,
        reqCreateFlow.getParentFlowID, reqCreateFlow.getUses,
        reqCreateFlow.getLinkedAppConnNames, reqCreateFlow.getDssLabels, reqCreateFlow.getOrcVersion,
        reqCreateFlow.getSchedulerAppConnName)
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

    case reqUnlockWorkflow: RequestUnlockWorkflow =>
      workflowManager.unlockWorkflow(reqUnlockWorkflow.getUsername, reqUnlockWorkflow.getFlowId, reqUnlockWorkflow.getConfirmDelete)

    case reqExportFlow: RequestExportWorkflow =>
      val dssExportFlowResource: BmlResource = workflowManager.exportWorkflow(
        reqExportFlow.userName,
        reqExportFlow.flowID,
        reqExportFlow.projectId,
        reqExportFlow.projectName,
        DSSCommonUtils.COMMON_GSON.fromJson(reqExportFlow.workspaceStr, classOf[Workspace]),
        reqExportFlow.dssLabelList)
      ResponseExportWorkflow(dssExportFlowResource.getResourceId, dssExportFlowResource.getVersion,
        reqExportFlow.flowID)

    case requestImportWorkflow: RequestImportWorkflow =>
      val dssFlowImportParam: DSSFlowImportParam = new DSSFlowImportParam()
      dssFlowImportParam.setProjectID(requestImportWorkflow.getProjectId)
      dssFlowImportParam.setProjectName(requestImportWorkflow.getProjectName)
      dssFlowImportParam.setUserName(requestImportWorkflow.getUserName)
      dssFlowImportParam.setOrcVersion(requestImportWorkflow.getOrcVersion)
      dssFlowImportParam.setWorkspace(requestImportWorkflow.getWorkspace)
      dssFlowImportParam.setContextId(requestImportWorkflow.getContextId)
      val dssFlows = workflowManager.importWorkflow(requestImportWorkflow.getUserName,
        requestImportWorkflow.getResourceId,
        requestImportWorkflow.getBmlVersion,
        dssFlowImportParam, requestImportWorkflow.getDssLabels)
      import scala.collection.JavaConversions._
      val dssFlowIds = dssFlows.map(dssFlow => (dssFlow.getId, dssFlow.getFlowJson)).toMap
      new ResponseImportWorkflow(JobStatus.Success, dssFlowIds)

    case requestCopyWorkflow: RequestCopyWorkflow =>
      val copyFlow: DSSFlow = workflowManager.copyRootFlowWithSubFlows(requestCopyWorkflow.getUserName,
        requestCopyWorkflow.getRootFlowId,
        requestCopyWorkflow.getWorkspace,
        requestCopyWorkflow.getProjectName,
        requestCopyWorkflow.getContextIdStr,
        requestCopyWorkflow.getOrcVersion,
        requestCopyWorkflow.getDescription,
        requestCopyWorkflow.getDssLabels,
        requestCopyWorkflow.getNodeSuffix,
        requestCopyWorkflow.getNewFlowName,
        requestCopyWorkflow.getTargetProjectId)
      new ResponseCopyWorkflow(copyFlow)

    case requestQueryWorkFlow: RequestQueryWorkFlow =>
      val dssFlow: DSSFlow = workflowManager.queryWorkflow(requestQueryWorkFlow.userName,
        requestQueryWorkFlow.rootFlowId)
      new ResponseQueryWorkflow(dssFlow)

    case requestConvertOrchestrator: RequestConvertOrchestrations =>
      workflowManager.convertWorkflow(requestConvertOrchestrator)
    case requestWorkflowIdList : RequestSubFlowContextIds =>
      workflowManager.getSubFlowContextIdsByFlowIds(requestWorkflowIdList)

    case _ => throw new DSSErrorException(90000, "Not support protocol " + message)
  }

  override def receiveAndReply(message: Any, duration: Duration, sender: Sender): Any = {}
}
