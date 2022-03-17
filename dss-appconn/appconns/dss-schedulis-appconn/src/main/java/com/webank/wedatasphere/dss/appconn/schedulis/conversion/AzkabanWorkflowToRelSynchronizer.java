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

package com.webank.wedatasphere.dss.appconn.schedulis.conversion;

import com.webank.wedatasphere.dss.appconn.schedulis.SchedulisAppConn;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanConvertedRel;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SchedulisHttpUtils;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.dss.orchestrator.converter.standard.operation.DSSToRelConversionOperation;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSUploadAction;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.operation.WorkflowToRelSynchronizer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.linkis.httpclient.request.BinaryBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AzkabanWorkflowToRelSynchronizer implements WorkflowToRelSynchronizer {

    public static final Logger LOGGER = LoggerFactory.getLogger(AzkabanWorkflowToRelSynchronizer.class);

    private String projectUrl;
    private DSSToRelConversionOperation dssToRelConversionOperation;

    public void init() {
        String baseUrl = dssToRelConversionOperation.getConversionService().getAppInstance().getBaseUrl();
        this.projectUrl = baseUrl.endsWith("/") ? baseUrl + "manager": baseUrl + "/manager";
    }

    @Override
    public void setDSSToRelConversionOperation(DSSToRelConversionOperation dssToRelConversionOperation) {
        this.dssToRelConversionOperation = dssToRelConversionOperation;
        init();
    }

    @Override
    public void syncToRel(ConvertedRel convertedRel) {
        String tmpSavePath;
        AzkabanConvertedRel azkabanConvertedRel = (AzkabanConvertedRel) convertedRel;
        try {
            String projectPath = azkabanConvertedRel.getStorePath();
            tmpSavePath = ZipHelper.zip(projectPath);
            //upload zip to Azkaban
            uploadProject(azkabanConvertedRel.getDSSToRelConversionRequestRef().getWorkspace(), tmpSavePath,
                azkabanConvertedRel.getDSSToRelConversionRequestRef().getDSSProject().getName(), azkabanConvertedRel.getDSSToRelConversionRequestRef().getUserName());
        } catch (Exception e) {
            throw new DSSRuntimeException(90012, ExceptionUtils.getRootCauseMessage(e), e);
        }
    }

    private void uploadProject(Workspace workspace, String tmpSavePath, String projectName, String releaseUser) throws Exception {

        File file = new File(tmpSavePath);
        InputStream inputStream = new FileInputStream(file);
        try {
            BinaryBody binaryBody = BinaryBody.apply("file",inputStream,file.getName(),"application/zip");
            List<BinaryBody> binaryBodyList =new ArrayList<>();
            binaryBodyList.add(binaryBody);
            DSSUploadAction uploadAction = new DSSUploadAction(binaryBodyList);
            uploadAction.getFormParams().put("ajax", "upload");
            uploadAction.getFormParams().put("project", projectName);
            uploadAction.getParameters().put("project", projectName);
            uploadAction.getParameters().put("ajax", "upload");
            uploadAction.setUrl(projectUrl);
            SchedulisHttpUtils.getHttpResult(projectUrl, uploadAction,
                    dssToRelConversionOperation.getConversionService().getSSORequestService()
                            .createSSORequestOperation(SchedulisAppConn.SCHEDULIS_APPCONN_NAME), workspace);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
