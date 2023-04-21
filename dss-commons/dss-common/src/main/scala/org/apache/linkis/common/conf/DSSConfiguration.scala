package org.apache.linkis.common.conf

import org.apache.commons.io.IOUtils
import org.apache.linkis.common.utils.{Logging, Utils}

import java.io.{File, FileInputStream, IOException, InputStream}
import java.util
import java.util.Properties
import scala.collection.JavaConverters._

object DSSConfiguration extends Logging {

  private val MAPPER_LOCATIONS = "wds.linkis.server.mybatis.mapperLocations"
  private val TYPE_ALIASES_PACKAGE = "wds.linkis.server.mybatis.typeAliasesPackage"
  private val BASE_PACKAGE = "wds.linkis.server.mybatis.BasePackage"
  private val RESTFUL_SCAN_PACKAGES = "wds.linkis.server.restful.scan.packages"

  def getAllProperties: Properties = BDPConfiguration.properties

  def addLegacyConfiguration(serverConfs: Array[String]): Unit = {
    val config = new Properties
    val mapperLocationList = new util.ArrayList[String]();
    val typeAliasesPackageList = new util.ArrayList[String]();
    val basePackageList = new util.ArrayList[String]();
    val restfulScanPackagesList = new util.ArrayList[String]();

    serverConfs.foreach { serverConf =>
      val serverConfFileURL = getClass.getClassLoader.getResource(serverConf)
      if (serverConfFileURL != null && new File(serverConfFileURL.getPath).exists) {
        logger.info(
          s"*********************** Notice: The DSS serverConf file is $serverConf ! ******************"
        )
        initConfig(config, serverConfFileURL.getPath)
      } else {
        logger.warn(
          s"**************** Notice: The DSS serverConf file $serverConf does not exist! *******************"
        )
      }
    }
    config.asScala.toMap.foreach { case (k, v) =>
      k match {
        case MAPPER_LOCATIONS => mapperLocationList.add(v)
        case TYPE_ALIASES_PACKAGE => typeAliasesPackageList.add(v)
        case BASE_PACKAGE => basePackageList.add(v)
        case RESTFUL_SCAN_PACKAGES => restfulScanPackagesList.add(v)
        case _ => BDPConfiguration.set(k, v)
      }
    }
    BDPConfiguration.set(MAPPER_LOCATIONS, String.join(",", mapperLocationList))
    BDPConfiguration.set(TYPE_ALIASES_PACKAGE, String.join(",", typeAliasesPackageList))
    BDPConfiguration.set(BASE_PACKAGE, String.join(",", basePackageList))
    BDPConfiguration.set(RESTFUL_SCAN_PACKAGES, String.join(",", restfulScanPackagesList))
  }

  private def initConfig(config: Properties, filePath: String): Unit = {
    var inputStream: InputStream = null
    Utils.tryFinally {
      Utils.tryCatch {
        inputStream = new FileInputStream(filePath)
        config.load(inputStream)
      } { case e: IOException =>
        logger.error("Can't load " + filePath, e)
      }
    } {
      IOUtils.closeQuietly(inputStream)
    }
  }

  def setConfig(key: String, value: String): Unit = {
    BDPConfiguration.set(key, value)
  }

  def setSpringApplicationName(name: String): Unit = {
    BDPConfiguration.set("spring.spring.application.name", name)
  }

}
