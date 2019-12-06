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

package com.webank.wedatasphere.dss.server.operate;


import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.execution.core.CommonAppJointNode;
import com.webank.wedatasphere.dss.appjoint.service.NodeService;
import com.webank.wedatasphere.dss.server.function.FunctionInvoker;
import com.webank.wedatasphere.dss.server.function.FunctionPool;
import com.webank.wedatasphere.dss.server.function.NodeServiceFunction;
import com.webank.wedatasphere.dss.server.service.DWSFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AppJointNodeOperate implements Operate {

    @Autowired
    private FunctionInvoker functionInvoker;

    @Override
    public boolean canInvokeOperate(Op op) {
        return !"workflow.subflow".equals(op.getNodeType());
    }

    @Override
    public void add(DWSFlowService dwsFlowService, Op op) throws AppJointErrorException {
        invokeNodeServiceFunction(op,FunctionPool.createNode);
    }

    @Override
    public void update(DWSFlowService dwsFlowService,Op op) throws AppJointErrorException {
        invokeNodeServiceFunction(op,FunctionPool.updateNode);
    }

    @Override
    public void delete(DWSFlowService dwsFlowService,Op op) throws AppJointErrorException {
        invokeNodeServiceFunction(op, FunctionPool.deleteNode);
    }

    private void invokeNodeServiceFunction(Op op,NodeServiceFunction function) throws AppJointErrorException {
        String userName = op.getParams().get("userName").toString();
        Long projectID  = Long.valueOf(op.getParams().get("projectID").toString());
        op.getParams().put("id",op.getId());
        CommonAppJointNode node = new CommonAppJointNode();
        node.setProjectId(projectID);
        node.setNodeType(op.getNodeType());
        functionInvoker.nodeServiceFunction(userName,op.getParams(),node,function);
    }



}
