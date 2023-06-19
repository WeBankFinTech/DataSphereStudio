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

package com.webank.wedatasphere.dss.orchestrator.server.restful;

import com.webank.wedatasphere.dss.common.auditlog.OperateTypeEnum;
import com.webank.wedatasphere.dss.common.auditlog.TargetTypeEnum;
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelKeyConvertor;
import com.webank.wedatasphere.dss.common.utils.AuditLogUtils;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorVersion;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.RequestImportOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.DSSOrchestratorContext;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.orchestrator.publish.ExportDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.publish.ImportDSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.orchestrator.server.service.OrchestratorService;
import com.webank.wedatasphere.dss.orchestrator.server.service.impl.OrchestratorFrameworkServiceImpl;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.commons.io.IOUtils;
import org.apache.linkis.common.io.FsPath;
import org.apache.linkis.filesystem.service.FsService;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.security.SecurityFilter;
import org.apache.linkis.storage.fs.FileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequestMapping(path = "/dss/framework/orchestrator", produces = {"application/json"})
@RestController
public class OrchestratorIERestful {
    private static final Logger logger = LoggerFactory.getLogger(OrchestratorIERestful.class);
    @Autowired
    @Qualifier("orchestratorBmlService")
    private BMLService bmlService;
    @Autowired
    private FsService fsService;
    @Autowired
    OrchestratorService orchestratorService;
    @Autowired
    private DSSOrchestratorContext orchestratorContext;


    @RequestMapping(path ="importOrchestratorFile", method = RequestMethod.POST)
    public Message importOrcFile(HttpServletRequest req,
                                  @RequestParam(required = false, name = "projectName") String projectName,
                                  @RequestParam(required = false, name = "projectID") Long projectID,
                                  @RequestParam(required = false, name = "labels") String labels,
                                  @RequestParam(required = false, name = "packageFile") MultipartFile packageFile,
                                  @RequestParam(required = false, name = "packageUri") String packageUri) throws Exception {

        if (null == packageFile && packageUri == null) {
            throw new DSSErrorException(100788, "Import orchestrator failed for files is empty");
        }
        String userName = SecurityFilter.getLoginUsername(req);
        //调用工具类生产label
        List<DSSLabel> dssLabelList = getDSSLabelList(labels);
        Workspace workspace = SSOHelper.getWorkspace(req);
        InputStream inputStream;
        String fileName;
        if (packageFile != null) {
            inputStream = packageFile.getInputStream();
            fileName = new String(packageFile.getOriginalFilename().getBytes("ISO8859-1"), "UTF-8");
            //3、打包新的zip包上传BML
            logger.info("User {} begin to import orchestrator file", userName);
        } else {
            FsPath fsPath = new FsPath(packageUri);
            FileSystem fileSystem = fsService.getFileSystem(userName, fsPath);
            if ( !fileSystem.exists(fsPath) ) {
                throw new DSSRuntimeException("路径上不存在文件！");
            }
            inputStream = fileSystem.read(fsPath);
            fileName = packageUri.substring(packageUri.lastIndexOf('/') + 1);
        }

        BmlResource resultMap = bmlService.upload(userName, inputStream, fileName, projectName);
        DSSOrchestratorVersion dssOrchestratorVersion;
        try {
            RequestImportOrchestrator importRequest = new RequestImportOrchestrator(userName, projectName,
                    projectID, resultMap.getResourceId(),
                    resultMap.getVersion(), null, dssLabelList, workspace);
            dssOrchestratorVersion = orchestratorContext.getDSSOrchestratorPlugin(ImportDSSOrchestratorPlugin.class).importOrchestrator(importRequest);
            AuditLogUtils.printLog(userName, workspace.getWorkspaceId(), workspace.getWorkspaceName(), TargetTypeEnum.ORCHESTRATOR,
                    projectID, projectName, OperateTypeEnum.CREATE, importRequest);
        } catch (Exception e) {
            logger.error("Import orchestrator failed for ", e);
            throw new DSSErrorException(100789, "Import orchestrator failed for " + e.getMessage());
        }
        return Message.ok().data("importOrcId", dssOrchestratorVersion.getOrchestratorId());
    }

