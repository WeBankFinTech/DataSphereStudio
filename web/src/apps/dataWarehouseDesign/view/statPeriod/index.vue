<template>
  <div class="console-page">
    <div class="top-line">
      <div style="display: flex">
        <Select
          v-model="searchParams.enabled"
          placeholder="统计周期状态"
          style="width: 120px"
          clearable
        >
          <Option :value="1"> 启用 </Option>
          <Option :value="0"> 禁用 </Option>
        </Select>
        <Input
          search
          v-model="searchParams.name"
          enter-button
          placeholder="输入名称搜索"
          style="width: 300px"
          @on-search="handleSearch"
        />
      </div>
      <Button type="primary" icon="md-add" @click="handleCreate">
        新增统计周期
      </Button>
    </div>
    <Table
      :columns="columns"
      :data="datalist"
      :loading="loading"
      style="margin-bottom: 16px"
    >
      <template slot-scope="{ row }" slot="principalName">
        <Tag v-for="name of row.principalName.split(',')" :key="name">
          {{ name }}
        </Tag>
      </template>
      <template slot-scope="{ row }" slot="isAvailable">
        {{ row.isAvailable ? "启用" : "禁用" }}
      </template>
      <template slot-scope="{ row }" slot="createTime">
        {{ row.createTime | formatDate }}
      </template>
      <template slot-scope="{ row }" slot="updateTime">
        {{ row.updateTime | formatDate }}
      </template>
      <template slot-scope="{ row }" slot="action">
        <Button
          size="small"
          @click="handleEdit(row.id)"
          style="margin-right: 5px"
        >
          编辑
        </Button>
        <Button
          size="small"
          @click="handleDisable(row.id)"
          style="margin-right: 5px"
          v-if="row.isAvailable"
        >
          禁用
        </Button>
        <Button
          type="primary"
          size="small"
          @click="handleEnable(row.id)"
          style="margin-right: 5px"
          v-else
        >
          启用
        </Button>
        <Button type="error" size="small" @click="handleDelete(row.id)">
          删除
        </Button>
      </template>
    </Table>
    <div class="page-line">
      <Page
        :total="pageCfg.total"
        :current.sync="pageCfg.page"
        :page-size="pageCfg.pageSize"
      />
    </div>
    <EditModal
      v-model="modalCfg.visible"
      :id="modalCfg.id"
      :mode="modalCfg.mode"
      @finish="handleModalFinish"
    />
  </div>
</template>

<script>
import {
  getStatisticalPeriods,
  deleteStatisticalPeriods,
  enableStatisticalPeriods,
  disableStatisticalPeriods,
} from "../../service/api";
import formatDate from "../../utils/formatDate";
import EditModal from "./editModal.vue";

/**
 *  @param (Number)
 *  @return (String)
 */
function number2boolean(num) {
  if (typeof num === "undefined") {
    return undefined;
  } else {
    return Boolean(num);
  }
}

export default {
  components: { EditModal },
  filters: { formatDate },
  methods: {
    // 弹框回调
    handleModalFinish() {
      this.handleGetData();
    },
    // 创建操作
    handleCreate() {
      this.modalCfg = {
        visible: true,
        mode: "create",
      };
    },
    // 删除操作
    async handleDelete(id) {
      this.$Modal.confirm({
        title: "警告",
        content: "确定删除此项吗？",
        onOk: async () => {
          this.loading = true;
          await deleteStatisticalPeriods(id);
          this.loading = false;
          this.handleGetData(true);
        },
      });
    },
    // 编辑操作
    handleEdit(id) {
      this.modalCfg = {
        visible: true,
        mode: "edit",
        id: id,
      };
    },
    // 启用
    async handleEnable(id) {
      this.loading = true;
      await enableStatisticalPeriods(id);
      this.loading = false;
      this.handleGetData(true);
    },
    // 禁用
    async handleDisable(id) {
      this.loading = true;
      await disableStatisticalPeriods(id);
      this.loading = false;
      this.handleGetData(true);
    },
    // 搜索
    handleSearch() {
      this.handleGetData();
    },
    // 获取数据
    async handleGetData(changePage = false) {
      if (changePage === false && this.pageCfg.page !== 1) {
        return (this.pageCfg.page = 1);
      }
      this.loading = true;
      let data = await getStatisticalPeriods({
        page: this.pageCfg.page,
        size: this.pageCfg.pageSize,
        name: this.searchParams.name,
        enabled: number2boolean(this.searchParams.enabled),
      });
      this.loading = false;
      let { items, total } = data.page;
      this.pageCfg.total = total;
      this.datalist = items;
    },
  },
  mounted() {
    this.handleGetData();
  },
  watch: {
    "pageCfg.page"() {
      this.handleGetData(true);
    },
  },
  data() {
    return {
      // 搜索参数
      searchParams: {
        name: "",
        enabled: undefined,
      },
      // 表格列
      columns: [
        {
          title: "名称",
          key: "name",
        },
        {
          title: "英文名",
          key: "name",
        },
        {
          title: "负责人",
          key: "name",
        },
        {
          title: "主题域",
          key: "themeArea",
        },
        {
          title: "分层",
          key: "layerArea",
        },
        {
          title: "选择权限",
          key: "principalName",
          slot: "principalName",
        },
        {
          title: "状态",
          key: "isAvailable",
          slot: "isAvailable",
        },
        {
          title: "描述",
          key: "description",
          ellipsis: true,
        },
        {
          title: "创建时间",
          key: "createTime",
          slot: "createTime",
        },
        {
          title: "更新时间",
          key: "updateTime",
          slot: "updateTime",
        },
        {
          title: "操作",
          slot: "action",
          minWidth: 80,
        },
      ],
      // 数据列表
      datalist: [],
      // 弹窗参数
      modalCfg: {
        mode: "",
        id: NaN,
        visible: false,
      },
      // 是否加载中
      loading: false,
      // 分页配置
      pageCfg: {
        page: 1,
        pageSize: 10,
        total: 10,
      },
    };
  },
};
</script>

<style lang="scss" scoped>
.top-line {
  margin-bottom: 16px;
  display: flex;
  justify-content: space-between;
}
.page-line {
  display: flex;
  justify-content: flex-end;
}
</style>
