<template>
  <div class="log-content">
    <div class="header-bar">
      <Button
        type="primary"
        @click="back"
        style="margin-right: 10px;">{{$t('message.linkis.back')}}</Button>
      <Button
        type="warning"
        @click="logRefresh"
        style="margin-right: 10px;">{{$t('message.linkis.refresh')}}</Button>
    </div>
    <log
      :logs="logs"
      :from-line="fromLine"/>
  </div>
</template>
<script>
import api from '@dataspherestudio/shared/common/service/api';
import log from '@dataspherestudio/shared/components/log/index.vue';
import util from '@dataspherestudio/shared/common/util';
export default {
  name: 'Logs',
  components: {
    log
  },
  data() {
    return {
      fromLine: 1,
      logs: {
        all: '',
        error: '',
        warning: '',
        info: '',
      },
      isLoading: true,
      params: {
        path: '',
        proxyUser: ''
      }
    }
  },
  created() {
    this.getPath(this.$route.query.taskID)
  },
  methods: {
    back() {
      if (this.isLoading) {
        return this.$Message.warning(this.$t('message.linkis.logLoading'));
      }
      this.$router.go(-1);
    },
    logRefresh() {
      if (this.isLoading) {
        return this.$Message.warning(this.$t('message.linkis.logLoading'));
      }
      if (!this.params.logPath) {
        return this.getPath(this.$route.query.taskID);
      }
      this.isLoading = true;
      this.getLogs(this.params);
    },
    getPath(jobId) {
      this.isLoading = true;
      api.fetch(`/jobhistory/${jobId}/get`, 'get').then((res) => {
        if (!res.task.logPath) {
          const errCode = res.task.errCode ? '\n错误码：' + res.task.errCode : '';
          const errDesc = res.task.errDesc ? '\n错误描述：' + res.task.errDesc : '';
          const info = this.$t('message.common.dss.nolog') + errCode + errDesc;
          this.logs = { all: info, error: '', warning: '', info: '' };
          this.fromLine = 1;
          this.isLoading = false;
          return;
        }
        this.params = {
          path: res.task.logPath,
          // proxyUser: res.task.umUser
        };
        this.getLogs(this.params);
      }).catch(() => {
        this.isLoading = false;
      });
    },
    getLogs() {
      api.fetch('/filesystem/openLog', this.params, 'get').then((rst) => {
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
    }
  }
}
</script>
<style lang="scss" scoped>
.log-content {
  position: relative;
  width: 80%;
  height: 100%;
  margin: auto;
  .header-bar {
    margin-top: 15px;
    padding-bottom: 10px;
  }
}
</style>
