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

package com.webank.wedatasphere.dss.appconn.schedulis.parser;

import com.webank.wedatasphere.dss.appconn.schedulis.entity.LinkisAzkabanReadNode;
import com.webank.wedatasphere.dss.appconn.schedulis.entity.LinkisAzkabanSchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.ReadNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.entity.SchedulerNode;
import com.webank.wedatasphere.dss.appconn.schedule.core.parser.SendEmailNodeParser;


public class LinkisAzkabanSendEmailNodeParser extends SendEmailNodeParser {

    @Override
    protected ReadNode createReadNode() {
        return new LinkisAzkabanReadNode();
    }

    @Override
    protected SchedulerNode createSchedulerNode() {
        return new LinkisAzkabanSchedulerNode();
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
