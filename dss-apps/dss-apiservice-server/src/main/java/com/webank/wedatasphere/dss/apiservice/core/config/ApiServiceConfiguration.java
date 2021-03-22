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

package com.webank.wedatasphere.dss.apiservice.core.config;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * @author allenlliu
 * @version 2.0.0
 * @date 2020/08/12 03:46 PM
 */
public class ApiServiceConfiguration {
    public final static CommonVars<String> LINKIS_AUTHOR_USER_TOKEN = CommonVars.apply("wds.linkis.client.api.service.author.user.token","172.0.0.1");
    public final static CommonVars<String> LINKIS_URL = CommonVars.apply("wds.linkis.gateway.url","wtss");
    public final static CommonVars<String> LINKIS_ADMIN_USER = CommonVars.apply("wds.linkis.client.api.service.adminuser","ws");

    public final static  CommonVars<Integer>  LINKIS_CONNECTION_TIMEOUT = CommonVars.apply("wds.linkis.flow.connection.timeout",30000);
    public final static CommonVars<String> LINKIS_API_VERSION = CommonVars.apply("wds.linkis.server.version","v1");

    public final static CommonVars<String> LINKIS_JOB_CREATOR = CommonVars.apply("wds.linkis.flow.job.creator","apiservice");

    public final static CommonVars<String> API_SERVICE_TOKEN_KEY = CommonVars.apply("wds.dss.api.service.token.key","ApiServiceToken");


    public final static CommonVars<String> ITSM_URL = CommonVars.apply("wds.dss.api.service.itsm.request.url", "");
    public final static CommonVars<String> ITSM_USER_ID = CommonVars.apply("wds.dss.api.service.itsm.client.userid", "");
    public final static CommonVars<String> ITSM_APPID = CommonVars.apply("wds.dss.api.service.itsm.client.appid", "");
    public final static CommonVars<String> ITSM_APPKEY = CommonVars.apply("wds.dss.api.service.itsm.client.appkey", "");
    public final static CommonVars<String> ITSM_FORM_ID = CommonVars.apply("wds.dss.api.service.itsm.form.id", "");
    public final static CommonVars<String> DSS_API_TOKEN_SECRET_ID = CommonVars.apply("wds.dss.api.service.secret", "DSSSECRETTEST001002");

    public final static  CommonVars<Integer>  LINKIS_JOB_REQUEST_STATUS_TIME = CommonVars.apply("wds.linkis.job.status.timeout",3000);

    public final static CommonVars<Integer> LOG_ARRAY_LEN = CommonVars.apply("wds.linkis.log.array.len",4);

    public final static CommonVars<Integer> RESULT_PRINT_SIZE = CommonVars.apply("wds.linkis.result.print.size",10);

    public final static CommonVars<String>  DOWNLOAD_URL = CommonVars.apply("wds.linkis.filesystem.url", "/api/rest_j/v1/filesystem/resultsetToExcel", "fileSystem下载");

}
