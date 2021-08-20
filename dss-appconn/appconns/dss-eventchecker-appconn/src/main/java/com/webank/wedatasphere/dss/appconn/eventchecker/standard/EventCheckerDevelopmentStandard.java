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

package com.webank.wedatasphere.dss.appconn.eventchecker.standard;

import com.webank.wedatasphere.dss.appconn.eventchecker.service.EventCheckerExecuteService;
import com.webank.wedatasphere.dss.standard.app.development.standard.OnlyExecutionDevelopmentStandard;
import com.webank.wedatasphere.dss.standard.app.development.service.RefExecutionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventCheckerDevelopmentStandard extends OnlyExecutionDevelopmentStandard {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventCheckerDevelopmentStandard.class);

    @Override
    protected RefExecutionService createRefExecutionService() {
        return new EventCheckerExecuteService();
    }


    @Override
    public void init() {
        LOGGER.info("class EventCheckerDevelopmentStandard init");
    }


    @Override
    public String getStandardName() {
        return "EventCheckDevelopmentStandard";
    }

}
