<template>
  <div class="table">
    <Table :columns="columns" :data="workOrders" style="margin-bottom: 10px;">
      <template slot-scope="{ row }" slot="workOrderId">
        <span>{{ row.workOrderId }}</span>
      </template>
      <template slot-scope="{ row }" slot="ctyunUserId">
        <span>{{ row.ctyunUserId }}</span>
      </template>
      <template slot-scope="{ row }" slot="workOrderItem">
        <Poptip word-wrap width="200" :content="row.workOrderItem">
          <Button size="small">查看详情</Button>
        </Poptip>
      </template>

      <template slot-scope="{ row }" slot="isSuccess">
        <span>{{ row.isSuccess }}</span>
      </template>
      <template slot-scope="{ row }" slot="action">
        <Button type="warning" style="margin-right: 5px" @click="quit(row)">退订</Button>
      </template>
    </Table>
    <Spin v-if="loading" size="large" fix />
  </div>
</template>

<script>
import api from "@/js/service/api";
import storage from "@/js/helper/storage";

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
          title: "订单用户ID",
          slot: "ctyunUserId"
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
          this.workOrders = res;
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },
    quit(row) {
      this.$Modal.confirm({
        title: "提示",
        content: "<p>退订之后，您的资源和数据将在15天内被回收，确认退订请点击确定按钮.</p>",
        onOk: () => {
          console.log(row)
          const url = `https://bigdata.ctyun.cn:8180/eorder/flydragon/unsubscribe?orderId=${row.workOrderId}`
          window.open(url)
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
.table {
  padding: 20px;
  .desc {
    width: 100%;
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
    cursor: pointer;
  }
}
</style>
