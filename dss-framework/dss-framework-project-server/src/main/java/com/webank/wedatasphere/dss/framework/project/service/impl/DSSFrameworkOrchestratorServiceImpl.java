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


import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.framework.project.contant.OrchestratorTypeEnum;
import com.webank.wedatasphere.dss.framework.project.contant.ProjectServerResponse;
import com.webank.wedatasphere.dss.framework.project.entity.request.OrchestratorCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.CommonOrchestratorVo;
import com.webank.wedatasphere.dss.framework.project.entity.vo.ProjectInfoVo;
import com.webank.wedatasphere.dss.framework.project.exception.DSSProjectErrorException;
import com.webank.wedatasphere.dss.framework.project.service.DSSFrameworkOrchestratorService;
import com.webank.wedatasphere.dss.framework.project.service.DSSOrchestratorService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCreateRequestRef;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCreateResponseRef;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorFrameworkAppConn;
import com.webank.wedatasphere.dss.orchestrator.core.ref.WorkflowOrchestratorCreateRequestRef;
import com.webank.wedatasphere.dss.orchestrator.core.type.OrchestratorKindEnum;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.process.DevProcessService;
import com.webank.wedatasphere.dss.standard.app.development.process.ProcessService;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * created by cooperyang on 2020/10/16
 * Description:
 */
