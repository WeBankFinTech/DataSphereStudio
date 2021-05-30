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

package com.webank.wedatasphere.dss.framework.release.dao;

import com.webank.wedatasphere.dss.framework.release.entity.orchestrator.OrchestratorReleaseInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * The interface Orchestrator release info mapper.
 *
 * @author yuxin.yuan
 * @date 2021 /05/20
 */
@Mapper
public interface OrchestratorReleaseInfoMapper {

    void insert(OrchestratorReleaseInfo orchestratorReleaseInfo);

    OrchestratorReleaseInfo getByOrchestratorId(Long orchestratorId);

    void update(OrchestratorReleaseInfo orchestratorReleaseInfo);
}
