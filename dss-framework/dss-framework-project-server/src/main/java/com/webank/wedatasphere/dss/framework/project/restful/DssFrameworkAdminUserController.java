package com.webank.wedatasphere.dss.framework.project.restful;


import com.webank.wedatasphere.dss.framework.admin.common.constant.UserConstants;
import com.webank.wedatasphere.dss.framework.admin.common.domain.Message;
import com.webank.wedatasphere.dss.framework.admin.common.domain.TableDataInfo;
import com.webank.wedatasphere.dss.framework.admin.common.utils.SecurityUtils;
import com.webank.wedatasphere.dss.framework.admin.common.utils.StringUtils;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminUser;
import com.webank.wedatasphere.dss.framework.admin.restful.BaseController;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;


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

    //    @GetMapping("/list")
    @GET
    @Path("/list")
    public TableDataInfo list(DssAdminUser user) {
        startPage();
        List<DssAdminUser> userList = dssAdminUserService.selectUserList(user);
        return getDataTable(userList);
    }

    //    @PostMapping("/add")
    @POST
    @Path("/add")
    public Message add(@Validated @RequestBody DssAdminUser user) {
        if (UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkUserNameUnique(user.getUserName()))) {
            return Message.error().message("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
        } else if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkPhoneUnique(user))) {
            return Message.error().message("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkEmailUnique(user))) {
            return Message.error().message("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }

        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return Message.ok().data("rows", dssAdminUserService.insertUser(user)).message("新增成功");

    }

    //    @GetMapping(value = {"/{id}"})
    @GET
    @Path("/{id}")
    public Message getInfo(@PathVariable(value = "id", required = false) Long userId) {
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
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return Message.ok().data("修改用户成功", dssAdminUserService.updateUser(user));
    }

}

