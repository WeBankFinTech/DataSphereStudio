<template>
  <div class="we-panel we-panel-vertical">
    <div ref="topPanel" :class="{'full-screen': scriptViewState.topPanelFull}"  class="editor-panel" :style="{'height': scriptViewState.topPanelHeight}">
      <editor ref="editor" :script="script" :work="work" :script-type="work.type" :readonly="readonly" @on-save="save" @on-run="run"
        @on-stop="stop"/>
      <Spin v-if="saveLoading" size="large" class="new-sidebar-spin" fix />
      <!-- 头部的线 -->
      <div class="script-line" @mousedown.stop="handleMouseDown"></div>
    </div>
    <!-- 使用v-if把非当前脚本DOM移除文档防止DOM节点过多影响性能 -->
    <div v-if="current===work.id" :class="{'full-screen': scriptViewState.bottomPanelFull}" class="log-panel">
      <div class="workbench-tabs">
        <div class="workbench-tab-wrapper">
          <div class="workbench-tab">
            <div v-if="bottomTab.progress" :class="{active: scriptViewState.showPanel == 'progress'}"
              class="workbench-tab-item" @click="showPanelTab('progress')">
              <span>{{ $t('message.scripts.tabs.progress') }}</span>
            </div>
            <div v-if="bottomTab.result" :class="{active: scriptViewState.showPanel == 'result'}"
              class="workbench-tab-item" @click="showPanelTab('result')">
              <span>{{ $t('message.scripts.tabs.result') }}</span>
            </div>
            <div v-if="bottomTab.log" :class="{active: scriptViewState.showPanel == 'log'}" class="workbench-tab-item"
              @click="showPanelTab('log')">
              <span>{{ $t('message.scripts.tabs.log') }}</span>
            </div>
            <div v-if="bottomTab.history" :class="{active: scriptViewState.showPanel == 'history'}"
              class="workbench-tab-item" @click="showPanelTab('history')">
              <span>{{ $t('message.scripts.tabs.history') }}</span>
            </div>
          </div>
        </div>
      </div>
      <we-progress
        v-if="bottomTab.show_progress"
        :current="script.progress.current"
        :waiting-size="script.progress.waitingSize"
        :steps="script.steps"
        :status="script.status"
        :script-view-state="scriptViewState"
        :application="script.application"
        :progress-info="script.progress.progressInfo"
        :cost-time="script.progress.costTime"
        @open-panel="openPanel" />
      <result
        v-if="bottomTab.show_result"
        ref="result"
        :script="script"
        :dispatch="dispatch"
        :visualShow="visualShow"
        :script-view-state="scriptViewState"
        :dataWranglerParams="dataWranglerParams"
        getResultUrl="filesystem"
        @on-set-change="changeResultSet"
        @on-analysis="openAnalysisTab"
        :visualParams="visualParams" />
      <log
        v-if="bottomTab.show_log"
        :status='script.status'
        :logs="script.log"
        :log-line="script.logLine"
        :script-view-state="scriptViewState"/>
      <history
        v-if="bottomTab.show_history"
        :history="script.history"
        :script-view-state="scriptViewState"
        :run-type="script.runType"
        :node="node" />
    </div>
  </div>
</template>
<script>
import {
  isEmpty,
  find,
  dropRight,
  toArray,
  values,
  isString,
  findIndex,
  last,
  isUndefined,
  debounce
} from 'lodash';
import api from '@/common/service/api';
import storage from '@/common/helper/storage';
import Execute from '@/common/service/execute';
import util from '@/common/util';
import {
  Script
} from '../modal.js';

