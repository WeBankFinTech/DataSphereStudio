package com.webank.wedatasphere.dss.git.constant;

import com.webank.wedatasphere.dss.git.common.protocol.config.GitServerConfig;
import com.webank.wedatasphere.dss.git.utils.FileUtils;

import java.io.File;

public class DSSGitConstant {
    public static String GIT_PATH_PRE = File.separator + FileUtils.normalizePath(GitServerConfig.GIT_SERVER_PATH.getValue()) + File.separator;
    public static String GIT_PATH_SUFFIX = File.separator + ".git";

    public static String GIT_USERNAME_FLAG = " by: ";

    // 修改未暂存
    public static String GIT_DIFF_STATUS_MODIFIED = "modified";
    // 新增文件未暂存
    public static String GIT_DIFF_STATUS_UNTRACKED = "untracked";
    // 删除未暂存
    public static String GIT_DIFF_STATUS_Missing = "missing";

}
