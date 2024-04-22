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

package com.webank.wedatasphere.dss.framework.project.service;

import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.framework.project.entity.OrchestratorBatchImportInfo;
import com.webank.wedatasphere.dss.common.entity.BmlResource;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorBaseInfo;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import org.apache.linkis.common.exception.ErrorException;

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
    OrchestratorBaseInfo importOrc(String orchestratorName, String releaseUser, Long projectId, String projectName,
                                   BmlResource bmlResource, DSSLabel dssLabel, String workspaceName, Workspace workspace) throws ErrorException;

    /**
     * 批量导入编排
     * @param userName 导入人用户名
     * @param projectPath 导入的资源目录文件夹路径
     * @param checkCode 校验码
     * @param dssLabel 标签
     * @param workspace 导入的工作空间
     * @return 导入的编排信息
     */
    OrchestratorBatchImportInfo batchImportOrc(String userName, Long projectId, String projectName, String projectPath,
                                               String checkCode, DSSLabel dssLabel, Workspace workspace)throws ErrorException;
}
