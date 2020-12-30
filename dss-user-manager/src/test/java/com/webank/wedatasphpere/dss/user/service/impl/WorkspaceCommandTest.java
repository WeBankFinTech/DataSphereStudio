package com.webank.wedatasphpere.dss.user.service.impl;


import com.webank.wedatasphpere.dss.user.dto.request.AuthorizationBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

class WorkspaceCommandTest {

    @Test
    @MethodSource("body")
    void authorization() {
        AuthorizationBody body = new AuthorizationBody();
        body.setUsername("luxl");
        body.setPassword("123321");
        WorkspaceCommand test = new WorkspaceCommand();
        test.authorization(body);
        System.out.println("当前测试方法结束");
    }
}