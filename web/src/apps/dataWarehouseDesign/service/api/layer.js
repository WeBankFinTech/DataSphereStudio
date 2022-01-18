import API_PATH from "@/common/config/apiPath.js";
import api from "@/common/service/api";

/**
 * 查询所有分层
 * @param isAvailable {Number} 是否启用
 * @return {Promise}
 */
export const getLayersAll = ({isAvailable = undefined}) => api.fetch(`${API_PATH.WAREHOUSE_PATH}layers/all`, {isAvailable}, "get")

/**
 * 查询所有预置分层
 * @return {Promise}
 */
export const getLayersPreset = () => api.fetch(`${API_PATH.WAREHOUSE_PATH}layers/preset`,{}, "get");

/**
 * 分页查询自定义分层
 * @param page {Number} 当前页
 * @param size {Number} 分页数量
 * @returns {Promise}
 */
export const getLayersCustom = ({page = 1,size = 10}) => api.fetch(`${API_PATH.WAREHOUSE_PATH}layers/custom`, { page,size }, "get");

/**
 * 新增自定义分层
 * @param body {Object} 数据体
 * @returns {Promise}
 */
export const createLayersCustom = body => api.fetch(`${API_PATH.WAREHOUSE_PATH}layers/custom`, body, "post");

/**
 * 根据ID查询某个分层信息
 * @param id {Number} id
 * @returns {Promise}
 */
export const getLayersById = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}layers/${id}`, {}, "get");

/**
 * 编辑分层
 * @param id {Number} id
 * @param body {Object} 数据体
 * @returns {Promise}
 */
export const editLayersCustom = (id, body) => api.fetch(`${API_PATH.WAREHOUSE_PATH}layers/${id}`, body, "put");

/**
 * 删除分层
 * @param id {Number} id
 * @returns {Promise}
 */
export const deleteLayers = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}layers/${id}`, {}, "delete");

/**
 * 禁用分层
 * @param id {Number} id
 * @returns {Promise}
 */
export const disableLayers = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}layers/${id}/disable`, {}, "put");

/**
 * 启用分层
 * @param id {Number} id
 * @returns {Promise}
 */
export const enableLayers = id => api.fetch(`${API_PATH.WAREHOUSE_PATH}layers/${id}/enable`, {}, "put");

/**
 * 获取可用库
 * @returns {Promise}
 */
export const getDbs = () => api.fetch(`${API_PATH.WAREHOUSE_PATH}dbs/hive`, {}, "get");
