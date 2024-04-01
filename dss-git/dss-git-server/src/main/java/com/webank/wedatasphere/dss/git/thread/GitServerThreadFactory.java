package com.webank.wedatasphere.dss.git.thread;

import com.webank.wedatasphere.dss.git.config.GitServerConfig;


public class GitServerThreadFactory {
    private static GitServerThreadPool gitServerThreadPool = new GitServerThreadPool(GitServerConfig.GIT_THREAD_NUM.getValue());

    public static GitServerThreadPool getGitServerThreadPool() {
        return gitServerThreadPool;
    }
}
