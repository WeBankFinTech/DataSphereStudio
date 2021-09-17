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

package com.webank.wedatasphere.dss.standard.common.core;

import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.service.AppService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;


public abstract class AbstractAppIntegrationStandard<Service extends AppIntegrationService, SSORequestService extends AppService>
    implements AppIntegrationStandard<SSORequestService> {

    private final Map<AppInstance, List<Service>> appServices = new HashMap<>();
    private SSORequestService ssoRequestService;

    protected <T extends Service> T getOrCreate(AppInstance appInstance, Supplier<T> create, Class<T> clazz) {
        Supplier<T> createAndPut = () -> {
            T t = create.get();
            if(t == null) {
                return null;
            }
            t.setSSORequestService(ssoRequestService);
            t.setAppInstance(appInstance);
            initService(t);
            appServices.get(appInstance).add(t);
            return t;
        };
        if(!appServices.containsKey(appInstance)) {
            synchronized (appServices) {
                if(!appServices.containsKey(appInstance)) {
                    appServices.put(appInstance, new ArrayList<>());
                    return createAndPut.get();
                }
            }
        }
        final List<Service> services = appServices.get(appInstance);
        Supplier<Optional<T>> filterService = () -> services.stream().filter(clazz::isInstance).findFirst().map(service -> (T) service);
        return filterService.get().orElseGet(() -> {
            synchronized (services) {
                return filterService.get().orElseGet(createAndPut);
            }
        });
    }

    protected <T extends Service> void initService(T service) {}

    @Override
    public void setSSORequestService(SSORequestService ssoRequestService) {
        this.ssoRequestService = ssoRequestService;
    }
}
