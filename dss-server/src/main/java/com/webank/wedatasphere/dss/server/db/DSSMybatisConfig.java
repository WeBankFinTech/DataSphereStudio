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

package com.webank.wedatasphere.dss.server.db;

import com.webank.wedatasphere.dss.server.conf.DSSServerConf;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
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
@EnableTransactionManagement(order = 2)
public class DSSMybatisConfig {

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    @Bean(name = "dataSource", destroyMethod = "close")
    @Primary
    public DataSource dataSource() {
        BasicDataSource datasource = new BasicDataSource();
        String dbUrl = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_URL.getValue();
        String username = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_USERNAME.getValue();
        String password = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_PASSWORD.getValue();
        String driverClassName = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_DRIVER_CLASS_NAME.getValue();
        int initialSize = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_INITIALSIZE.getValue();
        int minIdle = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_MINIDLE.getValue();
        int maxActive = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_MAXACTIVE.getValue();
        int maxWait = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_MAXWAIT.getValue();
        int timeBetweenEvictionRunsMillis = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_TBERM.getValue();
        int minEvictableIdleTimeMillis = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_MEITM.getValue();
        String validationQuery = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_VALIDATIONQUERY.getValue();
        boolean testWhileIdle = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_TESTWHILEIDLE.getValue();
        boolean testOnBorrow = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_TESTONBORROW.getValue();
        boolean testOnReturn = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_TESTONRETURN.getValue();
        boolean poolPreparedStatements = DSSServerConf.BDP_SERVER_MYBATIS_DATASOURCE_POOLPREPAREDSTATEMENTS.getValue();
        datasource.setUrl(dbUrl);
        datasource.setUsername(username);
        datasource.setPassword(password);
        datasource.setDriverClassName(driverClassName);
        datasource.setInitialSize(initialSize);
        datasource.setMinIdle(minIdle);
        datasource.setMaxActive(maxActive);
        datasource.setMaxWait(maxWait);
        datasource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        datasource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        datasource.setValidationQuery(validationQuery);
        datasource.setTestWhileIdle(testWhileIdle);
        datasource.setTestOnBorrow(testOnBorrow);
        datasource.setTestOnReturn(testOnReturn);
        datasource.setPoolPreparedStatements(poolPreparedStatements);
        logger.info("Database connection address information(数据库连接地址信息)=" + dbUrl);
        return datasource;
    }

    @Bean(name = "sqlSessionFactory")
    @Primary
    public SqlSessionFactory sqlSessionFactory(@Qualifier("dataSource") DataSource dataSource) throws Exception {
        String typeAliasesPackage = DSSServerConf.BDP_SERVER_MYBATIS_TYPEALIASESPACKAGE.getValue();
        //Configure the mapper scan to find all mapper.xml mapping files(配置mapper的扫描，找到所有的mapper.xml映射文件)
        String mapperLocations = DSSServerConf.BDP_SERVER_MYBATIS_MAPPER_LOCATIONS.getValue();
        //Load the global configuration file(加载全局的配置文件)
        String configLocation = DSSServerConf.BDP_SERVER_MYBATIS_CONFIGLOCATION.getValue();
        try {
            SqlSessionFactoryBean sessionFactoryBean = new SqlSessionFactoryBean();
            sessionFactoryBean.setDataSource(dataSource);

            logger.info("Mybatis typeAliasesPackage=" + typeAliasesPackage);
            logger.info("Mybatis mapperLocations=" + mapperLocations);
            logger.info("Mybatis configLocation=" + configLocation);
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

            return sessionFactoryBean.getObject();
        } catch (IOException e) {
            logger.error("mybatis resolver mapper*xml is error",e);
            return null;
        } catch (Exception e) {
            logger.error("mybatis sqlSessionFactoryBean create error",e);
            return null;
        }
    }

    @Primary
    public PlatformTransactionManager annotationDrivenTransactionManager(@Qualifier("dataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    @Bean
    public MapperScannerConfigurer mapperScannerConfigurer(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        MapperScannerConfigurer mapperScannerConfigurer = new MapperScannerConfigurer();
        mapperScannerConfigurer.setSqlSessionFactory(sqlSessionFactory);
        //Each table corresponds to the XXMapper.java interface type Java file
        //每张表对应的XXMapper.java interface类型的Java文件
        mapperScannerConfigurer.setBasePackage(DSSServerConf.BDP_SERVER_MYBATIS_BASEPACKAGE.getValue());
        return mapperScannerConfigurer;
    }

}