package com.webank.wedatasphere.dss.appconn.scheduler;

import com.webank.wedatasphere.dss.appconn.scheduler.structure.orchestration.OrchestrationService;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;

/**
 * @author enjoyyin
 * @date 2022-03-14
 * @since 0.5.0
 */
public interface SchedulerStructureIntegrationStandard extends StructureIntegrationStandard {

    /**
     * 统一编排规范，用于打通 DSS 与 调度系统的编排体系。
     * 例如：打通 DSS 工作流 与 Schedulis 工作流。
     * 请注意，如果对接的 SchedulerAppConn 系统本身不支持管理工作流，则无需实现该接口。
     * @return 如果对应的调度系统不支持管理工作流，则返回 null；否则返回具体的实现类
     */
    OrchestrationService getOrchestrationService(AppInstance appInstance);

}
