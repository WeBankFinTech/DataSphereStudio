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

package com.webank.wedatasphere.dss.appconn.sendemail.email;

import com.webank.wedatasphere.dss.appconn.sendemail.email.domain.Attachment;

public interface Email {

    String getContent();
    void setContent(String content);

    Attachment[] getAttachments();
    void setAttachments(Attachment[] attachments);

    String getSubject();
    void setSubject(String subject);

    String getFrom();
    void setFrom(String from);

    String getTo();
    void setTo(String to);

    String getCc();
    void setCc(String cc);

    String getBcc();
    void setBcc(String bcc);

}
