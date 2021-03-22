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

package com.webank.wedatasphere.dss.appconn.schedulis.conf;


import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * Created by v_wbjftang on 2019/9/16.
 */
public class AzkabanConf {

    public static final CommonVars<String> AZKABAN_BASE_URL = CommonVars.apply("wds.dss.appjoint.scheduler.azkaban.address", "");
    public static final CommonVars<String> DEFAULT_STORE_PATH = CommonVars.apply("wds.dss.appjoint.scheduler.project.store.dir", "/appcom/tmp/wds/dss");
    public static final CommonVars<String> AZKABAN_LOGIN_PWD = CommonVars.apply("wds.dss.appjoint.scheduler.azkaban.login.passwd", "password");

    public static final CommonVars<String> AZKABAN_RSA =
            CommonVars.apply("wds.dss.appjoint.scheduler.azkaban.rsa",
                    "iVGljygYsZvrNFCYqOpocm4Kpp7rJudeKomnM9FImWI4yNjdtmOt43Q1Brb7MFgzjRLIdJWJ0Ui760pDZpXPHOz81ctsj553E5cdKu7cg+h5C2AkBEZ6AvEQ+oet7ukwg+7ASSBuQufLkAxHKDqCjq/XxsC/MH11pkuHKaJpSTY=");

}
