import api from '@dataspherestudio/shared/common/service/api';

export const useData = () => {
  const apiPre = '/dss/framework/orchestrator/'

  // 通过模板id获取模板详情
  const getTemplateDescById = async (params) => {
    console.log('getTemplateDescById-params', params)
    let templateDesc = []
    await api.fetch('dss/framework/workspace/engineconf/getConfTemplateParamDetail', params, 'get').then(res => {
      templateDesc = res || []
    })
    return templateDesc
  };

  const getTemplateDatas = async (params) => {
    console.log('getTemplateDatas-params', params)
    let originTemplates = []
    await api.fetch(`${apiPre}getProjectTemplates`, params, 'get').then(res => {
      originTemplates = res.templates || []
    })
    return originTemplates
  }
  return {
    getTemplateDatas,
    getTemplateDescById,
  }
}