/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appjoint.execution.core;

import com.webank.wedatasphere.linkis.common.io.FsPath;
import com.webank.wedatasphere.linkis.common.io.MetaData;
import com.webank.wedatasphere.linkis.common.io.Record;
import com.webank.wedatasphere.linkis.common.io.resultset.ResultSet;
import com.webank.wedatasphere.linkis.common.io.resultset.ResultSetReader;
import com.webank.wedatasphere.linkis.common.io.resultset.ResultSetWriter;
import com.webank.wedatasphere.linkis.protocol.query.RequestPersistTask;
import com.webank.wedatasphere.linkis.storage.LineMetaData;
import com.webank.wedatasphere.linkis.storage.LineRecord;
import com.webank.wedatasphere.linkis.storage.resultset.table.TableMetaData;
import com.webank.wedatasphere.linkis.storage.resultset.table.TableRecord;

import java.util.HashMap;
import java.util.Map;

/**
 * created by enjoyyin on 2019/10/12
 * Description:
 */
public interface NodeContext {


    static final String TOKEN_USER_STR = "Token-User";

    static final String TOKEN_CODE_STR = "Token-Code";


    public boolean isReadNode();

    public AppJointNode getAppJointNode();

    public Map<String, Object> getRuntimeMap();

    public long[] getJobIdsOfShareNode();

    public RequestPersistTask getJobById(long jobId);


    //获取任务的所有的结果集路径
    FsPath[] getResultSetPathsByJobId(long jobId);

    public void appendLog(String log);

    public void updateProgress(float progress);

    /**
     * 获取本节点的操作用户
     * @return
     */
    public String getUser();

    void setStorePath(String storePath);

    String getStorePath();

    ResultSetWriter<TableMetaData, TableRecord> createTableResultSetWriter();

    ResultSetWriter<TableMetaData, TableRecord> createTableResultSetWriter(String resultSetAlias);

    ResultSetWriter<LineMetaData, LineRecord> createTextResultSetWriter();

    ResultSetWriter<LineMetaData, LineRecord> createTextResultSetWriter(String resultSetAlias);

    ResultSetWriter<LineMetaData, LineRecord> createHTMLResultSetWriter();

    ResultSetWriter<LineMetaData, LineRecord> createHTMLResultSetWriter(String resultSetAlias);

    ResultSetWriter<LineMetaData, LineRecord> createPictureResultSetWriter();

    ResultSetWriter<LineMetaData, LineRecord> createPictureResultSetWriter(String resultSetAlias);



    ResultSetWriter<? extends MetaData, ? extends Record>createResultSetWriter(ResultSet<? extends MetaData, ? extends Record> resultSet, String resultSetAlias);


    public ResultSetReader<? extends MetaData, ? extends Record> getResultSetReader(FsPath fsPath);

    /**
     *
     * @return
     */
    public String getGatewayUrl();


    default Map<String,String> getTokenHeader(String user){
        Map<String, String> tokenHeader = new HashMap<>();
        tokenHeader.put(TOKEN_CODE_STR, "dss-AUTH");
        tokenHeader.put(TOKEN_USER_STR, user);
        return tokenHeader;
    }

}
