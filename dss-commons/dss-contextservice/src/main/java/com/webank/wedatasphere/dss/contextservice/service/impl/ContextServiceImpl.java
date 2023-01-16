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

package com.webank.wedatasphere.dss.contextservice.service.impl;

import com.google.gson.*;
import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.exception.ErrorCode;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.cs.client.ContextClient;
import org.apache.linkis.cs.client.builder.ContextClientFactory;
import org.apache.linkis.cs.client.service.CSWorkService;
import org.apache.linkis.cs.client.service.CSWorkServiceImpl;
import org.apache.linkis.cs.client.utils.SerializeHelper;
import org.apache.linkis.cs.common.entity.enumeration.ContextScope;
import org.apache.linkis.cs.common.entity.enumeration.ContextType;
import org.apache.linkis.cs.common.entity.enumeration.WorkType;
import org.apache.linkis.cs.common.entity.object.CSFlowInfos;
import org.apache.linkis.cs.common.entity.object.LinkisVariable;
import org.apache.linkis.cs.common.entity.resource.LinkisBMLResource;
import org.apache.linkis.cs.common.entity.source.*;
import org.apache.linkis.cs.common.utils.CSCommonUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ContextServiceImpl implements ContextService {

    private static final Logger logger = LoggerFactory.getLogger(ContextServiceImpl.class);
    private static ContextClient contextClient = ContextClientFactory.getOrCreateContextClient();
    private static volatile ContextService contextService = null;

    private ContextServiceImpl() {}

    public static ContextService getInstance() {
        if (null == contextService) {
            synchronized (ContextServiceImpl.class) {
                if (null == contextService) {
                    contextService = new ContextServiceImpl();
                }
            }
        }
        return contextService;
    }

    @Override
    public String createContextID(String workspace, String project, String flow, String version, String user) {
        LinkisHAWorkFlowContextID contextID = new LinkisHAWorkFlowContextID();
        contextID.setWorkSpace(workspace);
        contextID.setProject(project);
        contextID.setFlow(flow);
        contextID.setUser(user);
        contextID.setVersion(version);
        contextID.setEnv(DSSCommonConf.DSS_IO_ENV.getValue());
        try {
            contextClient.createContext(contextID);
            return SerializeHelper.serializeContextID(contextID);
        } catch (Exception e) {
            logger.error("createContextID error. workspace : {}, project : {}, flow : {}, version : {}, user : {}", workspace, project, flow, version, user, e);
            throw new DSSRuntimeException(50032, "Try to ask Linkis for creating a new contextId failed(向Linkis请求创建一个ContextID失败)! Linkis error msg: " +
                    ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    @Override
    public String checkAndCreateContextID(String jsonFlow, String flowVersion, String workspace, String project, String flow, String user, boolean fullCheck) {
        JsonObject flowObject = null;
        try {
            flowObject = new Gson().fromJson(jsonFlow, JsonObject.class);
            if (!flowObject.has(CSCommonUtils.CONTEXT_ID_STR) ||
                    StringUtils.isBlank(flowObject.get(CSCommonUtils.CONTEXT_ID_STR).getAsString())||
                    !flowObject.get(CSCommonUtils.CONTEXT_ID_STR).isJsonPrimitive()) {
                String contextID = createContextID(workspace,
                        project,
                        flow, flowVersion, user);
                flowObject.addProperty(CSCommonUtils.CONTEXT_ID_STR, contextID);
            } else {
                boolean updateContextId = false;
                LinkisHAWorkFlowContextID contextID = null;
                try {
                    Object contextIDObj = SerializeHelper.deserializeContextID(flowObject.get(CSCommonUtils.CONTEXT_ID_STR).getAsString());
                    contextID = (LinkisHAWorkFlowContextID)contextIDObj;
                    if (fullCheck) {
                        if (!DSSCommonConf.DSS_IO_ENV.getValue().equalsIgnoreCase(contextID.getEnv())) {
                            updateContextId = true;
                        } else if (StringUtils.isBlank(contextID.getProject())
                                || StringUtils.isBlank(contextID.getFlow())) {
                            updateContextId = false;
                        } else if ((null != contextID.getWorkSpace() && !contextID.getWorkSpace().equalsIgnoreCase(workspace))
                                || !contextID.getProject().equalsIgnoreCase(project)
                                || !contextID.getFlow().equalsIgnoreCase(flow)
                                || !flowVersion.equalsIgnoreCase(contextID.getVersion())) {
                            updateContextId = true;
                        } else {
                            updateContextId = false;
                        }
                    }
                } catch (ErrorException e0) {
                    logger.error("Invalid contextID : {}, please contact with administrator", flowObject.get(CSCommonUtils.CONTEXT_ID_STR));
                }
                if (updateContextId) {
                    String newContextID = createContextID(workspace,
                            project,
                            flow, flowVersion, user);
                    flowObject.addProperty(CSCommonUtils.CONTEXT_ID_STR, newContextID);
                    logger.info("UpdateContextId true, old contextID : {}, new contextID : {}", CSCommonUtils.gson.toJson(contextID), CSCommonUtils.gson.toJson(newContextID));
                }
            }
        } catch (Exception e) {
            logger.error("Invalid json : {}", jsonFlow, e);
        }
        if (null != flowObject) {
            return flowObject.toString();
        } else {
            return jsonFlow;
        }
    }

    @Override
    public void checkAndSaveContext(String jsonFlow,String parentFlowID) throws DSSErrorException{
        logger.info("jsonFlow => \n" + jsonFlow);
        try {
            JsonObject flowObject = new Gson().fromJson(jsonFlow, JsonObject.class);
            if (!flowObject.has(CSCommonUtils.CONTEXT_ID_STR) || !flowObject.get(CSCommonUtils.CONTEXT_ID_STR).isJsonPrimitive()) {
                logger.error("Did not have invalid contextID, save context failed.");
                throw new DSSRuntimeException("does not have valid ContextID, save context failed(工作流格式错误，缺失有效的CS信息)");
            } else {
                String contextIDStr = flowObject.get(CSCommonUtils.CONTEXT_ID_STR).getAsString();
                // ①reset原有key 这里只清理
                CSWorkService csWorkService = CSWorkServiceImpl.getInstance();
                List<WorkType> workTypes = new ArrayList<>();
                workTypes.add(WorkType.WORKSPACE);
                workTypes.add(WorkType.PROJECT);
                workTypes.add(WorkType.FLOW);
                csWorkService.initContextServiceInfo(contextIDStr, workTypes);

                // 保存flow的资源
                if (flowObject.has(DSSCommonUtils.FLOW_RESOURCE_NAME)) {
                    JsonArray flowRes = flowObject.get(DSSCommonUtils.FLOW_RESOURCE_NAME).getAsJsonArray();
                    saveContextResource(contextIDStr, flowRes, contextClient, CSCommonUtils.FLOW_RESOURCE_PREFIX, null);
                }
                if (flowObject.has(DSSCommonUtils.FLOW_PROP_NAME)) {
                    JsonElement flowProp = flowObject.get(DSSCommonUtils.FLOW_PROP_NAME);
                    saveContextVariable(contextIDStr, flowProp, contextClient, CSCommonUtils.FLOW_VARIABLE_PREFIX, null);
                }
                // todo udf

                // 保存节点的资源
                if (flowObject.has(DSSCommonUtils.FLOW_NODE_NAME)) {
                    JsonArray nodes = flowObject.get(DSSCommonUtils.FLOW_NODE_NAME).getAsJsonArray();
                    for (JsonElement node : nodes) {
                        JsonObject json = node.getAsJsonObject();
                        String nodeName =json.get(DSSCommonUtils.NODE_NAME_NAME).getAsString();
                        initContextNodeVarInfo(contextIDStr,nodeName,contextClient);
                        if (json.has(DSSCommonUtils.NODE_RESOURCE_NAME)) {
                            JsonArray nodeRes = json.get(DSSCommonUtils.NODE_RESOURCE_NAME).getAsJsonArray();
                            saveContextResource(contextIDStr, nodeRes, contextClient,
                                    CSCommonUtils.NODE_PREFIX, json.get(DSSCommonUtils.NODE_NAME_NAME).getAsString());
                        }
//                        if (json.has(DSSCommonUtils.NODE_PROP_NAME)) {
//                            JsonObject nodePropObj = json.get(DSSCommonUtils.NODE_PROP_NAME).getAsJsonObject();
//                        }
                    }
                }
                // 保存info信息
                saveFlowInfo(contextIDStr, parentFlowID, jsonFlow);
            }
        } catch (Exception e) {
            logger.error("CheckAndSaveContext error. jsonFlow : {}, parentFlowId : {}, e : ", jsonFlow, parentFlowID, e);
            throw new DSSErrorException(ErrorCode.INVALID_PARAMS, "CheckAndSaveContext error : " + e.getMessage());
        }
    }

    private void initContextNodeVarInfo(String contextIDStr,String nodeName,ContextClient contextClient){
        try {
            ContextID contextID  = SerializeHelper.deserializeContextID(contextIDStr);
            contextClient.removeAllValueByKeyPrefixAndContextType(contextID, ContextType.Variable, CSCommonUtils.NODE_PREFIX + nodeName);
        } catch (ErrorException e) {
            logger.error("CheckAndSaveContext error. ContextID : {}, nodeName : {}, e : ", contextIDStr,nodeName, e);
        }
    }

    @Override
    public String checkAndInitContext(String jsonFlow, String parentFlowId, String workspace, String  projectName, String flowName, String flowVersion, String user) throws DSSErrorException {
        if (StringUtils.isBlank(jsonFlow) ) {
            logger.error("Invalid jsonFlow : {} or parentFlowId : {}.", jsonFlow, parentFlowId);
            throw new DSSErrorException(ErrorCode.INVALID_PARAMS, "Invalid jsonFlow : " + jsonFlow + ", or parentFlowId : " + parentFlowId + ".");
        }
        jsonFlow = checkAndCreateContextID(jsonFlow, flowVersion, workspace, projectName, flowName, user, true);
        checkAndSaveContext(jsonFlow,parentFlowId);
        JsonObject flowObject = new Gson().fromJson(jsonFlow, JsonObject.class);
        return flowObject.get(CSCommonUtils.CONTEXT_ID_STR).getAsString();
    }



    private void saveContextVariable(String contextIDStr, JsonElement variables, ContextClient contextClient, String variablePrefix, String nodeName) {
        try {
            if (variables.isJsonArray()) {
                JsonArray flowProp = variables.getAsJsonArray();
                for (JsonElement prop : flowProp) {
                    Set<Map.Entry<String, JsonElement>> entrySet = prop.getAsJsonObject().entrySet();
                    // assign entry num is 1
                    if (null != entrySet) {
                        for (Map.Entry<String, JsonElement> entry : entrySet) {
                            if (!entry.getValue().isJsonPrimitive()) {
                                continue;
                            }
                            saveContextVariableKeyValue(contextIDStr, contextClient, variablePrefix, entry, nodeName);
                        }
                    }

                }
            } else if (variables.isJsonObject()) {
                JsonObject variableJson = variables.getAsJsonObject();
                for (Map.Entry<String, JsonElement> entry : variableJson.entrySet()) {
                    if (!entry.getValue().isJsonPrimitive()) {
                        continue;
                    }
                    saveContextVariableKeyValue(contextIDStr, contextClient, variablePrefix, entry, nodeName);
                }
            } else {
                logger.error("Invalid JsonElement variables : {}", variables.toString());
            }

        } catch (ErrorException e) {
            logger.error("SaveContextVariable failed. contextIDStr : {}, variables : {}, variablePrefix : {}, e : ",
                    contextIDStr, variables.toString(), variablePrefix, e);
        }

    }

    private void saveContextVariableKeyValue(String contextIDStr, ContextClient contextClient, String uniKeyPrefix, Map.Entry<String, JsonElement> entry, String nodeName) throws ErrorException {
        String contextKeyPrefix = null;
        switch (uniKeyPrefix) {
            case CSCommonUtils.WORKSPACE_VARIABLE_PREFIX:
            case CSCommonUtils.PROJECT_VARIABLE_PREFIX:
            case CSCommonUtils.FLOW_VARIABLE_PREFIX:
                contextKeyPrefix = uniKeyPrefix;
                break;
            case CSCommonUtils.NODE_PREFIX:
                contextKeyPrefix = uniKeyPrefix + nodeName + "." + CSCommonUtils.VARIABLE_PREFIX;
                break;
            default:
                logger.error("Invalid contextKeyPrefix : {}", uniKeyPrefix);
                return;
        }
        LinkisVariable linkisVariable = new LinkisVariable();
        linkisVariable.setKey(entry.getKey());
        linkisVariable.setValue(entry.getValue().getAsString());
        ContextKey contextKey = new CommonContextKey();
        contextKey.setKey(contextKeyPrefix + linkisVariable.getKey());
        contextKey.setContextType(ContextType.Variable);
        contextKey.setContextScope(ContextScope.PUBLIC);
        ContextValue contextValue = new CommonContextValue();
        contextValue.setValue(linkisVariable);
        ContextKeyValue contextKeyValue = new CommonContextKeyValue(contextKey, contextValue);
        contextClient.setContextKeyValue(SerializeHelper.deserializeContextID(contextIDStr), contextKeyValue);
    }

    private void saveContextResource(String contextIDStr, JsonArray flowRes, ContextClient contextClient, String uniKeyPrefix, String nodeName) throws ErrorException {
        String contextKeyPrefix = null;
        switch (uniKeyPrefix) {
            case CSCommonUtils.WORKSPACE_RESOURCE_PREFIX:
            case CSCommonUtils.PROJECT_RESOURCE_PREFIX:
            case CSCommonUtils.FLOW_RESOURCE_PREFIX:
                contextKeyPrefix = uniKeyPrefix;
                break;
            case CSCommonUtils.NODE_PREFIX:
                contextKeyPrefix = uniKeyPrefix + nodeName + "." + CSCommonUtils.RESOURCE_PREFIX;
                break;
            default:
                logger.error("Invalid contextKeyPrefix : {}", uniKeyPrefix);
                return;
        }
        for (JsonElement res : flowRes) {
            LinkisBMLResource bmlResource = new LinkisBMLResource();
            JsonObject json = res.getAsJsonObject();
            if (!json.has("fileName") || !json.has("resourceId") || !json.has("version")) {
                logger.warn("Invalid resource: res : {}, contextidStr : {}, all res : {} ", CSCommonUtils.gson.toJson(json), contextIDStr, CSCommonUtils.gson.toJson(flowRes));
                continue;
            }
            if (json.get("fileName") instanceof JsonNull || json.get("resourceId") instanceof JsonNull || json.get("version") instanceof JsonNull) {
                logger.warn("Invalid resource: res : {}, contextidStr : {}, all res : {} ", CSCommonUtils.gson.toJson(json), contextIDStr, CSCommonUtils.gson.toJson(flowRes));
                continue;
            }
            bmlResource.setDownloadedFileName(json.get("fileName").getAsString());
            bmlResource.setResourceId(json.get("resourceId").getAsString());
            bmlResource.setVersion(json.get("version").getAsString());
            ContextKey contextKey = new CommonContextKey();
            contextKey.setKey(contextKeyPrefix + bmlResource.getDownloadedFileName());
            contextKey.setContextScope(ContextScope.PUBLIC);
            contextKey.setContextType(ContextType.RESOURCE);
            ContextValue contextValue = new CommonContextValue();
            contextValue.setValue(bmlResource);
            ContextKeyValue contextKeyValue = new CommonContextKeyValue(contextKey, contextValue);
            contextClient.setContextKeyValue(SerializeHelper.deserializeContextID(contextIDStr), contextKeyValue);
            //// todo test
            logger.info("Debug: saved contextKeyValue : {}", CSCommonUtils.gson.toJson(contextKeyValue));
        }
    }

    private void saveContextResource(String contextIDStr, List<Resource> resourceList, ContextClient contextClient, String uniKeyPrefix) throws ErrorException {

        String contextKeyPrefix = null;
        switch (uniKeyPrefix) {
            case CSCommonUtils.WORKSPACE_RESOURCE_PREFIX:
            case CSCommonUtils.PROJECT_RESOURCE_PREFIX:
            case CSCommonUtils.FLOW_RESOURCE_PREFIX:
                contextKeyPrefix = uniKeyPrefix;
                break;
            default:
                logger.error("Invalid contextKeyPrefix : {}", uniKeyPrefix);
                return;
        }
        for (Resource res : resourceList) {
            LinkisBMLResource bmlResource = new LinkisBMLResource();
            bmlResource.setDownloadedFileName(res.getFileName());
            bmlResource.setResourceId(res.getResourceId());
            bmlResource.setVersion(res.getVersion());
            ContextKey contextKey = new CommonContextKey();
            contextKey.setKey(contextKeyPrefix + bmlResource.getDownloadedFileName());
            contextKey.setContextScope(ContextScope.PUBLIC);
            contextKey.setContextType(ContextType.RESOURCE);
            ContextValue contextValue = new CommonContextValue();
            contextValue.setValue(bmlResource);
            ContextKeyValue contextKeyValue = new CommonContextKeyValue(contextKey, contextValue);
            contextClient.setContextKeyValue(SerializeHelper.deserializeContextID(contextIDStr), contextKeyValue);
            //// todo test
            logger.info("Debug: saved contextKeyValue : {}", CSCommonUtils.gson.toJson(contextKeyValue));
        }
    }


    /**
     * 保存节点间关系信息
     * @param contextIDStr
     * @param parentFlowID
     * @param parentFlowID
     */
    private void saveFlowInfo(String contextIDStr, String parentFlowID, String flowJson) {
        if (StringUtils.isBlank(contextIDStr)) {
            return;
        }
        ContextClient contextClient = ContextClientFactory.getOrCreateContextClient();
        JsonObject flowJsonObject = new Gson().fromJson(flowJson, JsonObject.class);
        CSFlowInfos flowInfos = new CSFlowInfos();
        Map<String, Object> flowInfoMap = new HashMap<>();
        // 保存边信息
        JsonArray flowEdges = new JsonArray();
        if (flowJsonObject.has(DSSCommonUtils.FLOW_EDGES_NAME)) {
            flowEdges = flowJsonObject.getAsJsonArray(DSSCommonUtils.FLOW_EDGES_NAME);
        }
        flowInfoMap.put(DSSCommonUtils.FLOW_EDGES_NAME, flowEdges);
        // 保存父节点 可以为空
        flowInfoMap.put(DSSCommonUtils.FLOW_PARENT_NAME, parentFlowID);
        //保存节点名
        JsonObject idNodeNameJson = new JsonObject();
        if (flowJsonObject.has(DSSCommonUtils.FLOW_NODE_NAME)) {
            JsonArray nodes = flowJsonObject.getAsJsonArray(DSSCommonUtils.FLOW_NODE_NAME);
            for (JsonElement element : nodes) {
                JsonObject jsonObject = element.getAsJsonObject();
                String id = jsonObject.get(DSSCommonUtils.NODE_ID_NAME).getAsString();
                String name = jsonObject.get(DSSCommonUtils.NODE_NAME_NAME).getAsString();
                idNodeNameJson.addProperty(id, name);
            }
        }
        flowInfoMap.put(CSCommonUtils.ID_NODE_NAME, idNodeNameJson);
        flowInfos.setInfos(flowInfoMap);
        ContextKey contextKey = new CommonContextKey();
        contextKey.setContextType(ContextType.OBJECT);
        contextKey.setContextScope(ContextScope.PUBLIC);
        contextKey.setKey(CSCommonUtils.FLOW_INFOS);
        ContextValue contextValue = new CommonContextValue();
        contextValue.setValue(flowInfos);
        ContextKeyValue contextKeyValue = new CommonContextKeyValue(contextKey, contextValue);
        try {
            contextClient.setContextKeyValue(SerializeHelper.deserializeContextID(contextIDStr), contextKeyValue);
            //// todo test
            logger.info("ContextID : {}, \nContextKey : {}, \nContextValue : {}",
                    CSCommonUtils.gson.toJson(SerializeHelper.deserializeContextID(contextIDStr)),
                    CSCommonUtils.gson.toJson(contextKey),
                    CSCommonUtils.gson.toJson(contextValue));
            ContextValue contextValue1 = contextClient.getContextValue(SerializeHelper.deserializeContextID(contextIDStr), contextKey);
            logger.info(CSCommonUtils.gson.toJson(contextValue1));
        } catch (ErrorException e) {
            logger.error("Set ContextKeyValue error. contextIDStr ");
        }
    }
}