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

package com.webank.wedatasphere.dss.framework.release.service;

import com.webank.wedatasphere.dss.framework.release.entity.export.ExportResult;
import com.webank.wedatasphere.dss.framework.release.entity.resource.BmlResource;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

import java.util.List;
import java.util.Map;

/**
 * created by cooperyang on 2020/12/9
 * Description: 专门使用导出的
 */
public interface ExportService {


    ExportResult export(String releaseUser, Long projectId, Long orchestratorId, Long orchestratorVersionId,
                        String projectName, String workspaceName, DSSLabel dssLabel, Workspace workspace) throws ErrorException;


    List<BmlResource> export(String releaseUser, Long projectId, Map<Long, Long> orchestratorInfoMap, DSSLabel dssLabel) throws ErrorException;


}
