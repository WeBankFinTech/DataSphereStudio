package com.webank.wedatasphere.dss.git.constant;

import com.webank.wedatasphere.dss.git.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.utils.FileUtils;

import java.io.File;

public class DSSGitConstant {
    public static String GIT_PATH_PRE = File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator;
    public static String GIT_PATH_SUFFIX = File.separator + ".git";
    public static String GIT_ACCESS_WRITE_TYPE = "write";
    public static String GIT_ACCESS_READ_TYPE = "read";
}
