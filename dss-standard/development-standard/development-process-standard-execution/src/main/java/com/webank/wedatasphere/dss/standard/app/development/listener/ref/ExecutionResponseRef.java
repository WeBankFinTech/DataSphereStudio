package com.webank.wedatasphere.dss.standard.app.development.listener.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefBuilder;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;

/**
 * @author enjoyyin
 * @date 2022-03-11
 * @since 0.5.0
 */
public interface ExecutionResponseRef extends ResponseRef {

    Throwable getException();

    static ExecutionResponseRefBuilder newBuilder() {
        return new ExecutionResponseRefBuilder();
    }

    class ExecutionResponseRefBuilder extends ResponseRefBuilder.ExternalResponseRefBuilder<ExecutionResponseRefBuilder, ExecutionResponseRef> {

        private Throwable exception;

        public ExecutionResponseRefBuilder setException(Throwable exception) {
            this.exception = exception;
            error(exception);
            return this;
        }

        public ExecutionResponseRefBuilder setResponseRef(ResponseRef responseRef) {
            status = responseRef.getStatus();
            errorMsg = responseRef.getErrorMsg();
            responseMap = responseRef.toMap();
            responseBody = responseRef.getResponseBody();
            return this;
        }

        public ExecutionResponseRef error() {
            if(exception != null) {
                return build();
            } else {
                return error("Unknown reason, Please ask admin for help.");
            }
        }

        class ExecutionResponseRefImpl extends ResponseRefImpl implements ExecutionResponseRef {
            public ExecutionResponseRefImpl() {
                super(ExecutionResponseRefBuilder.this.responseBody, ExecutionResponseRefBuilder.this.status,
                        ExecutionResponseRefBuilder.this.errorMsg, ExecutionResponseRefBuilder.this.responseMap);
            }
            @Override
            public Throwable getException() {
                return exception;
            }
        }

        @Override
        protected ExecutionResponseRef createResponseRef() {
            return new ExecutionResponseRefImpl();
        }
    }

}
