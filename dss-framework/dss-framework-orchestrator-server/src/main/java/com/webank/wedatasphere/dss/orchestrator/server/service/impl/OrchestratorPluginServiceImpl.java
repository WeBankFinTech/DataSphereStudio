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

package com.webank.wedatasphere.dss.orchestrator.server.service.impl;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.webank.wedatasphere.dss.common.constant.project.ProjectUserPrivEnum;
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.DSSLabelUtil;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.protocol.*;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectInfoRequest;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectUserAuthRequest;
import com.webank.wedatasphere.dss.common.protocol.project.ProjectUserAuthResponse;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.common.utils.*;
import com.webank.wedatasphere.dss.git.common.protocol.GitTree;
import com.webank.wedatasphere.dss.git.common.protocol.request.*;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitCommitResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitDiffResponse;
import com.webank.wedatasphere.dss.git.common.protocol.response.GitFileContentResponse;
import com.webank.wedatasphere.dss.orchestrator.common.entity.*;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestFrameworkConvertOrchestration;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseConvertOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestratorContext;
import com.webank.wedatasphere.dss.orchestrator.core.plugin.DSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorJobMapper;
import com.webank.wedatasphere.dss.orchestrator.db.dao.OrchestratorMapper;
import com.webank.wedatasphere.dss.orchestrator.publish.ConversionDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.publish.ExportDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.publish.conf.DSSOrchestratorConf;
import com.webank.wedatasphere.dss.orchestrator.publish.job.CommonUpdateConvertJobStatus;
import com.webank.wedatasphere.dss.orchestrator.publish.job.ConversionJobEntity;
import com.webank.wedatasphere.dss.orchestrator.publish.job.OrchestratorConversionJob;
import com.webank.wedatasphere.dss.orchestrator.server.entity.request.OrchestratorSubmitRequest;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorDiffDirVo;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorRelationVo;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorPluginService;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.dao.FlowMapper;
import com.webank.wedatasphere.dss.workflow.dao.LockMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.linkis.common.utils.ByteTimeUtils;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.manager.label.builder.factory.LabelBuilderFactoryContext;
import org.apache.linkis.rpc.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;


public class OrchestratorPluginServiceImpl implements OrchestratorPluginService {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrchestratorMapper orchestratorMapper;

    @Autowired
    private OrchestratorJobMapper orchestratorJobMapper;

    @Autowired
    private DSSOrchestratorContext dssOrchestratorContext;

    @Autowired
    private CommonUpdateConvertJobStatus commonUpdateConvertJobStatus;

    @Autowired
    private LockMapper lockMapper;

    @Autowired
    private FlowMapper flowMapper;

    @Autowired
    private OrchestratorService orchestratorService;

    @Autowired
    private OrchestratorPluginService orchestratorPluginService;

    @Autowired
    @Qualifier("workflowBmlService")
    private BMLService bmlService;

    private ExecutorService releaseThreadPool = Utils.newCachedThreadPool(50, "Convert-Orchestration-Thread-", true);

