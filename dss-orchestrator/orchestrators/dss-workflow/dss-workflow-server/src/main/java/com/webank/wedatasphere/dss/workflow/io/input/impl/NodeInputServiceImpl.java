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

package com.webank.wedatasphere.dss.workflow.io.input.impl;


import com.google.common.collect.ImmutableMap;
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.contextservice.service.impl.ContextServiceImpl;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.parser.NodeParser;
import com.webank.wedatasphere.dss.workflow.entity.CommonAppConnNode;
import com.webank.wedatasphere.dss.workflow.io.input.NodeInputService;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.workflow.service.WorkflowNodeService;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.function.Supplier;

import static com.webank.wedatasphere.dss.workflow.io.export.impl.NodeExportServiceImpl.APPCONN_FILE_NAME;

@Service
public class NodeInputServiceImpl implements NodeInputService {
    @Autowired
    @Qualifier("workflowBmlService")
    private BMLService bmlService;

    @Autowired
    private NodeParser nodeParser;

    @Autowired
    private WorkflowNodeService nodeService;

    private static ContextService contextService = ContextServiceImpl.getInstance();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String uploadResourceToBmlNew(String userName, String nodeJson, String nodePath,String nodeName, String projectName)
            throws IOException {
        List<Resource> resources = nodeParser.getNodeResource(nodeJson);
        Map<String, Object> jobContent = nodeParser.getNodeJobContent(nodeJson);
        String scriptName = Optional.ofNullable(jobContent).map(e->e.get("script")).map(Object::toString).orElse(null);
        if (resources != null && resources.size() > 0) {
            resources.forEach(resource -> {
                if (resource.getVersion() != null && resource.getFileName() != null && resource.getResourceId() != null) {
                    //需要区分代码节点和非代码节点。非代码节点直接根据filename上传即可
                    String fileName=resource.getFileName();
                    if(fileName.equals(scriptName)){
                        String extensionName = fileName.substring(fileName.lastIndexOf('.'));
                        fileName=Optional.ofNullable(nodeName).orElse("") + extensionName;
                    }
                    String filePath = IoUtils.addFileSeparator(nodePath, fileName);
                    InputStream resourceInputStream = bmlService.readLocalResourceFile(userName, filePath);
                    BmlResource bmlReturnMap = bmlService.upload(userName,
                            resourceInputStream, UUID.randomUUID().toString() + ".json", projectName);
                    resource.setResourceId(bmlReturnMap.getResourceId());
                    resource.setVersion(bmlReturnMap.getVersion());
                } else {
                    logger.warn("Illegal resource information");
                    logger.warn("username:{},fileName:{},version:{},resourceId:{}", userName, resource.getFileName(), resource.getVersion(), resource.getResourceId());
                }
            });
        }
        return nodeParser.updateNodeResource(nodeJson, resources);
    }
    @Override
    public String uploadResourceToBml(String userName, String nodeJson, String inputResourcePath, String projectName) throws IOException {
        List<Resource> resources = nodeParser.getNodeResource(nodeJson);
        if (resources != null && resources.size() > 0) {
            resources.forEach(resource -> {
                if (resource.getVersion() != null && resource.getFileName() != null && resource.getResourceId() != null) {
                    InputStream resourceInputStream = readResource(userName, resource, inputResourcePath);
                    BmlResource bmlReturnMap = bmlService.upload(userName,
                            resourceInputStream, UUID.randomUUID().toString() + ".json", projectName);
                    resource.setResourceId(bmlReturnMap.getResourceId());
                    resource.setVersion(bmlReturnMap.getVersion());
                } else {
                    logger.warn("Illegal resource information");
                    logger.warn("username:{},fileName:{},version:{},resourceId:{}", userName, resource.getFileName(), resource.getVersion(), resource.getResourceId());
                }
            });
        }
        return nodeParser.updateNodeResource(nodeJson, resources);
    }

