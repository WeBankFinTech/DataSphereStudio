<template>
  <div
    class="workbench-history">
    <history-table
      ref="globalHistory"
      :columns="column"
      :data="list"
      :height="height"
      :no-data-text="$t('message.scripts.emptyText')"
      size="small"
      border
      stripe/>
    <Page
      class="workbench-history-page"
      :total="PageTotal"
      :page-size-opts="page.sizeOpts"
      :page-size="page.size"
      :current="page.current"
      show-sizer
      show-total
      size="small"
      @on-change="change"
      @on-page-size-change="changeSize"/>
  </div>
</template>
<script>
import axios from 'axios';
import module from '../index';
import api from '@dataspherestudio/shared/common/service/api';
import util from '@dataspherestudio/shared/common/util';
import storage from '@dataspherestudio/shared/common/helper/storage';
import resizeMixin from './mixin.js';
import table from '@dataspherestudio/shared/components/virtualTable';
import mixin from '@dataspherestudio/shared/common/service/mixin';
import { EXECUTE_COMPLETE_TYPE } from '@dataspherestudio/shared/common/config/const';
export default {
  components: {
    historyTable: table.historyTable,
  },
  mixins: [mixin, resizeMixin],
  props: {
    history: {
      type: Array,
      required: true,
    },
    runType: String,
    node: Object,
    scriptViewState: Object
  },
  data() {
    return {
      column: [],
      page: {
        start: 0,
        end: 10,
        current: 1,
        size: 10,
        sizeOpts: [10, 20, 30, 50],
      },
    };
  },
  computed: {
    PageTotal() {
      return this.history.length;
    },
    list() {
      return this.history.slice(this.page.start, this.page.end).map(item =>{
        return {
          ...item,
          runningTime: EXECUTE_COMPLETE_TYPE.indexOf(item.status) !== -1 ? item.runningTime || 0 : new Date().getTime() - item.createDate
        }
      });
    },
    height() {
      return this.scriptViewState.bottomContentHeight - 34 - 35 // 减去tab及分页高度
    }
  },
  watch: {
    // history(v) {
    // }
  },
  mounted() {
    this.initData();
  },
  beforeDestroy() {
    if (this.updateTimer) {
      clearTimeout(this.updateTimer);
    }
    this.updateTimer = null;
  },
  methods: {
    initData() {
      this.pageingData();
      this.updateHistoryFromLinkis();
      const baseinfo = storage.get('baseInfo', 'local')
      this.column = [
        {
          width: 50,
          align: 'center',
          renderType: 'index',
        }, {
          title: this.$t('message.scripts.history.columns.taskID.title'),
          key: 'taskID',
          align: 'center',
          width: 80,
        }, {
          title: this.$t('message.scripts.history.columns.runningTime.title'),
          key: 'runningTime',
          align: 'center',
          width: 80,
          renderType: 'convertTime',
        }, {
          title: this.$t('message.scripts.history.columns.createDate.title'),
          key: 'createDate',
          align: 'center',
          width: 100,
          renderType: 'formatTime',
        }, {
          title: this.$t('message.scripts.history.columns.status.title'),
          key: 'status',
          align: 'center',
          width: 120,
          renderType: 'tag',
        }, {
          title: this.$t('message.scripts.history.columns.data.title'),
          key: 'data',
          align: 'center',
          // 溢出以...显示
          ellipsis: true,
          // renderType: 'tooltip',
        }, {
          title: this.$t('message.scripts.history.columns.failedReason.title'),
          key: 'failedReason',
          align: 'center',
          className: 'history-failed',
          width: 220,
          renderType: 'a'
        }, {
          title: this.$t('message.scripts.history.columns.control.title'),
          key: 'control',
          fixed: 'right',
          align: 'center',
          width: 140,
          className: 'history-control',
          renderType: 'button',
          renderParams: [{
            label: this.$t('message.scripts.history.columns.control.view'),
            action: this.viewHistory,
          }, {
            label: this.$t('message.scripts.history.columns.control.download'),
            action: this.downloadLog,
          }, {
            label: this.$t('message.scripts.history.columns.control.noticeopen'),
            action: this.subscribe,
            isHide: (data) => {
              // 任务状态为：已提交/排队中/资源申请中/运行/超时/重试时
              const status = ["Submitted","Inited","Scheduled","Running","Timeout","WaitForRetry"].indexOf(data.status) > -1
              return baseinfo.enableTaskNotice !== false && status && data.subscribed !== 1
            }
          }, {
            label: this.$t('message.scripts.history.columns.control.noticeclose'),
            action: this.subscribe,
            isHide: (data) => {
              // 任务状态为：已提交/排队中/资源申请中/运行/超时/重试时
              const status = ["Submitted","Inited","Scheduled","Running","Timeout","WaitForRetry"].indexOf(data.status) > -1
              return baseinfo.enableTaskNotice !== false && data.subscribed === 1 && status
            }
          }, {
            label: this.$t('message.scripts.solution'),
            action: this.viewFAQ,
            style: { color: '#ed4014'},
            isHide: (data)=> { return window.$APP_CONF && window.$APP_CONF.error_report && data && data.solution && data.solution.solutionUrl}
          }, {
            label: this.$t('message.scripts.reporterror'),
            action: this.report,
            style: { color: '#ed4014'},
            isHide: (data)=> { return window.$APP_CONF && window.$APP_CONF.error_report && data && data.status === 'Failed' && (!data.solution || !data.solution.solutionUrl)}
          }],
        },
      ];
    },

    updateHistoryFromLinkis() {
      // 未完成的任务
      const unCompleteTasks = this.history.map((item, index) => {
        if (EXECUTE_COMPLETE_TYPE.indexOf(item.status) === -1 && item.taskID) {
          return item.taskID
        }
      }).filter(item => item !== undefined);
      
      if (unCompleteTasks.length) {
        // 获取未完成的任务详情, 一个脚本同时正在执行的应该只有一个
        const promiseArr = [];
        unCompleteTasks.forEach(taskID => {
          const fetchReq = api.fetch(`/jobhistory/list`, { taskID }, 'get')
          promiseArr.push(fetchReq)
        })
        Promise.all(promiseArr).then(resArr => {
          resArr.forEach((it, index) => {
            const taskID = unCompleteTasks[index];
            const historyItemidx = this.history.findIndex(it => {
              return it.taskID === taskID
            })
            const task = it && it.tasks[0];
            if (task) {
              if (EXECUTE_COMPLETE_TYPE.indexOf(task.status) !== -1) {
                this.$emit('update-from-history', task);
              }
              const item = {
                ...this.history[historyItemidx], 
                runningTime: task.costTime, 
                createDate: task.createdTime,
                data: task.executionCode,
                status: task.status,
              }
              if (task.errDesc && !item.failedReason) {
                item.failedReason = (task.errCode || '') + task.errDesc
              }
              this.$set(this.history, historyItemidx, item)
            }
          })
        }).finally(() => {
          clearTimeout(this.updateTimer);
          this.updateTimer = setTimeout(this.updateHistoryFromLinkis, 5000);
        })
      }
    },

    async viewHistory(params) {
      const supportModes = this.getSupportModes();
      const match = supportModes.find((s) => s.rule.test(params.row.fileName));
      const ext = match ? match.ext : '.hql';
      const name = `history_item_${params.row.taskID}${ext}`;
      const md5Id = util.md5(name);
      this.dispatch('Workbench:add', {
        id: md5Id, // 唯一标识，就算文件名修改，它都能标识它是它
        taskID: params.row.taskID,
        execID: params.row.execID,
        filename: name,
        filepath: '',
        // saveAs表示临时脚本，需要关闭或保存时另存
        saveAs: true,
        code: params.row.data,
        type: 'historyScript',
        addWay: 'follow',
        currentNodeKey: this.node ? this.node.key : '',
        // status:params.row.status,
      }, (f) => {
        if (f) {
          this.$Message.success(this.$t('message.scripts.history.success.open'));
        }
      });
    },

    async downloadLog(params) {
      const name = params.row.fileName + '__' + Date.now() + '.log';
      if (!params.row.logPath) {
        await api.fetch(`/jobhistory/${params.row.taskID}/get`, 'get').then((rst) => {
          this.$set(params.row, 'logPath', rst.task.logPath);
        });
      }
      axios.post(module.data.API_PATH + 'filesystem/download', {
        path: params.row.logPath,
      }).then((rst) => {
        const link = document.createElement('a');
        const blob = new Blob([rst.data], { type: rst.headers['content-type'] });
        link.href = window.URL.createObjectURL(blob);
        link.download = name;
        let event = null;
        if (window.MouseEvent) {
          event = new MouseEvent('click');
        } else {
          event = document.createEvent('MouseEvents');
        }
        const flag = link.dispatchEvent(event);
        this.$nextTick(() => {
          if (flag) {
            this.$Message.success(this.$t('message.scripts.history.success.download'));
          }
        });
      }).catch((err) => {
        this.$Message.error(err.message);
      });
    },
    change(page) {
      this.page.current = page;
      this.pageingData();
    },
    changeSize(size) {
      this.page.size = size;
      this.page.current = 1;
      this.pageingData();
    },
    pageingData() {
      this.page.start = (this.page.current - 1) * this.page.size;
      this.page.end = this.page.start + this.page.size;
    },
    parsingLanguaue() {
      const supportTypes = this.getSupportModes();
      const match = supportTypes.find((s) => s.runType === this.runType);
      if (!match) {
        return '';
      }
      if (match.lang === 'hql') {
        return 'sql';
      }
      return match.lang;
    },
    viewFAQ({ row }) {
      row.solution && window.open(row.solution.solutionUrl, '_blank')
    },
    subscribe(params) {
      if (this.changeSubscribeStatus) return this.$Message.warning(this.$t('message.scripts.optlimit'))
      this.changeSubscribeStatus = true;
      const taskId = params.row.taskID;
      const action = params.row.subscribed ? 'cancel' : 'add';
      const scriptName = this.node ? this.node.title : params.row.fileName
      api.fetch(`/dss/scriptis/task/subscribe`, {action, taskId, scriptName}, 'get').then(() => {
        const index = this.history.findIndex(it => it.taskID === params.row.taskID)
        if (index > -1) {
          this.$set(this.history, index, {...this.history[index], subscribed: params.row.subscribed ? 0 : 1})
        }
        this.$Message.warning(action === 'add' ? this.$t('message.scripts.optsuccess') :this.$t('message.scripts.cancelOptSuccess'))
      }).finally(() => {
        setTimeout(() => {
          this.changeSubscribeStatus = false
        }, 5000);
      });
    },
    async report({row}) {
      const res = await api.fetch(`/jobhistory/${row.taskID}/get`, 'get') || { task: {}}
      if (res.task && (res.task.errCode || res.task.errDesc)) {
        api.fetch('/dss/guide/solution/reportProblem', {
          requestUrl: `/jobhistory/${row.taskID}/get`,
          queryParams: {},
          requestBody: {},
          requestHeaders: {},
          responseBody: {
            taskID: row.taskID,
            errCode: res.task.errCode,
            errDesc: res.task.errDesc
          }
        }).then(()=>{
          this.$Message.success(this.$t('message.scripts.reported'))
        })
      }
    }
  },
};
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
.workbench-history {

  .workbench-history-page {
      display: flex;
      align-items: center;
      justify-content: center;
      height: 30px;
      padding-top: 5px;
  }
  .history-failed {
      color: $error-color;
  }
  .history-control {
      .ivu-table-cell {
          padding: 0;
      }
  }
}
</style>

