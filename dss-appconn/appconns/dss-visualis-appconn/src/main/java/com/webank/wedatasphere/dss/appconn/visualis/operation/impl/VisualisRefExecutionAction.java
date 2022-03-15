package com.webank.wedatasphere.dss.appconn.visualis.operation.impl;

import com.webank.wedatasphere.dss.appconn.visualis.operation.OperationStrategy;
import com.webank.wedatasphere.dss.standard.app.development.listener.common.AbstractRefExecutionAction;
import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef;

/**
 * @author enjoyyin
 * @date 2022-03-09
 * @since 0.5.0
 */
public class VisualisRefExecutionAction extends AbstractRefExecutionAction {

    private RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef requestRef;
    private OperationStrategy strategy;

    public RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef getRequestRef() {
        return requestRef;
    }

    public void setRequestRef(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef requestRef) {
        this.requestRef = requestRef;
    }

    public OperationStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(OperationStrategy strategy) {
        this.strategy = strategy;
    }
}
