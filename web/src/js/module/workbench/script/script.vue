<template>
  <we-panel diretion="vertical">
    <we-panel-item
      ref="topPanel"
      :index="1"
      :min-size="100"
      class="editor-panel">
      <editor
        ref="editor"
        :script="script"
        :work="work"
        :script-type="work.type"
        @on-save="save"
        @on-run="run"
        @on-stop="stop"/>
      <Spin
        v-if="saveLoading"
        size="large"
        class="new-sidebar-spin"
        fix/>
    </we-panel-item>
    <we-panel-item
      ref="bottomPanel"
      :index="2"
      :height="scriptViewState.bottomPanelHeight"
      :min-size="32"
      class="log-panel"
      @on-change="changeBottomPanel">
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
            <div
              :class="{active: scriptViewState.showPanel == 'history'}"
              class="workbench-tab-item"
              @click="showPanelTab('history')">
              <span>{{ $t('message.workBench.body.script.script.tabs.history') }}</span>
            </div>
          </div>
          <div
            class="workbench-tab-button">
            <Dropdown
              trigger="click"
              placement="bottom-end"
              transfer
              @on-click="configLogPanel">
              <Icon type="md-list" />
              <DropdownMenu slot="list">
                <DropdownItem
                  name="fullScreen"
                  v-if="!isBottomPanelFull">{{ $t('message.constants.logPanelList.fullScreen') }}</DropdownItem>
                <DropdownItem
                  name="releaseFullScreen"
                  v-else>{{ $t('message.constants.logPanelList.releaseFullScreen') }}</DropdownItem>
                <DropdownItem
                  name="min"
                  v-if="!scriptViewState.bottomPanelMin">{{ $t('message.constants.logPanelList.min') }}</DropdownItem>
                <DropdownItem
                  name="releaseMin"
                  v-else>{{ $t('message.constants.logPanelList.releaseMin') }}</DropdownItem>
              </DropdownMenu>
            </Dropdown>
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
          <history
            v-if="scriptViewState.showPanel == 'history'"
            :history="script.history"
            :run-type="script.runType"/>
        </div>
      </div>
    </we-panel-item>
  </we-panel>
</template>
<script>
import { isEmpty, find, dropRight, toArray, values, isString, findIndex, last, isUndefined, debounce } from 'lodash';
import api from '@/js/service/api';
import storage from '@/js/helper/storage';

