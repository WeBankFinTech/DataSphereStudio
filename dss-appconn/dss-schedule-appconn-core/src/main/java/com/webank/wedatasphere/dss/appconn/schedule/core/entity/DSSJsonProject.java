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

package com.webank.wedatasphere.dss.appconn.schedule.core.entity;

import com.webank.wedatasphere.dss.standard.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSJsonFlow;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by allenlliu on 2019/9/16.
 */
public class DSSJsonProject extends DSSProject {
    private List<DSSJsonFlow> flows;


    public List<DSSJsonFlow> getFlows() {
        return this.flows;
    }


    public void setFlows(List<? extends DSSFlow> flows) {
        this.flows = flows.stream().map(f ->(DSSJsonFlow)f).collect(Collectors.toList());
    }
}
