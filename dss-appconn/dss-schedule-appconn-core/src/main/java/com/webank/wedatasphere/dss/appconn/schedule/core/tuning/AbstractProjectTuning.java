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

package com.webank.wedatasphere.dss.appconn.schedule.core.tuning;



import com.webank.wedatasphere.dss.appconn.schedule.core.entity.AbstractSchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerProject;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by v_wbjftang on 2019/9/25.
 */
public abstract class AbstractProjectTuning implements ProjectTuning {

    private FlowTuning [] flowTunings;

    @Override
    public void setFlowTunings(FlowTuning[] flowTunings) {
        this.flowTunings = flowTunings;
    }

    @Override
    public FlowTuning[] getFlowTunings() {
        return this.flowTunings;
    }

    @Override
    public SchedulerProject tuningSchedulerProject(SchedulerProject schedulerProject) {
        // TODO: 2019/9/25 这里强转是因为没有把 schedulerProject由接口修改为普通类
        // TODO: 2019/9/25 这里如果flow
        AbstractSchedulerProject abstractSchedulerProject = (AbstractSchedulerProject) schedulerProject;
        List<SchedulerFlow> schedulerFlows = abstractSchedulerProject.getSchedulerFlows();
        List<FlowTuning> flowTunings = Arrays.stream(getFlowTunings()).sorted((t1, t2) -> t2.getOrder() - t1.getOrder()).collect(Collectors.toList());
        for (FlowTuning flowTuning : flowTunings) {
            schedulerFlows = targetFlowTuning(flowTuning,schedulerFlows);
        }
        abstractSchedulerProject.setSchedulerFlows(schedulerFlows);
        return abstractSchedulerProject;
    }


    public SchedulerFlow  tuningSchedulerFlow(SchedulerFlow schedulerFlow){

        List<FlowTuning> flowTunings = Arrays.stream(getFlowTunings()).sorted((t1, t2) -> t2.getOrder() - t1.getOrder()).collect(Collectors.toList());
        for (FlowTuning flowTuning : flowTunings) {
            invokeFlowTuning(flowTuning,schedulerFlow);
        }
        return schedulerFlow;
    }




    private List<SchedulerFlow> targetFlowTuning(FlowTuning flowTuning,List<SchedulerFlow> schedulerFlows){
        return schedulerFlows.stream().map(flow ->invokeFlowTuning(flowTuning,flow)).collect(Collectors.toList());
    }

    private SchedulerFlow invokeFlowTuning(FlowTuning flowTuning,SchedulerFlow schedulerFlow){
        if (flowTuning.ifFlowCanTuning(schedulerFlow)){
            return flowTuning.tuningSchedulerFlow(schedulerFlow);
        }else {
            return schedulerFlow;
        }
    }
}
