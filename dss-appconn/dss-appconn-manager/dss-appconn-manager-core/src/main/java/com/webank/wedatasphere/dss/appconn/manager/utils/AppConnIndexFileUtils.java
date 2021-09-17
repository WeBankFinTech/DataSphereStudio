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

package com.webank.wedatasphere.dss.appconn.manager.utils;

import com.webank.wedatasphere.dss.appconn.manager.exception.AppConnIndexFileWarnException;
import com.webank.wedatasphere.dss.common.entity.Resource;
import java.io.File;
import java.util.Arrays;

import static com.webank.wedatasphere.dss.appconn.manager.utils.AppInstanceConstants.*;

public class AppConnIndexFileUtils {

    private static boolean isIndexFile(String fileName) {
        return fileName.startsWith(INDEX_FILE_PREFIX) && fileName.endsWith(INDEX_FILE_SUFFIX);
    }

    public static File getIndexFile(File parent) {
        File[] indexFiles = parent.listFiles((p, fileName) -> isIndexFile(fileName));
        if(indexFiles == null) {
            return null;
        }else if(indexFiles.length > 1) {
            throw new AppConnIndexFileWarnException(20533, "More than one index files exists, indexFile list: " + Arrays.asList(indexFiles));
        } else if(indexFiles.length == 0) {
            return null;
        } else {
            return indexFiles[0];
        }
    }

    public static boolean isLatestIndex(File parent, Resource resource) {
        File indexFile = getIndexFile(parent);
        if(indexFile == null) {
            return false;
        }
        String version = indexFile.getName().replaceAll(INDEX_FILE_PREFIX, "").replaceAll(INDEX_FILE_SUFFIX, "");
        return resource.getVersion().equals(version);
    }

    public static String getIndexFileName(Resource resource) {
        return INDEX_FILE_PREFIX + resource.getVersion() + INDEX_FILE_SUFFIX;
    }

}
