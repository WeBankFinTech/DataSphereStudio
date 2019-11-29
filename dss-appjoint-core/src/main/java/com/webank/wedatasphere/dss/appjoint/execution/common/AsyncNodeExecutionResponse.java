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

package com.webank.wedatasphere.dss.appjoint.execution.common;

import com.webank.wedatasphere.dss.appjoint.execution.async.NodeExecutionResponseListener;
import com.webank.wedatasphere.dss.appjoint.execution.core.LongTermNodeExecution;
import com.webank.wedatasphere.dss.appjoint.execution.core.AppJointNode;
import com.webank.wedatasphere.dss.appjoint.execution.core.NodeContext;

import java.util.ArrayList;
import java.util.List;

/**
 * created by enjoyyin on 2019/9/25
 * Description:
 */
public class AsyncNodeExecutionResponse implements NodeExecutionResponse {

    private NodeExecutionAction action;
    private LongTermNodeExecution nodeExecution;
    private AppJointNode appJointNode;
    private NodeContext nodeContext;
    private boolean isCompleted = false;
    private long startTime = System.currentTimeMillis();
    private long askStatePeriod = 1000;
    private long maxLoopTime = -1;

    /**
     * 通过监听的方式，任务一旦完成，我们就通知上层
     */
    private List<NodeExecutionResponseListener> listener = new ArrayList<>();


    public NodeExecutionAction getAction() {
        return action;
    }

    public void setAction(NodeExecutionAction action) {
        this.action = action;
    }

    public void addListener(NodeExecutionResponseListener responseListener){
        this.listener.add(responseListener);
    }

    public List<NodeExecutionResponseListener> getListeners(){
        return this.listener;
    }

    public AppJointNode getAppJointNode() {
        return appJointNode;
    }

    public void setAppJointNode(AppJointNode appJointNode) {
        this.appJointNode = appJointNode;
    }

    public NodeContext getNodeContext() {
        return nodeContext;
    }

    public void setNodeContext(NodeContext nodeContext) {
        this.nodeContext = nodeContext;
    }

    public LongTermNodeExecution getNodeExecution() {
        return nodeExecution;
    }

    public void setNodeExecution(LongTermNodeExecution nodeExecution) {
        this.nodeExecution = nodeExecution;
    }

    public long getAskStatePeriod() {
        return askStatePeriod;
    }

    public void setAskStatePeriod(long askStatePeriod) {
        this.askStatePeriod = askStatePeriod;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public long getMaxLoopTime() {
        return maxLoopTime;
    }

    public void setMaxLoopTime(long maxLoopTime) {
        this.maxLoopTime = maxLoopTime;
    }

    public long getStartTime() {
        return startTime;
    }
}
