import api from "./api.js"

const API_PATHG_PERMISSIONS = "/dss/framework/admin/";
const API_PATHG_PERMISSIONS_USER = "/dss/framework/admin/user";

// 获取部门列表
const GetDepartmentList = (query) => api.fetch(`${API_PATHG_PERMISSIONS}dept/list${query}`, {}, 'get');

// 获取部门树形列表
const GetDepartmentTree = (params) => api.fetch(`${API_PATHG_PERMISSIONS}dept/treeselect`, params, 'get');

// 新增部门
const AddNewDepartment = (params) => api.fetch(`${API_PATHG_PERMISSIONS}dept`, params, 'post');

// 修改部门信息
const ModifyDepartment = (params) => api.fetch(`${API_PATHG_PERMISSIONS}dept/edit`, params, 'post');

// 获取部门详情
const GetDepartmentDetail = (id) => api.fetch(`${API_PATHG_PERMISSIONS}dept/${id}`, {}, 'get');

// 删除部门
const DeleteDepartment = (id) => api.fetch(`${API_PATHG_PERMISSIONS}dept/${id}`, {}, 'post');


// 获取用户列表
const GetUserList = (query) => api.fetch(`${API_PATHG_PERMISSIONS_USER}/list${query}`, {}, 'get');


// 新增用户
const AddNewUser = (params) => api.fetch(`${API_PATHG_PERMISSIONS_USER}/add`, params, 'post');

// 修改用户信息
const ModifyUser = (params) => api.fetch(`${API_PATHG_PERMISSIONS_USER}/edit`, params, 'post');

// 修改用户密码
const ModifyUserPassword = (params) => api.fetch(`${API_PATHG_PERMISSIONS_USER}/resetPsw`, params, 'post');

export {
  GetDepartmentDetail,
  GetDepartmentList,
  GetDepartmentTree,
  AddNewDepartment,
  ModifyDepartment,
  DeleteDepartment,
  GetUserList,
  AddNewUser,
  ModifyUser,
  ModifyUserPassword
}