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

package com.webank.wedatasphere.dss.standard.app.structure.project;

import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;

/**
 * Created by enjoyyin on 2020/8/10.
 */
public interface ProjectService extends StructureService {

    /**
     * 是否支持DSS与各集成接入系统的协同开发能力
     * @return 默认为false
     */
    default boolean isCooperationSupported() {
        return false;
    }

    default boolean isProjectNameUnique() {
        return true;
    }

    ProjectCreationOperation createProjectCreationOperation();

    ProjectUpdateOperation createProjectUpdateOperation();

    ProjectDeletionOperation createProjectDeletionOperation();

    ProjectUrlOperation createProjectUrlOperation();


}
