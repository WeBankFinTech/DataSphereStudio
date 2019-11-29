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

package com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.job;

import com.webank.wedatasphere.dss.linkis.node.execution.job.ReadJob;
import com.webank.wedatasphere.dss.plugins.azkaban.linkis.jobtype.conf.LinkisJobTypeConf;

import java.util.Map;

/**
 * Created by peacewong on 2019/11/3.
 */
public class AzkabanAppJointLinkisReadJob extends AzkabanAppJointLinkisJob implements ReadJob {

    private Map<String, Object> sharedNodesInfo;

    @Override
    public Map<String, Object> getSharedNodesInfo() {
        return this.sharedNodesInfo;
    }

    @Override
    public String getSharedKey(String value) {
        String projectId = getJobProps().get(LinkisJobTypeConf.PROJECT_ID);
        String flowId = getJobProps().get(LinkisJobTypeConf.FLOW_NAME);
        String flowExecId = getJobProps().get(LinkisJobTypeConf.FLOW_EXEC_ID);
        return projectId + "." + flowId + "." + flowExecId + "." + value;
    }

    @Override
    public void setSharedNodesInfo(Map<String, Object> sharedNodesInfo) {
        this.sharedNodesInfo = sharedNodesInfo;
    }

    @Override
    public String[] getShareNodeIds() {
        String nodesStr = getJobProps().get(LinkisJobTypeConf.READ_NODE_TOKEN);
        String[] nodes = nodesStr.split(",");
        return nodes;
    }
}
