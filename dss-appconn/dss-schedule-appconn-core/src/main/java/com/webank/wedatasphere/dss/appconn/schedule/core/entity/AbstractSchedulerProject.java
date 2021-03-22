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

package com.webank.wedatasphere.dss.appconn.schedule.core.entity;
import com.webank.wedatasphere.dss.standard.common.entity.project.DSSProject;

import java.util.List;

/**
 * Created by v_wbjftang on 2019/9/25.
 */
public abstract class AbstractSchedulerProject implements SchedulerProject {

    private Long id;
    private String name;
    private String description;

    private DSSProject dssProject;
    private List<SchedulerFlow> schedulerFlows;

    public List<SchedulerFlow> getSchedulerFlows() {
        return schedulerFlows;
    }

    public void setSchedulerFlows(List<SchedulerFlow> schedulerFlows) {
        this.schedulerFlows = schedulerFlows;
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public DSSProject getDSSProject() {
        return this.dssProject;
    }

    @Override
    public void setDSSProject(DSSProject dssProject) {
        this.dssProject = dssProject;
    }
}
