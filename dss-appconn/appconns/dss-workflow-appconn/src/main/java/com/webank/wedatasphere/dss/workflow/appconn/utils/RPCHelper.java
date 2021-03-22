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

package com.webank.wedatasphere.dss.workflow.appconn.utils;

import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.linkis.common.ServiceInstance;
import com.webank.wedatasphere.linkis.rpc.Sender;

/**
 * @author allenlliu
 * @date 2020/11/24 11:32
 */
public class RPCHelper {

    public static Sender getRpcSenderByAppInstance(AppInstance appInstance){
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setApplicationName(appInstance.getConfig().get("name").toString());
//        serviceInstance.setInstance(Utils.getComputerName() + ":" + applicationContext.getEnvironment().getProperty("server.port"));
        Sender sender = Sender.getSender(serviceInstance);
        return sender;
    }
}
