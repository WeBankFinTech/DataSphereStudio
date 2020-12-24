package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.util;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.bean.response.BearerRefreshTokenResponse;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.client.RestTemplateClient;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.conf.AirflowConf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Xudong Zhang on 2020/8/6.
 */

@Slf4j
public class BearerTokenRenewer {

    public static class BearerToken {
        public final String accessToken;
        public final String refreshToken;
        public final long refreshTokenRefreshTime;
        public final long accessTokenRefreshTime;

        public BearerToken(String accessToken, long accessTokenRefreshTime, String refreshToken, long refreshTokenRefreshTime) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.refreshTokenRefreshTime = refreshTokenRefreshTime;
            this.accessTokenRefreshTime = accessTokenRefreshTime;
        }
    }

    volatile private BearerToken bearerToken;
    private static final long accessTokenRefreshInterval = 10L * 60 * 1000; //10 min
    private static final long refreshTokenRefreshInterval = 10L * 24 * 60 * 60 * 1000; //10 days
    public static final int AIR_FLOW_ERROR_CODE = 90023;


    private final String username;
    private final String password;

    private final String loginUrl;
    private final String refreshUrl;
    private final HttpEntity<String> refreshRefreshTokenRequest;

    public BearerTokenRenewer(String username, String password) {
        this.username = username; //AirflowConf.AIRFLOW_USERNAME.getValue();
        this.password = password; //AirflowConf.AIRFLOW_PASSWORD.getValue();
        this.loginUrl = AirflowConf.getUrl("bearer_login");
        this.refreshUrl = AirflowConf.getUrl("refresh_bearer_access_token");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JsonObject json = new JsonObject();
        try {
            json.addProperty("username", username);
            json.addProperty("password", password);
            json.addProperty("refresh", true);
            json.addProperty("provider", "db");
        } catch (JsonIOException e) {
            throw new RuntimeException(e.getMessage());
            //throw new OutsightException(ResultCode.FAIL, e.getMessage());
        }
        this.refreshRefreshTokenRequest = new HttpEntity<>(json.toString(), headers);
        init();
    }

    //for test
    protected String getRefreshTokenWithoutRenew() {
        return bearerToken.refreshToken;
    }

    public String getBearerAccessToken() {
        return getBearerAccessToken(System.currentTimeMillis());
    }

    public String getBearerAccessToken(long currentTime) {
        maybeRefreshToken(currentTime);
        return bearerToken.accessToken;
    }

    public boolean isTokenExpired() {
        return System.currentTimeMillis() - bearerToken.accessTokenRefreshTime >= accessTokenRefreshInterval;
    }

    /**
     * 因为会在token过期前提前比较久刷新，所以不必提心token在调用这个函数前是可用的但是调用后就不可用了。
     *
     * @param current
     */
    private void maybeRefreshToken(long current) {
        if ((current - bearerToken.refreshTokenRefreshTime < refreshTokenRefreshInterval) &&
                (current - bearerToken.accessTokenRefreshTime < accessTokenRefreshInterval))
            return;
        refreshToken(current);
    }

    private synchronized void refreshToken(long current) {
        if (current - bearerToken.refreshTokenRefreshTime >= refreshTokenRefreshInterval)
            refreshRefreshToken();
        else if (current - bearerToken.accessTokenRefreshTime >= accessTokenRefreshInterval) {
            refreshAccessToken();
        }
    }


    /**
     * {
     * "username": "admin",
     * "password": "adminadmin",
     * "refresh": true,
     * "provider": "db"
     * }
     */
    private void init() {
        refreshRefreshToken();
    }

    private synchronized void refreshRefreshToken() {
        log.info("Refreshing bearer refresh-token");
        BearerRefreshTokenResponse bearerToken = RestTemplateClient.getInstance().getRestTemplate().postForObject(loginUrl, refreshRefreshTokenRequest, BearerRefreshTokenResponse.class);
        long refreshTime = System.currentTimeMillis();
        BearerToken newToken = new BearerToken(
                bearerToken.getAccessToken(),
                refreshTime,
                bearerToken.getRefreshToken(),
                refreshTime);
        this.bearerToken = newToken;
    }

    private synchronized void refreshAccessToken() {
        log.debug("Refreshing bearer access-token");
        HttpHeaders headers = new HttpHeaders();
        //headers.setBearerAuth(bearerToken.refreshToken);
        headers.set("Authorization", "Bearer " + bearerToken.refreshToken);
        HttpEntity<String> request = new HttpEntity<String>("", headers);
        String newAccessToken = RestTemplateClient.getInstance().getRestTemplate().postForObject(refreshUrl, request, BearerRefreshTokenResponse.class).getAccessToken();
        BearerToken newToken = new BearerToken(newAccessToken, System.currentTimeMillis(), this.bearerToken.refreshToken,
                this.bearerToken.refreshTokenRefreshTime);
        this.bearerToken = newToken;
    }
}
