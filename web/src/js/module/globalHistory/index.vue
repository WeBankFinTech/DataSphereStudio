<template>
  <div class="global-history">
    <Form
      class="global-history-searchbar"
      ref="searchBar"
      :model="searchBar"
      :rules="ruleInline"
      inline>
      <FormItem
        prop="id"
        label="JobID"
        v-if="!isLogModuleShow">
        <InputNumber
          v-model="searchBar.id"
          :placeholder="$t('message.globalHistory.formItems.id.placeholder')"
          style="width:100px;"
          :min="1"
          :readonly="isLogModuleShow"></InputNumber>
      </FormItem>
      <Divider
        type="vertical"
        class="divider"
        v-if="isAdminModel && !isLogModuleShow"/>
      <FormItem
        prop="proxyUser"
        :label="$t('message.newGlobalHistory.userName')"
        v-if="isAdminModel && !isLogModuleShow">
        <Input
          :maxlength="50"
          v-model="searchBar.proxyUser"
          :placeholder="$t('message.newGlobalHistory.searchName')"
          style="width:120px;"
          :readonly="isLogModuleShow" />
      </FormItem>
      <Divider
        type="vertical"
        class="divider"
        v-if="!isLogModuleShow"/>
      <FormItem
        prop="shortcut"
        :label="$t('message.globalHistory.formItems.date.label')"
        v-if="!isLogModuleShow">
        <DatePicker
          :options="shortcutOpt"
          v-model="searchBar.shortcut"
          type="daterange"
          placement="bottom-start"
          :placeholder="$t('message.globalHistory.formItems.date.placeholder')"
          style="width: 200px"
          :editable="false"/>
      </FormItem>
      <Divider
        type="vertical"
        class="divider"
        v-if="!isLogModuleShow"/>
      <FormItem
        prop="engine"
        :label="$t('message.globalHistory.formItems.engine.label')"
        v-if="!isLogModuleShow">
        <Select
          v-model="searchBar.engine"
          style="min-width:70px;">
          <Option
            v-for="(item) in engineTypes"
            :label="item.label"
            :value="item.value"
            :key="item.value"/>
        </Select>
      </FormItem>
      <Divider
        type="vertical"
        class="divider"
        v-if="!isLogModuleShow"/>
      <FormItem
        prop="status"
        :label="$t('message.globalHistory.formItems.status.label')"
        v-if="!isLogModuleShow">
        <Select
          v-model="searchBar.status"
          style="min-width:90px;">
          <Option
            v-for="(item) in statusType"
            :label="item.label"
            :value="item.value"
            :key="item.value"/>
        </Select>
      </FormItem>
      <Divider
        type="vertical"
        class="divider"
        v-if="!isLogModuleShow"/>
      <FormItem
        :class="{'float-right': isLogModuleShow}">
        <Button
          type="primary"
          @click="search"
          style="margin-right: 10px;"
          v-if="!isLogModuleShow">{{ $t('message.constants.search') }}</Button>
        <Button
          type="warning"
          @click="reset"
          style="margin-right: 10px;"
          v-if="!isLogModuleShow">{{ $t('message.constants.reset') }}</Button>
        <Button
          type="warning"
          @click="logRefresh"
          style="margin-right: 10px;"
          v-if="isLogModuleShow">{{$t('message.constants.refresh')}}</Button>
        <Button
          type="primary"
          @click="back"
          style="margin-right: 10px;"
          v-if="isLogModuleShow">{{$t('message.newConst.back')}}</Button>
        <Button
          type="primary"
          @click="switchAdmin"
          v-if="!isLogModuleShow && isLogAdmin">{{ isAdminModel ? $t('message.newGlobalHistory.generalView') : $t('message.newGlobalHistory.manageView') }}</Button>
      </FormItem>
    </Form>
    <Icon
      v-show="isLoading"
      type="ios-loading"
      size="30"
      class="global-history-loading"/>
    <log
      v-if="isLogModuleShow"
      :logs="logs"
      :from-line="fromLine"/>
    <div v-else>
      <div
        class="global-history-table"
        :style="{'height': moduleHeight + 'px'}">
        <history-table
          v-show="!isLoading"
          ref="globalHistory"
          :columns="column"
          :data="list"
          :height="moduleHeight"
          :no-data-text="$t('message.constants.noDataText')"
          size="small"
          border
          stripe/>
      </div>
      <div class="global-history-page">
        <Page
          :total="pageSetting.total"
          :page-size="pageSetting.pageSize"
          :current="pageSetting.current"
          size="small"
          show-total
          show-elevator
          @on-change="changePage"/>
      </div>
    </div>
  </div>
