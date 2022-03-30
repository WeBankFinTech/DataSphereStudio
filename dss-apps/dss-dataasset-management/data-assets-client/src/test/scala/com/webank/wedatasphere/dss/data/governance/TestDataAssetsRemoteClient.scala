package com.webank.wedatasphere.dss.data.governance

import com.webank.wedatasphere.dss.data.governance.impl.LinkisDataAssetsRemoteClient
import com.webank.wedatasphere.dss.data.governance.request.{GetHiveTblPartitionAction, GetTblPartInfoByNameAction, SearchHiveDbAction}
import org.apache.linkis.httpclient.dws.authentication.TokenAuthenticationStrategy
import org.apache.linkis.httpclient.dws.config.DWSClientConfigBuilder

import java.util.concurrent.TimeUnit
import scala.Console.println


object TestDataAssetsRemoteClient {
  def main(args: Array[String]): Unit = {
    val clientConfig = DWSClientConfigBuilder.newBuilder()
      .addServerUrl("http://192.168.0.25:8088")
      .connectionTimeout(30000)
      .discoveryEnabled(false)
      .discoveryFrequency(1,TimeUnit.MINUTES)
      .loadbalancerEnabled(true)
      .maxConnectionSize(5)
      .retryEnabled(false)
      .readTimeout(30000)
      .setAuthenticationStrategy(new TokenAuthenticationStrategy())
      .setAuthTokenKey("hdfs")
      .setAuthTokenValue("BML-AUTH")
      .setDWSVersion("v1")
      .build()

    val dataAssetsClient = new LinkisDataAssetsRemoteClient(clientConfig)
//
//    val searchHiveTblResult = dataAssetsClient.searchHiveTbl(SearchHiveTblAction.builder().setUser("hdfs").setQuery("").setLimit(10).setOffset(0).setOwner("undefined").build()).getHiveList
//    println(searchHiveTblResult)
//
    val searchHiveDbResult = dataAssetsClient.searchHiveDb(SearchHiveDbAction.builder().setUser("hdfs").setQuery("").setLimit(10).setOffset(0).setOwner("undefined").build()).getHiveList
    println(searchHiveDbResult)
//
//    val hiveTblBasicResult = dataAssetsClient
//  .getHiveTblBasic(GetHiveTblBasicAction.builder().setUser("hdfs").setGuid("27920dc8-1eef-4d7d-9423-b5967d9e2d33").build())
//  .result
//    println(hiveTblBasicResult)
//
//    val hiveTblPartitionResult = dataAssetsClient.getHiveTblPartition(GetHiveTblPartitionAction.builder().setUser("hdfs").setGuid("a3be4a97-6465-4c3d-adee-76dfa662e531").build()).result
//    println(hiveTblPartitionResult)


//
//    val hiveTblCreateResult = dataAssetsClient.getHiveTblCreate(GetHiveTblCreateAction.builder().setUser("hdfs").setGuid("a3be4a97-6465-4c3d-adee-76dfa662e531").build()).result
//    println(hiveTblCreateResult)
//
//    val deleteModelTypeResult = dataAssetsClient.deleteModelType(DeleteModelTypeAction.builder().setUser("hdfs").setType(ClassificationConstant.INDICATOR).setName("test004").build())
//    println(deleteModelTypeResult.getResult)
//
//    val createModelTypeResult = dataAssetsClient.createModelType(CreateModelTypeAction.builder().setUser("hdfs").setType(ClassificationConstant.INDICATOR).setName("test004").build()).getInfo
//    println(createModelTypeResult)
//
//    val  updateModelTypeResult =dataAssetsClient.updateModelType(UpdateModelTypeAction.builder().setUser("hdfs").setType(ClassificationConstant.INDICATOR).setName("test000NEW").setOrgName("test000").build())
//    println(updateModelTypeResult.getInfo.getGuid)
//    println(updateModelTypeResult.getInfo.getName)
//
//    val bindResult = dataAssetsClient.bindModelType(BindModelTypeAction.builder()
//        .setUser("hdfs")
//        .setTableName("default.test02")
//        .setModelName("test001")
//        .setModelType(ClassificationConstant.INDICATOR).build())
//      println(bindResult.getResult)

//     val unBindResult = dataAssetsClient.unBindModelType(UnBindModelTypeAction.builder()
//            .setUser("hdfs")
//            .setTableName("default.test02")
//            .setModelName("test001")
//            .setModelType(ClassificationConstant.INDICATOR).build())
//    println(unBindResult.getResult)

//    val hiveTableSizeResult = dataAssetsClient.searchHiveTblSize(HiveTblSizeAction.builder()
//      .setUser("hdfs")
//      .setTableName("test04")
//      .setDbName("default").build())
//     println(hiveTableSizeResult.getResult)
//    val hiveTableStatsResult = dataAssetsClient.searchHiveTblStats(HiveTblStatsAction.builder()
//      .setUser("hdfs")
//      .setTableName("test04")
//      .setDbName("default").build())
//    println(hiveTableStatsResult.getResult)

//    val searchLabelResult = dataAssetsClient.searchLabel(SearchLabelAction.builder().setUser("hdfs").setQuery("t").build());
//    println(searchLabelResult.getLabelList)
//
//    val createLabelResult = dataAssetsClient.createLabel(CreateLabelAction.builder().setUser("hdfs").setName("label007").build())
//    println(createLabelResult.getInfo)
//
//    TimeUnit.SECONDS.sleep(5)
//
//    val bindLabelResult = dataAssetsClient.bindLabel(BindLabelAction.builder().setUser("hdfs").setLabel("term1").setTableName("linkis_db.linkis_test01").build())
//    println(bindLabelResult.getResult)
////
//    val unbindLabelResult = dataAssetsClient.unBindLabel(UnBindLabelAction.builder().setUser("hdfs").setLabel("term1").setTableName("linkis_db.linkis_test01").build())
//    println(unbindLabelResult.getResult)
//
//    val updateLabelResult = dataAssetsClient.updateLabel(UpdateLabelAction.builder().setUser("hdfs").setName("label008").setOrgName("label007").build())
//    println(updateLabelResult.getInfo)
//
//    val deleteLabelResult = dataAssetsClient.deleteLabel(DeleteLabelAction.builder().setUser("hdfs").setName("label008").build())
//    println(deleteLabelResult.getResult)

//
//    println(hiveTableStatsResult.getResult)

//    val partInfo = dataAssetsClient.getHiveTblPartInfoByNameResult(GetTblPartInfoByNameAction.builder().setUser("hdfs").setDbName("linkis_db").setTableName("linkis_partitions").build())
//    println(partInfo.getInfo)
  }
}
