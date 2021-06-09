import API_PATH from '@/common/config/apiPath.js';
import api from '@/common/service/api';

/**
 * 
 * @param {任务id} releaseTaskId 
 * @param {dss标签} dssLabel 
 * @returns 
 */
const getPublishStatus = (releaseTaskId, dssLabel)=>{
  return api.fetch(`${API_PATH.PUBLISH_PATH}getPublishStatus`, {releaseTaskId,  dssLabel}, 'get');
}

const getSchedulingStatus = (workspaceId, orchestratorId)=>{
  return api.fetch(`${API_PATH.PUBLISH_PATH}getSchedulerWorkflowStatus`, {orchestratorId, workspaceId}, 'get');
}


export {
  getPublishStatus,
  getSchedulingStatus
}