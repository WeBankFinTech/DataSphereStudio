package com.webank.wedatasphere.dss.framework.admin.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.webank.wedatasphere.dss.framework.admin.common.constant.UserConstants;
import com.webank.wedatasphere.dss.framework.admin.common.utils.StringUtils;
import com.webank.wedatasphere.dss.framework.admin.exception.DSSAdminWarnException;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.DssAdminDept;
import com.webank.wedatasphere.dss.framework.admin.pojo.entity.TreeSelect;
import com.webank.wedatasphere.dss.framework.admin.service.DssAdminDeptService;
import com.webank.wedatasphere.dss.framework.admin.xml.DssAdminDeptMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DssAdminDeptServiceImpl extends ServiceImpl<DssAdminDeptMapper, DssAdminDept> implements DssAdminDeptService {
    @Resource
    private DssAdminDeptMapper dssAdminDeptMapper;

    /**
     * 校验部门名称是否唯一
     *
     * @param dssAdminDept 部门信息
     * @return 结果
     */
    @Override
    public String checkDeptNameUnique(DssAdminDept dssAdminDept) {
        Long deptId = StringUtils.isNull(dssAdminDept.getId()) ? -1L : dssAdminDept.getId();
        DssAdminDept info = dssAdminDeptMapper.checkDeptNameUnique(dssAdminDept.getDeptName(), dssAdminDept.getParentId());
        if (StringUtils.isNotNull(info) && info.getId().longValue() != deptId.longValue()) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 新增保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int insertDept(DssAdminDept dept) {
        DssAdminDept info = dssAdminDeptMapper.selectDeptById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus())) {
            throw new DSSAdminWarnException("部门停用，不允许新增");
        }
        dept.setAncestors(info.getAncestors() + "," + dept.getParentId());
        return dssAdminDeptMapper.insertDept(dept);
    }


    @Override
    public List<DssAdminDept> selectDeptList(DssAdminDept dept) {
        return dssAdminDeptMapper.selectDeptList(dept);
    }


    /**
     * 构建前端所需要下拉树结构
     *
     * @param depts 部门列表
     * @return 下拉树结构列表
     */
    @Override
    public List<TreeSelect> buildDeptTreeSelect(List<DssAdminDept> depts) {
        List<DssAdminDept> deptTrees = buildDeptTree(depts);
        List<TreeSelect> treeSelects = deptTrees.stream().map(TreeSelect::new).collect(Collectors.toList());
        return treeSelects;
    }

    /**
     * 构建前端所需要树结构
     *
     * @param depts 部门列表
     * @return 树结构列表
     */
    @Override
    public List<DssAdminDept> buildDeptTree(List<DssAdminDept> depts) {
        List<DssAdminDept> returnList = new ArrayList<DssAdminDept>();
        List<Long> tempList = new ArrayList<Long>();
        for (DssAdminDept dept : depts) {
            tempList.add(dept.getId());
        }
        for (Iterator<DssAdminDept> iterator = depts.iterator(); iterator.hasNext(); ) {
            DssAdminDept dept = (DssAdminDept) iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;

    }

    @Override
    public DssAdminDept selectDeptById(Long deptId) {
        return dssAdminDeptMapper.selectDeptById(deptId);
    }

    /**
     * 是否存在子节点
     *
     * @param deptId 部门ID
     * @return 结果
     */
    @Override
    public boolean hasChildById(Long deptId) {
        int result = dssAdminDeptMapper.hasChildById(deptId);
        return result > 0 ? true : false;
    }

    /**
     * 根据ID查询所有子部门（正常状态）
     *
     * @param deptId 部门ID
     * @return 子部门数
     */
    @Override
    public int selectNormalChildrenDeptById(Long deptId) {
        return dssAdminDeptMapper.selectNormalChildrenDeptById(deptId);
    }

    /**
     * 查询部门是否存在用户
     *
     * @param deptId 部门ID
     * @return 结果 true 存在 false 不存在
     */
    @Override
    public boolean checkDeptExistUser(Long deptId) {
        int result = dssAdminDeptMapper.checkDeptExistUser(deptId);
        return result > 0 ? true : false;
    }


    /**
     * 修改保存部门信息
     *
     * @param dept 部门信息
     * @return 结果
     */
    @Override
    public int updateDept(DssAdminDept dept) {
        DssAdminDept newParentDept = dssAdminDeptMapper.selectDeptById(dept.getParentId());
        DssAdminDept oldDept = dssAdminDeptMapper.selectDeptById(dept.getId());
        if (StringUtils.isNotNull(newParentDept) && StringUtils.isNotNull(oldDept)) {
            String newAncestors = newParentDept.getAncestors() + "," + newParentDept.getId();
            String oldAncestors = oldDept.getAncestors();
            dept.setAncestors(newAncestors);
            updateDeptChildren(dept.getId(), newAncestors, oldAncestors);
        }
        int result = dssAdminDeptMapper.updateDept(dept);
        if (UserConstants.DEPT_NORMAL.equals(dept.getStatus())) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatus(dept);
        }
        return result;
    }

    @Override
    public int deleteDeptById(Long id) {
        return dssAdminDeptMapper.deleteDeptById(id);
    }

    @Override
    public boolean checkDeptFinalStage(Long parentId) {

        int result = dssAdminDeptMapper.checkDeptFinalStage(parentId);
        return result > 2 ? true : false;

    }

    /**
     * 修改子元素关系
     *
     * @param deptId       被修改的部门ID
     * @param newAncestors 新的父ID集合
     * @param oldAncestors 旧的父ID集合
     */
    public void updateDeptChildren(Long deptId, String newAncestors, String oldAncestors) {
        List<DssAdminDept> children = dssAdminDeptMapper.selectChildrenDeptById(deptId);
        for (DssAdminDept child : children) {
            child.setAncestors(child.getAncestors().replaceFirst(oldAncestors, newAncestors));
        }
        if (children.size() > 0) {
            dssAdminDeptMapper.updateDeptChildren(children);
        }
    }

    /**
     * 修改该部门的父级部门状态
     *
     * @param dept 当前部门
     */
    private void updateParentDeptStatus(DssAdminDept dept) {
        String updateBy = dept.getUpdateBy();
        dept = dssAdminDeptMapper.selectDeptById(dept.getId());
        dept.setUpdateBy(updateBy);
        dssAdminDeptMapper.updateDeptStatus(dept);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<DssAdminDept> list, DssAdminDept t) {
        // 得到子节点列表
        List<DssAdminDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (DssAdminDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<DssAdminDept> list, DssAdminDept t) {
        return getChildList(list, t).size() > 0 ? true : false;
    }

    /**
     * 得到子节点列表
     */
    private List<DssAdminDept> getChildList(List<DssAdminDept> list, DssAdminDept t) {
        List<DssAdminDept> tlist = new ArrayList<DssAdminDept>();
        Iterator<DssAdminDept> it = list.iterator();
        while (it.hasNext()) {
            DssAdminDept n = (DssAdminDept) it.next();
            if (StringUtils.isNotNull(n.getParentId()) && n.getParentId().longValue() == t.getId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

}
