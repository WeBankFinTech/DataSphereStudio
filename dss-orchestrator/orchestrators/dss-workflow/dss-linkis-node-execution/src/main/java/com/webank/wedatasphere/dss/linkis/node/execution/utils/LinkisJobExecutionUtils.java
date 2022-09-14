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

package com.webank.wedatasphere.dss.linkis.node.execution.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.webank.wedatasphere.dss.linkis.node.execution.conf.LinkisJobExecutionConfiguration;
import com.webank.wedatasphere.dss.linkis.node.execution.entity.BMLResource;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class LinkisJobExecutionUtils {

    public final static  Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

    public final static Gson gson1 = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

    public static BMLResource getBMLResourceByJson(String json){
        return gson.fromJson(json, BMLResource.class);
    }

    public static ArrayList<BMLResource> getResourceListByJson(String json){
        if(StringUtils.isEmpty(json)){
            return null;
        }
        return gson.fromJson(json,new TypeToken<List<BMLResource>>() {}.getType());
    }

    public static Boolean isCommonAppConnJob(String engineType){
        return  LinkisJobExecutionConfiguration.APPCONN.equalsIgnoreCase(engineType) || engineType.equalsIgnoreCase("appjoint");
    }

    public static final Integer IDX_FOR_LOG_TYPE_ALL = 3;// 0: Error 1: WARN 2:INFO 3: ALL
}

