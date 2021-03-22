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

package com.webank.wedatasphere.dss.standard.app.development.crud;

import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

public interface RefCRUDService extends RefOperationService {

    <K extends CreateRequestRef, V extends ResponseRef> RefCreationOperation<K, V> createTaskCreationOperation();

    <K extends CopyRequestRef, V extends ResponseRef> RefCopyOperation<K, V> createRefCopyOperation();

    <K extends RequestRef, V extends ResponseRef> RefUpdateOperation<K, V> createRefUpdateOperation();

    <K extends DeleteRequestRef, V extends ResponseRef> RefDeletionOperation<K, V> createRefDeletionOperation();

}
