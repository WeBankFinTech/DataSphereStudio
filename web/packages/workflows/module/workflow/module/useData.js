import api from '@dataspherestudio/shared/common/service/api';

export const useData = () => {
  const apiPre = '/dss/framework/orchestrator/'

  const getTemplateByOrchestratorId = async (params) => {
    let templateObjectArray = []
    let templateIdArray = []
    if(!params.orchestratorId) {
      return [];
    }

    templateIdArray = templateObjectArray.map((item) => {
      return item.templateId
    })
    return templateIdArray
  }

  const getTemplateDatas = async (params) => {
    console.log('getTemplateDatas-params', params)
    let originTemplates = []
    return originTemplates
  }
  return {
    getTemplateDatas,
    getTemplateByOrchestratorId,
  }
}
