import { ref, type ComputedRef } from 'vue';
import api from '../api';
import { useDateFormat } from '@vueuse/core';
import { request } from '@dataspherestudio/shared';
import { FMessage } from '@fesjs/fes-design';
const maxPageSize = 10000;

type ObjectType = Record<string, unknown>;

export const useDataList = (workspaceId?: ComputedRef) => {
  const bindApplications = ref([]); // 关联应用
  const engineTypes = ref([]); // 引擎类型
  const engineNames = ref([]); // 引擎名
  const templateNames = ref([]); // 模板名称
  const ruleTypes = ref([
    { value: '1', label: '工作空间新用户规则' },
    { value: '0', label: '临时规则' },
  ]); // 规则类型
  const overlayAreas = ref([
    { value: '0', label: '全部用户' },
    { value: '1', label: '指定用户' },
    { value: '2', label: '工作空间新用户' },
  ]); // 覆盖范围

  // 加载关联应用
  async function loadBindApplications() {
    const res = await request.fetch(api.getApplicationList, {});
    bindApplications.value = (res.data?.applications || []).map(
      (item: string) => ({
        valueField: item,
        labelField: item,
      })
    );
  }

  // 加载引擎类型
  async function loadEngineTypes(application?: string) {
    const res = await request.fetch(api.getEngineTypeList, { application });
    engineTypes.value = (res.data?.engineTypes || []).map((item: string) => ({
      valueField: item,
      labelField: item,
    }));
  }

  // 加载引擎名
  async function loadEngineNames(application?: string) {
    if (!application) {
      FMessage.warn('请先选择关联应用');
      return;
    }
    const res = await request.fetch(api.getEngineNameList, { application });
    engineNames.value = (res.data?.engineTypes || []).map((item: string) => ({
      valueField: item,
      labelField: item,
    }));
  }

  // 加载模板名称
  async function loadTemplateNames(engineName?: string) {
    if (!engineName) {
      FMessage.warn('请先选择引擎类型');
      return;
    }
    const param = {
      workspaceId: workspaceId?.value,
      engineName,
      pageNow: 1,
      pageSize: maxPageSize,
    };
    const res = await request.fetch(api.getConfTemplateList, param);
    templateNames.value = (res.data?.templateList || []).map(
      (item: ObjectType) => ({
        valueField: item.templateId,
        labelField: item.name,
      })
    );
  }

  // 函数映射关系
  const loadMap = {
    bind_application: loadBindApplications,
    engine_type: loadEngineTypes,
    engine_name: loadEngineNames,
    template_name: loadTemplateNames,
  };

  function handleSelect(type: keyof typeof loadMap, data?: string): void {
    const handler = loadMap[type];
    handler(data);
  }

  // 过滤出有值的对象
  function jsonFilter(obj: ObjectType): ObjectType {
    if (obj.toString().toLowerCase() !== '[object object]') {
      return {};
    }
    const result: ObjectType = {};
    Object.keys(obj).forEach((key: string) => {
      if (obj[key]) {
        result[key as keyof typeof obj] = obj[key];
      }
    });
    return result;
  }

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
    bindApplications, // 关联应用
    engineTypes, // 引擎类型
    engineNames, // 引擎名
    templateNames, // 模板名称
    ruleTypes, // 规则类型
    overlayAreas, // 覆盖范围
    handleSelect,
    jsonFilter,
    fillText,
    fillTimeText,
  };
};
