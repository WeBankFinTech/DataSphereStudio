package com.webank.wedatasphere;

import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * @author howeye
 */
public class Md5Utils {

    public static String getMd5L32(String str) throws Exception {
        try {
            // Generate a instance of md5
            MessageDigest md = MessageDigest.getInstance("MD5");
            // calculate
            md.update(str.getBytes());
            String result = new BigInteger(1, md.digest()).toString(16);

            return StringUtils.leftPad(result, 32, '0');
        } catch (Exception e) {
            throw new Exception("Error generating md5 of string: " + str, e);
        }
    }

}
