<template>
  <div
    ref="bottomPanel"
    class="log-panel">
    <div class="workbench-tabs">
      <div class="workbench-tab-wrapper">
        <div class="workbench-tab">
          <div
            :class="{active: scriptViewState.showPanel == 'progress'}"
            class="workbench-tab-item"
            @click="showPanelTab('progress')">
            <span>{{ $t('message.workBench.body.script.script.tabs.progress') }}</span>
          </div>
          <div
            v-if="script.result"
            :class="{active: scriptViewState.showPanel == 'result'}"
            class="workbench-tab-item"
            @click="showPanelTab('result')">
            <span>{{ $t('message.workBench.body.script.script.tabs.result') }}</span>
          </div>
          <div
            v-if="isLogShow"
            :class="{active: scriptViewState.showPanel == 'log'}"
            class="workbench-tab-item"
            @click="showPanelTab('log')">
            <span>{{ $t('message.workBench.body.script.script.tabs.log') }}</span>
          </div>
        </div>
        <div
          class="workbench-tab-button">
          <Icon
            type="ios-close"
            size="20"
            @click="closeConsole"></Icon>
        </div>
      </div>
      <div
        v-show="!scriptViewState.bottomPanelMin"
        class="workbench-container">
        <we-progress
          v-if="scriptViewState.showPanel == 'progress'"
          :current="script.progress.current"
          :waiting-size="script.progress.waitingSize"
          :steps="script.steps"
          :status="script.status"
          :application="script.application"
          :progress-info="script.progress.progressInfo"
          :cost-time="script.progress.costTime"
          @open-panel="openPanel"/>
        <result
          v-if="scriptViewState.showPanel == 'result'"
          ref="result"
          :result="script.result"
          :script="script"
          :height="scriptViewState.cacheBottomPanelHeight"
          @on-analysis="openAnalysisTab"
          @on-set-change="changeResultSet"/>
        <log
          v-if="scriptViewState.showPanel == 'log'"
          :logs="script.log"
          :log-line="script.logLine"
          :script-view-state="scriptViewState"
          :height="scriptViewState.cacheBottomPanelHeight"/>
      </div>
    </div>
  </div>
