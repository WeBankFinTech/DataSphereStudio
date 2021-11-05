package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.conf.DolphinSchedulerConf;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.constant.Constant;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.entity.DolphinSchedulerAccessToken;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.ref.DolphinSchedulerProjectResponseRef;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerGetRequestOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerHttpGet;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerHttpPost;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerPostRequestOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DateUtil;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinAppConnUtils;
import com.webank.wedatasphere.dss.appconn.schedulis.conf.SchedulisConf;
import com.webank.wedatasphere.dss.standard.app.development.operation.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.ref.CommonRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.service.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * The type Dolphin scheduler token operation.
 *
 * @author yuxin.yuan
 * @date 2021/06/07
 */
public class DolphinSchedulerTokenOperation implements RefQueryOperation {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerTokenOperation.class);

    // token提前30s过期
    private static final int expireTimeGap = 30000;

    private static DolphinSchedulerTokenOperation instance;

    Interner<String> pool = Interners.newWeakInterner();

    private AppDesc appDesc;

    private SSOUrlBuilderOperation ssoUrlBuilderOperation;

    private SSORequestOperation<DolphinSchedulerHttpGet, CloseableHttpResponse> getOperation;

    private SSORequestOperation<DolphinSchedulerHttpPost, CloseableHttpResponse> postOperation;

    private String verifyUserNameUrl;

    private String createUserUrl;

    private String queryUserPageUrl;

    private String queryAccessTokenListUrl;

    private String generateTokenUrl;

    private String createTokenUrl;

    private String updateTokenUrl;

    public static DolphinSchedulerTokenOperation getInstance(String baseUrl) {
        if (null == instance) {
            synchronized (DolphinSchedulerTokenOperation.class) {
                if (null == instance) {
                    instance = new DolphinSchedulerTokenOperation(baseUrl);
                }
            }
        }
        return instance;
    }

    private DolphinSchedulerTokenOperation(String baseUrl) {
        init(baseUrl);
    }

    public DolphinSchedulerTokenOperation(AppDesc appDesc) {
        this.appDesc = appDesc;
        String baseUrl = this.appDesc.getAppInstances().get(0).getBaseUrl();
        init(baseUrl);
    }

    private void init(String baseUrl) {
        this.getOperation = new DolphinSchedulerGetRequestOperation(baseUrl);
        this.postOperation = new DolphinSchedulerPostRequestOperation(baseUrl);

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
    public ResponseRef query(RequestRef ref) throws ExternalOperationFailedException {
        CommonRequestRef requestRef = (CommonRequestRef)ref;
        String userName = (String)requestRef.getParameter("userName");

        Map<String, String> result = new HashMap<>();
        if (Constant.DS_ADMIN_USERNAME.equals(userName)) {
            result.put("token", DolphinSchedulerConf.DS_ADMIN_TOKEN.getValue());
        } else {
            // 用户不存在，创建该用户
            if (verifyUserName(userName)) {
                createUser(userName);
            }

            String userId = getUserId(userName);
            result = getToken(userName, userId);
        }

        return new DolphinSchedulerProjectResponseRef(SchedulisConf.gson().toJson(result));
    }

    @Override
    public void setDevelopmentService(DevelopmentService service) {

    }

    /**
     * 验证用户名是否可用.
     *
     * @param userName
     * @return true：该用户名不存在，可使用.
     * @throws ExternalOperationFailedException
     */
    private boolean verifyUserName(String userName) throws ExternalOperationFailedException {
        String url = this.verifyUserNameUrl + "?userName=" + userName;
        DolphinSchedulerHttpGet httpGet = new DolphinSchedulerHttpGet(url, Constant.DS_ADMIN_USERNAME);

        int httpStatusCode = 0;
        try (CloseableHttpResponse response = this.getOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpGet)) {
            HttpEntity ent = response.getEntity();
            String entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
            httpStatusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != httpStatusCode) {
                logger.error("Dolphin Scheduler验证用户名失败，response status为{}", response.getStatusLine());
                throw new ExternalOperationFailedException(90051, "调度中心验证用户名失败");
            } else if (DolphinAppConnUtils.getCodeFromEntity(entString) == Constant.DS_RESULT_CODE_SUCCESS) {
                return true;
            } else {
                return false;
            }
        } catch (ExternalOperationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90051, "调度中心验证用户名失败", e);
        }
    }

    /**
     * DS中新建用户.
     *
     * @param userName
     * @throws ExternalOperationFailedException
     */
    private void createUser(String userName) throws ExternalOperationFailedException {
        logger.info("Dolphin Scheduler新建用户 {} ", userName);
        CloseableHttpResponse httpResponse = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(this.createUserUrl);
            uriBuilder.addParameter("userName", userName);
            uriBuilder.addParameter("userPassword", "rz3Lw7yFlKv@8GisM");
            uriBuilder.addParameter("tenantId", "1");
            uriBuilder.addParameter("email", "xx@qq.com");
            uriBuilder.addParameter("queue", "default");
            DolphinSchedulerHttpPost httpPost =
                new DolphinSchedulerHttpPost(uriBuilder.build(), Constant.DS_ADMIN_USERNAME);

            httpResponse = this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost);
            HttpEntity ent = httpResponse.getEntity();
            String entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);

            if (HttpStatus.SC_CREATED == httpResponse.getStatusLine().getStatusCode()
                && Constant.DS_RESULT_CODE_SUCCESS == DolphinAppConnUtils.getCodeFromEntity(entString)) {
                logger.info("Dolphin Scheduler新建用户 {} 成功, 返回的信息是 {}", userName,
                    DolphinAppConnUtils.getValueFromEntity(entString, "msg"));
            } else {
                throw new ExternalOperationFailedException(90052, "调度中心新建用户失败, 原因:" + entString);
            }
        } catch (ExternalOperationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90052, "调度中心新建用户失败", e);
        } finally {
            IOUtils.closeQuietly(httpResponse);
        }
    }

    private String getUserId(String userName) throws ExternalOperationFailedException {
        int i = 1;
        while (true) {
            String url = this.queryUserPageUrl + "?pageNo=" + i + "&pageSize=10&searchVal=" + userName;
            DolphinSchedulerHttpGet httpGet = new DolphinSchedulerHttpGet(url, Constant.DS_ADMIN_USERNAME);
            try (CloseableHttpResponse httpResponse =
                this.getOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpGet);) {
                HttpEntity ent = httpResponse.getEntity();
                String entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
                if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()
                    && DolphinAppConnUtils.getCodeFromEntity(entString) == Constant.DS_RESULT_CODE_SUCCESS) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.readTree(entString);
                    JsonNode data = jsonNode.get("data");
                    JsonNode totalList = data.get("totalList");
                    if (totalList.isArray()) {
                        for (JsonNode user : totalList) {
                            if (userName.equals(user.get("userName").asText())) {
                                return user.get("id").asText();
                            }
                        }
                    }
                    if (data.get("currentPage").asInt() >= data.get("totalPage").asInt()) {
                        throw new ExternalOperationFailedException(90054, "调度中心查询不到用户" + userName);
                    }
                    i++;
                } else {
                    logger.error("Dolphin Scheduler获取用户列表失败，返回的信息是 {}", entString);
                    throw new ExternalOperationFailedException(90053, "调度中心获取用户列表失败");
                }
            } catch (ExternalOperationFailedException e) {
                throw e;
            } catch (Exception e) {
                throw new ExternalOperationFailedException(90053, "调度中心获取用户列表失败", e);
            }
        }
    }

    private Map<String, String> getToken(String userName, String userId) throws ExternalOperationFailedException {
        DolphinSchedulerAccessToken accessToken = getTokenByUserName(userName, userId);
        // 该用户有token
        if (accessToken != null) {
            long expireTimeStamp = accessToken.getExpireTime().getTime() - expireTimeGap;
            // 已有token未过期，返回该token
            if (new Date().getTime() < expireTimeStamp) {
                Map<String, String> result = new HashMap<>();
                result.put("token", accessToken.getToken());
                result.put("expire_time", String.valueOf(expireTimeStamp));
                return result;
            }

            // 已过期，执行更新操作
            String expireTime = DateUtil.addHours(new Date(), 2);
            return updateToken(accessToken.getId(), userName, userId, expireTime);
        }

        // 该用户没有相应的token，执行创建操作
        String expireTime = DateUtil.addHours(new Date(), 2);
        return createToken(userName, userId, expireTime);

    }

    /**
     * 查找用户的accessToken，没有则返回null.
     * 
     * @param userName
     * @param userId
     * @return
     * @throws ExternalOperationFailedException
     */
    private DolphinSchedulerAccessToken getTokenByUserName(String userName, String userId)
        throws ExternalOperationFailedException {
        int i = 1;
        while (true) {
            String url = this.queryAccessTokenListUrl + "?pageNo=" + i + "&pageSize=10&searchVal=" + userName;
            DolphinSchedulerHttpGet httpGet = new DolphinSchedulerHttpGet(url, Constant.DS_ADMIN_USERNAME);
            try (CloseableHttpResponse httpResponse =
                this.getOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpGet);) {
                HttpEntity ent = httpResponse.getEntity();
                String entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
                if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()
                    && DolphinAppConnUtils.getCodeFromEntity(entString) == Constant.DS_RESULT_CODE_SUCCESS) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.readTree(entString);
                    JsonNode data = jsonNode.get("data");
                    JsonNode totalList = data.get("totalList");
                    if (totalList.isArray()) {
                        for (JsonNode token : totalList) {
                            if (userId.equals(token.get("userId").asText())) {
                                return mapper.treeToValue(token, DolphinSchedulerAccessToken.class);
                            }
                        }
                    }
                    if (data.get("currentPage").asInt() >= data.get("totalPage").asInt()) {
                        return null;
                    }
                    i++;
                } else {
                    logger.error("Dolphin Scheduler获取用户token列表失败，返回的信息是 {}", entString);
                    throw new ExternalOperationFailedException(90053, "调度中心获取用户token列表失败");
                }
            } catch (ExternalOperationFailedException e) {
                throw e;
            } catch (Exception e) {
                throw new ExternalOperationFailedException(90053, "调度中心获取用户token列表失败", e);
            }
        }
    }

    private Map<String, String> createToken(String userName, String userId, String expireTime)
        throws ExternalOperationFailedException {
        Map<String, String> result = new HashMap<>();
        synchronized (pool.intern(userId)) {
            DolphinSchedulerAccessToken accessToken = getTokenByUserName(userName, userId);
            // 已有token未过期，返回该token
            if (accessToken != null) {
                result.put("token", accessToken.getToken());
                result.put("expire_time", String.valueOf(accessToken.getExpireTime().getTime() - expireTimeGap));
                return result;
            }

            try {
                String token = generateToken(userId, expireTime);

                URIBuilder uriBuilder = new URIBuilder(this.createTokenUrl);
                uriBuilder.addParameter("userId", userId);
                uriBuilder.addParameter("expireTime", expireTime);
                uriBuilder.addParameter("token", token);
                DolphinSchedulerHttpPost httpPost =
                    new DolphinSchedulerHttpPost(uriBuilder.build(), Constant.DS_ADMIN_USERNAME);

                CloseableHttpResponse httpResponse =
                    this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost);
                HttpEntity ent = httpResponse.getEntity();
                String entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
                if (HttpStatus.SC_CREATED == httpResponse.getStatusLine().getStatusCode()
                    && DolphinAppConnUtils.getCodeFromEntity(entString) == Constant.DS_RESULT_CODE_SUCCESS) {
                    // 提前30s过期，方便刷新
                    long expireTimeStamp = DateUtil.strToTimeStamp(expireTime, DateUtil.FORMAT_LONG) - expireTimeGap;
                    result.put("token", token);
                    result.put("expire_time", String.valueOf(expireTimeStamp));
                    return result;
                } else {
                    logger.error("Dolphin Scheduler创建access token失败，返回的信息是 {}", entString);
                    throw new ExternalOperationFailedException(90053, "调度中心创建token失败");
                }
            } catch (ExternalOperationFailedException e) {
                throw e;
            } catch (Exception e) {
                throw new ExternalOperationFailedException(90053, "调度中心创建token失败", e);
            }
        }
    }

    private Map<String, String> updateToken(int tokenId, String userName, String userId, String expireTime)
        throws ExternalOperationFailedException {
        Map<String, String> result = new HashMap<>();
        synchronized (pool.intern(userId)) {
            DolphinSchedulerAccessToken accessToken = getTokenByUserName(userName, userId);
            long expireTimeStamp = accessToken.getExpireTime().getTime() - expireTimeGap;
            // 已有token未过期，返回该token
            if (new Date().getTime() < expireTimeStamp) {
                result.put("token", accessToken.getToken());
                result.put("expire_time", String.valueOf(expireTimeStamp));
                return result;
            }

            String token = generateToken(userId, expireTime);
            try {
                URIBuilder uriBuilder = new URIBuilder(this.updateTokenUrl);
                uriBuilder.addParameter("id", String.valueOf(tokenId));
                uriBuilder.addParameter("userId", userId);
                uriBuilder.addParameter("expireTime", expireTime);
                uriBuilder.addParameter("token", token);
                DolphinSchedulerHttpPost httpPost =
                    new DolphinSchedulerHttpPost(uriBuilder.build(), Constant.DS_ADMIN_USERNAME);

                CloseableHttpResponse httpResponse =
                    this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost);
                HttpEntity ent = httpResponse.getEntity();
                String entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
                if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()
                    && DolphinAppConnUtils.getCodeFromEntity(entString) == Constant.DS_RESULT_CODE_SUCCESS) {
                    expireTimeStamp = DateUtil.strToTimeStamp(expireTime, DateUtil.FORMAT_LONG) - expireTimeGap;
                    result.put("token", token);
                    result.put("expire_time", String.valueOf(expireTimeStamp));
                    return result;
                } else {
                    logger.error("Dolphin Scheduler更新access token失败，返回的信息是 {}", entString);
                    throw new ExternalOperationFailedException(90053, "调度中心更新token失败");
                }
            } catch (ExternalOperationFailedException e) {
                throw e;
            } catch (Exception e) {
                throw new ExternalOperationFailedException(90053, "调度中心更新token失败", e);
            }
        }
    }

    private String generateToken(String userId, String expireTime) throws ExternalOperationFailedException {
        try {
            URIBuilder uriBuilder = new URIBuilder(this.generateTokenUrl);
            uriBuilder.addParameter("userId", userId);
            uriBuilder.addParameter("expireTime", expireTime);
            DolphinSchedulerHttpPost httpPost =
                new DolphinSchedulerHttpPost(uriBuilder.build(), Constant.DS_ADMIN_USERNAME);

            CloseableHttpResponse httpResponse =
                this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost);
            HttpEntity ent = httpResponse.getEntity();
            String entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
            if (HttpStatus.SC_CREATED == httpResponse.getStatusLine().getStatusCode()
                && DolphinAppConnUtils.getCodeFromEntity(entString) == Constant.DS_RESULT_CODE_SUCCESS) {
                return DolphinAppConnUtils.getValueFromEntity(entString, "data");
            } else {
                logger.error("Dolphin Scheduler生成access token失败，返回的信息是 {}", entString);
                throw new ExternalOperationFailedException(90053, "调度中心生成token失败");
            }
        } catch (ExternalOperationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90053, "调度中心生成token失败", e);
        }
    }

}
