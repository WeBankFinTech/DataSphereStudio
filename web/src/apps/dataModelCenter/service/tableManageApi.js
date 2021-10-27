import API_PATH from "@/common/config/apiPath.js";
import api from "@/common/service/api";

/**
 * 搜索表
 * @returns {Object.result}
 */
export const searchTable = ({
  name,
  warehouseLayerName,
  warehouseThemeName,
  pageSize,
  pageNum
}) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/list`,
    { name, warehouseLayerName, warehouseThemeName, pageSize, pageNum },
    "post"
  );

/**
 * 获取收藏列表
 * @returns {Object.result}
 */
export const getCollectList = userName => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/collect/list`,
    { user: userName },
    "post"
  );
};
/**
 * 添加收藏
 * @returns {Object.result}
 */
export const addCollect = tableId => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/collect`,
    { tableId },
    "post"
  );
};
/**
 * 取消收藏
 * @returns {Object.result}
 */
export const delCancel = tableName => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/collect/cancel`,
    { tableName },
    "post"
  );
};

/**
 * 数据库列表
 * @returns {Object.result}
 */
export const getDataBasesList = (name = "") => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/databases/list`,
    { name },
    "post"
  );
};

/**
 * 根据id获取表详情
 * @returns {Object.result}
 */
export const getTableInfoById = id => {
  return api.fetch(`${API_PATH.DATAMODEL_PATH}tables/${id}`, {}, "get");
};

/**
 * 根据name和guid获取表详情
 * @returns {Object.result}
 */
export const getTableInfoByName = (name = "", guid = "") => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/name`,
    { name, guid },
    "post"
  );
};

/**
 * 分区统计信息
 * @returns {Object.result}
 */
export const getTablesPartitionStats = (name = "", guid = "") => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/partition/stats`,
    { name, guid },
    "post"
  );
};

/**
 * 生成建表语句
 * @returns {Object.result}
 */
export const getTablesCreateSql = (tableId = "", guid = "") => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/create/sql`,
    { tableId, guid },
    "post"
  );
};

/**
 * 获取预览信息
 * @returns {Object.result}
 */
export const getTablesPreview = tableName => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/data/preview`,
    { tableName },
    "post"
  );
};

/**
 * 主题域列表
 * @returns {Object.result}
 *
 */
export const getThemesList = () =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}tables/themes/list`, {}, "post");

/**
 * 分层列表
 * @returns {Object.result}
 *
 */
export const getLayersList = () =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}tables/layers/list`, {}, "post");

/**
 * 字典列表
 * @returns {Object.result}
 *
 */
export const getDictionariesList = type =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/dictionaries/list`,
    { type },
    "post"
  );

/**
 * 新增表
 * @returns {Object.result}
 *
 */
export const addTable = body =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}tables`, body, "post");

/**
 * 更新表
 * @returns {Object.result}
 *
 */
export const updateTable = (id, body) =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}tables/${id}`, body, "put");

/**
 * 执行表创建
 * @returns {Object.result}
 *
 */
export const createTable = tableId =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}tables/create`, { tableId }, "post");

/**
 * 版本列表
 * @returns {Object.result}
 *
 */
export const getVersionListByName = name =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}tables/versions/list`, { name }, "post");

/**
 * 版本回退
 * @returns {Object.result}
 *
 */
export const tableVersionRollback = (name, version) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/versions/rollback`,
    { name, version },
    "post"
  );

/**
 * 新增版本
 * @returns {Object.result}
 *
 */
export const generatorNewVersion = (id, data) =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}tables/versions/${id}`, data, "post");

/**
 * 检查表是否有数据
 * @returns {Object.result}
 *
 */
export const checkTableData = tableName =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/check/data`,
    { tableName },
    "post"
  );
