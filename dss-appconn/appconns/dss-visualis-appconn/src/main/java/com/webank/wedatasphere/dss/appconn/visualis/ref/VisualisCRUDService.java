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

package com.webank.wedatasphere.dss.appconn.visualis.ref;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.*;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryOperation;

public class VisualisCRUDService implements RefCRUDService {

    DevelopmentService developmentService;

    @Override
    public RefCreationOperation createTaskCreationOperation() {
        VisualisRefCreationOperation creationOperation = new VisualisRefCreationOperation(developmentService);
        return creationOperation;
    }

    @Override
    public RefCopyOperation createRefCopyOperation() {
        return null;
    }

    @Override
    public RefUpdateOperation createRefUpdateOperation() {
        return new VisualisRefUpdateOperation(developmentService);
    }

    @Override
    public RefDeletionOperation createRefDeletionOperation() {
        VisualisRefDeletionOperation deletionOperation = new VisualisRefDeletionOperation(developmentService);
        deletionOperation.setDevelopmentService(developmentService);
        return deletionOperation;
    }

    @Override
    public DevelopmentService getDevelopmentService() {
        return developmentService;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
