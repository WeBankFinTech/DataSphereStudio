package com.webank.wedatasphere.dss.common.utils;

/**
 * get cron parameter
 */
public class AssembleCronUtils {

    public static String getCron(Integer value) {

        if (value < 60) {
            return "0/" + value + " * * * * ?";
        } else if (value < 3600) {
            return "0 0/" + value / 60 + " * * * ?";
        } else {
            return "0 0 0/1 * * ?";
        }

    }

}
