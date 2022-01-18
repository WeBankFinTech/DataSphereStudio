import API_PATH from "@/common/config/apiPath.js";
import api from "@/common/service/api";

/**
 * 度量列表
 * @returns {Object.result}
 *
 */
export const getMeasures = ({
  pageNum,
  pageSize,
  isAvailable,
  owner,
  name,
  warehouseThemeName
}) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}measures/list`,
    {pageNum, pageSize, isAvailable, owner, name, warehouseThemeName},
    "post"
  );

/**
 * 新增度量
 * @returns {Object.result}
 *
 */
export const createMeasures = body =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}measures`, body, "post");

/**
 * 修改度量
 * @returns {Object.result}
 *
 */
export const editMeasures = (id, body) =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}measures/${id}`, body, "put");

/**
 * 根据id获取度量
 * @returns {Object.result}
 *
 */
export const getMeasuresById = id =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}measures/${id}`, {}, "get");

/**
 * 启用禁用度量
 * @returns {Object.result}
 *
 */
export const switchMeasuresStatus = (id, status) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}measures/enable/${id}`,
    {isAvailable: status},
    "put"
  );

/**
 * 删除度量
 * @returns {Object.result}
 *
 */
export const delMeasures = id =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}measures/${id}`, {}, "delete");
