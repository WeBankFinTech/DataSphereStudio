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

package com.webank.wedatasphere.dss.appjoint.sendemail.sender;

import com.google.common.collect.Lists;
import com.webank.bdp.email.client.EmailClient;
import com.webank.bdp.email.client.domain.*;
import com.webank.wedatasphere.dss.appjoint.sendemail.Attachment;
import com.webank.wedatasphere.dss.appjoint.sendemail.Email;
import com.webank.wedatasphere.dss.appjoint.sendemail.exception.EmailSendFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
/**
 * Created by shanhuang on 2019/10/12.
 */
/**
 * Webank行内的邮件sender
 *
 **/

public class EsbEmailSender extends AbstractEmailSender {

    private static final Logger logger = LoggerFactory.getLogger(EsbEmailSender.class);

    @Override
    public void send(Email email) throws EmailSendFailedException {
        EsbEmail esbEmail = parseToEsbEmail(email);
        logger.info("Begin to send Email(" + email.getSubject() + ").");
        String res = null;
        try {
            res = EmailClient.sendEmail(esbEmail);
        } catch (Throwable e) {
            logger.error("Send email failed: ", e);
            EmailSendFailedException ex = new EmailSendFailedException(80001, "Send email failed!");
            ex.initCause(e);
            throw ex;
        }
        logger.info("Send Email(" + email.getSubject() + ") succeed: " + res);
    }

    private EsbEmail parseToEsbEmail(Email email) {
        EsbEmail esbEmail = new EsbEmail(email.getSubject(), Lists.newArrayList(email.getTo()), email.getContent());
        esbEmail.setAttachments(parseToEsbAttachments(email.getAttachments()));
        esbEmail.setBodyFormat(BodyFormat.HTML);
        esbEmail.setEmailType(EmaiType.NORMAL);
        esbEmail.setCarbonCopyList(Lists.newArrayList(email.getCc()));
        esbEmail.setBlindCarbonCopyList(Lists.newArrayList(email.getBcc()));
        esbEmail.setPriority(Priority.NORMAL);
        return esbEmail;
    }

    private List<EmailAttachment> parseToEsbAttachments(Attachment[] attachments) {
        List<EmailAttachment> esbAttachments = Lists.newArrayList();
        for(Attachment attachment: attachments){
            EmailAttachment imageAttachment = new EsbMailAttachment(attachment.getName(), attachment.getBase64Str(), attachment.getMediaType());
            imageAttachment.setContentId(attachment.getName());
            imageAttachment.setLinkResource(true);
            esbAttachments.add(imageAttachment);
        }
        return esbAttachments;
    }

    class EsbMailAttachment extends EmailAttachment {
        private String mediaType;

        public EsbMailAttachment(String name, String data, String mediaType) {
            super(name, data);
            this.mediaType = mediaType;
        }

        public String getMediaType() {
            return mediaType;
        }

        public void setMediaType(String mediaType) {
            this.mediaType = mediaType;
        }
    }
}
