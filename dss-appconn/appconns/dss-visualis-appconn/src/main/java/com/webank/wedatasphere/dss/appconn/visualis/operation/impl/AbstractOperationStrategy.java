package com.webank.wedatasphere.dss.appconn.visualis.operation.impl;

import com.webank.wedatasphere.dss.appconn.visualis.operation.OperationStrategy;
import com.webank.wedatasphere.dss.appconn.visualis.utils.URLUtils;
import com.webank.wedatasphere.dss.appconn.visualis.utils.VisualisCommonUtil;
import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.QueryJumpUrlResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSGetAction;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.linkis.common.io.resultset.ResultSetWriter;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.apache.linkis.storage.domain.Column;
import org.apache.linkis.storage.domain.DataType;
import org.apache.linkis.storage.resultset.table.TableMetaData;
import org.apache.linkis.storage.resultset.table.TableRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.webank.wedatasphere.dss.appconn.visualis.constant.VisualisConstant.*;

/**
 * @author enjoyyin
 * @date 2022-03-08
 * @since 0.5.0
 */
public abstract class AbstractOperationStrategy implements OperationStrategy {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;
    protected String baseUrl;

    public void setSsoRequestOperation(SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation) {
        this.ssoRequestOperation = ssoRequestOperation;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    protected QueryJumpUrlResponseRef getQueryResponseRef(ThirdlyRequestRef.QueryJumpUrlRequestRefImpl requestRef, Long projectId,
                                                          String jumpUrlFormat, String id) {
        String jumpUrl = URLUtils.getUrl(baseUrl, jumpUrlFormat, projectId.toString(), id, requestRef.getName());
        String env = requestRef.getDSSLabels().stream().filter(dssLabel -> dssLabel instanceof EnvDSSLabel)
                .map(dssLabel -> (EnvDSSLabel) dssLabel).findAny().get().getEnv();
        String retJumpUrl = URLUtils.getEnvUrl(jumpUrl, env);
        return QueryJumpUrlResponseRef.newBuilder().setJumpUrl(retJumpUrl).success();
    }

    protected ResponseRef executeRef(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef ref,
                                     String url) throws ExternalOperationFailedException {
        logger.info("User {} try to execute Visualis {} with refJobContent: {} in url {}.", ref.getExecutionRequestRefContext().getSubmitUser(),
                ref.getType(), ref.getRefJobContent(), url);
        DSSGetAction visualisGetAction = new DSSGetAction();
        visualisGetAction.setUser(ref.getExecutionRequestRefContext().getSubmitUser());
        visualisGetAction.setParameter("labels", ((EnvDSSLabel) (ref.getDSSLabels().get(0))).getEnv());
        try {
            ResponseRef responseRef = VisualisCommonUtil.getExternalResponseRef(ref, ssoRequestOperation, url, visualisGetAction);
            Map<String, Object> resultMap = responseRef.toMap();
            List<Map<String, String>> columns = (List<Map<String, String>>) resultMap.get("columns");
            if (resultMap.get("columns") == null || columns.isEmpty()) {
                ref.getExecutionRequestRefContext().appendLog("Cannot execute an empty Widget!");
                throw new ExternalOperationFailedException(90176, "Cannot execute an empty Widget!", null);
            }
            List<Map<String, Object>> resultList = (List<Map<String, Object>>) resultMap.get("resultList");
            Column[] linkisColumns = columns.stream().map(columnData -> new Column(columnData.get("name"),
                    DataType.toDataType(columnData.get("type").toLowerCase()), ""))
                    .toArray(Column[]::new);
            ResultSetWriter resultSetWriter = ref.getExecutionRequestRefContext().createTableResultSetWriter();
            resultSetWriter.addMetaData(new TableMetaData(linkisColumns));
            for (Map<String, Object> recordMap : resultList) {
                resultSetWriter.addRecord(new TableRecord(recordMap.values().toArray()));
            }
            resultSetWriter.flush();
            IOUtils.closeQuietly(resultSetWriter);
            ref.getExecutionRequestRefContext().sendResultSet(resultSetWriter);
        } catch (IOException e) {
            ref.getExecutionRequestRefContext().appendLog("Failed to write widget resultSet to storage. Caused by: " + ExceptionUtils.getRootCauseMessage(e));
            throw new ExternalOperationFailedException(90176, "Failed to write widget resultSet to storage.", e);
        }
        return ResponseRef.newExternalBuilder().success();
    }

    protected RefExecutionState toRefExecutionState(String state) {
        switch (state) {
            case EXECUTION_FAILED:
            case EXECUTION_TIMEOUT:
                return RefExecutionState.Failed;
            case EXECUTION_SUCCEED:
                return RefExecutionState.Success;
            case EXECUTION_CANCELLED:
                return RefExecutionState.Killed;
            case EXECUTION_SCHEDULED:
            case EXECUTION_INITED:
                return RefExecutionState.Accepted;
            default:
                return RefExecutionState.Running;
        }
    }
}
