import api from "./api.js"

const GetGuideTree = () => api.fetch(`/dss/guide/admin/guidegroup/`, 'get')

const GetContentListByPath = (path) => api.fetch(`/dss/guide/admin/guidecontent/${path}`, 'get')

const SaveGuideGroup = (data) => api.fetch(`/dss/guide/admin/guidegroup`, data, 'post')

const GetContent = (id) => api.fetch(`/dss/guide/admin/guidecontent/${id}`, 'get')

const SaveGuideContent = (data) => api.fetch(`/dss/guide/admin/guidecontent`, data, 'post')

const UpdateGuideContent = (data) => api.fetch(`/dss/guide/admin/guidecontent/${data.id}/content`, data, 'post')

export {
  GetGuideTree,
  GetContentListByPath,
  SaveGuideGroup,
  SaveGuideContent,
  UpdateGuideContent,
  GetContent
}
