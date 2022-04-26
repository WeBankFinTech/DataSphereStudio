<template>
  <div class="paramsContainer">
    <div v-if="showTestPanel">
      <div class="testTitle">
        <div class="title">API测试</div>
        <Icon type="md-close" class="icon" @click="closeTestPanel" />
      </div>
      <Test
        :apiData="apiData.data"
        :fromDataService="true"
        @testSuccess="setTestSuccess"
      />
    </div>
    <div v-show="!showTestPanel">
      <div class="toolBar">
        <div
          v-for="(toolItem, index) in toolItems"
          :key="index"
          class="toolWrap"
          @click="handleToolShow(toolItem)"
        >
          <SvgIcon
            class="icon"
            :icon-class="toolItem.iconName"
            verticalAlign="0px"
          />
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
              <FormItem label="数据源名称" prop="datasourceId">
                <Select
                  v-model="dbForm.datasourceId"
                  style="width:300px"
                  filterable
                  @on-change="value => getDbTables(value)"
                >
                  <Option
                    v-for="item in datasourceIds"
                    :value="item.datasourceId + ''"
                    :key="item.datasourceId"
                  >{{ item.name }}</Option
                  >
                </Select>
              </FormItem>
              <div class="dataSourceTip">
                如需新建数据源，请点击
                <div class="dataSourceLink" @click="gotoCreateDatasource">
                  这里
                </div>
                创建
              </div>
              <FormItem
                label="数据表名称"
                prop="tblName"
                v-if="apiData.data.apiType === 'GUIDE'"
              >
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
        <div
          class="cardWrap cardTableWrap"
          v-if="apiData.data.apiType === 'SQL'"
        >
          <div class="cardTitle">编写查询SQL</div>
          <div style="margin-top: 10px;">
            <Alert show-icon
            >支持mybaits的 where,foreach,if 动态SQL; 比较字符 "<"请用
              "&amp;lt;"替代; 比较字符 "> "请用 "&amp;gt;"替代;
            </Alert>
          </div>
          <div class="contentWrap">
            <Input
              v-model="sql"
              type="textarea"
              placeholder="请填写SQL语句"
              :rows="10"
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
              v-if="!hideParamTable"
            >
              <template slot-scope="{ index, row, column }" slot="checkbox">
                <Checkbox
                  :value="row[column.key]"
                  @on-change="value => changeParams(value, index, column)"
                />
              </template>
              <template slot-scope="{ index, row }" slot="compare">
                <Select
                  :value="row.compare"
                  transfer
                  @on-change="value => changeParamCompare(value, index)"
                  v-if="row.setRequest"
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
        <div class="pageWrap">
          <div class="pageNumWrap">
            <Checkbox v-model="pageNumChecked" @on-change="handlePageNumChange"
            >返回结果分页</Checkbox
            >
            <div class="pageTip">
              当返回结果记录数大于500时请选择分页，不分页则最多返回500条记录。当无请求参数时，必须开启返回结果分页。
            </div>
          </div>
          <div v-show="pageNumChecked" style="margin-top:10px;">
            <Form
              ref="pageForm"
              :model="pageForm"
              :label-width="75"
              :rules="pageRuleValidate"
            >
              <FormItem label="每页条数" prop="pageSize">
                <Input
                  type="number"
                  v-model="pageForm.pageSize"
                  placeholder="请输入每页条数,不超过50条"
                  style="width: 300px"
                ><span slot="append">条</span></Input
                >
              </FormItem>
            </Form>
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
        <div
          class="cardWrap cardTableWrap"
          v-if="apiData.data.apiType === 'SQL'"
        >
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
                  :readonly="!!row.pageNumChecked && column.key === 'name'"
                ></Input>
              </template>
              <template slot-scope="{ index, row, column }" slot="type">
                <Select
                  :value="row.type"
                  transfer
                  @on-change="value => changeSqlParams(value, index, column)"
                  v-if="!row.pageNumChecked"
                >
                  <Option
                    v-for="(item, index) in sqlTypeOptions"
                    :value="item.value"
                    :key="index"
                  >{{ item.label }}</Option
                  >
                </Select>
                <Select
                  :value="row.type"
                  transfer
                  @on-change="value => changeSqlParams(value, index, column)"
                  v-if="!!row.pageNumChecked"
                >
                  <Option value="bigint">bigint</Option>
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
    <Spin v-show="confirmLoading" size="large" fix />
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import Test from "../common/test.vue";
import _ from "lodash";

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
    label: "等于",
    value: "="
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
  components: {
    Test
  },
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
    const validatePageSize = (rule, value, callback) => {
      if (!value && value !== 0) {
        callback(new Error("每页条数不能为空"));
      } else {
        if (value > 50 || value < 1) {
          callback(new Error("每页条数不能超过50条, 不少于1条"));
        } else if (!Number.isInteger(parseFloat(value))) {
          callback(new Error("每页条数必须为整数"));
        } else {
          callback();
        }
      }
    };
    return {
      confirmLoading: false,
      showTestPanel: false,
      toolItems: [
        {
          name: "属性",
          type: "property",
          iconName: "shuxing"
        },
        // {
        //   name: "版本",
        //   iconName: "banben",
        //   type: "version"
        // },
        {
          name: "保存",
          iconName: "baocun",
          type: "save"
        },
        {
          name: "测试",
          iconName: "ceshi",
          type: "test"
        },
        {
          name: "发布",
          iconName: "fabu",
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
        datasourceId: [
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
      setRequest: false,
      setResponse: false,
      hideParamTable: false,
      pageNumChecked: false,
      paramsColumns: [
        {
          title: "设为请求参数",
          key: "setRequest",
          slot: "checkbox",
          renderHeader: (h, params) => {
            return h(
              "div",
              {
                style: {
                  "margin-top": "7px"
                }
              },
              [
                h(
                  "checkbox",
                  {
                    props: {
                      value: this.setRequest
                    },
                    on: {
                      "on-change": value => {
                        console.log(value);
                        this.setRequest = value;
                        this.setParamsChoose(params.column, value);
                      }
                    }
                  },
                  "设为请求参数"
                )
              ]
            );
          }
        },
        {
          title: "设为返回参数",
          key: "setResponse",
          slot: "checkbox",
          renderHeader: (h, params) => {
            return h(
              "div",
              {
                style: {
                  "margin-top": "7px"
                }
              },
              [
                h(
                  "checkbox",
                  {
                    props: {
                      value: this.setResponse
                    },
                    on: {
                      "on-change": value => {
                        console.log(value);
                        this.setResponse = value;
                        this.setParamsChoose(params.column, value);
                      }
                    }
                  },
                  "设为返回参数"
                )
              ]
            );
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
      sortList: [],
      sql: "",
      pageForm: {
        pageSize: ""
      },
      pageRuleValidate: {
        pageSize: [
          {
            required: true,
            validator: validatePageSize,
            trigger: "change"
          }
        ]
      },
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
      ],
      hadTestSuccess: false
    };
  },
  computed: {},
  beforeMount() {
    const { data = {} } = this.apiData;
    const {
      id,
      apiType,
      datasourceId,
      tblName,
      memory,
      reqTimeout,
      sql,
      reqFields,
      pageSize
    } = data;
    this.getDataSourceIds(this.dbForm.datasourceType);
    if (!id) {
      return;
    }
    this.dbForm = {
      datasourceType: "MYSQL",
      datasourceId: datasourceId ? datasourceId + "" : "",
      tblName
    };
    this.envForm = {
      memory,
      reqTimeout
    };
    this.pageForm = { pageSize: pageSize ? pageSize : "" };
    this.pageNumChecked = !!pageSize;
    if (apiType === "SQL") {
      this.sql = sql;
      const reqValues = reqFields ? JSON.parse(reqFields) : [];
      this.sqlList = reqValues.map(item => {
        return { ...item };
      });
    }
    if (datasourceId && tblName) {
      this.getDbTables(datasourceId);
      this.getTableCols(tblName, true);
    }
  },
  methods: {
    handleToolShow(item) {
      const { data, originData } = this.apiData;
      const { type } = item;
      if (type === "property") {
        this.$emit("showApiForm", this.apiData);
      } else if (type === "save") {
        this.validateApi(type);
      } else if (type === "test") {
        if (!data.id) {
          this.$Message.info("请先保存API");
          return;
        }
        if (!_.isEqual(data, originData)) {
          this.$Message.info("api内容有更改，请先保存API");
          return;
        }
        this.validateApi(type);
      } else if (type === "release") {
        if (!this.hadTestSuccess) {
          this.$Message.info("请先测试API");
          return;
        }
        this.releaseApi();
      }
    },
    closeTestPanel() {
      this.showTestPanel = false;
    },
    setTestSuccess() {
      this.hadTestSuccess = true;
    },
    validateApi(action) {
      const { data } = this.apiData;
      const { apiType } = data;
      const isSave = action === "save";
      const {
        name,
        type,
        projectId,
        projectName,
        tempId,
        datasourceName,
        sql,
        path,
        ...rest
      } = data;
      console.log(
        type +
          tempId +
          sql +
          path +
          name +
          projectId +
          projectName +
          datasourceName
      );
      const { reqTimeout, memory } = this.envForm;
      let reqParams = {
        ...rest,
        ...this.dbForm,
        memory: parseFloat(memory),
        id: data.id || null,
        workspaceId: this.$route.query.workspaceId,
        pageSize: 0
      };
      if (reqParams.datasourceId) {
        reqParams.datasourceId = parseFloat(reqParams.datasourceId);
      }
      if (reqTimeout) {
        reqParams.reqTimeout = parseFloat(reqTimeout);
      }
      this.$refs["dbForm"].validate(valid => {
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
            if (this.pageNumChecked) {
              reqFields.push({
                name: "pageNum",
                type: "bigint",
                comment: "",
                compare: "=",
                pageNumChecked: true
              });
            }

            if (resFields.length === 0) {
              this.$Message.error("返回参数不能为空");
              return;
            }
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
            if (this.sqlList.some(item => !item.name)) {
              this.$Message.error("请求参数名称不能为空");
              return;
            }
            const reqes = this.sqlList.filter(item => !!item.name);
            if (reqes.length === 0) {
              this.$Message.error("请求参数不能为空");
              return;
            }
            reqParams = {
              ...reqParams,
              reqFields: reqes.length > 0 ? JSON.stringify(reqes) : "",
              sql: this.sql
            };
          }
          if (this.pageNumChecked) {
            this.$refs["pageForm"].validate(valid2 => {
              if (valid2) {
                reqParams.pageSize = parseInt(this.pageForm.pageSize);
                isSave
                  ? this.executeSaveApi(reqParams)
                  : this.showTest(reqParams);
              }
            });
            return;
          }
          isSave ? this.executeSaveApi(reqParams) : this.showTest(reqParams);
        }
      });
    },
    showTest(reqParams) {
      const { originData } = this.apiData;
      const isSame = Object.keys(reqParams).every(
        key => originData[key] + "" === reqParams[key] + ""
      );
      if (!isSame) {
        this.$Message.info("api内容有更改，请先保存API");
        return;
      }
      this.showTestPanel = true;
    },
    executeSaveApi(reqParams) {
      const { data } = this.apiData;
      this.confirmLoading = true;
      api
        .fetch(`/dss/data/api/save`, reqParams, "post")
        .then(() => {
          this.$emit("updateApiData", { ...data, ...reqParams });

          this.$Message.success("保存成功");
          this.confirmLoading = false;
        })
        .catch(() => {
          this.confirmLoading = false;
        });
    },
    releaseApi() {
      this.confirmLoading = true;
      api
        .fetch(
          `/dss/data/api/apimanager/online/${this.apiData.data.id}`,
          {},
          "post"
        )
        .then(() => {
          this.$Message.success("发布上线成功");
          this.confirmLoading = false;
        })
        .catch(() => {
          this.confirmLoading = false;
        });
    },
    addToSort(rowData) {
      const hit = this.sortList.find(
        item => item.columnName === rowData.columnName
      );
      if (hit) {
        return;
      }
      this.sortList = [...this.sortList, rowData].map((item, index) => {
        return { ...item, index: index + 1, type: "asc" };
      });
    },
    setParamsChoose(column, checked) {
      const { key } = column;
      this.paramsList = this.paramsList.map(item => {
        const tmp = { ...item };
        tmp[key] = checked;
        return tmp;
      });
    },
    changeParams(value, index, column) {
      const { key } = column;
      const datas = [...this.paramsList];
      datas[index][key] = value;
      this.paramsList = datas;
      this[key] = datas.every(item => !!item[key]);
    },
    changeParamCompare(value, index) {
      const datas = [...this.paramsList];
      datas[index]["compare"] = value;
      this.paramsList = datas;
    },
    changeSort(value, rowData) {
      this.sortList = [...this.sortList].map((item) => {
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
      datas[index][column.key] = value.trim();
      if (column.key === "type") {
        const demo = this.sqlTypeOptions.find(item => item.value === value);
        datas[index]["demo"] = demo.demo;
      }
      this.sqlList = datas;
    },
    addSqlParams(data = {}) {
      const datas = [...this.sqlList];
      datas.push({
        name: "",
        type: "string",
        demo: "liming",
        comment: "",
        ...data
      });
      this.sqlList = datas;
    },
    getDataSourceIds(datasourceType) {
      //获取数据源
      api
        .fetch(
          `/dss/data/api/datasource/connections?workspaceId=${this.$route.query.workspaceId}&type=${datasourceType}`,
          {},
          "get"
        )
        .then(res => {
          if (res && res.availableConns) {
            this.datasourceIds = [...res.availableConns];
          } else {
            this.datasourceIds = [];
          }
        });
    },
    getDbTables(datasourceId) {
      //获取数据表
      if (this.apiData.data.apiType === "SQL") {
        return;
      }
      this.dbTables = [];
      this.paramsList = [];
      this.sortList = [];
      this.destoryParamsTable();
      api
        .fetch(
          `/dss/data/api/datasource/tables?datasourceId=${datasourceId}`,
          {},
          "get"
        )
        .then(res => {
          if (res && res.allTables) {
            this.dbTables = res.allTables;
          }
        });
    },
    getTableCols(tableName, isInit) {
      //获取数据表的字段
      if (this.apiData.data.apiType === "SQL") {
        return;
      }
      this.paramsList = [];
      this.sortList = [];
      this.destoryParamsTable();
      api
        .fetch(
          `/dss/data/api/datasource/cols?datasourceId=${this.dbForm.datasourceId}&tableName=${tableName}`,
          {},
          "get"
        )
        .then(res => {
          if (res && res.allCols) {
            const { data = {} } = this.apiData;
            const { reqFields, orderFields, resFields } = data;
            const reqValues = reqFields && isInit ? JSON.parse(reqFields) : [];
            let resValues =
              resFields && isInit
                ? resFields.split(",").map(re => re.replace(/`/g, ""))
                : [];
            if (isInit) {
              const orderValues = orderFields ? JSON.parse(orderFields) : [];
              this.sortList = orderValues.map((ov, index) => {
                return { index: index + 1, ...ov, columnName: ov.name };
              });
            }
            this.paramsList = res.allCols.map(item => {
              const { columnName } = item;
              const hit = reqValues.find(re => re.name === columnName);
              const tmp = {
                ...item,
                compare: hit ? hit.compare : compareItems[0].value,
                setResponse: resValues.includes(columnName),
                setRequest: !!hit
              };
              return tmp;
            });
            this.setRequest = this.paramsList.every(item => !!item.setRequest);
            this.setResponse = this.paramsList.every(
              item => !!item.setResponse
            );
          }
        });
    },
    destoryParamsTable() {
      this.setRequest = false;
      this.setResponse = false;
      this.hideParamTable = true;
      this.$nextTick(() => {
        this.hideParamTable = false;
      });
    },
    handlePageNumChange(e) {
      const { data } = this.apiData;
      const { apiType } = data;
      if (apiType === "SQL") {
        if (e) {
          this.addSqlParams({
            name: "pageNum",
            type: "bigint",
            demo: "1",
            comment: "",
            pageNumChecked: true
          });
        } else {
          const index = this.sqlList.findIndex(
            item => item.name === "pageNum" && item.pageNumChecked
          );
          this.deleteSqlRow(index);
        }
      }
    },
    gotoCreateDatasource() {
      this.$router.push({
        name: "dataSourceAdministration",
        query: { workspaceId: this.$route.query.workspaceId }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.paramsContainer {
  padding: 0 0;
  .testTitle {
    width: 100%;
    height: 48px;
    @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
    @include bg-color(#f8f9fc, $dark-menu-base-color);
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 20px;
    border: 1px solid #dee4ec;
    @include border-color(
      rgba($color: #000000, $alpha: 0.2),
      $dark-border-color
    );
    border-left-width: 0;
    border-right-width: 0;
    box-sizing: border-box;
    margin-top: -5px;
    .title {

      font-size: 16px;
      @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
    }
    .icon {
      cursor: pointer;
    }
  }
  .toolBar {
    width: 100%;
    height: 48px;
    @include bg-color(#f8f9fc, $dark-menu-base-color);
    border: 1px solid #dee4ec;
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
      font-size: 16px;
      @include font-color(rgba(0, 0, 0, 0.65), $dark-text-color);
      & img {
        width: 16px;
      }
      .divider {
        width: 1px;
        height: 16px;
        @include bg-color(rgba(0, 0, 0, 0.25), $dark-border-color);
        &.last-divider {
          background: transparent;
        }
      }
      .icon {
        font-size: 20px;
      }
    }
  }
  .paramsCardContainer {
    padding: 0 20px;
    height: calc(100vh - 230px);
    overflow-y: auto;
    .cardWrap {
      padding: 20px 0;
      border-bottom: 1px solid #dee4ec;
      @include border-color(rgba(0, 0, 0, 0.2), $dark-border-color);
      .cardTitle {

        font-size: 16px;
        @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
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
    .pageWrap {
      border-bottom: 1px solid #dee4ec;
      padding-bottom: 5px;
      .pageNumWrap {
        display: flex;
        .pageTip {

          font-size: 12px;
          @include font-color(rgba(0, 0, 0, 0.45), $dark-text-color);
          margin-left: 10px;
        }
      }
    }
    .dataSourceTip {
      padding-left: 90px;
      margin-top: -12px;
      font-size: 12px;
      color: #515a6e;
      padding-bottom: 10px;
      .dataSourceLink {
        display: inline-block;
        cursor: pointer;
        color: #2e92f7;
        font-weight: 600;
        padding: 0px 2px;
      }
    }
  }
}
</style>
