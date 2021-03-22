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

package com.webank.wedatasphere.dss.appconn.sendemail.email.sender;

import com.webank.wedatasphere.dss.appconn.sendemail.conf.SendEmailAppConnConfiguration;
import com.webank.wedatasphere.dss.appconn.sendemail.email.Email;
import com.webank.wedatasphere.dss.appconn.sendemail.email.domain.Attachment;
import com.webank.wedatasphere.dss.appconn.sendemail.exception.EmailSendFailedException;
import java.util.Properties;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * Spring 通用的邮件发送sender
 *
 **/
public class SpringJavaEmailSender extends AbstractEmailSender {

    private static final Logger logger = LoggerFactory.getLogger(SpringJavaEmailSender.class);

    private JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

    public SpringJavaEmailSender() {
        javaMailSender.setHost(SendEmailAppConnConfiguration.EMAIL_HOST().getValue());
        javaMailSender.setPort(Integer.parseInt(SendEmailAppConnConfiguration.EMAIL_PORT().getValue()));
        javaMailSender.setProtocol(SendEmailAppConnConfiguration.EMAIL_PROTOCOL().getValue());
        javaMailSender.setUsername(SendEmailAppConnConfiguration.EMAIL_USERNAME().getValue());
        javaMailSender.setPassword(SendEmailAppConnConfiguration.EMAIL_PASSWORD().getValue());
        try {
            Properties prop = new Properties();
            prop.put("mail.smtp.auth", Boolean.parseBoolean(SendEmailAppConnConfiguration.EMAIL_SMTP_AUTH().getValue()));
            prop.put("mail.smtp.starttls.enable", Boolean.parseBoolean(SendEmailAppConnConfiguration.EMAIL_SMTP_STARTTLS_ENABLE().getValue()));
            prop.put("mail.smtp.starttls.required", Boolean.parseBoolean(SendEmailAppConnConfiguration.EMAIL_SMTP_STARTTLS_REQUIRED().getValue()));
            prop.put("mail.smtp.ssl.enable", Boolean.parseBoolean(SendEmailAppConnConfiguration.EMAIL_SMTP_SSL_ENABLED().getValue()));
            prop.put("mail.smtp.timeout", Integer.parseInt(SendEmailAppConnConfiguration.EMAIL_SMTP_TIMEOUT().getValue()));
            javaMailSender.setJavaMailProperties(prop);
        } catch (Exception e) {
            logger.error("Failed to read mail properties, roll back to default values.", e);
        }
    }

    @Override
    public void send(Email email) throws EmailSendFailedException {
        logger.info("Begin to send Email({}).", email.getSubject());
        try {
            javaMailSender.send(parseToMimeMessage(email));
        } catch (Exception e) {
            logger.error("Send email failed: ", e);
            EmailSendFailedException ex = new EmailSendFailedException(80001, "Send email failed!");
            ex.initCause(e);
            throw ex;
        }
        logger.info("Send Email({}) succeed.", email.getSubject());
    }

    private MimeMessage parseToMimeMessage(Email email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            if(StringUtils.isBlank(email.getFrom())) {
                messageHelper.setFrom(SendEmailAppConnConfiguration.DEFAULT_EMAIL_FROM().getValue());
            } else {
                messageHelper.setFrom(email.getFrom());
            }
            messageHelper.setSubject(email.getSubject());
            messageHelper.setTo(email.getTo());
            messageHelper.setCc(email.getCc());
            messageHelper.setBcc(email.getBcc());
            for(Attachment attachment: email.getAttachments()){
                messageHelper.addAttachment(attachment.getName(), new ByteArrayDataSource(attachment.getBase64Str(), attachment.getMediaType()));
            }
            messageHelper.setText(email.getContent(), true);
        } catch (Exception e) {
            logger.error("Send mail failed", e);
        }
        return message;
    }
}
