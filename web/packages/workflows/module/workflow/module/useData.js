import api from '@dataspherestudio/shared/common/service/api';

export const useData = () => {
  const apiPre = '/dss/framework/orchestrator/'

  const getTemplateByOrchestratorId = async (params) => {
    let templateObjectArray = []
    let templateIdArray = []
    await api.fetch(`${apiPre}getWrokflowDefaultTemplates`, params, 'get').then(res => {
      templateObjectArray = res.wrokflowDefaultTemplates || []
    })
    templateIdArray = templateObjectArray.map((item) => {
      return item.templateId
    })
    return templateIdArray
  }

  const getTemplateDatas = async (params) => {
    console.log('getTemplateDatas-params', params)
    let originTemplates = []
    await api.fetch(`${apiPre}getProjectTemplates`, params, 'get').then(res => {
      originTemplates = res.templates || []
    })
    originTemplates.forEach(e => {
      e.child.forEach(subE => {
        subE.disabled = false
        subE.enginType = e.enginType
      })
    })
    return originTemplates
  }
  return {
    getTemplateDatas,
    getTemplateByOrchestratorId,
  }
}