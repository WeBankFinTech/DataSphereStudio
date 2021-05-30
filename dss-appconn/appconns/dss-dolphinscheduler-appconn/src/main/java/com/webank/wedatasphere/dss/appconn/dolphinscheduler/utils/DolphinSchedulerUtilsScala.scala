

package com.webank.wedatasphere.dss.appconn.dolphinscheduler.utils

import java.util

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._


object DolphinSchedulerUtilsScala {

  def getRepeatNodeName(nodeList: java.util.List[String]): util.List[String] = {
    val res = nodeList.map(x => (x, 1)).groupBy(x => x._1).map(x => (x._1, x._2.size)).filter(x => x._2 > 1).map(x => x._1).toList
    res.asJava
  }

}
