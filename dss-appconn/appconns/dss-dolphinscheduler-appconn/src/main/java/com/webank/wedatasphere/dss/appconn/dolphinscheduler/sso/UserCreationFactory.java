package com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author enjoyyin
 * @date 2022-03-17
 * @since 0.5.0
 */
public class UserCreationFactory {

    public User createUser(String userName) {
        return new User() {
            @Override
            public String getUserName() {
                return userName;
            }
            @Override
            public String getUserPassword() {
                return RandomStringUtils.random(8);
            }
            @Override
            public String getTenantId() {
                return "1";
            }
            @Override
            public String getEmail() {
                return "xx@qq.com";
            }
            @Override
            public String getQueue() {
                return "default";
            }
        };
    }

    public interface User {

        String getUserName();

        String getUserPassword();

        String getTenantId();

        String getEmail();

        String getQueue();
    }
}
