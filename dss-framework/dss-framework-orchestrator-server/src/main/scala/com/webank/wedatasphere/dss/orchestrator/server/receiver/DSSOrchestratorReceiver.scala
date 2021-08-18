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

package com.webank.wedatasphere.dss.orchestrator.server.receiver

import java.util

import com.webank.wedatasphere.dss.common.exception.DSSErrorException
import com.webank.wedatasphere.dss.common.protocol._
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo
import com.webank.wedatasphere.dss.orchestrator.common.protocol._
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestratorContext
import com.webank.wedatasphere.dss.orchestrator.publish.{ExportDSSOrchestratorPlugin, ImportDSSOrchestratorPlugin}
import com.webank.wedatasphere.dss.orchestrator.server.service.{OrchestratorPluginService, OrchestratorService}
import com.webank.wedatasphere.dss.standard.app.sso.Workspace
import com.webank.wedatasphere.linkis.rpc.{Receiver, Sender}

import scala.concurrent.duration.Duration



class DSSOrchestratorReceiver(orchestratorService: OrchestratorService,orchestratorPluginService: OrchestratorPluginService,orchestratorContext: DSSOrchestratorContext) extends Receiver {

  override def receive(message: Any, sender: Sender): Unit = {}

  override def receiveAndReply(message: Any, sender: Sender): Any = message match {
    case reqCreateOrchestrator: RequestCreateOrchestrator =>
      val orchestratorVo: OrchestratorVo = orchestratorService.createOrchestrator(reqCreateOrchestrator.getUserName,
        reqCreateOrchestrator.getWorkspaceName,
        reqCreateOrchestrator.getProjectName,
        reqCreateOrchestrator.getProjectId,
        reqCreateOrchestrator.getDescription,
        reqCreateOrchestrator.getDssOrchestratorInfo,
        reqCreateOrchestrator.getDssLabels)
      ResponseCreateOrchestrator(orchestratorVo.getDssOrchestratorInfo.getId, orchestratorVo.getDssOrchestratorVersion.getId)

    case reqUpdateOrchestrator: RequestUpdateOrchestrator =>
      orchestratorService.updateOrchestrator(reqUpdateOrchestrator.getUserName,
        reqUpdateOrchestrator.getWorkspaceName,
        reqUpdateOrchestrator.getDssOrchestratorInfo,
        reqUpdateOrchestrator.getDssLabels)
      ResponseOperateOrchestrator.success()

    case reqDeleteOrchestrator: RequestDeleteOrchestrator =>
      orchestratorService.deleteOrchestrator(reqDeleteOrchestrator.getUserName,
        reqDeleteOrchestrator.getWorkspaceName,
        reqDeleteOrchestrator.getProjectName,
        reqDeleteOrchestrator.getOrchestratorId,
        reqDeleteOrchestrator.getDssLabels
      )
      ResponseOperateOrchestrator.success()

    case reqExportOrchestrator: RequestExportOrchestrator =>
      val dssExportOrcResource: util.Map[String, AnyRef] = orchestratorContext.getDSSOrchestratorPlugin(classOf[ExportDSSOrchestratorPlugin]).exportOrchestrator(
        reqExportOrchestrator.getUserName,
        reqExportOrchestrator.getWorkspaceName,
        reqExportOrchestrator.getOrchestratorId,
        reqExportOrchestrator.getOrcVersionId,
        reqExportOrchestrator.getProjectName,
        reqExportOrchestrator.getDssLabels,
        reqExportOrchestrator.getAddOrcVersion,
        DSSCommonUtils.COMMON_GSON.fromJson(reqExportOrchestrator.getWorkspaceStr, classOf[Workspace]))
      ResponseExportOrchestrator(dssExportOrcResource.get("resourceId").toString,
        dssExportOrcResource.get("version").toString, dssExportOrcResource.get("orcVersionId").asInstanceOf[Long]
      )

    case requestImportOrchestrator: RequestImportOrchestrator =>
      val importOrcId = orchestratorContext.getDSSOrchestratorPlugin(classOf[ImportDSSOrchestratorPlugin]).importOrchestrator(
        requestImportOrchestrator.getUserName,
        requestImportOrchestrator.getWorkspaceName,
        requestImportOrchestrator.getProjectName,
        requestImportOrchestrator.getProjectId,
        requestImportOrchestrator.getResourceId,
        requestImportOrchestrator.getBmlVersion,
        requestImportOrchestrator.getDssLabels,
        DSSCommonUtils.COMMON_GSON.fromJson(requestImportOrchestrator.getWorkspaceStr, classOf[Workspace]))
      ResponseImportOrchestrator(importOrcId)

    case requestQueryOrchestrator: RequestQueryOrchestrator =>
      val requestIdList = requestQueryOrchestrator.getOrchestratorIds
      val queryOrchestratorList: util.List[OrchestratorVo] = orchestratorService.getOrchestratorVoList(requestIdList)
      new ResponseQueryOrchestrator(queryOrchestratorList)

    case requestConversionOrchestration: RequestFrameworkConvertOrchestration =>
      //发布调度
      orchestratorPluginService.convertOrchestration(requestConversionOrchestration)
    case requestConversionOrchestrationStatus: RequestFrameworkConvertOrchestrationStatus =>
      orchestratorPluginService.getConvertOrchestrationStatus(requestConversionOrchestrationStatus.getId)

    case requestOrchestratorVersion: RequestOrchestratorVersion =>
      val projectId = requestOrchestratorVersion.getProjectId
      val username = requestOrchestratorVersion.getUsername
      val orchestratorId = requestOrchestratorVersion.getOrchestratorId
      val orchestratorVersions = orchestratorService.getOrchestratorVersions(username, projectId, orchestratorId)
      new ResponseOrchetratorVersion(projectId, orchestratorId, orchestratorVersions)
    case _ => throw new DSSErrorException(90000, "")
  }

  override def receiveAndReply(message: Any, duration: Duration, sender: Sender): Any = {}
}
