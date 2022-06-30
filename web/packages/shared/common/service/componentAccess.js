import api from './api.js'

// 获取类别数据
const GetMenu =  () => api.fetch('dss/framework/admin/component/query', {}, 'get');

// 查询全部数据
const QueryAllData = () => api.fetch('dss/framework/admin/component/queryAll', {}, 'get');

// 通过 id 获取数据
const QueryDataFromId = (id) => api.fetch(`dss/framework/admin/component/query/${id}`, {}, 'get');

// 通过 id 进行更新
const UpdateDataFromId = (id, params) => api.fetch(`dss/framework/admin/component/createApplication`, {id, ...params}, 'post');

// 创建新的数据
const CreateData = (params) => api.fetch('dss/framework/admin/component/createApplication', params, 'post');
export {
  GetMenu,
  QueryAllData,
  QueryDataFromId,
  UpdateDataFromId,
  CreateData
}
