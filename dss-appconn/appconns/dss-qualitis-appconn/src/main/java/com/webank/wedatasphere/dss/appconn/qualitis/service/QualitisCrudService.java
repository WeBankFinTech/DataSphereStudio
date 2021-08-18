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

package com.webank.wedatasphere.dss.appconn.qualitis.service;

import com.webank.wedatasphere.dss.appconn.qualitis.ref.operation.QualitisRefCreationOperation;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.operation.QualitisRefDeletionOperation;
import com.webank.wedatasphere.dss.appconn.qualitis.ref.operation.QualitisRefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.service.AbstractRefCRUDService;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;

public class QualitisCrudService extends AbstractRefCRUDService {

    DevelopmentService developmentService;

    @Override
    public RefCreationOperation createRefCreationOperation() {
        QualitisRefCreationOperation creationOperation = new QualitisRefCreationOperation(developmentService);
        return creationOperation;
    }

    @Override
    public RefCopyOperation createRefCopyOperation() {
        return null;
    }

    @Override
    public RefUpdateOperation createRefUpdateOperation() {
        return new QualitisRefUpdateOperation(developmentService);
    }

    @Override
    public RefDeletionOperation createRefDeletionOperation() {
        QualitisRefDeletionOperation deletionOperation = new QualitisRefDeletionOperation(developmentService);
        deletionOperation.setDevelopmentService(developmentService);
        return deletionOperation;
    }

}
