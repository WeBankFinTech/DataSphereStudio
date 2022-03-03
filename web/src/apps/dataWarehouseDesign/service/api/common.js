import api from "@/common/service/api";
import API_PATH from "@/common/config/apiPath";

/**
 * 获取可用角色列表
 * @param workspaceid {String} 工作区id
 * @return {Promise}
 */
export const getRolesList = (workspaceid) => api.fetch(`${API_PATH.WAREHOUSE_PATH}workspace/${workspaceid}/principal_roles`, {}, "get");

/**
 * 获取所有用户列表
 * @param workspaceid {String} 工作区id
 * @return {Promise}
 */
export const getUsersList = (workspaceid) => api.fetch(`${API_PATH.WAREHOUSE_PATH}workspace/${workspaceid}/principal_users`, {}, "get");
