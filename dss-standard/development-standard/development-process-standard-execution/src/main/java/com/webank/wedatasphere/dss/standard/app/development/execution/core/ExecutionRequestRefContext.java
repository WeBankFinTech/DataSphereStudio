/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.standard.app.development.execution.core;

import com.webank.wedatasphere.linkis.common.io.FsPath;
import com.webank.wedatasphere.linkis.common.io.MetaData;
import com.webank.wedatasphere.linkis.common.io.Record;
import com.webank.wedatasphere.linkis.common.io.resultset.ResultSet;
import com.webank.wedatasphere.linkis.common.io.resultset.ResultSetReader;
import com.webank.wedatasphere.linkis.common.io.resultset.ResultSetWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * created by cooperyang on 2019/10/12
 * Description:
 */
public interface ExecutionRequestRefContext {


    static final String TOKEN_USER_STR = "Token-User";

    static final String TOKEN_CODE_STR = "Token-Code";


    public Map<String, Object> getRuntimeMap();

    public void appendLog(String log);

    public void updateProgress(float progress);

    LinkisJob fetchLinkisJob(long jobId);

    //获取任务的所有的结果集路径
    FsPath[] fetchLinkisJobResultSetPaths(long jobId);

    /**
     * 获取本节点的操作用户
     * @return
     */
    public String getUser();

    void setStorePath(String storePath);

    String getStorePath();

    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createTableResultSetWriter();

    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createTableResultSetWriter(String resultSetAlias);

    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createTextResultSetWriter();

    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createTextResultSetWriter(String resultSetAlias);

    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createHTMLResultSetWriter();

    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createHTMLResultSetWriter(String resultSetAlias);

    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createPictureResultSetWriter();

    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createPictureResultSetWriter(String resultSetAlias);

    <M extends MetaData, R extends Record> ResultSetWriter<M, R> createResultSetWriter(ResultSet<? extends MetaData, ? extends Record> resultSet,
        String resultSetAlias);


    <M extends MetaData, R extends Record> ResultSetReader<M, R> getResultSetReader(FsPath fsPath);

    /**
     *
     * @return
     */
    String getGatewayUrl();


    default Map<String,String> getTokenHeader(String user){
        Map<String, String> tokenHeader = new HashMap<>();
        tokenHeader.put(TOKEN_CODE_STR, "dss-AUTH");
        tokenHeader.put(TOKEN_USER_STR, user);
        return tokenHeader;
    }

}
