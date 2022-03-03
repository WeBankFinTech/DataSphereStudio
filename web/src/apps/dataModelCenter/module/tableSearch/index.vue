<template>
  <div>
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
          @on-search="handleSearchAction"
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
          style="margin-bottom: 15px"
          type="primary"
          long
          @click="handleToCreateAction"
        >
          创建表
        </Button>
        <Card dis-hover style="margin-bottom: 15px">
          <div>
            <a @click="handleGetCollectionAction">我的收藏</a>
          </div>
        </Card>
        <Card dis-hover style="margin-bottom: 15px">
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
          <Input v-model="searchDataBaseValue" placeholder="请输入数据库名称" />
          <CellGroup @on-click="handleSearchTablesByDataBaseAction">
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
        <div class="searc-data-box">
          <div style="margin-bottom: 16px">
            <span class="title">
              {{ getDataMode === 0 ? "搜索结果" : "" }}
              {{ getDataMode === 1 ? "收藏列表" : "" }}
            </span>
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
              <template slot-scope="{ row }" slot="warehouseAttr">
                <p>主题：{{ row.warehouseThemeName }}</p>
                <p>分层：{{ row.warehouseLayerName }}</p>
              </template>
              <template slot-scope="{ row }" slot="createTime">
                {{ row.createTime | formatDate }}
              </template>
              <template slot-scope="{ row }" slot="updateTime">
                {{ row.updateTime | formatDate }}
              </template>
              <template slot-scope="{ row }" slot="action">
                <Button
                  v-if="getDataMode === 0"
                  size="small"
                  @click="handleEdit(row.id, row.name, row.guid)"
                  style="margin-right: 5px"
                >
                  编辑
                </Button>
                <Button
                  v-if="row.id"
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
  </div>
</template>

<script>
import {
  searchTable,
  getCollectList,
  getDataBasesList,
  deleteTableById,
} from "@/apps/dataModelCenter/service/api/tableManage";
import {
  getThemesList,
  getLayersList,
} from "@/apps/dataModelCenter/service/api/common";
import formatDate from "@/apps/dataModelCenter/utils/formatDate";
import VersionListModal from "./versionListModal.vue";
import mixin from "@/common/service/mixin";

