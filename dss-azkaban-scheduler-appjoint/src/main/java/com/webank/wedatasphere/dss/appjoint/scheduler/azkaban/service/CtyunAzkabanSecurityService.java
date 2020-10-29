package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.SSLContext;
import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.conf.AzkabanConf;
import com.webank.wedatasphere.dss.appjoint.scheduler.service.SchedulerSecurityService;
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrlImpl;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.DES;

public final class CtyunAzkabanSecurityService extends AppJointUrlImpl implements SchedulerSecurityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CtyunAzkabanSecurityService.class);

    private static final DataSource dataSource = dataSource();

    private static final long EXPIRE_TIME = 1000 * 60 * 10L;
    private static final String USER_NAME_KEY = "username";
    private static final String USER_TOKEN_KEY = AzkabanConf.AZKABAN_LOGIN_PWD.getValue();
    private static final String SESSION_ID_KEY = "azkaban.browser.session.id";

    private ConcurrentHashMap<String, Session> sessionCache = new ConcurrentHashMap<>();
    private Interner<String> pool = Interners.newWeakInterner();
    private String securityUrl;

    private static DataSource dataSource() {
        LOGGER.info("建立数据库连接，读取用户token");
        String dbUrl = AzkabanConf.DATASOURCE_URL.getValue();
        String username = AzkabanConf.DATASOURCE_USERNAME.getValue();
        String password = AzkabanConf.DATASOURCE_PASSWORD.getValue();
        String driverClassName = AzkabanConf.DATASOURCE_DRIVER_CLASS_NAME.getValue();

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(dbUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);

        HikariDataSource datasource = new HikariDataSource(hikariConfig);
        LOGGER.info("Database connection address information(数据库连接地址信息)={}", dbUrl);
        return datasource;
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.securityUrl = baseUrl + "/checkin";
    }

    @Override
    public Session login(String user) throws AppJointErrorException {
        synchronized (pool.intern(user)) {
            Session session = sessionCache.get(user);
            if (session != null && !isExpire(session)) {
                session.updateLastAccessTime();
                return session;
            }
            Session newSession;
            try {
                newSession = getSession(user, getUserToken(user));
            } catch (Exception e) {
                LOGGER.error("获取session失败", e);
                throw new AppJointErrorException(90019, "获取session失败", e);
            }
            ((AzkabanSession)newSession).setUser(user);
            sessionCache.put(user, newSession);
            return newSession;
        }
    }

    private String getUserToken(String user) throws AppJointErrorException {
        String token = null;
        // 从数据库中获取用户密码
        try (Connection connection = dataSource.getConnection();
            PreparedStatement preparedStatement = createPreparedStatement(connection, user);
            ResultSet resultSet = preparedStatement.executeQuery();) {
            if (resultSet.next()) {
                token = resultSet.getString(1);
            }
        } catch (SQLException e) {
            throw new AppJointErrorException(90020, "获取用户token失败", e);
        }

        if (token == null)
            throw new AppJointErrorException(90020, "用户token为空");

        // 密码解密
        while (user.length() < 8) {
            user += user;
        }
        byte[] key = user.getBytes();
        Arrays.sort(key);
        DES des = SecureUtil.des(key);
        return des.decryptStr(token, CharsetUtil.CHARSET_UTF_8);
    }

    private PreparedStatement createPreparedStatement(Connection connection, String username) throws SQLException {
        String sql = "SELECT password FROM ctyun_user WHERE username= ?";
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, username);
        return ps;
    }

    private Session getSession(String user, String token) throws IOException, AppJointErrorException {
        HttpPost httpPost = new HttpPost(securityUrl);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(USER_NAME_KEY, user));
        params.add(new BasicNameValuePair(USER_TOKEN_KEY, token));
        params.add(new BasicNameValuePair("action", "login"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));
        CookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        HttpClientContext context;
        String responseContent;
        try {
            LOGGER.info("获取session-url", httpPost.getURI());
            TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
            SSLContext sslContext = null;
            try {
                sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            } catch (Exception e) {
                e.printStackTrace();
            }
            SSLConnectionSocketFactory connectionSocketFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
            HttpClientBuilder httpClientBuilder = HttpClients.custom().setDefaultCookieStore(cookieStore);
            if(securityUrl.startsWith("https://")){
                httpClientBuilder.setSSLSocketFactory(connectionSocketFactory);
            }
            httpClient = httpClientBuilder.build();
            context = HttpClientContext.create();
            response = httpClient.execute(httpPost, context);
            HttpEntity entity = response.getEntity();
            responseContent = EntityUtils.toString(entity, "utf-8");
            LOGGER.info("Get azkaban response code is {},response: {}", response.getStatusLine().getStatusCode(),
                responseContent);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new AppJointErrorException(90041, responseContent);
            }
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
        List<Cookie> cookies = context.getCookieStore().getCookies();
        Optional<Session> session = cookies.stream().filter(this::findSessionId).map(this::cookieToSession).findFirst();
        return session
            .orElseThrow(() -> new AppJointErrorException(90041, "Get azkaban session is null : " + responseContent));
    }

    private boolean findSessionId(Cookie cookie) {
        return SESSION_ID_KEY.equals(cookie.getName());
    }

    private Session cookieToSession(Cookie cookie) {
        AzkabanSession session = new AzkabanSession();
        session.setCookies(new Cookie[] {cookie});
        return session;
    }

    @Override
    public void logout(String user) {
        synchronized (pool.intern(user)) {
            sessionCache.remove(user);
        }
    }

    private boolean isExpire(Session session) {
        return System.currentTimeMillis() - session.getLastAccessTime() > EXPIRE_TIME;
    }

}
