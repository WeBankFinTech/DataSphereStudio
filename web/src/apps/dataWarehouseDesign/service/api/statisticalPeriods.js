import api from "@/common/service/api";
import API_PATH from "@/common/config/apiPath";

/**
 *  创建统计周期
 * @param body {Object} 数据体
 * @return {Promise}
 */
export const createStatisticalPeriods = body => api.fetch(`${API_PATH.WAREHOUSE_PATH}statistical_periods`, body, "post");

/**
 *  删除统计周期
 * @param id {Number} 统计周期id
 * @return {Promise}
 */
export const deleteStatisticalPeriods = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}statistical_periods/${id}`, {}, "delete");

/**
 * 编辑统计周期
 * @param id {Number} 统计周期id
 * @param body {Object} 数据体
 * @return {Promise}
 */
export const editStatisticalPeriods = (id, body) => api.fetch(`${API_PATH.WAREHOUSE_PATH}statistical_periods/${id}`, body, "put");

/**
 * 分页获取统计周期
 * @param page {Number} 当前页
 * @param size {分页数量} 数量
 * @param name {String} 搜索名称
 * @param enabled {Boolean} 是否启用
 * @return {Promise}
 */
export const getStatisticalPeriods = ({page, size, name, enabled} = {}) => api.fetch(`${API_PATH.WAREHOUSE_PATH}statistical_periods`, {page, size, name, enabled}, "get");

/**
 *  根据id获取统计周期
 * @param id {Number} 周期id
 * @return {Promise}
 */
export const getStatisticalPeriodsById = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}statistical_periods/${id}`, {}, "get");

/**
 * 禁用统计周期
 * @param id {Number} 周期id
 * @return {Promise}
 */
export const disableStatisticalPeriods = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}statistical_periods/${id}/disable`, {}, "put");

/**
 *  启用统计周期
 * @param id {Number} 周期id
 * @return {Promise}
 */
export const enableStatisticalPeriods = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}statistical_periods/${id}/enable`, {}, "put");
