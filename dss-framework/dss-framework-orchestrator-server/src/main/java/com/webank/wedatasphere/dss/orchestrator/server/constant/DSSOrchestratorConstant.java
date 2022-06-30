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

package com.webank.wedatasphere.dss.orchestrator.server.constant;


import com.webank.wedatasphere.dss.orchestrator.publish.job.OrchestratorConversionJob;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DSSOrchestratorConstant {
    public static final String PUBLISH_FLOW_REPORT_FORMATE = "工作流名:%s,版本号:%s，工作流内容为空,请自行修改或者删除";

    public static Map<String, OrchestratorConversionJob> orchestratorConversionJobMap = new ConcurrentHashMap<>();

    public static final int MAX_CLEAR_SIZE = 1000;
}
