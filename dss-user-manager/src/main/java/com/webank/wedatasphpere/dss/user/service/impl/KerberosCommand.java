package com.webank.wedatasphpere.dss.user.service.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import com.webank.wedatasphpere.dss.user.conf.DSSUserManagerConfig;
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
        String rst = createKt(body);
        return rst != Command.SUCCESS ? rst : Command.SUCCESS;
    }

    private String createKt(AuthorizationBody body) throws Exception {
        String userName = body.getUsername();
        String hostName = InetAddress.getLocalHost().getHostName();
        String res = null;
        if(userName != null){
            res = callShell(DSSUserManagerConfig.KERBEROS_SCRIPT_PATH, userName,hostName,
                    DSSUserManagerConfig.KERBEROS_KEYTAB_PATH,DSSUserManagerConfig.KERBEROS_SSH_PORT,
                    DSSUserManagerConfig.KERBEROS_KDC_NODE,DSSUserManagerConfig.KERBEROS_KDC_USER_NAME,DSSUserManagerConfig.KERBEROS_KDC_USER_PASSWORD,DSSUserManagerConfig.KERBEROS_REALM,DSSUserManagerConfig.KERBEROS_ENABLE_SWITCH);
        }
        return res;
    }

    private String callShell(String shellFile, String username, String hostName, String keytabPath,
                             String sshPort, String kdcNode, String kdcUser,String password, String realm,String enableSwich) throws Exception {

        String bashCommand = getResource(shellFile);
        String scriptCmd ;
        if(null != hostName){
            scriptCmd = String.format("%s %s %s %s %s %s %s %s %s", username,hostName,keytabPath,sshPort,kdcNode,kdcUser,password,realm,enableSwich);
        }else {
            scriptCmd = String.format("%s %s %s %s %s %s %s %s", username,keytabPath,sshPort,kdcNode,kdcUser,password,realm,enableSwich);
        }
        String[] args = scriptCmd.split(" ");
        return this.runShell(bashCommand, args);
    }

}
