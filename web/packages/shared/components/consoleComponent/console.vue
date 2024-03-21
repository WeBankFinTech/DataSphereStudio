<template>
  <div
    ref="bottomPanel"
    class="log-panel">
    <div class="workbench-tabs">
      <div class="workbench-tab-wrapper">
        <div class="workbench-tab">
          <div
            v-if="!isHistory"
            :class="{active: scriptViewState.showPanel == 'progress'}"
            class="workbench-tab-item"
            @click="showPanelTab('progress')">
            <span>{{ $t('message.common.tabs.progress') }}</span>
          </div>
          <div
            v-if="script.result"
            :class="{active: scriptViewState.showPanel == 'result'}"
            class="workbench-tab-item"
            @click="showPanelTab('result')">
            <span>{{ $t('message.common.tabs.result') }}</span>
          </div>
          <div
            v-if="isLogShow || isHistory"
            :class="{active: scriptViewState.showPanel == 'log'}"
            class="workbench-tab-item"
            @click="showPanelTab('log')">
            <span>{{ $t('message.common.tabs.log') }}</span>
          </div>
          <div
            v-if="!isHistory"
            :class="{active: scriptViewState.showPanel == 'history'}"
            class="workbench-tab-item"
            @click="showPanelTab('history')">
            <span>{{ $t('message.common.tabs.history') }}</span>
          </div>
        </div>
      </div>
      <div
        class="workbench-container">
        <we-progress
          v-if="scriptViewState.showPanel == 'progress' && !isHistory"
          :script="script"
          :execute="execute"
          :script-view-state="scriptViewState"
          @open-panel="openPanel"
        />
        <result
          v-if="scriptViewState.showPanel == 'result'"
          ref="result"
          :result="script.result"
          :script="script"
          :dispatch="dispatch"
          :getResultUrl="getResultUrl"
          :work="work"
          :script-view-state="scriptViewState"
          @on-set-change="changeResultSet"/>
        <log
          v-if="scriptViewState.showPanel == 'log'"
          :logs="script.log"
          :log-line="script.logLine"
          :script-view-state="scriptViewState"/>
        <History
          v-if="scriptViewState.showPanel == 'history'"
          :history="historyList"
          :run-type="script.runType"
          :script-view-state="scriptViewState"
          @viewHistory="viewHistory"
        />
      </div>
    </div>
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import util from '@dataspherestudio/shared/common/util';
import storage from '@dataspherestudio/shared/common/helper/storage';
import {debounce, isUndefined, isEmpty, last} from 'lodash';
import {Script} from './modal.js';
import result from './result.vue';
import History from './history.vue'
import log from './log.vue';
import weProgress from './progress.vue';
import Execute from '@dataspherestudio/shared/common/service/execute';
import mixin from '@dataspherestudio/shared/common/service/mixin';
export default {
  components: {
    result,
    log,
    weProgress,
    History
  },
  mixins: [mixin],
  props: {
    stop: {
      type: Boolean,
    },
    work: {
      type: Object
    },
    height: {
      type: [String, Number]
    },
    dispatch: {
      type: Function,
      required: true
    },
    getResultUrl: {
      type: String,
      defalut: `filesystem`
    },
    isHistory: {
      type: Boolean,
      default: false
    },
    historyList: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      execute: null,
      scriptViewState: {
        showPanel: this.isHistory ? 'result' : 'progress',
        cacheLogScroll: 0,
        bottomContentHeight: this.height || '250'
      },
      isBottomPanelFull: false,
      isLogShow: false,
      localLog: {
        log: { all: '', error: '', warning: '', info: '' },
        logLine: 1,
      },
      script: {
        result: null,
        steps: [],
        progress: {},
        log: { all: '', error: '', warning: '', info: '' },
        logLine: 1,
        resultList: null,
        history: []
      },
    }
  },
  watch: {
    stop(val, oldval) {
      if (oldval && this.execute) {
        // 先保留工作流页，应该会用到
        this.killExecute();
      }
    },
    executRun(val) {
      this.$emit('executRun', val)
    },
    height(val){
      this.scriptViewState.bottomContentHeight = val
    }
  },
  computed: {
    executRun() {
      return this.execute ? this.execute.run : false
    }
  },
  mounted() {
  },
  methods: {
    viewHistory(...rest) {
      this.$emit('viewHistory', ...rest)
    },
    killExecute(flag = false) {
      if (this.execute) {
        this.execute.trigger('stop');
        if (flag) {
          this.execute.trigger('kill');
        }
      }

    },
    createScript() {
      // 历史服务中work无runtype字段，使用文件后缀名截取
      let runType = this.work.runType
      if (!runType && this.work.filename) {
        const fileParts = this.work.filename.split('.');
        if (fileParts.length > 1) {
          runType = fileParts[fileParts.length - 1];
        }
      }
      this.script = new Script({
        id: this.work.id,
        title: this.work.name,
        runType: runType,
        scriptViewState: this.scriptViewState,
      });
    },
    createExecute(needQuery) {
      const data = {
        getResultUrl: this.getResultUrl,
        method: '/api/rest_j/v1/entrance/execute',
        websocketTag: this.work.id,
        data: {
          executeApplicationName: null,
          executionCode: null,
          runType: this.work.runType,
          params: null,
          postType: 'http',
          source: {
            scriptPath: null,
          },
          fromLine: this.script.logLine,
        },
      }
      this.execute = new Execute(data);
      if (needQuery) {
        this.queryState();
      }
    },
    queryState() {
      if (this.work.execID) {
        const option = {
          taskID: this.work.taskID,
          execID: this.work.execID,
          isRestore: true,
          id: this.work.id,
          nodeId: this.work.key || undefined,
          openLog: this.isHistory,
          isQueryHistory: this.isHistory
        }
        this.execute.halfExecute(option);
        this.monitoringData();
      }
    },
    monitoringData() {
      if (this.execute.taskID !== this.work.taskID) {
        return;
      }
      this.execute.on('log', (logs) => {
        this.localLog = this.convertLogs(logs);
        this.script.log = this.localLog.log;
        if (this.scriptViewState.showPanel === 'log') {
          this.localLogShow();
        }
      });
      this.execute.on('result', (ret) => {
        this.showPanelTab('result');
        const storeResult = {
          'headRows': ret.metadata,
          'bodyRows': ret.fileContent,
          'total': ret.totalLine,
          'type': ret.type,
          'cache': {
            offsetX: 0,
            offsetY: 0,
          },
          'path': this.execute.currentResultPath,
          'current': 1,
          'size': 20,
          hugeData: !!ret.hugeData,
          tipMsg: ret.tipMsg
        };
        if (this.execute.resultList[0]) {
          this.$set(this.execute.resultList[0], 'result', storeResult);
        }
        this.$set(this.script, 'resultSet', 0);
        this.script.result = storeResult;
        this.script.resultList = this.execute.resultList;
        this.script.resultSet = 0;
        // 获取过progress的情况下设置为1
        if (this.script.progress.current) {
          this.script.progress.current = 1;
        }
      });
      this.execute.on('progress', ({ progress, progressInfo, waitingSize }) => {
        // 这里progressInfo可能只是个空数组，或者数据第一个数据是一个空对象
        if (progressInfo.length && !isEmpty(progressInfo[0])) {
          progressInfo.forEach((newProgress) => {
            let newId = newProgress.id;
            let index = this.script.progress.progressInfo.findIndex((progress) => {
              return progress.id === newId;
            });
            if (index !== -1) {
              this.script.progress.progressInfo.splice(index, 1, newProgress);
            } else {
              this.script.progress.progressInfo.push(newProgress);
            }
          });
        } else {
          this.script.progress.progressInfo = [];
        }

        if (progress == 1) {
          let runningScripts = storage.get(this._running_scripts_key, 'local') || {};
          delete runningScripts[this.script.nodeId];
          storage.set(this._running_scripts_key, runningScripts, 'local');
        }

        this.script.progress.current = progress;

        if (waitingSize !== null && waitingSize >= 0) {
          this.script.progress.waitingSize = waitingSize;
        }
      });
      this.execute.on('steps', (status) => {
        if (this.executeLastStatus === status) {
          return;
        }
        this.executeLastStatus = status;
        if (status === 'Inited') {
          this.script.steps = ['Submitted', 'Inited'];
        } else {
          const lastStep = last(this.script.steps);
          if (this.script.steps.indexOf(status) === -1) {
            this.script.steps.push(status);
            // 针对可能有WaitForRetry状态后，后台会重新推送Scheduled或running状态的时候
          } else if (lastStep !== status) {
            this.script.steps.push(status);
          }
        }
        if (status !== 'Inited' && this.script.steps.length === 1) {
          this.script.steps.unshift('ellipsis');
        }
      });
      this.execute.on('status', (status) => {
        this.script.status = status;
        if(['Failed', 'Cancelled', 'Timeout'].includes(status) && this.isHistory) {
          this.openPanel('log')
        }
      });
      this.execute.on('querySuccess', ({ type, task }) => {
        if (this.isHistory) return;
        this.$emit("updateHistory", task)
        const costTime = util.convertTimestamp(task.costTime);
        this.script.progress = {
          ...this.script.progress,
          costTime
        };
        const name = this.script.title;
        this.$Notice.close(name);
        this.$Notice.success({
          title: this.$root.$t('message.common.notice.querySuccess.title'),
          desc: '',
          render: (h) => {
            return h('span', {
              style: {
                'word-break': 'break-all',
                'line-height': '20px',
              },
            }, `${this.script.title} ${type}${this.$root.$t('message.common.notice.querySuccess.render')}：${costTime}！`);
          },
          name,
          duration: 3,
        });
      });
      this.execute.on('notice', ({ type, msg, autoJoin }) => {
        const name = this.script.title;
        const label = autoJoin ? `${this.$root.$t('message.common.script')}${this.script.title} ${msg}` : msg;
        this.$Notice.close(name);
        this.$Notice[type]({
          title: this.$root.$t('message.common.notice.notice.title'),
          name,
          desc: '',
          duration: 5,
          render: (h) => {
            return h('span', {
              style: {
                'word-break': 'break-all',
                'line-height': '20px',
              },
            }, label);
          },
        });
      });
      this.execute.on('costTime', (time) => {
        this.script.progress.costTime = util.convertTimestamp(time);
      });
      this.execute.on('history', (info) => {
        if (this.isHistory) return;
        this.$emit("updateHistory", info)
      })
    },
    resetQuery() {
      this.showPanelTab(this.isHistory ? 'result' : 'progress');
      this.isLogShow = false;
      this.localLog = {
        log: { all: '', error: '', warning: '', info: '' },
        logLine: 1,
      };
      this.script.progress = {
        current: null,
        progressInfo: [],
        waitingSize: null,
        costTime: null,
      };
      this.script.log = { all: '', error: '', warning: '', info: '' };
      this.script.logLine = 1;
      this.script.steps = ['Submitted'];
      this.script.diagnosis = null;
      this.script.result = {
        headRows: [],
        bodyRows: [],
        type: 'EMPTY',
        total: 0,
        path: '',
        cache: {},
      };
      this.script.resultList = null;
    },
    showPanelTab(type) {
      this.scriptViewState.showPanel = type;
      this.script.showPanel = type;
      if (type === 'log') {
        this.localLogShow();
      }
    },
    localLogShow() {
      if (!this.debounceLocalLogShow) {
        this.debounceLocalLogShow = debounce(() => {
          if (this.localLog) {
            this.script.log = Object.freeze({ ...this.localLog.log });
            this.script.logLine = this.localLog.logLine;
          }
        }, 1500);
      }
      this.debounceLocalLogShow();
    },
    changeResultSet(data, cb) {
      const resultSet = isUndefined(data.currentSet) ? this.script.resultSet : data.currentSet;
      const findResult = this.script.resultList[resultSet];
      const resultPath = findResult && findResult.path;
      const hasResult = Object.prototype.hasOwnProperty.call(this.script.resultList[resultSet], 'result');
      if (!hasResult) {
        const pageSize = 5000;
        const url = `/${this.getResultUrl}/openFile`;
        let params = {
          path: resultPath,
          pageSize,
        }
        // 如果是api执行需要带上taskId
        if (this.getResultUrl !== 'filesystem') {
          params.taskId = this.work.taskID
        }
        api.fetch(url, {
          ...params
        }, 'get')
          .then((ret) => {
            let result = {}
            if (ret.display_prohibited) {
              result = {
                'headRows': [],
                'bodyRows': [],
                'total': ret.totalLine,
                'type': ret.type,
                'path': resultPath,
                hugeData: true,
                tipMsg: localStorage.getItem("locale") === "en" ? ret.en_msg : ret.zh_msg
              };
            } else if (ret.metadata && ret.metadata.length >= 500) {
              result = {
                'headRows': [],
                'bodyRows': [],
                'total': ret.totalLine,
                'type': ret.type,
                'path': resultPath,
                hugeData: true
              };
            } else {
              result = {
                'headRows': ret.metadata,
                'bodyRows': ret.fileContent,
                // 如果totalLine是null，就显示为0
                'total': ret.totalLine ? ret.totalLine : 0,
                // 如果内容为null,就显示暂无数据
                'type': ret.fileContent ? ret.type : 0,
                'cache': {
                  offsetX: 0,
                  offsetY: 0,
                },
                'path': resultPath,
                'current': 1,
                'size': 20,
              };
            }
            this.$set(this.script.resultList[resultSet], 'result', result);
            this.$set(this.script, 'resultSet', resultSet);
            this.script.result = result;
            this.script.resultSet = resultSet;
            this.script.showPanel = 'result';
            cb();
          }).catch(() => {
            cb();
          });
      } else {
        this.script.result = this.script.resultList[resultSet].result;
        this.$set(this.script, 'resultSet', resultSet);
        this.script.resultSet = resultSet;
        this.script.showPanel = 'result';
        cb();
      }
    },
    openPanel(type) {
      if (type === 'log') {
        this.isLogShow = true;
        this.showPanelTab(type);
      }
    },
    checkFromCache() {
      // 每次右键控制台，都会创建一个新实例，所以把上一个执行的实例先停掉
      if (this.execute) {
        this.killExecute();
      }
      this.resetQuery();
      this.createScript();
      this.$nextTick(() => {
        this.createExecute(true);
      })
    },
    getLogs() {
      api.fetch(`/entrance/${this.work.execID}/log`, {
        fromLine: this.fromLine,
        size: -1,
      }, 'get')
        .then((rst) => {
          this.localLog = this.convertLogs(rst.log);
          this.script.log = this.localLog.log;
          this.script.logLine = this.fromLine = rst.fromLine;
          if (this.scriptViewState.showPanel === 'log') {
            this.localLogShow();
          }
        });
    },
    convertLogs(logs) {
      const tmpLogs = this.localLog || {
        log: { all: '', error: '', warning: '', info: '' },
        logLine: 1,
      };
      let hasLogInfo = Array.isArray(logs) ? logs.some((it) => it.length > 0) : logs;
      if (!hasLogInfo) {
        return tmpLogs;
      }
      const convertLogs = util.convertLog(logs);
      Object.keys(convertLogs).forEach((key) => {
        const convertLog = convertLogs[key];
        if (convertLog) {
          tmpLogs.log[key] += convertLog + '\n';
        }
        if (key === 'all') {
          tmpLogs.logLine += convertLog.split('\n').length;
        }
      });
      return tmpLogs;
    }
  }
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .log-panel {
    margin-top: 1px;
    border-top: $border-width-base $border-style-base $border-color-base;
    @include border-color($border-color-base, $dark-border-color-base);
    @include bg-color($light-base-color, $dark-base-color);
    flex: 1;
    .workbench-tabs {
      position: $relative;
      height: 100%;
      overflow: hidden;
      box-sizing: border-box;
      z-index: 3;
      display: flex;
      flex-flow: column;
      .workbench-tab-wrapper {
        height: 34px;
        display: flex;
        border-top: $border-width-base $border-style-base #dcdcdc;
        border-bottom: $border-width-base $border-style-base #dcdcdc;
        @include border-color($border-color-base, $dark-menu-base-color);
        .workbench-tab {
          flex: 1;
          display: flex;
          flex-direction: row;
          flex-wrap: nowrap;
          justify-content: flex-start;
          align-items: flex-start;
          height: 32px;
          background-color: $body-background;
          @include bg-color($light-base-color, $dark-base-color);
          width: calc(100% - 45px);
          overflow: hidden;
          &.work-list-tab {
            overflow-x: auto;
            overflow-y: hidden;
            &::-webkit-scrollbar {
              width: 0;
              height: 0;
              background-color: transparent;
            }
            .list-group>span {
              white-space: nowrap;
              display: block;
              height: 0;
            }
          }
          .workbench-tab-item {
            text-align: center;
            border-top: none;
            display: inline-block;
            height: 32px;
            line-height: 32px;
            @include bg-color($light-base-color, $dark-submenu-color);
            @include font-color($workspace-title-color, $dark-workspace-title-color);
            cursor: pointer;
            min-width: 100px;
            max-width: 200px;
            overflow: hidden;
            margin-right: 2px;
            border: 1px solid #eee;
            @include border-color($border-color-base, $dark-border-color-base);
            &.active {
              margin-top: 1px;
              @include bg-color($light-base-color, $dark-base-color);
              @include font-color($primary-color, $dark-primary-color);
              border-radius: 4px 4px 0 0;
              border-left: 1px solid $border-color-base;
              border-right: 1px solid $border-color-base;
              border-top: 1px solid $border-color-base;
              @include border-color($border-color-base, $dark-border-color-base);
            }
          }
        }
        .workbench-tab-control {
            flex: 0 0 45px;
            text-align: right;
            @include bg-color($light-base-color, $dark-base-color);
            border-left: $border-width-base $border-style-base $border-color-split;
            .ivu-icon {
              font-size: $font-size-base;
              margin-top: 8px;
              margin-right: 2px;
              cursor: pointer;
              &:hover {
                  color: $primary-color;
              }
              &.disable {
                  color: $btn-disable-color;
              }
            }
        }
        .workbench-tab-button {
            flex: 0 0 30px;
            text-align: center;
            @include bg-color($light-base-color, $dark-base-color);
            .ivu-icon {
                font-size: $font-size-base;
                margin-top: 8px;
                cursor: pointer;
            }
        }
      }
      .workbench-container {
        height: calc(100% - 36px);
        &.node {
            height: 100%;
        }
        flex: 1;
        @keyframes ivu-progress-active {
            0% {
                opacity: .3;
                width: 0;
            }
            100% {
                opacity: 0;
                width: 100%;
            }
        }
      }
    }
  }
</style>

