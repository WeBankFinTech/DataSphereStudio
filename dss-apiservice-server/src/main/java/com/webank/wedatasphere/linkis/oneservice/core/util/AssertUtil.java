package com.webank.wedatasphere.linkis.oneservice.core.util;


import com.webank.wedatasphere.linkis.oneservice.core.restful.exception.AssertException;

/**
 * @author lidongzhang
 */
public class AssertUtil {

    public static void isTrue(boolean b, String message) {
        if (!b) {
            throw new AssertException(message);
        }
    }

    public static void isFalse(boolean b, String message) {
        if (b) {
            throw new AssertException(message);
        }
    }

    public static void notEmpty(String str, String message) {
        if (str == null || str.isEmpty()) {
            throw new AssertException(message);
        }
    }

    public static void notNull(Object str, String message) {
        if (str == null) {
            throw new AssertException(message);
        }
    }
}
