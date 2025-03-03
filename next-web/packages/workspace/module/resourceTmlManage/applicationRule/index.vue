<template>
  <NavHeader :title="pageTitle" :nav-list="navList"></NavHeader>
  <BTablePage :is-loading="isLoading" action-type="loading">
    <template #search>
      <BSearch
        v-model:form="searchForm"
        @search="handleSearch"
        @reset="handleReset"
      >
        <template #form>
          <div>
            <span class="condition-label">模板名称</span>
            <FInput
              v-model="searchForm.templateName"
              placeholder="请输入"
            ></FInput>
          </div>
          <div>
            <span class="condition-label">引擎类型</span>
            <FSelect
              v-model="searchForm.engineType"
              :options="engineTypes"
              placeholder="请选择"
              filterable
              clearable
              value-field="valueField"
              label-field="labelField"
              @focus="handleSelect('engine_type')"
            />
          </div>
          <div>
            <span class="condition-label">规则类型</span>
            <FSelect
              v-model="searchForm.ruleType"
              :options="ruleTypes"
              placeholder="请选择"
              filterable
              clearable
            />
          </div>
          <div>
            <span class="condition-label">覆盖用户</span>
            <FSelect
              v-model="searchForm.user"
              :options="allWorkSpaceUserList"
              placeholder="请选择"
              filterable
              clearable
              value-field="value"
              label-field="label"
              @focus="loadAllWorkSpaceUserList(workspaceId as string)"
            />
          </div>
          <div>
            <span class="condition-label">关联应用</span>
            <FSelect
              v-model="searchForm.application"
              :options="bindApplications"
              placeholder="请选择"
              filterable
              clearable
              value-field="valueField"
              label-field="labelField"
              @focus="handleSelect('bind_application')"
            />
          </div>
        </template>
      </BSearch>
    </template>
    <template #operate>
      <FButton type="primary" @click="handleDrawer('add_rule')"
        >新建规则</FButton
      >
      <FButton @click="handleDrawer('execution_record')">规则执行记录</FButton>
    </template>
    <template #table>
      <FTable ref="tableRef" :data="tableList" @sort-change="tableSort">
        <FTable-column
          prop="ruleId"
          label="规则ID"
          :min-width="116"
          :formatter="fillText"
          ellipsis
        />
        <FTable-column
          v-slot="{ row }"
          prop="ruleType"
          label="规则类型"
          :min-width="166"
          :formatter="fillText"
          ellipsis
        >
          <span v-if="+row.ruleType === 1">工作空间新用户规则</span>
          <span v-else>临时规则</span>
        </FTable-column>
        <FTable-column
          prop="templateName"
          label="模板名称"
          :min-width="180"
          :formatter="fillText"
          ellipsis
        />
        <FTable-column
          prop="engineType"
          label="引擎类型"
          :min-width="120"
          :formatter="fillText"
          ellipsis
        />
        <FTable-column
          v-slot="{ row }"
          prop="permissionType"
          label="覆盖范围"
          :min-width="130"
          :formatter="fillText"
          ellipsis
        >
          <span v-if="+row.permissionType === 3">覆盖部门</span>
          <span v-else-if="+row.permissionType === 2">工作空间新用户</span>
          <span v-else-if="+row.permissionType === 1">指定用户</span>
          <span v-else>全部用户</span>
        </FTable-column>
        <FTable-column
          v-slot="{ row }"
          prop="permissionUserCount"
          label="覆盖用户数"
          :min-width="102"
          :formatter="fillText"
          ellipsis
        >
          <span
            v-if="row.permissionUserCount"
            style="color: #5384ff; cursor: pointer"
            @click="handleDrawer('view_users', row)"
            >{{ row.permissionUserCount }}</span
          >
          <span v-else style="color: #5384ff">- -</span>
        </FTable-column>
        <FTable-column
          prop="application"
          label="关联应用"
          :min-width="140"
          :formatter="fillText"
          ellipsis
        />
        <FTable-column
          v-slot="{ row }"
          prop="status"
          label="执行状态"
          :min-width="88"
          :formatter="fillText"
          ellipsis
        >
          <span v-if="+row.status === 3" style="color: #f75f56">部分失败</span>
          <span v-else-if="+row.status === 2" style="color: #f75f56"
            >执行失败</span
          >
          <span v-else-if="+row.status === 1" style="color: #00cb91"
            >执行成功</span
          >
          <span v-else style="color: #0f1222">未执行</span>
        </FTable-column>
        <FTable-column
          prop="executeTime"
          label="最近执行时间"
          sortable
          :min-width="182"
          :formatter="fillTimeText"
          ellipsis
        />
        <FTable-column
          prop="executeUser"
          label="最近执行人"
          :min-width="120"
          :formatter="fillText"
          ellipsis
        />
        <FTable-column
          prop="creator"
          label="创建人"
          :min-width="120"
          :formatter="fillText"
          ellipsis
        />
        <FTable-column
          prop="createTime"
          label="创建时间"
          sortable
          :min-width="182"
          :formatter="fillTimeText"
          ellipsis
        />
        <FTable-column
          v-slot="{ row }"
          label="操作"
          :min-width="60"
          fixed="right"
        >
          <span
            v-if="row.ruleType === 1"
            style="color: #ff4d4f; cursor: pointer"
            @click="handleDelete(row)"
            >删除</span
          >
          <span v-else>--</span>
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

  <!-- 动态组件 -->
  <FDrawer
    v-model:show="drawerConfig.show"
    :title="drawerConfig.title"
    :footer="drawerConfig.footer"
    width="50%"
    :mask-closable="false"
    display-directive="if"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <component
      :is="dynamicComponent"
      :ref="(el: any) => {
      dynamicRef = el;
    }
      "
      :form="currentRow"
      :workspace-id="workspaceId"
    />
    <template #footer>
      <FSpace>
        <FButton type="primary" @click="handleOk">{{
          drawerConfig.btnText
        }}</FButton>
        <FButton @click="handleCancel">取消</FButton>
      </FSpace>
    </template>
  </FDrawer>
