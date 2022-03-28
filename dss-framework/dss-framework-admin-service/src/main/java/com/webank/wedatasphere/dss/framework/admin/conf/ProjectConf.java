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

package com.webank.wedatasphere.dss.framework.admin.conf;

import org.apache.linkis.common.conf.CommonVars;

public interface ProjectConf {

    CommonVars<String> SUPPORT_ABILITY = CommonVars.apply("wds.dss.framework.project.support.ability", "import,export,publish");
    CommonVars<String> SERVICE_NAME = CommonVars.apply("wds.dss.framework.project.service.name", "dss-framework-project-server");

    CommonVars<String> DS_ADMIN_TOKEN = CommonVars.apply("wds.dss.ds.token", "");
    CommonVars<String> DS_URL = CommonVars.apply("wds.dss.ds.url", "");

    CommonVars<String> LDAP_ADMIN_NAME = CommonVars.apply("wds.dss.ldap.admin.name", "");
    CommonVars<String> LDAP_ADMIN_PASS = CommonVars.apply("wds.dss.ldap.admin.password", "");
    CommonVars<String> LDAP_URL = CommonVars.apply("wds.dss.ldap.url", "");
    CommonVars<String> LDAP_BASE_DN = CommonVars.apply("wds.dss.ldap.base.dn", "");
    CommonVars<String> EXCHANGE_URL = CommonVars.apply("wds.dss.exchange.url", "");
    CommonVars<String> EXCHANGE_ADMIN_COOKIE = CommonVars.apply("wds.dss.exchange.cookie", "");
    CommonVars<String> DS_TRUST_TOKEN = CommonVars.apply("wds.dss.trust.token", "");
    CommonVars<Boolean> DS_PROXY_SELF_ENABLE = CommonVars.apply("wds.dss.proxy.self.enable", true);
    CommonVars<String> DSS_PROXY_ADMIN_NAME = CommonVars.apply("wds.dss.proxy.admin.name", "adminUser");


}
