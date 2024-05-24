<template>
  <BTablePage
    :is-loading="isLoading"
    action-type="loading"
    class="dss-table-page"
  >
    <template #operate>
      <FButton type="primary" @click="handleEdit">{{
        $t('announcement.addAnnouncement')
      }}</FButton>
    </template>
    <template #table>
      <FTable ref="tableRef" :data="tableList" @sort-change="tableSort">
        <template v-for="(item, index) in tableColumns">
          <FTable-column
            v-if="item.type === 'text'"
            :key="item.prop + index"
            :label="item.label"
            :prop="item.prop"
            :ellipsis="item.ellipsis"
            :min-width="item.minWidth"
            :formatter="item.formatter"
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
          />
          <FTable-column
            v-else-if="item.type === 'link'"
            v-slot="{ row }"
            :key="'link' + index"
            :label="item.label"
            :prop="item.prop"
            :ellipsis="item.ellipsis"
            :min-width="item.minWidth"
            :formatter="item.formatter"
          >
            <span v-if="+row.status === 2" style="color: #93949b">{{
              $t('announcement.expired')
            }}</span>
            <span v-else-if="+row.status === 1" style="color: #00cb91">{{
              $t('announcement.inEffect')
            }}</span>
            <span v-else style="color: #0f1222">{{
              $t('announcement.notActivated')
            }}</span>
          </FTable-column>
          <FTable-column
            v-else-if="item.type === 'action'"
            v-slot="{ row }"
            :key="item.prop"
            :label="item.label"
            :min-width="item.minWidth"
            fixed="right"
          >
            <span
              v-if="+row.status !== 2"
              style="color: #ff4d4f; cursor: pointer"
              @click="handleDelete(row)"
              >{{ $t('announcement.delete') }}</span
            >
            <span v-else style="color: #b7b7bc; cursor: not-allowed">{{
              $t('announcement.delete')
            }}</span>
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

  <!-- 动态组件 -->
  <FModal
    v-model:show="showEdit"
    :title="$t('announcement.addAnnouncement')"
    :mask-closable="false"
    display-directive="if"
    @ok="handleOk"
    @cancel="handleCancel"
  >
    <EditAnnouncement ref="editRef" :api="announceApi" />
  </FModal>
</template>
<script lang="ts" setup>
import { onMounted, ref, computed } from 'vue';
import { useI18n } from 'vue-i18n';
import { request } from '@dataspherestudio/shared';
import { FModal, FMessage } from '@fesjs/fes-design';
import {
  usePagination,
  type PaginationAndParams,
} from '../hooks/usePagination';
import EditAnnouncement from './editAnnouncement.vue';
import api from './api';
import { useRoute } from 'vue-router';

type ObjectType = Record<string, unknown>;
const { t: $t } = useI18n();
const route = useRoute();

const init = () => ({
  sortBy: 'createTime',
  orderBy: 'descend',
});
const searchForm = ref(init());
const announceApi = computed(() =>
  route.query.env === 'scripts' ? api['scripts'] : api
);

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
  const parametter = {
    sortBy: param.sortBy,
    orderBy: param.orderBy,
    pageSize: param.pageSize,
    currentPage: param.pageNow,
  };
  const res = await request.fetch(announceApi.value.getNoticeList, parametter);
  tableList.value = res.data?.noticeList || [];
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

async function handleSearch() {
  await handleInit();
  tableRef.value?.sort(searchForm.value.sortBy, searchForm.value.orderBy);
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

// 编辑
type EditAnnouncementType = InstanceType<typeof EditAnnouncement>;
const editRef = ref<EditAnnouncementType | null>(null);
const showEdit = ref(false);
function handleEdit() {
  showEdit.value = true;
}

function handleCancel() {
  showEdit.value = false;
}

async function handleOk() {
  if (editRef.value) {
    await editRef.value.submit();
    handleCancel();
    handleSearch();
  }
}

// 删除
function handleDelete(row: ObjectType) {
  FModal.confirm({
    title: '确认',
    content: `确认将公告【${row.id}】删除吗？`,
    onOk: async () => {
      try {
        await request.fetch(
          announceApi.value.deleteNotice,
          { noticeId: row.id },
          'post'
        );
        FMessage.success('公告删除成功!');
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

// 设置表格
const tableColumns = ref([
  {
    type: 'text',
    prop: 'id',
    label: $t('announcement.announcementId'),
    ellipsis: true,
    minWidth: 116,
    formatter: fillText,
  },
  {
    type: 'text',
    prop: 'content',
    label: $t('announcement.announcementContent'),
    ellipsis: true,
    minWidth: 400,
    formatter: fillText,
  },
  {
    type: 'time',
    prop: 'startTime',
    label: $t('announcement.announcementStartTime'),
    ellipsis: true,
    minWidth: 182,
    sortable: true,
    formatter: fillTimeText,
  },
  {
    type: 'time',
    prop: 'endTime',
    label: $t('announcement.announcementEndTime'),
    ellipsis: true,
    minWidth: 182,
    sortable: true,
    formatter: fillTimeText,
  },
  {
    type: 'link',
    prop: 'status',
    label: $t('announcement.announcementStatus'),
    ellipsis: true,
    minWidth: 88,
    formatter: fillText,
  },
  {
    type: 'text',
    prop: 'createUser',
    label: $t('announcement.creator'),
    ellipsis: true,
    minWidth: 120,
    formatter: fillText,
  },
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
    type: 'action',
    prop: 'action',
    label: $t('announcement.operation'),
    minWidth: 60,
  },
]);
</script>
<style lang="less" scoped>
.dss-table-page {
  :deep(.fes-divider) {
    display: none;
  }
}
</style>
