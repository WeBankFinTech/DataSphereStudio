/*
 * Copyright 2019 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.dss.appconn.sendemail.email.sender;

import com.webank.wedatasphere.dss.appconn.sendemail.email.Email;
import com.webank.wedatasphere.dss.appconn.sendemail.email.domain.Attachment;
import com.webank.wedatasphere.dss.appconn.sendemail.exception.EmailSendFailedException;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.util.Base64;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;
import java.util.Map;
import java.util.Properties;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.webank.wedatasphere.dss.appconn.sendemail.conf.SendEmailAppConnConfiguration.*;

public class SpringJavaEmailSender extends AbstractEmailSender {

    private static final Logger logger = LoggerFactory.getLogger(SpringJavaEmailSender.class);

    private JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();

    @Override
    public void init(Map<String, String> properties) {
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", EMAIL_SMTP_AUTH().getValue(properties));
        prop.put("mail.smtp.starttls.enable", EMAIL_SMTP_STARTTLS_ENABLE().getValue(properties));
        prop.put("mail.smtp.starttls.required", EMAIL_SMTP_STARTTLS_REQUIRED().getValue(properties));
        prop.put("mail.smtp.ssl.enable", EMAIL_SMTP_SSL_ENABLED().getValue(properties));
        prop.put("mail.smtp.timeout", EMAIL_SMTP_TIMEOUT().getValue(properties));
        javaMailSender.setJavaMailProperties(prop);
        BiConsumer<Consumer<String>, CommonVars<String>> setProp = (consumer, c) -> {
            String value = c.getValue(properties);
            if(StringUtils.isBlank(value)) {
                throw new ExternalOperationFailedException(84002, "The value of " + c.key() + " in sendEmail AppConn is null, please set it in appconn.properties of sendEmail AppConn.");
            } else {
                consumer.accept(value);
            }
        };
        setProp.accept(javaMailSender::setHost, EMAIL_HOST());
        javaMailSender.setPort(EMAIL_PORT().getValue(properties));
        setProp.accept(javaMailSender::setUsername, EMAIL_USERNAME());
        setProp.accept(javaMailSender::setPassword, EMAIL_PASSWORD());
        javaMailSender.setProtocol(EMAIL_PROTOCOL().getValue(properties));
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
            if (StringUtils.isBlank(javaMailSender.getUsername())) {
                messageHelper.setFrom(DEFAULT_EMAIL_FROM().getValue());
            } else {
                messageHelper.setFrom(javaMailSender.getUsername());
            }
            messageHelper.setSubject(email.getSubject());
            messageHelper.setTo(email.getTo());
            if (StringUtils.isNotBlank(email.getCc())) {
                messageHelper.setCc(email.getCc());
            }
            if (StringUtils.isNotBlank(email.getBcc())) {
                messageHelper.setBcc(email.getBcc());
            }
            messageHelper.setText(email.getContent(), true);
            for (Attachment attachment : email.getAttachments()) {
                messageHelper.addInline(attachment.getName(), new ByteArrayDataSource(Base64.getMimeDecoder().decode(attachment.getBase64Str()), attachment.getMediaType()));
                messageHelper.addAttachment(attachment.getName(), new ByteArrayDataSource(Base64.getMimeDecoder().decode(attachment.getBase64Str()), attachment.getMediaType()));
            }

        } catch (Exception e) {
            logger.error("Send mail failed", e);
        }
        return message;
    }
}
