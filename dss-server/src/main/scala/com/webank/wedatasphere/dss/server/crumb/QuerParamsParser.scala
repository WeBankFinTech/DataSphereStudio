/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.server.crumb


import com.webank.wedatasphere.dss.server.entity.CrumbType
import com.webank.wedatasphere.dss.server.entity.CrumbType.CrumbType



object QuerParamsParser {

  def getCrumbType(queryParams: String): CrumbType = {
    queryParams.split("&").size match {
      case 1 => CrumbType.All
      case 2 => CrumbType.SortProject
      case 4 => CrumbType.Project
      case 5 => CrumbType.SortFlow
      case 6 => CrumbType.Flow
    }
  }

  def toArray(queryParams: String): Array[String] = {
    queryParams.split("&")
  }

  def toMap(queryParams: Array[String]): java.util.Map[String, String] = {
    import collection.JavaConverters._
    val regex = ("(.+)=(.+)").r
    queryParams.map {
      _ match {
        case regex(k, v) => k -> v
      }
    }.toMap.asJava
  }

  @Deprecated
  def toQueryString(params: java.util.Map[String, String]): String = {
    import collection.JavaConversions._
    val queryString = params.foldLeft("")((r, p) => s"${r}&${p._1}=${p._2}")
    queryString.substring(1, queryString.length)
  }


  def toQueryString(params:Array[String]):String ={
    val queryString = params.foldLeft("")((r,p) =>s"${r}&${p}")
    queryString.substring(1, queryString.length)
  }
}


/*object Test{
  def main(args: Array[String]): Unit = {
    val a = new util.HashMap[String,String]()
    a.put("a","b")
    a.put("c","d")
    a.put("e","f")
    val b = QuerParamsParser.toQueryString(a)
    print(b.substring(1,b.length))
  }
}*/




