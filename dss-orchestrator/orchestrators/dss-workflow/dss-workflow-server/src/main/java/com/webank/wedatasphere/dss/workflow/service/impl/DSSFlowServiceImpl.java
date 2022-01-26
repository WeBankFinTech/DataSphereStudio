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

package com.webank.wedatasphere.dss.workflow.service.impl;


import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.ThirdlyAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.entity.node.DSSNode;
import com.webank.wedatasphere.dss.common.entity.node.DSSNodeDefault;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.contextservice.service.ContextService;
import com.webank.wedatasphere.dss.contextservice.service.impl.ContextServiceImpl;
import com.webank.wedatasphere.dss.standard.app.development.ref.CopyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.RefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.plugin.SSOIntegrationConf;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AppConnRefFactoryUtils;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import com.webank.wedatasphere.dss.workflow.common.parser.WorkFlowParser;
import com.webank.wedatasphere.dss.workflow.constant.DSSWorkFlowConstant;
import com.webank.wedatasphere.dss.workflow.dao.FlowMapper;
import com.webank.wedatasphere.dss.workflow.io.export.NodeExportService;
import com.webank.wedatasphere.dss.workflow.io.input.NodeInputService;
import com.webank.wedatasphere.dss.workflow.lock.Lock;
import com.webank.wedatasphere.dss.workflow.service.BMLService;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.linkis.common.conf.Configuration;
import org.apache.linkis.cs.common.utils.CSCommonUtils;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DSSFlowServiceImpl implements DSSFlowService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private FlowMapper flowMapper;
    @Autowired
    private NodeInputService nodeInputService;
