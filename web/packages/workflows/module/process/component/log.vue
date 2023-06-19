<template>
  <div
    class="log-panel">
    <log
      :logs="script.log"
      :log-line="script.logLine"
      :script-view-state="scriptViewState"/>
    <Icon class="close-log" size="18" type="md-close" @click="closeTab"></Icon>
  </div>
</template>
<script>
import util from '@dataspherestudio/shared/common/util';
import log from '@dataspherestudio/shared/components/consoleComponent/log.vue';
import mixin from '@dataspherestudio/shared/common/service/mixin';
export default {
  components: {
    log,
  },
  mixins: [mixin],
  props: {
    height: Number,
    logs: {
      type: [String , Array],
    }
  },
  data() {
    return {
      localLog: {
        log: { all: '', error: '', warning: '', info: '' },
        logLine: 1,
      },
      scriptViewState: {
        cacheLogScroll: 0,
        bottomContentHeight: this.height || '250'
      },
      script: this.convertLogs(this.logs)
    }
  },
  beforeDestroy() {
  },
  watch: {
    logs(v) {
      this.script = this.convertLogs(v)
    }
  },
  mounted() {
  },
  methods: {
    convertLogs(logs) {
      const tmpLogs =  {
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
    },
    closeTab() {
      this.$emit('close')
    }
  }
}
</script>
<style lang="scss" scoped>
@import '@dataspherestudio/shared/common/style/variables.scss';
  .log-panel {
    z-index: 1001;
    margin-top: 1px;
    border-top: $border-width-base $border-style-base $border-color-base;
    @include border-color($border-color-base, $dark-border-color-base);
    @include bg-color($light-base-color, $dark-base-color);
  }
  .close-log {
    position: absolute;
    top: 9px;
    right: 15px;
  }
</style>
