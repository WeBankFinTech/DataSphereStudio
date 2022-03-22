package com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso;

import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerAccessToken;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerDataResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerPageInfoResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinSchedulerHttpUtils;
import com.webank.wedatasphere.dss.common.utils.MapUtils;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

import java.util.Map;
import java.util.Optional;

/**
 * @author enjoyyin
 * @date 2022-03-16
 * @since 0.5.0
 */
public class DolphinSchedulerTokenManager_1_X extends AbstractDolphinSchedulerTokenManager {

    private String verifyUserNameUrl;

    private String createUserUrl;

    private String queryUserPageUrl;

    private String queryAccessTokenListUrl;

    private String generateTokenUrl;

    private String createTokenUrl;

    private String updateTokenUrl;

    @Override
    public void init(String baseUrl) {
        super.init(baseUrl);
        baseUrl = getBaseUrl();
        this.verifyUserNameUrl =
                baseUrl.endsWith("/") ? baseUrl + "users/verify-user-name" : baseUrl + "/users/verify-user-name";
        this.createUserUrl = baseUrl.endsWith("/") ? baseUrl + "users/create" : baseUrl + "/users/create";
        this.queryUserPageUrl = baseUrl.endsWith("/") ? baseUrl + "users/list-paging" : baseUrl + "/users/list-paging";

        this.queryAccessTokenListUrl =
                baseUrl.endsWith("/") ? baseUrl + "access-token/list-paging" : baseUrl + "/access-token/list-paging";
        this.generateTokenUrl =
                baseUrl.endsWith("/") ? baseUrl + "access-token/generate" : baseUrl + "/access-token/generate";
        this.createTokenUrl =
                baseUrl.endsWith("/") ? baseUrl + "access-token/create" : baseUrl + "/access-token/create";
        this.updateTokenUrl =
                baseUrl.endsWith("/") ? baseUrl + "access-token/update" : baseUrl + "/access-token/update";
    }

    @Override
    public boolean isCompatible(String dsVersion) {
        return dsVersion.startsWith("1.3");
    }

    /**
     * 验证用户名是否可用.
     *
     * @param userName 用户名
     * @return true：该用户名不存在，可使用.
     * @throws ExternalOperationFailedException 失败
     */
    private boolean verifyUserName(String userName) throws ExternalOperationFailedException {
        String url = this.verifyUserNameUrl + "?userName=" + userName;
        try {
            getHttpGetResult(url);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90051, "DolphinScheduler 验证用户名失败.", e);
        }
        return true;
    }

    /**
     * DS中新建用户.
     *
     * @param userName 用户名
     * @throws ExternalOperationFailedException 失败
     */
    @Override
    protected void createUser(String userName) throws ExternalOperationFailedException {
        logger.info("Try to ask DolphinScheduler to create user {}.", userName);
        UserCreationFactory.User user = userCreationFactory.createUser(userName);
        Map<String, Object> formData = MapUtils.newCommonMapBuilder().put("userName", userName)
            .put("userPassword", user.getUserPassword())
            .put("tenantId", user.getTenantId())
            .put("email", user.getEmail())
            .put("queue", user.getQueue()).build();
        getHttpPostResult(createUserUrl, formData);
    }

    @Override
    protected Integer fetchUserId(String userName) throws ExternalOperationFailedException {
        String url = this.queryUserPageUrl + "?pageNo=1&pageSize=20&searchVal=" + userName;
        logger.info("begin to fetch userId for user:{}, url is: {}", userName, url);
        DolphinSchedulerPageInfoResponseRef responseRef = getHttpGetResult(url);
        Optional<Integer> userId = responseRef.getTotalList().stream()
                .filter(user -> userName.equals(user.get("userName")))
                .findAny().map(user -> (int) DolphinSchedulerHttpUtils.parseToLong(user.get("id")));
        return userId.orElse(null);
    }

    /**
     * 查找用户的 accessToken，没有则返回 null.
     *
     * @param userName 用户名
     * @param userId 用户Id
     * @return token
     * @throws ExternalOperationFailedException 失败
     */
    @Override
    protected DolphinSchedulerAccessToken getTokenByUserName(String userName, int userId)
            throws ExternalOperationFailedException {
        String url = this.queryAccessTokenListUrl + "?pageNo=1&pageSize=20&searchVal=" + userName;
        DolphinSchedulerPageInfoResponseRef responseRef = getHttpGetResult(url);
        return responseRef.getTotalList(DolphinSchedulerAccessToken.class).stream()
                .filter(token -> userId == token.getUserId()).findAny().orElse(null);
    }

    @Override
    protected String createToken(int userId, String expireTime)
            throws ExternalOperationFailedException {
        String token = generateToken(userId, expireTime);
        Map<String, Object> formData = MapUtils.newCommonMapBuilder()
                .put("userId", userId)
                .put("expireTime", expireTime)
                .put("token", token).build();
        getHttpPostResult(createTokenUrl, formData);
        return token;
    }

    @Override
    protected void updateToken(DolphinSchedulerAccessToken userToken, String expireTime)
            throws ExternalOperationFailedException {
        Map<String, Object> formData = MapUtils.newCommonMapBuilder().put("id", userToken.getId())
                .put("userId", userToken.getUserId())
                .put("expireTime", expireTime)
                .put("token", userToken.getToken()).build();
        getHttpPostResult(updateTokenUrl, formData);
    }

    @Override
    protected String generateToken(int userId, String expireTime) throws ExternalOperationFailedException {
        Map<String, Object> formData = MapUtils.newCommonMap("userId", userId, "expireTime", expireTime);
        DolphinSchedulerDataResponseRef responseRef = getHttpPostResult(generateTokenUrl, formData);
        return responseRef.getData();
    }
}
