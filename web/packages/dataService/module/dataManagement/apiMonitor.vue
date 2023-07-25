<template>
  <div class="monitor-wrap">
    <div class="monitor-holder"></div>
    <Tabs :value="currentTab" :animated="false" @on-click="changeTab" class="tab-wrap">
      <Tab-pane :label='$t("message.dataService.apiMonitor.staticsScreen")' name="screen">
        <div class="metrics-wrap">
          <div class="metrics">
            <div class="metrics-dashboard">
              <div class="dash-content">
                <span class="dash-title">{{$t("message.dataService.apiMonitor.apiTotal")}}</span>
                <span class="dash-item">{{$t("message.dataService.apiMonitor.published")}}<span class="dash-value">{{onlineCnt}}</span></span>
                <span class="dash-item">{{$t("message.dataService.apiMonitor.notPublish")}}<span class="dash-value">{{offlineCnt}}</span></span>
              </div>
              <rangeGroup @on-date-change="getRangeScreenData" />
            </div>
            <div class="metrics-overview">
              <div class="overview">
                <SvgIcon class="overview-icon" icon-class="total-cnt" />
                <div class="overview-info">
                  <div class="overview-label">{{$t("message.dataService.apiMonitor.callCnt")}}</div>
                  <div class="overview-value"><span>{{callTotalCnt}}</span>次</div>
                </div>
              </div>
              <div class="overview">
                <SvgIcon class="overview-icon" icon-class="total-time" />
                <div class="overview-info">
                  <div class="overview-label">{{$t("message.dataService.apiMonitor.callTime")}}</div>
                  <div class="overview-value"><span>{{callTotalTime}}</span>ms</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="metrics-wrap">
          <div class="metrics">
            <div class="metrics-head">
              <div class="metrics-head-title">{{$t("message.dataService.apiMonitor.resource")}}</div>
            </div>
            <div class="metrics-body">
              <div ref="resourceLine" style="height:300px"></div>
              <Spin v-show="loadingChart" size="large" fix/>
            </div>
          </div>
        </div>
        <div class="metrics-wrap">
          <div class="metrics">
            <div class="metrics-head">
              <div class="metrics-head-title">{{$t("message.dataService.apiMonitor.errorTop10")}}</div>
            </div>
            <div class="metrics-body">
              <Table :columns="columnsRate" :data="listRate" size="large">
                <template slot-scope="{ index }" slot="rank">
                  <span>{{index + 1}}</span>
                </template>
                <template slot-scope="{ row }" slot="failRate">
                  <span>{{`${row.failRate}%`}}</span>
                </template>
              </Table>
              <Spin v-show="loadingRate" size="large" fix/>
            </div>
          </div>
        </div>
        <div class="metrics-wrap">
          <div class="metrics">
            <div class="metrics-head">
              <div class="metrics-head-title">{{$t("message.dataService.apiMonitor.callTop10")}}</div>
            </div>
            <div class="metrics-body">
              <Table :columns="columnsCnt" :data="listCnt" size="large">
                <template slot-scope="{ index }" slot="rank">
                  <span>{{index + 1}}</span>
                </template>
              </Table>
              <Spin v-show="loadingCnt" size="large" fix/>
            </div>
          </div>
        </div>
      </Tab-pane>
      <Tab-pane :label='$t("message.dataService.apiMonitor.staticsDetail")' name="detail">
        <div class="metrics-wrap">
          <div class="metrics">
            <div class="metrics-body">
              <Table :columns="columnsDetail" :data="listDetail" size="large">
                <template slot-scope="{ row }" slot="operation">
                  <a class="operation" @click="showMonitorModal(row)">
                    {{$t("message.dataService.apiMonitor.viewMonitor")}}
                  </a>
                </template>
              </Table>
              <div class="pagebar" v-if="pageData.total">
                <Page
                  :total="pageData.total"
                  :current="pageData.pageNow"
                  show-elevator
                  show-sizer
                  show-total
                  @on-change="handlePageChange"
                  @on-page-size-change="handlePageSizeChange"
                />
              </div>
              <Spin v-show="loadingDetail" size="large" fix/>
            </div>
          </div>
        </div>
      </Tab-pane>
    </Tabs>
    <monitorChart v-if="monitorModalShow" :monitorModalShow="monitorModalShow" :api="selectApi" @on-hide="hideMonitorModal" />
  </div>
