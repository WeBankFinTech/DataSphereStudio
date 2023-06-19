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

package com.webank.wedatasphere.dss.appconn.manager.service;

import com.webank.wedatasphere.dss.appconn.loader.utils.AppConnUtils;
import com.webank.wedatasphere.dss.appconn.manager.entity.AppConnInfo;
import com.webank.wedatasphere.dss.appconn.manager.exception.AppConnHomeNotExistsWarnException;
import com.webank.wedatasphere.dss.appconn.manager.utils.AppConnIndexFileUtils;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.bml.client.BmlClient;
import org.apache.linkis.bml.client.BmlClientFactory;
import org.apache.linkis.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.webank.wedatasphere.dss.appconn.manager.conf.AppConnManagerClientConfiguration.APPCONN_WAIT_MAX_TIME;

public class AppConnResourceServiceImpl implements AppConnResourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConnResourceServiceImpl.class);
    private final Map<String, Boolean> appConnIsLoaded = new HashMap<>();

    private BmlClient bmlClient = BmlClientFactory.createBmlClient();

    @Override
    public String getAppConnHome(AppConnInfo appConnInfo) {
        File appConnHomePath = new File(AppConnUtils.getAppConnHomePath());
        String appConnName = appConnInfo.getAppConnName();
        if(!appConnHomePath.exists()) {
            throw new AppConnHomeNotExistsWarnException(20350, "AppConn home path " + appConnHomePath.getPath() + " not exists.");
        } else if (!appConnHomePath.isDirectory()) {
            throw new AppConnHomeNotExistsWarnException(20350, "AppConn home path " + appConnHomePath.getPath() + " is not a directory.");
        }
        File appConnPath = new File(appConnHomePath, appConnName);
        Resource resource = appConnInfo.getAppConnResource();
        Supplier<Boolean> isLatest = () -> appConnPath.exists() && appConnPath.isDirectory()
                && AppConnIndexFileUtils.isLatestIndex(appConnPath, resource);
        if(isLatest.get()) {
            if(!appConnIsLoaded.containsKey(appConnName)) {
                synchronized (appConnIsLoaded) {
                    if(!appConnIsLoaded.containsKey(appConnName)) {
                        appConnIsLoaded.put(appConnName, true);
                        LOGGER.warn("AppConn {} is newest, no necessary to reload it, just use it.", appConnName);
                    }
                }
            }
            return appConnPath.getPath();
        }
        File zipFilePath = new File(appConnHomePath, appConnName + "_" + resource.getVersion() + ".zip");
        if(zipFilePath.exists()) {
            long startTime = System.currentTimeMillis();
            LOGGER.warn("I found the {} is exists, maybe another service is loading the AppConn {}, I will try to wait for {} at max.",
                    zipFilePath, appConnName, APPCONN_WAIT_MAX_TIME.getValue().toString());
            while(!isLatest.get()) {
                Utils.sleepQuietly(3000);
                if(System.currentTimeMillis() - startTime >= APPCONN_WAIT_MAX_TIME.getValue().toLong()) {
                    break;
                }
            }
            if(isLatest.get()) {
                LOGGER.warn("AppConn {} is loaded by another service, now just use it.", appConnName);
                synchronized (appConnIsLoaded) {
                    appConnIsLoaded.put(appConnName, true);
                }
                return appConnPath.getPath();
            } else {
                LOGGER.warn("Since waited for {}, the AppConn {} has not been loaded by others, now I will try to load it by myself.", APPCONN_WAIT_MAX_TIME.getValue().toString(), appConnName);
                deleteFile(zipFilePath, "Delete the zip file " + zipFilePath.getName() + " of AppConn " + appConnName +  " failed");
            }
        }
        try {
            Files.createFile(zipFilePath.toPath());
        } catch (IOException e) {
            throw new AppConnHomeNotExistsWarnException(20350, "Cannot create zip file " + zipFilePath.getPath() + " for AppConn "
                    + appConnName, e);
        }
        LOGGER.warn("Try to load AppConn {}......", appConnName);
        LOGGER.info("First, download latest resource {} in version {} from BML for AppConn {}, and write it into file {}.", resource.getResourceId(),
            resource.getVersion(), appConnName, zipFilePath);
        // At first, Download AppConn files from bml.
        bmlClient.downloadResource(Utils.getJvmUser(), resource.getResourceId(), resource.getVersion(),
            "file://" + zipFilePath.getPath(), true);
        // Then, try to unzip it.
        if(appConnPath.exists()) {
            try {
                FileUtils.deleteDirectory(appConnPath);
            } catch (IOException e) {
                throw new AppConnHomeNotExistsWarnException(20350, "Cannot delete dir " + appConnPath.getPath() + " for AppConn " + appConnName, e);
            }
        }
        LOGGER.info("Then, unzip the latest resource file {}.", zipFilePath);
        try {
            ZipHelper.unzip(zipFilePath.getPath());
        } catch (DSSErrorException e) {
            throw new AppConnHomeNotExistsWarnException(20350, "Unzip " + zipFilePath + " failed, AppConn is " + appConnName, e);
        }

        File oldIndexFile = AppConnIndexFileUtils.getIndexFile(appConnPath);
        // delete old index file.
        if (oldIndexFile != null) {
            LOGGER.info("Thirdly, delete the old index file {} for AppConn {}.", oldIndexFile, appConnName);
            deleteFile(oldIndexFile, "Delete the index file " + oldIndexFile.getName() + " of AppConn " + appConnName +  " failed");
        }
        // create new index file.
        Path indexFile = Paths.get(appConnPath.getPath(), AppConnIndexFileUtils.getIndexFileName(resource));
        LOGGER.info("Finally, create the latest index file {} for AppConn {}.", indexFile.toFile(), appConnName);
        try {
            Files.createFile(indexFile);
        } catch (IOException e) {
            throw new AppConnHomeNotExistsWarnException(20350, "Cannot create index file " + indexFile.toFile().getPath() + " for AppConn "
                + appConnName, e);
        }
        synchronized (appConnIsLoaded) {
            appConnIsLoaded.put(appConnName, true);
        }
        // Only reserve latest 2 version files.
        File[] historyZipFiles = appConnHomePath.listFiles((p, fileName) -> fileName.startsWith(appConnName) && fileName.endsWith(".zip"));
        if(historyZipFiles != null && historyZipFiles.length > 2) {
            List<File> files = Arrays.stream(historyZipFiles).sorted().collect(Collectors.toList());
            // ignore the failed deletion.
            IntStream.range(0, files.size() - 2).forEach(index -> deleteFile(files.get(index), null));
        }
        LOGGER.warn("AppConn {} is loaded.", appConnName);
        return appConnPath.getPath();
    }

    private void deleteFile(File file, String errorMsg) {
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            if(StringUtils.isNotEmpty(errorMsg)) {
                throw new AppConnHomeNotExistsWarnException(20350, errorMsg + ". Please ensure the permission is all right.", e);
            }
        }
    }

}
