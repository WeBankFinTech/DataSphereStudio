package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.service;

import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.client.AirflowClient;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.util.BearerTokenRenewer;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import org.apache.http.Header;
import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xudong Zhang on 2020/8/6.
 */
public final class AirflowSession implements Session {

    private long lastAccessTime;
    private Cookie[] cookies;
    private Header[] headers;
    private String user;
    private Map<String,String> parameters = new HashMap<>();
    private AirflowClient airflowClient;

    @Override
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Override
    public Cookie[] getCookies() {
        return this.cookies;
    }

    @Override
    public Header[] getHeaders() {
        return this.headers;
    }

    @Override
    public Map<String,String> getParameters() {
        return this.parameters;
    }

    @Override
    public long getLastAccessTime() {
        return this.lastAccessTime;
    }

    public void setCookies(Cookie[] cookies){
        this.cookies = cookies;
    }

    @Override
    public void updateLastAccessTime() {
        lastAccessTime = System.currentTimeMillis();
    }

    public AirflowClient getAirflowClient() {
        return airflowClient;
    }

    public void setAirflowClient(AirflowClient airflowClient) {
        this.airflowClient = airflowClient;
    }
}
