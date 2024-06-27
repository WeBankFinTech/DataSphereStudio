<template>
  <FDrawer
    v-model:show="drawerShow"
    :title="
      mode === 'add' ? t('workSpace.addTemplate') : t('workSpace.editTemplate')
    "
    :mask-closable="false"
    display-directive="if"
    width="50%"
    :footer="true"
    ok-text="保存"
    content-class="operate-template"
    @cancel="closeDrawer"
    @ok="saveTemplate"
  >
    <div class="title">基础信息</div>
    <FForm
      ref="templateFormRef"
      label-position="top"
      :model="templateForm"
      :rules="templateFormRules"
    >
      <FFormItem :label="t('workSpace.tmlName')" prop="name">
        <FInput
          v-model="templateForm.name"
          :placeholder="t('common.pleaseInput') + t('workSpace.tmlName')"
          :disabled="mode === 'edit'"
        />
      </FFormItem>
      <FFormItem :label="t('workSpace.engineType')" prop="engineType">
        <FSelect
          v-model="templateForm.engineType"
          :placeholder="t('common.pleaseSelect') + t('workSpace.engineType')"
          :options="engineList"
          clearable
          filterable
          :disabled="mode === 'edit'"
          @change="getResParamsDetails('engineType', $event)"
        >
        </FSelect>
      </FFormItem>
      <FFormItem :label="t('workSpace.visibility')" prop="permissionType">
        <FSelect
          v-model="templateForm.permissionType"
          :placeholder="t('common.pleaseSelect') + t('workSpace.visibility')"
          :options="permissionTypeList"
          clearable
          filterable
          @change="handlePermissionTypeChange"
        >
        </FSelect>
      </FFormItem>
      <FFormItem
        v-if="templateForm.permissionType === 1"
        :label="t('workSpace.visibleUser')"
        prop="permissionUsers"
      >
        <FSelect
          v-model="templateForm.permissionUsers"
          :placeholder="t('common.pleaseSelect') + t('workSpace.visibleUser')"
          :options="allWorkSpaceUserList"
          clearable
          filterable
          multiple
          :collapse-tags="true"
          :collapse-tags-limit="5"
        >
        </FSelect>
      </FFormItem>
      <FFormItem :label="t('workSpace.tmlDes')" prop="description">
        <FInput
          v-model="templateForm.description"
          type="textarea"
          :placeholder="t('common.pleaseFillIn') + t('workSpace.tmlDes')"
          :maxlength="128"
        />
      </FFormItem>
    </FForm>
    <div class="title">
      资源参数<span class="tip">(不同引擎类型对应不同属性参数)</span>
    </div>
    <FForm label-position="top" :model="templateForm.paramDetails">
      <FFormItem
        v-for="(curResParam, index) in curResParams"
        :key="curResParam.key"
        :label="
          (curResParam.name ? curResParam.name : '') +
          '[' +
          curResParam.key +
          ']'
        "
        style="margin-bottom: 0px"
        :class="curResParam.require ? 'rule-valid-style' : ''"
      >
        <FForm
          :ref="
            (el) => {
              if (el) resParamsRefs[index] = el;
            }
          "
          layout="inline"
          :inline-item-width="380"
          :model="templateForm.paramDetails[index]"
          class="inline-res-form"
          @submit.prevent
        >
          <FFormItem
            prop="configValue"
            :rules="[
              {
                required: curResParam.require,
                message: '参数默认值不能为空',
                type: 'string',
                trigger: ['blur', 'change'],
              },
              {
                validator: (rule: any, value :string) => {
                  return regValidate(value, curResParam);
                },
                trigger: ['blur', 'change'],
                message: curResParam.description,
              },
              {
                validator: (rule: any, value: string) => {
                  return compareConfigValue(
                    value,
                    curResParam,
                    templateForm.paramDetails[index]
                  );
                },
                trigger: ['blur', 'change'],
                message: '默认值与上限值需同时填写，且小于等于上限值',
              },
            ]"
          >
            <FInput
              v-if="curResParam.validateType !== 'OFT'"
              v-model="templateForm.paramDetails[index].configValue"
              :placeholder="
                curResParam.defaultValue
                  ? '默认值:' + curResParam.defaultValue
                  : '无默认值'
              "
              :title="curResParam.description"
            >
              <template #prepend>默认值</template>
            </FInput>
            <div v-else class="form-group">
              <div class="select-prepend">默认值</div>
              <FSelect
                v-model="templateForm.paramDetails[index].configValue"
                :options="computeOFT(curResParam.validateRange)"
                :placeholder="
                  curResParam.defaultValue
                    ? '默认值:' + curResParam.defaultValue
                    : '无默认值'
                "
                :title="curResParam.description"
              >
              </FSelect>
            </div>
          </FFormItem>
          <FFormItem
            v-if="
              curResParam.boundaryType === 2 || curResParam.boundaryType === 3
            "
            prop="maxValue"
            :rules="[
              { 
                required: curResParam.require,
                message: '参数上限值不能为空',
                type: 'string' 
              },
              {
                validator: (rule: any, value: string) => {
                  return regValidate(value, curResParam);
                },
                trigger: ['blur', 'change'],
                message: curResParam.description,
              },
            ]"
          >
            <FInput
              v-model="templateForm.paramDetails[index].maxValue"
              :placeholder="
                curResParam.defaultValue
                  ? '默认值:' + curResParam.defaultValue
                  : '无默认值'
              "
              :title="curResParam.description"
              @change="handleMaxValueChange(index)"
            >
              <template #prepend>上限值</template>
            </FInput>
          </FFormItem>
        </FForm>
      </FFormItem>
    </FForm>
  </FDrawer>