    private InputStream readResource(String userName, Resource resource, String flowResourcePath) {
        String readPath = flowResourcePath + resource.getResourceId() + "_" + resource.getVersion() + ".re";
        return bmlService.readLocalResourceFile(userName, readPath);
    }
    @Override
    public String uploadAppConnResourceNew(String userName, String projectName, DSSFlow dssFlow,
                                           String nodeJson, String flowContextId, String nodePath,
                                           Workspace workspace, String orcVersion, List<DSSLabel> dssLabels) throws DSSErrorException, IOException {
        Map<String, Object> nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(nodeJson, Map.class);
        String nodeType = nodeJsonMap.get("jobType").toString();
        String nodeId = nodeJsonMap.get("id").toString();
        String nodeResourcePath = IoUtils.addFileSeparator(nodePath,APPCONN_FILE_NAME) ;
        Map<String, Object> nodeContent = (LinkedHashMap<String, Object>) nodeJsonMap.get("jobContent");
        CommonAppConnNode appConnNode = new CommonAppConnNode();
        appConnNode.setId(nodeId);
        appConnNode.setName((String) nodeJsonMap.get("title"));
        appConnNode.setDssLabels(dssLabels);
        appConnNode.setNodeType(nodeType);
        appConnNode.setJobContent(nodeContent);
        appConnNode.setFlowId(dssFlow.getId());
        appConnNode.setProjectId(dssFlow.getProjectId());
        appConnNode.setWorkspace(workspace);
        appConnNode.setContextId(flowContextId);

        Map<String, Object> nodeExportContent = null;
        logger.info("nodeResourcePath:{}", nodeResourcePath);
        File file = new File(nodeResourcePath);
        if (file.exists()) {
            InputStream resourceInputStream = bmlService.readLocalResourceFile(userName, nodeResourcePath);
            Supplier<Map<String, Object>> bmlResourceMap = () -> {
                BmlResource resource = bmlService.upload(userName, resourceInputStream, UUID.randomUUID().toString() + ".json",
                        projectName);
                return ImmutableMap.of(
                        "resourceId", resource.getResourceId(),
                        "version", resource.getVersion()
                );
            };
            Supplier<Map<String, Object>> streamResourceMap = () -> MapUtils.newCommonMap(ImportRequestRef.INPUT_STREAM_KEY, resourceInputStream);
            try {
                nodeExportContent = nodeService.importNode(userName, appConnNode, bmlResourceMap, streamResourceMap, orcVersion);
            } catch (ExternalOperationFailedException e) {
                logger.error("failed to import node.", e);
                throw new DSSErrorException(e.getErrCode(), e.getMessage());
            } catch (Exception e) {
                logger.error("failed to import node.", e);
                throw new DSSErrorException(90011, e.getMessage());
            }
            if (nodeExportContent != null) {
                if (nodeExportContent.get("project_id") != null) {
                    Long newProjectId = Long.parseLong(nodeExportContent.get("project_id").toString());
                    logger.warn(String.format("new appConn node add into dss,dssProjectId: %s,newProjectId: %s", appConnNode.getProjectId(), newProjectId));
                    nodeExportContent.remove("project_id");
                }
                nodeJsonMap.replace("jobContent", nodeExportContent);
                appConnNode.setJobContent(nodeExportContent);
                return BDPJettyServerHelper.jacksonJson().writeValueAsString(nodeJsonMap);
            }
        } else {
            logger.warn("appConn node resource file does not exists. nodeId: {}" + nodeId);
        }

        return  nodeJson ;
    }
    @Override
    public String uploadAppConnResource(String userName, String projectName, DSSFlow dssFlow,
                                        String nodeJson, String flowContextId, String appConnResourcePath,
                                        Workspace workspace, String orcVersion, List<DSSLabel> dssLabels) throws DSSErrorException, IOException {
        Map<String, Object> nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(nodeJson, Map.class);
        String nodeType = nodeJsonMap.get("jobType").toString();
        String nodeId = nodeJsonMap.get("id").toString();
        String nodeResourcePath = appConnResourcePath + File.separator + nodeId + ".appconnre";
        if (nodeType.contains("appjoint")) {
            nodeType = nodeType.replace("appjoint", "appconn");
            //兼容0.X导入路径
            nodeResourcePath = nodeResourcePath.replace("appconn", "appjoint");
        }

        Map<String, Object> nodeContent = (LinkedHashMap<String, Object>) nodeJsonMap.get("jobContent");
        CommonAppConnNode appConnNode = new CommonAppConnNode();
        appConnNode.setId(nodeId);
        appConnNode.setName((String) nodeJsonMap.get("title"));
        appConnNode.setDssLabels(dssLabels);
        appConnNode.setNodeType(nodeType);
        appConnNode.setJobContent(nodeContent);
        appConnNode.setFlowId(dssFlow.getId());
        appConnNode.setProjectId(dssFlow.getProjectId());
        appConnNode.setWorkspace(workspace);
        appConnNode.setContextId(flowContextId);

        Map<String, Object> nodeExportContent = null;
        logger.info("nodeResourcePath:{}", nodeResourcePath);
        File file = new File(nodeResourcePath);
        if (file.exists()) {
            InputStream resourceInputStream = bmlService.readLocalResourceFile(userName, nodeResourcePath);
            Supplier<Map<String, Object>> bmlResourceMap = () -> {
                BmlResource resource = bmlService.upload(userName, resourceInputStream, UUID.randomUUID().toString() + ".json",
                        projectName);
                return ImmutableMap.of(
                        "resourceId", resource.getResourceId(),
                        "version", resource.getVersion()
                );
            };
            Supplier<Map<String, Object>> streamResourceMap = () -> MapUtils.newCommonMap(ImportRequestRef.INPUT_STREAM_KEY, resourceInputStream);
            try {
                nodeExportContent = nodeService.importNode(userName, appConnNode, bmlResourceMap, streamResourceMap, orcVersion);
            } catch (ExternalOperationFailedException e) {
                logger.error("failed to import node.", e);
                throw new DSSErrorException(e.getErrCode(), e.getMessage());
            } catch (Exception e) {
                logger.error("failed to import node.", e);
                throw new DSSErrorException(90011, e.getMessage());
            }
            if (nodeExportContent != null) {
                if (nodeExportContent.get("project_id") != null) {
                    Long newProjectId = Long.parseLong(nodeExportContent.get("project_id").toString());
                    logger.warn(String.format("new appConn node add into dss,dssProjectId: %s,newProjectId: %s", appConnNode.getProjectId(), newProjectId));
                    nodeExportContent.remove("project_id");
                }
                nodeJsonMap.replace("jobContent", nodeExportContent);
                appConnNode.setJobContent(nodeExportContent);
                return BDPJettyServerHelper.jacksonJson().writeValueAsString(nodeJsonMap);
            }
        } else {
            logger.warn("appConn node resource file does not exists. nodeId: {}" + nodeId);
        }

        return nodeJson;
    }

    @Override
    public String updateNodeSubflowID(String nodeJson, long subflowID) throws IOException {
        return nodeParser.updateSubFlowID(nodeJson, subflowID);
    }

}
