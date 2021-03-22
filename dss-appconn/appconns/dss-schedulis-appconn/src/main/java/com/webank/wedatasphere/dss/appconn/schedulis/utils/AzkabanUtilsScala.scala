/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.appconn.schedulis.utils

import java.util
import scala.collection.JavaConversions._
import scala.collection.JavaConverters._

/**
  * Created by v_wbjftang on 2019/9/27.
  */
object AzkabanUtilsScala {

  def getRepeatNodeName(nodeList:java.util.List[String]):util.List[String]={
    val res =  nodeList.map(x=>(x,1)).groupBy(x => x._1).map(x =>(x._1,x._2.size)).filter(x=>x._2 >1).map(x=>x._1).toList
    res.asJava
  }

}
