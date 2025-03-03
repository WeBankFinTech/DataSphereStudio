<template>
  <div class="dss-drawer__table">
    <BTablePage :is-loading="isLoading" action-type="loading">
      <template #search>
        <BSearch
          v-model:form="searchForm"
          @search="handleSearch"
          @reset="handleReset"
        >
          <template #form>
            <div>
              <FSelect
                v-model="searchForm.username"
                :options="allWorkSpaceUserList"
                placeholder="用户名"
                filterable
                clearable
                value-field="value"
                label-field="label"
                @focus="loadAllWorkSpaceUserList(workspaceId as string)"
              />
            </div>
          </template>
        </BSearch>
      </template>
      <template #table>
        <FTable ref="tableRef" :data="tableList">
          <FTable-column
            prop="name"
            label="用户名"
            :formatter="fillText"
            ellipsis
          />
        </FTable>
      </template>
      <template #pagination>
        <FPagination
          show-total
          :page-size="pagination.pageSize"
          :current-page="pagination.pageNow"
          show-size-changer
          :page-size-option="[10, 20, 50, 100]"
          :total-count="pagination.totalCount"
          @change="handleCurrentChange"
        ></FPagination>
      </template>
    </BTablePage>
  </div>
</template>
<script lang="ts" setup>
import { onMounted, ref, computed } from 'vue';
import { usePagination } from '../../hooks/usePagination';
import { useDataList } from './hooks/useDataList';
import { useDataList as useOtherDataList } from '../hooks/useDataList';
import { request } from '@dataspherestudio/shared';
import api from './api';
import type { PaginationAndParams } from '../../hooks/usePagination';

const props = defineProps({
  form: {
    type: Object,
    defualt: () => ({}),
    required: true,
  },
  workspaceId: {
    type: String,
    required: true,
    default: '',
  },
});
const workspaceId = computed(() => props.workspaceId);
const { jsonFilter, fillText } = useDataList();
const { allWorkSpaceUserList, loadAllWorkSpaceUserList } = useOtherDataList();

interface FormType {
  username: string;
}

const init = (): FormType => ({
  username: '',
});
const searchForm = ref(init());

// 查询参数
const params = computed(() => ({
  ...searchForm.value,
}));

/**
 * @description: 获取table数据
 * @return {*}
 */
const tableList = ref([]);
async function loadTable(param: PaginationAndParams) {
  const parametter = jsonFilter({
    ...param,
    ruleId: props.form.ruleId,
    workspaceId: workspaceId.value,
  });
  const res = await request.fetch(api.getConfTemplateUserList, parametter);
  tableList.value = res.data?.users;
  return {
    totalPage: Math.ceil(res.data.total / (param.pageSize as number)),
    totalCount: res.data.total,
    pageNow: param.pageNow,
  };
}

/**
 * @description: 分页查询事件
 * @return {*}
 */
const { isLoading, pagination, handleInit, handleCurrentChange } =
  usePagination(params, loadTable);

// 查询
function handleSearch() {
  handleInit();
}

/**
 * @description: 重置
 * @return {*}
 */
function handleReset() {
  searchForm.value = init();
  handleInit();
}

onMounted(() => {
  handleSearch();
});
</script>
<style lang="less">
.dss-drawer__table {
  .wd-table-page {
    padding: 0 0;
  }
}
</style>