@Component
public class DSSFrameworkOrchestratorServiceImpl implements DSSFrameworkOrchestratorService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSFrameworkOrchestratorServiceImpl.class);

    @Autowired
    private AppConnService appConnService;
    @Autowired
    private DSSOrchestratorService orchestratorService;
    @Autowired
    private DSSProjectService projectService;

    private Sender sender = Sender.getSender("dss-server");

    /**
     * 1.拿到的dss orchestrator的appconn
     * 2.然后创建
     *
     * @param orchestratorCreateRequest 创建参数
     * @return
     */
    @Override
    @SuppressWarnings("ConstantConditions")
    public CommonOrchestratorVo createOrchestrator(String username, OrchestratorCreateRequest orchestratorCreateRequest,
                                                   Workspace workspace) throws Exception {
        //判断工程是否存在,并且取出工程名称和空间名称
        ProjectInfoVo projectInfoVo = projectService.getProjectInfoById(orchestratorCreateRequest.getProjectId());
        if (projectInfoVo == null) {
            DSSExceptionUtils.dealErrorException(ProjectServerResponse.PROJECT_NOT_EXIST.getCode(),
                    ProjectServerResponse.PROJECT_NOT_EXIST.getMsg(), DSSProjectErrorException.class);
        }
        //1.去orchestratorFramework创建编排模式
        //2.将工程和orchestrator的关系存储到的数据库中
        LOGGER.info("{} begins to create a orchestrator {}", username, orchestratorCreateRequest);
        //我们是需要在开发中心进行新建，不需要在生产中心进行建工作流 需要通过label进行判断
        //新建编排模式需要将编排模式的类型等内容提交给OrchestratorFramework
        CommonOrchestratorVo orchestratorVo = new CommonOrchestratorVo();
        try {
            OrchestratorKindEnum orchestratorKindEnum = OrchestratorKindEnum.
                    getType(OrchestratorTypeEnum.getTypeByKey(orchestratorCreateRequest.getOrchestratorMode()));
            List<DSSLabel> dssLabels = orchestratorCreateRequest.getDssLabels().stream()
                    .map(DSSLabel::new).collect(Collectors.toList());
            OrchestratorCreateRequestRef orchestratorCreateRequestRef = null;
            String appconnName = "workflow";
            switch (orchestratorKindEnum) {
                case WORKFLOW:
                    orchestratorCreateRequestRef = new WorkflowOrchestratorCreateRequestRef();
                    appconnName = "workflow";
                    break;
                case COMBINED:
                    appconnName = "combined";
                    break;
                case SINGLE_TASK:
                    appconnName = "singleTask";
                    break;
                default:
                    orchestratorCreateRequestRef = new WorkflowOrchestratorCreateRequestRef();
            }
            orchestratorCreateRequestRef.setUserName(username);
            orchestratorCreateRequestRef.setDSSLabels(dssLabels);
            orchestratorCreateRequestRef.setProjectId(orchestratorCreateRequest.getProjectId());
            orchestratorCreateRequestRef.setProjectName(orchestratorCreateRequest.getProjectName());
            orchestratorCreateRequestRef.setWorkspaceName(orchestratorCreateRequest.getWorkspaceName());
            DSSOrchestratorInfo dssOrchestratorInfo = new DSSOrchestratorInfo();
            dssOrchestratorInfo.setType(orchestratorKindEnum.getName());
            dssOrchestratorInfo.setDesc(orchestratorCreateRequest.getDescription());
            dssOrchestratorInfo.setCreateTime(new Date(System.currentTimeMillis()));
            dssOrchestratorInfo.setAppConnName(appconnName);
            dssOrchestratorInfo.setName(orchestratorCreateRequest.getOrchestratorName());
            dssOrchestratorInfo.setCreator(username);
            dssOrchestratorInfo.setProjectId(orchestratorCreateRequest.getProjectId());
            dssOrchestratorInfo.setComment(orchestratorCreateRequest.getDescription());
            dssOrchestratorInfo.setSecondaryType(orchestratorCreateRequest.getOrchestratorWays().toString());
            orchestratorCreateRequestRef.setDssOrchestratorInfo(dssOrchestratorInfo);
            //根据label获取对应label的创建operation
            RefCreationOperation<OrchestratorCreateRequestRef, OrchestratorCreateResponseRef> refCreationOperation =
                    getRefCreationOperation(dssLabels);
            //调用orchestrator服务 创建编排模式、编排模式版本
            OrchestratorCreateResponseRef responseRef = refCreationOperation.createRef(orchestratorCreateRequestRef);
            LOGGER.info("operation ends to create ref, content is {}", responseRef.getContent());
            Long orchestratorId = responseRef.getOrchestratorId();
            Long versionId = responseRef.getOrchestratorVersionId();
            LOGGER.info("orchestratorId is {}, and version is {}", orchestratorId, versionId);
            orchestratorVo.setOrchestratorId(orchestratorId);
            //保存编排模式到数据库
            orchestratorService.saveOrchestrator(orchestratorCreateRequest, responseRef, username);
        } catch (final Exception e) {
            DSSExceptionUtils.dealErrorException(60033, "failed to create orchestrator", e, DSSProjectErrorException.class);
        }
        return orchestratorVo;
    }

    /**
     * 获取更新的operation
     *
     * @param dssLabels 根据label获取对应label的创建operation
     *                  todo 目前已经写死为DEV环境
     * @return
     * @throws Exception
     */
    public RefCreationOperation<OrchestratorCreateRequestRef, OrchestratorCreateResponseRef> getRefCreationOperation(List<DSSLabel> dssLabels) throws Exception {
        OrchestratorFrameworkAppConn orcAppConn = appConnService.listAppConns().stream()
                .filter(appConn -> appConn instanceof OrchestratorFrameworkAppConn)
                .map(appConn -> (OrchestratorFrameworkAppConn) appConn)
                .findFirst().orElse(null);
        if (orcAppConn == null) {
            LOGGER.error("orcAppConn is null, will not go on creating orchestrator");
            DSSExceptionUtils.dealErrorException(60028, "orcAppConn is null", DSSProjectErrorException.class);
        }

        DevelopmentIntegrationStandard standard = orcAppConn.getAppStandards().stream()
                .filter(appStandard -> appStandard instanceof DevelopmentIntegrationStandard)
                .map(appStandard -> (DevelopmentIntegrationStandard) appStandard)
                .findFirst().orElse(null);
        if (standard == null) {
            LOGGER.error("standard is null, will not go on creating orchestrator");
            DSSExceptionUtils.dealErrorException(60028, "standard is null", DSSProjectErrorException.class);
        }

//        List<ProcessService> processServices = standard.getProcessServices();
//        ProcessService processService = processServices.stream()
//                .filter(service -> service instanceof DevProcessService)
//                .findFirst().orElse(null);
        ProcessService processService = standard.getProcessService(dssLabels);
        if (processService == null) {
            LOGGER.error("processService {} is null or not a DevProcessService, stop it", processService);
            throw new ErrorException(60053, "processServiceis null or not a DevProcessService, stop it");
        }

        DevProcessService devProcessService = (DevProcessService) processService;
        RefCRUDService service = devProcessService.getRefCRUDService();
        return service.createTaskCreationOperation();
    }
}
