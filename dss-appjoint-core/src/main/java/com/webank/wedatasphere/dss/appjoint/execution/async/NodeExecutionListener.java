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

package com.webank.wedatasphere.dss.appjoint.execution.async;

import com.webank.wedatasphere.dss.appjoint.execution.common.AsyncNodeExecutionResponse;
import com.webank.wedatasphere.dss.appjoint.execution.common.CompletedNodeExecutionResponse;
import com.webank.wedatasphere.dss.appjoint.execution.common.NodeExecutionAction;
import com.webank.wedatasphere.dss.appjoint.execution.core.AppJointNode;
import com.webank.wedatasphere.dss.appjoint.execution.core.NodeContext;

/**
 * created by enjoyyin on 2019/10/8
 * Description:
 */
public interface NodeExecutionListener {

    void beforeSubmit(AppJointNode appJointNode, NodeContext nodeContext);

    void afterSubmit(AppJointNode appJointNode, NodeContext nodeContext, NodeExecutionAction action);

    void afterAsyncNodeExecutionResponse(AsyncNodeExecutionResponse response);

    void afterCompletedNodeExecutionResponse(AppJointNode appJointNode, NodeContext nodeContext, NodeExecutionAction action,
                                             CompletedNodeExecutionResponse response);
}
