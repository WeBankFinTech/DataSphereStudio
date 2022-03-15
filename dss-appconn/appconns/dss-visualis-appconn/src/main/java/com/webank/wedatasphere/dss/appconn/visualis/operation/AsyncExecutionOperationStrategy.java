package com.webank.wedatasphere.dss.appconn.visualis.operation;

import com.webank.wedatasphere.dss.standard.app.development.listener.common.RefExecutionState;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.ExecutionResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * @author enjoyyin
 * @date 2022-03-09
 * @since 0.5.0
 */
public interface AsyncExecutionOperationStrategy extends OperationStrategy {

    String submit(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef ref) throws ExternalOperationFailedException;

    RefExecutionState state(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef ref, String execId) throws ExternalOperationFailedException;

    ExecutionResponseRef getAsyncResult(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef ref, String execId) throws ExternalOperationFailedException;

}
