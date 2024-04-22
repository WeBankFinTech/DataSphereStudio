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

package com.webank.wedatasphere.dss.framework.project.service.impl;

import com.google.gson.Gson;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.utils.*;
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkErrorException;
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.framework.project.entity.response.BatchExportResult;
import com.webank.wedatasphere.dss.framework.project.entity.response.ExportResult;
import com.webank.wedatasphere.dss.framework.project.service.ExportService;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestAddVersionAfterPublish;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorBaseInfo;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.RefExportService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import org.apache.commons.io.FileUtils;
import org.apache.linkis.common.exception.ErrorException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import org.apache.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * created by cooperyang on 2020/12/9
 * Description:
 */
@Service
public class ExportServiceImpl implements ExportService {
    private final String metaFileName = "meta.json";

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportServiceImpl.class);

    private static final String ORCHESTRATOR_FRAMEWORK_NAME = "orchestrator-framework";
    @Autowired
    @Qualifier("projectBmlService")
    private BMLService bmlService;


    @Override
    @SuppressWarnings("ConstantConditions")
    public ExportResult export(String releaseUser, Long projectId, OrchestratorInfo orchestrator,
                               String projectName, DSSLabel dssLabel, Workspace workspace) throws ErrorException {
        LOGGER.info("begin to export projectId {} orchestratorId {} orchestratorVersionId {} for user {}", projectId,
                orchestrator.getOrchestratorId(), orchestrator.getOrchestratorVersionId(), releaseUser);
        //获取到orchestratorFramework的appconn
        AppConn appConn = AppConnManager.getAppConnManager().getAppConn(ORCHESTRATOR_FRAMEWORK_NAME);
        //2.通过Label拿到对应的Service
        DevelopmentIntegrationStandard developmentIntegrationStandard = ((OnlyDevelopmentAppConn) appConn).getOrCreateDevelopmentStandard();
        if (developmentIntegrationStandard == null) {
            LOGGER.error("orchestrator framework development is null");
            DSSExceptionUtils.dealErrorException(60021, "orchestrator framework development is null", DSSFrameworkErrorException.class);
        }
        List<DSSLabel> dssLabelList = new ArrayList<>();
        dssLabelList.add(dssLabel);
        AppInstance appInstance = appConn.getAppDesc().getAppInstancesByLabels(dssLabelList).get(0);
        RefExportService refExportService = developmentIntegrationStandard.getRefExportService(appInstance);
        RefExportOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl> refExportOperation = refExportService.getRefExportOperation();
        ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef = new ThirdlyRequestRef.RefJobContentRequestRefImpl();
        Long orchestratorId = orchestrator.getOrchestratorId();
        Long orchestratorVersionId = orchestrator.getOrchestratorVersionId();
        requestRef.setRefJobContent(MapUtils.newCommonMap(OrchestratorRefConstant.ORCHESTRATOR_ID_KEY, orchestratorId,
                OrchestratorRefConstant.ORCHESTRATOR_VERSION_ID_KEY, orchestratorVersionId));
        requestRef.setDSSLabels(Collections.singletonList(dssLabel));
        requestRef.setRefProjectId(projectId);
        requestRef.setUserName(releaseUser);
        requestRef.setWorkspace(workspace);
        requestRef.setProjectName(projectName);
        ExportResponseRef responseRef = refExportOperation.exportRef(requestRef);
        BmlResource bmlResource = new BmlResource((String) responseRef.getResourceMap().get(ImportRequestRef.RESOURCE_ID_KEY),
                (String) responseRef.getResourceMap().get(ImportRequestRef.RESOURCE_VERSION_KEY));
        return new ExportResult(bmlResource, (Long) responseRef.getResourceMap().get(OrchestratorRefConstant.ORCHESTRATOR_VERSION_ID_KEY));

    }

    @Override
    public String batchExport(String userName, Long projectId, List<OrchestratorBaseInfo> orchestrators,
                                         String projectName, DSSLabel dssLabel, Workspace workspace) throws ErrorException {
        if(orchestrators==null||orchestrators.isEmpty()){
            throw new DSSRuntimeException("workflow list is empty,nothing to export.(导出的工作流列表为空，没有任何工作流可以导出)");
        }
        String exportSaveBasePath = IoUtils.generateTempIOPath(userName);
        for (OrchestratorBaseInfo orchestrator : orchestrators) {
            OrchestratorInfo orchestratorInfo = new OrchestratorInfo(orchestrator.getOrchestratorId(), orchestrator.getOrchestratorVersionId());
            BmlResource bmlOneOrc= export(userName, projectId, orchestratorInfo,
                    projectName, dssLabel, workspace)
                    .getBmlResource();
            String orcPath=exportSaveBasePath+orchestrator.getOrchestratorName()+".zip";
            bmlService.downloadToLocalPath(userName,bmlOneOrc.getResourceId(),bmlOneOrc.getVersion(),orcPath);
            LOGGER.info("export orchestrator file locate at {}",orcPath);
            ZipHelper.unzipFile(orcPath, exportSaveBasePath,true);
        }
        return IoUtils.addFileSeparator(exportSaveBasePath, projectName);
    }

    @Override
    public Long addVersionAfterPublish(String releaseUser, Long projectId, Long orchestratorId,
                                       Long orchestratorVersionId, String projectName, Workspace workspace,
                                       DSSLabel dssLabel, String comment) {
        List<DSSLabel> dssLabelList = new ArrayList<>();
        dssLabelList.add(dssLabel);
        RequestAddVersionAfterPublish requestAddVersionAfterPublish = new RequestAddVersionAfterPublish(releaseUser, workspace,
                orchestratorId, orchestratorVersionId, projectName, dssLabelList, comment);
        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender(dssLabelList);
        return RpcAskUtils.processAskException(sender.ask(requestAddVersionAfterPublish), Long.class, RequestAddVersionAfterPublish.class);
    }

}
