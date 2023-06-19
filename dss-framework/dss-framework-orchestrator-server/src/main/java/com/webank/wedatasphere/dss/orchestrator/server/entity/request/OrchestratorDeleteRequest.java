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

package com.webank.wedatasphere.dss.orchestrator.server.entity.request;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement
public class OrchestratorDeleteRequest extends OrchestratorRequest {

    @NotNull(message = "id不能为空")
    private Long id;

    private Boolean deleteSchedulerWorkflow = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getDeleteSchedulerWorkflow() {
        return deleteSchedulerWorkflow;
    }

    public void setDeleteSchedulerWorkflow(Boolean deleteSchedulerWorkflow) {
        this.deleteSchedulerWorkflow = deleteSchedulerWorkflow;
    }

    @Override
    public String toString() {
        return "OrchestratorDeleteRequest{" +
                "id=" + id +
                ", deleteSchedulerWorkflow=" + deleteSchedulerWorkflow +
                '}';
    }
}
