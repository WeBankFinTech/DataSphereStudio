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
        lubanCommand.add(new WorkspaceCommand());
<<<<<<< HEAD
        lubanCommand.add(new MetastoreCommand());
        lubanCommand.add(new KerberosCommand());
=======
        lubanCommand.add(new LdapCommand());
>>>>>>> 4d079d2 (创建ldap账户初始化)
    }

    public String authorization(AuthorizationBody body) {
        return lubanCommand.authorization(body);
    }
}
