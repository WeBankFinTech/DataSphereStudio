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

import com.webank.wedatasphere.dss.common.conf.DSSCommonConf;
import org.apache.linkis.common.conf.CommonVars;

public interface AdminConf {

    CommonVars<String> LDAP_ADMIN_NAME = CommonVars.apply("wds.dss.ldap.admin.name", "");
    CommonVars<String> LDAP_ADMIN_PASS = CommonVars.apply("wds.dss.ldap.admin.password", "");
    CommonVars<String> LDAP_URL = CommonVars.apply("wds.dss.ldap.url", "");
    CommonVars<String> LDAP_BASE_DN = CommonVars.apply("wds.dss.ldap.base.dn", "");

    String[] SUPER_ADMIN_LIST = DSSCommonConf.SUPER_ADMIN_LIST;


}
