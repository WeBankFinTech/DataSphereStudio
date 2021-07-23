<template>
  <div class="apiTest-wrap">
    <div class="apiTest-title">{{$t('message.dataService.apiTest.apiTest')}}</div>
    <div class="apiTest-head">
      <div class="api-select">
        <Select v-model="currentApiId" style="width:200px" @on-change="handleChangeApi">
          <Option v-for="item in apiList" :value="item.id" :key="item.path">{{ item.label }}</Option>
        </Select>
      </div>
      <div class="api-path" v-if="currentApi">
        API Path: {{currentApi.path}}
      </div>
    </div>

    <div class="apiTest-main">
      <div class="apiTest-panel">
        <template v-if="currentApi">
          <div class="panel-title">{{$t('message.dataService.apiTest.requestParam')}}</div>
          <Table :columns="columns" :data="currentApi.params">
            <template slot-scope="{row}" slot="value">
              <Input v-model="currentParams[row.name]" placeholder="请输入" style="width: 140px"></Input>
            </template>
          </Table>
        </template>
        <div class="panel-btn">
          <Button type="primary" @click="test">{{$t('message.dataService.apiTest.start')}}</Button>
        </div>
      </div>
      <div class="apiTest-panel">
        <div class="panel-title">{{$t('message.dataService.apiTest.requestLog')}}</div>
        <div class="panel-content">
          <p v-for="log in logs" :key="log">{{log}}</p>
        </div>
        <div class="panel-title">{{$t('message.dataService.apiTest.response')}}</div>
        <div class="panel-content-response">
          <Input v-model="response" type="textarea" :autosize="{minRows: 10,maxRows: 20}"></Input>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
// import api from "@/common/service/api";
export default {
  data() {
    return {
      currentApiId: this.$route.query.apiId || '',
      currentApi: null,
      currentParams: {},
      apiList: [
        { 
          id: '1',
          label: 'Api1', 
          path: '/user/api1', 
          params: [
            {
              name: 'id',
              type: 'INT',
              required: '是',
              value: ''
            }
          ]
        },
        { 
          id: '2',
          label: 'Api2', 
          path: '/user/api2', 
          params: [
            {
              name: 'id',
              type: 'INT',
              required: '是',
              value: ''
            },
            {
              name: 'name',
              type: 'String',
              required: '是',
              value: 'luban'
            }
          ]
        },
      ],
      columns: [
        {
          title: this.$t("message.dataService.apiTest.name"),
          key: 'name'
        },
        {
          title: this.$t("message.dataService.apiTest.type"),
          key: 'type'
        },
        {
          title: this.$t("message.dataService.apiTest.required"),
          key: 'required'
        },
        {
          title: this.$t("message.dataService.apiTest.value"),
          key: "value",
          slot: "value"
        }
      ],
      logs: [
        '[INFO][14:00:00] resource group',
        '[INFO][14:00:00] resource group is very hard, too long to read, resource group is very hard, too long to read, { too long to read too long to read too long to read }'
      ],
      response: JSON.stringify({
        "data": "ok",
        "message": "ok",
        "status": 1
      }, null, 4) 
    }
  },
  created() {
    // 获取api相关数据
    // api.fetch('/dss/apiservice/queryById', {
    //   id: this.$route.query.id,
    // }, 'get').then((rst) => {
    //   if (rst.result) {
    //     // api的基础信息
    //     this.apiData = rst.result;
    //   }
    // }).catch((err) => {
    //   console.error(err)
    // });
  },
  methods: {
    handleChangeApi(val) {
      this.currentApi = this.apiList.find(i => i.id == val);
      // 构造请求参数
      const params = {};
      this.currentApi.params.forEach(row => {
        params[row.name] = row.value;
      })
      this.currentParams = params;
    },
    test() {
      if (!this.currentApi) {
        this.$Message.warning(this.$t('message.dataService.apiTest.api_not_selected')); 
      } else {
        console.log(this.currentParams)
      }
    }
  },
}
</script>

<style lang="scss" scoped>
.apiTest-wrap {
  padding: 0 24px;
  background: #fff;
  .apiTest-title {
    margin-bottom: 15px;
    padding-bottom: 10px;
    font-size: 16px;
    color: #333;
    font-weight: bold;
    border-bottom: 1px solid #DEE4EC;
  }
  .apiTest-head {
    .api-path {
      margin: 15px 0;
      font-size: 14px;
      color: #333;
      line-height: 20px;
    }
  }
  .apiTest-main {
    display: flex;
    .apiTest-panel {
      flex: 1;
      margin-right: 24px;
      &:last-child {
        margin-right: 0;
      }
      .panel-title {
        margin-bottom: 20px;
        font-size: 14px;
        color: #333;
        line-height: 20px;
      }
      .panel-btn {
        margin-top: 20px;
      }
      .panel-content {
        margin-bottom: 20px;
        padding: 10px;
        height: 300px;
        overflow: auto;
        border-radius: 4px;
        border: 1px solid #DEE4EC;
        p {
          font-size: 14px;
          line-height: 24px;
        }
      }
      .panel-content-response {
        margin-bottom: 20px;
      }
    }
  }
}
</style>