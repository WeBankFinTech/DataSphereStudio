const DEVPROCESS = {
  DEVELOPMENTCENTER: 'dev',// 开发中心
  PRODUCTCENTER: 'prod',// 生产中心
  OPERATIONCENTER: 'scheduler',//运维中心
  IFRAME: 'iframe'
}

const ORCHESTRATORMODES = {
  WORKFLOW: 'pom_work_flow',// 工作流
  SINGLETASK: 'pom_single_task',// 单任务工作流
  CONSTSTORCHESTRATOR: 'pom_consist_orchestrator'// 组合工作流
}

// 脚本执行终态
const EXECUTE_COMPLETE_TYPE = ['Succeed', 'Failed', 'Cancelled', 'Timeout'];

export { DEVPROCESS, ORCHESTRATORMODES, EXECUTE_COMPLETE_TYPE }

