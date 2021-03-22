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

package com.webank.wedatasphere.dss.framework.project.utils;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

public class ProjectStringUtils {


    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectStringUtils.class);

    private static final String REDIRECT_FORMAT = "%s?redirect=%s&dssurl=${dssurl}&cookies=${cookies}";

    public static final String MODE_SPLIT = ",";
    public static final String KEY_SPLIT = "-";

    public static List<String> convertList(String str){
        if(org.apache.commons.lang.StringUtils.isEmpty(str)){
            return new ArrayList<>();
        }
        return Arrays.stream(str.split(MODE_SPLIT)).map(String::trim).filter((s)-> StringUtils.isNotBlank(s)).distinct().collect(Collectors.toList());
    }


    //拼接 前后使用英文逗号结尾
    public static String getModeStr(List<String> strList){
        if(CollectionUtils.isEmpty(strList)){
            return null;
        }
        return strList.stream().map(String::trim).filter((s)-> StringUtils.isNotBlank(s)).distinct().collect(Collectors.joining(MODE_SPLIT,MODE_SPLIT,MODE_SPLIT));
    }




    private static String URLEndoder(String str){
        try {
            return URLEncoder.encode(str,"utf-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.warn("endoe failed:",e);
            return str;
        }
    }

    public static String redirectUrlFormat(String redirectUrl,String url){
        return String.format(REDIRECT_FORMAT,redirectUrl,URLEndoder(url));
    }


}
