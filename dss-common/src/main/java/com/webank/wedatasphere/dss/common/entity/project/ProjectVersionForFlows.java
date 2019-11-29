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

package com.webank.wedatasphere.dss.common.entity.project;

import com.webank.wedatasphere.dss.common.entity.flow.Flow;

import java.util.List;

/**
 * Created by enjoyyin on 2019/9/26.
 */
public interface ProjectVersionForFlows extends ProjectVersion {

    List<? extends Flow> getFlows();

    void setFlows(List<? extends Flow> flows);

    void addFlow(Flow flow);

    void removeFlow(Flow flow);
}
