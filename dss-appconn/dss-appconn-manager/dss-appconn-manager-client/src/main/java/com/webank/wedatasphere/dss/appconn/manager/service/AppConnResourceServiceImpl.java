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
import com.webank.wedatasphere.dss.appconn.manager.AppConnManager;
import com.webank.wedatasphere.dss.appconn.manager.entity.AppConnInfo;
import com.webank.wedatasphere.dss.appconn.manager.exception.AppConnHomeNotExistsWarnException;
import com.webank.wedatasphere.dss.appconn.manager.impl.AbstractAppConnManager;
import com.webank.wedatasphere.dss.appconn.manager.utils.AppConnIndexFileUtils;
import com.webank.wedatasphere.dss.common.entity.Resource;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.ZipHelper;
import com.webank.wedatasphere.linkis.bml.client.BmlClient;
import com.webank.wedatasphere.linkis.bml.client.BmlClientFactory;
import com.webank.wedatasphere.linkis.common.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConnResourceServiceImpl implements AppConnResourceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConnResourceServiceImpl.class);

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
        if(!appConnPath.exists() && !appConnPath.mkdir()) {
            throw new AppConnHomeNotExistsWarnException(20350, "Cannot create dir " + appConnPath.getPath() + " for AppConn " + appConnName);
        }
        if(AppConnIndexFileUtils.isLatestIndex(appConnPath, resource)) {
            return appConnPath.getPath();
        }
        LOGGER.info("Try to download latest resource {} in version {} from BML for AppConn {}.", resource.getResourceId(),
            resource.getVersion(), appConnName);
        // At first, Download AppConn files from bml.
        String zipFilePath = new File(appConnHomePath, appConnName + "_" + resource.getVersion() + ".zip").getPath();
        bmlClient.downloadResource(Utils.getJvmUser(), resource.getResourceId(), resource.getVersion(),
            "file://" + zipFilePath, true);
        // Then, try to unzip it.
        if(!DeleteAppConnDir(appConnPath)) {
            throw new AppConnHomeNotExistsWarnException(20350, "Cannot delete dir " + appConnPath.getPath() + " for AppConn " + appConnName);
        }
        try {
            ZipHelper.unzip(zipFilePath);
        } catch (DSSErrorException e) {
            throw new AppConnHomeNotExistsWarnException(20350, "Unzip " + zipFilePath + " failed, AppConn " + appConnName, e);
        }

        File oldIndexFile = AppConnIndexFileUtils.getIndexFile(appConnPath);

        // update index file.
        if (oldIndexFile != null && !oldIndexFile.delete()) {
            throw new AppConnHomeNotExistsWarnException(20350, "Delete index file " + oldIndexFile.getName() + " failed, please ensure the permission is all right.");
        }

//      TODO  ZipUtils.fileToUnzip(zipFilePath, appConnHomePath.getPath());
        // Only reserve latest 2 version files.
        File[] historyZipFiles = appConnHomePath.listFiles((p, fileName) -> fileName.startsWith(appConnName) && fileName.endsWith(".zip"));
        if(historyZipFiles.length > 2) {
            List<File> files = Arrays.stream(historyZipFiles).sorted().collect(Collectors.toList());
            // ignore delete failed.
            IntStream.range(0, files.size() - 2).forEach(index -> files.get(index).delete());
        }
        // Finally, reload appconn and write index file.
        Path indexFile = Paths.get(appConnPath.getPath(), AppConnIndexFileUtils.getIndexFileName(resource));
        try {
            Files.createFile(indexFile);
        } catch (IOException e) {
            throw new AppConnHomeNotExistsWarnException(20350, "Cannot create index file " + indexFile.toFile().getPath() + " for AppConn "
                + appConnName, e);
        }
        ((AbstractAppConnManager) AppConnManager.getAppConnManager()).reloadAppConn(appConnInfo);
        return appConnPath.getPath();
    }

    public boolean DeleteAppConnDir(File f){
        if(f.isDirectory()){
            File[] files = f.listFiles();
            for (File key : files) {
                if(key.isFile()){
                    key.delete();
                }else{
                    DeleteAppConnDir(key);
                }
            }
        }
        return f.delete();
    }

}
