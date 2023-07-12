<template>
  <div
    class="progress"
    :style="{'overflow-y':'auto', 'padding-left': status ? '40px' : 0, height: height +'px'}">
    <we-toolbar
      :status="status"
      :application="script.application"
      :execute="execute"
      :progress="script.progress"
      @open-panel="openPanel"></we-toolbar>
    <!-- 进度步骤条 -->
    <steps
      :step-list="steps"
      :percent="percent"
      :costTime="costTime"
      v-if="steps.length"></steps>
    <!-- 错误信息 -->
    <div v-if="taskInfo.failedReason || taskInfo.solution" class="alert-tips" :class="errTipColor">
      <Icon :type="taskInfo.status == 'Failed'?'ios-close-circle-outline':'ios-alert-outline'" size="14" />
      <span style="padding-left: 10px; flex:1">{{taskInfo.failedReason}} </span>
      <Button
        v-if="taskInfo.solution !== undefined"
        type="error"
        size="small"
        @click="clickTipButton"
      >{{ taskInfo.solution && taskInfo.solution.solutionUrl?this.$t('message.common.viewSolution'):this.$t('message.common.reporterr')}}</Button>
    </div>
    <Row
      class="total-progress"
      v-if="isWaittingSizeShow">
      <Col
        span="2"
        class="title"
      >{{ $t('message.common.progress.title') + '：' }}</Col>
      <Col span="12">
        <span>{{ $t('message.common.progress.watingList', { num: waitingSize }) }}</span>
      </Col>
    </Row>
    <!-- 子任务进度 -->
    <Table
      class="progress-table"
      v-if="isStatusOk"
      :columns="columns"
      :data="progressInfo"
      size="small"
      stripe
      border/>
  </div>
</template>
<script>
import steps from './steps.vue';
import weToolbar from './toolbar_progress.vue';
import api from '@dataspherestudio/shared/common/service/api';
/**
 * 脚本执行进度tab面板
 * ! 1. 与工作流节点执行console.vue 共用
 */
