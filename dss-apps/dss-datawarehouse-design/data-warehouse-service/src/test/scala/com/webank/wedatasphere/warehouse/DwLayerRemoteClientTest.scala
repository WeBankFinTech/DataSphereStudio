package com.webank.wedatasphere.warehouse

import org.apache.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy
import org.apache.linkis.httpclient.dws.config.{DWSClientConfig, DWSClientConfigBuilder}

import java.util.concurrent.TimeUnit

object DwLayerRemoteClientTest {


  def main(args: Array[String]): Unit = {
    val clientCfg: DWSClientConfig = DWSClientConfigBuilder.newBuilder()
      .addServerUrl("http://gateway:8088")
      .connectionTimeout(30000)
      .discoveryEnabled(true)
      .loadbalancerEnabled(true)
      .maxConnectionSize(5)
      .retryEnabled(false)
      .readTimeout(30000)
      .setAuthenticationStrategy(new StaticAuthenticationStrategy())
      .setAuthTokenKey("hdfs")
      .setAuthTokenValue("hdfs")
      .setDWSVersion("v1")
      .build();



//    val client = new LinkisMetaDataRemoteClient(clientCfg)
//    val action = new ListDwLayerAction
//    val result = client.listLayers(action)
//    println(result.getAll)
//
//    val action1 = new ListDwThemeDomainAction
//    val result1 = client.listThemeDomains(action1)
//    println(result1.getAll)
//
//    val action2 = new ListDwModifierAction
//    val result2 = client.listModifiers(action2)
//    println(result2.getAll)
//
//    val action3 = new ListDwThemeDomainAction
//    val result3 = client.listThemeDomains(action3)
//    println(result3.getAll)
  }

}
