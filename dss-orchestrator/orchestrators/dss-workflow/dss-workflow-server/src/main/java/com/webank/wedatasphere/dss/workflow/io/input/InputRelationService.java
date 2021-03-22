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

package com.webank.wedatasphere.dss.workflow.io.input;


import com.webank.wedatasphere.dss.common.entity.IOEnv;

public interface InputRelationService {

    boolean projectIsFirstInput(Long sourceProjectID, IOEnv sourceEnv);

    boolean flowIsFirstInput(Long sourceFlowID, IOEnv sourceEnv);

    void insertProjectInputRelation(Long sourceProjectID, IOEnv sourceEnv, Long targetProjectID);

    void insertFlowInputRelation(Long sourceFlowID, IOEnv sourceEnv, Long targetFlowID);

    Long getProjectTargetID(Long sourceProjectID, IOEnv sourceEnv);

    Long getFlowTargetID(Long sourceFlowID, IOEnv sourceEnv);

    void removeFlowInputRelation(IOEnv sourceEnv, Long targetFlowID);
}
