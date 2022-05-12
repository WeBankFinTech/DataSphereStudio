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

package com.webank.wedatasphere.dss.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class FileHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileHelper.class);

    public static boolean checkDirExists(String dir) {
        File file = new File(dir);
        return file.exists() && file.isDirectory();
    }

    /**
     * 获取某个文件夹路径下所有文件的全路径名
     *
     * @param path         文件夹路径
     * @param fileNameList 所有文件全路径名称
     */
    public static void getAllFileNames(String path, List<String> fileNameList) {
        File file = new File(path);
        File[] files = file.listFiles();
        String[] names = file.list();
        if (names != null) {
            String[] completeNames = new String[names.length];
            for (int i = 0; i < names.length; i++) {
                completeNames[i] = path + names[i];
            }
            fileNameList.addAll(Arrays.asList(completeNames));
        }
        for (File tmpFile : files) {
            //如果文件夹下有子文件夹，获取子文件夹下的所有文件全路径。
            if (tmpFile.isDirectory()) {
                getAllFileNames(tmpFile.getAbsolutePath() + File.separator, fileNameList);
            }
        }
    }

    /**
     * 读取文本文件
     *
     * @param filename 文件全路径
     */
    public static String readFile(String filename) {
        StringBuffer fileContent = new StringBuffer();
        try (FileInputStream fis = new FileInputStream(filename);
             BufferedInputStream bufferedInput = new BufferedInputStream(fis)) {
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = bufferedInput.read(buffer)) != -1) {
                String tmpContent = new String(buffer, 0, bytesRead);
                fileContent.append(tmpContent);
            }
        } catch (FileNotFoundException ex) {
            LOGGER.error("，文件路径不存在，文件路径：{}", filename, ex);
        } catch (IOException e) {
            LOGGER.error("读取文件失败，文件路径：{}", filename, e);
        }
        return fileContent.toString();
    }
}