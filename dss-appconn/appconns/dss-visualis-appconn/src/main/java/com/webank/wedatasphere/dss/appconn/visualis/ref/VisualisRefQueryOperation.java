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

package com.webank.wedatasphere.dss.appconn.visualis.ref;

import com.webank.wedatasphere.dss.appconn.visualis.ref.entity.*;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.query.OpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;

public class VisualisRefQueryOperation implements RefQueryOperation<OpenRequestRef, ResponseRef> {

    DevelopmentService developmentService;

    @Override
    public ResponseRef query(OpenRequestRef ref) throws ExternalOperationFailedException {
        VisualisOpenRequestRef visualisOpenRequestRef = (VisualisOpenRequestRef) ref;
        try {
            String externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(visualisOpenRequestRef.getJobContent());
            Long projectId = (Long) visualisOpenRequestRef.getParameter("projectId");
            String baseUrl = visualisOpenRequestRef.getParameter("redirectUrl").toString();
            String jumpUrl = baseUrl;
            if("linkis.appjoint.visualis.widget".equalsIgnoreCase(visualisOpenRequestRef.getType())){
                WidgetCreateResponseRef widgetCreateResponseRef = new WidgetCreateResponseRef(externalContent);
                jumpUrl = URLUtils.getUrl(baseUrl, URLUtils.WIDGET_JUMP_URL_FORMAT, projectId.toString(), widgetCreateResponseRef.getWidgetId());
            } else if("linkis.appjoint.visualis.display".equalsIgnoreCase(visualisOpenRequestRef.getType())){
                DisplayCreateResponseRef displayCreateResponseRef = new DisplayCreateResponseRef(externalContent);
                jumpUrl = URLUtils.getUrl(baseUrl, URLUtils.DISPLAY_JUMP_URL_FORMAT, projectId.toString(), displayCreateResponseRef.getDisplayId());
            }else if("linkis.appjoint.visualis.dashboard".equalsIgnoreCase(visualisOpenRequestRef.getType())){
                DashboardCreateResponseRef dashboardCreateResponseRef = new DashboardCreateResponseRef(externalContent);
                jumpUrl = URLUtils.getUrl(baseUrl, URLUtils.DASHBOARD_JUMP_URL_FORMAT, projectId.toString(), dashboardCreateResponseRef.getDashboardPortalId(), visualisOpenRequestRef.getName());
            } else {
                throw new ExternalOperationFailedException(90177, "Unknown task type " + visualisOpenRequestRef.getType(), null);
            }
            return new VisualisOpenResponseRef(jumpUrl, 0);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Failed to parse jobContent ", e);
        }
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }
}
