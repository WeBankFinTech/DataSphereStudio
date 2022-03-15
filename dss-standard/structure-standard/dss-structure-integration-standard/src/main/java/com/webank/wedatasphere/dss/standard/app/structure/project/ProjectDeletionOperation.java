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
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.RefProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;


public interface ProjectDeletionOperation<R extends RefProjectContentRequestRef<R>>
        extends StructureOperation<R, ResponseRef> {

    /**
     * delete the related refProject in third-party AppConn by refProjectId
     * which returned by ProjectCreationOperation.
     * refProjectId must not be null, please use it to delete the refProject.
     * @param projectRef refProject info, refProjectId must not be null
     * @return the result of deletion, just success or failure.
     * @throws ExternalOperationFailedException
     */
    ResponseRef deleteProject(R projectRef) throws ExternalOperationFailedException;

}
