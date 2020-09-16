package com.webank.wedatasphere.linkis.oneservice.core.jdbc

import java.util

import com.webank.wedatasphere.linkis.common.exception.WarnException
import com.webank.wedatasphere.linkis.common.utils.Logging
import com.webank.wedatasphere.linkis.datasourcemanager.common.protocol.{DsInfoQueryRequest, DsInfoResponse}
import com.webank.wedatasphere.linkis.rpc.Sender

object JdbcUtil extends Logging {

  val sender : Sender = Sender.getSender("dsm-server")
  def getDatasourceInfo(params : util.Map[String, Any]) : (String, String, String) = {
    val datasourceId = params.get("configuration").asInstanceOf[util.Map[String, Any]]
      .getOrDefault("datasource", new util.HashMap[String, Any]())
      .asInstanceOf[util.Map[String, Any]].get("datasourceId")
    logger.info(s"begin to get datasource info from dsm, datasourceId: ${datasourceId}")
    if (datasourceId != null) {
      val ds = sender.ask(DsInfoQueryRequest(String.valueOf(datasourceId), "BDP")) match {
        case r: DsInfoResponse => r
        case warn: WarnException => throw warn
      }
      logger.info(s"get datasource info result: ${ds}")
      if (ds.status) {
        val url = ds.params.get("jdbc.url").asInstanceOf[String]
        val userName = ds.params.get("jdbc.username").asInstanceOf[String]
        val password = ds.params.get("jdbc.password").asInstanceOf[String]
        logger.info(s"get from dsm: url: ${url}, username: ${userName}, password: ${password}")
        return (url, userName, password)
      }
    }

    ("", "", "")
  }

}
