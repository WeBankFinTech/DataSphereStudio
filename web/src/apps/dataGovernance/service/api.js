import API_PATH from '@/common/config/apiPath.js';
import api from '@/common/service/api';

/**
 * 数据资产概要
 * @returns {Object.result}
 *
 */
const getHiveSummary = () => api.fetch(`${API_PATH.DATA_GOVERNANCE}hiveSummary`, 'get');

/**
 * 查询hive表--基础&列
 * @param {*} guid
 * @returns
 */
const getHiveTblBasic = guid => api.fetch(`${API_PATH.DATA_GOVERNANCE}hiveTbl/${guid}/basic`, {}, 'get')

/**
 * 查询hive表-分区信息
 * @param {*} guid
 * @returns
 */
const getHiveTblPartition = guid => api.fetch(`${API_PATH.DATA_GOVERNANCE}hiveTbl/${guid}/partition`, {}, 'get')

/**
 * 查询hive表--select语句
 * @param {*} guid
 * @returns
 */
const getSelectSql = guid => api.fetch(`${API_PATH.DATA_GOVERNANCE}hiveTbl/${guid}/select`, {}, 'get');

/**
 * 查询hive表--create语句
 * @param {*} guid
 * @returns
 */
const getSelectDdl = guid => api.fetch(`${API_PATH.DATA_GOVERNANCE}hiveTbl/${guid}/create`, {}, 'get')

export {
  getHiveSummary,
  getHiveTblBasic,
  getHiveTblPartition,
  getSelectSql,
  getSelectDdl
}
