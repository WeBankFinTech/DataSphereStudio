package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.service;

import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import org.apache.http.Header;
import org.apache.http.cookie.Cookie;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cooperyang on 2019/9/16.
 */
public final class AzkabanSession implements Session {

    private long lastAccessTime;
    private Cookie[] cookies;
    private Header[] headers;
    private String user;
    private Map<String,String> parameters = new HashMap<>();

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

}
