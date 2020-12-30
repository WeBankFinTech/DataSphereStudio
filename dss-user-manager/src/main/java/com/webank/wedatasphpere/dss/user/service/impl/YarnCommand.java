package com.webank.wedatasphpere.dss.user.service.impl;


import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;
import com.webank.wedatasphpere.dss.user.service.Command;

/**
 * @program: luban-authorization
 * @description: 开通yarn权限的实现
 * @author: luxl@chinatelecom.cn
 * @create: 2020-08-10 14:24
 **/
public class YarnCommand extends AbsCommand {

    @Override
    public String authorization(AuthorizationBody body) {

        return Command.SUCCESS;
    }
}
