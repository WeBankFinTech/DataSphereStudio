/*
 *
 *  * Copyright 2019 WeBank
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  * http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.webank.wedatasphere.dss.apiservice.test;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.DbSetupTracker;
import com.ninja_squad.dbsetup.Operations;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Operation;
import com.webank.wedatasphere.dss.apiservice.core.bo.ApiServiceToken;
import com.webank.wedatasphere.dss.apiservice.core.dao.ApiServiceAccessDao;
import com.webank.wedatasphere.dss.apiservice.core.dao.ApiServiceDao;
import com.webank.wedatasphere.dss.apiservice.core.service.ApiService;
import com.webank.wedatasphere.dss.apiservice.core.token.JwtManager;
import com.webank.wedatasphere.dss.apiservice.core.util.DateUtil;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiAccessVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiServiceVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApiVersionVo;
import com.webank.wedatasphere.dss.apiservice.core.vo.ApprovalVo;
import com.webank.wedatasphere.linkis.DataWorkCloudApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * @author allenlliu
 * @date 2020/10/19 03:29 PM
 */

@RunWith(SpringJUnit4ClassRunner.class)
//@MapperScan(annotationClass = Repository.class, basePackages = "com.webank.wedatasphere.dss.apiservice.dao" )
@SpringBootTest(classes = {DataWorkCloudApplication.class})
public class TestApiServiceDBOperation {
    private static DbSetupTracker dbSetupTracker = new DbSetupTracker();
    @Autowired
    ApiService apiService;
    @Autowired
    DataSource dataSource;
    @Autowired
    ApiServiceDao apiServiceDao;


    @Autowired
    ApiServiceAccessDao apiServiceAccessDao;

    @Before
    public void setUp() {
        Operation operation = Operations.sequenceOf(
                Operations.deleteAllFrom("dss_apiservice_api", "dss_apiservice_param"),
                Operations.insertInto("dss_apiservice_api")
                        .columns("name", "alias_name", "path", "protocol", "method", "tag", "scope", "description", "status", "type", "run_type", "create_time", "modify_time", "creator", "modifier", "script_path", "workspaceID", "api_comment")
                        .values("test01", "测试", "/test01", 0, "get", "new test", "shouquankejian", "miaoshu", "1", "sql", "spark", "2020-10-19 11:00:00", "2020-11-19 12:00:00", "allenlliu", "allenlliu", "123.sql", "180", "comment test")
                        .build(),
                Operations.insertInto("dss_apiservice_param")
                        .columns("api_version_id", "name", "display_name", "type", "required", "default_value", "description", "details")
                        .values(1, "idcard", "身份证", "String", 1, "430124000000000", "test param", "身份标识")
                        .build()
//                Operations.insertInto("dss_apiservice_access_info")
//                        .columns("api_version_id", "login_user", "display_name", "type", "required", "default_value", "description", "details").build(),
        );
        DbSetup dbSetup = new DbSetup(new DataSourceDestination(dataSource), operation);
        dbSetupTracker.launchIfNecessary(dbSetup);
    }

    @DisplayName("ApiService库表验证")
    @Test
    public void testApiServiceDBOperate() {
        dbSetupTracker.skipNextLaunch();

        Integer actual = apiServiceDao.enableApi(0L);
        ApiServiceVo apiServiceVo = apiServiceDao.queryByPath("/test01");
        Integer count = apiService.queryCountByName("test01");

        Assertions.assertEquals(apiServiceVo.getName(), "test01");
        assertThat(actual, equalTo(0));
        assertThat(count, equalTo(1));
    }

    @DisplayName("ApiService备注更新测试")
    @Test
    public void testApiServiceCommentUpdate() {
        dbSetupTracker.skipNextLaunch();

        ApiServiceVo apiServiceVo01 = apiServiceDao.queryByPath("/test01");
        Integer actual = apiServiceDao.updateApiServiceComment(apiServiceVo01.getId(), "new comment");
        ApiServiceVo apiServiceVo02 = apiServiceDao.queryByPath("/test01");


        Assertions.assertEquals(apiServiceVo02.getComment(), "new comment");
    }

    @DisplayName("ApiService保存和版本验证")
    @Test
    public void testApiServiceVersion() {
        dbSetupTracker.skipNextLaunch();


        ApiServiceVo apiServiceVo = apiServiceDao.queryByPath("/test01");
        apiServiceVo.setName("test02");
        apiServiceVo.setPath("/test02");
        //BML会上传失败，没有配置实际环境
        Assertions.assertThrows(NullPointerException.class, () -> {
            apiService.save(apiServiceVo);
            ;
        });

    }

//
//    @DisplayName("DataMap单库表解析验证")
//    @Test
//    public void testDataMapApprovalTableParse() {
//        System.out.println("DataMap单库表解析验证");
//        ApprovalVo approvalVo = new ApprovalVo();
//        approvalVo.setApiId(1L);
//        ApiServiceVo apiServiceVo = new ApiServiceVo();
//        apiServiceVo.setApprovalVo(approvalVo);
//        ApiVersionVo apiVersionVo = new ApiVersionVo();
//        String metaDtaInfo = "[default1.a1,default2.b]";
//        List<DataMapApplyContentData> dataMapApplyContentDataList = apiService.genDataMapApplyContentDatas(apiServiceVo, apiVersionVo, metaDtaInfo);
//        Assertions.assertAll("tableNames",
//                () -> Assertions.assertEquals(dataMapApplyContentDataList.get(0).getDbName(), "default1"),
//                () -> Assertions.assertEquals(dataMapApplyContentDataList.get(1).getTableName(), "b")
//
//        );
//
//    }

    @DisplayName("数据服务访问记录验证")
    @Test
    public void testApiServiceAccessInfo() {
        System.out.println("数据服务访问记录验证");
        ApiAccessVo apiAccessVo = new ApiAccessVo();
        apiAccessVo.setUser("allenlliu");
        apiAccessVo.setApiPublisher("testUser");
        apiAccessVo.setApiServiceName("testApi");
        apiAccessVo.setApiServiceId(102L);
        apiAccessVo.setApiServiceVersionId(10L);
        apiAccessVo.setProxyUser("hadoop");
        apiAccessVo.setAccessTime(DateUtil.getNow());
        apiServiceAccessDao.addAccessRecord(apiAccessVo);

        ApiAccessVo targetAccessVo = apiServiceAccessDao.queryByVersionId(10L);
        Assertions.assertAll("accessInfos",
                () -> Assertions.assertEquals(targetAccessVo.getProxyUser(), "hadoop"),
                () -> Assertions.assertEquals(targetAccessVo.getApiServiceName(), "testApi"),
                () -> Assertions.assertEquals(targetAccessVo.getApiPublisher(), "testUser")

        );

    }

    @DisplayName("Token 解析验证")
    @Test
    public void testTokenParse() {
        System.out.println("Token解析验证");
        final String applyUser = "allenlliu";
        ApiServiceToken apiServiceToken = new ApiServiceToken();
        apiServiceToken.setPublisher("allenlliu");
        apiServiceToken.setApiServiceId(150L);
        apiServiceToken.setApplyUser("testUser1");
        apiServiceToken.setApplyTime(new Date());
        Long duration = 365L;
        String token = JwtManager.createToken(applyUser, apiServiceToken, duration);

        ApiServiceToken parseToken = JwtManager.parseToken(token);

        Assertions.assertEquals(parseToken.getApiServiceId(), 150L);
        Assertions.assertEquals(parseToken.getApplyUser(), "testUser1");

    }
}