package com.webank.wedatasphere.dss.appconn.datachecker.utils;

import org.apache.commons.lang.StringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author lebronwang
 * @date 2022/08/04
 **/
public class Sha256Utils {

  public static String getSHA256L32(String str) throws NoSuchAlgorithmException {
    try {
      // Generate a instance of sha-256
      MessageDigest hash = MessageDigest.getInstance("SHA-256");
      // calculate
      hash.update(str.getBytes());
      String result = new BigInteger(1, hash.digest()).toString(16);

      return StringUtils.leftPad(result, 32, '0');
    } catch (NoSuchAlgorithmException e) {
      throw new NoSuchAlgorithmException("Error generating sha256 of string", e);
    }
  }


}
