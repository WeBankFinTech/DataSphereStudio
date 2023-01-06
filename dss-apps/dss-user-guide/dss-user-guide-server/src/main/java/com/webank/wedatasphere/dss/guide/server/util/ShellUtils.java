package com.webank.wedatasphere.dss.guide.server.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShellUtils {

    private static final Logger logger = LoggerFactory.getLogger(ShellUtils.class);

    /**
     * 执行shell命令
     *
     * @param shellString
     */
    public static void callShellByExec(String shellString) {
        try {
            logger.info("shellString:" + shellString);
            Process process = Runtime.getRuntime().exec(shellString);
            int exitValue = process.waitFor();
            if (0 != exitValue) {
                logger.error("call shell failed. error code is :" + exitValue);
            }
        } catch (Throwable e) {
            logger.error("call shell failed. " + e);
        }
    }

    /**
     * 执行shell命令
     *
     * @param shellString
     */
    public static String callShellQuery(String shellString) {
        logger.info("shellString:" + shellString);
        BufferedReader reader = null;
        String result = "";
        try {
            Process process = Runtime.getRuntime().exec(shellString);
            int exitValue = process.waitFor();
            if (0 != exitValue) {
                logger.error("call shell failed. error code is :" + exitValue);
            }
            // 返回值
            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            while ((line = reader.readLine()) != null) {
                result += line;
            }
        } catch (Throwable e) {
            logger.error("call shell failed. " + e);
        }
        logger.info("call shell query result:" + result);
        return result.trim();
    }

}
