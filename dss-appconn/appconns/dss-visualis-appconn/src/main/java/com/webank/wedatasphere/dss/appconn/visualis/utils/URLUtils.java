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

import org.apache.linkis.server.conf.ServerConfiguration;

public class URLUtils {
    public static final String widgetUrl =
            "/api/rest_j/"
                    + ServerConfiguration.BDP_SERVER_VERSION()
                    + "/visualis/widget"
                    + "/smartcreate";
    public static final String widgetUpdateUrl =
            "/api/rest_j/"
                    + ServerConfiguration.BDP_SERVER_VERSION()
                    + "/visualis/widget"
                    + "/rename";
    public static final String widgetContextUrl =
            "/api/rest_j/"
                    + ServerConfiguration.BDP_SERVER_VERSION()
                    + "/visualis/widget"
                    + "/setcontext";
    public static final String widgetDeleteUrl =
            "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/widgets";
    public static final String displayUrl =
            "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/displays";
    public static final String dashboardPortalUrl =
            "/api/rest_s/"
                    + ServerConfiguration.BDP_SERVER_VERSION()
                    + "/visualis/dashboardPortals";
    public static final String displaySlideConfig =
            "{\"slideParams\":{\"width\":1920,\"height\":1080,\"backgroundColor\":[255,255,255],\"scaleMode\":\"noScale\",\"backgroundImage\":null}}";
    public static final String projectUrl =
            "/api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/project";

    public static final String DISPLAY_PREVIEW_URL_FORMAT =
            "/api/rest_s/"
                    + ServerConfiguration.BDP_SERVER_VERSION()
                    + "/visualis/displays/%s/preview";
    public static final String DASHBOARD_PREVIEW_URL_FORMAT =
            "/api/rest_s/"
                    + ServerConfiguration.BDP_SERVER_VERSION()
                    + "/visualis/dashboard/portal/%s/preview";
    public static final String WIDGET_DATA_URL_FORMAT =
            "/api/rest_j/"
                    + ServerConfiguration.BDP_SERVER_VERSION()
                    + "/visualis/widget/%s/getdata";
    public static final String DISPLAY_METADATA_URL_FORMAT =
            "/api/rest_j/"
                    + ServerConfiguration.BDP_SERVER_VERSION()
                    + "/visualis/widget/display/%s/metadata";
    public static final String DASHBOARD_METADATA_URL_FORMAT =
            "/api/rest_j/"
                    + ServerConfiguration.BDP_SERVER_VERSION()
                    + "/visualis/widget/portal/%s/metadata";

    public static final String WIDGET_JUMP_URL_FORMAT = "dss/visualis/#/project/%s/widget/%s";
    public static final String DISPLAY_JUMP_URL_FORMAT = "dss/visualis/#/project/%s/display/%s";
    public static final String DASHBOARD_JUMP_URL_FORMAT =
            "dss/visualis/#/project/%s/portal/%s/portalName/%s";

    public static String getUrl(String baseUrl, String format, String entityId) {
        return baseUrl + String.format(format, entityId);
    }

    public static String getUrl(String baseUrl, String format, String... ids) {
        return baseUrl + String.format(format, ids);
    }
}
