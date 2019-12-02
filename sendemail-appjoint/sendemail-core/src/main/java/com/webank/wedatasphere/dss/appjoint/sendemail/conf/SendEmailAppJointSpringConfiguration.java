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

package com.webank.wedatasphere.dss.appjoint.sendemail.conf;

import com.webank.wedatasphere.dss.appjoint.sendemail.EmailContentGenerator;
import com.webank.wedatasphere.dss.appjoint.sendemail.EmailGenerator;
import com.webank.wedatasphere.dss.appjoint.sendemail.EmailSender;
import com.webank.wedatasphere.dss.appjoint.sendemail.email.generate.MultiContentEmailGenerator;
import com.webank.wedatasphere.dss.appjoint.sendemail.emailcontent.generator.MultiEmailContentGenerator;
import com.webank.wedatasphere.dss.appjoint.sendemail.sender.SpringJavaEmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by enjoyyin on 2019/10/14.
 */

public class SendEmailAppJointSpringConfiguration {


    private static final String ESB_EMAIL_SENDER = "com.webank.wedatasphere.dss.appjoint.sendemail.sender.EsbEmailSender";

    private static final Logger logger = LoggerFactory.getLogger(SendEmailAppJointSpringConfiguration.class);

    private static final EmailGenerator EMAIL_GENERATOR = new MultiContentEmailGenerator();

    private static final EmailSender SPRING_EMAIL_SENDER = new SpringJavaEmailSender();

    private static final EmailContentGenerator EMAIL_CONTENT_GENERATOR = new MultiEmailContentGenerator();

    private static EmailSender emailSender = null;

    static {
        try {
            Class clazz = Class.forName(ESB_EMAIL_SENDER);
            emailSender = (EmailSender) clazz.newInstance();
        } catch (Exception e) {
            logger.warn("{} can be instanced", ESB_EMAIL_SENDER, e);
        }
    }
    /**
     * 如果是行内就直接返回com.webank.wedatasphere.dss.appjoint.sendemail.sender.EsbEmailSender
     * @return
     */

    public static EmailSender createEmailSender() {

        if (null != emailSender) {
            return emailSender;
        } else{
            return SPRING_EMAIL_SENDER;
        }
    }


    public static EmailGenerator createEmailGenerator() {
        return EMAIL_GENERATOR;
    }


    public static EmailContentGenerator createEmailContentGenerator() {
        return EMAIL_CONTENT_GENERATOR;
    }

}
