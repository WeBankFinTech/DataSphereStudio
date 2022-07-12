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

package com.webank.wedatasphere.dss.standard.app.development.listener.core;

import org.apache.linkis.common.io.FsPath;
import org.apache.linkis.common.io.MetaData;
import org.apache.linkis.common.io.Record;
import org.apache.linkis.common.io.resultset.ResultSet;
import org.apache.linkis.common.io.resultset.ResultSetReader;
import org.apache.linkis.common.io.resultset.ResultSetWriter;

import java.util.Map;


public interface ExecutionRequestRefContext {

    /**
     * 包含了 DSS 工作流节点右侧属性栏的所有信息，与 {@code RefJobContentRequestRef} 中的 getRefJobContent()
     * 返回的内容完全相同。
     * 该方法是一个历史遗留方法，新的 AppConn 在实现时推荐直接使用 {@code RefJobContentRequestRef} 中的 getRefJobContent()
     * @return 包含了 DSS 工作流节点右侧属性栏所有信息的 Map
     */
    @Deprecated
    Map<String, Object> getRuntimeMap();

    /**
     * 往客户端推送实时日志，该方法与 {@code Logger.info()} 效果等同，都会被 Linkis EngineConn 框架推送回客户端，
     * 但是 Linkis EngineConn 在给 Entrance 推送日志时，会过滤掉一些无用日志，通过日志框架 {@code Logger} 打印的
     * 日志，一部分会被过滤规则识别并过滤掉，不会给 Entrance 和客户端推送，但是通过该方法推送的日志不会被过滤。
     * @param log 日志
     */
    void appendLog(String log);

    /**
     * 实时更新第三方 AppConn refJob 的执行进度
     * @param progress 进度条
     */
    void updateProgress(float progress);

    /**
     * 获取指定 Linkis Job 的基本信息。
     * 该方法通常是提供给第三方节点想要使用上游的工作流节点内容时使用
     * @param jobId Linkis Job Id
     * @return Linkis Job 基本信息
     */
    LinkisJob fetchLinkisJob(long jobId);

    /**
     * 获取指定 Linkis Job 的所有的结果集路径。
     * 该方法通常是提供给第三方节点想要使用上游的工作流节点内容时使用
     */
    FsPath[] fetchLinkisJobResultSetPaths(long jobId);

    /**
     * 获取节点的实际执行用户。
     * 例如：如果工作流的提交调度用户为 enjoyyin，而调度系统设置的代理执行用户为 hadoop, 则该方法返回的是 hadoop。
     * @return 执行用户
     */
    String getUser();

    /**
     * 获取节点的提交用户。
     * 例如：如果工作流的提交调度用户为 enjoyyin，而调度系统设置的代理执行用户为 hadoop, 则该方法返回的是 enjoyyin。
     * @return 提交用户
     */
    String getSubmitUser();

    /**
     * 创建一个数据表类型的结果集 Writer 类，用于将第三方节点产生的结果集作为一张表格写入到 Linkis 之中。
     * @param <M> 一般为 TableMetaData
     * @param <R> 一般为 TableRecord
     * @return 数据表类型的结果集 Writer 类
     */
    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createTableResultSetWriter();

    /**
     * 创建一个数据表格式类型的结果集 Writer 类，用于将第三方节点产生的结果集作为一张表格写入到 Linkis 之中。
     * @param resultSetAlias 结果集文件的文件名前缀，如果为空，则等同于 {@code createTableResultSetWriter()}
     * @param <M> 一般为 TableMetaData
     * @param <R> 一般为 TableRecord
     * @return 数据表类型的结果集 Writer 类
     */
    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createTableResultSetWriter(String resultSetAlias);

    /**
     * 创建一个文本格式类型的结果集 Writer 类，用于将第三方节点产生的结果集作为一个text文件写入到 Linkis 之中。
     * @param <M> 一般为 LineMetaData
     * @param <R> 一般为 LineRecord
     * @return 文本格式类型的结果集 Writer 类
     */
    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createTextResultSetWriter();

