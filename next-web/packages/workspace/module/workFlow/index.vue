<template>
  <BTablePage
    :is-loading="isLoading"
    action-type="loading"
    class="dss-table-page"
  >
    <template #search>
      <div class="table-header">
        <BSearch
          v-model:form="searchForm"
          v-model:advanceForm="advanceForm"
          :is-advance="true"
          @search="handleSearch"
          @reset="handleReset"
          @advance="showAdvance = true"
        >
          <template #form>
            <div>
              <span class="condition-label">节点名称</span>
              <FSelect
                v-model="searchForm.nodeName"
                :options="allNodes"
                placeholder="请选择"
                filterable
                clearable
                value-field="title"
                label-field="title"
              />
            </div>
            <div>
              <span class="condition-label">节点类型</span>
              <FSelect
                v-model="searchForm.nodeType"
                :options="nodeTypes"
                placeholder="请选择"
                filterable
                clearable
                multiple
                collapse-tags
                :collapse-tags-limit="1"
                value-field="value"
                label-field="label"
              />
            </div>
            <!-- <div>
            <span class="condition-label">创建人</span>
            <FSelect
              v-model="searchForm.ruleType"
              :options="creatorList"
              placeholder="请选择"
              filterable
              clearable
            />
          </div> -->
          </template>
        </BSearch>
        <FSpace>
          <FDropdown
            trigger="click"
            :options="pageOptions"
            @click="handleDropDown"
          >
            <FButton>更多操作</FButton>
          </FDropdown>
        </FSpace>
      </div>
    </template>
    <template #table>
      <FTable ref="tableRef" :data="tableList" @sort-change="tableSort">
        <template v-for="(item, index) in tableColumns">
          <FTable-column
            v-if="item.type === 'text'"
            :key="(item.prop as string) + index"
            :label="item.label"
            :prop="item.prop"
            :ellipsis="item.ellipsis"
            :min-width="item.minWidth"
            :formatter="item.formatter"
            :visible="checkTColShow(item.prop as string)"
          />
          <FTable-column
            v-else-if="item.type === 'time'"
            :key="'time' + index"
            :label="item.label"
            :prop="item.prop"
            :ellipsis="item.ellipsis"
            :min-width="item.minWidth"
            :formatter="item.formatter"
            :sortable="item.sortable"
            :visible="checkTColShow(item.prop as string)"
          />
          <FTable-column
            v-else-if="item.type === 'link'"
            v-slot="{ row }"
            :key="'link' + index"
            :label="item.label"
            :prop="item.prop"
            :ellipsis="item.ellipsis"
            :min-width="item.minWidth"
            :visible="checkTColShow(item.prop as string)"
            :col-style="{ color: '#5384FF', cursor: 'pointer' }"
          >
            <span
              @click="
                handleNodeClick(row, 'node_params', queryParam.flowId as string)
              "
              @dblclick="
                handleNodeDblClick(
                  row,
                  'open_node',
                  queryParam.flowId as string
                )
              "
              >{{ row.title }}</span
            >
          </FTable-column>
          <FTable-column
            v-else-if="item.type === 'node'"
            v-slot="{ row }"
            :key="'node' + index"
            :label="item.label"
            :prop="item.prop"
            :min-width="item.minWidth"
            :visible="checkTColShow(item.prop as string)"
            :col-style="{ position: 'relative' }"
          >
            <div
              class="dss-column-hover"
              @mouseenter="handleMouse(row, item.prop as string)"
              @mouseleave="handleMouse(row, '')"
            >
              <span
                :class="[
                  'column-ellipis',
                  row.hoverKey === item.prop ? 'hover' : '',
                ]"
              >
                <FEllipsis>{{ row[item.prop as string] || '- -' }}</FEllipsis>
              </span>
              <span
                v-if="row[item.prop as string] && row.hoverKey === item.prop"
                class="column-icon"
                @click="handleBtnClick(item.prop as string, 'nodeDrawer', row)"
              >
                <MoreCircleOutlined />
              </span>
            </div>
          </FTable-column>
          <FTable-column
            v-else-if="item.type === 'action'"
            :key="item.prop"
            :label="item.label"
            :min-width="item.minWidth"
            fixed="right"
          >
            <FDropdown
              placement="bottom-end"
              :options="moreOperations"
              trigger="click"
              disabled
            >
              <span style="color: #5384ff; cursor: pointer">
                <MoreCircleOutlined />
              </span>
            </FDropdown>
          </FTable-column>
        </template>
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

  <!-- 高级筛选 -->
  <AdvanceModal
    ref="advanceRef"
    v-model:show="showAdvance"
    :form="advanceForm"
    @success="handleAdvanceSearch"
  />

  <!-- 设置表格 -->
  <BTableHeaderConfig
    v-model:headers="headers"
    v-model:show="showTableHeaderConfig"
    :origin-headers="originHeaders"
    type="example"
  />

  <!-- 通用编辑 -->
  <component
    :is="currentComp"
    v-model:show="currentShow"
    :config="currentConfig"
    :form="currentForm"
    @success="handleSearch"
  />