//    @Autowired
//    private EventRelationMapper eventRelationMapper;

    @Autowired
    private WorkFlowParser workFlowParser;
    @Autowired
    private BMLService bmlService;
    @Autowired
    private NodeExportService nodeExportService;

    private static ContextService contextService = ContextServiceImpl.getInstance();

    private static final String TOKEN = CommonVars.apply("wds.dss.workspace.token", "BML-AUTH").getValue();

    @Override
    public DSSFlow getFlowByID(Long id) {
        return flowMapper.selectFlowByID(id);
    }

    @Override
    public DSSFlow getFlowWithJsonAndSubFlowsByID(Long rootFlowId) {
        return genDSSFlowTree(rootFlowId);
    }

    private DSSFlow genDSSFlowTree(Long parentFlowId) {
        DSSFlow cyFlow = flowMapper.selectFlowByID(parentFlowId);

        String userName = cyFlow.getCreator();
        Map<String, Object> query = bmlService.query(userName, cyFlow.getResourceId(), cyFlow.getBmlVersion());
        cyFlow.setFlowJson(query.get("string").toString());

        List<Long> subFlowIDs = flowMapper.selectSubFlowIDByParentFlowID(parentFlowId);
        for (Long subFlowID : subFlowIDs) {
            if (cyFlow.getChildren() == null) {
                cyFlow.setChildren(new ArrayList<DSSFlow>());
            }
            DSSFlow SubFlow = genDSSFlowTree(subFlowID);

            cyFlow.addChildren(SubFlow);
        }
        return cyFlow;
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public DSSFlow addFlow(DSSFlow dssFlow,
                               String contextID) throws DSSErrorException {
        try {
            flowMapper.insertFlow(dssFlow);
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage());
            throw new DSSErrorException(90003, "工作流名不能重复");
        }
        Map<String, Object> contextIDMap = new HashMap<>();
        String userName = dssFlow.getCreator();
        if (StringUtils.isNotBlank(contextID)) {
            contextIDMap.put(CSCommonUtils.CONTEXT_ID_STR, contextID);
        } else {
            logger.error("Generate contextID null.");
        }
        String jsonFlow = new Gson().toJson(contextIDMap);
        Map<String, Object> bmlReturnMap = bmlService.upload(userName, jsonFlow, UUID.randomUUID().toString() + ".json",
                dssFlow.getName());

        dssFlow.setResourceId(bmlReturnMap.get("resourceId").toString());

        dssFlow.setBmlVersion(bmlReturnMap.get("version").toString());
        Long parentFlowID = null;
        if (!dssFlow.getRootFlow()) {
            parentFlowID = flowMapper.getParentFlowID(dssFlow.getId());
        }
        //todo update dssflow
        contextService.checkAndSaveContext(jsonFlow, String.valueOf(parentFlowID));
        flowMapper.updateFlowInputInfo(dssFlow);
        return dssFlow;
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public DSSFlow addSubFlow(DSSFlow DSSFlow, Long parentFlowID, String contextIDStr) throws DSSErrorException {
        DSSFlow parentFlow = flowMapper.selectFlowByID(parentFlowID);
        DSSFlow.setProjectID(parentFlow.getProjectID());
        DSSFlow subFlow = addFlow(DSSFlow, contextIDStr);
        //数据库中插入关联信息
        flowMapper.insertFlowRelation(subFlow.getId(), parentFlowID);
        return subFlow;
    }

    @Override
    public DSSFlow getLatestVersionFlow(Long flowID) {
        DSSFlow DSSFlow = getFlowByID(flowID);
        //todo update
        String userName = DSSFlow.getCreator();
        Map<String, Object> query = bmlService.query(userName, DSSFlow.getResourceId(), DSSFlow.getBmlVersion());
        DSSFlow.setFlowJson(query.get("string").toString());
        return DSSFlow;
    }

    @Override
    public DSSFlow getOneVersionFlow(Long flowID) {
        DSSFlow DSSFlow = getFlowByID(flowID);
        String userName = DSSFlow.getCreator();
        Map<String, Object> query = bmlService.query(userName, DSSFlow.getResourceId(), DSSFlow.getBmlVersion());
        DSSFlow.setFlowJson(query.get("string").toString());
        return DSSFlow;
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public void updateFlowBaseInfo(DSSFlow DSSFlow) throws DSSErrorException {
        try {
            flowMapper.updateFlowBaseInfo(DSSFlow);
        } catch (DuplicateKeyException e) {
            logger.info(e.getMessage());
            throw new DSSErrorException(90003, "工作流名不能重复");
        }
    }

    @Lock
    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public void batchDeleteFlow(List<Long> flowIDlist) {
        flowIDlist.stream().forEach(f -> {
            deleteFlow(f);
        });
    }

    /*@Lock*/
    @Transactional(rollbackFor = {DSSErrorException.class})
    @Override
    public String saveFlow(Long flowID,
                           String jsonFlow,
                           String comment,
                           String userName,
                           String workspaceName,
                           String projectName
    ) throws DSSErrorException {

        DSSFlow dssFlow = flowMapper.selectFlowByID(flowID);
        String creator = dssFlow.getCreator();
        if (StringUtils.isNotEmpty(creator)) {
            userName = creator;
        }
        String flowJsonOld = getFlowJson(userName,projectName, dssFlow);
        if(isEqualTwoJson(flowJsonOld,jsonFlow)){
            logger.info("saveFlow is not change");
            return dssFlow.getBmlVersion();
        }else{
            logger.info("saveFlow is change");
        }
        String resourceId = dssFlow.getResourceId();
        Long parentFlowID = flowMapper.getParentFlowID(flowID);
        // 这里不要检查ContextID具体版本等，只要存在就不创建 2020-0423
        jsonFlow = contextService.checkAndCreateContextID(jsonFlow, dssFlow.getBmlVersion(),
                workspaceName, projectName, dssFlow.getName(), userName, false);

        Map<String, Object> bmlReturnMap = bmlService.update(userName, resourceId, jsonFlow);

        dssFlow.setId(flowID);
        dssFlow.setHasSaved(true);
        dssFlow.setDescription(comment);
        dssFlow.setResourceId(bmlReturnMap.get("resourceId").toString());
        dssFlow.setBmlVersion(bmlReturnMap.get("version").toString());
        //todo 数据库增加版本更新
        flowMapper.updateFlowInputInfo(dssFlow);

        try {
            contextService.checkAndSaveContext(jsonFlow,  String.valueOf(parentFlowID));
        }  catch (DSSErrorException e) {
            logger.error("Failed to saveContext: ", e);
        }

        String version = bmlReturnMap.get("version").toString();
        return version;
    }

    public boolean isEqualTwoJson(String oldJsonNode,String newJsonNode){
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(oldJsonNode).getAsJsonObject();
        jsonObject.remove("updateTime");
        jsonObject.remove("comment");
        String tempOldJson = gson.toJson(jsonObject);

        JsonObject jsonObject2  = parser.parse(newJsonNode).getAsJsonObject();
        jsonObject2.remove("updateTime");
        jsonObject2.remove("comment");
        String tempNewJson = gson.toJson(jsonObject2);
        return tempOldJson.equals(tempNewJson);
    }

    public String getFlowJson(String userName,String projectName, DSSFlow dssFlow){
        String flowExportSaveBasePath = IoUtils.generateIOPath(userName, projectName, "");
        String savePath = flowExportSaveBasePath + File.separator + dssFlow.getName() + File.separator + dssFlow.getName() + ".json";
        String flowJson = bmlService.downloadAndGetFlowJson(userName, dssFlow.getResourceId(), dssFlow.getBmlVersion(), savePath);
        return flowJson;
    }

    @Override
    public Integer getParentRank(Long parentFlowID) {
        return getFlowByID(parentFlowID).getRank();
    }

    @Override
    public Long getParentFlowID(Long flowID) {
        return flowMapper.getParentFlowID(flowID);
    }

    @Deprecated
    private Integer getParentFlowIDByFlowID(Long flowID, Integer rank) {
        Long parentFlowID = flowMapper.selectParentFlowIDByFlowID(flowID);
        if (parentFlowID != null) {
            rank++;
            getParentFlowIDByFlowID(parentFlowID, rank);
        }
        return rank;
    }

    public void deleteFlow(Long flowId) {
        List<Long> subFlowIDs = flowMapper.selectSubFlowIDByParentFlowID(flowId);
        for (Long subFlowID : subFlowIDs) {
            deleteFlow(subFlowID);
        }
        for (Long subFlowID : subFlowIDs) {
            deleteDWSDB(subFlowID);
            // TODO: 2019/6/5 wtss发布过的工作流的删除？
            // TODO: 2019/6/5 json中资源的删除
            // TODO: 2019/6/5 事务的保证
        }
        deleteDWSDB(flowId);
    }

    private void deleteDWSDB(Long flowID) {

        flowMapper.deleteFlowBaseInfo(flowID);
        flowMapper.deleteFlowRelation(flowID);
        //第一期没有工作流的发布，所以不需要删除dws工作流的发布表
    }


    @Override
    public DSSFlow copyRootFlow(Long rootFlowId, String userName, String workspaceName, String projectName, String version,String contextIdStr) throws DSSErrorException, IOException {
        DSSFlow dssFlow = flowMapper.selectFlowByID(rootFlowId);
        DSSFlow rootFlowWithSubFlows = copyFlowAndSetSubFlowInDB(dssFlow, userName);
        updateFlowJson(userName, workspaceName, projectName, rootFlowWithSubFlows, version, null,contextIdStr);
        DSSFlow copyFlow = flowMapper.selectFlowByID(rootFlowWithSubFlows.getId());
        return copyFlow;
    }

    private DSSFlow copyFlowAndSetSubFlowInDB(DSSFlow DSSFlow,
                                              String userName) {
        DSSFlow cyFlow = new DSSFlow();
        BeanUtils.copyProperties(DSSFlow, cyFlow, "children", "flowVersions");
        //封装flow信息
        cyFlow.setCreator(userName);
        cyFlow.setCreateTime(new Date());
        cyFlow.setId(null);
        flowMapper.insertFlow(cyFlow);
        List<Long> subFlowIDs = flowMapper.selectSubFlowIDByParentFlowID(DSSFlow.getId());
        for (Long subFlowID : subFlowIDs) {
            DSSFlow subDSSFlow = flowMapper.selectFlowByID(subFlowID);
            if (DSSFlow.getChildren() == null) {
                DSSFlow.setChildren(new ArrayList<DSSFlow>());
            }
            DSSFlow copySubFlow = copyFlowAndSetSubFlowInDB(subDSSFlow, userName);
            persistenceFlowRelation(copySubFlow.getId(), cyFlow.getId());
            cyFlow.addChildren(copySubFlow);
        }
        return cyFlow;
    }

    private void updateFlowJson(String userName, String workspaceName, String projectName, DSSFlow rootFlow, String version, Long parentFlowId,String contextIdStr) throws DSSErrorException, IOException {
        String flowJson = bmlService.readFlowJsonFromBML(userName, rootFlow.getResourceId(), rootFlow.getBmlVersion());
        //如果包含subflow,需要一同导入subflow内容，并更新parrentflow的json内容
        // TODO: 2020/7/31 优化update方法里面的saveContent
        String updateFlowJson = updateFlowContextId(flowJson,contextIdStr);
        //重新上传工作流资源
        updateFlowJson = uploadFlowResourceToBml(userName, updateFlowJson,projectName,rootFlow);
        //上传节点的资源或调用appconn的copyRef
        updateFlowJson = updateWorkFlowNodeJson(userName, projectName, updateFlowJson, rootFlow, version);
        List<? extends DSSFlow> subFlows = rootFlow.getChildren();
        if (subFlows != null) {
            for (DSSFlow subflow : subFlows) {
                updateFlowJson(userName, workspaceName, projectName, subflow, version, rootFlow.getId(),contextIdStr);
            }
        }

        DSSFlow updateDssFlow = uploadFlowJsonToBml(userName, projectName, rootFlow, updateFlowJson);
        //todo add dssflow to database
        flowMapper.updateFlowInputInfo(updateDssFlow);
        contextService.checkAndSaveContext(updateFlowJson, String.valueOf(parentFlowId));
    }

    //上传工作流资源
    public String uploadFlowResourceToBml(String userName, String flowJson, String projectName, DSSFlow dssFlow) throws IOException {
        List<Resource> resourceList = workFlowParser.getWorkFlowResources(flowJson);
        if (CollectionUtils.isEmpty(resourceList)) {
            return flowJson;
        }
        String flowExportSaveBasePath = IoUtils.generateIOPath(userName, projectName, "");
        String savePath = flowExportSaveBasePath + File.separator + dssFlow.getName() + File.separator + "resource";
        //上传文件获取resourceId和version save应该是已经有
        resourceList.forEach(resource -> {
            //从bml取出resource
            String flowResourcePath = savePath + File.separator + resource.getResourceId() + ".re";
            bmlService.downloadToLocalPath(userName, resource.getResourceId(), resource.getVersion(), flowResourcePath);
            //重新上传resource
            InputStream resourceInputStream = bmlService.readLocalResourceFile(userName, flowResourcePath);
            Map<String, Object> bmlReturnMap = bmlService.upload(userName, resourceInputStream, UUID.randomUUID().toString() + ".json", projectName);
            resource.setResourceId(bmlReturnMap.get("resourceId").toString());
            resource.setVersion(bmlReturnMap.get("version").toString());
        });
        //更新flowJson的resources
        return workFlowParser.updateFlowJsonWithKey(flowJson, "resources", resourceList);
    }


    private DSSFlow uploadFlowJsonToBml(String userName, String projectName, DSSFlow dssFlow, String flowJson) throws IOException {
        //上传文件获取resourceId和version save应该是已经有
        Map<String, Object> bmlReturnMap;

        //上传文件获取resourceId和version save应该是已经有
        bmlReturnMap = bmlService.upload(userName, flowJson, UUID.randomUUID().toString() + ".json", projectName);

        dssFlow.setCreator(userName);
        dssFlow.setBmlVersion(bmlReturnMap.get("version").toString());
        dssFlow.setResourceId(bmlReturnMap.get("resourceId").toString());
        dssFlow.setDescription("copy update workflow");
        dssFlow.setSource("copy更新");
        //version表中插入数据
        return dssFlow;
    }

    private String updateFlowContextId(String flowJson,String contextIdStr) throws  IOException {
//        String parentFlowIdStr = null;
//        if (parentFlowId != null) {
//            parentFlowIdStr = parentFlowId.toString();
//        }
//        String contextID = contextService.checkAndInitContext(flowJson, parentFlowIdStr, workspace, projectName, flowName, flowVersion, userName);
        String updatedFlowJson = workFlowParser.updateFlowJsonWithKey(flowJson, "contextID", contextIdStr);
        return updatedFlowJson;
    }


    private String updateWorkFlowNodeJson(String userName, String projectName, String flowJson, DSSFlow DSSFlow, String version) throws DSSErrorException, IOException {
        if (StringUtils.isEmpty(version)) {
            logger.warn("version id is null when updateWorkFlowNodeJson");
            version = String.valueOf((new Random().nextLong()));
        }
        List<String> nodeJsonList = workFlowParser.getWorkFlowNodesJson(flowJson);
        if (nodeJsonList == null) {
            throw new DSSErrorException(90073, "工作流内没有工作流节点，导入失败" + DSSFlow.getName());
        }
        String updateContextId = workFlowParser.getValueWithKey(flowJson, "contextID");
        if (nodeJsonList.size() == 0) {
            return flowJson;
        }
        List<DSSFlow> subflows = (List<DSSFlow>) DSSFlow.getChildren();
        List<Map<String, Object>> nodeJsonListRes = new ArrayList<>();
        if (nodeJsonList != null && nodeJsonList.size() > 0) {
            for (String nodeJson : nodeJsonList) {
                //重新上传一份jar文件到bml
                String updateNodeJson = inputNodeFiles(userName, projectName, nodeJson);

                Map<String, Object> nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(updateNodeJson, Map.class);
                //更新subflowID
                String nodeType = nodeJsonMap.get("jobType").toString();
                if ("workflow.subflow".equals(nodeType)) {
                    String subFlowName = nodeJsonMap.get("title").toString();
                    List<DSSFlow> DSSFlowList = subflows.stream().filter(subflow ->
                            subflow.getName().equals(subFlowName)
                    ).collect(Collectors.toList());
                    if (DSSFlowList.size() == 1) {
                        updateNodeJson = nodeInputService.updateNodeSubflowID(updateNodeJson, DSSFlowList.get(0).getId());
                        nodeJsonMap = BDPJettyServerHelper.jacksonJson().readValue(updateNodeJson, Map.class);
                        nodeJsonListRes.add(nodeJsonMap);
                    } else if (DSSFlowList.size() > 1) {
                        logger.error("工程内存在重复的子工作流节点名称，导入失败" + subFlowName);
                        throw new DSSErrorException(90077, "工程内存在重复的子工作流节点名称，导入失败" + subFlowName);
                    } else {
                        logger.error("工程内存在重复的子工作流节点名称，导入失败" + subFlowName);
                        throw new DSSErrorException(90078, "工程内未能找到子工作流节点，导入失败" + subFlowName);
                    }
                } else {
                    //todo  appconn节点的copy和json更新   目前只调用visualis、qualitis  的copy方法 jobContent加上非空校验，如果为空就不调用
                    if (nodeJsonMap.get("jobContent") != null
                            && (nodeType.contains(DSSWorkFlowConstant.APPCONN_NAME_VISUALIS)
                            || nodeType.contains(DSSWorkFlowConstant.APPCONN_NAME_QUALITIS))) {
                        try {
                            logger.info("begin to copy {} node {}, content is {} version is {}", nodeType,
                                    nodeJsonMap, nodeJsonMap.get("jobContent"), version);
                            AppConn appConn = null;
                            if (nodeType.contains(DSSWorkFlowConstant.APPCONN_NAME_VISUALIS)) {
                                appConn = AppConnManager.getAppConnManager().getAppConn(DSSWorkFlowConstant.APPCONN_NAME_VISUALIS);
                            } else if (nodeType.contains(DSSWorkFlowConstant.APPCONN_NAME_QUALITIS)) {
                                appConn = AppConnManager.getAppConnManager().getAppConn(DSSWorkFlowConstant.APPCONN_NAME_QUALITIS);
                            }
                            List<DSSLabel> devLabels = Lists.newArrayList(new EnvDSSLabel(DSSWorkFlowConstant.DSS_IMPORT_ENV.getValue().trim()));
                            AppInstance devInstance = appConn.getAppDesc().getAppInstancesByLabels(devLabels).get(0);
                            DevelopmentIntegrationStandard standard =
                                    ((ThirdlyAppConn) appConn).getOrCreateDevelopmentStandard();
                            RefCRUDService refCRUDService = standard.getRefCRUDService(devInstance);
                            CopyRequestRef requestRef =
                                    AppConnRefFactoryUtils.newAppConnRefByPackageName(CopyRequestRef.class, appConn.getClass().getClassLoader(), appConn.getClass().getPackage().getName());
                            requestRef.setParameter("jobContent", nodeJsonMap.get("jobContent"));
                            requestRef.setParameter("user", userName);
                            requestRef.setParameter("orcVersion", version);
                            requestRef.setParameter("contextId", updateContextId);
                            requestRef.setParameter("nodeType", nodeType);
                            String workspaceStr = "";
                            try {
                                workspaceStr = getWorkspace(userName);
                            } catch (final Exception e) {
                                logger.error("failed to get workspace");
                            }
                            Workspace workspace = SSOIntegrationConf.gson().fromJson(workspaceStr, Workspace.class);
                            requestRef.setWorkspace(workspace);
                            ResponseRef responseRef = refCRUDService.getRefCopyOperation().copyRef(requestRef);
                            logger.info("end to copy vis node response is {}", responseRef.toMap());
                            nodeJsonMap.put("jobContent", responseRef.toMap());
                            logger.info("end to copy {} node {}, content is {} version is {}", nodeType,
                                    nodeJsonMap, nodeJsonMap.get("jobContent"), version);
                        } catch (Exception sumExp) {
                            logger.error("copyRef error ", sumExp);
//                            throw sumExp;
                        }
                        nodeJsonListRes.add(nodeJsonMap);
                    } else {
                        nodeJsonListRes.add(nodeJsonMap);
                    }
                }
            }
        }

        return workFlowParser.updateFlowJsonWithKey(flowJson, "nodes", nodeJsonListRes);
    }

    //由于每一个节点可能含有jar文件，这个功能不能直接复制使用，因为删掉新版本节点会直接删掉旧版本的node中的jar文件
    //所以重新上传一份jar文件到bml
    private String inputNodeFiles(String userName, String projectName, String nodeJson) throws IOException {
        String flowPath = IoUtils.generateIOPath(userName, projectName, "");
        String workFlowResourceSavePath = flowPath + File.separator + "resource" + File.separator;
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(nodeJson).getAsJsonObject();
        DSSNode node = gson.fromJson(jsonObject, new TypeToken<DSSNodeDefault>() {
        }.getType());
        //先导出来
        nodeExportService.downloadNodeResourceToLocal(userName, node, workFlowResourceSavePath);
        //后导入到bml
        String updateNodeJson = nodeInputService.uploadResourceToBml(userName, nodeJson, workFlowResourceSavePath, projectName);
        return updateNodeJson;
    }

    private String getWorkspace(String user) throws Exception {
        String linkisUrl = Configuration.getGateWayURL();
        String token = TOKEN;
        String dssUrl = linkisUrl.endsWith("/") ? linkisUrl + "api/rest_j/v1/dss/framework/project/getWorkSpaceStr"
                : linkisUrl + "/api/rest_j/v1/dss/framework/project/getWorkSpaceStr";
        HttpGet httpGet = new HttpGet(dssUrl);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("Token-User", user));
        params.add(new BasicNameValuePair("Token-Code", token));
        httpGet.addHeader("Token-User", user);
        httpGet.addHeader("Token-Code", token);
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpClientContext context;
        String responseContent;
        try {
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            context = HttpClientContext.create();
            response = httpClient.execute(httpGet, context);
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity,"utf-8");
            Gson gson = new Gson();
            Map resultMap = gson.fromJson(responseContent, Map.class);
            Object obj = resultMap.get("data");
            if (obj instanceof Map){
                if (null != ((Map) obj).get("workspaceStr")){
                    return ((Map) obj).get("workspaceStr").toString();
                }else {
                    return "";
                }
            }
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
        return "";
    }


    private void persistenceFlowRelation(Long flowID, Long parentFlowID) {
        DSSFlowRelation relation = flowMapper.selectFlowRelation(flowID, parentFlowID);
        if (relation == null) {
            flowMapper.insertFlowRelation(flowID, parentFlowID);
        }
    }

}
