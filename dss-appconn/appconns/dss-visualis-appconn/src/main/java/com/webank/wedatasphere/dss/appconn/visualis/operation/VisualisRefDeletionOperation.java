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

package com.webank.wedatasphere.dss.appconn.visualis.operation;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;


public class VisualisRefDeletionOperation
        extends VisualisDevelopmentOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl, ResponseRef>
        implements RefDeletionOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl> {

    @Override
    public ResponseRef deleteRef(ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef) throws ExternalOperationFailedException {
        String nodeType = requestRef.getType().toLowerCase();
        logger.info("The {} of Visualis try to delete ref RefJobContent: {}.", nodeType, requestRef.getRefJobContent());
        OperationStrategyFactory.getInstance().getOperationStrategy(getAppInstance(), nodeType)
                .deleteRef(requestRef);
        return ResponseRef.newExternalBuilder().success();
    }
}
