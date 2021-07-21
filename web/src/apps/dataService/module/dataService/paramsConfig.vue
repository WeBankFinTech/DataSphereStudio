<template>
  <div class="paramsContainer">
    <div class="cardWrap">
      <div class="cardTitle">数据表</div>
      <div class="contentWrap">
        <Form
          ref="dbForm"
          :model="dbForm"
          :label-width="90"
          :rules="ruleValidate"
        >
          <FormItem label="数据源类型" prop="dataSource">
            <Select v-model="dbForm.dataSource" style="width:300px">
              <Option value="GET">GET</Option>
              <Option value="POST">POST</Option>
            </Select>
          </FormItem>
          <FormItem label="数据源名称" prop="dataSourceId">
            <Select v-model="dbForm.dataSourceId" style="width:300px">
              <Option value="GET">GET</Option>
              <Option value="POST">POST</Option>
            </Select>
          </FormItem>
          <FormItem label="数据表名称" prop="tblName">
            <Select v-model="dbForm.tblName" style="width:300px">
              <Option value="GET">GET</Option>
              <Option value="POST">POST</Option>
            </Select>
          </FormItem>
        </Form>
      </div>
    </div>
    <div class="cardWrap">
      <div class="cardTitle">环境配置</div>
      <div class="contentWrap">
        <Form
          ref="envForm"
          :model="envForm"
          :label-width="90"
          :rules="envRuleValidate"
        >
          <FormItem label="内存" prop="memory">
            <Select v-model="envForm.memory" style="width:300px">
              <Option value="GET">GET</Option>
              <Option value="POST">POST</Option>
            </Select>
          </FormItem>
          <FormItem label="超时时间" prop="reqTimeout">
            <Input
              type="text"
              v-model="envForm.reqTimeout"
              placeholder="请输入业务名称"
              style="width: 300px"
            ></Input>
          </FormItem>
        </Form>
      </div>
    </div>
    <div class="cardWrap">
      <div class="cardTitle">选择参数</div>
      <div class="contentWrap">
        <Table
          :columns="paramsColumns"
          :data="paramsList"
          :loading="tableLoading"
        >
          <template slot-scope="{ row }" slot="fieldSort">
            <div class="fieldSort" @click="addToSort(row)">
              添加到字段排序
            </div>
          </template>
        </Table>
      </div>
    </div>
    <div class="cardWrap">
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
  </div>
</template>
<script>
export default {
  data() {
    return {
      dbForm: {
        dataSource: "",
        dataSourceId: "",
        tblName: ""
      },
      ruleValidate: {
        dataSource: [
          {
            required: true,
            message: "请选择数据源类型",
            trigger: "blur"
          }
        ],
        dataSourceId: [
          {
            required: true,
            message: "请选择数据源名称",
            trigger: "blur"
          }
        ],
        tblName: [
          {
            required: true,
            message: "请选择数据表名称",
            trigger: "blur"
          }
        ]
      },
      envForm: {
        memory: "",
        reqTimeout: ""
      },
      envRuleValidate: {
        memory: [
          {
            required: true,
            message: "请选择数据源类型",
            trigger: "blur"
          }
        ],
        reqTimeout: [
          {
            required: true,
            message: "请选择数据源名称",
            trigger: "blur"
          }
        ]
      },
      paramsColumns: [
        {
          title: "设为请求参数",
          key: "setRequest",
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
      paramsList: [
        {
          Comment: "部门id",
          fieldType: "bigint(20)",
          columnName: "id"
        }
      ],
      tableLoading: false,
      sortColumns: [
        {
          title: "序号",
          key: "index"
        },
        {
          title: "字段名",
          key: "id"
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
        },
        {
          index: 2,
          id: "b",
          type: "asc"
        },
        {
          index: 3,
          id: "bigint(20)",
          type: "asc"
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
  methods: {
    removeWork(tabData) {
      console.log(tabData);
    },
    onChooseWork(tabData) {
      console.log(tabData);
    },
    handleToolShow(data) {
      console.log(data);
    },
    addToSort(rowData) {
      console.log(rowData);
    },
    setParamsChoose(column) {
      console.log(column);
    },
    changeSort(value, rowData) {
      console.log(value);
      console.log(rowData);
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
    }
  }
};
</script>

<style lang="scss" scoped>
@import "@/common/style/variables.scss";
.paramsContainer {
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
    }
  }
}
</style>
