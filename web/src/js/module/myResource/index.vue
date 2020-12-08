<template>
  <div class="container">
    <div class="container-warp">
      {{ $t('message.myResource.resourceInfo.title') }}
      <span class="error-msg" @click="openFeedBack">
        <img src="../../../assets/images/Warning-Circle-Fill.svg" class="error-img"/>
        <span class="error-text">{{ errorMsg }}</span>
      </span>
      <span class="refresh">
        <Icon type="md-refresh" @click="getHistory"/>
      </span>
    </div>
    <div class="resource-list">
      <Row :gutter="16">
        <Col span="6" v-for="(item, index) in resourceInfo" :key="index">
        <div class="resoure-item">
          <div class="resoure-item-img">
            <img :src="item.backgroundImg"/>
          </div>
          <div class="resoure-item-content">
            <div class="content-left">
              <div class="left-text">{{ item.title }}</div>
              <div class="left-value">{{ item.data }}</div>
            </div>
            <div class="content-right">
              <span @click="item.click(item, index)">
                <img :src="item.operateImg" class="operate-img"/>
                <div class="right-text">{{ item.operate }}</div>
              </span>
            </div>
          </div>
        </div>
        </Col>
      </Row>
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
        <template slot-scope="{ row }" slot="workOrderInfo">
          <div class="table-column">
            <div class="table-column-item">
              <span>{{ $t('message.myResource.history.columns.workOrderInfo.info.comptResource') }}</span>
              <span>{{ row.workOrderInfo.coreNum }}</span>
              <span>{{ row.workOrderInfo.memSize }}</span>
            </div>
            <div class="table-column-item">
              <span>{{ $t('message.myResource.history.columns.workOrderInfo.info.storageCycle') }}</span>
              <span>{{ row.workOrderInfo.storage }}</span>
            </div>
            <div class="table-column-item">
              <span>{{ $t('message.myResource.history.columns.workOrderInfo.info.orderCycle') }}</span>
              <span>{{ parseOrderCycle(row.workOrderInfo.cycleTime, row.workOrderInfo.cycleType) }}</span>
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
      </Table>
      <Page
        :total="page.total"
        :page-size-opts="page.sizeOpts"
        :page-size="page.size"
        :current="page.current"
        :styles="getPageStyles"
        show-sizer
        @on-change="handlePagechange"
        @on-page-size-change="handleSizeChange"/>
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
// import storage from '@/js/helper/storage';
// import moment from 'moment';
// import module from './index';
import api from '@/js/service/api';
import FeedBackDialog from '../feedBack/index.vue';

