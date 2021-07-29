<template>
  <div class="paramsContainer">
    <div class="toolBar">
      <div
        v-for="(toolItem, index) in toolItems"
        :key="index"
        class="toolWrap"
        @click="handleToolShow(toolItem)"
      >
        <img :src="toolItem.iconSrc" />
        <div>{{ toolItem.name }}</div>
        <div class="divider" :class="{ 'last-divider': index === 4 }" />
      </div>
    </div>
    <div class="paramsCardContainer">
      <div class="cardWrap">
        <div class="cardTitle">数据表</div>
        <div class="contentWrap">
          <Form
            ref="dbForm"
            :model="dbForm"
            :label-width="90"
            :rules="ruleValidate"
          >
            <FormItem label="数据源类型" prop="datasourceType">
              <Select v-model="dbForm.datasourceType" style="width:300px">
                <Option value="MYSQL">MYSQL</Option>
              </Select>
            </FormItem>
            <FormItem label="数据源名称" prop="datasourceId" required>
              <Select
                v-model="dbForm.datasourceId"
                style="width:300px"
                filterable
                @on-change="value => getDbTables(value)"
              >
                <Option
                  v-for="item in datasourceIds"
                  :value="item.datasourceId"
                  :key="item.datasourceId"
                  >{{ item.name }}</Option
                >
              </Select>
            </FormItem>
            <FormItem label="数据表名称" prop="tblName">
              <Select
                v-model="dbForm.tblName"
                style="width:300px"
                filterable
                @on-change="value => getTableCols(value)"
              >
                <Option v-for="item in dbTables" :value="item" :key="item">{{
                  item
                }}</Option>
              </Select>
            </FormItem>
          </Form>
        </div>
      </div>
      <div class="cardWrap">
        <div class="cardTitle">环境配置</div>
        <div class="contentWrap">
          <Form :model="envForm" :label-width="90">
            <FormItem label="内存" prop="memory">
              <Select v-model="envForm.memory" style="width:300px">
                <Option value="4096">4096M</Option>
              </Select>
            </FormItem>
            <FormItem label="超时时间" prop="reqTimeout">
              <Input
                type="number"
                v-model="envForm.reqTimeout"
                placeholder="请输入超时时间"
                style="width: 300px"
                ><span slot="append">ms</span></Input
              >
            </FormItem>
          </Form>
        </div>
      </div>
      <div class="cardWrap" v-if="apiData.data.apiType === 'SQL'">
        <div class="cardTitle">编写查询SQL</div>
        <div class="contentWrap">
          <Input
            v-model="sql"
            type="textarea"
            placeholder="请填写SQL语句"
            rows="10"
          />
        </div>
      </div>
      <div
        class="cardWrap cardTableWrap"
        v-if="apiData.data.apiType === 'GUIDE'"
      >
        <div class="cardTitle">选择参数</div>
        <div class="contentWrap">
          <Table
            :columns="paramsColumns"
            :data="paramsList"
            :loading="tableLoading"
          >
            <template slot-scope="{ index, column }" slot="checkbox">
              <Checkbox
                @on-change="value => changeParams(value, index, column)"
              />
            </template>
            <template slot-scope="{ index, row }" slot="compare">
              <Select
                :value="row.compare"
                transfer
                style="width:200px"
                @on-change="value => changeParamCompare(value, index)"
              >
                <Option
                  v-for="(item, index) in compareItems"
                  :value="item.value"
                  :key="index"
                  >{{ item.label }}</Option
                >
              </Select>
            </template>
            <template slot-scope="{ row }" slot="fieldSort">
              <div class="fieldSort" @click="addToSort(row)">
                添加到字段排序
              </div>
            </template>
          </Table>
        </div>
      </div>
      <div
        class="cardWrap cardTableWrap"
        v-if="apiData.data.apiType === 'GUIDE'"
      >
        <div class="cardTitle">排序字段</div>
        <div style="margin-top: 10px;">
          <Alert show-icon closable
            >排序字段为非必填项；如果您需要对字段进行排序，请首先在选择参数的列表中选择所需字段</Alert
          >
        </div>
        <div class="contentWrap">
          <Table :columns="sortColumns" :data="sortList">
            <template slot-scope="{ row }" slot="SortType">
              <Select
                :value="row.type"
                transfer
                style="width:200px"
                @on-change="value => changeSort(value, row)"
              >
                <Option value="asc">升序</Option>
                <Option value="desc">降序</Option>
              </Select>
            </template>
            <template slot-scope="{ index }" slot="operation">
              <div class="sortOperation">
                <div class="operation" @click="moveSortRow(index, 'up')">
                  上移
                </div>
                <Divider type="vertical" />
                <div class="operation" @click="moveSortRow(index, 'down')">
                  下移
                </div>
                <Divider type="vertical" />
                <div class="operation" @click="moveSortRow(index, 'delete')">
                  删除
                </div>
              </div>
            </template>
          </Table>
        </div>
      </div>
      <div class="cardWrap cardTableWrap" v-if="apiData.data.apiType === 'SQL'">
        <div class="cardTitle">请求参数</div>
        <div class="contentWrap">
          <Table :columns="sqlColumns" :data="sqlList">
            <template slot-scope="{ index, row, column }" slot="input">
              <Input
                type="text"
                :value="column.key === 'name' ? row.name : row.comment"
                @on-change="
                  value => changeSqlParams(value.target.value, index, column)
                "
                placeholder="请输入"
              ></Input>
            </template>
            <template slot-scope="{ index, row, column }" slot="type">
              <Select
                :value="row.type"
                transfer
                style="width:200px"
                @on-change="value => changeSqlParams(value, index, column)"
              >
                <Option
                  v-for="(item, index) in sqlTypeOptions"
                  :value="item.value"
                  :key="index"
                  >{{ item.label }}</Option
                >
              </Select>
            </template>
            <template slot-scope="{ index }" slot="operation">
              <div class="sqlOperation" @click="deleteSqlRow(index)">
                删除
              </div>
            </template>
          </Table>
          <div class="addSqlParams" @click="addSqlParams()">
            <Icon type="md-add" />
            <div style="margin-left:5px;">新增参数</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import api from "@/common/service/api";
