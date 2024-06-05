<template>
  <div class="workbench-log-view" :style="{height:height+'px'}">
    <div class="log-tools">
      <div class="log-tools-control">
        <Tabs
          v-model="curPage"
          class="log-tabs">
          <TabPane
            name="all"
            label="All"
          />
          <!-- <TabPane
            :label="errorLabel"
            name="error"
          /> -->
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
          :placeholder="$t('message.common.filter')"
          suffix="ios-search"
          size="small"
          class="log-search"/>
      </div>
    </div>
    <we-editor
      ref="logEditor"
      :value="formattedLogs()"
      :style="{height: height - 36 + 'px',overflow:'hidden'}"
      @scrollChange="change"
      @onload="editorOnload"
      type="log"/>
  </div>
</template>
<script>
import { trim, forEach, filter, debounce } from 'lodash';
import elementResizeEvent from '@dataspherestudio/shared/common/helper/elementResizeEvent';
export default {
  props: {
    status: String,
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
    logLine: {
      type: Number,
      default: 1,
    },
    scriptViewState: {
      type: Object,
      default: () => {
        return {};
      },
    }
  },
  data() {

    return {
      curPage: 'all',
      searchText: '',
      errorNum: 0,
      warnNum: 0,
      // errorLabel: (h) => {
      //   return h('div', [
      //     h('span', 'Error'),
      //     h('Badge', {
      //       props: {
      //         count: this.errorNum,
      //         className: 'err-badge',
      //         type: 'error'
      //       },
      //     }),
      //   ]);
      // },
      warnLabel: (h) => {
        return h('div', [
          h('span', 'Warning'),
          h('Badge', {
            props: {
              count: this.warnNum,
              className: 'warn-badge',
              type: 'warning'
            },
          }),
        ]);
      }
    };
  },
  watch: {
    logLine(val) {
      if (this.$refs.logEditor && this.$refs.logEditor.editor) {
        this.$refs.logEditor.editor.revealLine(val);
      }
    },
    logs() {
      if (this.$refs.logEditor && this.$refs.logEditor.editor && this.logs.all) {
        const firstError = this.logs.all.split('\n').findIndex(line => line.indexOf('ERROR') > -1)
        setTimeout(() => {
          if (this.$refs.logEditor && this.$refs.logEditor.editor) {
            this.$refs.logEditor.editor.revealLine(firstError > 0 ? firstError + 1 : 1);
          }
        }, 1200);
      }
    }
  },
  computed: {
    height() {
      return this.scriptViewState.bottomContentHeight - 34 // 减去tab高度
    }
  },
  mounted() {
    elementResizeEvent.bind(this.$el, this.resize);
  },
  beforeDestroy() {
    elementResizeEvent.unbind(this.$el);
  },
  methods: {
    resize: debounce(function() {
      if (this.$refs.logEditor && this.$refs.logEditor.editor) {
        this.$refs.logEditor.editor.layout();
      }
    }, 1000),
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
      return logs[this.curPage]
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
    }
  },
};
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .workbench-log-view {
    height: 100%;
    .log-tools {
      height: 36px;
      line-height: 36px;
      padding-left: 10px;
      @include bg-color($light-base-color, $dark-submenu-color);
      position: $relative;
      border-bottom: 2px solid $border-color-base;
      @include border-color($border-color-base, $dark-border-color-base);
      overflow: hidden;
      margin-bottom: -2px;
      .log-tools-control {
        display: inline-block;
        position: $absolute;
        top: 2px;
        .log-tabs {
          display: inline-block;
          position: $absolute;
        }
        .log-search {
          width: 100px;
          position: $absolute;
          left: 350px;
          top: 5px;
          font-size: $font-size-small;
        }
        .err-badge {
          background: $error-color !important;
        }
        .warn-badge {
          background: $yellow-color !important;
        }
      }
    }
  }
</style>

