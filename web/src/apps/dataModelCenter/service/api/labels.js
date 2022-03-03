import API_PATH from "@/common/config/apiPath.js";
import api from "@/common/service/api";

/**
 * 标签列表
 * @returns {Object.result}
 */
export const getLabelList = ({
  pageNum,
  pageSize,
  isAvailable,
  name,
  owner,
  warehouseThemeName
}) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}labels/list`,
    {
      pageNum,
      pageSize,
      isAvailable,
      name,
      owner,
      warehouseThemeName
    },
    "post"
  );

/**
 * 根据id获取标签
 * @returns {Object.result}
 */
export const getLabelById = id =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}labels/${id}`, {}, "get");

/**
 * 更新标签
 * @returns {Object.result}
 */
export const updateLabel = (id, body) =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}labels/${id}`, body, "put");

/**
 * 删除标签
 * @returns {Object.result}
 */
export const delLabel = id =>
  api.fetch(`${API_PATH.DATAMODEL_PATH}labels/${id}`, {}, "delete");

/**
 * 创建标签
 * @returns {Object.result}
 */
export const createLabel = body => {
  console.log(body);
  return api.fetch(`${API_PATH.DATAMODEL_PATH}labels`, body, "post");
};

/**
 * 启用禁用指标
 * @returns {Object.result}
 *
 */
export const switcLabelStatus = (id, status) =>
  api.fetch(
    `${API_PATH.DATAMODEL_PATH}labels/enable/${id}`,
    {isAvailable: status},
    "put"
  );
