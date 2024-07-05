package com.webank.wedatasphere.dss.git.common.protocol.constant;


import java.util.Arrays;
import java.util.List;

public class GitConstant {
    public static String GIT_ACCESS_WRITE_TYPE = "write";
    public static String GIT_ACCESS_READ_TYPE = "read";
    public static String GIT_SERVER_META_PATH = ".metaConf";
    public static List<String> GIT_SERVER_SEARCH_TYPE = Arrays.asList("*.sql","*.hql","*.jdbc", "*.py", "*.python", "*.scala", "*.sh");
}
