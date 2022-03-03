<template>
  <div>
    <div>
      <div class="title-line">
        <span class="title">
          <span>系统预置分层 </span>
        </span>
      </div>
      <Table
        :columns="columns"
        :data="presetDataList"
        :loading="presetloading"
        style="margin-bottom: 16px"
      >
        <template slot-scope="{ row }" slot="isAvailable">
          {{ row.isAvailable ? "启用" : "禁用" }}
        </template>
        <template slot-scope="{ row }" slot="createTime">
          {{ row.createTime | formatDate }}
        </template>
        <template slot-scope="{ row }" slot="updateTime">
          {{ row.updateTime | formatDate }}
        </template>
        <template slot-scope="{ row }" slot="principalName">
          <Tag v-for="name of row.principalName.split(',')" :key="name">
            {{ name }}
          </Tag>
        </template>
        <template slot-scope="{ row }" slot="dbs">
          <Tag v-for="name of row.dbs.split(',')" :key="name">
            {{ name }}
          </Tag>
        </template>
        <template slot-scope="{ row }" slot="action">
          <Button
            size="small"
            @click="handleDisable(0, row.id)"
            style="margin-right: 5px"
            v-if="row.isAvailable"
          >
            禁用
          </Button>
          <Button
            type="primary"
            size="small"
            @click="handleEnable(0, row.id)"
            style="margin-right: 5px"
            v-else
          >
            启用
          </Button>
        </template>
      </Table>
    </div>
    <div>
      <div class="title-line">
        <span class="title">
          <span>自定义分层 </span>
        </span>
        <Button type="primary" icon="md-add" @click="handleCreate">
          创建自定义分层
        </Button>
      </div>
      <Table
        :columns="columns"
        :data="customDataList"
        :loading="customloading"
        style="margin-bottom: 16px"
      >
        <template slot-scope="{ row }" slot="isAvailable">
          {{ row.isAvailable ? "启用" : "禁用" }}
        </template>
        <template slot-scope="{ row }" slot="createTime">
          {{ row.createTime | formatDate }}
        </template>
        <template slot-scope="{ row }" slot="updateTime">
          {{ row.updateTime | formatDate }}
        </template>
        <template slot-scope="{ row }" slot="principalName">
          <Tag v-for="name of row.principalName.split(',')" :key="name">
            {{ name }}
          </Tag>
        </template>
        <template slot-scope="{ row }" slot="dbs">
          <Tag v-for="name of row.dbs.split(',')" :key="name">
            {{ name }}
          </Tag>
        </template>
        <template slot-scope="{ row }" slot="action">
          <Button
            size="small"
            style="margin-right: 5px"
            :disabled="!row.isAvailable"
            @click="handleEdit(1, row.id)"
          >
            编辑
          </Button>
          <Button
            size="small"
            @click="handleDisable(1, row.id)"
            style="margin-right: 5px"
            v-if="row.isAvailable"
          >
            禁用
          </Button>
          <Button
            type="primary"
            size="small"
            @click="handleEnable(1, row.id)"
            style="margin-right: 5px"
            v-else
          >
            启用
          </Button>
          <Button
            type="error"
            size="small"
            :disabled="!!row.referenceCount"
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
    </div>
    <EditModal
      v-model="modalCfg.visible"
      :id="modalCfg.id"
      :mode="modalCfg.mode"
      :type="modalCfg.type"
      @finish="handleModalFinish"
    />
  </div>
</template>

<script>
import {
  getLayersPreset,
  getLayersCustom,
  deleteLayers,
  enableLayers,
  disableLayers,
} from "@/apps/dataWarehouseDesign/service/api/layer";
import EditModal from "./editModal.vue";
import formatDate from "@/apps/dataWarehouseDesign/utils/formatDate";

export default {
  components: { EditModal },
  filters: { formatDate },
  methods: {
    /**
     * 弹窗回调
     * @param type
     * @returns {void}
     */
    handleModalFinish(type) {
      if (type === 0) return this.handleGetLayersPreset();
      if (type === 1) return this.handleGetLayersCustom(true);
    },
    /**
     * 创建操作
     */
    handleCreate() {
      this.modalCfg = {
        visible: true,
        mode: "create",
        type: 1,
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
          this.customloading = true;
          await deleteLayers(id).catch(() => {});
          this.customloading = false;
          this.handleGetLayersCustom();
        },
      });
    },
    /**
     * 打开编辑弹窗
     * @param type
     * @param id
     */
    handleEdit(type, id) {
      this.modalCfg = {
        visible: true,
        mode: "edit",
        id: id,
        type: type,
      };
    },
    /**
     * 启用
     * @param type
     * @param id
     * @returns {void}
     */
    async handleEnable(type, id) {
      this.loading = true;
      await enableLayers(id);
      this.loading = false;
      if (type === 0) return this.handleGetLayersPreset();
      if (type === 1) return this.handleGetLayersCustom(true);
    },
    /**
     * 停用
     * @param type
     * @param id
     * @returns {void}
     */
    async handleDisable(type, id) {
      this.loading = true;
      await disableLayers(id);
      this.loading = false;
      if (type === 0) return this.handleGetLayersPreset();
      if (type === 1) return this.handleGetLayersCustom(true);
    },
    /**
     * 获取预设分层
     * @returns {void}
     */
    async handleGetLayersPreset() {
      this.presetloading = true;
      let { list } = await getLayersPreset();
      this.presetloading = false;
      this.presetDataList = list;
    },
    /**
     * 获取自定义分层
     * @returns {void}
     */
    async handleGetLayersCustom(changePage = false) {
      if (changePage === false && this.pageCfg.page !== 1) {
        return (this.pageCfg.page = 1);
      }
      this.customloading = true;
      let data = await getLayersCustom({
        page: this.pageCfg.page,
        size: this.pageCfg.pageSize
      });
      this.customloading = false;
      let { items, total } = data.page;
      this.pageCfg.total = total;
      this.customDataList = items;
    },
  },
  mounted() {
    this.handleGetLayersPreset();
    this.handleGetLayersCustom();
  },
  watch: {
    "pageCfg.page"() {
      this.handleGetLayersCustom(true);
    },
  },
  data() {
    return {
      columns: [
        {
          title: "分层名称",
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
          title: "分层选择权限",
          key: "principalName",
          slot: "principalName",
        },
        {
          title: "引用次数",
          key: "referenceCount",
        },
        {
          title: "可用库",
          key: "dbs",
          slot: "dbs",
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
          key: "action",
          slot: "action",
          minWidth: 60,
        },
      ],
      // 自定义分层加载中
      customloading: false,
      // 自定义分数据
      customDataList: [],
      // 预设分层加载中
      presetloading: false,
      // 预设分层数据
      presetDataList: [],
      // 弹窗参数
      modalCfg: {
        mode: "",
        id: NaN,
        visible: false,
        type: NaN,
      },
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
.title-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .title {
    border-left: 6px solid #1890ff;
    padding-left: 6px;
  }
}

.page-line {
  display: flex;
  justify-content: flex-end;
}
</style>
