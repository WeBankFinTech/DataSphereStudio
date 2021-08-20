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

import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisCommonResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisOpenRequestRef;
import com.webank.wedatasphere.dss.appconn.visualis.ref.VisualisOpenResponseRef;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.OpenRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;

import java.util.HashMap;
import java.util.Map;

public class VisualisRefQueryOperation implements RefQueryOperation<OpenRequestRef> {

    DevelopmentService developmentService;

    @Override
    public ResponseRef query(OpenRequestRef ref) throws ExternalOperationFailedException {
        VisualisOpenRequestRef visualisOpenRequestRef = (VisualisOpenRequestRef) ref;
        try {
            String externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(visualisOpenRequestRef.getJobContent());
            Long projectId = (Long) visualisOpenRequestRef.getParameter("projectId");
            String baseUrl = visualisOpenRequestRef.getParameter("redirectUrl").toString();
            String jumpUrl = baseUrl;
            if("linkis.appconn.visualis.widget".equalsIgnoreCase(visualisOpenRequestRef.getType())){
                VisualisCommonResponseRef widgetCreateResponseRef = new VisualisCommonResponseRef(externalContent);
                jumpUrl = URLUtils.getUrl(baseUrl, URLUtils.WIDGET_JUMP_URL_FORMAT, projectId.toString(), widgetCreateResponseRef.getWidgetId());
            } else if("linkis.appconn.visualis.display".equalsIgnoreCase(visualisOpenRequestRef.getType())){
                VisualisCommonResponseRef displayCreateResponseRef = new VisualisCommonResponseRef(externalContent);
                jumpUrl = URLUtils.getUrl(baseUrl, URLUtils.DISPLAY_JUMP_URL_FORMAT, projectId.toString(), displayCreateResponseRef.getDisplayId());
            }else if("linkis.appconn.visualis.dashboard".equalsIgnoreCase(visualisOpenRequestRef.getType())){
                VisualisCommonResponseRef dashboardCreateResponseRef = new VisualisCommonResponseRef(externalContent);
                jumpUrl = URLUtils.getUrl(baseUrl, URLUtils.DASHBOARD_JUMP_URL_FORMAT, projectId.toString(), dashboardCreateResponseRef.getDashboardId(), visualisOpenRequestRef.getName());
            } else {
                throw new ExternalOperationFailedException(90177, "Unknown task type " + visualisOpenRequestRef.getType(), null);
            }
            String retJumpUrl = getEnvUrl(jumpUrl, visualisOpenRequestRef);
            Map<String,String> retMap = new HashMap<>();
            retMap.put("jumpUrl",retJumpUrl);
            return new VisualisOpenResponseRef(DSSCommonUtils.COMMON_GSON.toJson(retMap), 0);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Failed to parse jobContent ", e);
        }
    }

    public String getEnvUrl(String url, VisualisOpenRequestRef visualisOpenRequestRef ){
        String env = ((Map<String, Object>) visualisOpenRequestRef.getParameter("params")).get(DSSCommonUtils.DSS_LABELS_KEY).toString();
        return url + "?env=" + env.toLowerCase();
    }


    @Override
    public void setDevelopmentService(DevelopmentService service) {
        this.developmentService = service;
    }
}
