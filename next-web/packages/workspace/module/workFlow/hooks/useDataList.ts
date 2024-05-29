import { ref } from 'vue';
import { request } from '@dataspherestudio/shared';
import api from '../api';
type ObjectType = Record<string, unknown>;

export const useDataList = () => {
  const nodeTypes = ref([]);
  const templateList = ref<ObjectType[]>([]);
  const userList = ref([]);

  // 获取节点类型
  async function getNodeTypes(param: ObjectType) {
    await request
      .fetch(api.getNodeTypes, { labels: param.labels })
      .then((res) => {
        nodeTypes.value = (res.data.nodeTypes || [])
          .map((item: ObjectType) => item.children)
          .flat()
          .map((item: ObjectType) => ({ label: item.title, value: item.type }));
      });
  }

  async function getConfTemplateList(param: ObjectType, pageNow = 1) {
    await request
      .fetch(api.getConfTemplateList, {
        workspaceId: param.workspaceId,
        pageNow,
        pageSize: 10000,
      })
      .then((res) => {
        const list = (res.data.templateList || []).map((item: ObjectType) => ({
          engineType: item.engineType,
          label: item.name,
          value: item.templateId,
        }));
        templateList.value = [...templateList.value, ...list];
        if (pageNow * 10000 < res.data.total) {
          getConfTemplateList(param, pageNow + 1);
        }
      });
  }

  // 获取用户
  function getUserList(param: ObjectType) {
    request
      .fetch(api.getAllWorkspaceUsers, {
        workspaceId: param.workspaceId,
      })
      .then((res) => {
        userList.value = res.data.users.accessUsers.map((item: ObjectType) => ({
          value: item,
          label: item,
        }));
      });
  }

  return {
    nodeTypes,
    templateList,
    userList,
    getNodeTypes,
    getConfTemplateList,
    getUserList,
  };
};
