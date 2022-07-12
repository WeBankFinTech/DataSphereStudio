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

import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryJumpUrlOperation;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;


public abstract class AbstractRefQueryService extends AbstractDevelopmentService implements RefQueryService {

    protected abstract RefQueryJumpUrlOperation createRefQueryOperation();

    @Override
    public RefQueryJumpUrlOperation getRefQueryOperation() {
        return getOrCreate(this::createRefQueryOperation, RefQueryJumpUrlOperation.class);
    }

    @Override
    public RefQueryOperation getExtraRefQueryOperation(Class<RefQueryOperation> clazz) {
        return getOrCreate(() -> createExtraRefQueryOperation(clazz), clazz);
    }

    protected RefQueryOperation createExtraRefQueryOperation(Class<RefQueryOperation> clazz) {
        return null;
    }
}
