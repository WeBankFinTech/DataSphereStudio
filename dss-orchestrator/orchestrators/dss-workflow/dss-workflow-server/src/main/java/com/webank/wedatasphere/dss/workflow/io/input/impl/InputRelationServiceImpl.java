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

package com.webank.wedatasphere.dss.workflow.io.input.impl;

import com.webank.wedatasphere.dss.common.entity.IOEnv;
import com.webank.wedatasphere.dss.common.entity.IOType;
import com.webank.wedatasphere.dss.common.entity.InputRelation;
import com.webank.wedatasphere.dss.common.utils.IoUtils;
import com.webank.wedatasphere.dss.workflow.dao.InputRelationMapper;
import com.webank.wedatasphere.dss.workflow.io.input.InputRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InputRelationServiceImpl implements InputRelationService {

    @Autowired
    private InputRelationMapper inputRelationMapper;

    @Override
    public boolean projectIsFirstInput(Long sourceProjectID, IOEnv sourceEnv) {
        InputRelation inputRelation = inputRelationMapper.selectInputRelation(IOType.PROJECT.name(), sourceEnv, sourceProjectID, IoUtils.getDSSServerEnv());
        return inputRelation == null;
    }

    @Override
    public boolean flowIsFirstInput(Long sourceFlowID, IOEnv sourceEnv) {
        InputRelation inputRelation = inputRelationMapper.selectInputRelation(IOType.FLOW.name(), sourceEnv, sourceFlowID, IoUtils.getDSSServerEnv());
        return inputRelation == null;
    }

    @Override
    public void insertProjectInputRelation(Long sourceProjectID, IOEnv sourceEnv, Long targetProjectID) {
        InputRelation inputRelation = buildInputRelation(sourceProjectID, sourceEnv, targetProjectID, IOType.PROJECT.name());
        inputRelationMapper.insertInputRelation(inputRelation);
    }

    @Override
    public void insertFlowInputRelation(Long sourceFlowID, IOEnv sourceEnv, Long targetFlowID) {
        InputRelation inputRelation = buildInputRelation(sourceFlowID, sourceEnv, targetFlowID, IOType.FLOW.name());
        inputRelationMapper.insertInputRelation(inputRelation);
    }

    @Override
    public Long getProjectTargetID(Long sourceProjectID, IOEnv sourceEnv) {
        return inputRelationMapper.selectInputRelation(IOType.PROJECT.name(),sourceEnv,sourceProjectID,IoUtils.getDSSServerEnv()).getTargetID();
    }

    @Override
    public Long getFlowTargetID(Long sourceFlowID, IOEnv sourceEnv) {
        InputRelation inputRelation = inputRelationMapper.selectInputRelation(IOType.FLOW.name(), sourceEnv, sourceFlowID, IoUtils.getDSSServerEnv());
        if(inputRelation == null) return null ;//新增的子flowID查询会为null
        return inputRelation.getTargetID();
    }

    @Override
    public void removeFlowInputRelation(IOEnv sourceEnv, Long targetFlowID) {
        inputRelationMapper.removeInputRelation(IOType.FLOW.name(),sourceEnv,targetFlowID,IoUtils.getDSSServerEnv());
    }

    public InputRelation buildInputRelation(Long sourceID, IOEnv sourceEnv, Long targetID, String type) {
        InputRelation inputRelation = new InputRelation();
        inputRelation.setSourceEnv(sourceEnv);
        inputRelation.setTargetEnv(IoUtils.getDSSServerEnv());
        inputRelation.setSourceID(sourceID);
        inputRelation.setTargetID(targetID);
        inputRelation.setType(type);
        return inputRelation;
    }


}
