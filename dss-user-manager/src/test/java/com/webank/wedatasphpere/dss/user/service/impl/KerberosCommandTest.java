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


package com.webank.wedatasphpere.dss.user.service.impl;


import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class KerberosCommandTest {

    @Test
//    @MethodSource("body")
    void authorization() {
        AuthorizationBody body = new AuthorizationBody();
        body.setUsername("anlexander");
        body.setPassword("123321");
        KerberosCommand test = new KerberosCommand();
        try {
            test.authorization(body);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("当前测试方法结束");
    }
}
