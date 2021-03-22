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

package com.webank.wedatasphere.dss.appconn.schedulis.service;

import com.webank.wedatasphere.dss.appconn.schedulis.operation.SchedulisProjectUploadOperation;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefSchedulerService;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.ProjectUploadToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.PublishToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.UploadToScheduleOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;

import java.util.List;

/**
 * @author allenlliu
 * @date 2020/12/11 15:12
 */
public class SchedulisRefSchedulerService implements RefSchedulerService {


    private AppDesc appDesc;


    private List<DSSLabel> dssLabels;

    public SchedulisRefSchedulerService(){

    }


    @Override
    public UploadToScheduleOperation<ProjectUploadToSchedulerRef> createRefUploadToScheduleOperation(PublishToSchedulerRef requestRef) {
        SchedulisProjectUploadOperation schedulisProjectUploadOperation = new SchedulisProjectUploadOperation(this.appDesc);
        schedulisProjectUploadOperation.setAppDesc(this.appDesc);
        schedulisProjectUploadOperation.setDssLabels(requestRef.getLabels());
        return schedulisProjectUploadOperation;
    }


    @Override
    public AppDesc getAppDesc() {
        return appDesc;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }


    @Override
    public void setDSSLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return this.dssLabels;
    }
}
