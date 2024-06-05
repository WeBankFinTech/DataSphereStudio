<template>
  <div class="dataServicesContent">
    <Row class="content-top">
      <i-col span="10">
        <h3 class="title">{{$t('message.apiServices.apiTestInfo.params')}}</h3>
        <Form ref="searchFrom" class="search-from" :label-width="100" :disabled="isHistory" :model="conditionResult">
          <FormItem v-for="(item, index) in conditionResult.items" :prop="`items.${index}.defaultValue`" :key="item.id"  :rules="[{
            required: item.required,
            message: $t('message.apiServices.placeholder.emter'),
            trigger: 'blur'
          }]">
            <div class="label-class" :title="item.displayName || item.name" slot="label">
              {{ `${item.displayName || item.name}:` }}
              <div :title="item.details || ''" class="details-tip"><Icon type="md-help-circle" /></div>
            </div>
            <Input class="input-bar" :class="{ verificationValue: tip[item.name] }" :disabled="isHistory" @on-blur="verificationValue(item)" :type="inputType(item.type)" v-model="item.defaultValue" :placeholder="item.description || $t('message.apiServices.placeholder.emter')">
            </Input>
          </FormItem>
          <Button class="execute-button" type="primary" :disabled="isHistory" @click="search">{{buttonText}}</Button>
        </Form>
      </i-col>
      <i-col span="10">
        <template v-if="workInfo && workInfo.comment">
          <h3 class="title">{{$t('message.apiServices.query.comment')}}</h3>
          <Alert class="alert-bar" show-icon>{{ workInfo.comment }}</Alert>
        </template>
      </i-col>
    </Row>
    <results
      ref="currentConsole"
      getResultUrl="dss/apiservice"
      :isHistory="isHistory"
      :historyList="historyList"
      :work="workInfo"
      :dispatch="dispatch"
      :height="height"
      @executRun="executRun"
      @viewHistory="viewHistory"
      @updateHistory="updateHistory"
    >
    </results>
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
  name: "ModuleApiServiceExecute",
  components: {
    results,
  },
  props: {
    workInfo: {
      type: Object,
      default: () => ({})
    },
    showConditionList: {
      type: Array,
      default: () => ([])
    },
    selectCondition: {
      type: Array,
      default: () => ([])
    },
    conditionList: {
      type: Array,
      default: () => ([])
    },
    triggerSearchFn: {
      type: Function,
      default: () => (() => {})
    },
    isHistory: {
      type: Boolean,
      default: false
    },
    historyList: {
      type: Array,
      default: () => ([])
    },
  },
  data() {
    return {
      conditionShow: false,
      excuteLoading: false,
      tip: {},
      height: 500,
      historyFormData: {}
    }
  },
  computed: {
    buttonText() {
      return this.excuteLoading ? this.$t('message.apiServices.query.stop') : this.$t('message.apiServices.query.buttonText')
    },
    // iview动态表单组件需要的是object
    conditionResult() {
      const list = this.showConditionList.map(item => {
        if(this.historyFormData[item.name]) {
          return { ...item, defaultValue: this.historyFormData[item.name] }
        }
        return item
      })
      return {items: list}
    }
  },
  watch: {
    showConditionList() {
      setTimeout(() => {
        this.height = this.$refs.currentConsole.$el.clientHeight
      }, 500)
    }
  },
  mounted() {
    if (this.isHistory) {
      this.$refs.currentConsole.checkFromCache();
      this.getHistoryFormData()
    } else {
      this.initHomeTask()
    }
    this.height = this.$refs.currentConsole.$el.clientHeight
  },
  // 关闭页面，停止状态接口的轮询
  beforeDestroy() {
    this.$refs.currentConsole.killExecute(false);
  },
  methods: {
    // 判断首页是否还有再执行的任务
    async initHomeTask() {
      if (this.workInfo.shouldRuning) {
        try {
          this.excuteLoading = true;
          await this.getHistoryFormData()
          this.$nextTick(() => {
            // 调用控制台的初始化实例方法
            this.$refs.currentConsole.checkFromCache();
          })
        } catch (error) {
          this.excuteLoading = false;
        }
      }
    },
    // 获取历史筛选条件
    async getHistoryFormData() {
      const result = await api.fetch('/dss/apiservice/getHistoryQueryParams', {
        taskId: this.workInfo.taskID
      }, 'get')
      this.historyFormData = JSON.parse(result.data) || {}
    },
    // 验证发布和更新的默认值是否满足条件
    verificationValue (row) {
      const method = this.workInfo.method || '';
      let flag;
      if (method.toUpperCase() === 'GET' && row.defaultValue.length > 500) {
        this.$Message.error({ content: '不能超过500字符' });
        flag = true;
      } else {
        flag = false;

      }
      console.warn(row)
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
    // 过滤出勾选的参数
    confirmSelect() {
      this.showConditionList = this.conditionList.filter((item) => this.selectCondition.includes(item.id));
    },
    search() {
      if(Object.values(this.tip).some(i => i)) return this.$Message.error({ content: this.$t('message.apiServices.outlimit') });
      // 后续的执行逻辑
      if (this.excuteLoading) { // 停止执行
        if(!(this.workInfo && this.workInfo.execID)) return this.$Message.error({ content: this.$t('message.apiServices.uninittask') });
        api.fetch(`/entrance/${this.workInfo.execID}/kill`, {taskID: this.workInfo.taskID}, 'get').then(() => {
          // kill成功后，去查kill后的状态
          this.$refs.currentConsole.killExecute(true);
          const name = this.workInfo.name;
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
              const method = this.workInfo.method || '';
              if (method.toUpperCase() === 'GET') {
                const conditionStr = this.showConditionList.map(item => item.defaultValue).filter(item => item).join('');
                if (conditionStr.length > 3000) {
                  this.$Message.error({ content: '输入内容总长度不能超过3000字符' });
                  return;
                }
              }

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
      if (!this.workInfo) return;
      this.excuteLoading = true;
      let params = {}
      this.showConditionList.forEach((item) => {
        params[item.name] = item.defaultValue || '';
      })
      params.ApiServiceToken = this.workInfo.userToken;
      // apiservice
      let url = this.workInfo.path.startsWith("/")?'/dss/apiservice/execute' + this.workInfo.path:'/dss/apiservice/execute/' + this.workInfo.path;
      // 这里接口定义不同请求，参数体不一样
      api.fetch(url, {
        moduleName: 'dss-web',
        params: params,
      }, this.workInfo.method.toLowerCase()).then((rst) => {
        // 行内改版会返回execID和taskID
        this.triggerSearchFn({
          taskID: rst.taskId,
          execID: rst.execId
        })
        this.$nextTick(() => {
          // 调用控制台的初始化实例方法
          this.$refs.currentConsole.checkFromCache();
        })
      }).catch(() => {
        this.excuteLoading = false;
      });
    },
    viewHistory(params) {
      this.$emit('viewHistory', params)
    },
    updateHistory(params) {
      this.$emit('updateHistory', params)
    }
  }
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .dataServicesContent {
    height: 100%;
    display: flex;
    flex-direction: column;
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
    ::v-deep.ivu-form-item-label {
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
    ::v-deep.ivu-input-group-prepend {
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
      max-height: 600px;
      overflow-y: auto;
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
  
  