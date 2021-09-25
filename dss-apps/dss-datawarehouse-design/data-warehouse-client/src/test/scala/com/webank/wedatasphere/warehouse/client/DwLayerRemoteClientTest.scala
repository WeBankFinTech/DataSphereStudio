package com.webank.wedatasphere.warehouse.client

import com.webank.wedatasphere.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy
import com.webank.wedatasphere.linkis.httpclient.dws.config.{DWSClientConfig, DWSClientConfigBuilder}
import com.webank.wedatasphere.warehouse.client.action.{ListDwLayerAction, ListDwModifierAction, ListDwThemeDomainAction}

import java.util.concurrent.TimeUnit

object DwLayerRemoteClientTest {
  val serverUrl: String = s"http://gateway:29001"
  val clientConfig: DWSClientConfig = DWSClientConfigBuilder.newBuilder()
    .addServerUrl(serverUrl)
    .connectionTimeout(30000L)
    .discoveryEnabled(true)
    .discoveryFrequency(1L, TimeUnit.MINUTES)
    .loadbalancerEnabled(true)
    .maxConnectionSize(5)
    .retryEnabled(false)
    .readTimeout(30000L)
    .setAuthenticationStrategy(new StaticAuthenticationStrategy())
    .setAuthTokenKey("hdfs")
    .setAuthTokenValue("hdfs")
    .setDWSVersion("v1")
    .build()

  val client = new GovernanceDwRemoteClient(clientConfig)

  def main(args: Array[String]): Unit = {
    val action = new ListDwLayerAction
    val result = client.listLayers(action)
    println(result.getAll)

    val action1 = new ListDwThemeDomainAction
    val result1 = client.listThemeDomains(action1)
    println(result1.getAll)

    val action2 = new ListDwModifierAction
    val result2 = client.listModifiers(action2)
    println(result2.getAll)

    val action3 = new ListDwThemeDomainAction
    val result3 = client.listThemeDomains(action3)
    println(result3.getAll)
  }

}
