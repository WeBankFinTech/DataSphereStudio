package com.webank.wedatasphpere.dss.user.service.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;
import com.webank.wedatasphpere.dss.user.service.Command;

/**
 * @author anlexander
 * @date 2021/1/5
 */
public class MetastoreCommand extends AbsCommand {

    @Override
    public String authorization(AuthorizationBody body) {
        Config parms = ConfigFactory.load("config/properties.conf");
        String rst = createDb(body,parms);
        return rst != Command.SUCCESS ? rst : Command.SUCCESS;
    }

    private String createDb(AuthorizationBody body, Config parms){

        String bashCommand = null;
        String scriptCmd = null;
        String userName = body.getUsername();
        if (userName != null) {
                String dbName = userName + parms.getString("db_tail");
                String path = parms.getString("base_path") + dbName + ".db";
                bashCommand = getResource(parms.getString("metastore_sh"));
                scriptCmd = String.format("%s %s %s %s %s %s", userName,dbName,path,
                                         parms.getString("realm"),parms.getString("admin"),parms.getString("keytabPath"));
        }
        return this.runShell(bashCommand, scriptCmd.split(" "));
    }
}
