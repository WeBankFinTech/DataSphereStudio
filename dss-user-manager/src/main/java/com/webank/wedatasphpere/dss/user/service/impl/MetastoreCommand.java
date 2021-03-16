package com.webank.wedatasphpere.dss.user.service.impl;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import com.webank.wedatasphpere.dss.user.conf.DSSUserManagerConfig;
import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import com.webank.wedatasphpere.dss.user.service.AbsCommand;
import com.webank.wedatasphpere.dss.user.service.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author anlexander
 * @date 2021/1/5
 */
public class MetastoreCommand extends AbsCommand {
    private static final Logger logger = LoggerFactory.getLogger(MetastoreCommand.class);

    @Override
    public String authorization(AuthorizationBody body) throws Exception {
        String rst = createDb(body);
        return rst != Command.SUCCESS ? rst : Command.SUCCESS;
    }

    private String createDb(AuthorizationBody body) throws Exception {
        String bashCommand = null;
        String[] args = null;
        String userName = body.getUsername();
        if (userName != null) {
            String dbName = userName + DSSUserManagerConfig.METASTORE_DB_TAIL;
            String path = DSSUserManagerConfig.METASTORE_HDFS_PATH + "/"+dbName+".db";
            bashCommand = getResource(DSSUserManagerConfig.METASTORE_SCRIPT_PAHT);
            args = new String[]{ userName,dbName,path,
                    DSSUserManagerConfig.KERBEROS_REALM,DSSUserManagerConfig.KERBEROS_ADMIN,DSSUserManagerConfig.KERBEROS_KEYTAB_PATH,DSSUserManagerConfig.KEBBEROS_ENABLE_SWITCH};
        }
        return this.runShell(bashCommand, args);
    }
}
