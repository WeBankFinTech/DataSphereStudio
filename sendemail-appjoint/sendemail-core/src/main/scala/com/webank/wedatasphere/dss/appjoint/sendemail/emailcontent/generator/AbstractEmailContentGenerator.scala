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

package com.webank.wedatasphere.dss.appjoint.sendemail.emailcontent.generator

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import com.webank.wedatasphere.dss.appjoint.sendemail.{Email, EmailContentGenerator}
import com.webank.wedatasphere.dss.appjoint.sendemail.{Email, EmailContentGenerator}

/**
  * Created by shanhuang on 2019/10/12.
  */
trait AbstractEmailContentGenerator extends EmailContentGenerator {

  /**
    * 兼容Visualis老版的变量设置方式
    * @param email
    */
  protected def formatSubjectOfOldVersion(email: Email): Unit = {
    var title = email.getSubject
    if (title.contains("YYYY-MM-DD HH:MM:SS")) {
      val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      val timeStr = sdf.format(new Date)
      title = title.replace("YYYY-MM-DD HH:MM:SS", timeStr)
    } else if (title.contains("YYYY-MM-DD-1")) {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")
      val calendar = Calendar.getInstance
      calendar.add(Calendar.DATE, -1)
      val timeStr = sdf.format(calendar.getTime)
      title = title.replace("YYYY-MM-DD-1", timeStr)
    } else if (title.contains("YYYY-MM-DD")) {
      val sdf = new SimpleDateFormat("yyyy-MM-dd")
      val timeStr = sdf.format(new Date)
      title = title.replace("YYYY-MM-DD", timeStr)
    } else {
      val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      val timeStr = sdf.format(new Date)
      title = title + timeStr
    }
    //    if (!title.contains("试运行版")) title = "【试运行版】" + title
    email.setSubject(title)
  }

  protected def formatSubject(email: Email): Unit = {

  }

}
