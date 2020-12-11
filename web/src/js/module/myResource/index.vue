<template>
  <div class="container">
    <div class="container-warp">
      {{ $t('message.myResource.resourceInfo.title') }}
      <span v-if="errorMsgShow" class="error-msg" @click="openFeedBack">
        <img src="../../../assets/images/Warning-Circle-Fill.svg" class="error-img"/>
        <span class="error-text">{{ errorMsg }}</span>
      </span>
      <span class="refresh">
        <Icon type="md-refresh" @click="handleSearch"/>
      </span>
    </div>
    <div class="resource-list">
      <ResourceInfo
        ref="resourceInfo"
        :resourceData="resourceData"
        @expansion="handleExpansion"
        @renewOrder="handleRenewOrder"
        @cancelOrder="handleCancelOrder"
      />
    </div>
    <div class="container-warp">{{ $t('message.myResource.history.title') }}</div>
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
              <span>{{ $t('message.myResource.history.columns.workOrderInfo.info.comptResource') }}</span>
              <span>{{ row.resourceInfo.coreNum }}</span>
              <span>{{ row.resourceInfo.memSize }}</span>
            </div>
            <div class="table-column-item">
              <span>{{ $t('message.myResource.history.columns.workOrderInfo.info.storageCycle') }}</span>
              <span>{{ row.resourceInfo.storage }}</span>
            </div>
            <div class="table-column-item">
              <span>{{ $t('message.myResource.history.columns.workOrderInfo.info.orderCycle') }}</span>
              <span>{{ row.resourceInfo.cycleTime }}</span>
            </div>
          </div>
        </template>
        <template slot-scope="{ row }" slot="status">
          <div class="table-column">
            <Badge v-if="row.status === 0" status="processing" :text="$t('message.myResource.history.stateType.processing')"/>
            <Badge v-if="row.status === 1" status="success" :text="$t('message.myResource.history.stateType.success')"/>
            <Badge v-if="row.status === 2" status="error" :text="$t('message.myResource.history.stateType.error')"/>
            <Badge v-if="row.status === 3" status="warning" :text="$t('message.myResource.history.stateType.warning')"/>
            <Badge v-if="row.status === -1" status="warning" :text="$t('message.myResource.history.stateType.expired')"/>
          </div>
        </template>
        <template slot-scope="{ row }" slot="createTime">
          <div>{{ row.createTime | formatDate }}</div>
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
// import { isEmpty } from 'lodash';
import storage from '@/js/helper/storage';
import moment from 'moment';
import module from './index';
import api from '@/js/service/api';
import ResourceInfo from './resourceInfo.vue';
import FeedBackDialog from '../feedBack/index.vue';

