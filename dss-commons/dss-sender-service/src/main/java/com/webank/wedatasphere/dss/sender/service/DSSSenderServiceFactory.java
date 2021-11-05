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

package com.webank.wedatasphere.dss.sender.service;

import com.webank.wedatasphere.dss.common.utils.ClassUtils;
import com.webank.wedatasphere.dss.sender.service.impl.DSSSenderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author allenlliu
 * @date 2021/6/28 11:41
 */
public class DSSSenderServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSSenderServiceFactory.class);
    private static final DSSSenderService service;

    static {
        service = ClassUtils.getInstanceOrDefault(DSSSenderService.class, new DSSSenderServiceImpl());
        LOGGER.info("Use {} to instance a available DSSSenderService.", service.getClass().getName());
    }


    public static DSSSenderService getOrCreateServiceInstance() {
        return service;
    }


}