</template>
<script>
import api from '@/js/service/api';
import util from '@/js/util';
import storage from '@/js/helper/storage';
import {debounce, isUndefined, isEmpty, last} from 'lodash';
import {Script} from '../modal.js';
import result from '@js/module/workbench/script/result.vue';
import log from '@js/module/workbench/script/log.vue';
import weProgress from '@js/module/workbench/script/progress.vue';
import Execute from '@js/module/workbench/script/execute';
export default {
  components: {
    result,
    log,
    weProgress,
  },
  props: {
    node: {
      type: Object,
    },
    stop: {
      type: Boolean,
    }
  },
  data() {
    return {
      execute: null,
      scriptViewState: {
        showPanel: 'progress',
        height: 0,
        bottomPanelHeight: 0,
        cacheBottomPanelHeight: 0,
        bottomPanelMin: false,
        cacheLogScroll: 0,
      },
      isBottomPanelFull: false,
      isLogShow: false,
      localLog: {
        log: { all: '', error: '', warning: '', info: '' },
        logLine: 1,
      },
      script: {
        result: {},
        steps: [],
        progress: {},
        log: { all: '', error: '', warning: '', info: '' },
        logLine: 1,
        resultList: null,
      },
    }
  },
  async created() {
    this.checkFromCache();
  },
  beforeDestroy() {
    if (this.execute) {
      this.execute.nodeExecuteStop = true;
    }
  },
  watch: {
    stop(val, oldval) {
      if (oldval && this.execute) {
        this.execute.nodeExecuteStop = true;
      }
    }
  },
  methods: {
    createScript() {
      const node = this.node;
      this.script = new Script({
        nodeId: `${node.title}_${node.createTime}`,
        title: node.title,
        scriptViewState: this.scriptViewState,
      });
      this.dispatch('IndexedDB:addNodeCache', {
        nodeId: `${node.title}_${node.createTime}`,
        value: this.script,
      });
    },
    createExecute(needQuery) {
      const data = {
        method: '/api/rest_j/v1/entrance/execute',
        websocketTag: this.node.id,
        data: {
          executeApplicationName: null,
          executionCode: null,
          runType: 'node',
          params: null,
          postType: 'http',
          source: {
            scriptPath: null,
          },
          fromLine: this.script.logLine,
        },
      }
      this.execute = new Execute(data, this.$t.bind(this));
      this.execute.nodeExecuteStop = false;
      if (needQuery) {
        this.queryState();
      }
    },
    queryState() {
      if (this.node.runState.execID) {
        const option = {
          taskID: this.node.runState.taskID,
          execID: this.node.runState.execID,
          isRestore: true,
          nodeId: this.node.id,
        }
        this.execute.halfExecute(option);
        this.monitoringData();
      }
    },
    monitoringData() {
      if (this.execute.taskID !== this.node.runState.taskID) {
        return;
      }
      this.execute.on('log', (logs) => {
        this.localLog = this.convertLogs(logs);
        if (this.scriptViewState.showPanel === 'log') {
          this.localLogShow();
        }
        this.updateNodeCache(['log']);
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
        };
        this.$set(this.execute.resultList[0], 'result', storeResult);
        this.$set(this.script, 'resultSet', 0);
        this.script.result = storeResult;
        this.script.resultList = this.execute.resultList;
        this.script.resultSet = 0;
        this.updateNodeCache(['result', 'resultList', 'resultSet']);
        // 获取过progress的情况下设置为1
        if (this.script.progress.current) {
          this.script.progress.current = 1;
          this.updateNodeCache('progress');
        }
      });
      this.execute.on('progress', ({ progress, progressInfo, waitingSize }) => {
        if (this.executeLastProgress === progress) {
          return;
        }
        this.executeLastProgress = progress;
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
          progressInfo = [];
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
        this.updateNodeCache('progress');
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
          this.updateNodeCache(['steps']);
        }
        if (status !== 'Inited' && this.script.steps.length === 1) {
          this.script.steps.unshift('ellipsis');
        }
      });
      this.execute.on('status', (status) => {
        this.script.status = status;
        this.updateNodeCache('status');
      });
      this.execute.on('stateEnd', () => {
        this.updateNodeCache('log');
      });
      this.execute.on('error', () => {
        this.updateNodeCache('log');
      });
      this.execute.on('querySuccess', ({ type, task }) => {
        const costTime = util.convertTimestamp(task.costTime);
        this.script.progress.costTime = costTime;
        const name = this.script.title;
        this.$Notice.close(name);
        this.$Notice.success({
          title: this.$t('message.workBench.body.script.script.notice.querySuccess.title'),
          desc: '',
          render: (h) => {
            return h('span', {
              style: {
                'word-break': 'break-all',
                'line-height': '20px',
              },
            }, `${this.script.title} ${type}${this.$t('message.workBench.body.script.script.notice.querySuccess.render')}：${costTime}！`);
          },
          name,
          duration: 3,
        });
      });
      this.execute.on('notice', ({ type, msg, autoJoin }) => {
        const name = this.script.title;
        const label = autoJoin ? `${this.$t('message.workBench.body.script.script.text')}${this.script.title} ${msg}` : msg;
        this.$Notice.close(name);
        this.$Notice[type]({
          title: this.$t('message.workBench.body.script.script.notice.notice.title'),
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
        this.updateNodeCache('progress');
      });
    },
    resetQuery() {
      this.showPanelTab('progress');
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
      this.updateNodeCache('showPanel');
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
            this.updateNodeCache('log');
          }
        }, 1500);
      }
      this.debounceLocalLogShow();
    },
    // 将数组格式化成json形式。
    async openAnalysisTab() {
      if (this.biLoading) return this.$Message.warning(this.$t('message.constants.warning.biLoading'));
      this.biLoading = true;
      const vsBiUrl = this.getVsBiUrl();

      let result = await api.fetch(`${vsBiUrl}/api/rest_j/v1/visualis/view`, {
        fileName: this.script.title,
        executionCode: this.script.executionCode || this.script.data,
        resultPath: this.script.result.path,
        runType: this.script.runType,
        resultNumber: this.script.result.current > 0 ? this.script.result.current - 1 : 0,
      }).catch(() => {
        this.biLoading = false;
      });
      if (result) {
        let { id, projectId } = result;
        const title = `${this.script.title}_project${projectId}_set${this.script.resultSet}.bi`;
        const md5Path = util.md5(title);
        this.dispatch('Workbench:add', {
          id: md5Path,
          title: title,
          saveAs: true,
          type: 'visualAnalysis',
          params: {
            viewId: id,
            projectId,
          },
        }, () => {
          this.biLoading = false;
        });
      }
    },
    changeResultSet(data, cb) {
      const resultSet = isUndefined(data.currentSet) ? this.script.resultSet : data.currentSet;
      const findResult = this.script.resultList[resultSet];
      const resultPath = findResult && findResult.path;
      const hasResult = this.script.resultList[resultSet].hasOwnProperty('result');
      if (!hasResult) {
        const pageSize = 5000;
        const url = '/filesystem/openFile';
        api.fetch(url, {
          path: resultPath,
          pageSize,
        }, 'get')
          .then((ret) => {
            const result = {
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
            this.$set(this.script.resultList[resultSet], 'result', result);
            this.$set(this.script, 'resultSet', resultSet);
            this.script.result = result;
            this.script.resultSet = resultSet;
            this.script.showPanel = 'result';
            this.updateNodeCache(['result', 'resultList', 'resultSet', 'showPanel']);
            cb();
          }).catch(() => {
            cb();
          });
      } else {
        this.script.result = this.script.resultList[resultSet].result;
        this.$set(this.script, 'resultSet', resultSet);
        this.script.resultSet = resultSet;
        this.script.showPanel = 'result';
        this.updateNodeCache(['result', 'resultList', 'resultSet', 'showPanel']);
        cb();
      }
    },
    closeConsole() {
      this.$emit('close-console');
    },
    openPanel(type) {
      if (type === 'log') {
        this.isLogShow = true;
        this.showPanelTab(type);
      }
    },
    checkFromCache() {
      this.resetQuery();
      const nodeId = `${this.node.title}_${this.node.createTime}`;
      this.dispatch('IndexedDB:getNodeCache', {
        nodeId,
        cb: (cache) => {
          let needQuery = true;
          if (cache) {
            this.script = Object.assign(new Script({
              nodeId,
              title: this.node.title,
              scriptViewState: this.scriptViewState,
            }), cache);
            this.localLog = {
              log: cache.log,
              logLine: cache.logLine,
            };
            if (!cache.log.all) {
              this.getLogs();
            }
            if (['Succeed', 'Failed', 'Cancelled', 'Timeout'].indexOf(this.script.status) !== -1) {
              needQuery = false;
            }
          } else {
            this.createScript();
          }
          this.$nextTick(() => {
            this.createExecute(needQuery);
          })
        }
      })
    },
    updateNodeCache(key) {
      this.dispatch('IndexedDB:updateNodeCache', {
        nodeId: this.script.nodeId,
        key,
        value: this.script,
      });
    },
    getLogs() {
      api.fetch(`/entrance/${this.node.runState.execID}/log`, {
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
          this.updateNodeCache(['log']);
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
