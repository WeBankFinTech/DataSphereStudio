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

import com.webank.wedatasphere.dss.framework.release.entity.orchestrator.OrchestratorReleaseInfo;
import com.webank.wedatasphere.dss.framework.release.entity.project.ProjectInfo;
import com.webank.wedatasphere.dss.framework.release.entity.resource.BmlResource;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;

import java.util.List;

/**
 * created by cooperyang on 2020/12/9
 * Description: 导入的操作都在此处进行
 * 导入需要携带
 * 1.工程需要的zip包，应该是是需要bml的resourceid和version就可以
 */
public interface ImportService {


    /**
     * 通过发布的导入,只需要将resourceId和version给到nextLabel的OrcFramework
     */
    List<OrchestratorReleaseInfo> importOrc(String orchestratorName, String releaseUser, ProjectInfo projectInfo,
                                            List<BmlResource> bmlResourceList, DSSLabel dssLabel, String workspaceName, Workspace workspace) throws ErrorException;


    /**
     * 如BDP的导入,通过导入的rest接口进入，此时应该是携带了所有的信息
     */
    void importOrc(ProjectInfo projectInfo);


}
