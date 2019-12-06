<template>
  <div
    class="resource">
    <Button
      class="resource-refresh"
      type="primary"
      size="small"
      @click="getExplorerInfo()">{{ $t('message.constants.refresh') }}</Button>
    <Tabs
      v-model="currentTab"
      :animated="false"
      @on-click="getExplorerInfo">
      <TabPane
        :label="$t('message.resource.tabs.first')"
        name="first">
        <Table
          :columns="engineColumns"
          :data="engineData"
          :height="tableHeight"
          border
          stripe/>
      </TabPane>
      <TabPane
        :label="$t('message.resource.tabs.second')"
        name="second">
        <Table
          :columns="userColumns"
          :data="userData"
          :height="tableHeight"
          border
          stripe/>
      </TabPane>
      <TabPane
        :label="$t('message.resource.tabs.third')"
        name="third">
        <Table
          :columns="serverColumns"
          :data="serverData"
          :height="tableHeight"
          border
          stripe/>
      </TabPane>
    </Tabs>
    <div
      :style="{'height': height + 'px'}"
      class="resource-loading"
      v-show="loading">
      <Icon
        type="ios-loading"
        size="30"
        class="resource-loading-icon"/>
    </div>
  </div>
</template>
<script>
import { keys } from 'lodash';
import moment from 'moment';
import expandRow from './table-expand.vue';
import api from '@/js/service/api';
export default {
  name: 'Resource',
  components: {
  },
  data() {
    return {
      height: 0,
      loading: false,
      // 默认页
      currentTab: 'first',
      // 点击查看时的状态
      isClickBefore: true,
      serverColumns: [{
        title: this.$t('message.resource.columns.moduleName'),
        key: 'moduleName',
        width: 180,
        align: 'center'
      }, {
        title: this.$t('message.resource.columns.totalResource'),
        align: 'center',
        children: [
          {
            title: 'CPU',
            key: 'totalCpu',
            align: 'center',
            sortable: true
          }, {
            title: this.$t('message.resource.columns.memory'),
            key: 'totalMemory',
            align: 'center',
            sortable: true
          }
        ]
      }, {
        title: this.$t('message.resource.columns.usedResource'),
        align: 'center',
        children: [
          {
            title: 'CPU',
            key: 'usedCpu',
            align: 'center',
            sortable: true
          }, {
            title: this.$t('message.resource.columns.memory'),
            key: 'usedMemory',
            align: 'center',
            sortable: true
          }
        ]
      }],
      userColumns: [{
        title: this.$t('message.resource.columns.moduleName'),
        key: 'moduleName',
        align: 'center'
      }, {
        title: this.$t('message.resource.columns.usedResource'),
        align: 'center',
        children: [
          {
            title: 'CPU',
            key: 'usedCpu',
            align: 'center',
            sortable: true
          }, {
            title: this.$t('message.resource.columns.memory'),
            key: 'usedMemory',
            align: 'center',
            sortable: true
          }
        ]
      }, {
        title: this.$t('message.resource.columns.initializingResource'),
        align: 'center',
        children: [
          {
            title: 'CPU',
            key: 'lockedCpu',
            align: 'center',
            sortable: true
          }, {
            title: this.$t('message.resource.columns.memory'),
            key: 'lockedMemory',
            align: 'center',
            sortable: true
          }
        ]
      }],
      engineColumns: [{
        type: 'expand',
        width: 50,
        render: (h, params) => {
          return h(expandRow, {
            props: {
              row: params.row,
              formatCpu: this.formatCpu
            }
          })
        }
      }, {
        title: this.$t('message.resource.columns.engineInstance'),
        key: 'engineInstance',
        align: 'center',
        sortable: true
      }, {
        title: this.$t('message.resource.columns.applicationName'),
        key: 'applicationName',
        align: 'center'
      },
      {
        title: this.$t('message.resource.columns.usedTime'),
        key: 'usedTime',
        align: 'center',
        width: 100,
        render: (h, scope) => {
          return h('span', {}, moment(scope.row.usedTime).format('YYYY-MM-DD HH:mm:ss'))
        }
      },
      {
        title: this.$t('message.resource.columns.engineStatus'),
        key: 'engineStatus',
        align: 'center',
        render: (h, scope) => {
          return h('span', {}, scope.row.engineStatus)
        }
      }, {
        title: this.$t('message.resource.columns.usedResource'),
        align: 'center',
        children: [
          {
            title: 'CPU',
            key: 'usedResource.driver.cpu',
            align: 'center',
            sortable: true,
            render: (h, scope) => {
              return h('span', {}, this.formatCpu(this.formatEngines(scope.row.usedResource).cpu))
            }
          }, {
            title: this.$t('message.resource.columns.memory'),
            key: 'usedResource.driver.memory',
            align: 'center',
            sortable: true,
            render: (h, scope) => {
              return h('span', {}, this.formatEngines(scope.row.usedResource).memory)
            }
          }
        ]
      }, {
        title: this.$t('message.resource.columns.username'),
        key: 'statusInfo.engineInfo.user',
        align: 'center',
        render: (h, scope) => {
          return h('span', {}, scope.row.statusInfo.engineInfo.user)
        }
      }],
      engineData: [],
      userData: [],
      serverData: [],
      tableHeight: 0,
    };
  },
  mounted() {
    this.height = this.$route.query.height;
    this.tableHeight = this.height - 50;
    this.currentTab = 'first';
    this.getExplorerInfo();
  },

  beforeDestroy() {
    this.engineData = [];
    this.serverData = [];
    this.userData = [];
  },

  methods: {
    // 获取资源管理器数据
    getExplorerInfo() {
      if (this.loading) return this.$Message.warning(this.$t('message.constants.warning.api'));
      this.loading = true;
      if (this.currentTab === 'first') {
        this.getEngines();
      } else if (this.currentTab === 'second') {
        this.getUserResource();
      } else if (this.currentTab === 'third') {
        this.getModuleResources();
      }
    },

    getEngines() {
      api.fetch('/resourcemanager/engines').then((rst) => {
        this.loading = false;
        this.engineData = rst.engines;
      }).catch((err) => {
        this.loading = false;
      });
    },

    getUserResource() {
      this.userData = [];
      api.fetch('/resourcemanager/userresources').then((rst) => {
        this.loading = false;
        const keyList = keys(rst);
        keyList.forEach((item, index) => {
          const lockedResource = JSON.parse(rst[item].lockedResource);
          const usedResource = JSON.parse(rst[item].usedResource);
          if (item === 'sparkEngineManager') {
            this.userData.push({
              moduleName: item,
              usedCpu: this.formatCpu(usedResource.driver.cpu),
              usedMemory: usedResource.driver.memory,
              lockedCpu: this.formatCpu(lockedResource.driver.cpu),
              lockedMemory: lockedResource.driver.memory,
            });
          } else {
            this.userData.push({
              moduleName: item,
              usedCpu: this.formatCpu(usedResource.cpu),
              usedMemory: usedResource.memory,
              lockedCpu: this.formatCpu(lockedResource.cpu),
              lockedMemory: lockedResource.memory,
            });
          }
        });
      }).catch((err) => {
        this.loading = false;
      });
    },

    // 调取接口获取数据
    getModuleResources() {
      this.serverData = [];
      api.fetch('/resourcemanager/moduleresources').then((rst) => {
        this.loading = false;
        const keyList = keys(rst);
        keyList.forEach((item, index) => {
          const totalResource = JSON.parse(rst[item].totalResource);
          const usedResource = JSON.parse(rst[item].usedResource);
          if (item === 'sparkEngineManager') {
            this.serverData.push({
              moduleName: item,
              usedCpu: this.formatCpu(usedResource.driver.cpu),
              usedMemory: usedResource.driver.memory,
              totalCpu: this.formatCpu(totalResource.driver.cpu),
              totalMemory: totalResource.driver.memory,
            });
          } else {
            this.serverData.push({
              moduleName: item,
              usedCpu: this.formatCpu(usedResource.cpu),
              usedMemory: usedResource.memory,
              totalCpu: this.formatCpu(totalResource.cpu),
              totalMemory: totalResource.memory,
            });
          }
        });
      }).catch((err) => {
        this.loading = false;
      });
    },

    formatCpu(str) {
      if (str === -1) {
        return this.$t('message.resource.noLimit');
      }
      return str + ` ${this.$t('message.resource.core')}`;
    },

    // 点击应用地址实现跳转
    skipTo(url) {
      location.href = `${url}`;
    },

    killSessionJob(index, row) {
    },

    killQueryJob(index, row, data) {
    },

    // 如果是特殊通道会话，颜色高亮显示，html结构中调用function不能带括号
    isSpecialSession(row, index) {
      return (row.description && row.description.specialSession && row.description.specialSession !== '<distribution>') ? 'blueText' : null;
    },

    formatEngines(resource) {
      const formated = JSON.parse(resource);
      if (formated.driver) {
        return formated.driver;
      }
      return formated;
    },
  },
};
</script>
<style src="./index.scss" lang="scss"></style>
