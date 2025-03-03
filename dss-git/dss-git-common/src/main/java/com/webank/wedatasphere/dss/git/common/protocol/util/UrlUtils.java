package com.webank.wedatasphere.dss.git.common.protocol.util;


public class UrlUtils {

    public static String normalizeIp(String ip) {
        ip = ip.trim();
        if(!ip.contains("http")) {
            ip = "http://" + ip;
        }
        while(ip.endsWith("/")) {
            ip = ip.substring(0, ip.length()-1);
        }
        return ip;
    }
}
