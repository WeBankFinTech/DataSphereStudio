package com.webank.wedatasphere.dss.appjoint.scheduler.airflow.service;

import com.webank.wedatasphere.dss.appjoint.exception.AppJointErrorException;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.client.AirflowClient;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.conf.AirflowConf;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.util.BearerTokenRenewer;
import com.webank.wedatasphere.dss.appjoint.scheduler.service.SchedulerSecurityService;
import com.webank.wedatasphere.dss.appjoint.service.AppJointUrlImpl;
import com.webank.wedatasphere.dss.appjoint.service.session.Session;
import com.webank.wedatasphere.dss.appjoint.scheduler.airflow.constant.AirflowConstant;
import com.webank.wedatasphere.linkis.common.utils.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
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
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Xudong Zhang on 2020/8/6.
 */
public final class AirflowSecurityService extends AppJointUrlImpl implements SchedulerSecurityService {

    private static Logger LOGGER = LoggerFactory.getLogger(AirflowSecurityService.class);

    private ConcurrentHashMap<String, Session> sessionCache = new ConcurrentHashMap<>();
    private String securityUrl;
    private static Properties userToken ;

    static {
        Utils.defaultScheduler().scheduleAtFixedRate(()->{
            LOGGER.info("开始读取用户token文件");
            Properties properties = new Properties();
            try {
                properties.load(AirflowSecurityService.class.getClassLoader().getResourceAsStream(AirflowConstant.TOKEN_FILE_NAME));
                userToken = properties;
            } catch (IOException e) {
                LOGGER.error("读取文件失败:",e);
            }
        },0,10, TimeUnit.MINUTES);
    }

    @Override
    public void setBaseUrl(String baseUrl) {
        this.securityUrl = AirflowConf.getUrl("bearer_login");
    }

    @Override
    public Session login(String user) throws AppJointErrorException {
        synchronized (user.intern()) {
            Session session = sessionCache.get(user);
            if (session != null && !isExpire(session)) {
                session.updateLastAccessTime();
                return session;
            } else if (session != null) { // session已经过期
                if (((AirflowSession)session).getAirflowClient().getBearerTokenRenewer() != null) {
                    ((AirflowSession)session).getAirflowClient().getBearerTokenRenewer().getBearerAccessToken(); // 刷新key
                }
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
            ((AirflowSession) newSession).setUser(user);
            sessionCache.put(user, newSession);
            return newSession;
        }
    }

    private Session getSession(String user, String token) throws IOException, AppJointErrorException {
        CloseableHttpClient httpClient = null;
        HttpClientContext context;

        AirflowSession session = new AirflowSession();

        try {
            AirflowClient airflowClient = new AirflowClient(user, token);
            session.setUser(user);
            session.setAirflowClient(airflowClient);
        } catch (Exception e) {
            throw e;
        } finally {

        }
        return session;
    }

    private String getUserToken(String user) throws AppJointErrorException {
        //直接从配置文件中读取，有需求可以自己实现
        Object token = userToken.get(user);
        if (token == null) {
            if (AirflowConf.isRbac()) {
                throw new AppJointErrorException(90020, "用户token为空");
            } else {
                return null;
            }
        }
        return token.toString();
    }

    @Override
    public void logout(String user) {
        synchronized (user.intern()) {
            sessionCache.remove(user);  // airflow端，让access token自行过期
        }
    }

    private boolean isExpire(Session session) {
        if (((AirflowSession)session).getAirflowClient().getBearerTokenRenewer() != null) {
            return ((AirflowSession)session).getAirflowClient().getBearerTokenRenewer().isTokenExpired();
        }
        return false;
    }


}
