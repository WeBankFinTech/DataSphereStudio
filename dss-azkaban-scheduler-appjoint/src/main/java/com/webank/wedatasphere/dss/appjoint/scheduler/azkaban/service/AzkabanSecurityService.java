package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.service;

import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.scheduler.service.SchedulerSecurityService;
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrlImpl;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.constant.AzkabanConstant;
import com.webank.wedatasphere.linkis.common.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by cooperyang on 2019/9/16.
 */
public final class AzkabanSecurityService extends AppJointUrlImpl implements SchedulerSecurityService {

    private static Logger LOGGER = LoggerFactory.getLogger(AzkabanSecurityService.class);

    private static final long EXPIRE_TIME = 1000 * 60 * 10L;
    private ConcurrentHashMap<String, Session> sessionCache = new ConcurrentHashMap<>();
    private String securityUrl;
    private static final String USER_NAME_KEY = "username";
    private static final String USER_TOKEN_KEY = "userpwd";
    private static final String SESSION_ID_KEY = "azkaban.browser.session.id";
    private static Properties userToken ;

    static {
        Utils.defaultScheduler().scheduleAtFixedRate(()->{
            LOGGER.info("开始读取用户token文件");
            Properties properties = new Properties();
            try {
                properties.load(AzkabanSecurityService.class.getClassLoader().getResourceAsStream(AzkabanConstant.TOKEN_FILE_NAME));
                userToken = properties;
            } catch (IOException e) {
                LOGGER.error("读取文件失败:",e);
            }
        },0,10, TimeUnit.MINUTES);
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.securityUrl = baseUrl + "/checkin";
    }

    @Override
    public Session login(String user) throws AppJointErrorException {
        synchronized (user.intern()) {
            Session session = sessionCache.get(user);
            if (session != null && !isExpire(session)) {
                session.updateLastAccessTime();
                return session;
            }
            Session newSession;
            try {
                newSession = getSession(user, getUserToken(user));
            } catch (Exception e) {
                LOGGER.error("获取session失败:", e);
                throw new AppJointErrorException(90019, e.getMessage(), e);
            }
            ((AzkabanSession) newSession).setUser(user);
            sessionCache.put(user, newSession);
            return newSession;
        }
    }

    private Session getSession(String user, String token) throws IOException {
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
        try {
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
            context = HttpClientContext.create();
            response = httpClient.execute(httpPost, context);
            LOGGER.info("Get azkaban response code is "+ response.getStatusLine().getStatusCode());
        } finally {
            IOUtils.closeQuietly(response);
            IOUtils.closeQuietly(httpClient);
        }
        List<Cookie> cookies = context.getCookieStore().getCookies();
        Optional<Session> session = cookies.stream().filter(this::findSessionId).map(this::cookieToSession).findFirst();
        return session.orElseThrow(() -> new IllegalAccessError("azkaban登录失败：无此用户"));
    }

    private boolean findSessionId(Cookie cookie) {
        return SESSION_ID_KEY.equals(cookie.getName());
    }

    private Session cookieToSession(Cookie cookie) {
        AzkabanSession session = new AzkabanSession();
        session.setCookies(new Cookie[]{cookie});
        return session;
    }

    private String getUserToken(String user) throws AppJointErrorException {
        //直接从配置文件中读取，有需求可以自己实现
        Object token = userToken.get(user);
        if (token == null) throw new AppJointErrorException(90020,"用户token为空");
        return token.toString();
    }

    @Override
    public void logout(String user) {
        synchronized (user.intern()) {
            sessionCache.remove(user);
        }
    }

    private boolean isExpire(Session session) {
        return System.currentTimeMillis() - session.getLastAccessTime() > EXPIRE_TIME;
    }


}
