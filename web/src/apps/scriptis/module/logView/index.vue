<template>
  <div class="log-view">
    <div
      class="log-search"
      @keyup.enter.stop.prevent="search">
      <span class="log-character">{{ $t('message.scripts.logView.taskId') }}</span>
      <Input
        v-model="jobId"
        style="width: 200px;"></Input>
      <Button
        type="primary"
        icon="search"
        :loading="isLoading"
        @click="search()">{{ $t('message.scripts.logView.search') }}</Button>
    </div>
    <div class="log-editor-wrap">
      <div class="log-tools">
        <div class="log-tools-control">
          <Tabs
            v-model="curPage"
            class="log-tabs">
            <TabPane
              name="all"
              label="All"
            />
            <TabPane
              :label="errorLabel"
              name="error"
            />
            <TabPane
              :label="warnLabel"
              name="warning"
            />
            <TabPane
              name="info"
              label="Info"/>
          </Tabs>
          <Input
            v-model="searchText"
            :placeholder="$t('message.scripts.logView.filter')"
            suffix="ios-search"
            size="small"
            class="log-search"></Input>
        </div>
      </div>
      <we-editor
        style="height: calc(100% - 34px);"
        ref="logEditor"
        v-model="curLogs"
        type="log"/>
    </div>
  </div>
</template>
<script>
import { trim, filter, forEach } from 'lodash';
import api from '@/common/service/api';
import util from '@/common/util';

const JOBID_REGEXP = /^\d*$/;

export default {
  data() {
    return {
      jobId: '',
      logs: '',
      fromLine: null,
      isLoading: false,
      curPage: 'all',
      searchText: '',
      errorNum: 0,
      warnNum: 0,
      errorLabel: (h) => {
        return h('div', [
          h('span', 'Error'),
          h('Badge', {
            props: {
              count: this.errorNum,
              className: 'err-badge',
            },
          }),
        ]);
      },
      warnLabel: (h) => {
        return h('div', [
          h('span', 'Warning'),
          h('Badge', {
            props: {
              count: this.warnNum,
              className: 'warn-badge',
            },
          }),
        ]);
      },
    };
  },
  computed: {
    curLogs: {
      get() {
        const logs = this.formattedLogs();
        return logs[this.curPage];
      },
      set: function(val) {
        return val;
      },
    },
  },
  watch: {
    fromLine(val) {
      if (this.$refs.logEditor.editor) {
        this.$refs.logEditor.editor.revealLine(val);
      }
    },
  },
  beforeDestroy() {
    this.logs = null;
    this.fromLine = null;
    this.jobId = null;
  },
  methods: {
    async search() {
      if (!this.isLoading) {
        this.isLoading = true;
        if (!this.jobId || !JOBID_REGEXP.test(this.jobId)) {
          this.$Notify.error({
            title: this.$t('message.scripts.logView.error'),
            message: this.$t('message.scripts.logView.inputTaskId'),
            duration: 1000,
          });
        } else if (this.jobId.length > 0) {
          await api.fetch(`/jobhistory/${this.jobId}/get`, 'get').then((res) => {
            const path = res.task.logPath;
            api.fetch('/filesystem/openLog', {
              path,
            }, 'get').then((rst) => {
              this.isLoading = false;
              if (rst) {
                const log = { all: '', error: '', warning: '', info: '' };
                const convertLogs = util.convertLog(rst.log);
                Object.keys(convertLogs).forEach((key) => {
                  if (convertLogs[key]) {
                    log[key] += convertLogs[key] + '\n';
                  }
                });
                this.logs = log;
                this.fromLine = log['all'].split('\n').length;
              }
            }).catch(() => {
              this.isLoading = false;
            });
          }).catch(() => {
            this.isLoading = false;
          });
        }
      }
    },
    formattedLogs() {
      let logs = {
        all: '',
        error: '',
        warning: '',
        info: '',
      };
      Object.keys(this.logs).map((key) => {
        logs[key] = this.getSearchList(this.logs[key]);
      });
      this.getPointNum(logs);
      return logs;
    },
    getPointNum(logs) {
      const errorLogs = trim(logs.error).split('\n').filter((e) => !!e);
      const warnLogs = trim(logs.warning).split('\n').filter((e) => !!e);
      this.errorNum = errorLogs.length;
      this.warnNum = warnLogs.length;
    },
    getSearchList(log) {
      let MatchText = '';
      const val = this.searchText;
      if (!log) return MatchText;
      if (val) {
        // 这部分代码是为了让正则表达式不报错，所以在特殊符号前面加上\
        let formatedVal = '';
        forEach(val, (o) => {
          if (/^[\w\u4e00-\u9fa5]$/.test(o)) {
            formatedVal += o;
          } else {
            formatedVal += `\\${o}`;
          }
        });
        // 全局和不区分大小写模式，正则是匹配searchText前后出了换行符之外的字符
        const regexp = new RegExp(`.*${formatedVal}.*`, 'gi');
        MatchText = filter(log.split('\n'), (item) => {
          return regexp.test(item);
        }).join('\n');
      } else {
        MatchText = log;
      }
      return MatchText;
    },
  },
};
</script>
<style lang="scss" src="./index.scss">
</style>
