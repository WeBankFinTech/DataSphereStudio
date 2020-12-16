<template>
  <div class="container">
    <div class="container-warp">
      我的资源
      <span v-if="errorMsgShow" class="error-msg" @click="openFeedBack">
        <img src="../../../assets/images/Warning-Circle-Fill.svg" class="error-img"/>
        <span class="error-text">{{ errorMsg }}</span>
      </span>
      <span class="refresh">
        <Icon type="md-refresh" @click="refresh"/>
      </span>
    </div>
    <div class="resource-list">
      <ResourceInfo
        ref="resourceInfo"
        :resourceInfo="resourceInfo"
        @expansion="handleExpansion"
        @renewOrder="handleRenewOrder"
        @cancelOrder="handleCancelOrder"
      />
    </div>
    <div class="container-warp">资源历史</div>
    <div class="resource-list">
      <Table
        :columns="columns"
        :data="historyList"
        :loading="loading"
        :border="false"
        max-height="600"
      >
        <template slot-scope="{ row }" slot="resourceInfo">
          <div class="table-column">
            <div class="table-column-item">
              <span>计算资源</span>
              <span>{{ row.resourceInfo.coreNum }}</span>
              <span>{{ row.resourceInfo.memSize }}</span>
            </div>
            <div class="table-column-item">
              <span>存储周期</span>
              <span>{{ row.resourceInfo.storage }}</span>
            </div>
            <div class="table-column-item">
              <span>订购周期</span>
              <span>{{ row.resourceInfo.cycleTime }}</span>
            </div>
          </div>
        </template>
        <template slot-scope="{ row }" slot="status">
          <div class="table-column">
            <Badge :status="parseStatus(row.status)" :text="parseStatusDesc(row.status)"/>
          </div>
        </template>
        <template slot-scope="{ row }" slot="createTime">
          <span>{{ parseDate(row.createTime) }}</span>
        </template>
      </Table>
    </div>
    <FeedBackDialog
      ref="feedBackForm"
      :action-type="feedBackActionType"
      :feedBackFormShow="feedBackShow"
      :feedBackType="feedBackType"
      @show="feedBackShowAction"
    />
  </div>
</template>
<script>
import { find, isEmpty } from 'lodash';
import storage from '@/js/helper/storage';
import moment from 'moment';
import module from './index';
import api from '@/js/service/api';
import ResourceInfo from './resourceInfo.vue';
import FeedBackDialog from '../feedBack/index.vue';
const REFRESH_TIME = 60000;
const ORDER_STATUS = [
  { code: 0, desc: '订购开通中', status: 'processing' },
  { code: 1, desc: '使用中', status: 'success' },
  { code: 2, desc: ' 订购开通失败', status: 'error' },
  { code: 3, desc: '订购已到期', status: 'warning' },
  { code: -1, desc: '异常订单', status: 'warning' },
];

