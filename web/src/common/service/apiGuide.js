import api from "./api.js"

// 前端展示使用
const GetGuideByPath = (param) => api.fetch(`/dss/guide/query/groupdetail/`, param, 'get')

// 以下是后台管理admin
const GetGuideTree = () => api.fetch(`/dss/guide/admin/guidegroup/`, 'get')

const GetContentListByPath = (path) => api.fetch(`/dss/guide/admin/guidecontent/${path}`, 'get')

const SaveGuideGroup = (data) => api.fetch(`/dss/guide/admin/guidegroup`, data, 'post')

const DeleteGuideGroup = (id) => api.fetch(`/dss/guide/admin/guidegroup/${id}/delete`, {}, 'post')

const GetContent = (id) => api.fetch(`/dss/guide/admin/guidecontent/${id}`, 'get')

const SaveGuideContent = (data) => api.fetch(`/dss/guide/admin/guidecontent`, data, 'post')

const UpdateGuideContent = (data) => api.fetch(`/dss/guide/admin/guidecontent/${data.id}/content`, data, 'post')

const DeleteGuideContent = (id) => api.fetch(`/dss/guide/admin/guidecontent/${id}/delete`, {}, 'post')

export {
  GetGuideByPath,
  GetGuideTree,
  GetContentListByPath,
  SaveGuideGroup,
  DeleteGuideGroup,
  SaveGuideContent,
  UpdateGuideContent,
  GetContent,
  DeleteGuideContent
}
