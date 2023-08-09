package com.webank.wedatasphere.dss.framework.appconn.conf;

import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.CommonVars;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AppConnConf {

    public static final CommonVars<String> PROJECT_QUALITY_CHECKER_IGNORE_LIST = CommonVars.apply("wds.dss.appconn.checker.project.ignore.list", "");

    public static final CommonVars<String> DEVELOPMENT_QUALITY_CHECKER_IGNORE_LIST = CommonVars.apply("wds.dss.appconn.checker.development.ignore.list", "");

    public static final CommonVars<Integer> APPCONN_UPLOAD_THREAD_NUM = CommonVars.apply("wds.dss.appconn.upload.thread.num", 2);

    public static final List<String> DISABLED_APP_CONNS = getDisabledAppConns();

    private static List<String> getDisabledAppConns() {
        String disabledAppConns = CommonVars.apply("wds.dss.appconn.disabled", "").getValue();
        if(StringUtils.isBlank(disabledAppConns)) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(disabledAppConns.split(","));
        }
    }

}