import editor from './editor.vue';
import history from './history.vue';
import result from '@/components/consoleComponent/result.vue';
import log from '@/components/consoleComponent/log.vue';
import weProgress from '@/components/consoleComponent/progress.vue';
import elementResizeEvent from '@/common/helper/elementResizeEvent';
import mixin from '@/common/service/mixin';

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
    current: {
      type: String,
      default: ''
    },
    node: Object,
    readonly: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    let showPanel = this.work.type === 'historyScript' ? 'log' : 'progress'
    return {
      script: {
        data: '',
        oldData: '',
        result: {},
        steps: [],
        progress: {},
        resultList: null,
        resultSet: 0,
        params: {},
        readOnly: false
      },
      visualParams: {},
      scriptViewState: {
        topPanelHeight: '250px',
        bottomContentHeight: '250',
        topPanelFull: false,
        showPanel,
        bottomPanelFull: false,
        cacheLogScroll: 0,
      },
      execute: null,
      // TODO【dss-scriptis】前端执行接口降级，原值为postType: 'socket'
      postType: 'http',
      biLoading: false,
      saveLoading: false,
      visualShow: 'table',
      dataWranglerParams: {}
    };
  },
  mixins: [mixin],
  computed: {
    scriptResult() {
      let res = {
        headRows: [],
        bodyRows: [],
        type: 'EMPTY',
        total: 0,
        path: ''
      };
      if (this.script.resultList) {
        res = this.script.resultList[this.script.resultSet || 0].result || res
      }
      return res
    },
    bottomTab() {
      let { showPanel } = this.scriptViewState;
      return {
        progress: this.work.type !== 'historyScript',
        result: this.scriptResult.total || (this.script.resultList && this.script.resultList.length),
        log: this.isLogShow || this.work.type === 'historyScript',
        history: this.work.type !== 'historyScript',
        show_progress: showPanel === 'progress',
        show_result: showPanel === 'result',
        show_log: showPanel === 'log',
        show_history: showPanel === 'history',
        show_visual: showPanel === 'visual'
      }
    }
  },
  watch: {
    'scriptResult.headRows': function() {
      if(this.visualShow === 'visual'){
        this.openAnalysisTab('table')
      }
    },
    'script.data': function () {
      if (!this.work.ismodifyByOldTab) {
        this.work.unsave = this.script.data != this.script.oldData;
        if (this.node) {
          this.node.isChange = this.work.unsave;
        }
      }
    },
    'script.oldData': function () {
      if (!this.work.ismodifyByOldTab) {
        this.work.unsave = this.script.data != this.script.oldData;
      }
    },
    'execute.run': function (val) {
      this.work.data.running = this.script.running = val;
      // 加入用户名来区分不同账户下的tab
      this.dispatch('IndexedDB:recordTab', { ...this.work, userName: this.userName });
    },
    'execute.status': function (val) {
      this.work.data.status = val;
      this.script.status = val;
      // 加入用户名来区分不同账户下的tab
      this.dispatch('IndexedDB:recordTab', { ...this.work, userName: this.userName });
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
    } else if (this.work.type === 'historyScript') {
      this.initHistory();
      return;
    } else {
      let supportedMode = find(this.getSupportModes(), (p) => p.rule.test(this.work.filename));
      if (this.work.specialSetting) {
        supportedMode = find(this.getSupportModes(), (p) => p.runType === this.work.specialSetting.runType);
      }
      // 判断特殊字符的文件, 后台编译可能会因为文件名存在特殊字符报错，所以无法运行
      const regLeal = /^[.\w\u4e00-\u9fa5-]{1,200}\.[A-Za-z]+$/;
      const islegal = regLeal.test(this.work.filename);
      this.work.data = this.script = new Script(Object.assign({}, supportedMode, {
        id: this.work.id,
        fileName: this.work.filename,
        // supportedMode的executable优先级高于islegal
        executable: !supportedMode.executable ? supportedMode.executable : islegal,
        data: this.work.code,
        params: this.work.params,
        readOnly: this.readonly
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
      this.dispatch('IndexedDB:getResult', {
        tabId: this.script.id,
        cb: (resultList) => {
          if (resultList) {

            this.script.resultList = dropRight(toArray(resultList), 3);
            this.script.resultSet = resultList.resultSet;
            this.showPanelTab(resultList.showPanel);

          }
        }
      });
      this.dispatch('IndexedDB:getLog', {
        tabId: this.script.id,
        cb: (logList) => {
          this.script.log = Object.freeze(logList);
        }
      });
      this.dispatch('IndexedDB:getProgress', {
        tabId: this.script.id,
        cb: ({
          content
        }) => {
          this.script.progress = {
            current: content.current,
            progressInfo: content.progressInfo,
            costTime: content.costTime,
          };
          this.script.steps = content.steps || [];
        }
      });
    }
    this.dispatch('IndexedDB:getHistory', {
      tabId: this.script.id,
      cb: (historyList) => {
        this.script.history = dropRight(values(historyList));
      }
    });
    let cacheWork = await this.getCacheWork(this.work);
    this._running_scripts_key = 'running_scripts_' + this.userName;
    if (cacheWork) { // 点击运行后脚本正在执行中未关掉Tab刷新页面时执行进度恢复
      let {
        data,
        taskID,
        execID
      } = cacheWork;
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
    // 加入用户名来区分不同账户下的tab
    this.dispatch('IndexedDB:recordTab', { ...this.work, userName: this.userName });
  },
  mounted() {
    window.addEventListener('mousemove', this.handleMouseMove);
    window.addEventListener('mouseup', this.handleMouseUp);
    elementResizeEvent.bind(this.$el, this.resize);
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
    // 关闭tab页面不再继续轮询接口
    if (this.execute && this.execute.run !== undefined) {
      this.execute.run = false;
    }
    window.removeEventListener('mousemove', this.handleMouseMove);
    window.removeEventListener('mouseup', this.handleMouseUp);
    elementResizeEvent.unbind(this.$el);
    if (this.animationFrameId) {
      elementResizeEvent.cancelAnimationFrame(this.animationFrameId);
    }
    clearTimeout(this.autoSaveTimer);
    let runningScripts = storage.get(this._running_scripts_key, 'local') || {};
    if (this.script.running && this.execute && this.execute.taskID && this.execute.id) {
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
  },
  methods: {
    // 鼠标位置事件兼容
    getStdMouseEvent(event) {
      return {
        positionX: event.clientX + document.body.scrollLeft + document.documentElement.scrollLeft,
        positionY: event.clientY + document.body.scrollTop + document.documentElement.scrollTop,
      };
    },
    // 拖拽的线鼠标按下事件
    handleMouseDown(e) {
      // 阻止冒泡事件和默认事件
      e.stopPropagation();
      e.preventDefault();
      // 先记录当前鼠标按下的位置
      const positionEvent = this.getStdMouseEvent(e);
      this.startY = positionEvent.positionY;
      this.mouseDownHeight = this.$refs.topPanel.offsetHeight;
      this.isMouseDown = true;
    },
    handleMouseMove(e) {
      if (this.isMouseDown) {
        const positionEvent = this.getStdMouseEvent(e);
        this.currentY = positionEvent.positionY;
        // 计算其实位置到移动的距离
        this.moveResult = this.currentY - this.startY;
        if (this.animationFrameId) {
          elementResizeEvent.cancelAnimationFrame(this.animationFrameId);
        }
        this.animationFrameId = elementResizeEvent.requestAnimationFrame(() => {
          let h = this.mouseDownHeight - -this.moveResult
          this.resize(null, h)
        })
      }
    },
    resize(evt, h) {
      h =  (!h || h < 1) ?  (this.$el ? this.$el.clientHeight * 0.618 : 350) : h
      let bh = this.$el ? this.$el.clientHeight - h - 75 : 0 // 底部内容区高度
      if (h > this.$el.clientHeight) {
        h = this.$el.clientHeight - 32 // 保持 tab 栏可见
        bh = 100 // 应该不可见^_^
      }
      this.scriptViewState = {
        ...this.scriptViewState,
        topPanelHeight: h + 'px',
        bottomContentHeight: bh
      }
    },
    handleMouseUp() {
      if (this.isMouseDown) {
        this.isMouseDown = false;
      }
    },
    'Workbench:save'(work) {
      if (work.id == this.script.id) {
        this.save();
      }
    },
    'Workbench:socket'({
      type,
      ...args
    }) {
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
          this.script.resultList = this.script.resultList || [this.scriptResult];
          const tmpResult = this.$refs.result.getResult();
          const resultSet = this.script.resultSet || 0;
          this.$set(this.script.resultList[resultSet], 'result', tmpResult);
          this.updateResult({
            tabId: this.script.id,
            resultSet: resultSet,
            showPanel: this.scriptViewState.showPanel,
            ...this.script.resultList,
          })
        }
      }
    },
    'Workbench:setEditorPanelSize'(option) {
      if (option.id === this.script.id) {
        if (option.status == 'fullScreen') {
          this.scriptViewState.topPanelFull = true
        } else if(option.status == 'releaseFullScreen') {
          this.scriptViewState.topPanelFull = false
        }
        this.$nextTick(()=>{
          this.scriptViewState = {...this.scriptViewState}
          this.$refs.editor.layout()
        })
      }
    },

    getCacheWork(work) {
      let {
        filepath,
        filename
      } = work;
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
      let params = {};
      if (this.node && Object.keys(this.node).length > 0) {
        params = this.nodeConvertSettingParams(this.script.params);
      } else {
        params = this.convertSettingParams(this.script.params)
      }
      let initData = {
        method: '/api/rest_j/v1/entrance/execute',
        websocketTag: this.work.id,
        data: {
          executeApplicationName: this.script.application,
          executionCode: this.script.executionCode,
          runType: this.script.runType,
          params,
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
        if (window.$Wa) window.$Wa.clickStat('run',this.script.fileName);
        if (this.scriptViewState.topPanelFull) {
          this.dispatch('Workbench:setTabPanelSize');
          this.scriptViewState.topPanelFull = false;
        }
        this.showPanelTab('progress');
        const data = this.getExecuteData(option);
        // 执行
        this.execute = new Execute(data);
        this.isLogShow = false;
        this.localLog = {
          log: {
            all: '',
            error: '',
            warning: '',
            info: ''
          },
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
        if (!this.work.filepath && this.work.unsave && !this.node) {
          this.$Notice.warning({
            title: this.$t('message.scripts.notice.unsave.title'),
            desc: this.$t('message.scripts.notice.unsave.desc'),
            duration: 3,
          });
        } else {
          this.save();
        }
        this.execute.once('sendStart', (code) => {
          const name = this.work.filepath || this.work.filename;
          this.$Notice.close(name);
          this.$Notice.info({
            title: this.$t('message.scripts.notice.sendStart.title'),
            desc: '',
            render: (h) => {
              return h('span', {
                style: {
                  'word-break': 'break-all',
                  'line-height': '20px',
                },
              },
              `${this.$t('message.scripts.notice.sendStart.render')} ${this.work.filename} ！`
              );
            },
            name,
            duration: 3,
          });
          this.work.execID = this.execute.id;
          this.work.taskID = this.execute.taskID;
          if (code) {
            this.script.executionCode = this.script.data = code;
          }
          // 加入用户名来区分不同账户下的tab
          this.dispatch('IndexedDB:recordTab', { ...this.work, userName: this.userName });
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
          const code = typeof (this.script.executionCode) === 'string' && this.script.executionCode ? this.script
            .executionCode : this.script.data;
          if (findHis) {
            if (ret.isPartialUpdate) {
              ret.runningTime = findHis.runningTime || 0;
              delete ret.isPartialUpdate;
              newItem = Object.assign(findHis, ret);
            } else if (Object.prototype.hasOwnProperty.call(ret, 'logPath')) {
              newItem = {
                taskID: ret.taskID,
                createDate: findHis.createDate,
                execID: ret.execID || findHis.execID,
                runningTime: findHis.runningTime,
                // executionCode代表是选中某段代码进行执行的
                data: code,
                status: ret.status,
                fileName: this.script.fileName,
                failedReason: ret.failedReason
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
                failedReason: ret.failedReason
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
              failedReason: ret.failedReason
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
            'path': this.execute.currentResultPath,
            'current': 1,
            'size': 20,
          };
          this.$set(this.execute.resultList[0], 'result', storeResult);
          this.$set(this.script, 'resultSet', 0);
          this.script.resultList = this.execute.resultList;
          this.dispatch('IndexedDB:getResult', {
            tabId: this.script.id,
            cb: (resultList) => {
              if (resultList) {
                this.updateResult({
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
            }
          });
        });
        this.execute.on('progress', ({
          progress,
          progressInfo,
          waitingSize
        }) => {
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
                  failedReason: res.task.errCode && res.task.errDesc ? res.task.errCode + res.task.errDesc :
                    res.task.errCode || res.task.errDesc || '',
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
              rst: Object.assign(this.script.progress, {
                steps: this.script.steps
              }),
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
        this.execute.on('querySuccess', ({
          type,
          task
        }) => {
          const costTime = util.convertTimestamp(task.costTime);
          this.script.progress.costTime = costTime;
          const name = this.work.filepath || this.work.filename;
          this.$Notice.close(name);
          this.$Notice.success({
            title: this.$root.$t('message.scripts.notice.querySuccess.title'),
            desc: '',
            render: (h) => {
              return h('span', {
                style: {
                  'word-break': 'break-all',
                  'line-height': '20px',
                },
              },
              `${this.work.filename} ${type}${this.$root.$t('message.scripts.notice.querySuccess.render')}：${costTime}！`
              );
            },
            name,
            duration: 3,
          });
        });
        this.execute.on('notice', ({
          type,
          msg,
          autoJoin
        }) => {
          const name = this.work.filepath || this.work.filename;
          const label = autoJoin ?
            `${this.$root.$t('message.scripts.script')}${this.work.filename} ${msg}` : msg;
          this.$Notice.close(name);
          this.$Notice[type]({
            title: this.$root.$t('message.scripts.notice.notice.title'),
            name,
            desc: '',
            duration: 5,
            render: (h) => {
              return h('div', {
                style: {
                  position: 'relative',
                  'padding-bottom': '15px'
                }
              }, [h('span', {
                style: {
                  'word-break': 'break-all',
                  'line-height': '20px',
                },
              }, label),
              h('span',{
                style: {
                  color: 'red',
                  position: 'absolute',
                  right: '10px',
                  bottom: '0px',
                  display: 'none',
                  cursor: 'pointer'
                },
                on: {
                  click: () => {
                    if (type === 'error') {
                      // 先根据执行的最新的任务记录获取错误码后查询是否有贴
                      const failedReason = this.work.data.history[0].failedReason
                      const errorCode = parseInt(failedReason) || '';
                      const errorDesc = failedReason.substring(errorCode.toString().length, failedReason.length)
                      this.checkErrorCode(errorCode, errorDesc);
                    }
                  }
                }
              }, '发布提问')
              ])
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
    checkErrorCode(errorCode, errorDesc) {
      api.fetch('/kn/isErrorDuplicate', {
        errorCode
      }, 'get').then((res) => {
        if (res.isDuplicate) {
          // 如果有就打开新浏览器跳转
        } else {
          // 没有就发帖
          this.postMessage(errorCode, errorDesc);
        }
      })
    },
    // 发帖
    postMessage(errorCode, errorDesc) {
      api.fetch('/kn/posting', {
        title: `errorDesc问题讨论`,
        content: {
          errorCode,
          errorDesc
        }
      }, 'post').then((res) => {
        window.console.log(res, '发帖成功')
      })
    },
    resetData() {
      // upgrade only one time
      this.script.log = {
        all: '',
        error: '',
        warning: '',
        info: ''
      };
      this.script.logLine = 1;
      this.script.steps = ['Submitted'];
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
        api.fetch(`/entrance/${this.execute.id}/kill`, {taskID: this.execute.taskID}, 'get').then(() => {
          this.execute.trigger('stop');
          this.execute.trigger('error');
          this.execute.trigger('kill');
          // 停止执行
          const name = this.work.filepath || this.work.filename;
          this.$Notice.close(name);
          this.$Notice.warning({
            title: this.$t('message.scripts.notice.kill.title'),
            desc: `${this.$t('message.scripts.notice.kill.desc')} ${this.work.filename} ！`,
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
    nodeSave() {
      if (this.readonly) return this.$Message.warning(this.$t('message.scripts.notSave'));
      let params = {
        fileName: this.script.fileName,
        scriptContent: this.script.data,
        creator: this.node.creator,
        projectName: this.$route.query.projectName || '',
        metadata: this.nodeConvertSettingParams(this.script.params),
      };
      const resources = this.node.resources;
      if (resources && resources.length && this.node.jobContent && this.node.jobContent.script) {
        params.resourceId = resources[0].resourceId;
      }
      this.saveLoading = true;
      this.node.params = JSON.parse(JSON.stringify(this.nodeConvertSettingParams(this.script.params)));
      // 删除节点json里的contextId
      delete this.node.params.configuration.runtime.contextID;
      delete this.node.params.configuration.runtime.nodeName;
      // 除了执行，其他的都不需要contextID
      let tempParams = JSON.parse(JSON.stringify(params));
      delete tempParams.metadata.configuration.runtime.contextID;
      delete tempParams.metadata.configuration.runtime.nodeName;
      return api.fetch('/filesystem/saveScriptToBML', tempParams, 'post')
        .then((res) => {
          this.$Message.success(this.$t('message.scripts.saveSuccess'));
          this.node.isChange = false;
          this.dispatch('IDE:saveNode', {
            resource: {
              fileName: this.script.fileName,
              resourceId: res.resourceId,
              version: res.version,
            },
            node: this.node,
            data: {
              content: this.script.data,
              params: this.script.params
            }

          });
          this.work.unsave = false;
          // 提交最新的内容，更新script.data和script.oldData
          this.script.oldData = this.script.data;
          // 保存时更新下缓存。
          // 加入用户名来区分不同账户下的tab
          this.dispatch('IndexedDB:recordTab', { ...this.work, userName: this.userName });
          this.dispatch('IndexedDB:updateGlobalCache', {
            id: this.userName,
            work: this.work,
          });
          this.saveLoading = false;
          // 和后台有确认次值只是用在资源管理器的运行任务名字展示上，对其他无影响
          return this.node.title;
        }).catch(() => {
          // this.$Message.error(this.$t('message.scripts.saveErr'));
          this.saveLoading = false;
          this.work.unsave = true;
        });
    },
    async save() {
      if (this.node && Object.keys(this.node).length > 0) {
        this.nodeSave();
      } else {
        const params = {
          path: this.work.filepath,
          scriptContent: this.script.data,
          params: this.convertSettingParams(this.script.params),
        };
          // this.work.code = this.script.data;
        const isHdfs = this.work.filepath.indexOf('hdfs') === 0;
        if (this.script.data) {
          if (this.work.unsave && !isHdfs) {
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
              // 加入用户名来区分不同账户下的tab
              this.dispatch('IndexedDB:recordTab', { ...this.work, userName: this.userName }, () => {
                api.fetch('/filesystem/saveScript', params).then(() => {
                  clearTimeout(timeout);
                  this.saveLoading = false;
                  this.$Message.success(this.$t('message.scripts.constants.success.save'));
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
            } else {
              // 保存时候判断是否为临时脚本
              this.$Modal.confirm({
                title: this.$t('message.scripts.confirmModal.title'),
                content: this.$t('message.scripts.confirmModal.content'),
                okText: this.$t('message.scripts.confirmModal.okText'),
                cancelText: this.$t('message.scripts.confirmModal.cancelText'),
                onOk: () => {
                  this.dispatch('Workbench:saveAs', this.work);
                },
              });
            }
          }
        } else {
          this.$Message.warning(this.$t('message.scripts.warning.empty'));
        }
        this.autoSave();
      }
    },
    showPanelTab(type) {
      let h = this.scriptViewState.topPanelHeight.replace('px', '') - 0
      let bottomContentHeight = this.scriptViewState.bottomContentHeight
      if(this.$el && this.$el.clientHeight > 0) {
        bottomContentHeight = this.$el.clientHeight - h - 75
      }
      this.scriptViewState = {
        ...this.scriptViewState,
        showPanel: type,
        bottomContentHeight
      }
      if (type === 'log') {
        this.localLogShow();
      }
    },
    localLogShow() {
      if (!this.debounceLocalLogShow) {
        this.debounceLocalLogShow = debounce(() => {
          if (this.localLog) {
            this.script.log = Object.freeze({
              ...this.localLog.log
            });
            this.script.logLine = this.localLog.logLine;
          }
        }, 1500);
      }
      this.debounceLocalLogShow();
    },
    configLogPanel(name) {
      this.scriptViewState = {
        ...this.scriptViewState,
        bottomPanelFull: name == 'fullScreen'
      }
    },
    changeResultSet(data, cb) {
      const resultSet = isUndefined(data.currentSet) ? this.script.resultSet : data.currentSet;
      const findResult = this.script.resultList[resultSet];
      const resultPath = findResult && findResult.path;
      const hasResult = Object.prototype.hasOwnProperty.call(this.script.resultList[resultSet], 'result');
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
              'path': resultPath,
              'current': 1,
              'size': 20,
            };
            this.script.resultList[resultSet].result = result;
            this.script.resultSet = resultSet;
            this.script = {
              ...this.script
            };
            this.updateResult({
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
        this.script.resultSet = resultSet;
        this.script = {
          ...this.script
        };
        this.updateResult({
          tabId: this.script.id,
          resultSet: resultSet,
          showPanel: 'result',
          ...this.script.resultList,
        });
        cb();
      }
    },
    async initHistory() {
      try {
        const rst = await api.fetch(`/jobhistory/${this.work.taskID}/get`, 'get');
        if (rst) {
          const option = rst.task;
          const supportList = this.getSupportModes();
          const supportedMode = find(supportList, (p) => p.rule.test(this.work.filename));
          this.work.data = this.script = new Script(Object.assign({}, supportedMode, option, {
            fileName: this.work.filename,
            filepath: this.work.filepath,
            id: this.work.id,
            data: option.executionCode,
            readOnly: true,
            executable: false,
            configurable: false,
          }));
          this.getLogs(option);
          if (option.resultLocation) {
            this.getResult(option);
          }
        }
      } catch (errorMsg) {
        console.error(errorMsg)
      }
    },
    async getLogs(option) {
      if (!option.logPath) {
        this.script.log = {
          all: '',
          error: '',
          warning: '',
          info: ''
        };
        this.script.logLine = 1;
        this.script.scriptViewState = this.scriptViewState;
        this.script.status = option.status;
        return;
      }
      const url = '/filesystem/openLog';
      const rst = await api.fetch(url, {
        path: option.logPath,
      }, 'get');
      if (rst) {
        const log = {
          all: '',
          error: '',
          warning: '',
          info: ''
        };
        const convertLogs = util.convertLog(rst.log);
        Object.keys(convertLogs).forEach((key) => {
          if (convertLogs[key]) {
            log[key] += convertLogs[key] + '\n';
          }
        });
        this.script.status = option.status;
        this.script.log = log;
        this.script.logLine = rst.fromLine;
        // 把新创建的scriptViewState挂到script对象上
        this.script.scriptViewState = { ...this.scriptViewState };
      }
    },
    async getResult(option) {
      const url1 = `/filesystem/getDirFileTrees`;
      const rst = await api.fetch(url1, {
        path: option.resultLocation,
      }, 'get');
      if (rst.dirFileTrees) {
        // 后台的结果集顺序是根据结果集名称按字符串排序的，展示时会出现结果集对应不上的问题，所以加上排序
        this.script.resultSet = 0
        this.script.resultList = rst.dirFileTrees.children.sort((a, b) => parseInt(a.name, 10) - parseInt(b.name,
          10));
        if (this.script.resultList.length) {
          const currentResultPath = rst.dirFileTrees.children[0].path;
          const url2 = `/filesystem/openFile`;
          api.fetch(url2, {
            path: currentResultPath,
            page: 1,
            pageSize: 5000,
          }, 'get').then((ret) => {
            let tmpResult = {
              'headRows': ret.metadata,
              'bodyRows': ret.fileContent,
              'total': ret.totalLine,
              'type': ret.type,
              'path': currentResultPath,
            };
            this.$set(this.script.resultList[0], 'result', tmpResult);
            this.scriptViewState.showPanel = 'result';
          });
        }
      }
    },
    // 将数组格式化成json形式。
    openAnalysisTab(type) {
      this.visualShow = type;

      if (type === 'visual') {
        this.biLoading = true;
        let rows = this.scriptResult.headRows;
        let model = {}
        let dates = ["DATE", "DATETIME", "TIMESTAMP", "TIME", "YEAR"]
        let numbers = [
          "TINYINT",
          "SMALLINT",
          "MEDIUMINT",
          "INT",
          "INTEGER",
          "BIGINT",
          "FLOAT",
          "DOUBLE",
          "DOUBLE PRECISION",
          "REAL",
          "DECIMAL",
          "BIT",
          "SERIAL",
          "BOOL",
          "BOOLEAN",
          "DEC",
          "FIXED",
          "NUMERIC"];
        rows.forEach(item=>{
          let sqlType = item.dataType.toUpperCase();
          let visualType = 'string'
          if (numbers.indexOf(sqlType) > -1) {
            visualType = 'number'
          } else if(dates.indexOf(sqlType) > -1) {
            visualType = 'date'
          }
          model[item.columnName] =  {
            sqlType,
            visualType,
            modelType: visualType ==="number" ? "value": "category"
          }
        })
        this.visualParams = {
          // viewId: id,
          // projectId,
          json: {
            name: `${this.script.fileName.replace(/\./g,'')}${this.script.resultSet}`,
            model,
            source: {
              "engineType": "spark", //引擎类型
              "dataSourceType": "resultset", //数据源类型，结果集、脚本、库表
              "dataSourceContent": {
                "resultLocation": this.scriptResult.path
              },
              "creator": "IDE"
            }
          }
        }
      } else if (type === 'dataWrangler') {
        this.dataWranglerParams = {
          simpleMode: true,
          showBottomBar: false,
          importConfig: {
            "dataSourceConfig": {
              "dataSourceType": "linkis",
              "dataSourceOptions": {"taskID": this.work.taskID || this.work.data.history[0].taskID}
            },
            "config": {
              "myConfig": {
                "resultSetPath": [this.scriptResult.path]
              },
              "importConfig": {
                "mergeTables": true,
                "limitRows": 5000,
                "pivotTable": false,
                "tableHeaderRows": 1,
              }
            }
          }
        }
      }
    },
    openPanel(type) {
      if (type === 'log') {
        this.isLogShow = true;
        this.showPanelTab(type);
      }
    },
    nodeConvertSettingParams(params) {
      const variable = isEmpty(params.variable) ? {} : util.convertArrayToObject(params.variable);
      const configuration = isEmpty(params.configuration) ? {
        special: {},
        runtime: {},
        startup: {},
      } : {
        special: params.configuration.special || {},
        runtime: params.configuration.runtime || {},
        startup: params.configuration.startup || {},
      };
      return {
        variable,
        configuration,
      };
    },
    convertSettingParams(params) {
      const variable = isEmpty(params.variable) ? {} : util.convertArrayToObject(params.variable);
      const configuration = isEmpty(params.variable) ? {} : {
        special: {},
        runtime: {},
        startup: {},
      };
      return {
        variable,
        configuration,
      };
    },
    updateResult(params) {
      this.dispatch('IndexedDB:updateResult', params);
    }
  },
};
</script>
<style lang="scss" scoped>
@import '@/common/style/variables.scss';
  .editor-panel {
    position: relative;
    .script-line{
        position: absolute;
        width: 100%;
        height: 6px;
        left: 0;
        bottom: -2px;
        z-index: 3;
        border-bottom: 1px solid #dcdee2;
        cursor: ns-resize;
    }
    &.full-screen {
        top: 85px;
        right: 0;
        bottom: 0;
        left: 0;
        position: fixed;
        z-index: 100;
        background-color: #fff;
        height: 100% !important;
    }
    .new-sidebar-spin {
        background-color: rgba(255, 255, 255, .1);
    }
  }
  .log-panel {
    margin-top: 1px;
    border-top: $border-width-base $border-style-base $border-color-base;
    background-color: $background-color-base;
    .workbench-tabs {
      position: $relative;
      height: 100%;
      overflow: hidden;
      box-sizing: border-box;
      z-index: 3;
      .workbench-tab-wrapper {
        display: flex;
        border-top: $border-width-base $border-style-base #dcdcdc;
        border-bottom: $border-width-base $border-style-base #dcdcdc;
        &.full-screen {
            position: fixed;
            left: 0;
            right: 0;
            top: 52px;
            z-index: 1200;
        }
        .workbench-tab {
          flex: 1;
          display: flex;
          flex-direction: row;
          flex-wrap: nowrap;
          justify-content: flex-start;
          align-items: flex-start;
          height: 32px;
          background-color: $body-background;
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
            background-color: $background-color-base;
            color: $title-color;
            cursor: pointer;
            min-width: 100px;
            max-width: 200px;
            overflow: hidden;
            margin-right: 2px;
            border: 1px solid #eee;
            &.active {
              margin-top: 1px;
              background-color: $body-background;
              color: $primary-color;
              border-radius: 4px 4px 0 0;
              border: 1px solid $border-color-base;
              border-bottom: 2px solid $primary-color;
            }
          }
        }
      }
    }
  }
</style>

