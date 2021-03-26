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


import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;
import com.webank.wedatasphpere.dss.user.service.Command;
import com.webank.wedatasphpere.dss.user.service.MacroCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @program: user-authorization
 * @description: 开通命令实现
 *
 * @create: 2020-08-10 14:24
 **/
public class UserMacroCommand implements MacroCommand {

    private List<AbsCommand> commandList =   new ArrayList<>();

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static Integer RETRY_COUNT = 1;

    @Override
    public void add(AbsCommand command) {

        commandList.add(command);
    }

    @Override
    public String authorization(AuthorizationBody body) throws Exception {

            return this.execute("authorization", body);

    }

    @Override
    public String undoAuthorization(AuthorizationBody json) throws Exception {

            return this.execute("undoAuthorization", json);

    }

    @Override
    public String capacity(AuthorizationBody json) throws Exception {

            return this.execute("capacity", json);

    }

    @Override
    public String renew(AuthorizationBody json) throws Exception {

            return this.execute("renew", json);

    }


    /**
     * 授权操作基础方法
     * @param funName  调用的函数名
     * @param body 传入的数据
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     */
    private String execute(String funName, AuthorizationBody body) throws Exception {

        for (AbsCommand command : commandList) {

            switch (funName) {
                case "authorization":
                     command.authorization(body);

                case "undoAuthorization":
                    command.undoAuthorization(body);

                case "capacity":
                    command.capacity(body);
            }

        }

        return "success";
    }


    }



