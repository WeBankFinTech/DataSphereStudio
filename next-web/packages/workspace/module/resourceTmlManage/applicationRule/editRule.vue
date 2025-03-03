<template>
  <FForm
    ref="formRef"
    :label-width="100"
    label-position="top"
    :model="formData"
    :rules="formRules"
  >
    <FFormItem label="规则类型" prop="ruleType">
      <FSelect
        v-model="formData.ruleType"
        :options="ruleTypes"
        placeholder="请选择规则类型"
        filterable
        clearable
        value-field="value"
        label-field="label"
        @change="handleChange('rule_type')"
      />
    </FFormItem>
    <FFormItem label="关联应用" prop="application">
      <FSelect
        v-model="formData.application"
        :options="bindApplications"
        placeholder="请选择关联应用"
        filterable
        clearable
        value-field="valueField"
        label-field="labelField"
        @focus="handleSelect('bind_application')"
        @change="handleChange('bind_application')"
      />
    </FFormItem>
    <FFormItem label="引擎类型" prop="engineName">
      <FSelect
        v-model="formData.engineName"
        :options="engineNames"
        placeholder="请选择引擎类型"
        filterable
        clearable
        value-field="valueField"
        label-field="labelField"
        @focus="handleSelect('engine_name', formData.application)"
        @change="handleChange('engine_name')"
      />
    </FFormItem>
    <FFormItem label="模板名称" prop="templateId">
      <FSelect
        v-model="formData.templateId"
        :options="templateNames"
        placeholder="请选择模板名称"
        filterable
        clearable
        value-field="valueField"
        label-field="labelField"
        @focus="handleSelect('template_name', formData.engineName)"
      />
    </FFormItem>
    <FFormItem label="覆盖范围" prop="permissionType">
      <FSelect
        v-model="formData.permissionType"
        :options="overlayList"
        placeholder="请选择覆盖范围"
        filterable
        clearable
        value-field="value"
        label-field="label"
        :disabled="+formData.ruleType === 1"
        @change="handleChange('permission_type')"
      />
    </FFormItem>
    <FFormItem
      v-if="+formData.permissionType === 1"
      ref="permissionUsersRef"
      label="覆盖用户"
      prop="permissionUsers"
    >
      <FSelectCascader
        v-model="formData.permissionUsers"
        :data="cascadUserList"
        placeholder="请选择覆盖用户"
        multiple
        :cascade="true"
        check-strictly="child"
        clearable
        expand-trigger="click"
        :show-path="false"
      />
      <UploadOutlined class="upload" :size="20" @click="openBatchUpload" />
    </FFormItem>
    <FFormItem
      v-if="+formData.permissionType === 2"
      label="覆盖部门"
      prop="permissionDepartments"
    >
      <FSelect
        v-model="formData.permissionDepartments"
        :options="allDeptList"
        placeholder="默认为全部，若指定，则只有指定部门的工作空间新用户，才会下发该规则"
        filterable
        clearable
        multiple
        collapse-tags
        :collapse-tags-limit="2"
      />
    </FFormItem>
  </FForm>
  <FModal
    v-model:show="showBatchUpload"
    title="批量导入用户"
    :mask-closable="false"
    display-directive="if"
    @ok="handleOkUpload"
    @cancel="handleCancelUpload"
  >
    <FInput
      v-model="batchUploadForm.batchUsers"
      type="textarea"
      placeholder="请输入用户名，例如: enjoyyin,owewnxu,leebai"
    />
  </FModal>
</template>
<script lang="ts" setup>
import { UploadOutlined } from '@fesjs/fes-design/icon';
import { ref, computed, onMounted } from 'vue';
import { useDataList } from './hooks/useDataList';
import { FForm, FMessage, FSelectCascader } from '@fesjs/fes-design';
import { request } from '@dataspherestudio/shared';
import { useDataList as useOtherDataList } from '../hooks/useDataList';
import api from './api';

const props = defineProps({
  workspaceId: {
    type: String,
    required: true,
    default: '',
  },
});
const workspaceId = computed(() => props.workspaceId);
const permissionUsersRef = ref(null);
const {
  bindApplications, // 关联应用
  engineNames, // 引擎类型
  templateNames, // 模板名称
  ruleTypes, // 规则类型
  overlayAreas, // 覆盖范围,
  handleSelect,
} = useDataList(workspaceId);

const {
  allDeptList,
  loadAllDeptList,
  allWorkSpaceUserDeptsList,
  loadAllWorkSpaceUserDeptsList,
} = useOtherDataList();

interface FormDataType {
  ruleType: string;
  templateId: string;
  engineName: string;
  permissionType: string | number;
  permissionUsers?: string[];
  application: string;
  permissionDepartments?: string[];
}

const init = (): FormDataType => ({
  ruleType: '', // 规则类型
  templateId: '', // 模板id
  engineName: '', // 引擎类型
  permissionType: '', // 覆盖范围
  permissionUsers: [], // 覆盖用户
  application: '', // 应用类型
  permissionDepartments: [], // 覆盖部门 只有工作空间新用户会选择
});
const formData = ref<FormDataType>(init());

