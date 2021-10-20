package com.webank.wedatasphere.dss.data.governance

import com.webank.wedatasphere.dss.data.governance.impl.LinkisDataAssetsRemoteClient
import com.webank.wedatasphere.dss.data.governance.request.{GetHiveTblBasicAction, GetHiveTblCreateAction, GetHiveTblPartitionAction, SearchHiveDbAction, SearchHiveTblAction}
import com.webank.wedatasphere.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfigBuilder

import java.util.concurrent.TimeUnit

object TestDataAssetsRemoteClient {
  def main(args: Array[String]): Unit = {
    val clientConfig = DWSClientConfigBuilder.newBuilder()
      .addServerUrl("http://localhost:8088")
      .connectionTimeout(30000)
      .discoveryEnabled(false)
      .discoveryFrequency(1,TimeUnit.MINUTES)
      .loadbalancerEnabled(true)
      .maxConnectionSize(5)
      .retryEnabled(false)
      .readTimeout(30000)
      .setAuthenticationStrategy(new StaticAuthenticationStrategy())
      .setAuthTokenKey("hdfs")
      .setAuthTokenValue("hdfs")
      .setDWSVersion("v1")
      .build()

    val dataAssetsClient = new LinkisDataAssetsRemoteClient(clientConfig)

    val searchHiveTblResult = dataAssetsClient.searchHiveTbl(SearchHiveTblAction.builder().setUser("hdfs").setQuery("").setLimit(10).setOffset(0).setOwner("undefined").build()).getHiveList
    println(searchHiveTblResult)

    val searchHiveDbResult = dataAssetsClient.searchHiveDb(SearchHiveDbAction.builder().setUser("hdfs").setQuery("").setLimit(10).setOffset(0).setOwner("undefined").build()).getHiveList
    println(searchHiveDbResult)

    val hiveTblBasicResult = dataAssetsClient.getHiveTblBasic(GetHiveTblBasicAction.builder().setUser("hdfs").setGuid("a3be4a97-6465-4c3d-adee-76dfa662e531").build()).result
    println(hiveTblBasicResult)

    val hiveTblPartitionResult = dataAssetsClient.getHiveTblPartition(GetHiveTblPartitionAction.builder().setUser("hdfs").setGuid("a3be4a97-6465-4c3d-adee-76dfa662e531").build()).result
    println(hiveTblPartitionResult)

    val hiveTblCreateResult = dataAssetsClient.getHiveTblCreate(GetHiveTblCreateAction.builder().setUser("hdfs").setGuid("a3be4a97-6465-4c3d-adee-76dfa662e531").build()).result
    println(hiveTblCreateResult)
  }
}
