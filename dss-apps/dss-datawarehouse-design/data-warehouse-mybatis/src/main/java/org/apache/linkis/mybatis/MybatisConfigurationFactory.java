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
 */

package org.apache.linkis.mybatis;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import org.apache.linkis.common.utils.JavaLog;
import org.apache.linkis.mybatis.conf.MybatisConfiguration;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Configuration
@ConfigurationProperties
@AutoConfigureAfter(DataSourceConfig.class)
@EnableTransactionManagement
public class MybatisConfigurationFactory extends JavaLog {

    @Autowired
    private DataSource dataSource;
    // Provide SqlSeesion(提供SqlSeesion)
    @Bean(name = "sqlSessionFactory")
    @Primary
    public MybatisSqlSessionFactoryBean sqlSessionFactory(ObjectProvider<Interceptor[]> interceptorsProvider) {
        String typeAliasesPackage = MybatisConfiguration.BDP_SERVER_MYBATIS_TYPEALIASESPACKAGE.getValue();
        //Configure the mapper scan to find all mapper.xml mapping files(配置mapper的扫描，找到所有的mapper.xml映射文件)
        String mapperLocations = MybatisConfiguration.BDP_SERVER_MYBATIS_MAPPER_LOCATIONS.getValue();
        //Load the global configuration file(加载全局的配置文件)
        String configLocation = MybatisConfiguration.BDP_SERVER_MYBATIS_CONFIGLOCATION.getValue();
        try {
            MybatisSqlSessionFactoryBean sessionFactoryBean = new MybatisSqlSessionFactoryBean();
            sessionFactoryBean.setDataSource(dataSource);

            info("Mybatis typeAliasesPackage=" + typeAliasesPackage);
            info("Mybatis mapperLocations=" + mapperLocations);
            info("Mybatis configLocation=" + configLocation);
            // Read configuration(读取配置)
            sessionFactoryBean.setTypeAliasesPackage(typeAliasesPackage);

            //Set the location of the mapper.xml file(设置mapper.xml文件所在位置)
            if(StringUtils.isNotBlank(mapperLocations)) {
                String[] mapperArray = mapperLocations.split(",");
                List<Resource> resources = new ArrayList<>();
                for(String mapperLocation : mapperArray){
                    CollectionUtils.addAll(resources,new PathMatchingResourcePatternResolver().getResources(mapperLocation));
                }
                sessionFactoryBean.setMapperLocations(resources.toArray(new Resource[0]));
            }
           /* Resource[] resources = new PathMatchingResourcePatternResolver().getResources(mapperLocations);
            sessionFactoryBean.setMapperLocations(resources);*/
//            Set the location of the mybatis-config.xml configuration file(设置mybatis-config.xml配置文件位置)
            sessionFactoryBean.setConfigLocation(new DefaultResourceLoader().getResource(configLocation));

            sessionFactoryBean.setPlugins(interceptorsProvider.getIfAvailable());

            return sessionFactoryBean;
        } catch (IOException e) {
            error("mybatis resolver mapper*xml is error",e);
            return null;
        } catch (Exception e) {
            error("mybatis sqlSessionFactoryBean create error",e);
            return null;
        }
    }

    @Bean
    @Primary
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    //Transaction management(事务管理)
    @Bean
    @Primary
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return new DataSourceTransactionManager(dataSource);
    }
//    Log the log to be executed (if you don't want to intercept it, comment out this method)
//    将要执行的sql进行日志打印(不想拦截，就把这方法注释掉)
//    @Bean
//    public SqlPrintInterceptor sqlPrintInterceptor(){
//        return new SqlPrintInterceptor();
//    }

    // 3.4.1
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

}