onMounted(() => {
  formData.value = init();
});

const formRules = computed(() => ({
  ruleType: [
    {
      required: true,
      message: '请选择',
      trigger: ['change', 'blur'],
    },
  ],
  templateId: [
    {
      required: true,
      message: '请选择',
      trigger: ['change', 'blur'],
    },
  ],
  engineName: [
    {
      required: true,
      message: '请选择',
      trigger: ['change', 'blur'],
    },
  ],
  application: [
    {
      required: true,
      message: '请选择',
      trigger: ['change', 'blur'],
    },
  ],
  permissionType: [
    {
      required: true,
      message: '请选择',
      trigger: ['change', 'blur'],
    },
  ],
  permissionUsers: [
    {
      required: true,
      type: 'array' as const,
      message: '请选择',
      trigger: ['change', 'blur'],
    },
  ],
}));

// 自身校验
const formRef = ref<InstanceType<typeof FForm> | null>(null);
async function submit() {
  await formRef.value?.validate();
  const param: { [index: string]: any } = {
    ruleType: formData.value.ruleType,
    templateId: formData.value.templateId,
    engineName: formData.value.engineName,
    permissionType: +formData.value.permissionType,
    application: formData.value.application,
  };
  if (param.permissionType === 1) {
    param.permissionUsers = formData.value.permissionUsers || [];
  }
  if (formData.value.permissionDepartments.length > 0) {
    param.permissionType = 3;
    param.permissionDepartments = formData.value.permissionDepartments || [];
  }
  await request.fetch(api.saveConfTemplateApplyRule, param, 'put');
  FMessage.success('新建规则成功!');
}

// 覆盖范围
const overlayList = ref<Record<string, unknown>[]>([]);
function handleChange(type: string) {
  switch (type) {
    case 'rule_type':
      if (+formData.value.ruleType === 1) {
        formData.value.permissionType = '2';
        formData.value.permissionUsers = [];
        formData.value.permissionDepartments = [];
        overlayList.value = overlayAreas.value.filter(
          (item) => item.value === '2'
        );
        formRef.value?.clearValidate();
      } else {
        formData.value.permissionType = '';
        formData.value.permissionUsers = [];
        formData.value.permissionDepartments = [];
        overlayList.value = overlayAreas.value.filter(
          (item) => item.value !== '2'
        );
      }
      break;
    case 'bind_application':
      engineNames.value = [];
      formData.value.engineName = '';
      templateNames.value = [];
      formData.value.templateId = '';
      break;
    case 'engine_name':
      templateNames.value = [];
      formData.value.templateId = '';
      break;
    case 'permission_type':
      formData.value.permissionUsers = [];
      break;
    default:
      break;
  }
}
const batchUploadForm = ref({
  batchUsers: '',
  batchUserArray: [],
});
const showBatchUpload = ref(false);
const openBatchUpload = () => {
  showBatchUpload.value = true;
  batchUploadForm.value.batchUsers = '';
};
const handleCancelUpload = () => {
  showBatchUpload.value = false;
};
const handleOkUpload = () => {
  batchUploadForm.value.batchUserArray =
    batchUploadForm.value.batchUsers.split(/[,，]/);
  const tempInsertData = batchUploadForm.value.batchUserArray.filter(
    (element) =>
      allWorkSpaceUserDeptsList.value.find((obj) => obj.name === element)
  );
  tempInsertData.forEach((element) => {
    if (!formData.value.permissionUsers.includes(element)) {
      formData.value.permissionUsers.push(element);
    }
  });
  showBatchUpload.value = false;
  if (formData.value.permissionUsers.length > 0) {
    permissionUsersRef.value.clearValidate();
  }
};

const cascadUserList = ref([]);
interface User {
  department: string;
  name: string;
  office: number;
}

interface CascadeData {
  label: string;
  value: string;
  children: CascadeData[];
}
function convertUserDataToCascadeData(userData: User[]): CascadeData[] {
  const cascadeData: CascadeData[] = [];

  for (const user of userData) {
    const department = user.department;
    const name = user.name;

    // 检查是否已存在对应的部门对象
    const departmentObj = cascadeData.find((obj) => obj.label === department);

    if (departmentObj) {
      // 部门对象已存在，将当前用户对象添加到部门对象的子数组中
      departmentObj.children.push({
        label: name,
        value: name,
        children: [],
      });
    } else {
      // 部门对象不存在，创建新的部门对象，并将当前用户对象添加到部门对象的子数组中
      const newDepartmentObj: CascadeData = {
        label: department,
        value: department,
        children: [
          {
            label: name,
            value: name,
            children: [],
          },
        ],
      };
      cascadeData.push(newDepartmentObj);
    }
  }

  return cascadeData;
}

onMounted(async () => {
  await loadAllDeptList();
  await loadAllWorkSpaceUserDeptsList(workspaceId.value as string);
  cascadUserList.value = convertUserDataToCascadeData(
    allWorkSpaceUserDeptsList.value
  );
});

defineExpose({ submit });
</script>
<style lang="less" scoped>
.upload {
  margin-left: 8px;
}
</style>
