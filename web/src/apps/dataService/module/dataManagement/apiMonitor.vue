<template>
  <div>
    <div class="monitor-holder"></div>
    <Tabs value="screen" class="tab-wrap">
      <Tab-pane :label='$t("message.dataService.apiMonitor.staticsScreen")' name="screen">
        <div class="dashboard">
          <div class="dash-content">
            <span class="dash-title">{{$t("message.dataService.apiMonitor.apiTotal")}}</span>
            <span class="dash-item">{{$t("message.dataService.apiMonitor.published")}}<span class="dash-value">{{onlineCnt}}</span></span>
            <span class="dash-item">{{$t("message.dataService.apiMonitor.notPublish")}}<span class="dash-value">{{offlineCnt}}</span></span>
          </div>
          <div class="dash-range-group">
            <div class="range-group-item" :class="{'group-item-checked': option.key == currentRange}" v-for="option in rangeOptions" :key="option.key" @click="handleDateChange(option)">
              <template v-if="option.key != 'picker'">{{option.name}}</template>
              <Date-picker
                v-else
                :open="datePickerOpen"
                :value="datePickerRange"
                type="daterange"
                placement="bottom-end"
                @on-change="handlePickerChange"
              >
                <div class="date-trigger" @click="handlePickerClick">
                  <span v-if="datePickerRange" class="date-show">{{datePickerRange.join('   -   ')}}</span>
                  <span>日期</span>
                </div>
              </Date-picker>
            </div>
          </div>
        </div>
        <div class="metrics-wrap">
          <div class="metrics metrics-mr">
            <div class="metrics-head">
              <div class="metrics-head-title">{{$t("message.dataService.apiMonitor.resource")}}</div>
            </div>
            <div class="metrics-body">
              hold
            </div>
          </div>
          <div class="metrics">
            <div class="metrics-head">
              <div class="metrics-head-title">{{$t("message.dataService.apiMonitor.dashboard")}}</div>
            </div>
            <div class="metrics-body">
              <div class="overview">
                <div class="overview-icon"></div>
                <div class="overview-info">
                  <div class="overview-label">{{$t("message.dataService.apiMonitor.callCnt")}}</div>
                  <div class="overview-value"><span>{{callTotalCnt}}</span>次</div>
                </div>
              </div>
              <div class="overview">
                <div class="overview-icon"></div>
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
                  <a class="operation" @click="copy(row)">
                    查看监控图表
                  </a>
                </template>
              </Table>
              <div class="pagebar">
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
  </div>
