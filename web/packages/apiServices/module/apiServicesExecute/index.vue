<template>
  <div class="dataServicesContent">
    <div class="tap-bar">
      <Breadcrumb class="breadcrumb" separator="/">
        <BreadcrumbItem :to="{ name: 'Apiservices', query:{ workspaceId: $route.query.workspaceId }}">
          {{$t('message.apiServices.title')}}
        </BreadcrumbItem>
        <BreadcrumbItem>
          {{apiData ? apiData.aliasName : ''}}
        </BreadcrumbItem>
      </Breadcrumb>
    </div>
    <div class="execute-content">
      <Row class="content-top">
        <i-col span="10">
          <h3 class="title">{{$t('message.apiServices.apiTestInfo.params')}}</h3>
          <Form ref="searchFrom" class="search-from" :label-width="100" :model="conditionResult">
            <FormItem v-for="(item, index) in conditionResult.items" :prop="`items.${index}.defaultValue`" :key="item.id"  :rules="[{
              required: item.required,
              message: $t('message.apiServices.placeholder.emter'),
              trigger: 'blur'
            }]">
              <div class="label-class" :title="item.displayName || item.name" slot="label">
                {{ `${item.displayName || item.name}:` }}
                <div :title="item.details || ''" class="details-tip"><Icon type="md-help-circle" /></div>
              </div>
              <Input class="input-bar" :class="{ verificationValue: tip[item.name] }" @on-blur="verificationValue(item)" :type="inputType(item.type)" v-model="item.defaultValue" :placeholder="item.description || $t('message.apiServices.placeholder.emter')">
              </Input>
            </FormItem>
            <Button class="execute-button" type="primary" @click="search">{{buttonText}}</Button>
          </Form>
        </i-col>
        <i-col span="10">
          <template v-if="apiData && apiData.comment">
            <h3 class="title">{{$t('message.apiServices.query.comment')}}</h3>
            <Alert class="alert-bar" show-icon>{{ apiData.comment }}</Alert>
          </template>
        </i-col>
      </Row>
      <results ref="currentConsole" getResultUrl="dss/apiservice" :work="apiData" :dispatch="dispatch" :height="height" @executRun="executRun"></results>
    </div>
    <Modal :title="$t('message.apiServices.apiTestInfo.params')" v-model="conditionShow" @on-ok="confirmSelect">
      <CheckboxGroup v-model="selectCondition">
        <Checkbox v-for="item in conditionList" :key="item.id" :label="item.id" :disabled="!!item.required">
          <span>{{item.name}}</span>
        </Checkbox>
      </CheckboxGroup>
    </Modal>
  </div>
