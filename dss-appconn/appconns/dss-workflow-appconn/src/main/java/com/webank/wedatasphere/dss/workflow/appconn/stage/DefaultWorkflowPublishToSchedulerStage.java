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

package com.webank.wedatasphere.dss.workflow.appconn.stage;

import com.webank.wedatasphere.dss.appconn.schedule.core.SchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.schedule.core.standard.SchedulerStructureStandard;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnService;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefSchedulerService;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.AbstractPublishToSchedulerStage;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.ProjectPublishToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.ProjectUploadToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.PublishToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.RefScheduleOperation;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.UploadToScheduleOperation;
import com.webank.wedatasphere.dss.standard.app.development.stage.GetRefStage;
import com.webank.wedatasphere.dss.standard.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.appconn.ref.DefaultProjectUploadToSchedulerRef;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.linkis.DataWorkCloudApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author allenlliu
 * @date 2020/11/30 15:37
 */
public class DefaultWorkflowPublishToSchedulerStage extends AbstractPublishToSchedulerStage {


    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWorkflowPublishToSchedulerStage.class);


    private AppConnService appConnService = DataWorkCloudApplication.getApplicationContext().getBean(AppConnService.class);

    private DevelopmentService service;

    @Override
    public GetRefStage createGetRefStage() {
        return new DefaultWorkflowGetRefStage();
    }


    @Override
    public ResponseRef publishToScheduler(PublishToSchedulerRef ref) throws ExternalOperationFailedException {

        // SchedulerAppConn appConn = (SchedulerAppConn) appConnService.getAppConn(WorkflowAppConnConstant.DSS_SCHEDULE_APPCONN_NAME.getValue());
        SchedulerAppConn appConn = (SchedulerAppConn)appConnService.getAppConn("dolphinscheduler");
        if (null != appConn) {
            SchedulerStructureStandard schedulerStructureStandard
                = (SchedulerStructureStandard)appConn.getAppStandards()
                .stream()
                .filter(appStandard -> appStandard instanceof SchedulerStructureStandard)
                .findAny()
                .orElse(null);

            if (null != schedulerStructureStandard) {
                RefSchedulerService refSchedulerService = schedulerStructureStandard.getSchedulerService();
                refSchedulerService.setAppDesc(appConn.getAppDesc());
                refSchedulerService.setDSSLabels(ref.getLabels());
                if (null != refSchedulerService) {
                    UploadToScheduleOperation uploadToScheduleOperation
                        = refSchedulerService.createRefUploadToScheduleOperation(ref);

                    //构建ScheduleProject
                    if (ref instanceof ProjectPublishToSchedulerRef) {
                        ProjectPublishToSchedulerRef projectPublishToSchedulerRef = (ProjectPublishToSchedulerRef)ref;
                        DSSProject dssProject = (DSSProject)projectPublishToSchedulerRef.getProject();
                        GetRefStage getRefStage = this.createGetRefStage();
                        List<DSSFlow> dssFlowList = new ArrayList<>();
                        for (Long rootFlowId : projectPublishToSchedulerRef.getOrcAppIds()) {
                            DSSFlow dssFlow = getRefStage.getDssFlowById(projectPublishToSchedulerRef.getUserName(),
                                rootFlowId, projectPublishToSchedulerRef.getLabels());
                            dssFlowList.add(dssFlow);
                        }

                        ProjectUploadToSchedulerRef projectUploadToSchedulerRef
                            = new DefaultProjectUploadToSchedulerRef();
                        projectUploadToSchedulerRef.setDSSProject(dssProject);
                        projectUploadToSchedulerRef.setDSSFlowList(dssFlowList);
                        projectUploadToSchedulerRef.setWorkspace(projectPublishToSchedulerRef.getWorkspace());
                        projectUploadToSchedulerRef.setUserName(projectPublishToSchedulerRef.getUserName());
                        return uploadToScheduleOperation.publish(projectUploadToSchedulerRef);
                    }
                } else {
                    LOGGER.error("scheduler Structure standard is null can not continue");
                    DSSExceptionUtils.dealErrorException(60059, "scheduler Structure standard is null can not continue", ExternalOperationFailedException.class);
                }
            }

        }
        return null;
    }

    @Override
    public void setSchedulerRefOperation(RefScheduleOperation refScheduleOperation) {

    }
}
