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

package com.webank.wedatasphere.dss.appjoint.execution.core;

import com.webank.wedatasphere.dss.appjoint.execution.NodeExecution;
import com.webank.wedatasphere.dss.appjoint.execution.async.NodeExecutionListener;
import com.webank.wedatasphere.dss.appjoint.execution.common.*;
import com.webank.wedatasphere.dss.appjoint.execution.scheduler.LongTermNodeExecutionScheduler;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * created by enjoyyin on 2019/9/26
 * Description: LongTermNodeExecution 是一个
 */
public abstract class LongTermNodeExecution implements NodeExecution {

    private static final Logger logger = LoggerFactory.getLogger(LongTermNodeExecution.class);

    private List<NodeExecutionListener> nodeExecutionListener = new ArrayList<NodeExecutionListener>();
    private LongTermNodeExecutionScheduler scheduler = SchedulerManager.getScheduler();

    public void addNodeExecutionListener(NodeExecutionListener nodeExecutionListener) {
        this.nodeExecutionListener.add(nodeExecutionListener);
    }

    public LongTermNodeExecutionScheduler getScheduler() {
        return scheduler;
    }

    public void setScheduler(LongTermNodeExecutionScheduler scheduler) {
        this.scheduler = scheduler;
    }

    protected abstract NodeExecutionAction submit(AppJointNode appJointNode, NodeContext nodeContext, Session session);

    public abstract NodeExecutionState state(NodeExecutionAction action);

    public abstract CompletedNodeExecutionResponse result(NodeExecutionAction action, NodeContext nodeContext);



    @Override
    public NodeExecutionResponse execute(AppJointNode appJointNode, NodeContext context, Session session) {
        nodeExecutionListener.forEach(l -> l.beforeSubmit(appJointNode, context));
        NodeExecutionAction action = submit(appJointNode, context, session);
        nodeExecutionListener.forEach(l -> l.afterSubmit(appJointNode, context, action));
        NodeExecutionState state = state(action);
        if(state != null && state.isCompleted()) {
            CompletedNodeExecutionResponse response = result(action, context);
            nodeExecutionListener.forEach(l -> l.afterCompletedNodeExecutionResponse(appJointNode, context, action, response));
            return response;
        } else {
            AsyncNodeExecutionResponse response = createAsyncNodeExecutionResponse(appJointNode, context, action);
            response.setNodeExecution(this);
            response.addListener(r -> {
                nodeExecutionListener.forEach(l -> l.afterCompletedNodeExecutionResponse(appJointNode, context, action, r));
            });
            nodeExecutionListener.forEach(l -> l.afterAsyncNodeExecutionResponse(response));
            if(scheduler != null) {
                scheduler.addAsyncResponse(response);
            }
            return response;
        }
    }

    protected AsyncNodeExecutionResponse createAsyncNodeExecutionResponse(AppJointNode appJointNode, NodeContext context, NodeExecutionAction action) {
        AsyncNodeExecutionResponse response =  new AsyncNodeExecutionResponse();
        response.setAction(action);
        response.setAppJointNode(appJointNode);
        response.setNodeContext(context);
        return response;
    }

}
