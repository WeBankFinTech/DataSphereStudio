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
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;


public class VisualisRefImportOperation
    extends VisualisDevelopmentOperation<ThirdlyRequestRef.ImportWitContextRequestRefImpl, RefJobContentResponseRef>
        implements RefImportOperation<ThirdlyRequestRef.ImportWitContextRequestRefImpl> {

    @Override
    public RefJobContentResponseRef importRef(ThirdlyRequestRef.ImportWitContextRequestRefImpl requestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + URLUtils.projectUrl + "/import";
        String nodeType = requestRef.getType().toLowerCase();
        logger.info("The {} of Visualis try to import ref RefJobContent: {} in url {}.", nodeType, requestRef.getRefJobContent(), url);
        DSSPostAction visualisPostAction = new DSSPostAction();
        visualisPostAction.setUser(requestRef.getUserName());
        if(null == requestRef.getRefProjectId()){
            throw new ExternalOperationFailedException(100067,"导入节点Visualis工程ID为空");
        }
        visualisPostAction.addRequestPayload("projectId", requestRef.getRefProjectId());
        visualisPostAction.addRequestPayload("projectVersion", "v1");
        visualisPostAction.addRequestPayload("flowVersion", requestRef.getNewVersion());
        visualisPostAction.addRequestPayload("resourceId", requestRef.getResourceMap().get(ThirdlyRequestRef.ImportWitContextRequestRefImpl.RESOURCE_ID_KEY));
        visualisPostAction.addRequestPayload("version", requestRef.getResourceMap().get(ThirdlyRequestRef.ImportWitContextRequestRefImpl.RESOURCE_VERSION_KEY));

        LabelRouteVO routeVO = new LabelRouteVO();
        routeVO.setRoute(requestRef.getDSSLabels().get(0).getValue().get("DSSEnv"));
        visualisPostAction.addRequestPayload("labels", routeVO);
        return OperationStrategyFactory.getInstance().getOperationStrategy(getAppInstance(), nodeType)
                .importRef(requestRef, url, visualisPostAction);

    }

}
