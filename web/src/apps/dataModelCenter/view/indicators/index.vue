<template>
  <div class="page-content">
    <div class="top-line">
      <div style="display: flex">
        <Select
          v-model="searchParams.isAvailable"
          placeholder="状态"
          style="width: 100px"
          clearable
        >
          <Option :value="1"> 启用 </Option>
          <Option :value="0"> 禁用 </Option>
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
          placeholder="指标名称"
          style="width: 200px"
          @on-search="handleSearch"
        />
      </div>
      <Button type="primary" icon="md-add" @click="handleCreate">
        新增指标
      </Button>
    </div>
    <Table
      :columns="columns"
      :data="datalist"
      :loading="loading"
      style="margin-bottom: 16px"
    >
      <template slot-scope="{ row }" slot="name">
        {{ row.name }}
        <Tag style="display: inline" color="primary">
          <span @click="handleOpenVersionList(row.name)">
            {{ row.version }}
          </span>
        </Tag>
      </template>
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
        :current="pageCfg.page"
        :page-size="pageCfg.pageSize"
        @on-change="changePage"
      />
    </div>
    <EditModal
      v-model="modalCfg.visible"
      :id="modalCfg.id"
      :mode="modalCfg.mode"
      @finish="handleModalFinish"
    />
     <VersionListModal
      v-model="versionListCfg.visible"
      :name="versionListCfg.name"
    />
  </div>
</template>

<script>
import { getIndicators, switcIndicatorsStatus } from "../../service/api";
import formatDate from "../../utils/formatDate";
import EditModal from "./editModal.vue";
import VersionListModal from "./versionListModal.vue";
export default {
  components: { EditModal, VersionListModal },
  methods: {
    handleModalFinish() {
      this.handleGetData();
    },
    handleCreate() {
      this.modalCfg = {
        visible: true,
        mode: "create",
      };
    },
    async handleDelete(id) {
      alert("删除" + id);
    },
    handleEdit(id) {
      this.modalCfg = {
        visible: true,
        mode: "edit",
        id,
      };
    },
    async handleEnable(id) {
      this.loading = true;
      await switcIndicatorsStatus(id, 1);
      this.loading = false;
      this.handleGetData();
    },
    async handleDisable(id) {
      this.loading = true;
      await switcIndicatorsStatus(id, 0);
      this.loading = false;
      this.handleGetData();
    },
    handleSearch() {
      this.pageCfg.page = 1;
      this.handleGetData();
    },
    async handleGetData() {
      this.loading = true;
      const { list, total } = await getIndicators(
        this.pageCfg.page,
        this.pageCfg.pageSize,
        this.searchParams.isAvailable,
        this.searchParams.owner,
        this.searchParams.name
      );
      this.loading = false;
      this.datalist = list;
      this.pageCfg.total = total;
    },
    changePage(page) {
      this.pageCfg.page = page;
    },
    handleOpenVersionList(name) {
      this.versionListCfg = {
        visible: true,
        name: name,
      };
    },
  },
  filters: {
    formatDate,
  },
  mounted() {
    this.handleGetData();
  },
  watch: {
    pageCfg: {
      handler: "handleGetData",
      deep: true,
    },
  },

  data() {
    return {
      searchParams: {
        name: "",
        isAvailable: undefined,
        owner: "",
      },
      columns: [
        {
          title: "指标名称",
          key: "name",
        },
        {
          title: "字段标识",
          key: "fieldIdentifier",
        },
        {
          title: "状态",
          key: "isAvailable",
          slot: "isAvailable",
        },
        {
          title: "负责人",
          key: "owner",
        },
        {
          title: "所属主题",
          key: "warehouseThemeName",
        },
        {
          title: "描述",
          key: "comment",
          ellipsis: true,
        },
        {
          title: "被引用次数",
          key: "refCount",
        },
        {
          title: "选择权限",
          key: "principalName",
          slot: "principalName",
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
      datalist: [],
      // 弹窗参数
      modalCfg: {
        mode: "",
        id: NaN,
        visible: false,
      },
      // 弹窗参数
      versionListCfg: {
        visible: false,
        name: "",
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
@import "../../assets/styles/common.scss";

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
