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

import com.webank.wedatasphere.dss.appconn.schedulis.SchedulisConf;
import com.webank.wedatasphere.dss.appconn.schedulis.ref.SchedulisProjectResponseRef;
import com.webank.wedatasphere.dss.appconn.schedulis.service.SchedulisProjectService;
import com.webank.wedatasphere.dss.appconn.schedulis.sso.SchedulisHttpPost;
import com.webank.wedatasphere.dss.appconn.schedulis.sso.SchedulisPostRequestOperation;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.AzkabanUtils;
import com.webank.wedatasphere.dss.appconn.schedulis.utils.SchedulisExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.impl.SSOUrlBuilderOperationImpl;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * created by cooperyang on 2020/11/13
 * Description:
 */
public class SchedulisProjectCreationOperation implements ProjectCreationOperation, SchedulisConf {



    private static final Logger LOGGER = LoggerFactory.getLogger(SchedulisProjectCreationOperation.class);

    private SchedulisProjectService schedulisProjectService;

    private SSOUrlBuilderOperation ssoUrlBuilderOperation;

    private SSORequestOperation<SchedulisHttpPost, CloseableHttpResponse> postOperation;

    private String projectUrl;

    private static final Long DEFAULT_PROJECT_ID = 0L;


    public SchedulisProjectCreationOperation(SchedulisProjectService schedulisProjectService){
        this.schedulisProjectService = schedulisProjectService;
        init();
    }


    private void init(){
        this.ssoUrlBuilderOperation = new SSOUrlBuilderOperationImpl();
        this.ssoUrlBuilderOperation.setAppName(schedulisProjectService.getAppDesc().getAppName());
        this.postOperation = new SchedulisPostRequestOperation(this.schedulisProjectService.getAppInstance().getBaseUrl());
        this.projectUrl = this.schedulisProjectService.getAppInstance().getBaseUrl().endsWith("/") ?
                this.schedulisProjectService.getAppInstance().getBaseUrl() + "manager" :
                this.schedulisProjectService.getAppInstance().getBaseUrl() + "/manager";
    }




    @Override
    public ProjectResponseRef createProject(ProjectRequestRef requestRef) throws ExternalOperationFailedException {
        //TODO 由于在Schedulis创建工程的时候
        LOGGER.info("begin to create project in schedulis project is {}", requestRef.getName());
        SchedulisProjectResponseRef responseRef = new SchedulisProjectResponseRef();
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("action", "create"));
        params.add(new BasicNameValuePair("name", requestRef.getName()));
        params.add(new BasicNameValuePair("description", requestRef.getDescription()));
        HttpEntity entity = EntityBuilder.create().
                setContentType(ContentType.create("application/x-www-form-urlencoded", Consts.UTF_8))
                .setParameters(params).build();
        SchedulisHttpPost httpPost = new SchedulisHttpPost(projectUrl, requestRef.getCreateBy());
        httpPost.setEntity(entity);
        CloseableHttpResponse httpResponse = null;
        try {
            httpResponse = this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost);
            HttpEntity ent = httpResponse.getEntity();
            String entStr = IOUtils.toString(ent.getContent(), "utf-8");
            LOGGER.error("新建工程 {}, azkaban 返回的信息是 {}", requestRef.getName(), entStr);
            String message = AzkabanUtils.handleAzkabanEntity(entStr);
            if (!"success".equals(message)) {
                throw new ExternalOperationFailedException(90008, "新建工程失败, 原因:" + message);
            }
            responseRef.setProjectRefId(DEFAULT_PROJECT_ID);
        } catch (final Throwable t) {
            SchedulisExceptionUtils.dealErrorException(60051, "failed to create project in schedulis", t,
                    ExternalOperationFailedException.class);
        } finally {
            IOUtils.closeQuietly(httpResponse);
        }
        //schedulis 是没有工程id返回的，所以无需set,另外的异常情况都throw出去了所以返回为0的正确码就OK
        return responseRef;
    }

    @Override
    public void setStructureService(StructureService service) {
        if (service instanceof SchedulisProjectService){
            this.schedulisProjectService = (SchedulisProjectService)service;
        }
    }

    private String getSchedulisProjectCreateUrl(){
        if (this.schedulisProjectService.getAppInstance().getBaseUrl().endsWith("/")){
            return this.schedulisProjectService.getAppInstance().getBaseUrl() + "manager";
        }else{
            return this.schedulisProjectService.getAppInstance().getBaseUrl() + "/manager";
        }
    }


}
