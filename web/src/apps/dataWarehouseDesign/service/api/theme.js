import API_PATH from "@/common/config/apiPath.js";
import api from "@/common/service/api";

/**
 * 查询主主题域
 * @param page {Number} 当前页
 * @param size {分页数量} 数量
 * @param name {String} 搜索名称
 * @param enabled {Boolean} 是否启用
 * @returns {Promise}
 */
export const getThemedomains = ({page, size, name, enabled} = {}) => api.fetch(`${API_PATH.WAREHOUSE_PATH}themedomains`, {page, size, name, enabled}, "get");

/**
 * 创建主题域
 * @param body {Object} 数据体
 * @returns {Promise}
 */
export const createThemedomains = body => api.fetch(`${API_PATH.WAREHOUSE_PATH}themedomains`, body, "post");

/**
 * 删除主题域
 * @params {id}
 * @returns {Promise}
 */
export const deleteThemedomains = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}themedomains/${id}`, {}, "delete");

/**
 * 禁用主题域
 * @param id {Number} id
 * @returns {Promise}
 */
export const disableThemedomains = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}themedomains/${id}/disable`, {}, "put");

/**
 * 启用主题域
 * @param id {Number} id
 * @returns {Promise}
 */
export const enableThemedomains = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}themedomains/${id}/enable`, {}, "put");

/**
 * 根据id获取主题
 * @param id {Number} id
 * @returns {Promise}
 */
export const getThemedomainsById = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}themedomains/${id}`, {}, "get");

/**
 * 更新主题
 * @param id {Number} id
 * @param body {Object} 数据体
 * @returns {Promise}
 */
export const editThemedomains = (id, body) => api.fetch(`${API_PATH.WAREHOUSE_PATH}themedomains/${id}`, body, "put");

