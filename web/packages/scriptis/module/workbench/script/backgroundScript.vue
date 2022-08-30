<template>
  <div class="background-script"></div>
</template>
<script>
import {find, findIndex, isEmpty, last, forEach, keys} from 'lodash';
import api from '@dataspherestudio/shared/common/service/api';
import util from '@dataspherestudio/shared/common/util';
import { Script } from '../modal.js';
import Execute from '@dataspherestudio/shared/common/service/execute';
import mixin from '@dataspherestudio/shared/common/service/mixin';
export default {
  mixins: [mixin],
  props: {
    work: {
      type: Object,
      required: true,
    },
  },
  data() {
    return {
      script: null,
      execute: null,
      // TODO【dss-scriptis】前端执行接口降级，原值为postType: 'socket'
      postType: 'http',
      userName: '',
      localLog: null,
    };
  },
  watch: {
    'execute.run': function(val) {
      this.work.data.running = this.script.running = val;
    },
    'execute.status': function(val) {
      this.work.data.status = val;
      this.script.status = val;
    },
  },
  async created() {
    this.userName = this.getUserName();
    // 如果当前work存在data，表示工作已经被打开
    if (this.work.data) {
      this.script = this.work.data;
    } else {
      const supportedMode = find(this.getSupportModes(), (p) => p.rule.test(this.work.filename));
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
      // 删掉无用的code和params，因为已经存储在script对象中
      delete this.work.code;
      delete this.work.params;
    }
    // 加入用户名来区分不同账户下的tab
    this.dispatch('IndexedDB:recordTab', { ...this.work, userName: this.userName });
    this.dispatch('IndexedDB:updateGlobalCache', {
      id: this.userName,
      work: this.work,
    });
  },
  mounted() {
    this.$nextTick(() => {
      this.dispatch('WebSocket:init');
      this.run();
    });
    window.onbeforeunload = () => {
      if (this.work.unsave || this.script.running) {
        return 'need confirm';
      }
      return null;
    };
  },
  beforeDestroy() {
    window.onbeforeunload = null;
  },
  methods: {
    'Workbench:save'(work) {
      if (work.id == this.script.id) {
        this.save();
      }
    },
    'Workbench:socket'({ type, ...args }) {
      if (this.execute) {
        this.execute.trigger(type, Object.assign(args, {
          execute: this.execute,
        }));
      } else if (type === 'downgrade') {
        this.postType = 'http';
      }
    },
    getExecuteData() {
      let initData = {
        method: '/api/rest_j/v1/entrance/execute',
        websocketTag: this.work.id,
        data: {
          executeApplicationName: this.script.application,
          executionCode: this.script.data,
          runType: this.script.runType,
          params: this.formatParams(this.script.params),
          postType: this.postType,
          scriptPath: this.work.filename,
        },
      };
      if (this.work.backgroundType) {
        initData.method = 'dss/scriptis/backgroundservice';
        initData.data.background = this.work.backgroundType;
        initData.data.scriptPath = this.work.filename;
      }

      return initData;
    },
    run() {
      const data = this.getExecuteData();
      // 执行
      this.execute = new Execute(data);
      this.localLog = {
        log: { all: '', error: '', warning: '', info: '' },
        logLine: 1,
      };
      this.execute.start();
      this.execute.once('sendStart', (code) => {
        const name = this.work.filepath || this.work.filename;
        this.$Notice.close(name);
        this.$Notice.info({
          title: this.$t('message.scripts.running'),
          desc: '',
          render: (h) => {
            return h('span', {
              style: {
                'word-break': 'break-all',
                'line-height': '20px',
              },
            }, `开始执行静默脚本 ${this.work.filename} ！`);
          },
          name,
          duration: 5,
        });
        this.work.execID = this.execute.id;
        this.work.taskID = this.execute.taskID;
        if (code) {
          this.script.executionCode = this.script.data = code;
        }
      });
      this.execute.on('log', (logs) => {
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
      });
      this.execute.on('history', (ret) => {
        const index = findIndex(this.script.history, (o) => o.taskID === ret.taskID);
        const findHis = find(this.script.history, (o) => o.taskID === ret.taskID);
        let newItem = null;
        // 这里针对的是导入导出脚本，executionCode为object的情况
        const code = typeof (this.script.executionCode) === 'string' && this.script.executionCode ? this.script.executionCode : this.script.data;
        if (findHis) {
          if (Object.prototype.hasOwnProperty.call(ret, 'logPath')) {
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
      this.execute.on('error', () => {
        this.dispatch('IndexedDB:appendLog', {
          tabId: this.script.id,
          rst: this.script.log,
        });
        setTimeout(() => {
          this.$emit('remove-work', this.work);
          this.dispatch('Workbench:add', {
            id: this.work.id,
            filename: this.work.filename,
            filepath: '',
            saveAs: true,
            noLoadCache: false,
            code: this.script.data,
          });
        }, 500);
      });
      this.execute.on('stateEnd', () => {
        this.dispatch('IndexedDB:appendLog', {
          tabId: this.script.id,
          rst: this.localLog.log,
        });
      });
      this.execute.on('querySuccess', ({ type, task }) => {
        const costTime = util.convertTimestamp(task.costTime);
        this.script.progress = {
          ...this.script.progress,
          costTime
        }
        const name = this.work.filepath || this.work.filename;
        this.$Notice.close(name);
        this.$Notice.success({
          title: this.$t('message.scripts.resultpropt'),
          desc: '',
          render: (h) => {
            return h('span', {
              style: {
                'word-break': 'break-all',
                'line-height': '20px',
              },
            }, `脚本${this.work.filename} ${type}成功, 共耗时: ${costTime}！`);
          },
          name,
          duration: 3,
        });
        // 删除成功时自动关闭脚本，并清空缓存，通知数据库刷新
        this.$emit('remove-work', this.work);
        this.dispatch('IndexedDB:clearLog', this.script.id);
        this.dispatch('IndexedDB:clearResult', this.script.id);
        this.dispatch('IndexedDB:clearProgress', this.script.id);
        this.dispatch('IndexedDB:clearHistory', this.script.id);
        this.dispatch('HiveSidebar:deletedAndRefresh');
      });
      this.execute.on('notice', ({ type, msg, autoJoin }) => {
        const name = this.work.filepath || this.work.filename;
        const label = autoJoin ? `脚本${this.work.filename} ${msg}` : msg;
        this.$Notice.close(name);
        this.$Notice[type]({
          title: this.$t('message.scripts.resultpropt'),
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
    },
    // 将数组格式化成json形式。
    formatParams(params) {
      const trans = (arr) => {
        const tmp = {};
        forEach(arr, (item) => {
          tmp[item.key] = item.value;
        });
        return tmp;
      };
      const variable = isEmpty(params.variable) ? {} : trans(params.variable);
      const configuration = {};
      if (!isEmpty(params.variable)) {
        keys(params.configuration).forEach((key) => {
          configuration[key] = trans(key);
        });
      }
      return {
        variable,
        configuration,
      };
    },
  },
};
</script>
