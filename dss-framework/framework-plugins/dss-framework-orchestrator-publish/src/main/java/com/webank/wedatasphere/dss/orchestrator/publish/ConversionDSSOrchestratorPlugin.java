/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.orchestrator.publish;

import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.label.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.common.protocol.ResponseOperateOrchestrator;
import com.webank.wedatasphere.dss.orchestrator.core.plugin.DSSOrchestratorPlugin;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;
import java.util.Map;


public interface ConversionDSSOrchestratorPlugin extends DSSOrchestratorPlugin {
    /**
     * 编排转化（转为具体调度形式，比如转为schedulis调度工作流）
     * 这里只是个rpc的client简单封装。
     * @param userName 发布人
     * @param project 编排所属项目
     * @param workspace 所属工作空间
     * @param orchestrationIdMap 要发布的编排,key为编排的appId，value为编排的refOrchestrationId
     * @param dssLabels 标签
     * @param approvalId 审批单号，如果不需要，可以传null
     * @return
     */
    ResponseOperateOrchestrator convert(String userName,
                                        DSSProject project,
                                        Workspace workspace,
                                        Map<Long, Long> orchestrationIdMap,
                                        List<DSSLabel> dssLabels,
                                        String approvalId);
}
