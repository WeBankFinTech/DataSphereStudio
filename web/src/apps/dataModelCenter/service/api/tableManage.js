import API_PATH from "@/common/config/apiPath.js";
import api from "@/common/service/api";

/**
 * @description 表搜索
 * @param name {String} 表名
 * @param warehouseLayerName {String} 分层
 * @param warehouseThemeName {String} 主题
 * @param pageSize {Number} 分页大小
 * @param pageNum {Number} 当前页
 * @param modelType {Number} 模型类型
 * @param modelName {String} 模型名字
 * @param tableType {Number} 逻辑表或物理表
 */
export const searchTable = ({
  name,
  warehouseLayerName,
  warehouseThemeName,
  pageSize,
  pageNum,
  modelType,
  modelName,
  tableType
}) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/list`,
    {
      name,
      warehouseLayerName,
      warehouseThemeName,
      pageSize,
      pageNum,
      modelType,
      modelName,
      tableType
    },
    "post"
  );

/**
 * @description 获取收藏表
 * @returns {Promise}
 * @param name {String}
 */
export const getCollectList = ({name} = {}) => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/collect/list`,
    {name},
    "post"
  );
};
/**
 * @description 添加收藏
 * @param
 * @returns {Promise}
 */
export const addCollect = body => {
  return api.fetch(`${API_PATH.DATAMODEL_PATH}tables/collect`, body, "post");
};
/**
 * 取消收藏
 * @returns {Promise}
 */
export const delCancel = tableName => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/collect/cancel`,
    {tableName},
    "post"
  );
};

/**
 * 数据库列表
 * @returns {Promise}
 */
export const getDataBasesList = (name = "") => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/databases/list`,
    {name},
    "post"
  );
};

/**
 * 根据id获取表详情
 * @returns {Promise}
 */
export const getTableInfoById = id => {
  return api.fetch(`${API_PATH.DATAMODEL_PATH}tables/${id}`, {}, "get");
};

/**
 * 根据name和guid获取表详情
 * @returns {Promise}
 */
export const getTableInfoByName = (name = "", guid = "") => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/name`,
    {name, guid},
    "post"
  );
};

/**
 * 分区统计信息
 * @returns {Promise}
 */
export const getTablesPartitionStats = (name = "", guid = "") => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/partition/stats`,
    {name, guid},
    "post"
  );
};

/**
 * 生成建表语句
 * @returns {Promise}
 */
export const getTablesCreateSql = (tableId = "", guid = "") => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/create/sql`,
    {tableId, guid},
    "post"
  );
};

/**
 * 获取预览信息
 * @returns {Promise}
 */
export const getTablesPreview = tableName => {
  return api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/data/preview`,
    {tableName},
    "post"
  );
};

/**
 * 字典列表
 * @returns {Promise}
 *
 */
export const getDictionariesList = type =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/dictionaries/list`,
    {type},
    "post"
  );

/**
 * 新增表
 * @returns {Promise}
 *
 */
export const addTable = body =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}tables`, body, "post");

/**
 * 更新表
 *  @returns {Promise}
 *
 */
export const updateTable = (id, body) =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}tables/${id}`, body, "put");

/**
 * 执行表创建
 *  @returns {Promise}
 *
 */
export const createTable = tableId =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}tables/create`, {tableId}, "post");

/**
 * 版本列表
 *  @returns {Promise}
 *
 */
export const getVersionListByName = name =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}tables/versions/list`, {name}, "post");

/**
 * 版本回退
 *  @returns {Promise}
 *
 */
export const tableVersionRollback = (name, version) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/versions/rollback`,
    {name, version},
    "post"
  );

/**
 * 新增版本
 *  @returns {Promise}
 *
 */
export const generatorNewVersion = (id, data) =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}tables/versions/${id}`, data, "post");

/**
 * 检查表是否有数据
 * @returns {Promise}
 *
 */
export const checkTableData = tableName =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}tables/check/data`,
    {tableName},
    "post"
  );

/**
 * @description 删除表
 * @param {String} id
 * @returns {Promise}
 */
export const deleteTableById = id =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}tables/${id}`, {}, "delete");

/**
 * 主动绑定
 *  @returns {Promise}
 *
 */
export const bindTable = id =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}tables/bind/${id}`, "put");
