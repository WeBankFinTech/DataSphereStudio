<template>
  <FDrawer
    v-model:show="showModal"
    :title="title"
    width="50%"
    :mask-closable="false"
    display-directive="if"
    content-class="customModalStyle"
  >
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
                v-model="searchForm.nodeType"
                :options="nodeTypes"
                placeholder="节点类型"
                filterable
                clearable
                multiple
                collapse-tags
                :collapse-tags-limit="1"
                value-field="value"
                label-field="label"
              />
            </div>
            <div>
              <FSelect
                v-model="searchForm.nodeName"
                :options="allNodes"
                placeholder="节点名称"
                filterable
                clearable
                value-field="title"
                label-field="title"
              />
            </div>
            <div>
              <FSelect
                v-model="searchForm.modifyUser"
                :options="userList"
                placeholder="更新人"
                filterable
                clearable
                value-field="value"
                label-field="label"
              />
            </div>
          </template>
        </BSearch>
      </template>
      <template #table>
        <FTable ref="tableRef" :data="tableList" @sort-change="tableSort">
          <FTable-column
            v-slot="{ row }"
            prop="title"
            label="节点名称"
            :min-width="160"
            :formatter="fillText"
            ellipsis
            :col-style="{ color: '#5384FF', cursor: 'pointer' }"
          >
            <span
              @click="
                handleNodeClick(
                  row,
                  'node_params',
                  props.config.flowId as string
                )
              "
              @dblclick="
                handleNodeDblClick(
                  row,
                  'open_node',
                  props.config.flowId as string
                )
              "
              >{{ row.title }}</span
            >
          </FTable-column>
          <FTable-column
            prop="jobText"
            label="节点类型"
            :min-width="120"
            :formatter="fillText"
            ellipsis
          >
          </FTable-column>
          <FTable-column
            prop="createTime"
            label="创建时间"
            :min-width="182"
            :formatter="fillTimeText"
            ellipsis
            sortable
          />
          <FTable-column
            prop="modifyUser"
            label="更新人"
            :min-width="120"
            :formatter="fillText"
            ellipsis
          />
          <FTable-column
            prop="modifyTime"
            label="更新时间"
            :min-width="182"
            :formatter="fillTimeText"
            ellipsis
            sortable
          />
          <FTable-column label="操作" :min-width="60" fixed="right">
            <FDropdown
              placement="bottom-end"
              :options="moreOperations"
              trigger="click"
              disabled
            >
              <span style="color: #5384ff; cursor: pointer"
                ><MoreCircleOutlined
              /></span>
            </FDropdown>
          </FTable-column>
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
          @change="handlePageChange"
        >
        </FPagination>
      </template>
    </BTablePage>
  </FDrawer>
</template>
<script lang="ts" setup name="nodeDrawer">
import { ref, computed, watch, inject, nextTick, type Ref } from 'vue';
import { MoreCircleOutlined } from '@fesjs/fes-design/icon';
import { useDataUtils } from './hooks/useDataUtils';
import {
  usePagination,
  type PaginationAndParams,
} from '../hooks/usePagination';
type ObjectType = Record<string, unknown>;
const emits = defineEmits(['update:show']);
const props = defineProps({
  show: {
    type: Object,
    default: null,
  },
  config: {
    type: Object,
    default: () => ({}),
  },
  form: {
    type: Object,
    default: () => ({}),
  },
});
const titleMap: { [key: string]: string } = {
  preNode: '的上游一级节点',
  nextNode: '的下游一级节点',
};
const title = computed(
  () => `${props.form.title}${titleMap[props.config.type]}`
);
const showModal = computed({
  get() {
    return !!props.show?.nodeDrawer;
  },
  set(val) {
    emits('update:show', { nodeDrawer: val });
  },
});

const init = () => ({
  nodeName: '',
  nodeType: [],
  modifyUser: '',
  sortBy: 'createTime',
  orderBy: 'descend',
});
const searchForm = ref(init());
const nodeTypes = inject('nodeTypes');
const userList = inject('userList');

const { filterate, handleNodeClick, handleNodeDblClick } = useDataUtils();
const allNodes = ref([]);
const tableList = ref<ObjectType[]>([]);
async function loadTable(param: PaginationAndParams) {
  allNodes.value = props.form[`${props.config.type}List`];
  const newNodes = allNodes.value.filter((item: ObjectType) =>
    filterate(item, param)
  );
  const pageNow = param.pageNow as number;
  const pageSize = param.pageSize as number;
  const start = (pageNow - 1) * pageSize;
  tableList.value = newNodes.slice(start, start + pageSize);
  const total = newNodes.length;
  return {
    totalPage: Math.ceil(total / pageSize),
    totalCount: total,
    pageNow: pageNow,
  };
}

/**
 * @description: 分页查询事件
 * @return {*}
 */
const {
  isLoading,
  pagination,
  handleInit,
  handleCurrent,
  handleCurrentChange,
  fillText,
  fillTimeText,
} = usePagination(searchForm, loadTable);

async function handleSearch() {
  await handleInit();
  tableRef.value?.sort(searchForm.value.sortBy, searchForm.value.orderBy);
}

async function handleReset() {
  await nextTick();
  searchForm.value = init();
  handleSearch();
}

async function handlePageChange(currentPage: number, pageSize: number) {
  await handleCurrentChange(currentPage, pageSize);
  tableRef.value?.sort(searchForm.value.sortBy, searchForm.value.orderBy);
}

// 判断排序参数是否和上次相同
const compareSortParams = (prop: string, order: string) => {
  return prop === searchForm.value.sortBy && order === searchForm.value.orderBy;
};

// 表格排序
const tableRef: Ref = ref(null);
const tableSort = async (sortParam: { prop: string; order: string }) => {
  const tempOrder = sortParam.order ? sortParam.order : 'descend';
  if (!compareSortParams(sortParam.prop, tempOrder)) {
    searchForm.value.sortBy = sortParam.prop;
    searchForm.value.orderBy = tempOrder;
    await handleCurrent();
    tableRef.value?.sort(searchForm.value.sortBy, searchForm.value.orderBy);
  }
};

const moreOperations = ref([]);

watch(
  () => showModal.value,
  async (show) => {
    if (show) {
      await nextTick();
      handleSearch();
    }
  },
  { immediate: true }
);
</script>
<style lang="less">
.customModalStyle {
  .wd-table-page {
    padding: 0 0;
  }
}
</style>
