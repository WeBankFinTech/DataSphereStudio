<template>
  <div class="monitor-wrap">
    <div class="monitor-holder"></div>
    <Tabs value="screen" :animated="false" class="tab-wrap">
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
                <Icon custom="iconfont icon-zongtiaoyongcishu" class="overview-icon"></Icon>
                <div class="overview-info">
                  <div class="overview-label">{{$t("message.dataService.apiMonitor.callCnt")}}</div>
                  <div class="overview-value"><span>{{callTotalCnt}}</span>次</div>
                </div>
              </div>
              <div class="overview">
                <Icon custom="iconfont icon-zongzhihangshichangyongliang" class="overview-icon"></Icon>
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
              </Table>
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
                  @on-change="handlePageChange"
                  @on-page-size-change="handlePageSizeChange"
                />
              </div>
            </div>
          </div>
        </div>
      </Tab-pane>
    </Tabs>
    <div class="monitor-chart-mask" :class="{'shown': monitorModalShow}" @click="hideMonitorModal"></div>
    <div class="monitor-chart-modal" :class="{'shown': monitorModalShow}">
      <div class="mc-title">
        <span>API监控图表</span>
        <div class="mc-close" @click="hideMonitorModal">
          <Icon custom="iconfont icon-riqi" size="16"></Icon>
        </div>
      </div>
      <div class="mc-body">
        <Tabs type="card">
          <Tab-pane label="数据服务错误码">
            <div class="mc-range">
              <rangeGroup @on-date-change="getModalChartData" />
            </div>
            <div ref="chartErrorcode" style="height:300px"></div>
          </Tab-pane>
          <Tab-pane label="APP请求次数">
            <div class="mc-range">
              <rangeGroup @on-date-change="getModalChartData" />
            </div>
            <div ref="chartReq" style="height:300px"></div>
          </Tab-pane>
          <Tab-pane label="流量带宽">
            <div class="mc-range">
              <rangeGroup @on-date-change="getModalChartData" />
            </div>
            <div ref="chartBrandwidth" style="height:300px"></div>
          </Tab-pane>
          <Tab-pane label="平均响应时间">
            <div class="mc-range">
              <rangeGroup @on-date-change="getModalChartData" />
            </div>
            <div ref="chartAvgTime" style="height:300px"></div>
          </Tab-pane>
        </Tabs>
      </div>
    </div>
  </div>
