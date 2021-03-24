/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */


package com.webank.wedatasphpere.dss.user.service;


import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import org.dom4j.DocumentException;

import java.io.IOException;

public interface Command {

    final public static String SUCCESS = "success";
    final public static String ERROR = "error";
    /**
     * 授权开通服务
     * @param body
     * @return 成功 success  其他失败
     */
    public String authorization(AuthorizationBody body) throws DocumentException, IOException, Exception;

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
