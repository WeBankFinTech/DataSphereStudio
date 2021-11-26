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

package com.webank.wedatasphere.dss.common.conf;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * @Author alexyang
 * @Date 2020/3/27
 */
public class DSSCommonConf {

    public static final CommonVars<String> DSS_IO_ENV = CommonVars.apply("wds.dss.server.io.env", "BDAP_DEV");
    public static final CommonVars<String> DSS_EXPORT_ENV = CommonVars.apply("wds.dss.server.export.env", "生产中心");

    public static final CommonVars<String> DSS_EXPORT_URL = CommonVars.apply("wds.dss.server.export.url", "/appcom/tmp/dss");
    public static final CommonVars<String> DSS_INPUT_TOKEN = CommonVars.apply("wds.dss.server.input.token", "QML-AUTH");

    public static final CommonVars<String> DSS_UPLOAD_PATH = CommonVars.apply("wds.dss.file.upload.dir", "/appcom/tmp/uploads");
    public static final CommonVars<String> DSS_INPUT_RESTFUL_URL = CommonVars.apply("wds.dss.server.input.url", "http://localhost:9004/api/rest_j/v1/dss/input");


}