</template>
<script>
import table from '@js/component/table';
import log from './log.vue';
import Module from './index';
import api from '@/js/service/api';
import util from '@/js/util';
export default {
  name: 'GlobalHistory',
  components: {
    historyTable: table.historyTable,
    log,
  },
  mixins: [Module.mixin],
  data() {
    return {
      list: [],
      column: [],
      isLoading: false,
      pageSetting: {
        total: 0,
        pageSize: 50,
        current: 1,
      },
      searchBar: {
        id: null,
        proxyUser: '',
        engine: 'all',
        status: 'all',
        shortcut: '',
      },
      lastActived: {
        id: null,
        proxyUser: '',
        logPath: '',
      },
      inputType: 'number',
      shortcutOpt: {
        shortcuts: [
          {
            text: this.$t('message.globalHistory.shortcuts.week'),
            value() {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              return [start, end];
            },
          },
          {
            text: this.$t('message.globalHistory.shortcuts.month'),
            value() {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              return [start, end];
            },
          },
          {
            text: this.$t('message.globalHistory.shortcuts.threeMonths'),
            value() {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              return [start, end];
            },
          },
        ],
      },
      statusType: [{
        label: this.$t('message.globalHistory.statusType.all'),
        value: 'all'
      }, {
        label: this.$t('message.globalHistory.statusType.inited'),
        value: 'Inited'
      }, {
        label: this.$t('message.globalHistory.statusType.running'),
        value: 'Running'
      }, {
        label: this.$t('message.globalHistory.statusType.succeed'),
        value: 'Succeed'
      }, {
        label: this.$t('message.globalHistory.statusType.cancelled'),
        value: 'Cancelled'
      }, {
        label: this.$t('message.globalHistory.statusType.failed'),
        value: 'Failed'
      }, {
        label: this.$t('message.globalHistory.statusType.scheduled'),
        value: 'Scheduled'
      }, {
        label: this.$t('message.globalHistory.statusType.timeout'),
        value: 'Timeout'
      }],
      engineTypes: [{
        label: this.$t('message.globalHistory.engineTypes.all'),
        value: 'all'
      }, {
        label: 'spark',
        value: 'spark'
      }, {
        label: 'hive',
        value: 'hive'
      }, {
        label: 'pipeline',
        value: 'pipeline'
      }, {
        label: 'python',
        value: 'python'
      }, {
        label: 'flowexecution',
        value: 'flowexecution'
      }, {
        label: 'appjoint',
        value: 'appjoint'
      }],
      ruleInline: {

      },
      isLogAdmin: false,
      isAdminModel: false,
      isLogModuleShow: false,
      logs: {
        all: '',
        error: '',
        warning: '',
        info: '',
      },
      fromLine: 1,
      moduleHeight: 0,
    };
  },
  mounted() {
    this.isLogAdmin = this.getLogManager();
    this.init();
    this.moduleHeight = this.$route.query.height - 90;
  },
  activated() {
    this.init();
  },
  methods: {
    init() {
      this.search();
    },
    convertTimes(runningTime) {
      const time = Math.floor(runningTime / 1000);
      if (time < 0) {
        return `0${this.$t('message.constants.time.second')}`;
      } else if (time < 60) {
        return `${time}${this.$t('message.constants.time.second')}`;
      } else if (time < 3600) {
        return `${(time / 60).toPrecision(2)}${this.$t('message.constants.time.minute')}`;
      } else if (time < 86400) {
        return `${(time / 3600).toPrecision(2)}${this.$t('message.constants.time.hour')}`;
      }
      return `${(time / 86400).toPrecision(2)}${this.$t('message.constants.time.day')}`;
    },
    async viewHistory(params) {
      this.isLogModuleShow = true;
      this.getLogs(params.row.taskID);
    },
    getLogs(jobId) {
      this.isLoading = true;
      this.lastActived.id = Number(this.searchBar.id);
      api.fetch(`/jobhistory/${jobId}/get`, 'get').then((res) => {
        this.searchBar.id = jobId;
        this.searchBar.proxyUser = this.lastActived.proxyUser = res.task.umUser;
        this.lastActived.logPath = res.task.logPath;
        if (!res.task.logPath) {
          const errCode = res.task.errCode ? '\n错误码：' + res.task.errCode : '';
          const errDesc = res.task.errDesc ? '\n错误描述：' + res.task.errDesc : '';
          const info = '未获取到日志！' + errCode + errDesc;
          this.logs = { all: info, error: '', warning: '', info: '' };
          this.fromLine = 1;
          return;
        }
        const params = {
          path: this.lastActived.logPath,
        };
        if (this.isAdminModel) {
          params.proxyUser = res.task.umUser;
        }
        api.fetch('/filesystem/openLog', params, 'get').then((rst) => {
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
    },
    getParams(page) {
      const startDate = this.searchBar.shortcut[0];
      const endDate = this.searchBar.shortcut[1];
      const params = {
        taskID: this.searchBar.id,
        executeApplicationName: this.searchBar.engine,
        status: this.searchBar.status,
        startDate: startDate && startDate.getTime(),
        endDate: endDate && endDate.getTime(),
        pageNow: page || this.pageSetting.current,
        pageSize: this.pageSetting.pageSize,
        proxyUser: this.searchBar.proxyUser,
      };
      if (!this.isAdminModel) {
        delete params.proxyUser;
      }
      if (this.searchBar.id) {
        delete params.executeApplicationName;
        delete params.status;
        delete params.startDate;
        delete params.endDate;
      } else {
        let { engine, status, shortcut } = this.searchBar;
        if (engine === 'all') {
          delete params.executeApplicationName;
        }
        if (status === 'all') {
          delete params.status;
        }
        if (!shortcut[0]) {
          delete params.startDate;
        }
        if (!shortcut[1]) {
          delete params.endDate;
        }
        delete params.taskID;
      }
      return params;
    },
    changePage(page) {
      this.isLoading = true;
      const params = this.getParams(page);
      this.column = this.getColumns();
      api.fetch('/jobhistory/list', params, 'get').then((rst) => {
        this.isLoading = false;
        this.list = this.getList(rst.tasks);
        this.pageSetting.current = page;
        this.pageSetting.total = rst.totalPage;
      }).catch(() => {
        this.isLoading = false;
        this.list = [];
      });
    },
    search() {
      this.isLoading = true;
      const params = this.getParams();
      this.column = this.getColumns();
      api.fetch('/jobhistory/list', params, 'get').then((rst) => {
        this.pageSetting.total = rst.totalPage;
        this.isLoading = false;
        this.list = this.getList(rst.tasks);
      }).catch(() => {
        this.list = [];
        this.isLoading = false;
      });
    },
    getList(list) {
      const getFailedReason = (item) => {
        return item.errCode && item.errDesc ? item.errCode + item.errDesc : item.errCode || item.errDesc || '';
      };
      if (!this.isAdminModel) {
        return list.map((item) => {
          return {
            taskID: item.taskID,
            fileName: item.fileName,
            executionCode: item.executionCode,
            status: item.status,
            costTime: item.costTime,
            requestApplicationName: item.requestApplicationName,
            executeApplicationName: item.executeApplicationName,
            createdTime: item.createdTime,
            progress: item.progress,
            failedReason: getFailedReason(item),
          };
        });
      }
      return list.map((item) => {
        return Object.assign(item, {
          failedReason: getFailedReason(item),
        });
      });
    },
    getColumns() {
      const column = [{
        title: this.$t('message.newGlobalHistory.tableColumns.control.title'),
        key: 'control',
        fixed: 'right',
        align: 'center',
        width: 80,
        className: 'history-control',
        renderType: 'button',
        renderParams: [{
          label: this.$t('message.newGlobalHistory.tableColumns.control.label'),
          action: this.viewHistory,
        }],
      }, {
        title: this.$t('message.newGlobalHistory.tableColumns.taskID'),
        key: 'taskID',
        align: 'center',
        width: 100,
      }, {
        title: this.$t('message.newGlobalHistory.tableColumns.fileName'),
        key: 'fileName',
        align: 'center',
        width: 150,
      }, {
        title: this.$t('message.newGlobalHistory.tableColumns.executionCode'),
        key: 'executionCode',
        align: 'center',
        width: 500,
        // 溢出以...显示
        ellipsis: true,
        // renderType: 'tooltip',
      }, {
        title: this.$t('message.newGlobalHistory.tableColumns.status'),
        key: 'status',
        align: 'center',
        width: 200,
        renderType: 'if',
        renderParams: {
          action: this.setRenderType,
        },
      }, {
        title: this.$t('message.newGlobalHistory.tableColumns.costTime'),
        key: 'costTime',
        align: 'center',
        width: 100,
        renderType: 'convertTime',
      }, {
        title: `${this.$t('message.newGlobalHistory.tableColumns.requestApplicationName')}/${this.$t('message.newGlobalHistory.tableColumns.executeApplicationName')}`,
        key: 'requestApplicationName',
        align: 'center',
        width: 140,
        renderType: 'concat',
        renderParams: {
          concatKey: 'executeApplicationName',
        },
      }, {
        title: this.$t('message.newGlobalHistory.tableColumns.user'),
        key: 'umUser',
        align: 'center',
        width: 100,
      }, {
        title: this.$t('message.newGlobalHistory.tableColumns.createdTime'),
        key: 'createdTime',
        align: 'center',
        width: 100,
        renderType: 'formatTime',
      }, {
        title: this.$t('message.newGlobalHistory.tableColumns.failedReason'),
        key: 'failedReason',
        align: 'center',
        className: 'history-failed',
        width: 220,
        renderType: 'a',
        renderParams: {
          hasDoc: this.checkIfHasDoc,
          action: this.linkTo,
        },
      }];
      if (!this.isAdminModel) {
        const index = column.findIndex((item) => item.key === 'umUser');
        column.splice(index, 1);
      }
      return column;
    },
    reset() {
      this.searchBar = {
        id: null,
        proxyUser: '',
        engine: 'all',
        status: 'all',
        shortcut: '',
      };
    },
    switchAdmin() {
      if (!this.isLoading) {
        if (this.isAdminModel) {
          this.isLogModuleShow = false;
          this.searchBar.id = null;
          this.searchBar.proxyUser = '';
        }
        this.isAdminModel = !this.isAdminModel;
        this.search();
      }
    },
    back() {
      if (this.isLoading) {
        return this.$Message.warning(this.$t('message.newGlobalHistory.logLoading'));
      }
      this.isLogModuleShow = false;
      this.searchBar.id = Number(this.lastActived.id);
      this.searchBar.proxyUser = this.lastActived.proxyUser;
    },
    linkTo(params) {
      this.$router.push({
        path: '/console/FAQ',
        query: {
          errCode: parseInt(params.row.failedReason),
          isSkip: true,
        },
      });
    },
    checkIfHasDoc(params) {
      const errCodeList = [11011, 11012, 11013, 11014, 11015, 11016, 11017];
      const errCode = parseInt(params.row.failedReason);
      if (errCodeList.indexOf(errCode) !== -1) {
        return true;
      }
      return false;
    },
    setRenderType(params) {
      if (params.row.status === 'Running' && params.row.progress !== 0) {
        return ({
          type: 'Progress',
          value: params.row.progress,
        });
      } else {
        return ({
          type: 'Tag',
          value: params.row.status,
        });
      }
    },
    logRefresh() {
      if (this.isLoading) {
        return this.$Message.warning(this.$t('message.newGlobalHistory.logLoading'));
      }
      if (!this.lastActived.logPath) {
        return this.getLogs(this.lastActived.id);
      }
      this.isLoading = true;
      const params = {
        path: this.lastActived.logPath,
      };
      if (this.isAdminModel) {
        params.proxyUser = this.lastActived.proxyUser;
      }
      api.fetch('/filesystem/openLog', params, 'get').then((rst) => {
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
    },
  },
};
</script>
<style src="./index.scss" lang="scss"></style>
