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
import com.webank.wedatasphere.dss.framework.project.entity.response.BatchExportResult;
import com.webank.wedatasphere.dss.framework.project.entity.response.ExportResult;
import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.server.entity.vo.OrchestratorBaseInfo;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import org.apache.linkis.common.exception.ErrorException;

import java.util.List;

/**
 * created by cooperyang on 2020/12/9
 * Description: 专门使用导出的
 */
public interface ExportService {

    /**
     * 导出一个编排到bml
     * @param releaseUser 导出人
     * @param projectId 项目id
     * @param projectName 项目名
     * @param dssLabel 标签
     * @param workspace 工作孔家
     * @return 编排的导出结果
     * @throws ErrorException
     */
    ExportResult export(String releaseUser, Long projectId, OrchestratorInfo orchestrator,
                        String projectName, DSSLabel dssLabel, Workspace workspace) throws ErrorException;

    /**
     * 批量导出编排,打包到一起生成一个bml文件
     * @param orchestrators 需要导出的编排们
     */
    BatchExportResult batchExport(String userName, Long projectId, List<OrchestratorBaseInfo> orchestrators,
                                  String projectName, DSSLabel dssLabel, Workspace workspace) throws ErrorException;



    Long addVersionAfterPublish(String releaseUser, Long projectId, Long orchestratorId, Long orchestratorVersionId,
                        String projectName, Workspace workspace, DSSLabel dssLabel,String comment) throws ErrorException;

}