</template>
<script lang="ts" setup>
import {
  onMounted,
  onUnmounted,
  ref,
  computed,
  provide,
  defineAsyncComponent,
  nextTick,
  type Component,
  type Ref,
} from 'vue';
import { useI18n } from 'vue-i18n';
import { request } from '@dataspherestudio/shared';
import { MoreCircleOutlined } from '@fesjs/fes-design/icon';
import {
  usePagination,
  type PaginationAndParams,
} from '../hooks/usePagination';
import { useTableConfig } from '../hooks/useTableConfig';
import AdvanceModal from './advanceModal.vue';
import { getUrlParams } from '@fesjs/traction-widget';
import { useDataList } from './hooks/useDataList';
import { useDataUtils } from './hooks/useDataUtils';
import api from './api';
import { FSpace } from '@fesjs/fes-design';

type ObjectType = Record<string, unknown>;
const { t: $t } = useI18n();
const queryParam = ref<ObjectType>({
  projectId: '',
  flowId: '',
  workspaceId: '',
  labels: '',
});
const allNodes = ref<ObjectType[]>([]);
const {
  nodeTypes,
  templateList,
  userList,
  getNodeTypes,
  getConfTemplateList,
  getUserList,
} = useDataList();

provide(
  'nodeTypes',
  computed(() => nodeTypes.value)
);
provide(
  'templateList',
  computed(() => templateList.value)
);
provide(
  'userList',
  computed(() => userList.value)
);
provide(
  'allNodeList',
  computed(() => allNodes.value)
);

const init = () => ({
  nodeName: '',
  nodeType: [],
  sortBy: 'createTime',
  orderBy: 'descend',
});
const searchForm = ref(init());

// 高级筛选
const initAdvance = () => ({
  nodeName: '',
  nodeType: [],
  modifyUser: '',
  templateId: '',
  updateTimes: [],
});
const advanceForm = ref<ObjectType>(initAdvance());
const showAdvance = ref<boolean>(false);
const searchType = ref<string>('common');

// 查询参数
const params = computed(() => {
  if (searchType.value === 'common') {
    return { ...searchForm.value };
  }
  return {
    ...advanceForm.value,
    sortBy: searchForm.value.sortBy,
    orderBy: searchForm.value.orderBy,
  };
});

function deconstruction(edges: ObjectType[], nodes: ObjectType[]) {
  const nodeMap: ObjectType = {};
  nodes.forEach((item: ObjectType) => {
    const job =
      nodeTypes.value.find((v: ObjectType) => v.value === item.jobType) || {};
    item.jobText = (job as ObjectType).label || '';
    item.hoverKey = '';
    nodeMap[item.key as string] = {
      key: item.key,
      bindViewKey: item.bindViewKey,
      createTime: item.createTime,
      ecConfTemplateId: item.ecConfTemplateId,
      ecConfTemplateName: item.ecConfTemplateName,
      desc: item.desc,
      id: item.id,
      jobContent: item.jobContent,
      jobType: item.jobType,
      jobText: item.jobText,
      layout: item.layout,
      modifyTime: item.modifyTime,
      modifyUser: item.modifyUser,
      params: item.params,
      resources: item.resources,
      selected: item.selected,
      title: item.title,
    };
  });
  const newNodes = nodes.map((item: ObjectType) => {
    item.preNodeList = edges
      .filter((edge: ObjectType) => edge.target === item.key)
      .map((edge: ObjectType) => nodeMap[edge.source as string]);
    item.preNode = (item.preNodeList as ObjectType[])
      .map((item: ObjectType) => item.title)
      .join(',');
    item.nextNodeList = edges
      .filter((edge: ObjectType) => edge.source === item.key)
      .map((edge: ObjectType) => nodeMap[edge.target as string]);
    item.nextNode = (item.nextNodeList as ObjectType[])
      .map((item: ObjectType) => item.title)
      .join(',');
    nodeMap[item.key as string] = item;
    return item;
  });
  return newNodes;
}

/**
 * @description: 获取table数据
 * @return {*}
 */
const tableList = ref<ObjectType[]>([]);
const { filterate, handleNodeClick, handleNodeDblClick } = useDataUtils();
async function loadTable(param: PaginationAndParams) {
  const res = await request.fetch(api.getFlowList, {
    isNotHaveLock: true,
    flowId: queryParam.value.flowId,
    labels: queryParam.value.labels,
  });
  const { edges, nodes } = JSON.parse(res.data.flow.flowJson);
  allNodes.value = deconstruction(edges, nodes)
    .filter((item: ObjectType) => filterate(item, param))
    .sort((a: { [key: string]: any }, b: { [key: string]: any }) => {
      const { orderBy, sortBy } = searchForm.value;
      if (orderBy === 'descend') {
        return b[sortBy] - a[sortBy];
      } else {
        return a[sortBy] - b[sortBy];
      }
    });
  const pageNow = param.pageNow as number;
  const pageSize = param.pageSize as number;
  const start = (pageNow - 1) * pageSize;
  tableList.value = allNodes.value.slice(start, start + pageSize);
  const total = allNodes.value.length;
  return {
    totalPage: Math.ceil(total / pageSize),
    totalCount: total,
    pageNow: pageNow,
  };
}

