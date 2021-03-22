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

package com.webank.wedatasphere.dss.appconn.visualis.utils;

import com.webank.wedatasphere.linkis.server.conf.ServerConfiguration;

public class URLUtils {
    public final static String widgetUrl = "/api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/widget" + "/smartcreate";
    public final static String widgetUpdateUrl = "/api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/widget" + "/rename";
    public final static String widgetContextUrl = "/api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/widget" + "/setcontext";
    public final static String widgetDeleteUrl = "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/widgets";
    public final static String displayUrl = "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/displays";
    public final static String dashboardPortalUrl = "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/dashboardPortals";
    public final static String displaySlideConfig = "{\"slideParams\":{\"width\":1920,\"height\":1080,\"backgroundColor\":[255,255,255],\"scaleMode\":\"noScale\",\"backgroundImage\":null}}";
    public final static String projectUrl = "/api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/project";

    public final static String DISPLAY_PREVIEW_URL_FORMAT = "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/displays/%s/preview";
    public final static String DASHBOARD_PREVIEW_URL_FORMAT = "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/dashboard/portal/%s/preview";
    public final static String WIDGET_DATA_URL_FORMAT = "/api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/widget/%s/getdata";
    public final static String DISPLAY_METADATA_URL_FORMAT = "/api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/widget/display/%s/metadata";
    public final static String DASHBOARD_METADATA_URL_FORMAT = "/api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/widget/portal/%s/metadata";

    public final static String WIDGET_JUMP_URL_FORMAT = "dss/visualis/#/project/%s/widget/%s";
    public final static String DISPLAY_JUMP_URL_FORMAT = "dss/visualis/#/project/%s/display/%s";
    public final static String DASHBOARD_JUMP_URL_FORMAT = "dss/visualis/#/project/%s/portal/%s/portalName/%s";


    public static String getUrl(String baseUrl, String format, String entityId){
        return baseUrl + String.format(format, entityId);
    }

    public static String getUrl(String baseUrl, String format, String... ids){
        return baseUrl + String.format(format, ids);
    }

}
