package com.webank.wedatasphere.dss.flow.execution.entrance.conf;

/**
 * Author: xlinliu
 * Date: 2023/1/16
 */

import org.apache.linkis.entrance.interceptor.EntranceInterceptor;
import org.apache.linkis.entrance.interceptor.impl.CommentInterceptor;
import org.apache.linkis.entrance.interceptor.impl.LabelCheckInterceptor;
import org.apache.linkis.entrance.interceptor.impl.LogPathCreateInterceptor;
import org.apache.linkis.entrance.interceptor.impl.StorePathEntranceInterceptor;
import org.apache.linkis.entrance.constant.ServiceNameConsts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlowEntranceSpringConfiguration  {
    private  final Logger logger = LoggerFactory.getLogger(getClass());
    @Bean(name=ServiceNameConsts.ENTRANCE_INTERCEPTOR)
    public EntranceInterceptor[] entranceInterceptors() {
        logger.info("dss workflow entrance load entranceInterceptors");
        return new EntranceInterceptor[] {
                new LabelCheckInterceptor(),
                new LogPathCreateInterceptor(),
                new StorePathEntranceInterceptor(),
                new CommentInterceptor(),
        };
    }
}