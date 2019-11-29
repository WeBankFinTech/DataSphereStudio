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


import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.ShareNode;

/**
 * Created by peacewong on 2019/11/6.
 */
public class FlowExecutonSharedNode extends FlowExecutionNode implements ShareNode {

    private SchedulerNode schedulerNode;
    private int shareTimes;

    @Override
    public int getShareTimes() {
        return this.shareTimes;
    }

    @Override
    public void setShareTimes(int i) {
        this.shareTimes = i;
    }

    @Override
    public SchedulerNode getSchedulerNode() {
        return this.schedulerNode;
    }

    @Override
    public void setSchedulerNode(SchedulerNode schedulerNode) {
        this.schedulerNode = schedulerNode;
    }
}