import util from '@/js/util';
import { Script } from '../modal.js';
import Execute from './execute';
import result from './result.vue';
import editor from './editor.vue';
import log from './log.vue';
import history from './history.vue';
import weProgress from './progress.vue';
export default {
  components: {
    result,
    editor,
    log,
    history,
    weProgress,
  },
  props: {
    work: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      script: {
        data: '',
        oldData: '',
        result: {},
        steps: [],
        progress: {},
        resultList: null,
      },
      scriptViewState: {
        showPanel: 'progress',
        height: 0,
        bottomPanelHeight: 0,
        cacheBottomPanelHeight: 0,
        bottomPanelMin: false,
        cacheLogScroll: 0,
      },
      execute: null,
      postType: 'socket',
      userName: '',
      biLoading: false,
      saveLoading: false,
      isLogShow: false,
      autoSaveTimer: null,
      isBottomPanelFull: false,
    };
  },
  computed: {
    isPanelMin() {
      if (!this.script) return null;
      const log = this.script.log;
      const emptyLog = isEmpty(log) || !(log.all || log.error || log.info || log.warning);
      const emptyHistory = isEmpty(this.script.history);
      const emptyResult = !this.script.result || isEmpty(this.script.result);
      const emptyProgress = isEmpty(this.script.progress.progressInfo);
      return emptyLog && emptyHistory && emptyResult && emptyProgress;
    },
  },
  watch: {
    'script.data': function() {
      if (!this.work.ismodifyByOldTab) {
        this.work.unsave = this.script.data != this.script.oldData;
      }
    },
    'script.oldData': function() {
      if (!this.work.ismodifyByOldTab) {
        this.work.unsave = this.script.data != this.script.oldData;
      }
    },
    'execute.run': function(val) {
      this.work.data.running = this.script.running = val;
      this.dispatch('IndexedDB:recordTab', this.work);
    },
    'isPanelMin': function(val) {
      if (val) {
        this.configLogPanel('min');
      } else {
        this.configLogPanel('releaseMin');
      }
    },
    'execute.status': function(val) {
      this.work.data.status = val;
      this.script.status = val;
      this.dispatch('IndexedDB:recordTab', this.work);
    },
  },
  async created() {
    this.userName = this.getUserName();
    // 如果当前work存在data，表示工作已经被打开
    if (this.work.data) {
      delete this.work.data.oldData;
      this.script = this.work.data;
      // 用缓存的scriptViewState替换当前this.scriptViewState
      this.scriptViewState = this.script.scriptViewState;
    } else {
      let supportedMode = find(this.getSupportModes(), (p) => p.rule.test(this.work.filename));
      if (this.work.specialSetting) {
        supportedMode = find(this.getSupportModes(), (p) => p.runType === this.work.specialSetting.runType);
      }
      // 判断特殊字符的文件, 后台编译可能会因为文件名存在特殊字符报错，所以无法运行
      const regLeal = /^[.\w\u4e00-\u9fa5]{1,200}\.[A-Za-z]+$/;
      const islegal = regLeal.test(this.work.filename);
      this.work.data = this.script = new Script(Object.assign(supportedMode, {
        id: this.work.id,
        fileName: this.work.filename,
        // supportedMode的executable优先级高于islegal
        executable: !supportedMode.executable ? supportedMode.executable : islegal,
        data: this.work.code,
        params: this.work.params,
      }));
      delete this.work.specialSetting;
      // 把新创建的scriptViewState挂到script对象上
      this.script.scriptViewState = this.scriptViewState;
    }

    // 删掉无用的code和params，因为已经存储在script对象中
    delete this.work.code;
    delete this.work.params;
    this.script.oldData = this.script.data;
    if (!this.work.noLoadCache) {
      this.dispatch('IndexedDB:getResult', { tabId: this.script.id,
        cb: (resultList) => {
          if (resultList) {
            this.script.result = resultList[resultList.resultSet].result;
            this.script.resultList = dropRight(toArray(resultList), 3);
            this.showPanelTab(resultList.showPanel);
            if (this.$refs.result) {
              this.$nextTick(() => {
                this.$refs.result.resultSet = resultList.resultSet;
                this.$refs.result.initPage();
              });
            }
          }
        } });
      this.dispatch('IndexedDB:getLog', { tabId: this.script.id,
        cb: (logList) => {
          this.script.log = Object.freeze(logList);
        } });
      this.dispatch('IndexedDB:getProgress', { tabId: this.script.id,
        cb: ({ content }) => {
          this.script.progress = {
            current: content.current,
            progressInfo: content.progressInfo,
            costTime: content.costTime,
          };
          this.script.steps = content.steps || [];
        } });
    }
    this.dispatch('IndexedDB:getHistory', { tabId: this.script.id,
      cb: (historyList) => {
        this.script.history = dropRight(values(historyList));
      } });
    let cacheWork = await this.getCacheWork(this.work);
    this._running_scripts_key = 'running_scripts_' + this.userName;
    if (cacheWork) { // 点击运行后脚本正在执行中未关掉Tab刷新页面时执行进度恢复
      let { data, taskID, execID } = cacheWork;
      // cacheWork.taskID && .execID && cacheWork.data && cacheWork.data.running
      if (taskID && execID && data && data.running) {
        let dbProgress = JSON.parse(JSON.stringify(this.script.progress));
        this.script = cacheWork.data;
        this.script.progress = dbProgress;
        this.run({
          taskID,
          execID,
          isRestore: true,
          id: this.script.id,
        });
        this.work.execID = execID;
        this.work.taskID = taskID;
      }
    } else { // 脚本正在执行中关掉了Tab，之后再打开页面，执行进度恢复
      let runningScripts = storage.get(this._running_scripts_key, 'local') || {};
      if (runningScripts[this.script.id]) {
        // 存在execID表示任务已经在执行，否则任务已提交或排尚未真正执行
        if (runningScripts[this.script.id].execID) {
          this.script.steps = runningScripts[this.script.id].steps;
          this.script.progress = runningScripts[this.script.id].progress;
          this.run({
            taskID: runningScripts[this.script.id].taskID,
            execID: runningScripts[this.script.id].execID,
            isRestore: true,
            id: this.script.id,
          });
          this.work.execID = runningScripts[this.script.id].execID;
          this.work.taskID = runningScripts[this.script.id].taskID;
        }
      } else {
        this.script.status = 'Inited';
      }
    }
    this.dispatch('IndexedDB:recordTab', this.work);
    // this.dispatch('IndexedDB:updateGlobalCache', {
    //   id: this.userName,
    //   work: this.work,
    // });
  },
  mounted() {
    this.scriptViewState.height = this.scriptViewState.height || this.$el.clientHeight;
    this.scriptViewState.bottomPanelHeight = this.scriptViewState.bottomPanelHeight || this.$el.clientHeight * 0.4;
    this.$nextTick(() => {
      this.dispatch('WebSocket:init');
    });
    window.onbeforeunload = () => {
      if (this.work.unsave || this.script.running) {
        return 'need confirm';
      }
      return null;
    };
    this.autoSave();
  },
  beforeDestroy() {
    clearTimeout(this.autoSaveTimer);
    let runningScripts = storage.get(this._running_scripts_key, 'local') || {};
    if (this.script.running && this.execute.taskID && this.execute.id) {
      runningScripts[this.script.id] = {
        execID: this.execute.id,
        taskID: this.execute.taskID,
        progress: this.script.progress,
        steps: this.script.steps,
      };
    } else {
      delete runningScripts[this.script.id];
    }
    storage.set(this._running_scripts_key, runningScripts, 'local');
    if (this.execute) {
      this.execute.off();
      this.execute = null;
    }
    window.onbeforeunload = null;
    this.scriptViewState.bottomPanelHeight = this.scriptViewState.cacheBottomPanelHeight || this.scriptViewState.bottomPanelHeight;
  },
  methods: {
    'Workbench:save'(work) {
      if (work.id == this.script.id) {
        this.save();
      }
    },
    'Workbench:socket'({ type, ...args }) {
      if (type === 'downgrade') {
        this.postType = 'http';
      }
      if (this.execute) {
        this.execute.trigger(type, Object.assign(args, {
          execute: this.execute,
        }));
      }
    },
    'Workbench:run'(option, cb) {
      if (option.id === this.script.id) {
        this.run(option, (status) => {
          if (status !== 'start' && status !== 'downgrade') {
            return cb && cb(true);
          }
        });
      }
    },
    'Workbench:setResultCache'(option) {
      if (option.id === this.script.id) {
        if (this.$refs.result) {
          this.script.resultList = this.script.resultList || [this.script.result];
          const tmpResult = this.$refs.result.getResult();
          const resultSet = this.$refs.result.resultSet;
          this.$set(this.script.resultList[resultSet], 'result', tmpResult);
          this.dispatch('IndexedDB:updateResult', {
            tabId: this.script.id,
            resultSet: resultSet,
            showPanel: this.scriptViewState.showPanel,
            ...this.script.resultList,
          });
        }
      }
    },
    'Workbench:setEditorPanelSize'(option) {
      if (option.id === this.script.id) {
        this.$refs.topPanel[option.status]({
          isDiy: true,
          diyStyle: {
            top: '33px',
            right: 0,
            bottom: 0,
            left: 0,
          },
        });
      }
    },
    getCacheWork(work) {
      let { filepath, filename } = work;
      return new Promise((resolve) => {
        this.dispatch('IndexedDB:getTabs', (tabs) => {
          let currentTab = null;
          for (let tab of tabs) {
            if (tab.filepath === filepath && tab.filename === filename) {
              currentTab = tab;
              break;
            }
          }
          resolve(currentTab);
        });
      });
    },
    getExecuteData(option) {
      const executionCode = isString(option.code) ? option.code : this.script.data;
      const isStorage = option && option.type === 'storage';
      this.script.executionCode = isStorage ? option.executionCode : executionCode;
      let initData = {
        method: '/api/rest_j/v1/entrance/execute',
        websocketTag: this.work.id,
        data: {
          executeApplicationName: this.script.application,
          executionCode: this.script.executionCode,
          runType: this.script.runType,
          params: this.convertSettingParams(this.script.params),
          postType: this.postType,
          source: {
            scriptPath: this.work.filepath || this.work.filename,
          },
        },
      };
      if (isStorage) {
        initData.method = '/api/rest_j/v1/entrance/backgroundservice';
        initData.data.background = option.backgroundType;
      }

      return initData;
    },
    run(option, cb) {
      if (option && option.id === this.script.id) {
        if (this.$refs.topPanel.isFullScreen) {
          this.dispatch('Workbench:setTabPanelSize');
          this.$refs.topPanel.releaseFullScreen();
        }
        this.showPanelTab('progress');
        const data = this.getExecuteData(option);
        // 执行
        this.execute = new Execute(data, this.$t.bind(this));
        this.isLogShow = false;
        this.localLog = {
          log: { all: '', error: '', warning: '', info: '' },
          logLine: 1,
        };
        if (option && option.isRestore) {
          this.execute.restore(option);
        } else {
          this.dispatch('IndexedDB:clearLog', this.script.id);
          this.dispatch('IndexedDB:clearResult', this.script.id);
          this.dispatch('IndexedDB:clearProgress', this.script.id);
          this.resetProgress();
          this.resetData();
          this.execute.start();
        }
        // 运行时，如果是临时脚本且未保存状态时，弹出一个警告的提醒，否则直接保存。
        if (!this.work.filepath && this.work.unsave) {
          this.$Notice.warning({
            title: this.$t('message.workBench.body.script.script.notice.unsave.title'),
            desc: this.$t('message.workBench.body.script.script.notice.unsave.desc'),
            duration: 3,
          });
        } else {
          this.save();
        }
        this.execute.once('sendStart', (code) => {
          const name = this.work.filepath || this.work.filename;
          this.$Notice.close(name);
          this.$Notice.info({
            title: this.$t('message.workBench.body.script.script.notice.sendStart.title'),
            desc: '',
            render: (h) => {
              return h('span', {
                style: {
                  'word-break': 'break-all',
                  'line-height': '20px',
                },
              }, `${this.$t('message.workBench.body.script.script.notice.sendStart.render')} ${this.work.filename} ！`);
            },
            name,
            duration: 3,
          });
          this.work.execID = this.execute.id;
          this.work.taskID = this.execute.taskID;
          if (code) {
            this.script.executionCode = this.script.data = code;
          }
          this.dispatch('IndexedDB:recordTab', this.work);
          cb && cb('start');
        });
        this.execute.on('updateResource', (num) => {
          this.dispatch('Footer:updateRunningJob', num);
        });
        this.execute.on('log', (logs) => {
          let hasLogInfo = Array.isArray(logs) ? logs.some((it) => it.length > 0) : logs;
          if (!hasLogInfo) {
            return;
          }
          const convertLogs = util.convertLog(logs);
          Object.keys(convertLogs).forEach((key) => {
            const convertLog = convertLogs[key];
            if (convertLog) {
              this.localLog.log[key] += convertLog + '\n';
            }
            if (key === 'all') {
              this.localLog.logLine += convertLog.split('\n').length;
            }
          });

          if (this.scriptViewState.showPanel === 'log') {
            this.localLogShow();
          }
        });

        this.execute.on('history', (ret) => {
          const index = findIndex(this.script.history, (o) => o.taskID === ret.taskID);
          const findHis = find(this.script.history, (o) => o.taskID === ret.taskID);
          let newItem = null;
          // 这里针对的是导入导出脚本，executionCode为object的情况
          const code = typeof (this.script.executionCode) === 'string' && this.script.executionCode ? this.script.executionCode : this.script.data;
          if (findHis) {
            if (ret.isPartialUpdate) {
              ret.runningTime = findHis.runningTime || 0;
              delete ret.isPartialUpdate;
              newItem = Object.assign(findHis, ret);
            } else if (ret.hasOwnProperty('logPath')) {
              newItem = {
                taskID: ret.taskID,
                createDate: findHis.createDate,
                execID: ret.execID || findHis.execID,
                runningTime: findHis.runningTime,
                // executionCode代表是选中某段代码进行执行的
                data: code,
                status: ret.status,
                fileName: this.script.fileName,
                failedReason: ret.failedReason,
              };
            } else {
              newItem = {
                taskID: ret.taskID,
                createDate: ret.createDate,
                execID: ret.execID || findHis.execID,
                runningTime: ret.runningTime,
                data: code,
                status: ret.status,
                fileName: this.script.fileName,
                failedReason: ret.failedReason,
              };
            }
          } else {
            newItem = {
              taskID: ret.taskID,
              createDate: ret.createDate,
              execID: ret.execID,
              runningTime: ret.runningTime,
              data: code,
              status: ret.status,
              fileName: this.script.fileName,
              failedReason: ret.failedReason,
            };
          }
          if (index === -1) {
            this.script.history.unshift(newItem);
            this.dispatch('IndexedDB:appendHistory', {
              tabId: this.script.id,
              ...this.script.history,
            });
          } else {
            this.script.history.splice(index, 1, newItem);
            this.dispatch('IndexedDB:updateHistory', {
              tabId: this.script.id,
              ...this.script.history,
            });
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
          };
          this.$set(this.execute.resultList[0], 'result', storeResult);
          this.$set(this.script, 'resultSet', 0);
          this.script.result = storeResult;
          this.script.resultList = this.execute.resultList;
          this.dispatch('IndexedDB:getResult', { tabId: this.script.id,
            cb: (resultList) => {
              if (resultList) {
                this.dispatch('IndexedDB:updateResult', {
                  tabId: this.script.id,
                  resultSet: 0,
                  showPanel: 'result',
                  ...this.script.resultList,
                });
              } else {
                this.dispatch('IndexedDB:appendResult', {
                  tabId: this.script.id,
                  resultSet: 0,
                  showPanel: 'result',
                  ...this.script.resultList,
                });
              }
            } });
          // 获取过progress的情况下设置为1
          if (this.script.progress.current) {
            this.script.progress.current = 1;
            this.dispatch('IndexedDB:updateProgress', {
              tabId: this.script.id,
              rst: this.script.progress,
            });
          }
        });
        this.execute.on('progress', ({ progress, progressInfo, waitingSize }) => {
          if (this._execute_last_progress === progress) {
            return;
          }
          this._execute_last_progress = progress;
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
            delete runningScripts[this.script.id];
            storage.set(this._running_scripts_key, runningScripts, 'local');
          }

          this.script.progress.current = progress;

          if (waitingSize !== null && waitingSize >= 0) {
            this.script.progress.waitingSize = waitingSize;
          }
          this.dispatch('IndexedDB:updateProgress', {
            tabId: this.script.id,
            rst: this.script.progress,
          });
        });
        this.execute.on('updateExpireHistory', (data) => {
          const index = findIndex(this.script.history, (o) => o.taskID === data.taskID);
          const findHis = find(this.script.history, (o) => o.execID === data.execID);
          if (findHis) {
            let newItem = null;
            api.fetch(`/jobhistory/${findHis.taskID}/get`, 'get')
              .then((res) => {
                newItem = {
                  taskID: res.task.taskID,
                  createDate: res.task.createdTime,
                  execID: res.task.strongerExecId,
                  runningTime: res.task.costTime,
                  data: res.task.executionCode,
                  status: res.task.status,
                  fileName: res.task.fileName,
                  failedReason: res.task.errCode && res.task.errDesc ? res.task.errCode + res.task.errDesc : res.task.errCode || res.task.errDesc || '',
                };
                this.script.history.splice(index, 1, newItem);
                this.dispatch('IndexedDB:updateHistory', {
                  tabId: this.script.id,
                  ...this.script.history,
                });
              });
          }
        });
        this.execute.on('steps', (status) => {
          if (this._execute_last_status === status) {
            return;
          }
          this._execute_last_status = status;
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
            this.dispatch('IndexedDB:updateProgress', {
              tabId: this.script.id,
              rst: Object.assign(this.script.progress, { steps: this.script.steps }),
            });
          }
        });
        this.execute.on('WebSocket:send', (data) => {
          this.dispatch('WebSocket:send', data);
        });
        this.execute.on('error', (type) => {
          // 执行错误的时候resolve，用于改变modal框中的loading状态
          cb && cb(type || 'error');
          if (this.scriptViewState.showPanel !== 'history') {
            this.showPanelTab('history');
            this.isLogShow = true;
          }
          this.dispatch('IndexedDB:appendLog', {
            tabId: this.script.id,
            rst: this.script.log,
          });
        });
        this.execute.on('stateEnd', () => {
          // 执行成功的时候resolve，用于改变modal框中的loading状态
          cb && cb('end');
          this.dispatch('IndexedDB:appendLog', {
            tabId: this.script.id,
            rst: this.localLog.log,
          });
        });
        this.execute.on('querySuccess', ({ type, task }) => {
          const costTime = util.convertTimestamp(task.costTime);
          this.script.progress.costTime = costTime;
          const name = this.work.filepath || this.work.filename;
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
              }, `${this.work.filename} ${type}${this.$t('message.workBench.body.script.script.notice.querySuccess.render')}：${costTime}！`);
            },
            name,
            duration: 3,
          });
        });
        this.execute.on('notice', ({ type, msg, autoJoin }) => {
          const name = this.work.filepath || this.work.filename;
          const label = autoJoin ? `${this.$t('message.workBench.body.script.script.text')}${this.work.filename} ${msg}` : msg;
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
          this.dispatch('IndexedDB:updateProgress', {
            tabId: this.script.id,
            rst: this.script.progress,
          });
        });
      }
    },
    resetData() {
      // upgrade only one time
      this.script.log = { all: '', error: '', warning: '', info: '' };
      this.script.logLine = 1;
      this.script.steps = ['Submitted'];
      this.script.result = {
        headRows: [],
        bodyRows: [],
        type: 'EMPTY',
        total: 0,
        path: '',
        cache: {},
      };
      this.script.resultList = null;
      this._execute_last_progress = null;
    },
    resetProgress() {
      this.script.progress = {
        current: null,
        progressInfo: [],
        waitingSize: null,
        costTime: null,
      };
    },
    stop(cb) {
      if (this.execute && this.execute.id) {
        api.fetch(`/entrance/${this.execute.id}/kill`, 'get').then(() => {
          this.execute.trigger('stop');
          this.execute.trigger('error');
          this.execute.trigger('kill');
          // 停止执行
          const name = this.work.filepath || this.work.filename;
          this.$Notice.close(name);
          this.$Notice.warning({
            title: this.$t('message.workBench.body.script.script.notice.kill.title'),
            desc: `${this.$t('message.workBench.body.script.script.notice.kill.desc')} ${this.work.filename} ！`,
            name: name,
            duration: 3,
          });
          cb();
        }).catch(() => {
          this.execute.trigger('stop');
          cb();
        });
      } else {
        cb();
        this.script.steps = []; // socket downgrade事件之前点击运行，终止运行loading后恢复
        this.script.running = false;
      }
    },
    autoSave() {
      clearTimeout(this.autoSaveTimer);
      if (!this.work.saveAs && this.work && this.work.data) {
        this.autoSaveTimer = setTimeout(() => {
          this.save();
        }, 1000 * 60 * 5);
      }
    },
    async save() {
      const params = {
        path: this.work.filepath,
        scriptContent: this.script.data,
        params: this.convertSettingParams(this.script.params),
      };
      // this.work.code = this.script.data;
      const isHdfs = this.work.filepath.indexOf('hdfs') === 0;
      if (this.script.data) {
        if (this.work.unsave) {
          if (this.work.filepath) {
            this.work.unsave = false;
            const timeout = setTimeout(() => {
              this.saveLoading = true;
            }, 2000);
            // 保存时更新下缓存。
            if (this.script.data !== this.work.data.data) {
              this.work.data.data = this.script.data;
            }
            delete this.work.data.oldData;
            this.dispatch('IndexedDB:recordTab', this.work, () => {
              api.fetch('/filesystem/saveScript', params).then(() => {
                clearTimeout(timeout);
                this.saveLoading = false;
                this.$Message.success(this.$t('message.constants.success.save'));
                // 提交最新的内容，更新script.data和script.oldData
                this.script.oldData = this.script.data;
                // 保存完后，需要去设置参数的原始值，用于在子模块判断unsave的值
                if (this.$refs.editor.$refs.setting) {
                  this.$refs.editor.$refs.setting.origin = JSON.stringify(this.script.params);
                }

                if (this.work.ismodifyByOldTab) {
                  this.work.ismodifyByOldTab = false;
                }
              }).catch(() => {
                clearTimeout(timeout);
                this.saveLoading = false;
                this.work.unsave = true;
              });
            });
            // this.dispatch('IndexedDB:updateGlobalCache', {
            //   id: this.userName,
            //   work: this.work,
            // });
          } else {
            // 保存时候判断是否为临时脚本
            this.$Modal.confirm({
              title: this.$t('message.workBench.body.script.script.confirmModal.title'),
              content: this.$t('message.workBench.body.script.script.confirmModal.content'),
              okText: this.$t('message.workBench.body.script.script.confirmModal.okText'),
              cancelText: this.$t('message.workBench.body.script.script.confirmModal.cancelText'),
              onOk: () => {
                this.dispatch('Workbench:saveAs', this.work);
              },
            });
          }
        }
      } else {
        this.$Message.warning(this.$t('message.workBench.body.script.script.warning.empty'));
      }
      this.autoSave();
    },
    showPanelTab(type) {
      this.scriptViewState.showPanel = type;
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
    configLogPanel(name) {
      if (name == 'fullScreen') {
        this.isBottomPanelFull = true;
        this.$refs.bottomPanel[name]({
          isDiy: false,
        });
      }
      if (name == 'releaseFullScreen') {
        this.isBottomPanelFull = false;
        this.$refs.bottomPanel[name]({
          isDiy: false,
        });
      }
      if (name == 'min') {
        this.scriptViewState.bottomPanelHeight = 32;
        this.scriptViewState.bottomPanelMin = true;
      }
      if (name == 'releaseMin') {
        this.scriptViewState.bottomPanelHeight = this.scriptViewState.height * 0.4;
        this.scriptViewState.bottomPanelMin = false;
      }
    },
    changeBottomPanel({ height }) {
      this.scriptViewState.cacheBottomPanelHeight = height;
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
            this.dispatch('IndexedDB:updateResult', {
              tabId: this.script.id,
              resultSet,
              showPanel: 'result',
              ...this.script.resultList,
            });
            cb();
          }).catch(() => {
            cb();
          });
      } else {
        this.script.result = this.script.resultList[resultSet].result;
        this.$set(this.script, 'resultSet', resultSet);
        this.dispatch('IndexedDB:updateResult', {
          tabId: this.script.id,
          resultSet: resultSet,
          showPanel: 'result',
          ...this.script.resultList,
        });
        cb();
      }
    },
    // 将数组格式化成json形式。
    async openAnalysisTab() {
      if (this.biLoading) return this.$Message.warning(this.$t('message.constants.warning.biLoading'));

      this.biLoading = true;
      const vsBiUrl = this.getVsBiUrl();
      let result = await api.fetch(`${vsBiUrl}/api/rest_j/v1/visualis/view`, {
        fileName: this.work.filename,
        executionCode: this.script.executionCode || this.script.data,
        resultPath: this.script.result.path,
        runType: this.script.runType,
        resultNumber: this.script.result.current > 0 ? this.script.result.current - 1 : 0,
      }).catch(() => {
        this.biLoading = false;
      });
      if (result) {
        let { id, projectId } = result;
        const filename = `${this.work.filename}_project${projectId}_set${this.script.resultSet}.bi`;
        const md5Path = util.md5(filename);
        this.dispatch('Workbench:add', {
          id: md5Path,
          filename: filename,
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
    async openPanel(type) {
      if (type === 'log') {
        this.isLogShow = true;
        this.showPanelTab(type);
      }
    },
    convertSettingParams(params) {
      let variable = {};
      let configuration = {
        special: {},
        runtime: {
          args: '',
          env: [],
          datasource: {
            datasourceId: null
          }
        },
        startup: {},
      };
      if (!isEmpty(params)) {
        variable = isEmpty(params.variable) ? [] : util.convertArrayToObject(params.variable);
        configuration = isEmpty(params.configuration) ? {} : {
          special: {},
          runtime: {
            args: params.configuration.runtime ? params.configuration.runtime.args || '' : '',
            env: params.configuration.runtime ? (isEmpty(params.configuration.runtime.env) ? [] : util.convertObjectToArray(params.configuration.runtime.env)) : [],
            datasource: params.configuration.runtime.datasource ? params.configuration.runtime.datasource : {datasourceId: null}
          },
          startup: {},
        };
      }
      return {
        variable,
        configuration,
      };
    },
  },
};
</script>
