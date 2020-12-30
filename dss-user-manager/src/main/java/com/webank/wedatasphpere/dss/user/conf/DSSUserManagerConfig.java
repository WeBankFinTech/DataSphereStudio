package com.webank.wedatasphpere.dss.user.conf;

import com.webank.wedatasphere.linkis.common.conf.CommonVars;
import com.webank.wedatasphere.linkis.common.conf.CommonVars$;

/**
 * @program: dss-appjoint-auth
 * @description: 用户模块配置文件
 * @author: luxl@chinatelecom.cn
 * @create: 2020-12-30 16:26
 **/


public class DSSUserManagerConfig {
    public final static CommonVars LOCAL_USER_ROOT_PATH = CommonVars$.MODULE$.apply("wds.linkis.workspace.filesystem.localuserrootpath","hdfs:///dss_workspace");
}
