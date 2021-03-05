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
 * @program: luban-authorization
 * @description: 开通命令实现
 * @author: luxl@chinatelecom.cn
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



