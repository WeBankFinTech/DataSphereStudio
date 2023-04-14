package com.webank.wedatasphere.dss.common.conf;

import com.webank.wedatasphere.dss.common.alter.CustomAlterServiceImpl;
import com.webank.wedatasphere.dss.common.alter.ExceptionAlterSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlterConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AlterConfiguration.class);

    private static final ExceptionAlterSender ALTER = createAlter();

    private static ExceptionAlterSender createAlter() {

        String alterClassName = DSSCommonConf.ALTER_CLASS.getValue();

        try {
            logger.info("Use user config Alter {}", alterClassName);
            return (ExceptionAlterSender) AlterConfiguration.class.getClassLoader().loadClass(alterClassName).newInstance();
        } catch (Exception e) {
            logger.warn("Use CustomAlter {}", alterClassName, e);
            return new CustomAlterServiceImpl();
        }

    }

    public static ExceptionAlterSender getAlter() {
        return ALTER;
    }

}
