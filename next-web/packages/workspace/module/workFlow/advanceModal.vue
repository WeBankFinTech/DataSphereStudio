<template>
  <FModal
    v-model:show="showModal"
    title="高级筛选"
    display-directive="if"
    ok-text="确定"
    :mask-closable="false"
    @ok="handleOk"
  >
    <FForm ref="filterForm" :label-width="90" label-position="right">
      <FFormItem label="节点名称">
        <FSelect
          v-model="formData.nodeName"
          :options="allNodeList"
          placeholder="请选择"
          filterable
          clearable
          value-field="title"
          label-field="title"
        />
      </FFormItem>
      <FFormItem label="节点类型">
        <FSelect
          v-model="formData.nodeType"
          :options="nodeTypes"
          placeholder="请选择"
          filterable
          clearable
          multiple
          collapse-tags
          :collapse-tags-limit="2"
          value-field="value"
          label-field="label"
        />
      </FFormItem>
      <FFormItem label="资源参数模板">
        <FSelectCascader
          v-model="formData.templateId"
          class="select-cascader"
          placeholder="请选择"
          :data="templateData"
          clearable
          remote
          :emit-path="false"
          expand-trigger="click"
          :show-path="false"
          :load-data="loadTemplates"
        ></FSelectCascader>
      </FFormItem>
      <FFormItem label="更新人">
        <FSelect
          v-model="formData.modifyUser"
          :options="userList"
          placeholder="请选择"
          filterable
          clearable
          value-field="value"
          label-field="label"
        />
      </FFormItem>
      <FFormItem label="修改时间">
        <FDatePicker
          v-model="formData.updateTimes"
          type="datetimerange"
          format="yyyy/MM/dd HH:mm:ss"
          clearable
        />
      </FFormItem>
    </FForm>
  </FModal>
</template>

<script setup>
import { ref, computed, defineProps, defineEmits, watch, inject } from 'vue';
// import { useI18n } from '@fesjs/fes';
import { cloneDeep } from 'lodash-es';
import { useTemplateList } from './hooks/useTemplateList';

const emits = defineEmits(['success', 'update:show']);
const props = defineProps({
  show: {
    type: Boolean,
    default: false,
  },
  form: {
    type: Object,
    default: () => ({}),
  },
});

// 默认筛选参数
const formData = ref({
  nodeName: '',
  nodeType: [],
  modifyUser: '',
  templateId: '',
  updateTimes: [],
});

// 控制弹窗显示
const showModal = computed({
  get: () => {
    return props.show;
  },
  set: (val) => {
    emits('update:show', val);
  },
});

watch(
  () => showModal.value,
  (show) => {
    if (show) {
      formData.value = cloneDeep(props.form);
    }
  }
);

/**
 * @description: 确认筛选
 * @return {*}
 */
const handleOk = () => {
  showModal.value = false;
  emits('success', formData.value);
};

const nodeTypes = inject('nodeTypes');
const templateList = inject('templateList');
const userList = inject('userList');
const allNodeList = inject('allNodeList');

const { templateData, createData, loadTemplates } =
  useTemplateList(templateList);

watch(
  () => templateList.value,
  (list) => {
    const arr = Array.from([
      ...new Set(list.map((item) => item.engineType)),
    ]).map((v) => ({ value: v, label: v }));
    templateData.value = createData(arr);
  },
  { immediate: true, deep: true }
);
</script>

<style lang="less" scoped></style>
