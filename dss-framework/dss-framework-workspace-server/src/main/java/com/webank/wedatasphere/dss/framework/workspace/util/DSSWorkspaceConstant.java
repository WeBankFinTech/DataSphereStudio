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

package com.webank.wedatasphere.dss.framework.workspace.util;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * created by cooperyang on 2020/3/15
 * Description:
 */
public interface DSSWorkspaceConstant {
    public static final String WORKSPACE_ID_STR = "workspaceId";

    public static final CommonVars<String> DEFAULT_WORKSPACE_NAME
            = CommonVars.apply("wds.dss.workspace.default.name", "bdapWorkspace");

    public static final String COMMON_URL_PREFIX = "/workspaceHome/common/";

    CommonVars<String> DEFAULT_DEMO_WORKSPACE_NAME = CommonVars.apply("wds.dss.default.demo.workspace", "WDS_DSS_DEMO");

}
