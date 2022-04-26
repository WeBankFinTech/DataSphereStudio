<template>
  <div class="apiTest-wrap">
    <div class="apiTest-title" v-if="!fromDataService">
      {{ $t("message.dataService.apiTest.apiTest") }}
    </div>
    <div class="apiTest-head">
      <div class="api-select" v-if="!fromDataService">
        <Select
          filterable
          v-model="currentApiId"
          style="width:200px"
          @on-change="handleChangeApi"
        >
          <Option v-for="item in apiList" :value="item.id" :key="item.id">{{
            item.name
          }}</Option>
        </Select>
      </div>
      <div class="api-path" v-if="currentApi">
        API Path: {{ currentApi.path }}
      </div>
    </div>

    <div class="apiTest-main">
      <div class="apiTest-panel">
        <template v-if="currentApi">
          <div class="panel-title" v-if="currentApi.reqFields">
            {{ $t("message.dataService.apiTest.requestParam") }}
          </div>
          <Table :columns="columns" :data="currentApi.reqFields" v-if="currentApi.reqFields">
            <template slot-scope="{ row }" slot="value">
              <Input
                v-model="currentParams[row.name]"
                :placeholder="
                  row.type.includes('Array')
                    ? $t('message.dataService.apiTest.holderArray')
                    : $t('message.dataService.apiTest.holderInput')
                "
              ></Input>
            </template>
          </Table>
        </template>
        <div class="panel-btn">
          <Button
            type="primary"
            size="large"
            @click="test"
            :loading="loading"
          >{{ $t("message.dataService.apiTest.start") }}</Button
          >
        </div>
      </div>
      <div class="apiTest-panel">
        <div class="panel-title">
          {{ $t("message.dataService.apiTest.requestLog") }}
        </div>
        <div class="panel-content">
          <p v-for="log in logs" :key="log">{{ log }}</p>
        </div>
        <div class="panel-title">
          {{ $t("message.dataService.apiTest.response") }}
        </div>
        <div class="panel-content-response">
          <Input
            v-model="response"
            type="textarea"
            :autosize="{ minRows: 10, maxRows: 20 }"
          ></Input>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
export default {
  props: {
    apiData: {
      type: Object,
      default: () => {}
    },
    fromDataService: {
      type: Boolean
    }
  },
  data() {
    return {
      currentApiId: this.$route.params.apiId ? +this.$route.params.apiId : "",
      currentApi: null,
      currentParams: {},
      apiList: [],
      columns: [
        {
          title: this.$t("message.dataService.apiTest.name"),
          key: "name"
        },
        {
          title: this.$t("message.dataService.apiTest.type"),
          key: "type"
        },
        {
          title: this.$t("message.dataService.apiTest.value"),
          key: "value",
          slot: "value"
        }
      ],
      logs: [],
      response: "",
      loading: false
    };
  },
  created() {
    if (!this.fromDataService) {
      this.getApiList();
    } else {
      this.handleChangeApi();
    }
  },
  methods: {
    getApiList() {
      api
        .fetch(
          "/dss/data/api/list",
          {
            workspaceId: this.$route.query.workspaceId
          },
          "get"
        )
        .then(res => {
          if (res.list) {
            const apiList = res.list.map(i => i.apis);
            this.apiList = [].concat(...apiList);
            if (this.currentApiId) {
              this.handleChangeApi(this.currentApiId);
            }
          }
        })
        .catch(err => {
          console.error(err);
        });
    },
    handleChangeApi(val) {
      const currentApi = this.fromDataService
        ? this.apiData
        : this.apiList.find(i => i.id == val);
      if (currentApi) {
        try {
          const reqFields = JSON.parse(currentApi.reqFields);
          this.currentApi = {
            ...currentApi,
            reqFields: reqFields
          };
          // 构造请求参数
          const params = {};
          this.currentApi.reqFields.forEach(row => {
            params[row.name] = "";
          });
          this.currentParams = params;
        } catch (error) {
          // reqFields为空,不传参数的类型
          this.currentApi = currentApi;
          this.currentParams = {};
        }
        // 重置请求log和response
        this.logs = [];
        this.response = "";
      }
    },
    test() {
      if (!this.currentApi) {
        this.$Message.warning(
          this.$t("message.dataService.apiTest.api_not_selected")
        );
      } else {
        const data = {};
        this.valid = true;
        // 存在reqFields为空,不传参数的类型
        if (this.currentApi.reqFields) {
          this.currentApi.reqFields.forEach(row => {
            // 检查输入值是否为空
            if (!this.currentParams[row.name] || !this.currentParams[row.name].trim()) {
              this.valid = false;
            }
            if (row.type.includes("Array")) {
              // 如果是数组类型，逗号分隔且trim，并过滤掉无效参数
              data[row.name] = this.currentParams[row.name]
                .split(",")
                .map(i => i.trim())
                .filter(i => !!`${i}`);
            } else {
              data[row.name] = this.currentParams[row.name];
            }
          });
        }
        if (!this.valid) {
          return this.$Message.warning(
            this.$t("message.dataService.apiTest.req_params_not_value")
          );
        }
        this.loading = true;
        api
          .fetch(
            `/dss/data/api/test/${this.currentApi.path}`,
            data,
            "post"
          )
          .then(res => {
            this.loading = false;
            if (res.response) {
              this.logs = res.response.log.split("\n");
              this.response = JSON.stringify(res.response.resList, null, 4);
              this.$emit("testSuccess");
            }
          })
          .catch(err => {
            this.loading = false;
            console.error(err);
          });
      }
    }
  }
};
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.apiTest-wrap {
  padding: 0 24px;
  @include bg-color(#fff, $dark-base-color);
  .apiTest-title {
    margin-bottom: 15px;
    padding-bottom: 10px;
    font-size: 16px;
    @include font-color(#333, $dark-text-color);
    font-weight: bold;
    border-bottom: 1px solid #dee4ec;
    @include border-color(#dee4ec, $dark-border-color);
  }
  .apiTest-head {
    .api-path {
      margin: 15px 0;
      font-size: 14px;
      @include font-color(#333, $dark-text-color);
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
        @include font-color(#333, $dark-text-color);
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
        border: 1px solid #dee4ec;
        @include border-color(#dee4ec, $dark-border-color);
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
