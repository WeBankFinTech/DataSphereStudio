package com.webank.wedatasphere.dss.datamodel.center.client

import com.webank.wedatasphere.dss.datamodel.center.client.impl.LinkisDatamodelCenterRemoteClient
import com.webank.wedatasphere.dss.datamodel.center.client.request.{CyclesReferenceAction, LayersReferenceAction, ModifiersReferenceAction, ThemesReferenceAction}
import org.apache.linkis.httpclient.dws.config.DWSClientConfigBuilder

import java.util.concurrent.TimeUnit

object TestDatamodelCenterRemoteClient {
  def main(args: Array[String]): Unit = {
    val clientConfig = DWSClientConfigBuilder.newBuilder()
      .addServerUrl("http://localhost:9321")
      .connectionTimeout(30000)
      .discoveryEnabled(false)
      .discoveryFrequency(1,TimeUnit.MINUTES)
      .loadbalancerEnabled(true)
      .maxConnectionSize(5)
      .retryEnabled(false)
      .readTimeout(30000)
      .setAuthenticationStrategy(null)
      .setAuthTokenKey("hdfs")
      .setAuthTokenValue("hdfs")
      .setDWSVersion("v1")
      .build()

    val datamodelCenterClient = new LinkisDatamodelCenterRemoteClient(clientConfig)

    val themesResult = datamodelCenterClient
      .themesReference(ThemesReferenceAction.builder().setUser("hdfs").setName("主题").build())
      .result
    println(themesResult)

    val layersResult = datamodelCenterClient
      .layersReference(LayersReferenceAction.builder().setUser("hdfs").setName("DWS").build())
      .result
    println(layersResult)

    val cyclesResult = datamodelCenterClient
      .cyclesReference(CyclesReferenceAction.builder().setUser("hdfs").setName("Once").build())
      .result
    println(cyclesResult)

    val modifiersResult = datamodelCenterClient
      .modifiersReference(ModifiersReferenceAction.builder().setUser("hdfs").setName("在商场").build())
      .result
    println(modifiersResult)
  }
}