</template>
<script>
import results from '@dataspherestudio/shared/components/consoleComponent';
import api from '@dataspherestudio/shared/common/service/api';
export default {
  components: {
    results
  },
  data() {
    return {
      conditionList: [],
      selectCondition: [],
      conditionShow: false,
      showConditionList: [],
      apiData: null,
      excuteLoading: false,
      tip: {},
      height: 500
    }
  },
  created() {
    this.getExecutePath();
  },
  computed: {
    buttonText() {
      return this.excuteLoading ? this.$t('message.apiServices.query.stop') : this.$t('message.apiServices.query.buttonText')
    },
    // iview动态表单组件需要的是object
    conditionResult() {
      return {items: this.showConditionList}
    }
  },
  mounted() {
    this.height = this.$el.clientHeight - 325
  },
  methods: {
    // 验证发布和更新的默认值是否满足条件
    verificationValue (row) {
      let flag;
      if(row.defaultValue.length > 1024 && Number(row.type) !== 4) {
        this.$Message.error({ content: this.$t('message.apiServices.more1024') });
        flag = true;
      } else if(row.type == 4 && row.defaultValue.split('\n').length > 5000) {
        this.$Message.error({ content: this.$t('message.apiServices.moreline') });
        flag = true;
      } else {
        flag = false
      }
      this.$set(this.tip, row.name, flag)
    },
    // 参数默认值输入框类型
    inputType(typeNumer) {
      typeNumer = Number(typeNumer);
      if (typeNumer === 1) {
        return 'text';
      } else if (typeNumer === 2) {
        return 'number';
      } else if (typeNumer === 3) {
        return 'text';
      } else if (typeNumer === 4) {
        return 'textarea';
      }
    },
    executRun(val) {
      this.excuteLoading = val;
    },
    goBack() {
      this.$router.go(-1)
    },
    initApiParamInfo() {
      api.fetch('/dss/apiservice/apiParamQuery', {
        scriptPath: this.apiData.scriptPath,
        versionId: this.apiData.latestVersionId
      }, 'get').then((rst) => {
        if (rst.result) {
          this.conditionList = rst.result;
          this.selectCondition = this.conditionList.map((item) => {
            if (item.required == 1) {
              return item.id;
            }
          })
          this.confirmSelect();
        }
      })
    },
    // 获取api相关数据
    getExecutePath() {
      api.fetch('/dss/apiservice/queryById', {
        id: this.$route.query.id
      }, 'get').then((rst) => {
        if (rst.result) {
          this.apiData = rst.result;
          // 更改网页title
          document.title = rst.result.aliasName || rst.result.name;
          this.initApiParamInfo()
        }
      }).catch((err) => {
        console.error(err)
      });
    },
    // 关闭页面，停止状态接口的轮询
    beforeDestroy() {
      this.$refs.currentConsole.killExecute(false);
    },
    moreConditionAction() {
      this.conditionShow = true;
    },
    // 过滤出勾选的参数
    confirmSelect() {
      this.showConditionList = this.conditionList.filter((item) => this.selectCondition.includes(item.id));
    },
    search() {
      if(Object.values(this.tip).some(i => i)) return this.$Message.error({ content: this.$t('message.apiServices.outlimit') });
      // 后续的执行逻辑
      if (this.excuteLoading) { // 停止执行
        if(!(this.apiData && this.apiData.execID)) return this.$Message.error({ content: this.$t('message.apiServices.uninittask') });
        api.fetch(`/entrance/${this.apiData.execID}/kill`, {taskID: this.apiData.taskID}, 'get').then(() => {
          // kill成功后，去查kill后的状态
          this.$refs.currentConsole.killExecute(true);
          const name = this.apiData.name;
          this.$Notice.close(name);
          this.$Notice.warning({
            title: this.$t('message.apiServices.kill.title'),
            desc: `${this.$t('message.apiServices.kill.desc')} ${name} ！`,
            name: name,
            duration: 3,
          });
        }).catch(() => {
          this.$refs.currentConsole.killExecute(false);
        });
      } else {
        if (this.showConditionList.length > 0) {
          this.$refs.searchFrom.validate(valid => {
            if (valid) {
              this.executAction();
            } else {
              console.log(this.showConditionList)
            }
          })
        } else {
          this.executAction();
        }
      }
    },
    executAction() {
      if (!this.apiData) return;
      this.excuteLoading = true;
      let params = {}
      this.showConditionList.forEach((item) => {
        params[item.name] = item.defaultValue || '';
      })
      params.ApiServiceToken = this.apiData.userToken;
      // apiservice
      let url = this.apiData.path.startsWith("/")?'/dss/apiservice/execute' + this.apiData.path:'/dss/apiservice/execute/' + this.apiData.path;
      // 这里接口定义不同请求，参数体不一样
      api.fetch(url, {
        moduleName: 'dss-web',
        params: params,
      }, this.apiData.method.toLowerCase()).then((rst) => {
        // 行内改版会返回execID和taskID
        this.apiData.taskID = rst.taskId;
        this.apiData.execID = rst.execId;
        this.$nextTick(() => {
          // 调用控制台的初始化实例方法
          this.$refs.currentConsole.checkFromCache();
        })
      }).catch(() => {
        this.excuteLoading = false;
      });
    },
  }
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .dataServicesContent {
    height: 100%;
    .tap-bar {
      @include bg-color($light-base-color, $dark-base-color);
      margin-bottom: $padding-25;
      border-bottom: $border-width-base $border-style-base #dcdcdc;
      @include border-color($border-color-base, $dark-menu-base-color);
      .breadcrumb {
        padding: 25px 25px 0;
        @include bg-color($light-base-color, $dark-base-color);
      }
      .ivu-breadcrumb {
        font-size: 16px;
        /deep/.ivu-breadcrumb-item-separator {
          @include font-color(rgba(0, 0, 0, 0.65), $dark-text-color);
        }
      }
    }
    .verificationValue {
      .ivu-input {
        color: red!important;
        border-color: red;
      }
    }
    .none {
    display: none !important;
    }
    .back {
      margin-right: $margin;
      padding-right: $padding;
      font-size: 1.5rem;
      position: relative;
      color: $subsidiary-color;
      cursor: pointer;
      font-weight: 700;
      &::after {
        content: "";
        position: absolute;
        border-right: $border-width-base $border-style-base $border-color-base;
        height: 60%;
        top: 50%;
        right: 0;
        transform: translateY(-50%);
        width: 0;
      }
    }
  }
  .title {
      font-size: $font-size-large;
      font-weight: bold;
      padding-left: 12px;
      border-left: 3px solid #2d8cf0;
      @include font-color(rgba(0, 0, 0, 0.85), $dark-text-color);
      line-height: 17px;
      margin-bottom: 15px;
  }
  .search-from {
    display: flex;
    justify-content: flex-start;
    align-items: flex-start;
    flex-wrap: wrap;
    .input-bar {
      width: 200px;
      position: relative;
      margin-right: 20px;
    }
    /deep/.ivu-form-item-label {
      padding-right: 20px;
      position: relative;
      line-height: 1.2;
      display: flex;
    }
    .label-class {
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    .more-condition {
      color: $primary-color;
      margin-right: 20px;
      padding: 6px;
      cursor: pointer;
    }
    .execute-button {
      margin-left: 15px;
      margin-Bottom: 24px;
    }
    /deep/.ivu-input-group-prepend {
      padding: 0!important;

    }
  }
  .details-tip {
    position: absolute;
    color: $primary-color;
    right: 0;
    top: 0;
    font-size: $font-size-large;
    height: $percent-all;
    width: 20px;
    display: flex;
    align-items: $align-center;
    justify-content: $align-center;
    box-sizing: border-box;
    vertical-align: middle;
    font-weight: 700;
    &:hover {
      color: $success-color;
    }
  }
  .execute-content {
    border: $border-width-base $border-style-base  #dcdcdc;
    @include border-color($border-color-base, $dark-menu-base-color);
    border-radius: 2px;
    overflow: hidden;
    @include bg-color($light-base-color, $dark-base-color);
    margin: 0 25px;
    .content-top {
      padding: 25px;
      .alert-bar {
        margin-left: 15px;
      }
    }
    .log-panel {
      margin-top: 0px;
      border-top: none;
    }
  }
</style>

