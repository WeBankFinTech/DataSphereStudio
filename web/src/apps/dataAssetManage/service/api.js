import API_PATH from '@/common/config/apiPath.js'
import api from '@/common/service/api'

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
    `${API_PATH.DATA_GOVERNANCE}hiveTbl/search?query=${params.query}&owner=${params.owner}&label=${params.label}&limit=${params.limit}&offset=${params.offset}`,
    {},
    'get'
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
export const getWorkspaceUsers = (workspackId) =>
  api.fetch(
    `${API_PATH.DATA_GOVERNANCE}users/${workspackId}`,
    {},
    'get'
  )

/**
 * 标签查询
 * @params {workspaceId}
 * @returns Array
 */
export const getLabels = (search) =>
  api.fetch(
    `${API_PATH.DATA_GOVERNANCE}labels/search?query=${search}`,
    {},
    'get'
  )
