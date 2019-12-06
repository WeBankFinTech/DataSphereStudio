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

package com.webank.wedatasphere.dss.appjoint.scheduler.entity;

import com.webank.wedatasphere.dss.common.entity.flow.Flow;
import com.webank.wedatasphere.dss.common.entity.project.ProjectVersionForFlows;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by enjoyyin on 2019/9/26.
 */
public class SchedulerProjectVersionForFlows implements ProjectVersionForFlows {

    private List<SchedulerFlow> allSchedulerFlows = new ArrayList<>();

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public void setVersion() {

    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public void setComment(String comment) {

    }

    @Override
    public List<SchedulerFlow> getFlows() {
        return this.allSchedulerFlows;
    }

    @Override
    public void setFlows(List<? extends Flow> flows) {
        this.allSchedulerFlows = flows.stream().map(f ->(SchedulerFlow)f).collect(Collectors.toList());
    }

    @Override
    public void addFlow(Flow flow) {
        this.allSchedulerFlows.add((SchedulerFlow) flow);
    }

    @Override
    public void removeFlow(Flow flow) {

    }
}
