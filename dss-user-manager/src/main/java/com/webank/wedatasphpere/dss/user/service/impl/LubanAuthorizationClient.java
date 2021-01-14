package com.webank.wedatasphpere.dss.user.service.impl;


import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;

/**
 * @program: luban-authorization
 * @description:
 * @author: luxl@chinatelecom.cn
 * @create: 2020-08-10 14:24
 **/
public class LubanAuthorizationClient {

    private LubanMacroCommand lubanCommand = new LubanMacroCommand();

    public LubanAuthorizationClient(){

    }

    public String authorization(AuthorizationBody body) {
        return lubanCommand.authorization(body);
    }
}
