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
import com.webank.wedatasphere.dss.appconn.visualis.constant.VisualisConstant;
import com.webank.wedatasphere.dss.appconn.visualis.enums.ModuleEnum;
import com.webank.wedatasphere.dss.appconn.visualis.operation.VisualisRefExportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.NodeRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


public class VisualisNodeUtils {
    private final static Logger logger = LoggerFactory.getLogger(VisualisRefExportOperation.class);

    public static String getId(NodeRequestRef nodeRequestRef) throws Exception {
        String externalContent = BDPJettyServerHelper.jacksonJson().writeValueAsString(nodeRequestRef.getJobContent());
        logger.info("externalContent:{}", externalContent);
        String nodeType = nodeRequestRef.getNodeType().toLowerCase();
        if (!nodeType.contains(VisualisConstant.NODE_NAME_PREFIX)) {
            nodeType = VisualisConstant.NODE_NAME_PREFIX + nodeType;
        }
        switch (ModuleEnum.getEnum(nodeType)) {
            case DISPLAY:
                return NumberUtils.parseDoubleString(getDisplayId(externalContent));
            case DASHBOARD:
                return NumberUtils.parseDoubleString(getDashboardPortalId(externalContent));
            case WIDGET:
                return NumberUtils.parseDoubleString(getWidgetId(externalContent));
            case VIEW:
                return NumberUtils.parseDoubleString(getViewId(externalContent));
            default:
                throw new ExternalOperationFailedException(90177, "Unknown task type when get Id  " + nodeType, null);
        }
    }


    public static String getDisplayId(String responseBody) throws ExternalOperationFailedException {
        String displayId = null;
        try {
            Map responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
//            displayId = ((Map<String, Object>) responseMap.get("payload")).get("id").toString();
            displayId = responseMap.get("displayId").toString();
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
//            widgetId = ((Map<String, Object>) responseMap.get("data")).get("widgetId").toString();
            widgetId = responseMap.get("widgetId").toString();
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
//            dashboardPortalId = ((Map<String, Object>) responseMap.get("payload")).get("id").toString();
            dashboardPortalId = responseMap.get("dashboardPortalId").toString();
        } catch (JsonMappingException e) {
            throw new ExternalOperationFailedException(1000056, "Get dashboardPortalId Id failed!", e);
        } catch (JsonProcessingException e) {
            throw new ExternalOperationFailedException(1000056, "Get dashboardPortalId Id failed!", e);
        }

        return Long.toString(Math.round(Double.parseDouble(dashboardPortalId)));
    }


    public static String getViewId(String responseBody) throws ExternalOperationFailedException {
        String viewId;
        try {
            Map responseMap = BDPJettyServerHelper.jacksonJson().readValue(responseBody, Map.class);
            viewId = responseMap.get("id").toString();
        } catch (JsonMappingException e) {
            throw new ExternalOperationFailedException(1000057, "Get view Id failed!", e);
        } catch (JsonProcessingException e) {
            throw new ExternalOperationFailedException(1000057, "Get view Id failed!", e);
        }
        return Long.toString(Math.round(Double.parseDouble(viewId)));
    }
}
