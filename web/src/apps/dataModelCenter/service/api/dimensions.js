import API_PATH from "@/common/config/apiPath.js";
import api from "@/common/service/api";

/**
 * 维度列表
 * @returns {Object.result}
 *
 */
export const getDimensions = ({
  pageNum,
  pageSize,
  isAvailable,
  owner,
  name,
  warehouseThemeName
}) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}dimensions/list`,
    { pageNum, pageSize, isAvailable, owner, name, warehouseThemeName },
    "post"
  );

/**
 * 新增维度
 * @returns {Object.result}
 *
 */
export const createDimensions = body =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}dimensions`, body, "post");

/**
 * 修改维度
 * @returns {Object.result}
 *
 */
export const editDimensions = (id, body) =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}dimensions/${id}`, body, "put");

/**
 * 根据id获取维度
 * @returns {Object.result}
 *
 */
export const getDimensionsById = id =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}dimensions/${id}`, {}, "get");

/**
 * 启用禁用维度
 * @returns {Object.result}
 *
 */
export const switchDimensionsStatus = (id, status) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}dimensions/enable/${id}`,
    { isAvailable: status },
    "put"
  );

/**
 * 删除维度
 * @returns {Object.result}
 */
export const delDimensions = id =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}dimensions/${id}`, {}, "delete");
