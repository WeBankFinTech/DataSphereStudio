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

import com.webank.wedatasphere.dss.appconn.schedulis.Action.FlowScheduleUploadAction;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanConvertedRel;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SSORequestWTSS;
import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestService;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.workflow.conversion.entity.ConvertedRel;
import com.webank.wedatasphere.dss.workflow.conversion.operation.WorkflowToRelSynchronizer;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.httpclient.request.BinaryBody;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AzkabanWorkflowToRelSynchronizer implements WorkflowToRelSynchronizer {

    public static final Logger LOGGER = LoggerFactory.getLogger(AzkabanWorkflowToRelSynchronizer.class);

    private String projectUrl;
    private SSORequestService ssoRequestService;

    @Override
    public void setAppInstance(AppInstance appInstance) {
        this.projectUrl = appInstance.getBaseUrl().endsWith("/") ? appInstance.getBaseUrl() + "manager":
            appInstance.getBaseUrl() + "/manager";
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

    @Override
    public void setSSORequestService(SSORequestService ssoRequestService) {
        this.ssoRequestService = ssoRequestService;
    }

    private void uploadProject(Workspace workspace, String tmpSavePath, String projectName, String releaseUser) throws Exception {

        File file = new File(tmpSavePath);
        InputStream inputStream = new FileInputStream(file);
        try {
            BinaryBody binaryBody =BinaryBody.apply("file",inputStream,file.getName(),"application/zip");
            List<BinaryBody> binaryBodyList =new ArrayList<>();
            binaryBodyList.add(binaryBody);
            FlowScheduleUploadAction uploadAction = new FlowScheduleUploadAction(binaryBodyList);
            uploadAction.getFormParams().put("ajax", "upload");
            uploadAction.getFormParams().put("project", projectName);

            uploadAction.getParameters().put("project", projectName);
            uploadAction.getParameters().put("ajax", "upload");
            uploadAction.setURl(projectUrl);


            HttpResult response = SSORequestWTSS.requestWTSSWithSSOUpload(projectUrl,uploadAction,this.ssoRequestService,workspace);

            if (response.getStatusCode() == 200 || response.getStatusCode()==0) {
                LOGGER.info("upload project:{} success!", projectName);
            }else{
                LOGGER.error("调用azkaban上传接口的返回不为200, status code 是 {}", response.getStatusCode());
                throw new ErrorException(90013, "release project failed, " + response.getResponseBody());
            }

        } catch (Exception e) {
            LOGGER.error("upload failed,reason:", e);
            throw new ErrorException(90014, e.getMessage());
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
