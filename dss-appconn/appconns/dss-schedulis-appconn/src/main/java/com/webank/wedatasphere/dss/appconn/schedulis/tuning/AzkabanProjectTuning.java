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

package com.webank.wedatasphere.dss.appconn.schedulis.tuning;

import com.webank.wedatasphere.dss.appconn.schedulis.conf.AzkabanConf;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanSchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanSchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.tuning.AbstractProjectTuning;
import com.webank.wedatasphere.dss.appconn.schedule.core.tuning.FlowTuning;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by v_wbjftang on 2019/9/26.
 */

public class AzkabanProjectTuning extends AbstractProjectTuning {

    public AzkabanProjectTuning(){
        ArrayList<FlowTuning> list = new ArrayList<>();
        list.add(new LinkisAzkabanFlowTuning());
        list.add(new LinkisShareNodeFlowTuning());
        FlowTuning[] flowTunings =new FlowTuning[list.size()];
        setFlowTunings(list.toArray(flowTunings));
    }

    @Override
    public SchedulerProject tuningSchedulerProject(SchedulerProject schedulerProject) {
        if(ifProjectCanTuning(schedulerProject)){
            AzkabanSchedulerProject project = (AzkabanSchedulerProject) schedulerProject;
            //1.给AzkabanSchedulerProject的storepath赋值
            assignStorePath(project);
            //2.给他的rootflow的storepath也赋值
            List<SchedulerFlow> schedulerFlows = project.getSchedulerFlows();
            schedulerFlows.forEach(flow ->setRootFlowStorePath(project.getStorePath(),flow));
        }
        return super.tuningSchedulerProject(schedulerProject);
    }

    public void setRootFlowStorePath(String projectStorePath,SchedulerFlow schedulerFlow){
        AzkabanSchedulerFlow azkabanSchedulerFlow = (AzkabanSchedulerFlow) schedulerFlow;
        azkabanSchedulerFlow.setStorePath(projectStorePath + File.separator + azkabanSchedulerFlow.getName());
    }


    @Override
    public void setFlowTunings(FlowTuning[] flowTunings) {
        super.setFlowTunings(flowTunings);
    }


    public boolean ifProjectCanTuning(SchedulerProject schedulerProject){
        return schedulerProject instanceof AzkabanSchedulerProject;
    }

    private void assignStorePath(AzkabanSchedulerProject azkabanSchedulerProject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(AzkabanSchedulerProject.DATE_FORMAT);
        Date date = new Date();
        String dateStr = dateFormat.format(date);
        String userName = azkabanSchedulerProject.getDSSProject().getUsername();
        String name = azkabanSchedulerProject.getName();
        String storePath = AzkabanConf.DEFAULT_STORE_PATH.getValue() + File.separator + userName
                + File.separator + dateStr + File.separator +name;
        azkabanSchedulerProject.setStorePath(storePath);
    }
}
