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

import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public class VisualisRefExportOperation extends VisualisDevelopmentOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl, ExportResponseRef>
        implements RefExportOperation<ThirdlyRequestRef.RefJobContentRequestRefImpl> {

    @Override
    public ExportResponseRef exportRef(ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + URLUtils.projectUrl + "/export";
        String nodeType = requestRef.getType().toLowerCase();
        logger.info("The {} of Visualis try to export ref RefJobContent: {} in url {}.", nodeType, requestRef.getRefJobContent(), url);
        DSSPostAction visualisPostAction = new DSSPostAction();
        visualisPostAction.setUser(requestRef.getUserName());
        visualisPostAction.addRequestPayload("projectId", requestRef.getProjectRefId());
        visualisPostAction.addRequestPayload("partial", true);
        LabelRouteVO routeVO = new LabelRouteVO();
        routeVO.setRoute(requestRef.getDSSLabels().get(0).getValue().get("DSSEnv"));
        visualisPostAction.addRequestPayload("labels", routeVO);
        return OperationStrategyFactory.getInstance().getOperationStrategy(getAppInstance(), nodeType)
                .exportRef(requestRef, url, visualisPostAction);
    }

}
