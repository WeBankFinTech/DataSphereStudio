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
import com.webank.wedatasphere.dss.common.entity.IOEnv;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkErrorException;
import com.webank.wedatasphere.dss.framework.release.entity.orchestrator.OrchestratorReleaseInfo;
import com.webank.wedatasphere.dss.framework.release.entity.project.ProjectInfo;
import com.webank.wedatasphere.dss.framework.release.entity.resource.BmlResource;
import com.webank.wedatasphere.dss.framework.release.service.ImportService;
import com.webank.wedatasphere.dss.framework.release.utils.ReleaseConf;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorImportRequestRef;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorImportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.process.ProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.ProdProcessService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefImportService;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RefFactory;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * created by cooperyang on 2020/12/9
 * Description:
 */
@Service
public class ImportServiceImpl implements ImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportServiceImpl.class);


    @Autowired
    private AppConnService appConnService;

    @Autowired
    @Qualifier("orcImportRefFactory")
    private RefFactory<OrchestratorImportRequestRef> refFactory;


    private static final String ORC_FRAMEWORK_NAME = ReleaseConf.ORCHESTRATOR_APPCONN_NAME.getValue();


    @Override
    @SuppressWarnings("ConstantConditions")
    public List<OrchestratorReleaseInfo> importOrc(String orchestratorName, String releaseUser, ProjectInfo projectInfo,
                                                   List<BmlResource> bmlResourceList, DSSLabel dssLabel, String workspaceName, Workspace workspace) throws ErrorException {
        //导入之后我们应该拿到的是导入之后的orchestrator的内容,这样我们在做同步到调度中心的时候才是同步的生产中心的内容
        LOGGER.info("Begin to import orc for project {} and orc resource is {}", projectInfo.getProjectName(), bmlResourceList);
        List<OrchestratorReleaseInfo> orchestratorReleaseInfos = new ArrayList<>();
        AppConn appConn = appConnService.getAppConn(ORC_FRAMEWORK_NAME);
        DevelopmentIntegrationStandard standard = appConn.
                getAppStandards().
                stream().
                filter(appStandard -> appStandard instanceof DevelopmentIntegrationStandard).
                map(appStandard -> (DevelopmentIntegrationStandard) appStandard).
                findFirst().
                orElse(null);
        if (standard == null) {
            LOGGER.error("developStandard is null, can not do import operation");
            DSSExceptionUtils.dealErrorException(60045, "developStandard is null, can not do import operation", DSSFrameworkErrorException.class);
        }
        ProcessService processService = standard.getProcessService(Collections.singletonList(dssLabel));
        if (processService instanceof ProdProcessService) {
            RefImportService refImportService = ((ProdProcessService) processService).getRefImportService();
            RefImportOperation<OrchestratorImportRequestRef, OrchestratorImportResponseRef> refImportOperation =
                    refImportService.createRefImportOperation();
            for (BmlResource bmlResource : bmlResourceList) {
                try {
                    OrchestratorImportRequestRef importRequestRef = refFactory.newRef(OrchestratorImportRequestRef.class,
                            appConn.getClass().getClassLoader(), "com.webank.wedatasphere.dss.appconn.orchestrator.ref");
                    importRequestRef.setResourceId(bmlResource.getResourceId());
                    importRequestRef.setBmlVersion(bmlResource.getVersion());
                    importRequestRef.setDSSLabels(Collections.singletonList(dssLabel));
                    importRequestRef.setProjectId(projectInfo.getProjectId());
                    importRequestRef.setUserName(releaseUser);
                    importRequestRef.setProjectName(projectInfo.getProjectName());
                    importRequestRef.setOrchestratorName(orchestratorName);
                    //todo workspace要提供的
                    importRequestRef.setWorkspaceName(workspaceName);
                    importRequestRef.setWorkspace(workspace);
                    importRequestRef.setSourceEnv(IOEnv.BDAP_DEV);
                    OrchestratorImportResponseRef responseRef = refImportOperation.importRef(importRequestRef);
                    String refContent = responseRef.getRefOrcContent();
                    LOGGER.error("succeed to get responseRef for projectInfo {}, refOrcId is {} ", projectInfo, responseRef.getRefOrcId());
                    //从返回的的内容中搞到导入之后的orc的信息
                    orchestratorReleaseInfos.add(OrchestratorReleaseInfo.newInstance(responseRef.getRefOrcId(), ""));
                } catch (Exception e) {
                    DSSExceptionUtils.dealErrorException(60035, "failed to import Ref for project", e, DSSFrameworkErrorException.class);
                }
            }
        } else {
            LOGGER.error("processService is not a type of ProdProcessService, it is a class of {}", processService.getClass().getName());
            DSSExceptionUtils.dealErrorException(50032, "processService is not a type of ProdProcessService", DSSFrameworkErrorException.class);
        }
        return orchestratorReleaseInfos;
    }

    @Override
    public void importOrc(ProjectInfo projectInfo) {

    }
}
