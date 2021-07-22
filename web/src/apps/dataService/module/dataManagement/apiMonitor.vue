<template>
  <div>
    <div class="monitor-holder"></div>
    <Tabs value="screen" class="tab-wrap">
      <Tab-pane label="API计量大屏" name="screen">
        <div class="dashboard">
          <div class="dash-content">
            <span class="dash-title">API总数</span>
            <span class="dash-item">已发布<span class="dash-value">3</span></span>
            <span class="dash-item">未发布<span class="dash-value">3</span></span>
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
              <div class="metrics-head-title">服务资源分配</div>
            </div>
            <div class="metrics-body">
              hold
            </div>
          </div>
          <div class="metrics">
            <div class="metrics-head">
              <div class="metrics-head-title">总体计量</div>
            </div>
            <div class="metrics-body">
              <div class="overview">
                <div class="overview-icon"></div>
                <div class="overview-info">
                  <div class="overview-label">总调用次数</div>
                  <div class="overview-value"><span>0</span>次</div>
                </div>
              </div>
              <div class="overview">
                <div class="overview-icon"></div>
                <div class="overview-info">
                  <div class="overview-label">总调用次数</div>
                  <div class="overview-value"><span>0</span>GB/s</div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="metrics-wrap">
          <div class="metrics">
            <div class="metrics-head">
              <div class="metrics-head-title">昨日出错排行Top10</div>
            </div>
            <div class="metrics-body">
              <Table :columns="columns" :data="data" size="large"></Table>
            </div>
          </div>
        </div>
        <div class="metrics-wrap">
          <div class="metrics">
            <div class="metrics-head">
              <div class="metrics-head-title">昨日调用量排行Top10</div>
            </div>
            <div class="metrics-body">
              <Table :columns="columns" :data="data" size="large"></Table>
            </div>
          </div>
        </div>
      </Tab-pane>
      <Tab-pane label="API计量详情" name="detail">
        <div class="metrics-wrap">
          <div class="metrics">
            <div class="metrics-body">
              <Table :columns="columns" :data="data" size="large"></Table>
            </div>
          </div>
        </div>
      </Tab-pane>
    </Tabs>
  </div>
</template>
<script>
// import api from "@/common/service/api";
export default {
  data() {
    return {
      columns: [
        {
          title: '姓名',
          key: 'name'
        },
        {
          title: '年龄',
          key: 'age'
        },
        {
          title: '地址',
          key: 'address'
        }
      ],
      data: [
        {
          name: '李小红',
          age: 30,
          address: '上海市浦东新区世纪大道'
        },
        {
          name: '周小伟',
          age: 26,
          address: '深圳市南山区深南大道'
        }
      ],
      pageData: {
        total: 20,
        pageNum: 1,
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
    // 获取api相关数据
    // api.fetch('/dss/apiservice/queryById', {
    //   id: this.$route.query.id,
    // }, 'get').then((rst) => {
    //   if (rst.result) {
    //     // api的基础信息
    //     this.apiData = rst.result;
    //     this.formValidate.approvalNo = this.apiData.approvalVo.approvalNo;
    //     // 更改网页title
    //     document.title = rst.result.aliasName || rst.result.name;
    //     // 加工api信息tab的数据
    //     this.apiInfoData = [
    //       { label: this.$t('message.apiServices.label.apiName'), value: rst.result.name },
    //       { label: this.$t('message.apiServices.label.path'), value: rst.result.path },
    //       { label: this.$t('message.apiServices.label.scriptsPath'), value: rst.result.scriptPath },
    //     ]
    //   }
    // }).catch((err) => {
    //   console.error(err)
    // });
  },
  methods: {
    handlePageSizeChange(pageSize) {
      console.log(pageSize);
      this.pageData.pageSize = pageSize;
    },
    handlePageChange(page) {
      console.log(page);
      this.pageData.pageNum = page;
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