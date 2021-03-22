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

package com.webank.wedatasphere.dss.framework.release.service.impl;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkErrorException;
import com.webank.wedatasphere.dss.framework.release.entity.export.ExportResult;
import com.webank.wedatasphere.dss.framework.release.entity.resource.BmlResource;
import com.webank.wedatasphere.dss.framework.release.service.ExportService;
import com.webank.wedatasphere.dss.framework.release.utils.ReleaseConf;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorExportRequestRef;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.process.DevProcessService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefExportService;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RefFactory;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * created by cooperyang on 2020/12/9
 * Description:
 */
@Service
public class ExportServiceImpl implements ExportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExportServiceImpl.class);

    private static final String ORCHESTRATOR_FRAMEWORK_NAME = ReleaseConf.ORCHESTRATOR_APPCONN_NAME.getValue();


    @Autowired
    private AppConnService appConnService;

    @Autowired
    @Qualifier("orcExportRefFactory")
    private RefFactory<OrchestratorExportRequestRef> refFactory;


    @PostConstruct
    private void init(){

    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public ExportResult export(String releaseUser, Long projectId, Long orchestratorId, Long orchestratorVersionId,
                               String projectName, String workspaceName, DSSLabel dssLabel, Workspace workspace) throws ErrorException {
        LOGGER.info("begin to export projectId {} orchestratorId {} orchestratorVersionId {} for user {}", projectId, orchestratorId, orchestratorVersionId,releaseUser);
        //获取到orchestratorFramework的appconn
        AppConn appConn = appConnService.getAppConn(ORCHESTRATOR_FRAMEWORK_NAME);
        //2.通过Label拿到对应的Service
        DevelopmentIntegrationStandard developmentIntegrationStandard = appConn.getAppStandards().
                stream().
                filter(appStandard -> appStandard instanceof DevelopmentIntegrationStandard).
                map(appStandard -> (DevelopmentIntegrationStandard)appStandard).findFirst().orElse(null);
        if (developmentIntegrationStandard == null){
            LOGGER.error("orchestrator framework development is null");
            DSSExceptionUtils.dealErrorException(60021, "orchestrator framework development is null", DSSFrameworkErrorException.class);
        }
        DevProcessService processService = developmentIntegrationStandard.
                getProcessServices().
                stream().
                filter(service -> service instanceof DevProcessService).
                map(s -> (DevProcessService)s).
                findFirst().
                orElse(null);
        //1.可以通过类型转换来进行,也可以通过反射的方式获取对应的方法,先通过类型转换的方式
        if (processService instanceof DevProcessService){
            RefExportService refExportService = processService.getRefExportService();
            RefExportOperation<OrchestratorExportRequestRef, OrchestratorExportResponseRef> refExportOperation = refExportService.createRefExportOperation();
            OrchestratorExportRequestRef requestRef = null;
            try{
                Class<?> clazz = appConn.getClass().
                        getClassLoader().
                        loadClass("com.webank.wedatasphere.dss.appconn.orchestrator.ref.DefaultOrchestratorExportRequestRef");
                requestRef = (OrchestratorExportRequestRef)clazz.newInstance();
//                requestRef = refFactory.newRef(OrchestratorExportRequestRef.class, appConn.getClass().getClassLoader(),
//                        "com.webank.wedatasphere.dss.appconn.orchestrator.ref");
            }catch(Exception e){
                LOGGER.error("failed to new ref for class {}", OrchestratorExportRequestRef.class.getName(), e);
                DSSExceptionUtils.dealErrorException(60058, "failed to new ref", e, DSSFrameworkErrorException.class);
            }

            requestRef.setOrchestratorId(orchestratorId);
            requestRef.setOrchestratorVersionId(orchestratorVersionId);
            requestRef.setDSSLabels(Collections.singletonList(dssLabel));
            requestRef.setProjectId(projectId);
            requestRef.setUserName(releaseUser);
            //todo 通过project去拿到workspaceName和projectName
            requestRef.setWorkspaceName(workspaceName);
            requestRef.setWorkspace(workspace);
            requestRef.setProjectName(projectName);
            requestRef.setAddOrcVersionFlag(true);
            OrchestratorExportResponseRef responseRef = refExportOperation.exportRef(requestRef);
            BmlResource bmlResource = BmlResource.newResource(responseRef.getResourceId(), responseRef.getBmlVersion());
            return new ExportResult(Collections.singletonList(bmlResource), responseRef.getOrchestratorVersionId());
        } else {
            LOGGER.error("processService is not a type of DevProcessService will not go on");
            return null;
        }
    }

    @Override
    public List<BmlResource> export(String releaseUser, Long projectId, Map<Long, Long> orchestratorInfoMap, DSSLabel dssLabel) {
        return null;
    }
}
