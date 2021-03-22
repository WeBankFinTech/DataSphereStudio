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

package com.webank.wedatasphere.dss.appconn.sendemail.conf

import com.webank.wedatasphere.linkis.common.conf.CommonVars

/**
  * Created by enjoyyin on 2019/10/13.
  */
object SendEmailAppConnConfiguration {

  val EMAIL_SENDER_CLASS = CommonVars("wds.dss.appconn.email.sender.class",
    "com.webank.wedatasphere.dss.appconn.sendemail.email.sender.EsbEmailSender")
  val EMAIL_HOOK_CLASSES = CommonVars("wds.dss.appconn.email.hook.classes", "")

  val EMAIL_IMAGE_HEIGHT = CommonVars("wds.dss.appconn.email.image.height", 500)
  val EMAIL_IMAGE_WIDTH = CommonVars("wds.dss.appconn.email.image.width", 1920)
  val DEFAULT_EMAIL_FROM = CommonVars("wds.dss.appconn.email.from.default", "")
  val DEFAULT_EMAIL_SUFFIX = CommonVars("wds.dss.appconn.email.suffix.default", "@webank.com")

  val DEV_CHECK = CommonVars("wds.dss.appconn.email.dev.check", true)
  val OLD_FORM_ID = CommonVars("wds.dataworkis.appjoint.email.itsm.old.form.id", "10001200")
  val EMAIL_HOST = CommonVars("wds.dss.appconn.email.host", "")
  val EMAIL_PORT = CommonVars("wds.dss.appconn.email.port", "")
  val EMAIL_PROTOCOL = CommonVars("wds.dss.appconn.email.protocol", "smtp")
  val EMAIL_USERNAME = CommonVars("wds.dss.appconn.email.username", "")
  val EMAIL_PASSWORD = CommonVars("wds.dss.appconn.email.password", "")

  val EMAIL_SMTP_AUTH = CommonVars("wds.dss.appconn.email.smtp.auth", "true")
  val EMAIL_SMTP_STARTTLS_ENABLE = CommonVars("wds.dss.appconn.email.smtp.starttls.enable", "true")
  val EMAIL_SMTP_STARTTLS_REQUIRED = CommonVars("wds.dss.appconn.email.smtp.starttls.required", "true")
  val EMAIL_SMTP_SSL_ENABLED = CommonVars("wds.dss.appconn.email.smtp.ssl.enable", "true")
  val EMAIL_SMTP_TIMEOUT = CommonVars("wds.dss.appconn.email.smtp.timeout", "25000")

}
