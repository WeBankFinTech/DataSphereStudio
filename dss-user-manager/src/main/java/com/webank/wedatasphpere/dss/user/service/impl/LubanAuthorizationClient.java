package com.webank.wedatasphpere.dss.user.service.impl;


//import cn.ctyun.lubanauthorization.dto.request.AuthorizationBody;
//import entity.LinkisUserDo;

/**
 * @program: luban-authorization
 * @description:
 * @author: luxl@chinatelecom.cn
 * @create: 2020-08-10 14:24
 **/
public class LubanAuthorizationClient {

    private LubanMacroCommand lubanCommand = new LubanMacroCommand();

    public LubanAuthorizationClient(){
        lubanCommand.add(new YarnCommand());
//        lubanCommand.add(new LDAPCommand());
    }

//    public String authorization(AuthorizationBody json, LinkisUserDo userInfo) {
//        return lubanCommand.authorization(json, userInfo);
//    }
//
//    public String undoAuthorization(AuthorizationBody json, LinkisUserDo userInfo) {
//        return lubanCommand.undoAuthorization(json, userInfo);
//    }
//
//    public String renew(AuthorizationBody json, LinkisUserDo userInfo) {
//        return lubanCommand.renew(json, userInfo);
//    }
//
//    public String capacity(AuthorizationBody json, LinkisUserDo userInfo) {
//        return lubanCommand.capacity(json, userInfo);
//    }
}
