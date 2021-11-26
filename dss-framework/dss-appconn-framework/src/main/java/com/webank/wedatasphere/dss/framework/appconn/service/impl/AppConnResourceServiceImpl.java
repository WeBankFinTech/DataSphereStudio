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
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.manager.entity.AppConnInfo;
import com.webank.wedatasphere.dss.appconn.manager.impl.AbstractAppConnManager;
import com.webank.wedatasphere.dss.appconn.manager.service.AppConnResourceService;
import com.webank.wedatasphere.dss.appconn.manager.utils.AppConnIndexFileUtils;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.dss.framework.appconn.dao.AppConnMapper;
import com.webank.wedatasphere.dss.framework.appconn.entity.AppConnBean;
import com.webank.wedatasphere.dss.framework.appconn.entity.AppConnResource;
import com.webank.wedatasphere.dss.framework.appconn.exception.AppConnNotExistsErrorException;
import com.webank.wedatasphere.dss.framework.appconn.service.AppConnResourceUploadService;
import com.webank.wedatasphere.dss.framework.appconn.utils.AppConnServiceUtils;
import com.webank.wedatasphere.linkis.bml.client.BmlClient;
import com.webank.wedatasphere.linkis.bml.client.BmlClientFactory;
import com.webank.wedatasphere.linkis.bml.protocol.BmlUpdateResponse;
import com.webank.wedatasphere.linkis.bml.protocol.BmlUploadResponse;
import com.webank.wedatasphere.linkis.common.utils.Utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


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
        AppConnBean appConnBean = (AppConnBean) appConnInfo;
        String appConnHome=appConnBean.getAppConnClassPath();
        if(appConnHome.endsWith(LIB_NAME)){
            appConnHome =appConnHome.substring(0,appConnHome.lastIndexOf(LIB_NAME));
        }
        return appConnHome;
    }

    @Override
    public void upload(String appConnName) throws DSSErrorException {
        File appConnPath = new File(AppConnUtils.getAppConnHomePath(), appConnName);
        if (!appConnPath.exists()) {
            throw new AppConnNotExistsErrorException(20350, "AppConn home path " + appConnPath.getPath() + " not exists.");
        } else if (!appConnPath.isDirectory()) {
            throw new AppConnNotExistsErrorException(20350, "AppConn home path " + appConnPath.getPath() + " is not a directory.");
        }
        File zipFile = new File(appConnPath.getPath() + ".zip");
        if (zipFile.exists() && !zipFile.delete()) {
            throw new AppConnNotExistsErrorException(20001, "No permission to delete old zip file " + zipFile);
        }
        ZipHelper.zip(appConnPath.getPath(), false);
//      TODO  ZipUtils.fileToZip(appConnPath.getPath(), AppConnUtils.getAppConnHomePath(), appConnName + ".zip");
        AppConnBean appConnBean = appConnMapper.getAppConnBeanByName(appConnName);
        AppConnResource appConnResource;
        File indexFile = null;
        if (appConnBean == null) {
            throw new AppConnNotExistsErrorException(20350, "AppConn not exists in DB, please update db at first.");
        } else if (StringUtils.isNotBlank(appConnBean.getResource())) {
            // If resource is exists, then indexFile is also exists.
            appConnResource = AppConnServiceUtils.stringToResource(appConnBean.getResource());
            indexFile = AppConnIndexFileUtils.getIndexFile(appConnPath);
            if (appConnPath.lastModified() == appConnResource.getLastModifiedTime()
                    && zipFile.length() == appConnResource.getSize() && AppConnIndexFileUtils.isLatestIndex(appConnPath, appConnResource.getResource())) {
                LOGGER.info("No necessary to update the AppConn {}, since it's packages has no changes in path {}.", appConnName, appConnPath.getPath());
                return;
            }
        } else {
            // If resource is not exists, this is the first time to upload this AppConn.
            appConnResource = new AppConnResource();
        }
        // At first, upload appConn file to BML
        Resource resource = new Resource();
        InputStream inputStream = null;
        if (appConnResource.getResource() != null) {
            try {
                inputStream = new FileInputStream(zipFile.getPath());

                BmlUpdateResponse response = bmlClient.updateResource(Utils.getJvmUser(), appConnResource.getResource().getResourceId(), zipFile.getPath(),inputStream);
                resource.setResourceId(appConnResource.getResource().getResourceId());
                resource.setVersion(response.version());
            } catch (FileNotFoundException e) {
                throw new AppConnNotExistsErrorException(20351, "AppConn update to bml failed"+e.getMessage());
            } finally {
                IOUtils.closeQuietly(inputStream);
            }
        } else {

            try {
                inputStream = new FileInputStream(zipFile.getPath());
                BmlUploadResponse response = bmlClient.uploadResource(Utils.getJvmUser(), zipFile.getPath(), inputStream);
                resource.setResourceId(response.resourceId());
                resource.setVersion(response.version());
            } catch (FileNotFoundException e) {
                throw new AppConnNotExistsErrorException(20352, "AppConn update to bml failed"+e.getMessage());
            } finally {
                IOUtils.closeQuietly(inputStream);
            }

        }
        resource.setFileName(zipFile.getName());
        // Then, insert into db.
        appConnResource.setLastModifiedTime(appConnPath.lastModified());
        appConnResource.setSize(zipFile.length());
        appConnResource.setResource(resource);
        String resourceStr = AppConnServiceUtils.resourceToString(appConnResource);

        AppConnBean appConnBeanReLoad = new AppConnBean();
        appConnBeanReLoad.setId(appConnBean.getId());
        appConnBeanReLoad.setResource(resourceStr);
        appConnBeanReLoad.setAppConnName(appConnName);
        appConnBeanReLoad.setAppConnClassPath(appConnPath.getPath());
        appConnBeanReLoad.setClassName(appConnBean.getClassName());
        appConnMapper.updateResourceByName(appConnBeanReLoad);
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
        // Finally, reload this AppConn.
        ((AbstractAppConnManager) AppConnManager.getAppConnManager()).reloadAppConn(appConnBeanReLoad);
        LOGGER.info("AppConn {} has updated resource to {}.", appConnName, resourceStr);
    }

    @PreDestroy
    public void destory() {
        IOUtils.closeQuietly(bmlClient);
    }
}
