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
          placeholder="输入指标名称搜索"
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
      <template slot-scope="{ row }" slot="version">
        <Tag style="display: inline" color="primary">
          <span @click="handleOpenVersionList(row.name)">
            V{{ row.version }}
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
      <template slot-scope="{ row }" slot="indicatorType">
        {{ row.indicatorType === 0 ? "原子指标" : "" }}
        {{ row.indicatorType === 1 ? "衍生指标" : "" }}
        {{ row.indicatorType === 2 ? "派生指标" : "" }}
        {{ row.indicatorType === 3 ? "复杂指标" : "" }}
        {{ row.indicatorType === 4 ? "自定义指标" : "" }}
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
    <!-- 编辑弹窗 -->
    <EditModal
      v-model="modalCfg.visible"
      :id="modalCfg.id"
      :mode="modalCfg.mode"
      @finish="handleModalFinish"
    />
    <!-- 版本列表 -->
     <VersionListModal
      v-model="versionListCfg.visible"
      :name="versionListCfg.name"
      @finish="handleModalFinish"
      @open="handleShowVersion"
    />
    <!-- 某个版本查看弹窗 -->
    <ShowVersionModal
      v-model="versionCfg.visible"
      :bodyData="versionCfg.bodyData"
    />
  </div>
</template>

<script>
import {
  getIndicators,
  switcIndicatorsStatus,
  delIndicators,
} from "@/apps/dataModelCenter/service/api/indicators";
import formatDate from "@/apps/dataModelCenter/utils/formatDate";
import EditModal from "./editModal.vue";
import VersionListModal from "./versionListModal.vue";
import ShowVersionModal from "./showVersionModal.vue";

export default {
  components: { EditModal, VersionListModal, ShowVersionModal },
  filters: { formatDate },
  methods: {
    // 表单完成回调
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
          await delIndicators(id).catch(() => {});
          this.loading = false;
          this.handleGetData();
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
      await switcIndicatorsStatus(id, 1);
      this.loading = false;
      this.handleGetData(true);
    },
    // 禁用
    async handleDisable(id) {
      this.loading = true;
      await switcIndicatorsStatus(id, 0);
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
      const { list, total } = await getIndicators({
        pageNum: this.pageCfg.page,
        pageSize: this.pageCfg.pageSize,
        isAvailable: this.searchParams.isAvailable,
        owner: this.searchParams.owner,
        name: this.searchParams.name,
      });
      this.loading = false;
      this.datalist = list;
      this.pageCfg.total = total;
    },
    // 查看单个版本详细信息
    handleShowVersion(data) {
      this.versionCfg = {
        visible: true,
        bodyData: JSON.parse(data.versionContext),
      };
    },
    // 打开版本列表
    handleOpenVersionList(name) {
      this.versionListCfg = {
        visible: true,
        name: name,
      };
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
          title: "版本",
          key: "version",
          slot: "version",
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
          title: "指标类型",
          key: "indicatorType",
          slot: "indicatorType",
        },
        {
          title: "负责人",
          key: "owner",
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
          minWidth: 80,
        },
      ],
      // 数据列表
      datalist: [],
      // 编辑弹窗参数
      modalCfg: {
        mode: "",
        id: NaN,
        visible: false,
      },
      // 版本列表参数
      versionListCfg: {
        visible: false,
        name: "",
      },
      // 某个版本详情弹框
      versionCfg: {
        visible: false,
        bodyData: {},
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