</template>
<script lang="ts" setup>
import { onMounted, computed, ref, reactive } from 'vue';
import { useI18n } from 'vue-i18n';
import { useDataList } from '../hooks/useDataList';
import { fetchResParams, fetchUniqueName, fetchSaveTemplate } from '../api';
import { FMessage, FForm } from '@fesjs/fes-design';
import { utils } from '../hooks/utils';
import { fetchTemplateUser } from '../api';
import { cloneDeep } from 'lodash-es';

type FormRefType = typeof FForm | null;
const resParamsRefs = ref<any[]>([]);
const templateFormRef = ref<FormRefType>(null);
type ParamDetail = {
  key: string;
  maxValue: string;
  configValue: string;
};
type TemplateFormType = {
  name: string;
  permissionType: number;
  engineType: string;
  description: string;
  paramDetails: ParamDetail[];
  templateId: string | null;
  permissionUsers?: string[] | null;
};

const { paramsObjectToString } = utils();
const templateForm = reactive<TemplateFormType>({
  name: '',
  templateId: '',
  permissionType: 0,
  engineType: '',
  description: '',
  permissionUsers: [],
  paramDetails: [],
});
const curResParams = ref<any[]>([]);
const {
  allWorkSpaceUserList,
  engineList,
  permissionTypeList,
  loadAllWorkSpaceUserList,
  loadEngineList,
} = useDataList();
const editInitUsers = ref<string[]>([]);
const props = defineProps({
  // add - 新增模板，edit - 编辑模板
  mode: {
    type: String,
    required: true,
  },
  show: {
    type: Boolean,
    required: true,
    default: false,
  },
  templateObj: {
    type: Object,
    required: true,
  },
  workPlaceId: {
    type: String,
    required: true,
  },
});
const { t } = useI18n();
const emit = defineEmits(['update:show', 'updateTemplateTable']);
const drawerShow = computed({
  get: () => props.show,
  set: (value) => {
    emit('update:show', value);
  },
});