</template>
<script>
import echarts from 'echarts';
import api from "@/common/service/api";
import rangeGroup from '../common/rangeGroup.vue';
export default {
  components: {
    rangeGroup,
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
          title: this.$t("message.dataService.apiMonitor.col_failRate"),
          key: 'failRate'
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
          title: this.$t("message.dataService.operation"),
          key: "operation",
          slot: "operation"
        }
      ],
      onlineCnt: 0,
      offlineCnt: 0,
      callTotalCnt: 0,
      callTotalTime: 0,
      listCnt: [],
      listRate: [],
      listDetail: [],
      pageData: {
        total: 0,
        pageNow: 1,
        pageSize: 10
      },
      monitorModalShow: false,
    }
  },
  mounted() {
    this.getOnlineApiCnt();
    this.getOfflineApiCnt();
    this.getCallListByCnt();
    this.getCallListByFailRate();
    this.getRangeScreenData();
    this.getCallListDetail();
    this.drawLine();
    window.addEventListener('resize', this.chartResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.chartResize)
  },
  methods: {
    dateFormat(date) {
      const dt = date ? date : new Date();
      const format = [
        dt.getFullYear(), dt.getMonth() + 1, dt.getDate()
      ].join('-').replace(/(?=\b\d\b)/g, '0'); // 正则补零
      return `${format} 00:00:00`;
    },
    chartResize() {
      this.echart && this.echart.resize()
    },
    drawLine() {
      this.echart = echarts.init(this.$refs.resourceLine)
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
          data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
          type: 'value',
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          name: "请求数目(平均QPS)",
          nameLocation: "middle",
          nameTextStyle: {
            color: "#333",
            fontSize: 16,
            verticalAlign: "top",
          },
          nameGap: 80
        },
        series: [{
          data: [82000, 93200, 90100, 93400, 129000, 133000, 130200],
          type: 'line',
          smooth: true,
          color: '#2E92F7'
        }]
      };
      this.echart.setOption(option);
    },
    getOnlineApiCnt() {
      api.fetch('/dss/framework/dbapi/apimonitor/onlineApiCnt', {
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.onlineCnt = res.onlineApiCnt;
      }).catch((err) => {
        console.error(err)
      });
    },
    getOfflineApiCnt() {
      api.fetch('/dss/framework/dbapi/apimonitor/offlineApiCnt', {
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.offlineCnt = res.offlineApiCnt;
      }).catch((err) => {
        console.error(err)
      });
    },
    getRangeScreenData(date) {
      const range = date || {
        startTime: this.dateFormat(new Date(Date.now() - 7*86400*1000)),
        endTime: this.dateFormat()
      }
      this.getCallTotalCnt(range);
      this.getCallTotalTime(range);
    },
    getCallTotalCnt(range) {
      api.fetch('/dss/framework/dbapi/apimonitor/callTotalCnt', {
        ...range,
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.callTotalCnt = res.callTotalCnt;
      }).catch((err) => {
        console.error(err)
      });
    },
    getCallTotalTime(range) {
      api.fetch('/dss/framework/dbapi/apimonitor/callTotalTime', {
        ...range,
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.callTotalTime = res.callTotalTime;
      }).catch((err) => {
        console.error(err)
      });
    },
    getCallListByCnt() {
      api.fetch('/dss/framework/dbapi/apimonitor/callListByCnt', {
        startTime: this.dateFormat(new Date(Date.now() - 86400*1000)),
        endTime: this.dateFormat(),
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.listCnt = res.list;
      }).catch((err) => {
        console.error(err)
      });
    },
    getCallListByFailRate() {
      api.fetch('/dss/framework/dbapi/apimonitor/callListByFailRate', {
        startTime: this.dateFormat(new Date(Date.now() - 86400*1000)),
        endTime: this.dateFormat(),
        workspaceId: this.$route.query.workspaceId
      }, 'get').then((res) => {
        this.listRate = res.list;
      }).catch((err) => {
        console.error(err)
      });
    },
    getCallListDetail() {
      api.fetch('/dss/framework/dbapi/apimonitor/list', {
        workspaceId: this.$route.query.workspaceId,
        pageNow: this.pageData.pageNow,
        pageSize: this.pageData.pageSize,
      }, 'get').then((res) => {
        this.listDetail = res.list;
        this.pageData.total = res.total;
      }).catch((err) => {
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
      this.monitorModalShow = true;
      // ajax
      this.drawMonitorChart();
    },
    hideMonitorModal() {
      this.monitorModalShow = false;
    },
    getModalChartData(range) {
      console.log(range)
    },
    drawMonitorChart() {
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
          data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        },
        yAxis: {
          type: 'value',
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          name: "请求数目(平均QPS)",
          nameLocation: "middle",
          nameTextStyle: {
            color: "#333",
            fontSize: 16,
            verticalAlign: "top",
          },
          nameGap: 80
        },
        series: [{
          data: [82000, 93200, 90100, 93400, 129000, 133000, 130200],
          type: 'line',
          smooth: true,
          color: '#2E92F7'
        }]
      };
      const chart1 = echarts.init(this.$refs.chartErrorcode)
      const chart2 = echarts.init(this.$refs.chartReq)
      const chart3 = echarts.init(this.$refs.chartBrandwidth)
      const chart4 = echarts.init(this.$refs.chartAvgTime)
      chart1.setOption(option)
      chart2.setOption(option)
      chart3.setOption(option)
      chart4.setOption(option)
    }
  },
}
</script>
<style lang="scss" scoped>
.monitor-wrap {
  min-height: calc(100% - 78px);
  position: relative;
  .monitor-holder {
    height: 36px;
    background: #fff;
  }
  .tab-wrap {
    margin-top: -36px;
    padding: 0 24px;
  }
  .monitor-chart-mask {
    display: none;
    z-index: 1;
    position: fixed;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    &.shown {
      display: block;
    }
  }
  .monitor-chart-modal {
    z-index: 2;
    position: absolute;
    bottom: 0;
    left: 0;
    right: 0;
    height: 460px;
    background: #fff;
    box-shadow: -2px 0 10px 0 #DEE4EC;
    border-radius: 2px;
    opacity: 0;
    &.shown {
      opacity: 1;
      animation: modalshow .4s 1;
    }
    @keyframes modalshow {
      from {
        transform: translateY(100%);
      }
      to {
        transform: translateY(0);
      }
    }
    .mc-title {
      position: relative;
      padding: 12px 24px;
      font-size: 16px;
      line-height: 24px;
      color: #333;
      background: #F8F9FC;
      .mc-close {
        position: absolute;
        padding: 12px 24px;
        right: 0;
        top: 0;
        cursor: pointer;
      }
    }
    .mc-body {
      padding: 15px 24px;
    }
    .mc-range {
      display: flex;
      justify-content: flex-end;
    }
  }
}
.metrics-wrap {
  display: flex;
  margin-bottom: 24px;
  .metrics {
    flex: 1;
    background: #fff;
    border-radius: 2px;
    .metrics-dashboard {
      height: 80px;
      display: flex;
      align-items: center;
      padding: 10px 24px;
      border-radius: 2px;
      background: #fff;
      .dash-content {
        flex: 1;
        .dash-title {
          font-size: 16px;
          line-height: 20px;
          color: #333;
        }
        .dash-item {
          margin-left: 10px;
          font-size: 14px;
          line-height: 20px;
          color: #666;
          .dash-value {
            margin-left: 5px;
            color: #220000;
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
        border-radius: 4px;
        &:last-child {
          margin-left: 24px;
        }
        .overview-icon {
          font-size: 40px;
          color: #2E92F7;;
        }
        .overview-info {
          flex: 1;
          margin-left: 24px;
          .overview-label {
            font-size: 14px;
            color: #666;
            line-height: 20px;
          }
          .overview-value {
            font-size: 14px;
            color: #000;
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
      .metrics-head-title {
        padding-left: 8px;
        border-left: 4px solid #2E92F7;
        font-size: 16px;
        line-height: 22px;
        color: #333;
      }
    }
    .metrics-body {
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