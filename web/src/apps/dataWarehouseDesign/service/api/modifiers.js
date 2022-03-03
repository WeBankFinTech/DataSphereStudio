import api from "@/common/service/api";
import API_PATH from "@/common/config/apiPath";

/**
 * 创建修饰词
 * @param body {Object} 数据体
 * @return {Promise}
 */
export const createModifiers = body => api.fetch(`${API_PATH.WAREHOUSE_PATH}modifiers`, body, "post");

/**
 *  删除修饰词
 * @param id {Number} id
 * @return {Promise}
 */
export const deleteModifiers = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}modifiers/${id}`, {}, "delete");

/**
 *  编辑修饰词
 * @param id {Number} id
 * @param body {Object} 数据体
 * @return {Promise}
 */
export const editModifiers = (id, body) => api.fetch(`${API_PATH.WAREHOUSE_PATH}modifiers/${id}`, body, "put");

/**
 * 分页获取修饰词
 * @param page {Number} 当前页
 * @param size {分页数量} 数量
 * @param name {String} 搜索名称
 * @param enabled {Boolean} 是否启用
 * @return {Promise}
 */
export const getModifiers = ({page, size, name, enabled} = {}) => api.fetch(`${API_PATH.WAREHOUSE_PATH}modifiers`, {page, size, name, enabled}, "get");

/**
 * 根据id获取
 * @param id {Number} id
 * @return {Promise}
 */
export const getModifiersById = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}modifiers/${id}`, {}, "get");

/**
 * 禁用修饰词
 * @param id {Number} id
 * @return {Promise}
 */
export const disableModifiers = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}modifiers/${id}/disable`, {}, "put");

/**
 *  启用修饰词
 * @param id {Number} id
 * @return {Promise}
 */
export const enableModifiers = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}modifiers/${id}/enable`, {}, "put");
