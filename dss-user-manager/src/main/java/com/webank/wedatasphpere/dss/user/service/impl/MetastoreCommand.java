package com.webank.wedatasphpere.dss.user.service.impl;

import com.typesafe.config.Config;
import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;
import com.webank.wedatasphpere.dss.user.service.Command;
import com.webank.wedatasphpere.dss.user.service.common.CommonFun;
import com.typesafe.config.ConfigFactory;
import java.io.IOException;

/**
 * @author anlexander
 * @date 2021/1/5
 */
public class MetastoreCommand extends AbsCommand {

    @Override
    public String authorization(AuthorizationBody body) throws IOException {
        Config parms = ConfigFactory.load("config/properties.conf");
        String rst = createDb(body,parms);
        return rst != Command.SUCCESS ? rst : Command.SUCCESS;
    }

    private String createDb(AuthorizationBody body, Config parms) throws IOException {

        String bashCommand;
        String userName = body.getUsername();
        String res = null;
        if (userName != null) {
                String dbName = userName + parms.getString("db_tail");
                String path = parms.getString("base_path") + dbName + ".db";
                bashCommand = this.getClass().getClassLoader().getResource(parms.getString("metastore_sh")).getPath();
                String scriptCmd = String.format("%s %s %s %s %s %s %s",  bashCommand,userName,dbName,path,
                                         parms.getString("realm"),parms.getString("admin"),parms.getString("keytabPath"));
                Process process = Runtime.getRuntime().exec("sudo sh " + scriptCmd);
                res = CommonFun.process(process);
        }
        return res;
    }
}
