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

package com.webank.wedatasphere.dss.framework.appconn.service.impl;

import com.webank.wedatasphere.dss.appconn.loader.utils.AppConnUtils;
import com.webank.wedatasphere.dss.appconn.manager.entity.AppConnInfo;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnResourceService;
import com.webank.wedatasphere.dss.appconn.manager.utils.AppConnIndexFileUtils;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.dss.framework.appconn.dao.AppConnMapper;
import com.webank.wedatasphere.dss.framework.appconn.entity.AppConnBean;
import com.webank.wedatasphere.dss.framework.appconn.entity.AppConnResource;
import com.webank.wedatasphere.dss.framework.appconn.exception.AppConnNotExistsErrorException;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnResourceUploadService;
import com.webank.wedatasphere.dss.framework.appconn.utils.AppConnServiceUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.bml.client.BmlClient;
import org.apache.linkis.bml.client.BmlClientFactory;
import org.apache.linkis.bml.protocol.BmlUpdateResponse;
import org.apache.linkis.bml.protocol.BmlUploadResponse;
import org.apache.linkis.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.*;
import java.nio.file.Paths;


@Component
public class AppConnResourceServiceImpl implements AppConnResourceService, AppConnResourceUploadService {

    private Logger LOGGER = LoggerFactory.getLogger(AppConnResourceServiceImpl.class);
    private static final String LIB_NAME = "/lib";

    @Autowired
    private AppConnMapper appConnMapper;
    private BmlClient bmlClient;

    @PostConstruct
    public void init() {
        bmlClient = BmlClientFactory.createBmlClient();
    }

    @Override
    public String getAppConnHome(AppConnInfo appConnInfo) {
        return Paths.get(AppConnUtils.getAppConnHomePath(), appConnInfo.getAppConnName()).toFile().getPath();
    }

    @Override
    public void upload(String appConnName) throws DSSErrorException {
        File appConnPath = new File(AppConnUtils.getAppConnHomePath(), appConnName);
        AppConnBean appConnBean = appConnMapper.getAppConnBeanByName(appConnName);
        if (!appConnPath.exists()) {
            //没有reference，必须有appconn目录
            if (StringUtils.isBlank(appConnBean.getReference())) {
                throw new AppConnNotExistsErrorException(20350, "AppConn home path " + appConnPath.getPath() + " not exists.");
            } else {
                LOGGER.info("Appconn {} references other appConns and has no directory, so no upload is required", appConnName);
                return;
            }
        } else if (!appConnPath.isDirectory()) {
            throw new AppConnNotExistsErrorException(20350, "AppConn home path " + appConnPath.getPath() + " is not a directory.");
        }
        AppConnResource appConnResource;
        File indexFile = null;
        if (appConnBean == null) {
            throw new AppConnNotExistsErrorException(20350, "AppConn not exists in DB, please update db at first.");
        } else if (StringUtils.isNotBlank(appConnBean.getResource())) {
            // If resource is exists, then indexFile is also exists.
            appConnResource = AppConnServiceUtils.stringToResource(appConnBean.getResource());
            indexFile = AppConnIndexFileUtils.getIndexFile(appConnPath);
            if (appConnPath.lastModified() == appConnResource.getLastModifiedTime()
                    && AppConnIndexFileUtils.isLatestIndex(appConnPath, appConnResource.getResource())) {
                LOGGER.info("No necessary to update the AppConn {}, since it's packages has no changes in path {}.", appConnName, appConnPath.getPath());
                return;
            }
        } else {
            // If resource is not exists, this is the first time to upload this AppConn.
            appConnResource = new AppConnResource();
        }
        File zipFile = new File(appConnPath.getPath() + ".zip");
        if (zipFile.exists() && !zipFile.delete()) {
            throw new AppConnNotExistsErrorException(20001, "No permission to delete old zip file " + zipFile);
        }
        ZipHelper.zip(appConnPath.getPath(), false);
        // At first, upload appConn file to BML
        Resource resource = new Resource();
        InputStream inputStream = null;
        if (appConnResource.getResource() != null) {
            try {
                inputStream = new FileInputStream(zipFile.getPath());
                BmlUpdateResponse response = bmlClient.updateResource(Utils.getJvmUser(), appConnResource.getResource().getResourceId(), zipFile.getPath(), inputStream);
                resource.setResourceId(appConnResource.getResource().getResourceId());
                resource.setVersion(response.version());
            } catch (FileNotFoundException e) {
                throw new AppConnNotExistsErrorException(20351, "AppConn update to bml failed" + e.getMessage());
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            LOGGER.info("AppConn {} updated Resource, from {} to {}.", appConnName,
                    DSSCommonUtils.COMMON_GSON.toJson(appConnResource.getResource()), DSSCommonUtils.COMMON_GSON.toJson(resource));
        } else {
            try {
                inputStream = new FileInputStream(zipFile.getPath());
                BmlUploadResponse response = bmlClient.uploadResource(Utils.getJvmUser(), zipFile.getPath(), inputStream);
                resource.setResourceId(response.resourceId());
                resource.setVersion(response.version());
            } catch (FileNotFoundException e) {
                throw new AppConnNotExistsErrorException(20352, "AppConn update to bml failed" + e.getMessage());
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
            LOGGER.info("AppConn {} completed the first upload of Resource with {}.", appConnName,
                    DSSCommonUtils.COMMON_GSON.toJson(resource));
        }
        resource.setFileName(zipFile.getName());
        // update index file.
        if (indexFile != null && !indexFile.delete()) {
            throw new AppConnNotExistsErrorException(20350, "Delete index file " + indexFile.getName() + " failed, please ensure the permission is all right.");
        }
        indexFile = new File(appConnPath, AppConnIndexFileUtils.getIndexFileName(resource));
        try {
            indexFile.createNewFile();
        } catch (IOException e) {
            throw new AppConnNotExistsErrorException(20350, "create index file " + indexFile.getName() + " failed, please ensure the permission is all right.", e);
        }
        // Then, insert into db.
        appConnResource.setLastModifiedTime(appConnPath.lastModified());
        appConnResource.setSize(zipFile.length());
        appConnResource.setResource(resource);
        String resourceStr = AppConnServiceUtils.resourceToString(appConnResource);
        AppConnBean appConnBeanReLoad = new AppConnBean();
        appConnBeanReLoad.setId(appConnBean.getId());
        appConnBeanReLoad.setResource(resourceStr);
        appConnBeanReLoad.setAppConnName(appConnName);
        appConnBeanReLoad.setClassName(appConnBean.getClassName());
        appConnMapper.updateResourceByName(appConnBeanReLoad);
        LOGGER.info("AppConn {} has updated resource to {}.", appConnName, resourceStr);
    }

    @PreDestroy
    public void destroy() {
        IOUtils.closeQuietly(bmlClient);
    }
}
