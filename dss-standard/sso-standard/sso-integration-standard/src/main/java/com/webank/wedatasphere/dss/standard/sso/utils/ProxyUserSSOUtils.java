package com.webank.wedatasphere.dss.standard.sso.utils;


import com.webank.wedatasphere.dss.common.exception.DSSRuntimeException;
import com.webank.wedatasphere.dss.standard.app.sso.builder.DssMsgBuilderOperation;
import org.apache.linkis.protocol.util.ImmutablePair;

public class ProxyUserSSOUtils {

    private static final String PROXY_USER_TICKET_ID_STRING = "dss_user_session_proxy_ticket_id_v1";
    private static final String PROXY_USER_PREFIX = "proxy_";

    /**
     * 从 cookie 中查找是否包含了代理用户的 cookie，用以表示 DSS 是否开启了代理功能
     * @param dssMsg
     * @return 返回 true 如果包含了代理用户，否则返回 false
     */
    public static boolean existsProxyUser(DssMsgBuilderOperation.DSSMsg dssMsg) {
        return dssMsg.getCookies().containsKey(PROXY_USER_TICKET_ID_STRING);
    }

    /**
     * 通过 {@code SSOMsgParseOperation} 的 getUser 方法中获得的 user，是否是由 userName + proxyUser 组成的
     * @param user {@code SSOMsgParseOperation} 的 getUser 方法中获得的 user
     * @return true 如果是 user 是由userName + proxyUser 组成，否则返回 false
     */
    public static boolean existsProxyUser(String user) {
        return user.startsWith(PROXY_USER_PREFIX);
    }

    /**
     * 通过 {@code SSOMsgParseOperation} 的 getUser 方法中获得的 user，获取 userName 和 proxyUser
     * @param user {@code SSOMsgParseOperation} 的 getUser 方法中获得的 user
     * @return key 为 userName，value 为 proxyUser
     */
    public static ImmutablePair<String, String> getUserAndProxyUser(String user) {
        if(!user.startsWith(PROXY_USER_PREFIX)) {
            throw new DSSRuntimeException(56000, "not exists proxyUser.");
        }
        String userAndProxyUser = user.substring(PROXY_USER_PREFIX.length());
        int length = Integer.parseInt(userAndProxyUser.substring(0, 2));
        String userName = userAndProxyUser.substring(2, 2 + length);
        String proxyUser = userAndProxyUser.substring(2 + length);
        return new ImmutablePair<>(userName, proxyUser);
    }

    /**
     * 返回一个经过特殊处理的、包含了 userName 和 proxyUser 的特殊字符串，可通过 {@code getUserAndProxyUser} 解密获取 userName 和 proxyUser
     * @param userName 登录用户名
     * @param proxyUser 代理用户
     * @return 返回包含了 userName 和 proxyUser 的特殊字符串
     */
    public static String setUserAndProxyUser(String userName, String proxyUser) {
        int length = userName.length();
        if(length > 99) {
            throw new DSSRuntimeException(56000, "the length of userName is too long, at most 99 characters are supported.");
        }
        String lengthStr = String.valueOf(length);
        if(length < 10) {
            lengthStr = "0" + lengthStr;
        }
        return String.format("%s%s%s%s", PROXY_USER_PREFIX, lengthStr, userName, proxyUser);
    }

}
