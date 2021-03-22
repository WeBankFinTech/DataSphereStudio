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

package com.webank.wedatasphere.dss.orchestrator.server.receiver

import java.util

import com.webank.wedatasphere.dss.common.exception.DSSErrorException
import com.webank.wedatasphere.dss.common.protocol._
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils
import com.webank.wedatasphere.dss.orchestrator.common.entity.{OrchestratorInfo, OrchestratorVo}
import com.webank.wedatasphere.dss.orchestrator.common.protocol.{RequestCreateOrchestrator, RequestDeleteOrchestrator, RequestExportOrchestrator, RequestImportOrchestrator, RequestOrcInfo, RequestOrchestratorVersion, RequestProdOrcDetail, RequestQueryOrchestrator, RequestUpdateOrchestrator, ResponseDeleteOrchestrator, ResponseOrcInfo, ResponseOrchetratorVersion, ResponseProdOrcDetail, ResponsePublishOrchestrator, ResponseQueryOrchestrator}
import com.webank.wedatasphere.dss.orchestrator.core.protocol.RequestPublishOrchestrator
import com.webank.wedatasphere.dss.orchestrator.publish.OrchestratorPublishService
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService
import com.webank.wedatasphere.dss.standard.app.sso.Workspace
import com.webank.wedatasphere.dss.standard.common.entity.project.DSSProject
import com.webank.wedatasphere.linkis.rpc.{Receiver, Sender}
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import scala.concurrent.duration.Duration


/**
 * Created by allenlliu on 2020/10/21.
 */
@Component
class DSSOrchestratorReceiver extends Receiver {

  @Autowired
  var orchestratorService: OrchestratorService = _

  @Autowired
  var orchestratorPublishService: OrchestratorPublishService = _


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
      new ResponsePublishOrchestrator(JobStatus.Success)

    case reqDeleteOrchestrator: RequestDeleteOrchestrator =>
      orchestratorService.deleteOrchestrator(reqDeleteOrchestrator.getUserName,
        reqDeleteOrchestrator.getWorkspaceName,
        reqDeleteOrchestrator.getProjectName,
        reqDeleteOrchestrator.getOrchestratorId,
        reqDeleteOrchestrator.getDssLabels
      )
      new ResponseDeleteOrchestrator(JobStatus.Success)

    case reqExportOrchestrator: RequestExportOrchestrator =>
      val dssExportOrcResource: util.Map[String, AnyRef] = orchestratorPublishService.exportOrchestrator(
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
      val importOrcId = orchestratorPublishService.importOrchestrator(
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

    case requestPublishOrchestrator: RequestPublishOrchestrator =>
      //发布调度
      val dssProject: DSSProject = new DSSProject();
      dssProject.setName(requestPublishOrchestrator.getProjectName)
      dssProject.setId(requestPublishOrchestrator.getProjectId)
      dssProject.setUsername(requestPublishOrchestrator.getUserName)
      dssProject.setCreateBy(requestPublishOrchestrator.getUserName)
      orchestratorPublishService.publishOrchestrator(requestPublishOrchestrator.getUserName,
        dssProject,
        requestPublishOrchestrator.getWorkspace,
        requestPublishOrchestrator.getOrcIds,
        requestPublishOrchestrator.getDssLabels
      )
      val responsePublishOrchestrator = new ResponsePublishOrchestrator(JobStatus.Success)
      responsePublishOrchestrator

    case requestOrcInfo: RequestOrcInfo =>
      val workflowId = requestOrcInfo.getWorkflowId
      val username = requestOrcInfo.getReleaseUser
      val orchestratorInfo: OrchestratorInfo = orchestratorService.getOrchestratorInfo(username, workflowId)
      new ResponseOrcInfo(orchestratorInfo.getOrchestratorId, orchestratorInfo.getOrchestratorVersionId, workflowId)

    case requestProdOrcDetail: RequestProdOrcDetail =>
      val projectId = requestProdOrcDetail.getProjectId
      val dssLabel = requestProdOrcDetail.getDssLabel
      val username = requestProdOrcDetail.getUsername
      val orchestratorProdDetails = orchestratorService.getOrchestratorDetails(username, projectId, dssLabel)
      new ResponseProdOrcDetail(projectId, dssLabel, username, orchestratorProdDetails)

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
