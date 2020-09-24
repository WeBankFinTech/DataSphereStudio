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

import com.webank.wedatasphere.dss.appjoint.sendemail.conf.SendEmailAppJointConfiguration;
import com.webank.wedatasphere.dss.appjoint.sendemail.Attachment;
import com.webank.wedatasphere.dss.appjoint.sendemail.Email;
import com.webank.wedatasphere.dss.appjoint.sendemail.exception.EmailSendFailedException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import javax.mail.Header;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by shanhuang on 2019/10/12.
 */
public class SpringJavaEmailSender extends AbstractEmailSender {

    private static final Logger logger = LoggerFactory.getLogger(SpringJavaEmailSender.class);

    private JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

    public SpringJavaEmailSender() {
        javaMailSender.setHost(SendEmailAppJointConfiguration.EMAIL_HOST().getValue());
        javaMailSender.setPort(Integer.parseInt(SendEmailAppJointConfiguration.EMAIL_PORT().getValue()));
        javaMailSender.setProtocol(SendEmailAppJointConfiguration.EMAIL_PROTOCOL().getValue());
        javaMailSender.setUsername(SendEmailAppJointConfiguration.EMAIL_USERNAME().getValue());
        javaMailSender.setPassword(SendEmailAppJointConfiguration.EMAIL_PASSWORD().getValue());
        try {
            Properties prop = new Properties();
            prop.put("mail.smtp.auth", SendEmailAppJointConfiguration.EMAIL_SMTP_AUTH().getValue());
            prop.put("mail.smtp.starttls.enable", SendEmailAppJointConfiguration.EMAIL_SMTP_STARTTLS_ENABLE().getValue());
            prop.put("mail.smtp.starttls.required", SendEmailAppJointConfiguration.EMAIL_SMTP_STARTTLS_REQUIRED().getValue());
            prop.put("mail.smtp.ssl.enable", SendEmailAppJointConfiguration.EMAIL_SMTP_SSL_ENABLED().getValue());
            prop.put("mail.smtp.timeout", SendEmailAppJointConfiguration.EMAIL_SMTP_TIMEOUT().getValue());
            if(Boolean.parseBoolean(SendEmailAppJointConfiguration.EMAIL_SMTP_SSL_ENABLED().getValue())){
                prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            }
            javaMailSender.setJavaMailProperties(prop);
        } catch (Exception e) {
            logger.error("Failed to read mail properties, roll back to default values.", e);
        }
    }

    @Override
    public void send(Email email) throws EmailSendFailedException {
        logger.info("Begin to send Email(" + email.getSubject() + ").");
        try {
            MailcapCommandMap mc = (MailcapCommandMap)CommandMap.getDefaultCommandMap();
            mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
            mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
            mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
            mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
            mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
            CommandMap.setDefaultCommandMap(mc);
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            javaMailSender.send(parseToMimeMessage(email));
        } catch (Exception e) {
            logger.error("Send email failed: ", e);
            EmailSendFailedException ex = new EmailSendFailedException(80001, "Send email failed!");
            ex.initCause(e);
            throw ex;
        }
        logger.info("Send Email(" + email.getSubject() + ") succeed.");
    }

    private MimeMessage parseToMimeMessage(Email email) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "utf-8");
            logger.info("use default from");
            messageHelper.setFrom(SendEmailAppJointConfiguration.DEFAULT_EMAIL_FROM().getValue());
            messageHelper.setSubject(email.getSubject());
            if(StringUtils.isNotBlank(email.getTo())){
                logger.info("message to: " + email.getTo());
                messageHelper.setTo(email.getTo().split(";"));
            }
            if(StringUtils.isNotBlank(email.getCc())){
                logger.info("message cc: " + email.getCc());
                messageHelper.setCc(email.getCc().split(";"));
            }
            if(StringUtils.isNotBlank(email.getBcc())){
                logger.info("message bcc: " + email.getBcc());
                messageHelper.setBcc(email.getBcc().split(";"));
            }
            for(Attachment attachment: email.getAttachments()){
                //messageHelper.addAttachment(attachment.getName(), new ByteArrayDataSource(attachment.getBase64Str(), attachment.getMediaType()));
                messageHelper.addInline(attachment.getName(), attachment.getFile());
            }
            logger.info("mail content: " + email.getContent());
            messageHelper.setText(email.getContent(), true);
        } catch (Exception e) {
            logger.error("Send mail failed", e);
        }
        return message;
    }
}
