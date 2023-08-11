package com.webank.wedatasphere.dss.standard.app.development.operation;

import com.webank.wedatasphere.dss.standard.app.development.ref.AppConn2LinkisResponseRef;
import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public interface AppConn2LinkisRefExecutionOperation<K extends RefJobContentRequestRef<K>> extends RefExecutionOperation<K> {

    /**
     * 该 Operation 用于通过第三方 refJob 的 jobContent 信息，生成一段可被 Linkis 某个引擎执行的代码。<br/>
     * 是为 AppConn2Linkis 工作流节点类型设计的一个执行类。具体可参照：AppConn2Linkis工作流节点类型开发指南。
     * @param requestRef 包含了第三方 refJob 信息的 requestRef
     * @return 包含了可被 Linkis 某个引擎执行的代码信息。
     * @throws ExternalOperationFailedException 如果生成代码失败，则抛出该异常
     */
    @Override
    AppConn2LinkisResponseRef execute(K requestRef) throws ExternalOperationFailedException;

}