const compareItems = [
  {
    label: "小于",
    value: "&lt;"
  },
  {
    label: "小于等于",
    value: "&lt;="
  },
  {
    label: "大于",
    value: "&gt;"
  },
  {
    label: "大于等于",
    value: "&gt;="
  }
];
export default {
  props: {
    apiData: {
      type: Object,
      default: () => {}
    },
    showApiForm: {
      type: Function
    }
  },
  data() {
    return {
      toolItems: [
        {
          name: "属性",
          iconSrc: require("../../assets/images/property.svg"),
          type: "property"
        },
        {
          name: "版本",
          iconSrc: require("../../assets/images/version.svg"),
          type: "version"
        },
        {
          name: "保存",
          iconSrc: require("../../assets/images/save.svg"),
          type: "save"
        },
        {
          name: "测试",
          iconSrc: require("../../assets/images/test.svg"),
          type: "test"
        },
        {
          name: "发布",
          iconSrc: require("../../assets/images/release.svg"),
          type: "release"
        }
      ],
      dbForm: {
        datasourceType: "MYSQL",
        datasourceId: "",
        tblName: ""
      },
      ruleValidate: {
        datasourceType: [
          {
            required: true,
            message: "请选择数据源类型",
            trigger: "blur"
          }
        ],
        datasourceIdss: [
          {
            required: true,
            message: "请选择数据源名称",
            trigger: "change"
          }
        ],
        tblName: [
          {
            required: true,
            message: "请选择数据表名称",
            trigger: "change"
          }
        ]
      },
      datasourceIds: [],
      dbTables: [],
      envForm: {
        memory: "4096",
        reqTimeout: ""
      },
      paramsColumns: [
        {
          title: "设为请求参数",
          key: "setRequest",
          slot: "checkbox",
          renderHeader: (h, params) => {
            return h("div", [
              h(
                "span",
                {
                  on: {
                    click: () => {
                      this.setParamsChoose(params.column);
                    }
                  }
                },
                [h("Checkbox")]
              ),

              h(
                "span",
                {
                  style: {
                    marginLeft: "2px"
                  }
                },
                "设为请求参数"
              )
            ]);
          }
        },
        {
          title: "设为返回参数",
          key: "setResponse",
          slot: "checkbox",
          renderHeader: (h, params) => {
            return h("div", [
              h(
                "span",
                {
                  on: {
                    click: () => {
                      this.setParamsChoose(params.column);
                    }
                  }
                },
                [h("Checkbox")]
              ),

              h(
                "span",
                {
                  style: {
                    marginLeft: "2px"
                  }
                },
                "设为返回参数"
              )
            ]);
          }
        },
        {
          title: "请求参数比较",
          key: "compare",
          slot: "compare"
        },
        {
          title: "字段名",
          key: "columnName"
        },
        {
          title: "字段类型",
          key: "fieldType"
        },
        {
          title: "字段描述",
          key: "Comment"
        },
        {
          title: "字段排序",
          key: "fieldSort",
          slot: "fieldSort"
        }
      ],
      paramsList: [],
      compareItems,
      tableLoading: false,
      sortColumns: [
        {
          title: "序号",
          key: "index"
        },
        {
          title: "字段名",
          key: "columnName"
        },
        {
          title: "排序方式",
          key: "type",
          slot: "SortType"
        },
        {
          title: "操作",
          key: "operation",
          slot: "operation"
        }
      ],
      sortList: [
        {
          index: 1,
          id: "a",
          type: "asc"
        }
      ],
      sql: "",
      sqlColumns: [
        {
          title: "参数名称",
          key: "name",
          slot: "input"
        },
        {
          title: "参数类型",
          key: "type",
          slot: "type"
        },
        {
          title: "示例值",
          key: "demo"
        },
        {
          title: "描述",
          key: "comment",
          slot: "input"
        },
        {
          title: "操作",
          key: "operation",
          slot: "operation"
        }
      ],
      sqlList: [],
      sqlTypeOptions: [
        { label: "string", value: "string", demo: "liming" },
        { label: "bigint", value: "bigint", demo: 11 },
        { label: "double", value: "double", demo: 20.01 },
        { label: "date", value: "date", demo: "2020-09-21 14:30:05" },

        {
          label: "string 数组",
          value: "Array<string>",
          demo: ["liming", "xiaohua"]
        },
        { label: "bigint 数组", value: "Array<bigint>", demo: [1, 2] },
        { label: "double 数组", value: "Array<double>", demo: [1.01, 2.02] },
        {
          label: "date 数组",
          value: "Array<date>",
          demo: ["2020-09-21 14:00:00", "2020-09-23 15:23:01"]
        }
      ]
    };
  },
  computed: {},
  created() {
    // 获取api相关数据
    // api.fetch('/dss/apiservice/queryById', {
    //   id: this.$route.query.id,
    // }, 'get').then((rst) => {
    //   if (rst.result) {
    //     // 加工api信息tab的数据
    //     this.apiInfoData = [
    //       { label: this.$t('message.apiServices.label.apiName'), value: rst.result.name },
    //       { label: this.$t('message.apiServices.label.path'), value: rst.result.path },
    //       { label: this.$t('message.apiServices.label.scriptsPath'), value: rst.result.scriptPath },
    //     ]
    //   }
    // }).catch((err) => {
    //   console.error(err)
    // });
  },
  mounted() {
    this.getDataSourceIds(this.dbForm.datasourceType);
  },
  methods: {
    handleToolShow(item) {
      const { type } = item;
      if (type === "property") {
        this.$emit("showApiForm", this.apiData);
      } else if (type === "save") {
        this.saveApi();
      }
    },
    saveApi() {
      const { data } = this.apiData;
      const { apiType } = data;
      const { reqTimeout, memory } = this.envForm;
      let reqParams = {
        ...data,
        ...this.dbForm,
        memory: parseFloat(memory),
        id: data.id || "",
        workspaceId: 223
      };
      if (reqTimeout) {
        reqParams.reqTimeout = parseFloat(reqTimeout);
      }
      this.$refs["dbForm"].validate(valid => {
        console.log(valid);
        if (valid) {
          if (apiType === "GUIDE") {
            const reqFields = [];
            const resFields = [];
            this.paramsList.forEach(item => {
              if (item.setRequest) {
                reqFields.push({
                  name: item.columnName,
                  type: item.fieldType,
                  compare: item.compare,
                  comment: item.Comment
                });
              }
              if (item.setResponse) {
                resFields.push("`" + item.columnName + "`");
              }
            });
            if (resFields.length === 0) {
              this.$Message.error("返回参数不能为空");
              return;
            }
            // if (this.sortList.length === 0) {
            //   this.$Message.error("排序字段不能为空");
            //   return;
            // }
            const orderFields = this.sortList.map(item => {
              return { name: item.columnName, type: item.type };
            });
            reqParams = {
              ...reqParams,
              reqFields: reqFields.length > 0 ? JSON.stringify(reqFields) : "",
              orderFields:
                orderFields.length > 0 ? JSON.stringify(orderFields) : "",
              resFields: resFields.join(",")
            };
          } else {
            if (!this.sql) {
              this.$Message.error("SQL语句不能为空");

              return;
            }
            const reqes = this.sqlList.filter(item => !!item.name);
            if (reqes.length === 0) {
              this.$Message.error("请求参数不能为空");
            }
            reqParams = {
              ...reqParams,
              reqFields: reqes.length > 0 ? JSON.stringify(reqes) : "",
              sql: this.sql
            };
          }
          api
            .fetch(`/dss/framework/dbapi/save`, reqParams, "post")
            .then(res => {
              console.log(res);
              this.confirmLoading = false;
            })
            .catch(() => {
              this.confirmLoading = false;
            });
        }
      });
    },
    addToSort(rowData) {
      console.log(rowData);
      this.sortList = [...this.sortList, rowData].map((item, index) => {
        return { ...item, index: index + 1, type: "asc" };
      });
    },
    setParamsChoose(column) {
      console.log(column);
    },
    changeParams(value, index, column) {
      const datas = [...this.paramsList];
      datas[index][column.key] = value;
      this.paramsList = datas;
      console.log(column);
    },
    changeParamCompare(value, index) {
      const datas = [...this.paramsList];
      datas[index]["compare"] = value;
      this.paramsList = datas;
      console.log(value);
    },
    changeSort(value, rowData) {
      console.log(value);
      this.sortList = [...this.sortList].map((item, index) => {
        const newItem = { ...item };
        if (rowData.columnName === item.columnName) {
          newItem.type = value;
        }
        return newItem;
      });
    },
    moveSortRow(index, action) {
      const datas = [...this.sortList];
      if (action === "up") {
        if (index === 0) {
          return;
        }
        const pre = datas[index - 1];
        const current = datas[index];
        datas[index - 1] = { ...current };
        datas[index] = { ...pre };
      } else if (action === "down") {
        if (index === datas.length - 1) {
          return;
        }
        const suf = datas[index + 1];
        const current = datas[index];
        datas[index + 1] = { ...current };
        datas[index] = { ...suf };
      } else if (action === "delete") {
        datas.splice(index, 1);
      }
      this.sortList = datas.map((item, index) => {
        return {
          ...item,
          index: index + 1
        };
      });
    },
    deleteSqlRow(index) {
      const datas = [...this.sqlList];
      datas.splice(index, 1);
      this.sqlList = datas;
    },
    changeSqlParams(value, index, column) {
      const datas = [...this.sqlList];
      datas[index][column.key] = value;
      if(column.key === 'type'){
        const demo = this.sqlTypeOptions.find(item => item.value === value);
        datas[index]["demo"] = demo.demo;
      }
      this.sqlList = datas;
    },
    addSqlParams() {
      const datas = [...this.sqlList];
      datas.push({
        name: "",
        type: "string",
        demo: "liming",
        comment: ""
      });
      this.sqlList = datas;
    },
    getDataSourceIds(datasourceType) {
      //获取数据源
      this.searchValue = "";
      api
        .fetch(
          `/dss/framework/dbapi/datasource/connections?workspaceId=${this.$route.query.workspaceId}&type=${datasourceType}`,
          {},
          "get"
        )
        .then(res => {
          console.log(res);
          if (res && res.availableConns) {
            this.datasourceIds = [...res.availableConns];
          } else {
            this.datasourceIds = [];
          }
        });
    },
    getDbTables(datasourceId) {
      //获取数据表
      this.searchValue = "";
      this.dbTables = [];
      this.paramsList = [];
      this.sortList = [];
      api
        .fetch(
          `/dss/framework/dbapi/datasource/tables?datasourceId=${datasourceId}`,
          {},
          "get"
        )
        .then(res => {
          console.log(res);
          if (res && res.allTables) {
            this.dbTables = res.allTables;
          } else {
            this.dbTables = [];
          }
        });
    },
    getTableCols(tableName) {
      //获取数据表的字段
      this.searchValue = "";
      this.paramsList = [];
      this.sortList = [];
      api
        .fetch(
          `/dss/framework/dbapi/datasource/cols?datasourceId=${this.dbForm.datasourceId}&tableName=${tableName}`,
          {},
          "get"
        )
        .then(res => {
          console.log(res);
          if (res && res.allCols) {
            this.paramsList = res.allCols.map(item => {
              return { ...item, compare: compareItems[0].value };
            });
          } else {
            this.paramsList = [];
          }
        });
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.paramsContainer {
  padding: 0 0;
  .toolBar {
    width: 100%;
    height: 48px;
    margin-top: -5px;
    background: #f8f9fc;
    border: 1px solid rgba($color: #000000, $alpha: 0.2);
    border-left-width: 0;
    border-right-width: 0;
    box-sizing: border-box;
    display: flex;
    justify-content: flex-start;
    align-items: center;
    padding-left: 10px;
    .toolWrap {
      display: flex;
      justify-content: space-around;
      align-items: center;
      width: 90px;
      cursor: pointer;
      font-family: PingFangSC-Regular;
      font-size: 16px;
      color: rgba(0, 0, 0, 0.65);
      & img {
        width: 16px;
      }
      .divider {
        width: 1px;
        height: 16px;
        background: rgba(0, 0, 0, 0.25);
        &.last-divider {
          background: transparent;
        }
      }
    }
  }
  .paramsCardContainer {
    padding: 0 20px;
    .cardWrap {
      padding: 20px 0;
      border-bottom: 1px solid rgba($color: #000000, $alpha: 0.2);
      .cardTitle {
        font-family: PingFangSC-Medium;
        font-size: 16px;
        color: rgba(0, 0, 0, 0.85);
      }
      .contentWrap {
        margin-top: 20px;
        .fieldSort {
          font-size: 14px;
          color: #2e92f7;
          cursor: pointer;
        }
        .sortOperation {
          display: flex;
          justify-content: flex-start;
          align-items: center;
          height: 38px;
          font-size: 14px;
          font-family: PingFangSC-Medium;
          .operation {
            font-size: 14px;
            color: #2e92f7;
            cursor: pointer;
          }
        }
        .sqlOperation {
          font-size: 14px;
          color: #2e92f7;
          cursor: pointer;
          font-family: PingFangSC-Medium;
        }
        .addSqlParams {
          margin-top: 10px;
          display: flex;
          background: #ffffff;
          border: 1px dashed #d9d9d9;
          border-radius: 4px;
          box-sizing: border-box;
          height: 32px;
          justify-content: center;
          align-items: center;
          cursor: pointer;
          font-family: PingFangSC-Regular;
          font-size: 14px;
          color: rgba(0, 0, 0, 0.65);
          &:hover {
            background-color: #f0faff;
          }
        }
      }
    }
    .cardTableWrap {
      border-bottom-width: 0;
    }
  }
}
</style>
