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

package com.webank.wedatasphere.dss.workflow.io.export;

import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/03/04 03:31 PM
 */
public interface WorkFlowExportService {

    String exportFlowInfo(Long dssProjectId, String projectName, long rootFlowId, String userName, Workspace workspace) throws Exception;

    void exportFlowResources(String userName, Long projectId, String projectSavePath, String flowJson, String flowName, Workspace workspace) throws Exception;

    String downloadFlowJsonFromBml(String userName, String resourceId, String version, String savePath);
}
