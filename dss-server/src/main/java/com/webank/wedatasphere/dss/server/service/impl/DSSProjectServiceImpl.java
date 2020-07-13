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

package com.webank.wedatasphere.dss.server.service.impl;


import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.scheduler.SchedulerAppJoint;
import com.webank.wedatasphere.dss.appjoint.scheduler.entity.SchedulerProject;
import com.webank.wedatasphere.dss.appjoint.scheduler.hooks.ProjectPublishHook;
import com.webank.wedatasphere.dss.appjoint.scheduler.parser.ProjectParser;
import com.webank.wedatasphere.dss.appjoint.scheduler.tuning.ProjectTuning;
import com.webank.wedatasphere.dss.appjoint.service.ProjectService;
import com.webank.wedatasphere.dss.application.entity.Application;
import com.webank.wedatasphere.dss.application.service.ApplicationService;
import com.webank.wedatasphere.dss.common.entity.flow.DSSFlow;
import com.webank.wedatasphere.dss.common.entity.flow.DSSFlowVersion;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.entity.project.DSSProjectPublishHistory;
import com.webank.wedatasphere.dss.common.entity.project.DSSProjectVersion;
import com.webank.wedatasphere.dss.common.entity.project.Project;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.protocol.RequestDSSProject;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.server.constant.DSSServerConstant;
import com.webank.wedatasphere.dss.server.dao.*;
import com.webank.wedatasphere.dss.server.entity.DSSFlowTaxonomy;
import com.webank.wedatasphere.dss.server.entity.DSSProjectTaxonomyRelation;
import com.webank.wedatasphere.dss.server.function.FunctionInvoker;
import com.webank.wedatasphere.dss.server.lock.Lock;
import com.webank.wedatasphere.dss.server.lock.LockEnum;
import com.webank.wedatasphere.dss.server.service.BMLService;
import com.webank.wedatasphere.dss.server.service.DSSFlowService;
import com.webank.wedatasphere.dss.server.service.DSSProjectService;
import com.webank.wedatasphere.dss.server.util.ThreadPoolTool;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
public class DSSProjectServiceImpl implements DSSProjectService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ProjectTaxonomyMapper projectTaxonomyMapper;
    @Autowired
    private DSSUserMapper dssUserMapper;
    @Autowired
    private FlowMapper flowMapper;
    @Autowired
    private FlowTaxonomyMapper flowTaxonomyMapper;
    @Autowired
    private DSSFlowService flowService;
    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private BMLService bmlService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private FunctionInvoker functionInvoker;

    private SchedulerAppJoint schedulerAppJoint=null;

    @Override
    public DSSProject getProjectByID(Long id) {
        /*JdbcProjectImpl instance = wtssdbConnector.getInjector().getInstance(JdbcProjectImpl.class);
        Project project = instance.fetchProjectById(id.intValue());
        DSSProject DSSProject = EntityUtils.Project2DSSProject(project);*/
        return projectMapper.selectProjectByID(id);
    }

    @Transactional(rollbackFor = {DSSErrorException.class,AppJointErrorException.class})
    @Override
    public Long addProject(String userName, String name, String description, Long taxonomyID,String product,Integer applicationArea,String business, Long workspaceId) throws DSSErrorException, AppJointErrorException {
        DSSProject dssProject = new DSSProject();
        dssProject.setUserName(userName);
        dssProject.setName(name);
        dssProject.setDescription(description);
        //创建scheduler的project
        if(existSchesulis()){
            createSchedulerProject(dssProject);
        }
        //创建appjoint的project
        Map<Long,Long> appjointProjectIDAndAppID = createAppjointProject(dssProject);
        Long userID = dssUserMapper.getUserID(userName);
        //创建dss自己的project
        Pair<Long, Long> pair = addDSSProject(userID, name, description,product,applicationArea,business, workspaceId);
        //添加关联
        projectTaxonomyMapper.addProjectTaxonomyRelation(pair.getFirst(), taxonomyID, userID);
        if(!appjointProjectIDAndAppID.isEmpty())projectMapper.addAccessProjectRelation(appjointProjectIDAndAppID,pair.getFirst());
        return pair.getSecond();
    }



    private Map<Long,Long> createAppjointProject(DSSProject project) throws DSSErrorException, AppJointErrorException {
        Map applicationProjectIDs = new HashMap<Long,Long>();
        List<Pair<Project, String>> pairs = functionInvoker.projectServiceAddFunction(project, ProjectService::createProject, applicationService.listAppjoint());
        for (Pair<Project, String> pair : pairs) {
            if(pair.getFirst().getId() != null){
                applicationProjectIDs.put(applicationService.getApplication(pair.getSecond()).getId(),pair.getFirst().getId());
            }
        }
        return applicationProjectIDs;
    }

    private Pair<Long,Long> addDSSProject(Long userID, String name, String description, String product, Integer applicationArea, String business, Long workspaceId) {
        DSSProject dssProject = new DSSProject();
        dssProject.setUserID(userID);
        dssProject.setName(name);
        dssProject.setDescription(description);
        dssProject.setSource(DSSServerConstant.DSS_PROJECT_SOURCE);
        dssProject.setCreateTime(new Date());
        dssProject.setCreateBy(userID);
        dssProject.setProduct(product);
        dssProject.setApplicationArea(applicationArea);
        dssProject.setBusiness(business);
        dssProject.setWorkspaceId(workspaceId);
        projectMapper.addProject(dssProject);
        DSSProjectVersion dssProjectVersion = new DSSProjectVersion();
        dssProjectVersion.setComment(DSSServerConstant.DSS_PROJECT_FIRST_VERSION_COMMENT);
        dssProjectVersion.setProjectID(dssProject.getId());
        dssProjectVersion.setUpdateTime(new Date());
        dssProjectVersion.setUpdatorID(userID);
        dssProjectVersion.setVersion(DSSServerConstant.DSS_PROJECT_FIRST_VERSION);
        dssProjectVersion.setLock(0);
        projectMapper.addProjectVersion(dssProjectVersion);
        return new Pair<Long,Long>(dssProject.getId(), dssProjectVersion.getId());
    }

    private void createSchedulerProject(DSSProject dssProject) throws DSSErrorException {
        try {
            if(getSchedulerAppJoint() != null) {
                functionInvoker.projectServiceAddFunction(dssProject, ProjectService::createProject, Arrays.asList(getSchedulerAppJoint()));
            }else{
                logger.error("Add scheduler project failed for scheduler appjoint is null");
            }
        } catch (Exception e) {
            logger.error("add scheduler project failed,", e);
            throw new DSSErrorException(90002, "add scheduler project failed" + e.getMessage());
        }
    }

    @Override
    public void updateProject(long projectID, String name, String description, String userName, String product ,Integer applicationArea ,String business) throws AppJointErrorException {
        // TODO: 2019/9/19  appjoint的update
        //无法修改名字
        //更新wtssProject中的description
        if(!StringUtils.isBlank(description)){
            DSSProject project = getProjectByID(projectID);
            project.setUserName(userName);
            project.setDescription(description);
            if(existSchesulis()){
                if(getSchedulerAppJoint() != null) {
                     functionInvoker.projectServiceFunction(project,ProjectService::updateProject,Arrays.asList(getSchedulerAppJoint()));
                }else{
                    logger.error("Update scheduler project failed for scheduler appjoint is null");
                }
            }
            functionInvoker.projectServiceFunction(project,ProjectService::updateProject,applicationService.listAppjoint());
        }
        projectMapper.updateDescription(projectID, description, product ,applicationArea ,business);
    }

    @Transactional(rollbackFor = DSSErrorException.class)
    @Override
    public void deleteProject(long projectID, String userName, Boolean ifDelScheduler) throws DSSErrorException {
        try {
            DSSProject project = getProjectByID(projectID);
            project.setUserName(userName);
            if(ifDelScheduler){
                if(existSchesulis()){
                    if(getSchedulerAppJoint() != null) {
                        functionInvoker.projectServiceFunction(project, ProjectService::deleteProject, Arrays.asList(getSchedulerAppJoint()));
                    }else{
                        logger.error("Delete scheduler project failed for scheduler appjoint is null");
                    }
                }
            }
            functionInvoker.projectServiceFunction(project,ProjectService::deleteProject,applicationService.listAppjoint());
            // TODO: 2019/11/15 删除relations表
        } catch (Exception e) {
            logger.info("删除工程失败，原因：", e);
            String errorMsg = e.getCause() == null ? e.getMessage() : e.getCause().getMessage();
            throw new DSSErrorException(90012, errorMsg);
        }
        flowTaxonomyMapper.deleteFlowTaxonomyByProjectID(projectID);
        List<DSSFlow> dssFlowList = flowMapper.listFlowByProjectID(projectID);
        flowService.batchDeleteFlow(dssFlowList.stream().map(f -> f.getId()).distinct().collect(Collectors.toList()), null);
        projectMapper.deleteProjectVersions(projectID);
        projectMapper.deleteProjectBaseInfo(projectID);
        projectTaxonomyMapper.deleteProjectTaxonomyRelationByProjectID(projectID);
    }

    @Override
    public DSSProject getLatestVersionProject(Long projectID) {
        DSSProject dssProject = getProjectByID(projectID);
        DSSProjectVersion dssProjectVersion = projectMapper.selectLatestVersionByProjectID(projectID);
        dssProject.setLatestVersion(dssProjectVersion);
        return dssProject;
    }

    /**
     * 通过projectVersionID获取一个projrct对象，不会返回最新的版本信息
     *
     * @param projectVersionID
     * @return
     */
    @Override
    public DSSProject getProjectByProjectVersionID(Long projectVersionID) {
        DSSProject dssProject = projectMapper.selectProjectByVersionID(projectVersionID);
        return dssProject;
    }

    @Override
    public Boolean isPublished(Long projectID) {
        return !projectMapper.noPublished(projectID);
    }

    @Override
    public List<DSSProjectVersion> listAllProjectVersions(Long projectID) {
        List<DSSProjectVersion> dssProjectVersions = projectMapper.listProjectVersionsByProjectID(projectID);
        for (DSSProjectVersion dssProjectVersion : dssProjectVersions) {
            DSSProjectPublishHistory publishHistory = projectMapper.selectProjectPublishHistoryByProjectVersionID(dssProjectVersion.getId());
            dssProjectVersion.setPublishHistory(publishHistory);
        }
        return dssProjectVersions;
    }


    @Lock(type = LockEnum.ADD)
    @Transactional(rollbackFor = {DSSErrorException.class, InterruptedException.class,AppJointErrorException.class})
    @Override
    public void publish(Long projectVersionID, String userName, String comment) throws DSSErrorException, InterruptedException, AppJointErrorException {

        SchedulerAppJoint schedulerAppJoint = getSchedulerAppJoint();
        if(schedulerAppJoint != null) {
            ProjectParser projectParser = schedulerAppJoint.getProjectParser();
            ProjectTuning projectTuning = schedulerAppJoint.getProjectTuning();
            ProjectPublishHook[] projectPublishHooks = schedulerAppJoint.getProjectPublishHooks();
            // TODO: 2019/9/24 try catch 下载json要挪到parser去
            //1.封装DSSProject
            DSSProject dssProject = projectMapper.selectProjectByVersionID(projectVersionID);
            dssProject.setUserName(dssUserMapper.getuserName(dssProject.getUserID()));
            logger.info(userName + "-开始发布工程：" + dssProject.getName() + "版本ID为：" + projectVersionID);
            ArrayList<DSSFlow> dssFlows = new ArrayList<>();
            List<DSSFlowVersion> dssFlowVersionList = flowMapper.listLatestRootFlowVersionByProjectVersionID(projectVersionID);
            for (DSSFlowVersion dssFlowVersion : dssFlowVersionList) {
                DSSFlow dssFlow = flowMapper.selectFlowByID(dssFlowVersion.getFlowID());
                String json = (String) bmlService.query(userName, dssFlowVersion.getJsonPath(), dssFlowVersion.getVersion()).get("string");
                if (!dssFlow.getHasSaved()) {
                    logger.info("工作流{}从未保存过，忽略", dssFlow.getName());
                } else if (StringUtils.isNotBlank(json)) {
                    dssFlowVersion.setJson(json);
                    dssFlow.setLatestVersion(dssFlowVersion);
                    createPublishProject(userName, dssFlowVersion.getFlowID(), dssFlow, projectVersionID);
                    dssFlows.add(dssFlow);
                } else {
                    String warnMsg = String.format(DSSServerConstant.PUBLISH_FLOW_REPORT_FORMATE, dssFlow.getName(), dssFlowVersion.getVersion());
                    logger.info(warnMsg);
                    throw new DSSErrorException(90013, warnMsg);
                }
            }
            if (dssFlows.isEmpty()) throw new DSSErrorException(90007, "该工程没有可以发布的工作流,请检查工作流是否都为空");
            dssProject.setFlows(dssFlows);
            //2.封装DSSProject完成，开始发布
            SchedulerProject schedulerProject = projectParser.parseProject(dssProject);
            projectTuning.tuningSchedulerProject(schedulerProject);
            Stream.of(projectPublishHooks).forEach(DSSExceptionUtils.handling(hook -> hook.prePublish(schedulerProject)));
            (schedulerAppJoint.getProjectService()).publishProject(schedulerProject, schedulerAppJoint.getSecurityService().login(userName));
            Stream.of(projectPublishHooks).forEach(DSSExceptionUtils.handling(hook -> hook.postPublish(schedulerProject)));
            //3.发布完成后复制工程
            DSSProjectVersion dssProjectVersion = projectMapper.selectProjectVersionByID(projectVersionID);
            copyProjectVersionMax(projectVersionID, dssProjectVersion, dssProjectVersion, userName, null);
        }else {
            logger.error("SchedulerAppJoint is null");
            throw new DSSErrorException(90014, "SchedulerAppJoint is null");
        }
    }

    @Override
    public Long createPublishHistory(String comment, Long creatorID, Long projectVersionID) {
        DSSProjectPublishHistory dssProjectPublishHistory = new DSSProjectPublishHistory();
        dssProjectPublishHistory.setComment(comment);
        dssProjectPublishHistory.setCreateID(creatorID);
        dssProjectPublishHistory.setCreateTime(new Date());
        dssProjectPublishHistory.setUpdateTime(new Date());
        dssProjectPublishHistory.setProjectVersionID(projectVersionID);
        dssProjectPublishHistory.setState(0);
        projectMapper.insertPublishHistory(dssProjectPublishHistory);
        return dssProjectPublishHistory.getId();
    }

    @Override
    public void updatePublishHistory(Long projectVersionID, Integer status, Date updateTime) {
        projectMapper.updatePublishHistoryState(projectVersionID, status);
    }

    @Override
    public DSSProjectPublishHistory getPublishHistoryByID(Long projectVersionID) {
        return projectMapper.selectProjectPublishHistoryByProjectVersionID(projectVersionID);
    }

    @Override
    public DSSProject getExecutionDSSProject(RequestDSSProject requestDSSProject) throws DSSErrorException {
        DSSFlow dssFlow = flowService.getOneVersionFlow(requestDSSProject.flowId(), requestDSSProject.version(), requestDSSProject.projectVersionId());
        DSSProject dssProject = projectMapper.selectProjectByVersionID(requestDSSProject.projectVersionId());
        dssProject.setUserName(dssUserMapper.getuserName(dssProject.getUserID()));
        DSSFlow returnFlow = recursiveGenerateParentFlow(dssFlow, requestDSSProject);
        dssProject.setFlows(Arrays.asList(returnFlow));
        return dssProject;
    }

    @Override
    public Long getAppjointProjectID(Long projectID, String nodeType) {
        // TODO: 2019/11/15 判断工程在dss中存在
        Application applicationbyNodeType = applicationService.getApplicationbyNodeType(nodeType);
        Long appjointProjectID = projectMapper.getAppjointProjectID(projectID, applicationbyNodeType.getId());
        return appjointProjectID == null? projectID:appjointProjectID;
    }

    @Override
    public Long getAppjointProjectIDByApplicationName(Long projectID, String applicationName) {
        Long appjointProjectID = projectMapper.getAppjointProjectID(projectID, applicationService.getApplication(applicationName).getId());
        return appjointProjectID == null? projectID:appjointProjectID;
    }

    private DSSFlow recursiveGenerateParentFlow(DSSFlow dssFlow, RequestDSSProject requestDSSProject) throws DSSErrorException {
        DSSFlow returnFlow = null;
        Long parentFlowID = flowService.getParentFlowID(dssFlow.getId());
        if(parentFlowID != null){
            //对于当前执行的工作流的父工作流，直接找其最新的版本
            DSSFlow parentFlow = flowService.getLatestVersionFlow(parentFlowID, requestDSSProject.projectVersionId());
            parentFlow.setChildren(Arrays.asList(dssFlow));
            returnFlow = recursiveGenerateParentFlow(parentFlow, requestDSSProject);
        }else {
            returnFlow = dssFlow;
        }
        return returnFlow;
    }

    private void createPublishProject(String userName, Long parentFlowID, DSSFlow dssFlowParent, Long projectVersionID) throws DSSErrorException {
       List<Long> subFlowIDS = flowMapper.selectSubFlowIDByParentFlowID(parentFlowID);
        ArrayList<DSSFlow> dssFlows = new ArrayList<>();
        for (Long subFlowID : subFlowIDS) {
            DSSFlowVersion dssFlowVersion = flowService.getLatestVersionByFlowIDAndProjectVersionID(subFlowID, projectVersionID);
            if (dssFlowVersion != null) { //subFlowIDS通过flow关联查出来的，但是有可能最新版本的project对已有的flows做了删除
                DSSFlow dssFlow = flowMapper.selectFlowByID(dssFlowVersion.getFlowID());
                String json = (String) bmlService.query(userName, dssFlowVersion.getJsonPath(), dssFlowVersion.getVersion()).get("string");
                if (!dssFlow.getHasSaved()) {
                    logger.info("工作流{}从未保存过，忽略", dssFlow.getName());
                }else if (StringUtils.isNotBlank(json)){
                    dssFlowVersion.setJson(json);
                    dssFlow.setLatestVersion(dssFlowVersion);
                    createPublishProject(userName, subFlowID, dssFlow, projectVersionID);
                    dssFlows.add(dssFlow);
                } else {
                    String warnMsg = String.format(DSSServerConstant.PUBLISH_FLOW_REPORT_FORMATE, dssFlow.getName(), dssFlowVersion.getVersion());
                    logger.info(warnMsg);
                    throw new DSSErrorException(90013, warnMsg);
                }
            }
        }
        dssFlowParent.setChildren(dssFlows);
    }

    /*
    复制工程
     */
    @Lock
    @Transactional(rollbackFor = {DSSErrorException.class, InterruptedException.class,AppJointErrorException.class})
    @Override
    public Long copyProject(Long projectVersionID, Long projectID, String projectName, String userName) throws DSSErrorException, InterruptedException, AppJointErrorException {
        DSSProject project = projectMapper.selectProjectByID(projectID);
        if (StringUtils.isNotEmpty(projectName)) {project.setName(projectName);}
        DSSProjectTaxonomyRelation projectTaxonomyRelation = projectTaxonomyMapper.selectProjectTaxonomyRelationByTaxonomyIdOrProjectId(projectID);
        //添加至wtss的project数据库，获取projectID
        project.setUserName(userName);
        if(existSchesulis()){
            createSchedulerProject(project);
        }
        Map<Long,Long> appjointProjectIDAndAppID = createAppjointProject(project);
        Long userID = dssUserMapper.getUserID(userName);
        //添加至DSS的project数据库，这里的projectID应该不需要自增
        //目前是相同数据库，需要自增id
        project.setUserID(userID);
        project.setCreateTime(new Date());
        project.setId(null);
        projectMapper.addProject(project);
        if(!appjointProjectIDAndAppID.isEmpty())projectMapper.addAccessProjectRelation(appjointProjectIDAndAppID,project.getId());
        projectTaxonomyMapper.addProjectTaxonomyRelation(project.getId(), projectTaxonomyRelation.getTaxonomyId(), userID);
        DSSProjectVersion maxVersion = projectMapper.selectLatestVersionByProjectID(projectID);
        copyProjectVersionMax(maxVersion.getId(), maxVersion, maxVersion, userName, project.getId());
        return project.getId();
    }

    private boolean existSchesulis(){
        return applicationService.getApplication("schedulis") != null;
    }


    /**
     * 复制工程的某个历史版本到最新版本,同时复制工作流
     *
     * @param projectVersionID
     * @param copyVersion      复制的工程版本
     * @param WTSSprojectID    如果是工程复制,则使用此参数. 指定复制工作流的初始版本号.
     *                         如果是工程版本复制,则设置为null即可.
     */
    @Lock(type = LockEnum.ADD)
    @Transactional(rollbackFor = {DSSErrorException.class, InterruptedException.class})
    @Override
    public void copyProjectVersionMax(Long projectVersionID, DSSProjectVersion maxVersion, DSSProjectVersion copyVersion, String userName, Long WTSSprojectID) throws DSSErrorException, InterruptedException {
//            copy project_version
        String maxVersionNum = generateNewVersion(maxVersion.getVersion());
        if (null != WTSSprojectID) {
            copyVersion.setVersion(DSSServerConstant.DSS_PROJECT_FIRST_VERSION);
            copyVersion.setProjectID(WTSSprojectID);
        } else {
            copyVersion.setVersion(maxVersionNum);
        }
        Long userID = dssUserMapper.getUserID(userName);
        copyVersion.setUpdatorID(userID);
        copyVersion.setUpdateTime(new Date());
        List<DSSFlowVersion> flowVersions = flowMapper.listLastFlowVersionsByProjectVersionID(copyVersion.getId())
                .stream().sorted((o1, o2) -> Integer.valueOf(o1.getFlowID().toString()) - Integer.valueOf(o2.getFlowID().toString()))
                .collect(Collectors.toList());
        Long oldProjectVersionID = copyVersion.getId();
        copyVersion.setId(null);
        projectMapper.addProjectVersion(copyVersion);
        if (copyVersion.getId() == null) {throw new DSSErrorException(90015, "复制工程版本失败");}
        Map<Long, Long> subAndParentFlowIDMap = new ConcurrentHashMap<>();
        // copy flow
        if (null != WTSSprojectID) {
            flowVersions.stream().forEach(f -> {
                DSSFlow flow = flowMapper.selectFlowByID(f.getFlowID());
                Long parentFlowID = flowMapper.selectParentFlowIDByFlowID(flow.getId());
                if (parentFlowID != null) {subAndParentFlowIDMap.put(flow.getId(), parentFlowID);}
            });
            for (DSSFlowVersion fv : flowVersions) {
                // 添加所有父子到map中
                DSSFlow flow = flowMapper.selectFlowByID(fv.getFlowID());
                flow.setCreatorID(userID);
                flow.setName(flow.getName());
                flow.setProjectID(copyVersion.getProjectID());
                flow.setCreateTime(new Date());

                Long taxonomyID = flowTaxonomyMapper.selectTaxonomyIDByFlowID(flow.getId());
                DSSFlowTaxonomy flowTaxonomy = flowTaxonomyMapper.selectFlowTaxonomyByID(taxonomyID);
                //新增flow相关数据
                fv.setOldFlowID(flow.getId());
                flow.setId(null);
                flowMapper.insertFlow(flow);
                if (null == flow.getId()) {throw new DSSErrorException(90016, "复制工作流失败");}
                for (Map.Entry<Long, Long> entry : subAndParentFlowIDMap.entrySet()) {
                    if (entry.getValue().equals(fv.getFlowID())){subAndParentFlowIDMap.put(entry.getKey(), flow.getId());}
                    if (entry.getKey().equals(fv.getFlowID())){subAndParentFlowIDMap.put(flow.getId(), entry.getValue());}
                }
                if (flowTaxonomy.getProjectID() != -1 && (!flowTaxonomy.getProjectID().equals(copyVersion.getProjectID()))) {
                    flowTaxonomy.setProjectID(copyVersion.getProjectID());
                    flowTaxonomy.setCreateTime(new Date());
                    flowTaxonomy.setUpdateTime(new Date());
                    flowTaxonomy.setProjectID(copyVersion.getUpdatorID());
                    flowTaxonomyMapper.insertFlowTaxonomy(flowTaxonomy);
                }
                if (null != taxonomyID){flowTaxonomyMapper.insertFlowTaxonomyRelation(flowTaxonomy.getId(), flow.getId());}
                fv.setFlowID(flow.getId());
            }
            for (DSSFlowVersion fv : flowVersions) {
                if (subAndParentFlowIDMap.get(fv.getFlowID()) != null){flowMapper.insertFlowRelation(fv.getFlowID(), subAndParentFlowIDMap.get(fv.getFlowID()));}
            }
        }
//        copy flow_version
        if (flowVersions.size() > 0) {
            ThreadPoolTool<DSSFlowVersion> tool = new ThreadPoolTool(5, flowVersions);
            tool.setCallBack(new ThreadPoolTool.CallBack<DSSFlowVersion>() {
                @Override
                public void method(List<DSSFlowVersion> flowVersions) {
                    for (DSSFlowVersion fv : flowVersions) {
                        //            工作流版本的json文件，都是需要重新上传到bml
                        Map<String, Object> bmlQueryMap = bmlService.download(dssUserMapper.getuserName(fv.getUpdatorID()), fv.getJsonPath(), fv.getVersion());
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader((InputStream) bmlQueryMap.get("is")));
                        StringBuilder sb = new StringBuilder();
                        String s = null;
                        try {
                            while ((s = bufferedReader.readLine()) != null) {
                                sb.append(s);
                                sb.append("\n");
                            }
                            String json = sb.toString().trim();
                            List<Long> newflowIDs = getSubNewFlowID(subAndParentFlowIDMap, fv.getFlowID());
                            List<Long> oldFlowIDs = getOldSubFlowID(subAndParentFlowIDMap, fv.getFlowID());
                            if (oldFlowIDs != null && newflowIDs != null) {
                                logger.info("replace之前:" + json);
                                if (json != null) {
                                    for (int i = 0; i < newflowIDs.size(); i++) {
                                        json = json.replace(DSSServerConstant.EMVEDDEDFLOWID + oldFlowIDs.get(i), DSSServerConstant.EMVEDDEDFLOWID + newflowIDs.get(i));
                                    }
                                }
                                logger.info("replace之后:" + json);
                            }
                            if (json != null) {
                                InputStream in_nocode = new ByteArrayInputStream(json.getBytes());
                                Map<String, Object> bmlReturnMap = bmlService.upload(userName, in_nocode, UUID.randomUUID().toString() + ".json");
                                fv.setProjectVersionID(copyVersion.getId());
                                fv.setUpdateTime(new Date());
                                fv.setUpdatorID(copyVersion.getUpdatorID());
                                String oldFlowVersion = fv.getVersion();
                                fv.setJsonPath(bmlReturnMap.get("resourceId").toString());
                                fv.setVersion(bmlReturnMap.get("version").toString());
                                fv.setSource("Copy from Old { ProjectVersionID : " + oldProjectVersionID + " ,FlowID:" + fv.getOldFlowID() + ",Version:" + oldFlowVersion + "} to New { ProjectVersionID:" + fv.getProjectVersionID() + " ,FlowID:" + fv.getFlowID() + ",Version:" + fv.getVersion() + "}");
                                bufferedReader.close();
                                in_nocode.close();
                            }
                        } catch (IOException e) {
                            logger.error("工作流复制IO异常", e);
                        }

                    }
                }
            });
            tool.excute();
        }
        if (flowVersions != null && flowVersions.size() > 0) {
            flowVersions.stream().forEach(f -> {
                logger.info("jsonPaht:{},oldeFlowID:{},newFlowID", f.getJsonPath(), f.getOldFlowID(), f.getFlowID());
            });
            flowMapper.batchInsertFlowVersion(flowVersions);
        }
    }

    private String generateNewVersion(String version){
        int next = Integer.parseInt(version.substring(1, version.length())) + 1;
        return DSSServerConstant.VERSION_PREFIX + String.format(DSSServerConstant.VERSION_FORMAT, next);
    }


    private List<Long> getOldSubFlowID(Map<Long, Long> subAndParentFlowIDMap, Long flowID) {
        List<Map.Entry<Long, Long>> collect = subAndParentFlowIDMap.entrySet().stream().filter(f -> f.getValue().equals(flowID)).collect(Collectors.toList());
        if (collect.isEmpty()) return null;
        int size = collect.size() / 2;
        ArrayList<Long> longs = new ArrayList<>();
        List<Map.Entry<Long, Long>> sortedCollections = collect.stream().sorted((o, n) -> {
            return Integer.valueOf(o.getKey().toString()) - Integer.valueOf(n.getKey().toString());
        }).collect(Collectors.toList());
        sortedCollections.subList(0, size).stream().forEach(p -> longs.add(p.getKey()));
        return longs;
        /*if(collect.isEmpty()){
            return  UUID.randomUUID().toString();
        }else {
            return  collect.get(0).getKey() - collect.get(1).getKey() <0? Constant.EMVEDDEDFLOWID +collect.get(0).getKey():Constant.EMVEDDEDFLOWID +collect.get(1).getKey();
        }*/
    }

    private List<Long> getSubNewFlowID(Map<Long, Long> subAndParentFlowIDMap, Long flowID) {
        List<Map.Entry<Long, Long>> collect = subAndParentFlowIDMap.entrySet().stream().filter(f -> f.getValue().equals(flowID)).collect(Collectors.toList());
        if (collect.isEmpty()) return null;
        int size = collect.size() / 2;
        ArrayList<Long> longs = new ArrayList<>();
        List<Map.Entry<Long, Long>> sortedCollections = collect.stream().sorted((o, n) -> {
            return Integer.valueOf(n.getKey().toString()) - Integer.valueOf(o.getKey().toString());
        }).collect(Collectors.toList());
        sortedCollections.subList(0, size).stream().forEach(p -> longs.add(p.getKey()));
        return longs.stream().sorted((o1, o2) -> {
            return Integer.valueOf(o1.toString()) - Integer.valueOf(o2.toString());
        }).collect(Collectors.toList());
    }


    private SchedulerAppJoint getSchedulerAppJoint(){
        if(schedulerAppJoint == null){
            try {
                schedulerAppJoint = (SchedulerAppJoint)applicationService.getAppjoint("schedulis");
            } catch (AppJointErrorException e) {
                logger.error("Schedule system init failed!", e);
            }
        }
        return schedulerAppJoint;
    }

}
