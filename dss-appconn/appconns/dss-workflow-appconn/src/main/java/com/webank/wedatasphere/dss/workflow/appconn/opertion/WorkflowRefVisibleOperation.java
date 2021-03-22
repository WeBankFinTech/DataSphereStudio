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

package com.webank.wedatasphere.dss.workflow.appconn.opertion;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.query.RefVisibleOperation;
import com.webank.wedatasphere.dss.standard.app.development.query.UrlResponseRef;
import com.webank.wedatasphere.dss.standard.common.desc.DSSLabelUtils;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.appconn.ref.WorkflowOpenRequestRef;
import com.webank.wedatasphere.dss.workflow.appconn.ref.WorkflowUrlResponseRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author allenlliu
 * @date 2020/12/23 15:13
 */
public class WorkflowRefVisibleOperation implements RefVisibleOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkflowRefVisibleOperation.class);

    private DevelopmentService service;

    @Override
    public UrlResponseRef getHomePage(RequestRef ref) throws ExternalOperationFailedException {
        return null;
    }

    @Override
    public WorkflowUrlResponseRef getRefIFrame(RequestRef ref) throws ExternalOperationFailedException {
        String urlStr = "";
        if (ref instanceof WorkflowOpenRequestRef) {
            WorkflowOpenRequestRef workflowOpenRequestRef = (WorkflowOpenRequestRef) ref;
            if (DSSLabelUtils.belongToDev(workflowOpenRequestRef.getDSSLabels())) {
                urlStr = "router/workflow/editable?labels=dev";
            } else {
                urlStr = "router/workflow/readonly?labels=prod";
            }
        }
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("url for  {} is {}", ref.getName(), urlStr);
        }
        WorkflowUrlResponseRef workflowUrlResponseRef = new WorkflowUrlResponseRef();
        workflowUrlResponseRef.setUrl(urlStr);
        return workflowUrlResponseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.service = service;
    }


}
