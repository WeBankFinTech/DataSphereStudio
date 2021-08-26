package com.webank.wedatasphere.dss.data.api.server.util;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

public class CryptoUtils {
    private CryptoUtils() {
    }

    public static String object2String(Serializable o) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.flush();
            oos.close();
            bos.close();
            return new String((new Base64()).encode(bos.toByteArray()));
        } catch (Exception var3) {
            throw new RuntimeException(var3);
        }
    }

    public static Object string2Object(String str) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream((new Base64()).decode(str.getBytes()));
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object o = ois.readObject();
            bis.close();
            ois.close();
            return o;
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static String md5(String source, String salt, int iterator) {
        StringBuilder token = new StringBuilder();

        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            if (StringUtils.isNotEmpty(salt)) {
                digest.update(salt.getBytes("UTF-8"));
            }

            byte[] result = digest.digest(source.getBytes());

            for(int i = 0; i < iterator - 1; ++i) {
                digest.reset();
                result = digest.digest(result);
            }

            byte[] var12 = result;
            int var7 = result.length;

            for(int var8 = 0; var8 < var7; ++var8) {
                byte aResult = var12[var8];
                int temp = aResult & 255;
                if (temp <= 15) {
                    token.append("0");
                }

                token.append(Integer.toHexString(temp));
            }

            return token.toString();
        } catch (Exception var11) {
            throw new RuntimeException(var11.getMessage());
        }
    }
}
