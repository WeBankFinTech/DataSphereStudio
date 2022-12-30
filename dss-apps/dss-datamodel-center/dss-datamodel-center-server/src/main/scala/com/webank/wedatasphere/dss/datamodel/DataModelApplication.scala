package com.webank.wedatasphere.dss.datamodel

import com.webank.wedatasphere.dss.common.utils.DSSMainHelper
import org.apache.linkis.DataWorkCloudApplication
import org.apache.linkis.common.utils.{Logging, Utils}

object DataModelApplication extends Logging{
  val userName: String = System.getProperty("user.name")
  val hostName: String = Utils.getComputerName

  def main(args: Array[String]): Unit = {
    val serviceName = System.getProperty("serviceName")//ProjectConf.SERVICE_NAME.getValue
    DSSMainHelper.formatPropertyFiles(serviceName)
    val allArgs = args ++ DSSMainHelper.getExtraSpringOptions
    System.setProperty("hostName", hostName)
    System.setProperty("userName", userName)
    info(s"Ready to start $serviceName with args: ${allArgs.toList}.")
    println(s"Test Ready to start $serviceName with args: ${allArgs.toList}.")
    DataWorkCloudApplication.main(allArgs)
  }
}
