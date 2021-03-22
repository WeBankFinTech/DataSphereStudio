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

package com.webank.wedatasphere.dss.appconn.schedulis.operation;

import com.webank.wedatasphere.dss.appconn.schedulis.entity.AzkabanSchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedulis.hooks.LinkisAzkabanProjectPublishHook;
import com.webank.wedatasphere.dss.appconn.schedulis.parser.AzkabanProjectParser;
import com.webank.wedatasphere.dss.appconn.schedulis.ref.SchedulisUploadProjectResponseRef;
import com.webank.wedatasphere.dss.appconn.schedulis.sso.SchedulisSecurityService;
import com.webank.wedatasphere.dss.appconn.schedulis.tuning.AzkabanProjectTuning;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerProject;
import com.webank.wedatasphere.dss.appconn.schedule.core.hooks.ProjectPublishHook;
import com.webank.wedatasphere.dss.appconn.schedule.core.parser.ProjectParser;
import com.webank.wedatasphere.dss.appconn.schedule.core.tuning.ProjectTuning;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.ProjectUploadToSchedulerRef;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.UploadToScheduleOperation;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.standard.common.entity.project.Project;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author allenlliu
 * @date 2020/12/17 12:01
 */
public class SchedulisProjectUploadOperation implements UploadToScheduleOperation<ProjectUploadToSchedulerRef> {


    private Logger LOGGER = LoggerFactory.getLogger(this.getClass());


    private AppDesc appDesc;

    private List<DSSLabel> dssLabels;

    private SchedulisSecurityService schedulisSecurityService;

    private String projectUrl;

    private SSOUrlBuilderOperation ssoUrlBuilderOperation;

    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;


    public SchedulisProjectUploadOperation(AppDesc appDesc) {
        this.appDesc = appDesc;
        init();
    }

    private void init() {
        //todo 先默认只有一个schedulis的instance
        this.schedulisSecurityService = SchedulisSecurityService.getInstance(this.appDesc.getAppInstances().get(0).getBaseUrl());
        this.projectUrl = this.appDesc.getAppInstances().get(0).getBaseUrl().endsWith("/") ? this.appDesc.getAppInstances().get(0).getBaseUrl() + "manager":
                this.appDesc.getAppInstances().get(0).getBaseUrl() + "/manager";
    }



    public AppDesc getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }

    public List<DSSLabel> getDssLabels() {
        return dssLabels;
    }

    public void setDssLabels(List<DSSLabel> dssLabels) {
        this.dssLabels = dssLabels;
    }

    @Override
    public ResponseRef publish(ProjectUploadToSchedulerRef projectUploadToSchedulerRef) throws ExternalOperationFailedException {
        publishDssProject(projectUploadToSchedulerRef.getWorkspace(),
                projectUploadToSchedulerRef.getUserName(),
                projectUploadToSchedulerRef.getDSSProject(),
                projectUploadToSchedulerRef.getDSSFlowList());
        return new SchedulisUploadProjectResponseRef("success", 0);
    }

    private void publishDssProject(Workspace workspace, String publishUser, DSSProject dssProject, List<DSSFlow> dssFlowList) throws ExternalOperationFailedException {
        ProjectParser projectParser = getProjectParser();
        ProjectTuning projectTuning = getProjectTuning();
        ProjectPublishHook[] projectPublishHooks = getProjectPublishHooks();
        SchedulerProject schedulerProject = projectParser.parseProject(dssProject, dssFlowList);
        projectTuning.tuningSchedulerProject(schedulerProject);
        Stream.of(projectPublishHooks).forEach(DSSExceptionUtils.handling(hook -> hook.prePublish(schedulerProject)));
        zipAndUploadProject(workspace, schedulerProject, publishUser);
        Stream.of(projectPublishHooks).forEach(DSSExceptionUtils.handling(hook -> hook.postPublish(schedulerProject)));
    }


    private void zipAndUploadProject(Workspace workspace, Project project, String publishUser) throws ExternalOperationFailedException {
        String tmpSavePath;
        try {
            AzkabanSchedulerProject publishProject = (AzkabanSchedulerProject) project;
            String projectPath = publishProject.getStorePath();
            tmpSavePath = zipProject(projectPath);
            //upload zip to Azkaban
            uploadProject(workspace, tmpSavePath, project.getName(), publishUser);
        } catch (Exception e) {
            //LOGGER.error("upload sheduler failed:reason", e);
            throw new ExternalOperationFailedException(90012, e.getMessage(), e);
        }
    }


    public ProjectParser getProjectParser() {
        return new AzkabanProjectParser();
    }


    public ProjectTuning getProjectTuning() {
        return new AzkabanProjectTuning();
    }


    public ProjectPublishHook[] getProjectPublishHooks() {
        return new ProjectPublishHook[]{new LinkisAzkabanProjectPublishHook()};
    }


    private void uploadProject(Workspace workspace, String tmpSavePath, String projectName, String releaseUser) throws Exception {

        //todo cooperyang 由于schedulis还没有进行sso的改造，先沿用过去的方案进行upload

        Cookie cookie = this.schedulisSecurityService.login(releaseUser);
        HttpPost httpPost = new HttpPost(projectUrl + "?project=" + projectName);
        httpPost.addHeader(HTTP.CONTENT_ENCODING, "UTF-8");
        CloseableHttpResponse response = null;
        File file = new File(tmpSavePath);
        CookieStore cookieStore = new BasicCookieStore();
        cookieStore.addCookie(cookie);
        CloseableHttpClient httpClient = null;
        InputStream inputStream = null;
        try {
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.addBinaryBody("file", file);
            entityBuilder.addTextBody("ajax", "upload");
            entityBuilder.addTextBody("project", projectName);
            httpPost.setEntity(entityBuilder.build());
            response = httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            inputStream = httpEntity.getContent();
            String entStr = null;
            entStr = IOUtils.toString(inputStream, "utf-8");
            if (response.getStatusLine().getStatusCode() != 200) {
                LOGGER.error("调用azkaban上传接口的返回不为200, status code 是 {}", response.getStatusLine().getStatusCode());
                throw new ErrorException(90013, "release project failed, " + entStr);
            }
            LOGGER.info("upload project:{} success!", projectName);
        } catch (Exception e) {
            LOGGER.error("upload failed,reason:", e);
            throw new ErrorException(90014, e.getMessage());
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
    }


    public String zipProject(String projectPath) throws DSSErrorException {
        return ZipHelper.zip(projectPath);
    }

    private String getSchedulisProjectCreateUrl(String projectName) {

        try {
            List<AppInstance> appInstances = this.appDesc.getAppInstancesByLabels(dssLabels);

            if (appInstances == null || appInstances.size() <= 0) {
                LOGGER.error("appInstances are null, can not get schedulis project url");
                return null;
            }
            AppInstance appInstance = appInstances.get(0);

            String projectUploadUrl = appInstance.getBaseUrl() + "?project=" + projectName;

            LOGGER.info("getSchedulisProjectCreateUrl is " + projectUploadUrl);

            return projectUploadUrl;
        } catch (final Exception e) {
            LOGGER.error("Failed to get schedulis project create url for {}", projectName, e);
            return null;
        }

    }



}
