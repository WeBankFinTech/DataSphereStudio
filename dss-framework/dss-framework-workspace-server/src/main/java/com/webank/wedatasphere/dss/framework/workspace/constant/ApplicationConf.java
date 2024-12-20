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

package com.webank.wedatasphere.dss.framework.workspace.constant;


import org.apache.linkis.common.conf.CommonVars;


public class ApplicationConf {

    public static final CommonVars<String> HOMEPAGE_MODULE_NAME =
            CommonVars.apply("wds.linkis.special.homepage.module.name", "apiServices");

    public static final CommonVars<String> HOMEPAGE_URL =
            CommonVars.apply("wds.linkis.special.homepage.module.url", "/newHome?workspaceId=");

    public static final CommonVars<String> ITSM_SECRETKEY = CommonVars.apply("wds.dss.itsm.secretkey","350965f1d6dfc38757cba3c34478163176aafcb2ed5ff2478d94a43b40d3ae42");

    public static final CommonVars<String> HOMEPAGE_DEFAULT_WORKSPACE = CommonVars.apply("wds.linkis.special.homepage.default.workspace","BRM_WORKSPACE,CF_WORKSPACE");
}
