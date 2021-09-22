package com.webank.wedatasphere.dss.datamodel.center.common.config;

import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;


@Configuration
public class CommonConfig {

    @Bean("myMetaObjectHandler")
    public MetaObjectHandler myMetaObjectHandler(){
        //TODO 无法自动组装生效
        return new MyMetaObjectHandler();
    }

}
