import api from './api.js'

// 获取类别数据
const GetMenu =  () => api.fetch('/application/getMenu', {}, 'get');

// 查询全部数据
const QueryAllData = () => api.fetch('/application/query', {}, 'get');

// 通过 id 获取数据
const QueryDataFromId = (id) => api.fetch(`/application/query?id=${id}`, {}, 'get');

// 通过 id 进行更新
const UpdateDataFromId = (id, params) => api.fetch(`/application/update?id=${id}`, params, 'post');

// 创建新的数据
const CreateData = (params) => api.fetch('/application/update', params, 'post');
export {
  GetMenu,
  QueryAllData,
  QueryDataFromId,
  UpdateDataFromId,
  CreateData
}
