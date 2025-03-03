<template>
  <FDrawer
    v-model:show="drawerShow"
    :title="`${t('workSpace.appRefTable')}(${templateObj.name})`"
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
          @search="getAppList"
          @reset="getAppList"
        >
          <template #form>
            <FSelect
              v-model="searchForm.username"
              :placeholder="t('workSpace.overwriteUser')"
              :options="allWorkSpaceUserList"
              filterable
              clearable
            >
            </FSelect>
          </template>
        </BSearch>
      </template>
      <template #table>
        <f-table :data="appList" class="table-container">
          <f-table-column
            prop="application"
            :label="t('workSpace.appName')"
            :width="120"
            ellipsis
          />
          <f-table-column
            prop="permissionUsers"
            :label="t('workSpace.overwriteUser')"
            :width="500"
            ellipsis
          >
            <template #default="{ row }">
              <FEllipsis v-if="row.permissionUsers">
                {{ row.permissionUsers }}
                <template #tooltip>
                  <div style="max-width: 500px; word-wrap: break-word">
                    {{ row.permissionUsers }}
                  </div>
                </template>
              </FEllipsis>
              <div v-else>--</div>
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
          @change="getAppList"
          @page-size-change="getAppList"
        />
      </template>
    </BTablePage>
  </FDrawer>
</template>
<script lang="ts" setup>
import { onMounted, computed, ref, reactive, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { fetchApps } from '../api';
import { useDataList } from '../hooks/useDataList';
import { utils } from '../hooks/utils';

const { t } = useI18n();
const isLoading = ref(false);
const actionType = ref('loading');
type ApplyInfo = {
  application: string;
  permissionUsers: string;
};
const appList: Ref<ApplyInfo[]> = ref([]);
const searchForm = reactive({
  username: '',
});
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
});
const { allWorkSpaceUserList, loadAllWorkSpaceUserList } = useDataList();
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
const getAppList = async () => {
  const params = {
    pageNow: pagination.current,
    pageSize: pagination.size,
    templateId: props.templateObj.templateId,
    username: searchForm.username,
  };
  const strParams = paramsObjectToString(params);
  try {
    isLoading.value = true;
    actionType.value = 'loading';
    const res = await fetchApps(strParams);
    appList.value = res.data.applyInfo.forEach((v: any) => {
      v.permissionUsers = v.permissionUsers.join(',');
    });
    appList.value = res.data.applyInfo;
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

onMounted(async () => {
  await getAppList();
  await loadAllWorkSpaceUserList(props.workPlaceId);
});
</script>
