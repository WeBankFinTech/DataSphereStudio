package com.webank.wedatasphere.dss.framework.admin.restful;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.webank.wedatasphere.dss.framework.admin.common.constant.UserConstants;
import com.webank.wedatasphere.dss.framework.admin.common.domain.Message;
import com.webank.wedatasphere.dss.framework.admin.common.domain.TableDataInfo;
import com.webank.wedatasphere.dss.framework.admin.common.utils.SecurityUtils;
import com.webank.wedatasphere.dss.framework.admin.common.utils.StringUtils;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssUser;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author Lvjw
 * @since 2021-06-01
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/dss/framework/admin/user")
public class DssAdminUserController extends BaseController {
    @Resource
    private DssAdminUserService dssAdminUserService;

    @ApiOperation("用户查询")
    @GetMapping("/list")
    public TableDataInfo list( DssUser user) {
        startPage();
        List<DssUser> userList = dssAdminUserService.selectUserList(user);
        return getDataTable(userList);
    }

    @ApiOperation("添加用户")
    @PostMapping
    public Message add(@Validated @RequestBody DssUser user) {
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
        return Message.ok().data("rows" , dssAdminUserService.insertUser(user)).message("新增成功");

    }
    @ApiOperation("根据id查询详细信息")
    @GetMapping(value = {"/{id}"})
    public Message getInfo(@PathVariable(value = "id" , required = false) Long userId) {
        return Message.ok().data("users" , dssAdminUserService.selectUserById(userId));
    }
    @ApiOperation("编辑用户")
    @PutMapping
    public Message edit(@Validated @RequestBody DssUser user) {
       if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkPhoneUnique(user))) {
            return Message.error().message("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkEmailUnique(user))) {
            return Message.error().message("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        user.setPassword(SecurityUtils.encryptPassword(user.getPassword()));
        return Message.ok().data("修改用户成功" , dssAdminUserService.updateUser(user));
    }

}

