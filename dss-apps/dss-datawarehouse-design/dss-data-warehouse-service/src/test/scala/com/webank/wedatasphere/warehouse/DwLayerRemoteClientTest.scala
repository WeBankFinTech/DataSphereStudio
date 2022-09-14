package com.webank.wedatasphere.warehouse

import org.apache.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy
import org.apache.linkis.httpclient.dws.config.{DWSClientConfig, DWSClientConfigBuilder}
import java.util.concurrent.TimeUnit

import com.webank.wedatasphere.dss.datamodel.center.client.impl.LinkisDatamodelCenterRemoteClient
import com.webank.wedatasphere.dss.datamodel.center.client.request.LayersReferenceAction
import com.webank.wedatasphere.dss.datamodel.center.client.response.LayersReferenceResult
import org.apache.linkis.datasource.client.impl.LinkisMetaDataRemoteClient

object DwLayerRemoteClientTest {


  def main(args: Array[String]): Unit = {
    val clientCfg: DWSClientConfig = DWSClientConfigBuilder.newBuilder()
      .addServerUrl("http://192.168.0.120:9001")
      .connectionTimeout(30000)
      .discoveryEnabled(true)
      .loadbalancerEnabled(true)
      .maxConnectionSize(5)
      .retryEnabled(false)
      .readTimeout(30000)
      .setAuthenticationStrategy(new StaticAuthenticationStrategy())
      .setAuthTokenKey("ws")
      .setAuthTokenValue("***REMOVED***")
      .setDWSVersion("v1")
      .build();

//    val dataModelRemoteClient = LinkisRemoteClientHolder.getDataModelRemoteClient
//    val action = new LayersReferenceAction.Builder().setUser(username).setName(dwLayer.getEnName).build
//    val layersReferenceResult = dataModelRemoteClient.layersReference(action)

    val client = new LinkisDatamodelCenterRemoteClient(clientCfg);
    val action = new LayersReferenceAction;
    action.setUser("hadoop")
    action.setParameter("name","test")
    val result = client.layersReference(action);

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
