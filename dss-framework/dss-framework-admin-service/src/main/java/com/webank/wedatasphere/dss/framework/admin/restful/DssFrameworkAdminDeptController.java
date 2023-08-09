package com.webank.wedatasphere.dss.framework.admin.restful;


import com.webank.wedatasphere.dss.framework.admin.common.constant.UserConstants;
import com.webank.wedatasphere.dss.framework.admin.common.domain.Message;
import com.webank.wedatasphere.dss.framework.admin.common.utils.StringUtils;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminDept;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminDeptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RequestMapping(path = "/dss/framework/admin/dept", produces = {"application/json"})
@RestController
public class DssFrameworkAdminDeptController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DssFrameworkAdminDeptController.class);

    @Resource
    private DssAdminDeptService dssAdminDeptService;

    @RequestMapping(path = "list", method = RequestMethod.GET)
    public Message listAll(@RequestParam(value = "parentId", required = false) Long parentId, @RequestParam(value = "deptName", required = false) String deptName) {
        LOGGER.info("begin to get dept list...parentId:{}, deptName:{}",parentId, deptName);
        DssAdminDept dept = new DssAdminDept();
        dept.setParentId(parentId);
        dept.setDeptName(deptName);
        List<DssAdminDept> list = dssAdminDeptService.selectDeptList(dept);
        return Message.ok().data("deptList", list).message("成功");
    }

    @RequestMapping(method = RequestMethod.POST)
    public Message add(@RequestBody DssAdminDept dssAdminDept) {
        if (UserConstants.NOT_UNIQUE.equals(dssAdminDeptService.checkDeptNameUnique(dssAdminDept))) {
            return Message.error().message("新增部门'" + dssAdminDept.getDeptName() + "'失败，部门名称已存在");
        } else if (dssAdminDept.getDeptName().contains(UserConstants.SINGLE_SPACE)) {
            return Message.error().message("新增部门'" + dssAdminDept.getDeptName() + "'部门名称中不能含有空格");
        } else if (dssAdminDeptService.checkDeptFinalStage(dssAdminDept.getParentId())) {
            return Message.error().message("新增部门'" + dssAdminDept.getDeptName() + "'失败，该部门是末级部门,不能新增下级部门");
        }
        LOGGER.info("Add new dept {}", dssAdminDept.getDeptName());
        int saveResult = dssAdminDeptService.insertDept(dssAdminDept);
        if (saveResult >= 1) {
            return Message.ok().message("保存成功");
        } else {
            return Message.error().message("保存失败");
        }
    }

    /**
     * 获取部门下拉树列表
     */
    @RequestMapping(path = "treeselect", method = RequestMethod.GET)
    public Message treeselect(DssAdminDept dept) {
        List<DssAdminDept> depts = dssAdminDeptService.selectDeptList(dept);
        return Message.ok().data("deptTree", dssAdminDeptService.buildDeptTreeSelect(depts)).message("树形部门获取成功");
    }


    @RequestMapping(path = "{deptId}", method = RequestMethod.GET)
    public Message getInfo(@PathVariable("deptId") Long deptId) {
        return Message.ok().data("deptInfo", dssAdminDeptService.selectDeptById(deptId));
    }


    @RequestMapping(path = "edit", method = RequestMethod.POST)
    public Message edit(@Validated @RequestBody DssAdminDept dept) {
        if (UserConstants.NOT_UNIQUE.equals(dssAdminDeptService.checkDeptNameUnique(dept))) {
            return Message.error().message("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        } else if (dept.getDeptName().contains(UserConstants.SINGLE_SPACE)) {
            return Message.error().message("修改部门'" + dept.getDeptName() + "'部门名称中不能含有空格");
        } else if (dept.getParentId().equals(dept.getId())) {
            return Message.error().message("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        } else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus())
                && dssAdminDeptService.selectNormalChildrenDeptById(dept.getId()) > 0) {
            return Message.error().message("该部门包含未停用的子部门！");
        }
        LOGGER.info("Modify dept name to {}",dept.getDeptName());
        return Message.ok().data("修改成功", dssAdminDeptService.updateDept(dept));
    }

    /**
     * 删除部门
     */

    @RequestMapping(path = "{deptId}", method = RequestMethod.POST)
    public Message remove(@PathVariable("deptId") Long deptId) {
        if (dssAdminDeptService.hasChildById(deptId)) {
            return Message.error().message("存在下级部门,不允许删除");
        }
        if (dssAdminDeptService.checkDeptExistUser(deptId)) {
            return Message.error().message("部门存在用户,不允许删除");
        }
        LOGGER.info("Delete dept {}", deptId);
        return Message.ok().data("删除成功", dssAdminDeptService.deleteDeptById(deptId));
    }


}
