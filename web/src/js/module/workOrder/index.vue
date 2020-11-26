<template>
  <div class="table">
    <Table :columns="columns" :data="workOrders" style="margin-bottom: 10px;">
      <template slot-scope="{ row }" slot="workOrderId">
        <span>{{ row.workOrderId }}</span>
      </template>
      <template slot-scope="{ row }" slot="workOrderItem">
        <div class="resource-item">
          <span class="label">计算资源</span><span>{{ parseComputed(row.workOrderItem.computeResource) }}</span>
        </div>
        <div class="resource-item">
          <span class="label">存储资源</span><span>{{ parseStorage(row.workOrderItem.storageResource) }}</span>
        </div>
        <div class="resource-item">
          <span class="label">订购周期</span><span>{{ parseCycle(row.workOrderItem.storageResource) }}</span>
        </div>
      </template>

      <template slot-scope="{ row }" slot="isSuccess">
        <span>{{ parseStatus(row.isSuccess) }}</span>
      </template>

      <template slot-scope="{ row }" slot="workOrderType">
        <span>{{ parseType(row.workOrderType) }}</span>
      </template>
      <template slot-scope="{ row }" slot="action">
        <Button v-show="row.isSuccess === 2" type="warning" style="margin-right: 5px" @click="quit(row)">退订</Button>
      </template>
    </Table>
    <Spin v-if="loading" size="large" fix />
  </div>
</template>

<script>
import api from "@/js/service/api";
import storage from "@/js/helper/storage";
import _ from 'lodash';
const ORDER_TYPE = [
  { code: '1', desc: '订购' },
  { code: '2', desc: '续订' },
  { code: '3', desc: '升级' },
  { code: '5', desc: '退订' },
  { code: '6', desc: '到期' },
  { code: '7', desc: '销毁' },
];

const ORDER_STATUS = [
  { code: 0, desc: '未订购' },
  { code: 1, desc: '订购开通中' },
  { code: 2, desc: '使用中' },
  { code: 3, desc: '订购开通失败' },
  { code: 4, desc: '失效' },
  { code: 5, desc: '销户中' },
  { code: 6, desc: '销户失败' }
];

export default {
  data() {
    return {
      workOrders: [],
      columns: [
        {
          title: "订单号",
          slot: "workOrderId"
        },
        {
          title: "订单详情",
          slot: "workOrderItem"
        },
        {
          title: "订单状态",
          slot: "isSuccess"
        },
        {
          title: "订单类型",
          slot: "workOrderType"
        },
        {
          title: "操作",
          slot: "action"
        }
      ],
      loading: false
    };
  },
  created() {
    const userInfo = storage.get("userInfo");
    this.userId = (userInfo.basic && userInfo.basic.ctyunUserId) || "";
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      // 获取workorder列表
      this.loading = true;
      api
        .fetch("workOrder", { userId: this.userId }, "get")
        .then(res => {
          this.workOrders = res.map((item) => {
            const workOrderItem = {
              computeResource: _.find(JSON.parse(item.workOrderItem), ['master', true]),
              storageResource: _.find(JSON.parse(item.workOrderItem), ['master', false]),
            }
            return { ...item, workOrderItem }
          });
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    quit(row) {
      this.$Modal.confirm({
        title: "提示",
        content: "<p>退订之后，您的资源和数据将在15天内被回收，确认退订请点击确定按钮。</p>",
        onOk: () => {
          console.log(row)
          const url = `https://saas.ctyun.cn/eorder/luban/unsubscribe?orderId=${row.workOrderId}`
          window.open(url)
        }
      });
    },
    parseComputed(computeResource) {
      const { workOrderItemConfig: { coreNum, memSize, value } } = computeResource;
      return `${coreNum}vCPU ${memSize/1024}GiB * ${value}`
    },
    parseStorage(storageResource) {
      const { workOrderItemConfig: { value } } = storageResource;
      if (value > 1024) {
        return `${value/1024} TB`;
      }
      return `${value} GB`;
    },
    parseCycle(computeResource) {
      const { workOrderItemConfig: { cycleCnt } } = computeResource;
      if (cycleCnt > 12) {
        const year = cycleCnt / 12;
        const month = cycleCnt % 12;
        return month ? `${year}年${month}个月` : `${year}年`;
      }
      return `${cycleCnt}个月`;
    },
    parseType(type) {
      const text = _.find(ORDER_TYPE, ['code', type]).desc;
      return text ? text : '';
    },
    parseStatus(status) {
      const orderStatus = _.find(ORDER_STATUS, ['code', status]).desc;
      return orderStatus ? orderStatus : '';
    }
  }
};
</script>

<style lang="scss" scoped>
.table {
  padding: 20px;
  .resource-item {
    margin: 5px 0;
    .label {
      margin-right: 10px;
    }
  }
  .desc {
    width: 100%;
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
    cursor: pointer;
  }
}
</style>
