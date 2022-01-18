<template>
  <div>
    <div class="top-line">
      <div style="display: flex">
        <Select
          v-model="searchParams.isAvailable"
          placeholder="状态"
          style="width: 100px"
          clearable
        >
          <Option :value="1"> 启用</Option>
          <Option :value="0"> 禁用</Option>
        </Select>
        <Input
          v-model="searchParams.owner"
          placeholder="负责人"
          style="width: 100px"
        />
        <Input
          search
          v-model="searchParams.name"
          enter-button
          placeholder="输入标签名称搜索"
          style="width: 200px"
          @on-search="handleSearch"
        />
      </div>
      <Button type="primary" icon="md-add" @click="handleCreate">
        新增标签
      </Button>
    </div>
    <Table
      :columns="columns"
      :data="dataList"
      :loading="loading"
      style="margin-bottom: 16px"
    >
      <template slot-scope="{ row }" slot="isAvailable">
        {{ row.isAvailable ? "启用" : "禁用" }}
      </template>
      <template slot-scope="{ row }" slot="principalName">
        <Tag v-for="name of row.principalName.split(',')" :key="name">
          {{ name }}
        </Tag>
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
          :disabled="!row.isAvailable"
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
        <Button
          type="error"
          size="small"
          :disabled="!!row.refCount"
          @click="handleDelete(row.id)"
        >
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
  getLabelList,
  delLabel,
  switcLabelStatus,
} from "@/apps/dataModelCenter/service/api/labels";
import formatDate from "@/apps/dataModelCenter/utils/formatDate";
import EditModal from "./editModal.vue";

export default {
  filters: {formatDate},
  components: {EditModal},
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
    /**
     * 删除操作
     * @param id
     */
    handleDelete(id) {
      this.$Modal.confirm({
        title: "警告",
        content: "确定删除此项吗？",
        onOk: async () => {
          this.loading = true;
          await delLabel(id).catch(() => {
          });
          this.loading = false;
          await this.handleGetData();
        },
      });
    },
    /**
     * @description 编辑操作
     * @param id
     */
    handleEdit(id) {
      this.modalCfg = {
        visible: true,
        mode: "edit",
        id: id,
      };
    },
    /**
     * @description 启用
     * @param id
     * @returns {Promise<void>}
     */
    async handleEnable(id) {
      this.loading = true;
      await switcLabelStatus(id, 1);
      this.loading = false;
      await this.handleGetData(true);
    },
    /**
     * @description 禁用
     * @param id
     * @returns {Promise<void>}
     */
    async handleDisable(id) {
      this.loading = true;
      await switcLabelStatus(id, 0);
      this.loading = false;
      await this.handleGetData(true);
    },
    /**
     * @description 搜索
     */
    handleSearch() {
      this.handleGetData();
    },
    // 获取数据
    async handleGetData(changePage = false) {
      if (changePage === false && this.pageCfg.page !== 1) {
        return (this.pageCfg.page = 1);
      }
      this.loading = true;
      let {list, total} = await getLabelList({
        pageNum: this.pageCfg.page,
        pageSize: this.pageCfg.pageSize,
        name: this.searchParams.name,
        isAvailable: this.searchParams.isAvailable,
        owner: this.searchParams.owner,
      });
      this.loading = false;
      this.dataList = list;
      this.pageCfg.total = total;
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
      // 搜索参数
      searchParams: {
        name: "",
        owner: "",
        isAvailable: void 0,
      },
      // 表格列
      columns: [
        {
          title: "标签名",
          key: "name",
        },
        {
          title: "英文名",
          key: "fieldIdentifier",
        },
        {
          title: "主题",
          key: "warehouseThemeName",
        },
        {
          title: "状态",
          key: "isAvailable",
          slot: "isAvailable",
        },
        {
          title: "负责人",
          key: "owner"
        },
        {
          title: "可用角色",
          key: "principalName",
          slot: "principalName"
        },
        {
          title: "引用次数",
          key: "refCount",
        },
        {
          title: "描述",
          key: "comment",
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
          minWidth: 60,
        },
      ],
      // 数据列表
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