// 分页查询事件
const {
  isLoading,
  pagination,
  handleInit,
  handleCurrent,
  handleCurrentChange,
  fillText,
  fillTimeText,
} = usePagination(params, loadTable);

async function handleSearch() {
  searchType.value = 'common';
  advanceForm.value = initAdvance();
  await handleInit();
  tableRef.value?.sort(searchForm.value.sortBy, searchForm.value.orderBy);
}

// 高级筛选查询
async function handleAdvanceSearch(data: ObjectType) {
  searchType.value = 'advance';
  searchForm.value = init();
  advanceForm.value = { ...data };
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

onMounted(async () => {
  queryParam.value = (getUrlParams() as ObjectType) || {};
  await getNodeTypes(queryParam.value);
  getConfTemplateList(queryParam.value);
  getUserList(queryParam.value);
  handleSearch();
  window.addEventListener('message', (event) => {
    const { type } = JSON.parse(event?.data || '{}');
    if (type === 'dss_change_node') {
      console.log('dss_change_node');
      handleSearch();
    }
  });
});

onUnmounted(() => {
  window.removeEventListener('message', () => {
    console.log('卸载监听');
  });
});

const pageOptions = [
  {
    value: '1',
    label: '节点路径关系查询',
  },
  {
    value: '3',
    label: '设置表格',
  },
];

// 更多操作点击
function handleDropDown(index: string) {
  switch (+index) {
    case 1:
      handleNodeClick(
        null,
        'node_dependance',
        queryParam.value.flowId as string
      );
      break;
    case 3:
      showTableHeaderConfig.value = !showTableHeaderConfig.value;
      break;
    default:
      break;
  }
}

function handleMouse(item: ObjectType, key: string) {
  item.hoverKey = key;
}

const moreOperations = ref([]);

const componentMap: { [key: string]: Component } = {
  nodeDrawer: defineAsyncComponent(() => import('./nodeDrawer.vue')),
};
const currentShow = ref<ObjectType | null>(null);
const currentForm = ref<ObjectType | null>(null);
const currentConfig = ref<ObjectType>({ type: 'add' });
const currentComp = ref<Component | null>(null);

// 编辑
function handleBtnClick(type: string, name: string, row: ObjectType | null) {
  currentShow.value = { [name]: true };
  currentConfig.value = { type, flowId: queryParam.value.flowId };
  currentComp.value = componentMap[name];
  currentForm.value = { ...row };
}

// 设置表格
const tableColumns = ref<ObjectType[]>([
  {
    type: 'link',
    prop: 'title',
    label: '节点名称',
    ellipsis: true,
    minWidth: 160,
    formatter: fillText,
  },
  {
    type: 'text',
    prop: 'jobText',
    label: '节点类型',
    ellipsis: true,
    minWidth: 120,
    formatter: fillText,
  },
  {
    type: 'node',
    prop: 'preNode',
    label: '上游一级节点',
    ellipsis: true,
    minWidth: 160,
    formatter: fillText,
  },
  {
    type: 'node',
    prop: 'nextNode',
    label: '下游一级节点',
    ellipsis: true,
    minWidth: 160,
    formatter: fillText,
  },
  {
    type: 'text',
    prop: 'ecConfTemplateName',
    label: '资源参数模板',
    ellipsis: true,
    minWidth: 180,
    formatter: fillText,
  },
  // {
  //   type: 'text',
  //   prop: 'createUser',
  //   label: $t('announcement.creator'),
  //   ellipsis: true,
  //   minWidth: 120,
  //   formatter: fillText,
  // },
  {
    type: 'time',
    prop: 'createTime',
    label: $t('announcement.createTime'),
    ellipsis: true,
    minWidth: 182,
    sortable: true,
    formatter: fillTimeText,
  },
  {
    type: 'text',
    prop: 'modifyUser',
    label: '更新人',
    ellipsis: true,
    minWidth: 120,
    formatter: fillText,
  },
  {
    type: 'time',
    prop: 'modifyTime',
    label: '更新时间',
    ellipsis: true,
    minWidth: 182,
    sortable: true,
    formatter: fillTimeText,
  },
  {
    type: 'action',
    prop: 'action',
    label: $t('announcement.operation'),
    minWidth: 60,
  },
]);

const { headers, originHeaders, showTableHeaderConfig, checkTColShow } =
  useTableConfig(tableColumns);
</script>
<style lang="less" scoped>
.dss-table-page {
  .table-header {
    display: flex;
    justify-content: space-between;
  }

  :deep(.fes-divider) {
    display: none;
  }

  .dss-column-hover {
    height: 54px;
    display: flex;
    align-items: center;
    position: absolute;
    width: calc(100% - 32px);
    top: 0;
    left: 16px;

    .column-ellipis {
      display: inline-block;
      vertical-align: text-bottom;
      margin-right: 5px;
      max-width: 100%;

      &.hover {
        max-width: calc(100% - 20px);
      }
    }

    .column-icon {
      cursor: pointer;
      display: inline-block;
      height: 19px;
      width: 20px;
      color: #93949b;
    }
  }
}
</style>
