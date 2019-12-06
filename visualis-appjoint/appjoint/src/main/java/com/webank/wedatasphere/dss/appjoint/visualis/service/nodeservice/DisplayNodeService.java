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


import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import com.webank.wedatasphere.linkis.server.conf.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by shanhuang on 2019/10/12.
 */
public class DisplayNodeService {
    private final static String displayUrl = "/api/rest_s/" + ServerConfiguration.BDP_SERVER_VERSION() + "/visualis/displays";
    private final static String config = "{\"slideParams\":{\"width\":1920,\"height\":1080,\"backgroundColor\":[255,255,255],\"scaleMode\":\"noScale\",\"backgroundImage\":null}}";
    private final static Logger logger = LoggerFactory.getLogger(DisplayNodeService.class);

    public static Map<String, Object> createNode(Session session, String url, String projectId,
                                        String nodeType, Map<String, Object> requestBody) throws AppJointErrorException {
        Map<String, Object> element;
        try {
            requestBody.put("projectId", Integer.valueOf(projectId));
            String jsonParams = BDPJettyServerHelper.jacksonJson().writeValueAsString(requestBody);
            logger.info("DisplayNodeServiceImpl request params is " + jsonParams + ",nodeType:" + nodeType);
            String displayRes = HttpUtils.sendPostReq(session, url + displayUrl, jsonParams, session.getUser());
            logger.info("Create Display AppJointNode First Return Value: " + displayRes);
            Map<String, Object> jsonObject = BDPJettyServerHelper.jacksonJson().readValue(displayRes, Map.class);
            Map<String, Object> header = (Map<String, Object>) jsonObject.get("header");
            int code = (int) header.get("code");
            if (code != 200) {
                String errorMsg = header.toString();
                throw new AppJointErrorException(code, errorMsg);
            }
            int displayId = (int) ((Map<String, Object>) jsonObject.get("payload")).get("id");
            Map<String, Object> reqMap = new HashMap<>();
            reqMap.put("config", config);
            reqMap.put("displayId", displayId);
            reqMap.put("index", 0);
            String reqJsonObject = BDPJettyServerHelper.jacksonJson().writeValueAsString(reqMap);
            logger.info("request slides params is: " + reqJsonObject);
            String result = HttpUtils.sendPostReq(session, url + displayUrl + "/" + displayId + "/slides", reqJsonObject, session.getUser());
            element = BDPJettyServerHelper.jacksonJson().readValue(result, Map.class);
            header = (Map<String, Object>) element.get("header");
            code = (int) header.get("code");
            if (code != 200) {
                String errorMsg = header.toString();
                throw new AppJointErrorException(code, errorMsg);
            }

        }catch (Exception ex){
            throw new AppJointErrorException(90173,"Create Display AppJointNode Exception",ex);
        }

        return element;

    }


    public static Map<String, Object> deleteNode(Session session, String url, String projectId,
                                        String nodeType, Map<String, Object> requestBody) throws AppJointErrorException{
        Map<String, Object> element;
        try {
            requestBody.put("projectId", Integer.valueOf(projectId));
            logger.info("request params is " + requestBody + ",nodeType:" + nodeType);
            logger.info("Delete display url is " + url);
            String nodeId = requestBody.get("id").toString();
            String resultString = HttpUtils.sendHttpDelete(session, url + displayUrl + "/" + nodeId, session.getUser());
            element = BDPJettyServerHelper.jacksonJson().readValue(resultString, Map.class);
            Map<String, Object> header = (Map<String, Object>) element.get("header");
            int code = (int) header.get("code");
            if (code != 200) {
                String errorMsg = header.toString();
                throw new AppJointErrorException(code, errorMsg);
            }
        }catch (Exception ex){
            throw new AppJointErrorException(90174,"Delete Display AppJointNode Exception",ex);
        }
        return element;
    }


    public static Map<String, Object> updateNode(Session session, String url, Long projectId,
                                        String nodeType, Map<String, Object> requestBody) throws AppJointErrorException{
        Map<String, Object> element;
        try {
            logger.info("[updateNode]: request projectId is {}, nodeType is {}, params: {}.", projectId, nodeType, requestBody);
            String nodeId = requestBody.get("id").toString();
            String jsonParams = BDPJettyServerHelper.jacksonJson().writeValueAsString(requestBody);
            String displayRes = HttpUtils.sendHttpPut(session, url + displayUrl + "/" + nodeId, jsonParams, session.getUser());
            element = BDPJettyServerHelper.jacksonJson().readValue(displayRes, Map.class);
            Map<String, Object> header = (Map<String, Object>) element.get("header");
            int code = (int) header.get("code");
            if (code != 200) {
                String errorMsg = header.toString();
                throw new AppJointErrorException(code, errorMsg);
            }
        }catch (Exception ex){
            throw new AppJointErrorException(90175,"Update Display AppJointNode Exception",ex);
        }
        return element;
    }

    public static String getNodeType() {
        return "linkis.appjoint.visualis.display";
    }
}
