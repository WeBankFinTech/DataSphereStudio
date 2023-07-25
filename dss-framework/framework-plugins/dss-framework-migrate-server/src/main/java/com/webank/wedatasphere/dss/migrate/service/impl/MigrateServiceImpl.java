package com.webank.wedatasphere.dss.migrate.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.entity.IOType;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.protocol.ResponseImportOrchestrator;
import com.webank.wedatasphere.dss.common.utils.*;
import com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO;
import com.webank.wedatasphere.dss.framework.project.entity.request.ProjectCreateRequest;
import com.webank.wedatasphere.dss.framework.project.entity.vo.DSSProjectVo;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.framework.project.service.DSSFrameworkProjectService;
import com.webank.wedatasphere.dss.framework.project.service.DSSProjectService;
import com.webank.wedatasphere.dss.migrate.exception.MigrateErrorException;
import com.webank.wedatasphere.dss.migrate.service.MetaService;
import com.webank.wedatasphere.dss.migrate.service.MigrateService;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.*;
import com.webank.wedatasphere.dss.sender.service.DSSSenderServiceFactory;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.linkis.rpc.Sender;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MigrateServiceImpl implements MigrateService {


    private static final Logger LOG = LoggerFactory.getLogger(MigrateServiceImpl.class);

    private static final String WORK_FLOW = "workflow";

    @Autowired
    private MetaService metaService;

    @Autowired
    private DSSProjectService dssProjectService;

    @Autowired
    DSSFrameworkProjectService dssFrameworkProjectService;

    @Autowired
    @Qualifier("projectBmlService")
    BMLService bmlService;

    private Sender orchestratorSender = DSSSenderServiceFactory.getOrCreateServiceInstance().getOrcSender();

    @Override
    public void migrate(String userName, String inputZipPath, Workspace workspace) throws Exception {
        //解压之后需要替换一下文件内容，可以进行反序列化
        String inputPath = ZipHelper.unzip(inputZipPath);
        String metaFilePath = inputPath.endsWith("/") ? inputPath + "meta.txt" : inputPath + "/meta.txt";
        try {
            replaceFileStr(metaFilePath, "com.webank.wedatasphere.dss.common.entity.project.DWSProject",
                    "com.webank.wedatasphere.dss.framework.project.entity.DSSProjectDO");
            replaceFileStr(metaFilePath, "com.webank.wedatasphere.dss.common.entity.flow.DWSFlow",
                    "com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow");
            replaceFileStr(metaFilePath, "com.webank.wedatasphere.dss.server.entity.DWSFlowRelation",
                    "com.webank.wedatasphere.dss.workflow.common.entity.DSSFlowRelation");
        } catch (final IOException e) {
            throw new MigrateErrorException(40012, "failed to replace meta.txt", e);
        }
        DSSProjectDO dssProject = metaService.readProject(inputPath);
        DSSProjectVo finalProject;
        DSSProjectDO dbProject = dssProjectService.getProjectByName(dssProject.getName());
        if (!dssProject.getUsername().equalsIgnoreCase(userName)) {
            LOG.error("fatal error, project owner is {} ，but export user is {}", dssProject.getUsername(), userName);
            throw new MigrateErrorException(40013, "project has been exported by others,not owner " + dssProject.getUsername());
        }
        if (dbProject == null) {
            //判断如果没有该工程，则开始调用接口来进行工程创建
            LOG.info("dssProject {} is not exist will create it first", dssProject.getName());
            ProjectCreateRequest projectCreateRequest = new ProjectCreateRequest();
            projectCreateRequest.setName(dssProject.getName());
            projectCreateRequest.setDescription(dssProject.getDescription());
            projectCreateRequest.setBusiness(dssProject.getBusiness());
            projectCreateRequest.setEditUsers(StringUtils.isNotBlank(dssProject.getUsername()) ? Lists.newArrayList(dssProject.getUsername()) : Lists.newArrayList(userName));
            projectCreateRequest.setAccessUsers(StringUtils.isNotBlank(dssProject.getUsername()) ? Lists.newArrayList(dssProject.getUsername()) : Lists.newArrayList(userName));
            projectCreateRequest.setReleaseUsers(StringUtils.isNotBlank(dssProject.getUsername()) ? Lists.newArrayList(dssProject.getUsername()) : Lists.newArrayList(userName));
            projectCreateRequest.setWorkspaceId(dssProject.getWorkspaceId());
            projectCreateRequest.setApplicationArea(dssProject.getApplicationArea());
            projectCreateRequest.setDevProcessList(Lists.newArrayList("dev", "prod"));
            projectCreateRequest.setOrchestratorModeList(Lists.newArrayList("pom_work_flow"));
            //todo
            workspace.setWorkspaceId(dssProject.getWorkspaceId());
            workspace.setWorkspaceName("");
            workspace.addCookie("workspaceId", String.valueOf(workspace.getWorkspaceId()));
            workspace.addCookie("workspaceName", String.valueOf(workspace.getWorkspaceName()));
            finalProject = dssFrameworkProjectService.createProject(projectCreateRequest, userName, workspace);
        } else if (!dbProject.getUsername().equals(dssProject.getUsername())) {
            LOG.error("fatal error, project {} 已经创建，但是创建人不是 {}", dssProject.getName(), dssProject.getCreateBy());
            throw new MigrateErrorException(40035, "project has been created by others");
        } else if (dbProject.getVisible() != null && dbProject.getVisible().intValue() == 0) {
            LOG.error("fatal error, project {} 已经被删除 {}", dssProject.getName());
            throw new MigrateErrorException(40036, "The project has been deleted.Please restore project before exporting");
        } else {
            DSSProjectVo dssProjectVo = new DSSProjectVo();
            dssProjectVo.setId(dbProject.getId());
            dssProjectVo.setName(dbProject.getName());
            dssProjectVo.setDescription(dbProject.getDescription());
            finalProject = dssProjectVo;
            dssProjectService.modifyOldProject(dssProject, dbProject);
        }
        List<DSSFlow> dssFlows = metaService.readFlow(inputPath);
        dssFlows.stream().forEach(dssFlow -> dssFlow.setProjectId(finalProject.getId()));

        List<DSSFlowRelation> dssFlowRelations = metaService.readFlowRelation(inputPath);
        List<DSSFlow> rootFlows = dssFlows.stream().filter(DSSFlow::getRootFlow).collect(Collectors.toList());

        //根据所有的rootflow创建新的orc信息并且写入到新的meta.txt,但是需要另外搞一个orc的路径
        for (DSSFlow rootFlow : rootFlows) {

            String orcPath = IoUtils.generateIOPath(userName, "input", "default_orc");
            try {
                mkdir(orcPath);
                //生成flow内容zip
                String flowZipPath = orcPath + File.separator + finalProject.getName();
                mkdir(flowZipPath);


                //由于0.x版本按照工程导出的zip包，所有元数据信息是在一块的，这里需要按照orc一个一个进行筛选出来。
                List<DSSFlow> rootFlowAndSubFlows = new ArrayList<>();
                genRootFlowAndSubFlows(rootFlow, dssFlows, dssFlowRelations, rootFlowAndSubFlows);

                //flowRelations
                List<DSSFlowRelation> rootFlowAndSubFlowsRelations = new ArrayList<>();
                genRootFlowAndSubFlowsRelations(rootFlow.getId(), dssFlowRelations, rootFlowAndSubFlowsRelations);

                //拷贝flow文件夹
                rootFlowAndSubFlows.stream().forEach(dssFlow -> {
                    try {
                        FileUtils.copyDirectoryToDirectory(new File(inputPath + File.separator + dssFlow.getName()), new File(flowZipPath));
                    } catch (IOException e) {
                        LOG.error("拷贝flow失败:" + dssFlow.getName(), e);
                    }
                });


                //标记当前导出为flow导出
                IoUtils.generateIOType(IOType.FLOW, flowZipPath);
                //标记当前导出环境
                IoUtils.generateIOEnv(flowZipPath);
                metaService.exportFlowBaseInfo(rootFlowAndSubFlows, rootFlowAndSubFlowsRelations, flowZipPath);
                String orcZipPath = ZipHelper.zip(flowZipPath);
                FileUtils.getFile(orcZipPath).renameTo(new File(orcPath + File.separator + "orc_flow.zip"));
//
            } catch (Exception e) {
                LOG.error("拷贝flow zip 失败", e);
            }
            DSSOrchestratorInfo orchestratorInfo = buildOrchestratorInfo(rootFlow, finalProject, dssProject.getWorkspaceId());
            //标记当前导出为project导出
            IoUtils.generateIOType(IOType.ORCHESTRATOR, orcPath);
            //标记当前导出环境env
            IoUtils.generateIOEnv(orcPath);
            metaService.writeOrchestratorInfo(orchestratorInfo, orcPath);
            String orcZipPath = ZipHelper.zip(orcPath);
            InputStream inputStream = new FileInputStream(orcZipPath);
            try {
                BmlResource bmlResource = bmlService.upload(userName, inputStream, "default_orc.zip", finalProject.getName());
                String resourceId = bmlResource.getResourceId();
                String version = bmlResource.getVersion();
                //不能走release的importservice接口 因为dev标签没有import操作
                importOrcToOrchestrator(resourceId, version, finalProject, userName, "dev", workspace, orchestratorInfo);
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }


    public static void genRootFlowAndSubFlows(DSSFlow parentFlow,
                                              List<DSSFlow> sourceFlowList,
                                              List<DSSFlowRelation> sourceFlowRelations,
                                              List<DSSFlow> resultFlows) {
        resultFlows.add(parentFlow);
        List<DSSFlowRelation> subFlowRelations = sourceFlowRelations.stream().filter(dssFlowRelation -> dssFlowRelation.getParentFlowID().equals(parentFlow.getId())).collect(Collectors.toList());
        List<Long> subFlowIds = subFlowRelations.stream().map(dssFlowRelation -> dssFlowRelation.getFlowID()).collect(Collectors.toList());
        if (subFlowIds.size() > 0) {
            for (Long subFlowId : subFlowIds) {

                DSSFlow subDssFlow = sourceFlowList.stream().filter(dssFlow -> dssFlow.getId().equals(subFlowId)).findAny().orElse(null);
                if (null != subDssFlow) {
                    resultFlows.add(subDssFlow);
                    parentFlow.addChildren(subDssFlow);
                    genRootFlowAndSubFlows(subDssFlow, sourceFlowList, sourceFlowRelations, resultFlows);
                }
            }
        }
    }


    public static void genRootFlowAndSubFlowsRelations(Long parentFlowID,
                                                       List<DSSFlowRelation> sourceFlowRelations,
                                                       List<DSSFlowRelation> resultFlowRelations) {

        List<DSSFlowRelation> subFlowRelations = sourceFlowRelations.stream().filter(dssFlowRelation -> dssFlowRelation.getParentFlowID().equals(parentFlowID)).collect(Collectors.toList());

        if (subFlowRelations.size() > 0) {
            for (DSSFlowRelation subFlowRelation : subFlowRelations) {
                resultFlowRelations.add(subFlowRelation);
                genRootFlowAndSubFlowsRelations(subFlowRelation.getFlowID(), sourceFlowRelations, resultFlowRelations);
            }
        }
    }

    @Override
    public long importOrcToOrchestrator(String resourceId, String version, DSSProjectVo project,
                                        String username, String label, Workspace workspace, DSSOrchestratorInfo dssOrchestratorInfo) {
        LOG.info("begin to import dss0.x workflow {} to orc-dev", dssOrchestratorInfo.getName());
        List<DSSLabel> dssLabels = Lists.newArrayList(new EnvDSSLabel(label));
        String workspaceStr = DSSCommonUtils.COMMON_GSON.toJson(workspace);
        RequestImportOrchestrator requestImportOrchestrator =
                new RequestImportOrchestrator(username, project.getName(),
                        project.getId(), resourceId, version, dssOrchestratorInfo.getName(), dssLabels, workspace);
        ResponseImportOrchestrator responseImportOrchestrator = RpcAskUtils.processAskException(this.orchestratorSender.ask(requestImportOrchestrator),
                ResponseImportOrchestrator.class, RequestImportOrchestrator.class);
        return responseImportOrchestrator.orcId();
    }

    @Override
    public DSSOrchestratorInfo buildOrchestratorInfo(DSSFlow dssFlow, DSSProjectVo dssProject, Long workspaceId) throws DSSErrorException {
        DSSOrchestratorInfo dssOrchestratorInfo = new DSSOrchestratorInfo();
        dssOrchestratorInfo.setName(dssFlow.getName());
        dssOrchestratorInfo.setComment(dssFlow.getDescription());
        dssOrchestratorInfo.setUses(dssFlow.getUses());
        dssOrchestratorInfo.setProjectId(dssProject.getId());
        dssOrchestratorInfo.setAppConnName(WORK_FLOW);
        dssOrchestratorInfo.setType(WORK_FLOW);
        dssOrchestratorInfo.setSecondaryType("[pom_work_flow_DAG]");
        dssOrchestratorInfo.setDesc(dssFlow.getDescription());
        String oldUUID = queryOrcUUIDByName(workspaceId, dssProject.getId(), dssFlow.getName());
        if (null != oldUUID) {
            dssOrchestratorInfo.setUUID(oldUUID);
        } else {
            String uuid = UUID.randomUUID().toString();
            dssOrchestratorInfo.setUUID(uuid);
        }
        dssOrchestratorInfo.setCreator(dssFlow.getCreator());

        return dssOrchestratorInfo;
    }

    @Override
    public String queryOrcUUIDByName(Long workspaceId, Long projectId, String orcName) throws DSSErrorException {
        String uuid = null;
        ResponseOrchestratorInfos responseOrchestratorInfos = RpcAskUtils.processAskException(orchestratorSender.ask(new RequestOrchestratorInfos(null, projectId, orcName, null)),
                ResponseOrchestratorInfos.class, RequestOrchestratorInfos.class);
        if (CollectionUtils.isNotEmpty(responseOrchestratorInfos.getOrchestratorInfos())) {
            uuid = responseOrchestratorInfos.getOrchestratorInfos().get(0).getUUID();
        }
        return uuid;
    }

    @Deprecated
    private List<OrchestratorVo> queryExitsOrc(List<Long> orcIds) throws DSSErrorException {
        RequestQueryOrchestrator queryRequest = new RequestQueryOrchestrator(orcIds);
        ResponseQueryOrchestrator queryResponse = null;
        try {
            queryResponse = (ResponseQueryOrchestrator) this.orchestratorSender.ask(queryRequest);
        } catch (Exception e) {
            DSSExceptionUtils.dealErrorException(60015, "create orchestrator ref failed",
                    DSSErrorException.class);
        }
        if (queryResponse == null) {
            LOG.error("query response is null, it is a fatal error");
            return null;
        }
        LOG.info("End to ask to query orchestrator, responseRef is {}", queryResponse);
        return queryResponse.getOrchestratorVoes();
    }


    private void replaceFileStr(String filepath, String sourceStr, String targetStr) throws IOException {
        FileReader fis = null;
        FileWriter fout = null;
        try {
            fis = new FileReader(filepath);
            char[] data = new char[1024];
            int rn = 0;
            StringBuilder sb = new StringBuilder();
            while ((rn = fis.read(data)) > 0) {
                String str = String.valueOf(data, 0, rn);
                sb.append(str);
            }
            String str = sb.toString().replace(sourceStr, targetStr);
            fout = new FileWriter(filepath);
            fout.write(str.toCharArray());
        } finally {
            IOUtils.closeQuietly(fis);
            IOUtils.closeQuietly(fout);
        }
    }


    public static void mkdir(String path) {
        File fd = null;
        try {
            fd = new File(path);
            if (!fd.exists()) {
                fd.mkdirs();
            }
        } catch (Exception e) {
            LOG.error("创建目录失败:" + path, e);
        } finally {
            fd = null;
        }
    }
}