export default {
  filters: { formatDate },
  components: { VersionListModal },
  mixins: [mixin],
  data() {
    return {
      // 获取数据的方式 0 搜索 1 收藏数据
      getDataMode: 0,
      // 版本列表弹窗
      versionListCfg: {
        visible: false,
        name: "",
      },
      // 搜索类型列表
      searchTypeOption: [
        {
          label: "按逻辑表名",
          value: 1,
        },
        {
          label: "按物理表名",
          value: 2,
        },
        {
          label: "按指标名",
          value: 3,
        },
        {
          label: "按维度名",
          value: 4,
        },
        {
          label: "按度量名",
          value: 5,
        },
      ],
      // 数据库搜索关键字
      searchDataBaseValue: "",
      // 输入框搜索参数
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
          slot: "warehouseAttr",
          align: "center",
          width: 190,
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
        page: 1,
        pageSize: 10,
        total: 10,
      },
      allTotal: 10,
    };
  },
  computed: {
    /**
     * @description 过滤库
     * @returns {[]|*[]}
     */
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
    // 监听分页变化获取数据
    "pageCfg.page"() {
      if (this.getDataMode === 0) {
        this.handleSearchTables(true);
      }
      if (this.getDataMode === 1) {
        this.handleGetCollectionData(true);
      }
    },
  },
  mounted() {
    // 获取主题
    this.handleGetThemesData();
    // 获取分层
    this.handleGetLayersData();
    // 获取数据库列表
    this.handleGetDataBaseData();
    // 执行默认搜搜
    this.handleSearchTables(true);
  },
  methods: {
    /**
     * @description 版本弹窗完成回调，重新获取数据
     */
    handleModalFinish() {
      this.handleSearchTables(true);
    },
    /**
     * @description 打开版本列表
     * @param name
     */
    handleOpenVersionListModal(name) {
      this.versionListCfg = {
        visible: true,
        name: name,
      };
    },
    /**
     * @description 搜索按钮动作
     */
    handleSearchAction() {
      // 设置模式为搜索
      this.getDataMode = 0;
      // 获取数据
      this.handleSearchTables(false);
    },
    /**
     * @description 查看收藏动作
     */
    handleGetCollectionAction() {
      // 设置获取数据方式为收藏
      this.getDataMode = 1;
      // 获取数据
      this.handleGetCollectionData(false);
    },
    /**
     * @description 删除
     * @param id
     */
    handleDelete(id) {
      this.$Modal.confirm({
        title: "警告",
        content: "确定删除此项吗？",
        onOk: async () => {
          this.loading = true;
          await deleteTableById(id).catch(() => {});
          this.loading = false;
          this.handleSearchTables();
        },
      });
    },
    /**
     * @description 获取搜索数据
     * @param changePage
     * @returns {number}
     */
    handleSearchTables(changePage = false) {
      if (changePage === false && this.pageCfg.page !== 1) {
        return (this.pageCfg.page = 1);
      }
      this.loading = true;
      let modelName;
      let name;
      let tableType;
      let modelType;
      switch (this.searchParams.searchType) {
        // 逻辑表
        case 1:
          name = this.searchParams.searchToken;
          break;
        // 物理表
        case 2:
          name = this.searchParams.searchToken;
          tableType = 1;
          break;
        // 指标
        case 3:
          modelName = this.searchParams.searchToken;
          modelType = 1;
          break;
        // 维度
        case 4:
          modelName = this.searchParams.searchToken;
          modelType = 0;
          break;
        // 度量
        case 5:
          modelName = this.searchParams.searchToken;
          modelType = 2;
          break;
      }
      searchTable({
        modelType: modelType, //0 维度 1 指标 2 度量
        modelName: modelName, //模型名称
        tableType: tableType,
        name: name,
        warehouseLayerName: this.currentLayer,
        warehouseThemeName: this.currentTheme,
        pageSize: this.pageCfg.pageSize,
        pageNum: this.pageCfg.page,
      }).then((res) => {
        this.loading = false;
        this.dataList = res.list;
        this.pageCfg.total = res.total;
        // 物理表查询 后端获取不到total 前端处理
        /**
         * 返回total 大于等于 当前页显示条数时 动态计算总页数：（当前页码 + 1） * 当前页需要显示条数
         * 创建变量存储一下当前总页数 如果小于的时候就等于此变量
         */
        if (this.searchParams.searchType === 2) {
          if (res.total >= this.pageCfg.pageSize) {
            this.pageCfg.total =
              (this.pageCfg.page + 1) * this.pageCfg.pageSize;
            this.allTotal = (this.pageCfg.page + 1) * this.pageCfg.pageSize;
          } else {
            this.pageCfg.total = this.allTotal;
          }
        }
      });
    },

    /**
     * @description 获取收藏列表
     * @returns {any}
     */
    async handleGetCollectionData(changePage = false) {
      if (changePage === false && this.pageCfg.page !== 1) {
        return (this.pageCfg.page = 1);
      }
      this.loading = true;
      let { list, total } = await getCollectList();
      this.loading = false;
      this.dataList = list;
      this.pageCfg.total = total;
    },
    /**
     * @description 获取所有数据库
     * @returns {Promise<void>}
     */
    async handleGetDataBaseData() {
      this.loading = true;
      let { list } = await getDataBasesList();
      this.loading = false;
      this.dataBaseList = list;
    },
    /**
     * @description 获取主题
     * @returns {Promise<void>}
     */
    async handleGetThemesData() {
      this.loading = true;
      let { list } = await getThemesList();
      this.loading = false;
      this.themeList = list;
    },
    /**
     * @description 获取分层
     * @returns {Promise<void>}
     */
    async handleGetLayersData() {
      this.loading = true;
      let { list } = await getLayersList();
      this.loading = false;
      this.layerList = list;
    },
    /**
     * @description 根据数据库名搜索表
     * @param name {String} dbName
     */
    handleSearchTablesByDataBaseAction(name) {
      this.getDataMode = 0;
      this.searchParams.searchToken = `${name}.`;
      this.handleSearchTables();
    },
    /**
     * @description 打开某个版本信息
     * @param data {Object} 表数据
     */
    handleShowVersion(data) {
      window.sessionStorage.setItem("_tableVersionInfo", JSON.stringify(data));
      this.$router.push("/datamodelcenter/tableManage/tableVersionInfo");
    },
    /**
     * @description 去往创建表
     */
    handleToCreateAction() {
      this.$router.push({
        path: `/datamodelcenter/tableManage/tableEditor`,
        query: { mode: "create" },
      });
    },
    /**
     * @description 去往编辑
     * @param id
     * @param name
     * @param guid
     */
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
    /**
     * @description 去往表详情
     * @param id
     * @param name
     * @param guid
     */
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
  padding: 20px;
  box-sizing: border-box;

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
