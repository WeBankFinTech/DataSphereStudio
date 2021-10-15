package com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils;

import org.apache.commons.lang3.StringUtils;

public class ProjectUtils {

    /**
     * 根据DSS空间名和项目名生成DS项目名：DSS空间名-DSS项目名
     *
     * @param workspaceName
     * @param projectName
     * @return
     */
    public static String generateDolphinProjectName(String workspaceName, String projectName) {
        return StringUtils.joinWith("-", workspaceName, projectName);
    }

}
