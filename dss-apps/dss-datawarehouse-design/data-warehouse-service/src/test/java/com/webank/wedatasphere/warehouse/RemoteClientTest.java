package com.webank.wedatasphere.warehouse;

import org.apache.linkis.datasource.client.impl.LinkisMetadataSourceRemoteClient;
import org.apache.linkis.datasource.client.request.GetMetadataSourceAllDatabasesAction;
import org.apache.linkis.datasource.client.response.GetMetadataSourceAllDatabasesResult;
import org.apache.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy;
import org.apache.linkis.httpclient.dws.config.DWSClientConfig;
import org.apache.linkis.httpclient.dws.config.DWSClientConfigBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class RemoteClientTest {

    public static void main(String[] args) {
        LinkisMetadataSourceRemoteClient client = getMetadataSourceRemoteClient();
        GetMetadataSourceAllDatabasesAction action = GetMetadataSourceAllDatabasesAction.builder().setUser("hdfs").build();
        GetMetadataSourceAllDatabasesResult result = client.getAllDBMetaDataSource(action);
        List<String> dbs = result.getDbs();
        System.out.println(dbs);
    }

    private static LinkisMetadataSourceRemoteClient getMetadataSourceRemoteClient(){
        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) (DWSClientConfigBuilder.newBuilder().addServerUrl("http://gateway:8088")
                .connectionTimeout(30000).discoveryEnabled(true)
                .discoveryFrequency(1, TimeUnit.MINUTES)
                .loadbalancerEnabled(true).maxConnectionSize(5)
                .retryEnabled(false).readTimeout(30000)
                .setAuthenticationStrategy(new StaticAuthenticationStrategy()).setAuthTokenKey("hdfs")
                .setAuthTokenValue("hdfs"))).setDWSVersion("v1").build();
        return new LinkisMetadataSourceRemoteClient(clientConfig);
    }

}
