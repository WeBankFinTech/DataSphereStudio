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

import org.apache.linkis.common.conf.CommonVars;

public class DSSCommonConf {

    public static final CommonVars<String> DSS_IO_ENV = CommonVars.apply("wds.dss.server.io.env", "BDAP_DEV");

    public static final CommonVars<String> DSS_EXPORT_URL = CommonVars.apply("wds.dss.server.export.url", "/appcom/tmp/dss");

    public static final CommonVars<Integer> DSS_DOMAIN_LEVEL = CommonVars.apply("wds.linkis.gateway.domain.level", 3);

    /**
     * The old value is bdp-user-ticket-id
      */
    public static final CommonVars<String> DSS_TOKEN_TICKET_KEY = CommonVars.apply("wds.dss.user.ticket.key", "linkis_user_session_ticket_id_v1");

    public static final String[] SUPER_ADMIN_LIST = CommonVars.apply("wds.dss.super.admin", "").getValue().split(",");

    public static final CommonVars<String> ALL_GLOBAL_LIMITS_PREFIX = CommonVars.apply("wds.dss.global.limits.prefix", "wds.dss.global.limits.");

    public static final CommonVars<String> GLOBAL_LIMIT_PREFIX = CommonVars.apply("wds.dss.global.limit.prefix", "wds.dss.global.limit.");

    /**
     * deploy DSS instance numbers
     */
    public static final CommonVars<Integer> DSS_INSTANCE_NUMBERS = CommonVars.apply("wds.dss.instance.numbers", 2);
    /**
     * check dss server is active period, The value can only be between 0 and 59 inclusive, or a multiple of 60 and its unit is 's'.
     */
    public static final CommonVars<Integer> DSS_CHECK_SERVER_ACTIVE_PERIOD = CommonVars.apply("wds.dss.check.server.active.period", 60);
    /**
     * The value can only be between 0 and 59 inclusive, or a multiple of 60 and its unit is 'second'.
     */
    public static final CommonVars<Integer> DSS_EC_KILL_PERIOD = CommonVars.apply("wds.dss.ec.kill.period", 60);
    public static final CommonVars<String> ALTER_CLASS = CommonVars.apply("wds.dss.alter.class", "com.webank.wedatasphere.dss.common.server.alter.ImsAlterServiceImpl");
    public static final CommonVars<String> ALTER_RECEIVER = CommonVars.apply("wds.dss.alter.receiver", "burdezhang,mouhonghao");
}