</template>
<script>
import echarts from 'echarts';
import api from '@dataspherestudio/shared/common/service/api';
import rangeGroup from '../common/rangeGroup.vue';
import monitorChart from './monitorChart.vue';
import util from '../common/util';
import eventbus from '@dataspherestudio/shared/common/helper/eventbus';
export default {
  components: {
    rangeGroup,
    monitorChart
  },
  data() {
    return {
      columnsRate: [
        {
          title: this.$t("message.dataService.apiMonitor.col_rank"),
          slot: 'rank'
        },
        {
          title: 'ID',
          key: 'id'
        },
        {
          title: this.$t("message.dataService.apiMonitor.col_apiName"),
          key: 'apiName'
        },
        {
          title: this.$t("message.dataService.apiMonitor.col_reqCnt"),
          key: 'totalCnt'
        },
        {
          title: this.$t("message.dataService.apiMonitor.col_failCnt"),
          key: 'failCnt'
        },
        {
          title: this.$t("message.dataService.apiMonitor.col_failRate"),
          key: 'failRate',
          slot: 'failRate'
        }
      ],
      columnsCnt: [
        {
          title: this.$t("message.dataService.apiMonitor.col_rank"),
          slot: 'rank'
        },
        {
          title: 'ID',
          key: 'id'
        },
        {
          title: this.$t("message.dataService.apiMonitor.col_apiName"),
          key: 'apiName'
        },
        {
          title: this.$t("message.dataService.apiMonitor.col_reqCnt"),
          key: 'totalCnt'
        },
        {
          title: this.$t("message.dataService.apiMonitor.col_totalTime"),
          key: 'totalTime'
        },
        {
          title: this.$t("message.dataService.apiMonitor.col_avgTime"),
          key: 'avgTime'
        }
      ],
      columnsDetail: [
        {
          title: 'ID',
          key: 'id'
        },
        {
          title: this.$t("message.dataService.apiMonitor.col_apiName"),
          key: 'apiName'
        },
        {
          title: this.$t("message.dataService.apiMonitor.col_groupName"),
          key: 'groupName'
        },
        {
          title: this.$t("message.dataService.apiMonitor.col_label"),
          key: 'label'
        },
        {
          title: this.$t("message.dataService.apiMonitor.col_createBy"),
          key: 'createBy'
        },
        {
          title: this.$t("message.dataService.apiMonitor.col_updateTime"),
          key: 'updateTime'
        },
        {
          title: this.$t("message.dataService.apiMonitor.col_createTime"),
          key: 'createTime'
        },
        {
          title: this.$t("message.dataService.operation"),
          key: "operation",
          slot: "operation"
        }
      ],
      currentTab: 'screen',
      onlineCnt: 0,
      offlineCnt: 0,
      callTotalCnt: 0,
      callTotalTime: 0,
      listCnt: [],
      listRate: [],
      dataPast24Hour: [],
      loadingCnt: true,
      loadingRate: true,
      loadingChart: true,
      // 计量详情tab
      listDetail: [],
      loadingDetail: true,
      pageData: {
        total: 0,
        pageNow: 1,
        pageSize: 10
      },
      monitorModalShow: false,
      selectApi: {}
    }
  },
  mounted() {
    this.getOnlineApiCnt();
    this.getOfflineApiCnt();
    this.getResource24Hour();
    this.getCallListByCnt();
    this.getCallListByFailRate();
    this.getRangeScreenData();
    this.getCallListDetail();
    window.addEventListener('resize', this.chartResize)
    eventbus.on('theme.change', this.changeTheme);
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.chartResize)
    eventbus.off('theme.change', this.changeTheme);
  },
  methods: {
    changeTab(tab) {
      this.currentTab = tab;
    },
    chartResize() {
      this.currentTab == 'screen' && this.echart && this.echart.resize();
    },
    changeTheme(theme) {
      if (this.echart) {
        this.drawResourceChart(theme)
      }
    },
    getResource24Hour() {
      this.loadingChart = true;
      api.fetch('/dss/data/api/apimonitor/callCntForPast24H', {
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.dataPast24Hour = res.list;
        this.drawResourceChart(localStorage.getItem('theme'));
        this.loadingChart = false;
      }).catch((err) => {
        console.error(err)
        this.loadingChart = false;
      });
    },
    drawResourceChart(theme) {
      this.echart = this.echart ? this.echart : echarts.init(this.$refs.resourceLine)
      var option = {
        grid: {
          left: 100,
          right: 40,
          top: 30,
          bottom: 30
        },
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          axisTick: {
            show: false
          },
          axisLabel: {
            color: theme == 'dark' ? "rgba(255,255,255,0.85)" : "#333"
          },
          axisLine: {
            lineStyle: {
              color: theme == 'dark' ? "rgba(255,255,255,0.85)" : "#ccc"
            }
          },
          data: this.dataPast24Hour.map(i => i.key)
        },
        yAxis: {
          type: 'value',
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          axisLabel: {
            color: theme == 'dark' ? "rgba(255,255,255,0.85)" : "#333"
          },
          name: "请求数目(平均QPS)",
          nameLocation: "middle",
          nameTextStyle: {
            color: theme == 'dark' ? "rgba(255,255,255,0.85)" : "#333",
            fontSize: 16,
            verticalAlign: "top",
          },
          nameGap: 80
        },
        series: [{
          data: this.dataPast24Hour.map(i => i.value),
          type: 'line',
          smooth: true,
          color: '#2E92F7'
        }]
      };
      this.echart.setOption(option);
    },
    getOnlineApiCnt() {
      api.fetch('/dss/data/api/apimonitor/onlineApiCnt', {
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.onlineCnt = res.onlineApiCnt;
      }).catch((err) => {
        console.error(err)
      });
    },
    getOfflineApiCnt() {
      api.fetch('/dss/data/api/apimonitor/offlineApiCnt', {
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.offlineCnt = res.offlineApiCnt;
      }).catch((err) => {
        console.error(err)
      });
    },
    getRangeScreenData(date) {
      const range = date || {
        startTime: util.dateFormat(new Date(Date.now() - 7*86400*1000)),
        endTime: util.dateFormat(new Date(), '23:59:59')
      }
      this.getCallTotalCnt(range);
      this.getCallTotalTime(range);
    },
    getCallTotalCnt(range) {
      api.fetch('/dss/data/api/apimonitor/callTotalCnt', {
        ...range,
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.callTotalCnt = res.callTotalCnt;
      }).catch((err) => {
        console.error(err)
      });
    },
    getCallTotalTime(range) {
      api.fetch('/dss/data/api/apimonitor/callTotalTime', {
        ...range,
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.callTotalTime = res.callTotalTime;
      }).catch((err) => {
        console.error(err)
      });
    },
    getCallListByCnt() {
      this.loadingCnt = true;
      api.fetch('/dss/data/api/apimonitor/callListByCnt', {
        startTime: util.dateFormat(new Date(Date.now() - 86400*1000)),
        endTime: util.dateFormat(),
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.listCnt = res.list;
        this.loadingCnt = false;
      }).catch((err) => {
        console.error(err)
        this.loadingCnt = false;
      });
    },
    getCallListByFailRate() {
      this.loadingRate = true;
      api.fetch('/dss/data/api/apimonitor/callListByFailRate', {
        startTime: util.dateFormat(new Date(Date.now() - 86400*1000)),
        endTime: util.dateFormat(),
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.listRate = res.list;
        this.loadingRate = false;
      }).catch((err) => {
        console.error(err)
        this.loadingRate = false;
      });
    },
    getCallListDetail() {
      this.loadingDetail = true;
      api.fetch('/dss/data/api/apimonitor/list', {
        workspaceId: this.$route.query.workspaceId,
        pageNow: this.pageData.pageNow,
        pageSize: this.pageData.pageSize,
      }, 'get').then((res) => {
        this.loadingDetail = false;
        this.listDetail = res.list;
        this.pageData.total = res.total;
      }).catch((err) => {
        this.loadingDetail = false;
        console.error(err)
      });
    },
    handlePageSizeChange(pageSize) {
      this.pageData.pageSize = pageSize;
      this.getCallListDetail();
    },
    handlePageChange(page) {
      this.pageData.pageNow = page;
      this.getCallListDetail();
    },
    showMonitorModal(row) {
      this.selectApi = row;
      this.monitorModalShow = true;
    },
    hideMonitorModal() {
      this.monitorModalShow = false;
      this.selectApi = {};
    },
  },
}
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.monitor-wrap {
  height: 100%;
  min-height: calc(100% - 78px);
  position: relative;
  .monitor-holder {
    height: 36px;
    @include bg-color(#fff, $dark-base-color);
  }
  .tab-wrap {
    min-height: 100%;
    margin-top: -36px;
    padding: 0 24px;
  }
}
.metrics-wrap {
  display: flex;
  margin-bottom: 24px;
  .metrics {
    flex: 1;
    width: 100%;
    @include bg-color(#fff, $dark-menu-base-color);
    border-radius: 2px;
    .metrics-dashboard {
      height: 80px;
      display: flex;
      align-items: center;
      padding: 10px 24px;
      border-radius: 2px;
      @include bg-color(#fff, $dark-menu-base-color);
      .dash-content {
        flex: 1;
        .dash-title {
          font-size: 16px;
          line-height: 20px;
          @include font-color(#333, $dark-text-color);
        }
        .dash-item {
          margin-left: 10px;
          font-size: 14px;
          line-height: 20px;
          @include font-color(#666, $dark-text-color);
          .dash-value {
            margin-left: 5px;
            @include font-color(#220000, $dark-text-color);
          }
        }
      }
    }
    .metrics-overview {
      display: flex;
      padding: 20px 24px;
      padding-top: 0;
      .overview {
        flex: 1;
        display: flex;
        align-items: center;
        padding: 25px 20px;
        border: 1px solid #DEE4EC;
        @include border-color(#DEE4EC, $dark-border-color);
        border-radius: 4px;
        color: #2E92F7;
        &:last-child {
          margin-left: 24px;
        }
        .overview-icon {
          font-size: 40px;
        }
        .overview-info {
          flex: 1;
          margin-left: 24px;
          .overview-label {
            font-size: 14px;
            @include font-color(#666, $dark-text-color);
            line-height: 20px;
          }
          .overview-value {
            font-size: 14px;
            @include font-color(#000, $dark-text-color);
            line-height: 30px;
            span {
              font-size: 16px;
              margin-right: 5px;
            }
          }
        }
      }
    }
    .metrics-head {
      padding: 16px 24px;
      border-bottom: 1px solid #D9D9D9;
      @include border-color(#D9D9D9, $dark-border-color);
      .metrics-head-title {
        padding-left: 8px;
        border-left: 4px solid #2E92F7;
        font-size: 16px;
        line-height: 22px;
        @include font-color(#333, $dark-text-color);
      }
    }
    .metrics-body {
      position: relative;
      padding: 20px 24px;
    }
  }
}
.pagebar {
  float: right;
  margin: 15px 0;
  padding: 10px 0;
}
</style>
