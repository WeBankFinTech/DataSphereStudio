<template>
  <div class="page-content">
    <div style="margin-bottom: 16px">
      <div class="flex-row-center top-title">
        <Icon type="md-grid" color="#2d8cf0" :size="50" class="top-icon" />
        <span class="top-text"> 数据表管理 </span>
      </div>
      <div class="flex-row-center">
        <Input
          style="width: 600px"
          v-model="searchParams.searchToken"
          size="large"
          search
          @on-search="handleSearchTables"
          enter-button="搜索"
          placeholder="输入关键字进行搜索"
        >
          <Select
            v-model="searchParams.searchType"
            slot="prepend"
            style="width: 90px"
            placeholder="搜索类型"
          >
            <Option
              v-for="item of searchTypeOption"
              :key="item.value"
              :value="item.value"
            >
              {{ item.label }}
            </Option>
          </Select>
        </Input>
      </div>
    </div>
    <Row :gutter="15">
      <Col :span="5">
        <Button
          style="margin-bottom: 12px"
          type="primary"
          long
          @click="handleToCreate"
        >
          创建表
        </Button>
        <Card dis-hover style="margin-bottom: 12px">
          <div>
            <a @click="handleGetCollectionData">我的收藏</a>
          </div>
        </Card>
        <Card dis-hover style="margin-bottom: 12px">
          <p slot="title">数仓筛搜索</p>
          <div class="flex-row-center" style="margin-bottom: 12px">
            <div style="width: 75px">主题域：</div>
            <Select v-model="currentTheme" clearable>
              <Option
                v-for="theme in themeList"
                :key="theme.id"
                :value="theme.name"
                :label="theme.name"
              >
              </Option>
            </Select>
          </div>
          <div class="flex-row-center">
            <div style="width: 75px">分层：</div>
            <Select v-model="currentLayer" clearable>
              <Option
                v-for="layer in layerList"
                :key="layer.id"
                :value="layer.name"
                :label="layer.name"
              >
              </Option>
            </Select>
          </div>
        </Card>
        <Card dis-hover>
          <p slot="title">按库搜索</p>
          <Input v-model="searchDataBaseValue" placeholder="请输入数据库名称">
          </Input>
          <CellGroup @on-click="handleSearchTablesByDataBase">
            <Cell
              v-for="dataBase in filterDataBaseList"
              :key="dataBase.guid"
              :name="dataBase.name"
            >
              <Icon slot="icon" type="md-menu" :size="22" />
              {{ dataBase.name }}
            </Cell>
          </CellGroup>
        </Card>
      </Col>
      <Col :span="19">
        <div class="page-content searc-data-box">
          <div style="margin-bottom: 16px">
            <span class="title">搜索结果</span>
          </div>
          <div>
            <Table
              :columns="columns"
              :data="dataList"
              :loading="loading"
              style="margin-bottom: 16px"
            >
              <template slot="name" slot-scope="{ row }">
                <a
                  :title="row.name"
                  style="
                    overflow: hidden;
                    display: inline-block;
                    width: 100%;
                    text-overflow: ellipsis;
                  "
                  @click="handleToTableInfo(row.id, row.name, row.guid)"
                >
                  {{ row.name }}
                </a>
              </template>
              <template slot-scope="{ row }" slot="version">
                <Button
                  size="small"
                  @click="handleOpenVersionListModal(row.name)"
                >
                  版本列表
                </Button>
              </template>
              <template slot-scope="{ row }" slot="createTime">
                {{ row.createTime | formatDate }}
              </template>
              <template slot-scope="{ row }" slot="updateTime">
                {{ row.updateTime | formatDate }}
              </template>
              <template slot="action">
                <Button
                  size="small"
                  @click="handleEdit(row.id, row.name, row.guid)"
                  style="margin-right: 5px"
                >
                  编辑
                </Button>
                <Button
                  size="small"
                  @click="handleDelete(row.id)"
                  style="margin-right: 5px"
                >
                  删除
                </Button>
              </template>
            </Table>
          </div>
          <div class="page-line">
            <Page
              v-show="pageCfg.visible"
              :total="pageCfg.total"
              :current.sync="pageCfg.page"
              :page-size="pageCfg.pageSize"
            />
          </div>
        </div>
      </Col>
    </Row>
    <!-- 版本列表 -->
    <VersionListModal
      v-model="versionListCfg.visible"
      :name="versionListCfg.name"
      @finish="handleModalFinish"
      @open="handleShowVersion"
    />
    <!-- 某个版本查看弹窗 -->
    <ShowVersion v-model="versionCfg.visible" :bodyData="versionCfg.bodyData" />
  </div>
