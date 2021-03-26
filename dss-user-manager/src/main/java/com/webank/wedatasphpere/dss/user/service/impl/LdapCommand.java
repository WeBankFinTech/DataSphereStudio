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


package com.webank.wedatasphpere.dss.user.service.impl;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import com.webank.wedatasphpere.dss.user.conf.DSSUserManagerConfig;
import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;
import com.webank.wedatasphpere.dss.user.service.Command;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;

/**
 * @program: user-authorization
 * @description: 创建用户空间
 *
 * @create: 2020-08-13 13:39
 **/

public class LdapCommand extends AbsCommand {

    @Override
    public String authorization(AuthorizationBody body) throws Exception {

        String userName = body.getUsername();
        String UserPassword = body.getPassword();
        String dssDeployPath = DSSUserManagerConfig.DSS_DEPLOY_PATH;

        String bashCommand = this.getClass().getClassLoader().getResource("default/CreateLdapAccount.sh").getPath();
        String[] args = {
                userName,
                UserPassword,
                dssDeployPath
        };

        return this.runShell(bashCommand, args);
    }

}
