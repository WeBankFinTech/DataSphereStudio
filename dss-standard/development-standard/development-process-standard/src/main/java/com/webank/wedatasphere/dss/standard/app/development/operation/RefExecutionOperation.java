/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.standard.app.development.operation;

import com.webank.wedatasphere.dss.standard.app.development.ref.RefJobContentRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

public interface RefExecutionOperation<K extends RefJobContentRequestRef<K>>
        extends DevelopmentOperation<K, ResponseRef> {

    /**
     * 执行第三方 AppConn 的 refJob。<br/>
     * requestRef 一般为 {@code RefExecutionRequestRef}，该接口定义了四个实现类，您可以根据需要去使用其中的一个 RefExecutionRequestRef 实现类。
     * {@code RefExecutionRequestRef} 中的 {@code ExecutionRequestRefContext} 为该节点执行的上下文信息类，包含了该第三方节点执行所需的
     * 所有信息，以及如果该第三方节点想要将执行完成后的结果集写入到 Linkis 中，以供下游节点使用时，{@code ExecutionRequestRefContext} 也提供了
     * 创建各种 createResultSetWriter 的方法；或是如果该第三方节点想要获取上游节点的结果集文件内容时，{@code ExecutionRequestRefContext} 也提供了获取
     * LinkisJob 信息和相关结果集文件路径的方法，可基于结果集路径调用 {@code ExecutionRequestRefContext.getResultSetReader()} 获取结果集文件内容。
     * 更多关于执行上下文的信息，请参考：{@code ExecutionRequestRefContext}。
     * <br/>
     * 通常情况下，如果您的这个 refJob 是一个运行时间较长的任务，如执行时间会超过2分钟，那么走异步提交执行的方式无疑对提升系统的高并发能力有非常大的帮助。
     * 这时我们建议大家继承 RefExecutionOperation 的子类 {@code LongTermRefExecutionOperation} 来实现异步提交执行能力。
     * 当绝大部分的 refJob 的执行时间会超过10分钟，我们强烈要求必须实现 {@code LongTermRefExecutionOperation}，否则当调度作业越来越多时（1小时超过1万个），
     * 系统可能会出现高并发场景下的性能瓶颈。
     * 更多关于常驻型作业执行的信息，请参考：{@code LongTermRefExecutionOperation}。
     * <br/>
     * 如果您的 refJob 作业是短时间运行的作业，您可以直接实现该接口提供执行能力。当您直接实现该接口时，该方法要求返回一个执行结果。
     * 如果您只想返回执行成功，可返回 {@code RequestRef.newExternalBuilder.success()}；如果执行失败，可返回一个包含了 exception 的 {@code ExecutionResponseRef}
     * @param requestRef 包含了第三方 refJob 信息的 requestRef
     * @return 返回一个执行结果，如果您只想返回执行成功，可返回 {@code RequestRef.newExternalBuilder.success()}；执行失败可返回一个包含了 exception 的 {@code ExecutionResponseRef}
     * @throws ExternalOperationFailedException 执行发生不可预知的错误时，抛出该异常
     */
    ResponseRef execute(K requestRef) throws ExternalOperationFailedException;

}
