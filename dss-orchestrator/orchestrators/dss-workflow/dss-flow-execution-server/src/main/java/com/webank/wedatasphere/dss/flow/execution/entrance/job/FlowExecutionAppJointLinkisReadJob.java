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

package com.webank.wedatasphere.dss.flow.execution.entrance.job;


import com.webank.wedatasphere.dss.appconn.schedule.core.entity.ReadNode;
import com.webank.wedatasphere.dss.flow.execution.entrance.conf.FlowExecutionEntranceConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.job.ReadJob;

import java.util.Map;

/**
 * Created by johnnwang on 2019/11/3.
 */
public class FlowExecutionAppJointLinkisReadJob extends FlowExecutionAppJointLinkisJob implements ReadJob {

    private Map<String, Object> sharedNodesInfo;

    private ReadNode readNode;

    public void setReadNode(ReadNode readNode){
        this.readNode = readNode;
    }

    @Override
    public Map<String, Object> getSharedNodesInfo() {
        return this.sharedNodesInfo;
    }

    @Override
    public String getSharedKey(String value) {
        String projectId = getJobProps().get(FlowExecutionEntranceConfiguration.PROJECT_NAME());
        String flowId = getJobProps().get(FlowExecutionEntranceConfiguration.FLOW_NAME());
        String flowExecId = getJobProps().get(FlowExecutionEntranceConfiguration.FLOW_EXEC_ID());
        return projectId + "." + flowId + "." + flowExecId + "." + value;
    }

    @Override
    public void setSharedNodesInfo(Map<String, Object> sharedNodesInfo) {
        this.sharedNodesInfo = sharedNodesInfo;
    }

    @Override
    public String[] getShareNodeIds() {
        return this.readNode.getShareNodeIds();
    }
}
