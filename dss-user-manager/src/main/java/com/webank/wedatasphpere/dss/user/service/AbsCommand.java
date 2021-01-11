package com.webank.wedatasphpere.dss.user.service;


import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;

import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * 各模块的授权 继承这个类 根据需要实现自己的类。
 */
public abstract class AbsCommand implements Command {

    @Override
    public String capacity(AuthorizationBody body) {
        return Command.SUCCESS;
    }

    @Override
    public String renew(AuthorizationBody body) {
        return Command.SUCCESS;
    }

    @Override
    public String undoAuthorization(AuthorizationBody body) { return Command.SUCCESS; }

    @Override
    public String authorization(AuthorizationBody body) throws IOException { return Command.SUCCESS; }

    public String toMessage(String msg) {
        return this.getClass().getSimpleName() + "模块开始执行："+ msg;
    }
}
