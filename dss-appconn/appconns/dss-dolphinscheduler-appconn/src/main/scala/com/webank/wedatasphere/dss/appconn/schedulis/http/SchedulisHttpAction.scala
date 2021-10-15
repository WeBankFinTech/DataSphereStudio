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

package com.webank.wedatasphere.dss.appconn.schedulis.http

import java.io.{File, InputStream}
import java.util

import com.webank.wedatasphere.dss.appconn.schedulis.conf.SchedulisConf
import com.webank.wedatasphere.linkis.httpclient.request.{GetAction, HttpAction, POSTAction, UploadAction, UserAction}

/**
 * created by cooperyang on 2020/11/16
 * Description:
 */
trait SchedulisHttpAction extends UserAction{

  private var user:String = _

  override def setUser(user: String): Unit = this.user = user

  override def getUser: String = this.user

}

abstract class SchedulisGetAction extends GetAction with SchedulisHttpAction


abstract class ScheudlisPostAction extends POSTAction with SchedulisHttpAction{

  override def getRequestPayload: String = SchedulisConf.gson.toJson(getRequestPayloads)

}




case class SchedulisUploadAction(filePaths:Array[String],
                           _inputStreams:util.Map[String,InputStream],uploadUrl:String) extends ScheudlisPostAction with UploadAction with SchedulisHttpAction{

  private val streamNames = new util.HashMap[String,String]

  override val files: util.Map[String, String] = {
    if (null == filePaths || filePaths.length == 0) new util.HashMap[String,String]() else{
      val map = new java.util.HashMap[String, String]
      filePaths foreach {
        filePath => val arr = filePath.split(File.separator)
          val fileName = arr(arr.length - 1)
          map.put("file", filePath)
      }
      map
    }
  }

  override def inputStreams: util.Map[String, InputStream] = _inputStreams

  override def inputStreamNames: util.Map[String, String] = streamNames

  private var _user:String = _

  override def setUser(user: String): Unit = this._user = user

  override def getUser: String = this._user

  override def getRequestPayload: String = ""

  override def getURL: String = uploadUrl
}

class SchedulisCreateProjectAction(url:String) extends ScheudlisPostAction{

  override def getURL: String = url

}


