<template>
  <div class="dataServicesContainer">
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
    <div ref="tab-list-scroll" class="workbench-tab work-list-tab">
      <div
        v-for="(item, index) in tabslist" :key="item.taskID"
        :class="{ active: item.taskID == currentTaskId }"
        class="workbench-tab-item"
        ref="work_item"
      >
        <we-title
          :index="index"
          :work="item"
          @on-choose="onChooseWork"
          @on-remove="onRemove"
        />
      </div>
    </div>
    <div class="execute-content" v-for="(item, index) in tabslist" :key="item.filename" v-show="item.taskID == currentTaskId">
      <ExecuteResult
        :ref="'executeResult'+ item.taskID"
        :key="item.filename"
        :workInfo="item"
        :conditionList="conditionList"
        :selectCondition="selectCondition"
        :showConditionList="showConditionList"
        :historyList="historyList"
        :isHistory="index !== 0"
        :triggerSearchFn="triggerSearchFn"
        @viewHistory="viewHistory"
        @updateHistory="updateHistory"/>
    </div>
    <div />
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
import ExecuteResult from './result.vue';
import api from '@dataspherestudio/shared/common/service/api';
import title from "./title.vue";
export default {
  components: {
    ExecuteResult,
    "we-title": title,
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
      height: 500,
      tabslist: [],
      currentTaskId: '',
      historyList: []
    }
  },
  created() {
    this.getExecutePath();
  },
  methods: {
    // 获取历史记录列表
    async getHistoryList() {
      let homeTabItem = {
        ...this.apiData,
        filename: this.apiData.aliasName || this.apiData.name,
        noClose: true,
        taskID: ''
      }
      const result = await api.fetch('/dss/apiservice/getApiHistory', {
        apiId: this.apiData.id,
        apiVersionId: this.apiData.latestVersionId
      }, 'get')
      this.historyList = (result.data || []).map(item => ({ execID: item.strongerExecId, ...item }))
      if (this.historyList[0] && ['Inited','Scheduled', 'Running'].includes(this.historyList[0].status)) {
        homeTabItem = {
          ...homeTabItem,
          ...this.historyList[0],
          shouldRuning: true
        }
        this.currentTaskId = this.historyList[0].taskID
      }
      this.tabslist.push(homeTabItem)
    },
    updateHistory(params) {
      const data = {
        ...this.historyList[0],
        fileName: this.apiData.aliasName || this.apiData.name,
        ...params
      }
      if (params.runningTime) {
        data.costTime = params.runningTime
      }
      this.historyList.splice(0, 1, data)
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
          this.getHistoryList()
        }
      }).catch((err) => {
        console.error(err)
      });
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
    // 过滤出勾选的参数
    confirmSelect() {
      this.showConditionList = this.conditionList.filter((item) => this.selectCondition.includes(item.id));
    },
    onChooseWork(params) {
      if (params.taskID === this.currentTaskId) {
        this.$Notice.info({
          desc: `当前任务【${params.taskID}】已在当前tab页打开，可切换进度/结果/日志栏目查看！`,
          duration: 3,
        });
        return
      }
      this.currentTaskId = params.taskID
    },
    triggerSearchFn(params) {
      this.historyList.unshift({
        costTime: '',
        createdTime: Date.now(),
        status: '',
        taskID: params.taskID,
        execID: params.execID,
        id: params.taskID
      })
      this.tabslist[0].taskID = params.taskID;
      this.tabslist[0].execID = params.execID;
      this.onChooseWork(params)
    },
    viewHistory(params) {
      if (this.tabslist.findIndex((item => item.taskID == params.taskID)) === -1) {
        this.tabslist.push(params)
      }
      this.onChooseWork(params)
    },
    onRemove(params) {
      const removeIndex = this.tabslist.findIndex((item => item.taskID === params.taskID))
      if (removeIndex !== -1) {
        this.tabslist.splice(removeIndex, 1)
        if (this.currentTaskId === params.taskID) {
          this.onChooseWork(this.tabslist[removeIndex - 1])
        }
      }
    }
  }
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .dataServicesContainer {
    height: 100%;
    // display: flex;
    // flex-direction: column;
    .tap-bar {
      @include bg-color($light-base-color, $dark-base-color);
      // margin-bottom: $padding-25;
      border-bottom: $border-width-base $border-style-base #dcdcdc;
      @include border-color($border-color-base, $dark-menu-base-color);
      .breadcrumb {
        padding: 25px 25px 0;
        @include bg-color($light-base-color, $dark-base-color);
      }
      .ivu-breadcrumb {
        font-size: 16px;
        ::v-deep.ivu-breadcrumb-item-separator {
          @include font-color(rgba(0, 0, 0, 0.65), $dark-text-color);
        }
      }
    }
  }
  .execute-content {
    flex: 1;
    border: $border-width-base $border-style-base  #dcdcdc;
    @include border-color($border-color-base, $dark-menu-base-color);
    border-radius: 2px;
    overflow: hidden;
    @include bg-color($light-base-color, $dark-base-color);
    margin: 0 25px;
    height: calc(100% - 110px);
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
  .workbench-tab {
    display: flex;
    flex-direction: row;
    flex-wrap: nowrap;
    justify-content: flex-start;
    align-items: center;
    height: 45px;
    @include bg-color($light-base-color, $dark-base-color);
    width: calc(100% - 45px);
    overflow: hidden;
    &.work-list-tab {
      overflow-x: auto;
      overflow-y: hidden;
      padding-left: 16px;
      &::-webkit-scrollbar {
        height: 6px;
      }
      &::-webkit-scrollbar-thumb {
        box-shadow: inset 0 0 2px rgba(0, 0, 0, 0.2);
        border-radius: 3px;
        background-color: #787d8b;
      }
      &::-webkit-scrollbar-track {
        box-shadow: inset 0 0 2px rgba(0, 0, 0, 0.2);
        border-radius: 3px;
      }
      .list-group > span {
        white-space: nowrap;
        display: block;
        height: 0;
      }
    }
    .workbench-tab-item {
      text-align: center;
      border-top: none;
      display: inline-block;
      height: 24px;
      line-height: 24px;
      @include bg-color(#e1e5ea, $dark-workspace-body-bg-color);
      @include font-color(
        $workspace-title-color,
        $dark-workspace-title-color
      );
      cursor: pointer;
      min-width: 100px;
      max-width: 200px;
      overflow: hidden;
      margin-right: 8px;
      border-radius: 12px;
      &.active {
        margin-top: 1px;
        @include bg-color(#e8eef4, $dark-workspace-body-bg-color);
        color: $primary-color;
        @include font-color($primary-color, $dark-primary-color);
      }

      &:hover {
        @include bg-color(#d1d7dd, $dark-workspace-body-bg-color);
      }
    }
  }
</style>

