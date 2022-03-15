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

package com.webank.wedatasphere.dss.appconn.visualis.project;

import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectSearchOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;

public class VisualisProjectService extends ProjectService {

    @Override
    public boolean isCooperationSupported() {
        return false;
    }

    @Override
    public boolean isProjectNameUnique() {
        return true;
    }

    @Override
    protected VisualisProjectCreationOperation createProjectCreationOperation() {
        return new VisualisProjectCreationOperation();
    }

    @Override
    protected ProjectUpdateOperation createProjectUpdateOperation() {
        return null;
    }

    @Override
    protected ProjectDeletionOperation createProjectDeletionOperation() {
        return null;
    }

    @Override
    protected ProjectSearchOperation createProjectSearchOperation() {
        return new VisualisProjectSearchOperation();
    }

}
