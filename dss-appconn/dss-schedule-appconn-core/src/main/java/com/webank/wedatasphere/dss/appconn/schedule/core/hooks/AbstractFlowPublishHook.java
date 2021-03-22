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

package com.webank.wedatasphere.dss.appconn.schedule.core.hooks;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerFlow;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerNode;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import java.util.Arrays;
import java.util.List;


/**
 * Created by v_wbjftang on 2019/9/25.
 */
public abstract class AbstractFlowPublishHook implements FlowPublishHook {

    private NodePublishHook[] nodePublishHooks;

    @Override
    public void setNodeHooks(NodePublishHook[] nodePublishHooks) {
        this.nodePublishHooks = nodePublishHooks;
    }

    @Override
    public NodePublishHook[] getNodeHooks() {
        return this.nodePublishHooks;
    }

    @Override
    public void prePublish(SchedulerFlow flow) throws DSSErrorException {
        // 调用nodeHook
        List<SchedulerNode> schedulerNodes = flow.getSchedulerNodes();
        Arrays.stream(getNodeHooks()).forEach(h -> invokePreNodePublishHook(schedulerNodes,h));
        //递归调用自身方法
        if(flow.getChildren() !=null){
            flow.getChildren().forEach(DSSExceptionUtils.handling(this::prePublish));
        }
    }

    @Override
    public void postPublish(SchedulerFlow flow) {
        // TODO: 2019/9/25 递归调用nodepublishHook
    }

    private void invokePreNodePublishHook(List<SchedulerNode> schedulerNodes, NodePublishHook nodePublishHook){
        schedulerNodes.forEach(DSSExceptionUtils.handling(nodePublishHook::prePublish));
    }
}
