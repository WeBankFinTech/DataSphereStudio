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
import com.webank.wedatasphere.dss.sender.service.conf.DSSSenderServiceConf;
import com.webank.wedatasphere.dss.sender.service.impl.DSSSenderServiceImpl;
import com.webank.wedatasphere.dss.sender.service.impl.DSSServerSenderServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DSSSenderServiceFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DSSSenderServiceFactory.class);
    private static final DSSSenderService service;

    static {
        if (DSSSenderServiceConf.USE_DSS_SENDER.getValue()) {
            service = ClassUtils.getInstanceOrDefault(DSSServerSenderServiceImpl.class, new DSSServerSenderServiceImpl());
        } else {
            service = ClassUtils.getInstanceOrDefault(DSSSenderServiceImpl.class, new DSSSenderServiceImpl());
        }
        LOGGER.info("Use {} to instance a available DSSSenderService.", service.getClass().getName());
    }


    public static DSSSenderService getOrCreateServiceInstance() {
        return service;
    }


}
