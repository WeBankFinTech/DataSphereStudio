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

package com.webank.wedatasphere.dss.standard.app.development.service;

import com.webank.wedatasphere.dss.standard.app.development.operation.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CopyRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.CreateRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.DeleteRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;

public abstract class AbstractRefCRUDService extends AbstractDevelopmentService implements RefCRUDService {

    protected abstract <K extends CreateRequestRef> RefCreationOperation<K> createRefCreationOperation();

    protected abstract <K extends CopyRequestRef> RefCopyOperation<K> createRefCopyOperation();

    protected abstract <K extends RequestRef> RefUpdateOperation<K> createRefUpdateOperation();

    protected abstract <K extends DeleteRequestRef> RefDeletionOperation<K> createRefDeletionOperation();

    @Override
    public <K extends CreateRequestRef> RefCreationOperation<K> getRefCreationOperation() {
        return getOrCreate(this::createRefCreationOperation, RefCreationOperation.class);
    }

    @Override
    public <K extends CopyRequestRef> RefCopyOperation<K> getRefCopyOperation() {
         return getOrCreate(this::createRefCopyOperation, RefCopyOperation.class);
    }

    @Override
    public <K extends RequestRef> RefUpdateOperation<K> getRefUpdateOperation() {
        return getOrCreate(this::createRefUpdateOperation, RefUpdateOperation.class);
    }

    @Override
    public <K extends DeleteRequestRef> RefDeletionOperation<K> getRefDeletionOperation() {
        return getOrCreate(this::createRefDeletionOperation, RefDeletionOperation.class);
    }

}
