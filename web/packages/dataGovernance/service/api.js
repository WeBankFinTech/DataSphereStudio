import API_PATH from '@dataspherestudio/shared/common/config/apiPath.js'
import api from '@dataspherestudio/shared/common/service/api'

/**
 * 数据资产概要
 * @returns {Object.result}
 *
 */
export const getHiveSummary = () =>
  api.fetch(`${API_PATH.DATA_GOVERNANCE}hiveSummary`, 'get')

/**
 * 查询hive表--基础&列
 * @param {*} guid
 * @returns
 */
export const getHiveTblBasic = guid =>
  api.fetch(`${API_PATH.DATA_GOVERNANCE}hiveTbl/${guid}/basic`, {}, 'get')

/**
 * 查询hive表-分区信息
 * @param {*} guid
 * @returns
 */
export const getHiveTblPartition = guid =>
  api.fetch(`${API_PATH.DATA_GOVERNANCE}hiveTbl/${guid}/partition`, {}, 'get')

/**
 * 查询hive表--select语句
 * @param {*} guid
 * @returns
 */
export const getSelectSql = guid =>
  api.fetch(`${API_PATH.DATA_GOVERNANCE}hiveTbl/${guid}/select`, {}, 'get')

/**
 * 查询hive表--create语句
 * @param {*} guid
 * @returns
 */
export const getSelectDdl = guid =>
  api.fetch(`${API_PATH.DATA_GOVERNANCE}hiveTbl/${guid}/create`, {}, 'get')

/**
 * 搜索hive表
 * @param {query}
 * @returns
 */
export const getHiveTbls = params =>
  api.fetch(
    `${API_PATH.DATA_GOVERNANCE}hiveTbl/search`,
    params,
    'get'
  )

/**
 * 修改主题域，分层
 * @params {guid, data}
 * @returns String
 */
export const updateClassifications = (guid, data) =>
  api.fetch(
    `${API_PATH.DATA_GOVERNANCE}hiveTbl/${guid}/classifications`,
    data,
    'put'
  )


/*
 * 查询hive表--血缘
 * @param {*} guid
 * @returnsL
 */
export const getLineage = guid =>
  api.fetch(`${API_PATH.DATA_GOVERNANCE}hiveTbl/${guid}/lineage`, {}, 'get')

/**
 *  批量修改注释
 * @params {Map}
 * @returns
 */
export const putCommetBulk = params => {
  return api.fetch(`${API_PATH.DATA_GOVERNANCE}comment/bulk`, params, 'put')
}

/**
 * 存储量前10表
 * @params {void}
 * @returns Array
 */
export const getTopStorage = () =>
  api.fetch(`${API_PATH.DATA_GOVERNANCE}hiveTbl/topStorage`, {}, 'get')

/**
 * 设置标签--表或列
 * @params {guid}
 * @returns String
 */
export const postSetLabel = (guid, params) =>
  api.fetch(`${API_PATH.DATA_GOVERNANCE}label/${guid}`, params, 'post')

/**
 * 删除标签
 * @params {guid}
 * @returns String
 */
export const putRemoveLabel = (guid, params) =>
  api.fetch(`${API_PATH.DATA_GOVERNANCE}label/${guid}`, params, 'put')

/**
 * 修改注释--表或列
 * @params {guid, Obiect}
 * @returns String
 */
export const postSetComment = (guid, comment) =>
  api.fetch(
    `${API_PATH.DATA_GOVERNANCE}comment/${guid}?comment=${comment}`,
    {},
    'put'
  )

/**
 * 负责人查询
 * @params {workspaceId}
 * @returns Array
 */
export const getWorkspaceUsers = (workspackId, search) =>
  api.fetch(
    `${API_PATH.DATA_GOVERNANCE}getWorkspaceUsers/${workspackId}/${search}`,
    {},
    'get'
  )

/**
 * 查询主主题域
 * @params {workspaceId}
 * @returns Array
 */
export const getThemedomains = (keyword) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}subject/subtypes`,
    { keyword },
    'get'
  )

/**
 * 创建主体域
 * @params {workspaceId}
 * @returns Array
 */
export const createThemedomains = body =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}batch`,
    body,
    'post'
  )

/**
 * 删除主题域
 * @params {id}
 * @returns Any
 */
export const deleteThemedomains = name =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}${name}/delete`,
    {},
    'post'
  )

/**
 * 禁用主题域
 * @params {workspaceId}
 * @returns Array
 */
export const disableThemedomains = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/themedomains/${id}/disable`,
    {},
    'put'
  )

/**
 * 启用主题域
 * @params {workspaceId}
 * @returns Array
 */
export const enableThemedomains = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/themedomains/${id}/enable`,
    {},
    'put'
  )

/**
 * 根据name获取主题
 * @params {workspaceId}
 * @returns Array
 */
export const getThemedomainsById = name =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}${name}`,
    {},
    'get'
  )

/**
 * 编辑主题
 * @params {workspaceId}
 * @returns Array
 */
