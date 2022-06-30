package com.webank.wedatasphere.dss.framework.admin.service;

import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminDept;
import com.baomidou.mybatisplus.extension.service.IService;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.TreeSelect;

import java.util.List;
import java.util.Map;

public interface DssAdminDeptService extends IService<DssAdminDept> {

    /**
     * 查询部门管理数据
     *
     * @param dept 部门信息
     * @return 部门信息集合
     */
    public List<DssAdminDept> selectDeptList(DssAdminDept dept);

    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    public List<DssAdminDept> buildDeptTree(List<DssAdminDept> depts);

    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    public List<TreeSelect> buildDeptTreeSelect(List<DssAdminDept> depts);


    /**
     * 根据部门ID查询信息
     *
     * @param deptId 部门ID
     * @return 部门信息
     */
    public DssAdminDept selectDeptById(Long deptId);

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    public int selectNormalChildrenDeptById(Long deptId);

    /**
     * 是否存在部门子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    public boolean hasChildById(Long deptId);

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    public boolean checkDeptExistUser(Long deptId);

    /**
     * 校验部门名称是否唯一
     *
     * @param dept 部门信息
     * @return 结果
     */
    public String checkDeptNameUnique(DssAdminDept dept);

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int insertDept(DssAdminDept dept);

    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    public int updateDept(DssAdminDept dept);

    /**
     * 删除部门管理信息
     *
     * @param id 部门ID
     * @return 结果
     */
    public int deleteDeptById(Long id);

    boolean checkDeptFinalStage(Long parentId);
}
