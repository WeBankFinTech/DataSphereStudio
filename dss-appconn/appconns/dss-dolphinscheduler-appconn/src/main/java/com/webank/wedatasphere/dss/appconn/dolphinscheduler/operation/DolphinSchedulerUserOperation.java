package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.constant.Constant;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerGetRequestOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerHttpGet;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerHttpPost;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerPostRequestOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinAppConnUtils;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.linkis.common.utils.JsonUtils;

/**
 * The type Dolphin scheduler user operation.
 *
 * @author yuxin.yuan
 * @date 2021/06/23
 */
public class DolphinSchedulerUserOperation {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerUserOperation.class);

    private SSOUrlBuilderOperation ssoUrlBuilderOperation;

    private SSORequestOperation<DolphinSchedulerHttpGet, CloseableHttpResponse> getOperation;

    private SSORequestOperation<DolphinSchedulerHttpPost, CloseableHttpResponse> postOperation;

    private String listPagingUrl;

    private String createUserUrl;

    private String grantProjectUrl;

    private String authedProjectUrl;

    public DolphinSchedulerUserOperation(String baseUrl) {
        this.getOperation = new DolphinSchedulerGetRequestOperation(baseUrl);
        this.postOperation = new DolphinSchedulerPostRequestOperation(baseUrl);

        this.listPagingUrl = baseUrl.endsWith("/") ? baseUrl + "users/list-paging" : baseUrl + "/users/list-paging";
        this.createUserUrl = baseUrl.endsWith("/") ? baseUrl + "users/create" : baseUrl + "/users/create";
        this.grantProjectUrl =
            baseUrl.endsWith("/") ? baseUrl + "users/grant-project" : baseUrl + "/users/grant-project";
        this.authedProjectUrl =
            baseUrl.endsWith("/") ? baseUrl + "projects/authed-project" : baseUrl + "/projects/authed-project";
    }

    /**
     * Gets user id.
     *
     * @param userName
     *            the user name
     * @return the user id，查询不到该用户时，返回null
     * @throws ExternalOperationFailedException
     *             the external operation failed exception
     */
    public Long getUserId(String userName) throws ExternalOperationFailedException {
        int i = 1;
        while (true) {
            String url = this.listPagingUrl + "?pageNo=" + i + "&pageSize=10&searchVal=" + userName;
            DolphinSchedulerHttpGet httpGet = new DolphinSchedulerHttpGet(url, Constant.DS_ADMIN_USERNAME);
            try (CloseableHttpResponse httpResponse =
                this.getOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpGet);) {
                HttpEntity ent = httpResponse.getEntity();
                String entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
                if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()
                    && DolphinAppConnUtils.getCodeFromEntity(entString) == Constant.DS_RESULT_CODE_SUCCESS) {
                    ObjectMapper mapper = JsonUtils.jackson();
                    JsonNode jsonNode = mapper.readTree(entString);
                    JsonNode data = jsonNode.get("data");
                    JsonNode totalList = data.get("totalList");
                    if (totalList.isArray()) {
                        for (JsonNode user : totalList) {
                            if (userName.equals(user.get("userName").asText())) {
                                return user.get("id").asLong();
                            }
                        }
                    }
                    if (data.get("currentPage").asInt() >= data.get("totalPage").asInt()) {
                        return null;
                    }
                    i++;
                } else {
                    logger.error("DolphinScheduler获取项目列表失败，返回的信息是 {}", entString);
                    throw new ExternalOperationFailedException(90001, "调度中心获取用户列表失败");
                }
            } catch (ExternalOperationFailedException e) {
                throw e;
            } catch (Exception e) {
                throw new ExternalOperationFailedException(90001, "调度中心获取用户列表失败", e);
            }
        }
    }

    /**
     * DS中新建用户.
     *
     * @param userName
     * @throws ExternalOperationFailedException
     */
    public void createUser(String userName) throws ExternalOperationFailedException {
        logger.info("DolphinScheduler新建用户 {} ", userName);
        CloseableHttpResponse httpResponse = null;
        try {
            URIBuilder uriBuilder = new URIBuilder(this.createUserUrl);
            uriBuilder.addParameter("userName", userName);
            uriBuilder.addParameter("userPassword", "rz3Lw7yFlKv@8GisM");
            uriBuilder.addParameter("tenantId", "1");
            uriBuilder.addParameter("email", "xx@chinatelecom.cn");
            uriBuilder.addParameter("queue", "default");
            DolphinSchedulerHttpPost httpPost =
                new DolphinSchedulerHttpPost(uriBuilder.build(), Constant.DS_ADMIN_USERNAME);

            httpResponse = this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost);
            HttpEntity ent = httpResponse.getEntity();
            String entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);

            if (HttpStatus.SC_CREATED == httpResponse.getStatusLine().getStatusCode()
                && Constant.DS_RESULT_CODE_SUCCESS == DolphinAppConnUtils.getCodeFromEntity(entString)) {
                logger.info("DolphinScheduler新建用户 {} 成功, 返回的信息是 {}", userName,
                    DolphinAppConnUtils.getValueFromEntity(entString, "msg"));
            } else {
                throw new ExternalOperationFailedException(90002, "调度中心新建用户失败, 原因:" + entString);
            }
        } catch (ExternalOperationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90002, "调度中心新建用户失败", e);
        } finally {
            IOUtils.closeQuietly(httpResponse);
        }
    }

    /**
     * Grant project.
     *
     * @param userName
     *            the user name
     * @param projectId
     *            the project id
     * @param cancelGrant
     *            the cancel grant，是否取消授权
     * @throws ExternalOperationFailedException
     *             the external operation failed exception
     */
    public void grantProject(String userName, Long projectId, boolean cancelGrant)
        throws ExternalOperationFailedException {
        // 如果待授权用户不存在，则新建
        Long userId = getUserId(userName);
        if (userId == null) {
            createUser(userName);
            userId = getUserId(userName);
        }

        List<Long> authedProjectIds = getAuthedProjectIds(userId, userName);
        if (cancelGrant) {
            if (!authedProjectIds.contains(projectId)) {
                return;
            }
            authedProjectIds.remove(projectId);
        } else {
            // 该项目已被授权
            if (authedProjectIds.contains(projectId)) {
                return;
            }
            authedProjectIds.add(projectId);
        }

        CloseableHttpResponse httpResponse = null;
        try {
            String url = this.grantProjectUrl + "?userId=" + userId + "&projectIds="
                + URLEncoder.encode(StringUtils.join(authedProjectIds, ","), "utf-8");
            DolphinSchedulerHttpPost httpPost = new DolphinSchedulerHttpPost(url, Constant.DS_ADMIN_USERNAME);

            httpResponse = this.postOperation.requestWithSSO(this.ssoUrlBuilderOperation, httpPost);
            HttpEntity ent = httpResponse.getEntity();
            String entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
            if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()
                && DolphinAppConnUtils.getCodeFromEntity(entString) == Constant.DS_RESULT_CODE_SUCCESS) {
                return;
            } else {
                logger.error("DolphinScheduler项目授权失败，返回的信息是 {}", entString);
                throw new ExternalOperationFailedException(90004, "调度中心给用户授权项目失败");
            }
        } catch (ExternalOperationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90004, "调度中心给用户授权项目失败");
        } finally {
            IOUtils.closeQuietly(httpResponse);
        }
    }

    private List<Long> getAuthedProjectIds(Long userId, String userName) throws ExternalOperationFailedException {
        List<Long> projectIds = new ArrayList<>();

        String url = this.authedProjectUrl + "?userId=" + userId;
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
                if (data.isArray()) {
                    for (JsonNode project : data) {
                        projectIds.add(project.get("id").asLong());
                    }
                }
                return projectIds;
            } else {
                logger.error("DolphinScheduler获取用户{}的已授权项目列表失败，返回的信息是 {}", userName, entString);
                throw new ExternalOperationFailedException(90003, "调度中心获取用户已授权项目列表失败");
            }
        } catch (ExternalOperationFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90003, "调度中心获取用户已授权项目列表失败", e);
        }
    }

}
