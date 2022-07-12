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
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectUpdateRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;


public interface ProjectUpdateOperation<R extends ProjectUpdateRequestRef<R>>
        extends StructureOperation<R, ResponseRef> {

    /**
     * Try to update the related refProject in third-party AppConn.
     * Usually, DSS only want to update the projectName, description and permissions info in third-party refProject,
     * so the refProjectId is always exists and can not be changeable.
     * <br>
     * Notice: do not try to change the refProjectId already related with the third-party refProject.
     * @param projectRef contains the DSS project info updated.
     * @return the result of update, just success or failure.
     * @throws ExternalOperationFailedException
     */
    ResponseRef updateProject(R projectRef) throws ExternalOperationFailedException;

}
