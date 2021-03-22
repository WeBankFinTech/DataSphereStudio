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

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

import java.util.Map;

/**
 * @author allenlliu
 * @date 2020/11/26 18:00
 */
public interface OrchestratorCreateResponseRef extends ResponseRef {

    /**
     * 返回第三方应用和orchestrator关联的Id
     * @return
     */


    void setOrchestratorId(Long orcId);


    /**
     * 返回第三方应用创建时返回的Content的内容
     * @return
     */
    String getContent();

    void setContent(String content);


    Long getOrchestratorId();


    Long getOrchestratorVersionId();

    void setOrchestratorVersionId(Long versionId);
}
