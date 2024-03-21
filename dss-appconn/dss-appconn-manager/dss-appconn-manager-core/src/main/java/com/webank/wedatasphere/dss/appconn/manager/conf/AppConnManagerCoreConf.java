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

package com.webank.wedatasphere.dss.appconn.manager.conf;

import org.apache.linkis.common.conf.CommonVars;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AppConnManagerCoreConf {

    public static String hostname;

    static {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
           throw new RuntimeException("UnknownHost",e);
        }
    }

    //在配置文件中，需要将该参数配置为主节点的hostname
    public static final CommonVars<String> IS_APPCONN_MANAGER = CommonVars.apply("wds.dss.appconn.framework.ismanager", "");
}