    /**
     * 创建一个文本格式类型的结果集 Writer 类，用于将第三方节点产生的结果集作为一个text文件写入到 Linkis 之中。
     * @param resultSetAlias 结果集文件的文件名前缀，如果为空，则等同于 {@code createTextResultSetWriter()}
     * @param <M> 一般为 LineMetaData
     * @param <R> 一般为 LineRecord
     * @return 文本格式类型的结果集 Writer 类
     */
    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createTextResultSetWriter(String resultSetAlias);

    /**
     * 创建一个 HTML 格式类型的结果集 Writer 类，用于将第三方节点产生的结果集作为一个 HTML 文件写入到 Linkis 之中。
     * @param <M> 一般为 LineMetaData
     * @param <R> 一般为 LineRecord
     * @return HTML 格式类型的结果集 Writer 类
     */
    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createHTMLResultSetWriter();

    /**
     * 创建一个 HTML 格式类型的结果集 Writer 类，用于将第三方节点产生的结果集作为一个 HTML 文件写入到 Linkis 之中。
     * @param resultSetAlias 结果集文件的文件名前缀，如果为空，则等同于 {@code createHTMLResultSetWriter()}
     * @param <M> 一般为 LineMetaData
     * @param <R> 一般为 LineRecord
     * @return HTML 格式类型的结果集 Writer 类
     */
    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createHTMLResultSetWriter(String resultSetAlias);

    /**
     * 创建一个图片格式类型的结果集 Writer 类，用于将第三方节点产生的结果集作为一个图片文件写入到 Linkis 之中。
     * @param <M> 一般为 LineMetaData
     * @param <R> 一般为 LineRecord
     * @return 图片格式类型的结果集 Writer 类
     */
    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createPictureResultSetWriter();

    /**
     * 创建一个图片格式类型的结果集 Writer 类，用于将第三方节点产生的结果集作为一个图片文件写入到 Linkis 之中。
     * @param resultSetAlias 结果集文件的文件名前缀，如果为空，则等同于 {@code createPictureResultSetWriter()}
     * @param <M> 一般为 LineMetaData
     * @param <R> 一般为 LineRecord
     * @return 图片格式类型的结果集 Writer 类
     */
    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createPictureResultSetWriter(String resultSetAlias);

    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createResultSetWriter(ResultSet<? extends MetaData, ? extends Record> resultSet,
        String resultSetAlias);

    /**
     * 通过传入一个结果集文件路径，获取一个可以读取结果集内容的 Reader。
     * 根据结果集类型的不同，返回的 {@code ResultSetReader<M, R>} 中，其 泛型 M 和 泛型 R 也不相同。
     * 如果结果集是 数据表格式类型，则泛型 M 为 TableMetaData，泛型 R 为 TableRecord；其他结果集格式类型的，目前
     * 泛型 M 都是 LineMetaData，泛型 R 为 LineRecord。<br/>
     * @param fsPath 结果集文件路径
     * @param <M> 如果结果集是 数据表格式类型，则泛型 M 为 TableMetaData；其他结果集格式类型的泛型 M 都是 LineMetaData
     * @param <R> 如果结果集是 数据表格式类型，则泛型 R 为 TableRecord；其他结果集格式类型的泛型 R 都是 LineRecord
     * @return 可以读取结果集内容的 Reader
     */
    <M extends MetaData, R extends Record> ResultSetReader<M, R> getResultSetReader(FsPath fsPath);

    /**
     * 该方法用于将第三方节点写入缓存的一个结果集文件，持久化并发送给Linkis Entrance。
     * 如果您调用了 createResultSetWriter 等方法，请在最后调用此方法。
     * @param resultSetWriter 需要持久化的结果集 Writer
     */
    void sendResultSet(ResultSetWriter<? extends MetaData, ? extends Record> resultSetWriter);

    /**
     * Linkis Gateway 的 base URL
     * @return Linkis Gateway 的 base URL
     */
    String getGatewayUrl();

}
