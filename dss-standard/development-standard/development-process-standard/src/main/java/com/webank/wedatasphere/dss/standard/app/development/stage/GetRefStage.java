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

package com.webank.wedatasphere.dss.standard.app.development.stage;

import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;

import java.util.List;

/**
 * @author allenlliu
 * @date 2020/11/30 15:19
 */
public interface GetRefStage {
    /**
     * 用来获取工作流详细信息，且DSSFlow应当包含其所有子Flow信息
     *
     * @param rootFlowId
     * @return
     */
    DSSFlow getDssFlowById(String userName, Long rootFlowId, List<DSSLabel> dssLabels) throws ExternalOperationFailedException;

    void setDevelopmentService(DevelopmentService service);
}
