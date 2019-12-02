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

package com.webank.wedatasphere.dss.appjoint.sendemail.conf

import com.webank.wedatasphere.linkis.common.conf.CommonVars

/**
  * Created by shanhuang on 2019/10/12.
  */
object SendEmailAppJointConfiguration {

  val EMAIL_IMAGE_HEIGHT = CommonVars("wds.dataworkis.appjoint.email.image.height", 500)
  val EMAIL_IMAGE_WIDTH = CommonVars("wds.dataworkis.appjoint.email.image.width", 1920)
  val DEFAULT_EMAIL_FROM = CommonVars("wds.dataworkis.appjoint.email.from.default", "")

  val EMAIL_HOST = CommonVars("wds.dataworkis.appjoint.email.host", "")
  val EMAIL_PORT = CommonVars("wds.dataworkis.appjoint.email.port", "")
  val EMAIL_PROTOCOL = CommonVars("wds.dataworkis.appjoint.email.protocol", "smtp")
  val EMAIL_USERNAME = CommonVars("wds.dataworkis.appjoint.email.username", "")
  val EMAIL_PASSWORD = CommonVars("wds.dataworkis.appjoint.email.password", "")

  val EMAIL_SMTP_AUTH = CommonVars("wds.dataworkis.appjoint.email.smtp.auth", "true")
  val EMAIL_SMTP_STARTTLS_ENABLE = CommonVars("wds.dataworkis.appjoint.email.smtp.starttls.enable", "true")
  val EMAIL_SMTP_STARTTLS_REQUIRED = CommonVars("wds.dataworkis.appjoint.email.smtp.starttls.required", "true")
  val EMAIL_SMTP_SSL_ENABLED = CommonVars("wds.dataworkis.appjoint.email.smtp.ssl.enable", "true")
  val EMAIL_SMTP_TIMEOUT = CommonVars("wds.dataworkis.appjoint.email.smtp.timeout", "25000")

}
