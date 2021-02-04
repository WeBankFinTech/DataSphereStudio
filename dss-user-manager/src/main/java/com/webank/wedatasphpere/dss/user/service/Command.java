package com.webank.wedatasphpere.dss.user.service;


import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import org.dom4j.DocumentException;

import java.io.IOException;

public interface Command {

    final public static String SUCCESS = "success";
    final public static String ERROR = "create account failed";

    /**
     * 授权开通服务
     * @param body
     * @return 成功 success  其他失败
     */
    public String authorization(AuthorizationBody body) throws Exception;

    /**
     * 关闭授权
     * @param body
     * @return 成功 success  其他失败
     */
    public String undoAuthorization(AuthorizationBody body) throws Exception;

    /**
     * 扩容
     * @param body
     * @return 成功 success  其他失败
     */
    public String capacity(AuthorizationBody body) throws Exception;

    /**
     * 续费
     * @param body
     * @return 成功 success  其他失败
     */
    public String renew(AuthorizationBody body) throws Exception;
}
