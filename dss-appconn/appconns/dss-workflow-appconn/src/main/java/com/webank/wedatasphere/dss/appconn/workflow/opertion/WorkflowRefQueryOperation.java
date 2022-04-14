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

import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.operation.AbstractDevelopmentOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryJumpUrlOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.QueryJumpUrlResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.OnlyDevelopmentRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationWarnException;

public class WorkflowRefQueryOperation
        extends AbstractDevelopmentOperation<OnlyDevelopmentRequestRef.QueryJumpUrlRequestRefImpl, QueryJumpUrlResponseRef>
        implements RefQueryJumpUrlOperation<OnlyDevelopmentRequestRef.QueryJumpUrlRequestRefImpl, QueryJumpUrlResponseRef> {

    @Override
    public QueryJumpUrlResponseRef query(OnlyDevelopmentRequestRef.QueryJumpUrlRequestRefImpl ref) {
        // Now only support to fetch workflow open url, not support other orchestrations.
        return ref.getDSSLabels().stream().filter(EnvDSSLabel.class::isInstance).findFirst().map(label -> {
            String urlStr = "router/workflow/editable?labels=" + ((EnvDSSLabel) label).getEnv();
            return QueryJumpUrlResponseRef.newBuilder().setJumpUrl(urlStr).success();
        }).orElseThrow(() -> new ExternalOperationWarnException(50321, "Not exists EnvDSSLabel, cannot fetch orchestration open url."));
    }

}
