package com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.DolphinSchedulerAppConn;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerAccessToken;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinSchedulerHttpUtils;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.common.utils.AppStandardClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.linkis.common.utils.ByteTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf.DS_TOKEN_EXPIRE_TIME;

/**
 * @author enjoyyin
 * @date 2022-03-17
 * @since 0.5.0
 */
public abstract class AbstractDolphinSchedulerTokenManager implements DolphinSchedulerTokenManager {

    // 一个DSS系统，只支持对接一个调度系统，所以这里只需要给出一个实例即可
    static DolphinSchedulerTokenManager dolphinSchedulerTokenManager;
    static final List<DolphinSchedulerTokenManager> dolphinSchedulerTokenManagers =
            AppStandardClassUtils.getInstance(DolphinSchedulerAppConn.DOLPHINSCHEDULER_APPCONN_NAME).getInstances(DolphinSchedulerTokenManager.class);

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected UserCreationFactory userCreationFactory;
    protected final Map<String, DolphinSchedulerAccessToken> userTokens = new ConcurrentHashMap<>();
    protected String baseUrl;
    protected SSORequestOperation ssoRequestOperation;

    private static final Object lock_1 = new Object();

    private static final Object lock_2 = new Object();

    @Override
    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public void init(String baseUrl) {
        if(StringUtils.isBlank(DolphinSchedulerConf.DS_ADMIN_TOKEN.getValue())) {
            logger.error("please set {} for DolphinScheduler AppConn.", DolphinSchedulerConf.DS_ADMIN_TOKEN.key());
            throw new ExternalOperationFailedException(90388, "please set " + DolphinSchedulerConf.DS_ADMIN_TOKEN.key() + " for DolphinScheduler AppConn.");
        }
        this.baseUrl = DolphinSchedulerHttpUtils.getDolphinSchedulerBaseUrl(baseUrl);
        userCreationFactory = AppStandardClassUtils.getInstance(DolphinSchedulerAppConn.DOLPHINSCHEDULER_APPCONN_NAME)
                .getInstanceOrDefault(UserCreationFactory.class, new UserCreationFactory());
        logger.info("use {} to create new DolphinScheduler users.", userCreationFactory.getClass().getSimpleName());
    }

    @Override
    public void setSSORequestOperation(SSORequestOperation ssoRequestOperation) {
        this.ssoRequestOperation = ssoRequestOperation;
    }

    @Override
    public int getUserId(String userName) {
        if(userTokens.containsKey(userName)) {
            return userTokens.get(userName).getUserId();
        }
        synchronized (lock_1) {
            if(userTokens.containsKey(userName)) {
                return userTokens.get(userName).getUserId();
            }
            Integer userId = fetchUserId(userName);
            if(userId == null) {
                // 用户不存在，创建该用户
                createUser(userName);
                userId = fetchUserId(userName);
            }
            return userId;
        }
    }

    @Override
    public long getTokenExpireTime(String userName) {
        if (DolphinSchedulerConf.DS_ADMIN_USER.getValue().equals(userName)) {
            return System.currentTimeMillis() + ByteTimeUtils.timeStringAsMs("24h");
        }
        if(userTokens.containsKey(userName)) {
            return userTokens.get(userName).getExpireTime().getTime();
        }
        getToken(userName);
        return userTokens.get(userName).getExpireTime().getTime();
    }

    @Override
    public final String getToken(String userName) {
        if (DolphinSchedulerConf.DS_ADMIN_USER.getValue().equals(userName)) {
            return DolphinSchedulerConf.DS_ADMIN_TOKEN.getValue();
        }
        if(userTokens.containsKey(userName)) {
            DolphinSchedulerAccessToken userToken = userTokens.get(userName);
            if(userToken.getExpireTime().getTime() - System.currentTimeMillis() <= DolphinSchedulerConf.DS_TOKEN_EXPIRE_TIME_GAP.getValue().toLong()) {
                // 刷新token
                updateToken(userToken, getNewExpireTime());
            }
            return userToken.getToken();
        }
        DolphinSchedulerAccessToken userToken = null;
        Integer userId;
        synchronized (lock_2) {
            userId = fetchUserId(userName);
            if(userId == null) {
                // 用户不存在，创建该用户
                createUser(userName);
                userId = fetchUserId(userName);
            } else {
                // 如果是已经存在的用户，可能已经有token了
                userToken = getTokenByUserName(userName, userId);
            }
            if(userToken != null) {
                // 已有token未过期，返回该token
                userToken.setUserName(userName);
                userTokens.put(userName, userToken);
                return userToken.getToken();
            }
            // 该用户没有相应的token，执行创建操作
            String expireTime = getNewExpireTime();
            createToken(userId, expireTime);
        }
        userToken = getTokenByUserName(userName, userId);
        if(userToken == null) {
            throw new ExternalOperationFailedException(90321, "cannot find the token from DolphinScheduler of user " + userName);
        }
        userToken.setUserName(userName);
        userTokens.put(userName, userToken);
        return userToken.getToken();
    }

    private String getNewExpireTime() {
        long expireTime = System.currentTimeMillis() + DS_TOKEN_EXPIRE_TIME.getValue().toLong();
        return DateFormatUtils.format(new Date(expireTime), "yyyy-MM-dd HH:mm:ss");
    }

    protected  <T extends ResponseRefImpl> T getHttpGetResult(String url) {
        return DolphinSchedulerHttpUtils.getHttpGetResult(ssoRequestOperation, url, DolphinSchedulerConf.DS_ADMIN_USER.getValue());
    }

    protected <T extends ResponseRefImpl> T getHttpPostResult(String url, Map<String, Object> formData) {
        return DolphinSchedulerHttpUtils.getHttpPostResult(ssoRequestOperation, url, DolphinSchedulerConf.DS_ADMIN_USER.getValue(), formData);
    }

    protected <T extends ResponseRefImpl> T getHttpPutResult(String url, Map<String, Object> formData) {
        return DolphinSchedulerHttpUtils.getHttpPutResult(ssoRequestOperation, url, DolphinSchedulerConf.DS_ADMIN_USER.getValue(), formData);
    }

    protected abstract void createUser(String userName) throws ExternalOperationFailedException;

    protected abstract Integer fetchUserId(String userName) throws ExternalOperationFailedException;

    protected abstract DolphinSchedulerAccessToken getTokenByUserName(String userName, int userId)
            throws ExternalOperationFailedException;

    protected abstract String createToken(int userId, String expireTime)
            throws ExternalOperationFailedException;

    protected abstract void updateToken(DolphinSchedulerAccessToken userToken, String expireTime)
            throws ExternalOperationFailedException;

    protected abstract String generateToken(int userId, String expireTime) throws ExternalOperationFailedException;
}
