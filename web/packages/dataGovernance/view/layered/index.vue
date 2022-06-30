<template>
  <div class="console-page">
    <div>
      <div class="title-line">
        <span class="title">
          <span>系统预置分层 </span>
        </span>
      </div>
      <Table
        :columns="columns"
        :data="presetDataList"
        :loading="preSetloading"
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
            @click="handleEdit(row.name)"
            disabled
          >
            编辑
          </Button>
          <!--<Button
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
          </Button>-->
        </template>
      </Table>
    </div>
    <div>
      <div class="title-line">
        <span class="title">
          <span>自定义分层 </span>
        </span>
        <Input
          search
          enter-button
          clearable
          placeholder="输入名称搜索"
          style="width: 300px; float: right"
          v-model="searchVal"
          @on-search="handleSearch"
          @on-clear="handleSearch"
        />
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
            @click="handleEdit(row.name)"
          >
            编辑
          </Button>
          <!--<Button
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
          </Button>-->
          <Button type="error" size="small" @click="handleDelete(row.name)">
            删除
          </Button>
        </template>
      </Table>
    </div>
    <EditModal
      v-model="modalCfg.visible"
      :id="modalCfg.name"
      :mode="modalCfg.mode"
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
  disableLayers
} from "../../service/api";
import EditModal from "./editModal.vue";
import formatDate from "../../utils/formatDate";
export default {
  components: { EditModal },
  filters: {
    formatDate
  },
  methods: {
    handleSearch() {
      this.handleGetLayersCustom()
    },
    handleModalFinish() {
      this.handleGetLayersCustom();
    },
    handleCreate() {
      this.modalCfg = {
        visible: true,
        mode: "create"
      };
    },
    async handleDelete(name) {
      this.customloading = true;
      await deleteLayers(name);
      this.customloading = false;
      this.handleGetLayersCustom();
    },
    handleEdit(name) {
      this.modalCfg = {
        visible: true,
        mode: "edit",
        name
      };
    },
    async handleEnable(id) {
      this.loading = true;
      await enableLayers(id);
      this.loading = false;
      this.handleGetLayersCustom();
    },
    async handleDisable(id) {
      this.loading = true;
      await disableLayers(id);
      this.loading = false;
      this.handleGetLayersCustom();
    },
    async handleGetLayersPreset() {
      this.preSetloading = true;
      let { result } = await getLayersPreset();
      this.preSetloading = false;
      this.presetDataList = result;
    },
    async handleGetLayersCustom() {
      this.customloading = true;
      let data = await getLayersCustom(this.searchVal.trim());
      this.customloading = false;
      let { result } = data;
      this.customDataList = result;
    }
  },
  mounted() {
    this.handleGetLayersPreset();
    this.handleGetLayersCustom();
  },
  watch: {},
  data() {
    return {
      columns: [
        {
          title: "分层名称",
          key: "name"
        },
        /*{
          title: '英文名',
          key: 'enName',
        },
        {
          title: '状态',
          key: 'isAvailable',
          slot: 'isAvailable',
        },*/
        {
          title: "描述",
          key: "description",
          ellipsis: true
        },
        /*{
          title: '分层选择权限',
          key: 'principalName',
          slot: 'principalName',
        },
        {
          title: '可用库',
          key: 'dbs',
          slot: 'dbs',
        },*/
        {
          title: "创建时间",
          key: "createTime",
          slot: "createTime"
        },
        {
          title: "更新时间",
          key: "updateTime",
          slot: "updateTime"
        },
        {
          title: "操作",
          key: "action",
          slot: "action",
          minWidth: 60
        }
      ],
      customloading: false,
      customDataList: [],
      preSetloading: false,
      presetDataList: [],
      // 弹窗参数
      modalCfg: {
        mode: "",
        name: "",
        visible: false
      },
      searchVal: ''
    };
  }
};
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.console-page{
  margin: 0 24px;
}
.title-line {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  .title {
    border-left: 6px solid #1890ff;
    @include border-color(#1890ff, $dark-border-color-base);
    padding-left: 6px;
  }
}
.page-line {
  display: flex;
  justify-content: flex-end;
}
</style>
