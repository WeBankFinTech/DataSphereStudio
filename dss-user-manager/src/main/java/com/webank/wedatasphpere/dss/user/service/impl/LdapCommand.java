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
 * @program: luban-authorization
 * @description: 创建用户空间
 * @author: luxl@chinatelecom.cn
 * @create: 2020-08-13 13:39
 **/

public class LdapCommand extends AbsCommand {

    @Override
    public String authorization(AuthorizationBody body) {

        String userName = body.getUsername();
        String password = body.getPassword();
        String ldapScriptServer = DSSUserManagerConfig.BDP_SERVER_LDAP_SCRIPT_SERVER;
        String ldapScriptRoot = DSSUserManagerConfig.BDP_SERVER_LDAP_SCRIPT_ROOT;
        String ldapScript = DSSUserManagerConfig.BDP_SERVER_LDAP_SCRIPT;

        String bashCommand = this.getClass().getClassLoader().getResource("default/CreateLdapAccount.sh").getPath();
        String[] args = {ldapScriptServer, ldapScriptRoot,ldapScript,userName,password};
        return this.runShell(bashCommand, args);
    }

}
