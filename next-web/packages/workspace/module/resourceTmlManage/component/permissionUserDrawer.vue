<template>
  <FDrawer
    v-model:show="drawerShow"
    :title="`可见用户列表（${templateObj.name})`"
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
          @search="getUserList"
          @reset="getUserList"
        >
          <template #form>
            <FSelect
              v-model="searchForm.username"
              :placeholder="$t('common.userName')"
              :options="allWorkSpaceUserList"
              filterable
              clearable
            >
            </FSelect>
          </template>
        </BSearch>
      </template>
      <template #table>
        <f-table :data="userList" class="table-container">
          <f-table-column
            prop="name"
            :label="$t('common.userName')"
            :width="180"
            ellipsis
          />
        </f-table>
      </template>
      <template #pagination>
        <FPagination
          v-model:currentPage="pagination.current"
          v-model:pageSize="pagination.size"
          show-size-changer
          show-total
          :total-count="pagination.total"
          @change="getUserList"
          @page-size-change="getUserList"
        />
      </template>
    </BTablePage>
  </FDrawer>
</template>
<script lang="ts" setup>
import { onMounted, computed, ref, reactive, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { fetchTemplateUser } from '../api';
import { useDataList } from '../hooks/useDataList';
import { utils } from '../hooks/utils';

const { t: $t } = useI18n();
const isLoading = ref(false);
const actionType = ref('loading');
type User = {
  name: string;
};
const userList: Ref<User[]> = ref([]);
const searchForm = reactive({
  username: '',
});
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
});
const { allWorkSpaceUserList, loadAllWorkSpaceUserList } = useDataList();
const { paramsObjectToString } = utils();
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

const emit = defineEmits(['update:show']);
const drawerShow = computed({
  get: () => props.show,
  set: (value) => {
    emit('update:show', value);
  },
});
const closeDrawer = () => {
  console.log('closeDrawer');
  emit('update:show', false);
};
const getUserList = async () => {
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
    const res = await fetchTemplateUser(strParams);
    userList.value = res.data.users;
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
  await getUserList();
  await loadAllWorkSpaceUserList(props.workPlaceId);
});
</script>
