<template>
  <BTablePage :is-loading="isLoading" :action-type="actionType">
    <template #search>
      <BSearch
        v-model:form="searchForm"
        @search="fetchTableDataMain"
        @reset="handleReset"
      >
        <template #form>
          <div>
            <span class="condition-label">{{ t('workSpace.tmlName') }}</span>
            <FInput
              v-model="searchForm.name"
              :placeholder="t('common.pleaseInput')"
              clearable
            >
            </FInput>
          </div>
          <div>
            <span class="condition-label">{{ t('workSpace.engineType') }}</span>
            <FSelect
              v-model="searchForm.engineType"
              :placeholder="t('common.pleaseSelect')"
              :options="engineList"
              clearable
              filterable
            >
            </FSelect>
          </div>
          <div>
            <span class="condition-label">{{ t('workSpace.relateUser') }}</span>
            <FSelect
              v-model="searchForm.user"
              :placeholder="t('common.pleaseSelect')"
              :options="allWorkSpaceUserList"
              clearable
              filterable
            >
            </FSelect>
          </div>
        </template>
      </BSearch>
    </template>
    <template #operate>
      <FButton type="primary" @click="openAddTemplate">{{
        t('workSpace.createTml')
      }}</FButton>
      <FButton @click="jumpToRule">{{ t('workSpace.tmlApplyRule') }}</FButton>
      <!-- <FButton>{{ t('common.moreAction') }}</FButton> -->
    </template>
    <template #table>
      <f-table
        ref="templateTableRef"
        :data="templateList"
        class="table-container"
        @sort-change="tableSort"
      >
        <f-table-column
          prop="name"
          :label="t('workSpace.tmlName')"
          :width="180"
          ellipsis
        />
        <f-table-column
          prop="engineType"
          :label="t('workSpace.engineType')"
          :width="120"
          ellipsis
        />
        <f-table-column
          prop="permissionType"
          :label="t('workSpace.visibility')"
          :width="88"
          ellipsis
        >
          <template #default="{ row }">
            {{ row.permissionType === 0 ? '全部用户' : '指定用户' }}
          </template>
        </f-table-column>
        <f-table-column
          prop="permissionUserCount"
          :label="t('workSpace.visibleUserCount')"
          :width="102"
          ellipsis
        >
          <template #default="{ row = {} }">
            <span
              class="a-link"
              href="javascript:void(0);"
              @click="openUserDrawer(row)"
              >{{ row.permissionUserCount }}</span
            >
          </template>
        </f-table-column>
        <f-table-column
          v-slot="{ row = {} }"
          :label="t('workSpace.resParams')"
          :width="88"
          ellipsis
        >
          <FTooltip
            popper-class="res-tooltip-style"
            mode="popover"
            placement="bottom"
          >
            <div class="a-link" @mouseover="getResParamsDetails(row)">查看</div>
            <template #content>
              <div class="card-wrapper">
                <FCard
                  header="资源参数"
                  :divider="false"
                  :bordered="false"
                  shadow="never"
                  :body-style="{ padding: '16px 0 0 0' }"
                >
                  <div v-for="(obj, key) in row?.paramsDetails" :key="key">
                    <span style="color: #63656f"
                      >{{ obj.name }}[{{ obj.key }}]：</span
                    >
                    <span>默认值：{{ obj.configValue }}</span
                    >&nbsp;&nbsp;
                    <span
                      v-if="obj?.boundaryType === 2 || obj?.boundaryType === 3"
                      >上限值：{{ obj.maxValue }}</span
                    >
                  </div>
                </FCard>
              </div>
            </template>
          </FTooltip>
        </f-table-column>
        <f-table-column
          prop="description"
          :label="t('workSpace.tmlDes')"
          :width="180"
          ellipsis
        >
          <template #default="{ row }">
            <FEllipsis v-if="row.description">
              {{ row.description }}
              <template #tooltip>
                <div style="max-width: 500px; word-wrap: break-word">
                  {{ row.description }}
                </div>
              </template>
            </FEllipsis>
            <div v-else>--</div>
          </template>
        </f-table-column>
        <f-table-column
          prop="creator"
          :label="t('common.createUser')"
          :width="120"
          ellipsis
        />
        <f-table-column
          prop="createTime"
          :label="t('common.createTime')"
          :width="182"
          ellipsis
          sortable
          :formatter="fillTimeText"
        />
        <f-table-column
          prop="modifier"
          :label="t('common.updateUser')"
          :width="120"
          ellipsis
        />
        <f-table-column
          prop="modifyTime"
          :label="t('common.updateTime')"
          :width="182"
          ellipsis
          sortable
          :formatter="fillTimeText"
        />
        <f-table-column
          v-slot="{ row }"
          :label="t('common.action')"
          align="center"
          fixed="right"
          :width="60"
          ellipsis
        >
          <FDropdown
            :options="tableMoreOptions"
            trigger="focus"
            @click="clickTableMore($event, row)"
          >
            <FButton class="table-operation-item">
              <template #icon> <MoreCircleOutlined /> </template>
            </FButton>
          </FDropdown>
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
        @change="fetchTableDataMain"
        @page-size-change="fetchTableDataMain"
      />
    </template>
  </BTablePage>
  <PermissionUserDrawer
    v-if="userDrawerShow"
    v-model:show="userDrawerShow"
    :template-obj="curTemplateObj"
    :work-place-id="curWorkPlaceId"
  >
  </PermissionUserDrawer>
  <AppDrawer
    v-if="appDrawerShow"
    v-model:show="appDrawerShow"
    :template-obj="curTemplateObj"
    :work-place-id="curWorkPlaceId"
  >
  </AppDrawer>
  <WorkFlowDrawer
    v-if="workFlowDrawerShow"
    v-model:show="workFlowDrawerShow"
    :template-obj="curTemplateObj"
    :work-place-id="curWorkPlaceId"
  >
  </WorkFlowDrawer>
  <TemplateOperateDrawer
    v-if="operateDrawerShow"
    v-model:show="operateDrawerShow"
    :template-obj="curTemplateObj"
    :mode="curMode"
    :work-place-id="curWorkPlaceId"
    @update-template-table="fetchTableDataMain"
  ></TemplateOperateDrawer>
