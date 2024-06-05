package com.webank.wedatasphere.dss.framework.workspace.restful;

import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.ws.rs.core.MediaType;

@TestMethodOrder(MethodOrderer.OrderAnnotation. class )
@SpringBootTest(classes=com.webank.wedatasphere.dss.Application.class)
//@AutoConfigureMockMvc
class DSSWorkspaceUserRestfulTest {

//    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DssAdminUserService dssUserService;

    @BeforeEach
    void before(WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .addFilter((request, response, chain) -> {
                    response.setCharacterEncoding("UTF-8");
                    chain.doFilter(request, response);
                }, "/*")
                .build();
    }
    @Order ( 2 )
    @SneakyThrows
    @org.junit.jupiter.api.Test
    void revokeUserRole() {
        String content = mockMvc.perform(MockMvcRequestBuilders.post("/dss/framework/workspace/revokeUserRole")
                        .content("{\n" +
                                "  \"userName\": \"v_xiangbiaowu\",\n" +
                                "  \"workspaceIds\": [224],\n" +
                                "  \"roleIds\": [1,2,3]\n" +
                                "}")
                .header("Token-Code", "HPMS-KhFGSQkdaaCPBYfE")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse().getContentAsString();
    }
    @Order ( 1 )
    @SneakyThrows
    @Test
    void getWorkspaceUserRole() {
        String content = mockMvc.perform(MockMvcRequestBuilders.get("/dss/framework/workspace/getUserRole")
                .param("userName", "hadoop")
                .header("Token-Code", "HPMS-KhFGSQkdaaCPBYfE")
                .contentType("application/json")
        ).andReturn().getResponse().getContentAsString();
    }

    @Order ( 3 )
    @Test
    void getWorkspaceUserRole2() {
        getWorkspaceUserRole();
    }

}