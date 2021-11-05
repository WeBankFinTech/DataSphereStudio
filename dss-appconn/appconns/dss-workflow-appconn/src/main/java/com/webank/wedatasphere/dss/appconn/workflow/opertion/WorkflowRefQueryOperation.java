/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appconn.workflow.opertion;

import com.webank.wedatasphere.dss.appconn.workflow.ref.WorkflowUrlResponseRef;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.OpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkflowRefQueryOperation implements RefQueryOperation<OpenRequestRef> {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private DevelopmentService developmentService;

    @Override
    public WorkflowUrlResponseRef query(OpenRequestRef ref) throws ExternalOperationFailedException {
        EnvDSSLabel label = (EnvDSSLabel) ref.getDSSLabels().stream().filter(EnvDSSLabel.class::isInstance)
                .findFirst().orElseThrow(() -> new DSSRuntimeException(50321, "Not exists EnvDSSLabel."));
        String urlStr = "router/workflow/editable?labels=" + label.getEnv();
        if (LOGGER.isDebugEnabled()){
            LOGGER.debug("url for  {} is {}", ref.getName(), urlStr);
        }
        WorkflowUrlResponseRef workflowUrlResponseRef = new WorkflowUrlResponseRef();
        workflowUrlResponseRef.setUrl(urlStr);
        return workflowUrlResponseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService =service;
    }
}
