import API_PATH from '@dataspherestudio/shared/common/config/apiPath.js';
import api from '@dataspherestudio/shared/common/service/api';

/**
 *
 * @param {任务id} releaseTaskId
 * @param {dss标签} dssLabel
 * @returns
 */
const getPublishStatus = (releaseTaskId, dssLabel)=>{
  // todo 开源行内接口不一样 PUBLISH_PATH
  return api.fetch(`${API_PATH.WORKFLOW_PATH}getReleaseStatus`, {releaseTaskId,  dssLabel}, 'get');
}

const getSchedulingStatus = (workspaceId, orchestratorId)=>{
  return api.fetch(`${API_PATH.PUBLISH_PATH}getSchedulerWorkflowStatus`, {orchestratorId, workspaceId}, 'get');
}


export {
  getPublishStatus,
  getSchedulingStatus
}
