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

package com.webank.wedatasphere.dss.standard.app.structure.project;

import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureService;


public abstract class ProjectService extends AbstractStructureService {

    /**
     * 是否支持DSS与各集成接入系统的协同开发能力
     * @return 默认为false
     */
    public boolean isCooperationSupported() {
        return false;
    }

    public boolean isProjectNameUnique() {
        return true;
    }

   public ProjectCreationOperation getProjectCreationOperation() {
       return getOrCreate(this::createProjectCreationOperation, ProjectCreationOperation.class);
   }

   protected abstract ProjectCreationOperation createProjectCreationOperation();

   public ProjectUpdateOperation getProjectUpdateOperation() {
       return getOrCreate(this::createProjectUpdateOperation, ProjectUpdateOperation.class);
   }

   protected abstract ProjectUpdateOperation createProjectUpdateOperation();

   public ProjectDeletionOperation getProjectDeletionOperation() {
       return getOrCreate(this::createProjectDeletionOperation, ProjectDeletionOperation.class);
   }

   protected abstract ProjectDeletionOperation createProjectDeletionOperation();

   public ProjectUrlOperation getProjectUrlOperation() {
       return getOrCreate(this::createProjectUrlOperation, ProjectUrlOperation.class);
   }

   protected abstract ProjectUrlOperation createProjectUrlOperation();

}
