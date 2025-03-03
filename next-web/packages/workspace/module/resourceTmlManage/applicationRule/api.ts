export default {
  // 公共接口
  getApplicationList: 'dss/framework/workspace/engineconf/getApplicationList', // 加载关联应用
  getEngineTypeList: 'dss/framework/workspace/engineconf/getEngineTypeList', // 加载引擎类型
  getEngineNameList: 'dss/framework/workspace/engineconf/getEngineNameList', // 加载引擎名
  getConfTemplateList: 'dss/framework/workspace/engineconf/getConfTemplateList', // 加载模板名称
  getConfTemplateUserList:
    'dss/framework/workspace/engineconf/getConfTemplateApplyRuleUserList', // 加载覆盖用户
  // 获取应用规则接口
  getConfTemplateApplyRuleList:
    'dss/framework/workspace/engineconf/getConfTemplateApplyRuleList', // 查询模板应用规则
  saveConfTemplateApplyRule:
    'dss/framework/workspace/engineconf/saveConfTemplateApplyRule', // 新增模板应用规则
  deleteConfTemplateApplyRule:
    'dss/framework/workspace/engineconf/deleteConfTemplateApplyRule', // 删除模板应用规则
  getConfTemplateApplyHistory:
    'dss/framework/workspace/engineconf/getConfTemplateApplyHistory', // 查询模板应用执行记录
};
