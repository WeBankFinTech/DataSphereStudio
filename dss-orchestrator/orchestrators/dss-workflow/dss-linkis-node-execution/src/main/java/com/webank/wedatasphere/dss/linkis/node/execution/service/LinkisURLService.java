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

package com.webank.wedatasphere.dss.linkis.node.execution.service;

import com.webank.wedatasphere.dss.linkis.node.execution.exception.LinkisJobExecutionWarnException;
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job;
import com.webank.wedatasphere.dss.linkis.node.execution.service.impl.LinkisURLServiceImpl;
import com.webank.wedatasphere.linkis.common.utils.ClassUtils;
import java.util.Optional;


public interface LinkisURLService {

    /**
     * Get Linkis gateway url by job. If multi-Linkis cluster is needed, user can override this method to provide different urls by job.
     * @param job this nodeJob
     * @return a accessiable Linkis gateway url.
     */
    String getLinkisURL(Job job);

    /**
     * Get default Linkis gateway url by job.
     * @param job this nodeJob
     * @return a accessiable Linkis gateway url.
     */
    String getDefaultLinkisURL(Job job);

    class Factory {

        private Factory() {
        }

        private static LinkisURLService linkisURLService;

        public static LinkisURLService getLinkisURLService() {
            if(linkisURLService != null) {
                return linkisURLService;
            }
            synchronized (Factory.class) {
                if(linkisURLService == null) {
                    Optional<Class<? extends LinkisURLService>> clazz = ClassUtils.reflections().getSubTypesOf(LinkisURLService.class).stream().filter(c -> !ClassUtils.isInterfaceOrAbstract(c) &&
                        !LinkisURLServiceImpl.class.isAssignableFrom(c)).findFirst();
                    if(clazz.isPresent()) {
                        try {
                            linkisURLService = clazz.get().newInstance();
                        } catch (Exception e) {
                            throw new LinkisJobExecutionWarnException(24335, "Get LinkisURLService failed!", e);
                        }
                    } else {
                        linkisURLService = new LinkisURLServiceImpl();
                    }
                }
            }
            return linkisURLService;
        }

    }

}
