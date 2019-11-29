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

package com.webank.wedatasphere.dss.flow.execution.entrance.entity;


import com.webank.wedatasphere.dss.appjoint.scheduler.entity.ReadNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;

/**
 * Created by peacewong on 2019/11/6.
 */
public class FlowExecutonReadNode extends FlowExecutionNode implements ReadNode {

    private SchedulerNode schedulerNode;
    private String[] nodeIds;

    @Override
    public String[] getShareNodeIds() {
        return this.nodeIds;
    }

    @Override
    public void setShareNodeIds(String[] strings) {
        this.nodeIds = strings;
    }

    @Override
    public SchedulerNode getSchedulerNode() {
        return schedulerNode;
    }

    @Override
    public void setSchedulerNode(SchedulerNode schedulerNode) {
        this.schedulerNode = schedulerNode;
    }
}
