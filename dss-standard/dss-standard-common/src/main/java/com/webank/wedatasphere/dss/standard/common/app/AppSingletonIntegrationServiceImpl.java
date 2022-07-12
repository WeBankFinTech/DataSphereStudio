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

package com.webank.wedatasphere.dss.standard.common.app;

import com.webank.wedatasphere.dss.standard.common.service.AppService;
import com.webank.wedatasphere.dss.standard.common.service.Operation;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


public class AppSingletonIntegrationServiceImpl<O extends Operation, T extends AppService> extends AppIntegrationServiceImpl<T> {

    private final List<O> appOperations = new ArrayList<>();

    protected <TO extends O> TO getOrCreate(Supplier<TO> create, Class<TO> clazz) {
        Supplier<TO> createAndPut = () -> {
            TO t = create.get();
            if(t == null) {
                return null;
            }
            initOperation(t);
            t.init();
            appOperations.add(t);
            return t;
        };
        Supplier<Optional<TO>> filterOperation = () -> appOperations.stream().filter(clazz::isInstance).findFirst().map(operation -> (TO) operation);
        return filterOperation.get().orElseGet(() -> {
            synchronized (appOperations) {
                return filterOperation.get().orElseGet(createAndPut);
            }
        });
    }

    protected void initOperation(O operation) {}

}
