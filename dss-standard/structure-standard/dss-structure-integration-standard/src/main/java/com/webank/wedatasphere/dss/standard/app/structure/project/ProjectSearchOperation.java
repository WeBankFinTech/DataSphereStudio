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

import com.webank.wedatasphere.dss.standard.app.structure.StructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public interface ProjectSearchOperation<R extends RefProjectContentRequestRef<R>>
        extends StructureOperation<R, ProjectResponseRef> {

    /**
     * Try to search the project by provide refProjectId or projectName.
     * If refProjectId is not null, then the third-part AppConn should use refProjectId to search the refProject;
     * otherwise, the third-part AppConn should use projectName to search the refProject.
     * <br>
     * If the refProject is exists, please return a ProjectResponseRef which has been set in the refProjectId;
     * otherwise, just return an empty succeeded ProjectResponseRef.
     * @param projectRef the refProject info
     * @return return a ProjectResponseRef contained refProjectId if the refProject is exists, otherwise
     * just return an empty succeeded ProjectResponseRef.
     * @throws ExternalOperationFailedException If some error are happened.
     */
    ProjectResponseRef searchProject(R projectRef) throws ExternalOperationFailedException;

}
