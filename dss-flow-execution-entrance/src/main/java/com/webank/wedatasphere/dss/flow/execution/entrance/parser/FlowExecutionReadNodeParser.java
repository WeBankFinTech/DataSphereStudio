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

package com.webank.wedatasphere.dss.flow.execution.entrance.parser;

import com.webank.wedatasphere.dss.appjoint.scheduler.entity.ReadNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appjoint.scheduler.parser.AbstractReadNodeParser;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.flow.execution.entrance.entity.FlowExecutionNode;
import com.webank.wedatasphere.dss.flow.execution.entrance.entity.FlowExecutonReadNode;
import com.webank.wedatasphere.dss.flow.execution.entrance.utils.FlowExecutionUtils;


import org.springframework.stereotype.Component;

/**
 * Created by peacewong on 2019/11/7.
 */
@Component
public class FlowExecutionReadNodeParser extends AbstractReadNodeParser {
    @Override
    public int getOrder() {
        return 2;
    }

    @Override
    public Boolean ifNodeCanParse(DSSNode dssNode) {
        return FlowExecutionUtils.isReadNode(dssNode.getNodeType());
    }

    @Override
    protected ReadNode createReadNode() {
        return new FlowExecutonReadNode();
    }

    @Override
    protected SchedulerNode createSchedulerNode() {
        return new FlowExecutionNode();
    }
}
