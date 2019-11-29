/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.server.operate;


import com.webank.wedatasphere.dss.server.service.DWSFlowService;
import com.webank.wedatasphere.dss.common.entity.flow.DWSFlow;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;


@Component
public class SubFlowOperate implements Operate {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String nodeType = "workflow.subflow";

    @Override
    public boolean canInvokeOperate(Op op) {
        return this.nodeType.equals(op.getNodeType());
    }

    @Override
    public void add(DWSFlowService dwsFlowService, Op op)throws DSSErrorException {
        afterOperateSubFlow(dwsFlowService,op);
    }

    @Override
    public void update(DWSFlowService dwsFlowService,Op op) throws DSSErrorException {
        logger.info("name:{},description:{}",op.getParams().get("name"),op.getParams().get("description"));
        DWSFlow dwsFlow = new DWSFlow();
        dwsFlow.setId(op.getId());
        dwsFlow.setName(op.getParams().get("name").toString());
        dwsFlow.setDescription(op.getParams().get("description").toString());
        dwsFlowService.updateFlowBaseInfo(dwsFlow, Long.valueOf(op.getParams().get("projectVersionID").toString()), null);
        afterOperateSubFlow(dwsFlowService,op);
    }

    @Override
    public void delete(DWSFlowService dwsFlowService,Op op) throws DSSErrorException {
        logger.info("delete subFlow{}",op.getId());
        dwsFlowService.batchDeleteFlow(Arrays.asList(op.getId()), Long.valueOf(op.getParams().get("projectVersionID").toString()));
        afterOperateSubFlow(dwsFlowService,op);
    }

    private void afterOperateSubFlow(DWSFlowService dwsFlowService,Op op) throws DSSErrorException {
        //更新工作流基本信息
        DWSFlow dwsFlow = new DWSFlow();
        dwsFlow.setId(op.getId());
        dwsFlow.setHasSaved(true);
        dwsFlowService.updateFlowBaseInfo(dwsFlow, Long.valueOf(op.getParams().get("projectVersionID").toString()), null);
    }
}
