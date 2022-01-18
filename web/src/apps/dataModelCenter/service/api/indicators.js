import API_PATH from "@/common/config/apiPath.js";
import api from "@/common/service/api";

/**
 * 指标列表
 * @returns {Object.result}
 *
 */
export const getIndicators = ({
  pageNum,
  pageSize,
  isAvailable,
  owner,
  name,
  indicatorType,
  warehouseThemeName
}) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}indicators/list`,
    {
      pageNum,
      pageSize,
      isAvailable,
      owner,
      name,
      indicatorType,
      warehouseThemeName
    },
    "post"
  );

/**
 * 新增指标
 * @returns {Object.result}
 *
 */
export const createIndicators = body =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}indicators`, body, "post");

/**
 * 修改指标
 * @returns {Object.result}
 *
 */
export const editIndicators = (id, body) =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}indicators/${id}`, body, "put");

/**
 * 根据id获取指标
 * @returns {Object.result}
 *
 */
export const getIndicatorsById = id =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}indicators/${id}`, {}, "get");

/**
 * 启用禁用指标
 * @returns {Object.result}
 *
 */
export const switcIndicatorsStatus = (id, status) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}indicators/enable/${id}`,
    { isAvailable: status },
    "put"
  );

/**
 * 获取版本列表
 * @returns {Object.result}
 *
 */
export const getIndicatorsVersionList = name =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}indicators/versions/list`,
    { name },
    "post"
  );

/**
 * 新增版本
 * @returns {Object.result}
 *
 */
export const buildIndicatorsVersion = (id, body) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}indicators/versions/${id}`,
    body,
    "post"
  );

/**
 * 回退版本
 * @returns {Object.result}
 *
 */
export const rollbackIndicatorsVersion = (name, version) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}indicators/versions/rollback`,
    { name, version },
    "post"
  );

/**
 * 删除指标
 * @returns {Object.result}
 */
export const delIndicators = id =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}indicators/${id}`, {}, "delete");