export default {
  name: 'MyResource',
  components: {
    ResourceInfo,
    FeedBackDialog
  },
  data() {
    this.REFRESH_TIME = 60000;
    this.columns = [
      {
        title: this.$t('message.myResource.history.columns.workOrderId'),
        key: 'workOrderId',
        minWidth: 50,
        align: 'left'
      },
      {
        title: this.$t('message.myResource.history.columns.workOrderInfo.headerTitle'),
        key: 'resourceInfo',
        slot: 'resourceInfo',
        align: 'left'
      },
      {
        title: this.$t('message.myResource.history.columns.status'),
        key: 'status',
        slot: 'status',
        align: 'left'
      },
      {
        title: this.$t('message.myResource.history.columns.orderType'),
        key: 'orderType',
        align: 'left'
      },
      {
        title: this.$t('message.myResource.history.columns.createTime'),
        key: 'createTime',
        slot: 'createTime',
        align: 'left'
      }
    ];
    // this.historyList = [
    //   {
    //     id: 1,
    //     workOrderId: '13ed82f1218d4ab68ed6303761380a0e',
    //     workOrderInfo: {
    //       coreNum: '2vCPU',
    //       memSize: '4GiB * 12',
    //       storage: '600TB',
    //       cycleTime: 2,
    //       cycleType: 'Y'
    //     },
    //     status: 2,
    //     orderType: '订购',
    //     createTime: '2020-01-04 09:41:00'
    //   },
    //   {
    //     id: 2,
    //     workOrderId: '13ed82f1218d4ab68ed6303761380a0e',
    //     workOrderInfo: {
    //       coreNum: '2vCPU',
    //       memSize: '4GiB * 12',
    //       storage: '600TB',
    //       cycleTime: 2,
    //       cycleType: 'D'
    //     },
    //     status: 1,
    //     orderType: '订购',
    //     createTime: '2020-01-04 09:41:00'
    //   },
    //   {
    //     id: 3,
    //     workOrderId: '13ed82f1218d4ab68ed6303761380a0e',
    //     workOrderInfo: {
    //       coreNum: '2vCPU',
    //       memSize: '4GiB * 12',
    //       storage: '600TB',
    //       cycleTime: 2,
    //       cycleType: 'M'
    //     },
    //     status: 0,
    //     orderType: '订购',
    //     createTime: '2020-01-04 09:41:00'
    //   }
    // ];
    return {
      loading: false,
      errorMsg: '',
      resourceData: {
        // status: 0,
        // ctyunUserId: 'd22ff64d3985458b9ef13e4e143bead3',
        // coreNum: '16C',
        // memSize: '64G',
        // storage: '1T',
        // expireTime: '2021-01-01',
        // resourceId: '53c4041b22924b09ac8aa47d9540447b'
      },
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
    // parseOrderCycle() {
    //   return (cycleTime, cycleType) => {
    //     let orderCycle = ''; 
    //     switch(cycleType) {
    //       case 'Y':
    //         orderCycle = `${cycleTime}${this.$t('message.myResource.history.orderCycle.year')}`;
    //         break;
    //       case 'M':
    //         orderCycle = `${cycleTime}${this.$t('message.myResource.history.orderCycle.month')}`;
    //         break;
    //       case 'D':
    //         orderCycle = `${cycleTime}${this.$t('message.myResource.history.orderCycle.day')}`;
    //         break;
    //       default:
    //         break;
    //     }
    //     return orderCycle;
    //   }
    // },
    errorMsgShow() {
      // 当用户处于“开通/扩容/退订/续订失败”状态，显示错误警示框
      if ((this.userStatus === 2 || this.userStatus === 4 || this.userStatus === 6 || this.userStatus === 8) && this.errorCount > 0) {
        return true;
      }
      return false;
    }
  },
  filters: {
    formatDate(date) {
      return moment(date).format('YYYY-MM-DD HH:mm:ss');
    }
  },
  created() {
    this.userId = storage.get('userInfo').basic.ctyunUserId;
    this.userStatus = storage.get('userInfo').basic.status;
    console.log(this.userInfo )
    // this.userName = userInfo.basic.username;
  },
  mounted() {
    this.handleSearch();
    // this.createTimer();
  },
  beforeDestroy() {
    clearInterval(this.timer);
  },
  methods: {
    handleSearch() {
      // this.userInfo.basic.ctyunUserId
      this.getResourceInfo();
      this.getHistory();
      // Promise.all([this.getResourceInfo(), this.getHistory()]).then((result) => {
      //   console.log(result)
      //   const [resourceInfo, historyData] = result;
      //   this.resourceData = resourceInfo;
      //   this.historyList = historyData;
      //   if (!isEmpty(this.historyList)) {
      //     this.errorCount = this.historyList.filter((item) => item.status === 2).length;
      //     this.errorMsg = `${this.errorCount}${this.$t('message.myResource.resourceInfo.errorText')}`;
      //   }
      // }).catch((error) => {
      //   console.log(error)
      // })
    },
    getResourceInfo() {
      api.fetch(`${module.data.API_PATH}luban/users/resources?ctyunUserId=8bdca937f2444172b3942198924de416`, 'get').then((rst) => {
        console.log(rst)
        this.resourceData = rst;
      }).catch(() => {});
      // return this.resourceData;
    },
    getHistory() {
      console.log('----获取订单信息')
      this.loading = true;
      api.fetch(`${module.data.API_PATH}workOrder?ctyunUserId=7962606d99764abba58e6eb37f135d1b`, 'get').then((rst) => {
        console.log(rst)
        this.historyList = rst;
        this.errorCount = this.historyList.filter((item) => item.status === 2).length;
        this.errorMsg = this.errorCount > 0 ? `${this.errorCount}${this.$t('message.myResource.resourceInfo.errorText')}` : '';
        this.loading = false;
      }).catch(() => {
        this.loading = false;
      });
      // return this.historyList;
    },
    // 扩容
    handleExpansion() {
      if (this.userStatus === 10) { // status = 10为使用中可扩容
        console.log('----扩容')
        window.location.href = 'https://saas.ctyun.cn/eorder/luban/prolong?orderId=e916d0ec912241d3829a9b8a8d453d2d'
      } else {
        this.$Message.warning(this.$t('message.myResource.warning.expansion'));
      }
    },
    // 续费
    handleRenewOrder() {
      console.log('----续费')
      // status = 9|10 为账户失效或者使用中可进行续费
      if (this.userStatus === 9 || this.userStatus === 10) {
        window.location.href = 'https://saas.ctyun.cn/eorder/luban/expansion?orderId=e916d0ec912241d3829a9b8a8d453d2d'
      } else {
        this.$Message.warning(this.$t('message.myResource.warning.renew'));
      }
    },
    // 退订
    handleCancelOrder() {
      console.log('----退订')   
    },
    openFeedBack() {
      this.feedBackActionType = 'add';
      this.feedBackShow = true;
    },
    feedBackShowAction(val) {
      this.feedBackShow = val;
    },
    createTimer() {
      this.handleSearch();
      this.timer = setInterval(this.handleSearch, this.REFRESH_TIME);
    },
  },
};
</script>
<style lang="scss" src="./index.scss"></style>
</style>
