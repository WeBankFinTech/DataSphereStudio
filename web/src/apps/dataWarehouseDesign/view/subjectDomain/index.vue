<template>
  <div class="console-page">
    <div class="top-line">
      <Input
        search
        enter-button
        placeholder="输入名称搜索"
        style="width: 300px"
        v-model="searchVal"
        @on-search="handleSearch"
      />
      <Button type="primary" icon="md-add" @click="handleCreate">
        创建主题域
      </Button>
    </div>
    <Table
      :columns="columns"
      :data="dataList"
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
import EditModal from "./editModal.vue";
import formatDate from "@dataWarehouseDesign/utils/formatDate";
import {
  getThemedomains,
  deleteThemedomains,
  enableThemedomains,
  disableThemedomains,
} from "@dataWarehouseDesign/service/api";

export default {
  components: { EditModal },
  filters: { formatDate },
  methods: {
    // 弹框回调
    handleModalFinish() {
      this.handleGetData(true);
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
          await deleteThemedomains(id);
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
        id,
      };
    },
    // 启用
    async handleEnable(id) {
      this.loading = true;
      await enableThemedomains(id);
      this.loading = false;
      this.handleGetData(true);
    },
    // 禁用
    async handleDisable(id) {
      this.loading = true;
      await disableThemedomains(id);
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
      let data = await getThemedomains({
        page: this.pageCfg.page,
        size: this.pageCfg.pageSize,
        name: this.searchVal,
      });
      this.loading = false;
      let { items, total } = data.page;
      this.pageCfg.total = total;
      this.dataList = items;
    },
  },
  mounted() {
    this.handleGetData(true);
  },
  watch: {
    "pageCfg.page"() {
      this.handleGetData(true);
    },
  },

  data() {
    return {
      // 搜索
      searchVal: "",
      columns: [
        {
          title: "名称",
          key: "name",
        },
        {
          title: "英文名",
          key: "enName",
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
          title: "选择权限",
          key: "principalName",
          slot: "principalName",
        },
        {
          title: "负责人",
          key: "owner",
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
          minWidth: 60,
        },
      ],
      dataList: [],
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
