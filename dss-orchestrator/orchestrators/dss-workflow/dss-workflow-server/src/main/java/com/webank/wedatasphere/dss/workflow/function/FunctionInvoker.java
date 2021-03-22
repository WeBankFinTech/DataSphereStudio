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

package com.webank.wedatasphere.dss.workflow.function;

import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.entity.AbstractAppConnNode;
import com.webank.wedatasphere.dss.workflow.service.WorkflowNodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by v_wbjftang on 2019/11/20.
 */
@Component
public class FunctionInvoker {

    @Autowired
    private WorkflowNodeService workflowNodeService;


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void nodeServiceFunction(String userName, Map<String, Object> requestBody, AbstractAppConnNode node, NodeServiceFunction function) throws IllegalAccessException, ExternalOperationFailedException, InstantiationException {
         Map<String, Object> jobContent = null;
        logger.info("appJoint NodeService is exist");
        jobContent = function.accept(userName, workflowNodeService, node, requestBody);
        node.setJobContent(jobContent);

    }


}
