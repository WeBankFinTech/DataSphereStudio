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

package com.webank.wedatasphere.dss.flow.execution.entrance.tuning;

import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.ShareNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.tuning.AbstractShareNodeFlowTuning;
import com.webank.wedatasphere.dss.appconn.schedule.core.tuning.NodeTuning;
import com.webank.wedatasphere.dss.flow.execution.entrance.entity.FlowExecutonSharedNode;

import org.springframework.stereotype.Component;

/**
 * Created by v_wbjftang on 2019/11/7.
 */
@Component
public class FlowExecutionFlowTuning extends AbstractShareNodeFlowTuning {
    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public SchedulerFlow tuningSchedulerFlow(SchedulerFlow schedulerFlow) {
        schedulerFlow = super.tuningSchedulerFlow(schedulerFlow);
        // TODO: 2019/11/7  这里可以setproxyuser  等等需要的参数
        return schedulerFlow;
    }

    @Override
    public Boolean ifFlowCanTuning(SchedulerFlow schedulerFlow) {
        return true ;
    }

    @Override
    protected ShareNode createShareNode() {
        return new FlowExecutonSharedNode();
    }

    @Override
    public NodeTuning[] getNodeTunings() {
        return new NodeTuning[0];
    }
}
