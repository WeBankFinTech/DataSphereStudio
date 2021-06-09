package com.webank.wedatasphere.dss.framework.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan(basePackages = {"com.webank.wedatasphere.dss.framework.admin.mapper"})

@ComponentScan({"com.webank.wedatasphere"})
public class DssFrameworkAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(DssFrameworkAdminApplication.class, args);
    }

}