</template>
<script lang="ts" setup>
import {
  onMounted,
  ref,
  computed,
  shallowRef,
  nextTick,
  type Component,
} from 'vue';
import { useRoute } from 'vue-router';
import { request } from '@dataspherestudio/shared';
import { FModal, FMessage } from '@fesjs/fes-design';
import { usePagination } from '../../hooks/usePagination';
import { useDataList } from './hooks/useDataList';
import { useDataList as useOtherDataList } from '../hooks/useDataList';
import NavHeader from '../../components/NavHeader.vue';
import EditRule from './editRule.vue';
import ExecutionRecord from './executionRecord.vue';
import ViewUsers from './viewUsers.vue';
import api from './api';

import type { PaginationAndParams } from '../../hooks/usePagination';
type ObjectType = Record<string, unknown>;

const route = useRoute();
const {
  bindApplications, // 关联应用
  engineTypes, // 引擎类型
  handleSelect,
  jsonFilter,
  ruleTypes,
} = useDataList();

const workspaceId = computed(() => route.query.workspaceId);
const { allWorkSpaceUserList, loadAllWorkSpaceUserList } = useOtherDataList();
const pageTitle = '模版应用规则';
const navList = ref<ObjectType[]>([]);

const init = () => ({
  templateName: '',
  engineType: '',
  user: '',
  application: '',
  ruleType: '',
  sortBy: 'createTime',
  orderBy: 'descend',
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
const tableList = ref<ObjectType[]>([]);
async function loadTable(param: PaginationAndParams) {
  const parametter = jsonFilter({
    ...param,
    workspaceId: workspaceId.value,
  });
  const res = await request.fetch(
    `${api.getConfTemplateApplyRuleList}`,
    parametter
  );
  tableList.value = res.data?.ruleList || [];
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
const {
  isLoading,
  pagination,
  handleInit,
  handleCurrent,
  handleCurrentChange,
  fillText,
  fillTimeText,
} = usePagination(params, loadTable);

/**
 * @description: 重置
 * @return {*}
 */
async function handleSearch() {
  await handleInit();
  tableRef.value?.sort(searchForm.value.sortBy, searchForm.value.orderBy);
}

/**
 * @description: 重置
 * @return {*}
 */
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
const tableRef: any = ref(null);
const tableSort = async (sortParam: { prop: string; order: string }) => {
  const tempOrder = sortParam.order ? sortParam.order : 'descend';
  if (!compareSortParams(sortParam.prop, tempOrder)) {
    searchForm.value.sortBy = sortParam.prop;
    searchForm.value.orderBy = tempOrder;
    await handleCurrent();
    tableRef.value?.sort(searchForm.value.sortBy, searchForm.value.orderBy);
  }
};
onMounted(() => {
  handleSearch();
});

// drawer相关操作
const editOptions = shallowRef([
  {
    value: 'add_rule',
    label: '新建规则',
    footer: true,
    btnText: '新建规则并执行',
    component: EditRule,
  },
  {
    value: 'execution_record',
    label: '规则执行记录',
    footer: false,
    component: ExecutionRecord,
  },
  {
    value: 'view_users',
    label: '覆盖用户列表',
    footer: false,
    component: ViewUsers,
  },
]);

// 点击drawer添加和编辑
const currentRow = ref<ObjectType | null>(null);
const dynamicComponent = shallowRef<Component | null>(null);
const drawerConfig = ref({
  show: false,
  footer: false,
  title: '',
  btnText: '',
});

interface DynamicItemType {
  value: string;
  label: string;
  footer: boolean;
  btnText: string;
  component: Component;
}
// 显示drawer
function handleDrawer(key: string, row?: ObjectType) {
  const current = editOptions.value.find(
    (item) => item.value === key
  ) as DynamicItemType;
  const { component, label, footer, btnText } = current;
  dynamicComponent.value = component;
  drawerConfig.value = {
    show: true,
    footer,
    title: label,
    btnText,
  };
  if (row) {
    currentRow.value = row;
    drawerConfig.value.title = `${label}(${row.ruleId})`;
  }
}

// 表单确认
type EditRuleType = InstanceType<typeof EditRule>;
const dynamicRef = ref<EditRuleType | null>(null);

// 取消重置组件和drawer
function handleCancel() {
  currentRow.value = null;
  dynamicRef.value = null;
  dynamicComponent.value = null;
  drawerConfig.value = {
    show: false,
    footer: false,
    title: '',
    btnText: '',
  };
}

// 确定操作
async function handleOk() {
  if (dynamicRef.value) {
    await dynamicRef.value.submit();
    handleCancel();
    handleSearch();
  }
}

// 删除
function handleDelete(row: ObjectType) {
  FModal.confirm({
    title: '确认',
    content: `确认将规则【${row.ruleId}】删除吗？`,
    onOk: async () => {
      try {
        await request.fetch(
          `${api.deleteConfTemplateApplyRule}/${row.ruleId}`,
          {},
          'post'
        );
        FMessage.success('规则删除成功!');
        handleSearch();
        return Promise.resolve();
      } catch (err) {
        console.warn(err);
      }
    },
    onCancel() {
      return Promise.resolve();
    },
  });
}
</script>