export const editThemedomains = (body) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}batch`,
    body,
    'put'
  )

/**
 * 查询所有分层
 * @params {workspaceId}
 * @returns Array
 */
export const getLayersAll = () =>
  api.fetch(`${API_PATH.WAREHOUSE_PATH}layers/all`, {}, 'get')

/**
 * 查询所有预置分层
 * @params {workspaceId}
 * @returns Array
 */
export const getLayersPreset = () =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}layer_system/subtypes`,
    {},
    'get'
  )

/**
 * 查询自定义分层
 * @params {workspaceId}
 * @returns Array
 */
export const getLayersCustom = (keyword) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}layer/subtypes`,
    {keyword},
    'get'
  )

/**
 * 新增自定义分层
 * @params {workspaceId}
 * @returns Array
 */
export const createLayersCustom = body =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}batch`,
    body,
    'post'
  )

/**
 * 根据ID查询某个分层信息
 * @params {workspaceId}
 * @returns Array
 */
export const getLayersById = name =>
  api.fetch(`${API_PATH.WAREHOUSE_PATH}${name}`, {}, 'get')

/**
 * 编辑分层
 * @params {workspaceId}
 * @returns Array
 */
export const editLayersCustom = (body) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}batch`,
    body,
    'put'
  )

/**
 * 删除分层
 * @params {workspaceId}
 * @returns Array
 */
export const deleteLayers = name =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}${name}/delete`,
    {},
    'post'
  )

/**
 * 禁用分层
 * @params {workspaceId}
 * @returns Array
 */
export const disableLayers = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/layers/${id}/disable`,
    {},
    'put'
  )

/**
 *  启用分层
 * @params {workspaceId}
 * @returns Array
 */
export const enableLayers = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/layers/${id}/enable`,
    {},
    'put'
  )

/**
 *  创建修饰词
 * @params {workspaceId}
 * @returns Array
 */
export const createModifiers = body =>
  api.fetch(`${API_PATH.WAREHOUSE_PATH}data-warehouse/modifiers`, body, 'post')

/**
 *  删除修饰词
 * @params {workspaceId}
 * @returns Array
 */
export const deleteModifiers = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/modifiers/${id}`,
    {},
    'delete'
  )

/**
 *  编辑修饰词
 * @params {workspaceId}
 * @returns Array
 */
export const editModifiers = (id, body) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/modifiers/${id}`,
    body,
    'put'
  )

/**
 *  分页获取修饰词
 * @params {workspaceId}
 * @returns Array
 */
export const getModifiers = (page, size, name, enabled) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/modifiers`,
    { page, size, name, enabled },
    'get'
  )

/**
 *  根据id获取
 * @params {workspaceId}
 * @returns Array
 */
export const getModifiersById = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/modifiers/${id}`,
    {},
    'get'
  )

/**
 * 禁用修饰词
 * @params {workspaceId}
 * @returns Array
 */
export const disableModifiers = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/modifiers/${id}/disable`,
    {},
    'put'
  )

/**
 *  启用修饰词
 * @params {workspaceId}
 * @returns Array
 */
export const enableModifiers = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/modifiers/${id}/enable`,
    {},
    'put'
  )

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
    'post'
  )

/**
 *  删除统计周期
 * @params {workspaceId}
 * @returns Array
 */
export const deleteStatisticalPeriods = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/statistical_periods/${id}`,
    {},
    'delete'
  )

/**
 *  编辑统计周期
 * @params {workspaceId}
 * @returns Array
 */
export const editStatisticalPeriods = (id, body) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/statistical_periods/${id}`,
    body,
    'put'
  )

/**
 *  分页获取统计周期
 * @params {workspaceId}
 * @returns Array
 */
export const getStatisticalPeriods = (page, size, name, enabled) =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/statistical_periods`,
    { page, size, name, enabled },
    'get'
  )

/**
 *  根据id获取统计周期
 * @params {workspaceId}
 * @returns Array
 */
export const getStatisticalPeriodsById = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/statistical_periods/${id}`,
    {},
    'get'
  )

/**
 * 禁用统计周期
 * @params {workspaceId}
 * @returns Array
 */
export const disableStatisticalPeriods = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/statistical_periods/${id}/disable`,
    {},
    'put'
  )

/**
 *  启用统计周期
 * @params {workspaceId}
 * @returns Array
 */
export const enableStatisticalPeriods = id =>
  api.fetch(
    `${API_PATH.WAREHOUSE_PATH}data-warehouse/statistical_periods/${id}/enable`,
    {},
    'put'
  )

// export {
//   getHiveSummary,
//   getHiveTblBasic,
//   getHiveTblPartition,
//   getSelectSql,
//   getSelectDdl,
//   getHiveTbls,
//   getLineage,
//   putCommetBulk,
//   getTopStorage,
//   postSetLabel,
//   postSetComment,
//   getWorkspaceUsers,
//   getThemedomains,
//   createThemedomains,
//   deleteThemedomains,
//   disableThemedomains,
//   enableThemedomains,
//   getThemedomainsById,
//   editThemedomains
// }
