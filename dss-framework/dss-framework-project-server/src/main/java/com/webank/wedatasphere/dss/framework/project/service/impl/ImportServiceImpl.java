/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.framework.project.service.impl;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.OnlyDevelopmentAppConn;
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.dss.framework.common.exception.DSSFrameworkErrorException;
import com.webank.wedatasphere.dss.framework.project.entity.OrchestratorBatchImportInfo;
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.framework.project.service.ImportService;
import com.webank.wedatasphere.dss.orchestrator.common.ref.OrchestratorRefConstant;
import com.webank.wedatasphere.dss.common.service.BMLService;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorBaseInfo;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.RefImportService;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.linkis.common.exception.ErrorException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * created by cooperyang on 2020/12/9
 * Description:
 */
@Service
public class ImportServiceImpl implements ImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ImportServiceImpl.class);

    private static final String ORC_FRAMEWORK_NAME = "orchestrator-framework";

    @Autowired
    @Qualifier("projectBmlService")
    private BMLService bmlService;

    @Override
    @SuppressWarnings("ConstantConditions")
    public OrchestratorBaseInfo importOrc(String orchestratorName, String releaseUser,Long projectId, String projectName,
                                                   BmlResource bmlResource, DSSLabel dssLabel, String workspaceName, Workspace workspace) throws ErrorException {
        //导入之后我们应该拿到的是导入之后的orchestrator的内容,这样我们在做同步到调度中心的时候才是同步的生产中心的内容
        LOGGER.info("Begin to import orc for project {} and orc resource is {}", projectName, bmlResource);
        OrchestratorBaseInfo orchestratorReleaseInfo =null;
        AppConn appConn = AppConnManager.getAppConnManager().getAppConn(ORC_FRAMEWORK_NAME);
        DevelopmentIntegrationStandard standard = ((OnlyDevelopmentAppConn)appConn).getOrCreateDevelopmentStandard();
        if (standard == null) {
            LOGGER.error("developStandard is null, can not do import operation");
            DSSExceptionUtils.dealErrorException(60045, "developStandard is null, can not do import operation", DSSFrameworkErrorException.class);
        }
        List<DSSLabel> dssLabelList = Collections.singletonList(dssLabel);
        AppInstance appInstance = appConn.getAppDesc().getAppInstancesByLabels(dssLabelList).get(0);
        RefImportService refImportService = standard.getRefImportService(appInstance);
        RefImportOperation<ThirdlyRequestRef.ImportRequestRefImpl> refImportOperation = refImportService.getRefImportOperation();
            try {
                ThirdlyRequestRef.ImportRequestRefImpl importRequestRef = new ThirdlyRequestRef.ImportRequestRefImpl();

                importRequestRef.setResourceMap(MapUtils.newCommonMap(
                        ImportRequestRef.RESOURCE_ID_KEY, bmlResource.getResourceId(), ImportRequestRef.RESOURCE_VERSION_KEY, bmlResource.getVersion()));
                importRequestRef.setDSSLabels(Collections.singletonList(dssLabel));
                importRequestRef.setRefProjectId(projectId);
                importRequestRef.setUserName(releaseUser);
                importRequestRef.setProjectName(projectName);
                importRequestRef.setName(orchestratorName);
                importRequestRef.setWorkspace(workspace);
                RefJobContentResponseRef responseRef = refImportOperation.importRef(importRequestRef);
                Long refOrcId = (Long) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATOR_ID_KEY);
                String orchestratorVersion=(String) responseRef.getRefJobContent().get(OrchestratorRefConstant.ORCHESTRATOR_VERSION_KEY);
                LOGGER.info("Succeed to get responseRef for projectInfo {}, refOrcId is {}.", projectName, refOrcId);
                //从返回的的内容中搞到导入之后的orc的信息
                orchestratorReleaseInfo=new OrchestratorBaseInfo();
                orchestratorReleaseInfo.setOrchestratorId(refOrcId);
                orchestratorReleaseInfo.setOrchestratorName(orchestratorName);
            } catch (Exception e) {
                DSSExceptionUtils.dealErrorException(60035, "Failed to import Ref for project: " + e.getMessage(), e, DSSFrameworkErrorException.class);
            }
        return orchestratorReleaseInfo;
    }

    @Override
    public OrchestratorBatchImportInfo batchImportOrc(String userName, Long projectId, String projectName,
                                                      BmlResource bmlResource, String checkCode,
                                                      DSSLabel dssLabel, Workspace workspace)
            throws ErrorException{
        //先核对md5
        String md5code;
        try(InputStream zipInputStream=(InputStream)bmlService.download(userName,
                bmlResource.getResourceId(),
                bmlResource.getVersion()).get("is")) {
            md5code=  DigestUtils.md5Hex(zipInputStream);
        } catch (IOException e) {
            LOGGER.error("md5 sum failed",e);
            throw new DSSRuntimeException(e.getMessage());
        }
        if(!md5code.equals(checkCode)){
            LOGGER.error("checkCode does not match the upload file。input code:{},real code:{}",checkCode,md5code);
            throw new DSSRuntimeException("checkCode does not match the upload file,please check your upload file and checkCode");
        }
        //下载到本地处理
        String importSaveBasePath = IoUtils.generateIOPath(userName, "","");
        String importFile=importSaveBasePath+File.separator+projectName+".zip";
        LOGGER.info("import zip file locate at {}",importFile);
        //下载压缩包
        bmlService.downloadToLocalPath(userName, bmlResource.getResourceId(), bmlResource.getVersion(), importFile);
        try{
            String  originProjectName=readImportZipProjectName(importFile);
            if(!projectName.equals(originProjectName)){
                String msg=String.format("target project name must be same with origin project name.origin project name:%s,target project name:%s(导入的目标工程名必须与导出时源工程名保持一致。源工程名：%s，目标工程名：%s)"
                        ,originProjectName,projectName,originProjectName,projectName);
                throw new DSSRuntimeException(msg);
            }
        }catch (IOException e){
            throw new DSSRuntimeException("upload file format error(导入包格式错误)");
        }
        //解压
        String unzipImportFile= ZipHelper.unzip(importFile);
        LOGGER.info("import unziped file locate at {}",unzipImportFile);
        //解析元数据
        String metaInfoPath=unzipImportFile+ File.separator+"meta.json";
        String metaJson = bmlService.readLocalTextFile(userName, metaInfoPath);
        Type type=new TypeToken<List<OrchestratorBaseInfo>>(){}.getType();
        List<OrchestratorBaseInfo> importOrcs=new Gson().fromJson(metaJson,type);

        Iterator<File> iterator= FileUtils.iterateFiles(new File(unzipImportFile),new String[]{"zip"},false);
        List<OrchestratorBaseInfo> importResultInfo = new ArrayList<>();
        while (iterator.hasNext()){
            File orcZipFile=iterator.next();
            InputStream inputStream = bmlService.readLocalResourceFile(userName, orcZipFile.getAbsolutePath());
            BmlResource uploadResult = bmlService.upload(userName, inputStream,
                    orcZipFile.getName() , projectName);
            String orcName = FilenameUtils.getBaseName(orcZipFile.getName());
            OrchestratorBaseInfo importInfo = importOrc(orcName, userName, projectId, projectName, uploadResult, dssLabel, workspace.getWorkspaceName(), workspace);
            importResultInfo.add(importInfo);
        }
        return new OrchestratorBatchImportInfo(importOrcs,importResultInfo);
    }
    private String readImportZipProjectName(String zipFilePath) throws IOException {
        try(ZipFile zipFile =new ZipFile(zipFilePath)){
            Enumeration<? extends ZipEntry> entries =zipFile.entries();
            if(entries.hasMoreElements()){
                String projectName=entries.nextElement().getName();
                while (projectName.endsWith(File.separator)){
                    projectName = projectName.substring(0, projectName.length() - 1);
                }
                return projectName;
            }
        }
        throw new IOException();
    }
}