    @RequestMapping(path ="exportOrchestrator", method = RequestMethod.GET)
    public void exportOrcFile(HttpServletRequest req,
                              HttpServletResponse resp,
                              @RequestParam(defaultValue = "exportOrc",required = false, name = "outputFileName") String outputFileName,
                              @RequestParam(defaultValue = "utf-8",required = false, name = "charset") String charset,
                              @RequestParam(defaultValue = "zip",required = false, name = "outputFileType") String outputFileType,
                              @RequestParam(name = "projectName") String projectName,
                              @RequestParam(name = "orchestratorId") Long orchestratorId,
                              @RequestParam(required = false, name = "orcVersionId") Long orcVersionId,
                              @RequestParam(defaultValue = "false",required = false, name = "addOrcVersion") Boolean addOrcVersion,
                              @RequestParam(required = false, name = "labels") String labels) throws DSSErrorException, IOException {
        resp.addHeader("Content-Disposition", "attachment;filename="
                + new String(outputFileName.getBytes("UTF-8"), "ISO8859-1") + "." + outputFileType);
        resp.setCharacterEncoding(charset);
        Workspace workspace = SSOHelper.getWorkspace(req);
        String userName = SecurityFilter.getLoginUsername(req);
        List<DSSLabel> dssLabelList = getDSSLabelList(labels);
        BmlResource res;
        OrchestratorVo orchestratorVo;
        if (orcVersionId != null) {
            orchestratorVo = orchestratorService.getOrchestratorVoByIdAndOrcVersionId(orchestratorId, orcVersionId);
        } else {
            orchestratorVo = orchestratorService.getOrchestratorVoById(orchestratorId);
        }
        orcVersionId = orchestratorVo.getDssOrchestratorVersion().getId();
        //鉴权
        OrchestratorFrameworkServiceImpl.validateOperation(orchestratorVo.getDssOrchestratorInfo().getProjectId(), userName);
        logger.info("export orchestrator orchestratorId " + orchestratorId + ",orcVersionId:" + orcVersionId);
        try {
            res = orchestratorContext.getDSSOrchestratorPlugin(ExportDSSOrchestratorPlugin.class).exportOrchestrator(userName,
                    orchestratorId, orcVersionId, projectName, dssLabelList, addOrcVersion, workspace).getBmlResource();
            AuditLogUtils.printLog(userName, workspace.getWorkspaceId(), workspace.getWorkspaceName(), TargetTypeEnum.ORCHESTRATOR,
                    orcVersionId, orchestratorVo.getDssOrchestratorInfo().getName(), OperateTypeEnum.UPDATE, orchestratorVo);
        } catch (Exception e) {
            logger.error("export orchestrator failed for ", e);
            throw new DSSErrorException(100789, "export orchestrator failed for " + e.getMessage());
        }
        if (null != res) {
            Map<String, Object> downRes = bmlService.download(userName,
                    res.getResourceId(),
                    res.getVersion());

            InputStream inputStream = (InputStream) downRes.get("is");
            try {
                IOUtils.copy(inputStream, resp.getOutputStream());
                resp.getOutputStream().flush();
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        }
    }

    //生成label list
    public List<DSSLabel> getDSSLabelList(String labels) {
        String labelStr = DSSCommonUtils.ENV_LABEL_VALUE_DEV;
        try{
            Map<String, Object> labelMap = DSSCommonUtils.COMMON_GSON.fromJson(labels, Map.class);
            if (labelMap.containsKey(LabelKeyConvertor.ROUTE_LABEL_KEY)) {
                labelStr = (String) labelMap.get(LabelKeyConvertor.ROUTE_LABEL_KEY);
            }
        }catch (Exception e){
            logger.error("get labels failed for {}", e.getMessage());
        }
        List<DSSLabel> dssLabelList = Arrays.asList(new EnvDSSLabel(labelStr));
        return dssLabelList;
    }
}