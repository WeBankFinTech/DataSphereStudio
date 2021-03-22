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

package com.webank.wedatasphere.dss.appconn.loader.loader;


import com.webank.wedatasphere.dss.appconn.core.AppConn;

/**
 * 加载AppConn的接口规范
 * */
public interface AppConnLoader {

    String APPCONN_DIR_NAME = "dss-appconns";

    String PROPERTIES_NAME = "appconn.properties";

    String LIB_NAME = "lib";

    String JAR_SUF_NAME = ".jar";

    String FILE_SCHEMA = "file://";

    AppConn getAppConn(String appConnName) throws Exception;

    AppConn getAppConn(String appConnName, String spi, String classPath) throws Exception;

}