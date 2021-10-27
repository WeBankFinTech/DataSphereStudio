package com.webank.wedatasphere.dss.appconn.dolphinscheduler.operation;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.constant.Constant;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerGetRequestOperation;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso.DolphinSchedulerHttpGet;
import com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils.DolphinAppConnUtils;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * The type Dolphin scheduler project query operation.
 *
 * @author yuxin.yuan
 * @date 2021/06/23
 */
public class DolphinSchedulerProjectQueryOperation {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerProjectQueryOperation.class);

    private String listPagingUrl;

    private SSORequestOperation<DolphinSchedulerHttpGet, CloseableHttpResponse> getOperation;

    public DolphinSchedulerProjectQueryOperation(String baseUrl) {
        this.listPagingUrl =
            baseUrl.endsWith("/") ? baseUrl + "projects/list-paging" : baseUrl + "/projects/list-paging";
        this.getOperation = new DolphinSchedulerGetRequestOperation(baseUrl);

    }

    public Long getProjectId(String projectName, String userName) throws ExternalOperationFailedException {
        int i = 1;
        while (true) {
            String url = this.listPagingUrl + "?pageNo=" + i + "&pageSize=10&searchVal=" + projectName;
            DolphinSchedulerHttpGet httpGet = new DolphinSchedulerHttpGet(url, userName);
            try (CloseableHttpResponse httpResponse =
                this.getOperation.requestWithSSO(null, httpGet);) {
                HttpEntity ent = httpResponse.getEntity();
                String entString = IOUtils.toString(ent.getContent(), StandardCharsets.UTF_8);
                if (HttpStatus.SC_OK == httpResponse.getStatusLine().getStatusCode()
                    && DolphinAppConnUtils.getCodeFromEntity(entString) == Constant.DS_RESULT_CODE_SUCCESS) {
                    ObjectMapper mapper = new ObjectMapper();
                    JsonNode jsonNode = mapper.readTree(entString);
                    JsonNode data = jsonNode.get("data");
                    JsonNode totalList = data.get("totalList");
                    if (totalList.isArray()) {
                        for (JsonNode project : totalList) {
                            if (projectName.equals(project.get("name").asText())) {
                                return project.get("id").asLong();
                            }
                        }
                    }
                    if (data.get("currentPage").asInt() >= data.get("totalPage").asInt()) {
                        throw new ExternalOperationFailedException(90023, "调度中心查询不到项目" + projectName);
                    }
                    i++;
                } else {
                    logger.error("DolphinScheduler获取项目列表失败，返回的信息是 {}", entString);
                    throw new ExternalOperationFailedException(90024, "调度中心获取项目列表失败");
                }
            } catch (ExternalOperationFailedException e) {
                throw e;
            } catch (Exception e) {
                throw new ExternalOperationFailedException(90024, "调度中心获取项目列表失败", e);
            }
        }
    }

}
