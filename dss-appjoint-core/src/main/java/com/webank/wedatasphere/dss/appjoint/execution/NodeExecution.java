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

package com.webank.wedatasphere.dss.appjoint.execution;


import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.execution.common.NodeExecutionResponse;
import com.webank.wedatasphere.dss.appjoint.execution.core.AppJointNode;
import com.webank.wedatasphere.dss.appjoint.execution.core.NodeContext;
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrl;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;


/**
 * created by enjoyyin on 2019/9/25
 * Description:
 * NodeExecution
 */
public interface NodeExecution extends AppJointUrl {

    void init(java.util.Map<String, Object> params) throws AppJointErrorException;

    /**
     * 表示任务能否提交到该AppJoint去执行
     * @param appJointNode AppJointNode
     * @return true is ok while false is not
     */
    boolean canExecute(AppJointNode appJointNode, NodeContext context, Session session);

    /**
     * 相应的appJoint提交到外部系统执行
     * @param appJointNode 就是工作流节点
     * @param context 运行时参数的context
     * @param session session是为了解决将任务提交给第三方系统时解决鉴权等问题
     */
    NodeExecutionResponse execute(AppJointNode appJointNode, NodeContext context, Session session) throws AppJointErrorException;

}