</template>
<script setup lang="ts">
import { useI18n } from 'vue-i18n';
import { BTablePage, BSearch } from '@fesjs/traction-widget';
import { onMounted, ref, reactive } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { MoreCircleOutlined } from '@fesjs/fes-design/icon';
import PermissionUserDrawer from './component/permissionUserDrawer.vue';
import AppDrawer from './component/appDrawer.vue';
import WorkFlowDrawer from './component/workFlowDrawer.vue';
import TemplateOperateDrawer from './component/templateOperateDrawer.vue';
import { FMessage, FModal, FTable } from '@fesjs/fes-design';
import { deleteTemplate } from './api';
import { useDataList } from './hooks/useDataList';
import { fetchTemplateTableData, fetchResParams } from './api';
import { utils } from './hooks/utils';

const router = useRouter();
const route = useRoute();
const { t } = useI18n();
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0,
});
const templateTableRef = ref<typeof FTable>();
const templateList = ref([]);
const searchForm = reactive({
  name: '',
  engineType: '',
  user: '',
  sortBy: 'createTime',
  orderBy: 'descend',
});
const isLoading = ref(false);
const actionType = ref<'loading' | 'emptyQueryResult'>('loading');
const userDrawerShow = ref(false);
const appDrawerShow = ref(false);
const workFlowDrawerShow = ref(false);
const operateDrawerShow = ref(false);
const curMode = ref('add');
const curWorkPlaceId = ref('');
const curTemplateObj = ref({});

const {
  allWorkSpaceUserList,
  engineList,
  loadAllWorkSpaceUserList,
  loadEngineList,
  fillTimeText,
} = useDataList();

const { paramsObjectToString, convertTimeStampToYMDHMS } = utils();
// 表格里的“更多”下拉弹框
const tableMoreOptions = ref([
  {
    value: 'appRefList',
    label: t('workSpace.appRefList'),
  },
  {
    value: 'workFlowRefList',
    label: t('workSpace.workFlowRefList'),
  },
  {
    value: 'edit',
    label: t('common.edit'),
  },
  {
    value: 'delete',
    label: t('common.delete'),
  },
]);

