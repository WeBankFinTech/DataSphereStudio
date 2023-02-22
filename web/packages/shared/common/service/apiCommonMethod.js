import api from "./api.js"
import API_PATH from '@dataspherestudio/shared/common/config/apiPath.js'

// 获取工作流模式选项卡
const GetDicSecondList = (params) => {
  return api.fetch(`${API_PATH.WORKSPACE_PATH}getDicSecondList`, {
    parentKey: "p_orchestrator_mode",
    workspaceId: params
  })
}
// 获取工作空间用户管理相关数据
const GetWorkspaceUserManagement = (params) => api.fetch(`${API_PATH.WORKSPACE_PATH}getWorkspaceUsers`, params, {method: 'get', cacheOptions: {time: 3000}})

// 获取工作空间用户的列表
const GetWorkspaceUserList = (params) => api.fetch(`${API_PATH.WORKSPACE_PATH}getAllWorkspaceUsers`, params, 'get')

// 获取工作空间数据
const GetWorkspaceData = (params) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces/${params}`, 'get')

// 获取工作空间应用商店数据
const GetWorkspaceApplications = (params) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces/${params}/appconns`, {}, {method: 'get', cacheOptions: {time: 3000}})

// 获取工作空间归属部门数据
const GetDepartments = () => api.fetch(`${API_PATH.WORKSPACE_FRAMEWORK_PATH}admin/dept/list`, 'get')

// 获取工作空间树形归属部门数据
const GetTreeDepartments = () => api.fetch(`${API_PATH.WORKSPACE_FRAMEWORK_PATH}admin/dept/treeselect`, 'get')

// GetTreeDepartments
const getAllDepartments = () => api.fetch(`${API_PATH.WORKSPACE_FRAMEWORK_PATH}workspace/listAllDepartments`, 'get')

// 判断工作空间是否重复
const CheckWorkspaceNameExist = (params) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces/exists`, params, 'get')

// 获取流程数据字典数据
const GetDicList = (params) => api.fetch(`${API_PATH.WORKSPACE_PATH}getDicList`, params, 'post')

// 获取工作空间列表或修改
const GetWorkspaceList = (params, method) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces`, params, method)

// 获取工程的应用领域
const GetAreaMap = () =>  api.fetch(`${API_PATH.PROJECT_PATH}listApplicationAreas`, "get")

// 获取常用功能列表
const GetFavorites = (workspaceId) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces/${workspaceId}/favorites`, workspaceId, 'get')

// 获奖 钉一钉 功能列表
const GetCollections = (workspaceId) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces/${workspaceId}/favorites`, {workspaceId, type: 'dingyiding'}, 'get')

// 添加常用应用
const AddFavorite = (workspaceId, data) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces/${workspaceId}/favorites`, data, 'post')

// 删除收藏菜单
const RemoveFavorite = (data) => api.fetch(`${API_PATH.WORKSPACE_PATH}workspaces/${data.workspaceId}/favorites/${data.applicationId}`, data, 'post')

// 检查项目重名
const CheckProjectNameRepeat = (name) => api.fetch(`${API_PATH.PROJECT_PATH}checkProjectName`, {name}, 'get')

export {
  GetDicSecondList,
  GetAreaMap,
  GetWorkspaceUserManagement,
  GetWorkspaceUserList,
  GetWorkspaceData,
  GetWorkspaceApplications,
  GetDepartments,
  GetTreeDepartments,
  getAllDepartments,
  CheckWorkspaceNameExist,
  GetDicList,
  GetWorkspaceList,
  GetFavorites,
  AddFavorite,
  RemoveFavorite,
  GetCollections,
  CheckProjectNameRepeat
}
