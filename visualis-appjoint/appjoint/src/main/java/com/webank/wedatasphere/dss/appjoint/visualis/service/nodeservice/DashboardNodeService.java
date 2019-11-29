/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appjoint.visualis.service.nodeservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import com.webank.wedatasphere.linkis.server.conf.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by shanhuang on 2019/10/12.
 */
public class DashboardNodeService {
    private final static String dashboardUrl = "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/dashboardPortals";
    private final static Logger logger = LoggerFactory.getLogger(DashboardNodeService.class);

    public static Map<String, Object> createNode(Session session, String url, String projectId, String nodeType,
                                        Map<String, Object>  requestBody) throws AppJointErrorException{
        Map<String, Object> element;
        try {
            HttpUtils httpUtils = new HttpUtils();
            requestBody.put("projectId", Integer.valueOf(projectId));
            String jsonParams = BDPJettyServerHelper.jacksonJson().writeValueAsString(requestBody);
            String resultString = httpUtils.sendPostReq(session, url + dashboardUrl, jsonParams, session.getUser());
            Map<String, Object> jsonObject = BDPJettyServerHelper.jacksonJson().readValue(resultString, Map.class);
            Map<String, Object> header = (Map<String, Object>) jsonObject.get("header");
            int code = (int) header.get("code");
            if (code != 200) {
                String errorMsg = header.toString();
                throw new AppJointErrorException(code, errorMsg);
            }
            int dashboardId = (int) ((Map<String, Object>) jsonObject.get("payload")).get("id");
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("config", "");
            reqMap.put("dashboardPortalId", dashboardId);
            reqMap.put("index", 0);
            reqMap.put("name", requestBody.get("name").toString());
            reqMap.put("parentId", 0);
            reqMap.put("type", 1);
            String reqJsonObject = BDPJettyServerHelper.jacksonJson().writeValueAsString(reqMap);
            String result = httpUtils.sendPostReq(session, url + dashboardUrl + "/" + dashboardId + "/dashboards", reqJsonObject, session.getUser());
            element = BDPJettyServerHelper.jacksonJson().readValue(result, Map.class);
            Map<String, Object> header1 = (Map<String, Object>) element.get("header");
            code = (int) header1.get("code");
            if (code != 200) {
                String errorMsg = header1.toString();
                throw new AppJointErrorException(code, errorMsg);
            }
            element.put("dashboardPortalId", dashboardId);
        }catch (Exception ex){
            throw new AppJointErrorException(90155,"Update Display AppJointNode Exception",ex);
        }

        return element;
    }

    public static Map<String, Object> deleteNode(Session session, String url, String projectId, String nodeType,
                                        Map<String, Object> requestBody) throws AppJointErrorException{
        Map<String, Object> element;
        try {
            HttpUtils httpUtils = new HttpUtils();
            requestBody.put("projectId", Integer.valueOf(projectId));
            logger.info("DashboardNodeServiceImpl request params is " + requestBody + ",nodeType:" + nodeType);
            logger.info("Dashboard url is " + url);
            String nodeId = requestBody.get("id").toString();
            String resultString = httpUtils.sendHttpDelete(session, url + dashboardUrl + "/" + nodeId, session.getUser());
            element = BDPJettyServerHelper.jacksonJson().readValue(resultString, Map.class);
            Map<String, Object> header = (Map<String, Object>) element.get("header");
            int code = (int) header.get("code");
            if (code != 200) {
                String errorMsg = header.toString();
                throw new AppJointErrorException(code, errorMsg);
            }
        }catch (Exception ex){
            throw new AppJointErrorException(90156,"Update Display AppJointNode Exception",ex);
        }
        return element;
    }

    public static Map<String, Object> updateNode(Session session, String url, Long projectId, String nodeType,
                                        Map<String, Object> requestBody) throws AppJointErrorException{
        Map<String, Object> element;
        try {
            HttpUtils httpUtils = new HttpUtils();
            requestBody.put("projectId", projectId.intValue());
            String jsonParams = BDPJettyServerHelper.jacksonJson().writeValueAsString(requestBody);
            logger.info("updateNode request params is " + jsonParams + ",nodeType:" + nodeType);
            logger.info("Dashboard url is " + url);
            String dashboardPortalId = requestBody.get("dashboardPortalId").toString();
            String resultString = httpUtils.sendHttpPut(session, url + dashboardUrl + "/" + dashboardPortalId + "/dashboards", jsonParams, session.getUser());
            element = BDPJettyServerHelper.jacksonJson().readValue(resultString, Map.class);
            Map<String, Object> header = (Map<String, Object>) element.get("header");
            int code = (int) header.get("code");
            if (code != 200) {
                String errorMsg = header.toString();
                throw new AppJointErrorException(code, errorMsg);
            }
//        int dashboardId = jsonObject.get("payload").getAsJsonObject().get("id").getAsInt();
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("avatar", "9");
            reqMap.put("description", "");
            reqMap.put("id", dashboardPortalId);
            reqMap.put("name", requestBody.get("name").toString());
            reqMap.put("publish", true);
            reqMap.put("roleIds", "[]");
            String reqJson = BDPJettyServerHelper.jacksonJson().writeValueAsString(reqMap);
            logger.info("updateNode request slides params is: " + reqJson);
            String result = httpUtils.sendHttpPut(session, url + dashboardUrl + "/" + dashboardPortalId, reqJson, session.getUser());
            element = BDPJettyServerHelper.jacksonJson().readValue(result, Map.class);
            header = (Map<String, Object>) element.get("header");
            code = (int) header.get("code");
            if (code != 200) {
                String errorMsg = header.toString();
                throw new AppJointErrorException(code, errorMsg);
            }
            element.put("dashboardPortalId", dashboardPortalId);
        }catch (Exception ex){
            throw new AppJointErrorException(90157,"Update Display AppJointNode Exception",ex);
        }

        return element;
    }

    public static String getNodeType() {
        return "linkis.appjoint.visualis.dashboard";
    }
}
