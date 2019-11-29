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

package com.webank.wedatasphere.dss.appjoint.sendemail.emailcontent.parser

import com.webank.wedatasphere.dss.appjoint.sendemail.email.MultiContentEmail
import com.webank.wedatasphere.dss.appjoint.sendemail.emailcontent.HtmlEmailContent
import org.springframework.stereotype.Component

/**
  * Created by shanhuang on 2019/10/12.
  */

object HtmlEmailContentParser extends AbstractEmailContentParser[HtmlEmailContent] {
  override protected def parseEmailContent(emailContent: HtmlEmailContent,
                                           multiContentEmail: MultiContentEmail): Unit = {
    getFirstLineRecord(emailContent).foreach(emailContent.setContent)
  }
}
