package com.webank.wedatasphpere.dss.user.service.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;
import com.webank.wedatasphpere.dss.user.service.Command;

import java.io.IOException;
import java.net.InetAddress;

/**
 * @author anlexander
 * @date 2021/1/5
 */
public class KerberosCommand extends AbsCommand {

    @Override
    public String authorization(AuthorizationBody body) throws Exception {
        Config parms = ConfigFactory.load("config/properties.conf");
        String rst = createKt(body, parms);
        return rst != Command.SUCCESS ? rst : Command.SUCCESS;
    }

    private String createKt(AuthorizationBody body, Config parms) throws Exception {

        String userName = body.getUsername();
        String hostName = InetAddress.getLocalHost().getHostName();
        String res = null;
        if(userName != null){
            res = callShell(parms.getString("shellFile"), userName,hostName,
                    parms.getString("keytabPath"),parms.getString("sshPort"),
                    parms.getString("kdcNode"),parms.getString("kdcUser"),parms.getString("password"),parms.getString("realm"));
        }
        return res;
    }

    private String callShell(String shellFile, String username, String hostName, String keytabPath,
                             String sshPort, String kdcNode, String kdcUser,String password, String realm) throws Exception {

        String bashCommand = getResource(shellFile);
        String scriptCmd ;
        if(null != hostName){
            scriptCmd = String.format("%s %s %s %s %s %s %s %s", username,hostName,keytabPath,sshPort,kdcNode,kdcUser,password,realm);
        }else {
            scriptCmd = String.format("%s %s %s %s %s %s %s", username,keytabPath,sshPort,kdcNode,kdcUser,password,realm);
        }
        String[] args = scriptCmd.split(" ");
        return this.runShell(bashCommand, args);
    }

}
