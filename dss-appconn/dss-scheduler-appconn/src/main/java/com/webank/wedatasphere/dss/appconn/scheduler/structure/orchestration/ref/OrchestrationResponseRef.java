package com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefBuilder;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRefImpl;

/**
 * @author enjoyyin
 * @date 2022-03-14
 * @since 0.5.0
 */
public interface OrchestrationResponseRef extends ResponseRef {

    /**
     * 对应于SchedulerAppConn（调度系统）的编排模式（一般就是调度系统的工作流）。
     * @return 返回调度系统的编排ID
     */
    Long getRefOrchestrationId();

    static ExternalBuilder newExternalBuilder() {
        return new ExternalBuilder();
    }

    class ExternalBuilder extends ResponseRefBuilder.ExternalResponseRefBuilder<ExternalBuilder, OrchestrationResponseRef> {
        private Long refOrchestrationId;
        public ExternalBuilder setRefOrchestrationId(Long refOrchestrationId) {
            this.refOrchestrationId = refOrchestrationId;
            return this;
        }
        @Override
        protected OrchestrationResponseRef createResponseRef() {
            return new OrchestrationResponseRefImpl();
        }

        class OrchestrationResponseRefImpl extends ResponseRefImpl implements OrchestrationResponseRef {
            public OrchestrationResponseRefImpl() {
                super(ExternalBuilder.this.responseBody, ExternalBuilder.this.status,
                        ExternalBuilder.this.errorMsg, ExternalBuilder.this.responseMap);
            }
            @Override
            public Long getRefOrchestrationId() {
                return refOrchestrationId;
            }
        }
    }

}
