package com.webank.wedatasphere.dss.framework.admin.restful;


import com.webank.wedatasphere.dss.common.auditlog.OperateTypeEnum;
import com.webank.wedatasphere.dss.common.auditlog.TargetTypeEnum;
import com.webank.wedatasphere.dss.common.utils.AuditLogUtils;
import com.webank.wedatasphere.dss.framework.admin.common.constant.UserConstants;
import com.webank.wedatasphere.dss.framework.admin.common.domain.Message;
import com.webank.wedatasphere.dss.framework.admin.common.domain.PasswordResult;
import com.webank.wedatasphere.dss.framework.admin.common.domain.TableDataInfo;
import com.webank.wedatasphere.dss.framework.admin.common.utils.PasswordUtils;
import com.webank.wedatasphere.dss.framework.admin.common.utils.StringUtils;
import com.webank.wedatasphere.dss.framework.admin.conf.AdminConf;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminUser;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminUserService;
import com.webank.wedatasphere.dss.framework.admin.service.LdapService;
import com.webank.wedatasphere.dss.framework.admin.xml.DssUserMapper;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.standard.common.exception.AppStandardWarnException;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.linkis.server.security.SecurityFilter;
import org.apache.linkis.server.utils.ModuleUserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.webank.wedatasphere.dss.framework.common.conf.TokenConf.HPMS_USER_TOKEN;

@RequestMapping(path = "/dss/framework/admin/user", produces = {"application/json"})
@RestController
public class DssFrameworkAdminUserController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DssFrameworkAdminUserController.class);

    @Resource
    private DssAdminUserService dssAdminUserService;
    @Autowired
    private LdapService ldapService;
    @Autowired
    DssUserMapper dssUserMapper;

    @RequestMapping(path = "list", method = RequestMethod.GET)
    public TableDataInfo list(@RequestParam(value = "userName", required = false) String userName,
                              @RequestParam(value = "deptId", required = false) Long deptId,
                              @RequestParam(value = "phonenumber", required = false) String phonenumber,
                              @RequestParam(value = "beginTime", required = false) String beginTime,
                              @RequestParam(value = "endTime", required = false) String endTime) {
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
        LOGGER.info("try to get DssAdminUser list, userList:{}", userList);
        return getDataTable(userList);
    }

    @RequestMapping(path = "add", method = RequestMethod.POST)
    public Message add(@Validated @RequestBody DssAdminUser user, HttpServletRequest req) {
        try {
            PasswordResult passwordResult = PasswordUtils.checkPwd(user.getPassword(), user);
            if (UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkUserNameUnique(user.getUserName()))) {
                return Message.error().message("新增用户'" + user.getUserName() + "'失败，登录账号已存在");
            } else if (user.getUserName().contains(UserConstants.SINGLE_SPACE)) {
                return Message.error().message("新增用户'" + user.getUserName() + "'用户名中不能含有空格");
            } else if (StringUtils.isNotEmpty(user.getPhonenumber())
                    && UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkPhoneUnique(user))) {
                return Message.error().message("新增用户'" + user.getUserName() + "'失败，手机号码已存在");
            } else if (StringUtils.isNotEmpty(user.getEmail())
                    && UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkEmailUnique(user))) {
                return Message.error().message("新增用户'" + user.getUserName() + "'失败，邮箱账号已存在");
            } else if (!PasswordResult.PASSWORD_RULE_PASS.equals(passwordResult)) {
                return Message.error().data("弱密码请关注:", passwordResult.getMessage());
            }
            boolean ldapExist = ldapService.exist(AdminConf.LDAP_ADMIN_NAME.getValue(), AdminConf.LDAP_ADMIN_PASS.getValue(), AdminConf.LDAP_URL.getValue(), AdminConf.LDAP_BASE_DN.getValue(), user.getUserName());
            if (ldapExist) {
                return Message.error().message("新增用户'" + user.getUserName() + "'失败，登录账号在ldap已存在");
            }
            String pwd = user.getPassword();
            user.setPassword(DigestUtils.md5Hex(pwd));
            user.setCreateBy(SecurityFilter.getLoginUsername(req));
            int rows = dssAdminUserService.insertUser(user, getWorkspace(req));
            String userName = user.getUserName();
            LOGGER.info("Add new user {}", userName);
            ldapService.addUser(AdminConf.LDAP_ADMIN_NAME.getValue(), AdminConf.LDAP_ADMIN_PASS.getValue(), AdminConf.LDAP_URL.getValue(), AdminConf.LDAP_BASE_DN.getValue(), userName, pwd);
            return Message.ok().data("rows", rows).message("新增成功");
        } catch (Exception exception) {
            return Message.error().data("rows", 0).message(exception.getMessage());
        }

    }

    private Workspace getWorkspace(HttpServletRequest req) {
        Workspace workspace = new Workspace();
        try {
            LOGGER.info("Put gateway url and cookies into workspace.");
            SSOHelper.addWorkspaceInfo(req, workspace);
        } catch (AppStandardWarnException ignored) {} // ignore it.
        return workspace;
    }


    @RequestMapping(path = "{id}", method = RequestMethod.GET)
    public Message getInfo(@PathVariable("id") Long userId) {
        return Message.ok().data("users", dssAdminUserService.selectUserById(userId));
    }

    @RequestMapping(path = "userInfo", method = RequestMethod.GET)
    public Message getLoginUserInfo(HttpServletRequest request) {
        String userName = SecurityFilter.getLoginUsername(request);
        return Message.ok().data("userInfo", dssAdminUserService.selectUserByName(userName));
    }


    @RequestMapping(path = "edit", method = RequestMethod.POST)
    public Message edit(@Validated @RequestBody DssAdminUser user, HttpServletRequest req) {
        if (StringUtils.isNotEmpty(user.getPhonenumber())
                && UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkPhoneUnique(user))) {
            return Message.error().message("修改用户'" + user.getUserName() + "'失败，手机号码已存在");
        } else if (StringUtils.isNotEmpty(user.getEmail())
                && UserConstants.NOT_UNIQUE.equals(dssAdminUserService.checkEmailUnique(user))) {
            return Message.error().message("修改用户'" + user.getUserName() + "'失败，邮箱账号已存在");
        }
        LOGGER.info("Modify user {} info", user.getUserName());
        return Message.ok().data("修改用户成功。", dssAdminUserService.updateUser(user, getWorkspace(req)));
    }

    @GetMapping("/getAllUserName")
    public Message getAllUsername(){
        return Message.ok().data("userNames", dssAdminUserService.getAllUsername());
    }

    @PostMapping("/deleteUser/{userName}")
    public Message deleteUser(HttpServletRequest httpServletRequest, @PathVariable String userName){
        String token = ModuleUserUtils.getToken(httpServletRequest);
        if (StringUtils.isNotBlank(token)) {
            if(!token.equals(HPMS_USER_TOKEN)){
                return Message.error().message("Token:" + token + " has no permission to revoke userRole.");
            }
        }else {
            return Message.error().message("User:" + userName + " has no permission to revoke userRole.");
        }
        dssAdminUserService.deleteUser(userName);
        AuditLogUtils.printLog(userName,null, null, TargetTypeEnum.USER_DEPT,null,
                "deleteUser", OperateTypeEnum.DELETE,null);
        return Message.ok();
    }

}

