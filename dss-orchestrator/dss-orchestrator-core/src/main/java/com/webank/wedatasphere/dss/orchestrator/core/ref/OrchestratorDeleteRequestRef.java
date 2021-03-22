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

package com.webank.wedatasphere.dss.orchestrator.core.ref;

import com.webank.wedatasphere.dss.standard.app.development.crud.DeleteRequestRef;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;

import java.util.List;

/**
 * @author allenlliu
 * @date 2020/11/27 15:16
 */
public interface OrchestratorDeleteRequestRef extends DeleteRequestRef {
    /**
     * 把当前OrchestratorId设置进去
     * @param orchestratorId
     */
    void setOrchestratorId(Long orchestratorId);

    Long getOrchestratorId();

    /**
     * 检索数据库中关联的第三方应用的id
     * @param appId
     */
    void setAppId(Long appId);

    Long getAppId();

    /**
     * 设置当前操作的用户
     * @param userName
     */
    void setUserName(String userName);

    String getUserName();

    String getWorkspaceName();

    void setWorkspaceName(String workspaceName);

    String getProjectName();
    void setProjectName(String projectName);

    @Override
    List<DSSLabel> getDSSLabels();

    void setDSSLabels(List<DSSLabel> dssLabelList);
}
