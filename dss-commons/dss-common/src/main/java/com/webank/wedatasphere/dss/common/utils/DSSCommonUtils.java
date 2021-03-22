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

package com.webank.wedatasphere.dss.common.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @Author alexyang
 * @Date 2020/3/16
 */
public class DSSCommonUtils {

    public static final String FLOW_RESOURCE_NAME = "resources";

    public static final String FLOW_EDGES_NAME = "edges";

    public static final String FLOW_PARENT_NAME = "parent";


    public static final String NODE_RESOURCE_NAME = "resources";

    public static final String FLOW_NODE_NAME = "nodes";

    public static final String FLOW_PROP_NAME = "props";

    public static final String NODE_PROP_NAME = "params";

    public static final String NODE_PROP_VARIABLE_NAME = "variable";

    public static final String NODE_ID_NAME = "id";

    public static final String NODE_NAME_NAME = "title";

    public static final String FLOW_CONTEXT_ID_PREFIX = "dss.context.id.";

    public static final Gson COMMON_GSON = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

    
}