</template>
<script>
import api from "@/common/service/api";
export default {
  data() {
    return {
      columnsRate: [
        {
          title: '排名',
          slot: 'rank'
        },
        {
          title: 'ID',
          key: 'id'
        },
        {
          title: 'API名称',
          key: 'apiName'
        },
        {
          title: '请求次数',
          key: 'totalCnt'
        },
        {
          title: '请求失败率',
          key: 'failRate'
        }
      ],
      columnsCnt: [
        {
          title: '排名',
          slot: 'rank'
        },
        {
          title: 'ID',
          key: 'id'
        },
        {
          title: 'API名称',
          key: 'apiName'
        },
        {
          title: '请求次数',
          key: 'totalCnt'
        },
        {
          title: '执行时长',
          key: 'totalTime'
        },
        {
          title: '平均调用时长(ms)',
          key: 'avgTime'
        }
      ],
      columnsDetail: [
        {
          title: 'ID',
          key: 'id'
        },
        {
          title: 'API名称',
          key: 'apiName'
        },
        {
          title: '业务流程',
          key: 'groupName'
        },
        {
          title: '标签',
          key: 'label'
        },
        {
          title: '负责人',
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
      rangeOptions: [
        { key: 'week', name: '7天' },
        { key: 'yesterday', name: '昨天' },
        { key: 'today', name: '今天' },
        { key: 'picker', name: 'picker' }
      ],
      currentRange: 'week',
      datePickerOpen: false,
      datePickerRange: []
    }
  },
  computed: {

  },
  created() {
    this.getOnlineApiCnt();
    this.getOfflineApiCnt();
    this.getCallTotalCnt();
    this.getCallTotalTime();
    this.getCallListByCnt();
    this.getCallListByFailRate();
    this.getCallListDetail();
  },
  methods: {
    getOnlineApiCnt() {
      api.fetch('/dss/framework/dbapi/apimonitor/onlineApiCnt', {
        // workspaceId: this.$route.query.workspaceId,
        workspaceId: 1,
      }, 'get').then((res) => {
        console.log(res)
        this.onlineCnt = res.onlineApiCnt;
      }).catch((err) => {
        console.error(err)
      });
    },
    getOfflineApiCnt() {
      api.fetch('/dss/framework/dbapi/apimonitor/offlineApiCnt', {
        // workspaceId: this.$route.query.workspaceId,
        workspaceId: 1,
      }, 'get').then((res) => {
        console.log(res)
        this.offlineCnt = res.offlineApiCnt;
      }).catch((err) => {
        console.error(err)
      });
    },
    getCallTotalCnt() {
      api.fetch('/dss/framework/dbapi/apimonitor/callTotalCnt', {
        // workspaceId: this.$route.query.workspaceId,
        workspaceId: 1,
        beginTime: '2021-07-21 00:00:00',
        endTime: '2021-07-23 00:00:00'
      }, 'get').then((res) => {
        console.log(res)
        this.callTotalCnt = res.callTotalCnt;
      }).catch((err) => {
        console.error(err)
      });
    },
    getCallTotalTime() {
      api.fetch('/dss/framework/dbapi/apimonitor/callTotalTime', {
        // workspaceId: this.$route.query.workspaceId,
        workspaceId: 1,
        beginTime: '2021-07-21 00:00:00',
        endTime: '2021-07-23 00:00:00'
      }, 'get').then((res) => {
        console.log(res)
        this.callTotalTime = res.callTotalTime;
      }).catch((err) => {
        console.error(err)
      });
    },
    getCallListByCnt() {
      api.fetch('/dss/framework/dbapi/apimonitor/callListByCnt', {
        // workspaceId: this.$route.query.workspaceId,
        workspaceId: 1,
        beginTime: '2021-07-21 00:00:00',
        endTime: '2021-07-23 00:00:00'
      }, 'get').then((res) => {
        console.log(res)
        this.listCnt = res.list;
      }).catch((err) => {
        console.error(err)
      });
    },
    getCallListByFailRate() {
      api.fetch('/dss/framework/dbapi/apimonitor/callListByFailRate', {
        // workspaceId: this.$route.query.workspaceId,
        workspaceId: 1,
        beginTime: '2021-07-21 00:00:00',
        endTime: '2021-07-23 00:00:00'
      }, 'get').then((res) => {
        console.log(res)
        this.listRate = res.list;
      }).catch((err) => {
        console.error(err)
      });
    },
    getCallListDetail() {
      api.fetch('/dss/framework/dbapi/apimonitor/list', {
        // workspaceId: this.$route.query.workspaceId,
        workspaceId: 1,
        pageNow: this.pageData.pageNow,
        pageSize: this.pageData.pageSize,
      }, 'get').then((res) => {
        console.log(res)
        this.listDetail = res.list;
        this.pageData.total = res.total;
      }).catch((err) => {
        console.error(err)
      });
    },
    handlePageSizeChange(pageSize) {
      console.log(pageSize);
      this.pageData.pageSize = pageSize;
      this.getCallListDetail();
    },
    handlePageChange(page) {
      console.log(page);
      this.pageData.pageNow = page;
      this.getCallListDetail();
    },
    handleDateChange(option) {
      this.currentRange = option.key;
      if (option.key != 'picker') {
        this.datePickerRange = [];
        this.datePickerOpen = false;
      }
    },

    handlePickerClick () {
      this.datePickerOpen = !this.datePickerOpen;
    },
    handlePickerChange (date) {
      this.datePickerRange = date;
      this.datePickerOpen = false;
    },
  },
}
</script>

<style lang="scss" scoped>
.monitor-holder {
  height: 36px;
  background: #fff;
}
.tab-wrap {
  margin-top: -36px;
  padding: 0 24px;
}
.dashboard {
  height: 80px;
  display: flex;
  align-items: center;
  padding: 10px 24px;
  border-radius: 2px;
  margin-top: 10px;
  margin-bottom: 24px;
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
  .dash-range-group {
    display: flex;
    align-items: center;
    .range-group-item {
      min-width: 60px;
      height: 32px;
      font-size: 14px;
      line-height: 30px;
      transition: all .2s ease-in-out;
      cursor: pointer;
      border: 1px solid #D9D9D9;
      border-left: 0;
      background: #fff;
      text-align: center;
      color: #666;
      &:first-child {
        border-radius: 4px 0 0 4px;
        border-left: 1px solid #D9D9D9;
      }
      &:last-child {
        border-radius: 0 4px 4px 0;
      }
      &.group-item-checked {
        background: #fff;
        border-color: #1890FF;
        color: #1890FF;
        box-shadow: -1px 0 0 0 #1890FF;
      }
      &.group-item-checked:first-child {
        box-shadow: none!important;
      }
      .date-trigger {
        padding: 0 15px;
        line-height: 32px;
        .date-show {
          margin-right: 15px;
        }
      }
    }
  }
}
.metrics-wrap {
  display: flex;
  margin-bottom: 24px;
  .metrics-mr {
    margin-right: 24px;
  }
  .metrics {
    flex: 1;
    background: #fff;
    border-radius: 2px;
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
      .overview {
        display: flex;
        align-items: center;
        margin-bottom: 20px;
        padding: 25px 20px;
        border: 1px solid #DEE4EC;
        border-radius: 4px;
        &:last-child {
          margin-bottom: 0;
        }
        .overview-icon {
          width: 40px;
          height: 40px;
          border-radius: 50%;
          background: aqua;
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
  }
}
</style>