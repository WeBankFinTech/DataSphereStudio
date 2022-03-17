package com.webank.wedatasphere.dss.appconn.visualis.operation;

import com.webank.wedatasphere.dss.standard.app.development.listener.ref.RefExecutionRequestRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.ExportResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.QueryJumpUrlResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.impl.ThirdlyRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;


public interface OperationStrategy {

    String getStrategyName();

    RefJobContentResponseRef createRef(ThirdlyRequestRef.DSSJobContentWithContextRequestRef requestRef) throws ExternalOperationFailedException;

    void deleteRef(ThirdlyRequestRef.RefJobContentRequestRefImpl visualisDeleteRequestRef) throws ExternalOperationFailedException;

    ExportResponseRef exportRef(ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef,
                                String url,
                                DSSPostAction visualisPostAction) throws ExternalOperationFailedException;

    QueryJumpUrlResponseRef query(ThirdlyRequestRef.RefJobContentRequestRefImpl requestRef);

    ResponseRef updateRef(ThirdlyRequestRef.UpdateWitContextRequestRefImpl requestRef) throws ExternalOperationFailedException;

    RefJobContentResponseRef copyRef(ThirdlyRequestRef.CopyWitContextRequestRefImpl requestRef,
                        String url,
                        DSSPostAction visualisPostAction) throws ExternalOperationFailedException;

    RefJobContentResponseRef importRef(ThirdlyRequestRef.ImportWitContextRequestRefImpl requestRef,
                                       String url,
                                       DSSPostAction visualisPostAction) throws ExternalOperationFailedException;

    ResponseRef executeRef(RefExecutionRequestRef.RefExecutionProjectWithContextRequestRef ref) throws ExternalOperationFailedException;

}
