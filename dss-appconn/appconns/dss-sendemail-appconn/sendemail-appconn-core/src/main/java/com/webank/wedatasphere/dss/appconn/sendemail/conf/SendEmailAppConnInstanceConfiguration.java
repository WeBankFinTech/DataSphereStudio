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

package com.webank.wedatasphere.dss.appconn.sendemail.conf;

import com.webank.wedatasphere.dss.appconn.sendemail.email.EmailGenerator;
import com.webank.wedatasphere.dss.appconn.sendemail.email.EmailSender;
import com.webank.wedatasphere.dss.appconn.sendemail.email.generate.MultiContentEmailGenerator;
import com.webank.wedatasphere.dss.appconn.sendemail.email.sender.SpringJavaEmailSender;
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.EmailContentGenerator;
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.EmailContentParser;
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.generator.MultiEmailContentGenerator;
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.parser.FileEmailContentParser$;
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.parser.HtmlEmailContentParser$;
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.parser.PictureEmailContentParser$;
import com.webank.wedatasphere.dss.appconn.sendemail.emailcontent.parser.TableEmailContentParser$;
import com.webank.wedatasphere.dss.appconn.sendemail.hook.SendEmailRefExecutionHook;
import com.webank.wedatasphere.dss.standard.common.utils.AppStandardClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class SendEmailAppConnInstanceConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SendEmailAppConnInstanceConfiguration.class);

    private static final EmailGenerator EMAIL_GENERATOR = new MultiContentEmailGenerator();

    private static final EmailSender EMAIL_SENDER = createEmailSender();

    private static final EmailContentGenerator[] EMAIL_CONTENT_GENERATOR = createEmailContentGenerators();

    private static final EmailContentParser[] emailContentParsers = createEmailContentParsers();

    private static final SendEmailRefExecutionHook[] sendEmailRefExecutionHooks = createSendEmailRefExecutionHooks();

    private static EmailSender createEmailSender() {
        String emailSenderClassName = SendEmailAppConnConfiguration.EMAIL_SENDER_CLASS().getValue();
        try {
            logger.info("Use user config EmailSender by conf:{}", emailSenderClassName);
            return (EmailSender)SendEmailAppConnInstanceConfiguration.class.getClassLoader().loadClass(emailSenderClassName).newInstance();
        } catch (Exception e) {
            logger.warn("{} can not be instanced, use SpringJavaEmailSender by default.", emailSenderClassName, e);
            return new SpringJavaEmailSender();
        }
    }

    private static EmailContentGenerator[] createEmailContentGenerators() {
        return new EmailContentGenerator[] {new MultiEmailContentGenerator()};
    }

    private static EmailContentParser[] createEmailContentParsers() {
        return new EmailContentParser[] {FileEmailContentParser$.MODULE$,
            HtmlEmailContentParser$.MODULE$, PictureEmailContentParser$.MODULE$, TableEmailContentParser$.MODULE$};
    }

    private static SendEmailRefExecutionHook[] createSendEmailRefExecutionHooks() {
        String hookClasses = SendEmailAppConnConfiguration.EMAIL_HOOK_CLASSES().getValue();
        logger.info("Use email hook class: {}", hookClasses);
        return Arrays.stream(hookClasses.split(",")).map(clazz -> {
            SendEmailRefExecutionHook sendEmailRefExecutionHook = null;
            try {
                sendEmailRefExecutionHook = (SendEmailRefExecutionHook)SendEmailAppConnInstanceConfiguration.class.getClassLoader().loadClass(clazz).newInstance();
                logger.info("Get hook class instance is : {}", sendEmailRefExecutionHook.getClass().getName());
            } catch (InstantiationException e) {
                logger.warn("{} can not be instanced", clazz, e);
            } catch (IllegalAccessException e) {
                logger.warn("{} can not be instanced", clazz, e);
            } catch (ClassNotFoundException e) {
                logger.warn("{} can not be instanced", clazz, e);
            }
            return sendEmailRefExecutionHook;
        }).filter(hook -> null!= hook).toArray(SendEmailRefExecutionHook[]::new);
    }

    public static EmailSender getEmailSender() {
        return EMAIL_SENDER;
    }

    public static void init(){
        logger.info("init SendEmailAppConnInstanceConfiguration");
    }

    public static EmailGenerator getEmailGenerator() {
        return EMAIL_GENERATOR;
    }


    public static EmailContentGenerator[] getEmailContentGenerators() {
        return EMAIL_CONTENT_GENERATOR;
    }

    public static EmailContentParser[] getEmailContentParsers() {
        return emailContentParsers;
    }

    public static SendEmailRefExecutionHook[] getSendEmailRefExecutionHooks() {
        return sendEmailRefExecutionHooks;
    }

}