</template>

<script>
import {
  searchTable,
  getCollectList,
  getDataBasesList,
  getThemesList,
  getLayersList,
} from "../../../service/tableManageApi";
import formatDate from "../../../utils/formatDate";
import storage from "@/common/helper/storage";
import VersionListModal from "./versionListModal.vue";
import ShowVersion from "./showVersion.vue";
export default {
  filters: { formatDate },
  components: { VersionListModal, ShowVersion },
  data() {
    return {
      // 版本列表
      versionListCfg: {
        visible: false,
        name: "",
      },
      // 查看版本
      versionCfg: {
        visible: false,
        bodyData: {},
      },
      // 搜索类型列表
      searchTypeOption: [
        {
          label: "按表名",
          value: 1,
        },
        {
          label: "按指标名",
          value: 2,
        },
        {
          label: "按维度名",
          value: 3,
        },
        {
          label: "按度量名",
          value: 4,
        },
        {
          label: "按表描述",
          value: 5,
        },
      ],
      // 数据库搜索
      searchDataBaseValue: "",
      // 输入框搜索
      searchParams: {
        // 搜索词
        searchToken: "",
        // 搜索类型
        searchType: 1,
      },
      // 当前选择的主题
      currentTheme: "",
      // 当前选择分层
      currentLayer: "",
      // 主题列表
      themeList: [],
      // 分层列表
      layerList: [],
      // 数据库列表
      dataBaseList: [],
      // 数据列表
      dataList: [],
      // 表格列
      columns: [
        {
          title: "表名",
          key: "name",
          slot: "name",
          width: 120,
          fixed: "left",
          ellipsis: true,
        },
        {
          title: "表别名",
          key: "alias",
          align: "center",
          width: 120,
        },
        {
          title: "库",
          key: "dataBase",
          align: "center",
          width: 120,
        },
        {
          title: "存储类型",
          key: "storageType",
          align: "center",
          width: 120,
        },
        {
          title: "表大小",
          key: "size",
          align: "center",
          width: 120,
        },
        {
          title: "属主",
          key: "creator",
          align: "center",
          width: 120,
        },
        {
          title: "数仓属性",
          key: "warehouseAttr",
          align: "center",
          width: 120,
        },
        {
          title: "版本",
          key: "version",
          slot: "version",
          align: "center",
          width: 120,
        },
        {
          title: "更新时间",
          key: "updateTime",
          slot: "updateTime",
          align: "center",
          width: 120,
        },
        {
          title: "创建时间",
          key: "createTime",
          slot: "createTime",
          align: "center",
          width: 200,
        },
        {
          title: "描述",
          align: "center",
          key: "comment",
          width: 200,
        },
        {
          title: "操作",
          slot: "action",
          align: "center",
          width: 140,
        },
      ],
      // 是否加载中
      loading: false,
      // 分页
      pageCfg: {
        visible: true,
        page: 1,
        pageSize: 10,
        total: 10,
      },
    };
  },
  computed: {
    // 过滤库
    filterDataBaseList() {
      if (this.searchDataBaseValue === "") {
        return this.dataBaseList;
      } else {
        return this.dataBaseList.filter((item) => {
          return item.name.indexOf(this.searchDataBaseValue) !== -1;
        });
      }
    },
  },
  watch: {
    "pageCfg.page"() {
      this.handleSearchTables(true);
    },
  },
  mounted() {
    // 分层
    this.handleGetThemesData();
    // 主题
    this.handleGetLayersData();
    // 数据库
    this.handleGetDataBaseData();
  },
  methods: {
    // 表单完成回调
    handleModalFinish() {
      this.handleSearchTables(true);
    },
    // 打开版本列表
    handleOpenVersionListModal(name) {
      this.versionListCfg = {
        visible: true,
        name: name,
      };
    },
    // 删除
    handleDelete(id) {
      this.$Modal.confirm({
        title: "警告",
        content: "确定删除此项吗？" + id,
        onOk: () => {
          this.$Message.info("删除");
        },
        onCancel: () => {
          this.$Message.info("取消");
        },
      });
    },
    // 处理搜索
    handleSearchTables(changePage = false) {
      this.pageCfg.visible = true;
      if (changePage === false && this.pageCfg.page !== 1) {
        return (this.pageCfg.page = 1);
      }
      this.loading = true;
      searchTable({
        name: this.searchParams.searchToken,
        warehouseLayerName: this.currentLayer,
        warehouseThemeName: this.currentTheme,
        pageSize: this.pageCfg.pageSize,
        pageNum: this.pageCfg.page,
      }).then((res) => {
        this.loading = false;
        this.dataList = res.list;
        this.pageCfg.total = res.total;
      });
    },
    // 处理获取收藏列表
    async handleGetCollectionData() {
      let userName = storage.get("baseInfo", "local").username;
      this.loading = true;
      let { list } = await getCollectList(userName);
      this.loading = false;
      this.dataList = list;
      this.pageCfg.visible = false;
    },
    // 处理获取所有数据库
    async handleGetDataBaseData() {
      this.loading = true;
      let { list } = await getDataBasesList();
      this.loading = false;
      this.dataBaseList = list;
    },
    // 处理获取主题
    async handleGetThemesData() {
      this.loading = true;
      let { list } = await getThemesList();
      this.loading = false;
      this.themeList = list;
    },
    // 处理获取分层
    async handleGetLayersData() {
      this.loading = true;
      let { list } = await getLayersList();
      this.loading = false;
      this.layerList = list;
    },
    // 根据数据库名搜索表
    handleSearchTablesByDataBase(name) {
      this.searchParams.searchToken = `${name}.`;
      this.handleSearchTables();
    },
    // 打开查看版本信息
    handleShowVersion(data) {
      this.versionCfg = {
        visible: true,
        bodyData: data,
      };
    },
    // 创建表
    handleToCreate() {
      this.$router.push({
        path: `/datamodelcenter/tableManage/tableEditor`,
        query: { mode: "create" },
      });
    },
    // 去往编辑
    handleEdit(id, name, guid) {
      this.$router.push({
        path: `/datamodelcenter/tableManage/tableEditor`,
        query: {
          mode: id ? "update" : "create",
          id: id,
          name: name,
          guid: guid,
        },
      });
    },
    // 去往表详情
    handleToTableInfo(id, name, guid) {
      this.$router.push({
        path: `/datamodelcenter/tableManage/tableInfo`,
        query: { id, name, guid },
      });
    },
  },
};
</script>

<style lang="scss" scoped>
@import "../../../assets/styles/common.scss";
.flex-row-center {
  display: flex;
  justify-content: center;
  align-items: center;
}
.top-title {
  margin-bottom: 16px;
  .top-icon {
    margin-right: 16px;
  }
  .top-text {
    font-size: 28px;
  }
}
.searc-data-box {
  border: 1px solid #e8eaec;
  .title {
    font-size: 18px;
    border-bottom: 3px solid #3c7aff;
  }
}
.page-line {
  display: flex;
  justify-content: flex-end;
}
</style>
