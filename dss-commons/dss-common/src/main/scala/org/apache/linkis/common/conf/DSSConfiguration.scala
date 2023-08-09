package org.apache.linkis.common.conf

import org.apache.commons.io.IOUtils
import org.apache.linkis.common.utils.{Logging, Utils}

import java.io.{File, FileInputStream, IOException, InputStream}
import java.util
import java.util.Properties
import java.util.stream.Collectors
import scala.collection.JavaConverters._

object DSSConfiguration extends Logging {

  private val MAPPER_LOCATIONS = "wds.linkis.server.mybatis.mapperLocations"
  private val TYPE_ALIASES_PACKAGE = "wds.linkis.server.mybatis.typeAliasesPackage"
  private val BASE_PACKAGE = "wds.linkis.server.mybatis.BasePackage"
  private val RESTFUL_SCAN_PACKAGES = "wds.linkis.server.restful.scan.packages"

  def getAllProperties: Properties = BDPConfiguration.properties

  def addLegacyConfiguration(serverConfs: Array[String]): Unit = {
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
        val config = new Properties
        initConfig(config, serverConfFileURL.getPath)
        config.asScala.toMap.foreach { case (k, v) =>
          k match {
            case MAPPER_LOCATIONS => mapperLocationList.add(v)
            case TYPE_ALIASES_PACKAGE => typeAliasesPackageList.add(v)
            case BASE_PACKAGE => basePackageList.add(v)
            case RESTFUL_SCAN_PACKAGES => restfulScanPackagesList.add(v)
            case _ => setConfig(k, v)
          }
        }
      } else {
        logger.warn(
          s"**************** Notice: The DSS serverConf file $serverConf does not exist! *******************"
        )
      }
    }
    setConfig(MAPPER_LOCATIONS, distinctStr(String.join(",", mapperLocationList)))
    setConfig(TYPE_ALIASES_PACKAGE, distinctStr(String.join(",", typeAliasesPackageList)))
    setConfig(BASE_PACKAGE, distinctStr(String.join(",", basePackageList)))
    setConfig(RESTFUL_SCAN_PACKAGES, distinctStr(String.join(",", restfulScanPackagesList)))
  }

  private def distinctStr(str: String): String = {
    String.join(",", util.Arrays.stream(str.split(',')).collect(Collectors.toSet()))
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
    logger.info(s"try to set a config, key:$key, value:$value")
    BDPConfiguration.set(key, value)
  }

  def setSpringApplicationName(name: String): Unit = {
    BDPConfiguration.set("spring.spring.application.name", name)
  }

}
