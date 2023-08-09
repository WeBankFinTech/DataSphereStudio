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

package com.webank.wedatasphere.dss.framework.workspace.util;

import org.apache.linkis.common.conf.CommonVars;


public interface DSSWorkspaceConstant {
    String WORKSPACE_ID_STR = "workspaceId";

    CommonVars<String> DEFAULT_WORKSPACE_NAME
            = CommonVars.apply("wds.dss.workspace.default.name", "bdapWorkspace");

    CommonVars<String> DEFAULT_0XWORKSPACE_NAME
            = CommonVars.apply("wds.dss.workspace.0x.default.name", "bdapWorkspace_0X");

    CommonVars<String> DEFAULT_DEMO_WORKSPACE_NAME = CommonVars.apply("wds.dss.default.demo.workspace", "WDS_DSS_DEMO");

    String WORKSPACE_MANAGEMENT_NAME = CommonVars.apply("wds.dss.workspace.management.name", "工作空间管理").getValue();

}
