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


import com.webank.wedatasphpere.dss.user.conf.DSSUserManagerConfig;
import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: user-authorization
 * @description:
 *
 * @create: 2020-08-10 14:24
 **/
public class UserAuthorizationClient {

    public UserMacroCommand userMacroCommand = new UserMacroCommand();
    protected final Logger logger = LoggerFactory.getLogger(UserAuthorizationClient.class);

    public UserAuthorizationClient()  {

        String[] commandPaths = DSSUserManagerConfig.USER_ACCOUNT_COMMANDS.split(",");
        for(String classPath: commandPaths){
            try {
                userMacroCommand.add((AbsCommand) Class.forName(classPath).newInstance());
            } catch (Exception e) {
                logger.info(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public String authorization(AuthorizationBody body) throws Exception {
        return userMacroCommand.authorization(body);
    }


}
