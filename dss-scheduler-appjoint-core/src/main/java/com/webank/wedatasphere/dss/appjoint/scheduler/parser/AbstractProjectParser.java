/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appjoint.scheduler.parser;

import com.webank.wedatasphere.dss.appjoint.scheduler.entity.AbstractSchedulerProject;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerProjectVersionForFlows;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerProject;
import com.webank.wedatasphere.dss.common.entity.flow.DSSFlow;
import com.webank.wedatasphere.dss.common.entity.flow.DSSJSONFlow;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.entity.project.DSSJSONProject;
import com.webank.wedatasphere.dss.common.entity.project.ProjectVersionForFlows;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by enjoyyin on 2019/9/16.
 */
public abstract class AbstractProjectParser implements ProjectParser {

    private FlowParser[] flowParsers;

    @Override
    public void setFlowParsers(FlowParser[] flowParsers) {
        this.flowParsers = flowParsers;
    }

    @Override
    public FlowParser[] getFlowParsers() {
        return flowParsers;
    }

    public DSSJSONProject parseToDssJsonProject(DSSProject dssProject){
        DSSJSONProject dssJsonProject = new DSSJSONProject();
        BeanUtils.copyProperties(dssProject, dssJsonProject,"flows","projectVersions");
        List<? extends DSSFlow> dwsFlows = dssProject.getFlows();
        List<DSSJSONFlow> dssJsonFlows = dwsFlows.stream().map(this::toDssJsonFlow).collect(Collectors.toList());
        dssJsonProject.setFlows(dssJsonFlows);
        return dssJsonProject;
    }

    private DSSJSONFlow toDssJsonFlow(DSSFlow dssFlow){
        DSSJSONFlow dssJsonFlow = new DSSJSONFlow();
        BeanUtils.copyProperties(dssFlow, dssJsonFlow,"children","flowVersions");
        dssJsonFlow.setJson(dssFlow.getLatestVersion().getJson());
        if(dssFlow.getChildren() != null){
            dssJsonFlow.setChildren(dssFlow.getChildren().stream().map(this::toDssJsonFlow).collect(Collectors.toList()));
        }
        return dssJsonFlow;
    }


    public SchedulerProject parseProject(DSSJSONProject project){
        AbstractSchedulerProject schedulerProject = createSchedulerProject();
        SchedulerProjectVersionForFlows projectVersionForFlows = new SchedulerProjectVersionForFlows();
        schedulerProject.setProjectVersions(new ArrayList<SchedulerProjectVersionForFlows>());
        schedulerProject.addProjectVersion(projectVersionForFlows);
        BeanUtils.copyProperties(project,schedulerProject,"projectVersions");
        List<SchedulerFlow> schedulerFlows = project.getFlows().stream().map(f -> invokeFlowParser(projectVersionForFlows,f, getFlowParsers())).collect(Collectors.toList());
        schedulerProject.setSchedulerFlows(schedulerFlows);
        return schedulerProject;
    }

    private SchedulerFlow invokeFlowParser(ProjectVersionForFlows projectVersionForFlows, DSSJSONFlow dssJsonFlow, FlowParser[] flowParsers){
        List<FlowParser> flowParsersF = Arrays.stream(flowParsers).filter(f -> f.ifFlowCanParse(dssJsonFlow)).collect(Collectors.toList());
        // TODO: 2019/9/25  如果flowParsers数量>1 ||<=0抛出异常
        SchedulerFlow schedulerFlow = flowParsersF.get(0).parseFlow(dssJsonFlow);
        //收集所有的不分层级的flow？
        projectVersionForFlows.addFlow(schedulerFlow);
        if(dssJsonFlow.getChildren() != null){
            List<SchedulerFlow> schedulerFlows = dssJsonFlow.getChildren().stream().map(f -> invokeFlowParser(projectVersionForFlows,f, flowParsers)).collect(Collectors.toList());
            schedulerFlow.setChildren(schedulerFlows);
        }
        return schedulerFlow;
    }

    @Override
    public SchedulerProject parseProject(DSSProject dssProject) {
        SchedulerProject schedulerProject = parseProject(parseToDssJsonProject(dssProject));
        schedulerProject.setDssProject(dssProject);
        return schedulerProject;
    }

    protected abstract AbstractSchedulerProject createSchedulerProject();
}
