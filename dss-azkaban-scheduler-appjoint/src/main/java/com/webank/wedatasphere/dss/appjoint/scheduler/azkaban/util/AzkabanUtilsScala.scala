package com.webank.wedatasphere.dss.appjoint.scheduler.azkaban.util

import java.util
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
  * Created by cooperyang on 2019/9/27.
  */
object AzkabanUtilsScala {

  def getRepeatNodeName(nodeList:java.util.List[String]):util.List[String]={
    val res =  nodeList.map(x=>(x,1)).groupBy(x => x._1).map(x =>(x._1,x._2.size)).filter(x=>x._2 >1).map(x=>x._1).toList
    res.asJava
  }

}
