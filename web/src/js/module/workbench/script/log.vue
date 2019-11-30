<template>
  <div class="workbench-log-view">
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
          :placeholder="$t('message.workBench.body.script.log.placeholder')"
          suffix="ios-search"
          size="small"
          class="log-search"></Input>
      </div>
    </div>
    <we-editor
      ref="logEditor"
      :value="curLogs()"
      @scrollChange="change"
      @onload="editorOnload"
      type="log"/>
  </div>
</template>
<script>
import { trim, forEach, filter } from 'lodash';
export default {
  props: {
    logs: {
      type: Object,
      default: function() {
        return {
          all: '',
          error: '',
          warning: '',
          info: '',
        };
      },
    },
    height: {
      type: Number,
      default: 0,
    },
    logLine: {
      type: Number,
      default: 1,
    },
    scriptViewState: {
      type: Object,
      default: () => {
        return {};
      },
    },
  },
  data() {
    return {
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
  watch: {
    // 如果发生拖动，对编辑器进行layout
    'height': function(val) {
      if (val && this.$refs.logEditor.editor) {
        this.$refs.logEditor.editor.layout();
      }
    },
    logLine(val) {
      if (this.$refs.logEditor.editor) {
        this.$refs.logEditor.editor.revealLine(val);
      }
    },
  },
  methods: {
    change() {
      this.scriptViewState.cacheLogScroll = this.$refs.logEditor.editor.getScrollTop();
    },
    editorOnload() {
      this.$refs.logEditor.editor.setScrollPosition({ scrollTop: this.scriptViewState.cacheLogScroll || 0 });
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
        let regexp = new RegExp(`.*${formatedVal}.*`, 'gi');
        MatchText = filter(log.split('\n'), (item) => {
          return regexp.test(item);
        }).join('\n');
        regexp = null;
      } else {
        MatchText = log;
      }
      return MatchText;
    },
    curLogs() {
      const logs = this.formattedLogs();
      return logs[this.curPage];
    },
  },
};
</script>
