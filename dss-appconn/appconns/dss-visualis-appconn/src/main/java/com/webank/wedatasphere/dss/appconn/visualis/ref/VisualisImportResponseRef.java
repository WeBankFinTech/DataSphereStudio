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

package com.webank.wedatasphere.dss.appconn.visualis.ref;

import com.webank.wedatasphere.dss.appconn.visualis.enums.ModuleEnum;
import com.webank.wedatasphere.dss.appconn.visualis.utils.NumberUtils;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.development.ref.DSSCommonResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

import java.util.Map;

public class VisualisImportResponseRef extends DSSCommonResponseRef {

    private Map<String, Object> newJobContent;

    public VisualisImportResponseRef(Map<String, Object> jobContent, String responseBody, String nodeType, Object projectId) throws Exception {
        super(responseBody);

        String id;
        @SuppressWarnings("unchecked")
        Map<String, Object> dataMap= (Map<String, Object>) responseMap.get("data");
        switch (ModuleEnum.getEnum(nodeType)) {
            case WIDGET:
                id = NumberUtils.parseDoubleString(jobContent.get("widgetId").toString());

                @SuppressWarnings("unchecked")
                Map<String, Object> widgetData =(Map<String, Object>) dataMap.get("widget");
                jobContent.put("widgetId", Double.parseDouble(widgetData.get(id).toString()));
                break;
            case DISPLAY:
                id =NumberUtils.parseDoubleString(jobContent.get("displayId").toString());

                @SuppressWarnings("unchecked")
                Map<String, Object> displayData =(Map<String, Object>) dataMap.get("display");
                jobContent.put("displayId", Double.parseDouble(displayData.get(id).toString()));
                break;
            case DASHBOARD:

                jobContent.put("projectId", projectId);
                String dashboardPortalId = NumberUtils.parseDoubleString(jobContent.get("dashboardPortalId").toString());
                @SuppressWarnings("unchecked")
                Map<String, Object> dashboardPortal =(Map<String, Object>) dataMap.get("dashboardPortal");
                jobContent.put("dashboardPortalId", Double.parseDouble(dashboardPortal.get(dashboardPortalId).toString()));

                String dashboardId = NumberUtils.parseDoubleString(jobContent.get("id").toString());
                Map<String, Object> dashboard =(Map<String, Object>) dataMap.get("dashboard");
                jobContent.put("id", Double.parseDouble(dashboard.get(dashboardId).toString()));

//                if(null==dashboard.get(dashboardId)){
//                    Map<String, Object> dashboardPortal =(Map<String, Object>) dataMap.get("dashboardPortal");
//                    jobContent.put("id", Double.parseDouble(dashboardPortal.get(dashboardId).toString()));
//                }else {
                   // jobContent.put("id", Double.parseDouble(dashboard.get(dashboardId).toString()));
//                }
//                if(jobContent.containsKey("dashboardPortalId")){
//                    jobContent.remove("dashboardPortalId");
//                }
                break;

            case VIEW:
                id = NumberUtils.parseDoubleString(jobContent.get("id").toString());

                @SuppressWarnings("unchecked")
                Map<String, Object> viewData =(Map<String, Object>) dataMap.get("view");
                jobContent.put("projectId", projectId);
                jobContent.put("id", Double.parseDouble(viewData.get(id).toString()));
                break;
            default:
                throw new ExternalOperationFailedException(90177, "Unknown task type when import  " + nodeType, null);

        }
        this.newJobContent = jobContent;
    }


    @Override
    public Map<String, Object> toMap() {
        return newJobContent;
    }

    public void updateResponseBody(Map<String, Object> jobContent) {
        super.responseBody = DSSCommonUtils.COMMON_GSON.toJson(jobContent);
        super.init();
    }

}
