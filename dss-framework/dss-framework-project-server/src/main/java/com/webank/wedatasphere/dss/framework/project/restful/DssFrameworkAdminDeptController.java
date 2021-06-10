package com.webank.wedatasphere.dss.framework.project.restful;


import com.webank.wedatasphere.dss.framework.admin.common.domain.Message;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminDept;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminDeptService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

//import io.swagger.annotations.Api;
//import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 部门表 前端控制器
 * </p>
 *
 * @author Lvjw
 * @since 2021-06-01
 */
//@RestController
////@Api(tags = "部门管理")
//@RequestMapping("/dss/framework/admin/dept")

@Component
@Path("/dss/framework/admin/dept")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DssFrameworkAdminDeptController {
    @Resource
    private DssAdminDeptService dssAdminDeptService;


//    @ApiOperation("部门查询")
//    @GetMapping("/list")
    @GET
    @Path("/list")
    public Message listAll(DssAdminDept dept) {
        List<DssAdminDept> list = dssAdminDeptService.selectDeptList(dept);
        return Message.ok().data("deptList" , list).message("成功");
    }
//
////    @ApiOperation("新增部门")
//    @PostMapping
//    public Message add(@RequestBody DssAdminDept dssAdminDept) {
//
//        if (UserConstants.NOT_UNIQUE.equals(dssAdminDeptService.checkDeptNameUnique(dssAdminDept))) {
//            return Message.error().message("新增部门'" + dssAdminDept.getDeptName() + "'失败，部门名称已存在");
//        }
//
//        int saveResult = dssAdminDeptService.insertDept(dssAdminDept);
//        System.out.println(saveResult);
//        if (saveResult >= 1) {
//            return Message.ok().message("保存成功");
//        } else {
//            return Message.error().message("保存失败");
//        }
//    }
//
//    /**
//     * 获取部门下拉树列表
//     */
////    @ApiOperation("获取部门下拉树列表")
//    @GetMapping("/treeselect")
//    public Message treeselect(DssAdminDept dept) {
//        List<DssAdminDept> depts = dssAdminDeptService.selectDeptList(dept);
//        return Message.ok().data("deptTree" , dssAdminDeptService.buildDeptTreeSelect(depts)).message("树形部门获取成功");
//    }
//
//
////    @ApiOperation("根据部门编号获取详细信息")
//    @GetMapping(value = "/{deptId}")
//    public Message getInfo(@PathVariable Long deptId) {
//        return Message.ok().data("deptInfo" , dssAdminDeptService.selectDeptById(deptId));
//    }
//
////    @ApiOperation("修改部门")
//    @PostMapping("/edit")
//    public Message edit(@Validated @RequestBody DssAdminDept dept) {
//        if (UserConstants.NOT_UNIQUE.equals(dssAdminDeptService.checkDeptNameUnique(dept))) {
//            return Message.error().message("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
//        } else if (dept.getParentId().equals(dept.getId())) {
//            return Message.error().message("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
//        } else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus())
//                && dssAdminDeptService.selectNormalChildrenDeptById(dept.getId()) > 0) {
//            return Message.error().message("该部门包含未停用的子部门！");
//        }
//
//        return Message.ok().data("修改成功" , dssAdminDeptService.updateDept(dept));
//    }
//
//
//    /**
//     * 删除部门
//     */
////    @ApiOperation("删除部门")
//    @PostMapping("/{deptId}")
//    public Message remove(@PathVariable Long deptId) {
//        if (dssAdminDeptService.hasChildById(deptId)) {
//            return Message.error().message("存在下级部门,不允许删除");
//        }
//        if (dssAdminDeptService.checkDeptExistUser(deptId)) {
//            return Message.error().message("部门存在用户,不允许删除");
//        }
//        return Message.ok().data("删除成功" , dssAdminDeptService.deleteDeptById(deptId));
//    }

}