    @Override
    public ResponseConvertOrchestrator convertOrchestration(RequestFrameworkConvertOrchestration requestConversionOrchestration) {
        // 如果是先导入，后发布的分两步模式，则走专门的逻辑
        if(DSSOrchestratorConf.DSS_IMPORT_VALID_IMMEDIATELY.getValue() &&
                requestConversionOrchestration.isConvertAllOrcs() &&
                requestConversionOrchestration.getProjectId()!=null){
            ResponseOperateOrchestrator responseOperateOrchestrator = convertWholeProjectOrcs(requestConversionOrchestration);
            return new ResponseConvertOrchestrator("-1", responseOperateOrchestrator);
        }
        LOGGER.info("conversion request is called, request is {}.", DSSCommonUtils.COMMON_GSON.toJson(requestConversionOrchestration));
        Long toPublishOrcId;
        if(requestConversionOrchestration.getOrcAppId() != null) {
            OrchestratorInfo orchestratorInfo = orchestratorMapper.getOrcInfoByAppId(requestConversionOrchestration.getOrcAppId());
            toPublishOrcId = orchestratorInfo.getOrchestratorId();
        } else if (CollectionUtils.isNotEmpty(requestConversionOrchestration.getOrcIds())) {
            toPublishOrcId = requestConversionOrchestration.getOrcIds().get(0);
        } else {
            return new ResponseConvertOrchestrator("-1", ResponseOperateOrchestrator.failed("Both orcAppId and orcIds are not exists."));
        }
        DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(toPublishOrcId);
        long projectId = dssOrchestratorInfo.getProjectId();
        //这里的 key 为 DSS 具体编排（如 DSS 工作流）的 id；这里的 value 为 DSS 编排所对应的第三方调度系统的工作流 ID
        //请注意：由于对接的 SchedulerAppConn 调度系统有可能没有实现 OrchestrationService，
        //所以可能存在 DSS 在创建 DSS 编排时，无法同步去 SchedulerAppConn 创建工作流的情况，从而导致这个 Map 的所有 value 都为 null。
        Map<Long, Long> orchestrationIdMap = new HashMap<>();
        List<Long> publishedOrcIds;
        List<DSSLabel> labels = LabelBuilderFactoryContext.getLabelBuilderFactory().getLabels(requestConversionOrchestration.getLabels());
        if(requestConversionOrchestration.isConvertAllOrcs()) {
            //获取所有的已经发布过的orchestrator
            publishedOrcIds = orchestratorMapper.getAllOrcIdsByProjectId(projectId,1);
            if (!publishedOrcIds.contains(toPublishOrcId)) {
                publishedOrcIds.add(toPublishOrcId);
            }
            for (Long orcId : publishedOrcIds) {
                int validFlag = 1;
                if (orcId.longValue() == toPublishOrcId.longValue() && !DSSLabelUtil.isDevEnv(labels)) {
                    validFlag = 0;
                }
                DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orcId, validFlag);
                if (dssOrchestratorVersion == null) {
                    continue;
                }
                DSSOrchestratorRefOrchestration dssOrchestratorRefOrchestration = orchestratorMapper.getRefOrchestrationId(orcId);
                if(dssOrchestratorRefOrchestration != null) {
                    orchestrationIdMap.put(dssOrchestratorVersion.getAppId(), dssOrchestratorRefOrchestration.getRefOrchestrationId());
                } else {
                    orchestrationIdMap.put(dssOrchestratorVersion.getAppId(), null);
                }
            }
        } else {
            publishedOrcIds = new ArrayList<>();
            if(requestConversionOrchestration.getOrcAppId() != null) {
                publishedOrcIds.add(toPublishOrcId);
                DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(toPublishOrcId,1);
                if (dssOrchestratorVersion != null) {
                    DSSOrchestratorRefOrchestration dssOrchestratorRefOrchestration = orchestratorMapper.getRefOrchestrationId(toPublishOrcId);
                    if(dssOrchestratorRefOrchestration != null) {
                        orchestrationIdMap.put(dssOrchestratorVersion.getAppId(), dssOrchestratorRefOrchestration.getRefOrchestrationId());
                    } else {
                        orchestrationIdMap.put(dssOrchestratorVersion.getAppId(), null);
                    }
                }
            }
            if(requestConversionOrchestration.getOrcIds() != null && !requestConversionOrchestration.getOrcIds().isEmpty()) {
                for (Long orcId : requestConversionOrchestration.getOrcIds()) {
                    DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orcId,1);
                    if (dssOrchestratorVersion == null) {
                        continue;
                    }
                    publishedOrcIds.add(orcId);
                    DSSOrchestratorRefOrchestration dssOrchestratorRefOrchestration = orchestratorMapper.getRefOrchestrationId(orcId);
                    if(dssOrchestratorRefOrchestration != null) {
                        orchestrationIdMap.put(dssOrchestratorVersion.getAppId(), dssOrchestratorRefOrchestration.getRefOrchestrationId());
                    } else {
                        orchestrationIdMap.put(dssOrchestratorVersion.getAppId(), null);
                    }
                }
            }
        }
        if(orchestrationIdMap.isEmpty()) {
            LOGGER.info("the project {} has no suitable workflow, the publish by user {} is ignored.", projectId,
                    requestConversionOrchestration.getUserName());
            return new ResponseConvertOrchestrator("no-necessary-id", ResponseOperateOrchestrator.failed("No suitable workflow(s) found, publish is ignored."));
        }
        OrchestratorConversionJob job = new OrchestratorConversionJob();
        job.setId(String.valueOf(UUID.randomUUID()));
        LOGGER.info("user {} try to submit a conversion job {}, the orchestrationIdMap is {}, the orcIdList is {}.", requestConversionOrchestration.getUserName(),
                job.getId(), orchestrationIdMap, publishedOrcIds);

        ConversionJobEntity entity = new ConversionJobEntity();
        entity.setResponse(ResponseOperateOrchestrator.inited());
        entity.setCreateTime(new Date());
        entity.setUserName(requestConversionOrchestration.getUserName());
        entity.setOrchestrationIdMap(orchestrationIdMap);
        entity.setOrcIdList(publishedOrcIds);
        entity.setLabels(labels);
        entity.setWorkspace((Workspace) requestConversionOrchestration.getWorkspace());
        DSSProject dssProject = new DSSProject();
        dssProject.setId(projectId);
        entity.setProject(dssProject);
        job.setConversionJobEntity(entity);
        job.setCommonUpdateConvertJobStatus(commonUpdateConvertJobStatus);
        job.setConversionDSSOrchestratorPlugins(dssOrchestratorContext.getOrchestratorPlugins());
        job.afterConversion(response -> this.updateDBAfterConversion(toPublishOrcId, response, job.getConversionJobEntity(), requestConversionOrchestration));

        OrchestratorPublishJob orchestratorPublishJob = new OrchestratorPublishJob();
        orchestratorPublishJob.setJobId(job.getId());
        orchestratorPublishJob.setStatus(JobStatus.Inited.getStatus());
        orchestratorPublishJob.setInstanceName(Sender.getThisInstance());
        orchestratorPublishJob.setCreateTime(new Date(System.currentTimeMillis()));
        orchestratorPublishJob.setUpdateTime(new Date(System.currentTimeMillis()));
        if (DSSCommonUtils.COMMON_GSON.toJson(entity).length() > 1024) {
            orchestratorPublishJob.setConversionJobJson(DSSCommonUtils.COMMON_GSON.toJson(entity).substring(0, 1024));
        } else {
            orchestratorPublishJob.setConversionJobJson(DSSCommonUtils.COMMON_GSON.toJson(entity));
        }
        job.setOrchestratorPublishJob(orchestratorPublishJob);
        orchestratorJobMapper.insertPublishJob(orchestratorPublishJob);
        //submit it
        releaseThreadPool.submit(job);

        LOGGER.info("publish orchestrator success. publishedOrcIds:{} ",publishedOrcIds);
        return new ResponseConvertOrchestrator(job.getId(), entity.getResponse());
    }

    @Override
    public void submitFlow(OrchestratorSubmitRequest flowRequest, String username, Workspace workspace) throws DSSErrorException {
        Long orchestratorId = flowRequest.getOrchestratorId();
        String status = lockMapper.selectOrchestratorStatus(orchestratorId);
        if (!StringUtils.isEmpty(status) && !status.equals(OrchestratorRefConstant.FLOW_STATUS_SAVE)) {
            throw new DSSErrorException(800001, "工作流无改动或改动未提交，请确认改动并保存再进行提交");
        }
        OrchestratorSubmitJob submitJob = new OrchestratorSubmitJob();
        submitJob.setOrchestratorId(orchestratorId);
        submitJob.setInstanceName(Sender.getThisInstance());
        submitJob.setStatus(OrchestratorRefConstant.FLOW_STATUS_PUSHING);
        orchestratorMapper.insertOrchestratorSubmitJob(submitJob);
        Long taskId = submitJob.getId();
        releaseThreadPool.submit(() ->{
            //1. 异步提交，更新提交状态
            try {
                submitWorkflowToBML(taskId, flowRequest, username, workspace);
            }  catch (Exception e) {
            LOGGER.error("push failed, the reason is : ", e);
            orchestratorMapper.updateOrchestratorSubmitJobStatus(taskId, OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED, e.toString());
        }
        });
    }

    @Override
    public void batchSubmitFlow(Map<String, List<OrchestratorRelationVo>> map, Map<String, Long> projectMap, String username, Workspace workspace, String label, String comment) throws DSSErrorException {
        if (map == null) {
            throw new DSSErrorException(80001, "批量提交的工作流不能为空");
        }

        for (Map.Entry<String, List<OrchestratorRelationVo>> entry : map.entrySet()) {
            List<OrchestratorRelationVo> orchestratorRelationVos = entry.getValue();
            String projectName = entry.getKey();
            Long projectId = projectMap.get(projectName);

            List<Long> orchestratorIdList = new ArrayList<>();

            ProjectUserAuthResponse projectUserAuthResponse = RpcAskUtils.processAskException(DSSSenderServiceFactory.getOrCreateServiceInstance()
                            .getProjectServerSender().ask(new ProjectUserAuthRequest(projectId, username)),
                    ProjectUserAuthResponse.class, ProjectUserAuthRequest.class);
            boolean isEditable = false;
            if (!CollectionUtils.isEmpty(projectUserAuthResponse.getPrivList())) {
                isEditable = projectUserAuthResponse.getPrivList().contains(ProjectUserPrivEnum.PRIV_EDIT.getRank());
            }
            isEditable = isEditable || projectUserAuthResponse.getProjectOwner().equals(username);

            if (!isEditable) {
                DSSExceptionUtils.dealErrorException(63335, "用户" + username + "没有项目" + projectName + "编辑权限，请检查后重新提交", DSSErrorException.class);
            }

            for (OrchestratorRelationVo relationVo : orchestratorRelationVos) {
                Long orchestratorId = relationVo.getOrchestratorId();
                orchestratorIdList.add(orchestratorId);
                String status = lockMapper.selectOrchestratorStatus(orchestratorId);
                if (StringUtils.isEmpty(status)) {
                    DSSOrchestratorInfo orchestrator = orchestratorMapper.getOrchestrator(orchestratorId);
                    throw new DSSErrorException(800001, projectName + "项目未接入git，批量提交失败");
                }
                if (!StringUtils.isEmpty(status) && !status.equals(OrchestratorRefConstant.FLOW_STATUS_SAVE)) {
                    DSSOrchestratorInfo orchestrator = orchestratorMapper.getOrchestrator(orchestratorId);
                    throw new DSSErrorException(800001, orchestrator.getName() + "工作流无改动或改动未提交，请确认改动并保存再进行提交");
                }
            }
            List<Long> taskIdList = new ArrayList<>();
            for (OrchestratorRelationVo relationVo : orchestratorRelationVos) {
                Long orchestratorId = relationVo.getOrchestratorId();

                OrchestratorSubmitJob submitJob = new OrchestratorSubmitJob();
                submitJob.setOrchestratorId(orchestratorId);
                submitJob.setInstanceName(Sender.getThisInstance());
                submitJob.setStatus(OrchestratorRefConstant.FLOW_STATUS_PUSHING);
                orchestratorMapper.insertOrchestratorSubmitJob(submitJob);
                Long taskId = submitJob.getId();
                taskIdList.add(taskId);

            }
            releaseThreadPool.submit(() ->{
                //1. 异步提交，更新提交状态
                try {
                    batchSubmitWorkflowToBML(taskIdList, orchestratorRelationVos, username, workspace, projectName, label, projectId, comment);
                }  catch (Exception e) {
                    LOGGER.error("push failed, the reason is : ", e);
                    orchestratorMapper.batchUpdateOrchestratorSubmitJobStatus(taskIdList, OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED, "batchSubmit error: " + e.toString());
                }
            });
        }
    }

    @Override
    public GitCommitResponse submitWorkflowSync(OrchestratorSubmitRequest flowRequest, String username, Workspace workspace) throws DSSErrorException {
        Long orchestratorId = flowRequest.getOrchestratorId();
        OrchestratorSubmitJob submitJob = new OrchestratorSubmitJob();
        submitJob.setOrchestratorId(orchestratorId);
        submitJob.setInstanceName(Sender.getThisInstance());
        submitJob.setStatus(OrchestratorRefConstant.FLOW_STATUS_PUSHING);
        orchestratorMapper.insertOrchestratorSubmitJob(submitJob);
        Long taskId = submitJob.getId();
        try {
            GitCommitResponse gitCommitResponse = submitWorkflowToBML(taskId, flowRequest, username, workspace);
            return gitCommitResponse;
        }  catch (Exception e) {
            LOGGER.error("push failed, the reason is : ", e);
            orchestratorMapper.updateOrchestratorSubmitJobStatus(taskId, OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED, e.toString());
            throw new DSSErrorException(80001, "push failed, the reason is : " + e.toString());
        }
    }


    private GitCommitResponse submitWorkflowToBML(Long taskId, OrchestratorSubmitRequest flowRequest, String username, Workspace workspace) {
        Long orchestratorId = flowRequest.getOrchestratorId();
        Long flowId = flowRequest.getFlowId();
        String projectName = flowRequest.getProjectName();
        String label = flowRequest.getLabels().getRoute();
        //2. 获取编排信息
        DSSOrchestratorInfo orchestrator = orchestratorMapper.getOrchestrator(orchestratorId);
        //3. 获取上传工作流信息
        BmlResource bmlResource = orchestratorMapper.getOrchestratorBmlVersion(orchestratorId);
        if (bmlResource == null) {
            bmlResource = uploadWorkflowToGit(flowId, projectName, label, username, workspace, orchestrator);
            orchestratorMapper.updateOrchestratorBmlVersion(orchestratorId, bmlResource.getResourceId(), bmlResource.getVersion());
        }
        //4. 调用git服务上传
        GitCommitResponse commit = push(orchestrator.getName(), bmlResource, username, workspace.getWorkspaceId(), projectName, flowRequest.getComment());
        if (commit == null) {
            LOGGER.info("change is empty");
        }
        orchestratorMapper.updateOrchestratorSubmitJobStatus(taskId, OrchestratorRefConstant.FLOW_STATUS_PUSH_SUCCESS, "");
        //5. 返回文件列表
        lockMapper.updateOrchestratorStatus(orchestratorId, OrchestratorRefConstant.FLOW_STATUS_PUSH);
        // 更新commitId
        lockMapper.updateOrchestratorVersionCommitId(commit.getCommitId(), flowId);
        return commit;
    }

    public GitCommitResponse batchSubmitWorkflowToBML(List<Long> taskIdList, List<OrchestratorRelationVo> relationVos, String username, Workspace workspace, String projectName, String label, Long projectId, String comment) throws Exception {

        List<Long> flowIdList = relationVos.stream().map(OrchestratorRelationVo::getFlowId).collect(Collectors.toList());
        BmlResource bmlResource = orchestratorPluginService.uploadWorkflowListToGit(flowIdList, projectName, label, username, workspace, projectId);
        List<Long> orchestratorIdList = relationVos.stream().map(OrchestratorRelationVo::getOrchestratorId).collect(Collectors.toList());
        List<String> orchestratorName = orchestratorMapper.getOrchestratorName(orchestratorIdList);
        //4. 调用git服务上传
        GitCommitResponse commit = batchPush(orchestratorName, bmlResource, username, workspace.getWorkspaceId(), projectName, comment);
        if (commit == null) {
            LOGGER.info("change is empty");
        }
        orchestratorMapper.batchUpdateOrchestratorSubmitJobStatus(taskIdList, OrchestratorRefConstant.FLOW_STATUS_PUSH_SUCCESS, "");
        //5. 返回文件列表
        lockMapper.batchUpdateOrchestratorStatus(orchestratorIdList, OrchestratorRefConstant.FLOW_STATUS_PUSH);
        // 更新commitId
        lockMapper.batchUpdateOrchestratorVersionCommitId(commit.getCommitId(), flowIdList);
        return commit;
    }

    private GitCommitResponse push (String path, BmlResource bmlResource, String username, Long workspaceId, String projectName, String comment) {
        LOGGER.info("-------=======================begin to add testGit1=======================-------");
        Sender gitSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        Map<String, BmlResource> file = new HashMap<>();
        file.put(path, bmlResource);
        GitCommitRequest request1 = new GitCommitRequest(workspaceId, projectName, file, comment, username);
        GitCommitResponse responseWorkflowValidNode = RpcAskUtils.processAskException(gitSender.ask(request1), GitCommitResponse.class, GitCommitRequest.class);
        LOGGER.info("-------=======================End to add testGit1=======================-------: {}", responseWorkflowValidNode);
        return responseWorkflowValidNode;
    }

    private GitCommitResponse batchPush (List<String> path, BmlResource bmlResource, String username, Long workspaceId, String projectName, String comment) {
        LOGGER.info("-------=======================begin to add testGit1=======================-------");
        Sender gitSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        GitBatchCommitRequest request1 = new GitBatchCommitRequest(workspaceId, projectName, comment, username, path, bmlResource);
        GitCommitResponse responseWorkflowValidNode = RpcAskUtils.processAskException(gitSender.ask(request1), GitCommitResponse.class, GitCommitRequest.class);
        LOGGER.info("-------=======================End to add testGit1=======================-------: {}", responseWorkflowValidNode);
        return responseWorkflowValidNode;
    }

    private BmlResource uploadWorkflowToGit(Long flowId, String projectName, String label, String username, Workspace workspace, DSSOrchestratorInfo orchestrator) {
       // 1. 将序列化好的工作流文件包提交给git服务，并拿到diff文件列表结果,

        Long projectId = orchestrator.getProjectId();
        List<DSSLabel> dssLabelList = new ArrayList<>();
        dssLabelList.add(new EnvDSSLabel(label));
        RequestExportWorkflow requestExportWorkflow = new RequestExportWorkflow(username,
                flowId,
                projectId,
                projectName,
                DSSCommonUtils.COMMON_GSON.toJson(workspace),
                dssLabelList,
                false);
        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getWorkflowSender(dssLabelList);
        ResponseExportWorkflow responseExportWorkflow = RpcAskUtils.processAskException(sender.ask(requestExportWorkflow),
                ResponseExportWorkflow.class, RequestExportWorkflow.class);

        BmlResource bmlResource = new BmlResource(responseExportWorkflow.resourceId(), responseExportWorkflow.version());

        return bmlResource;
    }

    @Override
    public String diffStatus(Long taskId) throws DSSErrorException {
        OrchestratorSubmitJob orchestratorSubmitJob = orchestratorMapper.selectSubmitJobStatusById(taskId);
        if (orchestratorSubmitJob != null) {
            String status = orchestratorSubmitJob.getStatus();
            if (OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED.equals(status)) {
                throw new DSSErrorException(80001, "获取工作流diff内容失败，原因为：" + orchestratorSubmitJob.getErrorMsg());
            }
            return status;
        }
        return null;
    }

    @Override
    public OrchestratorDiffDirVo diffContent(Long taskId) {
        String result = orchestratorMapper.selectResult(taskId);
        if (StringUtils.isNotEmpty(result)) {
            Type listType = new TypeToken<OrchestratorDiffDirVo>() {}.getType();
            Gson gson = new Gson();
            OrchestratorDiffDirVo gitTrees = gson.fromJson(result, listType);
            return gitTrees;
        }
        return null;
    }

    @Override
    public BmlResource uploadWorkflowListToGit(List<Long> flowIdList, String projectName, String label, String username, Workspace workspace, Long projectId) throws Exception{
        // 1. 将序列化好的工作流文件包提交给git服务，并拿到diff文件列表结果,
        List<DSSLabel> dssLabelList = new ArrayList<>();
        dssLabelList.add(new EnvDSSLabel(label));
        RequestExportWorkflowList requestExportWorkflow = new RequestExportWorkflowList(username,
                flowIdList,
                projectId,
                projectName,
                DSSCommonUtils.COMMON_GSON.toJson(workspace),
                dssLabelList,
                false);
        Sender sender = DSSSenderServiceFactory.getOrCreateServiceInstance().getWorkflowSender(dssLabelList);
        ResponseExportWorkflowList responseExportWorkflow = RpcAskUtils.processAskException(sender.ask(requestExportWorkflow),
                ResponseExportWorkflowList.class, RequestExportWorkflowList.class);
        Map<String, Object> resourceMap = new HashMap<>(2);
        BmlResource bmlResource = new BmlResource(responseExportWorkflow.resourceId(), responseExportWorkflow.version());

        return bmlResource;
    }

    private String readWorkflowNode(BmlResource bmlResource, String username, String projectName, String orchestratorName, String filePath) throws DSSErrorException {
        // 1. 将序列化好的工作流文件包提交给git服务，并拿到diff文件列表结果,
        String projectPath = IoUtils.generateProjectIOPath(username, projectName);
        String fullPath = projectPath + File.separator + projectName + File.separator + filePath;
        String zipFilePath = projectPath + File.separator + orchestratorName + ".zip";
        String fileContent = null;
        try {
            bmlService.downloadToLocalPath(username, bmlResource.getResourceId(), bmlResource.getVersion(), zipFilePath);
            ZipHelper.unzipFile(zipFilePath, projectPath, true);

            LOGGER.info("export workflow success.  orchestratorName:{},fullPath:{} .", filePath, projectPath);

            File file = new File(fullPath);
            if (file.exists()) {
                fileContent = new String(Files.readAllBytes(Paths.get(fullPath)));
            }
        } catch (Exception e) {
            LOGGER.error("exportFlowInfoNew failed , the reason is:", e);
            throw new DSSErrorException(100098, "工作流导出失败，原因为" + e.getMessage());
        } finally {
            //删掉整个目录
            if (StringUtils.isNotEmpty(projectPath)) {
                File file = new File(projectPath);
                if (ZipHelper.deleteDir(file)){
                    LOGGER.info("结束删除目录 {} 成功", projectPath);
                }else{
                    LOGGER.info("删除目录 {} 失败", projectPath);
                }
            }
        }
        return fileContent;
    }

    @Override
    public Long diffFlow(OrchestratorSubmitRequest flowRequest, String username, Workspace workspace) {
        Long orchestratorId = flowRequest.getOrchestratorId();
        DSSOrchestratorInfo orchestrator = orchestratorMapper.getOrchestrator(orchestratorId);
        Long flowId = flowRequest.getFlowId();
        String projectName = flowRequest.getProjectName();
        String label = flowRequest.getLabels().getRoute();

        OrchestratorSubmitJob diffJob = new OrchestratorSubmitJob();
        diffJob.setOrchestratorId(orchestratorId);
        diffJob.setInstanceName(Sender.getThisInstance());
        diffJob.setStatus(OrchestratorRefConstant.FLOW_STATUS_PUSHING);
        orchestratorMapper.insertOrchestratorSubmitJob(diffJob);
        Long taskId = diffJob.getId();
        releaseThreadPool.submit(() -> {
            try {
                BmlResource bmlResource = orchestratorMapper.getOrchestratorBmlVersion(orchestratorId);
                if (bmlResource == null) {
                    bmlResource = uploadWorkflowToGit(flowId, projectName, label, username, workspace, orchestrator);
                    orchestratorMapper.updateOrchestratorBmlVersion(orchestratorId, bmlResource.getResourceId(), bmlResource.getVersion());
                }

                GitDiffResponse diff = diff(orchestrator.getName(), bmlResource, username, workspace.getWorkspaceId(), flowRequest.getProjectName());
                String result = "";
                if (diff != null) {
                    List<GitTree> codeTree = diff.getCodeTree();
                    List<GitTree> metaTree = diff.getMetaTree();
                    OrchestratorDiffDirVo diffDirVo = new OrchestratorDiffDirVo(codeTree, metaTree);
                    Gson gson = new Gson();
                    result = gson.toJson(diffDirVo);
                } else {
                    LOGGER.info("change is empty");
                }
                orchestratorMapper.updateOrchestratorSubmitResult(taskId, OrchestratorRefConstant.FLOW_STATUS_PUSH_SUCCESS, result);
            }catch (Exception e) {
                LOGGER.error("push failed, the reason is : ", e);
                orchestratorMapper.updateOrchestratorSubmitJobStatus(taskId, OrchestratorRefConstant.FLOW_STATUS_PUSH_FAILED, e.toString());
            }
        });

        return taskId;
    }

    @Override
    public OrchestratorDiffDirVo diffPublish(OrchestratorSubmitRequest flowRequest, String username, Workspace workspace) {
        Long orchestratorId = flowRequest.getOrchestratorId();
        DSSOrchestratorInfo orchestrator = orchestratorMapper.getOrchestrator(orchestratorId);
        String commitId = getLastPublishCommitId(orchestrator);


        GitDiffResponse diff = diffPublish(orchestrator.getName(), commitId, username, workspace.getWorkspaceId(), flowRequest.getProjectName());
        if (diff == null) {
            LOGGER.info("change is empty");
            return null;
        }
        List<GitTree> codeTree = diff.getCodeTree();
        List<GitTree> metaTree = diff.getMetaTree();

        //4. 返回文件列表
        return new OrchestratorDiffDirVo(codeTree, metaTree);
    }

    private String getLastPublishCommitId(DSSOrchestratorInfo orchestrator) {
        List<DSSOrchestratorVersion> orchestratorVersions = orchestratorMapper.getVersionByOrchestratorId(orchestrator.getId());
        String commitId = null;
        if (CollectionUtils.isNotEmpty(orchestratorVersions)) {
            String commitIdNow = orchestratorVersions.get(0).getCommitId();
            if (commitIdNow != null) {
                int i = 0;
                for (DSSOrchestratorVersion version : orchestratorVersions) {
                    if (StringUtils.isNotEmpty(version.getCommitId()) && i++ != 0) {
                        commitId = version.getCommitId();
                        break;
                    }
                }
            }
        }
        return commitId;
    }

    @Override
    public GitFileContentResponse diffFlowContent(OrchestratorSubmitRequest flowRequest, String username, Workspace workspace) throws DSSErrorException {
        Long orchestratorId = flowRequest.getOrchestratorId();
        Long flowId = flowRequest.getFlowId();
        String projectName = flowRequest.getProjectName();
        String label = flowRequest.getLabels().getRoute();
        String filePath = flowRequest.getFilePath();
        DSSOrchestratorInfo orchestrator = orchestratorMapper.getOrchestrator(orchestratorId);
        BmlResource bmlResource = orchestratorMapper.getOrchestratorBmlVersion(orchestratorId);
        if (bmlResource == null) {
            bmlResource = uploadWorkflowToGit(flowId, projectName, label, username, workspace, orchestrator);
            orchestratorMapper.updateOrchestratorBmlVersion(orchestratorId, bmlResource.getResourceId(), bmlResource.getVersion());
        }
        String s = readWorkflowNode(bmlResource, username, projectName, orchestrator.getName(), filePath);


        GitFileContentRequest fileContentRequest = new GitFileContentRequest();
        fileContentRequest.setFilePath(filePath);
        fileContentRequest.setProjectName(projectName);
        fileContentRequest.setWorkspaceId(workspace.getWorkspaceId());
        fileContentRequest.setUsername(username);
        fileContentRequest.setPublish(flowRequest.getPublish());

        if (flowRequest.getPublish()) {
            String commitId = getLastPublishCommitId(orchestrator);
            fileContentRequest.setCommitId(commitId);
        }

        Sender gitSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        GitFileContentResponse contentResponse = RpcAskUtils.processAskException(gitSender.ask(fileContentRequest), GitFileContentResponse.class, GitFileContentRequest.class);
        if (flowRequest.getPublish()) {
            return contentResponse;
        }
        contentResponse.setAfter(s);
        return contentResponse;
    }

    private GitDiffResponse diff(String path, BmlResource bmlResource, String username, Long workspaceId, String projectName) {
        Sender gitSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        Map<String, BmlResource> file = new HashMap<>();
        file.put(path, bmlResource);
        GitDiffRequest request1 = new GitDiffRequest(workspaceId, projectName, file, username);
        LOGGER.info("-------=======================begin to diff {}=======================-------", request1.getProjectName());
        GitDiffResponse responseWorkflowValidNode = RpcAskUtils.processAskException(gitSender.ask(request1), GitDiffResponse.class, GitDiffRequest.class);
        LOGGER.info("-------=======================End to diff testGit1=======================-------: {}", responseWorkflowValidNode);
        return responseWorkflowValidNode;
    }

    private GitDiffResponse diffPublish(String path, String commitId, String username, Long workspaceId, String projectName) {
        Sender gitSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getGitSender();
        GitDiffTargetCommitRequest targetCommitRequest = new GitDiffTargetCommitRequest(workspaceId, projectName, commitId, username, path);
        GitDiffResponse responseWorkflowValidNode = RpcAskUtils.processAskException(gitSender.ask(targetCommitRequest), GitDiffResponse.class, GitDiffTargetCommitRequest.class);
        return responseWorkflowValidNode;
    }

    /**
     * 发布整个项目中所有未发布过的工作流。
     * 使用在先导入，后发布两步走的场景中
     */
    private ResponseOperateOrchestrator convertWholeProjectOrcs(RequestFrameworkConvertOrchestration requestConversionOrchestration){
        Long projectId=requestConversionOrchestration.getProjectId();
        List<DSSLabel> labels = LabelBuilderFactoryContext.getLabelBuilderFactory().getLabels(requestConversionOrchestration.getLabels());

        //这里的 key 为 DSS 具体编排（如 DSS 工作流）的 id；这里的 value 为 DSS 编排所对应的第三方调度系统的工作流 ID
        //请注意：由于对接的 SchedulerAppConn 调度系统有可能没有实现 OrchestrationService，
        //所以可能存在 DSS 在创建 DSS 编排时，无法同步去 SchedulerAppConn 创建工作流的情况，从而导致这个 Map 的所有 value 都为 null。
        Map<Long, Long> orchestrationIdMap = new HashMap<>();
        List<Long> orcIds=new ArrayList<>();
        //所有之前发布过的编排
        List<Long> publishedOrcIds = orchestratorMapper.getAllOrcIdsByProjectId(projectId,1);
        orcIds.addAll(publishedOrcIds);
        //所有之前未发布过的编排
        List<Long> notPublished= orchestratorMapper.getAllOrcIdsByProjectId(projectId,0);
        orcIds.addAll(notPublished);
        for (Long publishedOrcId : orcIds) {
            DSSOrchestratorVersion dssOrchestratorVersion = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(publishedOrcId, 1);
            if (dssOrchestratorVersion == null) {
                continue;
            }
            DSSOrchestratorRefOrchestration dssOrchestratorRefOrchestration = orchestratorMapper.getRefOrchestrationId(publishedOrcId);
            if(dssOrchestratorRefOrchestration != null) {
                orchestrationIdMap.put(dssOrchestratorVersion.getAppId(), dssOrchestratorRefOrchestration.getRefOrchestrationId());
            } else {
                orchestrationIdMap.put(dssOrchestratorVersion.getAppId(), null);
            }
        }
        if(orchestrationIdMap.isEmpty()) {
            LOGGER.info("the project {} has no suitable workflow, the publish by user {} is ignored.", projectId,
                    requestConversionOrchestration.getUserName());
            return ResponseOperateOrchestrator.failed("No suitable workflow(s) found, publish is ignored.");

        }

        LOGGER.info("user {} try to submit a conversion job , the orchestrationIdMap is {}, the orcIdList is {}.",
                requestConversionOrchestration.getUserName(),
                orchestrationIdMap, publishedOrcIds);
        long startTime=System.currentTimeMillis();
        //进行发布到schedulis等调度系统
        LOGGER.info(" begin to convert project {} for user {} to scheduler, the orchestrationIds is {}.",
                projectId,requestConversionOrchestration.getUserName() , orcIds);
        ConversionDSSOrchestratorPlugin conversionDSSOrchestratorPlugin = null;
        for (DSSOrchestratorPlugin plugin:dssOrchestratorContext.getOrchestratorPlugins()) {
            if(plugin instanceof ConversionDSSOrchestratorPlugin) {
                conversionDSSOrchestratorPlugin = (ConversionDSSOrchestratorPlugin) plugin;
            }
        }
        if(conversionDSSOrchestratorPlugin==null){
            return ResponseOperateOrchestrator.failed("can not find plugin type of ConversionDSSOrchestratorPlugin,operate failed!");
        }
        ProjectInfoRequest projectInfoRequest = new ProjectInfoRequest();
        projectInfoRequest.setProjectId(projectId);
        ResponseOperateOrchestrator response;
        try{
            DSSProject project = RpcAskUtils.processAskException(DSSSenderServiceFactory.getOrCreateServiceInstance().getProjectServerSender()
                    .ask(projectInfoRequest), DSSProject.class, ProjectInfoRequest.class);
            response = conversionDSSOrchestratorPlugin.convert(
                    requestConversionOrchestration.getUserName(),
                    project,
                    (Workspace) requestConversionOrchestration.getWorkspace(),
                    orchestrationIdMap,
                    labels,
                    requestConversionOrchestration.getApproveId());
            if(response.isFailed()) {
                String msg = response.getMessage() == null ? "Unknown reason, please ask admin for help!" : response.getMessage();
                throw new DSSErrorException(50000, msg);
            }
            final ResponseOperateOrchestrator finalResponse=response;
            //最后，把所有之前未发布过的编排，都标记为已发布。
            ConversionJobEntity entity = new ConversionJobEntity();
            entity.setUserName(requestConversionOrchestration.getUserName());
            entity.setOrcIdList(publishedOrcIds);
            entity.setLabels(labels);
            entity.setProject(project);
            notPublished.forEach(toPublishOrcId-> this.updateDBAfterConversion(toPublishOrcId, finalResponse, entity, requestConversionOrchestration) );
        } catch (final Exception t){
            LOGGER.error("convert for project failed"+projectId, t);
            response = ResponseOperateOrchestrator.failed(ExceptionUtils.getRootCauseMessage(t));
        }
        LOGGER.info("convert project {} for user {} to Orchestrators {}, costs {}.",projectId,
                requestConversionOrchestration.getUserName(),orcIds , ByteTimeUtils.msDurationToString(System.currentTimeMillis()-startTime));

        return response;
    }

    @Override
    public ResponseConvertOrchestrator getConvertOrchestrationStatus(String id) {
        String jobId;
        ResponseOperateOrchestrator responseOperateOrchestrator = new ResponseOperateOrchestrator();
        LOGGER.info("Convert to orchestrator id: {}", id);
        OrchestratorPublishJob publishJob = orchestratorJobMapper.getPublishJobByJobId(id);
        jobId = publishJob.getJobId();
        responseOperateOrchestrator.setJobStatus(JobStatus.getJobStatusByStatus(publishJob.getStatus()));
        if (publishJob.getErrorMsg() != null) {
            responseOperateOrchestrator.setMessage(publishJob.getErrorMsg());
        }
        return new ResponseConvertOrchestrator(jobId, responseOperateOrchestrator);
    }

    private void updateDBAfterConversion(Long toPublishOrcId,ResponseOperateOrchestrator response,
                                         ConversionJobEntity conversionJobEntity,
                                         RequestFrameworkConvertOrchestration requestConversionOrchestration) {
        if (response.isSucceed()) {
            //1. 进行导出,用于升级版本,目的是为了复用原来的代码
            List<Long> orcIdList = conversionJobEntity.getOrcIdList();
            LOGGER.info("the orcIdList is:{}", orcIdList);
            // 开发环境才新增版本号
            if(DSSLabelUtil.isDevEnv(conversionJobEntity.getLabels())){
                orcIdList.forEach(DSSExceptionUtils.handling(orcId -> {
                    DSSOrchestratorInfo dssOrchestratorInfo = orchestratorMapper.getOrchestrator(orcId);
                    dssOrchestratorContext.getDSSOrchestratorPlugin(ExportDSSOrchestratorPlugin.class)
                            .orchestratorVersionIncrease(orcId, conversionJobEntity.getUserName(), requestConversionOrchestration.getComment(),
                                    (Workspace) requestConversionOrchestration.getWorkspace(), dssOrchestratorInfo,
                                    conversionJobEntity.getProject().getName(), conversionJobEntity.getLabels());
                }));
            }
            //2. 做一个标记表示已经发布过了
            orcIdList.forEach(orchestratorMapper::setPublished);
            //3.更新当前的提交的发布编排模式版本的common
            updateCurrentPublishOrchestratorCommon(toPublishOrcId,requestConversionOrchestration.getComment(),conversionJobEntity.getLabels());
        }
    }

    public void updateCurrentPublishOrchestratorCommon(Long orcId,String realComment,List<DSSLabel> dssLabels) {
        //DEV环境获取获取最新有效的版本即可
        int validFlag = 1;
        if (!DSSOrchestratorConf.DSS_IMPORT_VALID_IMMEDIATELY.getValue()&& !DSSLabelUtil.isDevEnv(dssLabels)) {
            //PROD获取当前编排模式最新版本的ID
            validFlag = 0;
        }
        DSSOrchestratorVersion version = orchestratorMapper.getLatestOrchestratorVersionByIdAndValidFlag(orcId,validFlag);
        DSSOrchestratorVersion updateCommentVersion = new DSSOrchestratorVersion();
        updateCommentVersion.setId(version.getId());
        updateCommentVersion.setComment(realComment);
        updateCommentVersion.setUpdateTime(new Date());
        updateCommentVersion.setValidFlag(1);
        orchestratorMapper.updateOrchestratorVersion(updateCommentVersion);
    }
}
