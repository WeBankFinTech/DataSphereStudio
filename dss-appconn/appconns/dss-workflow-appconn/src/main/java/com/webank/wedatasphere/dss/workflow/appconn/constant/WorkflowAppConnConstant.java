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

package com.webank.wedatasphere.dss.workflow.appconn.constant;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * @author allenlliu
 * @date 2020/11/17 16:42
 */
public class WorkflowAppConnConstant {
    public static final CommonVars<String> DSS_WORKFLOW_APPLICATION_NAME = CommonVars.apply("wds.dss.workflow.module.application.name", "WorkFlow");

    public static final CommonVars<String> DSS_WORKFLOW_APPLICATION_NAME_PROD =
            CommonVars.apply("wds.dss.workflow.application.name.prod", "dss-workflow-server-prod");

    public static final CommonVars<String> DSS_SCHEDULE_APPCONN_NAME = CommonVars.apply(
        "wds.dss.schedule.module.appconn.name", "dolphinscheduler");
}