export default {
  name: 'MyResource',
  components: {
    ResourceInfo,
    FeedBackDialog
  },
  data() {
    this.expansionUrl = 'https://saas.ctyun.cn/eorder/luban/expansion'; // 扩容url
    this.prolongUrl = 'https://saas.ctyun.cn/eorder/luban/prolong'; // 续订url
    this.quitUrl = 'https://saas.ctyun.cn/eorder/luban/unsubscribe'; // 退订url
    this.columns = [
      {
        title: '订单号',
        key: 'workOrderId',
        minWidth: 50,
        align: 'left'
      },
      {
        title: '订单详情',
        key: 'resourceInfo',
        slot: 'resourceInfo',
        align: 'left'
      },
      {
        title: '订单状态',
        key: 'status',
        slot: 'status',
        align: 'left'
      },
      {
        title: '订单类型',
        key: 'orderType',
        align: 'left'
      },
      {
        title: '创建时间',
        key: 'createTime',
        slot: 'createTime',
        align: 'left'
      }
    ];
    return {
      loading: false,
      errorMsg: '',
      resourceInfo: {},
      historyList: [],
      feedBackShow: false,
      feedBackActionType: '',
      feedBackType: '',
      errorCount: 0, //异常情况数
      userId: null,
      userStatus: null,
      timer: ''
    };
  },
  computed: {
    errorMsgShow() {
      // 当用户处于“开通/扩容/退订/续订失败”状态，显示错误警示框
      if ((this.userStatus === 2 || this.userStatus === 4 || this.userStatus === 6 || this.userStatus === 8) && this.errorCount > 0) {
        return true;
      }
      return false;
    },
    parseStatus() {
      return (status) => {
        const orderStatus = find(ORDER_STATUS, ['code', status]).status;
        return orderStatus ? orderStatus : '';
      }
    },
    parseStatusDesc() {
      return (status) => {
        const orderStatusDesc = find(ORDER_STATUS, ['code', status]).desc;
        return orderStatusDesc ? orderStatusDesc : '';
      }
    }
  },
  created() {
    const userInfo = storage.get('userInfo');
    this.userId = (userInfo.basic && userInfo.basic.ctyunUserId) || "";
    this.userStatus = (userInfo.basic && userInfo.basic.status) || "";
  },
  mounted() {
    this.search();
    // this.createTimer();
  },
  beforeDestroy() {
    clearInterval(this.timer);
  },
  methods: {
    search() {
      this.getResourceInfo();
      this.getHistory();
    },
    getResourceInfo() {
      api.fetch(`${module.data.API_PATH}luban/users/resources?ctyunUserId=8bdca937f2444172b3942198924de416`, 'get').then((rst) => {
        this.resourceInfo = rst;
      }).catch(() => {});
    },
    getHistory() {
      this.loading = true;
      api.fetch(`${module.data.API_PATH}workOrder?ctyunUserId=7962606d99764abba58e6eb37f135d1b`, 'get').then((rst) => {
        this.historyList = rst;
        this.errorCount = this.historyList.filter((item) => item.status === 2).length;
        this.errorMsg = this.errorCount > 0 ? `${this.errorCount}个异常情况` : '';
        const time = Date.parse(new Date());
        console.log('======', time)
        this.historyList.forEach((item) => {
          const subTime = time - item.createTime;
          console.log('--------', subTime)
        });
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
    },
    // 扩容
    async handleExpansion() {
      await this.getUserInfo();
      console.log(this.userStatus)
      console.log(this.userStatus === 10)
      if (this.userStatus === 10) { // status = 10为使用中可扩容
        window.open(`this.expansionUrl?orderId=${this.historyList[0].workOrderId}`);
      } else {
        this.$Message.warning('当前状态不支持扩容！');
      }
    },
    // 续订
    async handleRenewOrder() {
      await this.getUserInfo();
      // status = 9|10 为账户失效或者使用中可进行续订
      if (this.userStatus === 9 || this.userStatus === 10) {
        window.open(`this.prolongUrl?orderId=${this.historyList[0].workOrderId}`);
      } else {
        this.$Message.warning('当前状态不支持续订！');
      }
    },
    // 退订
    async handleCancelOrder() {
      await this.getUserInfo();
      if (this.historyList.length === 0) {
        this.$Message.warning('不存在可退订的历史资源订单！');
        return;
      }
      if (this.userStatus === 10) { // 正常使用可退订
        this.$Modal.confirm({
          title: "提示",
          content: "<p>退订之后，您的资源和数据将在15天内被回收，确认退订请点击确定按钮。</p>",
          onOk: () => {
            window.open(`${this.quitUrl}?orderId=${this.historyList[0].workOrderId}`);
          }
        });
      } else {
        this.$Message.warning('当前状态不支持退订！');
      }
    },
    refresh() {
      this.search();
    },
    getUserInfo() {
      api.fetch('/dss/getBaseInfo', 'get').then((rst) => {
        if (!isEmpty(rst)) {
          storage.set('baseInfo', rst);
          storage.set('userInfo', rst.userInfo);
          this.userStatus = rst.userInfo.basic.status;
        }
      });
    },
    openFeedBack() {
      this.feedBackActionType = 'add';
      this.feedBackShow = true;
    },
    feedBackShowAction(val) {
      this.feedBackShow = val;
    },
    createTimer() {
      this.timer = setInterval(this.refresh, this.REFRESH_TIME);
    },
    parseDate(date) {
      return moment(date).format('YYYY-MM-DD HH:mm:ss');
    }
  },
};
</script>
<style lang="scss" src="./index.scss"></style>
</style>
