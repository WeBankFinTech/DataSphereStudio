<template>
  <FDrawer
    v-model:show="drawerShow"
    :title="`${t('workSpace.workFlowRefList')}(${templateObj.name})`"
    :mask-closable="false"
    display-directive="if"
    width="50%"
    @cancel="closeDrawer"
  >
    <BTablePage
      :is-loading="isLoading"
      :action-type="actionType"
      layout="drawer"
      :is-divider="false"
    >
      <template #search>
        <BSearch
          v-model:form="searchForm"
          @search="getWorkFlowList"
          @reset="getWorkFlowList"
        >
          <template #form>
            <FSelect
              v-model="searchForm.projectName"
              :placeholder="t('workSpace.projectName')"
              :options="projectNameList"
              filterable
              clearable
            >
            </FSelect>
            <FSelect
              v-model="searchForm.orchestratorName"
              :placeholder="t('workSpace.workFlowName')"
              :options="workflowNameList"
              filterable
              clearable
            >
            </FSelect>
          </template>
        </BSearch>
      </template>
      <template #table>
        <f-table :data="workFlowList" class="table-container">
          <f-table-column
            prop="projectName"
            :label="t('workSpace.projectName')"
            :width="360"
            ellipsis
          />
          <f-table-column
            prop="orchestratorName"
            :label="t('workSpace.workFlowName')"
            :width="360"
            ellipsis
          >
            <template #default="{ row = {} }">
              <span
                class="a-link"
                href="javascript:void(0);"
                @click="jumpToWorkFlowDec(row)"
                >{{ row.orchestratorName }}</span
              >
            </template>
          </f-table-column>
        </f-table>
      </template>
      <template #pagination>
        <FPagination
          v-model:currentPage="pagination.current"
          v-model:pageSize="pagination.size"
          show-size-changer
          show-total
          :total-count="pagination.total"
          @change="getWorkFlowList"
          @page-size-change="getWorkFlowList"
        />
      </template>
    </BTablePage>
  </FDrawer>
</template>
<script lang="ts" setup>
import { onMounted, computed, ref, reactive, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { fetchWorkFlows } from '../api';
import { useDataList } from '../hooks/useDataList';
import { utils } from '../hooks/utils';

const { t } = useI18n();
const isLoading = ref(false);
const actionType = ref('loading');
type workFlowInfo = {
  id: number;
  projectId: string;
  projectName: string;
  orchestratorId: number;
  orchestratorName: string;
  templateId: string;
};
const workFlowList: Ref<workFlowInfo[]> = ref([]);
const searchForm = reactive({
  projectName: '',
  orchestratorName: '',
});
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
});
const {
  projectNameList,
  workflowNameList,
  loadworkFlowProjectNames,
  loadworkFlowNames,
} = useDataList();
const props = defineProps({
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
const { paramsObjectToString } = utils();
const emit = defineEmits(['update:show']);
const drawerShow = computed({
  get: () => props.show,
  set: (value) => {
    emit('update:show', value);
  },
});
const closeDrawer = () => {
  emit('update:show', false);
};
const getWorkFlowList = async () => {
  const params = {
    pageNow: pagination.current,
    pageSize: pagination.size,
    templateId: props.templateObj.templateId,
    projectName: searchForm.projectName,
    orchestratorName: searchForm.orchestratorName,
  };
  const strParams = paramsObjectToString(params);
  try {
    isLoading.value = true;
    actionType.value = 'loading';
    const res = await fetchWorkFlows(strParams);
    workFlowList.value = res.data.pageInfo;
    pagination.total = res.data.total;
    if (pagination.total === 0) {
      actionType.value = 'emptyQueryResult';
    }
    isLoading.value = false;
  } catch (error) {
    console.log(error);
    isLoading.value = false;
  }
};

const jumpToWorkFlowDec = (row: any) => {
  console.log('jumpToWorkFlowDec', row);
  const baseUrl = window.location.href.split('next-web')[0] + '#/workflow?';
  const params =
    'workspaceId=' +
    props.workPlaceId +
    '&projectID=' +
    row.projectId +
    '&projectName=' +
    row.projectName +
    '&flowId=' +
    row.orchestratorId;
  const url = baseUrl + params;
  window.open(url);
};
onMounted(async () => {
  await getWorkFlowList();
  await loadworkFlowProjectNames(props.templateObj.templateId);
  await loadworkFlowNames(props.templateObj.templateId);
});
</script>
