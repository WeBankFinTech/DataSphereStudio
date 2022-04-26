<template>
  <div class="monitor-chart">
    <div class="monitor-chart-mask" :class="{'shown': monitorModalShow}" @click="hideMonitorModal"></div>
    <div class="monitor-chart-modal" :class="{'shown': monitorModalShow}">
      <div class="mc-title">
        <span>API监控图表</span>
        <div class="mc-close" @click="hideMonitorModal">
          <SvgIcon class="icon" icon-class="close2" />
        </div>
      </div>
      <div class="mc-body">
        <Tabs type="card">
          <Tab-pane label="API请求次数">
            <div class="mc-range">
              <div class="chart-title">API请求次数(次)</div>
              <rangeGroup @on-date-change="getCntByApi" />
            </div>
            <div ref="chartReq" style="height:300px"></div>
          </Tab-pane>
          <Tab-pane label="平均响应时间">
            <div class="mc-range">
              <div class="chart-title">平均响应时间(ms)</div>
              <rangeGroup @on-date-change="getTimeByApi" />
            </div>
            <div ref="chartAvgTime" style="height:300px"></div>
          </Tab-pane>
        </Tabs>
        <Spin v-show="loading" size="large" fix/>
      </div>
    </div>
  </div>
</template>
<script>
import echarts from 'echarts';
import api from '@dataspherestudio/shared/common/service/api';
import rangeGroup from '../common/rangeGroup.vue';
export default {
  components: {
    rangeGroup,
  },
  props: {
    monitorModalShow: {
      type: Boolean,
      default: false
    },
    api: {
      type: Object,
      default: () => {}
    }
  },
  data() {
    return {
      loading: true,
      dataApiTime: [],
      dataApiCnt: []
    }
  },
  mounted() {
    this.initMonitorChart();
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
      this.chartCnt && this.chartCnt.resize();
      this.chartTime && this.chartTime.resize();
    },
    initMonitorChart() {
      const range = {
        startTime: this.dateFormat(new Date(Date.now() - 7*86400*1000)),
        endTime: this.dateFormat()
      }
      this.getTimeByApi(range);
      this.getCntByApi(range);
    },
    getTimeByApi(range) {
      this.loading = true;
      api.fetch('/dss/data/api/apimonitor/callTimeForSinleApi', {
        ...range,
        workspaceId: this.$route.query.workspaceId,
        apiId: this.api.id
      }, 'get').then((res) => {
        this.dataApiTime = res.list;
        this.drawApiTimeChart(localStorage.getItem('theme'));
        this.loading = false;
      }).catch((err) => {
        console.error(err)
        this.loading = false;
      });
    },
    getCntByApi(range) {
      this.loading = true;
      api.fetch('/dss/data/api/apimonitor/callCntForSinleApi', {
        ...range,
        workspaceId: this.$route.query.workspaceId,
        apiId: this.api.id
      }, 'get').then((res) => {
        this.dataApiCnt = res.list;
        this.drawApiCntChart(localStorage.getItem('theme'));
        this.loading = false;
      }).catch((err) => {
        console.error(err)
        this.loading = false;
      });
    },
    hideMonitorModal() {
      this.$emit('on-hide');
      this.chartCnt = null;
      this.chartTime = null;
    },
    drawApiTimeChart(theme) {
      const option = {
        grid: {
          left: 60,
          right: 60,
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
          data: this.dataApiTime.map(i => i.key)
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
        },
        series: [{
          data: this.dataApiTime.map(i => i.value),
          type: 'line',
          smooth: true,
          color: '#2E92F7'
        }]
      };
      this.chartTime = echarts.init(this.$refs.chartAvgTime)
      this.chartTime.setOption(option)
    },
    drawApiCntChart(theme) {
      const option = {
        grid: {
          left: 60,
          right: 60,
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
          data: this.dataApiCnt.map(i => i.key)
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
        },
        series: [{
          data: this.dataApiCnt.map(i => i.value),
          type: 'line',
          smooth: true,
          color: '#2E92F7'
        }]
      };
      this.chartCnt = echarts.init(this.$refs.chartReq)
      this.chartCnt.setOption(option)
    }
  },
}
</script>
<style lang="scss" scoped>
@import "@dataspherestudio/shared/common/style/variables.scss";
.monitor-chart {
  position: relative;
  .monitor-chart-mask {
    display: none;
    z-index: 10;
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
    display: none;
    z-index: 20;
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    height: 460px;
    @include bg-color(#fff, $dark-base-color);
    box-shadow: -2px 0 10px 0 #DEE4EC;
    border-radius: 2px;
    &.shown {
      display: block;
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
      @include font-color(#333, $dark-text-color);
      @include bg-color(#F8F9FC, $dark-active-menu-item);
      .mc-close {
        position: absolute;
        padding: 12px 24px;
        right: 0;
        top: 0;
        cursor: pointer;
        font-size: 16px;
      }
    }
    .mc-body {
      position: relative;
      padding: 15px 24px;
    }
    .mc-range {
      display: flex;
      justify-content: space-between;
      .chart-title {
        font-size: 16px;
        line-height: 32px;
        font-weight: bold;
        @include font-color(#666, $dark-text-color);
      }
    }
  }
}
</style>