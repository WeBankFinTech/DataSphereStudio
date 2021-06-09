package com.webank.wedatasphere.dss.framework.admin.common.exception;


import com.webank.wedatasphere.dss.framework.admin.common.domain.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class Assert {

    /**
     * 断言对象不为空
     *
     * @param object       对象为空则，抛出异常
     * @param responseEnum
     */
    public static void notNull(Object object, ResponseEnum responseEnum) {
        if (object == null) {
            log.info("object is null...");
            throw new AdminException(responseEnum);
        }
    }

    /**
     * 断言对象为空
     *
     * @param object       对象不为空，则抛出异常
     * @param responseEnum
     */
    public static void isNull(Object object, ResponseEnum responseEnum) {
        if (object != null) {
            log.info("object is not null...");
            throw new AdminException(responseEnum);
        }
    }

    /**
     * 断言表达式为真
     * 如果不为真，则抛出异常
     *
     * @param expression 是否成功
     */
    public static void isTrue(boolean expression, ResponseEnum responseEnum) {
        if (!expression) {
            log.info("failed...");
            throw new AdminException(responseEnum);
        }
    }

    /**
     * 断言两个对象不相等
     * 如果相等，则抛出异常
     *
     * @param m1
     * @param m2
     * @param responseEnum
     */
    public static void notEquals(Object m1, Object m2, ResponseEnum responseEnum) {
        if (m1.equals(m2)) {
            log.info("equals...");
            throw new AdminException(responseEnum);
        }
    }

    /**
     * 断言两个对象相等
     * 如果不相等，则抛出异常
     *
     * @param m1
     * @param m2
     * @param responseEnum
     */
    public static void equals(Object m1, Object m2, ResponseEnum responseEnum) {
        if (!m1.equals(m2)) {
            log.info("not equals...");
            throw new AdminException(responseEnum);
        }
    }

    /**
     * 断言参数不为空
     * 如果为空，则抛出异常
     *
     * @param s
     * @param responseEnum
     */
    public static void notEmpty(String s, ResponseEnum responseEnum) {
        if (StringUtils.isEmpty(s)) {
            log.info("is empty...");
            throw new AdminException(responseEnum);
        }
    }
}
