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

// 知识库前端展示
const GetCatalogTop = () => api.fetch(`/dss/guide/query/guidecatalog/top`, 'get')

const GetCatalogById = (id) => api.fetch(`/dss/guide/query/guidecatalog/${id}/detail`, 'get')

const QueryChapter = (param) => api.fetch(`/dss/guide/query/guidechapter`, param, 'get')

const GetChapterDetail = (id) => api.fetch(`/dss/guide/query/guidechapter/${id}`, 'get')

// 以下是后台管理admin
const GetLibraryTreeTop = () => api.fetch(`/dss/guide/admin/guidecatalog/top`, 'get')

const GetLibraryTreeById = (id) => api.fetch(`/dss/guide/admin/guidecatalog/${id}/detail`, 'get')

const SaveCatalog = (data) => api.fetch(`/dss/guide/admin/guidecatalog`, data, 'post')

const DeleteCatalog = (id) => api.fetch(`/dss/guide/admin/guidecatalog/${id}/delete`, {}, 'post')

const GetChapter = (id) => api.fetch(`/dss/guide/admin/guidechapter/${id}`, 'get')

const SaveChapter = (data) => api.fetch(`/dss/guide/admin/guidechapter`, data, 'post')

const DeleteChapter = (id) => api.fetch(`/dss/guide/admin/guidechapter/${id}/delete`, {}, 'post')

export {
  GetGuideByPath,
  GetGuideTree,
  GetContentListByPath,
  SaveGuideGroup,
  DeleteGuideGroup,
  SaveGuideContent,
  UpdateGuideContent,
  GetContent,
  DeleteGuideContent,
  GetCatalogTop,
  GetCatalogById,
  QueryChapter,
  GetChapterDetail,
  GetLibraryTreeTop,
  GetLibraryTreeById,
  SaveCatalog,
  DeleteCatalog,
  GetChapter,
  SaveChapter,
  DeleteChapter
}
