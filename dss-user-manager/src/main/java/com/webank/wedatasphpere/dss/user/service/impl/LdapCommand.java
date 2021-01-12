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
        String passWord = body.getPassword();
        String ldapScriptServer = DSSUserManagerConfig.BDP_SERVER_LDAP_SCRIPT_SERVER;
        String ldapScriptRoot = DSSUserManagerConfig.BDP_SERVER_LDAP_SCRIPT_ROOT;
        String ldapScript = DSSUserManagerConfig.BDP_SERVER_LDAP_SCRIPT;
        createLdap(ldapScriptServer,ldapScriptRoot,ldapScript,userName,passWord);
        return Command.SUCCESS;
    }

    private String createLdap(String ldapScriptServer,String ldapScriptRoot,String ldapScript,String userName, String password) {
        String bashCommand;
        BufferedReader br;
        try {

            bashCommand = this.getClass().getClassLoader().getResource("./default/CreateLdapAccount.sh").getPath();

            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("sudo sh " + bashCommand + " " + ldapScriptServer + " " + ldapScriptRoot + " " + ldapScript + " " + userName+ " " + password);

            br = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String inline;
            while ((inline = br.readLine()) != null) {
                if (!inline.equals("")) {
                    inline = inline.replaceAll("<", "&lt;").replaceAll(">", "&gt;");

                    System.out.println(inline);
                } else {
                    System.out.println("\n");
                }
            }
            br.close();
            br = new BufferedReader(new InputStreamReader(process.getErrorStream()));    //错误信息
            while ((inline = br.readLine()) != null) {
                if (!inline.equals(""))
                    System.out.println( inline );
                else
                    System.out.println("\n");
            }

            int status = process.waitFor();
            if (status != 0){
                System.out.println("create ldap server error:"+status); ;
            }
            return Command.SUCCESS;
        }
        catch (Exception e){
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
