package com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client

import com.google.gson.Gson
import com.webank.wedatasphere.dss.linkis.node.execution.execution.impl.LinkisNodeExecutionImpl
import com.webank.wedatasphere.dss.linkis.node.execution.job.Job
import com.webank.wedatasphere.dss.linkis.node.execution.log.LinkisJobExecutionLog
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client.conf.LinkisJobTypeConf
import com.webank.wedatasphere.dss.plugins.dolphinscheduler.linkis.client.job.DolphinSchedulerJobBuilder
import org.apache.linkis.common.utils.Logging
import org.apache.commons.io.IOUtils

import java.io.{FileInputStream, InputStream, InputStreamReader}
import java.net.URL
import java.util.{Properties, Map => JMap}
import scala.io.{BufferedSource, Source, StdIn}

object LinkisDolphinSchedulerClient extends Logging {
  val DEFAULT_PROPERTY_FILE_NAME = "linkis.properties"
  val DEFAULT_CONFIG_DIR = "conf"
  val CHARSET_NAME = "utf-8"

  val logObj: LinkisJobExecutionLog = new LinkisJobExecutionLog {
    override def info(message: Any, t: Throwable): Unit = logger.info(message.toString, t)

    override def warn(message: Any, t: Throwable): Unit = logger.warn(message.toString, t)

    override def error(message: Any, t: Throwable): Unit = logger.error(message.toString, t)
  }

  def main(args: Array[String]): Unit = {
    val url: URL = getClass.getClassLoader.getResource("linkis.properties")
    val source: BufferedSource = Source.fromURL(url)
    val gatewayUrl = source.getLines().toList.filter(_.startsWith("wds.linkis.gateway.url")).map(_.stripPrefix("wds.linkis.gateway.url=")).head
    source.close()
    println("************************************Gateway URL is: " + gatewayUrl)
    println("************************************Args.head is: " + args.head)
    val jobProps: JMap[String, String] = new Gson().fromJson(args.head, classOf[JMap[String, String]])
    /** 本地断点调试代码
    //val gatewayUrl ="http://172.24.2.63:9001"
    //val json =
      """
        |{"proxy.user":"hadoop","resources":"[{\"fileName\":\"62482983-8c5a-4e26-b659-1fe8690350f0.sh\",\"resourceId\":\"8544d640-bf06-44de-b934-71f1acc3b251\",\"version\":\"v000003\"}]","type":"linkis","params":"{\"variable\":{\"run_date\":\"20220210\"},\"configuration\":{\"special\":{},\"runtime\":{},\"startup\":{}}}","command":"{\"script\":\"62482983-8c5a-4e26-b659-1fe8690350f0.sh\"}","linkistype":"linkis.shell.sh"}
        |""".stripMargin
    //val jobProps: JMap[String, String] = new Gson().fromJson(json, classOf[JMap[String, String]])
    */
    jobProps.put("linkis.version","1.0.3")
    jobProps.put("wds.linkis.gateway.url.v1", gatewayUrl)
    jobProps.put("wds.linkis.client.flow.adminuser", "hadoop")
    jobProps.put("wds.linkis.client.flow.author.user.token", "WS-AUTH")
    val job: Job = new DolphinSchedulerJobBuilder(jobProps).build()
    job.setLogObj(logObj)
    val execution = LinkisNodeExecutionImpl.getLinkisNodeExecution

    execution.runJob(job)
    execution.waitForComplete(job)


    //    val resultSize = execution.getResultSize(job)
    //    val result = LinkisNodeExecutionImpl.getLinkisNodeExecution.getResult(job, 0, resultSize)
    //    info(s"\n\n+++++++++++++++++++++执行成功+++++++++++++++++++++++\n执行结果：\n\n$result\n\n**************************************************")
    job.getLogObj().info(s"\n\n************************************ 执行成功 ************************************")
  }

  def parseKVMapFromArgs(args: Array[String]): JMap[String, String] = {
    val ret: JMap[String, String] = new java.util.HashMap[String, String]
    for (arg <- args) {
      val kvArr: Array[String] = arg.split("=", 2)
      if (kvArr.length == 2) ret.put(kvArr(0), kvArr(1))
    }
    ret
  }


  def parseConfigFromLinkisPropertiesFile(filePath: String): Properties = {
    val config = new Properties
    var inputStream: InputStream = null
    var inputStreamReader: InputStreamReader = null
    try {
      inputStream = new FileInputStream(filePath)
      inputStreamReader = new InputStreamReader(inputStream, CHARSET_NAME)
      config.load(inputStreamReader)
    } catch {
      case e: Exception =>
        error("Can't load " + filePath, e)
    } finally {
      IOUtils.closeQuietly(inputStream)
      IOUtils.closeQuietly(inputStreamReader)
    }
    config
  }
}
