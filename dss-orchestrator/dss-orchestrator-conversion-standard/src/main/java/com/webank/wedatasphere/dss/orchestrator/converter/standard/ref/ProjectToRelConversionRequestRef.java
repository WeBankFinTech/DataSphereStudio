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

package com.webank.wedatasphere.dss.orchestrator.converter.standard.ref;

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestration;
import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import java.util.List;
import java.util.Map;


public interface ProjectToRelConversionRequestRef<R extends ProjectToRelConversionRequestRef<R>>
        extends DSSToRelConversionRequestRef<R> {

    default List<DSSOrchestration> getDSSOrcList() {
        return (List<DSSOrchestration>) getParameter("dssOrcList");
    }

    default R setDSSOrcList(List<DSSOrchestration> dssOrcList) {
        setParameter("dssOrcList", dssOrcList);
        return (R) this;
    }

    /**
     * 该 Map 会返回 DSS 编排，与 SchedulerAppConn（调度系统）工作流的一一对应关系，也有可能返回 null。
     * 其中：key 为 DSS 编排名，value 为 SchedulerAppConn 工作流的 id。
     * 如何判断是否为空？第三方 SchedulerAppConn 如果实现了 OrchestrationService，则必定会存在，否则为空。
     * 具体请参考 OrchestrationService 的类注释。
     * @return 返回 DSS 编排与 SchedulerAppConn（调度系统）工作流的一一对应关系。
     */
    default Map<String, Long> getRefOrchestrationIdMap() {
        return (Map<String, Long>) getParameter("refOrchestrationIdMap");
    }

    default R setRefOrchestrationId(Map<String, Long> refOrchestrationIdMap) {
        setParameter("refOrchestrationIdMap", refOrchestrationIdMap);
        return (R) this;
    }

}
