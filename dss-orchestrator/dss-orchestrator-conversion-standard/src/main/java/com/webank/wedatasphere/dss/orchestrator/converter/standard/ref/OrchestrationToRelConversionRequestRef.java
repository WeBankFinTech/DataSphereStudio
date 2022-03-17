package com.webank.wedatasphere.dss.orchestrator.converter.standard.ref;

import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestration;

/**
 * @author enjoyyin
 * @date 2022-03-16
 * @since 0.5.0
 */
public interface OrchestrationToRelConversionRequestRef<R extends OrchestrationToRelConversionRequestRef<R>>
        extends DSSToRelConversionRequestRef<R> {

    default DSSOrchestration getDSSOrchestration() {
        return (DSSOrchestration) getParameter("dssOrchestration");
    }

    default R setDSSOrchestration(DSSOrchestration dssOrchestration) {
        setParameter("dssOrchestration", dssOrchestration);
        return (R) this;
    }

    /**
     * 返回一个 SchedulerAppConn（调度系统）工作流的 id，也有可能返回 null。
     * 该调度系统的工作流 id，与 getDSSOrchestration 方法所获取到的 DSS 编排存在一对一的关系。
     * 如何判断是否为空？第三方 SchedulerAppConn 如果实现了 OrchestrationService，则必定会存在，否则为空。
     * 具体请参考 OrchestrationService 的类注释。
     * @return
     */
    default Long getRefOrchestrationId() {
        return (Long) getParameter("refOrchestrationId");
    }

    default R setRefOrchestrationId(Long refOrchestrationId) {
        setParameter("refOrchestrationId", refOrchestrationId);
        return (R) this;
    }

}