// 输入上限值时，对默认值做校验
const handleMaxValueChange = (index: number) => {
  resParamsRefs.value[index].validate();
};
// 默认值与最大值的比较条件 默认值是输入框的形式且有上限值且上限值已填写
const compareConfigValue = (
  value: string,
  curResParam: any,
  paramDetail: ParamDetail
) => {
  if (!curResParam.require && !value && !paramDetail.maxValue) {
    return true;
  } else if (
    !curResParam.require &&
    curResParam.validateType !== 'OFT' &&
    (curResParam.boundaryType === 2 || curResParam.boundaryType === 3)
  ) {
    if (value && paramDetail.maxValue) {
      // 过滤单位提取数字比较，当数字0或-1表示无限大，不做比较
      const max = paramDetail.maxValue.match(/\d+(\.\d+)?/g);
      const vmatch = value.match(/\d+(\.\d+)?/g);
      if (max && vmatch) {
        const maxValue = Number(max[0]);
        const configValue = Number(vmatch[0]);
        if (maxValue === 0 || maxValue === -1 || configValue <= maxValue) {
          return true;
        }
      }
    }
    return false;
  }
  if (value && curResParam.validateType !== 'OFT' && paramDetail.maxValue) {
    // 过滤单位提取数字比较，当数字0或-1表示无限大，不做比较
    const max = paramDetail.maxValue.match(/\d+(\.\d+)?/g);
    const vmatch = value.match(/\d+(\.\d+)?/g);
    if (max && vmatch) {
      const maxValue = Number(max[0]);
      const configValue = Number(vmatch[0]);
      if (maxValue === 0 || maxValue === -1 || configValue <= maxValue) {
        return true;
      }
    }
    return false;
  }
  return true;
};
const regValidate = (value: string, curResParam: any) => {
  if (!curResParam.require && !value) {
    return true;
  }
  if (curResParam.validateType === 'Regex') {
    const reg = new RegExp(curResParam.validateRange);
    return reg.test(value);
  }
  if (curResParam.validateType === 'NumInterval') {
    const numReg = curResParam.validateRange
      .slice(1, curResParam.validateRange.length - 1)
      .split(',');
    if (
      Number(value) <= Number(numReg[1]) &&
      Number(value) >= Number(numReg[0])
    ) {
      return true;
    }
    return false;
  }
  return true;
};
const computeOFT = (v: string) => {
  const vArray = v.slice(2, v.length - 2).split('","');
  const optionsArray = vArray.map((v: string) => {
    if (v === '\\\\t') {
      v = '\\t';
    }
    return {
      label: v,
      value: v,
    };
  });
  return optionsArray || [];
};

const submitFormValid = async () => {
  try {
    const formValidArray = [
      ...resParamsRefs.value.map((item) => item?.validate()),
      templateFormRef.value?.validate(),
    ];
    const result = await Promise.all(formValidArray);
    console.log('表单验证成功: ', result);
    return true;
  } catch (error) {
    console.log('表单验证失败: ', error);
    return false;
  }
};

const saveTemplate = async () => {
  try {
    if (await submitFormValid()) {
      const paramsObject = cloneDeep(templateForm);
      // 只有ermissionType=1时，选择为指定用户时，才传permissionUsers
      if (paramsObject.permissionType === 0) {
        delete paramsObject.permissionUsers;
      }
      // 与编辑最初用户列表做比较，若无修改，permissionUsers传null
      else if (props.mode === 'edit') {
        const compare1 = editInitUsers.value.sort().join(',');
        const compare2 = paramsObject.permissionUsers?.sort().join(',');
        if (compare1 === compare2) {
          paramsObject.permissionUsers = null;
        }
      }
      // 新增操作，templateId为null
      if (props.mode === 'add') {
        paramsObject.templateId = null;
      }
      await fetchSaveTemplate(paramsObject);
      FMessage.success('保存成功!');
      emit('updateTemplateTable');
      closeDrawer();
    }
  } catch (err) {
    console.error(err);
  }
};
const closeDrawer = () => {
  emit('update:show', false);
};
const templateFormRules = computed(() => ({
  name: [
    { required: true, message: t('common.notEmpty') },
    {
      pattern: /^[\u4e00-\u9fa5a-z0-9_]{0,128}$/gi,
      message: '请输入中文、英文、数字、下划线,最长不超过128字符',
    },
    {
      trigger: ['blur'],
      validator: (
        rule: any,
        value: string,
        callback: (error?: string | Error) => void
      ) => {
        if (!value) {
          callback(t('common.notEmpty'));
        }
        if (props.mode === 'add') {
          fetchUniqueName(value).then((result: any) => {
            if (result.data.repeat) {
              callback('该模板名称已存在');
            } else {
              callback();
            }
          });
          return;
        }
        callback();
      },
    },
  ],
  engineType: [
    {
      required: true,
      message: t('common.notEmpty'),
      trigger: ['blur', 'change'],
    },
  ],
  permissionType: [
    {
      required: true,
      type: 'number' as const,
      message: t('common.notEmpty'),
      trigger: ['blur', 'change'],
    },
  ],
  permissionUsers: [
    {
      required: true,
      type: 'array' as const,
      message: t('common.notEmpty'),
      trigger: ['blur', 'change'],
    },
  ],
}));
// 获取参数ui的值
const getResParamsDetails = async (key: string, value: string) => {
  try {
    const params = key + '=' + value;
    const res = await fetchResParams(params);
    curResParams.value = res.conf;
    templateForm.paramDetails = [];
    curResParams.value.forEach((item) => {
      const obj = {
        key: item.key,
        maxValue: item.maxValue,
        configValue: item.configValue,
      };
      templateForm.paramDetails.push(obj);
    });
    resParamsRefs.value.map((item) => item?.clearValidate());
  } catch (err) {
    console.error(err);
  }
};

