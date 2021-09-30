import API_PATH from "@/common/config/apiPath.js";
import api from "@/common/service/api";

/**
 * 度量列表
 * @returns {Object.result}
 *
 */
export const getMeasures = (pageNum, pageSize, isAvailable, owner, name) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}datamodel/measures/list`,
    { pageNum, pageSize, isAvailable, owner, name },
    "post"
  );

/**
 * 新增度量
 * @returns {Object.result}
 *
 */
export const createMeasures = body =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}datamodel/measures`, body, "post");

/**
 * 修改度量
 * @returns {Object.result}
 *
 */
export const editMeasures = (id, body) =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}datamodel/measures/${id}`, body, "put");

/**
 * 根据id获取度量
 * @returns {Object.result}
 *
 */
export const getMeasuresById = id =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}datamodel/measures/${id}`, {}, "get");

/**
 * 启用禁用度量
 * @returns {Object.result}
 *
 */
export const switchMeasuresStatus = (id, status) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}datamodel/measures/${id}`,
    { isAvailable: status },
    "put"
  );

/**
 * 维度列表
 * @returns {Object.result}
 *
 */
export const getDimensions = (pageNum, pageSize, isAvailable, owner, name) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}datamodel/dimensions/list`,
    { pageNum, pageSize, isAvailable, owner, name },
    "post"
  );

/**
 * 新增维度
 * @returns {Object.result}
 *
 */
export const createDimensions = body =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}datamodel/dimensions`, body, "post");

/**
 * 修改维度
 * @returns {Object.result}
 *
 */
export const editDimensions = (id, body) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}datamodel/dimensions/${id}`,
    body,
    "put"
  );

/**
 * 根据id获取维度
 * @returns {Object.result}
 *
 */
export const getDimensionsById = id =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}datamodel/dimensions/${id}`, {}, "get");

/**
 * 启用禁用维度
 * @returns {Object.result}
 *
 */
export const switchDimensionsStatus = (id, status) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}datamodel/dimensions/${id}`,
    { isAvailable: status },
    "put"
  );

///////////////////////////////////////////

/**
 * 指标列表
 * @returns {Object.result}
 *
 */
export const getIndicators = (
  pageNum,
  pageSize,
  isAvailable,
  owner,
  name,
  indicatorType
) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}datamodel/indicators/list`,
    { pageNum, pageSize, isAvailable, owner, name, indicatorType },
    "post"
  );

/**
 * 新增指标
 * @returns {Object.result}
 *
 */
export const createIndicators = body =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}datamodel/indicators`, body, "post");

/**
 * 修改指标
 * @returns {Object.result}
 *
 */
export const editIndicators = (id, body) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}datamodel/indicators/${id}`,
    body,
    "put"
  );

/**
 * 根据id获取指标
 * @returns {Object.result}
 *
 */
export const getIndicatorsById = id =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}datamodel/indicators/${id}`, {}, "get");

/**
 * 启用禁用指标
 * @returns {Object.result}
 *
 */
export const switcIndicatorsStatus = (id, status) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}datamodel/indicators/enable/${id}`,
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
    `${API_PATH.DATAMODEL_PATH}datamodel/indicators/versions/list`,
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
    `${API_PATH.DATAMODEL_PATH}datamodel/indicators/versions/${id}`,
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
    `${API_PATH.DATAMODEL_PATH}datamodel/indicators/versions/rollback`,
    { name, version },
    "post"
  );
/////////////////////////////////////////////

/**
 * 主题域列表
 * @returns {Object.result}
 *
 */
export const getThemesList = () =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}datamodel/indicators/themes/list`,
    {},
    "post"
  );

/**
 * 分层列表
 * @returns {Object.result}
 *
 */
export const getLayersList = () =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}datamodel/indicators/layers/list`,
    {},
    "post"
  );

/**
 * 修饰词列表
 * @returns {Object.result}
 *
 */
export const getModifiersList = () =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}datamodel/indicators/modifiers/list`,
    {},
    "post"
  );

/**
 * 周期列表
 * @returns {Object.result}
 *
 */
export const getCyclesList = () =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}datamodel/indicators/cycles/list`,
    {},
    "post"
  );
