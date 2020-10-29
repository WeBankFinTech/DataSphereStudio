/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.application.conf;


import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * Created by chaogefeng on 2019/10/10.
 */
public class ApplicationConf {

    public static final CommonVars<String> FAQ = CommonVars.apply("wds.linkis.application.dws.params","");
    public static final CommonVars<Boolean> SSO = CommonVars.apply("wds.linkis.gateway.conf.enable.sso",false);
}
