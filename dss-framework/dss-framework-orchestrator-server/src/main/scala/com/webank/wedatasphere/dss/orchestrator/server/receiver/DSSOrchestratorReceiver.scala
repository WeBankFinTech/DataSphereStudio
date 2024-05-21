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

import com.webank.wedatasphere.dss.common.exception.DSSErrorException
import com.webank.wedatasphere.dss.common.protocol.{ResponseExportOrchestrator, ResponseImportOrchestrator}
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo
import com.webank.wedatasphere.dss.orchestrator.common.protocol._
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestratorContext
import com.webank.wedatasphere.dss.orchestrator.publish.entity.OrchestratorExportResult
import com.webank.wedatasphere.dss.orchestrator.publish.{ExportDSSOrchestratorPlugin, ImportDSSOrchestratorPlugin}
import com.webank.wedatasphere.dss.orchestrator.server.service.{OrchestratorPluginService, OrchestratorService}
import org.apache.linkis.rpc.{Receiver, Sender}
import org.slf4j.{Logger, LoggerFactory}

import java.util
import scala.concurrent.duration.Duration


class DSSOrchestratorReceiver(orchestratorService: OrchestratorService, orchestratorPluginService: OrchestratorPluginService, orchestratorContext: DSSOrchestratorContext) extends Receiver {

  private val LOGGER = LoggerFactory.getLogger(classOf[DSSOrchestratorReceiver])

  override def receive(message: Any, sender: Sender): Unit = {}

  override def receiveAndReply(message: Any, sender: Sender): Any = message match {

    case reqExportOrchestrator: RequestExportOrchestrator =>
      val dssExportOrcResource: OrchestratorExportResult = orchestratorContext.getDSSOrchestratorPlugin(classOf[ExportDSSOrchestratorPlugin]).exportOrchestratorNew(
        reqExportOrchestrator.getUserName,
        reqExportOrchestrator.getOrchestratorId,
        reqExportOrchestrator.getOrcVersionId,
        reqExportOrchestrator.getProjectName,
        reqExportOrchestrator.getDssLabels,
        reqExportOrchestrator.getAddOrcVersion,
        reqExportOrchestrator.getWorkspace)
      ResponseExportOrchestrator(dssExportOrcResource.getBmlResource.getResourceId,
        dssExportOrcResource.getBmlResource.getVersion, dssExportOrcResource.getOrcVersionId.toLong
      )

    case requestImportOrchestrator: RequestImportOrchestrator =>
      val dssOrchestratorVersion = orchestratorContext.getDSSOrchestratorPlugin(classOf[ImportDSSOrchestratorPlugin]).importOrchestratorNew(requestImportOrchestrator)
      ResponseImportOrchestrator(dssOrchestratorVersion.getOrchestratorId,dssOrchestratorVersion.getVersion)

    case addVersionAfterPublish: RequestAddVersionAfterPublish =>
      orchestratorContext.getDSSOrchestratorPlugin(classOf[ExportDSSOrchestratorPlugin]).addVersionAfterPublish(
        addVersionAfterPublish.getUserName,
        addVersionAfterPublish.getWorkspace,
        addVersionAfterPublish.getOrchestratorId,
        addVersionAfterPublish.getOrcVersionId,
        addVersionAfterPublish.getProjectName,
        addVersionAfterPublish.getDssLabels, addVersionAfterPublish.getComment)

    case requestQueryOrchestrator: RequestQueryOrchestrator =>
      val requestIdList = requestQueryOrchestrator.getOrchestratorIds
      val queryOrchestratorList: java.util.List[OrchestratorVo] = orchestratorService.getOrchestratorVoList(requestIdList)
      new ResponseQueryOrchestrator(queryOrchestratorList)

    case requestConversionOrchestration: RequestFrameworkConvertOrchestration =>
      //发布调度，请注意
      LOGGER.info("received requestConversionOrchestration, the class is: {}", requestConversionOrchestration)
      orchestratorPluginService.convertOrchestration(requestConversionOrchestration)
    case requestConversionOrchestrationStatus: RequestFrameworkConvertOrchestrationStatus =>
      orchestratorPluginService.getConvertOrchestrationStatus(requestConversionOrchestrationStatus.getId)

    case requestOrchestratorVersion: RequestOrchestratorVersion =>
      val projectId = requestOrchestratorVersion.getProjectId
      val username = requestOrchestratorVersion.getUsername
      val orchestratorId = requestOrchestratorVersion.getOrchestratorId
      val orchestratorVersions = orchestratorService.getOrchestratorVersions(username, projectId, orchestratorId)
      new ResponseOrchetratorVersion(projectId, orchestratorId, orchestratorVersions)

    case requestOrchestratorInfos: RequestOrchestratorInfos =>
      orchestratorService.queryOrchestratorInfos(requestOrchestratorInfos);

    case requestQueryByIdOrchestrator: RequestQueryByIdOrchestrator => {
      val orcVersionId = requestQueryByIdOrchestrator.getOrcVersionId
      val orchestratorId = requestQueryByIdOrchestrator.getOrchestratorId
      if (orcVersionId != null) {
        orchestratorService.getOrchestratorVoByIdAndOrcVersionId(orchestratorId, orcVersionId)
      } else {
        orchestratorService.getOrchestratorVoById(orchestratorId)
      }
    }

    case requestQuertByAppIdOrchestrator: RequestQuertByAppIdOrchestrator =>
      orchestratorService.getOrchestratorByAppId(requestQuertByAppIdOrchestrator.getAppId)

    case _ => throw new DSSErrorException(90000, "Not support message type " + message)
  }

  override def receiveAndReply(message: Any, duration: Duration, sender: Sender): Any = {}
}
