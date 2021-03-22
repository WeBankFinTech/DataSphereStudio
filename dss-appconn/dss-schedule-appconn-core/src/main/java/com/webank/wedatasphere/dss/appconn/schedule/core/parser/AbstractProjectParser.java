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

package com.webank.wedatasphere.dss.appconn.schedule.core.parser;


import com.webank.wedatasphere.dss.appconn.schedule.core.entity.AbstractSchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.DSSJsonProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerProject;
import com.webank.wedatasphere.dss.standard.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSJsonFlow;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;

/**
 * Created by allenlliu on 2019/9/16.
 */
public abstract class AbstractProjectParser implements ProjectParser {
    Logger logger = LoggerFactory.getLogger(AbstractProjectParser.class);

    private FlowParser[] flowParsers;

    @Override
    public void setFlowParsers(FlowParser[] flowParsers) {
        this.flowParsers = flowParsers;
    }

    @Override
    public FlowParser[] getFlowParsers() {
        return flowParsers;
    }

    public DSSJsonProject parseToDSSJsonProject(DSSProject dssProject, List<DSSFlow> dssFlowList){
        DSSJsonProject dssJsonProject = new DSSJsonProject();
        BeanUtils.copyProperties(dssProject,dssJsonProject,"flows");

        List<DSSJsonFlow> DSSJsonFlows = dssFlowList.stream().map(this::toDSSJsonFlow).collect(Collectors.toList());
        dssJsonProject.setFlows(DSSJsonFlows);
        return dssJsonProject;
    }

    public DSSJsonFlow toDSSJsonFlow(DSSFlow DSSFlow){
        DSSJsonFlow DSSJsonFlow = new DSSJsonFlow();
        BeanUtils.copyProperties(DSSFlow, DSSJsonFlow,"children");
        DSSJsonFlow.setJson(DSSFlow.getFlowJson());
        if(DSSFlow.getChildren() != null){
            DSSJsonFlow.setChildren(DSSFlow.getChildren().stream().map(this::toDSSJsonFlow).collect(Collectors.toList()));
        }
        return DSSJsonFlow;
    }


    public SchedulerProject parseProject(DSSJsonProject project){
        AbstractSchedulerProject schedulerProject = createSchedulerProject();

        BeanUtils.copyProperties(project,schedulerProject);
        List<SchedulerFlow> schedulerFlows = project.getFlows().stream().map(Jsonflow -> invokeFlowParser(Jsonflow, getFlowParsers())).collect(Collectors.toList());
        schedulerProject.setSchedulerFlows(schedulerFlows);
        return schedulerProject;
    }

    public SchedulerFlow invokeFlowParser(DSSJsonFlow dssJsonFlow, FlowParser[] flowParsers){
        if(flowParsers.length ==0){
            logger.error("flow parser is null");
            return null;
        }
        List<FlowParser> flowParsersF = Arrays.stream(flowParsers).filter(f -> f.ifFlowCanParse(dssJsonFlow)).collect(Collectors.toList());
        // TODO: 2019/9/25  如果flowParsers数量>1 ||<=0抛出异常
        SchedulerFlow schedulerFlow = flowParsersF.get(0).parseFlow(dssJsonFlow);
        //收集所有的不分层级的flow？
        if(dssJsonFlow.getChildren() != null){
            List<SchedulerFlow> schedulerFlows = dssJsonFlow.getChildren().stream().map(f -> invokeFlowParser(f, flowParsers)).collect(Collectors.toList());
            schedulerFlow.setChildren(schedulerFlows);
        }
        return schedulerFlow;
    }

    @Override
    public SchedulerProject parseProject(DSSProject dssProject,List<DSSFlow> dssFlowList) {
        SchedulerProject schedulerProject = parseProject(parseToDSSJsonProject(dssProject,dssFlowList));
        schedulerProject.setDSSProject(dssProject);
        return schedulerProject;
    }

    protected abstract AbstractSchedulerProject createSchedulerProject();
}
