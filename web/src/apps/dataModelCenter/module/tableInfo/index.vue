<template>
  <div style="position: relative">
    <Spin v-if="loading" fix></Spin>
    <div>
      <Card dis-hover>
        <div slot="title">
          <Icon type="ios-home" :size="24" />
          <span> 表管理 </span>
          /
          <span>{{ generalData.name }}</span>
          &nbsp;
          <Tag color="primary">V{{ generalData.version || 0 }}</Tag>
        </div>
        <div slot="extra">
          <Icon
            type="ios-star"
            :size="24"
            @click="handleSwitchCollect"
            :color="isCollect ? '#3399ff' : '#657180'"
          />
          <Icon
            type="ios-copy"
            :size="24"
            color="#3399ff"
            @click="handleCopyTable"
          />
          <Icon
            type="ios-create"
            :size="24"
            color="#3399ff"
            @click="handleToEditor"
          />
          <!-- <Dropdown style="margin-left: 20px">
            <Button type="primary"> 更多 </Button>
            <DropdownMenu slot="list">
              <DropdownItem>查看授权</DropdownItem>
              <DropdownItem>生成api</DropdownItem>
              <DropdownItem>导出表</DropdownItem>
            </DropdownMenu>
          </Dropdown> -->
        </div>
        <p style="margin-bottom: 16px">
          描述： {{ generalData.comment || "无" }}
        </p>
        <p>
          <Tag v-for="label in baseicData.label" :key="label" color="blue">
            {{ label }}
          </Tag>
        </p>
      </Card>
    </div>
    <Tabs v-model="currentTab">
      <TabPane label="基本信息" name="baseicTab">
        <Card :bordered="false" title="基本信息" dis-hover>
          <Row>
            <Col span="12">
              <Row>
                <Col span="7">表名：</Col>
                <Col span="17">{{ baseicData.name }}</Col>
              </Row>
            </Col>
            <Col span="12">
              <Row>
                <Col span="7">表别名：</Col>
                <Col span="17">{{ baseicData.alias }}</Col>
              </Row>
            </Col>
            <Col span="12">
              <Row>
                <Col span="7">创建人：</Col>
                <Col span="17">{{ baseicData.creator }}</Col>
              </Row>
            </Col>
            <Col span="12">
              <Row>
                <Col span="7">路径：</Col>
                <Col span="17">{{ baseicData.location }}</Col>
              </Row>
            </Col>
            <Col span="12">
              <Row>
                <Col span="7">所属库：</Col>
                <Col span="17">{{ baseicData.dataBase }}</Col>
              </Row>
            </Col>
            <Col span="12" offset="12"></Col>
            <Col span="12">
              <Row>
                <Col span="7">所属分层：</Col>
                <Col span="17">{{ baseicData.warehouseLayerName }}</Col>
              </Row>
            </Col>
            <Col span="12">
              <Row>
                <Col span="7">主题域：</Col>
                <Col span="17">{{ baseicData.warehouseThemeName }}</Col>
              </Row>
            </Col>
            <Col span="12">
              <Row>
                <Col span="7">标签：</Col>
                <Col span="17">
                  <Tag v-for="label in baseicData.label" :key="label">
                    {{ label }}
                  </Tag>
                </Col>
              </Row>
            </Col>
            <Col span="12" offset="12"></Col>
            <Col span="12">
              <Row>
                <Col span="7">创建时间：</Col>
                <Col span="17">{{ baseicData.createTime | formatDate }}</Col>
              </Row>
            </Col>
            <Col span="12">
              <Row>
                <Col span="7">最后修改时间：</Col>
                <Col span="17">{{ baseicData.updateTime | formatDate }}</Col>
              </Row>
            </Col>
            <Col span="12">
              <Row>
                <Col span="7">描述：</Col>
                <Col span="17">{{ baseicData.comment }}</Col>
              </Row>
            </Col>
          </Row>
        </Card>
        <Card :bordered="false" title="核心属性" dis-hover>
          <Row>
            <Col span="12">
              <Row>
                <Col span="7">是否分区表：</Col>
                <Col span="17">
                  {{ baseicData.isPartitionTable ? "是" : "否" }}
                </Col>
              </Row>
            </Col>
            <Col span="12">
              <Row>
                <Col span="7">表类型：</Col>
                <Col span="17">
                  {{ baseicData.isExternal ? "外部表" : "内部表" }}
                </Col>
              </Row>
            </Col>
            <Col span="12">
              <Row>
                <Col span="7">生命周期：</Col>
                <Col span="17">{{ baseicData.lifecycle }}</Col>
              </Row>
            </Col>
            <Col span="12">
              <Row>
                <Col span="7">文件格式：</Col>
                <Col span="17">{{ baseicData.fileType }}</Col>
              </Row>
            </Col>
            <Col span="12">
              <Row>
                <Col span="7">压缩存储格式：</Col>
                <Col span="17">{{ baseicData.compress }}</Col>
              </Row>
            </Col>
            <Col span="12">
              <Row>
                <Col span="7">存储引擎：</Col>
                <Col span="17">{{ baseicData.storageType }}</Col>
              </Row>
            </Col>
          </Row>
        </Card>
      </TabPane>
      <TabPane label="字段信息" name="columnTab">
        <div style="margin-bottom: 15px">
          <Button
            type="primary"
            style="margin-right: 15px"
            @click="handleCreateSql"
          >
            生成建表语句
          </Button>
          <Button type="primary" @click="handleCopyColumnsInfo">
            复制所有字段
          </Button>
        </div>
        <Table
          v-if="columnInfoTableData"
          :columns="columnInfoTableColumn"
          :data="columnInfoTableData"
        >
          <template slot-scope="{ row }" slot="isPartitionField">
            {{ row.isPartitionField ? "是" : "否" }}
          </template>
        </Table>
      </TabPane>
      <TabPane label="分区信息" name="partitionTable">
        <Card :bordered="false" title="表统计信息" dis-hover extra="asd">
          <template slot="extra">
            表数据最后访问时间： {{ baseicData.lastAccessTime | formatDate }}
          </template>
          <Row>
            <Col span="3">
              <div class="census-item">
                <p class="value">{{ tableCensusInfo.columnCount }}</p>
                <p class="label">字段数量</p>
              </div>
            </Col>
            <Col span="3">
              <div class="census-item">
                <p class="value">
                  {{ transformCompany(tableCensusInfo.storageSize).num }}
                  <span class="unit">
                    {{ transformCompany(tableCensusInfo.storageSize).unit }}
                  </span>
                </p>
                <p class="label">表大小</p>
              </div>
            </Col>
            <Col span="3">
              <div class="census-item">
                <p class="value">{{ tableCensusInfo.fileCount }}</p>
                <p class="label">文件数</p>
              </div>
            </Col>
            <Col span="3">
              <div class="census-item">
                <p class="value">{{ tableCensusInfo.partitionCount }}</p>
                <p class="label">分区数</p>
              </div>
            </Col>
            <Col span="3">
              <div class="census-item">
                <p class="value">{{ tableCensusInfo.accessCount }}</p>
                <p class="label">访问次数</p>
              </div>
            </Col>
            <Col span="3">
              <div class="census-item">
                <p class="value">{{ tableCensusInfo.collectCount }}</p>
                <p class="label">收藏次数</p>
              </div>
            </Col>
            <Col span="3">
              <div class="census-item">
                <p class="value">{{ tableCensusInfo.refCount }}</p>
                <p class="label">引用次数</p>
              </div>
            </Col>
          </Row>
        </Card>
        <Card :bordered="false" title="分区统计信息" dis-hover>
          <div style="margin-bottom: 15px">
            <Select
              style="width: 200px"
              placeholder="分区"
              v-model="partitionCensusInfoTableFilter.name"
              clearable
            >
              <Option
                v-for="item in partitionCensusInfoTableData"
                :key="item.partName"
                :value="item.partName"
                :label="item.partName"
              />
            </Select>
            <Select
              style="width: 200px"
              placeholder="大小"
              v-model="partitionCensusInfoTableFilter.size"
              clearable
            >
              <Option
                v-for="item in partitionCensusInfoTableFilterSizeOption"
                :key="item.value"
                :value="item.value"
                :label="item.label"
              />
            </Select>
          </div>
          <Table
            v-if="partitionCensusInfoTableData"
            :columns="partitionCensusInfoTableColumn"
            :data="partitionCensusInfoTableDataFilter"
          >
            <template slot-scope="{ row }" slot="store">
              {{ transformCompany(row.store).str }}
            </template>
            <template slot-scope="{ row }" slot="createTime">
              {{ row.createTime | formatDate }}
            </template>
            <template slot-scope="{ row }" slot="lastAccessTime">
              {{ row.lastAccessTime | formatDate }}
            </template>
          </Table>
        </Card>
      </TabPane>
      <TabPane label="数据预览" name="preViewTab">
        <div style="margin-bottom: 15px">
          <Button type="primary" @click="handleExportPreViewData">
            下载数据
          </Button>
        </div>
        `
        <Table
          v-if="preViewDataTableData"
          ref="preViewTable"
          :columns="preViewDataColumn"
          :data="preViewDataTableData"
        >
        </Table>
      </TabPane>
    </Tabs>
    <!-- 生成 select 和 DDL 弹窗 -->
    <Modal v-model="ddlSqlCfg.visible" title="生成建表语句" width="576">
      <div class="field-info-rich-text">
        <div v-html="viewSelectSql"></div>
      </div>
      <template v-slot:footer>
        <div>
          <Button type="primary" @click="handleCopyDDL"> 复制</Button>
        </div>
      </template>
    </Modal>
  </div>
</template>

<script>
import {
  addCollect,
  delCancel,
  getCollectList,
  getTableInfoById,
  getTableInfoByName,
  getTablesCreateSql,
  getTablesPartitionStats,
  getTablesPreview,
} from "@/apps/dataModelCenter/service/api/tableManage";
import formatDate from "@/apps/dataModelCenter/utils/formatDate";
import {
  fomatSqlForCopy,
  fomatSqlForShow,
} from "@/apps/dataModelCenter/utils/fomatSQL";
import handleClipboard from "@/apps/dataModelCenter/utils/clipboard";
import mixin from "@/common/service/mixin";

const columnInfoTableColumn = [
  {
    title: "字段名称",
    key: "name",
  },
  {
    title: "是否主键",
    key: "isPrimary",
  },
  {
    title: "是否分区字段",
    key: "isPartitionField",
    slot: "isPartitionField",
  },
  {
    title: "字段类型",
    key: "type",
  },
  {
    title: "字段别名",
    key: "alias",
  },
  {
    title: "关联数仓",
    key: "modelName",
  },
  {
    title: "校验规则",
    key: "rule",
  },
  {
    title: "字段说明",
    key: "comment",
  },
];

const partitionCensusInfoTableColumn = [
  {
    title: "分区名",
    align: "center",
    key: "partName",
  },
  {
    title: "记录数",
    align: "center",
    key: "reordCnt",
  },
  {
    title: "分区大小",
    align: "center",
    key: "store",
    slot: "store",
  },
  {
    title: "文件数",
    align: "center",
    key: "fileCount",
  },
  {
    title: "创建时间",
    align: "center",
    key: "createTime",
    slot: "createTime",
  },
  {
    title: "最后访问时间",
    align: "center",
    key: "lastAccessTime",
    slot: "lastAccessTime",
  },
];
export default {
  filters: { formatDate },
  mixins: [mixin],
  data() {
    return {
      // 当前tab
      currentTab: "baseicTab",
      // 是否加载中
      loading: false,
      // 普通数据
      generalData: {
        name: "",
        version: "",
        comment: "",
        label: "",
      },

      // 基础信息
      baseicData: {},
      // 表统计信息
      tableCensusInfo: {},
      // 字段信息
      columnInfoTableColumn,
      columnInfoTableData: null,
      // 数据预览信息
      preViewDataColumn: [],
      preViewDataTableData: null,
      // 分区统计信息
      partitionCensusInfoTableColumn,
      partitionCensusInfoTableData: null,
      // 分区信息过滤条件
      partitionCensusInfoTableFilter: {
        name: "",
        size: undefined,
      },
      partitionCensusInfoTableFilterSizeOption: [
        {
          label: "不为空",
          value: 1,
        },
        {
          label: "大于1G",
          value: 100,
        },
        {
          label: "大于10G",
          value: 1000,
        },
        {
          label: "大于100g",
          value: 10000,
        },
        {
          label: "大于1T",
          value: 100000,
        },
      ],
      // 生成的sql
      ddlSqlCfg: {
        visible: false,
        sql: "",
      },
      // 表信息
      config: {
        id: this.$route.query.id,
        name: this.$route.query.name,
        guid: this.$route.query.guid,
      },
      // 是否被收藏
      isCollect: false,
    };
  },
  watch: {
    // 监听tabs变化
    currentTab(value) {
      if (value === "preViewTab") {
        if (this.preViewDataTableData === null) {
          return this.handleGetTablesPreview();
        }
      }
      if (value === "partitionTable") {
        if (this.partitionCensusInfoTableData === null) {
          return this.handleGetTablesPartitionStats();
        }
      }
    },
  },
  computed: {
    viewSelectSql() {
      return fomatSqlForShow(this.ddlSqlCfg.sql);
    },
    // 过滤分区信息
    partitionCensusInfoTableDataFilter() {
      // 拿到条件
      let { name, size } = this.partitionCensusInfoTableFilter;
      if (!name && !size) return this.partitionCensusInfoTableData;
      // 根据雕件过滤
      return this.partitionCensusInfoTableData.filter((item) => {
        let ifName = item.partName === name;
        let ifSize = item.size >= size;
        if (name && size) {
          if (ifName && ifSize) return true;
        } else if (name) {
          if (ifName) return true;
        } else if (size) {
          if (ifSize) return true;
        }
        return false;
      });
    },
  },
  mounted() {
    // 获取数据
    this.handleGetData();
    // 检查收藏状态
    this.handleGetCollectionStatus();
  },
  methods: {
    /**
     * @description 去往编辑页面
     */
    handleToEditor() {
      let { id, name, guid } = this.config;
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
     * @description 导出预览数据
     */
    handleExportPreViewData() {
      this.$refs["preViewTable"].exportCsv({ filename: "preview.csv" });
    },
    /**
     * @description 复制所有字段信息
     * @param e
     */
    handleCopyColumnsInfo(e) {
      /**
       * @description 格式化数据为 字符串
       * @param columns
       * @param columnData
       * @returns {string}
       */
      function formatCopyStr(columns, columnData) {
        let str = "";
        // 构造头行
        for (let i = 0; i < columns.length; i++) {
          let column = columns[i];
          if (i === columns.length - 1) {
            str += column.title;
          } else {
            str += column.title + "    ";
          }
        }
        // 构造表格体
        for (let i = 0; i < columnData.length; i++) {
          let dataItem = columnData[i];
          let columnLine = "";
          for (let i = 0; i < columns.length; i++) {
            let column = columns[i];
            if (i === columns.length - 1) {
              columnLine += dataItem[column.key] || "空";
            } else {
              columnLine += (dataItem[column.key] || "空") + "    ";
            }
          }
          str += "\r\n" + columnLine;
        }
        // 返回结果
        return str;
      }

      if (this.columnInfoTableData) {
        let columns = [
          {
            title: "字段名称",
            key: "name",
          },
          {
            title: "别名",
            key: "alias",
          },
          {
            title: "是否分区字段",
            key: "type",
          },
          {
            title: "是否主键",
            key: "isPrimary",
          },
          {
            title: "描述",
            key: "comment",
          },
          {
            title: "校验规则",
            key: "rule",
          },
          {
            title: "关联数仓",
            key: "modelName",
          },
        ];
        handleClipboard(formatCopyStr(columns, this.columnInfoTableData), e);
      }
    },
    // 生成建表语句
    handleCreateSql() {
      let { id, guid } = this.config;
      getTablesCreateSql(id, guid).then((res) => {
        this.ddlSqlCfg = {
          visible: true,
          sql: res.detail,
        };
      });
    },
    /**
     * 复制表
     */
    handleCopyTable() {
      this.$router.push({
        path: `/datamodelcenter/tableManage/tableEditor`,
        query: {
          mode: "copy",
          id: this.config.id,
          guid: this.config.guid,
          name: this.config.name,
        },
      });
    },
    /**
     * @description 切换收藏
     * @returns {Promise<void>}
     */
    async handleSwitchCollect() {
      let { name } = this.config;
      this.loading = true;
      if (this.isCollect) {
        await delCancel(name);
        this.$Message.success("取消收藏成功");
      } else {
        let collectData = {
          tableId: this.config.id,
          dataBase: this.baseicData.dataBase,
          name: this.baseicData.name,
          alias: this.baseicData.alias,
          creator: this.baseicData.creator,
          comment: this.baseicData.comment,
          warehouseLayerName: this.baseicData.warehouseLayerName,
          warehouseThemeName: this.baseicData.warehouseThemeName,
          lifecycle: this.baseicData.lifecycle,
          isPartitionTable: this.baseicData.isPartitionTable,
          isAvailable: this.baseicData.isAvailable,
          storageType: this.baseicData.storageType,
          principalName: this.baseicData.principalName,
          compress: this.baseicData.compress,
          fileType: this.baseicData.fileType,
          isExternal: this.baseicData.isExternal,
          label: this.baseicData.label.join(";"),
          guid: this.config.guid,
        };
        await addCollect(collectData);
        this.$Message.success("添加收藏成功");
      }
      await this.handleGetCollectionStatus();
      this.loading = false;
    },
    /**
     * @description 获取收藏状态
     * @returns {Promise<void>}
     */
    async handleGetCollectionStatus() {
      let { total } = await getCollectList({ name: this.config.name });
      this.isCollect = !!total;
    },
    // 获取数据
    async handleGetData() {
      let { id, guid, name } = this.config;
      this.loading = true;
      let data;
      // 优先使用id获取
      if (id) {
        data = await getTableInfoById(id);
      } else {
        data = await getTableInfoByName(name, guid);
      }
      this.loading = false;
      let { detail } = data;
      // 如果有id更新id
      if (detail.id) {
        this.config.id = detail.id;
      }
      // 普通信息
      this.generalData = {
        name: detail.name,
        version: detail.version,
        comment: detail.comment,
      };
      // 基本信息
      this.baseicData = {
        // 表名
        name: detail.name,
        // 别名
        alias: detail.alias,
        // 路径
        location: detail.location,
        // 所属库
        dataBase: detail.dataBase,
        // 所属分层
        warehouseLayerName: detail.warehouseLayerName,
        // 主题域
        warehouseThemeName: detail.warehouseThemeName,
        // 标签
        label: detail.label ? detail.label.split(",") : [],
        // 创建时间
        createTime: detail.createTime,
        // 最后修改时间
        updateTime: detail.updateTime,
        // 最后访问时间
        lastAccessTime: detail.lastAccessTime,
        // 描述
        comment: detail.comment,
        // 创建人
        creator: detail.creator,
        // 是否启用
        isAvailable: detail.isAvailable,

        // 是否分区表
        isPartitionTable: detail.isPartitionTable,
        // 是否外部表
        isExternal: detail.isExternal,
        // 文件格式
        fileType: detail.fileType,
        // 生命周期
        lifecycle: detail.lifecycle,
        // 压缩存储格式
        compress: detail.compress,
        // 存储引擎
        storageType: detail.storageType,
      };
      // 字段信息
      this.columnInfoTableData = detail.columns;
      // 表统计信息
      this.tableCensusInfo = detail.stats;
    },
    /**
     * @description 获取分区信息
     */
    handleGetTablesPartitionStats() {
      let { guid, name } = this.config;
      this.loading = true;
      getTablesPartitionStats(name, guid).then((res) => {
        this.loading = false;
        this.partitionCensusInfoTableData = res.list;
      });
    },
    /**
     * @description 获取预览数据
     */
    handleGetTablesPreview() {
      let { name } = this.config;
      this.loading = true;
      getTablesPreview(name).then((res) => {
        this.loading = false;
        this.preViewDataColumn = res.detail.metadata.map((item) => {
          return {
            title: item.columnName,
            key: item.columnName,
            align: "center",
          };
        });
        this.preViewDataTableData = res.detail.data.map((data) => {
          let itemData = {};
          for (let j = 0; j < data.length; j++) {
            const key = res.detail.metadata[j].columnName;
            itemData[key] = data[j];
          }
          return itemData;
        });
      });
    },
    /**
     * @description 复制建表语句
     * @param e
     */
    handleCopyDDL(e) {
      handleClipboard(fomatSqlForCopy(this.ddlSqlCfg.sql), e);
    },
    // 转换存储单位
    transformCompany(number) {
      if (!number) {
        return { num: 0, unit: "B", str: "0B" };
      }
      let unitArr = ["B", "KB", "MB", "GB", "TB"];
      for (let i = unitArr.length - 1; i >= 0; i--) {
        const unit = unitArr[i];
        let res = number / Math.pow(1024, i);
        if (res > 1) {
          return { num: res.toFixed(2), unit, str: res.toFixed(2) + unit };
        }
      }
    },
  },
};
</script>

<style lang="scss" scoped>
.census-item {
  .value {
    font-weight: 700;
    font-style: normal;
    color: #2d8cf0;
    text-align: center;
    font-size: 34px;

    .unit {
      font-size: 16px;
      color: rgb(116, 116, 116);
    }
  }

  .label {
    font-size: 16px;
    text-align: center;
  }
}

.field-info-rich-text {
  margin: 8px 24px;
  padding: 5px 12px;
  background: #ffffff;
  border: 1px solid #dee4ec;
  border-radius: 4px;
  font-size: 14px;
  color: rgba(0, 0, 0, 0.65);
  line-height: 22px;
  overflow-y: auto;
  max-height: 200px;

  div {
    /deep/ span {
      font-weight: bold;
      margin-right: 6px;
    }
  }
}
</style>
