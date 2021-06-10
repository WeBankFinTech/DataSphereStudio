import api from "./api.js"
import API_PATH from '@/common/config/apiPath.js'
import storage from "@/common/helper/storage";
// 获取基本信息接口
const GetBaseInfo = (flag = true) => {
  // 如果缓存里有直接返回
  const baseInfo = storage.get('baseInfo', 'local')
  if (baseInfo && flag) {
    return new Promise((resolve) => {
      resolve(baseInfo)
    })
  } else {
    return api.fetch(`${API_PATH.WORKSPACE_PATH}getBaseInfo`, "get")
  }
}

// 获取编排模式选项卡
const GetDicSecondList = (params) => {
  return api.fetch(`${API_PATH.WORKSPACE_PATH}getDicSecondList`, {
    parentKey: "p_orchestrator_mode",
    workspaceId: params
  })
}
// 获取工作空间用户管理相关数据
const GetWorkspaceUserManagement = (params) => api.fetch(`${API_PATH.WORKSPACE_PATH}getWorkspaceUsers`, params, 'get')

// 获取工作空间用户的列表
const GetWorkspaceUserList = (params) => api.fetch(`${API_PATH.WORKSPACE_PATH}getAllWorkspaceUsers`, params, 'get')

// 获取工作空间数据
const GetWorkspaceData = (params) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces/${params}`, 'get')

// 获取工作空间应用商店数据
const GetWorkspaceApplications = (params) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces/${params}/applications`, {}, {method: 'get', cacheOptions: {time: 3000}})

// 获取工作空间归属部门数据
const GetDepartments = () => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces/departments`, 'get')

// 判断工作空间是否重复
const CheckWorkspaceNameExist = (params) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces/exists`, params, 'get')

// 获取流程数据字典数据
const GetDicList = (params) => api.fetch(`${API_PATH.WORKSPACE_PATH}getDicList`, params, 'post')

// 获取工作空间列表或修改
const GetWorkspaceList = (params, method) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces`, params, method)

// 获取工作空间基础信息
const GetWorkspaceBaseInfo = (params) =>  api.fetch(`${API_PATH.WORKSPACE_PATH}getWorkspaceBaseInfo`, params, 'get')

// 获取工程的应用领域
const GetAreaMap = () =>  api.fetch(`${API_PATH.PROJECT_PATH}listApplicationAreas`, "get")

// 获取常用功能列表
const GetFavorites = (workspaceId) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces/${workspaceId}/favorites`, workspaceId, 'get')

// 添加常用应用
const AddFavorite = (workspaceId, data) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces/${workspaceId}/favorites`, data, 'post')

// 删除收藏菜单
const RemoveFavorite = (data) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces/${data.workspaceId}/favorites/${data.applicationId}`, data, 'post')

export {
  GetDicSecondList,
  GetAreaMap,
  GetBaseInfo,
  GetWorkspaceUserManagement,
  GetWorkspaceUserList,
  GetWorkspaceData,
  GetWorkspaceApplications,
  GetDepartments,
  CheckWorkspaceNameExist,
  GetDicList,
  GetWorkspaceList,
  GetWorkspaceBaseInfo,
  GetFavorites,
  AddFavorite,
  RemoveFavorite
}