// 更多操作
const clickTableMore = async (
  value: string,
  row: { name: string; templateId: string }
) => {
  console.log('clickTableMore-row: ', row);
  curTemplateObj.value = row;
  // 打开应用抽屉
  if (value === 'appRefList') {
    appDrawerShow.value = true;
  } else if (value === 'workFlowRefList') {
    workFlowDrawerShow.value = true;
  } else if (value === 'edit') {
    curMode.value = 'edit';
    operateDrawerShow.value = true;
  } else if (value === 'delete') {
    FModal.confirm({
      title: '提示',
      content: `确认删除【${row.name}】模板？`,
      okText: '确定',
      cancelText: '取消',
      closable: true,
      onOk: async () => {
        try {
          await deleteTemplate(row.templateId);
          await fetchTableDataMain();
          FMessage.success('删除成功');
        } catch (err) {
          console.error(err);
        }
      },
    });
  }
};

// 打开新增模板
const openAddTemplate = () => {
  curMode.value = 'add';
  operateDrawerShow.value = true;
};

// 跳转到模板应用规则
const jumpToRule = () => {
  console.log('jumpToRule');
  router.push({
    path: '/workspace/resourceTmlManage/applicationRule',
    query: {
      workspaceId: curWorkPlaceId.value,
    },
  });
};

// 打开用户抽屉
const openUserDrawer = (row: object) => {
  console.log('openUserDrawer', row);
  curTemplateObj.value = row;
  userDrawerShow.value = true;
};

// 查询模板对应资源参数信息
const getResParamsDetails = async (row: {
  templateId: string;
  engineType: string;
  paramsDetails: object;
}) => {
  try {
    const params = {
      templateId: row.templateId,
      engineType: row.engineType,
    };
    const strParams = paramsObjectToString(params);
    const res = await fetchResParams(strParams);
    row.paramsDetails = res.conf;
  } catch (err) {
    console.error(err);
  }
};

// 获取表格数据
const fetchTableData = async () => {
  const params = {
    workspaceId: curWorkPlaceId.value,
    pageNow: pagination.current,
    pageSize: pagination.size,
    engineType: searchForm.engineType,
    name: searchForm.name,
    user: searchForm.user,
    sortBy: searchForm.sortBy,
    orderBy: searchForm.orderBy,
  };
  const strParams = paramsObjectToString(params);
  isLoading.value = true;
  actionType.value = 'loading';
  try {
    const res = await fetchTemplateTableData(strParams);
    templateList.value = res.data.templateList;
    pagination.total = res.data.total;
    if (pagination.total === 0) {
      actionType.value = 'emptyQueryResult';
    }
    isLoading.value = false;
  } catch (err) {
    console.error(err);
    isLoading.value = false;
  }
};
// 获取表格后刷新排序状态
const fetchTableDataMain = async () => {
  await fetchTableData();
  if (templateList.value.length > 0 && templateTableRef.value) {
    templateTableRef.value?.sort(searchForm.sortBy, searchForm.orderBy);
  }
};

// 重置操作
const handleReset = () => {
  pagination.current = 1;
  fetchTableDataMain();
};

// 判断排序参数是否和上次相同
const compareSortParams = (prop: string, order: string) => {
  if (prop !== searchForm.sortBy || order !== searchForm.orderBy) {
    return false;
  }
  return true;
};

// 表格排序
const tableSort = async (sortParam: { prop: string; order: string }) => {
  const tempOrder = sortParam.order ? sortParam.order : 'descend';
  if (!compareSortParams(sortParam.prop, tempOrder)) {
    searchForm.sortBy = sortParam.prop;
    searchForm.orderBy = tempOrder;
    await fetchTableDataMain();
  }
};
onMounted(async () => {
  curWorkPlaceId.value = route.query.workspaceId as string;
  await fetchTableDataMain();
  loadAllWorkSpaceUserList(curWorkPlaceId.value);
  loadEngineList();
});
</script>
<style lang="less" scoped>
.res-tooltip-style {
  color: #0f1222;
  background-color: #ffff;
}
.card-wrapper {
  min-width: 541px;
  :deep(.fes-card__header) {
    padding: 0;
  }
}
</style>