export default {
  name: 'MyResource',
  components: {
    FeedBackDialog
  },
  data() {
    this.columns = [
      {
        title: this.$t('message.myResource.history.columns.workOrderId'),
        key: 'workOrderId',
        minWidth: 50,
        align: 'left'
      },
      {
        title: this.$t('message.myResource.history.columns.workOrderInfo.headerTitle'),
        key: 'workOrderInfo',
        slot: 'workOrderInfo',
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
        align: 'left'
      }
    ];
    this.historyList = [
      {
        id: 1,
        workOrderId: '13ed82f1218d4ab68ed6303761380a0e',
        workOrderInfo: {
          coreNum: '2vCPU',
          memSize: '4GiB * 12',
          storage: '600TB',
          cycleTime: 2,
          cycleType: 'Y'
        },
        status: 2,
        orderType: '订购',
        createTime: '2020-01-04 09:41:00'
      },
      {
        id: 2,
        workOrderId: '13ed82f1218d4ab68ed6303761380a0e',
        workOrderInfo: {
          coreNum: '2vCPU',
          memSize: '4GiB * 12',
          storage: '600TB',
          cycleTime: 2,
          cycleType: 'D'
        },
        status: 1,
        orderType: '订购',
        createTime: '2020-01-04 09:41:00'
      },
      {
        id: 3,
        workOrderId: '13ed82f1218d4ab68ed6303761380a0e',
        workOrderInfo: {
          coreNum: '2vCPU',
          memSize: '4GiB * 12',
          storage: '600TB',
          cycleTime: 2,
          cycleType: 'M'
        },
        status: 0,
        orderType: '订购',
        createTime: '2020-01-04 09:41:00'
      }
    ];
    return {
      loading: false,
      errorMsg: '',
      page: {
        currentNum: 1, //当前页数
        size: 5,
        sizeOpts: [5, 20, 30, 50],
        total: 0,
      },
      resourceData: {
        ctyunUserId: 'd22ff64d3985458b9ef13e4e143bead3',
        coreNum: '16C',
        memSize: '64G',
        storage: '1T',
        expireTime: '2021-01-01',
        resourceId: '53c4041b22924b09ac8aa47d9540447b'
      },
      resourceInfo: [
        { 
          title: this.$t('message.myResource.resourceInfo.coreNumber'),
          operateImg: require('../../../assets/images/arrow-up-circle.svg'),
          operate: this.$t('message.myResource.resourceInfo.expansion'),
          backgroundImg: require('../../../assets/images/coreNumber.svg'),
          data: '16C',
          click: this.handleExpansion,
          key: 'coreNum'
        },
        { 
          title: this.$t('message.myResource.resourceInfo.memory'),
          operateImg: require('../../../assets/images/arrow-up-circle.svg'),
          operate: this.$t('message.myResource.resourceInfo.expansion'),
          backgroundImg: require('../../../assets/images/memory.svg'),
          data: '',
          click: this.handleExpansion,
          key: 'memory'
        },
        { 
          title: this.$t('message.myResource.resourceInfo.storageSpace'),
          operateImg: require('../../../assets/images/arrow-up-circle.svg'),
          operate: this.$t('message.myResource.resourceInfo.expansion'),
          backgroundImg: require('../../../assets/images/storageSpace.svg'),
          data: '',
          click: this.handleExpansion,
          key: 'storageSpace'
        },
        { 
          title: this.$t('message.myResource.resourceInfo.expireTime'),
          operateImg: require('../../../assets/images/add-circle.svg'),
          operate: this.$t('message.myResource.resourceInfo.renew'),
          backgroundImg: require('../../../assets/images/expireTime.svg'),
          data: '',
          click: this.handleRenew,
          key: 'expireTime'
        },
      ],
      feedBackShow: false,
      feedBackActionType: '',
      feedBackType: '',
      errorCount: 0, //异常情况数
    };
  },
  computed: {
    getPageStyles() {
      return { marginTop: '12px', textAlign: 'right' };
    },
    parseOrderCycle() {
      return (cycleTime, cycleType) => {
        let orderCycle = ''; 
        switch(cycleType) {
          case 'Y':
            orderCycle = `${cycleTime}${this.$t('message.myResource.history.orderCycle.year')}`;
            break;
          case 'M':
            orderCycle = `${cycleTime}${this.$t('message.myResource.history.orderCycle.month')}`;
            break;
          case 'D':
            orderCycle = `${cycleTime}${this.$t('message.myResource.history.orderCycle.day')}`;
            break;
          default:
            break;
        }
        return orderCycle;
      }
    }
  },
  created() {
    // const userInfo = storage.get('userInfo');
    // this.userName = userInfo.basic.username;
    // this.menuTitle = this.$route.query.menuName;
    // this.initSearchForm();
  },
  mounted() {
    // this.handleSearch();this
    this.errorMsg = `${this.errorCount}${this.$t('message.myResource.resourceInfo.errorText')}`;
    this.getResourceInfo();
  },
  methods: {
    getResourceInfo() {
      this.resourceInfo.forEach((item) => {
        switch(item.key) {
          case 'coreNum':
            item.data = this.resourceData.coreNum;
            break;
          case 'memory':
            item.data = this.resourceData.memSize;
            break;
          case 'storageSpace':
            item.data = this.resourceData.storage;
            break;
          case 'expireTime':
            item.data = this.resourceData.expireTime;
            break;
          default:
            break;
        }
      });
    },
    getHistory() {
      console.log('----获取订单信息')
    },
    // 扩容
    handleExpansion(item, index) {
      console.log('----扩容')
      this.resourceInfo[index].operateImg = require('../../../assets/images/arrow-down-circle.svg');
    },
    // 续费
    handleRenew() {
      console.log('----续费') 
    },
    handlePagechange(page) {
      this.page.currentNum = page;
    },
    handleSizeChange(size) {
      this.page.size = size;
      this.page.currentNum = 1;
    },
    openFeedBack() {
      this.feedBackActionType = 'add';
      this.feedBackShow = true;
    },
    feedBackShowAction(val) {
      this.feedBackShow = val;
    },
  },
};
</script>
<style lang="scss" scoped src="./index.scss"></style>
</style>
