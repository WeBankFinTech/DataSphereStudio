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
 */

package com.webank.wedatasphere.dss.datamodel;

import com.google.gson.Gson;
import com.webank.wedatasphere.linkis.common.utils.Utils;
import com.webank.wedatasphere.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy;
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfig;
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import com.webank.wedatasphere.linkis.ujes.client.UJESClient;
import com.webank.wedatasphere.linkis.ujes.client.UJESClientImpl;
import com.webank.wedatasphere.linkis.ujes.client.request.JobExecuteAction;
import com.webank.wedatasphere.linkis.ujes.client.request.ResultSetAction;
import com.webank.wedatasphere.linkis.ujes.client.response.*;
import lombok.Data;
import org.apache.commons.io.IOUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Deprecated
public class UJESClientImplTestJ{
    public static void main(String[] args){
        // Suggest to use LinkisJobClient to submit job to Linkis.
        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) (DWSClientConfigBuilder.newBuilder().addServerUrl("http://121.36.12.247:8088")
                .connectionTimeout(30000).discoveryEnabled(true)
                .discoveryFrequency(1, TimeUnit.MINUTES)
                .loadbalancerEnabled(true).maxConnectionSize(5)
                .retryEnabled(false).readTimeout(30000)
                .setAuthenticationStrategy(new StaticAuthenticationStrategy()).setAuthTokenKey("hdfs")
                .setAuthTokenValue("hdfs"))).setDWSVersion("v1").build();
        UJESClient client = new UJESClientImpl(clientConfig);

        JobExecuteResult jobExecuteResult = client.execute(JobExecuteAction.builder().setCreator("hdfs")
                .addExecuteCode("select * from linkis_db.linkis_partitions limit 1")
                //.addExecuteCode("show tables")
                .setEngineType((JobExecuteAction.EngineType)JobExecuteAction.EngineType$.MODULE$.HIVE()).setEngineTypeStr("hql")
                .setUser("hdfs").build());
        System.out.println("execId: " + jobExecuteResult.getExecID() + ", taskId: " + jobExecuteResult.taskID());
        JobStatusResult status = client.status(jobExecuteResult);
        while(!status.isCompleted()) {
            JobProgressResult progress = client.progress(jobExecuteResult);
            System.out.println("progress: " + progress.getProgress());
            Utils.sleepQuietly(500);
            status = client.status(jobExecuteResult);
        }
        JobInfoResult jobInfo = client.getJobInfo(jobExecuteResult);
        String resultSet = jobInfo.getResultSetList(client)[0];
        ResultSetResult resultSetResult =client.resultSet(ResultSetAction.builder().setPath(resultSet).setUser(jobExecuteResult.getUser()).build());
        Object fileContents = resultSetResult.getFileContent();
        Object metadata = resultSetResult.getMetadata();
        System.out.println("metadata: " + metadata);
        System.out.println("fileContents: " + fileContents);
        System.out.println("fileContents size: " + ((List)fileContents).size());
        Preview preview = new  Preview();
        preview.setMetadata(metadata);
        preview.setData(fileContents);
        Gson gson = new Gson();
        System.out.println(gson.toJson(preview));
        IOUtils.closeQuietly(client);
    }

    @Data
    public static class Preview{
        private Object metadata;

        private Object data;
    }

    @Data
    public static class Metadata{
        private String columnName;

        private String comment;

        private String dataType;
    }



}