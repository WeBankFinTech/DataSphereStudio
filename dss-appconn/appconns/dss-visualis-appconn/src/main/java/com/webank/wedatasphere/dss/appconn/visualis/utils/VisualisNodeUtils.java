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

package com.webank.wedatasphere.dss.appconn.visualis.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;

import java.util.Map;

public class VisualisNodeUtils {

    public static String getId(NodeRequestRef nodeRequestRef) throws Exception {
        String externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(nodeRequestRef.getJobContent());
        if ("linkis.appconn.visualis.display".equalsIgnoreCase(nodeRequestRef.getNodeType())) {
            return NumberUtils.parseDoubleString(getDisplayId(externalContent));
        } else if ("linkis.appconn.visualis.dashboard".equalsIgnoreCase(nodeRequestRef.getNodeType())) {
            return NumberUtils.parseDoubleString(getDashboardPortalId(externalContent));
        } else if ("linkis.appconn.visualis.widget".equalsIgnoreCase(nodeRequestRef.getNodeType())) {
            return NumberUtils.parseDoubleString(getWidgetId(externalContent));
        }
        return null;
    }


    public static String getDisplayId(String responseBody) throws ExternalOperationFailedException {
        String displayId = null;
        try {
            Map responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
            displayId = ((Map<String, Object>) responseMap.get("payload")).get("id").toString();
        } catch (JsonMappingException e) {
            throw new ExternalOperationFailedException(1000054, "Get Display Id failed!", e);
        } catch (JsonProcessingException e) {
            throw new ExternalOperationFailedException(1000054, "Get Display Id failed!", e);
        }

        return displayId;
    }

    public static String getWidgetId(String responseBody) throws ExternalOperationFailedException {
        String widgetId = null;
        try {
            Map responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
            widgetId = ((Map<String, Object>) responseMap.get("data")).get("widgetId").toString();
        } catch (JsonMappingException e) {
            throw new ExternalOperationFailedException(1000055, "Get widget Id failed!", e);
        } catch (JsonProcessingException e) {
            throw new ExternalOperationFailedException(1000055, "Get widget Id failed!", e);
        }
        return widgetId;
    }

    public static String getDashboardPortalId(String responseBody) throws ExternalOperationFailedException {
        String dashboardPortalId = null;
        try {
            Map responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
            dashboardPortalId = ((Map<String, Object>) responseMap.get("payload")).get("id").toString();
        } catch (JsonMappingException e) {
            throw new ExternalOperationFailedException(1000056, "Get dashboard Id failed!", e);
        } catch (JsonProcessingException e) {
            throw new ExternalOperationFailedException(1000056, "Get dashboard Id failed!", e);
        }

        return Long.toString(Math.round(Double.parseDouble(dashboardPortalId)));
    }


}
