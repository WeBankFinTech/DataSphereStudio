import { ref } from 'vue';
import {
  fetchTemplateUser,
  fetchRelateUsers,
  fetchRelateUsersDepts,
  fetchEngineTypes,
  fetchworkFlowProjectNames,
  fetchworkFlowNames,
  fetchDepartments,
} from '../api';
import { utils } from '../hooks/utils';
import { useDateFormat } from '@vueuse/core';

export const useDataList = () => {
  const templateUserList = ref([]);
  const allWorkSpaceUserList = ref([]);
  const allWorkSpaceUserDeptsList = ref([]);
  const allDeptList = ref([]);
  const engineList = ref([]);
  const projectNameList = ref([]);
  const workflowNameList = ref([]);
  const permissionTypeList = ref([
    {
      value: 0,
      label: '全部用户',
    },
    {
      value: 1,
      label: '指定用户',
    },
  ]);
  const { paramsObjectToString } = utils();

  const loadworkFlowNames = async (templateId: string) => {
    try {
      const data = await fetchworkFlowNames(templateId);
      workflowNameList.value = data?.orchestratorNames.map((v: string) => ({
        value: v,
        label: v,
      }));
    } catch (error) {
      console.log(error);
    }
  };

  const loadworkFlowProjectNames = async (templateId: string) => {
    try {
      const data = await fetchworkFlowProjectNames(templateId);
      projectNameList.value = data?.projectNames.map((v: string) => ({
        value: v,
        label: v,
      }));
    } catch (error) {
      console.log(error);
    }
  };

  const loadEngineList = async () => {
    try {
      const data = await fetchEngineTypes();
      engineList.value = data?.engineTypes.map((v: string) => ({
        value: v,
        label: v,
      }));
    } catch (error) {
      console.log(error);
    }
  };

  const loadAllWorkSpaceUserList = async (workspaceId: string) => {
    try {
      const data = await fetchRelateUsers(workspaceId);
      allWorkSpaceUserList.value = data?.users.accessUsers.map((v: string) => ({
        value: v,
        label: v,
      }));
    } catch (error) {
      console.log(error);
    }
  };

  const loadAllWorkSpaceUserDeptsList = async (workspaceId: string) => {
    try {
      const data = await fetchRelateUsersDepts(workspaceId);
      if (data && data.users && data.users.accessUsers) {
        allWorkSpaceUserDeptsList.value = data.users.accessUsers.map(
          (v: any) => v
        );
      } else {
        allWorkSpaceUserDeptsList.value = [];
      }
    } catch (error) {
      console.log(error);
    }
  };

  const loadAllDeptList = async () => {
    try {
      const data = await fetchDepartments();
      allDeptList.value = data?.departments.map((v: string) => ({
        value: v,
        label: v,
      }));
    } catch (error) {
      console.log(error);
    }
  };

  const loadTemplateUserList = async (templateId: string, name: string) => {
    const params = {
      pageNow: 1,
      pageSize: 50,
      templateId: templateId,
      username: name,
    };
    const strParams = paramsObjectToString(params);
    try {
      const res = await fetchTemplateUser(strParams);
      templateUserList.value = res.data.users.map((v: { name: string }) => ({
        value: v.name,
        label: v.name,
      }));
    } catch (error) {
      console.log(error);
    }
  };
  // 表格内容filter
  const fillText = (row: ObjectType) =>
    ['null', 'undefined', ''].includes(String(row.cellValue))
      ? '- -'
      : row.cellValue;

  // 表格内容filter
  const fillTimeText = (row: ObjectType) =>
    ['null', 'undefined', ''].includes(String(row.cellValue))
      ? '- -'
      : useDateFormat(row.cellValue as string, 'YYYY-MM-DD HH:mm:ss').value;

  return {
    templateUserList,
    allWorkSpaceUserList,
    engineList,
    permissionTypeList,
    projectNameList,
    workflowNameList,
    allDeptList,
    allWorkSpaceUserDeptsList,
    loadAllWorkSpaceUserDeptsList,
    loadAllDeptList,
    loadTemplateUserList,
    loadAllWorkSpaceUserList,
    loadEngineList,
    loadworkFlowProjectNames,
    loadworkFlowNames,
    fillText,
    fillTimeText,
  };
};