export default {
  components: {
    steps,
    weToolbar,
  },
  props: {
    execute: Object,
    scriptViewState: Object,
    script: Object
  },
  data() {
    let that = this;
    return {
      columns: [
        {
          title: this.$t('message.common.progress.columns.id.title'),
          key: 'id',
          width: 200,
        },
        {
          title: this.$t('message.common.progress.columns.progress.title'),
          key: 'action',
          render(h, { row }) {
            return h('div', {
              class: 'progress-wrap',
            }, [
              h('div', {
                class: 'progress-item progress-waiting',
                style: that.waitingStyle(row),
              }, {

              }),
              h('div', {
                class: 'progress-item progress-success',
                style: that.successStyle(row),
              }),
              h('div', {
                class: 'progress-item progress-failed',
                style: that.failedStyle(row),
              }),
            ]);
          },
        },
        {
          title: this.$t('message.common.progress.columns.taskNum.title'),
          key: 'action',
          render(h, { row }) {
            return h('div', [
              h('span', {
                class: 'progress-text',
                style: {
                  'color': '#2d8cf0',
                },
              }, `${that.$t('message.common.progress.columns.taskNum.status.running')}：${row.runningTasks || 0}`),
              h('span', {
                class: 'progress-text',
                style: {
                  'color': '#19be6b',
                },
              }, `${that.$t('message.common.progress.columns.taskNum.status.success')}：${row.succeedTasks || 0}`),
              h('span', {
                class: 'progress-text',
                style: {
                  'color': '#ed4014',
                },
              }, `${that.$t('message.common.progress.columns.taskNum.status.fail')}：${row.failedTasks || 0}`),
              h('span', {
                class: 'progress-text',
              }, `${that.$t('message.common.progress.columns.taskNum.status.totalTasks')}：${row.totalTasks || 0}`),
            ]);
          },
        },
      ],
      taskInfo: {}
    };
  },
  computed: {
    steps() {
      return this.script.steps || []
    },
    status() {
      return this.script.status
    },
    percent() {
      const numMulti = function (num1, num2) {
        var baseNum = 0;
        try { baseNum += num1.toString().split(".")[1].length; } catch (e) {
          //
        }
        try { baseNum += num2.toString().split(".")[1].length; } catch (e) {
          //
        }
        return Number(num1.toString().replace(".", "")) * Number(num2.toString().replace(".", "")) / Math.pow(10, baseNum);
      };
      return numMulti(this.script.progress.current , 100);
    },
    waitingSize() {
      return this.script.progress.waitingSize
    },
    progressInfo() {
      return this.script.progress.progressInfo
    },
    costTime() {
      return this.script.progress.costTime || ``
    },
    isWaittingSizeShow() {
      // 当waitingSize等于null时，waitingSize >= 0为true；
      return (this.status === 'Scheduled' || this.status === 'Inited') && (this.waitingSize !== null && this.waitingSize >= 0);
    },
    isStatusOk() {
      return this.status && !(this.status === 'Scheduled' || this.status === 'Inited');
    },
    height() {
      return this.scriptViewState.bottomContentHeight - 34 //减去tab高度
    },
    errTipColor() {
      return {
        'warn-color': this.taskInfo.status != 'Failed',
        'error-color': this.taskInfo.status == 'Failed'
      }
    }
  },
  mounted() {
    if (this.steps.length && this.taskInfo.errCode === undefined && this.script.history) {
      const ret  = this.script.history[0]
      if (ret) {
        this.taskInfo = {
          solution: ret.solution,
          errDesc: ret.errDesc,
          errCode: ret.errCode,
          status: ret.status,
          taskId: ret.taskID,
          failedReason: ret.failedReason
        }
      }
    }
  },
  methods: {
    successStyle({ failedTasks, runningTasks, succeedTasks, totalTasks }) {
      let succeedPercent = (succeedTasks / totalTasks) * 100;
      let runningPercent = (runningTasks / totalTasks) * 100;
      let borderStyle = {};
      if (failedTasks) {
        borderStyle = {
          'border-bottom-right-radius': 0,
          'border-top-right-radius': 0,
        };
      }
      if (runningTasks) {
        Object.assign(borderStyle, {
          'border-top-left-radius': 0,
          'border-bottom-left-radius': 0,
        });
      }
      if (succeedPercent) {
        Object.assign(borderStyle, {
          'min-width': '10px',
        });
      } else {
        Object.assign(borderStyle, {
          'min-width': '0px',
        });
      }
      return Object.assign(borderStyle, {
        'left': runningPercent + '%',
        'width': succeedPercent + '%',

      });
    },
    waitingStyle({ failedTasks, runningTasks, succeedTasks, totalTasks }) {
      let runningPercent = (runningTasks / totalTasks) * 100;
      let borderStyle = {};
      if (succeedTasks || failedTasks) {
        borderStyle = {
          'border-bottom-right-radius': 0,
          'border-top-right-radius': 0,
        };
      }
      if (runningPercent) {
        Object.assign(borderStyle, {
          'min-width': '10px',
        });
      } else {
        Object.assign(borderStyle, {
          'min-width': '0px',
        });
      }
      return Object.assign(borderStyle, {
        'width': runningPercent + '%',
      });
    },
    failedStyle({ failedTasks, runningTasks, succeedTasks, totalTasks }) {
      let leftPercent = ((succeedTasks + runningTasks) / totalTasks) * 100;
      let failedPercent = (failedTasks / totalTasks) * 100;
      let borderStyle = {};
      if (runningTasks || succeedTasks) {
        borderStyle = {
          'border-top-left-radius': 0,
          'border-bottom-left-radius': 0,
        };
      }
      if (failedPercent) {
        Object.assign(borderStyle, {
          'min-width': '10px',
        });
      } else {
        Object.assign(borderStyle, {
          'min-width': '0px',
        });
      }
      return Object.assign(borderStyle, {
        'left': leftPercent + '%',
        'width': failedPercent + '%',
      });
    },
    openPanel(type) {
      this.$emit('open-panel', type);
    },
    updateErrorMsg(data) {
      this.taskInfo = data
    },
    clickTipButton() {
      if (this.taskInfo.solution && this.taskInfo.solution.solutionUrl) {
        window.open(this.taskInfo.solution.solutionUrl, '_blank')
      } else if(this.taskInfo.errCode || this.taskInfo.errDesc) {
        api.fetch('/dss/guide/solution/reportProblem', {
          requestUrl: `/jobhistory/${this.taskInfo.taskID}/get`,
          queryParams: {},
          requestBody: {},
          requestHeaders: {},
          responseBody: {
            taskID: this.taskInfo.taskID,
            errCode: this.taskInfo.errCode,
            errDesc: this.taskInfo.errDesc
          }
        }).then(()=>{
          this.$Message.success(this.$t('message.common.reported'))
        })
      }
    }
  },
};
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .progress {
    @include bg-color($light-base-color, $dark-base-color);
    position: relative;
    margin-bottom: 0;
    .ivu-table-wrapper {
      border: none;
      .ivu-table th {
        @include font-color($light-base-color, $dark-base-color);
        @include bg-color($primary-color, $dark-primary-color);
      }
    }
    .progress-wrap {
        position: $relative;
        width: 100%;
        height: 10px;
        background: $background-color-select-hover;
        border-radius: 100px;
    }
    .progress-item {
        position: absolute;
        top: 0;
        left: 0;
        height: 100%;
        transition: all $transition-time linear;
        border-radius: 10px;
    }
    .progress-success {
        background: $success-color;
    }
    .progress-failed {
        background: $error-color;
    }
    .progress-waiting {
        background: $primary-color;
    }
    .progress-waiting:after {
        content: '';
        opacity: 0;
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        @include bg-color($light-base-color, $dark-base-color);
        border-radius: 10px;
        animation: ivu-progress-active 2s $ease-in-out infinite;
    }
    .progress-text {
        font-size: $font-size-small;
        margin-right: 10px;
    }
    .total-progress {
        padding: 6px 0;
        color: $title-color;
        @include font-color($workspace-title-color, $dark-workspace-title-color);
        .title {
            text-align: center;
        }
    }

    .alert-tips {
      display: flex;
      align-items: center;
      margin-bottom: 10px;
      margin: 0 20px 15px 20px;
      padding: 5px;
      border-radius: 4px;
    }
    .error-color {
      color: $error-color;
      background-color: #f1c7c763;
    }
    .warn-color {
      color: $warning-color;
      background-color: #f79f531f;
    }
  }
</style>

