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

package com.webank.wedatasphere.dss.orchestrator.core.utils;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrchestratorUtils {

    public static final String MODE_SPLIT = ",";

    public static String generateNewVersion() {
        return "v000001";
    }

    public static String generateNewCopyVersion(String suffix) {
        return suffix + "_v000001";
    }

    /**
     * 注意： flow版本更新需要同步更新ContextID
     *
     * @param oldVersion
     * @return
     */
    public static String increaseVersion(String oldVersion) {
        if (oldVersion.length() <= 7) {
            return newNormalVersion(oldVersion);
        } else {
            int i = oldVersion.lastIndexOf("_");
            String newVer = newNormalVersion(oldVersion.substring(i + 1));
            return oldVersion.substring(0, i + 1) + newVer;
        }
    }

    static String newNormalVersion(String oldVersion) {
        long num = Long.parseLong(oldVersion.substring(1)) + 1;
        String tmp = "00000" + num;
        return "v" + tmp.substring(tmp.length() - 6);
    }

    //拼接 前后使用英文逗号结尾
    public static String getModeStr(List<String> strList) {
        if (CollectionUtils.isEmpty(strList)) {
            return null;
        }
        return strList.stream().map(String::trim).filter((s) -> StringUtils.isNotBlank(s)).distinct().collect(Collectors.joining(MODE_SPLIT, MODE_SPLIT, MODE_SPLIT));
    }

    public static List<String> convertList(String str) {
        if (StringUtils.isEmpty(str)) {
            return new ArrayList<>();
        }
        return Arrays.stream(str.split(MODE_SPLIT)).map(String::trim).filter((s) -> StringUtils.isNotBlank(s)).distinct().collect(Collectors.toList());
    }
}
