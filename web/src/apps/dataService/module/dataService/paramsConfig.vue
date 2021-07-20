<template>
  <div class="paramsContainer">
    <div class="cardWrap">
      <div class="cardTitle">数据表</div>
      <div class="formWrap">
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
      }
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
    .formWrap {
      margin-top: 20px;
    }
  }
}
</style>
