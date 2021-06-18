package com.webank.wedatasphere.dss.framework.project.restful;


import com.webank.wedatasphere.dss.framework.admin.common.constant.UserConstants;
import com.webank.wedatasphere.dss.framework.admin.common.domain.Message;
import com.webank.wedatasphere.dss.framework.admin.common.domain.TableDataInfo;
import com.webank.wedatasphere.dss.framework.admin.common.utils.SecurityUtils;
import com.webank.wedatasphere.dss.framework.admin.common.utils.StringUtils;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminUser;
import com.webank.wedatasphere.dss.framework.admin.restful.BaseController;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import com.webank.wedatasphere.dss.framework.project.conf.ProjectConf;
import com.webank.wedatasphere.dss.framework.project.service.LdapService;
import com.webank.wedatasphere.dss.framework.project.utils.LdapUtils;
import com.webank.wedatasphere.dss.framework.project.utils.RestfulUtils;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author Lvjw
 * @since 2021-06-01
 */
//@RestController
//@RequestMapping("/dss/framework/admin/user")
@Component
@Path("/dss/framework/admin/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DssFrameworkAdminUserController extends BaseController {
    @Resource
    private DssAdminUserService dssAdminUserService;
    @Autowired
    private LdapService ldapService;

    //    @GetMapping("/list")
    @GET
    @Path("/list")
//    public TableDataInfo list(DssAdminUser user) {
    public TableDataInfo list(@QueryParam("userName") String userName, @QueryParam("deptId") Long deptId, @QueryParam("phonenumber") String phonenumber, @QueryParam("beginTime") String beginTime, @QueryParam("endTime") String endTime) {
        DssAdminUser user = new DssAdminUser();
        user.setUserName(userName);
        user.setDeptId(deptId);
        user.setPhonenumber(phonenumber);
        Map<String, Object> params = new HashMap<>();
        params.put("beginTime", beginTime);
        params.put("endTime", endTime);
        user.setParams(params);
        startPage();
        List<DssAdminUser> userList = dssAdminUserService.selectUserList(user);
        return getDataTable(userList);
    }

    //    @PostMapping("/add")
    @POST
    @Path("/add")
    public Message add(@Validated @RequestBody DssAdminUser user, @Context HttpServletRequest req
    ) {
        try {

            if (UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkUserNameUnique(user.getUserName()))) {
                return Message.error().message("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
            } else if (StringUtils.isNotEmpty(user.getPhonenumber())
                    && UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkPhoneUnique(user))) {
                return Message.error().message("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
            } else if (StringUtils.isNotEmpty(user.getEmail())
                    && UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkEmailUnique(user))) {
                return Message.error().message("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
            }

            String pwd = user.getPassword();
            user.setPassword(SecurityUtils.encryptPassword(pwd));
            user.setCreateBy(SecurityFilter.getLoginUsername(req));
            int rows = dssAdminUserService.insertUser(user);
            String userName = user.getUserName();
            ldapService.addUser(ProjectConf.LDAP_ADMIN_NAME.getValue(), ProjectConf.LDAP_ADMIN_PASS.getValue(), ProjectConf.LDAP_URL.getValue(), ProjectConf.LDAP_BASE_DN.getValue(), userName, pwd);
            return Message.ok().data("rows", rows).message("新增成功");
        } catch (Exception exception) {
            return Message.error().data("rows", 0).message(exception.getMessage());
        }

    }

    //    @GetMapping(value = {"/{id}"})@PathParam("id")
    @GET
    @Path("/{id}")
    public Message getInfo(@PathParam("id") Long userId) {
        return Message.ok().data("users", dssAdminUserService.selectUserById(userId));
    }

    //    @PostMapping("/edit")
    @POST
    @Path("/edit")
    public Message edit(@Validated @RequestBody DssAdminUser user) {
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkPhoneUnique(user))) {
            return Message.error().message("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkEmailUnique(user))) {
            return Message.error().message("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
//        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return Message.ok().data("修改用户成功", dssAdminUserService.updateUser(user));
    }

    @POST
    @Path("/resetPsw")
    public Message resetPwd(@RequestBody DssAdminUser user)
    {
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));

        return Message.ok().data("重置密码成功", dssAdminUserService.resetPwd(user));
    }
}

