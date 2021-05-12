package com.webank.wedatasphere.dss.appconn.dolphinscheduler.sso;

import com.webank.wedatasphere.linkis.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public final class DolphinSchedulerSecurityService {

    private static final Logger logger = LoggerFactory.getLogger(DolphinSchedulerSecurityService.class);

    private static Properties userToken;

    private static DolphinSchedulerSecurityService instance;

    private DolphinSchedulerSecurityService() {
    }

    public static DolphinSchedulerSecurityService getInstance(String baseUrl) {
        if (null == instance) {
            synchronized (DolphinSchedulerSecurityService.class) {
                if (null == instance) {
                    instance = new DolphinSchedulerSecurityService();
                }
            }
        }
        return instance;
    }

    static {
        Utils.defaultScheduler().scheduleAtFixedRate(() -> {
            logger.info("开始读取用户token文件");
            Properties properties = new Properties();
            try {
                properties.load(
                    DolphinSchedulerSecurityService.class.getClassLoader().getResourceAsStream("token.properties"));
                userToken = properties;
            } catch (IOException e) {
                logger.error("读取文件失败:", e);
            }
        }, 0, 10, TimeUnit.MINUTES);
    }

    public String getUserToken(String user) {
        //直接从配置文件中读取，有需求可以自己实现
        Object token = userToken.get(user);
        if (token == null) {
            return "";
        }
        return token.toString();
    }

}
