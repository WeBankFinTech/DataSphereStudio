import API_PATH from "@/common/config/apiPath.js";
import api from "@/common/service/api";

/**
 * 查询主主题域
 * @params {workspaceId}
 * @returns Array
 */
export const getThemedomains = ({ page, size, name } = {}) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/themedomains`,
    { page, size, name },
    "get"
  );

/**
 * 创建主体域
 * @params {workspaceId}
 * @returns Array
 */
export const createThemedomains = body =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/themedomains`,
    body,
    "post"
  );

/**
 * 删除主题域
 * @params {id}
 * @returns Any
 */
export const deleteThemedomains = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/themedomains/${id}`,
    {},
    "delete"
  );

/**
 * 禁用主题域
 * @params {workspaceId}
 * @returns Array
 */
export const disableThemedomains = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/themedomains/${id}/disable`,
    {},
    "put"
  );

/**
 * 启用主题域
 * @params {workspaceId}
 * @returns Array
 */
export const enableThemedomains = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/themedomains/${id}/enable`,
    {},
    "put"
  );

/**
 * 根据id获取主题
 * @params {workspaceId}
 * @returns Array
 */
export const getThemedomainsById = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/themedomains/${id}`,
    {},
    "get"
  );

/**
 * 编辑主题
 * @params {workspaceId}
 * @returns Array
 */
export const editThemedomains = (id, body) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/themedomains/${id}`,
    body,
    "put"
  );

/**
 * 查询所有分层
 * @params {workspaceId}
 * @returns Array
 */
export const getLayersAll = () =>
  api.fetch(`${API_PATH.WAREHOUSE_PATH}data-warehouse/layers/all`, {}, "get");

/**
 * 查询所有预置分层
 * @params {workspaceId}
 * @returns Array
 */
export const getLayersPreset = () =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/layers/preset`,
    {},
    "get"
  );

/**
 * 分页查询自定义分层
 * @params {workspaceId}
 * @returns Array
 */
export const getLayersCustom = (page = 1, size = 10) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/layers/custom`,
    { page, size },
    "get"
  );

/**
 * 新增自定义分层
 * @params {workspaceId}
 * @returns Array
 */
export const createLayersCustom = body =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/layers/custom`,
    body,
    "post"
  );

/**
 * 根据ID查询某个分层信息
 * @params {workspaceId}
 * @returns Array
 */
export const getLayersById = id =>
  api.fetch(`${API_PATH.WAREHOUSE_PATH}data-warehouse/layers/${id}`, {}, "get");

/**
 * 编辑分层
 * @params {workspaceId}
 * @returns Array
 */
export const editLayersCustom = (id, body) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/layers/${id}`,
    body,
    "put"
  );

/**
 * 删除分层
 * @params {workspaceId}
 * @returns Array
 */
export const deleteLayers = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/layers/${id}`,
    {},
    "delete"
  );

/**
 * 禁用分层
 * @params {workspaceId}
 * @returns Array
 */
export const disableLayers = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/layers/${id}/disable`,
    {},
    "put"
  );

/**
 *  启用分层
 * @params {workspaceId}
 * @returns Array
 */
export const enableLayers = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/layers/${id}/enable`,
    {},
    "put"
  );

/**
 *  创建修饰词
 * @params {workspaceId}
 * @returns Array
 */
export const createModifiers = body =>
  api.fetch(`${API_PATH.WAREHOUSE_PATH}data-warehouse/modifiers`, body, "post");

/**
 *  删除修饰词
 * @params {workspaceId}
 * @returns Array
 */
export const deleteModifiers = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/modifiers/${id}`,
    {},
    "delete"
  );

/**
 *  编辑修饰词
 * @params {workspaceId}
 * @returns Array
 */
export const editModifiers = (id, body) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/modifiers/${id}`,
    body,
    "put"
  );

/**
 *  分页获取修饰词
 * @params {workspaceId}
 * @returns Array
 */
export const getModifiers = ({ page, size, name, enabled } = {}) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/modifiers`,
    { page, size, name, enabled },
    "get"
  );

/**
 *  根据id获取
 * @params {workspaceId}
 * @returns Array
 */
export const getModifiersById = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/modifiers/${id}`,
    {},
    "get"
  );

/**
 * 禁用修饰词
 * @params {workspaceId}
 * @returns Array
 */
export const disableModifiers = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/modifiers/${id}/disable`,
    {},
    "put"
  );

/**
 *  启用修饰词
 * @params {workspaceId}
 * @returns Array
 */
export const enableModifiers = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/modifiers/${id}/enable`,
    {},
    "put"
  );

////////

/**
 *  创建统计周期
 * @params {workspaceId}
 * @returns Array
 */
export const createStatisticalPeriods = body =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/statistical_periods`,
    body,
    "post"
  );

/**
 *  删除统计周期
 * @params {workspaceId}
 * @returns Array
 */
export const deleteStatisticalPeriods = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/statistical_periods/${id}`,
    {},
    "delete"
  );

/**
 *  编辑统计周期
 * @params {workspaceId}
 * @returns Array
 */
export const editStatisticalPeriods = (id, body) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/statistical_periods/${id}`,
    body,
    "put"
  );

/**
 *  分页获取统计周期
 * @params {workspaceId}
 * @returns Array
 */
export const getStatisticalPeriods = ({ page, size, name, enabled } = {}) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/statistical_periods`,
    { page, size, name, enabled },
    "get"
  );

/**
 *  根据id获取统计周期
 * @params {workspaceId}
 * @returns Array
 */
export const getStatisticalPeriodsById = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/statistical_periods/${id}`,
    {},
    "get"
  );

/**
 * 禁用统计周期
 * @params {workspaceId}
 * @returns Array
 */
export const disableStatisticalPeriods = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/statistical_periods/${id}/disable`,
    {},
    "put"
  );

/**
 *  启用统计周期
 * @params {workspaceId}
 * @returns Array
 */
export const enableStatisticalPeriods = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/statistical_periods/${id}/enable`,
    {},
    "put"
  );