const handlePermissionTypeChange = async (v: number) => {
  if (allWorkSpaceUserList.value.length === 0 && v === 1) {
    // 工作空间ID看怎么拿
    await loadAllWorkSpaceUserList(props.workPlaceId);
  }
  templateForm.permissionUsers = [];
};

// 拉取用户数据
const getUserDetail = async () => {
  const params = {
    pageNow: 1,
    pageSize: 999999,
    templateId: templateForm.templateId,
  };
  const strParams = paramsObjectToString(params);
  try {
    const res = await fetchTemplateUser(strParams);
    templateForm.permissionUsers = res.data.users.map((v: { name: string }) => {
      return v.name;
    });
    editInitUsers.value = cloneDeep(templateForm.permissionUsers) as string[];
  } catch (error) {
    console.log(error);
  }
};

const editInitData = async () => {
  console.log('editInitData', props.templateObj);
  templateForm.templateId = props.templateObj.templateId;
  templateForm.name = props.templateObj.name;
  templateForm.engineType = props.templateObj.engineType;
  templateForm.permissionType = props.templateObj.permissionType;
  templateForm.description = props.templateObj.description;
  if (templateForm.permissionType === 1) {
    await loadAllWorkSpaceUserList(props.workPlaceId);
    await getUserDetail();
  }
  // 参数ui赋值
  await getResParamsDetails('templateId', templateForm.templateId as string);
};

onMounted(async () => {
  await loadEngineList();
  // 编辑时，根据打开的模板请求对应参数
  if (props.mode === 'edit') {
    await editInitData();
  }
});
</script>
<style lang="less" scoped>
.operate-template {
  .inline-res-form {
    margin-bottom: 8px;
    .fes-form-item {
      margin-bottom: 8px;
    }
  }
  .rule-valid-style {
    :deep(.fes-form-item-label::before) {
      display: inline-block;
      margin-top: 2px;
      margin-right: 4px;
      color: var(--f-danger-color);
      content: '*';
    }
  }
  .form-group {
    display: inline-table;
    width: 100%;
    line-height: normal;
    border-collapse: separate;
    border-spacing: 0;
    .select-prepend {
      position: relative;
      display: table-cell;
      box-sizing: border-box;
      width: 1px;
      padding: 0 20px;
      color: #93949c;
      white-space: nowrap;
      vertical-align: middle;
      background-color: #f8f8f8;
      border: 1px solid #cfd0d3;
      border-radius: 4px;
      border-right: 0;
      border-top-right-radius: 0;
      border-bottom-right-radius: 0;
    }
  }
  .title {
    font-weight: 550;
    color: #0f1222;
    margin-bottom: 16px;
    .tip {
      color: #93949b;
      font-weight: 400;
    }
  }
}
</style>
